(ns re-demo.alert-box
  (:require [re-com.core   :refer [h-box v-box box line gap title label alert-box alert-list p]]
            [re-com.alert  :refer [alert-box-args-desc alert-list-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.debug :refer-macros [dbg prn println log dev? warn warn-unless]]
            [reagent.core  :as    reagent]))

(defn alert-box-component-hierarchy
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
                [:pre "[alert-box\n"
                      "   ...\n"
                      "   :parts {:heading {:class \"blah\"\n"
                      "                     :style { ... }\n"
                      "                     :attr  { ... }}}]"]
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
                   [:td border-style-nw (indent-text 0 "[alert-box]")]
                   [:td border-style-nw "rc-alert-box"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "Outer wrapper of the alert box."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[h-box]")]
                   [:td border-style-nw "rc-alert-heading"]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:h4]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw (code-text ":h4")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[box]")]
                   [:td border-style-nw "rc-close-button"]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[h-box]")]
                   [:td border-style-nw "rc-alert-body"]
                   [:td border-style-nw (code-text ":body")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:div]")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]]]]]))

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
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"]]]
                                          (when @show-alert4
                                            [alert-box
                                             :id         1
                                             :alert-type :none
                                             :heading    "This Is An Unstyled Alert"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of :none, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert4 false)])

                                          [title
                                           :level :level3
                                           :label [:span [:code ":alert-type"] " set to " [:code ":none"] " with custom " [:code ":style"] " and " [:code ":body"]]]

                                          (when @show-alert5
                                            [:div
                                             [alert-box
                                              :alert-type :none
                                              :style {:color             "#222"
                                                      :background-color  "#eff9e3"
                                                      :border-top        "none"
                                                      :border-right      "none"
                                                      :border-bottom     "none"
                                                      :border-left       "4px solid green"
                                                      :border-radius     "0px"}
                                              :heading "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close #(reset! show-alert5 false)]])
                                          (when @show-alert6
                                            [:div
                                             [alert-box
                                              :alert-type :none
                                              :style {:color             "#222"
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
                                           :alert-type :none
                                           :style {:color             "#333"
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
                                          [gap :size "60px"]]]]]
                  [alert-box-component-hierarchy]]])))



;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [alert-box-demo])
