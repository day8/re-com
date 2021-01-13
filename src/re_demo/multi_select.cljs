(ns re-demo.multi-select
  (:require [cljs.pprint          :as pprint]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box gap v-box multi-select hyperlink-href p label]]
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
                                       [status-text "Alpha" {:color "red" :font-weight "bold"}]
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
                                       :left-label  "foo"
                                       :right-label "bar"
                                       :choices     [{:id :tesla-model-s  :label "Model S" :group "Tesla"}
                                                     {:id :tesla-model-3  :label "Model 3" :group "Tesla"}
                                                     {:id :porsche-taycan :label "Taycan"  :group "Porsche"}
                                                     {:id :renault-zoe    :label "Zoe"     :group "Renault"}
                                                     {:id :kia-e-niro     :label "e-Niro"  :group "Kia"}
                                                     {:id :kia-soul       :label "Soul"    :group "Kia"}]
                                       :model       model
                                       :sort-fn     :group
                                       :filter-box? true
                                       :on-change   #(reset! model %)]]]]]
              [multi-select-component-hierarchy]]])


