(ns re-com.alert
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.buttons  :refer [button]]
            [re-com.box      :refer [h-box v-box scroller border]]
            [re-com.validate :refer [extract-arg-data validate-args hiccup-or-string? alert-type? vector-of-maps?]]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def alert-box-args-desc
  [{:name :id              :required false                  :type "anything"                                          :description "a unique identifier, usually an integer or string"}
   {:name :alert-type      :required false :default "info"  :type "string"           :validate-fn alert-type?         :description "a bootstrap style: info, warning or danger"}
   {:name :heading         :required false                  :type "hiccup | string"  :validate-fn hiccup-or-string?   :description "displayed as header. One of :heading or :body must be provided"}
   {:name :body            :required false                  :type "hiccup | string"  :validate-fn hiccup-or-string?   :description "displayed within the body of the alert"}
   {:name :padding         :required false :default "15px"  :type "string"           :validate-fn string?             :description "padding surounding the alert"}
   {:name :closeable?      :required false :default false   :type "boolean"                                           :description "if true, render a close button.  :on-close should be supplied"}
   {:name :on-close        :required false                  :type "(:id) -> nil"     :validate-fn  fn?                :description "called when the user clicks a close 'X'. Passed the :id of the alert to close."}])

(def alert-box-args (extract-arg-data alert-box-args-desc))

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed."
  [& {:keys [id alert-type heading body padding closeable? on-close]
      :or   {alert-type "info"}
      :as   args}]
  {:pre [(validate-args alert-box-args args)]}
  (let [close-button [button
                      :label    "×"
                      :on-click (handler-fn (on-close id))
                      :class    "close"]
        alert-type    (if (= alert-type "info")
                        "success"
                        alert-type)]
    [:div
     {:class (str "rc-alert alert fade in alert-" alert-type)
      :style {:flex    "none"
              :padding (when padding padding)}}
     (when heading
       [h-box
        :justify :between
        :align :center
        :style {:margin-bottom (if body "10px" "0px")}
        :children [[:h4
                    {:style {:margin-bottom "0px"}} ;; Override h4
                    heading]
                   (when (and closeable? on-close)
                     close-button)]])
     (when body
       [h-box
        :justify :between
        :align :center
        :children [[:div body]
                   (when (and (not heading) closeable? on-close)
                     close-button)]])]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-args-desc
  [{:name :alerts        :required false                                :type "atom vector of maps" :validate-fn vector-of-maps? :description "atom containing alerts to render in a list, in order"}
   {:name :on-close      :required false                                :type "(:id) -> nil"        :validate-fn fn?             :description "called when the user clicks a close 'X'. Passed the alert's :id"}
   {:name :max-height    :required false :default "grows forever"       :type "string"              :validate-fn string?         :description "CSS style for list height."}
   {:name :padding       :required false :default "4px"                 :type "string"              :validate-fn string?         :description "CSS padding within the alert."}
   {:name :border-style  :required false :default "1px solid lightgrey" :type "string"              :validate-fn string?         :description "CSS border style surrounding the list"}])

(def alert-list-args (extract-arg-data alert-list-args-desc))

(defn alert-list
  "Displays a list of alert-box components in a v-box. Sample alerts object:
     [{:id 2
       :alert-type \"warning\"
       :heading \"Heading\"
       :body \"Body\"
       :padding \"8px\"
       :closeable? true}
      {:id 1
       :alert-type \"info\"
       :heading \"Heading\"
       :body \"Body\"}]"
  [& {:keys [alerts on-close max-height padding border-style]
      :or   {padding "4px"}
      :as   args}]
  {:pre [(validate-args alert-list-args args)]}
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
                                                   :closeable? closeable?
                                                   :on-close   on-close]))]]]])
