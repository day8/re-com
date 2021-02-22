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

(defmacro handler-fn
  ([& body]
   `(fn [~'event] ~@body nil)))  ;; force return nil


;; Obtain source code coordinates and assemble them into a map literal containing `:file` and `:line` keys
;; See explanation: https://re-com.day8.com.au/#/debug
(defmacro coords
  []
  (select-keys (meta &form) [:file :line]))

;; Obtain the current component's component-name and args to be provided as a :log argument to another component. Causes
;; the other component to masquerade in logs as the current component in terms of its component-name and args. Used when
;; the root of the current component is another re-com component.
(defmacro reflect
  []
  {:component '(re-com.debug/short-component-name (reagent.impl.component/component-name (reagent.core/current-component)))
   :args      'args})