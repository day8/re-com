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
  [{:name :id              :required false                  :type "any"           :description "a unique identifier for the alert, usually an integer or string, but could be anything."}
   {:name :alert-type      :required false :default "info"  :type "string"        :description "a bootstrap style: \"info\", \"warning\" or \"danger\"."}
   {:name :heading         :required false                  :type "markup|string" :description "describes the heading text. One of either heading or body must be provided."}
   {:name :body            :required false                  :type "markup|string" :description "describes the body of the alert. One of either heading or body must be provided."}
   {:name :padding         :required false :default "15px"  :type "string"        :description "CSS style for padding within the alert."}
   {:name :closeable?      :required false :default false   :type "boolean"       :description "if true, a close button 'X' is rendered (<code>:on-close</code> must also be supplied)."}
   {:name :on-close        :required false                  :type "function"      :description "the callback when the user clicks the close 'X'. Invoked with the single <code>:id</code> parameter. Required when <code>:closeable?</code> is true."}])

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
  [{:name :alerts          :required false                  :type "vector"    :description "containing alert maps to be rendered. The order is specified by the calling app."}
   {:name :on-close        :required false                  :type "function"  :description "the callback when the user clicks the close 'X'. Invoked with the single <code>:id</code> parameter."}
   {:name :max-height      :required false                  :type "string"    :description "CSS style describing the height this component can grow to grow as alerts are added. Default is to expand forever."}
   {:name :padding         :required false :default "4px"   :type "string"    :description "CSS style describing the padding within the alert."}
   {:name :border-style    :required false :default "1px solid lightgrey"   :type "string"   :description "CSS style describing the border style around the alert-list outer box."}])

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
