(ns re-demo.alerts
  (:require [re-com.util        :refer [insert-nth remove-id-item]]
            [re-com.core        :refer [button label title]]
            [re-com.tabs        :refer [vertical-bar-tabs]]
            [re-com.box         :refer [h-box v-box box gap]]
            [re-com.dropdown    :refer [single-dropdown filter-choices-by-keyword]]
            [re-com.alert       :refer [alert-box alert-list alert-box-args-desc alert-list-args-desc]]
            [re-demo.utils      :refer [panel-title component-title args-table]]
            [reagent.core       :as    reagent]))



(defn alert-box-demo
  []
  (let [show-alert (reagent/atom true)
        show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)]
    (fn []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[alert-box ... ]"]
                              [label :label "A component which renders a single alert-box."]
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
                                            [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]]]]])))


(defn add-alert
  [alerts id alert-type {:keys [heading body]}]
  (let [alert {:id id :alert-type alert-type :heading heading :body body :padding "8px" :closeable? true}]
    (reset! alerts (insert-nth @alerts 0 alert))))


(defn alert-list-demo
  []
  (let [alerts       (reagent/atom [])]
    (add-alert alerts 0 "danger"  {:heading "Woa! something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
    (add-alert alerts 1 "info"    {:heading "News Flash!" :body "The rain in Spain often falls on the mountatins too."})
    (add-alert alerts 2 "info"    {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain."})
    (add-alert alerts 3 "warning" {:heading "\"Oh bother\", said Pooh. And then ..." :body "\"Some people care too much. I think it's called love.\""})

    (fn []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[alert-list ... ]"]
                              [label :label "A component which renders a list of alert-boxes vertically."]
                              [args-table   alert-list-args-desc]]]
                  [v-box
                   :width    "500px"
                   :gap      "10px"
                   :children [[component-title "Demo"]
                              [:p "Press the 'Add alert' button to add some more."]
                              [:p ":max-height is set to 300px and a custom 'dotted' :border-style is set in this case."]
                              [alert-list
                               :alerts       alerts
                               :on-close     #(reset! alerts (remove-id-item % @alerts))
                               :max-height   "300px"
                               :border-style "1px dashed lightgrey"]
                              [button
                               :label "Add alert"
                               :style {:width "100px"}
                               :on-click #(add-alert alerts (gensym) "info" {:heading "New alert" :body "This alert was added byt the \"Add alert\" button ."})]]]]])))



(def demos [{:id 0 :label "alert-box"  :component alert-box-demo}
            {:id 1 :label "alert-list" :component alert-list-demo}])

(defn panel
  []
  (let [selected-demo-id (reagent/atom 0)]
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "Alert Components" ]
                  [h-box
                   :gap      "50px"
                   :children [[vertical-bar-tabs
                               :model selected-demo-id
                               :tabs demos
                               :on-change #(reset! selected-demo-id %)]
                              [(get-in demos [@selected-demo-id :component])]]]]])))
