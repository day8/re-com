(ns re-com.splits
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.util     :refer [get-element-by-id]]
   [re-com.box      :refer [flex-child-style flex-flow-style]]
   [re-com.theme    :as    theme]
   [re-com.validate :refer [string-or-hiccup? number-or-string? html-attr? css-style? parts? css-class?] :refer-macros [validate-args-macro]]
   [reagent.core    :as    r]
   [re-com.part :as p]
   re-com.splits.theme
   [re-com.h-split :as-alias hs]
   [re-com.v-split :as-alias vs]))

;; ------------------------------------------------------------------------------------
;;  Component: h-split
;; ------------------------------------------------------------------------------------

(def h-split-part-structure
  [::hs/wrapper
   [::hs/left]
   [::hs/splitter
    [::hs/handle
     [::hs/handle-bar-1]
     [::hs/handle-bar-2]]]
   [::hs/right]])

(def v-split-part-structure
  [::vs/wrapper
   [::vs/top]
   [::vs/splitter
    [::vs/handle
     [::vs/handle-bar-1]
     [::vs/handle-bar-2]]]
   [::vs/bottom]])

(def h-split-parts-desc
  (when include-args-desc?
    (p/describe h-split-part-structure)))

(def v-split-parts-desc
  (when include-args-desc?
    (p/describe v-split-part-structure)))

(def hv-split-parts-desc
  (when include-args-desc?
    (vec (concat h-split-parts-desc v-split-parts-desc))))

(def hv-split-parts
  (when include-args-desc?
    (into #{} (map :name) hv-split-parts-desc)))

(def hv-split-args-desc
  (when include-args-desc?
    [{:name :panel-1 :required true :type "hiccup" :validate-fn string-or-hiccup? :description "markup to go in the left (or top) panel"}
     {:name :panel-2 :required true :type "hiccup" :validate-fn string-or-hiccup? :description "markup to go in the right (or bottom) panel"}
     {:name :size :required false :default "auto" :type "string" :validate-fn string? :description [:span "applied to the outer container of the two panels. Equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"] ", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
     {:name :width :required false :type "string" :validate-fn string? :description "width of the outer container of the two panels. A CSS width style"}
     {:name :height :required false :type "string" :validate-fn string? :description "height of the outer container of the two panels. A CSS height style"}
     {:name :split-is-px? :required false :default false :type "boolean" :description [:span "when true, " [:code ":initial-split"] " is interpreted to be a fixed px value, otherwise a percentage value"]}
     {:name :initial-split :required false :default 50 :type "double | string" :validate-fn number-or-string? :description [:span "the initial size of " [:code ":panel-1"] ". Subject to " [:code ":split-is-px?"] ", it is either the initial split percentage for " [:code ":panel-1"] " (can be double value or string with/without percentage sign) or a fixed px value"]}
     {:name :on-split-change :required false :type "double -> nil" :validate-fn fn? :description [:span "called when the user moves the splitter bar (on mouse up, not on each mouse move). Given the new " [:code ":panel-1"] " percentage split"]}
     {:name :splitter-size :required false :default "8px" :type "string" :validate-fn string? :description "thickness of the splitter"}
     {:name :margin :required false :default "8px" :type "string" :validate-fn string? :description "thickness of the margin around the panels"}
     {:name :class :required false :type "string" :validate-fn css-class? :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style :required false :type "CSS style map" :validate-fn css-style? :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr :required false :type "HTML attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts :required false :type "map" :validate-fn (parts? hv-split-parts) :description "See Parts section below."}
     {:name :src :required false :type "map" :validate-fn map? :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as :required false :type "map" :validate-fn map? :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn h-split
  "Returns markup for a horizontal layout component"
  [& {:keys              [size width height split-is-px? on-split-change initial-split
                          splitter-size margin src pre-theme theme]
      {user-ref-fn :ref} :attr
      :or                {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as                args}]
  (or
   (validate-args-macro hv-split-args-desc args)
   (let [theme               (theme/comp pre-theme theme)
         wrapper-ref         (r/atom nil)
         wrapper-ref!        (fn [el]
                               (reset! wrapper-ref el)
                               (when (fn? user-ref-fn) (user-ref-fn el)))
         split-perc          (r/atom (js/parseInt initial-split)) ;; splitter position as a percentage of width
         dragging?           (r/atom false)                       ;; is the user dragging the splitter (mouse is down)?
         over?               (r/atom false)                       ;; is the mouse over the splitter, if so, highlight it
         stop-drag           (fn []
                               (when on-split-change (on-split-change @split-perc))
                               (reset! dragging? false))
         calc-perc           (fn [mouse-x]                                                                 ;; turn a mouse x coordinate into a percentage position
                               (when-let [wrapper @wrapper-ref]
                                 (let [c-width    (.-clientWidth wrapper)                                  ;; the container's width
                                       c-left-x   (+ (.-pageXOffset js/window)
                                                     (-> wrapper .getBoundingClientRect .-left))           ;; the container's left X
                                       relative-x (- mouse-x c-left-x)]                                      ;; the X of the mouse, relative to container
                                   (if split-is-px?
                                     relative-x                                              ;; return the left offset in px
                                     (* 100.0 (/ relative-x c-width))))))                     ;; return the percentage panel-1 width against container width
         <html>?             #(= % (.-documentElement js/document))                        ;; test for the <html> element
         mouseout            (fn [event]
                               (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                                 (stop-drag)))
         mousemove           (fn [event]
                               (reset! split-perc (calc-perc (.-clientX event))))
         mousedown           (fn [event]
                               (.preventDefault event)                                    ;; stop selection of text during drag
                               (reset! dragging? true))
         mouseover-split     #(reset! over? true) ;; true CANCELs mouse-over (false cancels all others)
         mouseout-split      #(reset! over? false)
         make-splitter-attrs (fn [class style attr])]
     (fn h-split-render
       [& {:keys [panel-1 panel-2 _size _width _height _on-split-change
                  _initial-split _splitter-size _margin
                  class style attr parts]
           :as   args}]
       (let [part (partial p/part h-split-part-structure args)]
         (part ::hs/wrapper
           {:theme      theme
            :post-props (select-keys args [:class :style])
            :props
            {:style (merge (flex-child-style size)
                           (flex-flow-style "row nowrap")
                           {:margin margin
                            :width  width
                            :height height})
             :attr  (merge
                     (when @dragging?
                       {:on-mouse-up   (handler-fn (stop-drag))
                        :on-mouse-move (handler-fn (mousemove event))
                        :on-mouse-out  (handler-fn (mouseout event))})
                     attr
                     (->attr (assoc-in args [:attr :ref] wrapper-ref!)))
             :children
             [(part ::hs/left
                {:theme theme
                 :props
                 {:style    (merge (flex-child-style
                                    (if split-is-px?
                                      (str "0 0 "  @split-perc "px")
                                      (str  @split-perc " 1 0px")))
                                   (when @dragging? {:pointer-events "none"})
                                   (get-in parts [:top :style]))
                  :attr     (get-in parts [:top :attr])                  ;; targetable using the :top part for backward compatibility
                  :children [panel-1]}})
              (part ::hs/splitter
                {:theme theme
                 :props
                 {:re-com {:state {:hover (if @over? :active :idle)}}
                  :style  (flex-child-style (str "0 0 " splitter-size))
                  :attr   {:on-mouse-down (handler-fn (mousedown event))
                           :on-mouse-over (handler-fn (mouseover-split))
                           :on-mouse-out  (handler-fn (mouseout-split))}
                  :children
                  [(part ::hs/handle
                     {:theme theme
                      :props {:re-com {:state {:hover (if @over? :active :idle)}}
                              :children
                              [(part ::hs/handle-bar-1
                                 {:theme theme
                                  :props {:re-com {:state {:hover (if @over? :active :idle)}}}})
                               (part ::hs/handle-bar-2
                                 {:theme theme
                                  :props {:re-com {:state {:hover (if @over? :active :idle)}}}})]}})]}})
              (let [percentage (if split-is-px?
                                 (- @split-perc)
                                 (- 100 @split-perc))]
                (part ::hs/right
                  {:theme theme
                   :props
                   {:style    (merge (flex-child-style
                                      (if split-is-px? "1 1 0px" (str percentage " 1 0px")))
                                     (when   @dragging? {:pointer-events "none"})
                                     (get-in parts [:bottom :style]))
                    :attr     (get-in parts [:bottom :attr])
                    :children [panel-2]}}))]}}))))))

;; ------------------------------------------------------------------------------------
;;  Component: v-split
;; ------------------------------------------------------------------------------------

(defn v-split
  "Returns markup for a vertical layout component"
  [& {:keys              [size width height split-is-px? on-split-change
                          initial-split splitter-size margin
                          src pre-theme theme]
      {user-ref-fn :ref} :attr
      :or                {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as                args}]
  (or
   (validate-args-macro hv-split-args-desc args)
   (let [theme           (theme/comp pre-theme theme)
         wrapper-ref     (r/atom nil)
         wrapper-ref!    (fn [el]
                           (reset! wrapper-ref el)
                           (when (fn? user-ref-fn) (user-ref-fn el)))
         split-perc      (r/atom (js/parseInt initial-split))  ;; splitter position as a percentage of height
         dragging?       (r/atom false)                        ;; is the user dragging the splitter (mouse is down)?
         over?           (r/atom false)                        ;; is the mouse over the splitter, if so, highlight it
         stop-drag       (fn []
                           (when on-split-change (on-split-change @split-perc))
                           (reset! dragging? false))
         calc-perc       (fn [mouse-y]                                                                ;; turn a mouse y coordinate into a percentage position
                           (when-let [wrapper @wrapper-ref]
                             (let [c-height   (.-clientHeight wrapper)                                ;; the container's height
                                   c-top-y    (+ (.-pageYOffset js/window)
                                                 (-> wrapper .getBoundingClientRect .-top))           ;; the container's top Y
                                   relative-y (- mouse-y c-top-y)]                                      ;; the Y of the mouse, relative to container
                               (if split-is-px?
                                 relative-y                                              ;; return the top offset in px
                                 (* 100.0 (/ relative-y c-height))))))                    ;; return the percentage panel-1 height against container width
         <html>?         #(= % (.-documentElement js/document))                        ;; test for the <html> element
         mouseout        (fn [event]
                           (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                             (stop-drag)))
         mousemove       (fn [event]
                           (reset! split-perc (calc-perc (.-clientY event))))
         mousedown       (fn [event]
                           (.preventDefault event)                                    ;; stop selection of text during drag
                           (reset! dragging? true))
         mouseover-split #(reset! over? true)
         mouseout-split  #(reset! over? false)]
     (fn v-split-render
       [& {:keys [panel-1 panel-2 _size _width _height _on-split-change
                  _initial-split _splitter-size _margin attr parts]
           :as   args}]
       (let [part (partial p/part v-split-part-structure args)]
         (part ::vs/wrapper
           {:theme      theme
            :post-props (select-keys args [:class :style])
            :props
            {:style (merge (flex-child-style size)
                           (flex-flow-style "column nowrap")
                           {:margin margin
                            :width  width
                            :height height})
             :attr  (merge
                     (when @dragging?
                       {:on-mouse-up   (handler-fn (stop-drag))
                        :on-mouse-move (handler-fn (mousemove event))
                        :on-mouse-out  (handler-fn (mouseout event))})
                     attr
                     {:ref wrapper-ref!}
                     (->attr (assoc-in args [:attr :ref] wrapper-ref!)))
             :children
             [(part ::vs/top
                {:theme theme
                 :props
                 {:style    (merge (flex-child-style
                                    (if split-is-px?
                                      (str "0 0 " @split-perc "px")
                                      (str @split-perc " 1 0px")))
                                   (when @dragging? {:pointer-events "none"})
                                   (get-in parts [:top :style]))
                  :attr     (get-in parts [:top :attr])
                  :children [panel-1]}})
              (part ::vs/splitter
                {:theme theme
                 :props
                 {:re-com {:state {:hover (if @over? :active :idle)}}
                  :style  (flex-child-style (str "0 0 " splitter-size))
                  :attr   {:on-mouse-down (handler-fn (mousedown event))
                           :on-mouse-over (handler-fn (mouseover-split))
                           :on-mouse-out  (handler-fn (mouseout-split))}
                  :children
                  [(part ::vs/handle
                     {:theme theme
                      :props {:re-com {:state {:hover (if @over? :active :idle)}}
                              :children
                              [(part ::vs/handle-bar-1
                                 {:theme theme
                                  :props {:re-com {:state {:hover (if @over? :active :idle)}}}})
                               (part ::vs/handle-bar-2
                                 {:theme theme
                                  :props {:re-com {:state {:hover (if @over? :active :idle)}}}})]}})]}})
              (let [percentage (if split-is-px?
                                 (- @split-perc)
                                 (- 100 @split-perc))]
                (part ::vs/bottom
                  {:theme theme
                   :props
                   {:style    (merge (flex-child-style
                                      (if split-is-px? "1 1 0px" (str percentage " 1 0px")))
                                     (when @dragging? {:pointer-events "none"})
                                     (get-in parts [:bottom :style]))
                    :attr     (get-in parts [:bottom :attr])
                    :children [panel-2]}}))]}}))))))

