(ns re-com.layout
  (:require [reagent.core :as reagent]))


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
;;  h-layout
;; ------------------------------------------------------------------------------------

(defn h-layout
  [& {:keys [left-panel right-panel splitter-size margin]
      :or {splitter-size "8px" margin "8px"}}]
  (let [container-id (gensym "h-layout-")

        this        (reagent/current-component)

        split-perc  (reagent/atom 50)                ;; splitter position as a percentage of width
        dragging?   (reagent/atom false)             ;; is the user dragging the splitter (mouse is down)?
        over?       (reagent/atom false)             ;; is the mouse over the splitter, if so, highlight it

        stop-drag   #(reset! dragging? false)

        calc-perc  (fn [mouse-x]                                                 ;; turn a mouse y coordinate into a percentage position
                     (let [container  (.getElementById js/document container-id) ;; the outside container
                           c-width   (.-clientWidth container)                   ;; the container's width
                           c-left-x   (.-offsetLeft container)                   ;; the container's left X
                           relative-x (- mouse-x c-left-x)]                      ;; the X of the mouse, relative to container
                       (* 100.0 (/ relative-x c-width))))                        ;; do the percentage calculation

        <html>?    #(= % (.-documentElement js/document))                        ;; test for the <html> element

        mouseout   (fn [event]
                     (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                       (stop-drag)))

        mousemove  (fn [event]
                     (reset! split-perc (calc-perc (.-clientX event))))

        mousedown  (fn [event]
                     (.preventDefault event)                                    ;; stop selection of text during drag
                     (reset! dragging? true))

        mouseover-split #(reset! over? true)
        mouseout-split  #(reset! over? false)

        make-container-style (fn [in-drag?]
                               (merge {:id container-id
                                       :style {:display "flex"
                                               :flex-flow "row nowrap"
                                               :height "100%"
                                               :margin margin}}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up stop-drag
                                         :on-mouse-move mousemove
                                         :on-mouse-out mouseout})))

        make-panel-style (fn [in-drag? percentage]
                           {:style (merge {:display "flex"
                                           :flex (str percentage " 1 0px")
                                           :overflow "hidden"}
                                          (when in-drag? {:pointer-events "none"}))})

        make-splitter-style (fn []
                              {:on-mouse-down mousedown
                               :on-mouse-over mouseover-split
                               :on-mouse-out  mouseout-split
                               :style (merge {:flex (str "0 0 " splitter-size)
                                              :cursor "ew-resize"}
                                             (when @over? {:background-color "#eeeeee"}))})]

    (fn []
      [:div.h-layout-container
       (make-container-style @dragging?)
       [:div.h-layout-top
        (make-panel-style @dragging? @split-perc)
        [left-panel]]
       [:div.h-layout-splitter (make-splitter-style)]
       [:div.h-layout-bottom
        (make-panel-style @dragging? (- 100 @split-perc))
        [right-panel]]])))


;; ------------------------------------------------------------------------------------
;;  v-layout
;; ------------------------------------------------------------------------------------

(defn v-layout
  [& {:keys [top-panel bottom-panel splitter-size margin]
      :or {splitter-size "8px" margin "8px"}}]
  (let [container-id (gensym "v-layout-")

        this        (reagent/current-component)

        split-perc  (reagent/atom 50)                ;; splitter position as a percentage of height
        dragging?   (reagent/atom false)             ;; is the user dragging the splitter (mouse is down)?

        stop-drag   #(reset! dragging? false)

        calc-perc  (fn [mouse-y]                                                 ;; turn a mouse y coordinate into a percentage position
                     (let [container  (.getElementById js/document container-id) ;; the outside container
                           c-height   (.-clientHeight container)                 ;; the container's height
                           c-top-y    (.-offsetTop container)                    ;; the container's top Y
                           relative-y (- mouse-y c-top-y)]                       ;; the Y of the mouse, relative to container
                       (* 100.0 (/ relative-y c-height))))                       ;; do the percentage calculation

        <html>?    #(= % (.-documentElement js/document))                        ;; test for the <html> element

        mouseout   (fn [event]
                     (if (<html>? (.-relatedTarget event))                       ;; stop drag if we leave the <html> element
                       (stop-drag)))

        mousemove  (fn [event]
                     (reset! split-perc (calc-perc (.-clientY event))))

        mousedown  (fn [event]
                     (.preventDefault event)                                    ;; stop selection of text during drag
                     (reset! dragging? true))

        make-container-style (fn [in-drag?]
                               (merge {:id container-id
                                       :style {:display "flex"
                                               :flex-flow "column nowrap"
                                               :height "100%"
                                               :margin margin}}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up stop-drag
                                         :on-mouse-move mousemove
                                         :on-mouse-out mouseout})))

        make-panel-style (fn [in-drag? percentage]
                           {:style (merge {:display "flex"
                                           :flex (str percentage " 1 0px")
                                           :overflow "hidden"}
                                          (when in-drag? {:pointer-events "none"}))})

        make-splitter-style (fn []
                              {:on-mouse-down mousedown
                               :style {:flex (str "0 0 " splitter-size)
                                       :cursor "ns-resize"}})]

    (fn []
      [:div.v-layout-container
       (make-container-style @dragging?)
       [:div.v-layout-top
        (make-panel-style @dragging? @split-perc)
        [top-panel]]
       [:div.v-layout-splitter (make-splitter-style)]
       [:div.v-layout-bottom
        (make-panel-style @dragging? (- 100 @split-perc))
        [bottom-panel]]])))
