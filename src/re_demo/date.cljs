(ns re-demo.date
  (:require [re-demo.util         :refer  [title]]
            [cljs-time.core       :refer  [now]]
            [re-com.core          :refer  [label checkbox]]
            [re-com.date          :refer  [inline-picker dropdown-picker previous-sunday]]
            [re-com.box           :refer  [h-box v-box box gap line border]]
            [reagent.core         :as     reagent]))

(defn inline-date
  []
  (let [model1      (reagent/atom (now))
        model2      (reagent/atom (now))
        model3      (reagent/atom (previous-sunday (now)))
        model4      (reagent/atom (previous-sunday (now)))
        disabled?   (reagent/atom false)
        show-today? (reagent/atom true)
        show-weeks? (reagent/atom false)]
    (fn []
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Inline Date"]
                [h-box
                 :gap "20px"
                 :align :start
                 :children [[label :label "options: "]
                            [checkbox
                             :label "Disabled"
                             :model disabled?
                             :on-change #(reset! disabled? %)]
                            [checkbox
                             :label "Show today"
                             :model show-today?
                             :on-change #(reset! show-today? %)]
                            [checkbox
                             :label "Show weeks"
                             :model show-weeks?
                             :on-change #(reset! show-weeks? %)]
                            ]]
                [h-box
                :gap "20px"
                :align :start
                :children [[inline-picker
                            :model        model1
                            :disabled     disabled?
                            :show-today   @show-today?
                            :show-weeks   @show-weeks?
                            :on-change    #(reset! model1 %)]
                           [inline-picker
                            :model        model2
                            :show-today   @show-today?
                            :show-weeks   @show-weeks?
                            :enabled-days #{:Mo :Tu :We :Th :Fr}
                            :disabled     disabled?
                            :on-change    #(reset! model2 %)]
                           [inline-picker
                            :model        model3
                            :show-today   @show-today?
                            :show-weeks   @show-weeks?
                            :enabled-days #{:Su}
                            :disabled     disabled?
                            :on-change    #(reset! model3 %)]
                           [inline-picker
                            :model        model4
                            :minimum      "20140831"
                            :maximum      "20141019"
                            :show-today   @show-today?
                            :show-weeks   @show-weeks?
                            :enabled-days #{:Su}
                            :disabled     disabled?
                            :on-change    #(reset! model4 %)]
                           ]]]])))

(defn popup-date
  []
  (let [model (reagent/atom (now))]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Popup Date"]
                ;; API same as inline-date-picker above + as follows:
                ;; :direction      :top-left  ;; :top-left , :bottom-right etc
                ;; :auto-collapse  true
                [dropdown-picker
                 :model     model
                 :disabled  false
                 :on-change #(reset! model %)]]]))

(defn panel
  []
  [v-box
   :children [[inline-date]
              [gap :height "30px"]
              [popup-date]]])