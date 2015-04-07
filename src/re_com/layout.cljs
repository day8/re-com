(ns re-com.layout
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util        :refer [get-element-by-id sum-scroll-offsets]]
            [re-com.box         :refer [flex-child-style flex-flow-style]]
            [re-com.validate    :refer [extract-arg-data string-or-hiccup? number-or-string?] :refer-macros [validate-args-macro]]
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
;;  Component: h-layout
;; ------------------------------------------------------------------------------------

(def h-layout-args-desc
  [{:name :panel-1       :required true                 :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the left panel"}
   {:name :panel-2       :required true                 :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the right panel"}
   {:name :initial-split :required false :default 50    :type "double | string" :validate-fn number-or-string? :description "initial split percentage of the left panel. Can be double value or string (with/without percentage sign)"}
   {:name :splitter-size :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the splitter"}
   {:name :margin        :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the margin around the panels"}])

;(def h-layout-args (extract-arg-data h-layout-args-desc))

(defn h-layout
  "Returns markup for a horizontal layout component"
  [& {:keys [panel-1 panel-2 initial-split splitter-size margin]
      :or   {initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args-macro h-layout-args-desc args "h-layout")]}
  (let [container-id         (gensym "h-layout-")
        split-perc           (reagent/atom (js/parseInt initial-split)) ;; splitter position as a percentage of width
        dragging?            (reagent/atom false)                       ;; is the user dragging the splitter (mouse is down)?
        over?                (reagent/atom false)                       ;; is the mouse over the splitter, if so, highlight it

        stop-drag            #(reset! dragging? false)

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

        make-container-style (fn [class in-drag?]
                               (merge {:class (str "display-flex " class)
                                       :id    container-id
                                       :style (merge (flex-child-style "auto")
                                                     (flex-flow-style "row nowrap")
                                                     {:margin margin})}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up   (handler-fn (stop-drag))
                                         :on-mouse-move (handler-fn (mousemove event))
                                         :on-mouse-out  (handler-fn (mouseout event))})))

        make-panel-style (fn [class in-drag? percentage]
                           {:class (str "display-flex " class)
                            :style (merge (flex-child-style (str percentage " 1 0px"))
                                          {:overflow "hidden" ;; TODO: Shouldn't have this...test removing it
                                           }
                                          (when in-drag? {:pointer-events "none"}))})

        make-splitter-style  (fn [class]
                               {:class         (str "display-flex " class)
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                      {:cursor "col-resize"}
                                                      (when @over? {:background-color "#f8f8f8"}))})]

    (fn []
      [:div (make-container-style "rc-h-layout" @dragging?)
       [:div (make-panel-style "rc-h-layout-top" @dragging? @split-perc)
        panel-1]
       [:div (make-splitter-style "rc-h-layout-splitter")
        [drag-handle :vertical @over?]]
       [:div (make-panel-style "rc-h-layout-bottom" @dragging? (- 100 @split-perc))
        panel-2]])))


;; ------------------------------------------------------------------------------------
;;  Component: v-layout
;; ------------------------------------------------------------------------------------

(def v-layout-args-desc
  [{:name :panel-1       :required true                 :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the top panel"}
   {:name :panel-2       :required true                 :type "hiccup"          :validate-fn string-or-hiccup? :description "markup to go in the bottom panel"}
   {:name :initial-split :required false :default 50    :type "double | string" :validate-fn number-or-string? :description "initial split percentage of the top panel. Can be double value or string (with/without percentage sign)"}
   {:name :splitter-size :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the splitter"}
   {:name :margin        :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the margin around the panels"}])

;(def v-layout-args (extract-arg-data v-layout-args-desc))

(defn v-layout
  "Returns markup for a vertical layout component"
  [& {:keys [panel-1 panel-2 initial-split splitter-size margin]
      :or   {initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args-macro v-layout-args-desc args "v-layout")]}
  (let [container-id         (gensym "v-layout-")
        split-perc           (reagent/atom (js/parseInt initial-split))  ;; splitter position as a percentage of height
        dragging?            (reagent/atom false)                        ;; is the user dragging the splitter (mouse is down)?
        over?                (reagent/atom false)                        ;; is the mouse over the splitter, if so, highlight it

        stop-drag            #(reset! dragging? false)

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

        make-container-style (fn [class in-drag?]
                               (merge {:class (str "display-flex " class)
                                       :id    container-id
                                       :style (merge (flex-child-style "auto")
                                                     (flex-flow-style "column nowrap")
                                                     {:margin margin})}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up   (handler-fn (stop-drag))
                                         :on-mouse-move (handler-fn (mousemove event))
                                         :on-mouse-out  (handler-fn (mouseout event))})))

        make-panel-style (fn [class in-drag? percentage]
                           {:class (str "display-flex " class)
                            :style (merge (flex-child-style (str percentage " 1 0px"))
                                          {:overflow "hidden" ;; TODO: Shouldn't have this...test removing it
                                           }
                                          (when in-drag? {:pointer-events "none"}))})

        make-splitter-style  (fn [class]
                               {:class         (str "display-flex " class)
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style         (merge (flex-child-style (str "0 0 " splitter-size))
                                                      {:cursor  "row-resize"}
                                                      (when @over? {:background-color "#f8f8f8"}))})]

    (fn []
      [:div (make-container-style "re-v-layout" @dragging?)
       [:div (make-panel-style "re-v-layout-top" @dragging? @split-perc)
        panel-1]
       [:div (make-splitter-style "re-v-layout-splitter")
        [drag-handle :horizontal @over?]]
       [:div (make-panel-style "re-v-layout-bottom" @dragging? (- 100 @split-perc))
        panel-2]])))
