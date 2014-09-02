(ns re-com.core
  (:require [reagent.core :as reagent]
            [re-com.util  :as util]
            [re-com.box   :refer [h-box box gap]]))


;; ------------------------------------------------------------------------------------
;;  Label
;; ------------------------------------------------------------------------------------

(defn label
  [& {:keys [label style class on-click]}]
  "returns markup for a basic label"
  [:span
   (merge
      {:class class
       :style (merge {:flex "none"} style)}
      (when on-click {:on-click #(do (println "click") (on-click))}))
   label])


;; ------------------------------------------------------------------------------------
;;  Input Text
;; ------------------------------------------------------------------------------------

(defn input-text
  [text callback & {:keys [style class]}]
  "returns markup for a basic text imput label"
  [:input
   {:type "text"
    :class class
    :style (merge {:flex "none"} style)
    :value text
    :on-change callback}])


;; ------------------------------------------------------------------------------------
;;  Button
;; ------------------------------------------------------------------------------------

(defn button
  [& {:keys [label on-click style class]
      :or {:class "btn-default"}}]
  "Return the markup for a basic button
   Parameters:
    - on-click  The function to call when the button is clicked
    - style     [optional] a map. Standard hicckup style map values
                e.g. {:color \"blue\" :margin \"4px\"}
    - class     [optional] string. One or more of the Bootstrap button styles
                e.g. \"btn-info\"
                See: http://getbootstrap.com/css/#buttons"
  [:button
   {:class    (str "btn " class)
    :style    (merge {:flex "none"} style)
    :on-click on-click}
   label])


;; ------------------------------------------------------------------------------------
;;  Checkbox
;; ------------------------------------------------------------------------------------
;; TODO: when disabled, should the text appear "disabled".
(defn checkbox
  "I return the markup for a checkbox, with an optional RHS label."
  [& {:keys [model on-change label disabled readonly style]}]
  (let [model      (if (satisfies? cljs.core/IDeref model)    @model    model)
        disabled   (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
        changeable (and on-change (not disabled))
        callback   (when changeable
                     #(on-change (not model)))]     ;; call on-change with either true or false
    [h-box
     :gap "8px"     ;; between the tickbox and the label
     :children [[:input
                 {:type      "checkbox"
                  :style     (merge {:flex "none"} style)
                  :disabled  disabled
                  :checked   model
                  :on-change callback}]
                (when label [re-com.core/label
                             :label label
                             :on-click callback])]]))    ;; ticking on the label is the same as clicking on the checkbox



;; ------------------------------------------------------------------------------------
;;  Radio Button
;; ------------------------------------------------------------------------------------

(defn radio-button
  [text callback & {:keys [style class]}]
  "Return the markup for a basic radio button
   Parameters:
   ..."
  #_[:div.radio
   [:label {:for "pf-radio2"}
    [:input#pf-radio2
     {:type      "radio"
      :name      "rgroup"
      :value     "2"
      :checked   (= (:radio-group @form-data) "2")
      :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))}
     "Saturation (initially checked)"]]]

  [:input
   {:type "radio"
    :class (str "btn " class)
    :style style
    :value text
    :on-click #(callback)}])


;; ------------------------------------------------------------------------------------
;;  spinner
;; ------------------------------------------------------------------------------------

(defn spinner
  []
  "Render an animated gif spinner"
  [:div {:style {:display "flex"
                 :margin "10px"}}
   [:img {:src "img/spinner.gif"
          :style {:margin "auto"}}]])


;; ------------------------------------------------------------------------------------
;;  progress-bar
;; ------------------------------------------------------------------------------------

(defn progress-bar
  [progress-percent]
  "Render a bootstrap styled progress bar"
  [:div.progress
   [:div.progress-bar ;;.progress-bar-striped.active
    {:role "progressbar"
     :style {:width (str @progress-percent "%")
             :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
    (str @progress-percent "%")]])