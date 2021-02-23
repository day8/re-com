(ns re-com.modal-panel
  (:require-macros
    [re-com.core     :refer [handler-fn at]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.validate :refer [string-or-hiccup? number-or-string? css-style? html-attr? parts?]]))

;; ------------------------------------------------------------------------------------
;;  modal-panel
;; ------------------------------------------------------------------------------------

(def modal-panel-parts-desc
  (when include-args-desc?
    [{:type :legacy          :level 0 :class "rc-modal-panel"           :impl "[modal-panel]" :notes "Outer wrapper of the modal panel, backdrop, everything."}
     {:name :backdrop        :level 1 :class "rc-modal-panel-backdrop"  :impl "[:div]"        :notes "Semi-transparent backdrop, which prevents other user interaction."}
     {:name :child-container :level 1 :class "rc-modal-panel-container" :impl "[:div]"        :notes [:span "The container for the " [:code ":child"] "component."]}]))

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
     {:name :class             :required false                  :type "string"          :validate-fn string?                    :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style             :required false                  :type "CSS style map"   :validate-fn css-style?                 :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr              :required false                  :type "HTML attr map"   :validate-fn html-attr?                 :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts             :required false                  :type "map"             :validate-fn (parts? modal-panel-parts) :description "See Parts section below."}
     {:name :src               :required false                  :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as          :required false                  :type "map"             :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))


(defn modal-panel
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
   main window to prevent UI interactivity and place user focus on the modal window.
   Parameters:
    - child:  The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)"
  [& {:keys [child wrap-nicely? backdrop-color backdrop-opacity backdrop-on-click class style attr parts]
      :or   {wrap-nicely? true backdrop-color "black" backdrop-opacity 0.6}
      :as   args}]
  (or
    (validate-args-macro modal-panel-args-desc args)
    [:div    ;; Containing div
     (merge {:class  (str "display-flex rc-modal-panel " class)
             :style (merge {:position "fixed"
                            :left     "0px"
                            :top      "0px"
                            :width    "100%"
                            :height   "100%"
                            :z-index  1020}
                           style)}
            (->attr args)
            attr)
     [:div    ;; Backdrop
      (merge
        {:class    (str "rc-modal-panel-backdrop " (get-in parts [:backdrop :class]))
         :style    (merge {:position         "fixed"
                           :width            "100%"
                           :height           "100%"
                           :background-color backdrop-color
                           :opacity          backdrop-opacity
                           :z-index          1}
                          (get-in parts [:backdrop :style]))
         :on-click (handler-fn (when backdrop-on-click (backdrop-on-click))
                               (.preventDefault event)
                               (.stopPropagation event))}
        (get-in parts [:backdrop :attr]))]
     [:div    ;; Child container
      (merge
        {:class (str  "rc-modal-panel-child-container " (get-in parts [:child-container :class]))
         :style (merge {:margin  "auto"
                        :z-index 2}
                       (get-in parts [:child-container :style])
                       (when wrap-nicely? {:background-color "white"
                                           :padding          "16px"
                                           :border-radius    "6px"}))}
        (get-in parts [:child-container :attr]))
      child]]))
