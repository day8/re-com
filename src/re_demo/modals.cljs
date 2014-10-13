(ns re-demo.modals
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button label spinner progress-bar]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-choice filter-choices-by-keyword]]
            [re-com.modal    :refer [modal-window cancel-button looper domino-process]]
            [cljs.core.async :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core    :as    reagent]))


(def demos [{:id 1 :label "Basic example"}
            {:id 2 :label "Other variations"}])


(defn demo1
  []
  [:span "*** TODO ***"])


(defn demo2
  []
  [:span "*** TODO ***"])


(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "General notes"]
              [:ul
               [:li "*** TODO ***"]]]])


(defn panel
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[:h3.page-header "Tour"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   demos
                                                        :model     selected-demo-id
                                                        :width     "300px"
                                                        :on-change #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2])]]]]]])))
