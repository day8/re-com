(ns re-com.cmerger-test
  (:require [cljs.test    :refer-macros [is are deftest]]
            [reagent.core :as reagent]
            [re-com.util   :as util]))

(deftest test-cmerger-minimal
  (let [result ((util/merge-css {:main {}} {}) :main)]
    (is (map? result))
    (is (empty? result))))

(def fake-css-spec
  {:main {:class ["c"]
          :style {:position "extradimensional"}
          :attr {:personality "nice"}}
   :tale {:class (fn [{:keys [is?]}]
                   (if is? "sickle" "less"))}})

(deftest test-cmerger-main
  (are [expected actual] (= expected actual)
    ["c"] (:class ((util/merge-css fake-css-spec {}) :main))
    
    "extradimensional" (->
                        ((util/merge-css fake-css-spec {}) :main)
                        :style :position)
    "here" (->
            ((util/merge-css
              fake-css-spec
              {})
             :main
             {:style {:position "here"}})
            :style :position)
    "there" (->
             ((util/merge-css
               fake-css-spec
               {:parts {:main {:style {:position "there"}}}})
              :main
              {:style {:position "here"}})
             :style :position)
    "anywhere" (->
                ((util/merge-css
                  fake-css-spec
                  {:style {:position "anywhere"}
                   :parts {:main {:style {:position "there"}}}})
                 :main
                 {:style {:position "here"}})
                :style :position)

    ))
