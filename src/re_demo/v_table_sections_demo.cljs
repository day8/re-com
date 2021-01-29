(ns re-demo.v-table-sections-demo
  (:require [re-com.core     :refer [h-box gap v-box box v-table hyperlink-href p label]]
            [re-demo.utils   :refer [panel-title title2 title3 args-table github-hyperlink status-text]]
            [re-com.validate :refer [vector-atom? vector-or-atom? map-atom? parts?]]
            [re-com.util     :refer [px]]
            [reagent.core    :as reagent]))


(def big-font "18px")
(def small-font "10px")

(defn render-two-lines
  [name section background height]
  [v-box
   :height   (px height)
   :style    {:color            "white"
              :background-color background}
   :align    :center
   :children [[label :label section :style {:font-size big-font}]
              [label :label name :style {:font-size small-font}]]])


(defn sections-demo
  []
  (let [light-blue                "#DBEFF9"
        medium-blue               "#5B9BD5"
        blue                      "#0F6FC6"
        num_rows                  6
        dummy-rows                (reagent/atom (mapv #(assoc nil :id %1) (range num_rows))) ;; TODO: Changed to atom for testing validation-fn, can change back when we successfully allow atom OR value
        row-height                25
        col-header-footer-height  40
        width-of-main-row-content 250
        header-footer-style       {:style {:height           (px row-height)
                                           :text-align       "center"
                                           :color            "white"
                                           :background-color medium-blue}}
        row-style                 {:style {:width            (px width-of-main-row-content)
                                           :height           (px row-height)
                                           :text-align       "center"
                                           :background-color light-blue}}]
    (fn []
      [v-box
       :gap      "10px"
       :children [[title2 "Sections Demo"]
                  [:p {:style {:width "450px"}} "There are nine sections in a v-table, of which only #5 is mandatory. This table has 6 rows of data. "]

                  [v-table
                   :model                  dummy-rows
                   :row-height             row-height
                   :row-content-width      width-of-main-row-content

                   ;; column header - section 4
                   :column-header-height   col-header-footer-height
                   :column-header-renderer (fn [] [render-two-lines "column headers" "4" medium-blue col-header-footer-height])

                   ;; row header, rows, row footer - sections 2, 5, 8
                   :row-header-renderer    (fn [row-index row] [:span header-footer-style (str "hdr " row-index)])
                   :row-renderer           (fn [row-index row] [:span row-style           (str "row " row-index)])
                   :row-footer-renderer    (fn [row-index row] [:span header-footer-style (str "ftr " row-index)])

                   ;; column footer - section 6
                   :column-footer-height   col-header-footer-height
                   :column-footer-renderer (fn [] [render-two-lines "column footers" "6" medium-blue col-header-footer-height])

                   ;; corners
                   :top-left-renderer     (fn [] [render-two-lines "top left"     "1" blue col-header-footer-height])
                   :bottom-left-renderer  (fn [] [render-two-lines "bottom left"  "3" blue col-header-footer-height])
                   :top-right-renderer    (fn [] [render-two-lines "top right"    "7" blue col-header-footer-height])
                   :bottom-right-renderer (fn [] [render-two-lines "bottom right" "9" blue col-header-footer-height])
                   ]]])))


;; MT's Notes: 
;; 
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

