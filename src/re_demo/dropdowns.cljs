(ns re-demo.dropdowns
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button spinner progress-bar label input-text]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-drop-down find-option]]
            [re-com.modal    :refer [modal-window
                                     cancel-button
                                     looper
                                     domino-process]]
            [cljs.core.async :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core    :as    reagent]))

(def bold-uk (reagent/atom true))

(defn test-button
  []
  [button
   :label "Algeria"
   :on-click #(
               reset! bold-uk (not @bold-uk))
   ])


(def countries [{:id "AU" :label "Australia"              :group "POPULAR COUNTRIES"}
                {:id "US" :label "United States"          :group "POPULAR COUNTRIES"}
                {:id "GB" :label (if @bold-uk [:strong "United Kingdom"] "Old Blighty!")
                                                          :group "POPULAR COUNTRIES"}
                {:id "AF" :label "Afghanistan"            :group "'A' COUNTRIES"}
                {:id "AB" :label "Albania"                :group "'A' COUNTRIES"}
                ;{:id "AG" :label [test-button]            :group "'A' COUNTRIES"}
                {:id "AG" :label "Algeria"                :group "'A' COUNTRIES"}
                {:id 06   :label "American Samoa"         :group "'A' COUNTRIES"}
                {:id 07   :label "Andorra"                :group "'A' COUNTRIES"}
                {:id true :label "Angola"                 :group "'A' COUNTRIES"}
                {:id [4]  :label "Anguilla"               :group "'A' COUNTRIES"}
                {:id "00" :label "Antarctica"             :group "'A' COUNTRIES"}
                {:id "11" :label "Antigua and Barbuda"    :group "'A' COUNTRIES"}
                {:id "12" :label "Argentina"              :group "'A' COUNTRIES"}
                {:id "13" :label "Armenia"                :group "'A' COUNTRIES"}
                {:id "14" :label "Aruba"                  :group "'A' COUNTRIES"}
                {:id "16" :label "Austria"                :group "'A' COUNTRIES"}
                {:id "17" :label "Azerbaijan"             :group "'A' COUNTRIES"}
                {:id "18" :label "Bahamas"                :group "'B' COUNTRIES"}
                {:id "19" :label "Bahrain"                :group "'B' COUNTRIES"}
                {:id "20" :label "Bangladesh"             :group "'B' COUNTRIES"}
                {:id "21" :label "Barbados"               :group "'B' COUNTRIES"}
                {:id "22" :label "Belarus"                :group "'B' COUNTRIES"}
                {:id "23" :label "Belgium"                :group "'B' COUNTRIES"}
                {:id "24" :label "Belize"                 :group "'B' COUNTRIES"}
                {:id "25" :label "Benin"                  :group "'B' COUNTRIES"}
                {:id "26" :label "Bermuda"                :group "'B' COUNTRIES"}
                {:id "27" :label "Bhutan"                 :group "'B' COUNTRIES"}
                {:id "28" :label "Bolivia"                :group "'B' COUNTRIES"}
                {:id "29" :label "Bosnia and Herzegovina" :group "'B' COUNTRIES"}
                {:id "30" :label "Botswana"               :group "'B' COUNTRIES"}
                {:id "31" :label "Bouvet Island"          :group "'B' COUNTRIES"}
                {:id "32" :label "Brazil"                 :group "'B' COUNTRIES"}
                {:id "34" :label "Brunei Darussalam"      :group "'B' COUNTRIES"}
                {:id "35" :label "Bulgaria"               :group "'B' COUNTRIES"}
                {:id "36" :label "Burkina Faso"           :group "'B' COUNTRIES"}
                {:id "37" :label "Burundi"                :group "'B' COUNTRIES"}])


(defn panel
  []
  (let [selected-country-id (reagent/atom "US")]
    (fn [] [v-box
            :children [[:h3.page-header "Dropdowns"]
                       [h-box
                        :gap      "10px"
                        :align    :center
                        :children [[label :label "Test tabbing"]
                                   [input-text "" #() :style {:width "80px"}]
                                   [single-drop-down
                                    :options countries
                                    :model selected-country-id
                                    :placeholder "Choose a country"
                                    :width "300px"
                                    :filter-box  true
                                    :disabled    false
                                    :on-select #(reset! selected-country-id %)]
                                   [input-text "" #() :style {:width "80px"}]
                                   [:div
                                    [:strong "Selected country: "]
                                    (if (nil? @selected-country-id)
                                      "None"
                                      (str (:label (find-option countries @selected-country-id)) " [" @selected-country-id "]"))]
                                   ]]
                       ]]
      )))

