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
          :style {:position "extradimensional"}}
   :tale {:class (fn [{:keys [is?]}]
                   (if is? "sickle" "less"))
          :attr {:personality "nice"}}})

(deftest test-cmerger-main
  (are [expected actual] (= expected actual)

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

    ["c"] (:class ((util/merge-css fake-css-spec {}) :main))
    ["c" "stowage" "2nd" "1st"]
    (:class ((util/merge-css
              fake-css-spec
              {:class "1st"
               :parts {:main {:class "2nd"}}})
             :main
             {:class "stowage"}))))

(deftest test-cmerger-parts
  (are [expected actual] (= expected actual)

    ["less"] (:class ((util/merge-css fake-css-spec {}) :tale))
    ["sickle"] (:class ((util/merge-css fake-css-spec {}) :tale {:is? true}))

    "nice" (->
            ((re-com.util/merge-css
              fake-css-spec
              {:attr {:personality "cranky"}
               :parts {:main {:attr {:personality "agressive"}}}})
             :tale)
            :attr :personality)

    "split" (->
            ((re-com.util/merge-css
              fake-css-spec
              {:attr {:personality "cranky"}
               :parts {:main {:attr {:personality "aggressive"}}}})
             :tale
             {:attr {:personality "split"}})
            :attr :personality)))


