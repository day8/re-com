(ns re-demo.throbber
  (:require [re-com.core     :refer [h-box v-box box gap line button label throbber p]]
            [re-com.throbber :refer [throbber-args-desc]]
            [re-demo.utils   :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util     :refer [px]]
            [reagent.core    :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-throbber  false}))

(defn throbber-component-hierarchy
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
                [:pre "[throbber\n"
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
                   [:td border-style-nw (indent-text 0 "[throbber]")]
                   [:td border-style-nw "rc-throbber-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the throbber."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:ul]")]
                   [:td border-style-nw "rc-throbber"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style "The throbber."]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[:li]")]
                   [:td border-style-nw "rc-throbber-segment"]
                   [:td border-style-nw (code-text ":segment")]
                   [:td border-style "Repeated eight times. Each represents one of the eight circles in the throbber."]]]]]]))

(defn throbber-demo
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[throbber ... ]"
                            "src/re_com/throbber.cljs"
                            "src/re_demo/throbber.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "A CSS Throbber."]
                                      [args-table throbber-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [h-box
                                       :gap "50px"
                                       :children [[v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":smaller"]]
                                                              [throbber
                                                               :size :smaller
                                                               :color "green"]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":small"]]
                                                              [throbber
                                                               :size  :small
                                                               :color "red"]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":regular"]]
                                                              [throbber]]]
                                                  [v-box
                                                   :align :center
                                                   :children [[box :align :start :child [:code ":large"]]
                                                              [throbber
                                                               :size  :large
                                                               :color "blue"]]]]]]]]]
              [throbber-component-hierarchy]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [throbber-demo])
