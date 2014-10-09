(ns re-demo.lists
  (:require [reagent.core         :as     r]
            [re-demo.util         :refer  [title]]
            [re-com.core          :refer  [label checkbox]]
            [re-com.box           :refer  [h-box v-box box gap line border]]
            [re-com.dropdown      :refer  [single-dropdown]]
            [re-com.list          :refer  [inline-list]]))


(defn- parameters-with
  [content multi-select? disabled? required?]
  (fn []
    [v-box
     :gap "20px"
     :align :start
     :children [[label :style {:font-style "italic"} :label "parameters:"]
                [h-box
                 :gap "20px"
                 :align :start
                 :children [[checkbox
                             :label ":disabled"
                             :model disabled?
                             :on-change #(reset! disabled? %)]
                            [checkbox
                             :label ":multi-select"
                             :model multi-select?
                             :on-change #(reset! multi-select? %)]
                            [checkbox
                             :label ":required"
                             :model required?
                             :on-change #(reset! required? %)]]]

                content]]))



(defn- show-variant
  [variation]
  (let [disabled?     (r/atom false)
        multi-select? (r/atom true)
        required?     (r/atom true)
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
                :children [[inline-list
                            :width        "400px"
                            :max-height   "7em"
                            :model        selections
                            :choices      items
                            :label-fn     :label
                            :multi-select multi-select?
                            :disabled     disabled?
                            :required     required?
                            :on-change    #(do (println %) (reset! selections %))]]]
               multi-select?
               disabled?
               required?])])))


(defn- notes
  [_]
  [v-box
   :width "500px"
   :children [[:div.h4 "Parameters:"]
              [:div {:style {:font-size "small"}}
               [:label {:style {:font-variant "small-caps"}} "required"]
               [:div {:style {:padding-left "10px"}}
                 [:p [:code ":model"]
                  " - set of currently selected items. Note items are considered distinct."]
                 [:p [:code ":choices"]
                 " - list of selectable items. Elements can be just strings and will be sent str unless :label-fn provided."]
                 [:p [:code ":multi-select"]
                  " - boolean. When true, items use check boxes otherwise radio buttons."]
                 [:p [:code ":on-change"]
                  " - callback will be passed set of selected items"]]
               [:label {:style {:font-variant "small-caps"}} "optional"]
               [:div {:style {:padding-left "10px"}}
                [:p [:code ":required"]
                 (str " - boolean. When true, at least one item must be selected. Default false. "
                      "Note: being able to un-select a radio button is not a common use case,"
                      " so this should probably be set to true when in single select mode.")]
                [:p [:code ":width"]
                 " - optional CSS style value e.g. \"250px\" Fixed, when specified item labels will be clipped. Otherwise width base on largest label."]
                [:p [:code ":height"]
                 " - optional CSS style value e.g. \"150px\" Fixed, beyond which items will scroll."]
                [:p [:code ":max-height"]
                 " - optional CSS style value e.g. \"150px\" Variable, beyond which items will scroll. If there are less items then this height, box will shrink."]
                [:p [:code ":disabled"]
                 " - boolean can be reagent/atom. (default false) If true, scrolling is allowed but selection is disabled."]
                [:p [:code ":hide-border"]
                 " - boolean. Default false."]
                [:p [:code ":label-fn"]
                 " - IFn to call on each element to get label string, default (str ...)"]]]]])


(def variations [{:id "1" :label "Toggle single/multiple selection"}])


(defn panel
  []
  (let [selected-variation (r/atom "1")]
    (fn []
      [v-box
       :children [[:h3.page-header "Single & Multiple Select List"]
                  [h-box
                   :gap "50px"
                   :children [[notes selected-variation]
                              [v-box
                               :gap "15px"
                               :size "auto"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a variation"]
                                                       [single-dropdown
                                                        :options   variations
                                                        :model     selected-variation
                                                        :width     "285px"
                                                        :on-select #(reset! selected-variation %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           [show-variant @selected-variation]]]]]]])))