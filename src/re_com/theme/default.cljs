(ns re-com.theme.default
  (:require
   [clojure.string :as str]
   [re-com.theme.util :refer [merge-props]]
   [re-com.dropdown :as-alias dropdown]
   [re-com.nested-grid :as-alias nested-grid]
   [re-com.tree-select :as-alias tree-select]))

(def golden-section-50
  {:sm-1   "1px"
   :sm-2   "2px"
   :sm-3   "3px"
   :sm-4   "5px"
   :sm-5   "7px"
   :sm-6   "12px"
   :md-1   "19px"
   :md-2   "31px"
   :md-3   "50px"
   :md-4   "81px"
   :md-5   "131px"
   :md-6   "212px"
   :lg-1   "343px"
   :lg-2   "554px"
   :lg-3   "897px"
   :lg-4   "1452px"
   :lg-5   "2349px"
   :lg-6   "3800px"
   :half   "25px"
   :double "100px"})

(def colors
  {:primary             "#0d6efd"
   :secondary           "#6c757d"
   :success             "#198754"
   :info                "#0dcaf0"
   :warning             "#ffc107"
   :danger              "#dc3545"
   :light               "#f8f9fa"
   :dark                "#212529"
   :neutral             "#555555"
   :foreground          "#767a7c"
   :light-neutral       "#eee"
   :background          "white"
   :background-disabled "#EEE"
   :border              "#ccc"
   :border-dark         "#aaa"
   :shadow              "rgba(0, 0, 0, 0.2)"})

(def static-variables (merge colors golden-section-50))

(defn base-variables [props ctx]
  [props (assoc ctx :variables static-variables)])

(defn base [props {:keys                   [state part transition!]
                   {:keys [sm-2]}          :variables
                   {:keys [anchor-height]} :component-props
                   :as                     ctx}]
  (->> {}
       (case part

         ::dropdown/wrapper
         {:attr  {:on-focus #(do (transition! :focus)
                                 (transition! :enter))
                  :on-blur  #(do (transition! :blur)
                                 (transition! :exit))}
          :style {:display  "inline-block"
                  :position "relative"}}

         ::dropdown/anchor-wrapper
         {:attr  {:tab-index (or (:tab-index state) 0)
                  :on-blur   #(do (transition! :blur)
                                  (transition! :exit))}
          :style {:outline        (when (and (= :focused (:focusable state))
                                             (not= :open (:openable state)))
                                    (str sm-2 " auto #ddd"))
                  :outline-offset (str "-" sm-2)
                  :position       "relative"
                  :display        "block"
                  :overflow       "hidden"
                  :user-select    "none"
                  :width          "100%"
                  :z-index        (case (:openable state)
                                    :open 99999 nil)}}

         ::dropdown/backdrop
         {:class    "noselect"
          :on-click #(do (transition! :close)
                         (transition! :blur))
          :style    {:position           "fixed"
                     :left               "0px"
                     :top                "0px"
                     :width              "100%"
                     :height             "100%"
                     #_#_:pointer-events "none"
                     :z-index            (case (:openable state)
                                           :open 99998 nil)}}

         ::dropdown/body-wrapper
         {:style {:position   "absolute"
                  :overflow-y "auto"
                  :overflow-x "visible"}}

         ::nested-grid/cell-grid-container
         {:style {:position        "relative"
                  :overflow        "auto"
                  :scrollbar-width "thin"
                  :gap             "0px"}}

         ::nested-grid/cell-wrapper
         {:style {#_#_:pointer-events "none"
                  :user-select        "none"
                  :overflow           "hidden"
                  :position           "relative"}}

         ::nested-grid/column-header-wrapper
         {:style {:position           "relative"
                  #_#_:pointer-events "none"
                  :user-select        "none"
                  :height             "100%"}}

         ::nested-grid/row-header-wrapper
         {:style {:position           "relative"
                  #_#_:pointer-events "none"
                  :user-select        "none"
                  :height             "100%"}})
       (merge-props props)))

(defn main-variables [props _] props)

(defn main [props {:keys                                                        [state part]
                   {:as   $
                    :keys [sm-1 sm-2 sm-3 sm-4 sm-6 md-1 md-2
                           dark shadow light light-neutral
                           border border-dark
                           foreground]} :variables}]
  (->> {}
       (case part

         ::dropdown/wrapper
         {:style {:max-width "250px"}}

         ::dropdown/body-wrapper
         {:style {:background-color "white"
                  :border-radius    sm-3
                  :border           (str sm-1 " solid " (:border $))
                  :padding          sm-3
                  :box-shadow       (str/join " " [sm-2 sm-2 sm-6 shadow])}}

         ::dropdown/backdrop
         {:style {:color      "black"
                  :opacity    (if (-> state :transitionable (= :in)) 0.1 0)
                  :transition "opacity 0.25s"}}

         ::dropdown/anchor-wrapper
         (let [open?   (= :open (:openable state))
               closed? (= :closed (:openable state))]
           {:style {:background-color (:light $)
                    :background-clip  "padding-box"
                    :border           (str "1px solid "
                                           (cond
                                             closed? "#cccccc"
                                             open?   "#66afe9"))
                    :border-radius    sm-3
                    :box-shadow       (cond-> "0 1px 1px rgba(0, 0, 0, .2) inset"
                                        open? (str ", 0 0 8px rgba(82, 168, 236, .6)"))
                    :color            (:neutral $)
                    :height           md-2
                    :line-height      md-2
                    :padding          "0 0 0 8px"
                    :text-decoration  "none"
                    :white-space      "nowrap"
                    :transition       "border 0.2s box-shadow 0.2s"}})

         ::dropdown/anchor
         {:style (cond-> {:color (:foreground $)
                          :overflow "hidden"
                          :text-overflow "ellipsis"
                          :white-space "nowrap"}
                   (-> state :enable (= :disabled))
                   (merge {:background-color (:background-disabled $)}))}

         ::nested-grid/cell-grid-container
         {:style {:padding          "0px"
                  :background-color "transparent"}}

         ::nested-grid/header-spacer
         {:style {:border-left      (if ((:edge state) :left)
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))
                  :border-top       (when (get (:edge state) :top)
                                      (str "thin solid " border-dark))
                  :border-bottom    (when (get (:edge state) :bottom)
                                      (str "thin solid " border))
                  :border-right     (str "thin" " solid " border)
                  :background-color light-neutral}}

         ::nested-grid/cell-wrapper
         {:style {:font-size        "12px"
                  :background-color "white"
                  :color            "#777"
                  :padding-top      sm-3
                  :pading-right     sm-3
                  :text-align       "right"
                  :border-right     (condp #(get %2 %1) (:edge state)
                                      :column-section-right
                                      (str "thin" " solid " border-dark)
                                      :right
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))
                  :border-bottom    (if ((:edge state) :bottom)
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))}}

         ::nested-grid/column-header-wrapper
         {:style {:padding-top      sm-3
                  :padding-right    sm-4
                  :padding-left     sm-4
                  :border-bottom    (str "thin" " solid" border)
                  :background-color light-neutral
                  :color            "#666"
                  :text-align       "center"
                  :font-size        "13px"
                  :border-top       (when (get (:edge state) :top) (str "thin solid " border-dark))
                  :border-right     (condp #(get %2 %1) (:edge state)
                                      :column-section-right
                                      (str "thin" " solid " border-dark)
                                      :right
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))
                  #_#_:font-weight  "bold"
                  :overflow         "hidden"
                  :white-space      "nowrap"
                  :text-overflow    "ellipsis"}}

         ::nested-grid/row-header-wrapper
         {:style {:padding-top      sm-3
                  :padding-right    sm-3
                  :padding-left     sm-6
                  :border-left      (if ((:edge state) :left)
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))
                  :border-bottom    (if ((:edge state) :bottom)
                                      (str "thin" " solid " border-dark)
                                      (str "thin" " solid " border))
                  :border-right     (str "thin" " solid" border)
                  :background-color light-neutral
                  :color            "#666"
                  :text-align       "left"
                  :font-size        "13px"
                  #_#_:font-weight  "bold"
                  :overflow         "hidden"
                  :white-space      "nowrap"
                  :text-overflow    "ellipsis"}}

         ::tree-select/dropdown-anchor
         {:style {:padding  "0 8px 0 0"
                  :overflow "hidden"
                  :color    foreground
                  :cursor   (if (-> state :enable (= :disabled))
                              "default" "pointer")}}

         ::tree-select/dropdown-counter
         {:style {:margin-left  "10px"
                  :margin-right "10px"
                  :opacity      "50%"}})
       (merge-props props)))
