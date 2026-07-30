(ns re-com.radio-button-test
  "Regression for #376: since the part migration, a string :label rendered as a
   bare text node — part/part's string shortcut returned it verbatim, bypassing
   the label element, the theme's padding/rc-radio-button-label, the caller's
   :label-style/:label-class, and the label's on-click (label clicks stopped
   toggling the radio). These tests render the component fn and walk the
   returned hiccup for the label part's props — no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [clojure.string :as str]
   [re-com.radio-button :as-alias rb]
   [re-com.radio-button :refer [radio-button]]))

(defn find-part
  "Walk a part-rendered hiccup tree (parts are [impl-fn props] vectors whose
   props carry :part) for the props map of part k. Descends map values so
   parts nested under :children are reached."
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
  (let [render (radio-button)]
    (apply render (mapcat identity
                          (merge {:model     :a
                                  :value     :b
                                  :on-change identity}
                                 args)))))

(deftest string-label-renders-as-a-real-label-part
  (let [tree  (render-tree :label "Click me"
                           :label-style {:font-size "20px"}
                           :label-class "my-label")
        props (find-part tree ::rb/label)]
    (testing "the label is an element-rendering part, not a bare text node"
      (is (some? props)
          "a ::rb/label part must exist in the rendered tree for a string :label"))
    (when props
      (testing "the label content is the part's child"
        (is (= ["Click me"] (:children props))))
      (testing "the bootstrap class and the caller's :label-class land"
        (is (str/includes? (pr-str (:class props)) "rc-radio-button-label"))
        (is (str/includes? (pr-str (:class props)) "my-label")))
      (testing "the theme's padding and the caller's :label-style land"
        (is (= "8px" (get-in props [:style :padding-left])))
        (is (= "20px" (get-in props [:style :font-size]))))
      (testing "clicking the label still toggles the radio (pre-2.29 behaviour)"
        (is (fn? (get-in props [:attr :on-click])))))))

(deftest hiccup-label-is-wrapped-not-replaced
  (let [tree  (render-tree :label [:em "styled"])
        props (find-part tree ::rb/label)]
    (is (some? props) "a hiccup :label renders inside the label part, as pre-2.29")
    (when props
      (is (= [[:em "styled"]] (:children props))))))

(deftest absent-label-renders-no-label-part
  (let [tree (render-tree)]
    (is (nil? (find-part tree ::rb/label)))))
