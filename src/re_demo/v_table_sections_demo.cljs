(ns re-demo.v-table-sections-demo
  (:require [re-com.core    :refer [h-box gap v-box box v-table hyperlink-href p label]]
            [re-demo.utils  :refer [panel-title title2 title3 args-table github-hyperlink status-text]]))



(defn render-two-lines
  [name section background]
  [v-box
   :size  "1 0 auto"
   :style {:color "white" :background-color background} 
   :align :center 
   :children [[label :label name] 
              [label :label section :style {:font-size 11}]]])


(defn sections-demo 
  []
  (let [light-blue "#DBEFF9"
        medium-blue "#5B9BD5"
        blue "#0F6FC6"
        num_rows 6
        dummy-rows (reduce  #(conj %1 {:id %2}) [] (range num_rows))
        row-height 20
        width-of-main-row-content 250
        header-footer-style  {:style {:color "white" :background-color medium-blue}}]
    (fn []
    [v-box
     :gap      "10px"
     :children [[title2 "Sections Demo"]
                [:p {:style {:width "450px"}} "There are nine sections in a v-table, of which only #5 is mandatory. This table has 6 rows of data. "]

                [v-table
                 :model              dummy-rows
                 :row-height         row-height
                 :row-content-width  width-of-main-row-content

               ;; :remove-empty-row-space? false

               ;; section 2
                 :row-header-renderer    (fn [row-index] [:div header-footer-style (str row-index (when (= row-index 2) "   row header") (when (= row-index 3) "   (section 2)"))])
                 :row-footer-renderer    (fn [row-index] [:div header-footer-style (str "row footer: " row-index)])

               ;; column header - section 4
                 :column-header-height   (* 2 row-height)
                 :column-header-renderer (fn [] [render-two-lines "column headers" "(section 4)" medium-blue])

               ;; column footer - section 5
                 :column-footer-height   (* 2 row-height)
                 :column-footer-renderer (fn [] [render-two-lines "column footers" "(section 6)" medium-blue])

               ;; corners 
                 :top-left-renderer     (fn [] [render-two-lines "top left"     "(section 1)" blue])
                 :bottom-left-renderer  (fn [] [render-two-lines "bottom left"  "(section 3)" blue])
                 :bottom-right-renderer (fn [] [render-two-lines "bottom right" "(section 9)" blue])
                 :top-right-renderer    (fn [] [render-two-lines "top right"    "(section 7)" blue])

                 :row-renderer           (fn [row-index] [:div  {:style {:flex "auto" :background-color light-blue}} (str  row-index)])]]])))


;; MT's Notes: 
;; 
;; If we put in a few demos, we should probably split them off to other namespaces, otherwise this one might get a bit complex
;; 
;; There is a lot of good information across in the v-table docstring which should be transfered into parameter docs. Because of text volume, perhaps make "Parameters" section wider. 
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
;; I'm surprised that row renderers don't get BOTH the `row-index` and the `row map` itself. That's to help with subscriptions I guess. Check.
;; 
;; Mention in docs that you are likely to use h-box and v-box in renderers.
;; 
;; Discuss with Gregg and Isaac:
;;   - the idea of variable row heights. 
;;   - performance: we have to reduce the amount of inline styles
;;   - is it really row_index that is passed into renderers? Or is it row id?  Clarify. Document. 
;;   - How do I create "CSS classes" in a namespace

