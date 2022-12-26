(ns re-com.close-button
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value px]]
    [re-com.validate :refer [string-or-hiccup? css-style? html-attr? parts?] :refer-macros [validate-args-macro]]
    [re-com.box      :refer [box]]
    [reagent.core    :as    reagent]))


;; ------------------------------------------------------------------------------------
;;  Component: close-button
;;
;;  Note: Should be in buttons module but that requires popover and
;;        popover requires this button
;; ------------------------------------------------------------------------------------

;; TODO: Add a demo page for this

(def close-button-args-desc
  (when include-args-desc?
    [{:name :on-click     :required false                   :type "-> nil"           :validate-fn fn?                        :description "a function which takes no params and returns nothing. Called when the button is clicked"}
     {:name :div-size     :required false  :default 16      :type "number"           :validate-fn number?                    :description "numeric px size of the div containing the close button (can be 0 because the 'x' button text is absolutely positioned and centered within the div)"}
     {:name :font-size    :required false  :default 16      :type "number"           :validate-fn number?                    :description "numeric px font size of the 'x' button text"}
     {:name :color        :required false  :default "#ccc"  :type "string"           :validate-fn string?                    :description "HTML color of the 'x' button text"}
     {:name :hover-color  :required false  :default "#999"  :type "string"           :validate-fn string?                    :description "HTML color of the button text when the mouse is hovering over it"}
     {:name :tooltip      :required false                   :type "string | hiccup"  :validate-fn string-or-hiccup?          :description "what to show in the tooltip"}
     {:name :top-offset   :required false                   :type "number"           :validate-fn number?                    :description "offset the 'x' button text up or down from it's default position in the containing div (can be positive or negative)"}
     {:name :left-offset  :required false                   :type "number"           :validate-fn number?                    :description "offset the 'x' button text left or right from it's default position in the containing div (can be positive or negative)"}
     {:name :disabled?    :required false  :default false   :type "boolean | r/atom"                                         :description "if true, the user can't click the button"}
     {:name :class        :required false                   :type "string"           :validate-fn string?                    :description "CSS class names, space separated (applies to the button, not the wrapping div)"}
     {:name :style        :required false                   :type "CSS style map"    :validate-fn css-style?                 :description "CSS styles (applies to the button, not the wrapping div)"}
     {:name :attr         :required false                   :type "HTML attr map"    :validate-fn html-attr?                 :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the button, not the wrapping div)"]}
     {:name :parts        :required false                   :type "map"              :validate-fn (parts? #{:wrapper :icon}) :description "See Parts section below."}
     {:name :src          :required false                   :type "map"              :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as     :required false                   :type "map"              :validate-fn map?                       :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn close-button
  []
  (let [over? (reagent/atom false)]
    (fn close-button-render
      [& {:keys [on-click div-size font-size color hover-color tooltip top-offset left-offset disabled? class style attr parts src debug-as] :as args
          :or   {div-size 16 font-size 16 color "#ccc" hover-color "#999"}}]
      (or
        (validate-args-macro close-button-args-desc args)
        (let [disabled?  (deref-or-value disabled?)]
          [box
           :src      src
           :debug-as (or debug-as (reflect-current-component))
           :class    (str "rc-close-button " (get-in parts [:wrapper :class]))
           :style    (merge {:display          "inline-block"
                             :position         "relative"
                             :width            (px div-size)
                             :height           (px div-size)}
                            (when disabled? {:pointer-events "none"})
                            (get-in parts [:wrapper :style]))
           :attr     (get-in parts [:wrapper :attr])
           :child    [box
                      :src   (at)
                      :class class
                      :style (merge
                               {:position  "absolute"
                                :cursor    (when-not disabled? "pointer")
                                :font-size (px font-size)
                                :color     (if @over? hover-color color)
                                :top       (px (- (/ (- font-size div-size) 2) top-offset)  :negative)
                                :left      (px (- (/ (- font-size div-size) 2) left-offset) :negative)}
                               style)
                      :attr  (merge
                               {:title          tooltip
                                :on-click       (handler-fn
                                                  (when (and on-click (not disabled?))
                                                    (on-click event)
                                                    (.stopPropagation event)))
                                :on-mouse-enter (handler-fn (reset! over? true))
                                :on-mouse-leave (handler-fn (reset! over? false))}
                               attr)
                      :child [:i
                              (merge
                                {:class (str "rc-close-button-icon zmdi zmdi-hc-fw-rc zmdi zmdi-close " (get-in parts [:icon :class]))
                                 :style (get-in parts [:icon :style] {})}
                                (get-in parts [:icon :attr]))]]])))))
