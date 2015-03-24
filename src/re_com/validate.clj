(ns re-com.validate)

#_(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not ^boolean 'js/goog.DEBUG  true (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))

#_(defmacro validate-args-macro
  [args-desc args component-name]
  '(if-not ^boolean js/goog.DEBUG true (re-com.validate/validate-args (re-com.validate/extract-arg-data args-desc) args component-name)))

#_(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not (with-meta js/goog.DEBUG {:tag `boolean}) true (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))

#_(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not  ~(with-meta 'js/goog.DEBUG {:tag boolean})
     true
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))

#_(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not (with-meta js/goog.DEBUG {:tag boolean})
     true
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))

#_(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not ~(vary-meta assoc js/goog.DEBUG :tag 'boolean)
     true
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))



#_(defmacro defn-meta
  [name & defn-args]
  `(defn ~(vary-meta name assoc :export true) ~@defn-args))

#_(defmacro add-meta [expr]
  (let [namespace {:namespace (name cljs.analyzer/*cljs-ns*)}
        source-details (meta &form)]
    `(with-meta ~expr '~(merge namespace source-details))))


(defmacro validate-args-macro
  [args-desc args component-name]
  `(if-not (re-com.validate/debug?)
     true
     (re-com.validate/validate-args (re-com.validate/extract-arg-data ~args-desc) ~args ~component-name)))
