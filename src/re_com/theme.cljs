(ns re-com.theme
  (:refer-clojure :exclude [apply merge])
  (:require
   [clojure.string :as str]
   [reagent.core :as r]
   [re-com.theme.util :as tu]
   [re-com.theme.template :as template]
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

(def merge-class tu/merge-class)

(defn merge [a {:re-com/keys [system] :keys [base main user main-variables user-variables base-variables] :as b}]
  (cond-> a
    base-variables (assoc  :base-variables      base-variables)
    main-variables (assoc  :main-variables      main-variables)
    user-variables (update :user-variables conj user-variables)
    base           (assoc  :base                base)
    main           (assoc  :main                main)
    user           (update :user           conj user)))

(defn rf [[props ctx] theme]
  (let [result (if (template/template? theme)
                 (template/template theme props ctx)
                 (theme props ctx))]
    (if (vector? result) result [result ctx])))

(defn apply
  ([props ctx themes]
   (->>
    (cond
      (map? themes) (re-com.theme/merge @registry themes)
      :else         (update @registry :user conj themes))
    named->vec
    flatten
    (remove nil?)
    (reduce rf [props ctx])
    first)))

(defn with-state [& args] {::template ::with-state :args args})

(defn parts [& args] {::template ::parts :args args})

(defn <-props [& args] {::template ::<-props :args args})

(defn top-level-part [{:keys [theme] :as props} part]
  (cond-> props
    theme (re-com.theme/apply {:part part} theme)
    theme (merge props)))

(defn part-class [props {:keys [part]}]
  (update props :class merge-class
          (str (str/replace (namespace part) "re-com." "rc-")
               "-" (name part))))

(defn defaults [{:keys [theme-vars base-theme main-theme theme parts]} & themes]
  (re-com.theme/merge
   {:variables     theme-vars
    :base          base-theme
    :main          main-theme
    :user          [theme
                    (re-com.theme/parts parts)
                    part-class]}
   themes))
