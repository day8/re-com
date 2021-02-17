(ns re-demo.checkbox
  (:require-macros
    [re-com.debug    :refer [src-coordinates]])
  (:require
    [re-com.core     :refer [h-box v-box box gap line checkbox label p]]
    [re-com.checkbox :refer [checkbox-parts-desc checkbox-args-desc]]
    [re-demo.utils   :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util     :refer [px]]
    [reagent.core    :as    reagent]))


(defn right-arrow
  []
  [:svg
   {:height 20  :width 25}
   [:line {:x1 "0" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "20,6 20,14 25,10" :style {:stroke "#888" :fill "#888"}}]])


(defn left-arrow
  []
  [:svg
   {:height 20  :width 25}
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
       :children [[panel-title "[checkbox ... ]"
                                "src/re_com/checkbox.cljs"
                                "src/re_demo/checkbox.cljs"]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A boostrap-styled checkbox, with optional label (always displayed to the right)."]
                                          [p "Clicking on the label is the same as clicking on the checkbox."]
                                          [args-table checkbox-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [v-box
                                           :gap "15px"
                                           :children [[h-box
                                                       :src      (src-coordinates)
                                                       :gap      "10px"
                                                       :height   "20px"
                                                       :children [[checkbox
                                                                   :src       (src-coordinates)
                                                                   :label     "tick me  "
                                                                   :model     ticked?
                                                                   :on-change #(reset! ticked? %)]
                                                                  (when @ticked? [left-arrow])
                                                                  (when @ticked? [label :label " is ticked"])]]

                                                      [h-box
                                                       :src      (src-coordinates)
                                                       :gap      "1px"
                                                       :children [[checkbox
                                                                   :src       (src-coordinates)
                                                                   :model     all-for-one?
                                                                   :on-change #(reset! all-for-one? %)]
                                                                  [checkbox
                                                                   :src       (src-coordinates)
                                                                   :model     all-for-one?
                                                                   :on-change #(reset! all-for-one? %)]
                                                                  [checkbox
                                                                   :src       (src-coordinates)
                                                                   :model     all-for-one?
                                                                   :on-change #(reset! all-for-one? %)
                                                                   :label     "all for one, and one for all.  "]]]

                                                      [h-box
                                                       :src      (src-coordinates)
                                                       :gap      "15px"
                                                       :children [[checkbox
                                                                   :src       (src-coordinates)
                                                                   :label     "tick this one, to \"disable\""
                                                                   :model     disabled?
                                                                   :on-change #(reset! disabled? %)]
                                                                  [right-arrow]
                                                                  [checkbox
                                                                   :src         (src-coordinates)
                                                                   :label       (if @disabled? "now disabled" "enabled")
                                                                   :model       something1?
                                                                   :disabled?   disabled?
                                                                   :label-style (if @disabled?  {:color "#888"})
                                                                   :on-change   #(reset! something1? %)]]]

                                                      [h-box
                                                       :gap      "1px"
                                                       :children [[checkbox
                                                                   :src       (src-coordinates)
                                                                   :model     something2?
                                                                   :on-change #(reset! something2? %)]
                                                                  [gap :size "50px"]
                                                                  [left-arrow]
                                                                  [gap :size "5px"]
                                                                  [label
                                                                   :label "no label on this one"]]]]]]]]]
                  [parts-table "checkbox" checkbox-parts-desc]]])))


;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [checkboxes-demo])
