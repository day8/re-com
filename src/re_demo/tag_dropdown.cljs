(ns re-demo.tag-dropdown
  (:require [cljs.pprint          :as pprint]
            [clojure.string       :as string]
            [reagent.core         :as reagent]
            [re-com.core          :refer [h-box box checkbox gap v-box tag-dropdown hyperlink-href p label line]]
            [re-com.slider        :refer [slider]]
            [re-com.tag-dropdown  :refer [tag-dropdown-parts-desc tag-dropdown-args-desc]]
            [re-demo.utils        :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util          :refer [px]]))

(def choices [{:id :bug           :description "Something isn't working"                    :label "bug"           :background-color "#fc2a29"}
              {:id :documentation :description "Improvements or additions to documentation" :label "documentation" :background-color "#0052cc"}
              {:id :duplicate     :description "This issue or pull request already exists"  :label "duplicate"     :background-color "#cccccc"}
              {:id :enhancement   :description "New feature or request"                     :label "enhancement"   :background-color "#84b6eb"}
              {:id :help          :description "Extra attention is needed"                  :label "help"          :background-color "#169819"}
              {:id :invalid       :description "This doesn't seem right"                    :label "invalid"       :background-color "#e6e6e6"}
              {:id :wontfix       :description "This will not be worked on"                 :label "wontfix"       :background-color "#eb6421"}])

(defn demo
  []
  (let [model             (reagent/atom #{:documentation})
        disabled?         (reagent/atom false)
        required?         (reagent/atom false)
        unselect-buttons? (reagent/atom false)
        placeholder?      (reagent/atom false)
        abbrev-fn?        (reagent/atom false)
        abbrev-threshold? (reagent/atom false)
        abbrev-threshold  (reagent/atom 13)
        min-width?        (reagent/atom true)
        min-width         (reagent/atom 200)
        max-width?        (reagent/atom true)
        max-width         (reagent/atom 300)]
    (fn []
      [v-box
       :gap      "11px"
       :width    "450px"
       :align    :start
       :children [[title2 "Demo"]
                  [tag-dropdown
                   :min-width         (when @min-width? (str @min-width "px"))
                   :max-width         (when @max-width? (str @max-width "px"))
                   :disabled?         disabled?
                   :required?         required?
                   :placeholder       (when @placeholder? "placeholder message")
                   :unselect-buttons? unselect-buttons?
                   :choices           choices
                   :model             model
                   :abbrev-fn         (when @abbrev-fn? #(string/upper-case (first (:label %))))
                   :abbrev-threshold  (when @abbrev-threshold? abbrev-threshold)
                   :on-change         #(reset! model %)]
                  [h-box
                   :height   "45px"
                   :gap      "5px"
                   :width    "100%"
                   :children [[label :label [:code ":model"]]
                              [label :label " is currently"]
                              [:code
                               {:class "display-flex"
                                :style {:flex "1"}}
                               (with-out-str (pprint/pprint @model))]]]
                  [title3 "Parameters"]
                  [v-box
                   :gap "20px"
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
                                           :child [:code ":unselect-buttons?"]]
                               :model     unselect-buttons?
                               :on-change #(reset! unselect-buttons? %)]
                              [checkbox
                               :label     [box
                                           :align :start
                                           :child [:span "Supply the string \"placeholder message\" for the " [:code ":placeholder"] " parameter"]]
                               :model     placeholder?
                               :on-change #(reset! placeholder? %)]
                              [v-box
                               :gap      "11px"
                               :children [[checkbox
                                           :label     [box
                                                       :align :start
                                                       :child [:span "Supply an " [:code ":abbrev-fn"] " of " [:code "#(clojure.string/upper-case (first (:label %)))"]]]
                                           :model     abbrev-fn?
                                           :on-change #(reset! abbrev-fn? %)]
                                          (when @abbrev-fn?
                                            [h-box
                                             :gap      "5px"
                                             :align    :center
                                             :children [[checkbox
                                                         :label     [box
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
                                                        [label :label @abbrev-threshold]]])]]
                              [h-box
                               :align    :center
                               :children [[checkbox
                                           :label     [box
                                                       :align :start
                                                       :child [:code ":min-width"]]
                                           :model     min-width?
                                           :on-change #(reset! min-width? %)]
                                          [gap :size "5px"]
                                          (when @min-width?
                                            [:<>
                                             [slider
                                              :model     min-width
                                              :on-change #(reset! min-width %)
                                              :min       50
                                              :max       400
                                              :step      1
                                              :width     "300px"]
                                             [gap :size "5px"]
                                             [label :label (str @min-width "px")]])]]
                              [h-box
                               :align    :center
                               :children [[checkbox
                                           :label     [box
                                                       :align :start
                                                       :child [:code ":max-width"]]
                                           :model     max-width?
                                           :on-change #(reset! max-width? %)]
                                          [gap :size "5px"]
                                          (when @max-width?
                                            [:<>
                                             [slider
                                              :model     max-width
                                              :on-change #(reset! max-width %)
                                              :min       50
                                              :max       400
                                              :step      1
                                              :width     "300px"]
                                             [gap :size "5px"]
                                             [label :label (str @max-width "px")]])]]]]
                  #_[gap :size "5px"]

                  [gap :size "10px"]]])))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[tag-dropdown ... ]"
                            "src/re_com/tag_dropdown.cljs"
                            "src/re_demo/tag_dropdown.cljs"
                            "v2.12.0"]

              [h-box
               :gap      "100px"
               :children [[v-box
                            :gap      "10px"
                            :width    "450px"
                            :children [[title2 "Notes"]
                                       [status-text "Alpha" {:color "red" :font-weight "bold"}]
                                       [p "A multi-select component. Useful when the list of choices is small and (optionally) colour coded, and where those selected need to all be visible to the user."]
                                       [p "If the user selects many of the choices, then displaying them horizontally can take more than " [:code ":width"] ". In this case, the programmer has two strategies:"]
                                       [:ol
                                        [:li  "allow the Component to grow horizontally to some limit by providing " [:code ":max-width"]]
                                        [:li  "allow the Component to switch from using \"name\" to using \"abrreviations\", see " [:code ":abbrev-fn"] " and  "[:code ":abbrev-threshold"]]]
                                       [args-table tag-dropdown-args-desc]]]
                          [demo]]]
              [parts-table "tag-dropdown" tag-dropdown-parts-desc]]])


