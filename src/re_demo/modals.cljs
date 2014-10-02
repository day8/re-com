(ns re-demo.modals
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button label spinner progress-bar]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-option filter-options-by-keyword]]
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
               [:li "To create an alert component, the following parameter is required:"
                [:ul
                 [:li.spacer [:strong ":alert-type"] " - a Bootstrap CSS string determining the style. Either \"info\", \"warning\" or \"danger\"."]]]
               [:li "The rest of the parameters are optional:"
                [:ul
                 [:li.spacer [:strong ":id"] " - A unique identifier, usually an integer or string. This is optional for single alerts. It's main use is in alert-list component."]
                 [:li.spacer "Note: Although heading and body are optional, you really need to specify at least one of them."]
                 [:li.spacer [:strong ":heading"] " - the heading section (hiccup markup or a string)."]
                 [:li.spacer [:strong ":body"] " - the body of the alert (hiccup markup or a string)."]
                 [:li.spacer [:strong ":padding"] " - the amount of padding within the alert (default is 15px)."]
                 [:li.spacer [:strong ":closeable"] " - A boolean which determines if the close button is rendered (on-close must also be specified)."]
                 [:li.spacer [:strong ":on-close"] " - A callback function which knows how to close the alert."]]]]]])


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
                                                        :options   demos
                                                        :model     selected-demo-id
                                                        :width     "300px"
                                                        :on-select #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2])]]]]]])))
