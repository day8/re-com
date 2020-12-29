(ns re-demo.alert-list
  (:require [re-com.core   :refer [h-box v-box box line gap label title button alert-box alert-list p]]
            [re-com.alert  :refer [alert-box-args-desc alert-list-args-desc]]
            [re-com.util   :refer [insert-nth remove-id-item px]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn add-alert
  [alerts id alert-type {:keys [heading body]}]
  (let [alert {:id id :alert-type alert-type :heading heading :body body :padding "8px" :closeable? true}]
    (reset! alerts (insert-nth @alerts 0 alert))))

(defn alert-list-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[alert-list\n"
                      "   ...\n"
                      "   :parts {:scroller {:class \"blah\"\n"
                      "                      :style { ... }\n"
                      "                      :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[alert-list]")]
                   [:td border-style-nw "rc-alert-list-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the alert list."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[border]")]
                   [:td border-style-nw "rc-alert-list"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[scroller]")]
                   [:td border-style-nw "rc-alert-list-scroller"]
                   [:td border-style-nw (code-text ":scroller")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-alert-list-v-box"]
                   [:td border-style-nw (code-text ":v-box")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[alert-box]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw "Use " (code-text ":alert-class") " or " (code-text ":alert-style") " arguments instead."]
                   [:td border-style ""]]]]]]))

(defn alert-list-demo
  []
  (let [alerts       (reagent/atom [])]
    (add-alert alerts 0 :danger  {:heading "Woa! something bad happened" :body "Next time you should take more care pressing that button! Did you read the fine print?  No, I didn't think so."})
    (add-alert alerts 1 :info    {:heading "No Wait!" :body "The rain in Spain often falls on the mountatins too."})
    (add-alert alerts 2 :info    {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain."})
    (add-alert alerts 3 :warning {:heading "\"Oh bother\", said Pooh. And then ..." :body "\"Some people care too much. I think it's called love.\""})

    (fn []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[alert-list ... ]"
                                "src/re_com/alert.cljs"
                                "src/re_demo/alert_list.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "Renders a dynamic list of alert-boxes vertically, with a scroll bar if necessary."]
                                          [args-table alert-list-args-desc]]]
                              [v-box
                               :width    "600px"
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :gap      "10px"
                                           :align    :center
                                           :children [[label :label "To insert alerts at the top of the list, click "]
                                                      [button
                                                       :label "Add alert"
                                                       :style {:width "100px"}
                                                       :on-click #(add-alert alerts (gensym) :info {:heading "New alert" :body "This alert was added by the \"Add alert\" button."})]]]
                                          [p "Also, try clicking the \"x\" on alerts."]
                                          [p [:code ":max-height"] " is set to 300px. A scroll bar will appear as necessary."]
                                          [p "For demonstration purposes, a 'dotted' " [:code ":border-style"] " is set."]
                                          [alert-list
                                           :alerts       alerts
                                           :on-close     #(reset! alerts (remove-id-item % @alerts))
                                           :max-height   "300px"
                                           :border-style "1px dashed lightgrey"]]]]]
                  [alert-list-component-hierarchy]]])))

;; need a level of indirection to get figwheel updates
(defn panel
  []
  [alert-list-demo])
