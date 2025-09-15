(ns re-com.alert
  (:require-macros
   [re-com.core         :refer [at reflect-current-component]])
  (:require
   re-com.alert-box.theme
   re-com.alert-list.theme
   [re-com.box          :refer [h-box v-box box scroller border flex-child-style]]
   [re-com.close-button :refer [close-button]]
   [re-com.config       :refer [include-args-desc?]]
   [re-com.debug        :as debug]
   [re-com.part         :as part]
   [re-com.alert-box    :as-alias ab]
   [re-com.alert-list   :as-alias al]
   [re-com.theme        :as    theme]
   [re-com.theme.util   :as    tu]
   [re-com.util         :refer [deref-or-value]]
   [re-com.validate     :refer [string-or-hiccup? alert-type? alert-types-list
                                vector-of-maps? css-style? css-class? html-attr? parts?] :refer-macros [validate-args-macro]]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def part-structure
  [::ab/wrapper {:impl 're-com.core/alert-box
                 :type :legacy}
   [::ab/header {:impl 're-com.core/h-box}
    [::ab/heading-wrapper {:tag :h4}
     [::ab/heading {:top-level-arg? true}]]
    [::ab/close-button {:impl 're-com.close-button/close-button}]]
   [::ab/body-wrapper
    [::ab/body {:top-level-arg? true}]]])

(def alert-box-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

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
     {:name :class      :required false                 :type "string"          :validate-fn css-class?                  :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style      :required false                 :type "CSS style map"   :validate-fn css-style?               :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr       :required false                 :type "HTML attr map"   :validate-fn html-attr?               :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts      :required false                 :type "map"             :validate-fn (parts? alert-box-parts) :description "See Parts section below."}
     {:name :src        :required false                 :type "map"             :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as   :required false                 :type "map"             :validate-fn map?                     :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed"
  [& {:keys [id alert-type body padding closeable? on-close pre-theme theme]
      :or   {alert-type :info}
      :as   props}]
  (or
   (validate-args-macro alert-box-args-desc props)
   (let [part              (partial part/part part-structure props)
         theme             (theme/comp pre-theme theme)
         heading-provided? (part/get-part part-structure props ::ab/heading)
         body-provided?    (part/get-part part-structure props ::ab/body)
         alert-class       (alert-type {:none    ""
                                        :info    "alert-success"
                                        :warning "alert-warning"
                                        :danger  "alert-danger"})
         close-alert       (part ::ab/close-button
                             {:impl       close-button
                              :post-props {:src (at)}
                              :props      {:on-click  #(on-close id)
                                           :div-size  20
                                           :font-size 20}})]
     (part ::ab/wrapper
       {:impl       v-box
        :post-props (-> props
                        (cond-> padding (tu/style {:padding padding}))
                        (select-keys [:class :style :attr])
                        (update :class theme/merge-class alert-class)
                        (debug/instrument props))
        :theme      theme
        :props
        {:children
         [(when heading-provided?
            (part ::ab/header
              {:impl       h-box
               :theme      theme
               :style      {:margin-bottom (if body "10px" "0px")}
               :post-props {:src (at)}
               :props
               {:children
                [(part ::ab/heading-wrapper
                   {:theme      theme
                    :post-props {:src (at)}
                    :props      {:tag      :h4
                                 :children [(part ::ab/heading
                                              {:theme theme})]}})
                 (when (and closeable? on-close)
                   close-alert)]}}))
          (when body-provided?
            (part ::ab/body-wrapper
              {:impl       h-box
               :theme      theme
               :post-props {:src (at)}
               :props      {:children
                            [(part ::ab/body
                               {:theme theme})
                             (when (and (not heading-provided?) closeable? on-close)
                               close-alert)]}}))]}}))))

;;--------------------------------------------------------------------------------------------------
;; Component: alert-list
;;--------------------------------------------------------------------------------------------------

(def alert-list-part-structure
  [::al/wrapper {:impl 're-com.alert/alert-list}
   [::al/body {:impl 're-com.box/border}
    [::al/scroller {:impl 're-com.box/scroller}]
    [::al/v-box {:impl 're-com.box/v-box}
     [::ab/alert-box {:impl       're-com.alert/alert-box
                      :name-label [:span "Use " [:code ":alert-class"] " or "
                                   [:code ":alert-style"] " arguments instead."]}]]]])

(def alert-list-parts-desc
  (when include-args-desc?
    (part/describe alert-list-part-structure)))

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
     {:name :class        :required false                                :type "string"                  :validate-fn css-class?                   :description "CSS class names, space separated (applies to the outer container)"}
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
  [& {:keys [alerts on-close max-height padding border-style alert-class alert-style pre-theme theme]
      :or   {padding "4px"}
      :as   props}]
  (or
   (validate-args-macro alert-list-args-desc props)
   (let [alerts (deref-or-value alerts)
         theme  (theme/comp pre-theme theme)
         part   (partial part/part alert-list-part-structure props)]
     (part ::al/wrapper
       {:impl       box
        :theme      theme
        :post-props (debug/instrument {} props)
        :props
        {:child
         (part ::al/body
           {:impl       border
            :theme      theme
            :post-props (merge (select-keys props [:class :style :attr])
                               {:border  border-style
                                :padding padding}
                               (tu/style {:max-height max-height}))
            :props
            {:child
             (part ::al/scroller
               {:impl  scroller
                :theme theme
                :props
                {:child
                 (part ::al/v-box
                   {:impl  v-box
                    :theme theme
                    :props
                    {:children
                     (for [alert alerts]
                       (let [{:keys [id alert-type heading body padding closeable?]} alert]
                         (part ::al/alert-box
                           {:impl       alert-box
                            :theme      theme
                            :key        id
                            :post-props {:class alert-class
                                         :style alert-style}
                            :props      {:id         id
                                         :alert-type alert-type
                                         :heading    heading
                                         :body       body
                                         :padding    padding
                                         :closeable? closeable?
                                         :on-close   on-close}})))}})}})}})}}))))
