(ns re-demo.date
  (:require [re-demo.util         :refer  [title]]
            [cljs-time.core       :refer  [now]]
            [re-com.core          :refer  [label checkbox]]
            [re-com.date          :refer  [inline-date-picker ->previous-sunday]]
            [re-com.box           :refer  [h-box v-box box gap line border]]
            [reagent.core         :as     reagent]))

(defn inline-date
  []
  (let [model1      (reagent/atom (now))
        model2      (reagent/atom (now))
        model3      (reagent/atom (->previous-sunday (now)))
        model4      (reagent/atom (->previous-sunday (now)))
        disabled?   (reagent/atom false)
        show-today? (reagent/atom true)
        show-weeks? (reagent/atom false)
        attributes1 (reagent/atom {
                                   :minimum      nil        ;; optional inclusive ISO8601
                                   :maximum      nil        ;; optional inclusive ISO8601
                                   :show-weeks   @show-weeks?
                                   :show-today   @show-today?
                                   ;:enabled-days #{:Su}    ;; optional set or nil for all
                                   })
        attributes2 (reagent/atom (merge @attributes1 {:enabled-days #{:Mo :Tu :We :Th :Fr}}))
        attributes3 (reagent/atom (merge @attributes1 {:enabled-days #{:Su}}))
        attributes4 (reagent/atom (merge @attributes1 {:enabled-days #{:Su}
                                                       :minimum "20140831"
                                                       :maximum "20141019"}))
        ]
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
                             :on-change #(do
                                          (reset! show-today? %)
                                          (reset! attributes1 (assoc @attributes1 :show-today @show-today?))
                                          (reset! attributes2 (assoc @attributes2 :show-today @show-today?))
                                          (reset! attributes3 (assoc @attributes3 :show-today @show-today?))
                                          (reset! attributes4 (assoc @attributes4 :show-today @show-today?)))]
                            [checkbox
                             :label "Show weeks"
                             :model show-weeks?
                             :on-change #(do
                                          (reset! show-weeks? %)
                                          (reset! attributes1 (assoc @attributes1 :show-weeks @show-weeks?))
                                          (reset! attributes2 (assoc @attributes2 :show-weeks @show-weeks?))
                                          (reset! attributes3 (assoc @attributes3 :show-weeks @show-weeks?))
                                          (reset! attributes4 (assoc @attributes4 :show-weeks @show-weeks?)))]
                            ]]
                [h-box
                :gap "20px"
                :align :start
                :children [[inline-date-picker
                            :model      model1       ;; atom / value TODO: Maybe just pass in an ISO date like min/max
                            :attributes attributes1  ;; atom / value
                            :disabled   disabled?    ;; navigation will be allowed, selection not. atom /value
                            :on-change  #(reset! model1 %)]
                           [inline-date-picker
                            :model      model2       ;; atom / value TODO: Maybe just pass in an ISO date like min/max
                            :attributes attributes2  ;; atom / value
                            :disabled   disabled?    ;; navigation will be allowed, selection not. atom /value
                            :on-change #(reset! model2 %)]
                           [inline-date-picker
                            :model      model3       ;; atom / value TODO: Maybe just pass in an ISO date like min/max
                            :attributes attributes3  ;; atom / value
                            :disabled   disabled?    ;; navigation will be allowed, selection not. atom /value
                            :on-change #(reset! model3 %)]
                           [inline-date-picker
                            :model      model4       ;; atom / value TODO: Maybe just pass in an ISO date like min/max
                            :attributes attributes4  ;; atom / value
                            :disabled   disabled?    ;; navigation will be allowed, selection not. atom /value
                            :on-change #(reset! model4 %)]
                           ]]]])))

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