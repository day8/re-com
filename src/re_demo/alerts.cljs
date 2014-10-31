(ns re-demo.alerts
  (:require [re-com.util     :refer [insert-nth remove-nth find-map-index]]
            [re-com.core     :refer [button label title]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-choice filter-choices-by-keyword]]
            [re-com.alert    :refer [alert-box alert-list]]
            [reagent.core    :as    reagent]))


(def demos [{:id 1 :label "alert-box"}
            {:id 3 :label "alert-list"}])


(defn demo1
  []
  (let [show-alert (reagent/atom true)
        show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)]
    (fn []
      [:div
       [:p "This alert has an :alert-type of 'info' and includes both a :heading, a :body and a close button."]
       (if @show-alert
         [alert-box
          :id         1
          :alert-type "info"
          :heading    "Sample Alert Heading"
          :body       "This is the body of an info-styled alert. Click the x to close it."
          :closeable? true
          :on-close   #(reset! show-alert false)]
         [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])

       [:br]
       [:br]
       [:br]
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
           :body       "Alert with :body but no :heading (:padding set to 4px)"
           :padding    "4px"
           :closeable? true
           :on-close   #(reset! show-alert2 false)]])
       [alert-box
        :alert-type "danger"
        :heading    ":alert-type is \"danger\""
        :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). " [:a {:href "http://google.com" :target "_blank"} "Link to Google"]]]])))


(defn demo2
  []
  (let []
    (fn []
      [:div

       ])))


(defn add-alert
  [alerts alerts-count alert-type {:keys [heading body]}]
  (let [id    (swap! alerts-count inc)
        alert {:id id :alert-type alert-type :heading heading :body body :padding "8px" :closeable? true}]
    (reset! alerts (insert-nth @alerts 0 alert))))


(defn demo3
  []
  (let [alerts       (reagent/atom [])
        alerts-count (reagent/atom 0)]
    (add-alert alerts alerts-count "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})
    (add-alert alerts alerts-count "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})
    (fn []
      [v-box
       :gap "10px"
       :children [[:p "An alert-list displays any number of alert-box components vertically. Press the 'Add alert' button to add some more."]
                  [:p ":max-height is set to 300px and a custom 'dotted' :border-style is set in this case."]
                  [alert-list
                   :alerts       alerts
                   :on-close     #(reset! alerts (remove-nth @alerts (find-map-index @alerts %)))
                   :max-height   "300px"
                   :border-style "1px dashed lightgrey"]
                  [button
                   :label "Add alert"
                   :style {:width "100px"}
                   :on-click #(add-alert alerts alerts-count "info" {:heading (str "New alert #" @alerts-count) :body "This alert was added when the button below was clicked."})]]])))


(defn notes
  []
  [v-box
   :width    "500px"
   :style    {:font-size "small"}
   :children [[:div.h4 "[alert-box ..."]
              [:ul
               [:li "All parameter are optional."]
               [:li.spacer [:code ":id"] " - a unique identifier, usually an integer or string."]
               [:li.spacer [:code ":alert-type"] " - a string contining a bootstrap style: \"info\", \"warning\" or \"danger\"."]
               [:li.spacer "Note: while heading and body are both optional, you'll need to supply at least one of them."]
               [:li.spacer [:code ":heading"] " - the heading section (hiccup markup or a string)."]
               [:li.spacer [:code ":body"] " - the body of the alert (hiccup markup or a string)."]
               [:li.spacer [:code ":padding"] " - the amount of padding within the alert (default is 15px)."]
               [:li.spacer [:code ":closeable?"] " - a boolean indicating if a close button 'X' is rendered (on-close must also be supplied)."]
               [:li.spacer [:code ":on-close"] " - the call back when the user clicks the close 'X'. Invoked with the single :id parameter."]]
              [:div.h4 "[alert-list ..."]
              [:ul
               [:li.spacer "A component which renders a list of alert-boxes."]
               [:li.spacer [:code ":alerts"] " - a vector containing alert maps to be rendered. The order is specified by the calling app."]
               [:li.spacer [:code ":on-close"] " - a call back when the user clicks the close 'X' of an item. Invoked with the single :id parameter."]
               [:li.spacer [:code ":max-height"] " - the initial height of this component is 0px and grows to this maximum as alerts are added. Default is to expand forever."]
               [:li.spacer [:code ":padding"] " - padding inside the alert-list outer box. Default is 4px."]
               [:li.spacer [:code ":border-style"] " - border style around the alert-list outer box. Default is \"1px solid lightgrey\"."]]]])


(defn panel
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[title "Alerts"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :margin    "20px 0px 0px 0px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   demos
                                                        :model     selected-demo-id
                                                        :width     "150px"
                                                        :on-change #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"]       ;; will cause double the normal gap
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2]
                                             3 [demo3])]]]]]])))
