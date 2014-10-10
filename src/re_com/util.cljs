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

(defn pad-zero [subject-str max-chars]
  "If subject-str zero pad subject-str from left up to max-chars."
  (if (< (count subject-str) max-chars)
  	(apply str (take-last max-chars (concat (repeat max-chars \0) subject-str)))
  	subject-str))

(defn pad-zero-number [subject-num max-chars]
  "If subject-num zero pad subject-str from left up to max-chars."
  (pad-zero (str subject-num) max-chars))

;; ----------- G O L D E N  R A T I O -----------------------------------------
;; https://en.wikipedia.org/wiki/Golden_ratio

(defn golden-ratio-a [b-segment]
  "Answer the A segment using golden ratio"
  (let [Phi 1.618]
    (/ b-segment Phi)))

(defn golden-ratio-b [segment]
  "Answer the B segment using golden ratio"
  (- segment (golden-ratio-a segment)))
;; ----------------------------------------------------------------------------
