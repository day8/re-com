(ns re-com.box
  (:require [reagent.core :as reagent]))


;; ------------------------------------------------------------------------------------
;;  Boxes
;; ------------------------------------------------------------------------------------

;; Use Cases
;;
;; Case 1:
;;   - fixed LHS ... certain px ?
;;    - one or more grwoable compents, perctage based
;;
;; Case 2:
;;    - fixed left and right
;;    - growable middle
;;
;; Case 3:
;;    - one of the box compents is a vbox insdie an hbox.
;;
;; Will ask for "perc" but underneath that's just ratios
;;
;; Align stuff, we will ignore.
;; max-size contraints
;;
;; [scrolling XXXX]  contins a box
;; [border XXXX]     contains a box for the children
;;
;;
;; Let's use HBox and VBox as the names
;;
;;
;; Notes
;;  - Center object in 
;;  - Override h1 to h6? with {:style {:display: inline}} in a .css file
;;  - Names for children: wrap, content, inside, inner, elements
;;
;;
;;

;; ------------------------------------------------------------------------------------
;;  h-box
;; ------------------------------------------------------------------------------------

(defn h-box
  [& {:keys [width height align padding gap children]
      :or   {width "100%" height "100%" align :between}}]
  (let [flex-container {:display "flex" :flex-direction "row" :flex-wrap "nowrap"}
        flex-child     {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        w-style        {:width width}
        h-style        {:height height}
        a-style        {:justify-content (case align
                                           :left    "flex-start"
                                           :right   "flex-end"
                                           :center  "center"
                                           :between "space-between"
                                           :around  "space-around")}
        p-style        (if padding {:padding padding} {})
        g-style        {}
        s              (merge flex-container flex-child w-style h-style a-style p-style g-style)]
    (into [:div {:style s}] children)))


;; ------------------------------------------------------------------------------------
;;  v-box
;; ------------------------------------------------------------------------------------

(defn v-box
  [& {:keys [width height align padding gap children]
      :or   {width "100%" height "100%" align :between}}]
  (let [flex-container {:display "flex" :flex-direction "column" :flex-wrap "nowrap"}
        flex-child     {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        w-style        {:width width}
        h-style        {:height height}
        a-style        {:justify-content (case align
                                           :top     "flex-start"
                                           :bottom  "flex-end"
                                           :center  "center"
                                           :between "space-between"
                                           :around  "space-around")}
        p-style        (if padding {:padding padding} {})
        g-style        {}
        s              (merge flex-container flex-child w-style h-style a-style p-style g-style)]
    (into [:div {:style s}] children)))


;; ------------------------------------------------------------------------------------
;;  box
;; ------------------------------------------------------------------------------------

(defn box
  [& {:keys [align child]}]
  (let [flex-child {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        a-style    (if align {:align-self (case align
                                            :start "flex-start"
                                            :end   "flex-end")}
                             {})
        s          (merge flex-child a-style)]
  [:div {:style s}
   child]))


;; ------------------------------------------------------------------------------------
;;  h-gap
;; ------------------------------------------------------------------------------------

(defn h-gap
  [& {:keys [height]
      :or {height "20px"}}]
  (let [flex-child {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        h-style    {:height height}
        s          (merge flex-child h-style)]
    [:div {:style s}]))


;; ------------------------------------------------------------------------------------
;;  v-gap
;; ------------------------------------------------------------------------------------

(defn v-gap
  [& {:keys [width]
      :or {width "20px"}}]
  (let [flex-child {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        h-style    {:width width}
        s          (merge flex-child h-style)]
    [:div {:style s}]))


;; ------------------------------------------------------------------------------------
;;  h-line
;; ------------------------------------------------------------------------------------

(defn h-line
  [& {:keys [height color]
      :or {height "1px" color "grey"}}]
  (let [flex-child {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        c-style    {:background-color color}
        h-style    {:height height}
        s          (merge flex-child c-style h-style)]
    [:div {:style s}]))


;; ------------------------------------------------------------------------------------
;;  v-line
;; ------------------------------------------------------------------------------------

(defn v-line
  [& {:keys [width color]
      :or {width "1px" color "grey"}}]
  (let [flex-child {:flex-grow 0 :flex-shrink 1 :flex-basis "auto"}
        c-style    {:background-color color}
        h-style    {:width width}
        s          (merge flex-child c-style h-style)]
    [:div {:style s}]))
