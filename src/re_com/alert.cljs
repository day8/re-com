(ns re-com.alert
  (:require [clojure.set  :refer [superset?]]
            [re-com.core  :refer [button]]
            [re-com.box   :refer [v-box scroller border]]
            [re-com.util  :as    util]
            [reagent.core :as    reagent]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def alert-box-args-desc
  [{:name :id              :required false                  :type "anything"         :description "a unique identifier, usually an integer or string"}
   {:name :alert-type      :required false :default "info"  :type "string"           :description "a bootstrap style: info, warning or danger"}
   {:name :heading         :required false                  :type "hicup | string"   :description "displayed as header. One of :heading or :body must be provided"}
   {:name :body            :required false                  :type "hicup | string"   :description "displayed within the body of the alert"}
   {:name :padding         :required false :default "15px"  :type "string"           :description "padding surounding the alert"}
   {:name :closeable?      :required false :default false   :type "boolean"          :description "if true, render a close button.  :on-close should be supplied"}
   {:name :on-close        :required false                  :type "(:id) -> nil"     :description "called when the user clicks a close 'X'. Passed the :id of the alert to close."}])

(def alert-box-args
  (set (map :name alert-box-args-desc)))

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed."
  [& {:keys [id alert-type heading body padding closeable? on-close]
      :or   {alert-type "info"}
      :as   args}]
  {:pre [(util/validate-arguments alert-box-args (keys args))]}
  [:div.alert.fade.in
   {:class (str "alert-" alert-type)
    :style {:flex "none" :padding (when padding padding)}}
   (when (and closeable? on-close)
     [button
      :label    "×"
      :on-click #(on-close id)
      :class    "close"])
   (when heading [:h4 (when-not body {:style {:margin "0px"}}) heading])
   (when body [:p body])])


;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-args-desc
  [{:name :alerts        :required false                                :type "vector of maps" :description "alerts to render in a list, in order"}
   {:name :on-close      :required false                                :type "(:id) -> nil"   :description "called when the user clicks a close 'X'. Passed the alert's :id"}
   {:name :max-height    :required false :default "grow forever"        :type "string"         :description "CSS style for list height."}
   {:name :padding       :required false :default "4px"                 :type "string"         :description "CSS padding within the alert."}
   {:name :border-style  :required false :default "1px solid lightgrey" :type "string"         :description "CSS border style surrounding the list"}])

(def alert-list-args
  (set (map :name alert-list-args-desc)))

(defn alert-list
  "Displays a list of alert-box components in a v-box."
  [& {:keys [alerts on-close max-height padding border-style]
      :or   {padding "4px"}
      :as   args}]
  {:pre [(util/validate-arguments alert-list-args (keys args))]}
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
