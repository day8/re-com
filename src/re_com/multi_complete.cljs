(ns re-com.multi-complete
  (:require [reagent.core :as r]
            [re-com.box      :refer [box border h-box v-box]]
            [clojure.string :as str]
            [re-com.core :as rc]
            [cljs.spec :as s]))



(def multi-complete-args-des
  [{:name :model
    :type "vector of strings | atom"
    :validate-fn vector?
    :description "a vector with all the selected items -- kept in order so that on-delete can remove them appropriately"
    }
   {:name :suggestions
    :type "vector of strings"
    :validate-fn vector?
    :description "All of your choices"
    }
   {:name :highlight-class
    :type "string"
    :validate-fn string?
    :description "class for selected item within suggestion dropdown"}
   {:name :item-class
    :type "string"
    :validate-fn string?
    :description "class for each item in suggestion dropdown"}
   {:name :save!
    :type "choice -> anything"
    :validate-fn ifn?
    :description "a callback that will be passed either a selected item from suggestions or the value of the text in the input field"
    }
    {:name :on-delete!
     :type "nil -> anything"
     :validate-fn ifn?
     :description "Fired whenever the text field is empty and delete button is pressed, purpose is generally to remove the last item in the model/selections atom"}
   {:name :placeholder
    :type "string"
    :description "A hint to make sure users know they can type here"}]
  )




(defn multi-complete [{:keys [highlight-class
                              placeholder
                              item-class
                              list-class
                              suggestions
                              save!
                              selections
                              on-delete]
                       :or {highlight-class "multi-complete-highlight"
                            item-class ""}}]
  (let [a (r/atom "")
        selected-index (r/atom -1)
        typeahead-hidden? (r/atom false)
        mouse-on-list? (r/atom false)]
    (fn []
      (let [options  (if (clojure.string/blank? @a)
                       []
                       (filter
                        #(-> % (.toLowerCase %) (.indexOf (.toLowerCase @a)) (> -1))
                        suggestions))
            matching-options (filter (comp not (set @selections)) options)
            choose-selected #(if (and (not-empty matching-options)
                                       (> @selected-index -1))
                              (let [choice (nth matching-options @selected-index)]
                                (save! choice)
                                (reset! selected-index 0)
                                (reset! a ""))
                              (when (not (str/blank? @a))
                                (do
                                  (save! @a)
                                  (reset! selected-index 0)
                                  (reset! a ""))))]
        [h-box
         :class "multi-complete"
         :children [[:div.multi-complete-output
                     (when @selections
                       (for [x @selections]
                         ^{:key x}[:button {:class "btn btn-default"
                                          :on-click #(swap! selections (fn [y] (remove #{x} y)))}(str x)]
                         ))
                     [:input.multi-complete-input
                      {:value @a
                       :placeholder placeholder
                       :on-change #(reset! a (-> % .-target .-value))
                       :on-key-down #(do
                                       (case (.-which %)
                                         38 (do
                                              (.preventDefault %)
                                              (when-not (= @selected-index -1)
                                                (swap! selected-index dec)))
                                         40 (do
                                              (.preventDefault %)
                                              (when-not (= @selected-index (dec (count matching-options)))
                                                (swap! selected-index inc)))
                                         9  (choose-selected)
                                         13 (choose-selected)
                                         8  (when (clojure.string/blank? @a)
                                              (on-delete))
                                         27 (do #_(reset! typeahead-hidden? true)
                                                (reset! selected-index -1))
                                         "default"))}]

                     [:ul {:style
                           {:display (if (or (empty? matching-options) @typeahead-hidden?) :none :block) }
                           :class list-class
                           :on-mouse-enter #(reset! mouse-on-list? true)
                           :on-mouse-leave #(reset! mouse-on-list? false)}
                      (doall
                       (map-indexed
                        (fn [index result]
                          [:li {:tab-index     index
                                :key           index
                                :class         (if (= @selected-index index) highlight-class item-class)
                                :on-mouse-over #(do
                                                  (reset! selected-index (js/parseInt (.getAttribute (.-target %) "tabIndex"))))
                                :on-click      #(do
                                                  (reset! a "")
                                                  (save! result)
                                                  )}
                           result])
                        matching-options))]]]]
        ))))



  ;; (defcard-rg tags-example
  ;;   (let [selections (r/atom [])]
  ;;     [multi-complete {:highlight-class "highlight"
  ;;                      :selections selections
  ;;                      :on-delete #(swap! selections pop)
  ;;                      :save! #(swap! selections conj %) 
  ;;                      :suggestions ["Reagent""Re-frame""Re-com""Reaction"]}])
  ;;   ))
