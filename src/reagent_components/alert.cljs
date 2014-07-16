(ns reagent-components.alert
  (:require [reagent-components.util :as util]
            [reagent.core :as reagent]))


(def alerts (reagent/atom (sorted-map-by >)))
(def alert-count (reagent/atom 0))


(defn close-alert [id]
  (util/console-log (str "closing alert #" id))
  (swap! alerts dissoc id)
  (util/console-log-prstr "after close" @alerts)
  )
;; (defn close-alert [id]
;;   (util/console-log (str "closing alert #" id))
;;   (swap! state/app-state update-in [:alerts] dissoc id)
;;   (util/console-log-prstr "after close" (:alerts @state/app-state))
;;   ) ;; [POPOVER]


(defn add-alert [alert-type {:keys [heading body]}]
  (let [id (swap! alert-count inc)]
    (swap! alerts assoc id {:id id :alert-type alert-type :heading heading :body body})
    (util/console-log-prstr "after add" @alerts)
;;     (swap! state/app-state assoc-in [:alerts id] {:id id :alert-type alert-type :heading heading :body body}) ;; [POPOVER]
;;     (util/console-log-prstr "after add" (:alerts @state/app-state))
    ))


(defn closeable-alert [alert-item close-callback]
  "Displays one Bootstrap alert box. A close button allows the message to be removed.
  Parameters:
  - alert-type is a string of either info, warning or danger
  - a map containing :heading and :body strings"

  ;; TODO
  ;;    - Either validate alert-type or find a better way
  ;;    - Allow them to enter artibrary html for the alert body
  ;;
  (let [{:keys [id alert-type heading body]} alert-item]
    (fn []
      (util/console-log (str "in closeable-alert for id #" (:id alert-item)))
      [:div.alert.fade.in {:class (str "alert-" alert-type)}
       [:button.close {:type "button"
                       :on-click #(close-callback id)} "×"]
       [:h4 (str id ": " heading)]
       [:p body]])))


(defn alert-list [alert-items close-callback]
  "Displays a list of closeable-alerts"

  (fn []
    (util/console-log "in alert-list")
    [:div#alert-list {:style {:border "1px dashed lightgrey"}} ;; TODO: id is only required for (popover-old ...), not (popover ...)
;;      (for [alert @alerts] ^{:key (:id (last alert))} [closeable-alert (last alert) close-callback])]))
;;      (for [alert alerts] ;; FAILS BECAUSE YOU MUST PASS LIVE ATOM RATHER THAN A MAP WITHIN IT
;;      (for [alert (:alerts @state/app-state)]
     (for [alert @alerts]
       (do
         (util/console-log (str "metadata :key=" (:id (last alert))))
         ^{:key (:id (last alert))} [closeable-alert (last alert) close-callback]
         )
       )])) ;; [POPOVER]
