(ns re-demo.dropdowns
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button label input-text checkbox]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown find-option]]
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
   :on-click #(reset! bold-uk (not @bold-uk))])


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
  (let [selected-country-id1 (reagent/atom "32")
        selected-country-id2 (reagent/atom nil)
        disabled?            (reagent/atom false)
        regex?               (reagent/atom false)]
    (fn [] [v-box
            :children [[:h3.page-header "Single Dropdowns"]
                       [h-box
                        :gap "50px"
                        :children [[v-box
                                    :width "400px"
                                    :children [[:div.h4 "Notes:"]
                                               [:ul
                                                [:li "The top dropdown has the initial model value set but no text filtering.
                                                      Max height set to 500px."]
                                                [:li "The bottom dropdown has initial value set to nil (nothing selected) and has text
                                                      filtering enabled. No max height set (defaults to 240px)."]]]]
                                   [v-box
                                    :gap "40px"
                                    :children [[h-box
                                                :gap      "10px"
                                                :align    :center
                                                :children [[label :label "Test tabbing"]
                                                           [input-text "" #() :style {:width "80px"}]
                                                           [single-dropdown
                                                            :options      countries
                                                            :model        selected-country-id1
                                                            :width        "300px"
                                                            :max-height   "500px"
                                                            :disabled     false
                                                            :filter-box   false
                                                            :regex-filter false
                                                            :on-select    #(reset! selected-country-id1 %)]
                                                           [:div
                                                            [:strong "Selected country: "]
                                                            (if (nil? @selected-country-id1)
                                                              "None"
                                                              (str (:label (find-option countries @selected-country-id1)) " [" @selected-country-id1 "]"))]]]
                                               [h-box
                                                :gap      "10px"
                                                :align    :center
                                                :children [[label :label "Test tabbing"]
                                                           [input-text "" #() :style {:width "160px"}]
                                                           [single-dropdown
                                                            :options      countries
                                                            :model        selected-country-id2
                                                            :placeholder  "Choose a country"
                                                            :width        "300px"
                                                            :disabled     @disabled?
                                                            :filter-box   true
                                                            :regex-filter @regex?
                                                            :on-select    #(reset! selected-country-id2 %)]
                                                           [:div
                                                            [:strong "Selected country: "]
                                                            (if (nil? @selected-country-id2)
                                                              "None"
                                                              (str (:label (find-option countries @selected-country-id2)) " [" @selected-country-id2 "]"))]]]
                                               [h-box
                                                :gap      "20px"
                                                :align    :center
                                                :children [[label :label "Options for above dropdown: "]
                                                           [checkbox
                                                            :label "Disabled"
                                                            :model  disabled?
                                                            :on-change  #(reset! disabled? %)]
                                                           [checkbox
                                                            :label "Allow regular expressions in filters"
                                                            :model  regex?
                                                            :on-change  #(reset! regex? %)]]]]]]]]])))
