(ns reagent-components.popover-form-demo
  (:require [reagent-components.util :as util]
            [reagent-components.alert :as alert]
            [reagent-components.popover :refer [popover make-button make-link]]
            [reagent.core :as reagent]))


(def img1-wink? (reagent/atom false))
(def img2-wink? (reagent/atom false))

(def show-this-popover? (reagent/atom false))
(def initial-form-data (reagent/atom {}))


;; ----- PRIMARY FORM

(def pform-data (reagent/atom {:email "gregg.ramsey@gmail.com"
                               :password "abc123"
                               :description "Description text area"
                               :range 45
                               :datetime "2014-07-10T22:40"
                               :radio "one"
                               :file "" ;; Value property is read-only, which causes problems with react, use fs module stuff
                               :checkbox true}))


(defn pform-submit [event]
  (let [selected-file ""] ;; (aget (.-target event) "file")
    (swap! pform-data assoc :file (.-value selected-file))
    (reset! show-this-popover? false)
    (util/console-log-prstr "Submitted PRIMARY form: pform-data" pform-data)
    (alert/add-alert "info" {:heading "Submitted PRIMARY form" :body (str "Form data submitted: " (. js/cljs.core (pr_str @pform-data)))})
    false)) ;; Prevent default "GET" form submission to server


(defn pform-cancel []
  (let []
    (reset! pform-data @initial-form-data)
    (reset! show-this-popover? false)
    (util/console-log-prstr "Cancelled PRIMARY form: pform-data" pform-data)
    (alert/add-alert "warning" {:heading "Cancelled PRIMARY form" :body (str "Form data reset to original values: " (. js/cljs.core (pr_str @pform-data)))})
    false)) ;; Prevent default "GET" form submission to server


(defn primary-form []
  [:div ;; [:form {:name "pform" :on-submit pform-submit}
   [:div.form-group
    [:label {:for "pf-email"} "Email address"]
    [:input#pf-email.form-control {:name "email"
                                   :type "text"
                                   :placeholder "Type email"
                                   :value (:email @pform-data)
                                   :on-change  #(swap! pform-data assoc :email (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-password"} "Password"]
    [:input#pf-password.form-control {:name "password"
                                      :type "password"
                                      :placeholder "Type password"
                                      :value (:password @pform-data)
                                      :on-change  #(swap! pform-data assoc :password (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-description"} "Description"]
    [:textarea#pf-description.form-control {:name "description"
                                            :placeholder "Write a description here"
                                            :rows 5
                                            :value (:description @pform-data)
                                            :on-change  #(swap! pform-data assoc :description (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-range"} "Range (0-100)"]
    [:input#pf-range {:name "range"
                      :type "range"
                      :min "0"
                      :max "100"
                      :value (:range @pform-data)
                      :on-change  #(swap! pform-data assoc :range (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-datetime"} "Date/Time"]
    [:span " "]
    [:input#pf-datetime {:name "datetime"
                         :type "datetime-local"
                         :style {:font-family "Consolas"}
                         :value (:datetime @pform-data)
                         :on-change  #(swap! pform-data assoc :datetime (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-file"} "File input"]
    [:input#pf-file {:name "file"
                     :type "file"}] ;; Can't do ":value (:file @pform-data)" because it's read-only. Need to just read the value in the submit function
    [:p.help-block "Example block-level help text here."]
    ]
   [:div.checkbox
    [:label [:input {:name "checkbox"
                     :type "checkbox"
                     :checked (:checkbox @pform-data)
                     :on-change  #(swap! pform-data assoc :checkbox (-> % .-target .-checked))}
             "Check me out"]]]
   [:div.form-group
    [:label {:for "pf-radio"} "Select one"]
    [:span " "]
    [:input#pf-radio {:name "radio"
                      :type "radio"
                      :value (:radio @pform-data)
                      :on-change  #(swap! pform-data assoc :radio (-> % .-target .-value))}] "One"
    [:span " "]
    [:input#pf-radio {:name "radio"
                      :type "radio"
                      :value (:radio @pform-data)
                      :on-change  #(swap! pform-data assoc :radio (-> % .-target .-value))}] "Two"
    ]
   [:button.btn.btn-primary {:type "button" ;; submit
                             :on-click pform-submit} "Apply"]
   [:span " "]
   [:button.btn.btn-default {:type "button"
                             :on-click pform-cancel} "Cancel"]
   ])


;; ----- SECONDARY FORM

(def sform-data (reagent/atom {:email ""
                               :password ""
                               :remember-me false}))


(defn sform-submit []
  (util/console-log-prstr "Submitted SECONDARY form: sform-data" sform-data)
  (alert/add-alert "info" {:heading "Submitted SECONDARY form" :body (str "Form data submitted: " (. js/cljs.core (pr_str @sform-data)))})
  false) ;; Prevent default "GET" form submission to server


(defn secondary-form []
  [:div.form-inline ;; [:form.form-inline {:name "sform" :on-submit sform-submit}
   [:div.form-group
    [:label.sr-only {:for "sf-email"} "Email"]
    [:input#sf-email.form-control {:name "email"
                                   :type "text"
                                   :placeholder "Type email"
                                   :value (:email @sform-data)
                                   :on-change  #(swap! sform-data assoc :email (-> % .-target .-value))}]]
   [:span " "]
   [:div.form-group
    [:label.sr-only {:for "sf-passord"} "Password"]
    [:input#sf-passord.form-control {:name "password"
                                     :type "password"
                                     :placeholder "Type password"
                                     :value (:password @sform-data)
                                     :on-change  #(swap! sform-data assoc :password (-> % .-target .-value))}]]
   [:span " "]
   [:div.checkbox
    [:label [:input {:name "remember-me"
                     :type "checkbox"
                     :checked (:checkbox @sform-data)
                     :on-change  #(swap! sform-data assoc :remember-me (-> % .-target .-checked))}
             "Remember me"]]]
   [:span " "]
   [:button.btn.btn-default {:type "button" ;; submit
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

   [:h3 "Image clipping " [:span {:style {:color "red"}} "(click left image)"]]
   [:img.img-rounded.smooth {:src "img/magdeburg-water-bridge.jpg"
                             :alt "Here is the tooltip for this image"
                             :style (merge {:width "49%" :margin-right "10px" :cursor "hand"}
                                           (when @img1-wink? {:border "5px solid purple"}))
                             :on-mouse-over #(reset! img1-wink? true)
                             :on-mouse-out #(reset! img1-wink? false)
                             :on-click #(do (reset! img2-wink? (not @img2-wink?))(util/console-log (str "CLICK-" @img2-wink?)))}]

   [:img.img-rounded.smooth {:src "img/Guru.jpg"
                             :alt "Here is the tooltip for this image"
                             :style (merge {:width "49%"}
                                           (when @img2-wink? {:border "10px solid green" :-webkit-transform "rotate(180deg)"}))}]
   [:hr]

   [:h3 "Checkboxes and Radio buttons"]
   [:div.checkbox [:label [:input {:type "checkbox" :value ""              } "Option one is this and that - be sure to include why it's great"]]]
   [:div.checkbox [:label [:input {:type "checkbox" :value "" :disabled nil} "Option two is disabled (well, not really ... TODO)"]]]
   [:div#optionsRadios1.radio [:label [:input {:type "radio" :name "optionsRadios" :value "option1" :checked nil } "Option one (checked - TODO)"]]]
   [:div#optionsRadios2.radio [:label [:input {:type "radio" :name "optionsRadios" :value "option2"              } "Option two"]]]
   [:div#optionsRadios3.radio [:label [:input {:type "radio" :name "optionsRadios" :value "option3" :disabled nil} "Option three (disabled - TODO)"]]]
   [:hr]

   [:h3 "Selects"]
   [:select.form-control
    [:option "Item 1"]
    (for [line (range 1 10)]
      ^{:key line} [:option (str "Item " line)])
    ]
   [:select.form-control {:style {:margin-top "25px"} :multiple ""} ;; :multiple is ignored :-(
    (for [line (range 1 10)]
      ^{:key line} [:option (str "Item " line)])
    ]
   [:hr]

   [:h3 "Table"]
   [:table.table.table-bordered.table-striped {:style {:margin-top "20px"}}
    (for [row (range 1 9)]
      ^{:key row} [:tr
                   (for [col (range 1 4)]
                     ^{:key col} [:td (str "row-" row " col-" col)])])

    ]])


(defn popover-title []
  [:div "Arbitrary " [:strong "markup"] " example (" [:span {:style {:color "red"}} "red text"] ")"
   [:button.close {:type "button"
                   :style {:font-size "36px" :margin-top "-8px"}
                   :on-click pform-cancel} "×"]])


(defn red-button []
  [:input.btn.btn-danger
   {:type "button"
    :value ":right-below"
    :style {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
    ;; :on-click #(reset! show-this-popover? (not @show-this-popover?))
    :on-click #(if @show-this-popover?
                 (pform-cancel)
                 (do (reset! show-this-popover? true)
                   (reset! initial-form-data @pform-data)
                   (util/console-log-prstr "Initialised PRIMARY form: pform-data" pform-data)))
    }])


(defn show []
  (let [popover-content {:width         800
                         :title         [popover-title]
                         :close-button? false            ;; We have to add our own close button because it does more than simply close the popover
                         :body          [popover-form]
                         }
        popover-options {:arrow-length 80
                         :arrow-width  10}]
    [popover :right-below show-this-popover? (red-button) popover-content popover-options]))
