(ns re-com.core
  (:require [clojure.set  :refer [superset?]]
            [reagent.core :as reagent]
            [re-com.util  :refer [deref-or-value]]
            [re-com.box   :refer [h-box v-box box gap line]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args
  #{:label      ;; Label to display
    :on-click   ;; Callback when label is clicked
    :class      ;; Class string
    :style      ;; A map. Standard hicckup style map values. e.g. {:color "blue" :margin "4px"}
    })


(defn label
  "Returns markup for a basic label"
  [& {:keys [label on-click class style]
      :as   args}]
  {:pre [(superset? label-args (keys args))]}

  [:span
   (merge
     {:class (str "rc-label " class)
      :style (merge {:flex "none"} style)}
     (when on-click {:on-click #(do (println "click") (on-click))}))
   label])


;; ------------------------------------------------------------------------------------
;;  Component: input-text
;; ------------------------------------------------------------------------------------

(def input-text-args
  #{:model           ;; Text of the input (can be atom or value).
    :placeholder     ;; Text to show when there is no under text in the component.
    :width           ;; Standard CSS width setting for this input.
    :on-change       ;; A function which takes one parameter, which is the new text (see :change-on-blur?).
    :change-on-blur? ;; When true, invoke on-change function on blur, otherwise on every change (character by character).
    :disabled?       ;; Set to true to disable the input box (can be atom or value).
    :class           ;; Class string.
    :style           ;; CSS style map.
    })


(defn input-text
  "Returns markup for a basic text imput label"
  [& {:keys [model] :as args}]
  {:pre [(superset? input-text-args (keys args))]}
  (let [external-model (reagent/atom (deref-or-value model))  ;; Holds the last known external value of model, to detect external model changes
        internal-model (reagent/atom @external-model)]        ;; Create a new atom from the model to be used internally
    (fn
      [& {:keys [model placeholder width on-change change-on-blur? disabled? class style]
          :or   {change-on-blur? true}
          :as   args}]
      {:pre [(superset? input-text-args (keys args))]}
      (let [disabled?        (deref-or-value disabled?)
            change-on-blur?  (deref-or-value change-on-blur?)
            latest-ext-model (deref-or-value model)]
        (when (not= @external-model latest-ext-model) ;; Has model changed externally?
          (reset! external-model latest-ext-model)
          (reset! internal-model latest-ext-model))
        [:input
         {:type        "text"
          :placeholder placeholder
          :value       @internal-model
          :disabled    (when disabled? true)
          :class       (str "rc-input-text form-control " class)
          :style       (merge
                         {:flex "none"}
                         {:width (if width width "250px")}
                         style)
          :on-change   #(when (and on-change (not disabled?))
                         (reset! internal-model (-> % .-target .-value))
                         (when-not change-on-blur?
                           (on-change @internal-model)))
          :on-blur     #(when change-on-blur?
                         (on-change @internal-model))
          :on-key-up   #(if disabled?
                         false
                         (case (.-which %)
                           13 (on-change @internal-model)
                           27 (reset! internal-model @external-model)
                           true))}]))))


;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def button-args
  #{:label      ;; Label for the button (can be artitrary markup)
    :on-click   ;; Callback when the button is clicked
    :disabled?  ;; Set to true to disable the button
    :class      ;; Class string. e.g. "btn-info" (see: http://getbootstrap.com/css/#buttons)
    :style      ;; CSS style map
    })


(defn button
  "Returns the markup for a basic button."
  [& {:keys [label on-click disabled? class style]
      :or   {:class "btn-default"}
      :as   args}]
  {:pre [(superset? button-args (keys args))]}
  (let [disabled?   (deref-or-value disabled?)
        callback-fn (if (and on-click (not disabled?)) #(on-click))]
    [:button
     {:class    (str "rc-button btn " class)
      :style    (merge {:flex "none" :align-self "flex-start"} style)
      :on-click callback-fn}
     label]))


;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def checkbox-args
  #{:model          ;; Holds state of the checkbox when it is called
    :on-change      ;; When model state is changed, call back with new state
    :label          ;; Checkbox label
    :disabled?      ;; Set to true to disable the checkbox
    :style          ;; Checkbox style map
    :label-class    ;; Label class string
    :label-style    ;; Label style map
    })


;; TODO: when disabled?, should the text appear "disabled".
(defn checkbox
  "I return the markup for a checkbox, with an optional RHS label."
  [& {:keys [model on-change label disabled? style label-class label-style]
      :as   args}]
  {:pre [(superset? checkbox-args (keys args))]}

  (let [model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn (if (and on-change (not disabled?)) #(on-change (not model)))]     ;; call on-change with either true or false
    [h-box
     :gap "8px"     ;; between the tickbox and the label
     :children [[:input
                 {:class     "rc-checkbox"
                  :type      "checkbox"
                  :style     (merge {:flex "none"} style)
                  :disabled  disabled?
                  :checked   model
                  :on-change callback-fn}]
                (when label [re-com.core/label
                             :label label
                             :class label-class
                             :style label-style
                             :on-click callback-fn])]]))    ;; ticking on the label is the same as clicking on the checkbox


;; ------------------------------------------------------------------------------------
;;  Component: radio-button
;; ------------------------------------------------------------------------------------

(def radio-button-args
  #{:model          ;; Holds state of the checkbox when it is called
    :value          ;; Value of the radio button OR button group
    :label          ;; Checkbox label
    :on-change      ;; When model state is changed, call back with new state
    :disabled?      ;; Set to true to disable the checkbox
    :style          ;; Checkbox style map
    :label-class    ;; Label class string
    :label-style    ;; Label style map
    })


(defn radio-button
  "I return the markup for a radio button, with an optional RHS label."
  [& {:keys [model value label on-change disabled? style label-class label-style]
      :as   args}]
  {:pre [(superset? radio-button-args (keys args))]}

  (let [model       (deref-or-value model)
        disabled?   (deref-or-value disabled?)
        callback-fn (if (and on-change (not disabled?)) #(on-change value))]
    [h-box
     :gap "8px"     ;; between the tickbox and the label
     :children [[:input
                 {:class     "rc-radio-button"
                  :type      "radio"
                  :style     (merge {:flex "none"} style)     ;; add in flex child style, so it can sit in a vbox
                  :disabled  disabled?
                  :checked   (= model value)
                  :on-change callback-fn}]
                (when label [re-com.core/label
                             :label label
                             :class label-class
                             :style label-style
                             :on-click callback-fn])]]))


;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-args
  #{:model   ;;
    })


(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model]
      :as   args}]
  {:pre [(superset? progress-bar-args (keys args))]}

  [:div
   {:class "rc-progress-bar progress"
    :style {:flex "none"}}
   [:div.progress-bar ;;.progress-bar-striped.active
    {:role "progressbar"
     :style {:width (str @model "%")
             :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
    (str @model "%")]])


;; ------------------------------------------------------------------------------------
;;  Component: spinner
;; ------------------------------------------------------------------------------------

(defn spinner
  "Render an animated gif spinner"
  []

  [:div {:style {:display "flex"
                 :flex    "none"
                 :margin "10px"}}
   [:img {:src "resources/img/spinner.gif"
          :style {:margin "auto"}}]])


;; ------------------------------------------------------------------------------------
;;  Component: title
;; ------------------------------------------------------------------------------------

(def title-args
  #{:label        ;; Text of the title
    :underline?   ;; Boolean determines whether an underline is placed under the title
    })


(defn title
  "An underlined, left justified, H3 Title"
  [& {:keys [label underline?]
      :or   {underline? true}
      :as   args}]
  {:pre [(superset? title-args (keys args))]}

  [v-box
   :children [[:h3 label]
              (when underline? [line :size "1px"])]])
