(ns re-demo.dropdowns
  (:require-macros
    [re-com.debug    :refer [src-coordinates]])
  (:require
    [re-com.core     :refer [h-box v-box box gap single-dropdown input-text checkbox label title hyperlink-href p p-span line]]
    [re-com.dropdown :refer [filter-choices-by-keyword single-dropdown-parts-desc single-dropdown-args-desc]]
    [re-com.util     :refer [item-for-id]]
    [re-demo.utils   :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
    [re-com.util     :refer [px]]
    [reagent.core    :as    reagent]))


(def demos [{:id 1  :label "Simple dropdown"}
            {:id 2  :label "Dropdown with grouping"}
            {:id 3  :label "Dropdown with filtering"}
            ;{:id 4 :label "Use of :id-fn etc."} ;; for testing
            {:id 5  :label "Keyboard support"}
            {:id 6  :label "Other parameters"}
            {:id 7  :label "Two dependent dropdowns"}
            {:id 8  :label "Custom markup"}
            {:id 9  :label "Async choices load"}
            {:id 10 :label "Drop above"}
            {:id 11 :label "Free text"}])


(def countries [{:id "au" :label "Australia"}
                {:id "us" :label "United States"}
                {:id "uk" :label "United Kingdom"}
                {:id "ca" :label "Canada"}
                {:id "nz" :label "New Zealand"}])


(def cities [{:id "01" :label "Sydney"       :country-id "au"}
             {:id "02" :label "Melbourne"    :country-id "au"}
             {:id "03" :label "Brisbane"     :country-id "au"}
             {:id "04" :label "Adelaide"     :country-id "au"}
             {:id "05" :label "Perth"        :country-id "au"}
             {:id "06" :label "Canberra"     :country-id "au"}
             {:id "07" :label "Hobart"       :country-id "au"}
             {:id "08" :label "Darwin"       :country-id "au"}
             {:id "09" :label "New York"     :country-id "us"}
             {:id "10" :label "Los Angeles"  :country-id "us"}
             {:id "11" :label "Dallas"       :country-id "us"}
             {:id "12" :label "Washington"   :country-id "us"}
             {:id "13" :label "Orlando"      :country-id "us"}
             {:id "14" :label "London"       :country-id "uk"}
             {:id "15" :label "Manchester"   :country-id "uk"}
             {:id "16" :label "Glasgow"      :country-id "uk"}
             {:id "17" :label "Brighton"     :country-id "uk"}
             {:id "18" :label "Birmingham"   :country-id "uk"}
             {:id "19" :label "Toronto"      :country-id "ca"}
             {:id "20" :label "Montreal"     :country-id "ca"}
             {:id "21" :label "Calgary"      :country-id "ca"}
             {:id "22" :label "Ottawa"       :country-id "ca"}
             {:id "23" :label "Edmonton"     :country-id "ca"}
             {:id "24" :label "Auckland"     :country-id "nz"}
             {:id "25" :label "Wellington"   :country-id "nz"}
             {:id "26" :label "Christchurch" :country-id "nz"}
             {:id "27" :label "Hamilton"     :country-id "nz"}
             {:id "28" :label "Dunedin"      :country-id "nz"}])



(def grouped-countries [{:id "AU" :label "Australia"                :group "EN Speakers"}
                        {:id "US" :label "United States"            :group "EN Speakers"}
                        {:id "GB" :label "United Kingdom"           :group "EN Speakers"}
                        {:id "E1" :label "Iraq"                     :group "Updated Axis Of Evil"}
                        {:id "E2" :label "New Zealand"              :group "Updated Axis Of Evil"}
                        {:id "E3" :label "Iran"                     :group "Updated Axis Of Evil"}
                        {:id "E4" :label "North Korea"              :group "Updated Axis Of Evil"}
                        {:id "03" :label "Afghanistan"              :group "'A' COUNTRIES"}
                        {:id "04" :label "Albania"                  :group "'A' COUNTRIES"}
                        {:id "05" :label "Algeria"                  :group "'A' COUNTRIES"}
                        {:id "06" :label "American Samoa"           :group "'A' COUNTRIES"}
                        {:id "07" :label "Andorra"                  :group "'A' COUNTRIES"}
                        {:id "08" :label "Angola"                   :group "'A' COUNTRIES"}
                        {:id "09" :label "Anguilla"                 :group "'A' COUNTRIES"}
                        {:id "10" :label "Antarctica"               :group "'A' COUNTRIES"}
                        {:id "11" :label "Antigua and Barbuda"      :group "'A' COUNTRIES"}
                        {:id "12" :label "Argentina"                :group "'A' COUNTRIES"}
                        {:id "13" :label "Armenia"                  :group "'A' COUNTRIES"}
                        {:id "14" :label "Aruba"                    :group "'A' COUNTRIES"}
                        {:id "16" :label "Austria"                  :group "'A' COUNTRIES"}
                        {:id "17" :label "Azerbaijan"               :group "'A' COUNTRIES"}
                        {:id "18" :label "Bahamas"                  :group "'B' COUNTRIES"}
                        {:id "19" :label "Bahrain"                  :group "'B' COUNTRIES"}
                        {:id "20" :label "Bangladesh"               :group "'B' COUNTRIES"}
                        {:id "21" :label "Barbados"                 :group "'B' COUNTRIES"}
                        {:id "22" :label "Belarus"                  :group "'B' COUNTRIES"}
                        {:id "23" :label "Belgium"                  :group "'B' COUNTRIES"}
                        {:id "24" :label "Belize"                   :group "'B' COUNTRIES"}
                        {:id "25" :label "Benin"                    :group "'B' COUNTRIES"}
                        {:id "26" :label "Bermuda"                  :group "'B' COUNTRIES"}
                        {:id "27" :label "Bhutan"                   :group "'B' COUNTRIES"}
                        {:id "28" :label "Bolivia"                  :group "'B' COUNTRIES"}
                        {:id "29" :label "Bosnia and Herzegovina"   :group "'B' COUNTRIES"}
                        {:id "30" :label "Botswana"                 :group "'B' COUNTRIES"}
                        {:id "31" :label "Bouvet Island"            :group "'B' COUNTRIES"}
                        {:id "32" :label "Brazil"                   :group "'B' COUNTRIES"}
                        {:id "34" :label "Brunei Darussalam"        :group "'B' COUNTRIES"}
                        {:id "35" :label "Bulgaria"                 :group "'B' COUNTRIES"}
                        {:id "36" :label "Burkina Faso"             :group "'B' COUNTRIES"}
                        {:id "37" :label "Burundi"                  :group "'B' COUNTRIES"}])


(def grouped-countries-2 [{:code "AU" :country "Australia"     :region "EN Speakers"}
                          {:code "US" :country "United States" :region "EN Speakers"}
                          {:code "E1" :country "Iraq"          :region "Updated Axis Of Evil"}
                          {:code "E2" :country "New Zealand"   :region "Updated Axis Of Evil"}
                          {:code "03" :country "Afghanistan"   :region "'A' COUNTRIES"}
                          {:code "04" :country "Albania"       :region "'A' COUNTRIES"}
                          {:code "18" :country "Bahamas"       :region "'B' COUNTRIES"}
                          {:code "19" :country "Bahrain"       :region "'B' COUNTRIES"}])

(defn simple-demo
  []
  [v-box
   :gap "10px"
   :children [[p "The dropdown above is the simple case."]
              [p "It presents a list of choices and allows one to be selected, via mouse or keyboard."]]])


(defn grouping-demo
  []
  (let [selected-country-id (reagent/atom nil)]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src         (src-coordinates)
                               :choices     grouped-countries
                               :model       selected-country-id
                               :title?      true
                               :placeholder "Choose a country"
                               :width       "300px"
                               :max-height  "400px"
                               :filter-box? false
                               :on-change   #(reset! selected-country-id %)]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]]]
                  [p "The dropdown above shows how related choices can be displayed in groups. In this case, several country related groups. e.g. 'EN COUNTRIES'."]
                  [p "This feature is triggered if any choice has a " [:code ":group"] " attribute. Typically all choices will have a " [:code ":group"] " or none will. It's up to you to ensure that choices with the same " [:code ":group"] " are adjacent in the vector."]
                  [p "Because :model is initially nil, the " [:code ":placeholder"] " text is initially displayed."]
                  [p [:code ":max-width"] " is set here to make the dropdown taller."]]])))



(defn filtering-demo
  []
  (let [selected-country-id (reagent/atom "US")]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src         (src-coordinates)
                               :choices     grouped-countries
                               :model       selected-country-id
                               :width       "300px"
                               :max-height  "400px"
                               :filter-box? true
                               :on-change   #(reset! selected-country-id %)]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]]]
                  [p "The dropdown above adds a filter text box to the dropdown section which is convenient for when there are many choices."]
                  [p "The filter text is searched for in both the :group and the :label values. If the text matches the " [:code ":group"] ", then all
                      choices under that group are considered to be 'matched'."]
                  [p "The initial model value has been set to 'US'."]]])))



(defn id-fn-demo
  []
  (let [id-fn               #(str (:code %) "$")
        label-fn            #(str (:country %) "!")
        group-fn            #(str "[" (:region %) "]")
        selected-country-id (reagent/atom (id-fn {:code "US"}))]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[p "This example is the same as the previous one except the list is shorter and the following parameters have been added to use different keywords for the data and transform the values provided:"]
                  [p [:code ":id-fn"] " is set to " [:code "#(str (:code %) \"$\")"]]
                  [p [:code ":label-fn"] " is set to " [:code "#(str (:country %) \"!\")"]]
                  [p [:code ":group-fn"] " is set to " [:code "#(str \"[\" (:region %) \"]\")"]]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src         (src-coordinates)
                               :choices     grouped-countries-2
                               :model       selected-country-id
                               :width       "300px"
                               :max-height  "400px"
                               :filter-box? true
                               :id-fn       id-fn
                               :label-fn    label-fn
                               :group-fn    group-fn
                               :on-change   #(reset! selected-country-id %)]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (label-fn (item-for-id @selected-country-id grouped-countries-2 :id-fn id-fn)) " [" @selected-country-id "]"))]]]]])))


(defn keyboard-demo
  []
  (let [selected-country-id   (reagent/atom "US")
        selected-country-id-1 (reagent/atom "AU")
        selected-country-id-2 (reagent/atom "E2")
        selected-country-id-3 (reagent/atom "GB")]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[p "[single-dropdown ...] supports tab key navigation."]
                  [p "The " [:code ":tab-index"] " parameter specifies position in the tab order (default is 0),
                       or it can be removed from the tab order using a value of -1."]
                  [p "Up-arrow and Down-arrow do sensible things."]
                  [p "Home and End keys move to the beginning and end of the list."]
                  [p "Enter, Tab and Shift+Tab trigger selection of the currently highlighted choice."]
                  [p "Esc closes the dropdown without making a selection."]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[label
                               :src   (src-coordinates)
                               :label "Test tabbing"]
                              [input-text
                               :src       (src-coordinates)
                               :model     ""
                               :on-change #()
                               :width     "80px"]
                              [single-dropdown
                               :src         (src-coordinates)
                               :choices     grouped-countries
                               :model       selected-country-id
                               :width       "200px"
                               :filter-box? true
                               :on-change   #(reset! selected-country-id %)]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]]]
                  [gap
                   :src  (src-coordinates)
                   :size "10px"]
                  [p "All components on this page have " [:code ":tab-index"] " set to the default (0) except the ones below.
                      Keep pressing the Tab key and note how the focus cycles through the components."]
                  [h-box
                   :src      (src-coordinates)
                   :align    :center
                   :gap      "10px"
                   :children [[label
                               :src   (src-coordinates)
                               :label [:span [:code ":tab-index"] " is set to 3"]]
                              [single-dropdown
                               :src           (src-coordinates)
                               :choices       grouped-countries
                               :model         selected-country-id-1
                               :tab-index     3
                               :width         "200px"
                               :filter-box?   true
                               :on-change     #(reset! selected-country-id-1 %)]]]
                  [h-box
                   :src      (src-coordinates)
                   :align    :center
                   :gap      "10px"
                   :children [[label
                               :src   (src-coordinates)
                               :label [:span [:code ":tab-index"] " is set to 2"]]
                              [single-dropdown
                               :src           (src-coordinates)
                               :choices       grouped-countries
                               :model         selected-country-id-2
                               :tab-index     2
                               :width         "200px"
                               :filter-box?   true
                               :on-change     #(reset! selected-country-id-2 %)]]]
                  [h-box
                   :src      (src-coordinates)
                   :align    :center
                   :gap      "10px"
                   :children [[label
                               :src   (src-coordinates)
                               :label [:span [:code ":tab-index"] " is set to 1"]]
                              [single-dropdown
                               :src           (src-coordinates)
                               :choices       grouped-countries
                               :model         selected-country-id-3
                               :tab-index     1
                               :width         "200px"
                               :filter-box?   true
                               :on-change     #(reset! selected-country-id-3 %)]]]]])))


(defn other-params-demo
  []
  (let [selected-country-id (reagent/atom "US")
        change-count        (reagent/atom 0)
        drop-count          (reagent/atom 0)
        disabled?           (reagent/atom false)
        regex?              (reagent/atom false)
        width?              (reagent/atom false)
        tooltip?            (reagent/atom false)
        tooltip-position?   (reagent/atom false)
        enter-drop?         (reagent/atom true)
        cancelable?         (reagent/atom true)
        filter-placeholder? (reagent/atom false)
        just-drop?          (reagent/atom false)
        repeat-change?      (reagent/atom false)
        i18n?               (reagent/atom false)
        on-drop?            (reagent/atom false)
        dropdown-width      "300px"]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [^{:key (str @just-drop? @on-drop?)}
                              [single-dropdown
                               :src                (src-coordinates)
                               :choices            grouped-countries
                               :model              selected-country-id
                               :disabled?          @disabled?
                               :filter-box?        true
                               :regex-filter?      @regex?
                               :width              (when @width? dropdown-width)
                               :tooltip            (when @tooltip? "An example tooltip")
                               :tooltip-position   (when @tooltip-position? :right-center)
                               :enter-drop?        @enter-drop?
                               :cancelable?        @cancelable?
                               :filter-placeholder (when @filter-placeholder? "An example filter placeholder")
                               :just-drop?         @just-drop?
                               :repeat-change?     @repeat-change?
                               :i18n               (when @i18n? {:no-results-match "Brak wyników odpowiadających \"%s\""})
                               :on-change          #(do
                                                      (swap! change-count inc)
                                                      (reset! selected-country-id %))
                               :on-drop            (when @on-drop?
                                                     #(swap! drop-count inc))]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]]]
                  [v-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :style    {:min-width        "150px"
                              :padding          "15px"
                              :border-top       "1px solid #DDD"
                              :background-color "#f7f7f7"}
                   :children [[title :level :level3 :label "Interactive Parameters" :style {:margin-top "0"}]
                              [p "Experiment with the checkboxes below to understand the effect of other parameters."]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":disabled?"]]
                                           :model       disabled?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! disabled? %)]
                                          [:span (str @disabled? " - " (if @disabled?
                                                                         "the dropdown is locked and cannot be changed."
                                                                         "the dropdown is enabled and a choice can be selected."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":regex-filter?"]]
                                           :model       regex?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! regex? %)]
                                          [:span (str @regex? " - " (if @regex?
                                                                      "the filter text box supports JavaScript regular expressions."
                                                                      "the filter text box supports plain text filtering only."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":width"]]
                                           :model       width?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! width? %)]
                                          [:span (str (if @width?
                                                        (str "\"" dropdown-width "\" - the dropdown is fixed to this width.")
                                                        "not specified - the dropdown takes up all available width."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src      (src-coordinates)
                                                         :align :start :child [:code ":tooltip"]]
                                           :model       tooltip?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! tooltip? %)]
                                          [:span (if @tooltip?
                                                   "hover over the dropdown to see the tooltip."
                                                   "no tooltip specified.")]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":tooltip-position"]]
                                           :model       tooltip-position?
                                           :label-style {:width "165px"}
                                           :disabled?   (not @tooltip?)
                                           :on-change   #(reset! tooltip-position? %)]
                                          [:span (cond
                                                   (not @tooltip?)    "does not apply."
                                                   @tooltip-position? "the tooltip will appear on the right hand side of the dropdown."
                                                   :else              "not specified - the tooltip will appear below the dropdown.")]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":enter-drop?"]]
                                           :model       enter-drop?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! enter-drop? %)]
                                          [:span (str @enter-drop? " - " (if @enter-drop?
                                                                           "pressing Enter (while focused) displays the dropdown part."
                                                                           "pressing Enter (while focused) does not display the dropdown part."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :start
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":cancelable?"]]
                                           :model       cancelable?
                                           :label-style {:width "165px"}
                                           :on-change #(reset! cancelable? %)]
                                          [:span (str @cancelable? " - " (if @cancelable?
                                                                           "selection made with arrow keys can be cancelled by pressing Esc or clicking outside the dropdown part."
                                                                           "selection made with arrow keys is immediately applied and thus cannot be cancelled."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":filter-placeholder"]]
                                           :model       filter-placeholder?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! filter-placeholder? %)]
                                          [:span (str (if @filter-placeholder?
                                                        "a placeholder text will be displayed when the filter is empty."
                                                        "not specified - no placeholder text will be displayed in the filter textbox."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":just-drop?"]]
                                           :model       just-drop?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! just-drop? %)]
                                          [:span (str @just-drop? " - " (if @just-drop?
                                                                          "just the dropdown part display."
                                                                          "normal display."))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":repeat-change?"]]
                                           :model       repeat-change?
                                           :label-style {:width "165px"}
                                           :on-change #(reset! repeat-change? %)]
                                          [:span (str @repeat-change? " - " (if @repeat-change?
                                                                              (str "the change count of " @change-count " will increase if an item is re-selected.")
                                                                              (str "the change count of " @change-count " will not increase if an item is re-selected.")))]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":i18n"]]
                                           :model       i18n?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! i18n? %)]
                                          [:span (if @i18n?
                                                   "type e.g. \"1\" in the filter box to see the \"No results match\" message translated."
                                                   "no internationalization applied.")]]]
                              [h-box
                               :src      (src-coordinates)
                               :align    :center
                               :children [[checkbox
                                           :src         (src-coordinates)
                                           :label       [box
                                                         :src   (src-coordinates)
                                                         :align :start
                                                         :child [:code ":on-drop?"]]
                                           :model       on-drop?
                                           :label-style {:width "165px"}
                                           :on-change   #(reset! on-drop? %)]
                                          [:span (str @on-drop? " - " (if @on-drop?
                                                                        (str "the drop count of " @drop-count " will increase on the dropdown part display.")
                                                                        (str "the drop count of " @drop-count " will not increase on the dropdown part display.")))]]]
                              [gap
                               :src  (src-coordinates)
                               :size "10px"]]]]])))



(defn two-dependent-demo
  []
  (let [selected-country-id (reagent/atom nil)
        filtered-cities     (reagent/atom [])
        selected-city-id    (reagent/atom nil)]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[p "Two dropdowns can be tied together in a parent-child relationship. In this case, countries and their cities."]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src       (src-coordinates)
                               :choices   countries
                               :model     selected-country-id
                               :width     "300px"
                               :on-change #(do
                                            (reset! selected-country-id %)
                                            (reset! filtered-cities (vec (filter-choices-by-keyword cities :country-id @selected-country-id)))
                                            (reset! selected-city-id nil))]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id countries)) " [" @selected-country-id "]"))]]]
                  [gap
                   :src  (src-coordinates)
                   :size "10px"]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src       (src-coordinates)
                               :choices   filtered-cities
                               :model     selected-city-id
                               :width     "300px"
                               :on-change #(reset! selected-city-id %)]
                              [:div
                               [:strong "Selected city: "]
                               (if (nil? @selected-city-id)
                                 "None"
                                 (str (:label (item-for-id @selected-city-id cities)) " [" @selected-city-id "]"))]]]]])))

(defn custom-markup-demo
  []
  (let [selected-country-id (reagent/atom nil)]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src         (src-coordinates)
                               :choices     countries
                               :render-fn   (fn [choice] [:div [:span (:label choice)]
                                                               [:span {:style {:float "right"}} "\u2691"]])
                               :model       selected-country-id
                               :placeholder "Choose a country"
                               :width       "300px"
                               :max-height  "400px"
                               :filter-box? true
                               :on-change   #(reset! selected-country-id %)]
                              [:div
                               [:strong "Selected country: "]
                               (if (nil? @selected-country-id)
                                 "None"
                                 (str (:label (item-for-id @selected-country-id grouped-countries)) " [" @selected-country-id "]"))]]]
                  [p "Dropdowns choices can be built with arbitrary markup using the " [:code ":render-fn"] " attribute. When filtering, only the text from the label will be considered."]]])))

(defn async-load-demo
  []
  (let [selected-country-id (reagent/atom nil)
        selected-city-id (reagent/atom nil)
        selected-country-id2 (reagent/atom nil)
        filter-countries (fn [filter-text]
                           (filter #(not= -1 (.indexOf (:label %) filter-text)) countries))
        filter-citites (fn [country-id filter-text]
                         (filter #(and (= (:country-id %) country-id)
                                       (not= -1 (.indexOf (:label %) filter-text))) cities))]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[p "You may pass " [:code "(fn [opts done fail] ...)"] " to :choices attribute to asynchronously load data.
                      When data is loaded callback either (done result) of (fail error) should be called."]
                  [p "Dropdown uses initial callback. This way we don't require managing callbacks and
                      allow passing inline callback. If callback will change (e.g. dependent dropdown) - :key may be used."]
                  [label
                   :src   (src-coordinates)
                   :label "Result after a second:"]
                  [single-dropdown
                   :src         (src-coordinates)
                   :choices     (fn [{:keys [filter-text]} done fail]
                                  (js/setTimeout
                                    (fn []
                                      (done (filter-countries filter-text)))
                                    1000))
                   :placeholder "Choose country"
                   :model       selected-country-id
                   :filter-box? true
                   :width       "300px"
                   :max-height  "400px"
                   :on-change   #(reset! selected-country-id %)]
                  [label
                   :src   (src-coordinates)
                   :label "Dependent dropdown:"]
                  [:div {:key @selected-country-id}
                   [single-dropdown
                    :src         (src-coordinates)
                    :choices     (fn [{:keys [filter-text]} done fail]
                                   (js/setTimeout
                                     (fn []
                                       (done (filter-citites @selected-country-id filter-text)))
                                     1000))
                    :placeholder "Choose city"
                    :model       selected-city-id
                    :filter-box? true
                    :width       "300px"
                    :max-height  "400px"
                    :on-change   #(reset! selected-city-id %)]]
                  [label
                   :src   (src-coordinates)
                   :label "With error:"]
                  [single-dropdown
                   :src         (src-coordinates)
                   :choices     (fn [{:keys [filter-text]} done fail]
                                  (js/setTimeout
                                    #(if (= "please" filter-text)
                                       (done countries)
                                       (fail "Server error"))
                                    1000))
                   :placeholder "Type 'please' to get results"
                   :model       @selected-country-id2
                   :filter-box? true
                   :width       "300px"
                   :max-height  "400px"
                   :on-change   #(reset! selected-country-id2 %)]]])))

(defn drop-above-demo
  []
  (let [selected-city-id (reagent/atom nil)]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "100px"
       :children [[h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [[single-dropdown
                               :src             (src-coordinates)
                               :can-drop-above? true
                               :choices         cities
                               :model           selected-city-id
                               :placeholder     "Choose a city"
                               :width           "300px"
                               :max-height      "500px"
                               :on-change       #(reset! selected-city-id %)]
                              [:div
                               [:strong "Selected city: "]
                               (if (nil? @selected-city-id)
                                 "None"
                                 (str (:label (item-for-id @selected-city-id cities)) " [" @selected-city-id "]"))]]]
                  [p "When the " [:code ":can-drop-above?"] " attribute is set the dropdown part will be displayed above the top part if the space below it is too small."]
                  [p "If items have non-standard height, the " [:code ":est-item-height"] "can be set to help guess where to display the dropdown part. This guess will be verified on the actual render so it is non-essential but can help eliminate an unnecessary redraw."]]])))


(defn free-text-demo
  []
  (let [disabled?      (reagent/atom false)
        selected-city  (reagent/atom nil)
        selected-city2 (reagent/atom nil)
        auto-complete? (reagent/atom false)
        capitalize?    (reagent/atom false)
        f              (fn [a]
                         [single-dropdown
                          :src             (src-coordinates)
                          :free-text?      true
                          :auto-complete?  @auto-complete?
                          :capitalize?     @capitalize?
                          :disabled?       @disabled?
                          :choices         (->> cities (mapv :label) sort)
                          :model           a
                          :placeholder     (str "Choose/type a city" (when @auto-complete? " or enter a first few letters"))
                          :width           "350px"
                          :on-change       #(reset! a %)])]
    (fn []
      [v-box
       :src      (src-coordinates)
       :gap      "10px"
       :children [[p "Allow user a free text input by setting the " [:code ":free-text?"] " attribute."]
                  [p "Additional options:"]
                  [h-box
                   :src      (src-coordinates)
                   :align    :center
                   :children [[checkbox
                               :src         (src-coordinates)
                               :label       [box
                                             :src   (src-coordinates)
                                             :align :start
                                             :child [:code ":disabled?"]]
                               :model       disabled?
                               :label-style {:width "165px"}
                               :on-change   #(reset! disabled? %)]
                              [:span (str @disabled? " - " (if @disabled?
                                                             "the dropdown is locked and cannot be changed."
                                                             "the dropdown is enabled and a choice can be selected."))]]]
                  [checkbox
                   :src         (src-coordinates)
                   :label       [box
                                 :src   (src-coordinates)
                                 :align :start
                                 :child [:code ":auto-complete?"]]
                   :model       auto-complete?
                   :label-style {:width "165px"}
                   :on-change   #(reset! auto-complete? %)]
                  [checkbox
                   :src         (src-coordinates)
                   :label       [box :align :start :child [:code ":capitalize?"]]
                   :model       capitalize?
                   :label-style {:width "165px"}
                   :on-change   #(reset! capitalize? %)]
                  [gap
                   :src  (src-coordinates)
                   :size "10px"]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [(f selected-city)
                              [:div
                               [:strong "City: "]
                               (if (seq @selected-city)
                                 @selected-city
                                 "None")]]]
                  [gap
                   :src  (src-coordinates)
                   :size "50px"]
                  [line
                   :src      (src-coordinates)]

                  [p "If the filter box is enabled, the filter text can be reused as the actual text entry. To try this, type e.g. \"1\" in the filter box and press Enter."]
                  [:pre ":filter-box?   true\n:set-to-filter #{:on-enter-press :on-no-results-match-click}"]
                  [gap
                   :src  (src-coordinates)
                   :size "10px"]
                  [h-box
                   :src      (src-coordinates)
                   :gap      "10px"
                   :align    :center
                   :children [(conj (f selected-city2)
                                :filter-box? true
                                :set-to-filter #{:on-enter-press :on-no-results-match-click})
                              [:div
                               [:strong "City: "]
                               (if (seq @selected-city2)
                                 @selected-city2
                                 "None")]]]]])))

(defonce selected-demo-id (reagent/atom 1))

(defn panel2
  []
  (fn []
    [v-box
     :src      (src-coordinates)
     :size     "auto"
     :gap      "10px"
     :children [[panel-title "[single-dropdown ... ]"
                              "src/re_com/dropdown.cljs"
                              "src/re_demo/dropdowns.cljs"]
                [h-box
                 :src      (src-coordinates)
                 :gap      "100px"
                 :children [[v-box
                             :src      (src-coordinates)
                             :gap      "10px"
                             :width    "450px"
                             :children [[title2 "Notes"]
                                        [status-text "Stable"]
                                        [p-span
                                          "A dropdown selection component, similar to "
                                          [hyperlink-href
                                           :label  "Chosen"
                                           :href   "http://harvesthq.github.io/chosen"
                                           :target "_blank"]
                                          ", styled using "
                                          [hyperlink-href
                                           :label  "Bootstrap"
                                           :href   "https://github.com/alxlit/bootstrap-chosen"
                                           :target "_blank"]
                                          "."]
                                        [p "Note: Single selection only."]
                                        [args-table single-dropdown-args-desc]]]
                            [v-box
                             :src      (src-coordinates)
                             :width     "700px"
                             :gap       "10px"
                             :children  [[title2 "Demo"]
                                         [h-box
                                          :src      (src-coordinates)
                                          :gap      "10px"
                                          :align    :center
                                          :children [[label
                                                      :src   (src-coordinates)
                                                      :label "Select a demo"]
                                                     [single-dropdown
                                                      :src        (src-coordinates)
                                                      :choices    demos
                                                      :model      selected-demo-id
                                                      :width      "300px"
                                                      :max-height "300px"
                                                      :on-change  #(reset! selected-demo-id %)]]]
                                         [gap
                                          :src  (src-coordinates)
                                          :size "0px"] ;; Force a bit more space here
                                         (case @selected-demo-id
                                           1  [simple-demo]
                                           2  [grouping-demo]
                                           3  [filtering-demo]
                                           4  [id-fn-demo] ;; for testing - uncomment equivalent line in demos vector above
                                           5  [keyboard-demo]
                                           6  [other-params-demo]
                                           7  [two-dependent-demo]
                                           8  [custom-markup-demo]
                                           9  [async-load-demo]
                                           10 [drop-above-demo]
                                           11 [free-text-demo])]]]]
                [parts-table "single-dropdown" single-dropdown-parts-desc]]]))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [panel2])
