(ns re-demo.tree-select
  (:require [cljs.pprint          :as pprint]
            [clojure.string       :as string]
            [reagent.core         :as reagent]
            [re-com.core          :refer [at h-box box checkbox gap v-box tree-select tree-select-dropdown p label]]
            [re-com.radio-button  :refer [radio-button]]
            [re-com.slider        :refer [slider]]
            [re-com.tree-select   :refer [tree-select-parts-desc tree-select-dropdown-parts-desc tree-select-dropdown-args-desc tree-select-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table status-text new-in-version prop-slider]]
            [re-com.util          :refer [px]]))

(def cities [{:id :sydney    :label "Sydney" :group [:oceania :australia :nsw]}
             {:id :newcastle    :label "Newcastle" :group [:oceania :australia :nsw]}
             {:id :central-coast    :label "Central Coast" :group [:oceania :australia :nsw]}
             {:id :wollongong    :label "Wollongong" :group [:oceania :australia :nsw]}
             {:id :melbourne :label "Melbourne" :group [:oceania :australia :victoria]}
             {:id :geelong :label "Geelong" :group [:oceania :australia :victoria]}
             {:id :ballarat :label "Ballarat" :group [:oceania :australia :victoria]}
             {:id :christchurch :label "Christchurch" :group [:oceania :new-zealand :canterbury]}
             {:id :auckland :label "Auckland" :group [:oceania :new-zealand]}
             {:id :hamilton :label "Hamilton" :group [:oceania :new-zealand]}
             {:id :wellington :label "Wellington" :group [:oceania :new-zealand :wellington]}
             {:id :lower-hutt :label "Lower Hutt" :group [:oceania :new-zealand :wellington]}
             {:id :atlantis :label "atlantis"}])

(defn demo
  []
  (let [model                   (reagent/atom #{:sydney :auckland})
        groups                  (reagent/atom nil)
        disabled?               (reagent/atom false)
        show-reset-button?      (reagent/atom false)
        initial-expanded-groups (reagent/atom nil)
        label-fn                (reagent/atom nil)
        group-label-fn          (reagent/atom nil)
        choice-disabled-fn      (reagent/atom nil)
        width                   (reagent/atom 212)
        min-width               (reagent/atom 200)
        max-width               (reagent/atom 300)
        min-height              (reagent/atom 100)
        max-height              (reagent/atom 350)
        anchor-width            (reagent/atom 212)
        tree-select*            (fn [& {:as props}]
                                  [tree-select
                                   (->
                                    {:src                (at)
                                     :width              (some-> @width px)
                                     :min-width          (some-> @min-width px)
                                     :max-width          (some-> @max-width px)
                                     :min-height         (some-> @min-height px)
                                     :max-height         (some-> @max-height px)
                                     :attr               {:key (gensym)}
                                     :disabled?          disabled?
                                     :label-fn           @label-fn
                                     :group-label-fn     @group-label-fn
                                     :choice-disabled-fn @choice-disabled-fn
                                     :choices            cities
                                     :model              model
                                     :expanded-groups    groups
                                     :on-change          #(reset! model %1)}
                                    (merge props))])
        open-to-chosen          (fn [] [tree-select* {:initial-expanded-groups :chosen}])
        open-to-nil             (fn [] [tree-select*])
        open-to-all             (fn [] [tree-select* {:initial-expanded-groups :all}])
        open-to-none            (fn [] [tree-select* {:initial-expanded-groups :none}])
        open-to-specified       (fn [] [tree-select* {:initial-expanded-groups #{[:oceania] [:oceania :new-zealand]}}])]
    (fn []
      [v-box :src (at)
       :gap      "11px"
       :width    "450px"
       :align    :start
       :children [[title2 "Demo"]
                  [label :src (at) :label "[tree-select ... ]"]
                  [gap :src (at) :size "5px"]
                  [(case @initial-expanded-groups
                     nil        open-to-nil
                     :chosen    open-to-chosen
                     :all       open-to-all
                     :none      open-to-none
                     :specified open-to-specified)]
                  [gap :src (at) :size "15px"]
                  [label :src (at) :label "[tree-select-dropdown ... ]"]
                  [gap :src (at) :size "5px"]
                  [tree-select-dropdown
                   {:width              (some-> @width px)
                    :min-width          (some-> @min-width px)
                    :max-width          (some-> @max-width px)
                    :min-height         (some-> @min-height px)
                    :max-height         (some-> @max-height px)
                    :anchor-width       (some-> @anchor-width px)
                    :disabled?          disabled?
                    :show-reset-button? @show-reset-button?
                    :label-fn           @label-fn
                    :group-label-fn     @group-label-fn
                    :choice-disabled-fn @choice-disabled-fn
                    :placeholder        "Select a city..."
                    :choices            cities
                    :model              model
                    :expanded-groups    groups
                    :on-change          #(do (reset! model %1) (println %2))}]
                  [gap :src (at) :size "96px"]
                  [h-box :src (at)
                   :height   "45px"
                   :gap      "5px"
                   :width    "100%"
                   :children [[label :src (at) :label [:code ":model"]]
                              [label :src (at) :label " is currently"]
                              [:code
                               {:class "display-flex"
                                :style {:flex "1"}}
                               (with-out-str (pprint/pprint @model))]]]
                  [v-box :src (at)
                   :gap "10px"
                   :style {:min-width        "550px"
                           :padding          "15px"
                           :border-top       "1px solid #DDD"
                           :background-color "#f7f7f7"}
                   :children [[title3 "Interactive Parameters" {:margin-top "0"}]
                              [v-box :src (at)
                               :gap "20px"
                               :children [[checkbox :src (at)
                                           :label     [box :src (at)
                                                       :align :start
                                                       :child [:code ":disabled?"]]
                                           :model     disabled?
                                           :on-change #(reset! disabled? %)]
                                          [checkbox :src (at)
                                           :label     [box :src (at)
                                                       :align :start
                                                       :child [:code ":show-reset-button?"]]
                                           :model     show-reset-button?
                                           :on-change #(reset! show-reset-button? %)]
                                          [v-box :src (at)
                                           :children [[box :src (at) :align :start :child [:code ":initial-expanded-groups"]]

                                                      [radio-button :src (at)
                                                       :label     [:span [:code "nil"] ", ommitted - use the intial value of " [:code "groups"] "."]
                                                       :value     nil
                                                       :model     @initial-expanded-groups
                                                       :on-change #(reset! initial-expanded-groups %)
                                                       :style {:margin-left "20px"}]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code ":chosen"] " - reveal every chosen item."]
                                                       :value     :chosen
                                                       :model     @initial-expanded-groups
                                                       :on-change #(reset! initial-expanded-groups %)
                                                       :style {:margin-left "20px"}]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code ":all"] " - expand all groups"]
                                                       :value     :all
                                                       :model     @initial-expanded-groups
                                                       :on-change #(reset! initial-expanded-groups %)
                                                       :style     {:margin-left "20px"}]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code ":none"] " - collapse all groups"]
                                                       :value     :none
                                                       :model     @initial-expanded-groups
                                                       :on-change #(reset! initial-expanded-groups %)
                                                       :style     {:margin-left "20px"}]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code "#{[:oceania] [:oceania :new-zealand]}"] " - expand specified groups"]
                                                       :value     :specified
                                                       :model     @initial-expanded-groups
                                                       :on-change #(reset! initial-expanded-groups %)
                                                       :style     {:margin-left "20px"}]]]
                                          [v-box :src (at)
                                           :gap      "11px"
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:span "Supply a " [:code ":label-fn"]
                                                                           " of "
                                                                           [:code "#(clojure.string/upper-case (:label %)))"]]]
                                                       :model     label-fn
                                                       :on-change (fn [] (swap! label-fn (fn [x] (if x nil #(clojure.string/upper-case (:label %))))))]
                                                      [checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:span "Supply a " [:code ":group-label-fn"]
                                                                           " of "
                                                                           [:code "#(clojure.string/upper-case (name (last %))))"]]]
                                                       :model     group-label-fn
                                                       :on-change (fn [] (swap! group-label-fn (fn [x] (if x nil #(clojure.string/upper-case (name (last (:group %))))))))]
                                                      [checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:span "Supply a " [:code ":choice-disabled-fn"]
                                                                           " of "
                                                                           [:code "#(contains? (set (:group %)) :australia)"]]]
                                                       :model     choice-disabled-fn
                                                       :on-change (fn [] (swap! choice-disabled-fn
                                                                                (fn [x]
                                                                                  (if x nil #(contains? (set (:group %)) :australia)))))]]]
                                          [prop-slider {:prop width :id :width :default 212 :default-on? true}]
                                          [prop-slider {:prop min-width :id :min-width :default 212 :default-on? false}]
                                          [prop-slider {:prop max-width :id :max-width :default 212 :default-on? false}]
                                          [prop-slider {:prop min-height :id :min-height :default 212 :default-on? false}]
                                          [prop-slider {:prop max-height :id :max-height :default 212 :default-on? false}]
                                          [prop-slider {:prop anchor-width :id :anchor-width :default 212 :default-on? false}]]]]]
                  [gap :src (at) :size "10px"]]])))

(defn panel []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[tree-select ... ]"
               "src/re_com/tree_select.cljs"
               "src/re_demo/tree_select.cljs"]

              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [new-in-version "v2.16.0"]
                                      [p "A tree-select component. Choices take the same form as those for a selection list or multi-select, except " [:code ":group"]
                                       "can be a vector. If so, the choice appears within a hierarchy of expandable groups."]
                                      [p [:code "tree-select-dropdown"]
                                       "includes an anchor label. By default, it lists all the selected choices, except if an entire group is selected, then that group appears in place of its descendants."]
                                      [args-table tree-select-args-desc {:title [:span "Parameters: " [:code "[:tree-select]"]]}]
                                      [args-table (remove (set tree-select-args-desc) tree-select-dropdown-args-desc)
                                       {:title [:span "Extra Parameters: " [:code "[:tree-select-dropdown]"]]}]]]
                          [demo]]]
              [parts-table "tree-select" tree-select-parts-desc :title [:span "Parts: " [:code "[:tree-select]"]]]
              [parts-table "tree-select-dropdown" tree-select-dropdown-parts-desc :title [:span "Parts: " [:code "[:tree-select-dropdown]"]]]]])
