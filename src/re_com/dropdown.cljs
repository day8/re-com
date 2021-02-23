(ns re-com.dropdown
  (:require-macros
    [re-com.core     :refer [handler-fn at]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value position-for-id item-for-id]]
    [re-com.box      :refer [align-style flex-child-style]]
    [re-com.validate :refer [vector-of-maps? css-style? html-attr? parts? number-or-string? log-warning
                             string-or-hiccup? position? position-options-list] :refer-macros [validate-args-macro]]
    [re-com.popover  :refer [popover-tooltip]]
    [clojure.string  :as    string]
    [reagent.core    :as    reagent]
    [goog.string     :as    gstring]
    [goog.string.format]
    [reagent.dom     :as    rdom]))

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


(defn- insert
  "Return text after insertion in place of selection"
  [& {:keys [text sel-start sel-end ins]}]
  (str (when text (subs text 0 sel-start))
    ins
    (when text (subs text sel-end))))


(defn auto-complete
  "Return text/selection map after insertion in place of selection & completion"
  [& {:keys [choices text sel-start sel-end ins]}]
  (let [text' (insert :text text :sel-start sel-start :sel-end sel-end :ins ins)
        find  #(gstring/caseInsensitiveStartsWith % text')
        ret   (when-first [choice (filter find choices)]
                {:text      (str text' (subs choice (count text')))
                 :sel-start (+ sel-start (count ins))
                 :sel-end   (count choice)})]
    (when (and (not= (:sel-start ret) (:sel-end ret))
               (seq ins))
      ret)))


(defn capitalize-first-letter
  "Capitalize the first letter leaving the rest as is"
  [text]
  (if (seq text)
    (str (string/upper-case (first text)) (subs text 1))
    text))


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
         (let [node (rdom/dom-node this)
               selected (= @internal-model id)]
           (when selected (show-selected-item node))))

       :component-did-update
       (fn [this]
         (let [node (rdom/dom-node this)
               selected (= @internal-model id)]
           (when selected (show-selected-item node))))

       :display-name "choice-item"

       :reagent-render
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
             :on-mouse-down (handler-fn
                              (on-click id)
                              (.preventDefault event))}         ;; Prevent free-text input as well as the normal dropdown from loosing focus
            label]))})))


(defn make-choice-item
  [id-fn render-fn callback internal-model opt]
  (let [id (id-fn opt)
        markup (render-fn opt)]
    ^{:key (str id)} [choice-item id markup callback internal-model]))


(defn- filter-text-box-base
  "Base function (before lifecycle metadata) to render a filter text box"
  [filter-box? filter-text key-handler drop-showing? set-filter-text filter-placeholder]
  [:div.chosen-search
   [:input
    {:type          "text"
     :auto-complete "off"
     :style         (when-not filter-box? {:position "absolute" ;; When no filter box required, use it but hide it off screen
                                           :width    "0px"      ;; The rest of these styles make the textbox invisible
                                           :padding  "0px"
                                           :border   "none"})
     :value         @filter-text
     :placeholder   filter-placeholder
     :on-change     (handler-fn (set-filter-text (-> event .-target .-value)))
     :on-key-down   (handler-fn (when-not (key-handler event)
                                  (.stopPropagation event)
                                  (.preventDefault event))) ;; When key-handler returns false, preventDefault
     :on-blur       (handler-fn (reset! drop-showing? false))}]])


(def ^:private filter-text-box
  "Render a filter text box"
  (with-meta filter-text-box-base
             {:component-did-mount #(let [node (.-firstChild (rdom/dom-node %))]
                                     (.focus node))
              :component-did-update #(let [node (.-firstChild (rdom/dom-node %))]
                                      (.focus node))}))

(defn- dropdown-top
  "Render the top part of the dropdown, with the clickable area and the up/down arrow"
  []
  (let [ignore-click (atom false)]
    (fn
      [internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-box? drop-showing? title? disabled?]
      (let [_    (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})
            text (if @internal-model
                   (label-fn (item-for-id @internal-model choices :id-fn id-fn))
                   placeholder)]
        [:a.chosen-single.chosen-default
         {:style         (when disabled?
                           {:background-color "#EEE"})
          :tab-index     (or tab-index 0)
          :on-click      (handler-fn
                           (if @ignore-click
                             (reset! ignore-click false)
                             (dropdown-click)))
          :on-mouse-down (handler-fn
                           (when @drop-showing?
                             (reset! ignore-click true)))   ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?) and removing the ignore-click stuff
          :on-key-down   (handler-fn
                           (key-handler event)
                           (when (= (.-which event) 13)     ;; Pressing enter on an anchor also triggers click event, which we don't want
                             (reset! ignore-click true)))}  ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?) and removing the ignore-click stuff
         [:span (when title?
                  {:title text})
          text]
         (when (not disabled?)
           [:div [:b]])])))) ;; This odd bit of markup produces the visual arrow on the right

(defn handle-free-text-insertion
  [event ins auto-complete? capitalize? choices internal-model free-text-sel-range free-text-change]
  (let [input             (.-target event)
        input-sel-start   (.-selectionStart input)
        input-sel-end     (.-selectionEnd input)
        ins'              (cond-> ins (and capitalize? (zero? input-sel-start)) capitalize-first-letter)
        auto-complete-ret (and auto-complete? (auto-complete :choices   choices
                                                             :text      @internal-model
                                                             :sel-start input-sel-start
                                                             :sel-end   input-sel-end
                                                             :ins       ins'))]
    (cond
      auto-complete-ret (let [{:keys [text sel-start sel-end]} auto-complete-ret]
                          (if (= @internal-model text)
                            (.setSelectionRange input sel-start sel-end)
                            (do
                              (reset! free-text-sel-range [sel-start sel-end])
                              (free-text-change text)))
                          (.preventDefault event))
      (not= ins ins')   (do
                          (reset! free-text-sel-range [(+ input-sel-start (count ins)) (+ input-sel-start (count ins))])
                          (free-text-change (insert :text      @internal-model
                                                    :sel-start input-sel-start
                                                    :sel-end   input-sel-end
                                                    :ins       ins'))
                          (.preventDefault event)))))

(defn- free-text-dropdown-top-base
  "Base function (before lifecycle metadata) to render the top part of the dropdown (free-text), with the editable area and the up/down arrow"
  [free-text-input select-free-text? free-text-focused? free-text-sel-range internal-model tab-index placeholder dropdown-click key-handler filter-box? drop-showing? cancel width free-text-change auto-complete? choices capitalize? disabled?]
  (js/console.log "disabled? " disabled?)
  [:ul.chosen-choices
   [:li.search-field
    [:div.free-text
     {:style (when disabled?
               {:background-color "#EEE"})}
     [:input
      {:type          "text"
       :auto-complete "off"
       :class         "form-control"
       :style         {:width width}
       :tab-index     tab-index
       :placeholder   placeholder
       :value         @internal-model
       :disabled      disabled?
       :on-change     (handler-fn (let [value (-> event .-target .-value)]
                                    (free-text-change (cond-> value capitalize? capitalize-first-letter))))
       :on-key-down   (handler-fn (when-not (key-handler event)
                                    (.stopPropagation event)
                                    (.preventDefault event))) ;; When key-handler returns false, preventDefault
       :on-key-press  (handler-fn
                        (let [ins (.-key event)]
                          (when (= (count ins) 1) ;; Filter out special keys (e.g. enter)
                            (handle-free-text-insertion event ins auto-complete? capitalize? choices internal-model free-text-sel-range free-text-change))))
       :on-paste      (handler-fn
                        (let [ins (.getData (.-clipboardData event) "Text")]
                          (handle-free-text-insertion event ins auto-complete? capitalize? choices internal-model free-text-sel-range free-text-change)))
       :on-focus      (handler-fn
                        (reset! free-text-focused? true)
                        (reset! select-free-text? true))
       :on-blur       (handler-fn
                        (when-not filter-box?
                          (cancel))
                        (reset! free-text-focused? false))  ;; Set free-text-focused? after calling cancel to prevent re-focusing
       :on-mouse-down (handler-fn (when @drop-showing?
                                    (cancel)
                                    (.preventDefault event))) ;; Prevent text selection flicker (esp. with filter-box)
       :ref           #(reset! free-text-input %)}]
     [:span.b-wrapper
      {:on-mouse-down (handler-fn
                        (dropdown-click)
                        (when @free-text-focused?
                          (.preventDefault event)))}            ;; Prevent free-text input from loosing focus
      (when (not disabled?)
        [:b])]]]])

(def ^:private free-text-dropdown-top
  "Render the top part of the dropdown (free-text), with the editable area and the up/down arrow"
  (with-meta free-text-dropdown-top-base
    {:component-did-update #(let [[_ free-text-input select-free-text? free-text-focused? free-text-sel-range] (reagent/argv %)]
                              (when (and @free-text-input @select-free-text? @free-text-focused?)
                                (.select @free-text-input))
                              (when (and @free-text-input @free-text-sel-range)
                                (.setSelectionRange @free-text-input (first @free-text-sel-range) (second @free-text-sel-range))
                                (reset! free-text-sel-range nil)))}))

(defn- fn-or-vector-of-maps-or-strings? ;; Would normally move this to re-com.validate but this is very specific to this component
  [v]
  (or (fn? v)
      (vector-of-maps? v)
      (and (sequential? v)
           (or (empty? v)
               (string? (first v))))))

(defn- load-choices*
  "Load choices if choices is callback."
  [choices-state choices text regex-filter?]
  (let [id (inc (:id @choices-state))
        callback (fn [{:keys [result error] :as args}]
                   (when (= id (:id @choices-state))
                     (swap! choices-state assoc
                            :loading? false
                            :error error
                            :choices result)))]
    (swap! choices-state assoc
           :loading? true
           :error nil
           :id id
           :timer nil)
    (choices {:filter-text   text
              :regex-filter? regex-filter?}
             #(callback {:result %})
             #(callback {:error %}))))

(defn- load-choices
  "Load choices or schedule lodaing depending on debounce?"
  [choices-state choices debounce-delay text regex-filter? debounce?]
  (when (fn? choices)
    (when-let [timer (:timer @choices-state)]
      (js/clearTimeout timer))
    (if debounce?
      (let [timer (js/setTimeout #(load-choices* choices-state choices text regex-filter?) debounce-delay)]
        (swap! choices-state assoc :timer timer))
      (load-choices* choices-state choices text regex-filter?))))

;;--------------------------------------------------------------------------------------------------
;; Component: single-dropdown
;;--------------------------------------------------------------------------------------------------

(def single-dropdown-parts-desc
  (when include-args-desc?
    [{:name :tooltip            :level 0 :class "rc-dropdown-tooltip"            :impl "[popover-tooltip]" :notes "Tooltip for the dropdown, if enabled."}
     {:type :legacy             :level 1 :class "rc-dropdown"                    :impl "[:div]"            :notes "The container for the rest of the dropdown."}
     {:name :chosen-drop        :level 2 :class "rc-dropdown-chosen-drop"        :impl "[:div]"}
     {:name :chosen-results     :level 3 :class "rc-dropdown-chosen-results"     :impl "[:ul]"}
     {:name :choices-loading    :level 4 :class "rc-dropdown-choices-loading"    :impl "[:li]"}
     {:name :choices-error      :level 4 :class "rc-dropdown-choices-error"      :impl "[:li]"}
     {:name :choices-no-results :level 4 :class "rc-dropdown-choices-no-results" :impl "[:li]"}]))

(def single-dropdown-parts
  (when include-args-desc?
    (-> (map :name single-dropdown-parts-desc) set)))

(def single-dropdown-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                         :type "vector of choices | r/atom | (opts, done, fail) -> nil" :validate-fn fn-or-vector-of-maps-or-strings? :description [:span "Each is expected to have an id, label and, optionally, a group, provided by " [:code ":id-fn"] ", " [:code ":label-fn"] " & " [:code ":group-fn"] ". May also be a callback " [:code "(opts, done, fail)"] " where opts is map of " [:code ":filter-text"] " and " [:code ":regex-filter?."]]}
     {:name :model              :required true                         :type "the id of a choice | r/atom"                                               :description [:span "the id of the selected choice. If nil, " [:code ":placeholder"] " text is shown"]}
     {:name :on-change          :required true                         :type "id -> nil"                     :validate-fn fn?                            :description [:span "called when a new choice is selected. Passed the id of new choice"]}
     {:name :id-fn              :required false :default :id           :type "choice -> anything"            :validate-fn ifn?                           :description [:span "given an element of " [:code ":choices"] ", returns its unique identifier (aka id)"]}
     {:name :label-fn           :required false :default :label        :type "choice -> string"              :validate-fn ifn?                           :description [:span "given an element of " [:code ":choices"] ", returns its displayable label."]}
     {:name :group-fn           :required false :default :group        :type "choice -> anything"            :validate-fn ifn?                           :description [:span "given an element of " [:code ":choices"] ", returns its group identifier"]}
     {:name :render-fn          :required false                        :type "choice -> string | hiccup"     :validate-fn ifn?                           :description [:span "given an element of " [:code ":choices"] ", returns the markup that will be rendered for that choice. Defaults to the label if no custom markup is required."]}
     {:name :disabled?          :required false :default false         :type "boolean | r/atom"                                                          :description "if true, no user selection is allowed"}
     {:name :filter-box?        :required false :default false         :type "boolean"                                                                   :description "if true, a filter text field is placed at the top of the dropdown"}
     {:name :regex-filter?      :required false :default false         :type "boolean | r/atom"                                                          :description "if true, the filter text field will support JavaScript regular expressions. If false, just plain text"}
     {:name :placeholder        :required false                        :type "string"                        :validate-fn string?                        :description "background text when no selection"}
     {:name :title?             :required false :default false         :type "boolean"                                                                   :description "if true, allows the title for the selected dropdown to be displayed via a mouse over. Handy when dropdown width is small and text is truncated"}
     {:name :width              :required false :default "100%"        :type "string"                        :validate-fn string?                        :description "the CSS width. e.g.: \"500px\" or \"20em\""}
     {:name :max-height         :required false :default "240px"       :type "string"                        :validate-fn string?                        :description "the maximum height of the dropdown part"}
     {:name :tab-index          :required false :default 0             :type "integer | string"              :validate-fn number-or-string?              :description "component's tabindex. A value of -1 removes from order"}
     {:name :debounce-delay     :required false                        :type "integer"                       :validate-fn number?                        :description [:span "delay to debounce loading requests when using callback " [:code ":choices"]]}
     {:name :tooltip            :required false                        :type "string | hiccup"               :validate-fn string-or-hiccup?              :description "what to show in the tooltip"}
     {:name :tooltip-position   :required false :default :below-center :type "keyword"                       :validate-fn position?                      :description [:span "relative to this anchor. One of " position-options-list]}
     {:name :free-text?         :required false :default false         :type "boolean"                                                                   :description [:span "is the text freely editable? If true then " [:code ":chocies"] " is a vector of strings, " [:code ":model"] " is a string (atom) and " [:code ":on-change"] " is called with a string"]}
     {:name :auto-complete?     :required false :default false         :type "boolean"                                                                   :description [:span "auto-complete text while typing using dropdown choices. Has no effect if " [:code ":free-text?"] " is not turned on"]}
     {:name :capitalize?        :required false :default false         :type "boolean"                                                                   :description [:span "capitalize the first letter. Has no effect if " [:code ":free-text?"] " is not turned on"]}
     {:name :enter-drop?        :required false :default true          :type "boolean"                                                                   :description "should pressing Enter display the dropdown part?"}
     {:name :cancelable?        :required false :default true          :type "boolean"                                                                   :description "should pressing Esc or clicking outside the dropdown part cancel selection made with arrow keys?"}
     {:name :set-to-filter      :required false :default #{}           :type "set"                           :validate-fn set?                           :description [:span "when " [:code ":filter-box?"] " and " [:code ":free-text?"] " are turned on and there are no results, current text can be set to filter text " [:code ":on-enter-press"] " and/or " [:code ":on-no-results-match-click"]]}
     {:name :filter-placeholder :required false                        :type "string"                        :validate-fn string?                        :description "background text in filter box when no filter"}
     {:name :can-drop-above?    :required false :default false         :type "boolean"                                                                   :description "should the dropdown part be displayed above if it does not fit below the top part?"}
     {:name :est-item-height    :required false :default 30            :type "integer"                       :validate-fn number?                        :description [:span "estimated dropdown item height (for " [:code ":can-drop-above?"] "). used only *before* the dropdown part is displayed to guess whether it fits below the top part or not which is later verified when the dropdown is displayed"]}
     {:name :just-drop?         :required false :default false         :type "boolean"                                                                   :description "display just the dropdown part"}
     {:name :repeat-change?     :required false :default false         :type "boolean"                                                                   :description [:span "repeat " [:code ":on-change"] " events if an already selected item is selected again"]}
     {:name :i18n               :required false                        :type "map"                                                                       :description [:span "internationalization map with optional keys " [:code ":loading"] ", " [:code ":no-results"] " and " [:code ":no-results-match"]]}
     {:name :on-drop            :required false                        :type "() -> nil"                     :validate-fn fn?                            :description "called when the dropdown part is displayed"}
     {:name :class              :required false                        :type "string"                        :validate-fn string?                        :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style              :required false                        :type "CSS style map"                 :validate-fn css-style?                     :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr               :required false                        :type "HTML attr map"                 :validate-fn html-attr?                     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts              :required false                        :type "map"                           :validate-fn (parts? single-dropdown-parts) :description "See Parts section below."}
     {:name :src                :required false                        :type "map"                           :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as           :required false                        :type "map"                           :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn single-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [choices model regex-filter? debounce-delay just-drop?]
      :or {debounce-delay 250}
      :as args}]
  (or
    (validate-args-macro single-dropdown-args-desc args)
    (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
          internal-model (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
          drop-showing?  (reagent/atom (boolean just-drop?))
          filter-text    (reagent/atom "")
          choices-fn?    (fn? choices)
          choices-state (reagent/atom {:loading? choices-fn?
                                       ; loading error
                                       :error nil
                                       :choices []
                                       ; request id to ignore handling response when new request was already made
                                       :id 0
                                       ; to debounce requests
                                       :timer nil})
          load-choices (partial load-choices choices-state choices debounce-delay)
          set-filter-text (fn [text {:keys [regex-filter?] :as args} debounce?]
                            (load-choices text regex-filter? debounce?)
                            (reset! filter-text text))
          over?               (reagent/atom false)
          showing?            (reagent/track #(and (not @drop-showing?) @over?))
          free-text-focused?  (reagent/atom false)
          free-text-input     (reagent/atom nil)
          select-free-text?   (reagent/atom false)
          free-text-sel-range (reagent/atom nil)
          focus-free-text     #(when @free-text-input (.focus @free-text-input))
          node                (reagent/atom nil)
          focus-anchor        #(some-> @node (.getElementsByClassName "chosen-single") (.item 0) (.focus))]
      (load-choices "" regex-filter? false)
      (fn single-dropdown-render
        [& {:keys [choices model on-change id-fn label-fn group-fn render-fn disabled? filter-box? regex-filter? placeholder title? free-text? auto-complete? capitalize? enter-drop? cancelable? set-to-filter filter-placeholder can-drop-above? est-item-height repeat-change? i18n on-drop width max-height tab-index debounce-delay tooltip tooltip-position class style attr parts]
            :or {id-fn :id label-fn :label group-fn :group render-fn label-fn enter-drop? true cancelable? true est-item-height 30}
            :as args}]
        (or
          (validate-args-macro single-dropdown-args-desc args)
          (let [choices          (if choices-fn? (:choices @choices-state) (deref-or-value choices))
                id-fn            (if free-text? identity id-fn)
                label-fn         (if free-text? identity label-fn)
                render-fn        (if free-text? identity render-fn)
                disabled?        (deref-or-value disabled?)
                regex-filter?    (deref-or-value regex-filter?)
                latest-ext-model (reagent/atom (deref-or-value model))
                _                (when (not= @external-model @latest-ext-model) ;; Has model changed externally?
                                   (reset! external-model @latest-ext-model)
                                   (reset! internal-model @latest-ext-model))
                changeable?      (and on-change (not disabled?))
                call-on-change   #(when (and changeable? (or (not= @internal-model @latest-ext-model)
                                                             repeat-change?))
                                   (reset! external-model @internal-model)
                                   (on-change @internal-model))
                callback         #(do
                                   (reset! internal-model (cond-> % (and free-text? capitalize?) capitalize-first-letter))
                                   (reset! select-free-text? true)
                                   (call-on-change)
                                   (let [current-drop-showing? @drop-showing?]
                                     (when current-drop-showing?
                                       (focus-free-text))
                                     (when-not just-drop?
                                       (reset! drop-showing? (not current-drop-showing?))) ;; toggle to allow opening dropdown on Enter key
                                     (when current-drop-showing?
                                       (focus-anchor)))
                                   (set-filter-text "" args false))
                free-text-change #(do
                                    (reset! internal-model %)
                                    (reset! select-free-text? false)
                                    (call-on-change))
                cancel           #(do
                                   (when-not @free-text-focused? ;; Prevent re-focusing free-text input on free-text input blur
                                     (focus-free-text))
                                   (reset! drop-showing? false)
                                   (set-filter-text "" args false)
                                   (reset! internal-model @external-model))
                dropdown-click   #(when-not disabled?
                                   (if @drop-showing?
                                     (cancel)
                                     (do
                                       (reset! drop-showing? true)
                                       (focus-free-text)            ;; After drop-showing? reset so hiding dropdown in filter-box blur will not be overwritten
                                       (reset! select-free-text? true))))
                filtered-choices (if choices-fn?
                                   choices
                                   (if regex-filter?
                                     (filter-choices-regex choices group-fn label-fn @filter-text)
                                     (filter-choices choices group-fn label-fn @filter-text)))
                visible-count    #(let [results-node (and @node
                                                          (.item (.getElementsByClassName @node "chosen-results") 0))]
                                   (if (and results-node (.-firstChild results-node))
                                     (quot (.-clientHeight results-node)
                                           (.-offsetHeight (.-firstChild results-node)))
                                     0))
                est-drop-height  #(let [items-height  (* (count filtered-choices) est-item-height)
                                        drop-margin   12
                                        filter-height 32
                                        maxh          (cond
                                                        (not max-height) 240
                                                        (string/ends-with? max-height "px") (js/parseInt max-height 10)
                                                        :else (do (log-warning "max-height is not in pxs, using 240px for estimation")
                                                                  240))]
                                   (min (+ items-height drop-margin (if filter-box? filter-height 0))
                                        maxh))
                drop-height      (reagent/track
                                   #(if-let [drop-node (and @node
                                                            (.item (.getElementsByClassName @node "chosen-drop") 0))]
                                     (-> drop-node .getBoundingClientRect .-height)
                                     (est-drop-height)))
                top-height       34
                drop-above?      (reagent/track
                                   #(when (and can-drop-above? @node)
                                     (let [node-top      (-> @node .getBoundingClientRect .-top)
                                           window-height (-> js/document .-documentElement .-clientHeight)]
                                       (> (+ node-top top-height @drop-height)
                                          window-height))))
                press-enter      (fn []
                                   (let [drop-was-showing? @drop-showing?]
                                     (cond
                                       disabled?                             (cancel)
                                       (and (:on-enter-press set-to-filter)
                                            (seq @filter-text)
                                            (empty? filtered-choices)
                                            free-text?
                                            @drop-showing?)                  (callback @filter-text)
                                       (or @drop-showing? enter-drop?)       (callback @internal-model))
                                     (not drop-was-showing?)))
                press-escape      (fn []
                                    (let [drop-was-showing? @drop-showing?]
                                      (cancel)
                                      (when drop-was-showing?
                                        (focus-anchor))
                                      (not drop-was-showing?)))
                press-tab         (fn [shift-key?]
                                    (if disabled?
                                      (cancel)
                                      (let [drop-was-showing? @drop-showing?]  ;; Was (callback @internal-model) but needed a customised version
                                        (call-on-change)
                                        (reset! drop-showing? false)
                                        (set-filter-text "" args false)
                                        (when (and drop-was-showing? shift-key?)
                                          (focus-anchor))))
                                    (reset! drop-showing? false)
                                    true)
                press-arrow       (fn [offset]
                                    (when (and @drop-showing? (seq filtered-choices))
                                      (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model offset))
                                      (when-not cancelable?
                                        (call-on-change)))
                                    (reset! drop-showing? true)
                                    (reset! select-free-text? true)
                                    true)
                press-up          #(press-arrow -1)                   ;; Up arrow
                press-down        #(press-arrow 1)                    ;; Down arrow
                press-page-up     #(press-arrow (- (dec (visible-count))))
                press-page-down   #(press-arrow (dec (visible-count)))
                press-home-or-end (fn [offset]
                                    (when (and (not @free-text-focused?)
                                               (seq filtered-choices))
                                      (reset! internal-model (move-to-new-choice filtered-choices id-fn @internal-model offset))
                                      (reset! select-free-text? true))
                                    true)
                press-home        #(press-home-or-end :start)
                press-end         #(press-home-or-end :end)
                key-handler      #(if disabled?
                                   false
                                   (case (.-which %)
                                     13 (press-enter)
                                     27 (press-escape)
                                     9  (press-tab (.-shiftKey %))
                                     38 (press-up)
                                     40 (press-down)
                                     33 (press-page-up)
                                     34 (press-page-down)
                                     36 (press-home)
                                     35 (press-end)
                                     (or filter-box? free-text?)))                  ;; Use this boolean to allow/prevent the key from being processed by the text box
                dropdown         [:div
                                  (merge
                                    {:class (str "rc-dropdown chosen-container " (if free-text? "chosen-container-multi " "chosen-container-single ") "noselect " (when (or @drop-showing? @free-text-focused?) "chosen-container-active ") (when @drop-showing? "chosen-with-drop ") class)          ;; Prevent user text selection
                                     :style (merge (flex-child-style (if width "0 0 auto" "auto"))
                                                   (align-style :align-self :start)
                                                   {:width width}
                                                   style)
                                     :ref   #(reset! node %)}
                                    (when tooltip
                                      {:on-mouse-over (handler-fn (reset! over? true))
                                       :on-mouse-out (handler-fn (reset! over? false))})
                                    (->attr args)
                                    attr)
                                  (cond
                                    just-drop? nil
                                    free-text? [free-text-dropdown-top free-text-input select-free-text? free-text-focused? free-text-sel-range internal-model tab-index placeholder dropdown-click key-handler filter-box? drop-showing? cancel width free-text-change auto-complete? choices capitalize? disabled?]
                                    :else [dropdown-top internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-box? drop-showing? title? disabled?])
                                  (when (and @drop-showing? (not disabled?))
                                    [:div
                                     (merge
                                       {:class (str "chosen-drop rc-dropdown-chosen-drop " (get-in parts [:chosen-drop :class]))
                                        :style (merge (when @drop-above? {:transform (gstring/format "translate3d(0px, -%ipx, 0px)" (+ top-height @drop-height -2))})
                                                      (get-in parts [:chosen-drop :style]))}
                                       (get-in parts [:chosen-drop :attr]))
                                     (when (and (or filter-box? (not free-text?))
                                                (not just-drop?))
                                       [filter-text-box filter-box? filter-text key-handler drop-showing? #(set-filter-text % args true) filter-placeholder])
                                     [:ul
                                      (merge
                                        {:class (str "chosen-results rc-dropdown-chosen-results " (get-in parts [:chosen-results :class]))
                                         :style (merge (when max-height {:max-height max-height})
                                                       (get-in parts [:chosen-results :style]))}
                                        (get-in parts [:chosen-results :attr]))
                                      (cond
                                        (and choices-fn? (:loading? @choices-state))
                                        [:li
                                         (merge
                                           {:class (str "loading rc-dropdown-choices-loading " (get-in parts [:choices-loading :class]))
                                            :style (get-in parts [:choices-loading :style] {})}
                                           (get-in parts [:choices-loading :attr]))
                                         (get i18n :loading "Loading...")]
                                        (and choices-fn? (:error @choices-state))
                                        [:li
                                         (merge
                                           {:class (str "error rc-dropdown-choices-error " (get-in parts [:choices-error :class]))
                                            :style (get-in parts [:choices-error :style] {})}
                                           (get-in parts [:choices-error :attr]))
                                         (:error @choices-state)]
                                        (-> filtered-choices count pos?)
                                        (let [[group-names group-opt-lists] (choices-with-group-headings filtered-choices group-fn)
                                              make-a-choice                 (partial make-choice-item id-fn render-fn callback internal-model)
                                              make-choices                  #(map make-a-choice %1)
                                              make-h-then-choices           (fn [h opts]
                                                                              (cons (make-group-heading h)
                                                                                    (make-choices opts)))
                                              has-no-group-names?           (nil? (:group (first group-names)))]
                                          (if (and (= 1 (count group-opt-lists)) has-no-group-names?)
                                            (make-choices (first group-opt-lists)) ;; one group means no headings
                                            (apply concat (map make-h-then-choices group-names group-opt-lists))))
                                        :else
                                        [:li
                                         (merge
                                           {:class (str "no-results rc-dropdown-choices-no-results " (get-in parts [:choices-no-results :class]))
                                            :style (get-in parts [:choices-no-results :style] {})
                                            :on-mouse-down (handler-fn
                                                             (when (and (:on-no-results-match-click set-to-filter)
                                                                        (seq @filter-text)
                                                                        free-text?)
                                                               (callback @filter-text)))}
                                           (get-in parts [:choices-no-results :attr]))
                                         (gstring/format (or (and (seq @filter-text) (:no-results-match i18n))
                                                             (and (empty? @filter-text) (:no-results i18n))
                                                             (:no-results-match i18n)
                                                             "No results match \"%s\"")
                                                         @filter-text)])]])]
                _                (when tooltip (add-watch drop-showing? :tooltip #(reset! over? false)))
                _                (when on-drop (add-watch drop-showing? :on-drop #(when (and (not %3) %4) (on-drop))))]
            (if tooltip
              [popover-tooltip
               :src      (at)
               :label    tooltip
               :position (or tooltip-position :below-center)
               :showing? showing?
               :anchor   dropdown
               :class    (str "rc-dropdown-tooltip " (get-in parts [:tooltip :class]))
               :style    (get-in parts [:tooltip :class])
               :attr     (get-in parts [:tooltip :attr])]
              dropdown)))))))
