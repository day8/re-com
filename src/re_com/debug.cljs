(ns re-com.debug
  (:require
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

(defn src->attr
  ([src]
   (src->attr src (component/component-name (r/current-component))))
  ([{:keys [file line] :as src} component-name]
   (if debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
     (if src
       {:data-rc-src       (str file ":" line)
        :data-rc-component (short-component-name component-name)}
       {})
     {})))

(defn component-stack
  ([el]
   (component-stack [] el))
  ([stack ^js/Element el]
   (if-not el ;; termination condition
     stack
     (let [^js/Element parent (.-parentElement el)]
       (-> (conj stack
                 {:el        el
                  :src       (.. el -dataset -rcSrc)
                  :component (.. el -dataset -rcComponent)})
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

(defn validate-args-log
  [element problems component-name {:keys [file line] :as src}]
  (let [source-url    (when root-url-for-compiler-output (str root-url-for-compiler-output file ":" line))]
    (js/console.group "%c\uD83D\uDCA5 re-com validation error " h1-style)
    (if src
      (if source-url
        (js/console.log
          (str "• ⚙️%c[" (short-component-name component-name) " ...]%c in file %c" file "%c at line %c" line "%c see " source-url)
          code-style "" code-style "" code-style "")
        (do
          (js/console.log
            (str "• ⚙️%c[" (short-component-name component-name) " ...]%c in file %c" file "%c at line %c" line)
            code-style "" code-style "" code-style)
          (js/console.log
            (str "• \uD83D\uDCD8 Add %cre-com.config/root-url-for-compiler-output%c to your %c:closure-defines%c to enable clickable source urls")
            code-style "" code-style "")))
      (do
        (js/console.log
          (str "• \uD83D\uDCC1️ %c[" (short-component-name component-name) " ...]")
          code-style)
        (js/console.log (str "• \uD83D\uDCD8 Learn how to add source coordinates to your components at https://re-com.day8.com.au/#/debug"))))
    (doseq [{:keys [problem arg-name expected actual validate-fn-result]} problems]
      (case problem
        ;; [IJ] TODO: :validate-fn-return
        :unknown         (js/console.log
                           (str "• ❓ %cUnknown parameter: %c" arg-name)
                           error-style code-style)
        :required        (js/console.log
                           (str "• ❗  %cMissing required parameter: %c" arg-name)
                           error-style code-style)
        :validate-fn     (js/console.log
                           (str "• ≢  %cParameter %c" arg-name "%c expected %c" (:type expected ) "%c but got %c" actual)
                           error-style code-style error-style code-style error-style code-style)
        :validate-fn-map (js/console.log
                           (str "• \uD83D\uDE45\uD83C\uDFFD %c" (:message validate-fn-result))
                           error-style)
        (js/console.log "• \uD83D\uDE15 Unknown problem reported")))
    (js/console.groupCollapsed (str "• %c component stack (click me)") h2-style)
    (doseq [{:keys [i el component src]} (map-indexed #(assoc %2 :i (inc %1)) (component-stack @element))]
      (if src
        (let [[file line] (string/split src #":")]
          (js/console.log
            (str "%c" i "%c ⚙️ %c[" component " ...]%c in file %c" file "%c at line %c" line "%c %o")
            index-style "" code-style "" code-style "" code-style "" el))
        (js/console.log (str "%c" i "%c \uD83C\uDF10 %o") index-style "" el)))
    (js/console.groupEnd)
    (js/console.groupEnd)))

(defn validate-args-problems
  [& {:keys [problems component-name src]}]
  (let [element                 (atom nil)
        internal-problems       (atom problems)
        internal-component-name (atom component-name)
        internal-src            (atom src)]
    (r/create-class
      {:display-name "validate-args-problems"

       :component-did-mount
       (fn [this]
         (validate-args-log element @internal-problems @internal-component-name @internal-src))

       :component-did-update
       (fn [this argv old-state snapshot]
         (validate-args-log element @internal-problems @internal-component-name @internal-src))

       :reagent-render
       (fn [& {:keys [problems component-name src]}]
         (reset! internal-problems problems)
         (reset! internal-component-name component-name)
         (reset! internal-src src)
         [:div
          (merge
            {:title    "re-com validation error. Look in the devtools console."
             :ref      (fn [el] (reset! element el))
             :style    (validate-args-problems-style)}
            (src->attr src component-name))
          "\uD83D\uDCA5"])})))

