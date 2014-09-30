(ns re-com.alert
  (:require [re-com.core  :refer [button]]
            [re-com.box   :refer [v-box scroller border]]
            [re-com.util  :as    util]
            [reagent.core :as    reagent]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(defn alert
  [& {:keys [id alert-type heading body padding closeable on-close]}]
  "Displays one alert box. A close button allows the message to be removed.
   Parameters:
    - :id           A unique identifier, usually an integer or string, but could be any complex data structure
                    This is optional for single alerts. It's main use is in alert-list below
    - :alert-type   A Bootstrap string determining the style. Either 'info', 'warning' or 'danger'
    - :heading      Hiccup markup or a string containing the heading text
    - :body         Hiccup markup or a string containing the body of the alert
    - :padding      The amount of padding within the alert (default is 15px)
    - :closeable    A boolean which determines if the close button is rendered (on-close must also be specified)
    - :on-close     A callback function which knows how to close the alert"
  [:div.alert.fade.in
   {:class (str "alert-" alert-type)
    :style {:flex "none" :padding (when padding padding)}}
   (when (and closeable on-close)
     [button
      :label "×"
      :on-click #(on-close id)
      :class "close"])
   (when heading [:h4 (when-not body {:style {:margin "0px"}}) heading])
   (when body [:p body])])


;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(defn alert-list
  [& {:keys [alerts on-close border-style]}]
  "Displays a list of alert components in a v-box
   Parameters:
    - :alerts       An atom containing a map of alerts. Would normally use (sorted-map-by >) so that latest alerts are
                    rendered at the top. A typical map will look like this (substitute double quotes for single quotes):
                      {2 {:alert-type 'warning'
                          :heading 'Heading'
                          :body 'Body'
                          :padding '8px'
                          :closeable true}
                       1 {:alert-type 'info'
                          :heading 'Heading'
                          :body 'Body'}}
    - :on-close     A callback function which knows how to close an alert based on the id passed to it.
                    Usually something like this:
                      #(swap! alerts dissoc %)
    - :border-style The border style around the outside of the component. e.g. '1px dashed lightgrey' "
  [border
   :padding "4px"
   :border  (when border-style border-style)
   :child [scroller
           :v-scroll :auto
           :height "300px"
           :child [v-box
                   :size "auto"
                   :children [(for [one-alert @alerts]
                                (let [id (first one-alert)
                                      {:keys [alert-type heading body padding closeable]} (last one-alert)]
                                  ^{:key id} [alert
                                              :id id
                                              :alert-type alert-type
                                              :heading heading
                                              :body body
                                              :padding padding
                                              :closeable closeable
                                              :on-close on-close]))]]]])
