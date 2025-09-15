(ns re-demo.table-filter
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [cljs.pprint]
   [re-com.core     :as rc :refer [at button checkbox h-box input-text label p
                                   p-span table-filter v-box]]
   [re-com.slider   :refer [slider]]
   [re-com.table-filter :refer [table-filter-args-desc table-filter-parts-desc]]
   [re-demo.utils   :refer [args-table panel-title parts-table status-text
                            title2 title3]]
   [reagent.core    :as r]
   [re-com.box :as box]))

(def sample-table-spec
  [{:id :name :name "Name" :type :text}
   {:id :age :name "Age" :type :number}
   {:id :email :name "Email" :type :text}
   {:id :salary :name "Salary" :type :number}
   {:id :department :name "Department" :type :select
    :options [{:id "engineering" :label "Engineering"}
              {:id "marketing" :label "Marketing"}
              {:id "sales" :label "Sales"}]}
   {:id :active :name "Active" :type :boolean}
   {:id :hire-date :name "Hire Date" :type :date}
   {:id :skills :name "Skills" :type :select
    :options [{:id "clojure" :label "Clojure"}
              {:id "javascript" :label "JavaScript"}
              {:id "python" :label "Python"}
              {:id "java" :label "Java"}]}])

(defn panel
  []
  (let [filter-model (r/atom {:type :group,
                              :logic :and,
                              :children
                              [{:type :filter, :col :age, :op :>=, :val "40"}
                               {:type :filter, :col :active, :op :is, :val true}
                               {:type :group,
                                :logic :or,
                                :children
                                [{:type :filter, :col :department, :op :is, :val "engineering"}
                                 {:type :filter,
                                  :col :skills,
                                  :op :is-any-of,
                                  :val #{"clojure" "javascript" "python"}}]}]})
        filter-valid? (r/atom true)
        disabled-model (r/atom false)
        max-depth-model (r/atom 2)]
    (fn []
      [v-box :src (at)
       :size "auto"
       :gap "10px"
       :children
       [[panel-title "[table-filter ... ]"
         "src/re_com/table_filter.cljs"
         "demo/re_demo/table_filter.cljs"]
        [h-box
         :src (at)
         :gap "50px"
         :children
         [[v-box :src (at) :gap "10px" :width "500px"
           :children
           [[title2 "Notes"]
            [status-text "Alpha" {:color "red"}]
            [p "Allows users to create hierarchical filter queries against tabular data. Think \"nested where clauses\" in SQL."
               "Supports nested filter groups with AND/OR logic, configurable nesting depth, and real-time validation."]
            [p "You provide a " [:code "table-spec"] " describing the tabular data to be filtered, and it will generate appropriate UI controls for each column type with appropriate operators:"]
            [:ul {:style {:margin-left "20px" :margin-bottom "15px"}}
             [:li [:strong "Text columns:"] " a text input together with operators like contains, starts with, ends with, is empty"]
             [:li [:strong "Number columns:"] " a numeric input with operators like >, >=, ="]
             [:li [:strong "Date columns:"] " a date picker or date range picker with operators like before, after, between"]
             [:li [:strong "Boolean columns:"] " a simple true/false selection"]
             [:li [:strong "Select columns:"] " a single or multi-value selection with operators like is, is not, etc"]]
            [args-table table-filter-args-desc]]]
          [v-box
           :src (at)
           :width "auto"
           :gap "10px"
           :children
           [[title2 "Interactive Demo"]
            [v-box :src (at) :gap "15px"
             :children
             [; Simple example usage
              [table-filter
               :src (at)
               :max-depth @max-depth-model
               :disabled? @disabled-model
               :table-spec sample-table-spec
               :model filter-model
               :on-change (fn [model is-valid?]
                            (reset! filter-model model)
                            ;(when (rand-nth [nil 1]) (reset! filter-model model))
                            (reset! filter-valid? is-valid?))]

              [h-box :gap "20px" :align :center
               :children
               [[p-span " • Filter is " [:strong {:style {:color (if @filter-valid? "green" "red")}} (if @filter-valid? "Valid" "Invalid")]]]]
              [title3 "Interactive Parameters"]
              [v-box :gap "15px" :style {:background-color "#f7f7f7" :padding "15px" :border-radius "8px"}
               :children
               [[h-box :gap "15px" :align :center
                 :children
                 [[label :label "Max Depth:"]
                  [slider
                   :model max-depth-model
                   :on-change #(reset! max-depth-model %)
                   :min 0
                   :max 5
                   :step 1
                   :width "200px"]
                  [label :label (str @max-depth-model)]]]

                [checkbox
                 :label "Disabled?"
                 :model disabled-model
                 :on-change #(reset! disabled-model %)]
                [h-box :gap "15px" :align :center
                 :children
                 [[button
                   :label "Clear Filters"
                   :class "btn-outline"
                   :style {:font-size "13px" :color "#dc2626" :font-weight "500"
                           :padding "8px 16px" :border "1px solid #dc2626"
                           :border-radius "6px"}
                   :disabled? @disabled-model
                   :on-click #(reset! filter-model nil)]
                  [label :label "Reset the filter to empty state"]]]]]

              [title3 "Current Filter Model:"]
              [:pre {:style {:background-color "#f9f9f9" :padding "15px" :font-size "11px" :max-height "250px" :overflow "auto" :border-radius "4px" :border "1px solid #e9ecef"}}
               (if @filter-model
                 (with-out-str (cljs.pprint/pprint @filter-model))
                 "nil")]
              [title3 "table-spec:"]
              [:pre {:style {:background-color "#f8f9fa" :padding "15px" :font-size "11px" :border-radius "4px" :border "1px solid #e9ecef"}}
               (with-out-str (cljs.pprint/pprint sample-table-spec))]

              [title3 "Parts System Demo"]
              [p "The same table-filter with some light custom styling via the " [:code ":parts"] " parameter:"]
              [table-filter
               :src (at)
               :max-depth @max-depth-model
               :disabled? @disabled-model
               :table-spec sample-table-spec
               :model @filter-model
               :on-change (fn [model is-valid?]
                            (reset! filter-model model)
                            (reset! filter-valid? is-valid?))
               :style {:font-family "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"
                       :font-size "13px" :background-color "#f8fafc"}  ; Modern font and smaller size
               :parts {:filter {:style {:align-items "center" :background-color "transparent"}}  ; Only alignment, no background styling

                       :column-dropdown {:style {:font-size "12px"
                                                 :border "1px solid #bfdbfe"
                                                 :border-radius "4px"
                                                 :width "90px"}}
                       :operator-dropdown {:style {:font-size "12px"
                                                   :border "1px solid #bfdbfe"
                                                   :border-radius "4px"
                                                   :width "115px"}}
                       :text-input {:style {:font-size "12px"
                                            :border "1px solid #bfdbfe"
                                            :border-radius "4px"
                                            :background-color "#fafbff"}}
                       :date-input {:style {:font-size "12px"}}
                       :daterange-input {:style {:font-size "12px"
                                                 :border "1px solid #bfdbfe"
                                                 :border-radius "4px"
                                                 :background-color "#fafbff"}}
                       :dropdown-input {:style {:font-size "12px"
                                                :border "1px solid #bfdbfe"
                                                :border-radius "4px"
                                                :background-color "#fafbff"
                                                :width "115px"}}
                       :where-label {:style {:color "#3b82f6"
                                             :font-size "12px"}}
                       :operator-button {:style {:font-size "12px"
                                                 :background-color "#f1f5f9"
                                                 :border "1px solid #cbd5e1"
                                                 :color "#475569"
                                                 :height "20px"
                                                 :display "flex"}}
                       :operator-text {:style {:font-size "12px"
                                               :color "#64748b"}}}]
              [box/gap :size "50px"]]]]]]]
        [parts-table "table-filter" table-filter-parts-desc]]])))
