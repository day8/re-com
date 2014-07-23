(ns reagent-components.h-layout
  (:require [reagent.core :as reagent]))


;; CSS
;;
;; html, body {
;;   height: 100%;
;;   margin: 0px;
;; }
;;
;; .my-page .h-layout-bottom { /* required for iframes to stretch to 100% width */
;;   flex-flow: inherit;
;; }


;; ----- Horizontal layout component (with splitter for resizing) -----
;; uses flex-box for positioning and sizing of children
;; see http://css-tricks.com/snippets/css/a-guide-to-flexbox/
;;

(defn h-layout [left-panel right-panel]
  (let [container-id (gensym "h-layout-")

        this        (reagent/current-component)

        margin      "8px"
        split-perc  (reagent/atom 50)                ;; splitter position as a percentage of width
        dragging?   (reagent/atom false)             ;; is the user dragging the splitter (mouse is down)?

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

        make-container-style (fn [in-drag?]
                               (merge {:id container-id
                                       :style {:display "flex"
                                               :flex-flow "row"
                                               ;; :height "100%"  ;; ?????????????
                                               :width "100%"
                                               :margin margin}}
                                      (when in-drag?                             ;; only listen when we are dragging
                                        {:on-mouse-up stop-drag
                                         :on-mouse-move mousemove
                                         :on-mouse-out mouseout})))

        make-panel-style (fn [in-drag? percentage]
                           {:style (merge {:display "flex"
                                           :flex-grow percentage
                                           :flex-shrink "1"
                                           :flex-basis "0"
                                           :overflow "hidden"}
                                          (when in-drag? {:pointer-events "none"}))})

        make-splitter-style (fn []
                              {:on-mouse-down mousedown
                               :style {:height margin
                                       :cursor "ew-resize"}})]

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
