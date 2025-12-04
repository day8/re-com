(ns re-com.typeahead
  (:require-macros
   [re-com.core            :refer [handler-fn at reflect-current-component]]
   [re-com.validate        :refer [validate-args-macro]]
   [cljs.core.async.macros :refer [alt! go-loop]])
  (:require
   re-com.typeahead.theme
   [cljs.core.async   :refer [chan timeout <! put!]]
   [re-com.args       :as args]
   [re-com.config     :refer [include-args-desc?]]
   [re-com.debug      :as debug]
   [re-com.part       :as part]
   [re-com.theme      :as theme]
   [re-com.theme.util :as tu]
   [re-com.throbber   :refer [throbber]]
   [re-com.input-text :refer [input-text]]
   [re-com.util       :refer [deref-or-value px]]
   [re-com.popover    :refer [popover-tooltip]]
   [re-com.box        :refer [h-box v-box box gap line flex-child-style align-style]]
   [re-com.validate   :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts? number-or-string?
                              string-or-atom? throbber-size? throbber-sizes-list css-class?]]
   [re-com.typeahead  :as-alias ta]
   [reagent.core      :as    reagent]
   [goog.events.KeyCodes]))

(declare debounce display-suggestion)
(defn- make-typeahead-state
  "Return an initial value for the typeahead state, given `args`."
  [{:as args :keys [on-change rigid? change-on-blur? immediate-model-update? data-source suggestion-to-string debounce-delay model]}]
  (let [external-model-value (deref-or-value model)]
    (cond-> (let [c-input (chan)]
              {:input-text ""
               :external-model (deref-or-value model)
               :model          (deref-or-value model)
               :waiting? false
               :suggestions []
               :displaying-suggestion? false
               :suggestion-to-string (or suggestion-to-string str)
               :data-source data-source
               :change-on-blur? change-on-blur?
               :immediate-model-update? immediate-model-update?
               :on-change  on-change
               :rigid?     rigid?
               :c-input    c-input
               :c-search   (debounce c-input debounce-delay)})
      external-model-value
      (display-suggestion external-model-value))))

;; ------------------------------------------------------------------------------------
;; State predicates:  state -> value? -> boolean
;; ------------------------------------------------------------------------------------

(defn- event-updates-model?
  "Should `event` update the `typeahead` `model`?"
  [{:as state :keys [change-on-blur? rigid? immediate-model-update?]} event]
  (let [change-on-blur?         (deref-or-value change-on-blur?)
        immediate-model-update? (deref-or-value immediate-model-update?)
        rigid?                  (deref-or-value rigid?)]
    (case event
      :input-text-blurred   (and change-on-blur? (not rigid?))
      :suggestion-activated (not change-on-blur?)
      :input-text-changed   (and (not rigid?) (or (not change-on-blur?) immediate-model-update?))
      false)))

(defn- event-displays-suggestion?
  "Should `event` cause the `input-text` value to be used to show the active suggestion?"
  [{:as state :keys [change-on-blur?]} event]
  (let [change-on-blur? (deref-or-value change-on-blur?)]
    (case event
      :suggestion-activated (not change-on-blur?)
      false)))

;; ------------------------------------------------------------------------------------
;; State update helpers: state -> value? -> next-state
;;   all pure, _except_ that they may call `on-change`
;; ------------------------------------------------------------------------------------

(defn- update-model
  "Change the `typeahead` `model` value to `new-value`"
  [{:as state :keys [on-change]} new-value]
  (when on-change (on-change new-value))
  (assoc state :model new-value))

(defn- display-suggestion
  "Change the `input-text` `model` to the string representation of `suggestion`"
  [{:as state :keys [suggestion-to-string]} suggestion]
  (let [suggestion-string (suggestion-to-string suggestion)]
    (cond-> state
      suggestion-string (assoc :input-text suggestion-string
                               :displaying-suggestion? true))))

(defn- clear-suggestions
  [state]
  (-> state
      (dissoc :suggestions :suggestion-active-index)))

(defn- activate-suggestion-by-index
  "Make the suggestion at `index` the active suggestion"
  [{:as state :keys [suggestions]} index]
  (let [suggestion (nth suggestions index)]
    (cond-> state
      :always (assoc :suggestion-active-index index)
      (event-updates-model?       state :suggestion-activated) (update-model suggestion)
      (event-displays-suggestion? state :suggestion-activated) (display-suggestion suggestion))))

(defn- choose-suggestion-by-index
  "Choose the suggestion at `index`"
  [{:as state :keys [suggestions]} index]
  (let [suggestion (nth suggestions index)]
    (-> state
        (activate-suggestion-by-index index)
        (update-model suggestion)
        (display-suggestion suggestion)
        clear-suggestions)))

(defn- choose-suggestion-active
  [{:as state :keys [suggestion-active-index]}]
  (cond-> state
    suggestion-active-index (choose-suggestion-by-index suggestion-active-index)))

(defn- wrap [index count] (mod (+ count index) count))

(defn- activate-suggestion-next
  [{:as state :keys [suggestions suggestion-active-index]}]
  (cond-> state
    (seq suggestions)
    (activate-suggestion-by-index (-> suggestion-active-index (or -1) inc (wrap (count suggestions))))))

(defn- activate-suggestion-prev
  [{:as state :keys [suggestions suggestion-active-index]}]
  (cond-> state
    (seq suggestions)
    (activate-suggestion-by-index (-> suggestion-active-index (or 0) dec (wrap (count suggestions))))))

(defn- reset-typeahead
  [state]
  (cond-> state
    :always clear-suggestions
    :always (assoc :waiting? false :input-text "" :displaying-suggestion? false)
    (event-updates-model? state :input-text-changed) (update-model nil)))

(defn- got-suggestions
  "Update state when new suggestions are available"
  [state suggestions]
  (-> state
      (assoc :suggestions suggestions
             :waiting? false
             :suggestion-active-index nil)))

(defn- input-text-will-blur
  "Update state when the `input-text` is about to lose focus."
  [{:keys [input-text displaying-suggestion? model] :as state}]
  (cond
    (and (not displaying-suggestion?)
         (event-updates-model? state :input-text-blurred))
    (update-model state input-text)
    :else (-> state
              (assoc :input-text input-text)
              clear-suggestions)))

(defn- change-data-source
  "Update `state` given a new `data-source`. Resets the typeahead since any existing suggestions
  came from the old `data-source`."
  [state data-source]
  (-> state
      reset-typeahead
      (assoc :data-source data-source)))

(defn- external-model-changed
  "Update state when the external model value has changed."
  [state new-value]
  (-> state
      (update-model new-value)
      (display-suggestion new-value)
      (assoc :external-model new-value)
      clear-suggestions))

;; ------------------------------------------------------------------------------------
;; Functions with side-effects
;; ------------------------------------------------------------------------------------

(defn- search-data-source!
  "Call the `data-source` fn with `text`, and then call `got-suggestions` with the result
  (asynchronously, if `data-source` does not return a truthy value)."
  [data-source state-atom text]
  (if-let [return-value (data-source text #(swap! state-atom got-suggestions %1))]
    (swap! state-atom got-suggestions return-value)
    (swap! state-atom assoc :waiting? true)))

(defn- search-data-source-loop!
  "For every value arriving on the `c-search` channel, call `search-data-source!`."
  [state-atom c-search]
  (go-loop []
    (let [new-text (<! c-search)
          data-source (:data-source @state-atom)]
      (if (= "" new-text)
        (do
          (swap! state-atom reset-typeahead)
          (search-data-source! data-source state-atom ""))
        (search-data-source! data-source state-atom new-text))
      (recur))))

(defn- input-text-on-change!
  "Update state in response to `input-text` `on-change`, and put text on the `c-input` channel"
  [state-atom new-text]
  (let [{:as state :keys [input-text c-input]} @state-atom]
    (if (= new-text input-text) state
        (do
          (put! c-input new-text)
          (swap! state-atom
                 #(cond-> %
                    :always (assoc :input-text new-text :displaying-suggestion? false)
                    (event-updates-model? state :input-text-changed) (update-model new-text)))))))

(defn- input-text-on-key-down!
  [state-atom event]
  (condp = (.-key event)
    "ArrowUp"   (swap! state-atom activate-suggestion-prev)
    "ArrowDown" (swap! state-atom activate-suggestion-next)
    "Enter"     (swap! state-atom choose-suggestion-active)
    "Escape"    (swap! state-atom got-suggestions [])
    "Tab"
    (if (not-empty (:suggestions @state-atom))
      (do (swap! state-atom activate-suggestion-next)
          (.preventDefault event))
      (swap! state-atom input-text-will-blur))
    true))

;; ------------------------------------------------------------------------------------
;;  Component: typeahead
;; ------------------------------------------------------------------------------------

(def part-structure
  [::ta/wrapper {:impl 're-com.box/v-box}
   [::ta/input {:impl 're-com.input-text/input-text}]
   [::ta/suggestions-wrapper {:impl 're-com.box/box}
    [::ta/suggestions-container {:impl 're-com.box/v-box}
     [::ta/throbber {:impl 're-com.throbber/throbber}]
     [::ta/suggestion {:impl 're-com.box/box}]]]])

(def typeahead-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def typeahead-parts
  (when include-args-desc?
    (-> (map :name typeahead-parts-desc) set)))

(def typeahead-args-desc
  (when include-args-desc?
    (into
     [{:name :data-source             :required true  :type "fn"                   :validate-fn fn?                :description [:span "Function that supplies suggestion objects. Accepts either (1) a string argument returning a collection of suggestions, or (2) a string and callback, returning " [:code "nil"] " and calling the callback with suggestions later."]}
      {:name :model                   :required false :type "object | atom"                                        :description [:span "Initial value (should match suggestion objects from " [:code ":data-source"] ")"]}
      {:name :on-change               :required false :type "suggestion -> nil"    :validate-fn fn?                :description [:span "Called with chosen suggestion. Timing controlled by " [:code ":change-on-blur?"]]}
      {:name :change-on-blur?         :required false :type "boolean | atom"       :default true                   :description [:span "When true, invoke " [:code ":on-change"] " only when choosing a suggestion. When false, invoke on every change (navigation or typing if not rigid)"]}
      {:name :immediate-model-update? :required false :type "boolean | atom"       :default false                  :description "Update model on every keystroke (similar to not change-on-blur, but no changes when mouse is over suggestions)"}
      {:name :rigid?                  :required false :type "boolean | atom"       :default true                   :description [:span "When false, allow arbitrary text input instead of requiring selection from " [:code ":data-source"]]}
      {:name :disabled?               :required false :type "boolean | atom"       :default false                  :description "When true, disable user interaction"}
      {:name :debounce-delay          :required false :type "integer"              :validate-fn integer? :default 250 :description [:span "Milliseconds to wait after input before calling " [:code ":data-source"]]}
      {:name :render-suggestion       :required false :type "suggestion -> hiccup" :validate-fn fn?                :description "Override default suggestion rendering. Receives the suggestion object, returns hiccup"}
      {:name :suggestion-to-string    :required false :type "suggestion -> string" :validate-fn fn?                :description "Convert chosen suggestion to string for input display"}
      {:name :placeholder             :required false :type "string"               :validate-fn string?            :description "Placeholder text when empty"}
      {:name :width                   :required false :type "string"               :validate-fn string? :default "250px" :description "CSS width (applies to wrapper)"}
      {:name :height                  :required false :type "string"               :validate-fn string?            :description "CSS height (applies to input)"}
      {:name :status                  :required false :type "keyword"              :validate-fn input-status-type? :description [:span "Validation status. " [:code "nil"] " for normal or one of: " input-status-types-list]}
      {:name :status-icon?            :required false :type "boolean"              :default false                  :description [:span "When true, display icon matching " [:code ":status"]]}
      {:name :status-tooltip          :required false :type "string"               :validate-fn string?            :description "Tooltip text for status icon"}
      (assoc args/class :description "CSS class names (applies to input)")
      (assoc args/style :description "CSS styles (applies to input)")
      (assoc args/attr :description [:span "HTML attributes like " [:code ":on-mouse-move"] " (applies to wrapper, not input)"])
      args/pre
      args/theme
      (args/parts typeahead-parts)
      args/src
      args/debug-as]
     (part/describe-args part-structure))))

(defn typeahead
  "typeahead reagent component"
  [& {:keys [pre-theme theme] :as args}]
  (or
   (validate-args-macro typeahead-args-desc args)
   (let [{:as state :keys [c-search c-input]} (make-typeahead-state args)
         state-atom (reagent/atom state)
         input-text-model (reagent/cursor state-atom [:input-text])
         theme (theme/comp pre-theme theme)]
     (search-data-source-loop! state-atom c-search)
     (fn typeahead-render
       [& {:as   args
           :keys [data-source model render-suggestion status status-icon? status-tooltip placeholder
                  width height disabled? src debug-as]}]
       (or
        (validate-args-macro typeahead-args-desc args)
        (let [{:as state :keys [suggestions waiting? suggestion-active-index external-model]} @state-atom
              last-data-source      (:data-source state)
              latest-external-model (deref-or-value model)
              width                 (or (deref-or-value width) "250px")
              height                (deref-or-value height)
              part                  (partial part/part part-structure args)
              re-com-ctx            {:state {:waiting?           waiting?
                                             :has-suggestions?   (not-empty suggestions)
                                             :show-suggestions?  (or (not-empty suggestions) waiting?)}}]
          (when (not= last-data-source data-source)
            (swap! state-atom change-data-source data-source))
          (when (not= latest-external-model external-model)
            (swap! state-atom external-model-changed latest-external-model))
          (part ::ta/wrapper
            {:impl       v-box
             :theme      theme
             :post-props (-> (select-keys args [:attr])
                             (assoc :src src :debug-as (or debug-as (reflect-current-component)))
                             (cond-> width (tu/style {:width width}))
                             (debug/instrument args))
             :props      {:re-com   re-com-ctx
                          :children [(part ::ta/input
                                       {:impl       input-text
                                        :theme      theme
                                        :post-props (cond-> (select-keys args [:class :style])
                                                      width   (assoc :width width)
                                                      height  (assoc :height height))
                                        :props      {:re-com          re-com-ctx
                                                     :src             (at)
                                                     :model           input-text-model
                                                     :disabled?       disabled?
                                                     :status-icon?    status-icon?
                                                     :status          status
                                                     :status-tooltip  status-tooltip
                                                     :placeholder     placeholder
                                                     :on-change       (partial input-text-on-change! state-atom)
                                                     :change-on-blur? false
                                                     :attr            {:on-key-down (partial input-text-on-key-down! state-atom)
                                                                       :on-focus    #()
                                                                       :on-blur     #(swap! state-atom input-text-will-blur)}}})
                                     (when (or (not-empty suggestions) waiting?)
                                       (part ::ta/suggestions-wrapper
                                         {:impl       box
                                          :theme      theme
                                          :post-props {:src (at)}
                                          :props
                                          {:re-com re-com-ctx
                                           :child
                                           (part ::ta/suggestions-container
                                             {:impl       v-box
                                              :theme      theme
                                              :post-props {:src (at)}
                                              :props
                                              {:re-com   re-com-ctx
                                               :children
                                               [(when waiting?
                                                  [box
                                                   :src   (at)
                                                   :align :center
                                                   :child
                                                   (part ::ta/throbber
                                                     {:impl  throbber
                                                      :theme theme
                                                      :props {:re-com re-com-ctx
                                                              :src    (at)
                                                              :size   :small}})])
                                                (for [[i s] (map vector (range) suggestions)
                                                      :let [selected?      (= suggestion-active-index i)
                                                            suggestion-ctx (assoc-in re-com-ctx [:state :selected?] selected?)]]
                                                  ^{:key i}
                                                  (part ::ta/suggestion
                                                    {:impl       box
                                                     :theme      theme
                                                     :post-props {:src (at)}
                                                     :props
                                                     {:re-com suggestion-ctx
                                                      :child  (if render-suggestion
                                                                (render-suggestion s)
                                                                s)
                                                      :attr   {:on-mouse-over #(swap! state-atom activate-suggestion-by-index i)
                                                               :on-mouse-down #(do (.preventDefault %) (swap! state-atom choose-suggestion-by-index i))}}}))]}})}}))]}})))))))

(defn- debounce
  "Return a channel which will receive a value from the `in` channel only
  if no further value is received on the `in` channel in the next `ms` milliseconds."
  [in ms]
  (let [out (chan)]
    (go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)]
        (let [v (alt!
                  in ([val _] val)
                  timer (do (>! out val) nil))]
          (recur v))))
    out))
