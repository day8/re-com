(ns re-demo.date
  (:require
    [reagent.core         :as     r]
    [cljs-time.core       :refer  [now days minus]]
    [cljs-time.format     :refer  [formatter unparse]]
    [re-com.core          :refer  [label checkbox title]]
    [re-com.datepicker    :refer  [datepicker datepicker-dropdown iso8601->date datepicker-args-desc]]
    [re-com.box           :refer  [h-box v-box gap]]
    [re-com.dropdown      :refer  [single-dropdown]]
    [re-com.util          :refer  [golden-ratio-a golden-ratio-b]]
    [re-demo.utils        :refer  [panel-title component-title args-table]]))


(defn- toggle-inclusion!
  "convenience function to include/exclude member from"
  [set-atom member]
  (reset! set-atom
          (if (contains? @set-atom member)
            (disj @set-atom member)
            (conj @set-atom member))))

(def ^:private days-map
  {:Su "S" :Mo "M" :Tu "T" :We "W" :Th "T" :Fr "F" :Sa "S"})

(defn- parameters-with
  [width content enabled-days disabled? show-today? show-weeks?]
  (let [day-check (fn [day] [v-box
                       :align    :center
                       :children [[:label {:class "day-enabled"} (day days-map)]
                                  [checkbox
                                   :model     (@enabled-days day)
                                   :on-change #(toggle-inclusion! enabled-days day)
                                   :style     {:margin-top "-2px"}]]])]
    (fn []
      [v-box
       :width    (str width "px")
       :gap      "20px"
       :align    :start
       :children [[label :style {:font-style "italic"} :label "parameters:"]
                  [h-box
                   :gap      "20px"
                   :align    :start
                   :children [[checkbox
                               :label     ":disabled?"
                               :model     disabled?
                               :on-change #(reset! disabled? %)]
                              [checkbox
                               :label     ":show-today?"
                               :model     show-today?
                               :on-change #(reset! show-today? %)]
                              [checkbox
                               :label     ":show-weeks?"
                               :model     show-weeks?
                               :on-change #(reset! show-weeks? %)]]]
                  [h-box
                   :gap      "2px"
                   :align    :center
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


(defn- date->string
  [date]
  (unparse (formatter "dd MMM, yyyy") date))

(defn- show-variant
  [variation width]
  (let [model1       (r/atom (minus (now) (days 3)))
        model2       (r/atom (iso8601->date "20140914"))
        disabled?    (r/atom false)
        show-today?  (r/atom true)
        show-weeks?  (r/atom false)
        enabled-days (r/atom (-> days-map keys set))
        label-style  {:font-style "italic" :font-size "smaller" :color "#777"}]
    (case variation
      :inline [(fn
                 []
                 [parameters-with
                  width
                  [h-box
                   :gap      "20px"
                   :align    :start
                   :children [[(fn []
                                 [v-box
                                  :gap      "5px"
                                  :children [[label :style label-style :label ":minimum or :maximum not specified"]
                                             [datepicker
                                              :model        model1
                                              :disabled?    disabled?
                                              :show-today?  @show-today?
                                              :show-weeks?  @show-weeks?
                                              :enabled-days @enabled-days
                                              :on-change    #(reset! model1 %)]
                                             [label :style label-style :label (str "selected: " (date->string @model1))]]])]
                              ;; restricted to both minimum & maximum date
                              [(fn []
                                 [v-box
                                  :gap      "5px"
                                  :children [[label :style label-style :label ":minimum \"20140831\" :maximum \"20141019\""]
                                             [datepicker
                                              :model        model2
                                              :minimum      (iso8601->date "20140831")
                                              :maximum      (iso8601->date "20141019")
                                              :show-today?  @show-today?
                                              :show-weeks?  @show-weeks?
                                              :enabled-days @enabled-days
                                              :disabled?     disabled?
                                              :on-change    #(reset! model2 %)]
                                             [label :style label-style :label (str "selected: " (date->string @model2))]]])]]]
                  enabled-days
                  disabled?
                  show-today?
                  show-weeks?])]
      :dropdown [(fn
                   []
                   [parameters-with
                    width
                    [h-box
                     :size     "auto"
                     :align    :start
                     :children [[gap :size "120px"]
                                [(fn []
                                   [datepicker-dropdown
                                    :model        model1
                                    :show-today?  @show-today?
                                    :show-weeks?  @show-weeks?
                                    :enabled-days @enabled-days
                                    :format       "dd MMM, yyyy"
                                    :disabled?    disabled?
                                    :on-change    #(reset! model1 %)])]]]
                    enabled-days
                    disabled?
                    show-today?
                    show-weeks?])])))


(defn- notes
  [width selected-variation]
  [v-box
   :width (str width "px")
   :children [[component-title "[datepicker ... ]"]
              [component-title "[datepicker-dropdown ... ]"]
              [gap :size "15px"]
              [args-table datepicker-args-desc]
              #_[v-box
               :style    {:font-size "small"}
               :children [#_[label :style {:font-variant "small-caps"} :label "general"]
                          #_[v-box
                           :style {:padding-left "10px"}
                           :children [[:p "All parameters are passed as named arguments using keyword value pairs in the component vector e.g."]
                                      (if (= :inline @selected-variation)
                                        [:pre {:style {:font-size "smaller"}} [:code "[datepicker :model (iso8601->date \"20140914\") :show-today? false]"]]
                                        [:pre {:style {:font-size "smaller"}} [:code "[datepicker-dropdown :model (now) :show-today? false]"]]
                                        )
                                      [:p ":model & :disabled? can optionally be a reagent atom and will be derefed."]]]

                           #_[label :style {:font-variant "small-caps"} :label "required parameters"]
                           #_[v-box
                            :style {:padding-left "10px"}
                            :children [[:p [:code ":model"]
                                        " - a instance of goog.date.UtcDateTime or an atom containing one. Represents displayed month and actual selected day. Must be one of :enabled-days"]
                                       [:p [:code ":on-change"]
                                        " - a function taking with one goog.date.UtcDateTime paramter."]]]
                           #_[label :style {:font-variant "small-caps"} :label "optional"]
                           #_[v-box
                            :style {:padding-left "10px"}
                            :children [[:p [:code ":disabled?"]
                                        " - boolean or a reagent/atom containing a boolean. Default is false.  If true, navigation is allowed but selection is disabled."]
                                       [:p [:code ":enabled-days"]
                                        " - a proper subset of #{:Su :Mo :Tu :We :Th :Fr :Sa}.  Dates falling on these days-of-week will be user-selectable, others not so. If nil or empty, all days are enabled."]
                                       [:p [:code ":show-weeks?"]
                                        " - if true, first column shows week numbers. Default is false."]
                                       [:p [:code ":show-today?"]
                                        " - if true, today's date is highlighted. Default is false"]
                                       [:p [:code ":minimum"]
                                        " - an instance of goog.date.UtcDateTime. Selection is blocked before this date."]
                                       [:p [:code ":maximum"]
                                        " - an instance of goog.date.UtcDateTime. Selection is blocked beyond this date."]
                                       [:p [:code ":hide-border?"]
                                        " - Defaults to false."]
                                       (when (= "2" @selected-variation)
                                         [:p [:code ":format"]
                                          " - string format for dropdown label showing currently selected date see cljs_time.format Default \"yyyy MMM dd\""])]]]]]])


(def variations ^:private
  [{:id :inline   :label "Inline"}
   {:id :dropdown :label "Dropdown"}])


(defn panel
  []
  (let [panel-width 980
        h-gap       70
        a-width     (- (golden-ratio-a panel-width) h-gap)
        b-width     (golden-ratio-b panel-width)
        selected-variation (r/atom :inline)]
    (fn []
      [v-box
       :width (str panel-width "px")
       :children [[panel-title "Date Components"]
                  [h-box
                   :gap      "50px"
                   :children [[notes a-width selected-variation]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :margin    "20px 0px 0px 0px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a variation"]
                                                       [single-dropdown
                                                        :choices   variations
                                                        :model     selected-variation
                                                        :width     "200px"
                                                        :on-change #(reset! selected-variation %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           [show-variant @selected-variation b-width]]]]]]])))