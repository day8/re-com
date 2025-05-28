(ns re-com.theme
  (:refer-clojure :exclude [comp])
  (:require
   [re-com.theme.util :as tu]
   [re-com.part :as part]
   [re-com.theme.default :as theme.default]))

(def ^:dynamic variables theme.default/variables)
(def ^:dynamic base theme.default/base)
(def ^:dynamic main theme.default/main)
(def ^:dynamic user nil)

(def args-desc
  [{:name        :theme
    :validate-fn ifn?
    :description [:span "See the theme section of re-com docs (TBD). "
                  "This argument is not reactive."]}
   {:name        :pre-theme
    :validate-fn ifn?
    :description [:span "See the theme section of re-com docs (TBD). "
                  "This argument is not reactive."]}])

(def merge-class tu/merge-class)

(def merge-style tu/merge-style)

(def merge-props tu/merge-props)

(defn part-class [{:keys [part] :as props}]
  (update props :class merge-class (part/css-class* part)))

(defn comp [component-local-pre-theme component-local-theme]
  (clojure.core/apply
   clojure.core/comp
   (filter some? [component-local-theme
                  part-class
                  *user*
                  *main*
                  *base*
                  component-local-pre-theme
                  *variables*])))
