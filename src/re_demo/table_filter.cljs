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
           [

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
            [v-box :src (at) :gap "15px" :style {:padding "20px"}
             :children
             [[label :label "Table Filter:"]
              [table-filter 
               :table-spec sample-table-spec 
               :model @filter-model 
               :on-change #(reset! filter-model %)]
              [title3 "Current Filter Model:"]
              [:pre {:style {:background-color "#f9f9f9" :padding "10px" :font-size "12px" :max-height "200px" :overflow "auto"}}
               (if @filter-model
                 (with-out-str (cljs.pprint/pprint @filter-model))
                 "nil")]
              [title3 "Debug Info:"]
              [p-span "Filter is " (if @filter-model "active" "empty")]
              (when @filter-model
                [p-span " - Contains " (count (tree-seq #(= (:type %) :group)
                                                        :children
                                                        @filter-model)) " nodes"])]]]]]]]])))