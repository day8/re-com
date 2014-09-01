(ns re-com.core
  (:require [reagent.core :as reagent]
            [re-com.util  :as util]
            [re-com.box   :refer [h-box box]]))



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
  [:span
   {:class class
    :style (merge {:flex: "0 0 auto"} style )}
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
    :style    (merge {:flex: "0 0 auto"} style)
    :on-click on-click}
   label])


;; ------------------------------------------------------------------------------------
;;  Checkbox
;; ------------------------------------------------------------------------------------

(defn checkbox
  "I return the markup for a checkbox and optional label."
  [& {:keys [model on-change label disabled readonly style]
      :or   {on-change #()
             disabled false
             readonly false}}]
  (let [model     (if (satisfies? cljs.core/IDeref model) @model model)
        disabled  (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
        readonly  (if (satisfies? cljs.core/IDeref readonly) @readonly readonly)]
    [h-box
     :gap "10px"
     :children [[:input
                 {:type      "checkbox"
                  :style     (merge {:display "inline-flex" :flex: "0 0 auto"} style)
                  :disabled  disabled
                  :checked   model
                  :on-click  #(do
                               (println "on-click: " (not readonly))
                               (not readonly))    ;; a value of false stops changes
                  :on-change #(on-change (-> % .-target .-checked))}]    ;; calls on-change with true or false
                (when label [re-com.core/label :label label])]]))


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