(ns re-demo.multi-select
  (:require-macros
    [re-com.debug  :refer [src-coordinates]])
  (:require [cljs.pprint          :as pprint]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box box checkbox gap v-box multi-select hyperlink-href p label]]
            [re-com.multi-select  :refer [multi-select-parts-desc multi-select-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text new-in-version]]
            [re-com.util          :refer [px]]
            [clojure.string]))

(def model (reagent/atom #{:tesla-model-s}))
(def disabled? (reagent/atom false))
(def required? (reagent/atom false))
(def filter-box? (reagent/atom false))
(def regex-filter? (reagent/atom false))

(def choices [{:id :tesla-model-s          :label "Model S"        :group "Tesla"}
              {:id :tesla-model-3          :label "Model 3"        :group "Tesla"}
              {:id :porsche-taycan         :label "Taycan"         :group "Porsche"}
              {:id :renault-zoe            :label "Zoe"            :group "Renault"}
              {:id :kia-e-niro             :label "e-Niro"         :group "Kia"}
              {:id :kia-soul               :label "Soul"           :group "Kia"}
              {:id :vw-id3                 :label "ID.3"           :group "VW"}
              {:id :vw-e-up                :label "e-Up"           :group "VW"}
              {:id :mini-electric          :label "Electric"       :group "Mini"}
              {:id :mustang                :label "Mustang Mach-E" :group "Ford"}
              {:id :ford-phev              :label "Escape PHEV"    :group "Ford"}
              {:id :volvo-xc40             :label "XC40 Recharge"  :group "Volvo"}])

(defn panel
  []
  [v-box
   :src      (src-coordinates)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[multi-select ... ]"
                            "src/re_com/multi_select.cljs"
                            "src/re_demo/multi_select.cljs"]

              [h-box
               :src      (src-coordinates)
               :gap      "100px"
               :children [[v-box
                           :src       (src-coordinates)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [new-in-version "v2.12.0"]
                                      [p "A compound component that allows the user to incrementally build up a selection from a list of choices, often a big list."]
                                      [p "Choices and selections can optionally be grouped. Filtering is available for big lists."]
                                      [p "Takes up a lot of screen real estate but can be placed in a popup."]
                                      [args-table multi-select-args-desc]]]
                          [v-box
                           :src      (src-coordinates)
                           :gap      "20px"
                           :width    "450px"
                           :children [[title2 "Demo"]
                                      [multi-select
                                       :width         "450px"
                                       :left-label    "Car Choices"
                                       :right-label   "Cars Selected"
                                       :placeholder   "Select some cars."
                                       :disabled?     disabled?
                                       :required?     required?
                                       :filter-box?   filter-box?
                                       :regex-filter? regex-filter?
                                       :choices       choices
                                       :model         model
                                       :sort-fn       :group
                                       :on-change     #(reset! model %)]
                                      [h-box
                                       :src    (src-coordinates)
                                       :height "90px" ;; means the Component (which is underneath) doesn't move up and down as the model changes
                                       :gap    "5px"
                                       :width  "100%"
                                       :children [[label
                                                   :src   (src-coordinates)
                                                   :label [:code ":model"]]
                                                  [label
                                                   :src   (src-coordinates)
                                                   :label " is currently"]
                                                  [:code
                                                   {:class "display-flex"
                                                    :style {:flex "1"}}
                                                   (with-out-str (pprint/pprint @model))]]]
                                      [gap
                                       :src  (src-coordinates)
                                       :size "0px"]
                                      [v-box
                                       :src   (src-coordinates)
                                       :gap   "10px"
                                       :style {:min-width        "550px"
                                               :padding          "15px"
                                               :border-top       "1px solid #DDD"
                                               :background-color "#f7f7f7"}
                                       :children [[title3 "Interactive Parameters" {:margin-top "0"}]
                                                  [h-box
                                                   :src      (src-coordinates)
                                                   :gap      "30px"
                                                   :children [[v-box
                                                               :gap "3px"
                                                               :children [[checkbox
                                                                           :src       (src-coordinates)
                                                                           :label     [box
                                                                                       :align :start
                                                                                       :child [:code ":disabled?"]]
                                                                           :model     disabled?
                                                                           :on-change #(reset! disabled? %)]
                                                                          [checkbox
                                                                           :src       (src-coordinates)
                                                                           :label     [box
                                                                                       :align :start
                                                                                       :child [:code ":required?"]]
                                                                           :model     required?
                                                                           :on-change #(reset! required? %)]]]
                                                              [v-box
                                                               :gap "3px"
                                                               :children [[checkbox
                                                                           :src       (src-coordinates)
                                                                           :label     [box
                                                                                       :align :start
                                                                                       :child [:code ":filter-box?"]]
                                                                           :model     filter-box?
                                                                           :on-change #(reset! filter-box? %)]
                                                                          (when @filter-box?
                                                                            [checkbox
                                                                             :src       (src-coordinates)
                                                                             :label     [box
                                                                                         :align :start
                                                                                         :child [:code ":regex-filter?"]]
                                                                             :model     regex-filter?
                                                                             :on-change #(reset! regex-filter? %)])]]]]

                                                  [v-box
                                                   :src      (src-coordinates)
                                                   :size     "initial"
                                                   :gap      "5px"
                                                   :children [[label
                                                               :src   (src-coordinates)
                                                               :label [:code ":choices"]]
                                                              [:pre (with-out-str (pprint/pprint choices))]]]]]]]]]

              [parts-table "multi-select" multi-select-parts-desc]]])


