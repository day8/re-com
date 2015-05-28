(ns re-com-test.selection-list-test
  (:require-macros [cemerick.cljs.test :refer (is are deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test]
            [devtools.core            :as    devtools]  ;; TODO remove this
            [reagent.core :as reagent]
            [re-com.selection-list :as s-list]))


;; TODO remove this EXPERIMENTAL requires Canary
;;(devtools/install!)
;;(.log js/console (range 200))


(defn div-app []
  (let [div (.createElement js/document "div")]
    (set! (.-id div) "app")
    div))

(deftest test-selection-list
  (let [new-comp (reagent/render-component
                   [s-list/selection-list
                    :choices [{:id "1" :name "item 1"} {:id "2" :name "item 2"} {:id "3" :name "item 3"}]
                    :model #{"2"}
                    :on-change #(println %)]
                   (div-app))
        props (last (-> new-comp .-_renderedComponent .-props .-argv .-tail))]
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
                    :on-change #(println %)]
                   (div-app))
        props (last (-> new-comp .-_renderedComponent .-props .-argv .-tail))]
      (is (false? (:multi-select? props))  "Expected :multi-select? to default to true.")))


