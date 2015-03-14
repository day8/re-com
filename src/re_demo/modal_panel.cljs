(ns re-demo.modal-panel
  (:require [re-com.core        :refer [h-box v-box box gap line border title label modal-panel progress-bar input-text checkbox button]]
            [re-com.modal-panel :refer [modal-panel-args-desc modal-panel-args]]
            [re-demo.utils      :refer [panel-title component-title args-table github-hyperlink status-text]]
            [reagent.core       :as    reagent]))


(defn please-wait-message
  "Create a button to show a 'Please wait...' message for 3 seconds"
  []
  (let [show? (reagent/atom false)]
    (fn []
      [v-box
       :children [[button
                   :label    "Please wait message"
                   :class    "btn-info"
                   :on-click (fn []
                               (reset! show? true)
                               (js/setTimeout #(reset! show? false) 3000))]
                  (when @show?
                    [modal-panel
                     :child [:span "Please wait..."]])]])))


(defn progress-bar-with-cancel-button
  "Create a button to show a modal with progress bar and cancel button. Not actually operational."
  []
  (let [show? (reagent/atom false)]
    (fn []
      [v-box
       :children [[button
                   :label    "Progress bar with cancel button"
                   :class    "btn-info"
                   :on-click #(reset! show? true)]
                  (when @show?
                    [modal-panel
                     :child [v-box
                             :width    "300px"
                             :children [[:p {:style {:text-align "center"}}
                                         [:strong "Recalculating..."] [:br]
                                         [:strong "Current step: 2 of 3"]]
                                        [progress-bar
                                         :model 33]
                                        [:span
                                         [button
                                          :label "Cancel"
                                          :class "btn-danger"
                                          :on-click #(reset! show? false)]
                                         [:span {:style {:font-variant "small-caps"}} " (static display only, press Cancel)"]]]]])]])))


(defn dialog-markup
  [form-data process-ok process-cancel]
  [border
   :border "1px solid #eee"
   :child  [v-box
            :padding  "10px"
            :style    {:background-color "cornsilk"}
            :children [[title :label "Welcome to MI6. Please log in" :h :h3 :underline? false]
                       [v-box
                        :class    "form-group"
                        :children [[:label {:for "pf-email"} "Email address"]
                                   [input-text
                                    :model       (:email @form-data)
                                    :on-change   #(swap! form-data assoc :email %)
                                    :placeholder "Enter email"
                                    :class       "form-control"
                                    :attr        {:id "pf-email"}]]]
                       [v-box
                        :class    "form-group"
                        :children [[:label {:for "pf-password"} "Password"]
                                   [input-text
                                    :model       (:password @form-data)
                                    :on-change   #(swap! form-data assoc :password %)
                                    :placeholder "Enter password"
                                    :class       "form-control"
                                    :attr        {:id "pf-password" :type "password"}]]]
                       [checkbox
                        :label     "Forget me"
                        :model     (:remember-me @form-data)
                        :on-change #(swap! form-data assoc :remember-me %)]
                       [line :color "#ddd" :style {:margin "10px 0 10px"}]
                       [h-box
                        :gap      "12px"
                        :children [[button
                                    :label    "Sign in"
                                    :class    "btn-primary"
                                    :on-click process-ok]
                                   [button
                                    :label    "Cancel"
                                    :on-click process-cancel]]]]]])


(defn modal-dialog
  "Create a button to test the modal component for modal dialogs"
  []
  (let [showing?       (reagent/atom false)
        form-data      (reagent/atom {:email       "james.bond.007@sis.gov.uk"
                                      :password    "abc123"
                                      :remember-me true})
        save-form-data (reagent/atom nil)
        process-ok     (fn [event]
                         (reset! showing? false)
                         (println "Submitted form data: " @form-data)
                         ;; ***** PROCESS THE RETURNED DATA HERE
                         false) ;; Prevent default "GET" form submission (if used)
        process-cancel (fn [event]
                         (reset! form-data @save-form-data)
                         (reset! showing? false)
                         (println "Cancelled form data: " @form-data)
                         false)]
    (fn []
      [v-box
       :children [[button
                   :label    "Modal Dialog"
                   :class    "btn-info"
                   :on-click #(do
                               (reset! save-form-data @form-data)
                               (reset! showing? true))]
                  (when @showing? [modal-panel
                                   :backdrop-color   "grey"
                                   :backdrop-opacity 0.4
                                   :style            {:font-family "Consolas"}
                                   :child            [dialog-markup
                                                      form-data
                                                      process-ok
                                                      process-cancel]])]])))


(defn panel2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[modal-panel ... ]"
                            [github-hyperlink "Component Source" "src/re_com/modal_panel.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/modal_panel.cljs"]]]

              [h-box
               :gap      "50px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[status-text "Alpha"]
                                      [component-title "Notes"]
                                      [:p "This component should be placed at the end of your markup to make sure it does actually cover everything. In certain cases, absolutely positioned components can appear over the backdrop."]
                                      [args-table modal-panel-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [v-box
                                       :gap      "10px"
                                       :children [[please-wait-message]
                                                  [progress-bar-with-cancel-button]
                                                  [modal-dialog]]]]]]]]])

(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
