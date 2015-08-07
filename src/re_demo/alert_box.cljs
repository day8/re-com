(ns re-demo.alert-box
  (:require [re-com.core   :refer [h-box v-box box line gap title label alert-box alert-list p]]
            [re-com.alert  :refer [alert-box-args-desc alert-list-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.debug :refer-macros [dbg prn println log dev? warn warn-unless]]
            [reagent.core  :as    reagent]))

(defn alert-box-demo
  []
  (let [show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)
        show-alert3 (reagent/atom true)
        show-alert4 (reagent/atom true)
        show-alert5 (reagent/atom true)
        show-alert6 (reagent/atom true)]
    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[alert-box ... ]"
                                "src/re_com/alert.cljs"
                                "src/re_demo/alert_box.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A component which renders a single bootstrap styled alert-box."]
                                          [args-table alert-box-args-desc]]]
                              [v-box
                               :width    "600px"
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          (if @show-alert1
                                            [alert-box      ;(alert-box-meta alert-box)
                                             :id         1
                                             :alert-type :info
                                             :heading    "This Is An Alert Heading"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of :info which makes it green, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert1 false)]
                                            [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])

                                          [gap :size "30px"]
                                          [title
                                           :level :level3
                                           :label "Further Variations"]
                                          (when @show-alert2
                                            [:div
                                             [alert-box
                                              :alert-type :info
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert2 false)]])
                                          (when @show-alert3
                                            [:div
                                             [alert-box
                                              :alert-type :warning
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert3 false)]])
                                          [alert-box
                                           :alert-type :danger
                                           :heading    ":alert-type is :danger"
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]

                                          [gap :size "30px"]
                                          [title
                                           :level :level3
                                           :label [:span "\"modern\" " [:code ":alert-type"] " Variations"]]
                                          (when @show-alert4
                                            [:div
                                             [alert-box
                                              :alert-type :info-modern
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert4 false)]])
                                          (when @show-alert5
                                            [:div
                                             [alert-box
                                              :alert-type :warning-modern
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert5 false)]])
                                          [alert-box
                                           :alert-type :danger-modern
                                           :heading    ":alert-type is :danger"
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]

                                          [gap :size "30px"]
                                          [title
                                           :level :level3
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"]]]
                                          (when @show-alert6
                                            [alert-box
                                             :id         1
                                             :alert-type :none
                                             :heading    "This Is An Unstyled Alert"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of :none, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert6 false)])

                                          [title
                                           :level :level3
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"] " with custom " [:code ":style"] " and " [:code ":body"]]]
                                          [alert-box
                                           :id         1
                                           :alert-type :none
                                           :body       [h-box
                                                        :gap      "10px"
                                                        :children [[box :child [:span "Last scan: 6/8/2015, 1:46:10 PM" [:br] "Scanned in 5.21s"]]
                                                                   [line :size "2px" :color "green"]
                                                                   [box :child [:span "Vendor:" [:br] "Model:"]]]]
                                           :style      {:background-color "rgba(223, 240, 200, 0.4)"
                                                        :border           "2px solid green"
                                                        :border-radius    "0px"
                                                        :box-shadow       "2px 2px 6px #ccc"}]
                                          [gap :size "60px"]]]]]]])))



;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [alert-box-demo])
