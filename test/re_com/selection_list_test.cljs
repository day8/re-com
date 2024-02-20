(ns re-com.selection-list-test
  (:require [cljs.test             :refer-macros [is are deftest]]
            [reagent.core          :as reagent]
            [re-com.selection-list :as s-list]))

;; --- Utilities ---

(defn div-app []
  (let [div (.createElement js/document "div")]
    (set! (.-id div) "app")
    div))

(extend-type js/NodeList
  ISeqable
  (-seq [array] (array-seq array 0)))

;; --- Tests ---
;; NOTE: Commented out becasue internal representation of props changes with new versions of React/Reagent.
;;       Find a more robust way to get props before reinstating

#_(deftest test-selection-list
    (let [new-comp (reagent/render-component
                    [s-list/selection-list
                     :choices [{:id "1" :name "item 1"} {:id "2" :name "item 2"} {:id "3" :name "item 3"}]
                     :model #{"2"}
                     :on-change #(println %)]
                    (div-app))
        ;props (last (-> new-comp .-_renderedComponent .-props .-argv .-tail))                                      ;; Old internal representation
          props (last (-> new-comp .-_reactInternalInstance .-_renderedComponent .-_instance .-props .-argv .-tail))  ;; New internal representation (very fragile!)
          ]
      (is (true?  (:multi-select? props))  "Expected :multi-select? to default to true.")
      (is (false? (:as-exclusions? props)) "Expected :as-exclusions? to default to false.")
      (is (false? (:required? props))      "Expected :required? to default to false.")
      (is (false? (:disabled? props))      "Expected :disabled? to default to false.")
      (is (false? (:hide-border? props))   "Expected :hide-border? to default to false.")
      (is (fn?    (:label-fn props))       "Expected :label-fn to default to a function."))
    (let [new-comp (reagent/render-component
                    [s-list/selection-list
                     :choices [{:id "1" :name "item 1"} {:id "2" :name "item 2"} {:id "3" :name "item 3"}]
                     :model #{"2"}
                     :multi-select? false
                     :on-change #()]
                    (div-app))
          ;props (last (-> new-comp .-_renderedComponent .-props .-argv .-tail))
          props (last (-> new-comp .-_reactInternalInstance .-_renderedComponent .-_instance .-props .-argv .-tail))]
      (is (false? (:multi-select? props))  "Expected :multi-select? to default to true.")))
