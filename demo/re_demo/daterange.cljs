(ns re-demo.daterange
  ;(:require-macro)
  (:require
   [goog.date.Date]
   [reagent.core      :as     reagent]
   [cljs-time.core    :as     cljs-time]
   [cljs-time.format  :refer [formatter unparse]]
   [re-com.core       :refer [at h-box v-box box gap single-dropdown
                              checkbox title p checkbox daterange daterange-dropdown]]
   [re-demo.utils     :refer [panel-title title2  parts-table args-table  status-text]]
   [re-com.daterange  :refer [daterange-parts-desc daterange-dropdown-args-desc]]))

(def week-start-choices
  [{:id 1 :label "Monday"}
   {:id 2 :label "Tuesday"}
   {:id 3 :label "Wednesday"}
   {:id 4 :label "Thursday"}
   {:id 5 :label "Friday"}
   {:id 6 :label "Saturday"}
   {:id 7 :label "Sunday"}])

(defn create-checkbox [atom day]
  [v-box
   :align :center
   :children [[box :style {:font-size "smaller"} :child day]
              [checkbox
               :model ((keyword day) @atom)
               :on-change #(swap! atom update-in [(keyword day)] not)]]])

(defn holder []
  (let [dropdown-model   (reagent/atom nil)     ;; TODO [GR-REMOVE] Align definitions (IntelliJ Ctrl+Alt+L, not sure what it is in VS Code)
        model-atom       (reagent/atom nil)
        today-model      (reagent/atom false)
        disabled-model   (reagent/atom false)
        weeks-model      (reagent/atom false)
        interval-model   (reagent/atom false)
        week-start-model (reagent/atom 2)
        selected-days    (reagent/atom {:M true :Tu true :W true :Th true :Fr true :Sa true :Su true}) ;model for all checkboxes ;; TODO [GR-REMOVE] Haven't changed it but would prefer consistency of :Mo and :We
        valid?           (fn [day] (nth (mapv val @selected-days) (dec (cljs-time/day-of-week day))))] ;convert to vector, check if day should be disabled
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "[daterange ...] & [daterange-dropdown ...]"
                   "src/re_com/daterange.cljs" ;; TODO [GR-REMOVE] corrected links
                   "demo/re_demo/daterange.cljs"]
                  [h-box
                   :gap "100px"
                   :children [[v-box
                               :gap "10px"
                               :width "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Alpha" {:color "red"}]
                                          [p "A date range picker component."]
                                          [args-table daterange-dropdown-args-desc]]]
                              [v-box
                               :gap "15px"
                               :children [[title2 "Demo"]
                                          [daterange
                                           :show-today? @today-model
                                           :disabled? @disabled-model
                                           :show-weeks? @weeks-model
                                           :check-interval? @interval-model
                                           :model model-atom
                                           :selectable-fn valid?
                                           :start-of-week @week-start-model
                                           :on-change #(reset! model-atom %)]

                                          ;; TODO [GR-REMOVE] because this line is just text, it doesn't need Flexbox power. You'll see we often make use is simple [:span]s for text only
                                          [h-box
                                           :align :center
                                           :children [[:code ":model"]
                                                      [box :child (str " is "
                                                                       (if @model-atom (str
                                                                                        (unparse (formatter "dd MMM, yyyy") (:start @model-atom)) " ... "
                                                                                        (unparse (formatter "dd MMM, yyyy") (:end @model-atom))) "nil"))]]]

                                          ;; TODO [GR-REMOVE] Here's the :span version (remove above when cleaning up)
                                          [:span
                                           [:code ":model"]
                                           (str " is "
                                                (if @model-atom ;; TODO [GR-REMOVE] Easier to read when formatted on multiple lines
                                                  (str
                                                   (unparse (formatter "dd MMM, yyyy") (:start @model-atom)) " ... "
                                                   (unparse (formatter "dd MMM, yyyy") (:end @model-atom)))
                                                  "nil"))]

                                          [v-box
                                           :src   (at)
                                           :gap   "10px"
                                           :style {:min-width        "550px"
                                                   :padding          "15px"
                                                   :border-top       "1px solid #DDD"
                                                   :background-color "#f7f7f7"}
                                           :children [[title
                                                       :src   (at)
                                                       :style {:margin-top "0"}
                                                       :level :level3 :label "Interactive Parameters"]
                                                      [checkbox
                                                       :src (at)
                                                       :model disabled-model
                                                       :on-change #(swap! disabled-model not)
                                                       :label [box :child [:code ":disabled?"]]]
                                                      [checkbox
                                                       :src (at)
                                                       :model today-model
                                                       :on-change #(swap! today-model not)
                                                       :label [box :child [:code ":show-today?"]]]
                                                      [checkbox
                                                       :src (at)
                                                       :model weeks-model
                                                       :on-change #(swap! weeks-model not)
                                                       :label [box :child [:code ":show-weeks?"]]]
                                                      [h-box
                                                       :gap "5px"
                                                       :align :end
                                                       :children [[box :child [:code ":start-of-week"]]
                                                                  [single-dropdown
                                                                   :width "110px"
                                                                   :choices week-start-choices
                                                                   :model week-start-model
                                                                   :on-change #(reset! week-start-model %)]]]
                                                      [h-box
                                                       :gap "5px"
                                                       :align :end
                                                       :children [[box :child [:code ":selectable-fn"]]
                                                                  [create-checkbox selected-days "M"]
                                                                  [create-checkbox selected-days "Tu"]
                                                                  [create-checkbox selected-days "W"]
                                                                  [create-checkbox selected-days "Th"]
                                                                  [create-checkbox selected-days "Fr"]
                                                                  [create-checkbox selected-days "Sa"]
                                                                  [create-checkbox selected-days "Su"]]]
                                                      [gap :size "5px"]
                                                      [checkbox
                                                       :model interval-model
                                                       :on-change #(swap! interval-model not)
                                                       :label [box :child [:code "check-interval?"]]]]]
                                          [v-box
                                           :align :start
                                           :gap "10px"
                                           :children [[title
                                                       :src   (at)
                                                       :level :level3 :label "Dropdown"]
                                                      [box
                                                       :src (at)
                                                       :child "Attached to the same model and interactive parameters."]
                                                      [daterange-dropdown ;; TODO [GR-REMOVE] I often like to align component args (but I haven't done it everywhere and there's no easy keyboard shortcut)
                                                       :show-today?     @today-model
                                                       :disabled?       @disabled-model
                                                       :show-weeks?     @weeks-model
                                                       :check-interval? @interval-model
                                                       :model           model-atom
                                                       :selectable-fn   valid?
                                                       :start-of-week   @week-start-model
                                                       :on-change       #(reset! model-atom %)
                                                       :placeholder     "Select a range of dates"]]]]]]]
                  [parts-table "daterange" daterange-parts-desc]]])))

(defn panel
  []
  [holder])
