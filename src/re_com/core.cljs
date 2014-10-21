(ns re-com.core
  (:require [clojure.set  :refer [superset?]]
            [reagent.core :as reagent]
            [re-com.util  :refer [deref-or-value]]
            [re-com.box   :refer [h-box box gap]]))


;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-args
  #{:label      ;; Label to display
    :style      ;; A map. Standard hicckup style map values. e.g. {:color "blue" :margin "4px"}
    :class      ;; Class string
    :on-click   ;; Callback when label is clicked
    })


(defn label
  [& {:keys [label style class on-click]
      :as   args}]
  {:pre [(superset? label-args (keys args))]}
  "Returns markup for a basic label"
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
  #{:text       ;; Text of the input
    :on-change  ;; Callback when the text is changed in any way
    :style      ;; CSS style map
    :class      ;; Class string
    })


(defn input-text
  [& {:keys [text on-change style class]
      :as   args}]
  {:pre [(superset? input-text-args (keys args))]}
  "returns markup for a basic text imput label"
  [:input
   {:type "text"
    :class (str "rc-input-text " class)
    :style (merge {:flex "none"} style)
    :value text
    :on-change on-change}])


;; ------------------------------------------------------------------------------------
;;  Component: button
;; ------------------------------------------------------------------------------------

(def button-args
  #{:label      ;; Label for the button (can be artitrary markup)
    :on-click   ;; Callback when the button is clicked
    :style      ;; CSS style map
    :class      ;; Class string. e.g. "btn-info" (see: http://getbootstrap.com/css/#buttons)
    })


(defn button
  [& {:keys [label on-click style class]
      :or   {:class "btn-default"}
      :as   args}]
  {:pre [(superset? button-args (keys args))]}
  "Returns the markup for a basic button."
  [:button
   {:class    (str "rc-button btn " class)
    :style    (merge {:flex "none" :align-self "flex-start"} style)
    :on-click on-click}
   label])


;; ------------------------------------------------------------------------------------
;;  Component: checkbox
;; ------------------------------------------------------------------------------------

(def checkbox-args
  #{:model          ;; Holds state of the checkbox when it is called
    :on-change      ;; When model state is changed, call back with new state
    :label          ;; Checkbox label
    :disabled       ;; Set to true to disable the checkbox
    :style          ;; Checkbox style map
    :label-class    ;; Label class string
    :label-style    ;; Label style map
    })


;; TODO: when disabled, should the text appear "disabled".
(defn checkbox
  [& {:keys [model on-change label disabled style label-class label-style]
      :as   args}]
  {:pre [(superset? checkbox-args (keys args))]}
  "I return the markup for a checkbox, with an optional RHS label."
  (let [model       (deref-or-value model)
        disabled    (deref-or-value disabled)
        callback-fn (if (and on-change (not disabled)) #(on-change (not model)))]     ;; call on-change with either true or false
    [h-box
     :gap "8px"     ;; between the tickbox and the label
     :children [[:input
                 {:class     "rc-checkbox"
                  :type      "checkbox"
                  :style     (merge {:flex "none"} style)
                  :disabled  disabled
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
    :disabled       ;; Set to true to disable the checkbox
    :style          ;; Checkbox style map
    :label-class    ;; Label class string
    :label-style    ;; Label style map
    })


(defn radio-button
  [& {:keys [model value label on-change disabled style label-class label-style]
      :as   args}]
  {:pre [(superset? radio-button-args (keys args))]}
  "I return the markup for a radio button, with an optional RHS label."
  (let [model       (deref-or-value model)
        disabled    (deref-or-value disabled)
        callback-fn (if (and on-change (not disabled)) #(on-change value))]
    [h-box
     :gap "8px"     ;; between the tickbox and the label
     :children [[:input
                 {:class     "rc-radio-button"
                  :type      "radio"
                  :style     (merge {:flex "none"} style)     ;; add in flex child style, so it can sit in a vbox
                  :disabled  disabled
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
  [& {:keys [model]
      :as   args}]
  {:pre [(superset? progress-bar-args (keys args))]}
  "Render a bootstrap styled progress bar"
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
  []
  "Render an animated gif spinner"
  [:div {:style {:display "flex"
                 :flex    "none"
                 :margin "10px"}}
   [:img {:src "resources/img/spinner.gif"
          :style {:margin "auto"}}]])
