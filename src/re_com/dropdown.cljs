(ns re-com.dropdown
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util      :refer [deref-or-value position-for-id item-for-id]]
            [clojure.string   :as    string]
            [re-com.validate  :refer [extract-arg-data validate-args vector-of-maps?]]
            [reagent.core     :as    reagent]))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select

(defn- move-to-new-choice
  "In a vector of maps (where each map has an :id), return the id of the choice offset posititions away
   from id (usually +1 or -1 to go to next/previous). Also accepts :start and :end"
  [choices id offset]
  (let [current-index (position-for-id id choices)
        new-index     (cond
                        (= offset :start) 0
                        (= offset :end) (dec (count choices))
                        (nil? current-index) 0
                        :else (mod (+ current-index offset) (count choices)))]
    (when current-index (:id (nth choices new-index)))))


(defn- choices-with-group-headings
  "If necessary, inserts group headings entries into the choices"
  [opts]
  (let [groups         (partition-by :group opts)
        group-headers  (->> groups
                            (map first)
                            (map :group)
                            (map #(hash-map :id % :group % :group-header? true)))]
    ;(if (= 1 (count groups))
    ;  opts
    ;  (flatten (interleave group-headers groups)))
    (flatten (interleave group-headers groups))))


(defn filter-choices
  "Filter a list of choices based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters"
  [choices filter-text]
  (let [lower-filter-text (string/lower-case filter-text)
        filter-fn         (fn [opt]
                            (let [group (if (nil? (:group opt)) "" (:group opt))
                                  label (str (:label opt))] ;; Need str for non-string labels like hiccup
                              (or
                                (>= (.indexOf (string/lower-case group) lower-filter-text) 0)
                                (>= (.indexOf (string/lower-case label) lower-filter-text) 0))))]
    (filter filter-fn choices)))


(defn filter-choices-regex
  "Filter a list of choices based on a filter string using regex's (case insensitive). More powerful but can cause
   confusion for users entering reserved characters such as [ ] * + . ( ) etc."
  [choices filter-text]
  (let [re        (try
                    (js/RegExp. filter-text "i")
                    (catch js/Object e nil))
        filter-fn (partial (fn [re opt]
                             (when-not (nil? re)
                               (or (.test re (:group opt)) (.test re (:label opt)))))
                           re)]
    (filter filter-fn choices)))


(defn filter-choices-by-keyword
  "Filter a list of choices extra data within the choices vector"
  [choices keyword value]
  (let [filter-fn (fn [opt] (>= (.indexOf (keyword opt) value) 0))]
    (filter filter-fn choices)))


(defn- choice-group-heading
  "Render a group choice item"
  [opt]
  [:li.group-result
   {:value (:value opt)}
   (:group opt)])


(defn- choice-item
  "Render a choice item and set up appropriate mouse events"
  [opt on-click internal-model]
  (let [mouse-over? (reagent/atom false)]
    (reagent/create-class
      {:component-did-mount
        (fn [event]
          (let [node     (reagent/dom-node event)
                selected (= @internal-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :component-did-update
        (fn [event]
          (let [node     (reagent/dom-node event)
                selected (= @internal-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :component-function
        (fn
          [opt on-click internal-model]
          (let [selected (= @internal-model (:id opt))
                class    (if selected
                           "highlighted"
                           (when @mouse-over? "mouseover"))]
            [:li
             {:class         (str "active-result group-option " class)
              :on-mouse-over (handler-fn (reset! mouse-over? true))
              :on-mouse-out  (handler-fn (reset! mouse-over? false))
              :on-mouse-down (handler-fn (on-click (:id opt)))}
             (:label opt)]))})))


(defn- filter-text-box-base
  "Base function (before lifecycle metadata) to render a filter text box"
  []
  (fn [filter-box? filter-text key-handler drop-showing?]
    [:div.chosen-search
     [:input
      {:type          "text"
       :auto-complete "off"
       :style         (when-not filter-box? {:position "absolute" ;; When no filter box required, use it but hide it off screen
                                            :left     "0px"
                                            :top      "-7770px"})
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
      [internal-model choices tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
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
            (:label (item-for-id @internal-model choices))
            placeholder)]
         [:div [:b]]])))) ;; This odd bit of markup produces the visual arrow on the right


;;--------------------------------------------------------------------------------------------------
;; Component: single-dropdown
;;--------------------------------------------------------------------------------------------------

(def single-dropdown-args-desc
  [{:name :choices         :required true                                   :type "vector of maps | atom"         :validate-fn vector-of-maps? :description "each has an :id, a :label and, optionally, a :group"}
   {:name :model           :required true                                   :type "an :id within :choices | atom"                              :description "the :id of the selected choice. If nil, :placeholder text is shown"}
   {:name :on-change       :required true                                   :type "(:id) -> nil"                  :validate-fn fn?             :description "called with one paramter: the :id of new selection"}
   {:name :disabled?       :required false :default false                   :type "boolean | atom"                                             :description "if true, no user selection is allowed"}
   {:name :filter-box?     :required false :default false                   :type "boolean"                                                    :description "if true, a filter text field is put at the top of the dropdown"}
   {:name :regex-filter?   :required false :default false                   :type "boolean | atom"                                             :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text"}
   {:name :placeholder     :required false                                  :type "string"                        :validate-fn string?         :description "text displayed if :model is 'nil'"}
   {:name :width           :required false :default "stretches"             :type "string"                        :validate-fn string?         :description "the CSS width. Eg: \"500px\" or \"20em\""}
   {:name :max-height      :required false :default "240px"                 :type "string"                        :validate-fn string?         :description "the maximum height the dropdown will occupy"}
   {:name :tab-index       :required false :default "use natural tab order" :type "number"                                                     :description "component's tabindex. A value of -1 removes from order"}
   {:name :class           :required false                                  :type "string"                        :validate-fn string?         :description "CSS classes (whitespace separated). Perhaps bootstrap like \"btn-info\" \"btn-small\""}
   {:name :style           :required false                                  :type "map"                           :validate-fn map?            :description "CSS styles"}
   {:name :attr            :required false                                  :type "map"                           :validate-fn map?            :description [:span "html attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(def single-dropdown-args (extract-arg-data single-dropdown-args-desc))

(defn single-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [model] :as args}]
  {:pre [(validate-args single-dropdown-args args "single-dropdown")]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
        drop-showing?  (reagent/atom false)
        filter-text    (reagent/atom "")]
    (fn [& {:keys [choices model on-change disabled? filter-box? regex-filter? placeholder width max-height tab-index class style attr] :as args}]
      {:pre [(validate-args single-dropdown-args args "single-dropdown")]}
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
                               (filter-choices-regex choices @filter-text)
                               (filter-choices choices @filter-text))
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
                                  (reset! internal-model (move-to-new-choice filtered-choices @internal-model -1))
                                  (reset! drop-showing? true))
                                true)
            press-down        (fn []
                                (if @drop-showing?  ;; Down arrow
                                  (reset! internal-model (move-to-new-choice filtered-choices @internal-model 1))
                                  (reset! drop-showing? true))
                                true)
            press-home        (fn []
                                (reset! internal-model (move-to-new-choice filtered-choices @internal-model :start))
                                true)
            press-end         (fn []
                                (reset! internal-model (move-to-new-choice filtered-choices @internal-model :end))
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
           {:class (str "rc-dropdown chosen-container chosen-container-single " (when @drop-showing? "chosen-container-active chosen-with-drop ") class)
            :style (merge {:flex                (if width "0 0 auto" "auto")
                           :align-self          "flex-start"
                           :width               (when width width)
                           :-webkit-user-select "none"}
                          style)}
           attr)          ;; Prevent user text selection
         [dropdown-top internal-model choices tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
         (when (and @drop-showing? (not disabled?))
           [:div.chosen-drop
            [filter-text-box filter-box? filter-text key-handler drop-showing?]
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-choices count pos?)
               (for [opt (choices-with-group-headings filtered-choices)]
                 (if (:group-header? opt)
                   ^{:key (str (:id opt))} [choice-group-heading opt]
                   ^{:key (str (:id opt))} [choice-item opt callback internal-model]))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))
