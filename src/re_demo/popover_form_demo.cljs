(ns re-demo.popover-form-demo
  (:require [reagent.core    :as    reagent]
            [re-com.util     :as    util]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.core    :refer  [button label spinner progress-bar checkbox radio-button]]
            [re-com.dropdown :refer [single-dropdown find-choice filter-choices-by-keyword]]
            [re-com.popover  :refer [popover make-button make-link]]))


(def show-this-popover? (reagent/atom false))


;; ----- PRIMARY FORM

(def initial-form-data (reagent/atom {}))
(def form-data (reagent/atom {:checkbox1      false
                              :checkbox2      true
                              :checkbox3      false
                              :radio-group    "2"}))


(defn form-initialise
  []
  (reset! initial-form-data @form-data)
  (reset! show-this-popover? true)
  (println "Initialised PRIMARY form: form-data" form-data)
  (util/console-log-prstr "Initialised PRIMARY form: form-data" form-data))


(defn form-submit
  [event]
  (let [selected-file ""] ;; (aget (.-target event) "file")
    (swap! form-data assoc :file (.-value selected-file))
    (reset! show-this-popover? false)
    (println "Submitted PRIMARY form: form-data" form-data)
    (util/console-log-prstr "Submitted PRIMARY form: form-data" form-data)
    false)) ;; Prevent default "GET" form submission to server


(defn form-cancel
  []
  (reset! form-data @initial-form-data)
  (reset! show-this-popover? false)
  (util/console-log-prstr "Cancelled PRIMARY form: form-data" form-data)
  false) ;; Returning false prevent default "GET" form submission to server in on-click event for forms


(defn sample-form-DELETE!
  []
  [:div {:style {:padding "5px" :background-color "cornsilk" :border "1px solid #eee"}} ;; [:form {:name "pform" :on-submit pform-submit}
   [:h4 "Checkboxes and Radio buttons"]
   [:div.container-fluid
    [:div.row
     [:div.col-lg-6
      [:div.checkbox
       [:label
        [:input
         {:type      "checkbox"
          :name      "cb1"
          :checked   (:checkbox1 @form-data)
          :on-change #(swap! form-data assoc :checkbox1 (-> % .-target .-checked))}
         "Red (toggle disabled)"]]]
      [:div.checkbox
       [:label
        [:input
         {:type      "checkbox"
          :name      "cb2"
          :checked   (:checkbox2 @form-data)
          :on-change #(swap! form-data assoc :checkbox2 (-> % .-target .-checked))}
         "Green (initially checked)"]]]
      [:div.checkbox
       [:label
        [:input
         {:type      "checkbox"
          :name      "cb3"
          :disabled  (not (:checkbox1 @form-data))
          :checked   (:checkbox3 @form-data)
          :on-change #(swap! form-data assoc :checkbox3 (-> % .-target .-checked))}
         (if (:checkbox1 @form-data) "Blue" "Blue (disabled)")]]] ;; (str "Blue" (when-not (:checkbox1 @form-data) " (disabled)"))
      ]
     [:div.col-lg-6
      [:div.radio
       [:label {:for "pf-radio1"}
        [:input#pf-radio1
         {:type      "radio"
          :name      "rgroup"                             ;; TODO: REMOVE ???????????
          :value     "1"                                  ;; TODO: REMOVE??????
          :checked   (= (:radio-group @form-data) "1")    ;; TODO: A bit nasty, ideally get from value
          :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))} ;; (-> % .-target .-value) ==> "1" ???????????
         "Hue"]]]
      [:div.radio
       [:label {:for "pf-radio2"}
        [:input#pf-radio2
         {:type      "radio"
          :name      "rgroup"
          :value     "2"
          :checked   (= (:radio-group @form-data) "2")
          :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))}
         "Saturation (initially checked)"]]]
      [:div.radio
       [:label {:for "pf-radio3"}
        [:input#pf-radio3
         {:type      "radio"
          :name      "rgroup"
          :value     "3"
          :disabled  (not (:checkbox1 @form-data))
          :checked   (= (:radio-group @form-data) "3")
          :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))}
         (str "Luminance" (when-not (:checkbox1 @form-data) " (disabled)"))]]]
      ]]]

   [:hr {:style {:margin "10px 0 10px"}}]
   [button
    :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
    :on-click form-submit
    :class "btn-primary"]
   [:span " "]
   [button
    :label [:span [:span.glyphicon.glyphicon-remove] " Cancel"]
    :on-click form-cancel]
   ])


(defn sample-form
  []
  [v-box
   ;{:style {:padding "5px" :background-color "cornsilk" :border "1px solid #eee"}} ;; [:form {:name "pform" :on-submit pform-submit}
   :children [[:h4 "Checkboxes and Radio buttons"]
              [:div.container-fluid
               [:div.row
                [:div.col-lg-6

                 [checkbox
                  :label     "Red (toggle disabled)"
                  :model     (:checkbox1 @form-data)
                  :on-change #(swap! form-data assoc :checkbox1 (-> % .-target .-checked))]

                 #_[:div.checkbox
                  [:label
                   [:input
                    {:type      "checkbox"
                     :name      "cb1"
                     :checked   (:checkbox1 @form-data)
                     :on-change #(swap! form-data assoc :checkbox1 (-> % .-target .-checked))}
                    "Red (toggle disabled)"]]]

                 [:div.checkbox
                  [:label
                   [:input
                    {:type      "checkbox"
                     :name      "cb2"
                     :checked   (:checkbox2 @form-data)
                     :on-change #(swap! form-data assoc :checkbox2 (-> % .-target .-checked))}
                    "Green (initially checked)"]]]
                 [:div.checkbox
                  [:label
                   [:input
                    {:type      "checkbox"
                     :name      "cb3"
                     :disabled  (not (:checkbox1 @form-data))
                     :checked   (:checkbox3 @form-data)
                     :on-change #(swap! form-data assoc :checkbox3 (-> % .-target .-checked))}
                    (if (:checkbox1 @form-data) "Blue" "Blue (disabled)")]]] ;; (str "Blue" (when-not (:checkbox1 @form-data) " (disabled)"))
                 ]
                [:div.col-lg-6
                 [:div.radio
                  [:label {:for "pf-radio1"}
                   [:input#pf-radio1
                    {:type      "radio"
                     :name      "rgroup"                             ;; TODO: REMOVE ???????????
                     :value     "1"                                  ;; TODO: REMOVE??????
                     :checked   (= (:radio-group @form-data) "1")    ;; TODO: A bit nasty, ideally get from value
                     :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))} ;; (-> % .-target .-value) ==> "1" ???????????
                    "Hue"]]]
                 [:div.radio
                  [:label {:for "pf-radio2"}
                   [:input#pf-radio2
                    {:type      "radio"
                     :name      "rgroup"
                     :value     "2"
                     :checked   (= (:radio-group @form-data) "2")
                     :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))}
                    "Saturation (initially checked)"]]]
                 [:div.radio
                  [:label {:for "pf-radio3"}
                   [:input#pf-radio3
                    {:type      "radio"
                     :name      "rgroup"
                     :value     "3"
                     :disabled  (not (:checkbox1 @form-data))
                     :checked   (= (:radio-group @form-data) "3")
                     :on-change #(swap! form-data assoc :radio-group (-> % .-target .-value))}
                    (str "Luminance" (when-not (:checkbox1 @form-data) " (disabled)"))]]]
                 ]]]

              [:hr {:style {:margin "10px 0 10px"}}]
              [button
               :label [:span [:span.glyphicon.glyphicon-ok] " Apply"]
               :on-click form-submit
               :class "btn-primary"]
              [:span " "]
              [button
               :label [:span [:span.glyphicon.glyphicon-remove] " Cancel"]
               :on-click form-cancel]
              ]
   ])


(defn popover-form
  []
  [:div
   [:h3 "Primary Form"]
   [:p "Here is a form which has some events"]
   [sample-form]])


(defn popover-title
  []
  [:div "Arbitrary " [:strong "markup"] " example (" [:span {:style {:color "red"}} "red text"] ")"
   [button :label "×" :on-click form-cancel
    :class "close"
    :style {:font-size "36px" :margin-top "-8px"}]
   ])


(defn red-button
  []
  [:input.btn.btn-danger
   {:type     "button"
    :value    "Popover Form"
    :style    {:flex-grow 0
               :flex-shrink 1
               :flex-basis "auto"}
    :on-click #(if @show-this-popover?
                 (form-cancel)
                 (form-initialise))
    }])


(defn show
  []
  (let [popover-content {:width         500
                         :title         [popover-title]
                         :close-button? false            ;; We have to add our own close button because it does more than simply close the popover
                         :body          [popover-form]}
        popover-options {:arrow-length      15
                         :arrow-width       10
                         :backdrop-callback form-cancel
                         :backdrop-opacity  0.3}]
    [popover
     :position :below-center
     :showing? show-this-popover?
     :anchor   [red-button]
     :popover  popover-content
     :options  popover-options]))
