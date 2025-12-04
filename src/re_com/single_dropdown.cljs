(ns re-com.single-dropdown
  (:require-macros
   [re-com.core     :refer [handler-fn at]])
  (:require
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug :refer [->attr]]
   [re-com.popover  :as popover]
   [re-com.theme    :as    theme]
   [re-com.theme.util    :as    tu]
   [re-com.util     :as    u :refer [deref-or-value position-for-id item-for-id]]
   [re-com.box      :refer [flex-child-style]]
   [re-com.validate :refer [vector-of-maps? number-or-string? log-warning
                            string-or-hiccup? position? position-options-list] :refer-macros [validate-args-macro]]
   [re-com.part     :as p]
   [clojure.string  :as    string]
   [reagent.core    :as    reagent]
   [goog.string     :as    gstring]
   [goog.string.format]
   re-com.single-dropdown.theme
   [re-com.single-dropdown :as-alias sd]
   [re-com.single-dropdown.parts :as sdp]
   [re-com.dropdown.parts :as dp]))

(def part-structure
  [::sd/chosen-container
   [::sd/chosen-single]
   [::sd/free-text-dropdown-top]
   [::sd/chosen-drop
    [::sd/chosen-search]
    [::sd/chosen-results
     [::sd/choice-item]
     [::sd/group-heading]
     [::sd/choices-loading]
     [::sd/choices-error]
     [::sd/choices-no-results]]]])

(def parts-desc
  (when include-args-desc?
    (p/describe part-structure)))

(def single-dropdown-parts
  (when include-args-desc?
    (-> (map :name parts-desc) set)))

(defn- fn-or-vector-of-maps-or-strings? ;; Would normally move this to re-com.validate but this is very specific to this component
  [v]
  (or (fn? v)
      (vector-of-maps? v)
      (and (sequential? v)
           (or (empty? v)
               (string? (first v))))))

(def args-desc
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
     {:name :drop-direction     :required false :default nil           :type "keyword"                                                                   :description [:span "Overrides any behavior which would position the body dynamically (such as "
                                                                                                                                                                       [:code ":can-drop-above?"] ")." [:code ":up"] " or " [:code ":above"]
                                                                                                                                                                       " positions the body above the anchor, and "
                                                                                                                                                                       [:code ":down"] [:code ":dn"] " or " [:code ":below"] " positions it below."]}
     {:name :est-item-height    :required false :default 30            :type "integer"                       :validate-fn number?                        :description [:span "estimated dropdown item height (for " [:code ":can-drop-above?"] "). used only *before* the dropdown part is displayed to guess whether it fits below the top part or not which is later verified when the dropdown is displayed"]}
     {:name :just-drop?         :required false :default false         :type "boolean"                                                                   :description "display just the dropdown part"}
     {:name :repeat-change?     :required false :default false         :type "boolean"                                                                   :description [:span "repeat " [:code ":on-change"] " events if an already selected item is selected again"]}
     {:name :i18n               :required false                        :type "map"                                                                       :description [:span "internationalization map with optional keys " [:code ":loading"] ", " [:code ":no-results"] " and " [:code ":no-results-match"]]}
     {:name :on-drop            :required false                        :type "() -> nil"                     :validate-fn fn?                            :description "called when the dropdown part is displayed"}
     args/pre
     args/theme
     args/class
     args/style
     args/attr
     args/src
     (args/parts single-dropdown-parts)
     args/debug]))

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
                    (catch js/Object _ nil))
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
  "Load choices or schedule loading depending on debounce?"
  [choices-state choices debounce-delay text regex-filter? debounce?]
  (when (fn? choices)
    (when-let [timer (:timer @choices-state)]
      (js/clearTimeout timer))
    (if debounce?
      (let [timer (js/setTimeout #(load-choices* choices-state choices text regex-filter?) debounce-delay)]
        (swap! choices-state assoc :timer timer))
      (load-choices* choices-state choices text regex-filter?))))

(defn single-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn
      [& {:keys [choices model regex-filter? debounce-delay just-drop?]
          :or   {debounce-delay 250}
          :as   args}]
      (or
       (validate-args-macro args-desc args)
       (let [external-model      (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
             internal-model      (reagent/atom @external-model)         ;; Create a new atom from the model to be used internally
             drop-showing?       (reagent/atom (boolean just-drop?))
             filter-text         (reagent/atom "")
             choices-fn?         (fn? choices)
             choices-state       (reagent/atom {:loading? choices-fn?
                                        ; loading error
                                                :error    nil
                                                :choices  []
                                        ; request id to ignore handling response when new request was already made
                                                :id       0
                                        ; to debounce requests
                                                :timer    nil})
             load-choices        (partial load-choices choices-state choices debounce-delay)
             set-filter-text     (fn [text {:keys [regex-filter?]} debounce?]
                                   (load-choices text regex-filter? debounce?)
                                   (reset! filter-text text))
             over?               (reagent/atom false)
             showing?            (reagent/track #(and (not @drop-showing?) @over?))
             free-text-focused?  (reagent/atom false)
             free-text-input     (reagent/atom nil)
             select-free-text?   (reagent/atom false)
             free-text-sel-range (reagent/atom nil)
             focus-free-text     #(when @free-text-input (.focus @free-text-input))
             anchor-el           (reagent/atom nil)
             body-el             (reagent/atom nil)
             anchor-ref!         #(reset! anchor-el %)
             body-ref!           #(reset! body-el %)
             focus-anchor        #(some-> @anchor-el (.getElementsByClassName "chosen-single") (.item 0) (.focus))
             transition!         #(case %
                                    :mouse-over (reset! over? true)
                                    :mouse-out  (reset! over? false))]
         (load-choices "" regex-filter? false)
         (fn single-dropdown-render
           [& {:keys [choices model on-change id-fn label-fn group-fn render-fn disabled? filter-box? regex-filter? placeholder title?
                      free-text? capitalize? enter-drop? cancelable? set-to-filter filter-placeholder can-drop-above? drop-direction
                      est-item-height repeat-change? i18n on-drop width max-height tab-index tooltip tooltip-position style parts auto-complete?]
               :or   {id-fn :id label-fn :label group-fn :group enter-drop? true cancelable? true est-item-height 30}
               :as   args}]
           (or
            (validate-args-macro args-desc args)
            (let [choices           (if choices-fn? (:choices @choices-state) (deref-or-value choices))
                  id-fn             (if free-text? identity id-fn)
                  label-fn          (if free-text? identity label-fn)
                  render-fn         (if free-text? identity (or render-fn label-fn))
                  disabled?         (deref-or-value disabled?)
                  regex-filter?     (deref-or-value regex-filter?)
                  latest-ext-model  (reagent/atom (deref-or-value model))
                  _                 (when (not= @external-model @latest-ext-model) ;; Has model changed externally?
                                      (reset! external-model @latest-ext-model)
                                      (reset! internal-model @latest-ext-model))
                  changeable?       (and on-change (not disabled?))
                  call-on-change    #(when (and changeable? (or (not= @internal-model @latest-ext-model)
                                                                repeat-change?))
                                       (reset! external-model @internal-model)
                                       (on-change @internal-model))
                  callback          #(do
                                       (reset! internal-model (cond-> % (and free-text? capitalize?) u/capitalize-first-letter))
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
                  free-text-change  #(do
                                       (reset! internal-model %)
                                       (reset! select-free-text? false)
                                       (call-on-change))
                  cancel            #(do
                                       (when-not @free-text-focused? ;; Prevent re-focusing free-text input on free-text input blur
                                         (focus-free-text))
                                       (reset! drop-showing? false)
                                       (set-filter-text "" args false)
                                       (reset! internal-model @external-model))
                  dropdown-click    #(when-not disabled?
                                       (if @drop-showing?
                                         (cancel)
                                         (do
                                           (reset! drop-showing? true)
                                           (focus-free-text)            ;; After drop-showing? reset so hiding dropdown in filter-box blur will not be overwritten
                                           (reset! select-free-text? true))))
                  filtered-choices  (if choices-fn?
                                      choices
                                      (if regex-filter?
                                        (filter-choices-regex choices group-fn label-fn @filter-text)
                                        (filter-choices choices group-fn label-fn @filter-text)))
                  visible-count     #(let [results-node (and @anchor-el
                                                             (.item (.getElementsByClassName @anchor-el "chosen-results") 0))]
                                       (if (and results-node (.-firstChild results-node))
                                         (quot (.-clientHeight results-node)
                                               (.-offsetHeight (.-firstChild results-node)))
                                         0))
                  est-drop-height   #(let [items-height  (* (count filtered-choices) est-item-height)
                                           drop-margin   12
                                           filter-height 32
                                           maxh          (cond
                                                           (not max-height)                    240
                                                           (string/ends-with? max-height "px") (js/parseInt max-height 10)
                                                           :else                               (do (log-warning "max-height is not in pxs, using 240px for estimation")
                                                                                                   240))]
                                       (min (+ items-height drop-margin (if filter-box? filter-height 0))
                                            maxh))
                  drop-height       (reagent/track
                                     #(if-let [drop-node (and @anchor-el
                                                              (.item (.getElementsByClassName @anchor-el "chosen-drop") 0))]
                                        (-> drop-node .getBoundingClientRect .-height)
                                        (est-drop-height)))
                  top-height        34
                  drop-above?       (cond
                                      (#{:above :up} drop-direction)       true
                                      (#{:below :down :dn} drop-direction) false
                                      :else
                                      (reagent/track
                                       #(when (and can-drop-above? @anchor-el)
                                          (let [node-top      (-> @anchor-el .getBoundingClientRect .-top)
                                                window-height (-> js/document .-documentElement .-clientHeight)
                                                clip-bot?     (> (+ node-top top-height @drop-height)
                                                                 window-height)
                                                clip-top?     (neg? (- node-top @drop-height))]
                                            (cond
                                              clip-top? false
                                              clip-bot? true)))))
                  press-enter       (fn []
                                      (let [drop-was-showing? @drop-showing?]
                                        (cond
                                          disabled?                       (cancel)
                                          (and (:on-enter-press set-to-filter)
                                               (seq @filter-text)
                                               (empty? filtered-choices)
                                               free-text?
                                               @drop-showing?)            (callback @filter-text)
                                          (or @drop-showing? enter-drop?) (callback @internal-model))
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
                  key-handler       #(if disabled?
                                       false
                                       (case (.-key %)
                                         "Enter"     (press-enter)
                                         "Escape"    (press-escape)
                                         "Tab"       (press-tab (.-shiftKey %))
                                         "ArrowUp"   (press-up)
                                         "ArrowDown" (press-down)
                                         "PageUp"    (press-page-up)
                                         "PageDown"  (press-page-down)
                                         "Home"      (press-home)
                                         "End"       (press-end)
                                         (or filter-box? free-text?)))                  ;; Use this boolean to allow/prevent the key from being processed by the text box
                  _                 (when tooltip (add-watch drop-showing? :tooltip #(reset! over? false)))
                  _                 (when on-drop (add-watch drop-showing? :on-drop #(when (and (not %3) %4) (on-drop))))
                  part              (partial p/part part-structure args)
                  re-com-ctx        {:state       {:free-text?    free-text?
                                                   :drop-showing? @drop-showing?
                                                   :focused?      @free-text-focused?
                                                   :tooltip?      tooltip
                                                   :chosen-drop   {:position (if (deref-or-value drop-above?)
                                                                               :above
                                                                               :below)}}
                                     :transition! transition!}
                  text              (if (some? @internal-model)
                                      (label-fn (item-for-id @internal-model choices :id-fn id-fn))
                                      placeholder)
                  chosen-single
                  (part ::sd/chosen-single
                    {:impl  sdp/chosen-single
                     :theme theme
                     :props {:disabled?      disabled?
                             :drop-showing?  drop-showing?
                             :dropdown-click dropdown-click
                             :filter-box?    filter-box?
                             :key-handler    key-handler
                             :tab-index      tab-index
                             :children       [[:span (when title? {:title text}) text]
                                              (when (not disabled?)
                                                [dp/indicator
                                                 {:style {:margin-right "8px" :margin-top "0.5px"}
                                                  :state {:openable (if @drop-showing? :open :closed)}}])]}})
                  chosen-results
                  (part ::sd/chosen-results
                    {:theme theme
                     :props
                     {:tag   :ul
                      :style (when max-height {:max-height max-height})
                      :children
                      (cond
                        (and choices-fn? (:loading? @choices-state))
                        [(part ::sd/choices-loading
                           {:theme theme
                            :props {:tag  :li
                                    :i18n i18n
                                    :children
                                    [(get i18n :loading "Loading...")]}})]
                        (and choices-fn? (:error @choices-state))
                        [(part ::sd/choices-error
                           {:theme theme
                            :props {:tag :li
                                    :children
                                    [(:error @choices-state)]}})]
                        (-> filtered-choices count pos?)
                        (let [[group-names
                               group-opt-lists] (choices-with-group-headings
                                                 filtered-choices group-fn)
                              choice-item       #(let [id (id-fn %)]
                                                   ^{:key (str id)}
                                                   (part ::sd/choice-item
                                                     {:theme theme
                                                      :impl  sdp/choice-item
                                                      :props {:id       id
                                                              :label    (render-fn %)
                                                              :on-click callback
                                                              :model    internal-model}}))
                              h-then-choices    (fn [{:keys [group id]} opts]
                                                  (cons
                                                   ^{:key id}
                                                   (part ::sd/group-heading
                                                     {:theme theme
                                                      :props {:tag      :li
                                                              :children [group]}})
                                                   (map choice-item opts)))
                              no-group-names?   (nil? (:group (first group-names)))
                              no-headings?      (= 1 (count group-opt-lists))]
                          (if (and no-headings? no-group-names?)
                            (map choice-item (first group-opt-lists))
                            (mapcat h-then-choices group-names group-opt-lists)))
                        :else
                        [(part ::sd/choices-no-results
                           {:theme theme
                            :props
                            {:tag  :li
                             :attr {:on-mouse-down
                                    (handler-fn
                                     (when (and (:on-no-results-match-click set-to-filter)
                                                (seq @filter-text)
                                                free-text?)
                                       (callback @filter-text)))}
                             :children
                             [(gstring/format
                               (or (and (seq @filter-text) (:no-results-match i18n))
                                   (and (empty? @filter-text) (:no-results i18n))
                                   (:no-results-match i18n)
                                   "No results match \"%s\"")
                               @filter-text)]}})])}})
                  free-text-dropdown-top
                  (part ::sd/free-text-dropdown-top
                    {:theme theme
                     :impl sdp/free-text-dropdown-top
                     :props
                     {:auto-complete?      auto-complete?
                      :cancel              cancel
                      :capitalize?         capitalize?
                      :choices             choices
                      :disabled?           disabled?
                      :drop-showing?       drop-showing?
                      :dropdown-click      dropdown-click
                      :filter-box?         filter-box?
                      :free-text-change    free-text-change
                      :free-text-focused?  free-text-focused?
                      :free-text-input     free-text-input
                      :free-text-sel-range free-text-sel-range
                      :internal-model      internal-model
                      :key-handler         key-handler
                      :placeholder         placeholder
                      :select-free-text?   select-free-text?
                      :tab-index           tab-index
                      :width               width}})
                  chosen-drop
                  (part ::sd/chosen-drop
                    {:theme theme
                     :props
                     {:top-height  top-height
                      :drop-height @drop-height
                      :attr        {:ref body-ref!}
                      :children
                      [(when (and (or filter-box? (not free-text?))
                                  (not just-drop?))
                         (part ::sd/chosen-search
                           {:impl  sdp/chosen-search
                            :theme theme
                            :props {:re-com             {:state
                                                         {:filter-box
                                                          (if (deref-or-value filter-box?)
                                                            :showing :hidden)}}
                                    :model              filter-text
                                    :key-handler        key-handler
                                    :drop-showing?      drop-showing?
                                    :on-change          #(set-filter-text % args true)
                                    :filter-placeholder filter-placeholder}}))
                       chosen-results]}})
                  chosen-container
                  (part ::sd/chosen-container
                    {:theme      theme
                     :post-props (-> (select-keys args [:attr :class])
                                     (tu/style {:width width} style)
                                     (debug/instrument args))
                     :props
                     {:re-com re-com-ctx
                      :style  (flex-child-style (if width "0 0 auto" "auto"))
                      :attr   {:ref anchor-ref!}
                      :children
                      [(when-not just-drop?
                         (if free-text?
                           free-text-dropdown-top
                           chosen-single))
                       (when (and @drop-showing? (not disabled?))
                         chosen-drop)]}})]
              (if tooltip
                [popover/popover-tooltip
                 :src      (at)
                 :label    tooltip
                 :position (or tooltip-position :below-center)
                 :showing? showing?
                 :anchor   chosen-container
                 :class    (str "rc-dropdown-tooltip " (get-in parts [:tooltip :class]))
                 :style    (get-in parts [:tooltip :class])
                 :attr     (get-in parts [:tooltip :attr])]
                chosen-container)))))))))
