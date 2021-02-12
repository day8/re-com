(ns re-demo.v-table-renderers
  (:require [re-com.core   :refer [v-box v-table label]]
            [re-com.util    :refer [px]]
            [reagent.core  :as reagent]))


(defn box-with-border
  [{:keys [name background height width]}]
  [v-box
   :height (px  height)
   :width   (if width (px width) "1 0 auto")
   :style  {:color "white" :background-color background :padding "3px" :border "solid white 1px"}
   :align  :center
   :justify :center
   :children [[label :label name :style {:font-size 11 :font-weight "bold"}]]])


(defn table-showing-renderers
  []
  (let [light-blue        "#d860a0"
        blue              "#60A0D8"
        gold              "#d89860"
        green             "#60d898"

        fib-ratio         0.618             ;; fibonacci ratios to make the visuals look pretty
        unit-50           50                ;; base for fibonacci calulations
        unit-121          (js/Math.round (/ unit-50 fib-ratio fib-ratio))
        unit-31           (js/Math.round (* unit-50 fib-ratio))

        num-rows          5
        row-height        unit-31
        total-row-height  (* num-rows row-height)

        width-of-main-row-content (js/Math.round (/ total-row-height fib-ratio))
        dummy-rows                (reagent/atom (mapv #(hash-map :id %1) (range num-rows)))]
    (fn []
      [v-table
       :model                   dummy-rows

       ;; ===== Column header/footer (section 4,6)
       :column-header-renderer  (fn [] [box-with-border {:name ":column-header-renderer" :background gold :height unit-50 :width width-of-main-row-content}])
       :column-header-height    unit-50
       :column-footer-renderer  (fn [] [box-with-border {:name ":column-footer-renderer" :background "#d8d460" :height unit-50 :width width-of-main-row-content}])
       :column-footer-height    unit-50

       ;; ===== Row header/footer (sections 2,8)
       :row-header-renderer     (fn [_row-index, _row] [box-with-border {:name ":row-header-renderer " :background green :height unit-31 :width unit-121}])
       :row-footer-renderer     (fn [_row-index, _row] [box-with-border {:name ":row-footer-renderer"  :background green :height unit-31 :width unit-121}])

       ;; ===== Rows (section 5)
       :row-renderer            (fn [_row_index, _row] [box-with-border {:name ":row-renderer" :background light-blue :height row-height :width width-of-main-row-content}])
       :row-height              row-height
       :row-content-width       width-of-main-row-content
       :max-row-viewport-height (- total-row-height row-height)    ;; force a vertical scrollbar

       ;; ===== Corners (sections 1,3,7,9)
       :top-left-renderer       (fn [] [box-with-border {:name ":top-left-renderer"     :background blue  :height unit-50 :width unit-121}])
       :bottom-left-renderer    (fn [] [box-with-border {:name ":bottom-left-renderer"  :background blue  :height unit-50 :width unit-121}])
       :top-right-renderer      (fn [] [box-with-border {:name ":top-right-renderer"    :background blue  :height unit-50 :width unit-121}])
       :bottom-right-renderer   (fn [] [box-with-border {:name ":bottom-right-renderer" :background blue  :height unit-50 :width unit-121}])])))


;; MT's Notes: 
;; 
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


