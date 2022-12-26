(ns re-com.splits
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]])
  (:require
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :refer [->attr]]
    [re-com.util     :refer [get-element-by-id sum-scroll-offsets]]
    [re-com.box      :refer [flex-child-style flex-flow-style]]
    [re-com.validate :refer [string-or-hiccup? number-or-string? html-attr? css-style? parts?] :refer-macros [validate-args-macro]]
    [reagent.core    :as    reagent]))


(defn drag-handle
  "Return a drag handle to go into a vertical or horizontal splitter bar:
    orientation: Can be :horizontal or :vertical
    over?:       When true, the mouse is assumed to be over the splitter so show a bolder color"
  [orientation over? parts]
  (let [vertical? (= orientation :vertical)
        length    "20px"
        width     "8px"
        pos1      "3px"
        pos2      "3px"
        color     (if over? "#999" "#ccc")
        border    (str "solid 1px " color)
        flex-flow (str (if vertical? "row" "column") " nowrap")]
    [:div
     (merge
       {:class (str "rc-" (if vertical? "v" "h") "-split-handle display-flex " (get-in parts [:handle :class]))
        :style (merge (flex-flow-style flex-flow)
                      {:width  (if vertical? width length)
                       :height (if vertical? length width)
                       :margin "auto"}
                      (get-in parts [:handle :style]))}
       (get-in parts [:handle :attr]))
     [:div
      (merge
        {:class (str "rc-" (if vertical? "v" "h") "-split-handle-bar-1 " (get-in parts [:handle-bar-1 :class]))
         :style (merge
                  (if vertical?
                    {:width pos1   :height length :border-right  border}
                    {:width length :height pos1   :border-bottom border})
                  (get-in parts [:handle-bar-1 :style]))}
        (get-in parts [:handle-bar-1 :attr]))]
     [:div
      (merge
        {:class (str "rc-" (if vertical? "v" "h") "-split-handle-bar-2 " (get-in parts [:handle-bar-2 :class]))
         :style (merge
                  (if vertical?
                    {:width pos2   :height length :border-right  border}
                    {:width length :height pos2   :border-bottom border})
                  (get-in parts [:handle-bar-2 :style]))}
        (get-in parts [:handle-bar-2 :attr]))]]))


;; ------------------------------------------------------------------------------------
;;  Component: h-split
;; ------------------------------------------------------------------------------------

(def hv-split-parts-desc
  (when include-args-desc?
    [{:type :legacy       :level 0 :class "rc-h-split"              :impl "[h-split]" :notes "Outer wrapper of the split."}
     {:name :left         :level 1 :class "rc-h-split-left"         :impl "[:div]"    :notes "First (i.e. left) panel of the split."}
     {:name :splitter     :level 1 :class "rc-h-split-splitter"     :impl "[:div]"    :notes "The splitter between panels."}
     {:name :handle       :level 2 :class "rc-h-split-handle"       :impl "[:div]"    :notes "The splitter handle."}
     {:name :handle-bar-1 :level 3 :class "rc-h-split-handle-bar-1" :impl "[:div]"    :notes "The splitter handle's first bar."}
     {:name :handle-bar-2 :level 3 :class "rc-h-split-handle-bar-2" :impl "[:div]"    :notes "The splitter handle's second bar."}
     {:name :right        :level 1 :class "rc-h-split-right"        :impl "[:div]"    :notes "Second (i.e. right) panel of the split."}
     {:type :legacy       :level 0 :class "rc-v-split"              :impl "[v-split]" :notes "Outer wrapper of the split."}
     {:name :top          :level 1 :class "rc-v-split-top"          :impl "[:div]"    :notes "First (i.e. top) panel of the split."}
     {:name :splitter     :level 1 :class "rc-v-split-splitter"     :impl "[:div]"    :notes "The splitter between panels."}
     {:name :handle       :level 2 :class "rc-v-split-handle"       :impl "[:div]"    :notes "The splitter handle."}
     {:name :handle-bar-1 :level 3 :class "rc-v-split-handle-bar-1" :impl "[:div]"    :notes "The splitter handle's first bar."}
     {:name :handle-bar-2 :level 3 :class "rc-v-split-handle-bar-2" :impl "[:div]"    :notes "The splitter handle's second bar."}
     {:name :bottom       :level 1 :class "rc-v-split-bottom"       :impl "[:div]"    :notes "Second (i.e. bottom) panel of the split."}]))

(def hv-split-parts
  (when include-args-desc?
    (-> (map :name hv-split-parts-desc) set)))

(def hv-split-args-desc
  (when include-args-desc?
    [{:name :panel-1         :required true                  :type "hiccup"          :validate-fn string-or-hiccup?       :description "markup to go in the left (or top) panel"}
     {:name :panel-2         :required true                  :type "hiccup"          :validate-fn string-or-hiccup?       :description "markup to go in the right (or bottom) panel"}
     {:name :size            :required false :default "auto" :type "string"          :validate-fn string?                 :description [:span "applied to the outer container of the two panels. Equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
     {:name :width           :required false                 :type "string"          :validate-fn string?                 :description "width of the outer container of the two panels. A CSS width style"}
     {:name :height          :required false                 :type "string"          :validate-fn string?                 :description "height of the outer container of the two panels. A CSS height style"}
     {:name :split-is-px?    :required false :default false  :type "boolean"                                              :description [:span "when true, " [:code ":initial-split"] " is interpreted to be a fixed px value, otherwise a percentage value"]}
     {:name :initial-split   :required false :default 50     :type "double | string" :validate-fn number-or-string?       :description [:span "the initial size of " [:code ":panel-1"] ". Subject to " [:code ":split-is-px?"] ", it is either the initial split percentage for " [:code ":panel-1"] " (can be double value or string with/without percentage sign) or a fixed px value"]}
     {:name :on-split-change :required false                 :type "double -> nil"   :validate-fn fn?                     :description [:span "called when the user moves the splitter bar (on mouse up, not on each mouse move). Given the new " [:code ":panel-1"] " percentage split"]}
     {:name :splitter-size   :required false :default "8px"  :type "string"          :validate-fn string?                 :description "thickness of the splitter"}
     {:name :margin          :required false :default "8px"  :type "string"          :validate-fn string?                 :description "thickness of the margin around the panels"}
     {:name :class           :required false                 :type "string"          :validate-fn string?                 :description "CSS class names, space separated (applies to the outer container)"}
     {:name :style           :required false                 :type "CSS style map"   :validate-fn css-style?              :description "CSS styles to add or override (applies to the outer container)"}
     {:name :attr            :required false                 :type "HTML attr map"   :validate-fn html-attr?              :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     {:name :parts           :required false                 :type "map"             :validate-fn (parts? hv-split-parts) :description "See Parts section below."}
     {:name :src             :required false                 :type "map"             :validate-fn map?                    :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as        :required false                 :type "map"             :validate-fn map?                    :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn h-split
  "Returns markup for a horizontal layout component"
  [& {:keys [size width height split-is-px? on-split-change initial-split splitter-size margin src]
      :or   {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  (or
    (validate-args-macro hv-split-args-desc args)
    (let [container-id         (gensym "h-split-")
          split-perc           (reagent/atom (js/parseInt initial-split)) ;; splitter position as a percentage of width
          dragging?            (reagent/atom false)                       ;; is the user dragging the splitter (mouse is down)?
          over?                (reagent/atom false)                       ;; is the mouse over the splitter, if so, highlight it

          stop-drag            (fn []
                                 (when on-split-change (on-split-change @split-perc))
                                 (reset! dragging? false))

          calc-perc            (fn [mouse-x]                                                 ;; turn a mouse y coordinate into a percentage position
                                 (let [container  (get-element-by-id container-id)           ;; the outside container
                                       offsets    (sum-scroll-offsets container)             ;; take any scrolling into account
                                       c-width    (.-clientWidth container)                  ;; the container's width
                                       c-left-x   (.-offsetLeft container)                   ;; the container's left X
                                       relative-x (+ (- mouse-x c-left-x) (:left offsets))]  ;; the X of the mouse, relative to container
                                   (if split-is-px?
                                     relative-x                                              ;; return the left offset in px
                                     (* 100.0 (/ relative-x c-width)))))                     ;; return the percentage panel-1 width against container width

          <html>?              #(= % (.-documentElement js/document))                        ;; test for the <html> element

          mouseout             (fn [event]
                                 (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                                   (stop-drag)))

          mousemove            (fn [event]
                                 (reset! split-perc (calc-perc (.-clientX event))))

          mousedown            (fn [event]
                                 (.preventDefault event)                                    ;; stop selection of text during drag
                                 (reset! dragging? true))

          mouseover-split      #(reset! over? true) ;; true CANCELs mouse-over (false cancels all others)
          mouseout-split       #(reset! over? false)

          make-container-attrs (fn [class style attr in-drag?]
                                 (merge {:class (str "rc-h-split display-flex " class)
                                         :id    container-id
                                         :style (merge (flex-child-style size)
                                                       (flex-flow-style "row nowrap")
                                                       {:margin margin
                                                        :width  width
                                                        :height height}
                                                       style)}
                                        (when in-drag?                             ;; only listen when we are dragging
                                          {:on-mouse-up   (handler-fn (stop-drag))
                                           :on-mouse-move (handler-fn (mousemove event))
                                           :on-mouse-out  (handler-fn (mouseout event))})
                                        (->attr args)
                                        attr))

          make-panel-attrs     (fn [class style attr in-drag? percentage]
                                 (merge
                                   {:class (str "display-flex " class)
                                    :style (merge (flex-child-style (if split-is-px?
                                                                      (if (pos? percentage)
                                                                        (str "0 0 " percentage "px") ;; flex for panel-1
                                                                        (str "1 1 0px"))             ;; flex for panel-2
                                                                      (str percentage " 1 0px")))
                                                  {:overflow "hidden"} ;; TODO: Shouldn't have this...test removing it
                                                  (when in-drag? {:pointer-events "none"})
                                                  style)}
                                   attr))

          make-splitter-attrs  (fn [class style attr]
                                 (merge
                                   {:class         (str "display-flex " class)
                                    :on-mouse-down (handler-fn (mousedown event))
                                    :on-mouse-over (handler-fn (mouseover-split))
                                    :on-mouse-out  (handler-fn (mouseout-split))
                                    :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                          {:cursor "col-resize"}
                                                          (when @over? {:background-color "#f8f8f8"})
                                                          style)}
                                   attr))]

      (fn h-split-render
        [& {:keys [panel-1 panel-2 _size _width _height _on-split-change _initial-split _splitter-size _margin class style attr parts src]}]
        [:div (make-container-attrs class style attr @dragging?)
         [:div (make-panel-attrs
                 ;; Leaving rc-h-split-top class (below) for backwards compatibility only.
                 (str "rc-h-split-top rc-h-split-left " (get-in parts [:left :class]))
                 (get-in parts [:top :style])
                 (get-in parts [:top :attr])
                 @dragging? @split-perc)
          panel-1]
         [:div (make-splitter-attrs
                 (str "rc-h-split-splitter " (get-in parts [:splitter :class]))
                 (get-in parts [:splitter :style])
                 (get-in parts [:splitter :attr]))
          [drag-handle :vertical @over? parts]]
         [:div (make-panel-attrs
                 ;; Leaving rc-h-split-bottom class (below) for backwards compatibility only.
                 (str "rc-h-split-bottom rc-h-split-right " (get-in parts [:right :class]))
                 (get-in parts [:bottom :style])
                 (get-in parts [:bottom :attr])
                 @dragging? (if split-is-px?
                              (- @split-perc) ;; Negative value indicates this is for panel-2
                              (- 100 @split-perc)))
          panel-2]]))))


;; ------------------------------------------------------------------------------------
;;  Component: v-split
;; ------------------------------------------------------------------------------------

(defn v-split
  "Returns markup for a vertical layout component"
  [& {:keys [size width height split-is-px? on-split-change initial-split splitter-size margin src]
      :or   {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  (or
    (validate-args-macro hv-split-args-desc args)
    (let [container-id         (gensym "v-split-")
          split-perc           (reagent/atom (js/parseInt initial-split))  ;; splitter position as a percentage of height
          dragging?            (reagent/atom false)                        ;; is the user dragging the splitter (mouse is down)?
          over?                (reagent/atom false)                        ;; is the mouse over the splitter, if so, highlight it

          stop-drag            (fn []
                                 (when on-split-change (on-split-change @split-perc))
                                 (reset! dragging? false))

          calc-perc            (fn [mouse-y]                                                 ;; turn a mouse y coordinate into a percentage position
                                 (let [container  (get-element-by-id container-id)           ;; the outside container
                                       offsets    (sum-scroll-offsets container)             ;; take any scrolling into account
                                       c-height   (.-clientHeight container)                 ;; the container's height
                                       c-top-y    (.-offsetTop container)                    ;; the container's top Y
                                       relative-y (+ (- mouse-y c-top-y) (:top offsets))]    ;; the Y of the mouse, relative to container
                                   (if split-is-px?
                                     relative-y                                              ;; return the top offset in px
                                     (* 100.0 (/ relative-y c-height)))))                    ;; return the percentage panel-1 height against container width

          <html>?              #(= % (.-documentElement js/document))                        ;; test for the <html> element

          mouseout             (fn [event]
                                 (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                                   (stop-drag)))

          mousemove            (fn [event]
                                 (reset! split-perc (calc-perc (.-clientY event))))

          mousedown            (fn [event]
                                 (.preventDefault event)                                    ;; stop selection of text during drag
                                 (reset! dragging? true))

          mouseover-split      #(reset! over? true)
          mouseout-split       #(reset! over? false)

          make-container-attrs (fn [class style attr in-drag?]
                                 (merge {:class (str "rc-v-split display-flex " class)
                                         :id    container-id
                                         :style (merge (flex-child-style size)
                                                       (flex-flow-style "column nowrap")
                                                       {:margin margin
                                                        :width  width
                                                        :height height}
                                                       style)}
                                        (when in-drag?                             ;; only listen when we are dragging
                                          {:on-mouse-up   (handler-fn (stop-drag))
                                           :on-mouse-move (handler-fn (mousemove event))
                                           :on-mouse-out  (handler-fn (mouseout event))})
                                        (->attr args)
                                        attr))

          make-panel-attrs     (fn [class style attr in-drag? percentage]
                                 (merge
                                   {:class (str "display-flex " class)
                                    :style (merge (flex-child-style (if split-is-px?
                                                                      (if (pos? percentage)
                                                                        (str "0 0 " percentage "px") ;; flex for panel-1
                                                                        (str "1 1 0px"))             ;; flex for panel-2
                                                                      (str percentage " 1 0px")))
                                                  {:overflow "hidden"} ;; TODO: Shouldn't have this...test removing it
                                                  (when in-drag? {:pointer-events "none"})
                                                  style)}
                                   attr))

          make-splitter-attrs  (fn [class style attr]
                                 (merge
                                   {:class         (str "display-flex " class)
                                    :on-mouse-down (handler-fn (mousedown event))
                                    :on-mouse-over (handler-fn (mouseover-split))
                                    :on-mouse-out  (handler-fn (mouseout-split))
                                    :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                          {:cursor  "row-resize"}
                                                          (when @over? {:background-color "#f8f8f8"})
                                                          style)}
                                   attr))]

      (fn v-split-render
        [& {:keys [panel-1 panel-2 _size _width _height _on-split-change _initial-split _splitter-size _margin class style attr parts src]}]
        [:div (make-container-attrs class style attr @dragging?)
         [:div (make-panel-attrs
                 (str "rc-v-split-top " (get-in parts [:top :class]))
                 (get-in parts [:top :style])
                 (get-in parts [:top :attr])
                 @dragging?
                 @split-perc)
          panel-1]
         [:div (make-splitter-attrs
                 (str "rc-v-split-splitter " (get-in parts [:splitter :class]))
                 (get-in parts [:splitter :style])
                 (get-in parts [:splitter :attr]))
          [drag-handle :horizontal @over? parts]]
         [:div (make-panel-attrs
                 (str "rc-v-split-bottom " (get-in parts [:bottom :class]))
                 (get-in parts [:bottom :style])
                 (get-in parts [:bottom :attr])
                 @dragging?
                 (if split-is-px?
                   (- @split-perc) ;; Negative value indicates this is for panel-2
                   (- 100 @split-perc)))
          panel-2]]))))
