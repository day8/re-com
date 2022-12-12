(ns re-com.util
  (:require
   [reagent.ratom :refer [RAtom Reaction RCursor Track Wrapper]]
   [reagent.dom.server :refer [render-to-string]]
    [goog.date.DateTime]
    [goog.date.UtcDateTime]
    [clojure.string :as string]))

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



;; Merge-css - a tool for handling the css that comes into re-com components
;;
;; # Rationale
;;
;; Hard coded CSS is bad. Flexibility is good. Re-com has a few ways to keep the CSS flexible. For the end user, an extended 'parts' structure can be passed into components that allows :class :style and :attr of most elements in the component to be addressed. This is on top of the main :class, :style, and :attr that most components receive. Another method is meant for internal re-com use. It's a structure similar to 'parts', and generally stored with a *-css-spec name. This is meant to give access to serious re-com modifiers who want to use another CSS framework or no CSS at all. It is a place to put CSS values that would normally be hard coded into the components.
;;
;; Merge-css is the tool to pull all of the sources of styling together and apply them appropriately.
;;
;; # Basic Anatomy
;;
;; Merge-css takes two parameters and returns a closure. The first parameter is the *-css-spec for the component in operation. The second is a map containing parameters. Merge-css will be looking for :class, :style, :attr, and :parts keys in this map. Typically, the component's `args` will be passed in as this parameters map.
;;
;; The closure returned by merge-css, conventionally stored as `cmerger`, takes two parameters. The first is a keyword designating the section to retrieve from the *-css-spec and the `parts` structures. The second is a parameters map. The keys :class, :style, and :attr will be extracted from this map and applied as last minute hard coded overrides to the css. The remainder of the map will be passed as run time variables to functions found in the *-css-spec structure.
;;
;; # Components vs. Elements
;;
;; Re-com components, as called from hiccup, do not take options the same way that HTML elements do. The former takes :class, :style and :attr parameters directly, where the latter takes a map of attributes containing :class and :style as the first parameter. The return value from `cmerger` - a map containing :class, :style and :attr - is not directly usable by either. A special utility is available for each case.
;;
;; Components:
;;
;; >    (add-map-to-hiccup-call
;; >      (cmerger :some-place {:optional :options...})
;; >      [my-component
;; >        :id 101
;; >        etc...
;;
;; The hiccup 'call' to my-component will be rewritten to have :class, :style and :attr options added to it from the map supplied by `cmerger`. Note: results are undefined if you put :class, etc. into [my-component ...] by hand. Add-map-to-hiccup-call will not check for existing keys!
;;
;; Elements:
;;
;; >    [:div
;; >      (flatten-attr (cmerger :some-place)) ;;No options this time
;; >      ...
;;
;; Because the contents of :attr from `cmerger` belong directly in first parameter of the :div element, flatten-attr will get rid of the :attr key and place its contents in the toplevel of the returned map.
;;
;; What if you want to add something - say, an event handler - to the attributes of the :div? This can be done in two ways:
;;
;; >    [:div
;; >      (flatten-attr (cmerger :some-place
;; >                             {:attr {:on-squirm ...}}))
;; >      ...
;;
;;
;; >    [:div
;; >      (merge
;; >       (flatten-attr (cmerger :some-place))
;; >       {:on-squirm ...})
;; >      ...
;;
;; ## Fixme: other component parameters
;;
;; Currently there is no way to specify other parameters for re-com components. The goal is to have all hard coded CSS values be stored in -css-spec structures where they can be replaced easily. At first glance, the :attr section may seem to be the place for these other parameters, but it specifically addresses the attributes of the main internal element of the component. Another section for other parameters and variables may need to be added.
;;
;; # The -css-spec structure
;;
;; A css-spec structure will take the following form:
;;
;; >    (def button-css-spec
;; >      {:main {:class ["rc-button" "btn"]
;; >              :style {:background-color ... }
;; >       :wrapper {:class (fn [{:keys [... ]}]
;; >                          ...)}
;; >       ...})
;;
;; At root it is a map, with a key for each section or element in the component. The key names should line up with the keys in the -parts-desc structure whereever possible. Each section is a map that may contain a :class, :style and an :attr object. The class spec should be a vector or a function. The others should be a map or a function. The function should return, respectively, a vector or a map. The function will receive the 'other' parameters mentioned above in the discussion of the `cmerger` closure.
;;
;; ## The :main key
;;
;; The :main key is a special case. Most re-com components receive a :parts parameter for fine-grained css control. But they also receive a simpler set of :class, :style and :attr components. What happens to these? They are generally directed toward the element that the end user will perceive as being the central one. The :main one, in other words. So merge-css will take these toplevel CSS parameters and apply them to the request named :main. The :use-toplevel key, placed in the :main section of the -css-spec structure with a `false` value, will suppress this behavior. Placed in another section - say, :wrapper - with a `true` value, it will cause that element to receive the toplevel user CSS parameters.


(defn merge-css [css-desc {:as params :keys [class style attr parts]}]
  (for [[k v] css-desc
        :when (not (and (keyword? k) (map? v)))]
    (throw (js/Error. "CSS description must contain only keywords and maps")))

  (defn combine-css [a b]
    (let [a (or a {})
          b (or b {})
          acl (:class a)
          bcl (:class b)
          class (reduce into [] [(if (string? acl) [acl] acl) (if (string? bcl) [bcl] bcl)])
          style (reduce into {} [(:style a) (:style b)])
          attr (reduce into {} [(:attr a) (:attr b)])]
      (into {}
            [(when-not (empty? class) [:class class])
             (when-not (empty? style) [:style style])
             (when-not (empty? attr) [:attr attr])])))

  (defn fetch-merged-css
    ([tag]
     (fetch-merged-css tag {}))
    ([tag options]
     (let [xoptions (reduce (partial dissoc options) [:class :style :attr])
           defaults (get css-desc (or tag :main))
           use-toplevel (get :use-toplevel defaults (if (= tag :main) true false))
           user (combine-css (get parts tag)
                             (and use-toplevel {:class class :style style :attr attr}))
           defaults (into {} (for [k [:class :style :attr]
                                   :when (contains? defaults k)
                                   :let [v (get defaults k)]]
                               [k (if (fn? v) (v xoptions) v)]))]
       (when (and tag (not (contains? css-desc tag)))
         (println "Missing!!!: " tag))
       (reduce combine-css [defaults options user]))))
  fetch-merged-css)

(defn flatten-attr [stuff]
  (merge (dissoc stuff :attr) (:attr stuff)))

(defn add-map-to-hiccup-call [map hiccup]
  (with-meta
    (reduce into [[(first hiccup)]
                  (for [[k v] map
                        :let [v (if (= k :class) (string/join " " v) v)]
                        itm [k v]]
                    itm)
                  (rest hiccup)])
    (meta hiccup)))


