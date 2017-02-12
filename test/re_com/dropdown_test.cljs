(ns re-com.dropdown-test
  (:require [cljs.test       :refer-macros [is are deftest]]
            [reagent.core    :as reagent]
            [re-com.dropdown :as dropdown]))

(def test-choices
  [{:id 1 :label "Choice 1"}
   {:id 2 :label "Choice 2"}
   {:id 3 :label "Choice 3"}
   {:id 4 :label "Choice 4"}])

(deftest test-single-dropdown-parent-style
  (let [selected-test-choice (reagent/atom 1)
        args                 (list
                              :choices     test-choices
                              :model       selected-test-choice
                              :placeholder "Choose a choice"
                              :width       "300px"
                              :max-height  "400px"
                              :on-change   #(reset! selected-test-choice %))
        actual               (apply (apply dropdown/single-dropdown args) args)]
    (is (= (nth actual 0) :div))
    (is (= (nth actual 1) {:class "rc-dropdown chosen-container chosen-container-single noselect "
                           :style {:-webkit-flex       "0 0 auto"
                                   :flex               "0 0 auto"
                                   :-webkit-align-self "flex-start"
                                   :align-self         "flex-start"
                                   :width              "300px"}}))))
       
