(ns re-com.part-test
  (:require
   [cljs.test :refer-macros [is are deftest testing]]
   [re-com.part :as part]))

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
  (let [get-part (partial part/get-part structure)]
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
         [{:name :wrapper, :class "rc-part-test-wrapper", :level 0, :impl "[:div]"}
          {:name :cell-grid, :class "rc-part-test-cell-grid", :level 1, :impl "[:div]"}
          {:name :cell, :class "rc-part-test-cell", :level 2, :impl "[:div]" :top-level-arg? true}
          {:name :cell-label, :class "rc-part-test-cell-label", :level 3, :impl "[:div]"}])))

(deftest validate-props
  (are [props problems]
       (= (part/args-valid? structure props [])
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

;;--------------------------------------------------------------------------------------------------
;; Part-Value Rendering Tests
;;--------------------------------------------------------------------------------------------------

(defn component
  "Minimal component to test part-value rendering"
  [& {:as props}]
  (let [part (partial part/part structure props)]
    (part ::wrapper
          {:props {:props    {:style {:color :blue}}
                   :children [(part ::cell {})]}})))

(deftest as-string
  (let [part-val "A"
        result (component :cell part-val)]
    (is (= part-val (get-in result [1 :children 0])))))

(deftest as-hiccup
  (let [part-val [:a]
        result   (component :cell part-val)]
    (is (= part-val (get-in result [1 :children 0])))))

(deftest as-fn
  (let [part-val      (fn [_])
        result        (component :cell part-val)
        rendered-part (get-in result [1 :children 0])]
    (is (= part-val (first rendered-part)))
    (is (= ::cell (:part (second rendered-part))))))

(deftest as-map
  (let [part-val   {:class "A"
                    :style {:color :red}
                    :attr  {:title "B"}}
        result     (component :parts {:cell part-val})
        part-props (get-in result [1 :children 0 1])]
    (is (sequential? (:class part-props)))
    (is (= {:color :red} (:style part-props)))
    (is (= {:title "B"} (:attr part-props)))))
