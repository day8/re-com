(ns re-com.util
  (:require
    [clojure.set :refer [superset?]]
    [reagent.ratom :refer [RAtom Reaction RCursor Track Wrapper]]
    [goog.date.DateTime]
    [goog.date.UtcDateTime]))

(defn fmap
  "Takes a function 'f' amd a map 'm'.  Applies 'f' to each value in 'm' and returns.
   (fmap  inc  {:a 4  :b 2})   =>   {:a 5  :b 3}"
  [f m]
  (into {} (for [[k val] m] [k (f val)])))

(defn deep-merge
  "Recursively merges maps. If vals are not maps, the last value wins."
  [& vals]
  (if (every? map? vals)
    (apply merge-with deep-merge vals)
    (last vals)))


(defn deref-or-value
  "Takes a value or an atom
  If it's a value, returns it
  If it's a Reagent object that supports IDeref, returns the value inside it by derefing
  "
  [val-or-atom]
  (if (satisfies? IDeref val-or-atom)
    @val-or-atom
    val-or-atom))


(defn deref-or-value-peek
  "Takes a value or an atom
  If it's a value, returns it
  If it's a Reagent object that supports IDeref, returns the value inside it, but WITHOUT derefing

  The arg validation code uses this, since calling deref-or-value adds this arg to the watched ratom list for the component
  in question, which in turn can cause different rendering behaviour between dev (where we validate) and prod (where we don't).

  This was experienced in popover-content-wrapper with the position-injected atom which was not derefed there, however
  the dev-only validation caused it to be derefed, modifying its render behaviour and causing mayhem and madness for the developer.

  See below that different Reagent types have different ways of retrieving the value without causing capture, although in the case of
  Track, we just deref it as there is no peek or state, so hopefully this won't cause issues (surely this is used very rarely).
  "
  [val-or-atom]
  (if (satisfies? IDeref val-or-atom)
    (cond
      (instance? RAtom    val-or-atom) val-or-atom.state
      (instance? Reaction val-or-atom) (._peek-at val-or-atom)
      (instance? RCursor  val-or-atom) (._peek val-or-atom)
      (instance? Track    val-or-atom) @val-or-atom
      (instance? Wrapper  val-or-atom) val-or-atom.state
      :else                            (throw (js/Error. "Unknown reactive data type")))
    val-or-atom))


(defn get-element-by-id
  [id]
  (.getElementById js/document id))


(defn pad-zero
  "Left pad a string 's' with '0', until 's' has length 'len'. Return 's' unchanged, if it is already len or greater"
  [s len]
  (if (< (count s) len)
    (apply str (take-last len (concat (repeat len \0) s)))
    s))


(defn pad-zero-number
  "return 'num' as a string of 'len' characters, left padding with '0' as necessary"
  [num len]
  (pad-zero (str num) len))


(defn px
  "takes a number (and optional :negative keyword to indicate a negative value) and returns that number as a string with 'px' at the end"
  [val & negative]
  (str (if negative (- val) val) "px"))


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
  "Takes a vector of maps 'v'. Returns the position of the first item in 'v' whose id-fn (default :id) matches 'id'.
   Returns nil if id not found"
  [id v & {:keys [id-fn] :or {id-fn :id}}]
  (let [index-fn (fn [index item] (when (= (id-fn item) id) index))]
    (first (keep-indexed index-fn v))))



(defn item-for-id
  "Takes a vector of maps 'v'. Returns the first item in 'v' whose id-fn (default :id) matches 'id'.
   Returns nil if id not found"
  [id v & {:keys [id-fn] :or {id-fn :id}}]
  (first (filter #(= (id-fn %) id) v)))


(defn remove-id-item
  "Takes a vector of maps 'v', each of which has an id-fn (default :id) key.
  Return v where item matching 'id' is excluded"
  [id v & {:keys [id-fn] :or {id-fn :id}}]
  (filterv #(not= (id-fn %) id) v))


;; ----------------------------------------------------------------------------
;; Other functions
;; ----------------------------------------------------------------------------

(defn enumerate
  "(for [[index item first? last?] (enumerate coll)] ...)  "
  [coll]
  (let [c (dec (count coll))
        f (fn [index item] [index item (= 0 index) (= c index)])]
    (map-indexed f coll)))

(defn sum-scroll-offsets
  "Given a DOM node, I traverse through all ascendant nodes (until I reach body), summing any scrollLeft and scrollTop values
   and return these sums in a map"
  [node]
  (loop [current-node    (.-parentNode node) ;; Begin at parent
         sum-scroll-left 0
         sum-scroll-top  0]
    (if (not= (.-tagName current-node) "BODY")
      (recur (.-parentNode current-node)
             (+ sum-scroll-left (.-scrollLeft current-node))
             (+ sum-scroll-top  (.-scrollTop  current-node)))
      {:left sum-scroll-left
       :top  sum-scroll-top})))

;; ----------------------------------------------------------------------------
;; date functions
;; ----------------------------------------------------------------------------

(defn now->utc
  "Answer a goog.date.UtcDateTime based on local date/time."
  []
  (let [local-date (js/goog.date.DateTime.)]
    (js/goog.date.UtcDateTime.
      (.getYear local-date)
      (.getMonth local-date)
      (.getDate local-date)
      0 0 0 0)))
