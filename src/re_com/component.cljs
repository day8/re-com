(ns re-com.component
  (:require
    [goog.object   :as gobj]
    [reagent.core  :as r]
    [react :as react]
    [reagent.impl.component :as comp]
    [reagent.impl.batching :as batch]
    [re-com.config :refer [debug?]]
    [re-com.debug  :refer [src->__source src->props]]))


#_(defn create-class
   [spec src]
   (let [constructor (fn [this props]
                       (let [js-props (gobj/get this "props")]
                         (gobj/set js-props "__source" (src->__source src))
                         (js/console.log (type js-props))
                         (js/console.log js-props)))
         new-spec (assoc spec :constructor constructor)]
     (r/create-class new-spec)))


(defn create-class
  [spec src]
  (let [cmp (r/create-class spec)]
    (when debug? ;; This is in a separate `when` so Google Closure dead code elimination can run...
       (when src
         ;; reagent.impl.component/built-in-static-method-names does not include :defaultProps as a static property as it
         ;; should, so we need to manually extend cmp ourselves:
         (gobj/extend cmp #js {:defaultProps (src->props src)})))
    cmp))

#_(defn create-class
    "Creates JS class based on provided Clojure map.

  Map keys should use `React.Component` method names (https://reactjs.org/docs/react-component.html),
  and can be provided in snake-case or camelCase.
  Constructor function is defined using key `:getInitialState`.

  React built-in static methods or properties are automatically defined as statics."
    [body src]
    {:pre [(map? body)]}
    (let [body (comp/cljsify body)
          methods (comp/map-to-js (apply dissoc body :displayName :getInitialState :constructor
                                         :render :reagentRender
                                         comp/built-in-static-method-names))
          static-methods (comp/map-to-js (select-keys body comp/built-in-static-method-names))
          display-name (:displayName body)
          get-initial-state (:getInitialState body)
          construct (:constructor body)
          cmp (fn [props context updater]
                (let [js-props #js {:argv (.-argv props)
                                    :__source (src->__source src)}]
                  (this-as this
                    (.call react/Component this js-props context updater)
                    (when construct
                      (construct this js-props))
                    (when get-initial-state
                      (set! (.-state this) (get-initial-state this)))
                    (set! (.-cljsMountOrder ^clj this) (batch/next-mount-count))
                    this)))]

      (gobj/extend (.-prototype cmp) (.-prototype react/Component) methods)

      ;; These names SHOULD be mangled by Closure so we can't use goog/extend

      (when (:render body)
        (set! (.-render ^js (.-prototype cmp)) (:render body)))

      (when (:reagentRender body)
        (set! (.-reagentRender ^clj (.-prototype cmp)) (:reagentRender body)))

      (when (:cljsLegacyRender body)
        (set! (.-cljsLegacyRender ^clj (.-prototype cmp)) (:cljsLegacyRender body)))

      (gobj/extend cmp react/Component static-methods)

      (when display-name
        (set! (.-displayName cmp) display-name)
        (set! (.-cljs$lang$ctorStr cmp) display-name)
        (set! (.-cljs$lang$ctorPrWriter cmp)
              (fn [this writer opt]
                (cljs.core/-write writer display-name))))

      (set! (.-cljs$lang$type cmp) true)
      (set! (.. cmp -prototype -constructor) cmp)

      cmp))