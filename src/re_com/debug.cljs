(ns re-com.debug
  (:require
    [goog.object            :as    gobj]
    [cljs.reader            :refer [read-string]]
    [clojure.string         :as    string]
    [reagent.core           :as    r]
    [reagent.impl.component :as    component]
    [re-com.config          :refer [debug? root-url-for-compiler-output]]))

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
      (dissoc args :src :child :children :panel-1 :panel-2 :debug-as)
      ;; Remove args with nil value
      (remove (comp nil? second))
      (into {}))
    args))

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
      (cond->
        {:ref     ref-fn
         :data-rc rc-component}
        src
        (assoc :data-rc-src (str file ":" line))))))

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
      ;; [IJ] TODO: :validate-fn-return
      :unknown         (js/console.log
                         (str "• %cUnknown parameter: %c" arg-name)
                         error-style code-style)
      :required        (js/console.log
                         (str "• %cMissing required parameter: %c" arg-name)
                         error-style code-style)
      :validate-fn     (js/console.log
                         (str "• %cParameter %c" arg-name "%c expected %c" (:type expected ) "%c but got %c" actual)
                         error-style code-style error-style code-style error-style code-style)
      :validate-fn-map (js/console.log
                         (str "• %c" (:message validate-fn-result))
                         error-style)
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
            (str "• " blue-book-icon " Add %cre-com.config/root-url-for-compiler-output%c to your %c:closure-defines%c to enable clickable source urls")
            code-style "" code-style "")))
      (do
        (js/console.log
          (str "• " gear-icon "%c[" (short-component-name component-name) " ...]")
          code-style)
        (js/console.log (str "• " blue-book-icon " Learn how to add source coordinates to your components at https://re-com.day8.com.au/#/debug"))))
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
