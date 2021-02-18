(ns re-com.debug
  (:require
    [re-com.config :refer [debug?]]))

(defn src->attr
  [{:keys [file line] :as src}]
  (if true ;; debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
    (if src
      {:data-rc-src (str file ":" line)}
      {})
    {}))