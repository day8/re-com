(ns re-com.layout
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.validate    :refer [extract-arg-data validate-args string-or-hiccup? number-or-string?]]
            [reagent.core :as    reagent]))


;; CSS
;;
;; html, body {
;;   height: 100%;
;;   margin: 0px;
;; }
;;
;; .my-page .v-layout-bottom { /* required for iframes to stretch to 100% width */
;;   flex-flow: inherit;
;; }


;; ------------------------------------------------------------------------------------
;;  Component: h-layout
;; ------------------------------------------------------------------------------------

(def h-layout-args-desc
  [{:name :left-panel    :required true                 :type "component"       :validate-fn string-or-hiccup? :description "markup to go in the left panel"}
   {:name :right-panel   :required true                 :type "component"       :validate-fn string-or-hiccup? :description "markup to go in the right panel"}
   {:name :initial-split :required false :default 50    :type "double | string" :validate-fn number-or-string? :description "initial split percentage of the left panel. Can be double value or string (with/without percentage sign)"}
   {:name :splitter-size :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the splitter"}
   {:name :margin        :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the margin around the panels"}])

(def h-layout-args (extract-arg-data h-layout-args-desc))

(defn h-layout
  "Returns markup for a horizontal layout component"
  [& {:keys [left-panel right-panel initial-split splitter-size margin]
      :or   {initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args h-layout-args args "h-layout")]}
  (let [container-id         (gensym "h-layout-")
        this                 (reagent/current-component)
        split-perc           (reagent/atom (js/parseInt initial-split)) ;; splitter position as a percentage of width
        dragging?            (reagent/atom false)                       ;; is the user dragging the splitter (mouse is down)?
        over?                (reagent/atom false)                       ;; is the mouse over the splitter, if so, highlight it

        stop-drag            #(reset! dragging? false)

        calc-perc            (fn [mouse-x]                                                 ;; turn a mouse y coordinate into a percentage position
                               (let [container  (.getElementById js/document container-id) ;; the outside container
                                     c-width   (.-clientWidth container)                   ;; the container's width
                                     c-left-x   (.-offsetLeft container)                   ;; the container's left X
                                     relative-x (- mouse-x c-left-x)]                      ;; the X of the mouse, relative to container
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
                               (merge {:class class
                                       :id container-id
                                       :style {:display "flex"
                                               :flex-flow "row nowrap"
                                               :flex "auto"
                                               :margin margin}}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up   (handler-fn (stop-drag))
                                         :on-mouse-move (handler-fn (mousemove event))
                                         :on-mouse-out  (handler-fn (mouseout event))})))

        make-panel-style (fn [class in-drag? percentage]
                               {:class class
                                :style (merge {:display "flex"
                                               :flex (str percentage " 1 0px")
                                               :overflow "hidden" ;; TODO: Shouldn't have this...test removing it
                                               }
                                              (when in-drag? {:pointer-events "none"}))})

        make-splitter-style  (fn [class]
                               {:class class
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style (merge {:flex (str "0 0 " splitter-size)
                                               :cursor "ew-resize"}
                                              (when @over? {:background-color "#eeeeee"}))})]

    (fn []
      [:div (make-container-style "rc-h-layout" @dragging?)
       [:div (make-panel-style "rc-h-layout-top" @dragging? @split-perc)
        [left-panel]]
       [:div (make-splitter-style "rc-h-layout-splitter")]
       [:div (make-panel-style "rc-h-layout-bottom" @dragging? (- 100 @split-perc))
        [right-panel]]])))


;; ------------------------------------------------------------------------------------
;;  Component: v-layout
;; ------------------------------------------------------------------------------------

(def v-layout-args-desc
  [{:name :top-panel     :required true                 :type "component"       :validate-fn string-or-hiccup? :description "markup to go in the top panel"}
   {:name :bottom-panel  :required true                 :type "component"       :validate-fn string-or-hiccup? :description "markup to go in the bottom panel"}
   {:name :initial-split :required false :default 50    :type "double | string" :validate-fn number-or-string? :description "initial split percentage of the top panel. Can be double value or string (with/without percentage sign)"}
   {:name :splitter-size :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the splitter"}
   {:name :margin        :required false :default "8px" :type "string"          :validate-fn string?           :description "thickness of the margin around the panels"}])

(def v-layout-args (extract-arg-data v-layout-args-desc))

(defn v-layout
  "Returns markup for a vertical layout component"
  [& {:keys [top-panel bottom-panel initial-split splitter-size margin]
      :or   {initial-split 50 splitter-size "8px" margin "8px"}
      :as   args}]
  {:pre [(validate-args v-layout-args args "v-layout")]}
  (let [container-id         (gensym "v-layout-")
        this                 (reagent/current-component)
        split-perc           (reagent/atom (js/parseInt initial-split))  ;; splitter position as a percentage of height
        dragging?            (reagent/atom false)                        ;; is the user dragging the splitter (mouse is down)?
        over?                (reagent/atom false)                        ;; is the mouse over the splitter, if so, highlight it

        stop-drag            #(reset! dragging? false)

        calc-perc            (fn [mouse-y]                                                 ;; turn a mouse y coordinate into a percentage position
                               (let [container  (.getElementById js/document container-id) ;; the outside container
                                     c-height   (.-clientHeight container)                 ;; the container's height
                                     c-top-y    (.-offsetTop container)                    ;; the container's top Y
                                     relative-y (- mouse-y c-top-y)]                       ;; the Y of the mouse, relative to container
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
                               (merge {:class class
                                       :id container-id
                                       :style {:display "flex"
                                               :flex-flow "column nowrap"
                                               :flex "auto"
                                               :margin margin}}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up   (handler-fn (stop-drag))
                                         :on-mouse-move (handler-fn (mousemove event))
                                         :on-mouse-out  (handler-fn (mouseout event))})))

        make-panel-style (fn [class in-drag? percentage]
                               {:class class
                                :style (merge {:display "flex"
                                               :flex (str percentage " 1 0px")
                                               :overflow "hidden" ;; TODO: Shouldn't have this...test removing it
                                               }
                                              (when in-drag? {:pointer-events "none"}))})

        make-splitter-style  (fn [class]
                               {:class class
                                :on-mouse-down (handler-fn (mousedown event))
                                :on-mouse-over (handler-fn (mouseover-split))
                                :on-mouse-out  (handler-fn (mouseout-split))
                                :style (merge {:flex (str "0 0 " splitter-size)
                                               :cursor "ns-resize"}
                                              (when @over? {:background-color "#eeeeee"}))})]

    (fn []
      [:div (make-container-style "re-v-layout" @dragging?)
       [:div (make-panel-style "re-v-layout-top" @dragging? @split-perc)
        [top-panel]]
       [:div (make-splitter-style "re-v-layout-splitter")]
       [:div (make-panel-style "re-v-layout-bottom" @dragging? (- 100 @split-perc))
        [bottom-panel]]])))
