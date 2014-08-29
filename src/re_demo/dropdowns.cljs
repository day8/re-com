(ns re-demo.dropdowns
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util              :as    util]
            [re-com.core              :refer [button spinner progress-bar]]
            [re-com.dropdown          :refer [single-drop-down]]
            [re-com.modal             :refer [modal-window
                                              cancel-button
                                              looper
                                              domino-process]]
            [cljs.core.async          :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core             :as    reagent]))

(def bold-uk (reagent/atom true))

(defn test-button
  []
  [button
   :label "Algeria"
   :on-click #(
               reset! bold-uk (not @bold-uk))
   ])


(def countries [{:value "15" :label "Australia"             }
                {:value "01" :label "United States"         }
                {:value "02" :label (if @bold-uk [:strong "United Kingdom"] "Old Blighty!")}
                {:value "03" :label "Afghanistan"           }
                {:value "04" :label "Albania"               }
                {:value "05" :label [test-button]           }
                {:value "06" :label "American Samoa"        }
                {:value "07" :label "Andorra"               }
                {:value "08" :label "Angola"                }
                {:value "09" :label "Anguilla"              }
                {:value "00" :label "Antarctica"            }
                {:value "11" :label "Antigua and Barbuda"   }
                {:value "12" :label "Argentina"             }
                {:value "13" :label "Armenia"               }
                {:value "14" :label "Aruba"                 }
                {:value "16" :label "Austria"               }
                {:value "17" :label "Azerbaijan"            }
                {:value "18" :label "Bahamas"               }
                {:value "19" :label "Bahrain"               }
                {:value "20" :label "Bangladesh"            }
                {:value "21" :label "Barbados"              }
                {:value "22" :label "Belarus"               }
                {:value "23" :label "Belgium"               }
                {:value "24" :label "Belize"                }
                {:value "24" :label "Belize"                }
                {:value "25" :label "Benin"                 }
                {:value "26" :label "Bermuda"               }
                {:value "27" :label "Bhutan"                }
                {:value "28" :label "Bolivia"               }
                {:value "29" :label "Bosnia and Herzegovina"}
                {:value "30" :label "Botswana"              }
                {:value "31" :label "Bouvet Island"         }
                {:value "32" :label "Brazil"                }
                {:value "34" :label "Brunei Darussalam"     }
                {:value "35" :label "Bulgaria"              }
                {:value "36" :label "Burkina Faso"          }
                {:value "37" :label "Burundi"               }])


(defn panel
  []
  (let [selected-country-index (reagent/atom nil)]
    [:div
     [:h3.page-header "Dropdowns"]
     [single-drop-down
      :options     countries
      :placeholder "Choose a country"
      :model       selected-country-index
      :disabled    false
      :read-only   false]
     [:div
      {:style {:display "inline-block"
               :margin-left "20px"}}
      [:strong "Selected country: "] (if (nil? @selected-country-index)
                                       "None"
                                       (:label (nth countries @selected-country-index)))]
     ]))

