(ns re-demo.alert-box
  (:require [re-com.box    :refer [h-box v-box box line gap]]
            [re-com.alert  :refer [alert-box alert-list alert-box-args-desc alert-list-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table]]
            [reagent.core  :as    reagent]))

(defn alert-box-demo
  []
  (let [show-alert (reagent/atom true)
        show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)]
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "[alert-box ... ]"]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :style    {:font-size "small"}
                               :width    "450px"
                               :children [#_[component-title "Notes"]
                                          #_[label :label "A component which renders a single alert-box."]
                                          [args-table alert-box-args-desc]]]
                              [v-box
                               :width    "500px"
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          (if @show-alert
                                            [alert-box
                                             :id         1
                                             :alert-type "info"
                                             :heading    "This Is An Alert Heading"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of 'info' which makes it blue, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert false)]
                                            [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])
                                          [gap :size "50px"]
                                          [:p "Further Variations ..."]
                                          (when @show-alert1
                                            [:div
                                             [alert-box
                                              :alert-type "info"
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert1 false)]])
                                          (when @show-alert2
                                            [:div
                                             [alert-box
                                              :alert-type "warning"
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert2 false)]])
                                          [alert-box
                                           :alert-type "danger"
                                           :heading    ":alert-type is \"danger\""
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]]]]]]])))



(defn panel   ;; Introduce a level of naming indirection so that figwheel updates work
  []
  [alert-box-demo])
