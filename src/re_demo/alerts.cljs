(ns re-demo.alerts
  (:require [re-com.util     :as     util]
            [re-com.core     :refer [button label input-text checkbox]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-option filter-options-by-keyword]]
            [re-com.alert    :refer [alert-box alert-list]]
            [reagent.core    :as     reagent]))


(def demos [{:id 1 :label "Single alert with close button"}
            {:id 2 :label "More sample alerts"}
            {:id 3 :label "Alert list"}])


(defn demo1
  []
  (let [show-alert (reagent/atom true)]
    (fn []
      [:div
       [:p "The alert below is of type 'info' and includes a heading, body text and a close button to remove it."]
       (if @show-alert
         [alert-box
          :id 1
          :alert-type "info"
          :heading    "Sample Alert Heading"
          :body "This is the body of an info-styled alert. Click the x to close it."
          :closeable  true
          :on-close   #(reset! show-alert false)]
         [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])])))


(defn demo2
  []
  (let [show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)]
    (fn []
      [:div
       (when @show-alert1
         [:div
          [alert-box
           :alert-type "info"
           :heading "Alert with :heading but no :body"
           :closeable true
           :on-close #(reset! show-alert1 false)]])
       (when @show-alert2
         [:div
          [alert-box
           :alert-type "warning"
           :body "Alert with :body but no :heading (:padding set to 4px)"
           :padding "4px"
           :closeable true
           :on-close #(reset! show-alert2 false)]])
       [alert-box
        :alert-type "danger"
        :heading    ":alert-type is \"danger\""
        :body [:span "This is the :body of an danger-styled alert with :closeable omitted (defaults to false). " [:a {:href "http://google.com" :target "_blank"} "Link to Google"]]]])))


(defn add-alert
  [alerts alerts-count alert-type {:keys [heading body]}]
  (let [id (swap! alerts-count inc)]
    (swap! alerts assoc id {:alert-type alert-type :heading heading :body body :padding "8px" :closeable true})))


(defn demo3
  []
  (let [alerts       (reagent/atom (sorted-map-by >))
        alerts-count (reagent/atom 0)]
    (add-alert alerts alerts-count "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})
    (add-alert alerts alerts-count "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})
    (fn []
      [v-box
       :gap "10px"
       :children [[:p "An alert list displays any number of alert components vertically.
                       Press the 'Add alert' button to add some more. It is wrapped in a scroller (height 300px) and a border. Padding is set to 8px."]
                  [alert-list
                   :alerts       alerts
                   :on-close     #(swap! alerts dissoc %)
                   :border-style "1px dashed lightgrey"]
                  [button
                   :label "Add alert"
                   :style {:width "100px"}
                   :on-click #(add-alert alerts alerts-count "info" {:heading (str "New alert #" @alerts-count) :body "This alert was added when the button below was clicked."})]]])))


(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "[alert-box ..."]
              [:ul
               [:li "All parameter are optional."]
               [:li.spacer [:code ":id"] " - A unique identifier, usually an integer or string. This is optional for single alerts. It's main use is in alert-list component."]
               [:li.spacer [:code ":alert-type"] " - a Bootstrap CSS string determining the style. Either \"info\", \"warning\" or \"danger\"."]
               [:li.spacer "Note: Although heading and body are optional, you really need to specify at least one of them."]
               [:li.spacer [:code ":heading"] " - the heading section (hiccup markup or a string)."]
               [:li.spacer [:code ":body"] " - the body of the alert (hiccup markup or a string)."]
               [:li.spacer [:code ":padding"] " - the amount of padding within the alert (default is 15px)."]
               [:li.spacer [:code ":closeable"] " - A boolean which determines if the close button is rendered (on-close must also be specified)."]
               [:li.spacer [:code ":on-close"] " - A callback function which knows how to close the alert."]]
              [:div.h4 "[alert-list ..."]
              [:ul
               [:li.spacer "Renders an alert-list component which is a container for alert-boxes"]
               [:li.spacer [:code ":alerts"] " - A vector of alert maps..."]
               [:li.spacer [:code ":on-close"] " - A function called with a single :id parameter when the user closes an alert."]]
               [:li.spacer [:code ":border-style"] " - ."]
              ]])


(defn panel
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[:h3.page-header "Alerts"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :options   demos
                                                        :model     selected-demo-id
                                                        :width     "300px"
                                                        :on-select #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2]
                                             3 [demo3])]]]]]])))
