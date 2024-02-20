(ns re-com.multi-select
  (:require-macros
   [re-com.core     :refer [handler-fn at]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
    [clojure.set                 :as set]
    [clojure.string              :as string]
    [goog.string                 :as gstring]
    [re-com.config               :refer [include-args-desc?]]
    [re-com.debug                :refer [->attr]]
    [re-com.input-text           :refer [input-text]]
    [re-com.box                  :as box]
    [re-com.text                 :as text]
    [re-com.buttons              :as buttons]
    [re-com.close-button         :as close-button]
    [re-com.util                 :as rc.util :refer [deref-or-value add-map-to-hiccup-call flatten-attr merge-css]]
    [re-com.validate             :as validate :refer [string-or-hiccup? parts?]]
    [reagent.core                :as reagent]))

(declare multi-select-css-spec)

(defn items-with-group-headings
  "Split a list of maps by a group key then return both the group"
  [items group-fn id-fn]
  (let [groups         (partition-by group-fn items)
        group-headers  (->> groups
                            (map first)
                            (map #(hash-map :id    [(group-fn %) (id-fn %)]
                                            :group (group-fn %))))]
    ;; Sample output:
    ;; group-headers    ({:group "Fruits"     :id "Fruits"}
    ;;                   {:group "Vegetables" :id "Vegetables"})
    ;;
    ;; groups           (({:short "Watermelon" :id "0001" :display-type "fruit"     :sort 110 ...}
    ;;                    {:short "Strawberry" :id "0002" :display-type "fruit"     :sort 120 ...}
    ;;                    {:short "Cherry"     :id "0003" :display-type "fruit"     :sort 130 ...})
    ;;                   ({:short "Corn"       :id "0004" :display-type "vegetable" :sort 430 ...}))
    [group-headers groups]))

(defn filter-items
  "Filter a list of items based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters"
  [group-fn filter-fn filter-text]
  (let [lower-filter-text (string/lower-case filter-text)]
    (fn [item]
      (let [group (or (group-fn item) "")
            label (str (filter-fn item))] ;; Need str for non-string labels like hiccup
        (or
         (string/includes? (string/lower-case group) lower-filter-text)
         (string/includes? (string/lower-case label) lower-filter-text))))))

(defn filter-items-regex
  "Filter a list of items based on a filter string using regex's (case insensitive). More powerful but can cause
   confusion for users entering reserved characters such as [ ] * + . ( ) etc."
  [group-fn filter-fn filter-text]
  (let [re (try
             (js/RegExp. filter-text "i")
             (catch js/Object e nil))]
    (partial (fn [re item]
               (when-not (nil? re)
                 (or (.test re (group-fn item)) (.test re (filter-fn item)))))
             re)))

(defn filter-text-box
  "Base function (before lifecycle metadata) to render a filter text box"
  [*filter-text placeholder *warning-message disabled? parts]
  (let [cmerger (merge-css multi-select-css-spec {:parts parts})]
    (add-map-to-hiccup-call
     (cmerger :filter-text-box)
     [box/h-box
      :width    "100%"
      :align    :center
      :children [(add-map-to-hiccup-call
                  (cmerger :filter-input-text)
                  [input-text
                   :model           *filter-text
                   :change-on-blur? false
                   :placeholder     placeholder
                                        ;:disabled?       disabled? ;; Left here just in case we DO want to prevent searches while disabled
                   :width           "100%"
                   :height          "28px"
                   :on-change       #(do (reset! *filter-text %)
                                         (reset! *warning-message nil))])
                 (add-map-to-hiccup-call
                  (cmerger :filter-reset-button)
                  [close-button/close-button
                   :on-click    #(reset! *filter-text "")
                   :div-size    0
                   :font-size   20
                   :left-offset -13])]])))

(defn group-heading-item
  "Render a group heading and set up appropriate mouse events"
  []
  (let [*mouse-over? (reagent/atom false)
        cmerger (merge-css multi-select-css-spec {})]
    (fn group-heading-render
      [& {:keys [heading disabled? click-callback double-click-callback selected-item-id]}]
      (let [id        (:id heading)
            selected? (= selected-item-id id)]
        [:li
         (flatten-attr
          (cmerger
           :group-heading-item
           {:selected? selected?
            :disabled? disabled?
            :mouse-over? @*mouse-over?
            :attr {:on-mouse-over   (handler-fn (reset! *mouse-over? true))
                   :on-mouse-out    (handler-fn (reset! *mouse-over? false))
                   :on-click        (when-not disabled? (handler-fn (click-callback id true))) ;; true = group-heading item selected
                   :on-double-click (when-not disabled? (handler-fn (double-click-callback id)))}}))
         (:group heading)]))))

(defn list-item
  "Render a list item and set up appropriate mouse events"
  []
  (let [*mouse-over? (reagent/atom false)
        cmerger (merge-css multi-select-css-spec {})]
    (fn list-item-render
      [& {:keys [item id-fn label-fn disabled? click-callback double-click-callback selected-item-id group-selected?]}]
      (let [id              (id-fn item)
            selected?       (= id selected-item-id)]
        [:li
         (flatten-attr
          (cmerger
           :list-item
           {:group-selected? group-selected?
            :selected? selected?
            :disabled? disabled?
            :mouse-over? @*mouse-over?
            :attr {:on-mouse-over   (handler-fn (reset! *mouse-over? true))
                   :on-mouse-out    (handler-fn (reset! *mouse-over? false))
                   :on-click        (when-not disabled? (handler-fn (click-callback id false))) ;; false = group-heading item NOT selected
                   :on-double-click (when-not disabled? (handler-fn (double-click-callback id)))}}))
         (label-fn item)]))))

(defn list-box
  "Render a list box which can be a single list or a grouped list"
  [& {:keys [items id-fn label-fn group-fn disabled? *current-item-id group-heading-selected? click-callback double-click-callback filter-choices-text src class style attr parts] :as args}]
  (let [[group-names group-item-lists] (items-with-group-headings items group-fn id-fn)
        has-group-names?               (not (and (nil? (:group (first group-names))) (= 1 (count group-item-lists)))) ;; if 0 or 1 group names, no headings to display
        make-list-item                 (fn [item]
                                         ^{:key (str (id-fn item))}
                                         [list-item
                                          :item                  item
                                          :id-fn                 id-fn
                                          :label-fn              label-fn
                                          :disabled?             disabled?
                                          :click-callback        click-callback
                                          :double-click-callback double-click-callback
                                          :selected-item-id      @*current-item-id
                                          :group-selected?       (when group-heading-selected? (= (first @*current-item-id) (group-fn item)))]) ;; for group headings, group-label is the first item in the vector
        make-items                     (fn [items] (doall (map make-list-item items)))
        make-group-heading-item        (fn [heading]
                                         ^{:key (:id heading)}
                                         [group-heading-item
                                          :heading               heading
                                          :disabled?             disabled?
                                          :click-callback        click-callback
                                          :double-click-callback double-click-callback
                                          :selected-item-id      @*current-item-id])
        make-heading-then-items        (fn [heading items]
                                         (cons (make-group-heading-item heading) (make-items items)))
        cmerger (merge-css multi-select-css-spec args)]
    (add-map-to-hiccup-call
     (cmerger :list-box {:disabled? disabled?})
     [box/box
      :src   src
      :size  "1"
      :child [:ul
              (flatten-attr (cmerger :list-box-results))
              (if (-> items count pos?)
                (if has-group-names?
                  (apply concat (doall (map make-heading-then-items group-names group-item-lists)))
                  (make-items (first group-item-lists)))
                (if (string/blank? filter-choices-text)
                  ""
                  [:li
                   (flatten-attr (cmerger :list-box-no-results))
                   (str "No results match \"" filter-choices-text "\"")]))]])))

(defn multi-button [& {:keys [disabled? label icon on-click class style attr] :as args}]

  (let [cmerger (merge-css multi-select-css-spec {})]
    (add-map-to-hiccup-call
     (cmerger :button)
     [buttons/button
      :src       (at)
      :label     [:span
                  [:i (flatten-attr (cmerger :button-icon {:icon icon}))]
                  [:span
                   (flatten-attr (cmerger :button-content))
                   (or label "")]]
      :disabled? disabled?
      :on-click  on-click])))

;;--------------------------------------------------------------------------------------------------
;; Component: multi-select
;;--------------------------------------------------------------------------------------------------

;; Set of candidates (sorted externally)
;; a set of ids

;; LHS: set of candidates with selected id set removed, sorted/grouped by fn
;; RHS: set of candidates selecting on id, sorted/grouped by fn

(def multi-select-parts-desc
  (when include-args-desc?
    [{:type :legacy                    :level 0 :class "rc-multi-select"                           :impl "[multi-select]"}
     {:name :container                 :level 1 :class "rc-multi-select-container"                 :impl "[h-box]"}
     {:name :left                      :level 2 :class "rc-multi-select-left"                      :impl "[v-box]"}
     {:name :left-label-container      :level 3 :class "rc-multi-select-left-label-container"      :impl "[h-box]"}
     {:name :left-label                :level 4 :class "rc-multi-select-left-label"                :impl "[:span]"}
     {:name :left-label-item-count     :level 4 :class "rc-multi-select-left-label-item-count"     :impl "[:span]"}
     {:name :left-list-box             :level 3 :class "rc-multi-select-left-list-box"             :impl "[list-box]"}
     {:name :filter-text-box           :level 3 :class "rc-multi-select-filter-text-box"           :impl "[h-box]"}
     {:name :filter-input-text         :level 4 :class "rc-multi-select-filter-input-text"         :impl "[input-text]"}
     {:name :filter-reset-button       :level 4 :class "rc-multi-select-filter-reset-button"       :impl "[close-button]"}
     {:name :left-filter-result-count  :level 3 :class "rc-multi-select-left-filter-result-count"  :impl "[label]"}
     {:name :middle-container          :level 2 :class "rc-multi-select-middle-container"          :impl "[v-box]"}
     {:name :middle-top-spacer         :level 3 :class "rc-multi-select-middle-top-spacer"         :impl "[box]"}
     {:name :middle                    :level 3 :class "rc-multi-select-middle"                    :impl "[v-box]"}
     {:name :include-all-button        :level 4 :class "rc-multi-select-include-all-button"        :impl "[button]"}
     {:name :include-selected-button   :level 4 :class "rc-multi-select-include-selected-button"   :impl "[button]"}
     {:name :exclude-selected-button   :level 4 :class "rc-multi-select-exclude-selected-button"   :impl "[button]"}
     {:name :exclude-all-button        :level 4 :class "rc-multi-select-exclude-all-button"        :impl "[button]"}
     {:name :middle-bottom-spacer      :level 3 :class "rc-multi-select-middle-bottom-spacer"      :impl "[box]"}
     {:name :right                     :level 2 :class "rc-multi-select-right"                     :impl "[v-box]"}
     {:name :warning-message           :level 3 :class "rc-multi-select-warning-message"           :impl "[label]"}
     {:name :right-label-container     :level 3 :class "rc-multi-select-right-label-container"     :impl "[h-box]"}
     {:name :right-label               :level 4 :class "rc-multi-select-right-label"               :impl "[:span]"}
     {:name :right-label-item-count    :level 4 :class "rc-multi-select-right-label-item-count"    :impl "[:span]"}
     {:name :right-list-box            :level 3 :class "rc-multi-select-right-list-box"            :impl "[list-box]"}
     {:name :filter-text-box           :level 3 :class "rc-multi-select-filter-text-box"           :impl "[h-box]"}
     {:name :filter-input-text         :level 4 :class "rc-multi-select-filter-input-text"         :impl "[input-text]"}
     {:name :filter-reset-button       :level 4 :class "rc-multi-select-filter-reset-button"       :impl "[close-button]"}
     {:name :right-filter-result-count :level 3 :class "rc-multi-select-right-filter-result-count" :impl "[label]"}]))

(def multi-select-css-spec
  {:main {:class ["rc-multi-select" "noselect" "chosen-container" "chosen-container-single"]
          :style (fn [{:keys [width]}]
                   (merge (box/flex-child-style (if width "0 0 auto" "auto"))
                          (box/align-style :align-self :start)
                          {:overflow "hidden"
                           :width width}))}
   :container {:class ["rc-multi-select-container"]}
   :left {:class ["rc-multi-select-left"]}
   :left-label-container {:class ["rc-multi-select-left-label-container"]}
   :left-label {:class ["rc-multi-select-left-label"]
                :style {:font-size "small"
                        :font-weight "bold"}}
   :left-label-item-count {:class ["rc-multi-select-left-label-item-count"]
                           :style {:font-size "smaller"}}
   :left-list-box {:class ["rc-multi-select-left-list-box"]}
   :filter-text-box {:class ["rc-multi-select-filter-text-box"]
                     :style {:position "relative"}}
   :filter-input-text {:class ["rc-multi-select-filter-input-text"]
                       :style {:padding "3px 4px"}}
   :filter-reset-button {:class ["rc-multi-select-filter-reset-button"]}
   :left-filter-result-count {:class ["rc-multi-select-left-filter-result-count"]
                              :style {:font-size "smaller"}}
   :middle-container {:class ["rc-multi-select-middle-container"]}
   :middle-spacer {:class ["rc-multi-select-middle-spacer"]}
   ;;TODO: cleanup: middle-[top|bottom]-spacer are not used. Should they go away, or should it be middle-spacer that goes?
   :middle-top-spacer {:class ["rc-multi-select-middle-top-spacer"]}
   :middle {:class ["rc-multi-select-middle"]}
   :button {:class ["rc-multi-select-button"]
            :style {:width        "86px"
                    :height       "24px"
                    :padding      "0px 8px 2px 8px"
                    :margin       "8px 6px"
                    :text-align   "left"
                    :font-variant "small-caps"
                    :font-size    11}}
   :button-icon {:class (fn [{:keys [icon]}]
                          ["zmdi" "zmdi-hc-fw-rc" (str "zmdi-" icon)])}
   :button-content {:style {:position "relative" :top "-1px"}}
   :include-all-button {:class ["rc-multi-select-include-all-button"]}
   :include-selected-button {:class ["rc-multi-select-include-selected-button"]}
   :exclude-selected-button {:class ["rc-multi-select-exclude-selected-button"]}
   :exclude-all-button {:class ["rc-multi-select-exclude-all-button"]}
   :middle-bottom-spacer {:class ["rc-multi-select-middle-bottom-spacer"]}
   :right {:class ["rc-multi-select-right"]
           :style {:position "relative"}}
   :warning-message {:class ["rc-multi-select-warning-message"]
                     :style (fn [{:keys [warning-message]}]
                              (when warning-message
                                {:color            "white"
                                 :background-color "green"
                                 :border-radius    "0px"
                                 :opacity            "0"
                                 :position           "absolute"
                                 :right              "0px"
                                 :z-index            1
                                 :height             "25px"
                                 :padding            "3px 6px"
                                 :animation-name     "rc-multi-select-fade-warning-msg"
                                 :animation-duration "5000ms"}))}
   :right-label-container {:class ["rc-multi-select-right-label-container"]}
   :right-label {:class ["rc-multi-select-right-label"]
                 :style {:font-size "small"
                         :font-weight "bold"}}
   :right-label-item-count {:class ["rc-multi-select-right-label-item-count"]
                            :style {:font-size "smaller"}}
   :right-list-box {:class ["rc-multi-select-right-list-box"]}
   :right-filter-result-count {:class ["rc-multi-select-right-filter-result-count"]
                               :style {:font-size "smaller"}}
   :group-heading-item {:class (fn [{:keys [selected? mouse-over?]}]
                           ["group-result" (if selected?
                                             "highlighted"
                                             (when mouse-over? "mouseover"))])
                        :style (fn [{:keys [disabled? selected?]}]
                                 (merge {:padding-left "6px"
                                         :cursor       (when-not disabled? "pointer")
                                         :color        (if selected? "white" "#444")}
                                        (when disabled?
                                          {:pointer-events "none"})))}
   :list-item {:class (fn [{:keys [selected? mouse-over? disabled?]}]
                        ["active-result" "group-option" (if (and selected? (not disabled?))
                                                          "highlighted"
                                                          (when mouse-over? "mouseover"))])
               :style (fn [{:keys [group-selected? disabled?]}]
                        (merge (when group-selected? {:background-color "hsl(208, 56%, 92%)"})
                               (when disabled? {:cursor         "default"
                                                :pointer-events "none"})))}
   ;;TODO: These class names look foreign, need review
   :list-box {:class (fn [{:keys [disabled?]}]
                       [(if disabled? "bm-multi-select-list-disabled" "bm-multi-select-list")])
              :style {:background-color "#fafafa"
                      :border           "1px solid #ccc"
                      :border-radius    "4px"}}
   :list-box-results {:class ["chosen-results"]
                      :style {:max-height "none"}} ;; Override the 240px in the class
   :list-box-no-results {:class ["no-results"]}})

(def multi-select-parts
  (when include-args-desc?
    (-> (map :name multi-select-parts-desc) set)))

(def multi-select-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                      :type "vector of maps | r/atom"  :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, label and, optionally, a group, are extracted by the functions " [:code ":id-fn"] ", " [:code ":label-fn"] " & " [:code ":group-fn"]  ". See below."]}
     {:name :id-fn              :required false :default :id        :type "map -> anything"          :validate-fn ifn?                        :description [:span "a function taking one argument (a map) and returns the unique identifier for that map. Called for each element in " [:code ":choices"]]}
     {:name :label-fn           :required false :default :label     :type "map -> string | hiccup"   :validate-fn ifn?                        :description [:span "a function taking one argument (a map) and returns the displayable label for that map. Called for each element in " [:code ":choices"]]}
     {:name :filter-fn          :required false :default "str∘label-fn" :type "map -> string"      :validate-fn ifn?                        :description [:span "a function taking one argument (a map) and returns the string to filter by. Called for each element in " [:code ":choices"] ". (Note: items are also filtered by group-fn)"]}
     {:name :group-fn           :required false :default :group     :type "map -> string | hiccup"   :validate-fn ifn?                        :description [:span "a function taking one argument (a map) and returns the group identifier for that map. Called for each element in " [:code ":choices"]]}
     {:name :sort-fn            :required false :default "compare"  :type "map, map -> integer"      :validate-fn ifn?                        :description [:span "The comparator function used with " [:code "cljs.core/sort-by"] " to sort choices."]}
     {:name :model              :required true                      :type "a set of ids | r/atom"                                             :description [:span "a set of the ids for currently selected choices. If nil, see " [:code ":placeholder"] "."]}
     {:name :required?          :required false :default false      :type "boolean | r/atom"                                                  :description "when true, at least one item must be selected"}
     {:name :max-selected-items :required false :default nil        :type "integer"                                                           :description "maximum number of items that can be selected"}
     {:name :left-label         :required false                     :type "string | hiccup"          :validate-fn string-or-hiccup?           :description "label displayed above the left list"}
     {:name :right-label        :required false                     :type "string | hiccup"          :validate-fn string-or-hiccup?           :description "label displayed above the right list"}
     {:name :on-change          :required true                      :type "id -> nil"                :validate-fn fn?                         :description [:span "a function that will be called when the selection changes. Passed the set of selected ids. See " [:code ":model"] "."]}
     {:name :disabled?          :required false :default false      :type "boolean | r/atom"                                                  :description "if true, no user selection is allowed"}
     {:name :filter-box?        :required false :default false      :type "boolean | r/atom"                                                  :description "if true, a filter text field is placed at the bottom of the component"}
     {:name :regex-filter?      :required false :default false      :type "boolean | r/atom"                                                  :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text"}
     {:name :placeholder        :required false                     :type "string"                   :validate-fn string?                     :description "background text when no selection"} ;; TODO this is actually broken, does not display background text
     {:name :width              :required false :default "100%"     :type "string"                   :validate-fn string?                     :description "the CSS width. e.g.: \"500px\" or \"20em\""}
     {:name :height             :required false                     :type "string"                   :validate-fn string?                     :description "the specific height of the component"}
     {:name :max-height         :required false                     :type "string"                   :validate-fn string?                     :description "the maximum height of the component"}
     {:name :tab-index          :required false                     :type "integer | string"         :validate-fn validate/number-or-string?  :description "component's tabindex. A value of -1 removes from the tab order"}
     {:name :class              :required false                     :type "string"                   :validate-fn string?                     :description "CSS class names, space separated"}
     {:name :style              :required false                     :type "CSS style map"            :validate-fn validate/css-style?         :description "CSS styles to add or override"}
     {:name :attr               :required false                     :type "HTML attr map"            :validate-fn validate/html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}
     {:name :parts              :required false                     :type "map"                      :validate-fn (parts? multi-select-parts) :description "See Parts section below."}
     {:name :src                :required false                     :type "map"                      :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as           :required false                     :type "map"                      :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn multi-select
  "Render a multi-select component which emulates the bootstrap-choosen style. Sample choices object:
  [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
   {:id \"US\" :label \"United States\"  :group \"Group 1\"}
   {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
   {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [model sort-fn src]
      :or   {sort-fn identity}
      :as   args}]
  "Internal glossary:
  LHS - choices    - comes from choices                 - the full list of items to select from
  RHS - selections - comes from model => internal-model - the selected items from choices collection
  "
  (or
    (validate-args-macro multi-select-args-desc args)
    (let [*external-model                    (reagent/atom (deref-or-value model)) ;; Holds the last known external value of model, to detect external model changes
          *internal-model                    (reagent/atom @*external-model) ;; Create a new atom from the model to be used internally
          *current-choice-id                 (reagent/atom nil)
          *current-selection-id              (reagent/atom nil)
          *choice-group-heading-selected?    (reagent/atom false)
          *selection-group-heading-selected? (reagent/atom false)
          *warning-message                   (reagent/atom nil)
          *filter-choices-text               (reagent/atom "")
          *filter-selections-text            (reagent/atom "")]
      (fn multi-select-render
        [& {:keys [choices model required? max-selected-items left-label right-label on-change disabled? filter-box? regex-filter?
                   placeholder width height max-height tab-index id-fn label-fn group-fn sort-fn filter-fn class style attr parts src]
            :or   {id-fn     :id
                   label-fn  :label
                   group-fn  :group
                   sort-fn   compare
                   required? false}
            :as   args}]
        (or
          (validate-args-macro multi-select-args-desc args)
          (let [required?              (deref-or-value required?)
                filter-box?            (deref-or-value filter-box?)
                regex-filter?          (deref-or-value regex-filter?)
                min-msg                "Must have at least one"
                max-msg                (str "Max items allowed is " max-selected-items)
                group-fn               (or group-fn ::$$$) ;; TODO: If nil is passed because of a when, this will prevent exceptions...smelly!
                choices                (set (deref-or-value choices))
                disabled?              (deref-or-value disabled?)
                regex-filter?          (deref-or-value regex-filter?)
                *latest-ext-model      (reagent/atom (deref-or-value model))
                _                      (when (not= @*external-model @*latest-ext-model) ;; Has model changed externally?
                                         (reset! *external-model @*latest-ext-model)
                                         (reset! *internal-model @*latest-ext-model))
                changeable?            (and on-change (not disabled?))
                excludable?            (and @*current-selection-id (> (count @*internal-model) (if required? 1 0)))
                choices-filter-fn      (if regex-filter?
                                         (filter-items-regex group-fn filter-fn @*filter-choices-text)
                                         (filter-items group-fn filter-fn @*filter-choices-text))
                filtered-choices       (into []
                                             (->> choices
                                                  (remove #(contains? @*internal-model (id-fn %)))
                                                  (filter choices-filter-fn)
                                                  (sort-by sort-fn)))
                selections             (into []
                                             (->> @*internal-model
                                                  (map #(rc.util/item-for-id % choices :id-fn id-fn))
                                                  (sort-by sort-fn)))
                selections-filter-fn   (if regex-filter?
                                         (filter-items-regex group-fn filter-fn @*filter-selections-text)
                                         (filter-items group-fn filter-fn @*filter-selections-text))
                filtered-selections    (into []
                                             (->> selections
                                                  (filter selections-filter-fn)
                                                  (sort-by sort-fn)))
                potential-count        (->> @*internal-model
                                            (set/difference (set (map id-fn choices)))
                                            count)
                chosen-count           (count selections)
                choice-click           (fn [id group-heading-selected?]
                                         (reset! *current-choice-id id)
                                         (reset! *choice-group-heading-selected? group-heading-selected?)
                                         (reset! *warning-message nil))
                selection-click        (fn [id group-heading-selected?]
                                         (reset! *current-selection-id id)
                                         (reset! *selection-group-heading-selected? group-heading-selected?)
                                         (reset! *warning-message nil))
                include-filtered-click #(do (if (and (some? max-selected-items) (> (+ (count @*internal-model) (count filtered-choices)) max-selected-items))
                                              (reset! *warning-message max-msg)
                                              (do
                                                (swap! *internal-model conj @*current-choice-id)
                                                (reset! *warning-message nil))))
                                          (when (and changeable? (not= @*internal-model @*latest-ext-model))
                                            (reset! *external-model @*internal-model)
                                            (on-change @*internal-model))
                                          (reset! *current-choice-id nil))
              exclude-click          #(do (if excludable?
                                            (if @*selection-group-heading-selected?
                                              (let [new-internal-model (->> filtered-selections
                                                                            (filter (fn [item] (= (first @*current-selection-id) (group-fn item))))
                                                                            (map id-fn)
                                                                            set
                                                                            (set/difference @*internal-model))]
                                                (if (and required? (empty? new-internal-model))
                                                  (do
                                                    (reset! *internal-model (hash-set (first @*internal-model)))
                                                    (reset! *warning-message min-msg))
                                                  (do
                                                    (reset! *internal-model new-internal-model)
                                                    (reset! *selection-group-heading-selected? false)
                                                    (reset! *warning-message nil))))
                                              (do
                                                (swap! *internal-model disj @*current-selection-id)
                                                (reset! *warning-message nil)))
                                            (reset! *warning-message min-msg))
                                          (when (and changeable? (not= @*internal-model @*latest-ext-model))
                                            (reset! *external-model @*internal-model)
                                            (on-change @*internal-model))
                                          (reset! *current-selection-id nil))
              exclude-filtered-click #(let [new-internal-model (set/difference @*internal-model (set (map id-fn filtered-selections)))]
                                        (if (and required? (zero? (count new-internal-model)))
                                          (do
                                            (reset! *internal-model (hash-set (first @*internal-model)))
                                            (reset! *warning-message min-msg))
                                          (do
                                            (reset! *internal-model new-internal-model)
                                            (reset! *warning-message nil)))
                                        (when (and changeable? (not= @*internal-model @*latest-ext-model))
                                          (reset! *external-model @*internal-model)
                                          (on-change @*internal-model))
                                        (reset! *current-selection-id nil))
                cmerger (merge-css multi-select-css-spec args)]
            [:div
             (merge
              (flatten-attr (cmerger :main {:width width}))
              (->attr args)) ;; Prevent user text selection
             (add-map-to-hiccup-call
              (cmerger :container)
              [box/h-box
               :src        (at)
               :height     height
               :max-height max-height
               :gap        "4px"
               :children   [(add-map-to-hiccup-call
                             (cmerger :left)
                             [box/v-box
                              :src      (at)
                              :size     "50%"
                              :gap      "4px"
                              :children [(when left-label
                                           (if (string? left-label)
                                             (add-map-to-hiccup-call
                                              (cmerger :left-label-container)
                                              [box/h-box
                                               :src      (at)
                                               :justify  :between
                                               :children [[:span
                                                           (flatten-attr (cmerger :left-label))
                                                           left-label]
                                                          [:span
                                                           (flatten-attr
                                                            (cmerger :left-label-item-count))
                                                           (if (string/blank? @*filter-choices-text)
                                                             (rc.util/pluralize potential-count "item")
                                                             (str "showing " (count filtered-choices) " of " potential-count))]]])
                                             left-label))
                                         (add-map-to-hiccup-call
                                          (cmerger :left-list-box)
                                          [list-box
                                           :src                     (at)
                                           :items                   filtered-choices
                                           :id-fn                   id-fn
                                           :label-fn                label-fn
                                           :group-fn                group-fn
                                           :disabled?               disabled?
                                           :*current-item-id        *current-choice-id
                                           :group-heading-selected? @*choice-group-heading-selected?
                                           :click-callback          choice-click
                                           :double-click-callback   include-click
                                           :filter-choices-text     @*filter-choices-text])
                                         (when filter-box?
                                           [:<>
                                            [box/gap
                                             :src  (at)
                                             :size "4px"]
                                            [filter-text-box *filter-choices-text placeholder *warning-message disabled? parts]
                                            [box/gap
                                             :src  (at)
                                             :size "4px"]
                                            (if (string/blank? @*filter-choices-text)
                                              [text/label
                                               :src   (at)
                                               :label (gstring/unescapeEntities "&nbsp;")
                                               :style {:font-size "smaller"}]
                                              (add-map-to-hiccup-call
                                               (cmerger :left-filter-result-count)
                                               [text/label
                                                :src   (at)
                                                :label [:span "Found " (rc.util/pluralize (count filtered-choices) "match" "matches") " containing " [:strong @*filter-choices-text]]]))])]])

                            (add-map-to-hiccup-call
                             (cmerger :middle-container)
                             [box/v-box
                              :src      (at)
                              :justify  :between
                              :children [(add-map-to-hiccup-call
                                          (cmerger :middle-spacer)
                                          [box/box
                                           :src   (at)
                                           :size  "0 1 22px" ;; 22 = (+ 18 4) - height of the top components
                                           :child ""])
                                         (add-map-to-hiccup-call
                                          (cmerger :middle)
                                          [box/v-box
                                           :src      (at)
                                           :justify  :center
                                           :children [(add-map-to-hiccup-call
                                                       (cmerger :include-all-button)
                                                       [multi-button
                                                        :src (at)
                                                        :label (str " include " (if (string/blank? @*filter-choices-text) potential-count (count filtered-choices)))
                                                        :icon "fast-forward"
                                                        :disabled? (or disabled? (zero? (count filtered-choices)))
                                                        :on-click  include-filtered-click])
                                                      (add-map-to-hiccup-call
                                                       (cmerger :include-selected-button)
                                                       [multi-button
                                                        :src (at)
                                                        :label (str " include " (when @*choice-group-heading-selected?
                                                                                 (->> filtered-choices ;; TODO: Inefficient
                                                                                      (filter (fn [item] (= (first @*current-choice-id) (group-fn item))))
                                                                                      count)))
                                                        :icon "play"
                                                        :disabled? (or disabled? (not @*current-choice-id))
                                                        :on-click  include-click])
                                                      (add-map-to-hiccup-call
                                                       (cmerger :exclude-selected-button)
                                                       [multi-button
                                                        :src (at)
                                                        :label (str " exclude " (when @*selection-group-heading-selected?
                                                                                 (->> filtered-selections ;; TODO: Inefficient
                                                                                      (filter (fn [item] (= (first @*current-selection-id) (group-fn item))))
                                                                                      count)))
                                                        ;;TODO: Zmdi reference should be in -css-spec
                                                        :icon "play zmdi-hc-rotate-180"
                                                        :disabled? (or disabled? (not excludable?))
                                                        :on-click  exclude-click])
                                                      (add-map-to-hiccup-call
                                                       (cmerger :exclude-all-button)
                                                       [multi-button
                                                        :src       (at)
                                                        :label (str " exclude " (if (string/blank? @*filter-selections-text) chosen-count (count filtered-selections)))
                                                        :icon "fast-rewind"
                                                        :disabled? (or disabled? (zero? (count filtered-selections)) (not (> (count @*internal-model) (if required? 1 0))))
                                                        :on-click  exclude-filtered-click])]])
                                         [box/box
                                          :src   (at)
                                          :size  (str "0 2 " (if filter-box? "55px" "0px")) ;; 55 = (+ 4 4 28 4 15) - height of the bottom components
                                        ;:style {:background-color "lightblue"}
                                          :child ""]]])
                            (add-map-to-hiccup-call
                             (cmerger :right)
                             [box/v-box
                              :src      (at)
                              :size     "50%"
                              :gap      "4px"
                              :children [^{:key (gensym)}
                                         (add-map-to-hiccup-call
                                          (cmerger :warning-message {:warning-message @*warning-message})
                                          [text/label
                                           :src   (at)
                                           :label @*warning-message])
                                         (when right-label
                                           (if (string? right-label)
                                             (add-map-to-hiccup-call
                                              (cmerger :right-label-container)
                                              [box/h-box
                                               :src      (at)
                                               :justify  :between
                                               :children [[:span
                                                           (flatten-attr (cmerger :right-label))
                                                           right-label]
                                                          [:span
                                                           (flatten-attr (cmerger :right-label-item-count))
                                                           (if (string/blank? @*filter-selections-text)
                                                             (rc.util/pluralize chosen-count "item")
                                                             (str "showing " (count filtered-selections) " of " chosen-count))]]])
                                             right-label))
                                         (add-map-to-hiccup-call
                                          (cmerger :right-list-box)
                                          [list-box
                                           :src                     (at)
                                           :items                   filtered-selections
                                           :id-fn                   id-fn
                                           :label-fn                label-fn
                                           :group-fn                group-fn
                                           :disabled?               disabled?
                                           :*current-item-id        *current-selection-id
                                           :group-heading-selected? @*selection-group-heading-selected?
                                           :click-callback          selection-click
                                           :double-click-callback   exclude-click
                                           :filter-choices-text     @*filter-selections-text])
                                         (when filter-box?
                                           [:<>
                                            [box/gap
                                             :src  (at)
                                             :size "4px"]
                                            [filter-text-box *filter-selections-text placeholder *warning-message disabled? parts]
                                            [box/gap
                                             :src  (at)
                                             :size "4px"]
                                            (if (string/blank? @*filter-selections-text)
                                              [text/label
                                               :src   (at)
                                               :label (gstring/unescapeEntities "&nbsp;")
                                               :style {:font-size "smaller"}]
                                              (add-map-to-hiccup-call
                                               (cmerger :right-filter-result-count)
                                               [text/label
                                                :src   (at)
                                                :label [:span "Found " (rc.util/pluralize (count filtered-selections) "match" "matches") " containing " [:strong @*filter-selections-text]]]))])]])]])]))))))
