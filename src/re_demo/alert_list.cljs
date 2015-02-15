(ns re-demo.alert-list
  (:require [re-com.util        :refer [insert-nth remove-id-item]]
            [re-com.core        :refer [label title]]
            [re-com.buttons     :refer [button]]
            [re-com.tabs        :refer [vertical-bar-tabs]]
            [re-com.box         :refer [h-box v-box box line gap]]
            [re-com.dropdown    :refer [single-dropdown]]
            [re-com.alert       :refer [alert-box alert-list alert-box-args-desc alert-list-args-desc]]
            [re-demo.utils      :refer [panel-title component-title args-table]]
            [reagent.core       :as    reagent]))


(defn add-alert
  [alerts id alert-type {:keys [heading body]}]
  (let [alert {:id id :alert-type alert-type :heading heading :body body :padding "8px" :closeable? true}]
    (reset! alerts (insert-nth @alerts 0 alert))))


(defn alert-list-demo
  []
  (let [alerts       (reagent/atom [])]
    (add-alert alerts 0 "danger"  {:heading "Woa! something bad happened" :body "Next time you should take more care pressing that button! Did you read the fine print?  No, I didn't think so."})
    (add-alert alerts 1 "info"    {:heading "No Wait!" :body "The rain in Spain often falls on the mountatins too."})
    (add-alert alerts 2 "info"    {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain."})
    (add-alert alerts 3 "warning" {:heading "\"Oh bother\", said Pooh. And then ..." :body "\"Some people care too much. I think it's called love.\""})

    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "[alert-list ... ]"]
                  [h-box
                   :gap      "50px"
                   :children [#_[component-title "[alert-list ... ]"]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Notes"]
                                          [label :label "Renders a dynamic list of alert-boxes vertically."]
                                          [args-table   alert-list-args-desc]]]
                              [v-box
                               :width    "500px"
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [:p "To insert alerts at the top of the list, click "
                                           [button
                                            :label "Add alert"
                                            :style {:width "100px"}
                                            :on-click #(add-alert alerts (gensym) "info" {:heading "New alert" :body "This alert was added by the \"Add alert\" button ."})]]
                                          [:p "Also, try clicking the \"x\" on alerts."]
                                          [:p ":max-height is set to 300px. A scroll bar will appear as necessary."]
                                          [:p "For demonstration purposes, a 'dotted' :border-style is set."]
                                          [alert-list
                                           :alerts       alerts
                                           :on-close     #(reset! alerts (remove-id-item % @alerts))
                                           :max-height   "300px"
                                           :border-style "1px dashed lightgrey"]
                                          ]]]]]])))


(defn panel   ;; Introduce a level of naming indirection so that figwheel updates work
  []
  [alert-list-demo])
