(ns re-com.part-test
  (:require
   [cljs.test :refer-macros [is are deftest]]
   [re-com.part :as part]
   [re-com.validate :as validate]))

(def structure
  [::wrapper
   [::cell-grid
    [::cell {:top-level-arg? true}
     [::cell-label]]]])

(deftest depth
  (is (= 1 (part/depth structure ::wrapper)))
  (is (= 4 (part/depth structure ::cell-label))))

(deftest tree-walk
  (is (= (tree-seq part/branch? part/children structure)
         '([::wrapper
            [::cell-grid
             [::cell {:top-level-arg? true}
              [::cell-label]]]]
           [::cell-grid
            [::cell {:top-level-arg? true}
             [::cell-label]]]
           [::cell {:top-level-arg? true}
            [::cell-label]]
           [::cell-label]))))

(deftest get-part
  (let [get-part (partial part/destructure structure)]
    (are [props k] (get-part props k)
      {:parts {:wrapper true}} :wrapper
      {:parts {:wrapper true}} :wrapper
      {:cell true}             :cell)
    (is (= [true true true true]
           [(part/top-level-arg? structure :cell)
            (get-part {:cell true} :cell)
            (not (part/top-level-arg? structure :wrapper))
            (not
             (get-part {:wrapper true} :wrapper))])
        "The getter function looks in a component's top-level keys,
         but only when :top-level-arg? is explicitly declared in the part structure.")))

(deftest describe
  (is (= (part/describe structure)
         [{:name :wrapper, :class "rc-part-test-wrapper", :level 1, :impl "[:div]"}
          {:name :cell-grid, :class "rc-part-test-cell-grid", :level 2, :impl "[:div]"}
          {:name :cell, :class "rc-part-test-cell", :level 3, :impl "[:div]"}
          {:name :cell-label, :class "rc-part-test-cell-label", :level 4, :impl "[:div]"}])))

(deftest validate-props
  (are [props problems]
       (= (validate/part-keys-valid? structure props [])
          problems)
    {:cell true}             []
    {:parts {:cell true}}    []
    {:parts {:wrapper true}} []
    {:cell  true
     :parts {:cell true}}    [{:problem  :part-top-level-collision
                               :arg-name :cell}]
    {:wrapper true}          [{:problem  :part-top-level-unsupported
                               :arg-name :wrapper}]
    {:wrapper true
     :cell    true
     :parts   {:cell true}}  [{:problem  :part-top-level-collision
                               :arg-name :cell}
                              {:problem  :part-top-level-unsupported
                               :arg-name :wrapper}]))
