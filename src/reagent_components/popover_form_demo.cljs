(ns reagent-components.popover-form-demo
  (:require [reagent-components.util :as util]
            [reagent-components.alert :refer [add-alert]]
            [reagent-components.popover :refer [popover make-button make-link]]
            [reagent.core :as reagent]))


(def img1-wink? (reagent/atom false))
(def img2-wink? (reagent/atom false))

(def show-this-popover? (reagent/atom false))


;; ----- PRIMARY FORM

(def initial-form-data (reagent/atom {}))
(def form-data (reagent/atom {:email          "gregg.ramsey@gmail.com"
                              :password       "abc123"
                              :description    "Description text area"
                              :range          45
                              :datetime       "2014-07-10T22:40"
                              :file           "" ;; Value property is read-only, which causes problems with react, use fs module stuff
                              :checkbox1      false
                              :checkbox2      true
                              :checkbox3      false
                              :radio-group    "2"
                              :drop-down      nil
                              :listbox-single nil
                              :listbox-multi  ["Item 2" "Item 4" "Item 7"]}))


(defn pform-initialise []
  (reset! initial-form-data @form-data)
  (reset! show-this-popover? true)
  (util/console-log-prstr "Initialised PRIMARY form: form-data" form-data))


(defn pform-submit [event]
  (let [selected-file ""] ;; (aget (.-target event) "file")
    (swap! form-data assoc :file (.-value selected-file))
    (reset! show-this-popover? false)
    (util/console-log-prstr "Submitted PRIMARY form: form-data" form-data)
    (add-alert "info" {:heading "Submitted PRIMARY form" :body (str "Form data submitted: " (. js/cljs.core (pr_str @form-data)))})
    false)) ;; Prevent default "GET" form submission to server


(defn pform-cancel []
  (reset! form-data @initial-form-data)
  (reset! show-this-popover? false)
  (util/console-log-prstr "Cancelled PRIMARY form: form-data" form-data)
  (add-alert "warning" {:heading "Cancelled PRIMARY form" :body (str "Form data reset to original values: " (. js/cljs.core (pr_str @form-data)))})
  false) ;; Returning false prevent default "GET" form submission to server in on-click event for forms


(defn primary-form []
  [:div {:style {:padding "5px" :background-color "cornsilk" :border "1px solid #eee"}} ;; [:form {:name "pform" :on-submit pform-submit}
   [:div.form-group
    [:label {:for "pf-email"} "Email address"]
    [:input#pf-email.form-control
     {:name        "email"
      :type        "text"
      :placeholder "Type email"
      :value       (:email @form-data)
      :on-change   #(swap! form-data assoc :email (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-password"} "Password"]
    [:input#pf-password.form-control
     {:name        "password"
      :type        "password"
      :placeholder "Type password"
      :value       (:password @form-data)
      :on-change   #(swap! form-data assoc :password (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-desc"} "Description"]
    [:textarea#pf-desc.form-control
     {:name        "description"
      :placeholder "Write a description here"
      :rows        5
      :value       (:description @form-data)
      :on-change   #(swap! form-data assoc :description (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-range"} "Range (0-100)"]
    [:input#pf-range
     {:name      "range"
      :type      "range"
      :min       "0"
      :max       "100"
      :value     (:range @form-data)
      :on-change #(swap! form-data assoc :range (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-datetime"} "Date/Time"]
    [:span " "]
    [:input#pf-datetime
     {:name      "datetime"
      :type      "datetime-local"
      :style     {:font-family "Consolas"}
      :value     (:datetime @form-data)
      :on-change #(swap! form-data assoc :datetime (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-file"} "File input"]
    [:input#pf-file
     {:name "file"
      :type "file"}] ;; Can't do ":value (:file @form-data)" because it's read-only. Need to just read the value in the submit function
    [:p.help-block "This is the Bootstrap help text style."]
    ]

   [:h4 "Checkboxes and Radio buttons"]
   [:div.container-fluid
    [:div.row
     [:div.col-lg-4
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
     [:div.col-lg-4
      [:div.radio
       [:label {:for "pf-radio1"}
        [:input#pf-radio1
         {:type      "radio"
          :name      "rgroup" ;; TODO: REMOVE ???????????
          :value     "1"  ;; TODO: REMOVE??????
          :checked   (= (:radio-group @form-data) "1") ;; TODO: A bit nasty, ideally get from value
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

   ;; NOTE: Can't use "selected" attribute on <option>s in React/Reagent
   ;;       Must set "value" property on <select>
   ;;       See http://facebook.github.io/react/docs/forms.html#why-select-value

   [:h4 "Selects"]
   [:div.container-fluid
    [:div.row
     [:div.col-lg-4
      [:p "Drop-down List (Combo Box)"]
      [:select.form-control {:value     (:drop-down @form-data)
                             :on-change #(swap! form-data assoc :drop-down (-> % .-target .-value))}
       ^{:key 0} [:option {:value ""} "-- Select an option --"]
       (for [line (range 1 21)]
         ^{:key line} [:option {:value line} (str "Item " line)]) ;; value will be "1" to "20"
       ]
      ]
     [:div.col-lg-4
      [:p "Single-select List Box"]
      [:select.form-control {:size      8
                             :value     (:listbox-single @form-data)
                             :on-change #(swap! form-data assoc :listbox-single (-> % .-target .-value))}
       (for [line (range 1 21)]
         ^{:key line} [:option (str "Item " line)])               ;; value will be "Item 1" to "Item 20" because value not explicitly set
       ]
      ]
     [:div.col-lg-4
      [:p "Multi-select List Box"]
      [:select.form-control
       {:size      8
        :multiple  true
        :value     (:listbox-multi @form-data)
        :on-change #(let [selected-nodes  (-> % .-target .-selectedOptions) ;; HTMLElement array is not ISeq-able (could make a fucntion out of this) TODO: TRY ################ js->cljs
                          count           (.-length selected-nodes)
                          selected-values (for [index (range count)
                                                :let [item (.-value (aget selected-nodes index))]]
                                            item)]
                      (swap! form-data assoc :listbox-multi (vec selected-values)))}
       (for [line (range 1 21)]
         ^{:key line} [:option (str "Item " line)])
       ]
      ]]]

   [:hr {:style {:margin "10px 0 10px"}}]
   [:button.btn.btn-primary
    {:type     "button" ;; submit
     :on-click pform-submit}
    "Apply"]
   [:span " "]
   [:button.btn.btn-default
    {:type     "button"
     :on-click pform-cancel}
    "Cancel"]
   ])


;; ----- SECONDARY FORM

(def sform-data (reagent/atom {:email       ""
                               :password    ""
                               :remember-me false}))


(defn sform-submit []
  (util/console-log-prstr "Submitted SECONDARY form: sform-data" sform-data)
  (add-alert "info" {:heading "Submitted SECONDARY form" :body (str "Form data submitted: " (. js/cljs.core (pr_str @sform-data)))})
  false) ;; Prevent default "GET" form submission to server


(defn secondary-form []
  [:div.form-inline
   {:style {:padding          "5px"
            :background-color "cornsilk"
            :border           "1px solid #eee"}} ;; [:form.form-inline {:name "sform" :on-submit sform-submit}
   [:div.form-group
    [:label.sr-only {:for "sf-email"} "Email"]
    [:input#sf-email.form-control
     {:name        "email"
      :type        "text"
      :placeholder "Type email"
      :value       (:email @sform-data)
      :on-change   #(swap! sform-data assoc :email (-> % .-target .-value))}]]
   [:span " "]
   [:div.form-group
    [:label.sr-only {:for "sf-passord"} "Password"]
    [:input#sf-passord.form-control
     {:name        "password"
      :type        "password"
      :placeholder "Type password"
      :value       (:password @sform-data)
      :on-change   #(swap! sform-data assoc :password (-> % .-target .-value))}]]
   [:span " "]
   [:div.checkbox
    [:label
     [:input
      {:name      "remember-me"
       :type      "checkbox"
       :checked   (:checkbox @sform-data)
       :on-change #(swap! sform-data assoc :remember-me (-> % .-target .-checked))}
      "Remember me"]]]
   [:span " "]
   [:button.btn.btn-default {:type     "button" ;; submit
                             :on-click sform-submit} "Sign in"]]
  )


(defn popover-form []
  [:div
   [:h3 "Primary Form"]
   [:p "Here is a form which has some events"]
   [primary-form]
   [:hr]

   [:h3 "Secondary Form - Inline"]
   [secondary-form]
   [:hr]

   [:h3 "Image clipping "
    [:span {:style {:color "red"}}
     "(click left image)"]]
   [:img.img-rounded.smooth
    {:src           "img/magdeburg-water-bridge.jpg"
     :alt           "Here is the tooltip for this image"
     :style         (merge {:width "49%" :margin-right "10px" :cursor "hand"}
                           (when @img1-wink? {:border "5px solid purple"}))
     :on-mouse-over #(reset! img1-wink? true)
     :on-mouse-out  #(reset! img1-wink? false)
     :on-click      #(do (reset! img2-wink? (not @img2-wink?))(util/console-log (str "CLICK-" @img2-wink?)))}]
   [:img.img-rounded.smooth
    {:src   "img/Guru.jpg"
     :alt   "Here is the tooltip for this image"
     :style (merge {:width "49%"}
                   (when @img2-wink?
                     {:border            "10px solid green"
                      :-webkit-transform "rotate(180deg)"}))}]
   [:hr]

   [:h3 "Table"]
   [:table.table.table-bordered.table-striped {:style {:margin-top "20px"}}
    (for [row (range 1 11)]
      ^{:key row} [:tr
                   (for [col (range 1 6)]
                     ^{:key col} [:td (str "row-" row " col-" col)])])]
   ])


(defn popover-title []
  [:div "Arbitrary " [:strong "markup"] " example (" [:span {:style {:color "red"}} "red text"] ")"
   [:button.close
    {:type     "button"
     :style    {:font-size "36px" :margin-top "-8px"}
     :on-click pform-cancel} "×"]])


(defn red-button []
  [:input.btn.btn-danger
   {:type     "button"
    :value    ":right-below"
    :style    {:flex-grow 0
               :flex-shrink 1
               :flex-basis "auto"}
    :on-click #(if @show-this-popover?
                 (pform-cancel)
                 (pform-initialise))
    }])


(defn show []
  (let [popover-content {:width         800
                         :title         [popover-title]
                         :close-button? false            ;; We have to add our own close button because it does more than simply close the popover
                         :body          [popover-form]}
        popover-options {:arrow-length      80
                         :arrow-width       10
                         :backdrop-callback pform-cancel
                         :backdrop-opacity  .3}]
    [popover :right-below show-this-popover? (red-button) popover-content popover-options]))
