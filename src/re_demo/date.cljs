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
                              [gap :size "5px"]
                              [label :label ":enabled-days"]
                              [gap :size "15px"]
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
  []
  [v-box
   :gap      "10px"
   :width    "450px"
   :children [[component-title "[datepicker ... ]"]
              [component-title "[datepicker-dropdown ... ]"]
              [gap :size "15px"]
              [args-table datepicker-args-desc]]])


(def variations ^:private
  [{:id :inline   :label "Inline"}
   {:id :dropdown :label "Dropdown"}])


(defn panel2
  []
  (let [panel-width 980
        ;h-gap       70
        ;a-width     (- (golden-ratio-a panel-width) h-gap)
        b-width     (golden-ratio-b panel-width)
        selected-variation (r/atom :inline)
        ]
    (fn []
      [v-box
       :gap      "10px"
       ;:width (str panel-width "px")
       :children [[panel-title "Date Components"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "20px"
                               :size      "auto"
                               ;:margin    "20px 0px 0px 0px"
                               :children  [[component-title "Demo"]
                                           [h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Choose Demo:"]
                                                       [single-dropdown
                                                        :choices   variations
                                                        :model     selected-variation
                                                        :width     "200px"
                                                        :on-change #(reset! selected-variation %)]]]
                                           [show-variant @selected-variation b-width]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
