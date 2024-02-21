(ns re-com.splits
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.util     :refer [get-element-by-id sum-scroll-offsets merge-css flatten-attr]]
   [re-com.box      :refer [flex-child-style flex-flow-style]]
   [re-com.validate :refer [string-or-hiccup? number-or-string? html-attr? css-style? parts?] :refer-macros [validate-args-macro]]
   [reagent.core    :as    reagent]))

(declare hv-split-css-spec)

(defn drag-handle
  "Return a drag handle to go into a vertical or horizontal splitter bar:
    orientation: Can be :horizontal or :vertical
    over?:       When true, the mouse is assumed to be over the splitter so show a bolder color"
  [orientation over? parts]
  (let [vertical? (= orientation :vertical)
        cmerger (merge-css hv-split-css-spec {:parts parts})]
    [:div
     (flatten-attr (cmerger :handle {:vertical? vertical?}))
     [:div
      (flatten-attr (cmerger :handle-bar-1 {:vertical? vertical? :over? over?}))]
     [:div
      (flatten-attr (cmerger :handle-bar-2 {:vertical? vertical? :over? over?}))]]))

(defn calculate-split-flex-style [value is-px?]
  (if is-px?
    (if (pos? value)
      (str "0 0 " value "px") ;; flex for panel-1
      (str "1 1 0px")) ;; flex for panel-2
    (str value " 1 0px")))

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

(def hv-split-css-spec
  {:main {:class (fn [{:keys [vertical?]}]
                   [(if vertical? "rc-v-split" "rc-h-split") "display-flex"])
          :style (fn [{:keys [size margin width height vertical?]}]
                   (merge (flex-child-style size)
                          (flex-flow-style (str (if vertical? "column" "row") " nowrap"))
                          {:margin margin
                           :width width
                           :height height}))}
   :splitter {:class (fn [{:keys [vertical?]}]
                       ["display-flex" (if vertical? "rc-v-split-splitter" "rc-h-split-splitter")])
              :style (fn [{:keys [vertical? size over?]}]
                       (merge (flex-child-style (str "0 0 " size))
                              {:cursor (if vertical? "row-resize" "col-resize")}
                              (when over? {:background-color "#f8f8f8"})))}
   :handle {:class (fn [{:keys [vertical?]}]
                     [(if vertical? "rc-v-split-handle" "rc-h-split-handle") "display-flex"])
            :style (fn [{:keys [vertical?]}]
                     (let [[width height] (if vertical? ["8px" "20px"] ["20px" "8px"])]
                       (merge
                        (flex-flow-style (str (if vertical? "row" "column") " nowrap"))
                        {:width width :height height :margin "auto"})))}
   :handle-bar-1 {:class (fn [{:keys [vertical?]}]
                           [(if vertical? "rc-v-split-handle-bar-1" "rc-h-split-handle-bar-1")])
                  :style (fn [{:keys [vertical? over?]}]
                           (let [border (str "solid 1px " (if over? "#999" "#ccc"))]
                             (if vertical?
                               {:width "3px" :height "20px" :border-right border}
                               {:width "20px" :height "3px" :border-bottom border})))}
   :handle-bar-2 {:class (fn [{:keys [vertical?]}]
                           [(if vertical? "rc-v-split-handle-bar-2" "rc-h-split-handle-bar-2")])
                  :style (fn [{:keys [vertical? over?]}]
                           (let [border (str "solid 1px " (if over? "#999" "#ccc"))]
                             (if vertical?
                               {:width "3px" :height "20px" :border-right border}
                               {:width "20px" :height "3px" :border-bottom border})))}
   ;; Leaving rc-h-split-top class (below) for backwards compatibility only.
   :left {:class ["rc-h-split-left" "rc-h-split-top" "display-flex"]
          :style (fn [{:keys [flex dragging?]}]
                   (merge (flex-child-style flex)
                          (when dragging? {:pointer-events "none"})))}
   ;; Leaving rc-h-split-bottom class (below) for backwards compatibility only.
   :right {:class ["rc-h-split-right" "rc-h-split-bottom" "display-flex"]
           :style (fn [{:keys [flex dragging?]}]
                    (merge (flex-child-style flex)
                           (when dragging? {:pointer-events "none"})))}
   :top {:class ["rc-v-split-top" "display-flex"]
         :style (fn [{:keys [flex dragging?]}]
                  (merge (flex-child-style flex)
                         (when dragging? {:pointer-events "none"})))}
   :bottom {:class ["rc-v-split-bottom" "display-flex"]
            :style (fn [{:keys [flex dragging?]}]
                     (merge (flex-child-style flex)
                            (when dragging? {:pointer-events "none"})))}})

(def hv-split-parts
  (when include-args-desc?
    (-> (map :name hv-split-parts-desc) set)))

(def hv-split-args-desc
  (when include-args-desc?
    [{:name :panel-1         :required true                  :type "hiccup"          :validate-fn string-or-hiccup?       :description "markup to go in the left (or top) panel"}
     {:name :panel-2         :required true                  :type "hiccup"          :validate-fn string-or-hiccup?       :description "markup to go in the right (or bottom) panel"}
     {:name :size            :required false :default "auto" :type "string"          :validate-fn string?                 :description [:span "applied to the outer container of the two panels. Equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"] ", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
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

         calc-perc            (fn [mouse-x]                                                                 ;; turn a mouse x coordinate into a percentage position
                                (let [container  (get-element-by-id container-id)                           ;; the outside container
                                      c-width    (.-clientWidth container)                                  ;; the container's width
                                      c-left-x   (+ (.-pageXOffset js/window)
                                                    (-> container .getBoundingClientRect .-left))           ;; the container's left X
                                      relative-x (- mouse-x c-left-x)]                                      ;; the X of the mouse, relative to container
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
         mouseout-split       #(reset! over? false)]

     (fn h-split-render
       [& {:keys [panel-1 panel-2 _size _width _height _on-split-change _initial-split _splitter-size _margin class style attr parts src]}]
       (let [cmerger (merge-css hv-split-css-spec args)]
         [:div
          (flatten-attr
           (cmerger :main {:vertical? false :size size :margin margin :width width :height height
                           :attr (merge
                                  {:id container-id}
                                  (when @dragging?  ;; only listen when we are dragging
                                    {:on-mouse-up   (handler-fn (stop-drag))
                                     :on-mouse-move (handler-fn (mousemove event))
                                     :on-mouse-out  (handler-fn (mouseout event))}))}))
          [:div
           (flatten-attr
            (cmerger :left {:dragging? @dragging?
                            :flex (calculate-split-flex-style @split-perc split-is-px?)}))
           panel-1]
          [:div
           (flatten-attr
            (cmerger :splitter {:vertical? false :size splitter-size :over? @over?
                                :attr {:on-mouse-down (handler-fn (mousedown event))
                                       :on-mouse-over (handler-fn (mouseover-split))
                                       :on-mouse-out  (handler-fn (mouseout-split))}}))
           [drag-handle :vertical @over? parts]]
          [:div
           (flatten-attr
            (cmerger :right {:dragging? @dragging?
                             :flex (calculate-split-flex-style (if split-is-px?
                                                                 (- @split-perc) ;; Negative value indicates this is for panel-2
                                                                 (- 100 @split-perc))
                                                               split-is-px?)}))
           panel-2]])))))

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

         calc-perc            (fn [mouse-y]                                                                ;; turn a mouse y coordinate into a percentage position
                                (let [container  (get-element-by-id container-id)                          ;; the outside container
                                      c-height   (.-clientHeight container)                                ;; the container's height
                                      c-top-y    (+ (.-pageYOffset js/window)
                                                    (-> container .getBoundingClientRect .-top))           ;; the container's top Y
                                      relative-y (- mouse-y c-top-y)]                                      ;; the Y of the mouse, relative to container
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
         mouseout-split       #(reset! over? false)]

     (fn v-split-render
       [& {:keys [panel-1 panel-2 _size _width _height _on-split-change _initial-split _splitter-size _margin class style attr parts src]}]

       (let [cmerger (merge-css hv-split-css-spec args)]
         [:div
          (flatten-attr
           (cmerger :main {:vertical? true :size size :margin margin :width width :height height
                           :attr (merge
                                  {:id container-id}
                                  (when @dragging?  ;; only listen when we are dragging
                                    {:on-mouse-up   (handler-fn (stop-drag))
                                     :on-mouse-move (handler-fn (mousemove event))
                                     :on-mouse-out  (handler-fn (mouseout event))}))}))
          [:div
           (flatten-attr
            (cmerger :top {:dragging? @dragging?
                           :flex (calculate-split-flex-style @split-perc split-is-px?)}))
           panel-1]
          [:div
           (flatten-attr
            (cmerger :splitter {:vertical? true :size splitter-size :over? @over?
                                :attr {:on-mouse-down (handler-fn (mousedown event))
                                       :on-mouse-over (handler-fn (mouseover-split))
                                       :on-mouse-out  (handler-fn (mouseout-split))}}))
           [drag-handle :horizontal @over? parts]]
          [:div
           (flatten-attr
            (cmerger :bottom {:dragging? @dragging?
                              :flex (calculate-split-flex-style (if split-is-px?
                                                                  (- @split-perc) ;; Negative value indicates this is for panel-2
                                                                  (- 100 @split-perc))
                                                                split-is-px?)}))
           panel-2]])))))
