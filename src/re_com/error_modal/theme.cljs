(ns re-com.error-modal.theme
  (:require
   [re-com.util :refer [px]]
   [re-com.theme.util :refer [merge-props]]
   [re-com.theme.default :refer [main]]
   [re-com.error-modal :as-alias em]))

(defmethod main ::em/modal
  [props]
  (merge-props props
               {:wrap-nicely? false
                :style        {:z-index 50}}))

(defmethod main ::em/inner-wrapper
  [{:as            props
    {$ :variables} :re-com}]
  (merge-props props
               {:style {:background-color (:white $)
                        :box-shadow       "2.82843px 2.82843px 4px rgba(1,1,1,0.2)"
                        :font-size        (:font-size/medium $)
                        :min-width        (px 474)
                        :min-height       (px 300)
                        :max-width        (px 525)}}))

(defmethod main ::em/top-bar
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

(defmethod main ::em/title-wrapper
  [{:as props {$ :variables} :re-com}]
  (merge-props props
               {:style {:font-size 25
                        :color     (:white $)
                        :padding   0
                        :margin    "0px"}}))

(defmethod main ::em/triangle
  [{:as            props
    {{:keys [severity]} :error-modal
     $                  :variables} :re-com}]
  (merge-props props
               {:style {:fill (case severity
                                :error   (:error $)
                                :warning (:warning $)
                                "#1e1e1e")}}))

(defmethod main ::em/code
  [{:as props {$ :variables} :re-com}]
  (merge-props props
               {:style {:font-family "monospace"
                        :white-space "pre-wrap"
                        :font-size   :font-size/xx-small
                        :color       (:neutral $)}}))

(defmethod main ::em/body
  [{:as props {{:keys [md-2 sm-4]} :variables} :re-com}]
  (merge-props props
               {:style {:padding (str sm-4 " " md-2)}}))
