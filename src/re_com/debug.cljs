(ns re-com.debug
  (:require-macros
   [re-com.core         :refer [handler-fn]])
  (:require
   [cljs.pprint            :refer [pprint]]
   [goog.object            :as    gobj]
   [cljs.reader            :refer [read-string]]
   [clojure.string         :as    string]
   [reagent.core           :as    r]
   [reagent.impl.component :as    component]
   [re-com.config          :refer [debug? debug-parts? root-url-for-compiler-output log-format]]))

(def log
  (case (some-> log-format name)
    "pr-str" (comp js/console.log pr-str)
    "js"     js/console.log
    "pretty" pprint
    js/console.log))

(defn short-component-name
  "Returns the interesting part of component-name"
  [component-name]
  ;; reagent.impl.component/component-name is used to obtain the component name, which returns
  ;; e.g. re_com.checkbox.checkbox. We are only interested in the last part.
  ;;
  ;; Also some components are form-2 or form-3 so will return -return from the anonymous render
  ;; function name. We keep the -render in the anonymous function name for JavaScript stack
  ;; traces for non-validation errors (i.e. exceptions), but we are not interested in that here.
  (-> component-name
      (string/split #"\.")
      (last)
      (string/replace #"_render" "")
      (string/replace #"_" "-")))

(defn loggable-args
  "Return a version of args which is stripped of uninteresting values, suitable for logging."
  [args]
  (if (map? args)
    (->> ;; Remove args already represented in component hierarchy
     (dissoc args :src :child :children :panel-1 :panel-2 :debug-as :theme :cell :edge)
      ;; Remove args with nil value
     (remove (comp nil? second))
     (into {}))
    args))

(defn log-on-alt-click* [event & {:as args}]
  (when (.-altKey event) (log args)))

(defn log-on-alt-click [{:as args} & {:keys [show-all-args?] :or {show-all-args? false}}]
  (if debug?
    (handler-fn (log-on-alt-click* event (cond-> args (not show-all-args?) loggable-args)))
    (if debug-parts?
      (handler-fn (log-on-alt-click* event (cond-> args (not show-all-args?) loggable-args)))
      nil)))

;; Runtime probe for re-frame.trace. Re-com intentionally
;; doesn't depend on re-frame (re-frame-10x inlines its own copy via
;; mranderson, so 10x as a peer dep doesn't carry re-frame), but
;; consumer apps that do load re-frame can opt in to the
;; `:re-com/render` trace stream documented below. We resolve the
;; trace fns lazily via `goog.global` so re-com builds without
;; re-frame still compile and run cleanly.
;;
;; Names looked up here are all `defn`/`def` in re-frame.trace and
;; therefore exist as JS exports. `finish-trace` and `with-trace` in
;; re-frame.trace are CLJ-only `defmacro`s wrapped in
;; `(macros/deftime ...)` — they do NOT compile to JS exports, so
;; resolving them dynamically returns undefined. Instead we inline
;; the `finish-trace` macro's body using the exported `traces` atom
;; and `run-tracing-callbacks!` fn.
(def rf-trace
  ;; Cached so we don't walk goog.global on every render. Resets
  ;; to `:unset` on hot-reload (defonce semantics aren't appropriate
  ;; — re-frame may load AFTER re-com namespaces in some boot orders).
  (atom :unset))

(defn resolve-rf-trace! []
  (when-let [g (some-> js/goog .-global)]
    (when-let [tns (some-> g (gobj/get "re_frame") (gobj/get "trace"))]
      (let [start            (gobj/get tns "start_trace")
            enabled?         (gobj/get tns "is_trace_enabled_QMARK_")
            traces           (gobj/get tns "traces")
            run-tracing-cbs! (gobj/get tns "run_tracing_callbacks_BANG_")]
        (when (and start enabled? traces run-tracing-cbs!)
          {:start            start
           :enabled?         enabled?
           :traces           traces
           :run-tracing-cbs! run-tracing-cbs!})))))

(defn- now*
  []
  (if (and (exists? js/performance) (.-now js/performance))
    (.now js/performance)
    (.now js/Date)))

(defn emit-render-src-trace!
  "Q1 result is NEGATIVE: Reagent's :render trace `:tags`
   carries only `:component-name` (10x's wrap-funs in
   day8/reagent/impl/component.cljs fires the `with-trace` with an
   empty body before `do-render`, so hiccup-metadata via
   `(with-meta [:div ...] {:src ...})` never reaches it). The
   workaround is a sibling marker trace: zero-body `with-trace`
   carrying `:src` + `:component-name`, fired once per `->attr`
   call. Consumers (re-frame-pair, custom 10x panels) correlate
   `:re-com/render` to the matching `:render` by component-name
   + nearby `:start` timestamp.

   No-op when re-frame.trace isn't loaded into the runtime —
   re-com stays decoupled from re-frame as a hard dep.
   No-op when tracing is disabled (`trace-enabled?` false) —
   matches re-frame.trace's own gating semantics."
  [rc-component src]
  (when-let [t (let [v @rf-trace]
                 (if (= :unset v)
                   (let [r (resolve-rf-trace!)]
                     (reset! rf-trace r)
                     r)
                   v))]
    (let [enabled? (:enabled? t)]
      (when (enabled?)
        (let [start            (:start t)
              traces-atom      (:traces t)
              run-tracing-cbs! (:run-tracing-cbs! t)
              tr               (start {:op-type   :re-com/render
                                       :operation rc-component
                                       :tags      {:component-name rc-component
                                                   :src            src}})
              end              (now*)
              finished         (assoc tr
                                      :duration (- end (:start tr))
                                      :end      end)]
          (swap! traces-atom conj finished)
          (run-tracing-cbs! end))))))

(defn ->attr
  [{:keys [src debug-as] :as args}]
  (if-not debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
    {}
    (let [rc-component        (or (:component debug-as)
                                  (short-component-name (component/component-name (r/current-component))))
          rc-args             (loggable-args
                               (or (:args debug-as)
                                   args))
          ref-fn              (fn [^js/Element el]
                                ;; If the ref callback is defined as an inline function, it will get called twice during updates,
                                ;; first with null and then again with the DOM element.
                                ;;
                                ;; See: 'Caveats with callback refs' at
                                ;; https://reactjs.org/docs/refs-and-the-dom.html#caveats-with-callback-refs
                                (when el
                                  ;; Remember args so they can be logged later:
                                  (gobj/set el "__rc-args" rc-args))
                                ;; User may have supplied their own ref like so: {:attr {:ref (fn ...)}}
                                (when-let [user-ref-fn (get-in args [:attr :ref])]
                                  (when (fn? user-ref-fn)
                                    (user-ref-fn el))))
          {:keys [file line]} src]
      (when src (emit-render-src-trace! rc-component src))
      (cond->
       {:ref     ref-fn
        :data-rc rc-component}
        src
        (assoc :data-rc-src (str file ":" line))))))

(defn instrument [m props]
  (if-not debug? m (update m :attr merge (->attr props))))

(defn component-stack
  ([el]
   (component-stack [] el))
  ([stack ^js/Element el]
   (if-not el ;; termination condition
     stack
     (let [component          (.. el -dataset -rc)
           ^js/Element parent (.-parentElement el)]
       (->
        (if (= "stack-spy" component)
          stack
          (conj stack
                {:el        el
                 :src       (.. el -dataset -rcSrc)
                 :component component
                 :args      (gobj/get el "__rc-args")}))
        (component-stack parent))))))

(defn validate-args-problems-style
  []
  ;; [IJ] TODO: take min-width, min-height, height, width, size etc from valid args if present; w/ a floor for min-width/min-height
  ;; [IJ] TODO: verify flexbox support in all cases.
  {:min-width      "32px"
   :min-height     "32px"
   :font-size      "1.4em"
   :text-align     "center"
   :vertical-align "center"
   :background      "#FF4136"})

(def h1-style "background: #FF4136; color: white; font-size: 1.4em; padding: 3px")
(def h2-style "background: #0074D9; color: white; padding: 0.25em")
(def code-style "font-family: monospace; font-weight: bold; background: #eee; color: #333; padding: 3px")
(def error-style "font-weight: bold")
(def index-style "font-weight: bold; font-size: 1.1em")

(def collision-icon "\uD83D\uDCA5")
(def gear-icon "⚙️") ;; the trailing 'space' is an intentional modifier, not an actual space, so do not delete it!
(def blue-book-icon "\uD83D\uDCD8")
(def confused-icon "\uD83D\uDE15")
(def globe-icon "\uD83C\uDF10")

(defn log-component-stack
  [stack]
  (js/console.groupCollapsed (str "• %c Component stack (click me)") h2-style)
  (doseq [{:keys [i el component src args]} (map-indexed #(assoc %2 :i (inc %1)) stack)]
    (if component
      (if src
        (let [[file line] (string/split src #":")]
          (if args
            (js/console.log
             (str "%c" i "%c " gear-icon " %c[" component " ...]%c in file %c" file "%c at line %c" line "%c\n      Parameters: %O\n      DOM: %o")
             index-style "" code-style "" code-style "" code-style "" args el)
            (js/console.log
             (str "%c" i "%c " gear-icon " %c[" component " ...]%c in file %c" file "%c at line %c" line "%c\n      DOM: %o")
             index-style "" code-style "" code-style "" code-style "" el)))
        (js/console.log
         (str "%c" i "%c " gear-icon " %c[" component " ...]%c\n      Parameters: %O\n      DOM: %o")
         index-style "" code-style "" args el))
      (js/console.log (str "%c" i "%c " globe-icon " %o") index-style "" el)))
  (js/console.groupEnd))

(defn log-validate-args-error-problems
  [problems]
  (doseq [{:keys [problem arg-name expected actual validate-fn-result]} problems]
    (case problem
      :unknown
      (js/console.log
       (str "• %cUnknown parameter: %c" arg-name)
       error-style code-style)

      :required
      (js/console.log
       (str "• %cMissing required parameter: %c" arg-name)
       error-style code-style)

      :ref
      (js/console.log
       (str "• %cParameter %c" arg-name "%c expected a reactive atom but got a %c" actual)
       error-style code-style error-style code-style)

      :validate-fn
      (js/console.log
       (str "• %cParameter %c" arg-name "%c expected %c" (:type expected) "%c but got %c" actual)
       error-style code-style error-style code-style error-style code-style)

      :validate-fn-map
      (js/console.log
       (str "• %c" (:message validate-fn-result))
       error-style)

      :validate-fn-return
      (js/console.log
       (str "• %c" (if (string? validate-fn-result)
                     validate-fn-result
                     (str "Parameter " arg-name " failed validation: " (pr-str validate-fn-result))))
       error-style)

      :part-top-level-collision
      (js/console.log
       (str "• %cParameter %c" arg-name "%c has been passed both as a top-level argument and within %c:parts%c"
            "\n  - Re-com doesn't know which value to use for configuring the %c" arg-name
            "%c part. \n  - Please delete one or the other.")
       error-style code-style error-style code-style error-style code-style error-style)

      :part-top-level-unsupported
      (js/console.log
       (str "• %cParameter %c" arg-name "%c has been passed as a top-level argument."
            "\n  - This is unsupported. "
            "However, it is supported within %c:parts%c \n  - Please declare %c" arg-name
            "%c within the %c:parts%c map.")
       error-style code-style error-style code-style error-style code-style error-style code-style error-style)

      :part-req-missing
      (js/console.log
       (str "• %cPart %c" arg-name "%c is required but undeclared."
            "\n - Please declare %c" arg-name "%c within the %c:parts%c map.")
       error-style code-style error-style code-style error-style code-style error-style)

      (js/console.log "• " confused-icon " Unknown problem reported"))))

(defn log-validate-args-error
  [element problems component-name {:keys [file line] :as src}]
  (let [source-url    (when (not (empty? root-url-for-compiler-output)) (str root-url-for-compiler-output file ":" line))]
    (js/console.group (str "%c" collision-icon " re-com validation error ") h1-style)
    (if src
      (if source-url
        (js/console.log
         (str "• " gear-icon "%c[" (short-component-name component-name) " ...]%c in file %c" file "%c at line %c" line "%c see " source-url)
         code-style "" code-style "" code-style "")
        (do
          (js/console.log
           (str "• " gear-icon "%c[" (short-component-name component-name) " ...]%c in file %c" file "%c at line %c" line)
           code-style "" code-style "" code-style)
          (js/console.log
           (str "• To enable clickable source urls, add %cre-com.config/root-url-for-compiler-output%c to your %c:closure-defines%c. See https://re-com.day8.com.au/#/config")
           code-style "" code-style "")))
      (do
        (js/console.log
         (str "• " gear-icon "%c[" (short-component-name component-name) " ...]")
         code-style)
        (js/console.log (str "• Learn how to add source coordinates to your components at https://re-com.day8.com.au/#/debug"))))
    (log-validate-args-error-problems problems)
    (log-component-stack (component-stack @element))
    (js/console.groupEnd)))

(defn validate-args-error
  [& {:keys [problems component args]}]
  (let [element            (atom nil)
        ref-fn             (fn [el]
                             (when el
                               (reset! element el)))
        internal-problems  (atom problems)
        internal-component (atom component)
        internal-args      (atom args)]
    (r/create-class
     {:display-name "validate-args-error"

      :component-did-mount
      (fn [this]
        (log-validate-args-error element @internal-problems @internal-component (:src @internal-args)))

      :component-did-update
      (fn [this argv old-state snapshot]
        (log-validate-args-error element @internal-problems @internal-component (:src @internal-args)))

      :reagent-render
      (fn [& {:keys [problems component args]}]
        (reset! internal-problems problems)
        (reset! internal-component component)
        (reset! internal-args args)
        [:div
         (merge
          (->attr {:src      (:src args)
                   :debug-as {:component component
                              :args      args}
                   :attr     {:ref ref-fn}})  ;; important that this ref-fn doesn't get overridden by (->attr ...)
          {:title    "re-com validation error. Look in the DevTools console."
           :style    (validate-args-problems-style)})

         collision-icon])})))

(defn stack-spy
  [& {:keys [component src]}]
  (let [element (atom nil)
        ref-fn  (fn [el]
                  ;; If the ref callback is defined as an inline function, it will get called twice during updates,
                  ;; first with null and then again with the DOM element.
                  ;;
                  ;; See: 'Caveats with callback refs' at
                  ;; https://reactjs.org/docs/refs-and-the-dom.html#caveats-with-callback-refs
                  (when el
                    (reset! element el)))
        log-fn  (fn []
                  (let [el @element]
                    (when el
                      (let [first-child (first (.-children el))]
                        (js/console.group "%c[stack-spy ...]" code-style)
                        (log-component-stack (component-stack first-child))
                        (js/console.groupEnd)))))]
    (r/create-class
     {:display-name         "stack-spy"
      :component-did-mount  log-fn
      :component-did-update log-fn
      :reagent-render
      (fn [& {:keys [component src]}]
        [:div
         (->attr {:src src :attr {:ref ref-fn}}) ;; important that this ref-fn doesn't get overridden by (->attr ...)
         component])})))
