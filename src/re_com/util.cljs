(ns re-com.util
  (:require  [clojure.string :as string]))


(defn console-log [msg]
  (. js/console (log msg)))


(defn console-log-stringify [msg obj]
  (. js/console (log (str msg ": " (. js/JSON (stringify obj))))))


(defn console-log-prstr [msg obj]
  (. js/console (log (str msg ": " (. js/cljs.core (pr_str obj))))))


(defn get-element-by-id [id]
  (.getElementById js/document id))
