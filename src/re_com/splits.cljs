(ns re-com.splits
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util        :refer [get-element-by-id sum-scroll-offsets]]
            [re-com.box         :refer [flex-child-style flex-flow-style]]
            [re-com.validate    :refer [string-or-hiccup? number-or-string? html-attr? css-style?] :refer-macros [validate-args-macro]]
            [reagent.core       :as    reagent]))


(defn drag-handle
  "Return a drag handle to go into a vertical or horizontal splitter bar:
    orientation: Can be :horizonal or :vertical
    over?:       When true, the mouse is assumed to be over the splitter so show a bolder color"
  [orientation over?]
  (let [vertical? (= orientation :vertical)
        length    "20px"
        width     "8px"
        pos1      "3px"
        pos2      "3px"
        color     (if over? "#999" "#ccc")
        border    (str "solid 1px " color)
        flex-flow (str (if vertical? "row" "column") " nowrap")]
    [:div {:class "display-flex"
           :style (merge (flex-flow-style flex-flow)
                         {:width  (if vertical? width length)
                          :height (if vertical? length width)
                          :margin "auto"})}
     [:div {:style (if vertical?
                     {:width pos1   :height length :border-right  border}
                     {:width length :height pos1   :border-bottom border})}]
     [:div {:style (if vertical?
                     {:width pos2   :height length :border-right  border}
                     {:width length :height pos2   :border-bottom border})}]]))


;; ------------------------------------------------------------------------------------
;;  Component: h-split
;; ------------------------------------------------------------------------------------

(def hv-split-args-desc
  [{:name :panel-1         :required true                  :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the left (or top) panel"}
   {:name :panel-2         :required true                  :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the right (or bottom) panel"}
   {:name :size            :required false :default "auto" :type "string"          :validate-fn string?           :description [:span "applied to the outer container of the two panels. Equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :width           :required false                 :type "string"          :validate-fn string?           :description "width of the outer container of the two panels. A CSS width style"}
   {:name :height          :required false                 :type "string"          :validate-fn string?           :description "height of the outer container of the two panels. A CSS height style"}
   {:name :on-split-change :required false                 :type "double -> nil"   :validate-fn fn?               :description [:span "called when the user moves the splitter bar (on mouse up, not on each mouse move). Given the new " [:code ":panel-1"] " percentage split"]}
   {:name :initial-split   :required false :default 50     :type "double | string" :validate-fn number-or-string? :description [:span "initial split percentage for " [:code ":panel-1"] ". Can be double value or string (with/without percentage sign)"]}
   {:name :splitter-size   :required false :default "8px"  :type "string"          :validate-fn string?           :description "thickness of the splitter"}
   {:name :margin          :required false :default "8px"  :type "string"          :validate-fn string?           :description "thickness of the margin around the panels"}
   {:name :class           :required false                 :type "string"          :validate-fn string?           :description "CSS class names, space separated, applied to outer container"}
   {:name :style           :required false                 :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override, applied to outer container"}
   {:name :attr            :required false                 :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed, applied to outer container"]}])

(defn h-split
  "Returns markup for a horizontal layout component"
  [& {:keys [panel-1 panel-2 size width height on-split-change initial-split splitter-size margin class style attr]
      :or   {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args-macro hv-split-args-desc args "h-split")]}
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
                                 (* 100.0 (/ relative-x c-width))))                        ;; do the percentage calculation

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
                                      attr))

        make-panel-attrs     (fn [class in-drag? percentage]
                               {:class (str "display-flex " class)
                                :style (merge (flex-child-style (str percentage " 1 0px"))
                                              {:overflow "hidden"} ;; TODO: Shouldn't have this...test removing it
                                              (when in-drag? {:pointer-events "none"}))})

        make-splitter-attrs  (fn [class]
                               {:class         (str "display-flex " class)
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                      {:cursor "col-resize"}
                                                      (when @over? {:background-color "#f8f8f8"}))})]

    (fn []
      [:div (make-container-attrs class style attr @dragging?)
       [:div (make-panel-attrs "rc-h-split-top" @dragging? @split-perc)
        panel-1]
       [:div (make-splitter-attrs "rc-h-split-splitter")
        [drag-handle :vertical @over?]]
       [:div (make-panel-attrs "rc-h-split-bottom" @dragging? (- 100 @split-perc))
        panel-2]])))


;; ------------------------------------------------------------------------------------
;;  Component: v-split
;; ------------------------------------------------------------------------------------

(defn v-split
  "Returns markup for a vertical layout component"
  [& {:keys [panel-1 panel-2 size width height on-split-change initial-split splitter-size margin class style attr]
      :or   {size "auto" initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args-macro hv-split-args-desc args "v-split")]}
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
                                 (* 100.0 (/ relative-y c-height))))                       ;; do the percentage calculation

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
                                      attr))

        make-panel-attrs     (fn [class in-drag? percentage]
                               {:class (str "display-flex " class)
                                :style (merge (flex-child-style (str percentage " 1 0px"))
                                              {:overflow "hidden"} ;; TODO: Shouldn't have this...test removing it
                                              (when in-drag? {:pointer-events "none"}))})

        make-splitter-attrs  (fn [class]
                               {:class         (str "display-flex " class)
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                      {:cursor  "row-resize"}
                                                      (when @over? {:background-color "#f8f8f8"}))})]

    (fn []
      [:div (make-container-attrs class style attr @dragging?)
       [:div (make-panel-attrs "re-v-split-top" @dragging? @split-perc)
        panel-1]
       [:div (make-splitter-attrs "re-v-split-splitter")
        [drag-handle :horizontal @over?]]
       [:div (make-panel-attrs "re-v-split-bottom" @dragging? (- 100 @split-perc))
        panel-2]])))
