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

(defn extract-arg-data
  "Package up all the relevant data for validation purposes from the xxx-args-desc map into a new map."
  [args-desc]
  {:names       (set (map :name args-desc))
   :required    (->> args-desc
                     (filter :required)
                     (map :name)
                     set)
   :validate-fns (filter :validate-fn args-desc)})

;; ----------------------------------------------------------------------------
;; Primary validation functions
;; ----------------------------------------------------------------------------

(defn args-names-valid?
  "Checks that arg names passed in are all one of the expected ones. If so, returns true. Prints errors to console."
  [defined-args passed-args]
  (or (superset? defined-args passed-args)
      (let [missing-args (remove defined-args passed-args)]
        (log-error "Invalid arguments: " missing-args))))

(defn required-args-passed?
  "Checks that all :required args are included in the arg list. If so, returns true. Prints errors to console"
  [required-args passed-args]
  (or (superset? passed-args required-args)
      (let [missing-args (remove passed-args required-args)]
        (log-error "Missing required arguments: " missing-args))))


(defn validate-fns-pass?
  "Call validate-fn for each arg that has one (and only if the arg was actually passed). Return true if ALL were successful. Prints errors to console.
   NOTE: Return value for validate-fn is boolean (with a twist):
         - true:   validation success
         - false:  validation failed - use standard error message
         - string: validation failed - use this string in place of standard error message"
  [v-arg-defs passed-args]
  (let [validate-arg (fn [v-arg-def]
                       (let [arg-name        (:name v-arg-def)
                             arg-val         (arg-name passed-args)
                             validate-result ((:validate-fn v-arg-def) arg-val)]
                         (cond
                           (true?   validate-result) true
                           (false?  validate-result) (log-error "Argument '" arg-name "' validation failed. Expected '" (:type v-arg-def) "'. Got '" (left-string arg-val 40) "'")
                           (string? validate-result) (log-error validate-result)
                           :else                     (log-error "Invalid return from validate-fn: " validate-result))))]
    (->> (select-keys v-arg-defs (keys passed-args))
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
    (and (args-names-valid?     (:names        arg-defs) passed-arg-keys)
         (required-args-passed? (:required     arg-defs) passed-arg-keys)
         (validate-fns-pass?    (:validate-fns arg-defs) passed-args))))


;; ----------------------------------------------------------------------------
;; Custom :validate-fn functions based on (validate-arg-against-set)
;; ----------------------------------------------------------------------------

(defn validate-arg-against-set
  "Validates the passed argument against the expected set."
  [arg arg-type valid-args]
  (or (contains? valid-args arg)
      (str "Invalid " arg-type ". Expected one of " valid-args ". Got '" (left-string arg 40) "'")))

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
