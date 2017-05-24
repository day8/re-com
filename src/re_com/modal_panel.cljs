(ns re-com.modal-panel
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.validate :refer [string-or-hiccup? number-or-string? css-style? html-attr?] :refer-macros [validate-args-macro]]))

;; ------------------------------------------------------------------------------------
;;  modal-panel
;; ------------------------------------------------------------------------------------

(def modal-panel-args-desc
  [{:name :child             :required true                   :type "string | hiccup" :validate-fn string-or-hiccup? :description "hiccup to be centered within in the browser window"}
   {:name :wrap-nicely?      :required false :default true    :type "boolean"                                        :description [:span "if true, wrap " [:code ":child"] " in a white, rounded panel"]}
   {:name :backdrop-color    :required false :default "black" :type "string"          :validate-fn string?           :description "CSS color of backdrop"}
   {:name :backdrop-opacity  :required false :default 0.6     :type "double | string" :validate-fn number-or-string? :description [:span "opacity of backdrop from:" [:br] "0.0 (transparent) to 1.0 (opaque)"]}
   {:name :backdrop-on-click :required false :default nil     :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the backdrop is clicked"}
   {:name :class             :required false                  :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style             :required false                  :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr              :required false                  :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn modal-panel
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
   main window to prevent UI interactivity and place user focus on the modal window.
   Parameters:
    - child:  The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)"
  [& {:keys [child wrap-nicely? backdrop-color backdrop-opacity backdrop-on-click class style attr]
      :or   {wrap-nicely? true backdrop-color "black" backdrop-opacity 0.6}
      :as   args}]
  {:pre [(validate-args-macro modal-panel-args-desc args "modal-panel")]}
  [:div    ;; Containing div
   (merge {:class  (str "rc-modal-panel display-flex " class)
           :style (merge {:position "fixed"
                          :left     "0px"
                          :top      "0px"
                          :width    "100%"
                          :height   "100%"
                          :z-index  1020}
                         style)}
          attr)
   [:div    ;; Backdrop
    {:style    {:position         "fixed"
                :width            "100%"
                :height           "100%"
                :background-color backdrop-color
                :opacity          backdrop-opacity
                :z-index          1}
     :on-click (handler-fn (when backdrop-on-click (backdrop-on-click))
                           (.preventDefault event)
                           (.stopPropagation event))}]
   [:div    ;; Child container
    {:style (merge {:margin  "auto"
                    :z-index 2}
                   (when wrap-nicely? {:background-color "white"
                                       :padding          "16px"
                                       :border-radius    "6px"}))}
    child]])
