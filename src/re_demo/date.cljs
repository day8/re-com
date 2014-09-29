(ns re-demo.date
  (:require [re-demo.util         :refer  [title]]
            [cljs-time.core       :refer  [now]]
            [cljs-time.predicates :refer  [sunday? monday?]]
            [re-com.core          :refer  [label checkbox]]
            [re-com.date          :refer  [inline-picker dropdown-picker previous iso8601->date]]
            [re-com.box           :refer  [h-box v-box box gap line border]]
            [re-com.dropdown      :refer  [single-dropdown]]
            [reagent.core         :as     r]))

(defn- toggle-inclusion! [set-atom member]
  "convenience function to include/exclude member from"
  (reset! set-atom
          (if (contains? @set-atom member)
            (disj @set-atom member)
            (conj @set-atom member))))

(defn inline-date
  []
  (let [model1       (r/atom (now))
        model4       (r/atom (iso8601->date "20140914"))
        disabled?    (r/atom false)
        show-today?  (r/atom true)
        show-weeks?  (r/atom false)
        days-map     {:Su "S" :Mo "M" :Tu "T" :We "W" :Th "T" :Fr "F" :Sa "S"}
        enabled-days (r/atom (-> days-map keys set))
        day-check    (fn [day] [v-box
                                :align :center
                                :children [[:label     {:class "day-enabled"} (day days-map)]
                                           [checkbox
                                            :model     (@enabled-days day)
                                            :on-change #(toggle-inclusion! enabled-days day)
                                            :style     {:margin-top "-2px"}]]])]
    (fn []
      [v-box
       :gap "20px"
       :align :start
       :children [[h-box
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
                   :gap "2px"
                   :align :center
                   :children [[label :label "enabled days:"]
                              [gap :width "5px"]
                              [day-check :Su]
                              [day-check :Mo]
                              [day-check :Tu]
                              [day-check :We]
                              [day-check :Th]
                              [day-check :Fr]
                              [day-check :Sa]
                              [gap :width "15px"]
                              [:label
                               {:class "day-enabled"}
                               "(warning: excluding selected day causes assertion error)"]]]
                  [h-box
                   :gap "20px"
                   :align :start
                   :children [[inline-picker
                               :model        model1
                               :disabled     disabled?
                               :show-today   @show-today?
                               :show-weeks   @show-weeks?
                               :enabled-days @enabled-days
                               :on-change    #(reset! model1 %)]
                              [inline-picker
                               :model        model4
                               :minimum      (iso8601->date "20140831")
                               :maximum      (iso8601->date "20141019")
                               :show-today   @show-today?
                               :show-weeks   @show-weeks?
                               :enabled-days #{:Su}
                               :disabled     disabled?
                               :on-change    #(reset! model4 %)]
                              ]]]])))

(defn popup-date
  []
  ;; API same as inline-date-picker above
  (let [example-date (iso8601->date "20140914") ;; A sunday. Must be one of :enabled-days
        model        (r/atom example-date)
        disabled?    (r/atom false)
        show-today?  (r/atom true)
        show-weeks?  (r/atom true)]
    (fn
      []
      [v-box
       :gap "5px"
       :align :start
       :children [[h-box
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
                               :on-change #(reset! show-weeks? %)]]]
                  [:label
                   {:style {:font-size "12px" :font-weight "normal"}}
                   "(show today & show weeks won't refresh while dropped down)"]
                  [h-box
                   :size "auto"
                   :align :start
                   :children [[gap :size "120px"]
                              [dropdown-picker
                               :model        model
                               :show-today   @show-today?
                               :show-weeks   @show-weeks?
                               :enabled-days #{:Su}
                               :format       "dd MMM, yyyy"
                               :disabled     disabled?
                               :on-change    #(reset! model %)]]]]])))

(defn notes
  []
  [v-box
   :width "500px"
   :children [[:div.h4 "Component parameters:"]
              [:ul
               [:li "required:"
                [:ul
                 [:li.spacer [:strong ":model"]     " - goog.date.UtcDateTime can be reagent/atom. The calendar will be focused on corresponding date and the date represents selection."]
                 [:li.spacer [:strong ":on-change"] " - callback will be passed single arg of the selected goog.date.UtcDateTime."]]]
               [:li "optional:"
                [:ul
                 [:li.spacer [:strong ":disabled"] " - a boolean indicating whether the control should be disabled. false if not specified."]
                 ]]]]])

(def variations [{:id "1" :label "Inline"}
                 {:id "2" :label "Dropdown"}
                 #_{:id "3" :label "Inline with min/max"}])

(defn panel
  []
  (let [selected-variation (r/atom "1")]
    (fn []
      [v-box
       :children [[:h3.page-header "Date Picker"]
                  [h-box
                   :gap "50px"
                   :children [[notes]
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
                                                        :width     "300px"
                                                        :on-select #(reset! selected-variation %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-variation
                                             "1" [inline-date]
                                             "2" [popup-date]
                                             ;;TODO: implement min/max variation
                                             )]]]]]])))