(ns reagent-components.alert
  (:require [reagent-components.util :as util]
            [reagent.core :as reagent]))

			
;;--------------------------------------------------------------------------------------------------
;; Component: closeable-alert
;; 
;;   Displays one Bootstrap alert box. A close button allows the message to be removed.
;;   See function for more details
;; 
;;   Notes/todo:
;;    - Either validate alert-type or find a better way to specify the alert
;;--------------------------------------------------------------------------------------------------

(defn closeable-alert
  "Displays one Bootstrap alert box. A close button allows the message to be removed.
  Parameters:
  - alert-item:       A map containing the definition of the alert:
  .   - :id           A unique identifier, usually an integer or string
  .   - :alert-type   A Bootstrap string determining the style. Either 'info', 'warning' or 'danger'
  .   - :heading      Hiccup markup or a string containing the heading text
  .   - :body         Hiccup markup or a string containing the body of the alert
  - close-callback:   A callback function which knows how to close the alert"
  [alert-item close-callback]

  (let [{:keys [id alert-type heading body]} alert-item]
    (fn []
      (util/console-log (str "in closeable-alert for id #" (:id alert-item)))
      [:div.alert.fade.in {:class (str "alert-" alert-type)}
       [:button.close {:type "button"
                       :on-click #(close-callback id)} "×"]
       [:h4 heading] ;; (str id ": " heading)
       [:p body]])))


;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;
;;   To render this component, simply add the following hiccup where required:
;;       [alert/alert-list]
;;
;;   To add a new alert to the list, for example:
;;       (alert/add-alert "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
;;
;;   Notes/todo:
;;    - Currently uses internal storage for alerts
;;    - User can click the close button of an alert to close it. Again handled internally
;;    - Currently not currently generic. Only a single alert-list can be rendered
;;--------------------------------------------------------------------------------------------------

(def alerts       (reagent/atom (sorted-map-by >)))
(def alerts-count (reagent/atom 0))

(defn close-alert [id]
  (util/console-log (str "closing alert #" id))
  (swap! alerts dissoc id)
  (util/console-log-prstr "after close" @alerts))


(defn add-alert [alert-type {:keys [heading body]}]
  (let [id (swap! alerts-count inc)]
    (swap! alerts assoc id {:id id :alert-type alert-type :heading heading :body body})
    (util/console-log-prstr "after add" @alerts)))


(defn alert-list
  "Displays a list of closeable-alerts
  Parameters:
  - alert-items:      An atom containing a map of alerts. Normally (sorted-map-by >) so that latest alerts are rendered at the top
  - close-callback:   A callback function which knows how to close an alert"
  [alert-items close-callback] ;; TODO: Currently hard coded to interal version

  (fn []
    (util/console-log "in alert-list")
    [:div {:style {:border "1px dashed lightgrey"}}
     (for [alert @alerts]
       (do
         (util/console-log (str "metadata :key=" (:id (last alert))))
         ^{:key (:id (last alert))} [closeable-alert (last alert) close-alert]))]))
