(ns re-com.alert-box-test
  "Regressions from alert-box's part migration. Before it, the body was
   wrapped in a div, so a body such as a v-box could shrink and wrap inside
   the body h-box; content passed as :body is now returned verbatim by
   part/part and lands directly in the h-box. The h4 heading also lost its
   inline margin-bottom: 0, so it gained the h4 margin.
   Same hiccup-walk technique as re-com.checkbox-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [re-com.alert-box :as-alias ab]
   [re-com.alert :refer [alert-box]]))

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
  (let [render (alert-box)]
    (apply render (mapcat identity args))))

(deftest content-body-is-wrapped-in-the-body-part
  (testing "hiccup body"
    (let [body  [:p "Some text"]
          props (find-part (render-tree :body body) ::ab/body)]
      (is (some? props) "a ::ab/body part element wraps the body content")
      (when props
        (is (= [body] (:children props))))))
  (testing "string body"
    (let [props (find-part (render-tree :body "Some text") ::ab/body)]
      (is (some? props))
      (when props
        (is (= ["Some text"] (:children props)))))))

(deftest heading-has-no-bottom-margin
  (let [props (find-part (render-tree :heading "Heading" :body "Some text") ::ab/heading-wrapper)]
    (is (= "0px" (get-in props [:style :margin-bottom])))))
