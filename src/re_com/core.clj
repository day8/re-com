(ns re-com.core)

;; There is a trap when writing DOM event handlers.  This looks innocent enough:
;;
;;     :on-mouse-out  #(reset! my-over-atom false)
;;
;; But notice that it inadvertently returns false.  returning false means something!!
;; v0.11 of ReactJS will invoke  both stopPropagation() and preventDefault()
;; on the  event. Almost certainly not what we want.
;;
;; Note: v0.12 of ReactJS will do the same as v0.11, except it also issues a
;; deprecation warning about false returns.
;;
;; Note: ReactJS only tests explicitly for false, not falsy values. So 'nil' is a
;; safe return value.
;;
;; So 'handler-fn' is a macro which will stop you from inadvertently returning
;; false in a handler.
;;
;;
;; Examples:
;;
;;     :on-mouse-out  (handler-fn (reset! my-over-atom false))    ;; notice no # in front reset! form
;;
;;
;;     :on-mouse-out  (handler-fn
;;                       (reset! over-atom false)     ;; notice: no need for a 'do'
;;                       (now do something else)
;;                       (.preventDefault event))     ;; notice access to the 'event'
;;
;; Testing
;;
;;     On first glance, this code might look like it should return true:
;;
;;         (nil? (handler-fn (reset! atm false)))
;;
;;     But it doesn't because the test code is returning a fn, so you need to call it with an 'event' arg (doesn't matter what arg is)
;;
;;         (nil? ((handler-fn (reset! over-atom false)) {})

(defmacro handler-fn
  ([& body]
    `(fn [~'event] ~@body nil)))  ;; force return nil


(defmacro defn-meta
  [name & defn-args]
  `(defn ~(vary-meta name assoc :export true) ~@defn-args))

(defmacro add-meta [expr]
  (let [namespace {:namespace (name cljs.analyzer/*cljs-ns*)}
        source-details (meta &form)]
    `(with-meta ~expr '~(merge namespace source-details))))

