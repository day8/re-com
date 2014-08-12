(ns re-com.core
  (:require [reagent.core :as reagent]
            [re-com.util  :as util]))



;; ------------------------------------------------------------------------------------
;;  Gaps
;; ------------------------------------------------------------------------------------


(defn gap
  [&{:keys [height width]}]
  (let [h-style  (if height {:padding-top  (str height "px")} {})
        w-style  (if width  {:padding-left (str width  "px")} {})
        s        (merge (merge h-style w-style))]
  [:div {:style s}]))


;; ------------------------------------------------------------------------------------
;;  Label
;; ------------------------------------------------------------------------------------

(defn label
  [& {:keys [label style class]}]
  "Return the markup for a basic label
  Parameters:
  ...
  "
  [:label
   {:class class
    :style style}
   label])


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
    :on-change callback}])


;; ------------------------------------------------------------------------------------
;;  Button
;; ------------------------------------------------------------------------------------

(defn button
  [& {:keys [label on-click style class]
      :or {:label "blank" :class "btn-default"}}]
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
   {:class    (str "btn " class)
    :style    style
    :on-click on-click}
   label])


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
  "Render a bootstrap styled progress bar
  "
  [:div.progress
   [:div.progress-bar ;;.progress-bar-striped.active
    {:role "progressbar"
     :style {:width (str @progress-percent "%")
             :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
    (str @progress-percent "%")]]
  )

