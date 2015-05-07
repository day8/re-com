(ns re-com.dropdown
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util      :refer [deref-or-value position-for-id item-for-id]]
            [re-com.box       :refer [align-style flex-child-style]]
            [re-com.validate  :refer [vector-of-maps? css-style? html-attr? number-or-string?] :refer-macros [validate-args-macro]]
            [clojure.string   :as    string]
            [reagent.core     :as    reagent]))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select

(defn- move-to-new-choice
  "In a vector of maps (where each map has an :id), return the id of the choice offset posititions away
   from id (usually +1 or -1 to go to next/previous). Also accepts :start and :end"
  [choices id-fn id offset]
  (let [current-index (position-for-id id choices :id-fn id-fn)
        new-index     (cond
                        (= offset :start)    0
                        (= offset :end)      (dec (count choices))
                        (nil? current-index) 0
                        :else                (mod (+ current-index offset) (count choices)))]
    (when new-index (id-fn (nth choices new-index)))))


(defn- choices-with-group-headings
  "If necessary, inserts group headings entries into the choices"
  [opts group-fn]
  (let [groups         (partition-by group-fn opts)
        group-headers  (->> groups
                            (map first)
                            (map group-fn)
                            (map #(hash-map :id (gensym) :group %)))]
    [group-headers groups]))


(defn- filter-choices
  "Filter a list of choices based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters"
  [choices group-fn label-fn filter-text]
  (let [lower-filter-text (string/lower-case filter-text)
        filter-fn         (fn [opt]
                            (let [group (if (nil? (group-fn opt)) "" (group-fn opt))
                                  label (str (label-fn opt))] ;; Need str for non-string labels like hiccup
                              (or
                                (>= (.indexOf (string/lower-case group) lower-filter-text) 0)
                                (>= (.indexOf (string/lower-case label) lower-filter-text) 0))))]
    (filter filter-fn choices)))


(defn- filter-choices-regex
  "Filter a list of choices based on a filter string using regex's (case insensitive). More powerful but can cause
   confusion for users entering reserved characters such as [ ] * + . ( ) etc."
  [choices group-fn label-fn filter-text]
  (let [re        (try
                    (js/RegExp. filter-text "i")
                    (catch js/Object e nil))
        filter-fn (partial (fn [re opt]
                             (when-not (nil? re)
                               (or (.test re (group-fn opt)) (.test re (label-fn opt)))))
                           re)]
    (filter filter-fn choices)))


(defn filter-choices-by-keyword
  "Filter a list of choices extra data within the choices vector"
  [choices keyword value]
  (let [filter-fn (fn [opt] (>= (.indexOf (keyword opt) value) 0))]
    (filter filter-fn choices)))


(defn show-selected-item
  [node]
  (let [item-offset-top       (.-offsetTop node)
        item-offset-bottom    (+ item-offset-top (.-clientHeight node))
        parent                (.-parentNode node)
        parent-height         (.-clientHeight parent)
        parent-visible-top    (.-scrollTop parent)
        parent-visible-bottom (+ parent-visible-top parent-height)
        new-scroll-top        (cond
                                (> item-offset-bottom parent-visible-bottom) (max (- item-offset-bottom parent-height) 0)
                                (< item-offset-top parent-visible-top)       item-offset-top)]
    (when new-scroll-top (set! (.-scrollTop parent) new-scroll-top))))


(defn- make-group-heading
  "Render a group heading"
  [m]
  ^{:key (:id m)} [:li.group-result
                   (:group m)])


(defn- choice-item
  "Render a choice item and set up appropriate mouse events"
  [id label on-click internal-model]
  (let [mouse-over? (reagent/atom false)]
    (reagent/create-class
      {:component-did-mount
       (fn [this]
         (let [node (reagent/dom-node this)
               selected (= @internal-model id)]
           (when selected (show-selected-item node))))

       :component-did-update
       (fn [this]
         (let [node (reagent/dom-node this)
               selected (= @internal-model id)]
           (when selected (show-selected-item node))))

       :component-function
       (fn
         [id label on-click internal-model]
         (let [selected (= @internal-model id)
               class (if selected
                       "highlighted"
                       (when @mouse-over? "mouseover"))]
           [:li
            {:class         (str "active-result group-option " class)
             :on-mouse-over (handler-fn (reset! mouse-over? true))
             :on-mouse-out  (handler-fn (reset! mouse-over? false))
             :on-mouse-down (handler-fn (on-click id))}
            label]))})))


(defn make-choice-item
  [id-fn label-fn callback internal-model opt]
  (let [id (id-fn opt)
        label (label-fn opt)]
    ^{:key (str id)} [choice-item id label callback internal-model]))


(defn- filter-text-box-base
  "Base function (before lifecycle metadata) to render a filter text box"
  []
  (fn [filter-box? filter-text key-handler drop-showing?]
    [:div.chosen-search
     [:input
      {:type          "text"
       :auto-complete "off"
       :style         (when-not filter-box? {:position "absolute" ;; When no filter box required, use it but hide it off screen
                                             :width    "0px"      ;; The rest of these styles make the textbox invisible
                                             :padding  "0px"
                                             :border   "none"})
       :value         @filter-text
       :on-change     (handler-fn (reset! filter-text (-> event .-target .-value)))
       :on-key-down   (handler-fn (when-not (key-handler event)
                                    (.preventDefault event))) ;; When key-handler returns false, preventDefault
       :on-blur       (handler-fn (reset! drop-showing? false))}]]))


(def ^:private filter-text-box
  "Render a filter text box"
  (with-meta filter-text-box-base
             {:component-did-mount #(let [node (.-firstChild (reagent/dom-node %))]
                                     (.focus node))
              :component-did-update #(let [node (.-firstChild (reagent/dom-node %))]
                                      (.focus node))}))

(defn- dropdown-top
  "Render the top part of the dropdown, with the clickable area and the up/down arrow"
  []
  (let [ignore-click (atom false)]
    (fn
      [internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
      (let [_ (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})]
        [:a.chosen-single.chosen-default
         {:href          "javascript:"   ;; Required to make this anchor appear in the tab order
          :tab-index     (when tab-index tab-index)
          :on-click      (handler-fn
                           (if @ignore-click
                             (reset! ignore-click false)
                             (dropdown-click)))
          :on-mouse-down (handler-fn
                           (when @drop-showing?
                             (reset! ignore-click true)))  ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?) and removing the ignore-click stuff
          :on-key-down   (handler-fn
                           (key-handler event)
                           (when (= (.-which event) 13)  ;; Pressing enter on an anchor also triggers click event, which we don't want
                             (reset! ignore-click true)))  ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?) and removing the ignore-click stuff

          }
         [:span
          (if @internal-model
            (label-fn (item-for-id @internal-model choices :id-fn id-fn))
            placeholder)]
         [:div [:b]]])))) ;; This odd bit of markup produces the visual arrow on the right


;;--------------------------------------------------------------------------------------------------
;; Component: single-dropdown
;;--------------------------------------------------------------------------------------------------

(def single-dropdown-args-desc
  [{:name :choices       :required true                   :type "vector of maps | atom"         :validate-fn vector-of-maps?   :description "each has an :id, a :label and, optionally, a :group (list of maps also allowed)"}
   {:name :model         :required true                   :type "an :id within :choices | atom"                                :description "the :id of the selected choice. If nil, :placeholder text is shown"}
   {:name :on-change     :required true                   :type ":id -> nil"                    :validate-fn fn?               :description [:span "called when a new selection is made. Passed the " [:code ":id"] " of new selection"] }
   {:name :disabled?     :required false :default false   :type "boolean | atom"                                               :description "if true, no user selection is allowed"}
   {:name :filter-box?   :required false :default false   :type "boolean"                                                      :description "if true, a filter text field is placed at the top of the dropdown"}
   {:name :regex-filter? :required false :default false   :type "boolean | atom"                                               :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text"}
   {:name :placeholder   :required false                  :type "string"                        :validate-fn string?           :description "background text when no selection"}
   {:name :width         :required false :default "100%"  :type "string"                        :validate-fn string?           :description "the CSS width. e.g.: \"500px\" or \"20em\""}
   {:name :max-height    :required false :default "240px" :type "string"                        :validate-fn string?           :description "the maximum height of the dropdown part"}
   {:name :tab-index     :required false                  :type "integer | string"              :validate-fn number-or-string? :description "component's tabindex. A value of -1 removes from order"}
   {:name :id-fn         :required false :default :id     :type "map -> anything"               :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns the unique identifier for this dropdown entry"]}
   {:name :label-fn      :required false :default :label  :type "map -> string | hiccup"        :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns what should be displayed in this dropdown entry"]}
   {:name :group-fn      :required false :default :group  :type "map -> anything"               :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns the group identifier for this dropdown entry"]}
   {:name :class         :required false                  :type "string"                        :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style         :required false                  :type "CSS style map"                 :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr          :required false                  :type "HTML attr map"                 :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn single-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [model] :as args}]
  {:pre [(validate-args-macro single-dropdown-args-desc args "single-dropdown")]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
        drop-showing?  (reagent/atom false)
        filter-text    (reagent/atom "")]
    (fn [& {:keys [choices model on-change disabled? filter-box? regex-filter? placeholder width max-height tab-index id-fn label-fn group-fn class style attr]
            :or {id-fn :id label-fn :label group-fn :group}
            :as args}]
      {:pre [(validate-args-macro single-dropdown-args-desc args "single-dropdown")]}
      (let [choices          (deref-or-value choices)
            disabled?        (deref-or-value disabled?)
            regex-filter?    (deref-or-value regex-filter?)
            latest-ext-model (reagent/atom (deref-or-value model))
            _                (when (not= @external-model @latest-ext-model) ;; Has model changed externally?
                               (reset! external-model @latest-ext-model)
                               (reset! internal-model @latest-ext-model))
            changeable?      (and on-change (not disabled?))
            callback         #(do
                               (reset! internal-model %)
                               (when changeable? (on-change @internal-model))
                               (swap! drop-showing? not) ;; toggle to allow opening dropdown on Enter key
                               (reset! filter-text ""))
            cancel           #(do
                               (reset! drop-showing? false)
                               (reset! filter-text "")
                               (reset! internal-model @external-model))
            dropdown-click   #(when-not disabled?
                               (swap! drop-showing? not))
            filtered-choices (if regex-filter?
                               (filter-choices-regex choices group-fn label-fn @filter-text)
                               (filter-choices choices group-fn label-fn @filter-text))
            press-enter      (fn []
                               (if disabled?
                                 (cancel)
                                 (callback @internal-model))
                               true)
            press-escape      (fn []
                                (cancel)
                                true)
            press-tab         (fn []
                                (if disabled?
                                  (cancel)
                                  (do  ;; Was (callback @internal-model) but needed a customised version
                                    (when changeable? (on-change @internal-model))
                                    (reset! drop-showing? false)
                                    (reset! filter-text "")))
                                (reset! drop-showing? false)
                                true)
            press-up          (fn []
                                (if @drop-showing?  ;; Up arrow
                                  (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model -1))
                                  (reset! drop-showing? true))
                                true)
            press-down        (fn []
                                (if @drop-showing?  ;; Down arrow
                                  (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model 1))
                                  (reset! drop-showing? true))
                                true)
            press-home        (fn []
                                (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model :start))
                                true)
            press-end         (fn []
                                (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model :end))
                                true)
            key-handler      #(if disabled?
                               false
                               (case (.-which %)
                                 13 (press-enter)
                                 27 (press-escape)
                                 9  (press-tab)
                                 38 (press-up)
                                 40 (press-down)
                                 36 (press-home)
                                 35 (press-end)
                                 filter-box?))]  ;; Use this boolean to allow/prevent the key from being processed by the text box
        [:div
         (merge
           {:class (str "rc-dropdown chosen-container chosen-container-single noselect " (when @drop-showing? "chosen-container-active chosen-with-drop ") class)
            :style (merge (flex-child-style (if width "0 0 auto" "auto"))
                          (align-style :align-self :start)
                          {:width (when width width)}
                          style)}
           attr)          ;; Prevent user text selection
         [dropdown-top internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
         (when (and @drop-showing? (not disabled?))
           [:div.chosen-drop
            [filter-text-box filter-box? filter-text key-handler drop-showing?]
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-choices count pos?)
               (let [[group-names group-opt-lists] (choices-with-group-headings filtered-choices group-fn)
                     make-a-choice                 (partial make-choice-item id-fn label-fn callback internal-model)
                     make-choices                  #(map make-a-choice %1)
                     make-h-then-choices           (fn [h opts]
                                                     (cons (make-group-heading h)
                                                           (make-choices opts)))
                     has-no-group-names?           (nil? (:group (first group-names)))]
                 (if (and (= 1 (count group-opt-lists)) has-no-group-names?)
                   (make-choices (first group-opt-lists)) ;; one group means no headings
                   (apply concat (map make-h-then-choices group-names group-opt-lists))))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))
