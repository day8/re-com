(ns re-com.debug
  (:require
    [clojure.string         :as    string]
    [reagent.core           :as    r]
    [reagent.impl.component :as    component]
    [re-com.config          :refer [debug? root-url-for-compiler-output]]))

(defn src->attr
  ([src]
   (src->attr src (component/component-name (r/current-component))))
  ([{:keys [file line] :as src} component-name]
   (if debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
     (if src
       {:data-rc-src            (str file ":" line)
        :data-rc-component-name component-name}
       {})
     {})))

(defn component-stack
  ([el]
   (component-stack [] el))
  ([stack ^js/Element el]
   (if-not el ;; termination condition
     stack
     (let [src                (.. el -dataset -rcSrc)
           component-name     (.. el -dataset -rcComponentName)
           ^js/Element parent (.-parentElement el)]
       (-> (conj stack
                 {:el             el
                  :src            src
                  :component-name component-name
                  :width          (.-offsetWidth el)
                  :height         (.-offsetHeight el)})
           (component-stack parent))))))

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
      (string/replace #"-render" "")))

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

(defn validate-args-log
  [element problems component-name {:keys [file line] :as src}]
  (let [monospace-style      "font-family: monospace; font-weight: bold; background: #eee; color: #333; padding: 3px"]
    (js/console.group "%c\uD83D\uDCA5 re-com validation error " "background: #FF4136; color: white; font-size: 1.4em; padding: 3px")
    (js/console.log (str "• ⚙️ %c[" (short-component-name component-name) " ...]") monospace-style)
    (if src
      ;; [IJ] TODO add Google Closure build config for this base URL
      (do
        (js/console.log (str "• \uD83D\uDCC1 in file %c" file "%c at line %c" line) monospace-style "" monospace-style)
        (when-not (empty? root-url-for-compiler-output)
          (js/console.log (str "• \uD83D\uDD17 source: " root-url-for-compiler-output file ":" line))))
      (js/console.log (str "• \uD83D\uDCD8 learn how to add source coordinates to your components at https://re-com.day8.com.au/#/debug")))
    (doseq [{:keys [problem arg-name expected actual validate-fn-result]} problems]
      (case problem
        ;; [IJ] TODO: :validate-fn-return
        :unknown         (js/console.log (str "• ❓ %cUnknown parameter: %c" arg-name) "font-weight: bold" monospace-style)
        :required        (js/console.log (str "• ❗  %cMissing required parameter: %c" arg-name) "font-weight: bold" monospace-style)
        :validate-fn     (js/console.log (str "• ≢  Parameter %c" arg-name "%c expected %c" (:type expected ) "%c but got %c" actual) monospace-style "" monospace-style "" monospace-style)
        :validate-fn-map (js/console.log (str "• \uD83D\uDE45\uD83C\uDFFD " (:message validate-fn-result)))
        (js/console.log "• \uD83D\uDE15 Unknown problem reported")))
    (js/console.groupCollapsed (str "• %c component stack (click me)")
                               "background: #0074D9; color: white; padding: 0.25em ")
    ;; [IJ] TODO: width and height etc.
    (doseq [{:keys [i el component-name src width height]} (map-indexed #(assoc %2 :i (inc %1)) (component-stack @element))]
      (if src
        (let [[file line] (string/split src #":")]
          (js/console.log (str "%c" i "%c ⚙️ %c[" (short-component-name component-name) " ...]%c \uD83D\uDCC1 %c" file "%c:%c" line " %o") "font-weight: bold; font-size: 1.2em" "" monospace-style "" monospace-style "" monospace-style el))
        (js/console.log (str "%c" i "%c \uD83C\uDF10 %o") "font-weight: bold; font-size: 1.2em" "" el)))
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

