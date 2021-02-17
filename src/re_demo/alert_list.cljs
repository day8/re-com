(ns re-demo.alert-list
  (:require-macros
    [re-com.debug  :refer [src-coordinates]])
  (:require
    [re-com.core   :refer [h-box v-box box line gap label title button alert-box alert-list p]]
    [re-com.alert  :refer [alert-list-parts-desc alert-box-args-desc alert-list-args-desc]]
    [re-com.util   :refer [insert-nth remove-id-item px]]
    [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [reagent.core  :as    reagent]))


(defn add-alert
  [alerts id alert-type {:keys [heading body]}]
  (let [alert {:id id :alert-type alert-type :heading heading :body body :padding "8px" :closeable? true}]
    (reset! alerts (insert-nth @alerts 0 alert))))

(defn alert-list-demo
  []
  (let [alerts       (reagent/atom [])]
    (add-alert alerts 0 :danger  {:heading "Woa! something bad happened" :body "Next time you should take more care pressing that button! Did you read the fine print?  No, I didn't think so."})
    (add-alert alerts 1 :info    {:heading "No Wait!" :body "The rain in Spain often falls on the mountatins too."})
    (add-alert alerts 2 :info    {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain."})
    (add-alert alerts 3 :warning {:heading "\"Oh bother\", said Pooh. And then ..." :body "\"Some people care too much. I think it's called love.\""})

    (fn []
      [v-box
       :src      (src-coordinates)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[alert-list ... ]"
                                "src/re_com/alert.cljs"
                                "src/re_demo/alert_list.cljs"]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "100px"
                   :children [[v-box
                               :src      (src-coordinates)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Renders a dynamic list of alert-boxes vertically, with a scroll bar if necessary."]
                                          [args-table alert-list-args-desc]]]
                              [v-box
                               :src      (src-coordinates)
                               :width    "600px"
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :src      (src-coordinates)
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label
                                                       :src   (src-coordinates)
                                                       :label "To insert alerts at the top of the list, click "]
                                                      [button
                                                       :src      (src-coordinates)
                                                       :label    "Add alert"
                                                       :style    {:width "100px"}
                                                       :on-click #(add-alert alerts (gensym) :info {:heading "New alert" :body "This alert was added by the \"Add alert\" button."})]]]
                                          [p "Also, try clicking the \"x\" on alerts."]
                                          [p [:code ":max-height"] " is set to 300px. A scroll bar will appear as necessary."]
                                          [p "For demonstration purposes, a 'dotted' " [:code ":border-style"] " is set."]
                                          [alert-list
                                           :src          (src-coordinates)
                                           :alerts       alerts
                                           :on-close     #(reset! alerts (remove-id-item % @alerts))
                                           :max-height   "300px"
                                           :border-style "1px dashed lightgrey"]]]]]
                  [parts-table "alert-list" alert-list-parts-desc]]])))

;; need a level of indirection to get figwheel updates
(defn panel
  []
  [alert-list-demo])
