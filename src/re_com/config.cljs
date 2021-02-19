(ns re-com.config)

;; debug? is true for development builds (e.g. lein watch) and false for production release builds (e.g. lein prod-once).
;; It is used to disable component argument validation in production, and to determine the value of include-args-desc?
;; below.
(def debug?
  ^boolean js/goog.DEBUG)

;; We don't want to include the data structures for argument and parts descriptions in production builds that use re-com
;; EXCEPT for the demo site (https://re-com.day8.com.au/) which is a special case. On the demo site we use those data
;; structures to generate the documentation (i.e. argument and parts tables). The following Closure define is therefore
;; set via shadow-cljs compiler options for demo builds only, to ensure the data structures are available regardless of
;; debug?.
(goog-define force-include-args-desc? false)

(goog-define root-url-for-compiler-output "")

;; When include-args-desc? is true, the data structures for arguments and parts will be included in the output JS file,
;; otherwise they will not be included.
(def ^boolean include-args-desc?
  (or debug? force-include-args-desc?))

(goog-define version "")
