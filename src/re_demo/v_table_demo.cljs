(ns re-demo.v-table-demo
  (:require [re-com.core   :refer [h-box gap v-box box v-table p label]]
            [re-com.util    :refer [px]]
            [re-demo.utils :refer [title2]]
            [reagent.core  :as reagent]))



(defn demo
  []
  [v-box
   :gap      "10px"
   :children [[title2 "Demo"]
              [p "coming soon"]]])


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


