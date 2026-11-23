(ns re-com.row-button-test
  "Regression from row-button's part migration: :class, :style and :attr are
   documented as applying to the button, but the ::rb/button part was built
   without :post-props, so all three were silently dropped.
   Same hiccup-walk technique as re-com.checkbox-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [clojure.string :as str]
   [re-com.row-button :as-alias rb]
   [re-com.buttons :refer [row-button]]))

(defn find-part
  "Walk a part-rendered hiccup tree (parts are [impl-fn props] vectors whose
   props carry :part) for the props map of part k. Descends map values so
   parts nested under :children or :child are reached."
  [x k]
  (cond
    (and (vector? x)
         (map? (second x))
         (= k (:part (second x))))
    (second x)

    (or (vector? x) (seq? x))
    (some #(find-part % k) x)

    (map? x)
    (some #(find-part % k) (vals x))

    :else nil))

(defn render-tree [& {:as args}]
  (let [render (row-button)]
    (apply render (mapcat identity (merge {:md-icon-name "zmdi-plus"} args)))))

(deftest class-style-and-attr-reach-the-button
  (let [move  (fn [_])
        props (find-part (render-tree :class "my-button"
                                      :style {:font-size "20px"}
                                      :attr  {:on-mouse-move move})
                         ::rb/button)]
    (is (some? props) "the button part exists")
    (when props
      (testing "the caller's :class lands next to the theme's classes"
        (is (str/includes? (pr-str (:class props)) "my-button"))
        (is (str/includes? (pr-str (:class props)) "rc-row-button")))
      (testing "the caller's :style lands"
        (is (= "20px" (get-in props [:style :font-size]))))
      (testing "the caller's :attr lands and the theme's click handler is kept"
        (is (= move (get-in props [:attr :on-mouse-move])))
        (is (fn? (get-in props [:attr :on-click])))))))
