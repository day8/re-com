(ns reagent-components.v-layout
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


;; ----- Vertical layout component (with splitter for resizing) -----
;; uses flex-box for positioning and sizing of children
;; see http://css-tricks.com/snippets/css/a-guide-to-flexbox/
;;

(defn create [top-panel bottom-panel]
  (let [container-id (gensym "v-layout-")

        this        (reagent/current-component)

        margin      "8px"
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
                                               :flex-flow "column"
                                               :height "100%"
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
