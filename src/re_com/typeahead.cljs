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
            [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: typeahead
;; ------------------------------------------------------------------------------------

(def typeahead-args-desc
  [{:name :model            :required true                   :type "string | atom"    :validate-fn string-or-atom?    :description "text of the input (can be atom or value)"}
   {:name :on-change        :required true                   :type "string -> nil"    :validate-fn fn?                :description [:span [:code ":change-on-blur?"] " controls when it is called. Passed the current input string"] }
   {:name :data-source      :required true                   :type "string -> nil"    :validate-fn fn?                :description [:span [:code ":data-source"] " populates the suggestions. Should be a unary function accepting a callback. The callback should take a string and return a vector of results."] }
   {:name :status           :required false                  :type "keyword"          :validate-fn input-status-type? :description [:span "validation status. " [:code "nil/omitted"] " for normal status or one of: " input-status-types-list]}
   {:name :status-icon?     :required false :default false   :type "boolean"                                          :description [:span "when true, display an icon to match " [:code ":status"] " (no icon for nil)"]}
   {:name :status-tooltip   :required false                  :type "string"           :validate-fn string?            :description "displayed in status icon's tooltip"}
   {:name :placeholder      :required false                  :type "string"           :validate-fn string?            :description "background text shown when empty"}
   {:name :width            :required false :default "250px" :type "string"           :validate-fn string?            :description "standard CSS width setting for this input"}
   {:name :height           :required false                  :type "string"           :validate-fn string?            :description "standard CSS height setting for this input"}
   {:name :rows             :required false :default 3       :type "integer | string" :validate-fn number-or-string?  :description "ONLY applies to 'input-textarea': the number of rows of text to show"}
   {:name :change-on-blur?  :required false :default true    :type "boolean | atom"                                   :description [:span "when true, invoke " [:code ":on-change"] " function on blur, otherwise on every change (character by character)"] }
   {:name :validation-regex :required false                  :type "regex"            :validate-fn regex?             :description "user input is only accepted if it would result in a string that matches this regular expression"}
   {:name :disabled?        :required false :default false   :type "boolean | atom"                                   :description "if true, the user can't interact (input anything)"}
   {:name :class            :required false                  :type "string"           :validate-fn string?            :description "CSS class names, space separated"}
   {:name :style            :required false                  :type "CSS style map"    :validate-fn css-style?         :description "CSS styles to add or override"}
   {:name :attr             :required false                  :type "HTML attr map"    :validate-fn html-attr?         :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}
   {:name :input-type       :required false                  :type "keyword"          :validate-fn keyword?           :description "ONLY applies to super function 'base-typeahead': either :input or :textarea"}])

;; Sample regex's:
;;  - #"^(-{0,1})(\d*)$"                   ;; Signed integer
;;  - #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$"  ;; Specific numeric value ##.#
;;  - #"^.{0,8}$"                          ;; 8 chars max
;;  - #"^[0-9a-fA-F]*$"                    ;; Hex number
;;  - #"^(\d{0,2})()()$|^(\d{0,1})(:{0,1})(\d{0,2})$|^(\d{0,2})(:{0,1})(\d{0,2})$" ;; Time input

;; move this elsewhere
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

(defn- typeahead-base
  "Returns markup for a typeahead text input"
  [& {:keys [model data-source] :as args}]
  {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
  (let [input-text-model      (reagent/atom "")
        awaiting-suggestions? (reagent/atom false)
        suggestions           (reagent/atom [])
        suggestions-chan (chan)
        input-text-chan (chan)
        input-text-chan-debounced (debounce input-text-chan 250) ;; FIXME hard-coded
        reset-typeahead (fn []
                          (reset! awaiting-suggestions? false)
                          (reset! suggestions []))

        ;; FIXME ignore next suggestions if val is changed
        ;; or, very shwoopty, would be continue filtering the ones we did get while waiting for the others
        ;; that's messy though because it makes an assumption about data-source
        ;; perhaps disallow typing while waiting for the results?
        ;; let's try that... nope, input-text doesn't expose a focused? atom and the thing is unfocused after dis/re-enable

        ;; FIXME cleanup on unmount
        _ (go-loop []
            (let [[search suggs] (<! suggestions-chan)]
              (println "got " search suggs)
              (if (= search @input-text-model)
                (do (reset! suggestions suggs)
                    (reset! awaiting-suggestions? false))
                (do (println "search " search " not= " @input-text-model " so we skipped")))
              (recur)))
        ;; FIXME need a way to let this thing die when component is gone
        _ (go-loop []
            (let [v (<! input-text-chan-debounced)]
              (if (= "" v)
                (reset-typeahead)
                (do (reset! awaiting-suggestions? true)
                    (data-source v #(put! suggestions-chan [ %1 %2 ])) ) )
              (recur)))
        
        select-suggestion-next (fn []
                                 (println "Next suggestion"))
        ]
    (fn
      [& {:keys [model data-source]
          :as   args}]
      {:pre [(validate-args-macro typeahead-args-desc args "typeahead")]}
      [v-box
       :children [[input-text
                   :model input-text-model
                   :on-change (fn [v]
                                (reset! input-text-model v)
                                (put! input-text-chan v))
                   :change-on-blur? false]
                  #_[:pre @input-text-model]
                  
                  (let [suggs (not-empty @suggestions)
                        waiting? @awaiting-suggestions?]
                    (if (or suggs waiting?)
                      [v-box
                       :class "rc-typeahead-suggestions"
                       :children [(when waiting?
                                    [throbber :size :small :class "rc-typeahead-throbber"])
                                  (for [s suggs]
                                    [box
                                     :child s
                                     :class "rc-typeahead-suggestions-item"])]]))]])))

(defn typeahead
  [& args]
  (apply typeahead-base :input-type :input args))
