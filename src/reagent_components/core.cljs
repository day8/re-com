(ns reagent-components.core
  (:require [reagent-components.util :as util]
            [reagent.core            :as reagent]))

;; ------------------------------------------------------------------------------------
;;  Label
;; ------------------------------------------------------------------------------------

(defn label
  [text & {:keys [style class]}]
  "Return the markup for a basic label
  Parameters:
  ...
  "
  [:label
   {:class class
    :style style}
   text])


;; ------------------------------------------------------------------------------------
;;  Input Text
;; ------------------------------------------------------------------------------------

(defn input-text
  [text callback & {:keys [style class]}]
  "Return the markup for a basic text input box
  Parameters:
  ...
  "
  [:input
   {:type "text"
    :class class
    :style style
    :value text
    :on-change #(callback)}])


;; ------------------------------------------------------------------------------------
;;  Button
;; ------------------------------------------------------------------------------------

(defn button
  [text callback & {:keys [style class]
                    :or {class "btn-default"}}]
  "Return the markup for a basic button
  Parameters:
  - text      Text to display on the button
  - callback  The function to call when the button is clicked
  - style     [optional] :style {map} where map is the standard hicckup style map values
  .           e.g. :style {:color \"blue\" :margin \"4px\"}
  - class     [optional] :class "class" where class is one or more of the Bootstrap button styles
  .           e.g. \"btn-info\"
  .           See: http://getbootstrap.com/css/#buttons"
  [:button
   {:class (str "btn " class)
    :style style
    :on-click #(callback)} text])


;; ------------------------------------------------------------------------------------
;;  Checkbox
;; ------------------------------------------------------------------------------------

(defn checkbox
  [text callback & {:keys [style class]}]
  "Return the markup for a basic checkbox
  Parameters:
  ...
  "

  #_[:div.checkbox
   [:label
    [:input
     {:name      "remember-me"
      :type      "checkbox"
      :checked   (:remember-me @form-data)
      :on-change #(swap! form-data assoc :remember-me (-> % .-target .-checked))}
     "Remember me"]]]

  [:input
   {:type "checkbox"
    :class (str "btn " class)
    :style style
    :value text
    :on-click #(callback)}])


;; ------------------------------------------------------------------------------------
;;  Radio Button
;; ------------------------------------------------------------------------------------

(defn radio-button
  [text callback & {:keys [style class]}]
  "Return the markup for a basic radio button
  Parameters:
  ...
  "
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
