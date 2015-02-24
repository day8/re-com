(ns re-com.validate
  (:require  [clojure.set           :refer [superset?]]
             [re-com.util           :refer [deref-or-value]]
             [reagent.impl.template :refer [valid-tag?]]
             [goog.string           :as    gstring]))


;; -- Global Switch -----------------------------------------------------------

;; if true, then validation occurs.
;; It is expected that will be flicked to off, in production systems.
(defonce arg-validation (atom true))


(defn set-validation!
  "Turns argument validation on or off based on a boolean argument"
  [val]
  (reset! arg-validation val))


;; -- Helpers -----------------------------------------------------------------

(defn left-string
  "Converts obj to a string and truncates it to max-len chars if necessary.
   When truncation is necessary, adds an elipsis to the end"
  [obj max-len]
  (gstring/truncate (str obj) max-len))

(defn log-error
  "Sends a message to the DeV Tools console as an error"
  [& args]
  (.error js/console (apply str args))
  false)

(defn log-warning
  "Sends a message to the DeV Tools console as an warning"
  [& args]
  (.warn js/console (apply str args))
  false)


(defn hash-map-with-name-keys
  [v]
  (zipmap (map :name v) v))


(defn extract-arg-data
  "Package up all the relevant data for validation purposes from the xxx-args-desc map into a new map"
  [args-desc]
  {:arg-names      (set (map :name args-desc))
   :required-args  (->> args-desc
                        (filter :required)
                        (map :name)
                        set)
   :validated-args (->> (filter :validate-fn args-desc)
                        vec
                        (hash-map-with-name-keys))})

;; ----------------------------------------------------------------------------
;; Primary validation functions
;; ----------------------------------------------------------------------------

(defn arg-names-valid?
  "returns true if every passed-args is value. Otherwise log the problem and return false"
  [defined-args passed-args]
  (or (superset? defined-args passed-args)
      (let [missing-args (remove defined-args passed-args)]
        (log-error "Invalid arguments: " missing-args))))

(defn required-args-passed?
  "returns true if all the required args are supplied. Otherwise log the error and return false"
  [required-args passed-args]
  (or (superset? passed-args required-args)
      (let [missing-args (remove passed-args required-args)]
        (log-error "Missing required arguments: " missing-args))))


(defn validate-fns-pass?
  "returns true if all argument values are valid  (for args which have a validator).
   Otherwise log error and return false.
   Validation functions can return:
         - true:   validation success
         - false:  validation failed - use standard error message
         - string: validation failed - use this string in place of standard error message"
  [args-with-validators passed-args component-name]
  (let [validate-arg (fn [[_ v-arg-def]]
                       (let [arg-name        (:name v-arg-def)
                             arg-val         (deref-or-value (arg-name passed-args)) ;; Automatically extract value if it's in an atom
                             required?       (:required v-arg-def)
                             validate-result ((:validate-fn v-arg-def) arg-val)]
                         ;(println (str "[" component-name "] " arg-name " = '" (if (nil? arg-val) "nil" (left-string arg-val 40)) "'"))
                         (cond
                           (or (true? validate-result)
                               (and (nil? arg-val)          ;; Allow nil values through if the arg is NOT required
                                    (not required?))) true
                           (false?  validate-result)  (log-error "Argument '" arg-name "' validation failed in component '" component-name "'. Expected '" (:type v-arg-def)
                                                                 "'. Got '" (if (nil? arg-val) "nil" (left-string arg-val 40)) "'")
                           (string? validate-result)  (log-error validate-result)
                           :else                      (log-error "Invalid return from validate-fn: " validate-result))))]
    (->> (select-keys args-with-validators (vec (keys passed-args)))
         (map validate-arg)
         (every? true?))))

(defn validate-args
  "Calls three validation tests:
    - Are arg names valid?
    - Have all required args been passed?
    - Specific valiadation function calls to check arg values if specified
   If they all pass, returns true.
   Normally used for a call to the {:pre...} at the beginning of a function"
  [arg-defs passed-args & component-name]
  (let [passed-arg-keys (set (keys passed-args))]
    (and (arg-names-valid?      (:arg-names      arg-defs) passed-arg-keys)
         (required-args-passed? (:required-args  arg-defs) passed-arg-keys)
         (validate-fns-pass?    (:validated-args arg-defs) passed-args (first component-name)))))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions based on (validate-arg-against-set)
;; ----------------------------------------------------------------------------

(def justify-options      [:start :end :center :between :around])
(def align-options        [:start :end :center :baseline :stretch])
(def scroll-options       [:auto :off :on :spill])
(def alert-types          ["info" "warning" "danger"])
(def button-sizes         [:regular :smaller :larger])
(def input-status-types   [:warning :error])
(def popover-status-types [:warning :error :info])
(def position-options     [:above-left  :above-center :above-right
                           :below-left  :below-center :below-right
                           :left-above  :left-center  :left-below
                           :right-above :right-center :right-below])

;; TODO: Is here the right place for make-code-list and all the lists? It's efficient because it makes sure they are only defined once

(defn validate-arg-against-set
  "Validates the passed argument against the expected set"
  [arg arg-name valid-set]
  (let [arg (deref-or-value arg)]
    (or (not= (some (hash-set arg) valid-set) nil)
        (str "Invalid " arg-name ". Expected one of " valid-set ". Got '" (left-string arg 40) "'"))))

(defn justify-style?       [arg] (validate-arg-against-set arg ":justify-style" justify-options))
(defn align-style?         [arg] (validate-arg-against-set arg ":align-style"   align-options))
(defn scroll-style?        [arg] (validate-arg-against-set arg ":scroll-style"  scroll-options))
(defn alert-type?          [arg] (validate-arg-against-set arg ":alert-type"    alert-types))
(defn button-size?         [arg] (validate-arg-against-set arg ":size"          button-sizes))
(defn input-status-type?   [arg] (validate-arg-against-set arg ":status"        input-status-types))
(defn popover-status-type? [arg] (validate-arg-against-set arg ":status"        popover-status-types))
(defn position?            [arg] (validate-arg-against-set arg ":position"      position-options))

;; ----------------------------------------------------------------------------
;; Predefined hiccup lists for streamlined consumption in arg documentation
;; ----------------------------------------------------------------------------

(defn make-code-list
  "Given a vector or list of codes, create a [:span] hiccup vector containing a comma separated list of the codes"
  [codes]
  (into [:span] (interpose ", " (map #(vector :code (str %)) codes))))

(def justify-options-list      (make-code-list justify-options))
(def align-options-list        (make-code-list align-options))
(def scroll-options-list       (make-code-list scroll-options))
(def alert-types-list          (make-code-list alert-types))
(def button-sizes-list         (make-code-list button-sizes))
(def input-status-types-list   (make-code-list input-status-types))
(def popover-status-types-list (make-code-list popover-status-types))
(def position-options-list     (make-code-list position-options))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions
;; ----------------------------------------------------------------------------

(defn string-or-hiccup?
  "Returns true if the passed argument is either valid hiccup or a string"
  [arg]
  (valid-tag? (deref-or-value arg)))

(defn vector-of-maps?
  "Returns true if the passed argument is a vector of maps (either directly or contained in an atom). Notes:
    - vector can be empty
    - only checks the first element in the vector"
  [arg]
  (let [arg (deref-or-value arg)]
    (and (vector? arg)
         (or (empty? arg)
             (map? (first arg))))))

(defn goog-date?  ;; TODO: Write the code
  "Returns true if the passed argument is a valid goog.date.UtcDateTime"
  [arg]
  (let [arg (deref-or-value arg)]
    true))

(defn regex?  ;; TODO: Write the code
  "Returns true if the passed argument is a valid regular expression"
  [arg]
  (let [arg (deref-or-value arg)]
    ;(println "arg=" arg "source=" (.-source js/arg))
    true))
