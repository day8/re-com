(ns re-com.dropdown
  (:require
    [re-com.util       :as     util]
    [reagent.core      :as     reagent]))


;;  http://alxlit.name/bootstrap-chosen/
;;  Alternative: http://silviomoreto.github.io/bootstrap-select/

;; Will need a multi dropdown and a single dropdown

;; allow clear button on right
;; disabled ???
;; style
;; options is   {::id id  :label "DDDD"  :group  "XXXX"  }

(defn single-drop-down
  [& {:keys [options model placeholder]}]
  "Render a bootstrap styled choosen"
  (let [id                (gensym "select_")
        has-focus         (reagent/atom false)
        drop-showing?     (reagent/atom false)
        mouse-over?       (reagent/atom false)
        filter-text       (reagent/atom "")
        backdrop-callback #(reset! drop-showing? false)
        click-handler     #(reset! drop-showing? (not @drop-showing?))
        ]
    (fn []
      [:div
       {:class (str "chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
        :style {:width "300px"
                ;:display "inline-block"
                }
        }
       (when @drop-showing?
         [:div
          {:style {:position         "fixed"
                   :left             "0px"
                   :top              "0px"
                   :width            "100%"
                   :height           "100%"
                   :background-color "black"
                   :opacity          0.05}
           :on-click backdrop-callback}])
       [:a.chosen-single.chosen-default
        {:on-click  click-handler
         :tab-index "-1"}
        [:span placeholder]
        [:div [:b]]]
       [:div.chosen-drop
        [:div.chosen-search
         [:input
          {:type          "text"
           :auto-complete "off"
           :tab-index     "2"
           :on-change     #(reset! filter-text (-> % .-target .-value))}]]
        [:ul.chosen-results
         (let [re        (js/RegExp. @filter-text "i")
               filter-fn (partial (fn [re opt] (.test re (:label opt))) re)]
           (doall (for [opt (filter filter-fn options)]       ;; doall prevents warning (https://github.com/holmsand/reagent/issues/18)
                    ^{:key (:value opt)} [:li
                                        {:class         (str "active-result" (when @mouse-over? " highlighted")) ;;@mouse-over?
                                         :on-mouse-over #(reset! mouse-over? true)
                                         :on-mouse-out  #(reset! mouse-over? false)
                                         :value         (:value opt)}
                                        (:label opt)])))]]
       ])))
