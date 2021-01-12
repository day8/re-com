(ns re-demo.multi-select
  (:require [cljs.pprint          :as pprint]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box gap v-box multi-select hyperlink-href p label]]
            [re-com.multi-select  :refer [multi-select-args-desc]]
            [re-demo.utils        :refer [panel-title title2 args-table github-hyperlink status-text]]))

(def model (reagent/atom #{:tesla-model-s}))

(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[multi-select ... ]"
                            "src/re_com/multi_select.cljs"
                            "src/re_demo/multi_select.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                            :gap      "10px"
                            :width    "450px"
                            :children [[title2 "Notes"]
                                       [status-text "Stable"]
                                       [p "A multi-select component."]
                                       [args-table multi-select-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Demo"]
                                      [h-box
                                       :children [[label :label [:code ":model"]]
                                                  [:code (with-out-str (pprint/pprint @model))]]]
                                      [multi-select
                                       :width       "450px"
                                       :choices     [{:id :tesla-model-s  :label "Model S" :group "Tesla"}
                                                     {:id :tesla-model-3  :label "Model 3" :group "Tesla"}
                                                     {:id :porsche-taycan :label "Taycan"  :group "Porsche"}
                                                     {:id :renault-zoe    :label "Zoe"     :group "Renault"}
                                                     {:id :kia-e-niro     :label "e-Niro"  :group "Kia"}
                                                     {:id :kia-soul       :label "Soul"    :group "Kia"}]
                                       :model       model
                                       :sort-fn     :group
                                       :filter-box? true
                                       :on-change   #(reset! model %)]]]]]]])


