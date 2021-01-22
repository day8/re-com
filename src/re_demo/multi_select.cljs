(ns re-demo.multi-select
  (:require [cljs.pprint          :as pprint]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box box checkbox gap v-box multi-select hyperlink-href p label]]
            [re-com.multi-select  :refer [multi-select-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.util          :refer [px]]))

(defn multi-select-component-hierarchy
  []
  (let [indent          20
        table-style     {:style {:border "2px solid lightgrey" :margin-right "10px"}}
        border          {:border "1px solid lightgrey" :padding "6px 12px"}
        border-style    {:style border}
        border-style-nw {:style (merge border {:white-space "nowrap"})}
        valign          {:vertical-align "top"}
        valign-style    {:style valign}
        valign-style-hd {:style (merge valign {:background-color "#e8e8e8"})}
        indent-text     (fn [level text] [:span {:style {:padding-left (px (* level indent))}} text])
        highlight-text  (fn [text & [color]] [:span {:style {:font-weight "bold" :color (or color "dodgerblue")}} text])
        code-text       (fn [text] [:span {:style {:font-size "smaller" :line-height "150%"}} " " [:code {:style {:white-space "nowrap"}} text]])]
    [v-box
     :gap      "10px"
     :children [[title2 "Parts"]
                [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                    Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                [:pre "[multi-select\n"
                      "   ...\n"
                      "   :parts {:left {:class \"blah\"\n"
                      "                  :style { ... }\n"
                      "                  :attr  { ... }}}]"]
                [title3 "Part Hierarchy"]
                [:table table-style
                 [:thead valign-style-hd
                  [:tr
                   [:th border-style-nw "Part"]
                   [:th border-style-nw "CSS Class"]
                   [:th border-style-nw "Keyword"]
                   [:th border-style "Notes"]]]
                 [:tbody valign-style
                  [:tr
                   [:td border-style-nw (indent-text 0 "[multi-select]")]
                   [:td border-style-nw "rc-multi-select"]
                   [:td border-style-nw "Use " (code-text ":class") ", " (code-text ":style") " or " (code-text ":attr") " arguments instead."]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 1 "[h-box]")]
                   [:td border-style-nw "rc-multi-select-container"]
                   [:td border-style-nw (code-text ":container")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-multi-select-left"]
                   [:td border-style-nw (code-text ":left")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[h-box]")]
                   [:td border-style-nw "rc-multi-select-left-label-container"]
                   [:td border-style-nw (code-text ":left-label-container")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:span]")]
                   [:td border-style-nw "rc-multi-select-left-label"]
                   [:td border-style-nw (code-text ":left-label")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 5 ":left-label")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:span]")]
                   [:td border-style-nw "rc-multi-select-left-label-item-count"]
                   [:td border-style-nw (code-text ":left-label-item-count")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[list-box]")]
                   [:td border-style-nw "rc-multi-select-left-list-box"]
                   [:td border-style-nw (code-text ":left-list-box")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[h-box]")]
                   [:td border-style-nw "rc-multi-select-filter-text-box"]
                   [:td border-style-nw (code-text ":filter-text-box")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[input-text]")]
                   [:td border-style-nw "rc-multi-select-filter-input-text"]
                   [:td border-style-nw (code-text ":filter-input-text")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[close-button]")]
                   [:td border-style-nw "rc-multi-select-filter-reset-button"]
                   [:td border-style-nw (code-text ":filter-reset-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[label]")]
                   [:td border-style-nw "rc-multi-select-left-filter-result-count"]
                   [:td border-style-nw (code-text ":left-filter-result-count")]
                   [:td border-style ""]]

                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-multi-select-middle-container"]
                   [:td border-style-nw (code-text ":middle-container")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-multi-select-middle-top-spacer"]
                   [:td border-style-nw (code-text ":middle-top-spacer")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[v-box]")]
                   [:td border-style-nw "rc-multi-select-middle"]
                   [:td border-style-nw (code-text ":middle")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[button]")]
                   [:td border-style-nw "rc-multi-select-include-all-button"]
                   [:td border-style-nw (code-text ":include-all-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[button]")]
                   [:td border-style-nw "rc-multi-select-include-selected-button"]
                   [:td border-style-nw (code-text ":include-selected-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[button]")]
                   [:td border-style-nw "rc-multi-select-exclude-selected-button"]
                   [:td border-style-nw (code-text ":exclude-selected-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[button]")]
                   [:td border-style-nw "rc-multi-select-exclude-all-button"]
                   [:td border-style-nw (code-text ":exclude-all-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[box]")]
                   [:td border-style-nw "rc-multi-select-middle-bottom-spacer"]
                   [:td border-style-nw (code-text ":middle-bottom-spacer")]
                   [:td border-style ""]]

                  [:tr
                   [:td border-style-nw (indent-text 2 "[v-box]")]
                   [:td border-style-nw "rc-multi-select-right"]
                   [:td border-style-nw (code-text ":right")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[label]")]
                   [:td border-style-nw "rc-multi-select-warning-message"]
                   [:td border-style-nw (code-text ":warning-message")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[h-box]")]
                   [:td border-style-nw "rc-multi-select-right-label-container"]
                   [:td border-style-nw (code-text ":right-label-container")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:span]")]
                   [:td border-style-nw "rc-multi-select-right-label"]
                   [:td border-style-nw (code-text ":right-label")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 5 ":right-label arg")]
                   [:td border-style-nw ""]
                   [:td border-style-nw ""]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[:span]")]
                   [:td border-style-nw "rc-multi-select-right-label-item-count"]
                   [:td border-style-nw (code-text ":right-label-item-count")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[list-box]")]
                   [:td border-style-nw "rc-multi-select-right-list-box"]
                   [:td border-style-nw (code-text ":right-list-box")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[h-box]")]
                   [:td border-style-nw "rc-multi-select-filter-text-box"]
                   [:td border-style-nw (code-text ":filter-text-box")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[input-text]")]
                   [:td border-style-nw "rc-multi-select-filter-input-text"]
                   [:td border-style-nw (code-text ":filter-input-text")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 4 "[close-button]")]
                   [:td border-style-nw "rc-multi-select-filter-reset-button"]
                   [:td border-style-nw (code-text ":filter-reset-button")]
                   [:td border-style ""]]
                  [:tr
                   [:td border-style-nw (indent-text 3 "[label]")]
                   [:td border-style-nw "rc-multi-select-right-filter-result-count"]
                   [:td border-style-nw (code-text ":right-filter-result-count")]
                   [:td border-style ""]]]]]]))

(def model (reagent/atom #{:tesla-model-s}))
(def disabled? (reagent/atom false))
(def required? (reagent/atom false))
(def filter-box? (reagent/atom false))
(def regex-filter? (reagent/atom false))

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
                                       [p "A compound component that allows the user to incrementally build up a selection from a list of choices, often a big list."]
                                       [p "Choices and selections can optionally be grouped. Filtering is available for big lists."]
                                       [p "Takes up a lot of screen real estate but can be placed in a popup."]
                                       [args-table multi-select-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Demo"]
                                      [title3 "Parameters"]
                                      [h-box
                                       :children [[checkbox
                                                   :label     [box
                                                               :align :start
                                                               :child [:code ":disabled?"]]
                                                   :model     disabled?
                                                   :on-change #(reset! disabled? %)]
                                                  [checkbox
                                                   :label     [box
                                                               :align :start
                                                               :child [:code ":required?"]]
                                                   :model     required?
                                                   :on-change #(reset! required? %)]
                                                  [checkbox
                                                   :label     [box
                                                               :align :start
                                                               :child [:code ":filter-box?"]]
                                                   :model     filter-box?
                                                   :on-change #(reset! filter-box? %)]
                                                  [checkbox
                                                   :label     [box
                                                               :align :start
                                                               :child [:code ":regex-filter?"]]
                                                   :model     regex-filter?
                                                   :on-change #(reset! regex-filter? %)]]]

                                      [h-box
                                       :children [[label :label [:code ":model"]]
                                                  [:code (with-out-str (pprint/pprint @model))]]]
                                      [multi-select
                                       :width         "450px"
                                       :left-label    "Car Choices"
                                       :right-label   "Cars Selected"
                                       :placeholder   "Select some cars."
                                       :disabled?     disabled?
                                       :required?     required?
                                       :filter-box?   filter-box?
                                       :regex-filter? regex-filter?
                                       :choices       [{:id :tesla-model-s          :label "Model S"        :group "Tesla"}
                                                       {:id :tesla-model-3          :label "Model 3"        :group "Tesla"}
                                                       {:id :porsche-taycan         :label "Taycan"         :group "Porsche"}
                                                       {:id :renault-zoe            :label "Zoe"            :group "Renault"}
                                                       {:id :kia-e-niro             :label "e-Niro"         :group "Kia"}
                                                       {:id :kia-soul               :label "Soul"           :group "Kia"}
                                                       {:id :vw-id3                 :label "ID.3"           :group "VW"}
                                                       {:id :vw-e-up                :label "e-Up"           :group "VW"}
                                                       {:id :mini-electric          :label "Electric"       :group "Mini"}
                                                       {:id :ford-mustang-mach-e    :label "Mustang Mach-E" :group "Ford"}
                                                       {:id :ford-phev              :label "Escape PHEV"    :group "Ford"}
                                                       {:id :volvo-xc40-recharge    :label "XC40 Recharge"  :group "Volvo"}
                                                       {:id :hyundai-ioniq-electric :label "Ioniq Electric" :group "Hyundai"}]
                                       :model         model
                                       :sort-fn       :group
                                       :on-change     #(reset! model %)]]]]]
              [multi-select-component-hierarchy]]])


