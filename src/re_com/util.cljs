(ns re-com.util
  (:require  [clojure.string :as string]))

(defn fmap [f m]
  "I return a new version of 'm' in which f has been applied to each value.
   (fmap  inc  {:a 4  :b 2})   =>   {:a 5  :b 3}"
  ;;TODO Now a duplicate of day8core/core_utils.cljs in mwireader project. We need common core util libs !
  (into {} (for [[k val] m] [k (f val)])))

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
