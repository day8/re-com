(ns re-demo.checkbox
  (:require [re-com.core   :refer [h-box v-box box gap line checkbox label]]
            [re-com.misc   :refer [checkbox-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core  :as    reagent]))


(defn right-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "0" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "20,6 20,14 25,10" :style {:stroke "#888" :fill "#888"}}]])


(defn left-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "5" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "5,6 5,14 0,10" :style {:stroke "#888" :fill "#888"}}]])


(defn checkboxes-demo
  []
  (let [; always-false (reagent/atom false)
        disabled?    (reagent/atom false)
        ticked?      (reagent/atom false)
        something1?  (reagent/atom false)
        something2?  (reagent/atom true)
        all-for-one? (reagent/atom true)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[checkbox ... ]"
                                [github-hyperlink "Component Source" "src/re_com/misc.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/checkbox.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "A boostrap-styled checkbox, with optional label (always displayed to the right)."]
                                           [:p "Clicking on the label is the same as clicking on the checkbox."]]
                                          [args-table checkbox-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [#_[checkbox
                   :label "always ticked (state stays true when you click)"
                   :model (= 1 1)]    ;; true means always ticked

                                                      #_[checkbox
                                                       :label "untickable (state stays false when you click)"
                                                       :model always-false]

                                                      [h-box
                                                       :gap      "10px"
                                                       :height   "20px"
                                                       :children [[checkbox
                                                                   :label     "tick me  "
                                                                   :model     ticked?
                                                                   :on-change #(reset! ticked? %)]
                                                                  (when @ticked? [left-arrow])
                                                                  (when @ticked? [label :label " is ticked"])]]

                                                      [h-box
                                                       :gap      "1px"
                                                       :children [[checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)]
                                                                  [checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)]
                                                                  [checkbox  :model all-for-one? :on-change #(reset! all-for-one? %)  :label  "all for one, and one for all.  "]]]

                                                      [h-box
                                                       :gap      "15px"
                                                       :children [[checkbox
                                                                   :label     "tick this one, to \"disable\""
                                                                   :model     disabled?
                                                                   :on-change #(reset! disabled? %)]
                                                                  [right-arrow]
                                                                  [checkbox
                                                                   :label       (if @disabled? "now disabled" "enabled")
                                                                   :model       something1?
                                                                   :disabled?   disabled?
                                                                   :label-style (if @disabled?  {:color "#888"})
                                                                   :on-change   #(reset! something1? %)]]]

                                                      [h-box
                                                       :gap      "1px"
                                                       :children [[checkbox
                                                                   :model     something2?
                                                                   :on-change #(reset! something2? %)]
                                                                  [gap :size "50px"]
                                                                  [left-arrow]
                                                                  [gap :size "5px"]
                                                                  [label
                                                                   :label "no label on this one"]]]]]]]]]]])))


;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [checkboxes-demo])
