(ns re-com.component
  (:require
    [goog.object   :as gobj]
    [reagent.core  :as r]
    [re-com.config :refer [debug?]]
    [re-com.debug  :refer [src->props]]))

(defn create-class
  [spec src]
  (let [cmp (r/create-class spec)]
    (when debug? ;; This is in a separate `when` so Google Closure dead code elimination can run...
      (when src
        ;; reagent.impl.component/built-in-static-method-names does not include :defaultProps as a static property as it
        ;; should, so we need to manually extend cmp ourselves:
        (gobj/extend cmp #js {:defaultProps (src->props src)})))
    cmp))

