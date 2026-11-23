(ns re-com.label-test
  "Regression for label's part migration (sibling of #376 and #377): :on-click
   was passed to the ::label-wrapper part as a top-level prop, which the default
   part impl never renders, so label clicks did nothing. A string :label was
   also returned verbatim by part/part's content shortcut, bypassing the ::label
   element, its rc-label class and its flex-child style.
   Same hiccup-walk technique as re-com.checkbox-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [clojure.string :as str]
   [re-com.label :as-alias l]
   [re-com.text :refer [label]]))

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
  (let [render (label)]
    (apply render (mapcat identity args))))

(deftest on-click-reaches-the-element
  (let [clicks (atom 0)
        tree   (render-tree :label "Click me" :on-click #(swap! clicks inc))
        props  (find-part tree ::l/label-wrapper)]
    (testing "the label wrapper carries the click handler as a DOM attribute"
      (is (fn? (get-in props [:attr :on-click]))))
    (testing "the handler calls :on-click"
      (when-let [handler (get-in props [:attr :on-click])]
        (handler nil)
        (is (= 1 @clicks))))))

(deftest on-click-keeps-the-callers-attr
  (let [over  (fn [_])
        tree  (render-tree :label "Hover me"
                           :on-click (fn [])
                           :attr {:on-mouse-over over})
        props (find-part tree ::l/label-wrapper)]
    (is (fn? (get-in props [:attr :on-click])))
    (is (= over (get-in props [:attr :on-mouse-over])))))

(deftest no-on-click-means-no-handler
  (let [props (find-part (render-tree :label "Static") ::l/label-wrapper)]
    (is (nil? (get-in props [:attr :on-click])))))

(deftest string-label-renders-as-a-real-label-part
  (let [props (find-part (render-tree :label "Text") ::l/label)]
    (testing "the label is an element-rendering part, not a bare text node"
      (is (some? props)
          "a ::l/label part must exist in the rendered tree for a string :label"))
    (when props
      (testing "the label content is the part's child"
        (is (= ["Text"] (:children props))))
      (testing "the automatic rc-label class lands"
        (is (str/includes? (pr-str (:class props)) "rc-label")))
      (testing "the label does not shrink or wrap inside its wrapper"
        (is (= "none" (get-in props [:style :flex])))))))
