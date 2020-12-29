(ns re-demo.progress-bar
  (:require [re-com.core   :refer [h-box v-box box gap line label title progress-bar slider checkbox p]]
            [re-com.misc   :refer [progress-bar-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.core  :as    reagent]))

(defn progress-bar-component-hierarchy
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
                [:pre "[progress-bar\n"
                      "   ...\n"
                      "   :parts {:wrapper {:class \"blah\"\n"
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
                   [:td border-style-nw (indent-text 0 "[progress-bar]")]
                   [:td border-style-nw "rc-progress-bar-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the progress bar."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:div]")]
                   [:td border-style-nw "rc-progress-bar"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The container for the progress bar."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:div]")]
                   [:td border-style-nw "rc-progress-bar-portion"]
                   [:td border-style-nw "Use " (code-text ":bar-class") " argument instead."]
                   [:td border-style "The portion of the progress bar complete so far."]]]]]]))

(defn progress-bar-demo
  []
  (let [progress (reagent/atom 75)
        striped? (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[progress-bar ... ]"
                                "src/re_com/misc.cljs"
                                "src/re_demo/progress_bar.cljs"]
                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A Bootstrap styled progress bar."]
                                          [args-table progress-bar-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap      "20px"
                                           :children [[progress-bar
                                                       :model    progress
                                                       :width    "350px"
                                                       :striped? @striped?]
                                                      [title :level :level3 :label "Parameters"]
                                                      [h-box
                                                       :gap "10px"
                                                       :children [[box :align :start :child [:code ":model"]]
                                                                  [slider
                                                                   :model     progress
                                                                   :min       0
                                                                   :max       100
                                                                   :width     "200px"
                                                                   :on-change #(reset! progress %)]
                                                                  [label :label @progress]]]
                                                      [checkbox
                                                       :label     [box :align :start :child [:code ":striped?"]]
                                                       :model     striped?
                                                       :on-change #(reset! striped? %)]]]]]]]
                  [progress-bar-component-hierarchy]]])))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [progress-bar-demo])
