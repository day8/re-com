(ns re-demo.modal-panel
  (:require [re-com.core        :refer [h-box v-box box gap line border title label modal-panel progress-bar input-text checkbox button p]]
            [re-com.modal-panel :refer [modal-panel-parts-desc modal-panel-args-desc]]
            [re-demo.utils      :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util        :refer [px]]
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
                     :backdrop-on-click #(reset! show? false)
                     :child             [:span "Please wait for 3 seconds" [:br] "(or click on backdrop)"]])]])))


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
                     :backdrop-on-click #(reset! show? false)
                     :child [v-box
                             :width    "300px"
                             :children [[title :level :level2 :label "Recalculating..."]
                                        [gap :size "20px"]
                                        [progress-bar
                                         :model 33]
                                        [gap :size "10px"]
                                        [h-box
                                         :children [[button
                                                     :label    "Cancel"
                                                     :class    "btn-danger"
                                                     :style    {:margin-right "15px"}
                                                     :on-click #(reset! show? false)]
                                                    [:span "pretend only, click Cancel" [:br] "(or click on backdrop)"]]]]]])]])))


(defn dialog-markup
  [form-data process-ok process-cancel]
  [border
   :border "1px solid #eee"
   :child  [v-box
            :padding  "10px"
            :style    {:background-color "cornsilk"}
            :children [[title :label "Welcome to MI6. Please log in" :level :level2]
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
  (let [show?       (reagent/atom false)
        form-data      (reagent/atom {:email       "james.bond.007@sis.gov.uk"
                                      :password    "abc123"
                                      :remember-me true})
        save-form-data (reagent/atom nil)
        process-ok     (fn [event]
                         (reset! show? false)
                         (println "Submitted form data: " @form-data)
                         ;; ***** PROCESS THE RETURNED DATA HERE
                         false) ;; Prevent default "GET" form submission (if used)
        process-cancel (fn [event]
                         (reset! form-data @save-form-data)
                         (reset! show? false)
                         (println "Cancelled form data: " @form-data)
                         false)]
    (fn []
      [v-box
       :children [[button
                   :label    "Modal Dialog"
                   :class    "btn-info"
                   :on-click #(do
                               (reset! save-form-data @form-data)
                               (reset! show? true))]
                  (when @show? [modal-panel
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
   :children [[panel-title "[modal-panel ... ]"
                            "src/re_com/modal_panel.cljs"
                            "src/re_demo/modal_panel.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Displays a " [:code ":child"] " component centered with a semi-transparent
                                       backdrop, which prevents other user interaction."]
                                      [p "Good for showing progress of long running operations and gathering user
                                       input via modal dialogs."]
                                      [p "Warning: This component should be placed at the end of surrounding markup
                                       to ensure the backdrop covers everything. Otherwise, in certain cases,
                                       absolutely positioned components added to the DOM after this component can
                                       appear above the backdrop."]
                                      [args-table modal-panel-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-box
                                       :gap      "10px"
                                       :children [[please-wait-message]
                                                  [progress-bar-with-cancel-button]
                                                  [modal-dialog]]]]]]]
              [parts-table "modal-panel" modal-panel-parts-desc]]])



;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
