(ns re-com.modal-panel
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.modal-panel.theme
   [re-com.modal-panel :as-alias mp]
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.part     :as part]
   [re-com.theme    :as theme]
   [re-com.theme.util :as tu]
   [re-com.validate :refer [string-or-hiccup? number-or-string?]]))

;; ------------------------------------------------------------------------------------
;;  modal-panel
;; ------------------------------------------------------------------------------------

(def part-structure
  [::mp/wrapper {:type :legacy :notes "Outer wrapper of the modal panel, backdrop, everything."}
   [::mp/backdrop {:tag :div :notes "Semi-transparent backdrop, which prevents other user interaction."}]
   [::mp/child-container {:tag :div}]])

(def modal-panel-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def modal-panel-parts
  (when include-args-desc?
    (-> (map :name modal-panel-parts-desc) set)))

(def modal-panel-args-desc
  (when include-args-desc?
    [{:name :child             :required true                   :type "string | hiccup" :validate-fn string-or-hiccup?          :description "hiccup to be centered within in the browser window"}
     {:name :wrap-nicely?      :required false :default true    :type "boolean"                                                 :description [:span "if true, wrap " [:code ":child"] " in a white, rounded panel"]}
     {:name :backdrop-color    :required false :default "black" :type "string"          :validate-fn string?                    :description "CSS color of backdrop"}
     {:name :backdrop-opacity  :required false :default 0.6     :type "double | string" :validate-fn number-or-string?          :description [:span "opacity of backdrop from:" [:br] "0.0 (transparent) to 1.0 (opaque)"]}
     {:name :backdrop-on-click :required false :default nil     :type "-> nil"          :validate-fn fn?                        :description "a function which takes no params and returns nothing. Called when the backdrop is clicked"}
     args/pre
     args/theme
     args/class
     args/style
     args/attr
     (args/parts modal-panel-parts)
     args/src
     args/debug-as]))

(defn modal-panel
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
   main window to prevent UI interactivity and place user focus on the modal window.
   Parameters:
    - child:  The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)"
  [& {:keys [pre-theme theme] :as args}]
  (let [theme (theme/comp pre-theme theme)]
    (fn modal-panel-render
      [& {:keys [child wrap-nicely? backdrop-color backdrop-opacity backdrop-on-click class style attr src debug-as]
          :or   {wrap-nicely? true backdrop-color "black" backdrop-opacity 0.6}
          :as   args}]
      (or
       (validate-args-macro modal-panel-args-desc args)
       (let [part       (partial part/part part-structure args)
             re-com-ctx {:state {:wrap (if wrap-nicely? :nicely :default)}}]
         (part ::mp/wrapper
           {:theme      theme
            :post-props (-> {}
                            (cond-> class (tu/class class)
                                    style (tu/style style)
                                    attr  (assoc :attr attr))
                            (debug/instrument args))
            :props
            {:re-com   re-com-ctx
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :tag      :div
             :children
             [(part ::mp/backdrop
                {:theme theme
                 :props {:re-com re-com-ctx
                         :style  {:background-color backdrop-color
                                  :opacity          backdrop-opacity}
                         :attr   {:on-click (handler-fn (when backdrop-on-click (backdrop-on-click))
                                                        (.preventDefault event)
                                                        (.stopPropagation event))}}})
              (part ::mp/child-container
                {:theme      theme
                 :props      {:re-com re-com-ctx
                              :children [child]}})]}}))))))
