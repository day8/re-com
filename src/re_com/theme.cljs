(ns re-com.theme
  (:refer-clojure :exclude [apply merge])
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

(defn merge [a {:keys [base main user main-variables user-variables base-variables] :as b}]
  (cond-> a
    base-variables (assoc  :base-variables      base-variables)
    main-variables (assoc  :main-variables      main-variables)
    user-variables (update :user-variables conj user-variables)
    base           (assoc  :base                base)
    main           (assoc  :main                main)
    user           (update :user           conj user)))

(defn apply
  ([props ctx themes]
   (->>
    (if-not (map? themes)
      (update @registry :user conj themes)
      (merge @registry themes))
    named->vec
    flatten
    (remove nil?)
    (reduce rf [props ctx])
    first)))

(defn props [ctx themes]
  (apply {} ctx themes))

(defn remove-keys [m ks]
  (select-keys m (remove (set ks) (keys m))))

(defn <-props [outer-props
               & {:keys [part exclude include]
                  :or   {include [:style :attr :class
                                  :width :min-width :max-width
                                  :height :min-height :max-height]
                         exclude []}}]
  (fn [props ctx _]
    (let [outer-style-keys [:width  :min-width :max-width
                            :height :max-height :min-width :min-height]
          outer-attr-keys  [:tab-index]
          outer-props      (cond-> outer-props
                             (seq include) (select-keys include)
                             (seq exclude) (remove-keys exclude))]
      (cond-> props
        (= part (:part ctx))
        (-> (merge-props (remove-keys outer-props (concat outer-style-keys outer-attr-keys)))
            (update :style clojure.core/merge
                    (select-keys outer-props outer-style-keys))
            (update :attr clojure.core/merge
                    (select-keys outer-props outer-attr-keys)))))))
