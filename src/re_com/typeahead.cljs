(ns re-com.typeahead
  (:require-macros
   [re-com.core            :refer [handler-fn at reflect-current-component]]
   [re-com.validate        :refer [validate-args-macro]]
   [cljs.core.async.macros :refer [alt! go-loop]])
  (:require
   [cljs.core.async   :refer [chan timeout <! put!]]
   [re-com.config     :refer [include-args-desc?]]
   [re-com.debug      :refer [->attr]]
   [re-com.throbber   :refer [throbber]]
   [re-com.input-text :refer [input-text]]
   [re-com.theme      :as theme]
   [re-com.util       :refer [deref-or-value px]]
   [re-com.popover    :refer [popover-tooltip]] ;; need?
   [re-com.box        :refer [h-box v-box box gap line flex-child-style align-style]] ;; need?
   [re-com.validate   :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts? number-or-string?
                              string-or-atom? throbber-size? throbber-sizes-list css-class?]]
   [reagent.core      :as    reagent]
   [goog.events.KeyCodes]))

;; TODO
;; ability to focus & blur the input-text would be nice... this is also missing from input-text
;; the typeahead should blur the input-text after a selection is chosen

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
              ;; if nothing was actually selected, then view should be the unchanged value
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
    (if (= new-text input-text) state ;; keypresses that do not change the value still call on-change, ignore these
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
    ;; tab requires special treatment
    ;; trap it IFF there are suggestions, otherwise let the input defocus
    "Tab"
    (if (not-empty (:suggestions @state-atom))
      (do (swap! state-atom activate-suggestion-next)
          (.preventDefault event))
      (swap! state-atom input-text-will-blur))
    true))

;; ------------------------------------------------------------------------------------
;;  Component: typeahead
;; ------------------------------------------------------------------------------------

(def typeahead-args-desc
  (when include-args-desc?
    [{:name :data-source             :required true                   :type "fn"                   :validate-fn fn?                :description [:span [:code ":data-source"] " supplies suggestion objects. This can either accept a single string argument (the search term), or a string and a callback. For the first case, the fn should return a collection of suggestion objects (which can be anything). For the second case, the fn should return " [:code "nil"] ", and eventually result in a call to the callback with a collection of suggestion objects."]}
     {:name :on-change               :required false :default nil     :type "string -> nil"        :validate-fn fn?                :description [:span [:code ":change-on-blur?"] " controls when it is called. It is passed a suggestion object."]}
     {:name :change-on-blur?         :required false :default true    :type "boolean | r/atom"                                     :description [:span "when true, invoke " [:code ":on-change"] " when the user chooses a suggestion, otherwise invoke it on every change (navigating through suggestions with the mouse or keyboard, or if " [:code "rigid?"] " is also " [:code "false"] ", invoke it on every character typed.)"]}
     {:name :immediate-model-update? :required false :default false   :type "boolean | r/atom"                                     :description [:span "update model with currently entered text on every keystroke (similar to " [:code ":change-on-blur?"] " but no changes to model if mouse is over suggestions)"]}
     {:name :model                   :required false :default nil     :type "object | r/atom"                                      :description "the initial value of the typeahead (should match the suggestion objects returned by " [:code ":data-source"] ")."}
     {:name :debounce-delay          :required false :default 250     :type "integer"              :validate-fn integer?           :description [:span "after receiving input, the typeahead will wait this many milliseconds without receiving new input before calling " [:code ":data-source"] "."]}
     {:name :render-suggestion       :required false                  :type "render fn"            :validate-fn fn?                :description "override the rendering of the suggestion items by passing a fn that returns hiccup forms. The fn will receive two arguments: the search term, and the suggestion object."}
     {:name :suggestion-to-string    :required false                  :type "suggestion -> string" :validate-fn fn?                :description "when a suggestion is chosen, the input-text value will be set to the result of calling this fn with the suggestion object."}
     {:name :rigid?                  :required false :default true    :type "boolean | r/atom"                                     :description [:span "If " [:code "false"] " the user will be allowed to choose arbitrary text input rather than a suggestion from " [:code ":data-source"] ". In this case, a string will be supplied in lieu of a suggestion object."]}

     ;; the rest of the arguments are forwarded to the wrapped `input-text`
     {:name :status                  :required false                  :type "keyword"              :validate-fn input-status-type? :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
     {:name :status-icon?            :required false :default false   :type "boolean"                                              :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
     {:name :status-tooltip          :required false                  :type "string"               :validate-fn string?            :description "displayed in status icon's tooltip"}
     {:name :placeholder             :required false                  :type "string"               :validate-fn string?            :description "background text shown when empty"}
     {:name :width                   :required false :default "250px" :type "string"               :validate-fn string?            :description "standard CSS width setting for this input"}
     {:name :height                  :required false                  :type "string"               :validate-fn string?            :description "standard CSS height setting for this input"}
     {:name :disabled?               :required false :default false   :type "boolean | r/atom"                                     :description "if true, the user can't interact (input anything)"}
     {:name :class                   :required false                  :type "string"               :validate-fn css-class?            :description "CSS class names, space separated (applies to the textbox)"}
     {:name :style                   :required false                  :type "CSS style map"        :validate-fn css-style?         :description "CSS styles to add or override (applies to the textbox)"}
     {:name :attr                    :required false                  :type "HTML attr map"        :validate-fn html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to " [:span.bold "the outer container"] ", rather than the textbox)"]}
     {:name :parts                   :required false                  :type "map"                  :validate-fn (parts? #{:suggestions-container :suggestion :throbber}) :description "See Parts section below."}
     {:name :src                     :required false                  :type "map"                  :validate-fn map?               :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as                :required false                  :type "map"                  :validate-fn map?               :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn typeahead
  "typeahead reagent component"
  [& {:keys [] :as args}]
  (or
   (validate-args-macro typeahead-args-desc args)
   (let [{:as state :keys [c-search c-input]} (make-typeahead-state args)
         state-atom (reagent/atom state)
         input-text-model (reagent/cursor state-atom [:input-text])]
     (search-data-source-loop! state-atom c-search)
     (fn typeahead-render
       [& {:as   args
           :keys [data-source _on-change _change-on-blur? _immediate-model-update? model _debounce-delay render-suggestion _suggestion-to-string _rigid?
                   ;; forwarded to wrapped `input-text`:
                  status status-icon? status-tooltip placeholder width height disabled? class style attr parts src debug-as]}]
       (or
        (validate-args-macro typeahead-args-desc args)
        (let [{:as state :keys [suggestions waiting? suggestion-active-index external-model]} @state-atom
              last-data-source      (:data-source state)
              latest-external-model (deref-or-value model)
              width                 (or width "250px")]
          (when (not= last-data-source data-source)
            (swap! state-atom change-data-source data-source))
          (when (not= latest-external-model external-model)
            (swap! state-atom external-model-changed latest-external-model))
          [v-box
           :src      src
           :debug-as (or debug-as (reflect-current-component))
           :class    "rc-typeahead"
           :attr     attr
           :width    width
           :children [[input-text
                       :src            (at)
                       :model          input-text-model
                       :class          class
                       :style          style
                       :disabled?      disabled?
                       :status-icon?   status-icon?
                       :status         status
                       :status-tooltip status-tooltip
                       :width          width
                       :height         height
                       :placeholder    placeholder
                       :on-change      (partial input-text-on-change! state-atom)
                       :change-on-blur? false
                       :attr {:on-key-down (partial input-text-on-key-down! state-atom)
                              :on-focus #()
                                ;; on-blur should behave the same as tabbing off
                              :on-blur #(swap! state-atom input-text-will-blur)}]
                      (if (or (not-empty suggestions) waiting?)
                        [box
                         :src   (at)
                         :style {:position "relative"}
                         :child [v-box
                                 :src      (at)
                                 :class    (str "rc-typeahead-suggestions-container " (get-in parts [:suggestions-container :class]))
                                 :children [(when waiting?
                                              [box
                                               :src   (at)
                                               :align :center
                                               :child [throbber
                                                       :src   (at)
                                                       :size  :small
                                                       :class (theme/merge-class "rc-typeahead-throbber"
                                                                                 (get-in parts [:throbber :class]))]])
                                            (for [[i s] (map vector (range) suggestions)
                                                  :let [selected? (= suggestion-active-index i)]]
                                              ^{:key i}
                                              [box
                                               :src   (at)
                                               :child (if render-suggestion
                                                        (render-suggestion s)
                                                        s)
                                               :class (theme/merge-class "rc-typeahead-suggestion"
                                                                         (when selected? " active")
                                                                         (get-in parts [:suggestion :class]))
                                               :attr {:on-mouse-over #(swap! state-atom activate-suggestion-by-index i)
                                                      :on-mouse-down #(do (.preventDefault %) (swap! state-atom choose-suggestion-by-index i))}])]]])]]))))))

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
