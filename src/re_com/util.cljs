(ns re-com.util
  (:require
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

(defn assoc-in-if-empty
  "Only assoc-in if no value exists at ks"
  [m ks v]
  (assoc-in m ks (get-in m ks v)))


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


(defn px-n
  "takes n numbers (could also be strings) and converts them to a space separated px string
  e.g. (px-n 10 2 30 4) => '10px 2px 30px 4px' for use in :padding, :margin etc.
  Most useful when the args are calculations
  e.g. (px-n top-margin (inc h-width) (- top-margin 5) (dec h-width))
  Note: Doesn't support :negative like px above but it will work with negative numbers"
  [& vals]
  (clojure.string/join " " (map #(str % "px") vals)))


(defn pluralize
  "Return a pluralized phrase, appending an s to the singular form if no plural is provided.
  For example:
     (pluralize 5 \"month\") => \"5 months\"
     (pluralize 1 \"month\") => \"1 month\"
     (pluralize 1 \"radius\" \"radii\") => \"1 radius\"
     (pluralize 9 \"radius\" \"radii\") => \"9 radii\"
     From https://github.com/flatland/useful/blob/194950/src/flatland/useful/string.clj#L25-L33"
  [num singular & [plural]]
  (str num " " (if (= 1 num) singular (or plural (str singular "s")))))

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
  "(for [[index item first? last?] (enumerate coll)] ...)"
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
;; Date functions
;;
;; For reference:
;;
;; (js/console.log "(now)................" (now))
;; (js/console.log "(now->utc)..........." (now->utc))
;; (js/console.log "(today).............." (today))
;;
;; (js/console.log "(time-now)..........." (time-now))
;; (js/console.log "(today-at-midnight).." (today-at-midnight))
;; (js/console.log "(date-midnight)......" (date-midnight   2018 11 22))
;;;
;; (js/console.log "(date-time).........." (date-time       2018 11 22 08 05 05))
;; (js/console.log "(local-date-time)...." (local-date-time 2018 11 22 08 05 05))
;; (js/console.log "(local-date)........." (local-date      2018 11 22))
;;
;; Run at 09:22:35 in Sydney Australia...
;;
;; (now)................ goog.date.UtcDateTime {date: Thu Nov 22 2018 09:22:35 GMT+1100 (Australian Eastern Daylight Time)}
;; (now->utc)........... goog.date.UtcDateTime {date: Thu Nov 22 2018 11:00:00 GMT+1100 (Australian Eastern Daylight Time)}
;; (today).............. goog.date.Date        {date: Thu Nov 22 2018 00:00:00 GMT+1100 (Australian Eastern Daylight Time)}
;;
;; (time-now)........... goog.date.DateTime    {date: Thu Nov 22 2018 09:22:35 GMT+1100 (Australian Eastern Daylight Time)}
;; (today-at-midnight).. goog.date.UtcDateTime {date: Wed Nov 21 2018 11:00:00 GMT+1100 (Australian Eastern Daylight Time), firstDayOfWeek_: 6, firstWeekCutOffDay_: 5}
;; (date-midnight)...... goog.date.UtcDateTime {date: Thu Nov 22 2018 11:00:00 GMT+1100 (Australian Eastern Daylight Time)}
;;
;; (date-time).......... goog.date.UtcDateTime {date: Thu Nov 22 2018 19:05:05 GMT+1100 (Australian Eastern Daylight Time)}
;; (local-date-time).... goog.date.DateTime    {date: Thu Nov 22 2018 08:05:05 GMT+1100 (Australian Eastern Daylight Time)}
;; (local-date)......... goog.date.Date        {date: Thu Nov 22 2018 00:00:00 GMT+1100 (Australian Eastern Daylight Time)}
;;
;; ----------------------------------------------------------------------------

(defn now->utc
  "Return a goog.date.UtcDateTime based on local date/time."
  []
  (let [local-date-time (js/goog.date.DateTime.)]
    (js/goog.date.UtcDateTime.
      (.getYear local-date-time)
      (.getMonth local-date-time)
      (.getDate local-date-time)
      0 0 0 0)))
