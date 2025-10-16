(ns re-com.alert
  (:require-macros
   [re-com.core         :refer [at]])
  (:require
   [re-com.args :as args]
   re-com.alert-box.theme
   re-com.alert-list.theme
   [re-com.box          :refer [h-box v-box box scroller border]]
   [re-com.close-button :refer [close-button]]
   [re-com.config       :refer [include-args-desc?]]
   [re-com.debug        :as debug]
   [re-com.part         :as part]
   [re-com.alert-box    :as-alias ab]
   [re-com.alert-list   :as-alias al]
   [re-com.theme        :as    theme]
   [re-com.theme.util   :as    tu]
   [re-com.util         :refer [deref-or-value]]
   [re-com.validate     :refer [alert-type? alert-types-list
                                vector-of-maps? css-style?] :refer-macros [validate-args-macro]]))

;;--------------------------------------------------------------------------------------------------
;; Component: alert
;;--------------------------------------------------------------------------------------------------

(def part-structure
  [::ab/wrapper {:impl 're-com.core/v-box}
   [::ab/header {:impl 're-com.core/h-box}
    [::ab/heading-wrapper {:tag :h4}
     [::ab/heading {:top-level-arg? true}]]
    [::ab/close-button {:impl 're-com.close-button/close-button}]]
   [::ab/body-wrapper {:impl 're-com.core/h-box}
    [::ab/body {:top-level-arg? true}]]])

(def alert-box-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def alert-box-parts
  (when include-args-desc?
    (-> (map :name alert-box-parts-desc) set)))

(def alert-box-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :id         :required false                 :type "anything"                                              :description [:span "a unique identifier, usually an integer or string."]}
       {:name :alert-type :required false :default :info  :type "keyword"         :validate-fn alert-type?              :description [:span "one of " alert-types-list]}
       {:name :padding    :required false :default "15px" :type "string"          :validate-fn string?                  :description "padding surounding the alert"}
       {:name :closeable? :required false :default false  :type "boolean"                                               :description [:span "if true, render a close button. " [:code ":on-close"] " should be supplied"]}
       {:name :on-close   :required false                 :type ":id -> nil"      :validate-fn fn?                      :description [:span "called when the user clicks the close 'X' button. Passed the " [:code ":id"] " of the alert to close"]}
       args/class
       args/style
       args/attr
       (args/parts alert-box-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args part-structure)))))

(defn alert-box
  "Displays one alert box. A close button allows the message to be removed"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn alert-box-render
      [& {:keys [id alert-type padding closeable? on-close]
          :or   {alert-type :info}
          :as   props}]
      (or
       (validate-args-macro alert-box-args-desc props)
       (let [part        (partial part/part part-structure props)
             heading?    (part/get-part part-structure props ::ab/heading)
             body?       (part/get-part part-structure props ::ab/body)
             re-com      {:state {:alert-type alert-type
                                  :body       body?
                                  :closeable? closeable?
                                  :padding    padding}}
             close-alert (part ::ab/close-button
                           {:impl       close-button
                            :theme      theme
                            :post-props {:src (at)}
                            :props      {:re-com    re-com
                                         :on-click  #(on-close id)
                                         :div-size  20
                                         :font-size 20}})]
         (part ::ab/wrapper
           {:impl       v-box
            :theme      theme
            :post-props (-> props
                            (select-keys [:class :style :attr])
                            (cond-> padding (tu/style {:padding padding}))
                            (debug/instrument props))
            :props      {:re-com re-com
                         :children
                         [(when heading?
                            (part ::ab/header
                              {:impl       h-box
                               :theme      theme
                               :post-props {:src (at)}
                               :props      {:re-com re-com
                                            :children
                                            [(part ::ab/heading-wrapper
                                               {:theme      theme
                                                :post-props {:src (at)}
                                                :props      {:tag      :h4
                                                             :re-com   re-com
                                                             :children [(part ::ab/heading
                                                                          {:theme theme
                                                                           :props {:re-com re-com}})]}})
                                             (when (and closeable? on-close)
                                               close-alert)]}}))
                          (when body?
                            (part ::ab/body-wrapper
                              {:impl       h-box
                               :theme      theme
                               :post-props {:src (at)}
                               :props      {:re-com re-com
                                            :children
                                            [(part ::ab/body
                                               {:theme theme
                                                :props {:re-com re-com}})
                                             (when (and (not heading?) closeable? on-close)
                                               close-alert)]}}))]}}))))))

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
    (vec
     (concat
      [{:name :alerts :required true :type "vector of maps | r/atom" :validate-fn vector-of-maps? :description "alerts to render (in the order supplied). Can also be a list of maps"}
       {:name :on-close :required true :type ":id -> nil" :validate-fn fn? :description [:span "called when the user clicks the close 'X' button. Passed the alert's " [:code ":id"]]}
       {:name :max-height :required false :type "string" :validate-fn string? :description "CSS style for maximum list height. By default, it grows forever"}
       {:name :padding :required false :default "4px" :type "string" :validate-fn string? :description "CSS padding within the alert"}
       {:name :border-style :required false :default "1px solid lightgrey" :type "string" :validate-fn string? :description "CSS border style surrounding the list"}
       {:name :alert-class :required false :type "string" :validate-fn string? :description "CSS class names, space separated (applies to each alert-box component)"}
       {:name :alert-style :required false :type "CSS style map" :validate-fn css-style? :description "CSS styles (applies to each alert-box component)"}
       args/class
       args/style
       args/attr
       (args/parts alert-list-parts)
       args/src
       args/debug-as
       args/src]
      theme/args-desc
      (part/describe-args alert-list-part-structure)))))

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
         part   (partial part/part alert-list-part-structure props)
         re-com {:state {:max-height   max-height
                         :padding      padding
                         :border-style border-style
                         :alert-class  alert-class
                         :alert-style  alert-style}}]
     (part ::al/wrapper
       {:impl       box
        :theme      theme
        :post-props (debug/instrument {} props)
        :props
        {:re-com re-com
         :child
         (part ::al/body
           {:impl       border
            :theme      theme
            :post-props (-> props
                            (select-keys [:class :style :attr])
                            (assoc :border border-style
                                   :padding padding))
            :props
            {:re-com re-com
             :child
             (part ::al/scroller
               {:impl       scroller
                :theme      theme
                :post-props {:style (when max-height {:max-height max-height})}
                :props      {:re-com re-com
                             :child
                             (part ::al/v-box
                               {:impl  v-box
                                :theme theme
                                :props
                                {:re-com re-com
                                 :children
                                 (for [alert alerts]
                                   (let [{:keys [id alert-type heading body padding closeable?]} alert]
                                     (part ::al/alert-box
                                       {:impl       alert-box
                                        :theme      theme
                                        :key        id
                                        :post-props {:class alert-class
                                                     :style alert-style}
                                        :props
                                        {:id         id
                                         :alert-type alert-type
                                         :heading    heading
                                         :body       body
                                         :padding    padding
                                         :closeable? closeable?
                                         :on-close   on-close}})))}})}})}})}}))))
