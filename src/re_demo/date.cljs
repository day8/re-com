(ns re-demo.date
  (:require
    [reagent.core         :as     r]
    [cljs-time.core       :refer  [now]]
    [re-com.core          :refer  [label checkbox]]
    [re-com.datepicker    :refer  [datepicker datepicker-dropdown iso8601->date]]
    [re-com.box           :refer  [h-box v-box box gap border]]
    [re-com.dropdown      :refer  [single-dropdown]]))

(defn- toggle-inclusion! [set-atom member]
  "convenience function to include/exclude member from"
  (reset! set-atom
          (if (contains? @set-atom member)
            (disj @set-atom member)
            (conj @set-atom member))))

(def days-map {:Su "S" :Mo "M" :Tu "T" :We "W" :Th "T" :Fr "F" :Sa "S"})

(defn- parameters-with
  [content enabled-days disabled? show-today? show-weeks?]
  (let [day-check (fn [day] [v-box
                       :align :center
                       :children [[:label {:class "day-enabled"} (day days-map)]
                                  [checkbox
                                   :model (@enabled-days day)
                                   :on-change #(toggle-inclusion! enabled-days day)
                                   :style {:margin-top "-2px"}]]])]
    (fn []
      [v-box
       :gap "20px"
       :align :start
       :children [[label :style {:font-style "italic"} :label "parameters:"]
                  [h-box
                   :gap "20px"
                   :align :start
                   :children [[checkbox
                               :label ":disabled"
                               :model disabled?
                               :on-change #(reset! disabled? %)]
                              [checkbox
                               :label ":show-today"
                               :model show-today?
                               :on-change #(reset! show-today? %)]
                              [checkbox
                               :label ":show-weeks"
                               :model show-weeks?
                               :on-change #(reset! show-weeks? %)]]]
                  [h-box
                   :gap "2px"
                   :align :center
                   :children [[day-check :Su]
                              [day-check :Mo]
                              [day-check :Tu]
                              [day-check :We]
                              [day-check :Th]
                              [day-check :Fr]
                              [day-check :Sa]
                              [gap :width "5px"]
                              [label :label ":enabled-days"]
                              [gap :width "15px"]
                              [:label
                               {:class "day-enabled" :style {:color "orange"}}
                               "(warning: excluding selected day causes assertion error)"]]]
                  content]])))



  (defn- show-variant
    [variation]
    (let [model1       (r/atom (now))
          model2       (r/atom (iso8601->date "20140914"))
          disabled?    (r/atom false)
          show-today?  (r/atom true)
          show-weeks?  (r/atom false)
          enabled-days (r/atom (-> days-map keys set))
          label-style  {:font-style "italic" :font-size "smaller" :color "#777"}]
      (case variation
        "1" [(fn
               []
               [parameters-with
                [h-box
                 :gap "20px"
                 :align :start
                 :children [[(fn []
                               [v-box
                                :gap "5px"
                                :children [[label :style label-style :label ":minimum or :maximum not specified"]
                                           [datepicker
                                            :model model1
                                            :disabled disabled?
                                            :show-today @show-today?
                                            :show-weeks @show-weeks?
                                            :enabled-days @enabled-days
                                            :on-change #(reset! model1 %)]
                                           [label :style label-style :label (str "selected: " @model1)]]])]
                            ;; restricted to both minimum & maximum date
                            [(fn []
                               [v-box
                                :gap "5px"
                                :children [[label :style label-style :label ":minimum \"20140831\" :maximum \"20141019\""]
                                           [datepicker
                                            :model model2
                                            :minimum (iso8601->date "20140831")
                                            :maximum (iso8601->date "20141019")
                                            :show-today @show-today?
                                            :show-weeks @show-weeks?
                                            :enabled-days @enabled-days
                                            :disabled disabled?
                                            :on-change #(reset! model2 %)]
                                           [label :style label-style :label (str "selected: " @model2)]]])]]]
                enabled-days
                disabled?
                show-today?
                show-weeks?])]
        "2" [(fn
               []
               [parameters-with
                [h-box
                 :size "auto"
                 :align :start
                 :children [[gap :size "120px"]
                            [(fn []
                               [datepicker-dropdown
                                :model model1
                                :show-today @show-today?
                                :show-weeks @show-weeks?
                                :enabled-days @enabled-days
                                :format "dd MMM, yyyy"
                                :disabled disabled?
                                :on-change #(reset! model1 %)])]]]
                enabled-days
                disabled?
                show-today?
                show-weeks?])])))


(defn- notes
  [selected-variation]
  [v-box
   :width "500px"
   :children [[:div.h4 "Parameters:"]
              [:div {:style {:font-size "small"}}
               [:label {:style {:font-variant "small-caps"}} "required"]
                [:ul
                 [:li.spacer [:code ":model"]
                  " - goog.date.UtcDateTime can be reagent/atom. Represents displayed month and actual selected day. Must be one of :enabled-days"]
                 [:li.spacer [:code ":on-change"]
                  " - callback will be passed single arg of the selected goog.date.UtcDateTime."]
                 ]
               [:label {:style {:font-variant "small-caps"}} "optional"]
                [:ul
                 [:li.spacer [:code ":disabled"]
                  " - boolean can be reagent/atom. (default false) If true, navigation is allowed but selection is disabled."]
                 [:li.spacer [:code ":enabled-days"]
                  " - set of any #{:Su :Mo :Tu :We :Th :Fr :Sa} If nil or empty, all days are enabled."]
                 [:li.spacer [:code ":show-weeks"]
                  " - boolean. (default false) If true, first column shows week numbers."]
                 [:li.spacer [:code ":show-today"]
                  " - boolean. (default false) If true, today's date is highlighted different to selection. When both today's date and selected day are the same, selected highlight takes precedence."]
                 [:li.spacer [:code ":minimum"]
                  " - goog.date.UtcDateTime inclusive beyond which navigation and selection is blocked."]
                 [:li.spacer [:code ":maximum"]
                  " - goog.date.UtcDateTime inclusive beyond which navigation and selection is blocked."]
                 [:li.spacer [:code ":hide-border"]
                  " - boolean. Default false."]
                 (when (= "2" @selected-variation)
                   [:li.spacer [:code ":format"]
                    " - string format for dropdown label showing currently selected date see cljs_time.format Default \"yyyy MMM dd\""])]]]])


(def variations [{:id "1" :label "Inline"}
                 {:id "2" :label "Dropdown"}])


(defn panel
  []
  (let [selected-variation (r/atom "1")]
    (fn []
      [v-box
       :children [[:h3.page-header "Date Picker"]
                  [h-box
                   :gap "50px"
                   :children [[notes selected-variation]
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
                                           [show-variant @selected-variation]]]]]]])))