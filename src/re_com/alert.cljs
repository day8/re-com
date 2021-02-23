(ns re-com.alert
  (:require-macros
    [re-com.core         :refer [handler-fn at reflect-current-component]])
  (:require
    [re-com.box          :refer [h-box v-box box scroller border flex-child-style]]
    [re-com.buttons      :refer [button]]
    [re-com.close-button :refer [close-button]]
    [re-com.config       :refer [include-args-desc?]]
    [re-com.debug        :refer [->attr]]
    [re-com.util         :refer [deref-or-value]]
    [re-com.validate     :refer [string-or-hiccup? alert-type? alert-types-list
                                 vector-of-maps? css-style? html-attr? parts?] :refer-macros [validate-args-macro]]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def alert-box-parts-desc
  (when include-args-desc?
    [{:type :legacy       :level 0 :class "rc-alert-box"          :impl "[alert-box]"}
     {:name :heading      :level 1 :class "rc-alert-heading"      :impl "[h-box]"}
     {:name :h4           :level 2 :class "rc-alert-h4"           :impl "[:h4]"}
     {:name :close-button :level 2 :class "rc-alert-close-button" :impl "[close-button]"}
     {:name :body         :level 1 :class "rc-alert-body"         :impl "[h-box]"}]))

(def alert-box-parts
  (when include-args-desc?
    (-> (map :name alert-box-parts-desc) set)))

(def alert-box-args-desc
  (when include-args-desc?
    [{:name :id         :required false                 :type "anything"                                              :description [:span "a unique identifier, usually an integer or string."]}
     {:name :alert-type :required false :default :info  :type "keyword"         :validate-fn alert-type?              :description [:span "one of " alert-types-list]}
     {:name :heading    :required false                 :type "string | hiccup" :validate-fn string-or-hiccup?        :description [:span "displayed as a larger heading. One of " [:code ":heading"] " or " [:code ":body"] " should be provided"]}
     {:name :body       :required false                 :type "string | hiccup" :validate-fn string-or-hiccup?        :description "displayed within the body of the alert"}
     {:name :padding    :required false :default "15px" :type "string"          :validate-fn string?                  :description "padding surounding the alert"}
     {:name :closeable? :required false :default false  :type "boolean"                                               :description [:span "if true, render a close button. " [:code ":on-close"] " should be supplied"]}
     {:name :on-close   :required false                 :type ":id -> nil"      :validate-fn fn?                      :description [:span "called when the user clicks the close 'X' button. Passed the " [:code ":id"] " of the alert to close"]}
     {:name :class      :required false                 :type "string"          :validate-fn string?                  :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style      :required false                 :type "CSS style map"   :validate-fn css-style?               :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr       :required false                 :type "HTML attr map"   :validate-fn html-attr?               :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts      :required false                 :type "map"             :validate-fn (parts? alert-box-parts) :description "See Parts section below."}
     {:name :src        :required false                 :type "map"             :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as   :required false                 :type "map"             :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed"
  [& {:keys [id alert-type heading body padding closeable? on-close class style attr parts]
      :or   {alert-type :info}
      :as   args}]
  (or
    (validate-args-macro alert-box-args-desc args)
    (let [close-alert  [close-button
                        :src       (at)
                        :class     (str "rc-alert-close-button " (get-in parts [:close-button :class]))
                        :style     (get-in parts [:close-button :style])
                        :attr      (get-in parts [:close-button :attr])
                        :on-click  #(on-close id)
                        :div-size  20
                        :font-size 20]
          alert-class  (alert-type {:none           ""
                                    :info           "alert-success"
                                    :warning        "alert-warning"
                                    :danger         "alert-danger"})]
      [:div
       (merge {:class (str "rc-alert alert fade in " alert-class " " class)
               :style (merge (flex-child-style "none")
                             {:padding padding}
                             style)}
              (->attr args)
              attr)
       (when heading
         [h-box
          :src      (at)
          :justify  :between
          :align    :center
          :class    (str "rc-alert-heading " (get-in parts [:heading :class]))
          :style    (merge {:margin-bottom (if body "10px" "0px")}
                           (get-in parts [:heading :style]))
          :attr     (get-in parts [:heading :attr] {})
          :children [[:h4
                      (merge
                        {:class (str "rc-alert-h4 " (get-in parts [:h4 :class]))
                         :style (merge {:margin-bottom "0px"}
                                       (get-in parts [:h4 :style]))}
                        (get-in parts [:h4 :attr])) ;; Override h4
                      heading]
                     (when (and closeable? on-close)
                       close-alert)]])
       (when body
         [h-box
          :src      (at)
          :justify  :between
          :align    :center
          :class    (str "rc-alert-body " (get-in parts [:body :class]))
          :style    (get-in parts [:body :style] {})
          :attr     (get-in parts [:body :attr] {})
          :children [[:div body]
                     (when (and (not heading) closeable? on-close)
                       close-alert)]])])))

;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-parts-desc
  (when include-args-desc?
    [{:name :wrapper  :level 0 :class "rc-alert-list-wrapper"  :impl "[alert-list]"}
     {:type :legacy   :level 1 :class "rc-alert-list"          :impl "[border]"}
     {:name :scroller :level 2 :class "rc-alert-list-scroller" :impl "[scroller]"}
     {:name :v-box    :level 2 :class "rc-alert-list-v-box"    :impl "[v-box]"}
     {:type :legacy   :level 3 :class "rc-alert-box"           :impl "[alert-box]"
      :name-label [:span "Use " [:code ":alert-class"] " or " [:code ":alert-style"] " arguments instead."]}]))

(def alert-list-parts
  (when include-args-desc?
    (-> (map :name alert-list-parts-desc) set)))

(def alert-list-args-desc
  (when include-args-desc?
    [{:name :alerts       :required true                                 :type "vector of maps | r/atom" :validate-fn vector-of-maps?           :description "alerts to render (in the order supplied). Can also be a list of maps"}
     {:name :on-close     :required true                                 :type ":id -> nil"              :validate-fn fn?                       :description [:span "called when the user clicks the close 'X' button. Passed the alert's " [:code ":id"]]}
     {:name :max-height   :required false                                :type "string"                  :validate-fn string?                   :description "CSS style for maximum list height. By default, it grows forever"}
     {:name :padding      :required false :default "4px"                 :type "string"                  :validate-fn string?                   :description "CSS padding within the alert"}
     {:name :border-style :required false :default "1px solid lightgrey" :type "string"                  :validate-fn string?                   :description "CSS border style surrounding the list"}
     {:name :alert-class  :required false                                :type "string"                  :validate-fn string?                   :description "CSS class names, space separated (applies to each alert-box component)"}
     {:name :alert-style  :required false                                :type "CSS style map"           :validate-fn css-style?                :description "CSS styles (applies to each alert-box component)"}
     {:name :class        :required false                                :type "string"                  :validate-fn string?                   :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style        :required false                                :type "CSS style map"           :validate-fn css-style?                :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr         :required false                                :type "HTML attr map"           :validate-fn html-attr?                :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts        :required false                                :type "map"                     :validate-fn (parts? alert-list-parts) :description "See Parts section below."}
     {:name :src          :required false                                :type "map"                     :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as     :required false                                :type "map"                     :validate-fn map?                      :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

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
  [& {:keys [alerts on-close max-height padding border-style alert-class alert-style class style attr parts src debug-as]
      :or   {padding "4px"}
      :as   args}]
  (or
    (validate-args-macro alert-list-args-desc args)
    (let [alerts (deref-or-value alerts)]
      [box
       :src      src
       :debug-as (or debug-as (reflect-current-component))
       :class    (str "rc-alert-list-wrapper " (get-in parts [:wrapper :class]))
       :style    (get-in parts [:wrapper :style] {})
       :attr     (get-in parts [:wrapper :attr] {})
       :child    [border
                  :src     (at)
                  :class   (str "rc-alert-list " class)
                  :style   style
                  :attr    attr
                  :padding padding
                  :border  border-style
                  :child   [scroller
                            :src      (at)
                            :v-scroll :auto
                            :class    (str "rc-alert-list-scroller " (get-in parts [:scroller :class]))
                            :style    (merge {:max-height max-height}
                                             (get-in parts [:scroller :style]))
                            :attr     (get-in parts [:scroller :attr])
                            :child    [v-box
                                       :src      (at)
                                       :size     "auto"
                                       :class    (str "rc-alert-list-v-box " (get-in parts [:v-box :class]))
                                       :style    (get-in parts [:v-box :style])
                                       :attr     (get-in parts [:v-box :attr])
                                       :children [(for [alert alerts]
                                                    (let [{:keys [id alert-type heading body padding closeable?]} alert]
                                                      ^{:key id} [alert-box
                                                                  :src        (at)
                                                                  :id         id
                                                                  :alert-type alert-type
                                                                  :heading    heading
                                                                  :body       body
                                                                  :padding    padding
                                                                  :closeable? closeable?
                                                                  :on-close   on-close
                                                                  :class      alert-class
                                                                  :style      (merge alert-style (:style alert))]))]]]]])))
