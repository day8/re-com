(ns re-demo.lists
  (:require [reagent.core          :as     r]
            [re-demo.util          :refer  [title]]
            [re-com.core           :refer  [label checkbox]]
            [re-com.box            :refer  [h-box v-box box gap line border]]
            [re-com.dropdown       :refer  [single-dropdown]]
            [re-com.selection-list :refer  [selection-list]]
            [re-com.util           :refer  [golden-ratio-a golden-ratio-b]]))


(defn- parameters-with
  [content multi-select? disabled? required? as-exclusions?]
  (fn []
    (let [check-style {:font-size "small" :margin-top "1px"}]
      [v-box
       :gap "20px"
       :align :start
       :children [[label :style {:font-style "italic"} :label "boolean parameters:"]
                  [h-box
                   :gap "15px"
                   :align :start
                   :children [[checkbox
                               :label ":disabled"
                               :label-style check-style
                               :model disabled?
                               :on-change #(reset! disabled? %)]
                              [checkbox
                               :label ":multi-select"
                               :label-style check-style
                               :model multi-select?
                               :on-change #(reset! multi-select? %)]
                              [checkbox
                               :label ":required"
                               :label-style check-style
                               :model required?
                               :on-change #(reset! required? %)]
                              [checkbox
                               :label ":as-exclusions"
                               :label-style check-style
                               :model as-exclusions?
                               :on-change #(reset! as-exclusions? %)]]]
                  content]])))



(defn- show-variant
  [variation]
  (let [disabled?     (r/atom false)
        multi-select? (r/atom true)
        required?     (r/atom true)
        as-exlcusion? (r/atom false)
        items         (r/atom [{:id "1" :label "1 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 1"}
                               {:id "2" :label "2 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 2"}
                               {:id "3" :label "3 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 3"}
                               {:id "4" :label "4 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 4"}
                               {:id "5" :label "5 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 5"}
                               {:id "6" :label "6 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 6"}
                               {:id "7" :label "7 Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit" :short "Short label 7"}])
        selections    (r/atom (set [(second @items)]))]
    (case variation
      "1" [(fn
             []
             [parameters-with
               [v-box
                :gap      "20px"
                :align    :start
                :children [[selection-list
                            :width         "391px"      ;; manual hack for width of variation panel A+B 1024px
                            :max-height    "95px"       ;; based on compact style @ 19px x 5 rows
                            :model         selections
                            :choices       items
                            :label-fn      :label
                            :as-exclusions as-exlcusion?
                            :multi-select  multi-select?
                            :disabled      disabled?
                            :required      required?
                            :on-change     #(do (println %) (reset! selections %))]]]
               multi-select?
               disabled?
               required?
               as-exlcusion?])])))


(defn- notes
  [_ width]
  [v-box
   :width (str width "px")
   :children [[:div.h4 "Parameters:"]
              [:div {:style {:font-size "small"}}
               [:label {:style {:font-variant "small-caps"}} "general"]
               [:div {:style {:padding-left "10px"}}
                 [:p "All parameters are passed as named arguments using keyword value pairs in the component vector e.g."]
                 [:pre {:style {:font-size "smaller"}} [:code "[inline-list :choices [\"pick 1\" \"pick 2\"] :model #{\"pick 2\"} :required false]"]]
                 [:p "Any parameter can optionally be a reagent atom and will be derefed."]]
               [:label {:style {:font-variant "small-caps"}} "required"]
               [:div {:style {:padding-left "10px"}}
                [:p [:code ":choices"]
                 (str " - list of selectable items. Elements can be strings or "
                      " more interesting data items like {:label \"some name\" :sort 5}"
                      " can be used. Also see :label-fn bellow.")]
                [:p [:code ":model"]
                  " - set of currently selected items. Note: items are considered distinct."]
                 [:p [:code ":on-change"]
                  " - callback will be passed set of selected items"]]
               [:label {:style {:font-variant "small-caps"}} "optional"]
               [:div {:style {:padding-left "10px"}}
                [:p [:code ":multi-select"]
                 " - boolean. When true, items use check boxes otherwise radio buttons. (default: true)"]
                [:p [:code ":as-exclusions"]
                 " - boolean. When true, selected items are shown with struck-out labels. (default: false)"]
                [:p [:code ":required"]
                 (str " - boolean. When true, at least one item must be selected. (default: false) "
                      "Note: being able to un-select a radio button is not a common use case,"
                      " so this should probably be set to true when in single select mode.")]
                [:p [:code ":width"]
                 " - CSS style value e.g. \"250px\" Fixed, when specified item labels will be clipped. Otherwise based on widest label."]
                [:p [:code ":height"]
                 " - CSS style value e.g. \"150px\" Fixed, beyond which items will scroll."]
                [:p [:code ":max-height"]
                 " - CSS style value e.g. \"150px\" Variable, beyond which items will scroll. If there are less items then this height, box will shrink."]
                [:p [:code ":disabled"]
                 " - boolean. When true, scrolling is allowed but selection is disabled. (default: false)"]
                [:p [:code ":hide-border"]
                 " - boolean. (default: false)"]
                [:p [:code ":label-fn"]
                 " - IFn to call on each element to get label string, default #(str %)"]]]]])


(def variations [{:id "1" :label "Toggle single/multiple selection"}])


(defn panel
  []
  (let [selected-variation (r/atom "1")]
    (fn []
      (let [panel-width 1024
            h-gap         70
            a-width     (- (golden-ratio-a panel-width) h-gap)
            b-width     (golden-ratio-b panel-width)]
        [v-box
       :width    (str panel-width "px")
       :children [[:h3.page-header "Multiple & Single Selection List"]
                  [h-box
                   :gap      (str h-gap "px")
                   :children [[notes selected-variation a-width]
                              [v-box
                               :gap "15px"
                               :width (str b-width "px")
                               :children  [[h-box
                                            :align    :center
                                            :justify  :between
                                            :children [[label :label "Choose variation"]
                                                       [single-dropdown
                                                        :width     "auto"
                                                        :options   variations
                                                        :model     selected-variation
                                                        :on-select #(reset! selected-variation %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           [show-variant @selected-variation]]]]]]]))))