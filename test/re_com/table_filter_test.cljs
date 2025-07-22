(ns re-com.table-filter-test
  (:require [cljs.test :refer-macros [is are deftest]]
            [reagent.core :as reagent]
            [re-com.table-filter :as table-filter]))

;; Test data
(def sample-table-spec
  [{:id :name :name "Name" :type :text}
   {:id :age :name "Age" :type :number}
   {:id :active :name "Active" :type :boolean}
   {:id :department :name "Department" :type :select 
    :options [{:id "engineering" :label "Engineering"}
              {:id "marketing" :label "Marketing"}]}])

;; Helper function tests
(deftest test-valid-number?
  (are [expected actual] (= expected actual)
    true  (table-filter/valid-number? "123")
    true  (table-filter/valid-number? "123.45") 
    true  (table-filter/valid-number? "-123")
    true  (table-filter/valid-number? "-123.45")
    false (table-filter/valid-number? "abc")
    false (table-filter/valid-number? "123abc")
    false (table-filter/valid-number? "")
    false (table-filter/valid-number? nil)))

(deftest test-valid-date?
  (are [expected actual] (= expected actual)
    true  (table-filter/valid-date? "2023-01-01")
    true  (table-filter/valid-date? #inst "2023-01-01")
    false (table-filter/valid-date? nil)
    false (table-filter/valid-date? "")))

;; Validation function tests
(deftest test-table-spec?
  (is (true? (table-filter/table-spec? sample-table-spec)))
  (is (false? (table-filter/table-spec? [])))
  (is (false? (table-filter/table-spec? nil)))
  (is (false? (table-filter/table-spec? [{}]))) ; missing required keys
  (is (false? (table-filter/table-spec? [{:id :name}])))) ; missing name and type

(deftest test-filter-node?
  (let [valid-filter {:type :filter :col :name :op :contains :val "test"}
        invalid-filter {:type :filter :col "name"}] ; col should be keyword
    (is (true? (table-filter/filter-node? valid-filter)))
    (is (false? (table-filter/filter-node? invalid-filter)))
    (is (false? (table-filter/filter-node? nil)))
    (is (false? (table-filter/filter-node? {})))))

(deftest test-group-node?
  (let [valid-group {:type :group :logic :and :children []}
        invalid-group {:type :group :logic "and"}] ; logic should be keyword
    (is (true? (table-filter/group-node? valid-group)))
    (is (false? (table-filter/group-node? invalid-group)))
    (is (false? (table-filter/group-node? nil)))
    (is (false? (table-filter/group-node? {})))))

;; Tree manipulation tests
(deftest test-add-ids
  (let [model-without-ids {:type :filter :col :name :op :contains :val "test"}
        model-with-ids (table-filter/add-ids model-without-ids)]
    (is (map? model-with-ids))
    (is (string? (:id model-with-ids)))
    (is (= (:type model-with-ids) :filter))
    (is (= (:col model-with-ids) :name))))

(deftest test-remove-ids
  (let [model-with-ids {:id "test-id" :type :filter :col :name :op :contains :val "test"}
        model-without-ids (table-filter/remove-ids model-with-ids)]
    (is (map? model-without-ids))
    (is (nil? (:id model-without-ids)))
    (is (= (:type model-without-ids) :filter))
    (is (= (:col model-without-ids) :name))))

(deftest test-update-item-by-id
  (let [original-tree {:id "group-1" :type :group :logic :and 
                       :children [{:id "filter-1" :type :filter :col :name :op :contains :val "old"}]}
        updated-tree (table-filter/update-item-by-id original-tree "filter-1" 
                                                     #(assoc % :val "new"))]
    (is (= "new" (get-in updated-tree [:children 0 :val])))))

(deftest test-remove-item-by-id
  (let [original-tree {:id "group-1" :type :group :logic :and 
                       :children [{:id "filter-1" :type :filter :col :name :op :contains :val "test"}
                                  {:id "filter-2" :type :filter :col :age :op :equals :val "25"}]}
        updated-tree (table-filter/remove-item-by-id original-tree "filter-1")]
    (is (= 1 (count (:children updated-tree))))
    (is (= "filter-2" (get-in updated-tree [:children 0 :id])))))

(deftest test-remove-item-with-cleanup
  (let [original-tree {:id "group-1" :type :group :logic :and
                       :children [{:id "filter-1" :type :filter :col :name :op :contains :val "test"}
                                  {:id "group-2" :type :group :logic :and
                                   :children [{:id "filter-3" :type :filter :col :age :op :equals :val "25"}]}]}

        updated-tree (table-filter/remove-item-with-cleanup original-tree "filter-3" sample-table-spec)]
    (is (= 1 (count (:children updated-tree))))
    (is (= "filter-1" (get-in updated-tree [:children 0 :id])))))

(deftest test-add-child-to-group
  (let [original-tree {:id "group-1" :type :group :logic :and :children []}
        new-filter {:id "filter-1" :type :filter :col :name :op :contains :val "test"}
        updated-tree (table-filter/add-child-to-group original-tree "group-1" new-filter)]
    (is (= 1 (count (:children updated-tree))))
    (is (= "filter-1" (get-in updated-tree [:children 0 :id])))))

;; Empty structure creation tests
(deftest test-empty-filter-external
  (let [empty-filter (table-filter/empty-filter-external sample-table-spec)]
    (is (= :filter (:type empty-filter)))
    (is (= :name (:col empty-filter))) ; should use first column
    (is (keyword? (:op empty-filter)))
    (is (nil? (:val empty-filter)))
    (is (nil? (:id empty-filter))))) ; external format should not have ID

(deftest test-empty-group-external
  (let [empty-group (table-filter/empty-group-external sample-table-spec)]
    (is (= :group (:type empty-group)))
    (is (= :and (:logic empty-group)))
    (is (= 1 (count (:children empty-group))))
    (is (nil? (:id empty-group))))) ; external format should not have ID

;; Component render test (basic smoke test)
(deftest test-table-filter-component
  (let [test-model (reagent/atom nil)
        component-fn (table-filter/table-filter)
        component (component-fn 
                   :table-spec sample-table-spec
                   :model test-model
                   :on-change identity)]
    (is (vector? component))
    (is (= :div (first component)))))

