(ns reagent-components.modal
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent-components.util :as util]
            [cljs.core.async         :as async :refer [<! >! chan close! sliding-buffer put! alts!]]
            [reagent.core            :as reagent]
            ;[goog.dom                :as dom]
            [goog.events             :as events]))


(defn listen [el type]
  (let [out (chan)]
    (events/listen el type (fn [e] (put! out e)))
    out))


;;-------------------------------------------------------------------------------
;; WITH core.async
;;-------------------------------------------------------------------------------

(defn modal-dialog [markup button-id show-modal?]
  (let []
    (util/console-log "In modal-dialog")
    (reagent/create-class
     {
      :component-did-mount
      (fn []
        (let [elem   (util/get-element-by-id button-id) ;; dom/getElement
              clicks (listen elem "click")]

          (util/console-log (str "modal-dialog :component-did-mount - " (.-value elem)))
          (go (while true
                (<! clicks)
                (util/console-log "CANCEL CLICKED")
                (reset! show-modal? false)
                )))
        )

      :render
      (fn []
        (util/console-log "modal-dialog :render")
        [:div
         {:style {:display "flex"
                  :position "fixed"
                  :left "0px"
                  :top "0px"
                  :width "100%"
                  :height "100%"
                  :background-color "rgba(0,0,0,0.85)"
                  :z-index 1020}}
         [:div
          {:style {:margin "auto"
                   :background-color "white"
                   :padding "16px"
                   :border-radius "6px"
                   :z-index 1020}}
          [markup]]]
        )
      })))


;;-------------------------------------------------------------------------------
;; Original without core.async
;;-------------------------------------------------------------------------------

#_(defn modal-dialog [markup button-id show-modal?]
  (let []
    (util/console-log "In modal-dialog")
    [:div
     {:style {:display "flex"
              :position "fixed"
              :left "0px"
              :top "0px"
              :width "100%"
              :height "100%"
              :background-color "rgba(0,0,0,0.85)"
              :z-index 1020}}
     [:div
      {:style {:margin "auto"
               :background-color "white"
               :padding "16px"
               :border-radius "6px"
               :z-index 1020}}
      [markup]]]))


