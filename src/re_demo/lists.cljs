(ns re-demo.lists
  (:require
    [reagent.core          :as    r]
    [re-com.core           :refer [label checkbox title]]
    [re-com.box            :refer [h-box v-box]]
    [re-com.selection-list :refer [selection-list]]
    [re-com.util           :refer [golden-ratio-a golden-ratio-b]]))


(defn- options-with
  [width content multi-select? disabled? required? as-exclusions?]
  (fn []
    (let [check-style {:font-size "small" :margin-top "1px"}]
      [v-box
       :width (str width "px")
       :gap      "20px"
       :align    :start
       :margin   "20px 0px 0px 0px"
       :children [[label :style {:font-style "italic"} :label "boolean parameters:"]
                  [h-box
                   :gap      "15px"
                   :align    :start
                   :children [[checkbox
                               :label       ":disabled?"
                               :label-style check-style
                               :model       disabled?
                               :on-change   #(reset! disabled? %)]
                              [checkbox
                               :label       ":multi-select?"
                               :label-style check-style
                               :model       multi-select?
                               :on-change   #(reset! multi-select? %)]
                              [checkbox
                               :label       ":required?"
                               :label-style check-style
                               :model       required?
                               :on-change   #(reset! required? %)]
                              [checkbox
                               :label       ":as-exclusions?"
                               :label-style check-style
                               :model       as-exclusions?
                               :on-change   #(reset! as-exclusions? %)]]]
                  content]])))



(defn- list-with-options
  [width]
  (let [disabled?     (r/atom false)
        multi-select? (r/atom true)
        required?     (r/atom true)
        as-exlcusion? (r/atom false)
        items         (r/atom [{:id "1" :label "1st RULE: You do not talk about FIGHT CLUB." :short "1st RULE"}
                               {:id "2" :label "2nd RULE: You DO NOT talk about FIGHT CLUB." :short "2nd RULE"}
                               {:id "3" :label "3rd RULE: If someone says \"stop\" or goes limp, taps out the fight is over." :short "3rd RULE"}
                               {:id "4" :label "4th RULE: Only two guys to a fight." :short "4th RULE"}
                               {:id "5" :label "5th RULE: One fight at a time." :short "5th RULE"}
                               {:id "6" :label "6th RULE: No shirts, no shoes." :short "6th RULE"}
                               {:id "7" :label "7th RULE: Fights will go on as long as they have to." :short "7th RULE"}
                               {:id "8" :label "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight." :short "8th RULE"}])
        selections (r/atom (set [(second @items)]))]
    [options-with
     width
     [v-box ;; TODO: v-box required to constrain height of internal border.
      :children [[selection-list
                  :width          "391px"      ;; manual hack for width of variation panel A+B 1024px
                  :max-height     "95px"       ;; based on compact style @ 19px x 5 rows
                  :model          selections
                  :choices        items
                  :label-fn       :label
                  :as-exclusions? as-exlcusion?
                  :multi-select?  multi-select?
                  :disabled?      disabled?
                  :required?      required?
                  :on-change      #(do (println %) (reset! selections %))]]]
     multi-select?
     disabled?
     required?
     as-exlcusion?]))


(defn- notes
  [width]
  [v-box
   :width (str width "px")
   :children [[:h4 "Parameters"]
              [v-box
               :style {:font-size "small"}
               :children [[label :style {:font-variant "small-caps"} :label "general"]
                          [v-box
                           :style    {:padding-left "10px"}
                           :children [[:p "All parameters are passed as named arguments using keyword value pairs in the component vector e.g."]
                                      [:pre {:style {:font-size "smaller"}} [:code "[selection-list :choices [\"pick1\" \"pick2\"] :model #{\"pick2\"} :required? true]"]]
                                      [:p "Any parameter can optionally be a reagent atom and will be derefed."]]]
                          [label     :style {:font-variant "small-caps"} :label "required"]
                          [v-box
                           :style    {:padding-left "10px"}
                           :children [[:p [:code ":choices"]
                                       (str " - list of selectable items. Elements can be strings or "
                                            " more interesting data items like {:label \"some name\" :sort 5}"
                                            " can be used. Also see :label-fn bellow.")]
                                      [:p [:code ":model"]
                                       " - set of currently selected items. Note: items are considered distinct."]
                                      [:p [:code ":on-change"]
                                       " - callback will be passed set of selected items"]]]
                          [label     :style {:font-variant "small-caps"} :label "optional"]
                          [v-box
                           :style    {:padding-left "10px"}
                           :children [[:p [:code ":multi-select?"]
                                       " - boolean. When true, items use check boxes otherwise radio buttons. (default: true)"]
                                      [:p [:code ":as-exclusions?"]
                                       " - boolean. When true, selected items are shown with struck-out labels. (default: false)"]
                                      [:p [:code ":required?"]
                                       (str " - boolean. When true, at least one item must be selected. (default: false) "
                                            "Note: being able to un-select a radio button is not a common use case,"
                                            " so this should probably be set to true when in single select mode.")]
                                      [:p [:code ":width"]
                                       " - CSS style value e.g. \"250px\" Fixed, when specified item labels will be clipped. Otherwise based on widest label."]
                                      [:p [:code ":height"]
                                       " - CSS style value e.g. \"150px\" Fixed, beyond which items will scroll."]
                                      [:p [:code ":max-height"]
                                       " - CSS style value e.g. \"150px\" Variable, beyond which items will scroll. If there are less items then this height, box will shrink."]
                                      [:p [:code ":disabled?"]
                                       " - boolean. When true, scrolling is allowed but selection is disabled. (default: false)"]
                                      [:p [:code ":hide-border?"]
                                       " - boolean. (default: false)"]
                                      [:p [:code ":label-fn"]
                                       " - IFn to call on each element to get label string, default #(str %)"]
                                      [:p [:code ":item-renderer"]
                                       (str " - IFn to call on each element during setup, the returned component renders the element, respond to clicks etc"
                                            " Following example renders plain label")]
                                      [:pre {:style {:font-size "smaller"}}
                                       [:code (str "(defn as-label\n"
                                                   "  [item selections on-change disabled? label-fn required? as-exclusions?]\n"
                                                   "  [label :label (label-fn item) :style {:width \"200px\" :color \"#428bca\"}])")]]]
                           ]]]]])


(defn panel
  []
  (let [panel-width 1024
        h-gap       70
        a-width     (- (golden-ratio-a panel-width) h-gap)
        b-width     (golden-ratio-b panel-width)]
    [v-box
     :width    (str panel-width "px")
     :children [[title "Multiple & Single Selection List"]
                [h-box
                 :gap      (str h-gap "px")
                 :children [[notes a-width]
                            [list-with-options b-width]]]]]))