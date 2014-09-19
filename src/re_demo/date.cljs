(ns re-demo.date
  (:require [re-demo.util    :refer  [title]]
            [cljs-time.core  :refer  [now]]
            [re-com.core     :refer  [label]]
            [re-com.date     :refer  [inline-date-picker]]
            [re-com.box      :refer  [h-box v-box box gap line border]]
            [reagent.core    :as     reagent]))

(defn inline-date
  []
  (let [model      (reagent/atom (now))
        attributes1 (reagent/atom {
                                   :minimum      nil        ;; optional inclusive
                                   :maximum      nil        ;; optional inclusive
                                   :show-weeks   true       ;; value
                                   :disabled     false      ;; navigation will be allowed, selection not
                                   :enable-days #{:SUNDAY}  ;; optional set or nil for all
                                   })
        attributes2 (reagent/atom {
                                    :minimum     nil        ;; optional inclusive
                                    :maximum     nil        ;; optional inclusive
                                    :show-weeks  false      ;; value
                                    :disabled    false      ;; navigation will be allowed, selection not
                                    :enable-days #{:SUNDAY} ;; optional set or nil for all
                                    })]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Inline Date"]
                [h-box
                :gap "20px"
                :align :start
                :children [[inline-date-picker
                            :model      model      ;; atom / value
                            :attributes attributes1 ;; atom / value
                            :on-change  #(println %)]
                           [inline-date-picker
                            :model model      ;; atom / value
                            :attributes attributes2 ;; atom / value
                            :on-change #(println %)]]]]]))

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