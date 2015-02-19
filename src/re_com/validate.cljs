(ns re-com.validate
  (:require  [clojure.set           :refer [superset?]]
             [re-com.util           :refer [deref-or-value]]
             [reagent.impl.template :refer [valid-tag?]]
             [goog.string :as gstring]))


;; -- Global Switch ------------------------------------------------------------------------------------

;; if true, then validation occurs.
;; It is expected that will be flicked to off, in production systems.
(defonce arg-validation (atom true))


(defn set-validation!
  "Turns argument validation on or off based on a boolean argument."
  [val]
  (reset! arg-validation val))


;; -- Helpers ------------------------------------------------------------------------------------

(defn left-string
  "Converts obj to a string and truncates it to max-len chars if necessary.
   When truncation is necessary, adds an elipsis to the end."
  [obj max-len]
  (gstring/truncate (str obj) max-len))

(defn log-error
  "Sends a message to the DeV Tools console as an error."
  [& args]
  (.error js/console (apply str args))
  false)

(defn log-warning
  "Sends a message to the DeV Tools console as an warning."
  [& args]
  (.warn js/console (apply str args))
  false)


(defn hash-map-with-name-keys
  [v]
  (zipmap (map :name v) v))


(defn extract-arg-data
  "Package up all the relevant data for validation purposes from the xxx-args-desc map into a new map."
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
  "returns true is all the required args are supplied. Otherwise log the error and return false."
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
  [args-with-validators passed-args]
  (let [validate-arg (fn [[_ v-arg-def]]
                       (let [arg-name        (:name v-arg-def)
                             arg-val         (arg-name passed-args)
                             validate-result ((:validate-fn v-arg-def) arg-val)]
                         (cond
                           (true?   validate-result) true
                           (false?  validate-result) (log-error "Argument '" arg-name "' validation failed. Expected '" (:type v-arg-def) "'. Got '" (left-string arg-val 40) "'")
                           (string? validate-result) (log-error validate-result)
                           :else                     (log-error "Invalid return from validate-fn: " validate-result))))]
    (->> (select-keys args-with-validators (vec (keys passed-args)))
         (map validate-arg)
         (every? true?))))

(defn validate-args
  "Calls three validation tests:
    - Are arg names valid?
    - Have all required args been passed?
    - Specific valiadation function calls to check arg values if specified
   If they all pass, returns true.
   Normally used for a call to the {:pre...} at the beginning of a function."
  [arg-defs passed-args]
  (let [passed-arg-keys (set (keys passed-args))]
    (and (arg-names-valid?      (:arg-names      arg-defs) passed-arg-keys)
         (required-args-passed? (:required-args  arg-defs) passed-arg-keys)
         (validate-fns-pass?    (:validated-args arg-defs) passed-args))))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions based on (validate-arg-against-set)
;; ----------------------------------------------------------------------------

(defn validate-arg-against-set
  "Validates the passed argument against the expected set."
  [arg arg-name valid-set]
  (or (contains? valid-set arg)
      (str "Invalid " arg-name ". Expected one of " valid-set ". Got '" (left-string arg 40) "'")))

(defn justify-style?
  [arg]
  (validate-arg-against-set arg ":justify-style" #{:start :end :center :between :around}))

(defn align-style?
  [arg]
  (validate-arg-against-set arg ":align-style"   #{:start :end :center :baseline :stretch}))

(defn scroll-style?
  [arg]
  (validate-arg-against-set arg ":scroll-style"  #{:auto :off :on :spill}))

(defn alert-type?
  [arg]
  (validate-arg-against-set arg ":alert-type"    #{"info" "warning" "danger"}))

;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions
;; ----------------------------------------------------------------------------

(defn hiccup-or-string?
  "Returns true if the passed argument is either valid hiccup or a string"
  [arg]
  (valid-tag? arg))

(defn vector-of-maps?
  "Returns true if the passed argument is a vector of maps (either directly or contained in an atom). Notes:
    - vector can be empty
    - only checks the first element in the vector"
  [arg]
  (let [val (deref-or-value arg)]
    (and (vector? val)
         (or (empty? val)
             (map? (first val))))))
