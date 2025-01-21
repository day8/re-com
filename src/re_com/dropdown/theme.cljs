(ns re-com.dropdown.theme
  (:require
   [clojure.string :as str]
   [re-com.dropdown :as-alias dd]
   [re-com.util :refer [px]]
   [re-com.theme.util :refer [merge-props merge-style]]
   [re-com.theme.default :refer [base main]]))

(defmethod base ::dd/body-wrapper
  [{:keys [position top left anchor-top] :as props}]
  (update props :style merge {:position   position
                              :top        (px top)
                              :left       (px left)
                              :opacity    (when-not anchor-top 0)
                              :overflow-y "auto"
                              :overflow-x "visible"
                              :z-index    30}))

(defmethod base ::dd/anchor-wrapper
  [{{:keys [state transition!]
     {:keys [sm-2]} :variables} :re-com
    :as                         props}]
  (-> props
      (merge-props
       {:attr  {:tab-index   (or (:tab-index state) 0)
                :on-click    #(transition! :toggle)
                #_#_:on-blur #(do (transition! :blur)
                                  (transition! :exit))}
        :style {:outline        (when (and (= :focused (:focusable state))
                                           (not= :open (:openable state)))
                                  (str sm-2 " auto #ddd"))
                :outline-offset (str "-" sm-2)
                :position       "relative"
                #_#_:display    "block"
                :overflow       "hidden"
                :user-select    "none"
                #_#_:width      "100%"
                :z-index        (case (:openable state)
                                  :open 20 nil)}})))

(defmethod main ::dd/anchor-wrapper
  [{:as            props
    {:keys [state]
     $     :variables} :re-com}]
  (let [open?   (= :open (:openable state))
        closed? (= :closed (:openable state))]
    (-> props
        (merge-props
         {:align :center
          :style {:background-color (:white $)
                  :background-clip  "padding-box"
                  :border           (str "1px solid "
                                         (cond
                                           closed? (:border $)
                                           open?   "#66afe9"))
                  :border-radius    "4px"
                  :box-shadow       (cond-> "0 1px 1px rgba(0, 0, 0, .075) inset"
                                      open? (str ", 0 0 8px rgba(82, 168, 236, .6)"))
                  :color            (:foreground $)
                  :height           "34px"
                  :padding          "0 8px 0 8px"
                  :text-decoration  "none"
                  :white-space      "nowrap"
                  :transition       "border 0.2s box-shadow 0.2s"}}))))

(defmethod base ::dd/backdrop
  [props]
  (merge-props props
               {:class "noselect"
                :style {:position       "fixed"
                        :left           "0px"
                        :top            "0px"
                        :width          "100%"
                        :height         "100%"
                        :pointer-events "none"}}))

(defmethod main ::dd/backdrop
  [{{:keys [state]} :re-com :as props}]
  (merge-style props
               {:background-color "black"
                :opacity          (if (-> state :transitionable (= :in)) 0.1 0)
                :transition       "opacity 0.25s"}))

(defmethod base ::dd/wrapper
  [props]
  (merge-style props
               {:display  "inline-block"
                :position "relative"}))

(defmethod main ::dd/body-wrapper
  [props]
  (let [{:keys [sm-2 sm-3 sm-6 shadow border]} (-> props :re-com :variables)]
    (update props :style merge {:background-color "white"
                                :border-radius    "4px"
                                :border           (str "thin solid " border)
                                :padding          sm-3
                                :box-shadow       (str/join " " [sm-2 sm-2 sm-6 shadow])})))

(defmethod main ::dd/anchor
  [{:keys          [state]
    {$ :variables} :re-com
    :as            props}]
  (merge-style props
               (cond-> {:color (:foreground $)
                        :overflow "hidden"
                        :text-overflow "ellipsis"
                        :white-space "nowrap"}
                 (-> state :enable (= :disabled))
                 (merge {:background-color (:background-disabled $)}))))
