(ns re-com.theme.default
  (:require
   [clojure.string :as str]
   [re-com.util :as ru :refer [px]]
   [re-com.theme.util :refer [merge-props merge-style]]
   [re-com.dropdown :as-alias dropdown]
   [re-com.error-modal :as-alias error-modal]
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
   :error               "#bf1010"
   :warning             "#ffc107"
   :danger              "#dc3545"
   :light               "#f8f9fa"
   :white               "#ffffff"
   :dark                "#212529"
   :black               "#000000"
   :neutral             "#555555"
   :foreground          "#767a7c"
   :light-background    "#eee"
   :light-foreground    "#ccc"
   :background          "white"
   :background-disabled "#EEE"
   :border              "#cccccc"
   :border-dark         "#aaa"
   :shadow              "rgba(0, 0, 0, 0.2)"})

(def font-sizes
  {:font-size/xx-small (px 11)
   :font-size/x-small  (px 12)
   :font-size/small    (px 13)
   :font-size/medium   (px 14)
   :font-size/large    (px 15)
   :font-size/x-large  (px 16)
   :font-size/xx-large (px 17)})

(def static-variables (merge colors golden-section-50 font-sizes))

(defn variables [props]
  (assoc-in props [:re-com :variables] static-variables))

(defmulti base :part)

(defmethod base :default [{:keys [part] :as props}]
  #_(js/console.log "Re-com's default theme function was passed an unknown part: " part
                    ". This is a no-op.")
  props)

(defmulti main :part)

(defmethod main :default [{:keys [part] :as props}]
  #_(js/console.log "Re-com's default theme function was passed an unknown part: " part
                    ". This is a no-op.")
  props)

(defmethod base :default [props] props)

(def cell-wrapper-base {#_#_:pointer-events "none"
                        :user-select        "none"
                        :overflow           "hidden"
                        :position           "relative"})

(defmethod base ::nested-grid/cell-wrapper [props]
  (update props :style merge cell-wrapper-base))

(def row-header-wrapper-base {:user-select        "none"
                              :height             "100%"})

(defmethod base ::nested-grid/row-header-wrapper [props]
  (update props :style merge row-header-wrapper-base))

(defmethod base ::nested-grid/column-header-wrapper [props]
  (update props :style merge
          {:user-select "none"
           :width       "100%"
           :height      "100%"}))

(defmethod main ::nested-grid/column-header-wrapper
  [{:keys        [header-spec]
    {{:keys [sm-4 sm-3 light-background border-dark border]}
     :variables} :re-com
    :as          props}]
  (let [{:keys [align-column align-column-header align]} header-spec]
    (update props :style merge
            {:padding-top      sm-3
             :padding-right    sm-4
             :padding-left     sm-4
             :white-space      :nowrap
             :text-overflow    :ellipsis
             :overflow         :hidden
             :background-color light-background
             :color            "#666"
             :text-align       (or align-column-header align-column align :center)
             :font-size        "13px"
             :border-top       (when (get (:edge props) :top) (str "thin solid " border-dark))
             :border-bottom    (str "thin solid " border)
             :border-right     (cond
                                 (get (:edge props) :column-section-right)
                                 (str "thin" " solid " border-dark)
                                 (get (:edge props) :right)
                                 (str "thin" " solid " border-dark)
                                 :else
                                 (str "thin" " solid " border))})))

(defmethod base ::nested-grid/row-header
  [props]
  (update props :style merge {:width         "100%"
                              :text-overflow :ellipsis
                              :overflow      :hidden
                              :white-space   :nowrap
                              :position      :sticky}))

(defmethod base ::nested-grid/column-header
  [props]
  (update props :style merge {:height        "100%"
                              :text-overflow :ellipsis
                              :overflow      :hidden
                              :whitespace    :nowrap}))

(def row-header-wrapper-main
  (let [{:keys [sm-3 sm-6]}               golden-section-50
        {:keys [border light-background]} colors]
    {:padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-6
     :background-color light-background
     :color            "#666"
     :text-align       "left"
     :font-size        "13px"
     :white-space      "nowrap"
     :border-left      "thin solid #ccc"
     :border-bottom    "thin solid #ccc"}))

(defmethod base ::nested-grid/corner-header-wrapper
  [props]
  (update props :style merge row-header-wrapper-base))

(defmethod main ::nested-grid/corner-header-wrapper
  [{{{:keys [border-dark border light-background]} :variables} :re-com :as props}]
  (update props :style merge
          row-header-wrapper-main
          {:overflow      "hidden"
           :text-overflow "ellipsis"
           :white-space   "nowrap"}
          {:border-left      (when (contains? (:edge props) :left)
                               (str "thin" " solid " border-dark))
           :border-top       (when (get (:edge props) :top)
                               (str "thin solid " border-dark))
           :border-bottom    (when (get (:edge props) :bottom)
                               (str "thin solid " border))
           :border-right     (when (get (:edge props) :right)
                               (str "thin" " solid " border))
           :background-color light-background}))

(defmethod base ::dropdown/body-wrapper
  [{:keys [ref position top left anchor-top] :as props}]
  (update props :style merge {:position   position
                              :top        (px top)
                              :left       (px left)
                              :opacity    (when-not anchor-top 0)
                              :overflow-y "auto"
                              :overflow-x "visible"
                              :z-index    30}))

(defmethod base ::dropdown/anchor-wrapper
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

(defmethod main ::dropdown/anchor-wrapper
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

(defmethod base ::dropdown/backdrop
  [props]
  (merge-props props
               {:class "noselect"
                :style {:position       "fixed"
                        :left           "0px"
                        :top            "0px"
                        :width          "100%"
                        :height         "100%"
                        :pointer-events "none"}}))

(defmethod main ::dropdown/backdrop
  [{{:keys [state]} :re-com :as props}]
  (merge-style props
               {:background-color "black"
                :opacity          (if (-> state :transitionable (= :in)) 0.1 0)
                :transition       "opacity 0.25s"}))

(defmethod base ::dropdown/wrapper
  [props]
  (merge-style props
               {:display  "inline-block"
                :position "relative"}))

(defmethod base ::nested-grid/cell-grid-container
  [props]
  (merge-style props
               {:position "relative"
                :gap      "0px"}))

(defmethod main ::nested-grid/cell-grid-container
  [props]
  (merge-style props
               {:padding          "0px"
                :background-color "transparent"}))

(def cell-wrapper-main
  (let [{:keys [sm-3]} golden-section-50]
    {:font-size        12
     :background-color "white"
     :color            "#777"
     :padding-top      sm-3
     :padding-right    sm-3
     :padding-left     sm-3
     :text-align :right
     :border-right "thin solid #ccc"
     :border-bottom "thin solid #ccc"}))

(defmethod main ::nested-grid/cell-wrapper
  [{:keys [edge value column-path] :as props}]
  (let [align (some :align column-path)]
    (update props :style merge
            cell-wrapper-main
            (cond align
                  {:text-align align}
                  (string? value)
                  {:text-align :left})
            (when (seq edge)
              {:border-right  (cond
                                (contains? edge :column-section-right)
                                "thin solid #aaa"
                                (contains? edge :right)
                                "thin solid #aaa"
                                :else
                                "thin solid #ccc")
               :border-bottom (if (contains? edge :bottom)
                                "thin solid #aaa"
                                "thin solid #ccc")}))))

(defmethod main ::nested-grid/row-header-wrapper
  [{:keys [edge] :as props}]
  (update props :style merge
          row-header-wrapper-main
          (when (contains? edge :right)
            {:border-right "thin solid #aaa"})
          (when (contains? edge :left)
            {:border-left "thin solid #aaa"})
          (when (contains? edge :bottom)
            {:border-bottom "thin solid #aaa"})))

(defmethod main ::dropdown/body-wrapper
  [props]
  (let [{:keys [sm-2 sm-3 sm-6 shadow border]} (-> props :re-com :variables)]
    (update props :style merge {:background-color "white"
                                :border-radius    "4px"
                                :border           (str "thin solid " border)
                                :padding          sm-3
                                :box-shadow       (str/join " " [sm-2 sm-2 sm-6 shadow])})))

(defmethod base ::tree-select/label
  [props]
  (update props :style merge
          {:white-space   :nowrap
           :overflow      :hidden
           :text-overflow :ellipsis}))

(defmethod main ::error-modal/modal
  [props]
  (merge-props props
               {:wrap-nicely? false
                :style        {:z-index 50}}))

(defmethod main ::error-modal/inner-wrapper
  [{:as            props
    {$ :variables} :re-com}]
  (merge-props props
               {:style {:background-color (:white $)
                        :box-shadow       "2.82843px 2.82843px 4px rgba(1,1,1,0.2)"
                        :font-size        (:font-size/medium $)
                        :min-width        (px 474)
                        :min-height       (px 300)
                        :max-width        (px 525)}}))

(defmethod main ::error-modal/top-bar
  [{:as                                    props
    {{:keys [severity]}        :error-modal
     {:keys [md-2 sm-6] :as $} :variables} :re-com}]
  (merge-props props
               {:justify :between
                :align   :center
                :style   {:background-color (case severity
                                              :error   (:error $)
                                              :warning (:warning $)
                                              "#1e1e1e")
                          :color            "#FFFFFF"
                          :padding-left     md-2
                          :padding-right    sm-6}
                :height  (px 50)}))

(defmethod main ::error-modal/title-wrapper
  [{:as props {$ :variables} :re-com}]
  (merge-props props
               {:style {:font-size 25
                        :color     (:white $)
                        :padding   0
                        :margin    "0px"}}))

(defmethod main ::error-modal/triangle
  [{:as            props
    {{:keys [severity]} :error-modal
     $                  :variables} :re-com}]
  (merge-props props
               {:style {:fill (case severity
                                :error   (:error $)
                                :warning (:warning $)
                                "#1e1e1e")}}))

(defmethod main ::error-modal/code
  [{:as props {$ :variables} :re-com}]
  (merge-props props
               {:style {:font-family "monospace"
                        :white-space "pre-wrap"
                        :font-size   :font-size/xx-small
                        :color       (:neutral $)}}))

(defmethod main ::error-modal/body
  [{:as props {{:keys [md-2 sm-4]} :variables} :re-com}]
  (merge-props props
               {:style {:padding (str sm-4 " " md-2)}}))

(defmethod main ::dropdown/anchor
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

(defmethod main ::tree-select/dropdown-anchor
  [{:keys          [state]
    {$ :variables} :re-com
    :as            props}]
  (merge-style props {:padding  "0 0 0 0"
                      :overflow "hidden"
                      :color    (:foreground $)
                      :cursor   (if (-> state :enable (= :disabled))
                                  "default" "pointer")}))

(defmethod main ::tree-select/dropdown-indicator
  [{{$ :variables} :re-com
    :as            props}]
  (merge-props props {:align :center
                      :style {:gap   "5px"
                              :color (:light-foreground $)}}))

(defmethod main
  ::tree-select/dropdown-indicator-triangle
  [{{$ :variables} :re-com
    :as props}]
  (merge-props props
               {:align :center
                :style {:gap   "5px"
                        :color (:foreground $)}}))

(defmethod main ::tree-select/dropdown-counter
  [props]
  (merge-style props
               {:style {#_#_:margin-left  "5px"
                        #_#_:margin-right "5px"
                        :opacity          "50%"}}))
