(ns re-demo.tree-select
  (:require [cljs.pprint          :as pprint]
            [clojure.string       :as string]
            [reagent.core         :as reagent]
            [re-com.core          :refer [at h-box box checkbox gap v-box tree-select tag-dropdown hyperlink-href p label line]]
            [re-com.radio-button  :refer [radio-button]]
            [re-com.slider        :refer [slider]]
            [re-com.tree-select   :refer [tree-select-parts-desc tree-select-args-desc]]
            [re-com.tag-dropdown  :refer [tag-dropdown-parts-desc tag-dropdown-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text new-in-version]]
            [re-com.util          :refer [px]]))

(def cities [{:id :sydney    :label "Sydney" :group [:oceania :australia :nsw]}
             {:id :newcastle    :label "Newcastle" :group [:oceania :australia :nsw]}
             {:id :melbourne :label "Melbourne" :group [:oceania :australia :victoria]}
             {:id :christchurch :label "Christchurch" :group [:oceania :new-zealand :canterbury]}
             {:id :auckland :label "Auckland" :group [:oceania :new-zealand]}
             {:id :wellington :label "Wellington" :group [:oceania :new-zealand]}
             {:id :atlantis :label "atlantis"}])

(def choices [{:id :bug           :description "Something isn't working"                    :label "bug"           :background-color "#fc2a29" :group [:x :national :metro]}
              {:id :documentation :description "Improvements or additions to documentation" :label "documentation" :background-color "#0052cc"}
              {:id :duplicate     :description "This issue or pull request already exists"  :label "duplicate"     :background-color "#cccccc"}
              {:id :enhancement   :description "New feature or request"                     :label "enhancement"   :background-color "#84b6eb"}
              {:id :help          :description "Extra attention is needed"                  :label "help"          :background-color "#169819"}
              {:id :invalid       :description "This doesn't seem right"                    :label "invalid"       :background-color "#e6e6e6"}
              {:id :wontfix       :description "This will not be worked on"                 :label "wontfix"       :background-color "#eb6421"}])

(defn demo
  []
  (let [model             (reagent/atom #{:sydney :auckland})
        groups            (reagent/atom nil)
        disabled?         (reagent/atom false)
        open-to           (reagent/atom :chosen)
        placeholder?      (reagent/atom false)
        abbrev-fn?        (reagent/atom false)
        abbrev-threshold? (reagent/atom false)
        abbrev-threshold  (reagent/atom 13)
        min-width?        (reagent/atom true)
        min-width         (reagent/atom 200)
        max-width?        (reagent/atom true)
        max-width         (reagent/atom 300)]
    (fn []
      [v-box :src (at)
       :gap      "11px"
       :width    "450px"
       :align    :start
       :children [[title2 "Demo"]
                  [tree-select :src (at)
                   :min-width         (when @min-width? (str @min-width "px"))
                   :max-width         (when @max-width? (str @max-width "px"))
                   :disabled?         disabled?
                   :open-to           @open-to
                   :placeholder       (when @placeholder? "placeholder message")
                   :choices           cities
                   :model             model
                   :groups            groups
                   :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                   :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                   :on-change         #(reset! model %)]
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
                                          [checkbox :src (at)
                                           :label     [box :src (at)
                                                       :align :start
                                                       :child [:span "Supply the string \"placeholder message\" for the " [:code ":placeholder"] " parameter"]]
                                           :model     placeholder?
                                           :on-change #(reset! placeholder? %)]
                                          [v-box :src (at)
                                           :children [[box :src (at) :align :start :child [:code ":open-to"]]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code ":chosen"] ", " [:code "nil"] ", ommitted - reveal every chosen item."]
                                                       :value     :chosen
                                                       :model     @open-to
                                                       :on-change #(reset! open-to %)
                                                       :style {:margin-left "20px"}]
                                                      [radio-button :src (at)
                                                       :label     [:span [:code ":all"] " - expand all groups"]
                                                       :value     :all
                                                       :model     @open-to
                                                       :on-change #(reset! open-to %)
                                                       :style     {:margin-left "20px"}]]]
                                          [v-box :src (at)
                                           :gap      "11px"
                                           :children [[checkbox :src (at)
                                                       :label     [box :src (at)
                                                                   :align :start
                                                                   :child [:span "Supply an " [:code ":abbrev-fn"] " of " [:code "#(clojure.string/upper-case (first (:label %)))"]]]
                                                       :model     abbrev-fn?
                                                       :on-change #(reset! abbrev-fn? %)]
                                                      (when @abbrev-fn?
                                                        [h-box :src (at)
                                                         :gap      "5px"
                                                         :align    :center
                                                         :children [[checkbox :src (at)
                                                                     :label     [box :src (at)
                                                                                 :align :start
                                                                                 :child [:span " and also supply an " [:code ":abbrev-threshold"] " of "]]
                                                                     :model     abbrev-threshold?
                                                                     :on-change #(reset! abbrev-threshold? %)]
                                                                    [slider
                                                                     :model     abbrev-threshold
                                                                     :on-change #(reset! abbrev-threshold %)
                                                                     :min       10
                                                                     :max       50
                                                                     :step      1
                                                                     :width     "160px"]
                                                                    [label :src (at) :label @abbrev-threshold]]])]]
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
                                                         [label :src (at) :label (str @max-width "px")]])]]]]]]
                  #_[gap :src (at) :size "5px"]

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
                                      [p "A multi-select component. Useful when the list of choices is small and (optionally) colour coded, and where those selected need to all be visible to the user."]
                                      [p "If the user selects many of the choices, then displaying them horizontally can take more than " [:code ":width"] ". In this case, the programmer has two strategies:"]
                                      [:ol
                                       [:li  "allow the Component to grow horizontally to some limit by providing " [:code ":max-width"]]
                                       [:li  "allow the Component to switch from using \"name\" to using \"abrreviations\", see " [:code ":abbrev-fn"] " and  " [:code ":abbrev-threshold"]]]
                                      [args-table tree-select-args-desc]]]
                          [demo]]]
              [parts-table "tag-dropdown" tree-select-parts-desc]]])