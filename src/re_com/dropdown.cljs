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
    (when (and new-index (pos? (count choices)))
      (id-fn (nth choices new-index)))))


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
                                  label (str (label-fn opt))]
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
  [id label on-click internal-model current-item multi?]
  (let [selected? #((if multi? contains? =) % id)]
    (reagent/create-class
      {:component-did-mount
                     (fn [this]
                       (let [node (reagent/dom-node this)]
                         (when (selected? @internal-model) (show-selected-item node))))

       :component-did-update
                     (fn [this]
                       (let [node (reagent/dom-node this)]
                         (when (selected? @internal-model) (show-selected-item node))))

       :display-name "choice-item"

       :reagent-render
                     (fn
                       [id label on-click internal-model]
                       (let [class (if (selected? @internal-model)
                                     "highlighted"
                                     (when (= id @current-item) "mouseover"))]
                         [:li
                          {:class         (str "active-result group-option " class)
                           :on-mouse-over (handler-fn (reset! current-item id))
                           :on-mouse-out  (handler-fn (reset! current-item id))
                           :on-mouse-down (handler-fn (on-click id))}
                          label]))})))


(defn make-choice-item
  [id-fn render-fn callback internal-model current-item multi? opt]
  (let [id (id-fn opt)
        markup (render-fn opt)]
    ^{:key (str id)} [choice-item id markup callback internal-model current-item multi?]))


(defn- filter-text-box-base
  "Base function (before lifecycle metadata) to render a filter text box"
  []
  (fn [filter-box? filter-text key-handler drop-showing?]
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
      :on-blur       (handler-fn (reset! drop-showing? false))}]))


(def ^:private filter-text-box
  "Render a filter text box"
  (with-meta filter-text-box-base
             {:component-did-mount #(let [node (reagent/dom-node %)]
                                     (.focus node))
              :component-did-update #(let [node (reagent/dom-node %)]
                                      (.focus node))}))

(defn- tag-item
  "Renders a removable tag from a multi choice dropdown"
  [internal-model id-fn label-fn remove-callback ignore-click current-item item]
  (let [id (id-fn item)]
    [:li.search-choice
     [:span (label-fn item)]
     [:a.search-choice-close {:on-click (handler-fn (reset! ignore-click true)
                                                    (remove-callback id))}]]))

(defn- dropdown-top
  "Render the top part of the dropdown, with the clickable area and the up/down arrow"
  []
  (let [ignore-click (atom false)]
    (fn
      [internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-text remove-callback current-item filter-box? drop-showing? title? multi?]
      (let [_    (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})]
        [(if multi? :ul.chosen-choices :a.chosen-single.chosen-default)
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
                           (when-not multi?
                             (key-handler event))
                           (when (= (.-which event) 13)  ;; Pressing enter on an anchor also triggers click event, which we don't want
                             (reset! ignore-click true)))  ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?) and removing the ignore-click stuff
          }
         (if multi?
           (let [make-tag-item (partial tag-item internal-model id-fn label-fn remove-callback ignore-click current-item)
                 make-tags #(map make-tag-item %)]
             (make-tags (map (comp first #(get (group-by id-fn choices) %)) @internal-model)))

           (let [text (if @internal-model
                        (label-fn (item-for-id @internal-model choices :id-fn id-fn))
                        placeholder)]
             [:span (when title?
                      {:title text})
              text]))

         (if multi?
           [:li.search-field
            (when (empty? @internal-model) [:span {:style {:color "#777"}} placeholder])
            [filter-text-box-base filter-box? filter-text key-handler drop-showing?]]
           [:div [:b]])])))) ;; This odd bit of markup produces the visual arrow on the right


;;--------------------------------------------------------------------------------------------------
;; Component: dropdown
;;--------------------------------------------------------------------------------------------------

(def dropdown-args-desc
  [{:name :choices          :required true                   :type "vector of choices | atom"                 :validate-fn vector-of-maps?   :description [:span "Each is expected to have an id, label and, optionally, a group, provided by " [:code ":id-fn"] ", " [:code ":label-fn"] " & " [:code ":group-fn"]]}
   {:name :model            :required true                   :type "the id of a choice | set of ids | atom"                                  :description [:span "the id or set of ids of the selected choice(s). If nil, " [:code ":placeholder"] " text is shown"]}
   {:name :on-change        :required true                   :type "id -> nil"                                :validate-fn fn?               :description [:span "called when a new choice is selected. Passed the id of new choice"] }
   {:name :id-fn            :required false :default :id     :type "choice -> anything"                       :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns its unique identifier (aka id)"]}
   {:name :label-fn         :required false :default :label  :type "choice -> string"                         :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns its displayable label."]}
   {:name :group-fn         :required false :default :group  :type "choice -> anything"                       :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns its group identifier"]}
   {:name :render-fn        :required false                  :type "choice -> string | hiccup"                :validate-fn ifn?              :description [:span "given an element of " [:code ":choices"] ", returns the markup that will be rendered for that choice. Defaults to the label if no custom markup is required."]}
   {:name :disabled?        :required false :default false   :type "boolean | atom"                                                          :description "if true, no user selection is allowed"}
   {:name :filter-box?      :required false :default false   :type "boolean"                                                                 :description "if true, a filter text field is placed at the top of the dropdown"}
   {:name :multi?           :required false :default false   :type "boolean"                                                                 :description "if true, becomes a multi select dropdown"}
   {:name :remove-selected? :required false :default false   :type "boolean"                                                                 :description "if true, removes selected options from available choices"}
   {:name :regex-filter?    :required false :default false   :type "boolean | atom"                                                          :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text"}
   {:name :placeholder      :required false                  :type "string"                                   :validate-fn string?           :description "background text when no selection"}
   {:name :title?           :required false :default false   :type "boolean"                                                                 :description "if true, allows the title for the selected dropdown to be displayed via a mouse over. Handy when dropdown width is small and text is truncated"}
   {:name :width            :required false :default "100%"  :type "string"                                   :validate-fn string?           :description "the CSS width. e.g.: \"500px\" or \"20em\""}
   {:name :max-height       :required false :default "240px" :type "string"                                   :validate-fn string?           :description "the maximum height of the dropdown part"}
   {:name :tab-index        :required false                  :type "integer | string"                         :validate-fn number-or-string? :description "component's tabindex. A value of -1 removes from order"}
   {:name :class            :required false                  :type "string"                                   :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style            :required false                  :type "CSS style map"                            :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr             :required false                  :type "HTML attr map"                            :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn dropdown
  "Render a dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [model] :as args}]
  {:pre [(validate-args-macro dropdown-args-desc args "dropdown")]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
        drop-showing?  (reagent/atom false)
        filter-text    (reagent/atom "")
        current-item   (reagent/atom nil)]
    (fn [& {:keys [choices model on-change disabled? filter-box? multi? remove-selected? regex-filter? placeholder width max-height tab-index id-fn label-fn group-fn render-fn class style attr title?]
            :or {id-fn :id label-fn :label group-fn :group render-fn label-fn}
            :as args}]
      {:pre [(validate-args-macro dropdown-args-desc args "dropdown")]}
      (let [choices          (deref-or-value choices)
            disabled?        (deref-or-value disabled?)
            regex-filter?    (deref-or-value regex-filter?)
            latest-ext-model (reagent/atom (deref-or-value model))
            filter-box?      (or multi? filter-box?)
            _                (when (not= @external-model @latest-ext-model) ;; Has model changed externally?
                               (reset! external-model @latest-ext-model)
                               (reset! internal-model @latest-ext-model))
            changeable?      (and on-change (not disabled?))
            callback         #(do
                                (if multi?
                                  (if (contains? @internal-model %)
                                    (swap! internal-model disj %)
                                    (swap! internal-model conj %))
                                  (reset! internal-model %))
                               (when (and changeable? (not= @internal-model @latest-ext-model))
                                 (on-change @internal-model))
                               (swap! drop-showing? not) ;; toggle to allow opening dropdown on Enter key
                               (reset! filter-text ""))
            remove-callback  #(do
                                (swap! internal-model disj %)
                                (when (and changeable? (not= @internal-model @latest-ext-model))
                                  (on-change @internal-model)))
            cancel           #(do
                               (reset! drop-showing? false)
                               (reset! filter-text "")
                               (reset! internal-model @external-model))
            dropdown-click   #(when-not disabled?
                               (swap! drop-showing? not))
            remaining-choices (if remove-selected?
                                (remove #((if multi? contains? =) @internal-model (id-fn %)) choices)
                                choices)
            filtered-choices (if regex-filter?
                               (filter-choices-regex remaining-choices group-fn label-fn @filter-text)
                               (filter-choices remaining-choices group-fn label-fn @filter-text))
            press-enter      (fn []
                               (if disabled?
                                 (cancel)
                                 (if multi?
                                   (when @current-item
                                     (callback @current-item))
                                   (callback @internal-model)))
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
            item-for-moving   (if multi? current-item internal-model)
            press-up          (fn []
                                (if @drop-showing?  ;; Up arrow
                                  (reset! item-for-moving (move-to-new-choice filtered-choices id-fn @item-for-moving -1))
                                  (reset! drop-showing? true))
                                true)
            press-down        (fn []
                                (if @drop-showing?  ;; Down arrow
                                  (reset! item-for-moving (move-to-new-choice filtered-choices id-fn @item-for-moving 1))
                                  (reset! drop-showing? true))
                                true)
            press-home        (fn []
                                (reset! item-for-moving (move-to-new-choice filtered-choices id-fn @item-for-moving :start))
                                true)
            press-end         (fn []
                                (reset! item-for-moving (move-to-new-choice filtered-choices id-fn @item-for-moving :end))
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
           {:class (str "rc-dropdown chosen-container noselect "
                        (if multi? "chosen-container-multi " "chosen-container-single ")
                        (when @drop-showing? "chosen-container-active chosen-with-drop ") class)
            :style (merge (flex-child-style (if width "0 0 auto" "auto"))
                          (align-style :align-self :start)
                          {:width (when width width)}
                          style)}
           attr)          ;; Prevent user text selection
         [dropdown-top internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-text remove-callback current-item filter-box? drop-showing? title? multi?]
         (when (and @drop-showing? (not disabled?))
           [:div.chosen-drop
            (when-not multi? [:div.chosen-search [filter-text-box filter-box? filter-text key-handler drop-showing?]])
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-choices count pos?)
               (let [[group-names group-opt-lists] (choices-with-group-headings filtered-choices group-fn)
                     make-a-choice                 (partial make-choice-item id-fn render-fn callback internal-model current-item multi?)
                     make-choices                  #(map make-a-choice %1)
                     make-h-then-choices           (fn [h opts]
                                                     (cons (make-group-heading h)
                                                           (make-choices opts)))
                     has-no-group-names?           (nil? (:group (first group-names)))]
                 (if (and (= 1 (count group-opt-lists)) has-no-group-names?)
                   (make-choices (first group-opt-lists)) ;; one group means no headings
                   (apply concat (map make-h-then-choices group-names group-opt-lists))))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))

;;--------------------------------------------------------------------------------------------------
;; Deprecated Component: single-dropdown
;;--------------------------------------------------------------------------------------------------

(defn single-dropdown [& args]
  (apply (partial dropdown :multi? false :remove-selected? false) args))