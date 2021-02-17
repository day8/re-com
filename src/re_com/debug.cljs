(ns re-com.debug
  (:require
    [re-com.config :refer [debug?]]))

(defn src->__source
  "Emulates @babel/plugin-transform-react-jsx-source __source prop data structure.
   Ref: https://github.com/babel/babel/blob/e498bee10f0123bb208baa228ce6417542a2c3c4/packages/babel-plugin-transform-react-jsx-source/src/index.js#L43"
  [{:keys [file line]}]
  #js {:fileName     file
       :lineNumber   line
       :columnNumber 1})

(defn src->props
  "Emulates @babel/plugin-transform-react-jsx-source __source prop data structure."
  [src]
  #js {:argv #js {:__source (src->__source src)}})

(defn src->attr
  [{:keys [file line] :as src}]
  (if debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
    (if src
      {:data-rc-src (str file ":" line)}
      {})
    {}))