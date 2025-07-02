(ns re-demo.table-filter
  (:require-macros
   [re-com.core     :refer []])
  (:require
   [re-com.core     :as rc :refer [at h-box v-box label p-span table-filter]]
   [re-com.table-filter :refer [table-filter-parts-desc table-filter-args-desc]]
   [re-demo.utils   :refer [panel-title title2 title3 parts-table args-table]]
   [reagent.core    :as r]
   [cljs.pprint]))

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

(def sample-data
  [{:name "Alice Johnson" :age 28 :email "alice@company.com" :salary 85000 :department "engineering" :active true :hire-date "2022-03-15" :skills #{"clojure" "javascript"}}
   {:name "Bob Smith" :age 34 :email "bob@company.com" :salary 92000 :department "engineering" :active true :hire-date "2021-08-22" :skills #{"python" "java"}}
   {:name "Carol Davis" :age 29 :email "carol@company.com" :salary 78000 :department "marketing" :active false :hire-date "2023-01-10" :skills #{"javascript"}}
   {:name "David Wilson" :age 42 :email "david@company.com" :salary 105000 :department "sales" :active true :hire-date "2020-05-03" :skills #{"python" "clojure"}}
   {:name "Eva Martinez" :age 26 :email "eva@company.com" :salary 72000 :department "marketing" :active true :hire-date "2023-06-18" :skills #{"java" "javascript"}}])

(defn panel
  []
  (let [filter-model (r/atom nil)]
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
           [[args-table table-filter-args-desc]]]
          [v-box :src (at) :width "700px" :gap "10px"
           :children
           [[title2 "Interactive Demo"]
            [v-box :src (at) :gap "15px" :style {:padding "20px"}
             :children
             [[table-filter
               ;:disabled? true
               :table-spec sample-table-spec
               :model @filter-model
               :on-change #(reset! filter-model %)]
              [h-box :gap "20px" :align :center
               :children
               [[p-span "Rows shown: " [:strong (str (count sample-data) " total")]]
                (when @filter-model
                  [p-span " • Filter contains " [:strong (str (count (tree-seq #(= (:type %) :group) :children @filter-model)) " nodes")]])]]
              [title3 "Current Filter Model:"]
              [:pre {:style {:background-color "#f9f9f9" :padding "15px" :font-size "11px" :max-height "250px" :overflow "auto" :border-radius "4px" :border "1px solid #e9ecef"}}
               (if @filter-model
                 (with-out-str (cljs.pprint/pprint @filter-model))
                 "nil")]
              [title3 "Table Schema:"]
              [:pre {:style {:background-color "#f8f9fa" :padding "15px" :font-size "11px" :border-radius "4px" :border "1px solid #e9ecef"}}
               (with-out-str (cljs.pprint/pprint sample-table-spec))]
              [title3 "Sample Data:"]
              [:pre {:style {:background-color "#f8f9fa" :padding "15px" :font-size "11px" :border-radius "4px" :border "1px solid #e9ecef" :max-height "300px" :overflow "auto"}}
               (with-out-str (cljs.pprint/pprint sample-data))]]]]]]]]])))