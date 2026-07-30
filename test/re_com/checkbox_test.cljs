(ns re-com.checkbox-test
  "Regression for #377 (sibling of #376): a string :label bypassed the ::label
   part via part/part's string shortcut, so the label element, the automatic
   rc-checkbox-label class, the caller's :label-style/:label-class and the
   label's on-click (label clicks toggle the checkbox) were all silently lost.
   Masked visually because the checkbox theme spaces via the wrapper's :gap.
   Same hiccup-walk technique as re-com.radio-button-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [clojure.string :as str]
   [re-com.checkbox :as-alias cb]
   [re-com.checkbox :refer [checkbox]]))

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
  (let [render (checkbox)]
    (apply render (mapcat identity
                          (merge {:model     true
                                  :on-change identity}
                                 args)))))

(deftest string-label-renders-as-a-real-label-part
  (let [tree  (render-tree :label "Click me"
                           :label-style {:font-size "20px"}
                           :label-class "my-label")
        props (find-part tree ::cb/label)]
    (testing "the label is an element-rendering part, not a bare text node"
      (is (some? props)
          "a ::cb/label part must exist in the rendered tree for a string :label"))
    (when props
      (testing "the label content is the part's child"
        (is (= ["Click me"] (:children props))))
      (testing "the automatic part class and the caller's :label-class land"
        (is (str/includes? (pr-str (:class props)) "rc-checkbox-label"))
        (is (str/includes? (pr-str (:class props)) "my-label")))
      (testing "the caller's :label-style lands"
        (is (= "20px" (get-in props [:style :font-size]))))
      (testing "clicking the label still toggles the checkbox"
        (is (fn? (get-in props [:attr :on-click])))))))

(deftest hiccup-label-is-wrapped-not-replaced
  (let [tree  (render-tree :label [:em "styled"])
        props (find-part tree ::cb/label)]
    (is (some? props) "a hiccup :label renders inside the label part")
    (when props
      (is (= [[:em "styled"]] (:children props))))))

(deftest absent-label-renders-no-label-part
  (let [tree (render-tree)]
    (is (nil? (find-part tree ::cb/label)))))
