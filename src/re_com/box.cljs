(ns re-com.box
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(def debug false)

;; ------------------------------------------------------------------------------------
;;  Boxes
;; ------------------------------------------------------------------------------------

;; Use Cases
;;
;; Case 1:
;;   - fixed LHS ... certain px ?
;;    - one or more growable compents, perctage based
;;
;; Case 2:
;;    - fixed left and right
;;    - growable middle
;;
;; Case 3:
;;    - one of the box components is a vbox inside an hbox.
;;
;; Will ask for "perc" but underneath that's just ratios
;;
;; Align stuff, we will ignore.
;; max-size contraints
;;
;; [scrolling XXXX]  contins a box
;; [border XXXX]     contains a box for the children
;;
;; Let's use HBox and VBox as the names
;;
;; Notes
;;  - Center object in 
;;  - Override h1 to h6? with {:style {:display: inline}} in a .css file
;;  - Names for children: wrap, content, inside, inner, elements
;;
;;
;;

;; ------------------------------------------------------------------------------------
;;  helper functions
;; ------------------------------------------------------------------------------------

#_(defn split-size ;; REDUNDANT, use clojure.string/split
  [size]
  "Splits a CSS size attibute into two parts. e.g.
    - '100'   will return ['100' '']
    - '100px' will return ['100' 'px']
    - '50%'   will return ['50' '%']
    - 'auto'  will return [nil nil] (so will anything that isn't in the form of {number}{string}"
  (let [re (js/RegExp. #"(\d+)(.*)")]
    (if-let [res  (js->clj (.exec re size))]
      [(second res) (last res)]
      [nil nil])))


(defn flex-child-style
  [size]
  "Determines the value for the 'flex' attribute, based on the size parameter.
   e.g. 100px, 60% or auto
   Also handles passing a custom flex string if there is more than one word in size. e.g. '0 0 auto'"
  (let [split-size      (str/split (str/trim size) #" +")
        size-only       (when (= (count split-size) 1) split-size)
        split-size-only (when size-only (str/split size-only #"(\d+)(.*)"))
        [_ num units]   (if size-only (js->clj split-size-only) nil)
        percent         (or (= units "%") (= units "") (= units "\"]")) ;; TODO: Not sure why we're getting the "]
        grow            (if percent num 0)
        shrink          1
        basis           (if percent "0px" size)]
    {:flex (if size-only
             (str grow " " shrink " " basis)
             size)}))


(defn justify-style
  [justify]
  "Determines the value for the flex 'justify-content' attribute.
   This parameter determines how children are aligned along the normal axis.
   The justify parameter is a keyword."
  {:justify-content (case justify
                      :start   "flex-start"
                      :end     "flex-end"
                      :center  "center"
                      :between "space-between"
                      :around  "space-around")})


(defn align-style
  [align]
  "Determines the value for the flex 'align' or 'self-align' attributes.
   This parameter determines how children are aligned on the cross-axis.
   The justify parameter is a keyword."
  {:align-items (case align
                  :start    "flex-start"
                  :end      "flex-end"
                  :center   "center"
                  :baseline "baseline"
                  :stretch  "stretch")})


(defn scroll-style
  [scroll]
  "Determines the value for the 'overflow' attribute.
   The scroll parameter is a keyword.
   Because we're translating scroll into overflow, the keyword doesn't appear to match the attribute value."
  {:overflow (case scroll
                  :auto  "auto"
                  :off   "hidden"
                  :on    "scroll"
                  :spill "visible")})


;; ------------------------------------------------------------------------------------
;;  gap (debug colour: chocolate)
;; ------------------------------------------------------------------------------------

(defn gap
  [& {:keys [size]
      :or {size "20px"}}]
  "Returns markup which produces a gap between children in a v-box/h-box along the normal axis.
   Specify size in pixels. Defaults to 20px."
  (let [g-style {:flex (str "0 0 " size)}
        d-style (when debug {:background-color "chocolate"
                             :border "1px dashed black"})
        s       (merge g-style d-style)]
    [:div {:class "rc-gap" :style s}]))


;; ------------------------------------------------------------------------------------
;;  line
;; ------------------------------------------------------------------------------------

(defn line
  [& {:keys [size color]
      :or {size "1px" color "red"}}]
  "Returns markup which produces a line between children in a v-box/h-box along the normal axis.
   Specify size in pixels and a stancard CSS colour. Defaults to a 1px red line."
  (let [flex-child {:flex (str "0 0 " size)}
        c-style    {:background-color color}
        s          (merge flex-child c-style)]
    [:div {:class "rc-line" :style s}]))


;; ------------------------------------------------------------------------------------
;;  h-box (debug colour: gold)
;; ------------------------------------------------------------------------------------

(defn h-box
  [& {:keys [f-child size width height min-width min-height justify align margin padding gap children]
      :or   {f-child true size "auto" justify :start align :stretch}}]
  "Returns markup which produces a horizontal box.
   It's primary role is to act as a container for child components and lays it's children from left to right.
   By default, it also acts as a child under it's parent."
  (let [flex-container {:display "flex" :flex-flow "row nowrap"}
        flex-child     (when f-child (flex-child-style size)) ;; Was {:flex "1 1 0px"}
        w-style        (if width
                         {:width width}
                         {:width "inherit"}) ;; width inheritence is actually optional, but here for consistency with
        h-style        (when height {:height height})
        mw-style       (when min-width {:min-width min-width})
        mh-style       (when min-height {:min-height min-height})
        j-style        (justify-style justify)
        a-style        (align-style align)
        m-style        (when margin {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
        p-style        (when padding {:padding padding})
        d-style        (when debug {:background-color "gold"})
        s              (merge flex-container flex-child w-style h-style mw-style mh-style j-style a-style m-style p-style d-style)
        gap-form       (when gap [re-com.box/gap :size gap])
        children       (if gap
                         (drop-last (interleave children (repeat gap-form))) ;; Probably not more readable: (->> gap-form repeat (interleave children) drop-last)
                         children)]
    (into [:div {:class "rc-h-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  v-box (debug colour: antiquewhite)
;; ------------------------------------------------------------------------------------

(defn v-box
  [& {:keys [f-child size width height min-width min-height justify align margin padding gap children]
      :or   {f-child true size "auto" justify :start align :stretch}}]
  "Returns markup which produces a vertical box.
   It's primary role is to act as a container for child components and lays it's children from top to bottom.
   By default, it also acts as a child under it's parent."
  (let [flex-container {:display "flex" :flex-flow "column nowrap"}
        flex-child     (when f-child    (flex-child-style size)) ;; Was {:flex "1 1 0px"}
        w-style        (when width      {:width width})
        h-style        (if height
                         {:height height}
                         {:height "inherit"})
        mw-style       (when min-width  {:min-width min-width})
        mh-style       (when min-height {:min-height min-height})
        j-style        (justify-style justify)
        a-style        (align-style align)
        m-style        (when margin     {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
        p-style        (when padding    {:padding padding})
        d-style        (when debug      {:background-color "antiquewhite"})
        s              (merge flex-container flex-child w-style h-style mw-style mh-style j-style a-style m-style p-style d-style)
        gap-form       (when gap [re-com.box/gap :size gap])
        children       (if gap
                         (drop-last (interleave children (repeat gap-form)))
                         children)]
    (into [:div {:class "rc-v-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  box-base (debug colour: lightblue)
;; ------------------------------------------------------------------------------------

(defn box-base
  [& {:keys [class f-child f-container size scroll width height min-width min-height justify align align-self
             margin padding border l-border r-border t-border b-border radius bk-color child]}]
  "This should generally NOT be used as it is the basis for the box, scroller and border components."
  (let [flex-child     (when f-child     (flex-child-style size))
        flex-container (when f-container {:display "flex" :flex-flow "inherit"})
        s-style        (when scroll      (scroll-style scroll))  ;; TODO: Possibly also implement h-scroll and v-scroll
        w-style        (when width       {:width width})
        h-style        (when height      {:height height})
        mw-style       (when min-width   {:min-width min-width})
        mh-style       (when min-height  {:min-height min-height})
        j-style        (when (and f-container justify) (justify-style justify))
        a-style        (when (and f-container align) (align-style align))
        as-style       (when align-self  (align-style align-self))
        m-style        (when margin      {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
        p-style        (when padding     {:padding padding})
        b-style        (when border      {:border        border})
        bl-style       (when l-border    {:border-left   l-border})
        br-style       (when r-border    {:border-right  r-border})
        bt-style       (when t-border    {:border-top    t-border})
        bb-style       (when b-border    {:border-bottom b-border})
        r-style        (when radius      {:border-radius   radius})
        c-style        (if bk-color
                         {:background-color bk-color}
                         (if debug {:background-color "lightblue"} {}))
        s              (merge flex-child flex-container s-style w-style h-style mw-style mh-style j-style a-style as-style
                              m-style p-style b-style bl-style br-style bt-style bb-style r-style c-style)]
    [:div {:class class :style s}
     child]))


;; ------------------------------------------------------------------------------------
;;  box
;; ------------------------------------------------------------------------------------

(defn box
  [& {:keys [f-child f-container size width height min-width min-height justify align align-self margin padding child]
      :or   {f-child true f-container true size "auto"}}]
  "Returns markup which produces a box, which is generally used as a child of a v-box or an h-box.
   By default, it also acts as a container for further child compenents, or another h-box or v-box."
  (box-base :class       "rc-box"
            :f-child     f-child
            :f-container f-container
            :size        size
            ;:scroll      scroll
            :width       width
            :height      height
            :min-width   min-width
            :min-height  min-height
            :justify     justify
            :align       align
            :align-self  align-self
            :margin      margin
            :padding     padding
            ;:border      border
            ;:l-border    l-border
            ;:r-border    r-border
            ;:t-border    t-border
            ;:b-border    b-border
            ;:bk-color    bk-color
            ;:radius      radius
            :child       child))


;; ------------------------------------------------------------------------------------
;;  scroller
;; ------------------------------------------------------------------------------------

(defn scroller
  [& {:keys [scroll width height min-width min-height align-self margin padding child]
      :or   {}}]
  "Returns markup which produces a scoller component.
   This is the way scroll bars are added to boxes, in favour of adding the scroll attributes directly to the boxes themselves.
   scroll property syntax: :auto  Only show scroll bars if rquired.
                           :on    Always show scroll bars.
                           :off   Never show scroll bars (content not in the bounds of the scroller can not be seen).
                           :spill Never show scroll bars (content not in the bounds of the scroller spills all over the place)."
  (box-base :class       "rc-scroller"
            :f-child     true
            :f-container false
            :size        "auto"                         ;; Was "1" which produces "1 1 0px"
            :scroll      scroll
            :width       width
            :height      height
            :min-width   min-width
            :min-height  min-height
            ;:justify     justify
            ;:align       align
            :align-self  align-self
            :margin      margin
            :padding     padding
            ;:border      border
            ;:l-border    l-border
            ;:r-border    r-border
            ;:t-border    t-border
            ;:b-border    b-border
            ;:bk-color    bk-color
            ;:radius      radius
            :child       child))


;; ------------------------------------------------------------------------------------
;;  border
;; ------------------------------------------------------------------------------------

(defn border
  [& {:keys [margin padding border l-border r-border t-border b-border radius child]
      :or   {}}]
  "Returns markup which produces a border component.
   This is the way borders are added to boxes, in favour of adding the border attributes directly to the boxes themselves.
   border property syntax: '<border-width> || <border-style> || <color>'
    - border-width: thin, medium, thick or standard CSS size (e.g. 2px, 0.5em)
    - border-style: none, hidden, dotted, dashed, solid, double, groove, ridge, inset, outset
    - color:        standard CSS color (e.g. grey #88ffee)"
  (let [no-border      (every? nil? [border l-border r-border t-border b-border])
        default-border "1px solid lightgrey"]
    (box-base :class       "rc-border"
              :f-child     true
              :f-container true
              :size        "1"
              ;:scroll      scroll
              ;:width       width
              ;:height      height
              ;:min-width   min-width
              ;:min-height  min-height
              ;:justify     justify
              ;:align       align
              ;:align-self  align-self
              :margin      margin
              :padding     padding
              :border      (if no-border default-border border)
              :l-border    l-border
              :r-border    r-border
              :t-border    t-border
              :b-border    b-border
              :radius      radius
              ;:bk-color    bk-color
              :child       child)))
