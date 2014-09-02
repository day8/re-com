(ns re-demo.dropdowns
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util     :as    util]
            [re-com.core     :refer [button spinner progress-bar]]
            [re-com.box      :refer [gap]]
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
                {:id "AG" :label [test-button]            :group "'A' COUNTRIES"}
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


#_(def countries [{:id "G1" :group "POPULAR COUNTRIES"}
                {:id "AU" :label "Australia"}
                {:id "US" :label "United States"}
                {:id "GB" :label (if @bold-uk [:strong "United Kingdom"] "Old Blighty!")}
                {:id "G2" :group "'A' COUNTRIES"}
                {:id "AF" :label "Afghanistan"            }
                {:id "AB" :label "Albania"                }
                {:id "AG" :label [test-button]            }
                {:id 06   :label "American Samoa"         }
                {:id 07   :label "Andorra"                }
                {:id true :label "Angola"                 }
                {:id [4]  :label "Anguilla"               }
                {:id "00" :label "Antarctica"             }
                {:id "11" :label "Antigua and Barbuda"    }
                {:id "12" :label "Argentina"              }
                {:id "13" :label "Armenia"                }
                {:id "14" :label "Aruba"                  }
                {:id "16" :label "Austria"                }
                {:id "17" :label "Azerbaijan"             }
                {:id "G3" :group "'B' COUNTRIES"}
                {:id "18" :label "Bahamas"                }
                {:id "19" :label "Bahrain"                }
                {:id "20" :label "Bangladesh"             }
                {:id "21" :label "Barbados"               }
                {:id "22" :label "Belarus"                }
                {:id "23" :label "Belgium"                }
                {:id "24" :label "Belize"                 }
                {:id "25" :label "Benin"                  }
                {:id "26" :label "Bermuda"                }
                {:id "27" :label "Bhutan"                 }
                {:id "28" :label "Bolivia"                }
                {:id "29" :label "Bosnia and Herzegovina" }
                {:id "30" :label "Botswana"               }
                {:id "31" :label "Bouvet Island"          }
                {:id "32" :label "Brazil"                 }
                {:id "34" :label "Brunei Darussalam"      }
                {:id "35" :label "Bulgaria"               }
                {:id "36" :label "Burkina Faso"           }
                {:id "37" :label "Burundi"                }])


#_(def countries [{:id "AU" :label "Australia"              }
                {:id "US" :label "United States"          }
                {:id "GB" :label (if @bold-uk [:strong "United Kingdom"] "Old Blighty!")}
                {:id "AF" :label "Afghanistan"            }
                {:id "AB" :label "Albania"                }
                {:id "AG" :label [test-button]            }
                {:id 06   :label "American Samoa"         }
                {:id 07   :label "Andorra"                }
                {:id true :label "Angola"                 }
                {:id [4]  :label "Anguilla"               }
                {:id "00" :label "Antarctica"             }
                {:id "11" :label "Antigua and Barbuda"    }
                {:id "12" :label "Argentina"              }
                {:id "13" :label "Armenia"                }
                {:id "14" :label "Aruba"                  }
                {:id "16" :label "Austria"                }
                {:id "17" :label "Azerbaijan"             }
                {:id "18" :label "Bahamas"                }
                {:id "19" :label "Bahrain"                }
                {:id "20" :label "Bangladesh"             }
                {:id "21" :label "Barbados"               }
                {:id "22" :label "Belarus"                }
                {:id "23" :label "Belgium"                }
                {:id "24" :label "Belize"                 }
                {:id "25" :label "Benin"                  }
                {:id "26" :label "Bermuda"                }
                {:id "27" :label "Bhutan"                 }
                {:id "28" :label "Bolivia"                }
                {:id "29" :label "Bosnia and Herzegovina" }
                {:id "30" :label "Botswana"               }
                {:id "31" :label "Bouvet Island"          }
                {:id "32" :label "Brazil"                 }
                {:id "34" :label "Brunei Darussalam"      }
                {:id "35" :label "Bulgaria"               }
                {:id "36" :label "Burkina Faso"           }
                {:id "37" :label "Burundi"                }])

#_(def countries [{:id    "G1"
                 :label "POPULAR COUNTRIES"
                 :items [{:id "AU" :label "Australia"             }
                         {:id "US" :label "United States"         }
                         {:id "GB" :label (if @bold-uk [:strong "United Kingdom"] "Old Blighty!")}]}
                {:id    "G2"
                 :label "'A' COUNTRIES"
                 :items [{:id "AF" :label "Afghanistan"           }
                         {:id "AB" :label "Albania"               }
                         {:id "AG" :label [test-button]           }
                         {:id 06   :label "American Samoa"        }
                         {:id 07   :label "Andorra"               }
                         {:id true :label "Angola"                }
                         {:id [4]  :label "Anguilla"              }
                         {:id "00" :label "Antarctica"            }
                         {:id "11" :label "Antigua and Barbuda"   }
                         {:id "12" :label "Argentina"             }
                         {:id "13" :label "Armenia"               }
                         {:id "14" :label "Aruba"                 }
                         {:id "16" :label "Austria"               }
                         {:id "17" :label "Azerbaijan"            }]}
                {:id    "G3"
                 :label "B COUNTRIES"
                 :items [{:id "18" :label "Bahamas"               }
                         {:id "19" :label "Bahrain"               }
                         {:id "20" :label "Bangladesh"            }
                         {:id "21" :label "Barbados"              }
                         {:id "22" :label "Belarus"               }
                         {:id "23" :label "Belgium"               }
                         {:id "24" :label "Belize"                }
                         {:id "25" :label "Benin"                 }
                         {:id "26" :label "Bermuda"               }
                         {:id "27" :label "Bhutan"                }
                         {:id "28" :label "Bolivia"               }
                         {:id "29" :label "Bosnia and Herzegovina"}
                         {:id "30" :label "Botswana"              }
                         {:id "31" :label "Bouvet Island"         }
                         {:id "32" :label "Brazil"                }
                         {:id "34" :label "Brunei Darussalam"     }
                         {:id "35" :label "Bulgaria"              }
                         {:id "36" :label "Burkina Faso"          }
                         {:id "37" :label "Burundi"               }]}
                {:id "i1" :label "Orphan Item 1"}
                {:id "i2" :label "Orphan Item 2"}
                {:id "i3" :label "Orphan Item 3"}
                ])

(defn panel
  []
  (let [selected-country-id (reagent/atom nil)]
    (fn [] [:div
            [:h3.page-header "Dropdowns"]
            [single-drop-down
             :options     countries
             :placeholder "Choose a country"
             :model       selected-country-id
             :width       "300px"
             :disabled    false         ;; TODO
             :read-only   false]        ;; TODO
            [:div
             {:style {:display     "inline-block"
                      :margin-left "20px"}}
             [:strong "Selected country: "] (if (nil? @selected-country-id)
                                              "None"
                                              (str (:label (find-option countries @selected-country-id)) " [" @selected-country-id "]"))]
            ])))

