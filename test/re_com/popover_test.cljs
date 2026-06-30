(ns re-com.popover-test
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [re-com.part :as part]))

;; ---------------------------------------------------------------------------
;; Style-precedence contract that the popover :style fix relies on:
;; a user's :style (via :post-props or :parts) must win over a component's
;; own :style. Mirrors the pure composition tests in theme_test / part_test.
;; ---------------------------------------------------------------------------

(def structure
  [::wrapper
   [::border]])

(deftest user-post-props-style-wins-over-component-style
  (testing "a user :style (carried in :post-props) overrides a colliding component :style"
    (let [[_ props] (part/part structure {} ::border
                               {:props      {:style {:opacity "0" :max-width "none" :color "black"}}
                                :post-props {:style {:opacity "0.42" :max-width "137px"}}})]
      (is (= "0.42"  (get-in props [:style :opacity])))
      (is (= "137px" (get-in props [:style :max-width])))
      (is (= "black" (get-in props [:style :color])) "non-colliding component style is preserved"))))

(deftest user-parts-style-wins-over-component-style
  (testing "a user :parts {part {:style …}} overrides a colliding component :style in :props"
    (let [[_ props] (part/part structure
                               {:parts {:border {:style {:color "white" :font-size "12px"}}}}
                               ::border
                               {:props {:style {:color "black" :font-size "14px" :font-weight "bold"}}})]
      (is (= "white" (get-in props [:style :color])))
      (is (= "12px"  (get-in props [:style :font-size])))
      (is (= "bold"  (get-in props [:style :font-weight])) "non-colliding component style is preserved"))))
