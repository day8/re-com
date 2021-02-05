(ns re-demo.selection-list
  (:require
    [cljs.pprint           :as pprint]
    [re-com.core           :refer [h-box v-box box gap selection-list label title checkbox p]]
    [re-com.selection-list :refer [selection-list-parts-desc selection-list-args-desc]]
    [re-demo.utils         :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util           :refer [px]]
    [reagent.core          :as    reagent]))

(defn- list-with-options
  [width]
  (let [disabled?      (reagent/atom false)
        multi-select?  (reagent/atom true)
        required?      (reagent/atom true)
        as-exclusions? (reagent/atom false)
        items          (reagent/atom [{:id "1" :label "1st RULE: You do not talk about FIGHT CLUB." :short "1st RULE"}
                                      {:id "2" :label "2nd RULE: You DO NOT talk about FIGHT CLUB." :short "2nd RULE"}
                                      {:id "3" :label "3rd RULE: If someone says \"stop\" or goes limp, taps out the fight is over." :short "3rd RULE"}
                                      {:id "4" :label "4th RULE: Only two guys to a fight." :short "4th RULE"}
                                      {:id "5" :label "5th RULE: One fight at a time." :short "5th RULE"}
                                      {:id "6" :label "6th RULE: No shirts, no shoes." :short "6th RULE"}
                                      {:id "7" :label "7th RULE: Fights will go on as long as they have to." :short "7th RULE"}
                                      {:id "8" :label "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight." :short "8th RULE"}])
        selections     (reagent/atom #{"2"})]  ;; (second @items)
    (fn []
      [v-box
       :width (str width "px")
       :gap      "20px"
       :align    :start
       :children [[title2 "Demo"]
                  [v-box ;; TODO: v-box required to constrain height of internal border.
                   :children [[selection-list
                               :width          "391px"      ;; manual hack for width of variation panel A+B 1024px
                               :max-height     "95px"       ;; based on compact style @ 19px x 5 rows
                               :model          selections
                               :choices        items
                               :label-fn       :label
                               :as-exclusions? as-exclusions?
                               :multi-select?  multi-select?
                               :disabled?      disabled?
                               :required?      required?
                               :on-change      #(reset! selections %)]
                              [gap :size "10px"]
                              [h-box
                               :height "60px"
                               :gap    "5px"
                               :width  "100%"
                               :children [[label :label [:code ":model"]]
                                          [label :label "is"]
                                          [:code
                                           {:class "display-flex"
                                            :style {:flex "1"}}
                                           (with-out-str (pprint/pprint @selections))]]]]]
                  [title :level :level3 :label "Parameters"]
                  [checkbox
                   :label       [box :align :start :child [:code ":disabled?"]]
                   :model       disabled?
                   :on-change   #(reset! disabled? %)]
                  [checkbox
                   :label       [box :align :start :child [:code ":multi-select?"]]
                   :model       multi-select?
                   :on-change   #(reset! multi-select? %)]
                  [checkbox
                   :label       [box :align :start :child [:code ":required?"]]
                   :model       required?
                   :on-change   #(reset! required? %)]
                  [checkbox
                   :label       [box :align :start :child [:code ":as-exclusions?"]]
                   :model       as-exclusions?
                   :on-change   #(reset! as-exclusions? %)]]])))

(defn panel2
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[selection-list ... ]"
                            "src/re_com/selection_list.cljs"
                            "src/re_demo/selection_list.cljs"]
              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Allows the user to select items from a list (single or multi)."]
                                      [p "Uses radio buttons when single selecting, and checkboxes when multi-selecting."]
                                      [p "Via strike-through, it supports the notion of selections representing exclusions, rather than inclusions."]
                                      [args-table selection-list-args-desc]]]
                          [list-with-options 600]]]
              [parts-table "selection-list" selection-list-parts-desc]]])


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
