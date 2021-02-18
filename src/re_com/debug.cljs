(ns re-com.debug
  (:require
    [clojure.string :as string]
    [reagent.core   :as r]
    [re-com.config  :refer [debug?]]))

(defn src->attr
  [{:keys [file line] :as src}]
  (if true ;; debug? ;; This is in a separate `if` so Google Closure dead code elimination can run...
    (if src
      {:data-rc-src (str file ":" line)}
      {})
    {}))

(defn component-stack
  ([el]
   (component-stack [] el))
  ([stack ^js/Element el]
   (if-not el ;; termination condition
     stack
     (let [src                (.. el -dataset -rcSrc)
           ^js/Element parent (.-parentElement el)]
       (-> (conj stack
                 {:el el
                  :src src
                  :width (.-offsetWidth el)
                  :height (.-offsetHeight el)})
           (component-stack parent))))))

(defn validate-args-problems-style
  []
  {:min-width  "32px"
   :min-height "32px"
   :background "#FF4136"})

(defn validate-args-problems
  [& {:keys [problems component-name src]}]
  (let [element              (atom nil)
        {:keys [file line]}  src
        monospace-style      "font-family: monospace; font-weight: bold; background: #eee; color: #333; padding: 3px"
        ;; reagent.impl.component/component-name is used to obtain the component name, which returns
        ;; e.g. re_com.checkbox.checkbox. We are only interested in the last part.
        ;;
        ;; Also some components are form-2 or form-3 so will return -return from the anonymous render
        ;; function name. We keep the -render in the anonymous function name for JavaScript stack
        ;; traces for non-validation errors (i.e. exceptions), but we are not interested in that here.
        short-component-name (-> component-name
                                 (string/split #"\.")
                                 (last)
                                 (string/replace #"-render" ""))]
    (r/create-class
      {:display-name "validate-args-problems"

       :component-did-mount
       (fn [this]
         (js/console.group "%c\uD83D\uDC80re-com validation errors" "background: #FF4136; color: white; font-size: 1.4em; padding: 3px")
         (js/console.log (str "• ⚙️ %c[" short-component-name " ...]") monospace-style)
         (if src
           ;; [IJ] TODO add Google Closure build config for this base URL
           (do
             (js/console.log (str "• \uD83D\uDCC1 in file %c" file "%c at line %c" line) monospace-style "" monospace-style)
             (js/console.log (str "• \uD83D\uDD17 source: http://localhost:3449/compiled_dev/demo/cljs-runtime/" file ":" line)))
           (js/console.log (str "• \uD83D\uDCD8 learn to add source coordinates to your components at https://re-com.day8.com.au/#/debug")))
         (doseq [{:keys [problem arg-name expected actual validate-fn-result]} problems]
           (case problem
             ;; [IJ] TODO: :validate-fn-map and :validate-fn-return
             :unknown     (js/console.log (str "• ❓ unknown argument: %c" arg-name) monospace-style)
             :required    (js/console.log (str "• ❗  missing required argument: %c" arg-name) monospace-style)
             :validate-fn (js/console.log (str "• ≢  expected %c" (:type expected ) "%c but got %c" actual) monospace-style "" monospace-style)
             (js/console.log "• \uD83D\uDE15 unknown problem reported")))
         (js/console.groupCollapsed (str "%c\uD83C\uDF32 component stack") "background: #0074D9; color: white; font-size: 1.4em; padding: 3px")
         ;; [IJ] TODO: width and height etc.
         (doseq [{:keys [i el src width height]} (map-indexed #(assoc %2 :i (inc %1)) (component-stack @element))]
           (if src
             (let [[file line] (string/split src #":")]
               (js/console.log (str "%c" i "%c in file %c" file "%c at line %c" line " %o") "font-weight: bold; font-size: 1.2em" "" monospace-style "" monospace-style el)
               (js/console.log (str "%c" i "%c %o") "font-weight: bold; font-size: 1.2em" "" el))))
         (js/console.groupEnd)
         (js/console.groupEnd))


       :reagent-render
       (fn [& {:keys [problems component-name]}]
         [:div
          (merge
            {:title    "re-com validation error. Look in the devtools console."
             :ref      (fn [el] (reset! element el))
             :style    (validate-args-problems-style)}
            (src->attr src))])})))

