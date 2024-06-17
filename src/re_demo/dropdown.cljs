(ns re-demo.dropdown
  (:require-macros
   [re-com.core     :refer []])
  (:require
   [re-com.core     :refer [at h-box v-box single-dropdown label hyperlink-href p p-span]]
   [re-com.dropdown :refer [dropdown-parts-desc dropdown-args-desc dropdown]]
   [re-demo.utils   :refer [panel-title title2 parts-table args-table status-text]]
   [reagent.core    :as    reagent]))

(def model (reagent/atom false))

(defn panel*
  []
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
           {:anchor     (fn [{:keys [state]}]
                          [:div "I am " (:openable state) " ;)"])
            #_#_:parts      {:backdrop {:style {:background-color "blue"}}}
            :body       [:div "Hello World!"]
            :model      model
            :min-width  "300px"
            :max-height "300px"
            :min-height "200px"
            :on-change  #(reset! model %)}]]]]]
      [parts-table "dropdown" dropdown-parts-desc]]]))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel*])
