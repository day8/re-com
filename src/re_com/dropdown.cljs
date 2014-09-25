(ns re-com.dropdown
  ;(:require-macros [clairvoyant.core :refer [trace-forms]]) ;;Usage: (trace-forms {:tracer default-tracer} (your-code))
  (:require
    [re-com.util  :as util]
    [clojure.string :as string]
    ;[clairvoyant.core :refer [default-tracer]]
    [reagent.core :as reagent]))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select


(defn find-option-index
  [options id]
  "In a vector of maps (where each map has an :id), return the index of the first map containing the id parameter.
   Returns nil if id not found."
  (let [index-fn (fn [index item] (when (= (:id item) id) index))
        index-of-id (first (keep-indexed index-fn options))]
    index-of-id))


(defn- move-to-new-option
  [options id offset]
  "In a vector of maps (where each map has an :id), return the id of the option offset posititions away
   from id (usually +1 or -1 to go to next/previous). Also accepts :start and :end."
  (let [current-index (find-option-index options id)
        new-index (cond
                    (= offset :start) 0
                    (= offset :end) (dec (count options))
                    (nil? current-index) 0
                    :else (mod (+ current-index offset) (count options)))]
    (:id (nth options new-index))))


(defn find-option
  [options id]
  "In a vector of maps (where each map has an :id), return the first map containing the id parameter."
  (let [current-index (find-option-index options id)
        _ (assert ((complement nil?) current-index) (str "Can't find option index '" id "' in options vector"))]
    (nth options current-index)))


(defn- options-with-headings
  [opts]
  "Converts the user specified data for the dropdown into a form that this code can better work with. The main thing it
   does is to split heading boundaries into two lines, one for the heading and one for the item."
  (let [new-opts   (atom [])
        last-group (atom nil)]
    (doall
      (for [opt opts]
        (let [new-group (not= (:group opt) @last-group)
              _         (reset! last-group (:group opt))]
          (when new-group
            (swap! new-opts conj {:id    (str (:id opt) "##")
                                  :group (:group opt)}))
          (swap! new-opts conj {:id    (:id opt)
                                :label (:label opt)}))))
    @new-opts))


(defn filter-options
  "Filter a list of options based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters."
  [options filter-text]
  (let [lower-filter-text (string/lower-case filter-text)
        filter-fn         (fn [opt]
                            (let [group (if (nil? (:group opt)) "" (:group opt))
                                  label (str (:label opt))] ;; Need str for non-string labels like hiccup
                              (or
                                (>= (.indexOf (string/lower-case group) lower-filter-text) 0)
                                (>= (.indexOf (string/lower-case label) lower-filter-text) 0))))]
    (filter filter-fn options)))


(defn filter-options-regex
  "Filter a list of options based on a filter string using regex's (case insensitive). More powerful but can cause
   confusion for users entering reserved characters such as [ ] * + . ( ) etc."
  [options filter-text]
  (let [re        (try
                    (js/RegExp. filter-text "i")
                    (catch js/Object e nil))
        filter-fn (partial (fn [re opt]
                             (when-not (nil? re)
                               (or (.test re (:group opt)) (.test re (:label opt)))))
                           re)]
    (filter filter-fn options)))


(defn filter-options-by-keyword
  "Filter a list of options extra data within the options vector."
  [options keyword value]
  (let [filter-fn (fn [opt] (>= (.indexOf (keyword opt) value) 0))]
    (filter filter-fn options)))


(defn- option-group-heading
  [opt]
  "Render a group option item."
  [:li.group-result
   {:value (:value opt)}
   (:group opt)])


(defn- option-item
  [opt on-click tmp-model]
  "Render an option item and set up appropriate mouse events."
  (let [mouse-over? (reagent/atom false)]
    (reagent/create-class
      {:component-did-mount
        (fn [me]
          (let [node     (reagent/dom-node me)
                selected (= @tmp-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :component-did-update
        (fn [me]
          (let [node     (reagent/dom-node me)
                selected (= @tmp-model (:id opt))]
            (when selected (.scrollIntoView node false))))

       :render
        (fn []
          (let [selected (= @tmp-model (:id opt))
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
  []
  "Base function (before lifecycle metadata) to render a filter text box."
  (fn [filter-box filter-text key-handler drop-showing?]
    [:div.chosen-search
     [:input
      {:type          "text"
       :auto-complete "off"
       :style         (when-not filter-box {:position "absolute" ;; When no filter box required, use it but hide it off screen
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
  []
  "Render the top part of the dropdown, with the clickable area and the up/down arrow."
  (let [ignore-click (atom false)]
    (fn
      [tmp-model options tab-index placeholder dropdown-click key-handler filter-box drop-showing?]
      (let [_ (reagent/set-state (reagent/current-component) {:filter-box filter-box})]
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
          (if @tmp-model
            (:label (find-option options @tmp-model))
            placeholder)]
         [:div [:b]]])))) ;; This odd bit of markup produces the visual arrow on the right


(defn single-dropdown
  [& {:keys [model]}]
  "Render a single dropdown component which emulates the bootstrap-choosen style."
  (let [tmp-model     (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model)) ;; Create a new atom from the model value passed in for use with keyboard actions
        ext-model     (reagent/atom @tmp-model) ;; Holds the last known external value of model, to detect external model changes
        drop-showing? (reagent/atom false)
        filter-text   (reagent/atom "")]
    (fn [& {:keys [options model on-select disabled filter-box regex-filter placeholder width max-height tab-index]}]
      (let [options          (if (satisfies? cljs.core/IDeref options) @options options)
            disabled         (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
            regex-filter     (if (satisfies? cljs.core/IDeref regex-filter) @regex-filter regex-filter)
            latest-model     (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model))
            _                (when (not= @ext-model @latest-model) ;; Has model changed externally?
                               (reset! ext-model @latest-model)
                               (reset! tmp-model @latest-model))
            changeable       (and on-select (not disabled))
            callback         #(do
                               (reset! tmp-model %)
                               (when changeable (on-select @tmp-model))
                               (reset! drop-showing? (not @drop-showing?)) ;; toggle to allow opening dropdown on Enter key
                               (reset! filter-text ""))
            cancel           #(do
                               (reset! drop-showing? false)
                               (reset! filter-text "")
                               (reset! tmp-model @ext-model)) ;; Was save-model
            dropdown-click   #(when-not disabled
                               (reset! drop-showing? (not @drop-showing?)))
            filtered-options (if regex-filter
                               (filter-options-regex options @filter-text)
                               (filter-options options @filter-text))
            press-enter      (fn []
                               (if disabled
                                 (cancel)
                                 (callback @tmp-model))
                               true)
            press-escape      (fn []
                                (cancel)
                                true)
            press-tab         (fn []
                                (if disabled
                                  (cancel)
                                  (do                ;; Was (callback @tmp-model) but needed a customised version
                                    (when changeable (on-select @tmp-model))
                                    (reset! drop-showing? false)
                                    (reset! filter-text "")))
                                (reset! drop-showing? false)
                                true)
            press-up          (fn []
                                (if @drop-showing?  ;; Up arrow
                                  (reset! tmp-model (move-to-new-option filtered-options @tmp-model -1))
                                  (reset! drop-showing? true))
                                true)
            press-down        (fn []
                                (if @drop-showing?  ;; Down arrow
                                  (reset! tmp-model (move-to-new-option filtered-options @tmp-model 1))
                                  (reset! drop-showing? true))
                                true)
            press-home        (fn []
                                (reset! tmp-model (move-to-new-option filtered-options @tmp-model :start))
                                true)
            press-end         (fn []
                                (reset! tmp-model (move-to-new-option filtered-options @tmp-model :end))
                                true)
            key-handler      #(if disabled
                               false
                               (case (.-which %)
                                 13 (press-enter)
                                 27 (press-escape)
                                 9  (press-tab)
                                 38 (press-up)
                                 40 (press-down)
                                 36 (press-home)
                                 35 (press-end)
                                 filter-box))] ;; Use this boolean to allow/prevent the key from being processed by the text box
        [:div
         {:class (str "chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
          :style {:flex  (if width (str "0 0 " width) "auto")
                  :width (when width width)
                  :-webkit-user-select "none"}} ;; Prevent user text selection
         [dropdown-top tmp-model options tab-index placeholder dropdown-click key-handler filter-box drop-showing?]
         (when (and @drop-showing? (not disabled))
           [:div.chosen-drop
            [filter-text-box filter-box filter-text key-handler drop-showing?]
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-options count pos?)
               (for [opt (options-with-headings filtered-options)]
                 (if (:group opt)
                   ^{:key (:id opt)} [option-group-heading opt]
                   ^{:key (:id opt)} [option-item opt callback tmp-model]))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))
