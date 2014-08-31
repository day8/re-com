(ns re-com.core
  (:require [reagent.core :as reagent]
            [re-com.util  :as util]))



;; ------------------------------------------------------------------------------------
;;  Gaps
;; ------------------------------------------------------------------------------------

;; TODO remove ??
(defn gap-old
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
  "returns the markup for a basic label"
  [:label
   {:class class
    :style style}
   label])


;; ------------------------------------------------------------------------------------
;;  Input Text
;; ------------------------------------------------------------------------------------

(defn input-text
  [text callback & {:keys [style class]}]
  "returns the markup for a basic text imput label"
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
    :style    style
    :on-click on-click}
   label])


;; ------------------------------------------------------------------------------------
;;  Checkbox
;; ------------------------------------------------------------------------------------

;; TODO:  label ???  probably needs to be an hbox ?  or make that a labeled-checkbox ?
;; provide a model or a callback ??
;; what if model is nil ??
;; document that con-chamnge will be passed the new value
(defn checkbox
  [& {:keys [model on-change label disabled readonly style class]
      :or   {:on-change #() :disabled false :readonly false}}]
  (let [ ; current  @model
         disabled     (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
         readonly     (if (satisfies? cljs.core/IDeref disabled) @readonly readonly)]
    "I return the markup for a basic checkbox"
    [:input
     {:type "checkbox"
      :class (str "btn " class)
      :style style
      :value label
      :disabled disabled
      :readonly readonly
      :checked  (if model "true" "false")
      :onclick  #(on-change (.-checked %))}]))


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