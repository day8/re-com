(ns re-demo.tree-select
  (:require [cljs.pprint          :as pprint]
            [clojure.string       :as string]
            [reagent.core         :as reagent]
            [re-com.core          :refer [at h-box box checkbox gap v-box tree-select tree-select-dropdown hyperlink-href p label line]]
            [re-com.radio-button  :refer [radio-button]]
            [re-com.slider        :refer [slider]]
            [re-com.tree-select   :refer [tree-select-parts-desc tree-select-args-desc]]
            [re-com.tag-dropdown  :refer [tag-dropdown-parts-desc tag-dropdown-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text new-in-version]]
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
  (let [model             (reagent/atom #{:sydney :auckland})
        groups            (reagent/atom nil)
        disabled?         (reagent/atom false)
        initial-expanded-groups           (reagent/atom nil)
        label-fn          (reagent/atom nil)
        group-label-fn    (reagent/atom nil)
        placeholder?      (reagent/atom false)
        abbrev-fn?        (reagent/atom false)
        abbrev-threshold? (reagent/atom false)
        abbrev-threshold  (reagent/atom 13)
        min-width?        (reagent/atom true)
        min-width         (reagent/atom 200)
        max-width?        (reagent/atom true)
        max-width         (reagent/atom 300)
        min-height?       (reagent/atom true)
        min-height        (reagent/atom 100)
        max-height?       (reagent/atom true)
        max-height        (reagent/atom 350)
        open-to-chosen (fn []
                         [tree-select :src (at)
                          :min-width         (when @min-width? (str @min-width "px"))
                          :max-width         (when @max-width? (str @max-width "px"))
                          :min-height        (when @min-height? (str @min-height "px"))
                          :max-height        (when @max-height? (str @max-height "px"))
                          :disabled?         disabled?
                          :label-fn          @label-fn
                          :group-label-fn    @group-label-fn
                          :initial-expanded-groups :chosen
                          :choices           cities
                          :model             model
                          :groups            groups
                          :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                          :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                          :on-change         #(reset! model %1)])
        open-to-nil (fn []
                      [tree-select :src (at)
                       :min-width         (when @min-width? (str @min-width "px"))
                       :max-width         (when @max-width? (str @max-width "px"))
                       :min-height        (when @min-height? (str @min-height "px"))
                       :max-height        (when @max-height? (str @max-height "px"))
                       :disabled?         disabled?
                       :label-fn          @label-fn
                       :group-label-fn    @group-label-fn
                       :choices           cities
                       :model             model
                       :groups            groups
                       :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                       :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                       :on-change         #(reset! model %1)])
        open-to-all (fn []
                      [tree-select :src (at)
                       :min-width         (when @min-width? (str @min-width "px"))
                       :max-width         (when @max-width? (str @max-width "px"))
                       :min-height        (when @min-height? (str @min-height "px"))
                       :max-height        (when @max-height? (str @max-height "px"))
                       :disabled?         disabled?
                       :label-fn          @label-fn
                       :group-label-fn    @group-label-fn
                       :initial-expanded-groups           :all
                       :choices           cities
                       :model             model
                       :groups            groups
                       :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                       :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                       :on-change         #(reset! model %1)])
        open-to-none (fn []
                       [tree-select :src (at)
                        :min-width         (when @min-width? (str @min-width "px"))
                        :max-width         (when @max-width? (str @max-width "px"))
                        :min-height        (when @min-height? (str @min-height "px"))
                        :max-height        (when @max-height? (str @max-height "px"))
                        :disabled?         disabled?
                        :label-fn          @label-fn
                        :group-label-fn    @group-label-fn
                        :initial-expanded-groups           :none
                        :choices           cities
                        :model             model
                        :groups            groups
                        :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                        :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                        :on-change         #(reset! model %1)])
        open-to-specified (fn []
                            [tree-select :src (at)
                             :min-width         (when @min-width? (str @min-width "px"))
                             :max-width         (when @max-width? (str @max-width "px"))
                             :min-height        (when @min-height? (str @min-height "px"))
                             :max-height        (when @max-height? (str @max-height "px"))
                             :disabled?         disabled?
                             :label-fn          @label-fn
                             :group-label-fn    @group-label-fn
                             :initial-expanded-groups #{[:oceania] [:oceania :new-zealand]}
                             :choices           cities
                             :model             model
                             :groups            groups
                             :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                             :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                             :on-change         #(reset! model %1)])]
    (fn []
      [v-box :src (at)
       :gap      "11px"
       :width    "450px"
       :align    :start
       :children [[title2 "Demo"]
                  [label :src (at) :label "[tree-select ... ]"]
                  [gap :src (at) :size "5px"]
                  [(case @initial-expanded-groups
                     nil open-to-nil
                     :chosen open-to-chosen
                     :all open-to-all
                     :none open-to-none
                     :specified open-to-specified)]
                  [gap :src (at) :size "15px"]
                  [label :src (at) :label "[tree-select-dropdown ... ]"]
                  [gap :src (at) :size "5px"]
                  [tree-select-dropdown
                   :min-width         (when @min-width? (str @min-width "px"))
                   :max-width         (when @max-width? (str @max-width "px"))
                   :min-height        (when @min-height? (str @min-height "px"))
                   :max-height        (when @max-height? (str @max-height "px"))
                   :disabled?         disabled?
                   :label-fn          @label-fn
                   :group-label-fn    @group-label-fn
                   :open-to           :chosen
                   :placeholder       "Select a city..."
                   :choices           cities
                   :model             model
                   :groups            groups
                   :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                   :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                   :on-change         #(do (reset! model %1) (println %2))]
                  [gap :src (at) :size "5px"]
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
                  [h-box :src (at)
                   :height   "45px"
                   :gap      "5px"
                   :width    "100%"
                   :children [[label :src (at) :label [:code ":groups"]]
                              [label :src (at) :label " is currently"]
                              [:code
                               {:class "display-flex"
                                :style {:flex "1"}}
                               (with-out-str (pprint/pprint @groups))]]]
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
                                          [v-box :src (at)
                                           :children [[box :src (at) :align :start :child [:code ":open-to"]]

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
                                                       :on-change (fn [] (swap! group-label-fn (fn [x] (if x nil #(clojure.string/upper-case (name (last (:group %))))))))]]]
                                          [h-box :src (at)
                                           :align    :center
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:code ":min-width"]]
                                                       :model     min-width?
                                                       :on-change #(reset! min-width? %)]
                                                      [gap :src (at) :size "5px"]
                                                      (when @min-width?
                                                        [:<>
                                                         [slider
                                                          :model     min-width
                                                          :on-change #(reset! min-width %)
                                                          :min       50
                                                          :max       400
                                                          :step      1
                                                          :width     "300px"]
                                                         [gap :src (at) :size "5px"]
                                                         [label :src (at) :label (str @min-width "px")]])]]
                                          [h-box :src (at)
                                           :align    :center
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:code ":max-width"]]
                                                       :model     max-width?
                                                       :on-change #(reset! max-width? %)]
                                                      [gap :src (at) :size "5px"]
                                                      (when @max-width?
                                                        [:<>
                                                         [slider
                                                          :model     max-width
                                                          :on-change #(reset! max-width %)
                                                          :min       50
                                                          :max       400
                                                          :step      1
                                                          :width     "300px"]
                                                         [gap :src (at) :size "5px"]
                                                         [label :src (at) :label (str @max-width "px")]])]]
                                          [h-box :src (at)
                                           :align    :center
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:code ":min-height"]]
                                                       :model     min-height?
                                                       :on-change #(reset! min-height? %)]
                                                      [gap :src (at) :size "5px"]
                                                      (when @min-height?
                                                        [:<>
                                                         [slider
                                                          :model     min-height
                                                          :on-change #(reset! min-height %)
                                                          :min       50
                                                          :max       400
                                                          :step      1
                                                          :width     "300px"]
                                                         [gap :src (at) :size "5px"]
                                                         [label :src (at) :label (str @min-height "px")]])]]
                                          [h-box :src (at)
                                           :align    :center
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:code ":max-height"]]
                                                       :model     max-height?
                                                       :on-change #(reset! max-height? %)]
                                                      [gap :src (at) :size "5px"]
                                                      (when @max-height?
                                                        [:<>
                                                         [slider
                                                          :model     max-height
                                                          :on-change #(reset! max-height %)
                                                          :min       50
                                                          :max       400
                                                          :step      1
                                                          :width     "300px"]
                                                         [gap :src (at) :size "5px"]
                                                         [label :src (at) :label (str @max-height "px")]])]]]]]]
                  [gap :src (at) :size "10px"]]])))

(defn panel []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[tree-select ... ]"
               "src/re_com/tree-select.cljs"
               "src/re_demo/tree-select.cljs"]

              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                      [new-in-version "v2.13.0"]
                                      [p "A tree-select component. Choices take the same form as those for a selection list or multi-select, except " [:code ":group"]
                                       "can be a vector. If so, the choice appears within a hierarchy of expandable groups."]
                                      [p [:code "tree-select-dropdown"]
                                       "includes an anchor label. By default, it lists all the selected choices, except if an entire group is selected, then that group appears in place of its descendants. "
                                       ". In this case, the programmer has two strategies:"]
                                      [:ol
                                       [:li  "allow the Component to grow horizontally to some limit by providing " [:code ":max-width"]]
                                       [:li  "allow the Component to switch from using \"name\" to using \"abrreviations\", see " [:code ":abbrev-fn"] " and  " [:code ":abbrev-threshold"]]]
                                      [args-table tree-select-args-desc]]]
                          [demo]]]
              [parts-table "tag-dropdown" tree-select-parts-desc]]])
