(ns re-demo.v-table-renderers
  (:require [re-com.core   :refer [h-box gap v-box box v-table p label]]
            [re-com.util    :refer [px]]
            [reagent.core  :as reagent]))



(defn render-two-lines
  [{:keys [name section background height width grid-colour]}]
  [v-box
   :height (px  height)
   :width   (if width (px width) "1 0 auto")
   :style  {:color "white" :background-color background :padding "3px" :border "solid white 1px"}
   :align  :center
   :justify :center
   :children [[label :label section :style {:font-size 10 :font-weight "bold"}]
              [label :label name :style {:font-size 11 :font-weight "bold"}]]])


(defn table-showing-renderers
  []
  (let [light-blue                "#d860a0"
        medium-blue               "#60A0D8"
        blue                      "#60A0D8"
        gold                      "#d89860"
        green                     "#60d898"


        fib-ratio          0.618             ;; fibonachi ratios to make the visuals look pretty
        unit-50            50
        unit-81           (js/Math.round (/ unit-50 fib-ratio))
        unit-121          (js/Math.round (/ unit-50 fib-ratio fib-ratio))
        unit-31           (js/Math.round (* unit-50 fib-ratio))
        num-rows           5
        row-height         unit-31
        total-row-height   (* num-rows row-height)

        width-of-main-row-content (js/Math.round (/ total-row-height fib-ratio))
        dummy-rows                (reagent/atom (mapv #(hash-map :id %1) (range num-rows)))] ;; TODO: Changed to atom for testing validation-fn, can change back when we successfully allow atom OR value
    (fn []
      [v-box
       :children [[v-table
                   :model              dummy-rows
                   :row-height         row-height
                   :row-content-width  width-of-main-row-content

                   :max-row-viewport-height  (- total-row-height row-height)    ;; force a vertical scrollbar


                   :row-header-renderer    (fn [row-index, row] [render-two-lines {:name ":row-header-renderer " :section "" :background green :height unit-31 :width unit-121}])
                   :row-footer-renderer    (fn [row-index, row] [render-two-lines {:name ":row-footer-renderer" :section "" :background green :height unit-31 :width unit-121}])

        ;; column header - section 4
                   :column-header-height   unit-50
                   :column-header-renderer (fn [] [render-two-lines {:name ":column-header-renderer" :section "" :background gold :height unit-50 :width width-of-main-row-content}])

        ;; column footer - section 5
                   :column-footer-height   unit-50
                   :column-footer-renderer (fn [] [render-two-lines {:name ":column-footer-renderer" :section "" :background "#d8d460" :height unit-50 :width width-of-main-row-content}])

        ;; corners
                   :top-left-renderer     (fn [] [render-two-lines {:name ":top-left-renderer"     :section "" :background blue :height unit-50 :width unit-121}])
                   :bottom-left-renderer  (fn [] [render-two-lines {:name ":bottom-left-renderer"  :section "" :background blue :height unit-50 :width unit-121}])
                   :bottom-right-renderer (fn [] [render-two-lines {:name ":bottom-right-renderer" :section "" :background blue :height unit-50 :width unit-121}])
                   :top-right-renderer    (fn [] [render-two-lines {:name ":top-right-renderer"    :section "" :background blue :height unit-50 :width unit-121}])

                   :row-renderer          (fn [row_index, row] [render-two-lines {:name ":row-renderer" :section "" :background light-blue :height row-height :width width-of-main-row-content :grid-colour "grey"}])]

                  [gap :size "0"]
                  [v-box :children []]]])))


;; MT's Notes: 
;; 
;; With the :row-renderer above, i had to passin the height as "width-of-main-row-content"   ??????
;; On section width:
;;   - the width of left sections 1,2,3 is determined by the widest hiccup returned by the 3 renderers for these sections. 
;;   - the width of center  sections 4,5,6 is determined by `:row-content-width`
;;   - the width of left sections 7,8,9 is determined by by the widest hiccup returned by the 3 renderers for these sections.
;; 
;; the viewport width for 4,5,6 is determined by the widest hiccup returned by renderers.  Once I put in an `h-box` it expanded out. When i only had `div` the viewport collapsed to the size of the content.
;; puzzled about column headings XXX
;; 
;; For `:row-viewport-width` the docs say if not specified will take up all available space but this is not 
;; correct. 
;; 
;; I have to provide `:column-header-height`. Could the height of top sections 1, 4, 7 should provide the height. 
;; 
;; Mention in docs that you are likely to use h-box and v-box in renderers.
;; 
;; Discuss with Gregg and Isaac:
;;   - the idea of variable row heights. 
;;   - performance: we have to reduce the amount of inline styles
;;   - How do I create "CSS classes" in a namespace


