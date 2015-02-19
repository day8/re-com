(ns re-com.util
  (:require  [clojure.set :refer [superset?]]))

(defn fmap
  "Takes a fucntion 'f' amd a map 'm'.  Applies 'f' to each value in 'm' and returns.
   (fmap  inc  {:a 4  :b 2})   =>   {:a 5  :b 3}"
  [f m]
  (into {} (for [[k val] m] [k (f val)])))


(defn deref-or-value
  [val-or-atom]
  (if (satisfies? IDeref val-or-atom) @val-or-atom val-or-atom))


(defn get-element-by-id
  [id]
  (.getElementById js/document id))


(defn pad-zero
  "Left pad a string 's' with '0', until 's' has length 'len'. Return 's' unchanged, if it is already len or greater."
  [s len]
  (if (< (count s) len)
    (apply str (take-last len (concat (repeat len \0) s)))
    s))


(defn pad-zero-number
  "return 'num' as a string of 'len' characters, left padding with '0' as necessary."
  [num len]
  (pad-zero (str num) len))


(defn px
  "takes a number (and optional :negative keyword to indicate a negative value) and returns that number as a string with 'px' at the end."
  [val & negative]
  (str (if negative (- 0 val) val) "px"))


;; ----------------------------------------------------------------------------
;; G O L D E N  R A T I O  https://en.wikipedia.org/wiki/Golden_ratio
;; ----------------------------------------------------------------------------

(defn golden-ratio-a
  "Answer the A segment using golden ratio"
  [b-segment]
  (let [Phi 1.618]
    (/ b-segment Phi)))


(defn golden-ratio-b
  "Answer the B segment using golden ratio"
  [segment]
  (- segment (golden-ratio-a segment)))


;; ----------------------------------------------------------------------------
;; Handy vector functions
;; ----------------------------------------------------------------------------

(defn remove-nth
  "Removes the item at position n from a vector v, returning a shrunk vector"
  [v n]
  (vec
    (concat
      (subvec v 0 n) (subvec v (inc n) (count v)))))


(defn insert-nth
  [vect index item]
  (apply merge (subvec vect 0 index) item (subvec vect index)))


;; ----------------------------------------------------------------------------
;; Utilities for vectors of maps containing :id
;; ----------------------------------------------------------------------------

(defn position-for-id
  "Takes a vector of maps 'v'. Returns the postion of the first item in 'v' whose :id matches 'id'.
   Returns nil if id not found."
  [id v]
  (let [index-fn (fn [index item] (when (= (:id item) id) index))]
    (first (keep-indexed index-fn v))))



(defn item-for-id
  "Takes a vector of maps 'v'. Returns the first item in 'v' whose :id matches 'id'.
   Returns nil if id not found."
  [id v]
  (first (filter #(= (:id %) id) v)))


(defn remove-id-item
  "Takes a vector of maps 'v', each of which has an :id key.
  Return v where item matching 'id' is excluded"
  [id v]
  (filterv #(not= (:id %) id) v))


;; ----------------------------------------------------------------------------
;; Argument validation functions
;; ----------------------------------------------------------------------------

;; TODO: Remove this OLD one!
(defn validate-arguments
  [defined-args passed-args]
  (if (superset? defined-args passed-args)
    true
    (let [missing (remove defined-args passed-args)]
      (.error js/console (str "The following arguments are not supported: " missing))
      false)))

;; ----------------------------------------------------------------------------
;; Other functions
;; ----------------------------------------------------------------------------

(defn enumerate
  "(for [[index item first? last?] (enumerate coll)] ...)  "
  [coll]
  (let [c (dec (count coll))
        f (fn [index item] [index item (= 0 index) (= c index)])]
    (map-indexed f coll)))
