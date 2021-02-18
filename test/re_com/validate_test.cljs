(ns re-com.validate-test
  (:require [cljs.test       :refer-macros [is are deftest]]
            [reagent.core    :as reagent]
            [re-com.validate :as validate]))

(deftest test-hash-map-with-name-keys
  (let [obj1 {:name "obj1", :value 1}
        obj2 {:name "obj2", :value 2}
        dup  {:name "obj1", :value 3}]
    (are [expected actual] (= expected actual)
      {"obj1" obj1} (validate/hash-map-with-name-keys [obj1])
      {"obj1" obj1, "obj2" obj2} (validate/hash-map-with-name-keys [obj1 obj2])
      {"obj1" obj1, "obj2" obj2} (validate/hash-map-with-name-keys [obj1 obj2 obj1])
      {"obj1" dup, "obj2" obj2} (validate/hash-map-with-name-keys [obj1 obj2 dup]))))

(deftest test-extract-arg-data
  (let [arg1 {:name "arg1", :required true, :validate-fn true}
        arg2 {:name "arg2", :required false, :validate-fn true}
        args [arg1
              arg2
              {:name "arg3", :required true, :validate-fn false}
              {:name "arg4", :required false, :validate-fn false}]
        arg-data (validate/extract-arg-data args)]
    (is (= #{"arg1", "arg2", "arg3", "arg4"} (:arg-names arg-data)))
    (is (= #{"arg1", "arg3"} (:required-args arg-data)))
    (is (= {"arg1" arg1, "arg2" arg2} (:validated-args arg-data)))))

(deftest test-arg-names-valid?
  (are [expected actual] (= expected actual)
                         true (validate/arg-names-known? #{:arg1} #{:arg1} "test-arg-names-valid?")
                         false (validate/arg-names-known? #{:arg1} #{:arg2} "test-arg-names-valid?")
                         true (validate/arg-names-known? #{:arg1 :arg2} #{:arg2} "test-arg-names-valid?")
                         false (validate/arg-names-known? #{:arg1 :arg2} #{:arg1 :arg3} "test-arg-names-valid?")))

(deftest test-required-args-passed?
  (are [expected actual] (= expected actual)
                         true (validate/required-args? #{:arg1} #{:arg1} "test-required-args-passed?")
                         false (validate/required-args? #{:arg1} #{:arg2} "test-required-args-passed?")
                         false (validate/required-args? #{:arg1 :arg2} #{:arg2} "test-required-args-passed?")
                         false (validate/required-args? #{:arg1 :arg2} #{:arg1 :arg3} "test-required-args-passed?")
                         true (validate/required-args? #{:arg1 :arg2} #{:arg1 :arg2} "test-required-args-passed?")
                         true (validate/required-args? #{:arg1 :arg2} #{:arg1 :arg2 :arg3} "test-required-args-passed?")))

(deftest test-extension-attribute?
  (is (validate/extension-attribute? :data-attribute))
  (is (not (validate/extension-attribute? :foo-attribute)))
  (is (validate/extension-attribute? :aria-attribute))
  (is (not (validate/extension-attribute? :dataattribute)))
  (is (not (validate/extension-attribute? :ariaattribute))))

(deftest test-string-or-atom?
  (are [expected actual] (= expected actual)
    true (validate/string-or-atom? "test")
    false (validate/string-or-atom? 1)
    true (validate/string-or-atom? (reagent/atom "test"))
    false (validate/string-or-atom? (reagent/atom 1))))

(deftest test-number-or-string?
  (are [expected actual] (= expected actual)
    true (validate/number-or-string? "test")
    true (validate/number-or-string? 1)
    true (validate/number-or-string? (reagent/atom "test"))
    true (validate/number-or-string? (reagent/atom 1))
    false (validate/number-or-string? [])
    false (validate/number-or-string? (reagent/atom []))))
