(ns re-com.dropdown
  ;(:require-macros [clairvoyant.core :refer [trace-forms]]) ;;Usage: (trace-forms {:tracer default-tracer} (your-code))
  (:require [re-com.util      :refer [deref-or-value find-map-index validate-arguments]]
            [clojure.string   :as    string]
            ;[clairvoyant.core :refer [default-tracer]]
            [reagent.core     :as    reagent]))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select


(defn- move-to-new-choice
  "In a vector of maps (where each map has an :id), return the id of the choice offset posititions away
   from id (usually +1 or -1 to go to next/previous). Also accepts :start and :end."
  [choices id offset]
  (let [current-index (find-map-index choices id)
        new-index (cond
                    (= offset :start) 0
                    (= offset :end) (dec (count choices))
                    (nil? current-index) 0
                    :else (mod (+ current-index offset) (count choices)))]
    (:id (nth choices new-index))))


(defn find-choice
  "In a vector of maps (where each map has an :id), return the first map containing the id parameter."
  [choices id]
  (let [current-index (find-map-index choices id)
        _ (assert ((complement nil?) current-index) (str "Can't find choice index '" id "' in choices vector"))]
    (nth choices current-index)))


(defn- choices-with-group-headings
  "If necessary, inserts group headings entries into the choices"
  [opts]
  (let [groups         (partition-by :group opts)
        group-headers  (->> groups
                            (map first)
                            (map :group)
                            (map #(hash-map :id % :group % :group-header? true)))]
    (if (= 1 (count groups))
      opts
      (flatten (interleave group-headers groups)))))


(defn filter-choices
  "Filter a list of choices based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters."
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
  "Filter a list of choices extra data within the choices vector."
  [choices keyword value]
  (let [filter-fn (fn [opt] (>= (.indexOf (keyword opt) value) 0))]
    (filter filter-fn choices)))


(defn- choice-group-heading
  "Render a group choice item."
  [opt]
  [:li.group-result
   {:value (:value opt)}
   (:group opt)])


(defn- choice-item
  "Render a choice item and set up appropriate mouse events."
  [opt on-click internal-model]
  (let [mouse-over? (reagent/atom false)]
    (reagent/create-class
      {:component-did-mount
        (fn [me]
          (let [node     (reagent/dom-node me)
                selected (= @internal-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :component-did-update
        (fn [me]
          (let [node     (reagent/dom-node me)
                selected (= @internal-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :render
        (fn []
          (let [selected (= @internal-model (:id opt))
                class    (if selected
                           "highlighted"
                           (when @mouse-over? "mouseover"))]
            [:li
             {:class         (str "active-result group-option " class)
              :on-mouse-over #(reset! mouse-over? true)
              :on-mouse-out  #(reset! mouse-over? false)
              :on-mouse-down #(on-click (:id opt))}
             (:label opt)]))})))


(defn- filter-text-box-base
  "Base function (before lifecycle metadata) to render a filter text box."
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
       :on-change     #(reset! filter-text (-> % .-target .-value))
       :on-blur       #(reset! drop-showing? false)
       :on-key-down   key-handler}]]))


(def ^:private filter-text-box
  "Render a filter text box."
  (with-meta filter-text-box-base
             {:component-did-mount #(let [node (.-firstChild (reagent/dom-node %))]
                                     (.focus node))
              :component-did-update #(let [node (.-firstChild (reagent/dom-node %))]
                                      (.focus node))}))

(defn- dropdown-top
  "Render the top part of the dropdown, with the clickable area and the up/down arrow."
  []
  (let [ignore-click (atom false)]
    (fn
      [internal-model choices tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
      (let [_ (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})]
        [:a.chosen-single.chosen-default
         {:href          "javascript:"   ;; Required to make this anchor appear in the tab order
          :tab-index     (when tab-index tab-index)
          :on-click      #(if @ignore-click
                           (reset! ignore-click false)
                           (dropdown-click))
          :on-mouse-down #(when @drop-showing? (reset! ignore-click true))
          :on-key-down   #(do
                           (key-handler %)
                           (when (= (.-which %) 13) (reset! ignore-click true)))} ;; Pressing enter on an anchor also triggers click event, which we don't want
         [:span
          (if @internal-model
            (:label (find-choice choices @internal-model))
            placeholder)]
         [:div [:b]]])))) ;; This odd bit of markup produces the visual arrow on the right


;;--------------------------------------------------------------------------------------------------
;; Component: single-dropdown
;;--------------------------------------------------------------------------------------------------

(def single-dropdown-desc
  [{:name :choices         :required true                       :type "vector of maps"          :description "Each has an :id, a :label and, optionally, a :group"}
   {:name :model           :required true                       :type "an :id within :choices"  :description "the :id of the selected choice. If nil, :placeholder text is shown"}
   {:name :on-change       :required true                       :type "(:id) -> nil"   :description "called with one paramter: the :id of new selection"}
   {:name :disabled?       :required false :default false       :type "boolean"     :description "if true, no user selection is allowed."}
   {:name :filter-box?     :required false :default false       :type "boolean"     :description "if true, a filter text field is put at the top of the dropdown."}
   {:name :regex-filter?   :required false :default false       :type "boolean"     :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text."}
   {:name :placeholder     :required false                      :type "string"      :description "text displayed if :model is 'nil'"}
   {:name :width           :required false :default "stretches" :type "string"      :description "the CSS width. Eg: \"500px\" or \"20em\""}
   {:name :max-height      :required false :default "240px"     :type "string"      :description "the maximum height the dropdown will occupy."}
   {:name :tab-index       :required false :default " use natural tab order" :type "nummber" :description "component's tabindex. A value of -1 removes from order."}])

(def single-dropdown-args
  (set (map :name single-dropdown-desc)))

(defn single-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style."
  [& {:keys [model] :as args}]
  {:pre [(validate-arguments single-dropdown-args (keys args))]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
        drop-showing?  (reagent/atom false)
        filter-text    (reagent/atom "")]
    (fn [& {:keys [choices model on-change disabled? filter-box? regex-filter? placeholder width max-height tab-index] :as args}]
      {:pre [(validate-arguments single-dropdown-args (keys args))]}
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
                                  (do                ;; Was (callback @internal-model) but needed a customised version
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
                                 filter-box?))] ;; Use this boolean to allow/prevent the key from being processed by the text box
        [:div
         {:class (str "rc-dropdown chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
          :style {:flex       (if width "0 0 auto" "auto")
                  :align-self "flex-start"
                  :width      (when width width)
                  :-webkit-user-select "none"}} ;; Prevent user text selection
         [dropdown-top internal-model choices tab-index placeholder dropdown-click key-handler filter-box? drop-showing?]
         (when (and @drop-showing? (not disabled?))
           [:div.chosen-drop
            [filter-text-box filter-box? filter-text key-handler drop-showing?]
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-choices count pos?)
               (for [opt (choices-with-group-headings filtered-choices)]
                 (if (:group-header? opt)
                   ^{:key (:id opt)} [choice-group-heading opt]
                   ^{:key (:id opt)} [choice-item opt callback internal-model]))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))
