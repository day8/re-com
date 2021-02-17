(ns re-demo.alert-box
  (:require-macros
    [reagent.debug :refer [dbg prn println log dev? warn warn-unless]]
    [re-com.debug  :refer [src-coordinates]])
  (:require
    [re-com.core   :refer [h-box v-box box line gap title label alert-box alert-list p]]
    [re-com.alert  :refer [alert-box-parts-desc alert-box-args-desc alert-list-args-desc]]
    [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util   :refer [px]]
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
       :src      (src-coordinates)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[alert-box ... ]"
                                "src/re_com/alert.cljs"
                                "src/re_demo/alert_box.cljs"]

                  [h-box
                   :src      (src-coordinates)
                   :gap      "100px"
                   :children [[v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A component which renders a single bootstrap styled alert-box."]
                                          [args-table alert-box-args-desc]]]
                              [v-box
                               :src      (src-coordinates)
                               :width    "600px"
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          (if @show-alert1
                                            [alert-box      ;(alert-box-meta alert-box)
                                             :src        (src-coordinates)
                                             :id         1
                                             :alert-type :info
                                             :heading    "This Is An Alert Heading"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of :info which makes it green, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert1 false)]
                                            [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])

                                          [gap
                                           :src  (src-coordinates)
                                           :size "30px"]
                                          [title
                                           :src   (src-coordinates)
                                           :level :level3
                                           :label "Further Variations"]
                                          (when @show-alert2
                                            [:div
                                             [alert-box
                                              :src        (src-coordinates)
                                              :alert-type :info
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert2 false)]])
                                          (when @show-alert3
                                            [:div
                                             [alert-box
                                              :src        (src-coordinates)
                                              :alert-type :warning
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert3 false)]])
                                          [alert-box
                                           :src        (src-coordinates)
                                           :alert-type :danger
                                           :heading    ":alert-type is :danger"
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]
                                          [gap
                                           :src  (src-coordinates)
                                           :size "30px"]
                                          [title
                                           :src   (src-coordinates)
                                           :level :level3
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"]]]
                                          (when @show-alert4
                                            [alert-box
                                             :src        (src-coordinates)
                                             :id         1
                                             :alert-type :none
                                             :heading    "This Is An Unstyled Alert"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of :none, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert4 false)])

                                          [title
                                           :src   (src-coordinates)
                                           :level :level3
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"] " with custom " [:code ":style"] " and " [:code ":body"]]]

                                          (when @show-alert5
                                            [:div
                                             [alert-box
                                              :src        (src-coordinates)
                                              :alert-type :none
                                              :style      {:color             "#222"
                                                           :background-color  "#eff9e3"
                                                           :border-top        "none"
                                                           :border-right      "none"
                                                           :border-bottom     "none"
                                                           :border-left       "4px solid green"
                                                           :border-radius     "0px"}
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert5 false)]])
                                          (when @show-alert6
                                            [:div
                                             [alert-box
                                              :src        (src-coordinates)
                                              :alert-type :none
                                              :style      {:color             "#222"
                                                           :background-color  "rgba(255, 165, 0, 0.1)"
                                                           :border-top        "none"
                                                           :border-right      "none"
                                                           :border-bottom     "none"
                                                           :border-left       "4px solid rgba(255, 165, 0, 0.8)"
                                                           :border-radius     "0px"}
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert6 false)]])
                                          [alert-box
                                           :src        (src-coordinates)
                                           :alert-type :none
                                           :style      {:color             "#333"
                                                        :background-color  "rgba(255, 0, 0, 0.1)"
                                                        :border-top        "none"
                                                        :border-right      "none"
                                                        :border-bottom     "none"
                                                        :border-left       "4px solid rgba(255, 0, 0, 0.8)"
                                                        :border-radius     "0px"}
                                           :heading    ":alert-type is :danger"
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]

                                          [alert-box
                                           :src        (src-coordinates)
                                           :id         1
                                           :alert-type :none
                                           :body       [h-box
                                                        :src      (src-coordinates)
                                                        :gap      "10px"
                                                        :children [[box
                                                                    :src   (src-coordinates)
                                                                    :child [:span "Last scan: 6/8/2015, 1:46:10 PM" [:br] "Scanned in 5.21s"]]
                                                                   [line
                                                                    :src  (src-coordinates)
                                                                    :size "2px" :color "green"]
                                                                   [box
                                                                    :src   (src-coordinates)
                                                                    :child [:span "Vendor:" [:br] "Model:"]]]]
                                           :style      {:background-color "rgba(223, 240, 200, 0.4)"
                                                        :border           "2px solid green"
                                                        :border-radius    "0px"
                                                        :box-shadow       "2px 2px 6px #ccc"}]
                                          [gap
                                           :src  (src-coordinates)
                                           :size "60px"]]]]]
                  [parts-table "alert-box" alert-box-parts-desc]]])))



;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [alert-box-demo])
