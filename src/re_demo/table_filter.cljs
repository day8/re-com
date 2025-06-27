(ns re-demo.table-filter
  (:require-macros
   [re-com.core     :refer []])
  (:require
   [re-com.core     :as rc :refer [at h-box v-box label p p-span single-dropdown]]
   [re-com.table-filter :refer [table-filter]]
   [re-demo.utils   :refer [panel-title title2 title3]]
   [reagent.core    :as r]
   [cljs.pprint]
   [re-demo.dropdown :as dropdown]
   [re-demo.box :as box]))

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
   {:id :skills :name "Skills" :type :multi-select
    :options [{:id "clojure" :label "Clojure"}
              {:id "javascript" :label "JavaScript"}
              {:id "python" :label "Python"}
              {:id "java" :label "Java"}]}])

(defn panel
  []
  (let [filter-model (r/atom nil)
        dropdown-test-model (r/atom nil)]
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
         [[v-box :src (at) :gap "10px" :width "450px"
           :children
           [[title2 "Notes"]
            [p-span "An intuitive table filter with smart grouping logic:"]
            [:ul
             [:li "Progressive complexity - starts with one rule"]
             [:li "Smart + buttons - click after any rule to add AND/OR"]
             [:li "Intelligent grouping - same operators extend lists, different create sub-groups"]
             [:li "Visual hierarchy - nested boxes show logical relationships"]
             [:li "Multiple data types: text, number, date, boolean, select, multi-select"]
             [:li "Real-time validation with warning icons"]]
            [title3 "How it works:"]
            [:ul
             [:li "Start with one empty rule (must be filled for valid filter)"]
             [:li "Click + after rule → choose AND/OR → smart grouping happens"]
             [:li "Same operator (AND + AND) = extends current list"]
             [:li "Different operator (AND + OR) = creates smart sub-grouping"]
             [:li "Visual boxes show which rules are grouped together"]]
            [title3 "Table Spec"]
            [p-span "Define your table columns with their types and options:"]

            [:<>
             [:style ".custom-chosen-single span { margin-right: 2px !important; }"]
             [single-dropdown
              :src (at)
              :width (if (nil? @dropdown-test-model) "80px" "60px")
              :parts {:chosen-single {:class "custom-chosen-single"
                                      :style {:margin-right "2 px"
                                              ;:background-color "red"
                                              }
                                      }}
              :choices (if (nil? @dropdown-test-model) ["AND" "OR"] [{:id "AND" :label "AND"}
                                                                     {:id "OR" :label "OR"}])
              :placeholder "Add Filter"
              :model dropdown-test-model
              :on-change #(reset! dropdown-test-model %)
              :free-text? (if (nil? @dropdown-test-model) true false)
              :cancelable? true]]

            [p (str "is: " @dropdown-test-model)]

            [:pre {:style {:background-color "#f5f5f5" :padding "10px" :font-size "12px"}}
             (str sample-table-spec)]]]
          [v-box :src (at) :width "700px" :gap "10px"
           :children
           [[title2 "Demo"]
            [v-box :src (at) :gap "15px" :style {:padding "20px" :border "1px solid #ddd"}
             :children
             [[label :label "Table Filter:"]
              [table-filter sample-table-spec @filter-model
               #(reset! filter-model %)]
              [title3 "Current Filter Model:"]
              [:pre {:style {:background-color "#f9f9f9" :padding "10px" :font-size "12px" :max-height "200px" :overflow "auto"}}
               (if @filter-model
                 (with-out-str (cljs.pprint/pprint @filter-model))
                 "nil")]
              [title3 "Debug Info:"]
              [p-span "Filter is " (if @filter-model "active" "empty")]
              (when @filter-model
                [p-span " - Contains " (count (tree-seq #(or (:and %) (:or %) (:not %))
                                                        #(or (:and %) (:or %) (vector (:not %)))
                                                        @filter-model)) " nodes"])]]]]]]]])))