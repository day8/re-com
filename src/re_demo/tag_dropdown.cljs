(ns re-demo.tag-dropdown
  (:require [cljs.pprint          :as pprint]
            [clojure.string       :as string]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box box checkbox gap v-box tag-dropdown hyperlink-href p label line]]
            [re-com.slider        :refer [slider]]
            [re-com.tag-dropdown  :refer [tag-dropdown-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util          :refer [px]]))

(defn tag-dropdown-component-hierarchy
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
     :gap     "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[tag-dropdown\n"
                      "   ...\n"
                      "   :parts {:left {:class \"blah\"\n"
                      "                  :style { ... }\n"
                      "                  :attr  { ... }}}]"]
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
                   [:td border-style-nw (indent-text 0 "[popover-anchor-wrapper]")]
                   [:td border-style-nw "rc-tag-dropdown-popover-anchor-wrapper"]
                   [:td border-style-nw (code-text ":popover-anchor-wrapper")]
                   #_[:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[h-box]")]
                   [:td border-style-nw "rc-tag-dropdown"]
                   [:td border-style-nw (code-text ":main")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[h-box]")]
                   [:td border-style-nw "rc-tag-dropdown-tags"]
                   [:td border-style-nw (code-text ":tags")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[h-box]")]
                   [:td border-style-nw "rc-text-tag"]
                   [:td border-style-nw (code-text ":...")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[popover-content-wrapper]")]
                   [:td border-style-nw "rc-tag-dropdown-popover-content-wrapper"]
                   [:td border-style-nw (code-text ":popover-content-wrapper")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[selection-list]")]
                   [:td border-style-nw "rc-tag-dropdown-selection-list"]
                   [:td border-style-nw (code-text ":selection-list")]
                   [:td border-style ""]]]]]]))


(def choices [{:id :bug           :description "Something isn't working"                    :label "bug"           :background-color "#fc2a29"}
              {:id :documentation :description "Improvements or additions to documentation" :label "documentation" :background-color "#0052cc"}
              {:id :duplicate     :description "This issue or pull request already exists"  :label "duplicate"     :background-color "#cccccc"}
              {:id :enhancement   :description "New feature or request"                     :label "enhancement"   :background-color "#84b6eb"}
              {:id :help          :description "Extra attention is needed"                  :label "help"          :background-color "#169819"}
              {:id :invalid       :description "This doesn't seem right"                    :label "invalid"       :background-color "#e6e6e6"}
              {:id :wontfix       :description "This will not be worked on"                 :label "wontfix"       :background-color "#eb6421"}])

(defn demo
  []
  (let [model             (reagent/atom #{:documentation})
        disabled?         (reagent/atom false)
        unselect-buttons? (reagent/atom false)
        placeholder?      (reagent/atom false)
        abbrev?           (reagent/atom false)
        abbrev-threshold  (reagent/atom 27)
        min-width         (reagent/atom 200)
        max-width         (reagent/atom 300)]
    (fn []
      [v-box
       :gap      "10px"
       :width    "450px"
       :align    :start
       :children [[title2 "Demo"]
                  [title3 "Parameters"]
                  [v-box
                   :gap "3px"
                   :children [[checkbox
                               :label     [box
                                           :align :start
                                           :child [:code ":disabled?"]]
                               :model     disabled?
                               :on-change #(reset! disabled? %)]
                              [checkbox
                               :label     [box
                                           :align :start
                                           :child [:code ":unselect-buttons?"]]
                               :model     unselect-buttons?
                               :on-change #(reset! unselect-buttons? %)]
                              [checkbox
                               :label     [box
                                           :align :start
                                           :child [:code ":placeholder"]]
                               :model     placeholder?
                               :on-change #(reset! placeholder? %)]
                              [checkbox
                               :label     [box
                                           :align :start
                                           :child [:code ":abbrev?"]]
                               :model     abbrev?
                               :on-change #(reset! abbrev? %)]
                              (when @abbrev?
                                [h-box
                                 :children [[label :label [:code ":abbrev-threshold"]]
                                            [slider
                                             :model     abbrev-threshold
                                             :on-change #(reset! abbrev-threshold %)
                                             :min       10
                                             :max       50
                                             :step      1
                                             :width     "160px"]
                                            [label :label @abbrev-threshold]]])
                              [h-box
                               :children [[label :label [:code ":min-width"]]
                                          [slider
                                           :model     min-width
                                           :on-change #(reset! min-width %)
                                           :min       50
                                           :max       400
                                           :step      1
                                           :width     "300px"]
                                          [label :label (str @min-width "px")]]]
                              [h-box
                               :children [[label :label [:code ":max-width"]]
                                          [slider
                                           :model     max-width
                                           :on-change #(reset! max-width %)
                                           :min       50
                                           :max       400
                                           :step      1
                                           :width     "300px"]
                                          [label :label (str @max-width "px")]]]]]
                  #_[gap :size "5px"]
                  [h-box
                   :height "45px"      ;; means the Compontent (which is underneath) doesn't move up and down as the model changes
                   :gap "5px"
                   :children [[label :label [:code ":model"]]
                              [label :label " is currently"]
                              [:code (with-out-str (pprint/pprint @model))]]]
                  [gap :size "10px"]
                  [tag-dropdown
                    :min-width         (str @min-width "px")
                    :max-width         (str @max-width "px")
                    :disabled?         disabled?
                    :placeholder       (when @placeholder? "Select some tags!")
                    :unselect-buttons? unselect-buttons?
                    :choices           choices
                    :model             model
                    :abbrev-fn         (when @abbrev? #(string/upper-case (first (:label %))))
                    :abbrev-threshold  abbrev-threshold
                    :on-change         #(reset! model %)]]])))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[tag-dropdown ... ]"
                            "src/re_com/tag_dropdown.cljs"
                            "src/re_demo/tag_dropdown.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                            :gap      "10px"
                            :width    "450px"
                            :children [[title2 "Notes"]
                                       [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                       [p "A multi-select component. Useful when the list of choices is small and (optionally) colour coded, and where those selected need to all be visible to the user."]
                                       [p "If the user selects many of the choices, then displaying them horizontally can take more than " [:code ":width"] ". In this case, the programmer has two strategies:" 
                                        [:ol
                                         [:li  "allow the Component to grow horizontally to some limit by providing " [:code ":max-width"]]
                                         [:li  "allow the Component to switch from using \"name\" to using \"abrreviations\", see " [:code ":abbrev-fn"] " and  "[:code ":abbrev-threshold"]] ]]
                                       [args-table tag-dropdown-args-desc]]]
                          [demo]]]

              [tag-dropdown-component-hierarchy]]])


