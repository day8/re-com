(ns re-com.popover-title-test
  "Regression from 2.29.4: popover-title documents :close-button? as
   defaulting to true, but popover-content-wrapper always passes the key, so
   when its caller omits :close-button? the title receives nil, the :or
   default does not apply, and the close button disappears.
   Same hiccup-walk technique as re-com.checkbox-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [re-com.popover-title :as-alias pt]
   [re-com.popover :refer [popover-title]]))

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

(defn close-button-part [& {:as args}]
  (let [render (popover-title)]
    (find-part (apply render (mapcat identity (merge {:showing? (atom true)
                                                      :title    "Title"}
                                                     args)))
               ::pt/close-button)))

(deftest close-button-defaults-to-shown
  (testing "omitted"
    (is (some? (close-button-part))))
  (testing "passed as nil, as popover-content-wrapper does when its caller omits it"
    (is (some? (close-button-part :close-button? nil))))
  (testing "true"
    (is (some? (close-button-part :close-button? true))))
  (testing "false hides it"
    (is (nil? (close-button-part :close-button? false)))))
