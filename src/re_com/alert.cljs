(ns re-com.alert
  (:require [clojure.set  :refer [superset?]]
            [re-com.core  :refer [button]]
            [re-com.box   :refer [v-box scroller border]]
            [re-com.util  :as    util]
            [reagent.core :as    reagent]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def alert-box-args
  #{:id           ; A unique identifier for the alert, usually an integer or string, but could be anything.
    :alert-type   ; A string contining a bootstrap style: 'info', 'warning' or 'danger'
    :heading      ; Hiccup markup or a string containing the heading text
    :body         ; Hiccup markup or a string containing the body of the alert
    :padding      ; The amount of padding within the alert (default is 15px)
    :closeable?   ; Should a close 'X' button is rendered (:on-close must also be supplied)
    :on-close     ; The function to call back when the user clicks the close 'X'. Invoked with the single :id parameter"
    })


(defn alert-box
  "Displays one alert box. A close button allows the message to be removed."
  [& {:keys [id alert-type heading body padding closeable? on-close]
      :or   {alert-type "info"}
      :as   args}]
  {:pre [(superset? alert-box-args (keys args))]}
  [:div.alert.fade.in
   {:class (str "alert-" alert-type)
    :style {:flex "none" :padding (when padding padding)}}
   (when (and closeable? on-close)
     [button
      :label "×"
      :on-click #(on-close id)
      :class "close"])
   (when heading [:h4 (when-not body {:style {:margin "0px"}}) heading])
   (when body [:p body])])


;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-args
  #{:alerts         ; An atom containing a vector of alert maps. A typical alerts vector will look like:
                    ;     [{:id 2
                    ;       :alert-type "warning"
                    ;       :heading "Heading"
                    ;       :body "Body"
                    ;       :padding "8px"
                    ;       :closeable? true}
                    ;      {:id 1
                    ;        :alert-type "info"
                    ;       :heading "Heading"
                    ;       :body "Body"}]
    :on-close       ; The function to call back when the user clicks the close 'X' of an item. Invoked with the a single :id parameter.
    :max-height     ; The initial height of this component is 0px and grows to this maximum as alerts are added. Default is to expand forever.
    :padding        ; Padding within the alert-list outer box. Default is 4px.
    :border-style   ; The border style around the alert-list outer box. Default is "1px solid lightgrey".
    })


(defn alert-list
  "Displays a list of alert-box components in a v-box."
  [& {:keys [alerts on-close max-height padding border-style]
      :or   {padding "4px"}
      :as   args}]
  {:pre [(superset? alert-list-args (keys args))]}
  [border
   :padding padding
   :border  border-style
   :child [scroller
           :v-scroll   :auto
           :style      {:max-height max-height}
           :child      [v-box
                        :size "auto"
                        :children [(for [alert @alerts]
                                     (let [{:keys [id alert-type heading body padding closeable?]} alert]
                                       ^{:key id} [alert-box
                                                   :id         id
                                                   :alert-type alert-type
                                                   :heading    heading
                                                   :body       body
                                                   :padding    padding
                                                   :closeable?  closeable?
                                                   :on-close   on-close]))]]]])
