(ns re-com.progress-bar
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [deref-or-value px]]
    [re-com.popover  :refer [popover-tooltip]]
    [re-com.box      :refer [h-box v-box box gap line flex-child-style align-style]]
    [re-com.validate :refer [input-status-type? input-status-types-list regex? string-or-hiccup? css-style? html-attr? parts?
                                     number-or-string? string-or-atom? nillable-string-or-atom? throbber-size? throbber-sizes-list]]))

;; ------------------------------------------------------------------------------------
;;  Component: progress-bar
;; ------------------------------------------------------------------------------------

(def progress-bar-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-progress-bar-wrapper" :impl "[progress-bar]" :notes "Outer wrapper of the progress bar."}
     {:type :legacy  :level 1 :class "rc-progress-bar"         :impl "[:div]"         :notes "The container for the progress bar."}
     {:type :legacy  :level 2 :class "rc-progress-bar-portion" :impl "[:div]"         :notes "The portion of the progress bar complete so far."
      :name-label [:span "Use " [:code ":bar-class"] " instead."]}]))

(def progress-bar-parts
  (when include-args-desc?
    (-> (map :name progress-bar-parts-desc) set)))

(def progress-bar-args-desc
  (when include-args-desc?
    [{:name :model     :required true                  :type "double | string | r/atom" :validate-fn number-or-string?           :description "current value of the slider. A number between 0 and 100"}
     {:name :width     :required false :default "100%" :type "string"                   :validate-fn string?                     :description "a CSS width"}
     {:name :striped?  :required false :default false  :type "boolean"                                                           :description "when true, the progress section is a set of animated stripes"}
     {:name :bar-class :required false                 :type "string"                   :validate-fn string?                     :description "CSS class name(s) for the actual progress bar itself, space separated"}
     {:name :class     :required false                 :type "string"                   :validate-fn string?                     :description "CSS class names, space separated (applies to the progress-bar, not the wrapping div)"}
     {:name :style     :required false                 :type "CSS style map"            :validate-fn css-style?                  :description "CSS styles to add or override (applies to the progress-bar, not the wrapping div)"}
     {:name :attr      :required false                 :type "HTML attr map"            :validate-fn html-attr?                  :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the progress-bar, not the wrapping div)"]}
     {:name :parts     :required false                 :type "map"                      :validate-fn (parts? progress-bar-parts) :description "See Parts section below."}
     {:name :src       :required false                 :type "map"                      :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as  :required false                 :type "map"                      :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn progress-bar
  "Render a bootstrap styled progress bar"
  [& {:keys [model width striped? class bar-class style attr parts src debug-as]
      :or   {width "100%"}
      :as   args}]
  (or
    (validate-args-macro progress-bar-args-desc args)
    (let [model (deref-or-value model)]
      [box
       :src      src
       :debug-as (or debug-as (reflect-current-component))
       :class    (str "rc-progress-bar-wrapper " (get-in parts [:wrapper :class]))
       :style    (get-in parts [:wrapper :style])
       :attr     (get-in parts [:wrapper :attr])
       :align    :start
       :child    [:div
                  (merge
                    {:class (str "progress rc-progress-bar " class)
                     :style (merge (flex-child-style "none")
                                   {:width width}
                                   style)}
                    attr)
                  [:div
                   {:class (str "progress-bar " (when striped? "progress-bar-striped active rc-progress-bar-portion ") bar-class)
                    :role  "progressbar"
                    :style {:width      (str model "%")
                            :transition "none"}}                 ;; Default BS transitions cause the progress bar to lag behind
                   (str model "%")]]])))
