(ns re-demo.title
  (:require [re-com.core   :refer [h-box v-box box gap line title label checkbox hyperlink-href p p-span]]
            [re-com.text   :refer [title-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.core  :as    reagent]))

(defn title-component-hierarchy
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
                [:pre "[title\n"
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
                   [:td border-style-nw (indent-text 0 "[title]")]
                   [:td border-style-nw "rc-title-wrapper"]
                   [:td border-style-nw (code-text ":wrapper")]
                   [:td border-style "Outer wrapper of the title."]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[:span]")]
                   [:td border-style-nw "rc-title"]
                   [:td border-style-nw "Use " (code-text ":class"), (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 ":label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[line]")]
                   [:td border-style-nw "rc-title-underline"]
                   [:td border-style-nw (code-text ":underline")]
                   [:td border-style ""]]]]]]))

(defn title-demo
  []
  (let [underline? (reagent/atom false)]
    (fn
      []
      (let [base-url  (str "https://github.com/day8/re-com/tree/" (if ^boolean js/goog.DEBUG "develop" "master") "/")
            para-text [p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quod si ita est, sequitur id ipsum, quod te velle video, omnes semper beatos esse sapientes. Tamen a proposito, inquam, aberramus. "]]
        [v-box
         :size "auto"
         :gap "10px"
         :children [[panel-title "[title ... ]"
                                  "src/re_com/text.cljs"
                                  "src/re_demo/title.cljs"]
                    [h-box
                     :gap "100px"
                     :children [[v-box
                                 :gap "10px"
                                 :width "450px"
                                 :children [[title2 "Notes"]
                                            [status-text "Stable"]
                                            [p "We use a four tier system of titles, the equivalent of h1 to h4."]
                                            [p-span
                                             "If you actually use [:h1] to [:h4] then "
                                             [hyperlink-href
                                              :label "Bootstrap styles"
                                              :href "http://getbootstrap.com/css/#type"
                                              :target "_blank"]
                                             " will apply."]
                                            [p-span
                                             "Re-com uses "
                                             [hyperlink-href
                                              :label  "Segoe UI"
                                              :href   "https://www.microsoft.com/typography/fonts/family.aspx?FID=331"
                                              :target "_blank"]
                                             " as its default font (available on Windows) with a fallback to the public domain "
                                             [hyperlink-href
                                              :label  "Roboto"
                                              :href   "http://www.google.com/fonts/specimen/Roboto"
                                              :target "_blank"]
                                             " font. See "
                                             [hyperlink-href
                                              :label  "re-com.css"
                                              :href   (str base-url "run/resources/public/assets/css/re-com.css")
                                              :target "_blank"]
                                             "."]
                                            [args-table title-args-desc]]]
                                [v-box
                                 :gap "10px"
                                 :children [[title2 "Demo"]
                                            [v-box
                                             :children [[checkbox
                                                         :label [box :align :start :child [:code ":underline?"]]
                                                         :model underline?
                                                         :on-change #(reset! underline? %)]
                                                        [gap :size "40px"]
                                                        para-text
                                                        [title :level :level1 :underline? @underline? :label ":level1 - Light 42px"]
                                                        para-text
                                                        [title :level :level2 :underline? @underline? :label ":level2 - Light 26px"]
                                                        para-text
                                                        [title :level :level3 :underline? @underline? :label ":level3 - Semibold 15px"]
                                                        para-text
                                                        [title :level :level4 :underline? @underline? :label ":level4 - Semibold 15px"]
                                                        para-text]]]]]]
                    [title-component-hierarchy]]]))))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [title-demo])
