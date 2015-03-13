(ns re-demo.selection-list
  (:require [re-com.core           :refer [h-box v-box box selection-list label checkbox]]
            [re-com.selection-list :refer [selection-list-args-desc]]
            [re-demo.utils         :refer [panel-title component-title args-table github-hyperlink status-text]]
            [re-com.util           :refer [golden-ratio-a golden-ratio-b]]
            [reagent.core          :as    reagent]))


(defn- options-with
  [width content multi-select? disabled? required? as-exclusions?]
  (fn []
    (let [check-style {:margin-top "1px"}]
      [v-box
       :width (str width "px")
       :gap      "20px"
       :align    :start
       :children [[component-title "Demo"]
                  [label :style {:font-style "italic"} :label "boolean parameters:"]
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
  (let [disabled?     (reagent/atom false)
        multi-select? (reagent/atom true)
        required?     (reagent/atom true)
        as-exlcusion? (reagent/atom false)
        items         (reagent/atom [{:id "1" :label "1st RULE: You do not talk about FIGHT CLUB."                                  :short "1st RULE"}
                                     {:id "2" :label "2nd RULE: You DO NOT talk about FIGHT CLUB."                                  :short "2nd RULE"}
                                     {:id "3" :label "3rd RULE: If someone says \"stop\" or goes limp, taps out the fight is over." :short "3rd RULE"}
                                     {:id "4" :label "4th RULE: Only two guys to a fight."                                          :short "4th RULE"}
                                     {:id "5" :label "5th RULE: One fight at a time."                                               :short "5th RULE"}
                                     {:id "6" :label "6th RULE: No shirts, no shoes."                                               :short "6th RULE"}
                                     {:id "7" :label "7th RULE: Fights will go on as long as they have to."                         :short "7th RULE"}
                                     {:id "8" :label "8th RULE: If this is your first night at FIGHT CLUB, you HAVE to fight."      :short "8th RULE"}])
        selections (reagent/atom (set [(second @items)]))]
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
                  :on-change      #(reset! selections %)]]]
     multi-select?
     disabled?
     required?
     as-exlcusion?]))


(defn- notes
  []
  [v-box
   :gap      "10px"
   :width    "450px"
   :children [[component-title "Notes"]
              [v-box
               :children [[:p "Allows the user to select items from a list (single or multi)."]
                          [:p "Uses radio buttons when single selecting, and checkboxes when multi-selecting."]
                          [:p "Via strike-through, it supports the notion of selections representing exclusions, rather than inclusions."]
                          [args-table selection-list-args-desc]]]]])


(defn panel2
  []
  (let [panel-width 1024
        ;h-gap       70
        ;a-width     (- (golden-ratio-a panel-width) h-gap)
        b-width     (golden-ratio-b panel-width)]
    [v-box
     :size     "auto"
     :gap      "10px"
     :children [[panel-title [:span "[selection-list ... ]"
                              [github-hyperlink "Component Source" "src/re_com/selection_list.cljs"]
                              [github-hyperlink "Page Source"      "src/re_demo/selection_list.cljs"]
                              [status-text "Alpha"]]]
                [h-box
                 ;:gap      (str h-gap "px")
                 :gap      "50px"
                 :children [[notes]                         ;; Removed a-width - need fixed 450px
                            [list-with-options b-width]]]]]))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
