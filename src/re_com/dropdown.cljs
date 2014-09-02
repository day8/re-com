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


(defn find-option
  [options id]
  "In a vector of maps (where each map has an :id), return the first map containing the id parameter
   (although there should probably only be one "
  (let [index-fn    (fn [index item] (when (= (:id item) id) index))
        index-of-id (first (keep-indexed index-fn options))]
    (nth options index-of-id)))


(defn morph [opts]
  (let [new-opts   (atom [])
        last-group (atom nil)]
    (doall
      (for [opt opts]
        (let [new-group (not= (:group opt) @last-group)
              _         (reset! last-group (:group opt))]
          (when new-group
            (swap! new-opts conj {:id (str (:id opt) "##")
                                  :group (:group opt)}))
          (swap! new-opts conj {:id (:id opt)
                                :label (:label opt)}))
        ))
    @new-opts))


(defn option-group-item
  [opt]
  [:li.group-result
   {:value (:value opt)}
   (:group opt)])


(defn option-item
  []
  (let [mouse-over? (reagent/atom false)]
    (fn [opt on-click]
      [:li
       {:class         (str "active-result group-option" (if @mouse-over? " highlighted"))
        :on-mouse-over #(reset! mouse-over? true)
        :on-mouse-out  #(reset! mouse-over? false)
        :on-click      #(on-click (:id opt))}
       (:label opt)])))


(defn single-drop-down
  [& {:keys [options model placeholder width]}]
  "Render a bootstrap styled choosen"
  (let [has-focus         (reagent/atom false)              ;; TODO: Implement?
        drop-showing?     (reagent/atom false)
        filter-text       (reagent/atom "")
        selected-item     (reagent/atom model)
        backdrop-click    #(reset! drop-showing? false)
        dropdown-click    #(reset! drop-showing? (not @drop-showing?))
        item-click        #(do
                            (reset! model %)
                            (reset! drop-showing? false)
                            (reset! filter-text ""))]
    (fn []
      [:div
       {:class (str "chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
        :style (when width {:width width})}
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
         :on-key-up #(case (.-which %)
                      13 (reset! drop-showing? false)
                      27 (reset! drop-showing? false)
                      nil)
         :tab-index "-1"}
        [:span (if @model
                 (:label (find-option options @model))
                 placeholder)]
        [:div [:b]]]
       [:div.chosen-drop
        [:div.chosen-search
         [:input
          {:type          "text"
           :auto-complete "off"
           :tab-index     "2"
           :value         @filter-text
           :on-change     #(reset! filter-text (-> % .-target .-value))
           :on-key-up     #(case (.-which %)
                            13 (reset! drop-showing? false)
                            27 (reset! drop-showing? false)
                            nil)
           }]]
        [:ul.chosen-results
         (let [re         (try
                            (js/RegExp. @filter-text "i")
                            (catch js/Object e nil))
               filter-fn  (partial (fn [re opt]
                                     (when-not (nil? re)
                                       (or (.test re (:group opt)) (.test re (:label opt)))))
                                   re)]
           (doall (for [opt (morph (filter filter-fn options))] ;; doall prevents warning (https://github.com/holmsand/reagent/issues/18)
                    (if (:group opt)
                       ^{:key (:id opt)} [option-group-item opt]
                       ^{:key (:id opt)} [option-item opt item-click]))))]]
       ])))
