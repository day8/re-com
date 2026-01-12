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
   [re-com.theme                :as theme]
   [re-com.part :as p]
   [re-com.args :as args]
   [re-com.util                 :as rc.util :refer [deref-or-value]]
   [re-com.validate             :as validate :refer [string-or-hiccup? parts? css-class?]]
   [reagent.core                :as reagent]
   re-com.multi-select.theme
   [re-com.multi-select :as-alias ms]))

(defn items-with-group-headings
  "Split a list of maps by a group key then return both the group"
  [items group-fn id-fn]
  (let [groups        (partition-by group-fn items)
        group-headers (->> groups
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
  [{:keys                        [*filter-text placeholder *warning-message]
    {part :part-fn theme :theme} :re-com}]
  (part ::ms/filter-text-box
    {:theme theme
     :impl  box/h-box
     :props
     {:children
      [(part ::filter-input-text
         {:theme theme
          :impl  input-text
          :props {:class           "rc-multi-select-filter-input-text"
                  :model           *filter-text
                  :change-on-blur? false
                  :placeholder     placeholder
                  :on-change       #(do (reset! *filter-text %)
                                        (reset! *warning-message nil))}})
       (part ::filter-reset-button
         {:theme theme
          :impl  close-button/close-button
          :props {:on-click #(reset! *filter-text "")}})]}}))

(defn group-heading-item
  "Render a group heading and set up appropriate mouse events"
  []
  (let [*mouse-over? (reagent/atom false)]
    (fn group-heading-render
      [& {:keys [heading disabled? click-callback double-click-callback selected-item-id]}]
      (let [id        (:id heading)
            selected? (= selected-item-id id)
            class     (if selected?
                        "highlighted"
                        (when @*mouse-over? "mouseover"))]
        [:li.group-result
         {:class           class
          :style           (merge {:padding-left "6px"
                                   :cursor       (when-not disabled? "pointer")
                                   :color        (if selected? "white" "#444")}
                                  (when disabled?
                                    {:pointer-events "none"}))
          :on-mouse-over   (handler-fn (reset! *mouse-over? true))
          :on-mouse-out    (handler-fn (reset! *mouse-over? false))
          :on-click        (when-not disabled? (handler-fn (click-callback id true))) ;; true = group-heading item selected
          :on-double-click (when-not disabled? (handler-fn (double-click-callback id)))}
         (:group heading)]))))

(defn list-item
  "Render a list item and set up appropriate mouse events"
  []
  (let [*mouse-over? (reagent/atom false)]
    (fn list-item-render
      [& {:keys [item id-fn label-fn disabled? click-callback double-click-callback selected-item-id group-selected?]}]
      (let [id        (id-fn item)
            selected? (= id selected-item-id)
            class     (if (and selected? (not disabled?))
                        "highlighted"
                        (when @*mouse-over? "mouseover"))]
        [:li
         {:class           (str "active-result group-option " class)
          :style           (merge (when group-selected? {:background-color "hsl(208, 56%, 92%)"})
                                  (when disabled? {:cursor         "default"
                                                   :pointer-events "none"}))
          :on-mouse-over   (handler-fn (reset! *mouse-over? true))
          :on-mouse-out    (handler-fn (reset! *mouse-over? false))
          :on-click        (when-not disabled? (handler-fn (click-callback id false))) ;; false = group-heading item NOT selected
          :on-double-click (when-not disabled? (handler-fn (double-click-callback id)))}
         (label-fn item)]))))

(defn list-box
  "Render a list box which can be a single list or a grouped list"
  [& {:keys [items id-fn label-fn group-fn disabled? *current-item-id group-heading-selected? click-callback double-click-callback filter-choices-text src]}]
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
                                         (cons (make-group-heading-item heading) (make-items items)))]
    [box/box
     :src   src
     :size  "1"
     :class (if disabled? "bm-multi-select-list-disabled" "bm-multi-select-list")
     :style {:background-color "#fafafa"
             :border           "1px solid #ccc"
             :border-radius    "4px"}
     :child [:ul.chosen-results
             {:style {:max-height "none"}} ;; Override the 240px in the class
             (if (-> items count pos?)
               (if has-group-names?
                 (apply concat (doall (map make-heading-then-items group-names group-item-lists)))
                 (make-items (first group-item-lists)))
               (if (string/blank? filter-choices-text)
                 ""
                 [:li.no-results (str "No results match \"" filter-choices-text "\"")]))]]))

;;--------------------------------------------------------------------------------------------------
;; Component: multi-select
;;--------------------------------------------------------------------------------------------------

;; Set of candidates (sorted externally)
;; a set of ids

;; LHS: set of candidates with selected id set removed, sorted/grouped by fn
;; RHS: set of candidates selecting on id, sorted/grouped by fn

(def part-structure
  (when include-args-desc?
    [::container {:impl 'h-box}
     [::left {:impl 'v-box}
      [::left-label-container {:impl 'h-box}
       [::left-label {:tag :span}]
       [::left-label-item-count {:tag :span}]]
      [::left-list-box {:impl 'list-box}]
      [::filter-text-box {:impl 'h-box}
       [::filter-input-text {:impl 'input-text}]
       [::filter-reset-button {:impl 'close-button}]]
      [::left-filter-result-count {:impl 'label}]]
     [::middle-container {:impl 'v-box}
      [::middle-top-spacer {:impl 'box}]
      [::middle {:impl 'v-box}
       [::include-all-button {:impl 'button}]
       [::include-selected-button {:impl 'button}]
       [::exclude-selected-button {:impl 'button}]
       [::exclude-all-button {:impl 'button}]]
      [::middle-bottom-spacer {:impl 'box}]]
     [::right {:impl 'v-box}
      [::warning-message {:impl 'label}]
      [::right-label-container {:impl 'h-box}
       [::right-label {:tag :span}]
       [::right-label-item-count {:tag :span}]]
      [::right-list-box {:impl 'list-box}]
      [::filter-text-box {:impl 'h-box}
       [::filter-input-text {:impl 'input-text}]
       [::filter-reset-button {:impl 'close-button}]]
      [::right-filter-result-count {:impl 'label}]]]))

(def multi-select-parts-desc
  (when include-args-desc?
    (p/describe part-structure)))

(def multi-select-parts
  (when include-args-desc?
    (-> (map :name multi-select-parts-desc) set)))

(def multi-select-args-desc
  (when include-args-desc?
    (into
     [{:description
       [:span
        "Each map represents a choice. Values corresponding to id, label and, "
        "optionally, a group, are extracted by the functions "
        [:code ":id-fn"] ", " [:code ":label-fn"] " & " [:code ":group-fn"]
        ". See below."]
       :name        :choices
       :required    true
       :type        "vector of maps | r/atom"
       :validate-fn validate/vector-of-maps?}
      {:default     :id
       :description
       [:span
        "a function taking one argument (a map) and returns the unique identifier "
        "for that map. Called for each element in "
        [:code ":choices"]]
       :name        :id-fn
       :required    false
       :type        "map -> anything"
       :validate-fn ifn?}
      {:default     :label
       :description
       [:span
        "a function taking one argument (a map) and returns the displayable label "
        "for that map. Called for each element in "
        [:code ":choices"]]
       :name        :label-fn
       :required    false
       :type        "map -> string | hiccup"
       :validate-fn ifn?}
      {:default     "str∘label-fn"
       :description
       [:span
        "a function taking one argument (a map) and returns the string to filter by. "
        "Called for each element in "
        [:code ":choices"] ". (Note: items are also filtered by group-fn)"]
       :name        :filter-fn
       :required    false
       :type        "map -> string"
       :validate-fn ifn?}
      {:default     :group
       :description
       [:span
        "a function taking one argument (a map) and returns the group identifier "
        "for that map. Called for each element in "
        [:code ":choices"]]
       :name        :group-fn
       :required    false
       :type        "map -> string | hiccup"
       :validate-fn ifn?}
      {:default     "compare"
       :description [:span "The comparator function used with "
                     [:code "cljs.core/sort-by"] " to sort choices."]
       :name        :sort-fn
       :required    false
       :type        "map, map -> integer"
       :validate-fn ifn?}
      {:description [:span
                     "a set of the ids for currently selected choices. If nil, see "
                     [:code ":placeholder"] "."]
       :name        :model
       :required    true
       :type        "a set of ids | r/atom"}
      {:default     false
       :description "when true, at least one item must be selected"
       :name        :required?
       :required    false
       :type        "boolean | r/atom"}
      {:default     nil
       :description "maximum number of items that can be selected"
       :name        :max-selected-items
       :required    false
       :type        "integer"}
      {:description "label displayed above the left list"
       :name        :left-label
       :required    false
       :type        "string | hiccup"
       :validate-fn string-or-hiccup?}
      {:description "label displayed above the right list"
       :name        :right-label
       :required    false
       :type        "string | hiccup"
       :validate-fn string-or-hiccup?}
      {:description
       [:span
        "a function that will be called when the selection changes. "
        "Passed the set of selected ids. See "
        [:code ":model"] "."]
       :name        :on-change
       :required    true
       :type        "id -> nil"
       :validate-fn fn?}
      {:default     false
       :description "if true, no user selection is allowed"
       :name        :disabled?
       :required    false
       :type        "boolean | r/atom"}
      {:default  false
       :description
       "if true, a filter text field is placed at the bottom of the component"
       :name     :filter-box?
       :required false
       :type     "boolean | r/atom"}
      {:default  false
       :description
       [:span "if true, the filter text field will support JavaScript regular expressions. "
        "If false, just plain text"]
       :name     :regex-filter?
       :required false
       :type     "boolean | r/atom"}
      ;; TODO this is actually broken, does not display background text
      {:description "background text when no selection"
       :name        :placeholder
       :required    false
       :type        "string"
       :validate-fn string?}
      {:default     "100%"
       :description "the CSS width. e.g.: \"500px\" or \"20em\""
       :name        :width
       :required    false
       :type        "string"
       :validate-fn string?}
      {:description "the specific height of the component"
       :name        :height
       :required    false
       :type        "string"
       :validate-fn string?}
      {:description "the maximum height of the component"
       :name        :max-height
       :required    false
       :type        "string"
       :validate-fn string?}
      {:description "component's tabindex. A value of -1 removes from the tab order"
       :name        :tab-index
       :required    false
       :type        "integer | string"
       :validate-fn validate/number-or-string?}
      {:description "See Parts section below."
       :name        :parts
       :required    false
       :type        "map"
       :validate-fn (parts? multi-select-parts)}]
     args/std)))

(defn multi-select
  "Render a multi-select component which emulates the bootstrap-choosen style. Sample choices object:
  [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
   {:id \"US\" :label \"United States\"  :group \"Group 1\"}
   {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
   {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [model pre theme sort-fn src]
      :or   {sort-fn identity}
      :as   args}]
  "Internal glossary:
  LHS - choices    - comes from choices                 - the full list of items to select from
  RHS - selections - comes from model => internal-model - the selected items from choices collection
  "
  (or
   (validate-args-macro multi-select-args-desc args)
   (let [theme                              (theme/comp pre theme)
         *external-model                    (reagent/atom (deref-or-value model)) ;; Holds the last known external value of model, to detect external model changes
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
        (let [part                   (partial p/part part-structure args)
              required?              (deref-or-value required?)
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
              filter-fn              (or filter-fn (comp str label-fn))
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
                                              (reset! *internal-model (set (concat @*internal-model (map id-fn filtered-choices))))
                                              (reset! *warning-message nil)))
                                          (when (and changeable? (not= @*internal-model @*latest-ext-model))
                                            (reset! *external-model @*internal-model)
                                            (on-change @*internal-model))
                                          (reset! *current-choice-id nil))
              include-click          #(do (if @*choice-group-heading-selected?
                                            (let [choices-to-include (->> filtered-choices
                                                                          (filter (fn [item] (= (first @*current-choice-id) (group-fn item))))
                                                                          (map id-fn) ;; TODO: Need to realise map output for prod build (dev doesn't need it). Why?
                                                                          set)]       ;; TODO: See https://github.com/day8/apps-lib/issues/35
                                              (if (and (some? max-selected-items) (> (+ (count @*internal-model) (count choices-to-include)) max-selected-items))
                                                (reset! *warning-message max-msg)
                                                (do
                                                  (reset! *internal-model (set (concat @*internal-model choices-to-include)))
                                                  (reset! *choice-group-heading-selected? false))))
                                            (if (and (some? max-selected-items) (>= (count @*internal-model) max-selected-items))
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
                                        (reset! *current-selection-id nil))]
          (part ::container
            {:theme      theme
             :post-props {:attr  (merge (->attr args) attr)
                          :class class
                          :style (merge style
                                        {:width width}
                                        (box/flex-child-style
                                         (if width "0 0 auto" "auto")))}
             :props
                 ;; Prevent user text selection
             {:children [(part ::inner-container
                           {:theme      theme
                            :impl       box/h-box
                            :post-props {:height     height
                                         :max-height max-height}
                            :props
                            {:src (at)
                             :children
                             [(part ::left
                                {:theme theme
                                 :impl  box/v-box
                                 :props
                                 {:src (at)
                                  :children
                                  [(when left-label
                                     (if-not (string? left-label)
                                       left-label
                                       (part ::left-label-container
                                         {:theme theme
                                          :impl  box/h-box
                                          :props
                                          {:src (at)
                                           :children
                                           [(part ::left-label
                                              {:theme theme
                                               :props {:tag      :span
                                                       :children [left-label]}})
                                            (part ::left-label-item-count
                                              {:theme theme
                                               :props
                                               {:tag :span
                                                :children
                                                (if (string/blank? @*filter-choices-text)
                                                  [(rc.util/pluralize potential-count "item")]
                                                  [(str "showing " (count filtered-choices) " of " potential-count)])}})]}})))
                                   (part ::left-list-box
                                     {:theme theme
                                      :impl  list-box
                                      :props
                                      {:src                     (at)
                                       :items                   filtered-choices
                                       :id-fn                   id-fn
                                       :label-fn                label-fn
                                       :group-fn                group-fn
                                       :disabled?               disabled?
                                       :*current-item-id        *current-choice-id
                                       :group-heading-selected? @*choice-group-heading-selected?
                                       :click-callback          choice-click
                                       :double-click-callback   include-click
                                       :filter-choices-text     @*filter-choices-text}})
                                   (when filter-box?
                                     [:<>
                                      [box/gap
                                       :src  (at)
                                       :size "4px"]
                                      [filter-text-box {:re-com           {:part-fn part
                                                                           :theme   theme}
                                                        :*filter-text     *filter-choices-text
                                                        :*warning-message *warning-message
                                                        :disabled?        disabled?
                                                        :placeholder      placeholder}]
                                      [box/gap
                                       :src  (at)
                                       :size "4px"]
                                      (if (string/blank? @*filter-choices-text)
                                        [text/label
                                         :src   (at)
                                         :label (gstring/unescapeEntities "&nbsp;")
                                         :style {:font-size "smaller"}]
                                        (part ::ms/left-filter-result-count
                                          {:theme theme
                                           :impl  text/label
                                           :props {:src   (at)
                                                   :label [:span "Found " (rc.util/pluralize
                                                                           (count filtered-choices)
                                                                           "match"
                                                                           "matches")
                                                           " containing "
                                                           [:strong @*filter-choices-text]]}}))])]}})
                              (part ::middle-container
                                {:impl box/v-box
                                 :props
                                 {:src     (at)
                                  :children
                                  [(part ::middle-spacer
                                     {:theme theme
                                      :impl  box/box
                                      :props {:src (at)
                                              :child ""}})
                                   (part ::middle
                                     {:theme theme
                                      :impl  box/v-box
                                      :props
                                      {:src     (at)
                                       :children
                                       [(part ::include-all-button
                                          {:theme theme
                                           :impl  buttons/button
                                           :props
                                           {:src       (at)
                                            :label     [:span
                                                        [:i {:class (theme/merge-class "zmdi" "zmdi-hc-fw-rc" "zmdi-fast-forward")}]
                                                        [:span
                                                         {:style {:position "relative" :top "-1px"}}
                                                         (str " include " (if (string/blank? @*filter-choices-text)
                                                                            potential-count
                                                                            (count filtered-choices)))]]
                                            :disabled? (or disabled? (zero? (count filtered-choices)))
                                            :on-click  include-filtered-click}})
                                        (part ::include-selected-button
                                          {:theme theme
                                           :impl  buttons/button
                                           :props
                                           {:src       (at)
                                            :label     [:span
                                                        [:i {:class (theme/merge-class "zmdi" "zmdi-hc-fw-rc" "zmdi-play")}]
                                                        [:span
                                                         {:style {:position "relative" :top "-1px"}}
                                                         (str " include " (when @*choice-group-heading-selected?
                                                                            (->> filtered-choices ;; TODO: Inefficient
                                                                                 (filter (fn [item] (= (first @*current-choice-id) (group-fn item))))
                                                                                 count)))]]
                                            :disabled? (or disabled? (not @*current-choice-id))
                                            :on-click  include-click}})
                                        (part ::exclude-selected-button
                                          {:theme theme
                                           :impl  buttons/button
                                           :props {:src       (at)
                                                   :label     [:span
                                                               [:i {:class (theme/merge-class "zmdi" "zmdi-hc-fw-rc" "zmdi-play" "zmdi-hc-rotate-180")}]
                                                               [:span
                                                                {:style {:position "relative" :top "-1px"}}
                                                                (str " exclude " (when @*selection-group-heading-selected?
                                                                                   (->> filtered-selections ;; TODO: Inefficient
                                                                                        (filter (fn [item] (= (first @*current-selection-id) (group-fn item))))
                                                                                        count)))]]
                                                   :disabled? (or disabled? (not excludable?))
                                                   :on-click  exclude-click}})
                                        (part ::exclude-all-button
                                          {:theme theme
                                           :impl  buttons/button
                                           :props
                                           {:src       (at)
                                            :label     [:span
                                                        [:i {:class (theme/merge-class "zmdi" "zmdi-hc-fw-rc" "zmdi-fast-rewind")}]
                                                        [:span
                                                         {:style {:position "relative" :top "-1px"}}
                                                         (str " exclude " (if (string/blank? @*filter-selections-text) chosen-count (count filtered-selections)))]]
                                            :disabled? (or disabled?
                                                           (zero? (count filtered-selections))
                                                           (not (> (count @*internal-model) (if required? 1 0))))
                                            :on-click  exclude-filtered-click}})]}})
                                   [box/box
                                    :src   (at)
                                    :size  (str "0 2 " (if filter-box? "55px" "0px")) ;; 55 = (+ 4 4 28 4 15) - height of the bottom components
                                        ;:style {:background-color "lightblue"}
                                    :child ""]]}})
                              (part ::right
                                {:theme theme
                                 :impl  box/v-box
                                 :props
                                 {:src   (at)
                                  :children
                                  [^{:key (gensym)}
                                   (part ::warning-message
                                     {:theme theme
                                      :impl  text/label
                                      :props
                                      {:src   (at)
                                       :label @*warning-message
                                       :style (when @*warning-message
                                                {:color              "white"
                                                 :background-color   "green"
                                                 :border-radius      "0px"
                                                 :opacity            "0"
                                                 :position           "absolute"
                                                 :right              "0px"
                                                 :z-index            1
                                                 :height             "25px"
                                                 :padding            "3px 6px"
                                                 :animation-name     "rc-multi-select-fade-warning-msg"
                                                 :animation-duration "5000ms"})}})
                                   (when right-label
                                     (if (string? right-label)
                                       (part ::right-label-container
                                         {:theme theme
                                          :impl  box/h-box
                                          :props
                                          {:src     (at)
                                           :justify :between
                                           :children
                                           [(part ::right-label
                                              {:theme theme
                                               :props {:tag      :span
                                                       :children [right-label]}})
                                            (part ::right-label-item-count
                                              {:theme theme
                                               :props {:tag      :span
                                                       :children [(if (string/blank? @*filter-selections-text)
                                                                    (rc.util/pluralize chosen-count "item")
                                                                    (str "showing " (count filtered-selections) " of " chosen-count))]}})]}})
                                       right-label))
                                   (part ::right-list-box
                                     {:theme theme
                                      :impl  list-box
                                      :props
                                      {:src                     (at)
                                       :items                   filtered-selections
                                       :id-fn                   id-fn
                                       :label-fn                label-fn
                                       :group-fn                group-fn
                                       :disabled?               disabled?
                                       :*current-item-id        *current-selection-id
                                       :group-heading-selected? @*selection-group-heading-selected?
                                       :click-callback          selection-click
                                       :double-click-callback   exclude-click
                                       :filter-choices-text     @*filter-selections-text}})
                                   (when filter-box?
                                     [:<>
                                      [box/gap :size "4px"]
                                      [filter-text-box {:*filter-text     *filter-selections-text
                                                        :placeholder      placeholder
                                                        :*warning-message *warning-message
                                                        :disabled?        disabled?
                                                        :parts            parts
                                                        :re-com           {:theme theme :part-fn part}}]
                                      [box/gap :size "4px"]
                                      (if (string/blank? @*filter-selections-text)
                                        [text/label
                                         :src   (at)
                                         :label (gstring/unescapeEntities "&nbsp;")
                                         :style {:font-size "smaller"}]
                                        (part ::ms/right-filter-result-count
                                          {:theme      theme
                                           :impl       text/label
                                           :post-props {:src (at)}
                                           :props      {:label [:span "Found " (rc.util/pluralize (count filtered-selections) "match" "matches")
                                                                " containing " [:strong @*filter-selections-text]]}}))])]}})]}})]}})))))))
