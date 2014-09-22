(ns re-demo.date
  (:require [re-demo.util         :refer  [title]]
            [cljs-time.core       :refer  [now minus]]
            [cljs-time.predicates :refer  [sunday?]]
            [re-com.core          :refer  [label]]
            [re-com.date          :refer  [inline-date-picker ->previous-sunday]]
            [re-com.box           :refer  [h-box v-box box gap line border]]
            [reagent.core         :as     reagent]))

(defn inline-date
  []
  (let [model1      (reagent/atom (now))
        model2      (reagent/atom (now))
        model3      (reagent/atom (->previous-sunday (now)))
        attributes1 (reagent/atom {
                                   :minimum      nil        ;; optional inclusive
                                   :maximum      nil        ;; optional inclusive
                                   :show-weeks   true
                                   :show-today   true
                                   ;:enabled-days #{:Su}    ;; optional set or nil for all
                                   })
        attributes2 (reagent/atom (merge @attributes1 {:enabled-days #{:Mo :Tu :We :Th :Fr}
                                                       :show-today false}))
        attributes3 (reagent/atom (merge @attributes1 {:enabled-days #{:Su}
                                                       :show-today false}))
        disabled (reagent/atom false)]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Inline Date"]
                [h-box
                :gap "20px"
                :align :start
                :children [[inline-date-picker
                            :model      model1       ;; atom / value
                            :attributes attributes1  ;; atom / value
                            :disabled   disabled     ;; navigation will be allowed, selection not. atom /value
                            :on-change  #(reset! model1 %)]
                           [inline-date-picker
                            :model      model2       ;; atom / value
                            :attributes attributes2  ;; atom / value
                            :disabled   disabled     ;; navigation will be allowed, selection not. atom /value
                            :on-change #(reset! model2 %)]
                           [inline-date-picker
                            :model      model3       ;; atom / value
                            :attributes attributes3  ;; atom / value
                            :disabled   disabled     ;; navigation will be allowed, selection not. atom /value
                            :on-change #(reset! model3 %)]
                           ]]]]))

(defn popup-date
  []
  ;;TODO: pop-up date reuses inline-date-picker and adds the following API for wrapper conponent
  (let [model (reagent/atom (now))]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Popup Date"]
                ;; API same as inline-date-picker above + as follows:
                ;; :direction     :top-left  ;; :top-left , :bottom-right etc
                ;; :auto-colapse  true
                 ]]))

(defn panel
  []
  [v-box
   :children [[inline-date]
              [gap :height "30px"]
              [popup-date]]])