(ns re-demo.dropdown
  (:require-macros
   [re-com.core     :refer []])
  (:require
   [re-com.core     :refer [at h-box v-box single-dropdown label hyperlink-href p p-span]]
   [re-com.dropdown :refer [dropdown-parts-desc dropdown-args-desc dropdown]]
   [re-demo.utils   :refer [panel-title title2 title3 parts-table args-table status-text prop-slider]]
   [re-com.util :refer [px]]
   [reagent.core    :as    r]))

(def model (r/atom false))

(defn panel*
  []
  (let [width        (r/atom 200)
        height       (r/atom 200)
        min-width    (r/atom 200)
        max-width    (r/atom 200)
        max-height   (r/atom 200)
        min-height   (r/atom 200)
        body-width   (r/atom 200)
        anchor-width (r/atom 200)]
    (fn []
      [v-box :src (at) :size "auto" :gap "10px"
       :children
       [[panel-title "[dropdown ... ]"
         "src/re_com/dropdown.cljs"
         "src/re_demo/dropdown.cljs"]
        [h-box :src (at) :gap "100px"
         :children
         [[v-box :src (at) :gap "10px" :width "450px"
           :children
           [[title2 "Notes"]
            [status-text "Alpha" {:color "red"}]
            [p-span "A generic dropdown component. You pass in your own components for "
             [:code ":anchor"] " and " [:code ":body"] "."]
            [p-span [:code ":dropdown"] " provides: "]
            [:ul
             [:li "state management (" [:span {:style {:color "red"}} "alpha!"] ") for opening and closing"]
             [:li "dynamically positioned container elements"]]
            [args-table dropdown-args-desc]]]
          [v-box :src (at) :width "700px" :gap "10px"
           :children
           [[title2 "Demo"]
            [dropdown
             {:anchor       (fn [{:keys [state]}]
                              [:div "I am " (:openable state) " ;)"])
              #_#_:parts    {:backdrop {:style {:background-color "blue"}}}
              :body         [:div "Hello World!"]
              :model        model
              :width        (some-> @width px)
              :height       (some-> @height px)
              :min-width    (some-> @min-width px)
              :max-width    (some-> @max-width px)
              :max-height   (some-> @max-height px)
              :min-height   (some-> @min-height px)
              :body-width   (some-> @body-width px)
              :anchor-width (some-> @anchor-width px)
              :on-change    #(reset! model %)}]
            [v-box :src (at)
             :gap "10px"
             :style {:min-width        "550px"
                     :padding          "15px"
                     :border-top       "1px solid #DDD"
                     :background-color "#f7f7f7"}
             :children [[title3 "Interactive Parameters" {:margin-top "0"}]
                        [v-box :src (at)
                         :gap "20px"
                         :children [[prop-slider {:prop width :id :width :default 212 :default-on? false}]
                                    [prop-slider {:prop height :id :height :default 212 :default-on? false}]
                                    [prop-slider {:prop min-width :id :min-width :default 212 :default-on? false}]
                                    [prop-slider {:prop max-width :id :max-width :default 212 :default-on? false}]
                                    [prop-slider {:prop min-height :id :min-height :default 212 :default-on? false}]
                                    [prop-slider {:prop max-height :id :max-height :default 212 :default-on? false}]
                                    [prop-slider {:prop body-width :id :body-width :default 212 :default-on? false}]
                                    [prop-slider {:prop anchor-width :id :anchor-width :default 212 :default-on? false}]]]]]]]]]
        [parts-table "dropdown" dropdown-parts-desc]]])))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel*])
