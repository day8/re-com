(ns re-demo.table-filter
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [cljs.pprint]
   [re-com.core     :as rc :refer [at button checkbox h-box input-text label
                                   p-span table-filter v-box p]]
   [re-com.slider   :refer [slider]]
   [re-com.table-filter :refer [table-filter-args-desc table-filter-parts-desc]]
   [re-demo.utils   :refer [args-table panel-title parts-table status-text
                            title2 title3]]
   [reagent.core    :as r]))

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

(def sample-data
  [{:name "Alice Johnson" :age 28 :email "alice@company.com" :salary 85000 :department "engineering" :active true :hire-date "2022-03-15" :skills #{"clojure" "javascript"}}
   {:name "Bob Smith" :age 34 :email "bob@company.com" :salary 92000 :department "engineering" :active true :hire-date "2021-08-22" :skills #{"python" "java"}}
   {:name "Carol Davis" :age 29 :email "carol@company.com" :salary 78000 :department "marketing" :active false :hire-date "2023-01-10" :skills #{"javascript"}}
   {:name "David Wilson" :age 42 :email "david@company.com" :salary 105000 :department "sales" :active true :hire-date "2020-05-03" :skills #{"python" "clojure"}}
   {:name "Eva Martinez" :age 26 :email "eva@company.com" :salary 72000 :department "marketing" :active true :hire-date "2023-06-18" :skills #{"java" "javascript"}}])

(defn demo2 [max-depth-model top-label-model hide-border-model disabled-model filter-model filter-valid?]
  (fn []
    [v-box
     :children
     [[title3 "Another Parts Demo"]
      [table-filter
       :src (at)
       :max-depth @max-depth-model
       :hide-border? @hide-border-model
       :disabled? @disabled-model
       :table-spec sample-table-spec
       :model @filter-model
       :on-change (fn [model is-valid?]
                    (reset! filter-model model)
                    (reset! filter-valid? is-valid?))
       :style {:font-size "14px"
               :background-color "#f8f9f5"
               :color "#2d3d2d"}
       :parts {:wrapper {:style {:background-color "#f8f9f5"
                                 :border "2px solid #9eb893"
                                 :border-radius "50px"
                                 :padding "20px"
                                 :box-shadow "0 4px 12px rgba(125, 132, 113, 0.15)"}}
               :header {:style {:color "#5a8a72"
                                :font-size "16px"
                                :font-weight "600"}}

               :filter {:style {:background-color "transparent"}}
               :group {:style {:border-radius "50px"}}
               :where-label {:style {:color "#5a8a72"
                                     :font-weight "500"}}
               :column-dropdown {:style {:border "1px solid #dcdcdc"
                                         :border-radius "100px"}
                                 :parts {:chosen-single {:style {:border-radius "100px"
                                                                 :color "#2d3d2d"
                                                                 :height "50px"
                                                                 :line-height "50px"}}}}

               :operator-dropdown {:style {:border "1px solid #dcdcdc"
                                           :border-radius "100px"
                                           :height "50px"}
                                   :parts {:chosen-single {:style {:border-radius "100px"
                                                                   :color "#2d3d2d"
                                                                   :height "50px"
                                                                   :line-height "50px"}}}}
               :text-input {:style {:border "1px solid #dcdcdc"
                                    :border-radius "100px"
                                    :color "#2d3d2d"
                                    :height "50px"}}
               :date-input {:style {}
                            :parts {:anchor-label {:style {:border-radius "100px"
                                                           :color "#2d3d2d"
                                                           :height "50px"
                                                           :line-height "50px"}}}}

               :daterange-input {
                                 :parts {}}
               :dropdown-input {:style {:border "1px solid #dcdcdc"
                                        :border-radius "100px"
                                        :height "50px"}
                                :parts {:chosen-single {:style {:color "#2d3d2d"
                                                                :border-radius "100px"
                                                                :height "50px"
                                                                :line-height "50px"}}}}
               :tag-dropdown-input {:style {:border "1px solid #dcdcdc"
                                            :border-radius "100px"
                                            :color "#2d3d2d"
                                            :height "50px"}
                                    :parts {:popover-anchor-wrapper {:style {:border-radius "50px"}}}}
               :add-button {:style {:background-color "#e6f0e6"
                                    :color "#5a8a72"
                                    :border "1px solid #9eb893"
                                    :border-radius "100px"
                                    :height "50px"}}

               :operator-button {:style {:color "#5a8a72"
                                         :border "1px solid #dcdcdc"
                                         :border-radius "100px"
                                         :height "50px"}}
               :operator-text {:style {:color "#5a8a72"
                                       :font-weight "500"}}
               :warning-icon {:style {:color "#d4af37"}}}]]]))

(defn panel
  []
  (let [filter-model (r/atom nil)
        filter-valid? (r/atom false)
        disabled-model (r/atom false)
        hide-border-model (r/atom false)
        top-label-model (r/atom "Select rows")
        max-depth-model (r/atom 2)]
    (fn []
      [v-box :src (at)
       :size "auto"
       :gap "10px"
       :children
       [[panel-title "[table-filter ... ]"
         "src/re_com/table_filter.cljs"
         "src/re_demo/table_filter.cljs"]
        [h-box
         :src (at)
         :gap "50px"
         :children
         [[v-box :src (at) :gap "10px" :width "500px"
           :children
           [[title2 "Notes"]
            [status-text "Alpha" {:color "red"}]
            [p "Build complex, hierarchical filter conditions with an intuitive UI. Perfect for allowing end users to create sophisticated queries against tabular data without writing code."]
            [p "The component uses a " [:code "table-spec"] " to automatically generate appropriate UI controls for each column type:"]
            [:ul {:style {:margin-left "20px" :margin-bottom "15px"}}
             [:li [:strong "Text columns:"] " is, contains, starts with, ends with, is empty operators with text input"]
             [:li [:strong "Number columns:"] " comparison operators (>, >=, =, etc.) with numeric input"]
             [:li [:strong "Date columns:"] " before, after, between operators with date pickers"]
             [:li [:strong "Boolean columns:"] " simple true/false selection"]
             [:li [:strong "Select columns:"] " single or multi-value selection from predefined options"]]
            [p "Supports nested filter groups with AND/OR logic, configurable nesting depth, and real-time validation."]
            [args-table table-filter-args-desc]]]
          [v-box
           :src (at)
           :width "auto"
           :gap "10px"
           :children
           [[title2 "Interactive Demo"]
            [v-box :src (at) :gap "15px"
             :children
             [[table-filter
               :src (at)
               :max-depth @max-depth-model
               :top-label @top-label-model
               :hide-border? @hide-border-model
               :disabled? @disabled-model
               :table-spec sample-table-spec
               :model @filter-model
               :on-change (fn [model is-valid?]
                            (reset! filter-model model)
                            (reset! filter-valid? is-valid?))]
              [h-box :gap "20px" :align :center
               :children
               [[p-span " • Filter contains " [:strong (str (count (tree-seq #(= (:type %) :group) :children @filter-model)) " nodes")]]
                [p-span " • Filter is " [:strong {:style {:color (if @filter-valid? "green" "red")}} (if @filter-valid? "Valid" "Invalid")]]]]
              [title3 "Interactive Parameters"]
              [v-box :gap "15px" :style {:background-color "#f7f7f7" :padding "15px" :border-radius "8px"}
               :children
               [[h-box :gap "15px" :align :center
                 :children
                 [[label :label "Top Label:"]
                  [input-text
                   :model top-label-model
                   :on-change #(reset! top-label-model %)
                   :width "200px"
                   :placeholder "Enter header text"]]]
                [h-box :gap "15px" :align :center
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
                 :label "Hide Border?"
                 :model hide-border-model
                 :on-change #(reset! hide-border-model %)]
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
                           :border-radius "6px" }
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
              [p "The same table-filter with custom styling via the " [:code ":parts"] " parameter:"]
              [p "Note: Each part (like " [:code ":column-dropdown"] ") can have both direct styling and nested " [:code ":parts"] " to customize its internal components:"]
              [:pre {:style {:background-color "#f8f9fa" :padding "10px" :font-size "10px" :border-radius "4px" :border "1px solid #e9ecef" :margin-bottom "10px"}}
               ":parts {:column-dropdown {:style {:border \"1px solid blue\"}    ; Style the dropdown wrapper\n                          :parts {:chosen-single {:style {:color \"red\"}}}}  ; Style internal dropdown parts"]
              [table-filter
               :src (at)
               :max-depth @max-depth-model
               :top-label @top-label-model
               :hide-border? @hide-border-model
               :disabled? @disabled-model
               :table-spec sample-table-spec
               :model @filter-model
               :on-change (fn [model is-valid?]
                            (reset! filter-model model)
                            (reset! filter-valid? is-valid?))
               :style {:font-family "'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif"
                       :font-size "13px" :background-color "#f8fafc"}  ; Modern font and smaller size
               :parts {:wrapper {:style {:background-color "#f8fafc"
                                         :border "1px solid #e2e8f0"
                                         :border-radius "6px"
                                         :box-shadow "0 1px 3px rgba(0, 0, 0, 0.1)"}}
                       :header {:style {:color "#1e40af"
                                        :font-size "14px"
                                        :font-weight "500"}}
                       :filter {:style {:align-items "center" :background-color "transparent"}}  ; Only alignment, no background styling
                       :add-button {:style {:background-color "#dbeafe"
                                            :color "#1d4ed8"
                                            :font-weight "500"
                                            :font-size "12px"
                                            :border-radius "4px"
                                            :height "20px"    ; Consistent height
                                            :line-height "15px"  ; Center text vertically
                                            :display "flex"}}
                       :column-dropdown {:style {:font-size "12px"
                                                 :border "1px solid #bfdbfe"
                                                 :border-radius "4px"
                                                 :width "90px"}
                                         :parts {:chosen-single {:style {:height "20px"
                                                                         :line-height "18px"}}}}
                       :operator-dropdown {:style {:font-size "12px"
                                                   :border "1px solid #bfdbfe"
                                                   :border-radius "4px"
                                                   :width "115px"}
                                           :parts {:chosen-single {:style {:height "20px"
                                                                           :line-height "18px"}}}}
                       :text-input {:style {:font-size "12px"
                                            :border "1px solid #bfdbfe"
                                            :border-radius "4px"
                                            :background-color "#fafbff"
                                            :height "20px"}
                                    :parts {:wrapper {:style {:width "150px"}}}}
                       :date-input {:style {:font-size "12px"
                                            ;:border "1px solid #bfdbfe"
                                            ;:border-radius "4px"
                                            ;:background-color "#fafbff"
                                            ;:height "20px"
                                            }
                                    :parts {:anchor-label {:style {:font-size "12px"
                                                                   :width "90px"
                                                                   :height "20px"
                                                                   :line-height "7px"}}}}
                       :daterange-input {:style {:font-size "12px"
                                                 :border "1px solid #bfdbfe"
                                                 :border-radius "4px"
                                                 :background-color "#fafbff"}
                                         :parts {:wrapper {:style {:width "150px"}}}}
                       :dropdown-input {:style {:font-size "12px"
                                                :border "1px solid #bfdbfe"
                                                :border-radius "4px"
                                                :background-color "#fafbff"
                                                :height "20px"
                                                :width "115px"}
                                        :parts {:chosen-single {:style {:height "20px"
                                                                        :line-height "18px"}}}}
                       :tag-dropdown-input {:style {;:font-size "12px"
                                                    ;:border "1px solid #bfdbfe"
                                                    ;:border-radius "4px"
                                                    ;:background-color "#fafbff"
                                                    :height "20px"
                                                   ; :width "150px"
                                                    }}
                       :where-label {:style {:color "#3b82f6"
                                             :font-size "12px"}}
                       :operator-button {:style {:font-size "12px"
                                                 :background-color "#f1f5f9"
                                                 :border "1px solid #cbd5e1"
                                                 :color "#475569"
                                                 :height "20px"
                                                 :display "flex"}}
                       :operator-text {:style {:font-size "12px"
                                               :color "#64748b"
                                               :height "20px"}}}]

              [demo2 max-depth-model top-label-model hide-border-model disabled-model filter-model filter-valid?]
              ]]]]]]
              [parts-table "table-filter" table-filter-parts-desc]]
              ])))
