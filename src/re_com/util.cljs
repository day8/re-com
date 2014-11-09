(ns re-com.util
  (:require  [clojure.string :as string]))

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
  "Left pad a string 's' with '0', until 's' has length 'len'. If 's' is already len or greater, return 's'"
  [s len]
  (if (< (count s) len)
    (apply str (take-last len (concat (repeat len \0) s)))
    s))


(defn pad-zero-number
  "return 'num' as a string of 'len' characters, left padding with '0' as necessary."
  [num len]
  (pad-zero (str num) len))


(defn find-map-index    ;;  TODO: rename this  'id-position'
  "Takes a vector of maps 'v'. Returns the postion of the first item in 'v' whose :id matches 'id'.
   Returns nil if id not found."
  [v id]
  (let [index-fn (fn [index item] (when (= (:id item) id) index))]
    (first (keep-indexed index-fn v))))


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
