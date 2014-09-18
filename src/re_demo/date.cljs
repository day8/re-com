(ns re-demo.date
  (:require [re-demo.util    :refer  [title]]
            [cljs-time.core  :refer  [now]]
            [re-com.core     :refer  [label]]
            [re-com.date     :refer  [inline-date-picker]]
            [re-com.box      :refer  [h-box v-box box gap line border]]
            [reagent.core    :as     reagent]))

(defn inline-date
  []
  (let [model (reagent/atom (now))]
    [v-box
     :gap "20px"
     :align :start
     :children [[title "Inline Date"]
                [inline-date-picker
                 :minimum    nil        ;; optional atom / value
                 :maximum    nil        ;; optional atom / value
                 :show-weeks false      ;; value
                 :disabled   false      ;; optional
                 :model      model      ;; atom / value
                 :on-change  #()
                 :allow      #{:SUNDAY} ;; optional set or nil for all
                 ]]]))

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