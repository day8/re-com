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
  [{:name :model            :required true                   :type "string | atom"    :validate-fn string-or-atom?    :description "text of the input (can be atom or value)"}
   {:name :on-change        :required true                   :type "string -> nil"    :validate-fn fn?                :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed the current input string"] }
   {:name :data-source      :required true                   :type "async fn"         :validate-fn fn?                :description [:span [:code ":data-source"] " populates the suggestions. Should be a 2 argument function taking a search query and a callback, and should call the callback with the search query and a vector of suggestions."]}
   {:name :status           :required false                  :type "keyword"          :validate-fn input-status-type? :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
   {:name :status-icon?     :required false :default false   :type "boolean"                                          :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
   {:name :status-tooltip   :required false                  :type "string"           :validate-fn string?            :description "displayed in status icon's tooltip"}
   {:name :placeholder      :required false                  :type "string"           :validate-fn string?            :description "background text shown when empty"}
   {:name :width            :required false :default "250px" :type "string"           :validate-fn string?            :description "standard CSS width setting for this input"}
   {:name :height           :required false                  :type "string"           :validate-fn string?            :description "standard CSS height setting for this input"}
   {:name :change-on-blur?  :required false :default true    :type "boolean | atom"                                   :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"] }
   {:name :disabled?        :required false :default false   :type "boolean | atom"                                   :description "if true, the user can't interact (input anything)"}
   {:name :class            :required false                  :type "string"           :validate-fn string?            :description "CSS class names, space separated"}
   {:name :style            :required false                  :type "CSS style map"    :validate-fn css-style?         :description "CSS styles to add or override"}
   {:name :attr             :required false                  :type "HTML attr map"    :validate-fn html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

;; TODO
;; fix docs/demo
;; respect change-on-blur?, on-change
;; allow custom component as suggestion-item
;; add an option to allow arbitrary text (vs only a suggestion)
;; set the model properly

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
  {:text ""
   :waiting? false
   :suggestions []})

(defn- wrap [index count]
  (mod (+ count index) count))

(defn- suggestion-select-prev
  [{:as state :keys [suggestions]}]
  (update state :suggestion-selected-index #(-> % (or 0) dec (wrap (count suggestions)))))

(defn- suggestion-select-next
  [{:as state :keys [suggestions]}]
  (update state :suggestion-selected-index #(-> % (or -1) inc (wrap (count suggestions)))))

(defn- suggestion-select-set
  [state i]
  (assoc state :suggestion-selected-index i))

(defn- suggestion-select-choose
  [{:as state :keys [suggestions suggestion-selected-index]}]
  (-> state
      (assoc :model (nth suggestions suggestion-selected-index))
      (dissoc :suggestions)))

(defn- suggestion-select-reset
  [state]
  (dissoc state :suggestion-selected-index))

(defn- reset-typeahead
  [state]
  (-> state
      (assoc :waiting? false :suggestions [])
      suggestion-select-reset))

(defn- search-data-source
  "Call the user-supplied `data-source` fn and put the result on `c-sugg`.
  Set `waiting?` state."
  [data-source state-atom c-sugg text]
  (data-source text #(put! c-sugg [ %1 %2 ]))
  (swap! state-atom assoc :waiting? true))

(defn- handle-search
  [state-atom c-sugg data-source c-search]
  (go-loop []
    (let [new-text (<! c-search)]
      (if (= "" new-text)
        (swap! state-atom reset-typeahead)
        (search-data-source data-source state-atom c-sugg new-text))
      (recur))))

(defn- handle-search-results
  [state-atom c-sugg]
  (go-loop []
    (let [[search suggestions] (<! c-sugg)]
      (when (= search (:model @state-atom))
        (swap! state-atom #(-> %
                               (assoc :suggestions suggestions :waiting? false)
                               suggestion-select-reset))))
    (recur)))

(defn- make-typeahead-state
  []
  (let [c-input (chan)]
    (assoc typeahead-state-initial
           :c-keypress (chan)
           :c-input c-input
           :c-search (debounce c-input 250) ;; FIXME hard-coded
           :c-sugg (chan))))

(def ^:private keys-by-code
  {40 :down
   38 :up
   9 :tab
   13 :enter})

;; ^ FIXME
;; goog.events.KeyCodes


(defn- on-key-up
  [state-atom event]
  (when-let [key (keys-by-code (.-which event))]
    (.preventDefault event)
    (case key
      :up    (swap! state-atom suggestion-select-prev)
      :down  (swap! state-atom suggestion-select-next)
      :enter (swap! state-atom suggestion-select-choose)
      true)))

(defn- on-key-down
  [state-atom event]
  (when-let [key (keys-by-code (.-which event))]
    (.preventDefault event)
    (case key
      :tab   (swap! state-atom suggestion-select-next)
      true)))

(defn- typeahead
  "Returns markup for a typeahead text input"
  [& {:keys [model data-source] :as args}]
  {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
  (let [{:as state :keys [ c-search c-sugg c-input c-keypress ]} (make-typeahead-state)
        state-atom (reagent/atom state)]
    (handle-search-results state-atom c-sugg)
    (handle-search state-atom c-sugg data-source c-search)
    (fn
      [& {:keys [model placeholder width height status-icon? status status-tooltip disabled? class style]
          :as   args}]
      {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
      (let [{:keys [suggestions waiting? model suggestion-selected-index]} @state-atom
            width (or width "250px")]
        [v-box
         :width width
         :children
         [[input-text
           :class          class
           :style          style
           :disabled?      disabled?
           :status-icon?   status-icon?
           :status         status
           :status-tooltip status-tooltip
           :width          width
           :height         height
           :placeholder    placeholder
           :on-change #(do (swap! state-atom assoc :model %)
                           (put! c-input %))
           :change-on-blur? false
           :model (or model "")
           :attr {:on-key-up   #(on-key-up state-atom %)
                  :on-key-down #(on-key-down state-atom %)}]
          (if (or (not-empty suggestions) waiting?)
            [v-box
             :class "rc-typeahead-suggestions"
             :children [(when waiting?
                          [box :align :center :child [throbber :size :small :class "rc-typeahead-throbber"]])
                        (for [[ i s ] (map vector (range) suggestions)
                              :let [selected? (= suggestion-selected-index i)]]
                          [box
                           :child s
                           :class (str "rc-typeahead-suggestions-item"
                                       (when selected? " active"))
                           :attr {:on-mouse-over #(swap! state-atom suggestion-select-set i)
                                  :on-mouse-down #(do (.preventDefault %) (swap! state-atom suggestion-select-choose))
                                  }])]])]]))))
