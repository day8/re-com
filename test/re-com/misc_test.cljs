(ns re-com.misc-test
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [cljs.test    :refer-macros [is deftest]]
            [reagent.core :as reagent]))


(deftest test-handler-fn
         (let [atm (reagent/atom false)]
           (is (false? @atm))
           (is (false? (reset! atm false)))
           (is (nil? ((handler-fn (reset! atm true) {}))) "expected handler-fn to return nil")
           (is (true? (reset! atm true)))
           (is (nil? ((handler-fn (reset! atm false) {}))) "expected handler-fn to return nil")
           (is (false? (reset! atm false)))))
