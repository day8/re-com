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

(defn opt-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [opt on-click]
      [:li
       {:class         (str "active-result" (when @mouse-over? " highlighted"))
        :on-mouse-over #(reset! mouse-over? true)
        :on-mouse-out  #(reset! mouse-over? false)
        :on-click      #(on-click (:index opt))
        :value         (:value opt)}
       (:label opt)])))


(defn single-drop-down
  [& {:keys [options model placeholder]}]
  "Render a bootstrap styled choosen"
  (let [id                (gensym "select_")
        has-focus         (reagent/atom false)
        drop-showing?     (reagent/atom false)
        filter-text       (reagent/atom "")
        backdrop-click    #(reset! drop-showing? false)
        dropdown-click    #(reset! drop-showing? (not @drop-showing?))
        item-click        #(do
                            (reset! model %)
                            (reset! drop-showing? false)
                            (reset! filter-text ""))
        ]
    (fn []
      [:div
       {:class (str "chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
        :style {:width "300px"}}
       (when @drop-showing?
         [:div
          {:style {:position         "fixed"
                   :left             "0px"
                   :top              "0px"
                   :width            "100%"
                   :height           "100%"
                   :background-color "black"
                   :opacity          0.05}
           :on-click backdrop-click}])
       [:a.chosen-single.chosen-default
        {:on-click  dropdown-click
         :tab-index "-1"}
        [:span (if @model (:label (nth options @model)) placeholder)]
        [:div [:b]]]
       [:div.chosen-drop
        [:div.chosen-search
         [:input
          {:type          "text"
           :auto-complete "off"
           :tab-index     "2"
           :value         @filter-text
           :on-change     #(reset! filter-text (-> % .-target .-value))}]]
        [:ul.chosen-results
         (let [index     (atom -1)
               options   (reduce #(conj %1 (assoc %2 :index (swap! index inc))) [] options)
               re        (try
                           (js/RegExp. @filter-text "i")
                           (catch js/Object e nil))
               filter-fn (partial (fn [re opt]
                                    (when-not (nil? re) (.test re (:label opt)))) re)
               ]
           (doall (for [opt (filter filter-fn options)]       ;; doall prevents warning (https://github.com/holmsand/reagent/issues/18)
                    ^{:key (:value opt)} [opt-item opt item-click])))]]
       ])))
