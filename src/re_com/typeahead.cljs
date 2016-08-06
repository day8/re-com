(ns re-com.typeahead
  (:require-macros [re-com.core :refer [handler-fn]]
                   [cljs.core.async.macros :refer [alt! go-loop]])
  (:require [cljs.core.async :refer [chan timeout <! put!]]
            [re-com.misc     :refer [throbber input-text]]
            [re-com.util     :refer [deref-or-value px]]
            [re-com.popover  :refer [popover-tooltip]] ;; need?
            [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]] ;; need?
            [re-com.validate :refer [input-status-type? input-status-types-list regex?
                                     string-or-hiccup? css-style? html-attr? number-or-string?
                                     string-or-atom? throbber-size? throbber-sizes-list] :refer-macros [validate-args-macro]]
            [reagent.core    :as    reagent]
            #_[goog.events.KeyCodes :as KeyCodes]))

;; ------------------------------------------------------------------------------------
;;  Component: typeahead
;; ------------------------------------------------------------------------------------

(def typeahead-args-desc
  [{:name :model             :required false :default nil     :type "atom"             :validate-fn string-or-atom?    :description "The suggestion object currently selected."}
   {:name :on-change         :required true                   :type "string -> nil"    :validate-fn fn?                :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed a suggestion object (which come from ." [:code ":data-source"] ")"] }
   {:name :data-source       :required true                   :type "fn"               :validate-fn fn?                :description [:span [:code ":data-source"] " supplies suggestion objects. This can either accept a single string argument (the search term), or a string and a callback. For the first case, the fn should return a 2-element vector containing the search term and a collection of suggestion objects (which can be anything). For the second case, the fn should return nil, and eventually result in a call to the callback passing two arguments: the search query and a vector of suggestion objects."]}
   {:name :render-suggestion :required false                  :type "render fn"        :validate-fn fn?                :description "override the rendering of the suggestion items by passing a fn that returns hiccup forms. The fn will receive two arguments: the search term, and the suggestion object."}
   {:name :suggestion-to-string :required false               :type "suggestion -> string" :validate-fn fn?            :description "When a suggestion is chosen, the input-text value will be set to the result of calling this fn with the suggestion object."}
   {:name :rigid?            :required false :default true    :type "boolean | atom"                                   :description [:span "If "[:code "false"]" the user will be allowed to choose arbitrary text input rather than a suggestion from " [:code ":data-source"]". In this case, a String will be supplied in lieu of a suggestion object." ]}
   {:name :status            :required false                  :type "keyword"          :validate-fn input-status-type? :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
   {:name :status-icon?      :required false :default false   :type "boolean"                                          :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
   {:name :status-tooltip    :required false                  :type "string"           :validate-fn string?            :description "displayed in status icon's tooltip"}
   {:name :placeholder       :required false                  :type "string"           :validate-fn string?            :description "background text shown when empty"}
   {:name :width             :required false :default "250px" :type "string"           :validate-fn string?            :description "standard CSS width setting for this input"}
   {:name :height            :required false                  :type "string"           :validate-fn string?            :description "standard CSS height setting for this input"}
   {:name :change-on-blur?   :required false :default true    :type "boolean | atom"                                   :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"] }
   {:name :disabled?         :required false :default false   :type "boolean | atom"                                   :description "if true, the user can't interact (input anything)"}
   {:name :class             :required false                  :type "string"           :validate-fn string?            :description "CSS class names, space separated"}
   {:name :style             :required false                  :type "CSS style map"    :validate-fn css-style?         :description "CSS styles to add or override"}
   {:name :attr              :required false                  :type "HTML attr map"    :validate-fn html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

;; respect change-on-blur? (if false, typeahead model should change as user moves through suggestions with kbd/mouse)
;;   if rigid? is also false, then model should change as the user types as well

;; TODO
;; ability to focus the input-text would be nice... this is also missing from input-text

(defn debounce [in ms]
  (let [out (chan)]
    (go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)]
        (let [v (alt!
                  in ([val _] val)
                  timer (do (>! out val) nil))]
          (recur v))))
    out))

(def ^:private typeahead-state-initial
  {:input-text ""
   :waiting? false
   :suggestions []})

(defn- wrap [index count]
  (mod (+ count index) count))

(defn- choice-made
  [{:as state :keys [on-change change-on-blur?]} choice]
  (when on-change (on-change choice))
  (cond-> state
    :always (assoc :model choice :input-text "")
    ;; the trouble with this is that if change-on-blur? is false then clicking doesn't get rid of the suggestions either
    (deref-or-value change-on-blur?) (dissoc :suggestions)))

(defn- select-choose
  [{:as state :keys [suggestions selected-index on-change rigid? input-text]}]
  (cond-> state

    (and (not (deref-or-value rigid?)) (not selected-index))
    (choice-made input-text)

    selected-index
    (choice-made (let [[_ suggestion] (nth suggestions selected-index)] suggestion))))

(defn- select-prev
  [{:as state :keys [suggestions change-on-blur?]}]
  (cond-> state
    (not-empty suggestions) (update :selected-index #(-> % (or 0) dec (wrap (count suggestions))))
    (not (deref-or-value change-on-blur?)) select-choose))

(defn- select-next
  [{:as state :keys [suggestions change-on-blur?]}]
  (cond-> state
    (not-empty suggestions) (update :selected-index #(-> % (or -1) inc (wrap (count suggestions))))
    (not (deref-or-value change-on-blur?)) select-choose))

(defn- select-by-index
  "Called when the mouse hovers over a suggestion."
  [{:as state :keys [change-on-blur?]} index]
  (cond-> state
    :always (assoc :selected-index index)
    (not (deref-or-value change-on-blur?)) select-choose))

(defn- choose-by-index
  "Called when the mouse clicks a suggestion."
  [{:as state :keys [change-on-blur?]} index]
  (-> state
      (select-by-index index)
      (dissoc :suggestions)))

(defn- select-reset
  [state]
  (dissoc state :selected-index))

(defn- reset-typeahead
  [state]
  (-> state
      (assoc :waiting? false :suggestions [] :input-text "")
      select-reset))

(defn- change-text-input
  "Called when the text in the wrapped input-text is changed."
  [{:as state :keys [change-on-blur? rigid?]} new-text]
  (cond-> state
    :always
    (assoc :input-text new-text)

    (and (not (deref-or-value change-on-blur?))
         (not (deref-or-value rigid?)))
    (choice-made new-text)))

(defn- search-data-source
  "Call the user-supplied `data-source` fn and put the result on `c-sugg`.
  Set `waiting?` state."
  [data-source state-atom c-sugg text]
  (when-let [return-value (data-source text #(put! c-sugg [ %1 %2 ]))]
    (put! c-sugg return-value))
  (swap! state-atom assoc :waiting? true))

(defn- handle-search
  [state-atom c-sugg c-search]
  (go-loop []
    (let [new-text (<! c-search)
          data-source (:data-source @state-atom)]
      (if (= "" new-text)
        (swap! state-atom reset-typeahead)
        (search-data-source data-source state-atom c-sugg new-text))
      (recur))))

(defn- handle-search-results
  [state-atom c-sugg]
  (go-loop []
    (let [[search suggestions] (<! c-sugg)
          {:keys [input-text]} @state-atom]
      (swap! state-atom #(-> %
                             (assoc :suggestions suggestions :waiting? false)
                             select-reset)))
    (recur)))

(defn- make-typeahead-state
  [{:as args :keys [on-change rigid? change-on-blur? data-source]}]
  (let [c-input (chan)]
    (assoc typeahead-state-initial
           :data-source data-source
           :change-on-blur? change-on-blur?
           :on-change  on-change
           :rigid?     rigid?
           :c-input    c-input
           :c-search   (debounce c-input 250) ;; FIXME hard-coded
           :c-keypress (chan)
           :c-sugg     (chan))))

(def ^:private keys-by-code
  {40 :down
   38 :up
   9 :tab
   13 :enter
   27 :escape})

;; ^ FIXME
;; goog.events.KeyCodes
;; requiring it didn't work for me... skipping for now

(defn- typeahead-blur
  [{:keys [rigid? input-text] :as state}]
  (cond-> state
    (not (deref-or-value rigid?))
    (choice-made input-text)))

(defn- on-key-down
  [state-atom event]
  (when-let [key (keys-by-code (.-which event))]
    (case key
      :up    (swap! state-atom select-prev)
      :down  (swap! state-atom select-next)
      :enter (swap! state-atom select-choose)
      :escape (swap! state-atom reset-typeahead)
      ;; tab requires special treatment
      ;; if there are no suggestions, then let the event propagate so it defocuses as normal
      :tab (do (println "THATS TAB" (boolean (not-empty (:suggestions @state-atom))))
               (if (not-empty (:suggestions @state-atom))
                 (do (swap! state-atom select-next)
                     (.preventDefault event))
                 (swap! state-atom typeahead-blur)))
      true)))

(defn- change-data-source
  "Return new state after changing data-source. The typeahead render fn checks to see if it
  has changed since the last render, and calls this."
  [state data-source]
  (-> state
      reset-typeahead
      (assoc :data-source data-source)))

(defn- typeahead
  "Returns markup for a typeahead text input"
  [& {:keys [data-source on-change rigid? change-on-blur?] :as args}]
  {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
  (let [{:as state :keys [ c-search c-sugg c-input c-keypress ]} (make-typeahead-state args)
        state-atom (reagent/atom state)
        input-text-model (reagent/cursor state-atom [:input-text])]
    (handle-search-results state-atom c-sugg)
    (handle-search state-atom c-sugg c-search)
    (fn
      [& {:keys [data-source on-change rigid?
                 model placeholder width height
                 status-icon? status status-tooltip
                 disabled? class style render-suggestion
                 suggestion-to-string]
          :as   args}]
      {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
      (let [{:as state :keys [suggestions waiting? model selected-index]} @state-atom
            last-data-source (:data-source state)
            width (or width "250px")]
        (when (not= last-data-source data-source)
          (swap! state-atom change-data-source data-source))
        [v-box
         :width width
         :children
         [[input-text
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
           :on-change      #(do (swap! state-atom change-text-input %) (put! c-input %))
           :change-on-blur? false
           :attr {:on-key-down #(on-key-down state-atom %)}]
          (if (or (not-empty suggestions) waiting?)
            [v-box
             :class "rc-typeahead-suggestions-container"
             :children [(when waiting?
                          [box :align :center :child [throbber :size :small :class "rc-typeahead-throbber"]])
                        (for [[ i s ] (map vector (range) suggestions)
                              :let [selected? (= selected-index i)]]
                          ^{:key i}
                          [box
                           :child (if render-suggestion
                                    (render-suggestion s)
                                    s)
                           :class (str "rc-typeahead-suggestion"
                                       (when selected? " active"))
                           :attr {:on-mouse-over #(swap! state-atom select-by-index i)
                                  :on-mouse-down #(do (.preventDefault %) (swap! state-atom choose-by-index i))}])]])]]))))
