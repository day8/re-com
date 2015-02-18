(ns re-com.validate
  (:require-macros [re-com.util :refer [assert*]])
  (:require  [clojure.set :refer [superset?]]))

(defonce arg-validation (atom true))

(defn set-validation
  "Turns argument validation on or off based on a boolean argument."
  [val]
  (reset! arg-validation val))

(defn left-string
  "Converts obj to a string and truncates it to max-len chars if necessary."
  [obj max-len]
  (.substring (str obj) 0 max-len))

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
                     (filter #(:required %))
                     (map :name)
                     set)
   :validate-fns (filter #(:validate-fn %) args-desc)})

;; ----------------------------------------------------------------------------
;; Primary validation functions
;; ----------------------------------------------------------------------------

(defn args-names-valid?
  "Checks that arg names passed in are all one of the expected ones. If so, returns true. Prints errors to console."
  [defined-args passed-args]
  (if (superset? defined-args passed-args)
    true
    (let [missing-args (remove defined-args passed-args)]
      (log-error "Invalid arguments: " missing-args))))

(defn required-args-passed?
  "Checks that all :required args are included in the arg list. If so, returns true. Prints errors to console"
  [required-args passed-args]
  (if (superset? passed-args required-args)
    true
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
                           (false?  validate-result) (log-error "Argument '" arg-name "' validation failed. Expected '" (:type v-arg-def) "'. Got '" (left-string arg-val 20) "'")
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
;; Custom :validate-fn functions
;; ----------------------------------------------------------------------------

(defn validate-justify-style
  "Validates the passed argument against the expected justify-style set below."
  [justify-style]
  (let [expected #{:start :end :center :between :around}
        valid? (contains? expected justify-style)]
    (if valid?
      true
      (str "Invalid justify-style. Expected one of keyword " expected ". Got '" justify-style "'"))))

(defn validate-align-style
  "Validates the passed argument against the expected align-style set below."
  [align-style]
  (let [expected #{:start :end :center :baseline :stretch}
        valid? (contains? expected align-style)]
    (if valid?
      true
      (str "Invalid align-style. Expected one of keyword " expected ". Got '" align-style "'"))))

(defn validate-scroll-style
  "Validates the passed argument against the expected scroll-style set below."
  [scroll-style]
  (let [expected #{:auto :off :on :spill}
        valid? (contains? expected scroll-style)]
    (if valid?
      true
      (str "Invalid scroll-style. Expected one of keyword " expected ". Got '" scroll-style "'"))))

(defn hiccup-or-string? ;; TODO:Fill this in
  "Confirms that the passed argument is either valid hiccup or a string"
  [val]
  true)
