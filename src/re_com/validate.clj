(ns re-com.validate)

(defmacro validate-args-macro
  "if goog.DEBUG is true then validate the args, otherwise replace the validation code with true
  for production builds which the {:pre ...} will be happy with"
  [args-desc args src]
  `(if-not ~(vary-meta 'js/goog.DEBUG assoc :tag 'boolean)
     nil
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~src)))
