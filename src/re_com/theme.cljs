(ns re-com.theme
  (:refer-clojure :exclude [apply])
  (:require
   [reagent.core :as r]
   [re-com.theme.util :as tu]
   [re-com.theme.default :as theme.default]))

(def registry (r/atom {:base-variables theme.default/base-variables
                       :main-variables theme.default/main-variables
                       :user-variables []
                       :base           theme.default/base
                       :main           theme.default/main
                       :user           []}))

(def named->vec
  (memoize
   (juxt :base-variables :main-variables :user-variables :base :main :user)))

(def global (r/reaction (flatten (named->vec @registry))))

(def merge-props tu/merge-props)

(def parts tu/parts)

(defn rf [[props ctx] theme]
  (let [result (theme props ctx)]
    (if (vector? result) result [result ctx])))

(defn apply
  ([props ctx themes]
   (->>
    (if-not (map? themes)
      (update @registry :user conj themes)
      (let [{:keys [base main user main-variables user-variables base-variables]} themes]
        (cond-> @registry
          base-variables (assoc  :base-variables      base-variables)
          main-variables (assoc  :main-variables      main-variables)
          user-variables (update :user-variables conj user-variables)
          base           (assoc  :base                base)
          main           (assoc  :main                main)
          user           (update :user           conj user))))
    named->vec
        flatten
        (remove nil?)
        (reduce rf [props ctx])
        first)))

(defn props [ctx themes]
  (apply {} ctx themes))
