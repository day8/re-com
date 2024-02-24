(ns re-com.box-test
  (:require [cljs.test    :refer-macros [is are deftest]]
            [reagent.core :as reagent]
            [re-com.box   :as box]))

(deftest test-flex-child-style
  (are [expected actual] (= expected actual)
    "initial"   (:flex (box/flex-child-style "initial"))
    "auto"      (:flex (box/flex-child-style "auto"))
    "none"      (:flex (box/flex-child-style "none"))
    "0 0 100px" (:flex (box/flex-child-style "100px"))
    "0 0 4.5em" (:flex (box/flex-child-style "4.5em"))
    "60 1 0px"  (:flex (box/flex-child-style "60%"))
    "60 1 0px"  (:flex (box/flex-child-style "60"))
    "5 4 0%"    (:flex (box/flex-child-style "5 4 0%"))))

(deftest test-flex-flow-style
  (is (= (box/flex-flow-style "row wrap")
         {:-webkit-flex-flow "row wrap"
          :flex-flow "row wrap"})))

(deftest test-justify-style
  (let [make-expected (fn [x] {:-webkit-justify-content x
                               :justify-content x})]
    (are [expected actual] (= expected actual)
      (make-expected "flex-start") (box/justify-style :start)
      (make-expected "flex-end") (box/justify-style :end)
      (make-expected "center") (box/justify-style :center)
      (make-expected "space-between") (box/justify-style :between)
      (make-expected "space-around") (box/justify-style :around))))

(deftest test-align-style
  (let [make-align-items (fn [x] {:-webkit-align-items x
                                  :align-items x})]
    (are [expected actual] (= expected actual)
      (make-align-items "flex-start") (box/align-style :align-items :start)
      (make-align-items "flex-end") (box/align-style :align-items :end)
      (make-align-items "center") (box/align-style :align-items :center)
      (make-align-items "baseline") (box/align-style :align-items :baseline)
      (make-align-items "stretch") (box/align-style :align-items :stretch))))

(deftest test-scroll-style
  (are [expected actual] (= expected actual)
    {:overflow "auto"} (box/scroll-style :overflow :auto)
    {:overflow "hidden"} (box/scroll-style :overflow :off)
    {:overflow "scroll"} (box/scroll-style :overflow :on)
    {:overflow "visible"} (box/scroll-style :overflow :spill)))

(defn without-debug
  "Returns hiccup form without debug in first attrs as writing tests for that would be complex (i.e. equality of ref-fn fns etc)."
  [[tag attrs & rest]]
  (into [tag (dissoc attrs :data-rc :ref)] rest))

(deftest test-gap
  (are [expected actual] (= expected actual)
    [:div
     {:class ["rc-gap" "my-gap"]
      :style {:flex "0 0 1px"
              :-webkit-flex "0 0 1px"}
      :id "my-id"}]
    (without-debug (box/gap :class "my-gap" :attr {:id "my-id"} :size "1px"))))

(deftest test-line
  (are [expected actual] (= expected actual)
    [:div
     {:class ["rc-line" "my-line"]
      :style {:flex "0 0 1px"
              :-webkit-flex "0 0 1px"
              :background-color "lightgray"}
      :id "my-id"}]
    (without-debug (box/line :class "my-line" :attr {:id "my-id"}))))

(deftest test-box
  (are [expected actual] (= expected actual)
    [:div
     {:class ["display-flex" "rc-box" "my-box"]
      :style {:flex "none"
              :-webkit-flex "none"
              :flex-flow "inherit"
              :-webkit-flex-flow "inherit"}
      :id "my-id"}
     "text"]
    (without-debug (box/box :class "my-box" :attr {:id "my-id"} :child "text"))))

(deftest test-scroller
  (are [expected actual] (= expected actual)
    [:div
     {:class ["display-flex" "rc-scroller" "my-scroller"]
      :style {:flex "auto"
              :-webkit-flex "auto"
              :flex-flow "inherit"
              :-webkit-flex-flow "inherit"
              :overflow "auto"}
      :id "my-id"}
     "text"]
    (without-debug (box/scroller :class "my-scroller" :attr {:id "my-id"} :child "text"))))

(deftest test-border
  (are [expected actual] (= expected actual)
    [:div
     {:class ["display-flex" "rc-border" "my-border"]
      :style {:flex "none"
              :-webkit-flex "none"
              :flex-flow "inherit"
              :-webkit-flex-flow "inherit"
              :border "1px solid lightgrey"}
      :id "my-id"}
     "text"]
    (without-debug (box/border :class "my-border" :attr {:id "my-id"} :child "text"))))
