(ns re-com.dropdown.theme
  (:require
   [clojure.string :as str]
   [re-com.dropdown :as-alias dd]
   [re-com.table-filter :as-alias tf]
   [re-com.util :refer [px]]
   [re-com.theme.util :as tu :refer [merge-props]]
   [re-com.theme.default :refer [base main]]))

(defmethod base ::dd/body-wrapper
  [{:keys [position top left anchor-top] :as props}]
  (tu/style props {:position   position
                   :top        (px top)
                   :left       (px left)
                   :opacity    (when-not anchor-top 0)
                   :overflow-y "auto"
                   :overflow-x "visible"
                   :z-index    30}))

(defmethod base ::dd/anchor-wrapper
  [{{:keys          [state transition!]
     {:keys [sm-2]} :variables} :re-com
    :as                         props}]
  (let [disabled? (= :disabled (:enable state))]
    (-> props
        (tu/attr {:tab-index   (or (:tab-index state) 0)
                  :on-click    (when-not disabled? #(transition! :toggle))
                  #_#_:on-blur #(do (transition! :blur)
                                    (transition! :exit))})
        (tu/style {:outline        (when (and (= :focused (:focusable state))
                                              (not= :open (:openable state)))
                                     (str sm-2 " auto #ddd"))
                   :outline-offset (str "-" sm-2)
                   :position       "relative"
                   #_#_:display    "block"
                   :overflow       "hidden"
                   :user-select    "none"
                   #_#_:width      "100%"
                   :z-index        (case (:openable state)
                                     :open 20 nil)}))))

(defmethod main ::dd/anchor-wrapper
  [{:as                props
    {:keys [state]
     $     :variables} :re-com}]
  (let [open?     (= :open (:openable state))
        closed?   (= :closed (:openable state))
        disabled? (= :disabled (:enable state))]
    (-> props
        (merge {:align :center})
        (tu/style {:background-color (:background $)
                   :background-clip  "padding-box"
                   :border           (str "1px solid "
                                          (cond
                                            disabled? "#d1d5db"
                                            closed?   (:border $)
                                            open?     "#66afe9"))
                   :border-radius    "4px"
                   :box-shadow       (when-not disabled?
                                       (cond-> "0 1px 1px rgba(0, 0, 0, .075) inset"
                                         open? (str ", 0 0 8px rgba(82, 168, 236, .6)")))
                   :color            (if disabled? "#9ca3af" (:foreground $))
                   :height           "34px"
                   :padding          "0 8px 0 8px"
                   :text-decoration  "none"
                   :white-space      "nowrap"
                   :transition       "border 0.2s box-shadow 0.2s"}
                  (when disabled?
                    {:background-color "#EEE"})))))

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
  (tu/style props
            {:background-color "black"
             :opacity          (if (-> state :transitionable (= :in)) 0.1 0)
             :transition       "opacity 0.25s"}))

(defmethod base ::dd/wrapper
  [props]
  (tu/style props
            {:display  "inline-block"
             :position "relative"}))

(defmethod main ::dd/body-wrapper
  [props]
  (let [{:keys [sm-2 sm-3 sm-6 shadow border background]} (-> props :re-com :variables)]
    (tu/style props {:background-color background
                     :border-radius    "4px"
                     :border           (str "thin solid " border)
                     :padding          sm-3
                     :box-shadow       (str/join " " [sm-2 sm-2 sm-6 shadow])})))

(defmethod main ::dd/body
  [props]
  (let [{:keys [foreground]} (-> props :re-com :variables)]
    (tu/style props {:color foreground})))

(defmethod main ::dd/anchor
  [{:keys                       [state]
    {:keys [from] $ :variables} :re-com
    :as                         props}]
  (tu/style props
            {:color         (:foreground $)
             :overflow      "hidden"
             :text-overflow "ellipsis"
             :white-space   "nowrap"}
            (when (= :disabled (:enable state))
              {:background-color (:background-disabled $)})))
