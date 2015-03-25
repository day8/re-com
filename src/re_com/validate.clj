(ns re-com.validate)

(defmacro validate-args-macro
  "if goog.DEBUG is true then validate the args, otherwise replace the validation code with true
  for production builds which the {:pre ...} will be happy with"
  [args-desc args component-name]
  `(if-not ~(vary-meta 'js/goog.DEBUG assoc :tag 'boolean)
     true
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))
