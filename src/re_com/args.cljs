(ns re-com.args
  (:require
   [re-com.validate :refer [css-style? html-attr? parts? vector-of-maps? css-class?]]))

(def class
  {:name        :class,
   :required    false,
   :type        "string | vector",
   :validate-fn css-class?,
   :description [:span "See " [:a {:href "#/customization"} "Customization"]]})

(def style
  {:name :style,
   :required false,
   :type "CSS style map",
   :validate-fn css-style?,
   :description [:span "See " [:a {:href "#/customization"} "Customization"]]})

(def attr
  {:name :attr,
   :required false,
   :type "HTML attr map",
   :validate-fn html-attr?,
   :description [:span "See " [:a {:href "#/customization"} "Customization"]]})

(defn parts [part-names]
  {:name :parts,
   :required false,
   :type "map",
   :validate-fn (parts? part-names),
   :description [:span "See " [:a {:href "#/parts"} "Parts"]]})

(def pre {:name        :pre-theme
          :type        "map -> map"
          :description [:span "See " [:a {:href "#/theme"} "Theme"]]})

(def theme {:name        :theme
            :type        "map -> map"
            :description [:span "See " [:a {:href "#/theme"} "Theme"]]})

(def ref {:name :ref :description "re-com internal"})
(def data-rc-src {:name :data-rc-src :description "re-com internal"})
(def data-rc {:name :data-rc :description "re-com internal"})

(def src
  {:name :src
   :required false
   :type "map"
   :validate-fn map?
   :description [:span "See " [:a {:href "#/debug"} "Debugging"]]})

(def debug-as
  {:name        :debug-as,
   :required    false,
   :type        "map",
   :validate-fn map?,
   :description
   [:span
    "Used in dev builds to assist with debugging, when one component is used to implement another component, "
    "and we want the implementation component to masquerade as the original component in debug output, "
    "such as component stacks. A map optionally containing keys"
    [:code ":component"] "and" [:code ":args"] "."]})

(def debug [src debug-as])

(def std (concat [class style attr theme pre] debug))
