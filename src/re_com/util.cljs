(ns re-com.util
  (:require  [clojure.string :as string]))

(defn deref-or-value [val-or-atom]
  (if (satisfies? IDeref val-or-atom) @val-or-atom val-or-atom))

(defn console-log
  [msg]
  (. js/console (log msg)))


(defn console-log-stringify
  [msg obj]
  (. js/console (log (str msg ": " (. js/JSON (stringify obj))))))


(defn console-log-prstr
  [msg obj]
  (. js/console (log (str msg ": " (. js/cljs.core (pr_str obj))))))


(defn get-element-by-id
  [id]
  (.getElementById js/document id))

(defn pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

(defn pad-zero-number [subject-num max-chars]
  "If subject-num zero pad subject-str from left up to max-chars."
  (pad-zero (str subject-num) max-chars))
