(ns re-com.alert
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [deref-or-value]]
            [re-com.buttons  :refer [button]]
            [re-com.box      :refer [h-box v-box box scroller border flex-child-style]]
            [re-com.validate :refer [string-or-hiccup? alert-type? alert-types-list
                                     vector-of-maps? css-style? html-attr?] :refer-macros [validate-args-macro]]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def alert-box-args-desc
  [{:name :id         :required false                 :type "anything"                                       :description [:span "a unique identifier, usually an integer or string."]}
   {:name :alert-type :required false :default :info  :type "keyword"         :validate-fn alert-type?       :description [:span "one of " alert-types-list]}
   {:name :heading    :required false                 :type "string | hiccup" :validate-fn string-or-hiccup? :description [:span "displayed as a larger heading. One of " [:code ":heading"] " or " [:code ":body"] " should be provided"]}
   {:name :body       :required false                 :type "string | hiccup" :validate-fn string-or-hiccup? :description "displayed within the body of the alert"}
   {:name :padding    :required false :default "15px" :type "string"          :validate-fn string?           :description "padding surounding the alert"}
   {:name :closeable? :required false :default false  :type "boolean"                                        :description [:span "if true, render a close button. " [:code ":on-close"] " should be supplied"]}
   {:name :on-close   :required false                 :type ":id -> nil"      :validate-fn fn?               :description [:span "called when the user clicks the close 'X' button. Passed the " [:code ":id"] " of the alert to close"]}
   {:name :class      :required false                 :type "string"          :validate-fn string?           :description "CSS classes (whitespace separated). Applied to outer container"}
   {:name :style      :required false                 :type "CSS style map"   :validate-fn css-style?        :description "CSS styles. Applied to outer container"}
   {:name :attr       :required false                 :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed. Applied to outer container"]}])

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed"
  [& {:keys [id alert-type heading body padding closeable? on-close class style attr]
      :or   {alert-type :info}
      :as   args}]
  {:pre [(validate-args-macro alert-box-args-desc args "alert-box")]}
  (let [close-button [button
                      :label    [:i {:class "md-close"
                                     :style {:font-size "20px"}}]    ;"×"
                      :on-click (handler-fn (on-close id))
                      :class    "close"]
        alert-type    (if (= alert-type :info)
                        "success"
                        (name alert-type))]
    [:div
     (merge {:class (str "rc-alert alert fade in alert-" alert-type " " class)
             :style (merge (flex-child-style "none")
                           {:padding (when padding padding)}
                           style)}
            attr)
     (when heading
       [h-box
        :justify  :between
        :align    :center
        :style    {:margin-bottom (if body "10px" "0px")}
        :children [[:h4
                    {:style {:margin-bottom "0px"}} ;; Override h4
                    heading]
                   (when (and closeable? on-close)
                     close-button)]])
     (when body
       [h-box
        :justify  :between
        :align    :center
        :children [[:div body]
                   (when (and (not heading) closeable? on-close)
                     close-button)]])]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-args-desc
  [{:name :alerts       :required true                                 :type "vector of maps | atom" :validate-fn vector-of-maps? :description "alerts to render (in the order supplied). Can also be a list of maps"}
   {:name :on-close     :required true                                 :type ":id -> nil"            :validate-fn fn?             :description [:span "called when the user clicks the close 'X' button. Passed the alert's " [:code ":id"]]}
   {:name :max-height   :required false                                :type "string"                :validate-fn string?         :description "CSS style for maximum list height. By default, it grows forever"}
   {:name :padding      :required false :default "4px"                 :type "string"                :validate-fn string?         :description "CSS padding within the alert"}
   {:name :border-style :required false :default "1px solid lightgrey" :type "string"                :validate-fn string?         :description "CSS border style surrounding the list"}
   {:name :class        :required false                                :type "string"                :validate-fn string?         :description "CSS class names, space separated. Applied to outer container"}
   {:name :style        :required false                                :type "CSS style map"         :validate-fn css-style?      :description "CSS styles. Applied to outer container"}
   {:name :attr         :required false                                :type "HTML attr map"         :validate-fn html-attr?      :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed. Applied to outer container"]}])

(defn alert-list
  "Displays a list of alert-box components in a v-box. Sample alerts object:
     [{:id 2
       :alert-type :warning
       :heading \"Heading\"
       :body \"Body\"
       :padding \"8px\"
       :closeable? true}
      {:id 1
       :alert-type :info
       :heading \"Heading\"
       :body \"Body\"}]"
  [& {:keys [alerts on-close max-height padding border-style class style attr]
      :or   {padding "4px"}
      :as   args}]
  {:pre [(validate-args-macro alert-list-args-desc args "alert-list")]}
  (let [alerts (deref-or-value alerts)]
    [box
     :child [border
             :padding padding
             :border border-style
             :class class
             :style style
             :attr attr
             :child [scroller
                     :v-scroll :auto
                     :style {:max-height max-height}
                     :child [v-box
                             :size "auto"
                             :children [(for [alert alerts]
                                          (let [{:keys [id alert-type heading body padding closeable?]} alert]
                                            ^{:key id} [alert-box
                                                        :id id
                                                        :alert-type alert-type
                                                        :heading heading
                                                        :body body
                                                        :padding padding
                                                        :closeable? closeable?
                                                        :on-close on-close]))]]]]]))
