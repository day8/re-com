(ns re-com.close-button
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.close-button.theme
   [re-com.close-button :as-alias cb]
   [re-com.args     :as args]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :as debug]
   [re-com.theme    :as    theme]
   [re-com.part     :as    part]
   [re-com.util     :refer [deref-or-value px]]
   [re-com.validate :refer [string-or-hiccup? css-style? css-class? html-attr? parts?]]
   [re-com.box      :refer [box]]
   [reagent.core    :as    reagent]))

;; ------------------------------------------------------------------------------------
;;  Component: close-button
;;
;;  Note: Should be in buttons module but that requires popover and
;;        popover requires this button
;; ------------------------------------------------------------------------------------

;; TODO: Add a demo page for this

(def close-button-part-structure
  [::cb/wrapper {:impl 're-com.box/box}
   [::cb/button {:impl 're-com.box/box}
    [::cb/icon {:tag :i}]]])

(def close-button-parts-desc
  (when include-args-desc?
    (part/describe close-button-part-structure)))

(def close-button-parts
  (when include-args-desc?
    (-> (map :name close-button-parts-desc) set)))

(def close-button-args-desc
  (when include-args-desc?
    (into
     [{:name :on-click     :required false                   :type "-> nil"           :validate-fn fn?                        :description "a function which takes no params and returns nothing. Called when the button is clicked"}
      {:name :div-size     :required false  :default 16      :type "number"           :validate-fn number?                    :description "numeric px size of the div containing the close button (can be 0 because the 'x' button text is absolutely positioned and centered within the div)"}
      {:name :font-size    :required false  :default 16      :type "number"           :validate-fn number?                    :description "numeric px font size of the 'x' button text"}
      {:name :color        :required false  :default "#ccc"  :type "string"           :validate-fn string?                    :description "HTML color of the 'x' button text"}
      {:name :hover-color  :required false  :default "#999"  :type "string"           :validate-fn string?                    :description "HTML color of the button text when the mouse is hovering over it"}
      {:name :tooltip      :required false                   :type "string | hiccup"  :validate-fn string-or-hiccup?          :description "what to show in the tooltip"}
      {:name :top-offset   :required false                   :type "number"           :validate-fn number?                    :description "offset the 'x' button text up or down from it's default position in the containing div (can be positive or negative)"}
      {:name :left-offset  :required false                   :type "number"           :validate-fn number?                    :description "offset the 'x' button text left or right from it's default position in the containing div (can be positive or negative)"}
      {:name :disabled?    :required false  :default false   :type "boolean | r/atom"                                         :description "if true, the user can't click the button"}
      args/class
      args/style
      args/attr
      (args/parts close-button-parts)
      args/src
      args/debug-as]
     (concat theme/args-desc
             (part/describe-args close-button-part-structure)))))

(defn close-button
  [& {:keys [pre-theme theme debug-as] :as props}]
  (let [theme (theme/comp pre-theme theme)
        over? (reagent/atom false)]
    (fn [& {:keys [on-click div-size font-size color hover-color tooltip top-offset left-offset disabled?]
            :or   {div-size 16 font-size 16 color "#ccc" hover-color "#999"}
            :as   args}]
      (or
       (validate-args-macro close-button-args-desc args)
       (let [disabled?  (deref-or-value disabled?)
             part       (partial part/part close-button-part-structure args)
             re-com-ctx {:state {:div-size     div-size
                                 :font-size    font-size
                                 :color        color
                                 :hover-color  hover-color
                                 :top-offset   top-offset
                                 :left-offset  left-offset
                                 :disabled?    disabled?
                                 :hover?       @over?}}]
         (part ::cb/wrapper
           {:impl       box
            :theme      theme
            :post-props (-> {:debug-as (or debug-as (reflect-current-component))}
                            (debug/instrument args))
            :props      {:re-com   re-com-ctx
                         :src      (at)
                         :child    (part ::cb/button
                                     {:impl       box
                                      :theme      theme
                                      :post-props (-> (select-keys args [:class :style :attr])
                                                      (update :attr merge {:title          tooltip
                                                                           :on-click       (handler-fn
                                                                                            (when (and on-click (not disabled?))
                                                                                              (on-click event)
                                                                                              (.stopPropagation event)))
                                                                           :on-mouse-enter (handler-fn (reset! over? true))
                                                                           :on-mouse-leave (handler-fn (reset! over? false))}))
                                      :props      {:re-com   re-com-ctx
                                                   :src      (at)
                                                   :child    (part ::cb/icon
                                                               {:theme theme
                                                                :props {:re-com re-com-ctx
                                                                        :src    (at)
                                                                        :tag    :i}})}})}}))))))
