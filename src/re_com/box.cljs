(ns re-com.box
  (:require [clojure.set    :refer [superset?]]
            [reagent.core :as reagent]
            [clojure.string :as string]))

(def debug false)


;; ------------------------------------------------------------------------------------
;;  Private Helper functions
;; ------------------------------------------------------------------------------------

(defn- flex-child-style
  "Determines the value for the 'flex' attribute (which has grow, shrink and basis), based on the size parameter.
   IMPORTANT: The term 'size' means width of the item in the case of flex-direction 'row' OR height of the item in the case of flex-direction 'column'.
   Flex property explanation:
    - grow    Integer ratio (used with other siblings) to determined how a flex item grows it's size if there is extra space to distribute. 0 for no growing.
    - shrink  Integer ratio (used with other siblings) to determined how a flex item shrinks it's size if space needs to be removed. 0 for no shrinking.
    - basis   Initial size of item before any growing or shrinking. Can be any size value, e.g. 60%, 100px, auto
   Supported values:
    - initial            '0 1 auto'  - Use item's width/height for dimensions (or content dimensions if w/h not specifed). Never grow. Shrink (to min-size) if necessary.
                                       Good for creating boxes with fixed maximum size, but that can shrink to a fixed smaller size (min-width/height) if space becomes tight.
                                       NOTE: When using initial, you should also set a width/height value (depending on flex-direction) to specify it's default size
                                             and an optional min-width/height value to specify the size it can shrink to.
    - auto               '1 1 auto'  - Use item's width/height for dimensions. Grow if necessary. Shrink (to min-size) if necessary.
                                       Good for creating really flexible boxes that will gobble as much available space as they are allowed or shrink as much as they are forced to.
    - none               '0 0 auto'  - Use item's width/height for dimensions (or content dimensions if not specifed). Never grow. Never shrink.
                                       Good for creating rigid boxes that stick to their width/height if specified, otherwise their content size.
    - 100px              '0 0 100px' - Non flexible 100px size (in the flex direction) box.
                                       Good for fixed headers/footers and side bars of an exact size.
    - 60%                '60 1 0px'  - Set the item's size (it's width/height depending on flex-direction) to be 60% of the parent container's width/height.
                                       NOTE: If you use this, then all siblings with percentage values must add up to 100%.
    - 60                 '60 1 0px'  - Same as percentage above.
    - grow shrink basis  'grow shrink basis' - If none of the above common valaues above meet your needs, this gives you precise control.
   If number of words is not 1 or 3, an exception is thrown.
   Reference: http://www.w3.org/TR/css3-flexbox/#flexibility
   Regex101 testing: ^(initial|auto|none)|(\\d+)(px|%|em)|(\\d+)\\w(\\d+)\\w(.*) - remove double backslashes"
  [size]
  ;; TODO: Could make initial/auto/none into keywords???
  (let [split-size      (string/split (string/trim size) #"\s+")                  ;; Split into words separated by whitespace
        split-count     (count split-size)
        _               (assert (contains? #{1 3} split-count) "Must pass either 1 or 3 words to flex-child-style")
        size-only       (when (= split-count 1) (first split-size))         ;; Contains value when only one word passed (e.g. auto, 60px)
        split-size-only (when size-only (string/split size-only #"(\d+)(.*)")) ;; Split into number + string
        [_ num units]   (when size-only split-size-only)                    ;; grab number and units
        pass-through?   (nil? num)                                          ;; If we can't split, then we'll pass this straign through
        grow-ratio?     (or (= units "%") (= units "") (nil? units))        ;; Determine case for using grow ratio
        grow            (if grow-ratio? num "0")                            ;; Set grow based on percent or integer, otherwise no grow
        shrink          (if grow-ratio? "1" "0")                            ;; If grow set, then set shrink to even shrinkage as well
        basis           (if grow-ratio? "0px" size)                         ;; If grow set, then even growing, otherwise set basis size to the passed in size (e.g. 100px, 5em)
        flex            (if (and size-only (not pass-through?))
                          (str grow " " shrink " " basis)
                          size)]
    {:flex flex}))


(defn- justify-style
  "Determines the value for the flex 'justify-content' attribute.
   This parameter determines how children are aligned along the main axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#justify-content-property"
  [justify]
  {:justify-content (case justify
                      :start   "flex-start"
                      :end     "flex-end"
                      :center  "center"
                      :between "space-between"
                      :around  "space-around")})


(defn- align-style
  "Determines the value for the flex align type attributes.
   This parameter determines how children are aligned on the cross axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#align-items-property"
  [attribute align]
  {attribute (case align
               :start    "flex-start"
               :end      "flex-end"
               :center   "center"
               :baseline "baseline"
               :stretch  "stretch")})


(defn- scroll-style
  "Determines the value for the 'overflow' attribute.
   The scroll parameter is a keyword.
   Because we're translating scroll into overflow, the keyword doesn't appear to match the attribute value."
  [attribute scroll]
  {attribute (case scroll
                  :auto  "auto"
                  :off   "hidden"
                  :on    "scroll"
                  :spill "visible")})


;; ------------------------------------------------------------------------------------
;;  Private Component: box-base (debug colour: lightblue)
;; ------------------------------------------------------------------------------------

(def box-base-args
  #{:class        ;;
    :f-child      ;;
    :f-container  ;;
    :size         ;;
    :scroll       ;;
    :h-scroll     ;;
    :v-scroll     ;;
    :width        ;;
    :height       ;;
    :min-width    ;;
    :min-height   ;;
    :justify      ;;
    :align        ;;
    :align-self   ;;
    :margin       ;;
    :padding      ;;
    :border       ;;
    :l-border     ;;
    :r-border     ;;
    :t-border     ;;
    :b-border     ;;
    :radius       ;;
    :bk-color     ;;
    :child        ;;
    :style        ;;
    })


(defn- box-base
  "This should generally NOT be used as it is the basis for the box, scroller and border components."
  [& {:keys [class f-child f-container size scroll h-scroll v-scroll width height min-width min-height justify align align-self
             margin padding border l-border r-border t-border b-border radius bk-color child style]
      :as   args}]
  {:pre [(superset? box-base-args (keys args))]}
  (let [s (merge
            (when f-child     (flex-child-style size))
            (when f-container {:display "flex" :flex-flow "inherit"})
            (when scroll      (scroll-style :overflow scroll))
            (when h-scroll    (scroll-style :overflow-x h-scroll))
            (when v-scroll    (scroll-style :overflow-y v-scroll))
            (when width       {:width width})
            (when height      {:height height})
            (when min-width   {:min-width min-width})
            (when min-height  {:min-height min-height})
            (when (and f-container justify) (justify-style justify))
            (when (and f-container align) (align-style :align-items align))
            (when align-self  (align-style :align-self align-self))
            (when margin      {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
            (when padding     {:padding padding})
            (when border      {:border        border})
            (when l-border    {:border-left   l-border})
            (when r-border    {:border-right  r-border})
            (when t-border    {:border-top    t-border})
            (when b-border    {:border-bottom b-border})
            (when radius      {:border-radius   radius})
            (if bk-color
              {:background-color bk-color}
              (if debug {:background-color "lightblue"} {}))
            style)]
    [:div {:class class :style s}
     child]))


;; ------------------------------------------------------------------------------------
;;  Component: gap (debug colour: chocolate)
;; ------------------------------------------------------------------------------------

(def gap-args
  #{:size     ;; Specify size in any sizing amount, usually px or % or perhaps em.
    :width    ;; Width will overrise size, but best to use size as it knows if it should be width or height .
    :height   ;; As per width above.
    :style    ;; If you really need to style this then go for it, but this is supposed to be layout, not content/styling.
    })


(defn gap
  "Returns markup which produces a gap between children in a v-box/h-box along the main axis."
  [& {:keys [size width height style]
      :as   args}]
  {:pre [(superset? gap-args (keys args))]}
  (let [s (merge
            (when size {:flex (str "0 0 " size)})
            (when width {:width width})
            (when height {:height height})
            (when debug {:background-color "chocolate"})
            style)]
    [:div {:class "rc-gap" :style s}]))


;; ------------------------------------------------------------------------------------
;;  Component: line
;; ------------------------------------------------------------------------------------

(def line-args
  #{:size   ;; Specify size in any sizing amount, usually px. Defaults to 1px.
    :color  ;; Specify colour using CSS colour methods. Defaults to lightgray.
    :style  ;; Extra style specification, but generally shouldn't need this.
    })


(defn line
  "Returns markup which produces a line between children in a v-box/h-box along the main axis.
   Specify size in pixels and a stancard CSS colour. Defaults to a 1px red line."
  [& {:keys [size color style]
      :or   {size "1px" color "lightgray"}
      :as   args}]
  {:pre [(superset? line-args (keys args))]}
  (let [s (merge
            {:flex (str "0 0 " size)}
            {:background-color color}
            style)]
    [:div {:class "rc-line" :style s}]))


;; ------------------------------------------------------------------------------------
;;  Component: h-box (debug colour: gold)
;; ------------------------------------------------------------------------------------

(def h-box-args
  #{:f-child      ;;
    :size         ;;
    :width        ;;
    :height       ;;
    :min-width    ;;
    :min-height   ;;
    :justify      ;;
    :align        ;;
    :margin       ;;
    :padding      ;;
    :gap          ;;
    :children     ;;
    :style        ;;
    })


(defn h-box
  "Returns markup which produces a horizontal box.
   It's primary role is to act as a container for child components and lays it's children from left to right.
   By default, it also acts as a child under it's parent."
  [& {:keys [f-child size width height min-width min-height justify align margin padding gap children style]
      :or   {f-child true size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(superset? h-box-args (keys args))]}
  (let [s        (merge
                   {:display "flex" :flex-flow "row nowrap"}
                   (when f-child (flex-child-style size))
                   (if width {:width width})
                   (when height {:height height})
                   (when min-width {:min-width min-width})
                   (when min-height {:min-height min-height})
                   (justify-style justify)
                   (align-style :align-items align)
                   (when margin {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
                   (when padding {:padding padding})
                   (when debug {:background-color "gold"})
                   style)
        gap-form (when gap [re-com.box/gap :size gap :width gap])
        children (if gap
                   (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                   children)]
    (into [:div {:class "rc-h-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  Component: v-box (debug colour: antiquewhite)
;; ------------------------------------------------------------------------------------

(def v-box-args
  #{:f-child      ;;
    :size         ;;
    :width        ;;
    :height       ;;
    :min-width    ;;
    :min-height   ;;
    :justify      ;;
    :align        ;;
    :margin       ;;
    :padding      ;;
    :gap          ;;
    :children     ;;
    :style        ;;
    })


(defn v-box
  "Returns markup which produces a vertical box.
   It's primary role is to act as a container for child components and lays it's children from top to bottom.
   By default, it also acts as a child under it's parent."
  [& {:keys [f-child size width height min-width min-height justify align margin padding gap children style]
      :or   {f-child true size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(superset? v-box-args (keys args))]}
  (let [s        (merge
                   {:display "flex" :flex-flow "column nowrap"}
                   (when f-child    (flex-child-style size))
                   (when width      {:width width})
                   {:height (if height height "inherit")}
                   (when min-width  {:min-width min-width})
                   (when min-height {:min-height min-height})
                   (justify-style justify)
                   (align-style :align-items align)
                   (when margin     {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
                   (when padding    {:padding padding})
                   (when debug      {:background-color "antiquewhite"})
                   style)
        gap-form (when gap [re-com.box/gap :size gap :height gap])
        children (if gap
                   (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                   children)]
    (into [:div {:class "rc-v-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  Component: box
;; ------------------------------------------------------------------------------------

(def box-args
  #{:f-child      ;;
    :f-container  ;;
    :size         ;;
    :width        ;;
    :height       ;;
    :min-width    ;;
    :min-height   ;;
    :justify      ;;
    :align        ;;
    :align-self   ;;
    :margin       ;;
    :padding      ;;
    :child        ;;
    :style        ;;
    })


(defn box
  "Returns markup which produces a box, which is generally used as a child of a v-box or an h-box.
   By default, it also acts as a container for further child compenents, or another h-box or v-box."
  [& {:keys [f-child f-container size width height min-width min-height justify align align-self margin padding child style]
      :or   {f-child true f-container true size "none"}
      :as   args}]
  {:pre [(superset? box-args (keys args))]}
  (box-base :class       "rc-box"
            :f-child     f-child
            :f-container f-container
            :size        size
            ;:scroll      scroll
            ;:h-scroll    h-scroll
            ;:v-scroll    v-scroll
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
            :child       child
            :style       style))


;; ------------------------------------------------------------------------------------
;;  Component: scroller
;; ------------------------------------------------------------------------------------

(def scroller-args
  #{:size         ;;
    :scroll       ;;
    :h-scroll     ;;
    :v-scroll     ;;
    :width        ;;
    :height       ;;
    :min-width    ;;
    :min-height   ;;
    :align-self   ;;
    :margin       ;;
    :padding      ;;
    :child        ;;
    :style        ;;
    })


(defn scroller
  "Returns markup which produces a scoller component.
   This is the way scroll bars are added to boxes, in favour of adding the scroll attributes directly to the boxes themselves.
   IMPORTANT: Because this component becomes the flex child in place of the component it is wrapping, you must copy the size attibutes to this componenet.
   There are three scroll types:
    - h-scroll  Determines how the horizontal scroll bar will be displayed.
    - v-scroll  Determines how the vertical scroll bar will be displayed.
    - scroll    Sets both h-scroll and v-scroll at once.
   Syntax: :auto   [DEFAULT] Only show scroll bar(s) if the content is larger than the scroller.
           :on     Always show scroll bar(s).
           :off    Never show scroll bar(s). Content which is not in the bounds of the scroller can not be seen.
           :spill  Never show scroll bar(s). Content which is not in the bounds of the scroller spills all over the place.
   Note:   If scroll is set, then setting h-scroll or v-scroll overrides the scroll value."
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height align-self margin padding child style]
      :or   {size "auto"}
      :as   args}]
  {:pre [(superset? scroller-args (keys args))]}
  (let [not-v-or-h (and (nil? v-scroll) (nil? h-scroll))
        scroll     (if (and (nil? scroll) not-v-or-h) :auto scroll)
        _          (+)]
    (box-base :class "rc-scroller"
              :f-child true
              :f-container true
              :size size
              :scroll scroll
              :h-scroll h-scroll
              :v-scroll v-scroll
              :width width
              :height height
              :min-width min-width
              :min-height min-height
              ;:justify     justify
              ;:align       align
              :align-self align-self
              :margin margin
              :padding padding
              ;:border      border
              ;:l-border    l-border
              ;:r-border    r-border
              ;:t-border    t-border
              ;:b-border    b-border
              ;:bk-color    bk-color
              ;:radius      radius
              :child child
              :style style)))


;; ------------------------------------------------------------------------------------
;;  Component: border
;; ------------------------------------------------------------------------------------

(def border-args
  #{:size       ;;
    :width      ;;
    :height     ;;
    :min-width  ;;
    :min-height ;;
    :margin     ;;
    :padding    ;;
    :border     ;;
    :l-border   ;;
    :r-border   ;;
    :t-border   ;;
    :b-border   ;;
    :radius     ;;
    :child      ;;
    :style      ;;
    })


(defn border
  "Returns markup which produces a border component.
   This is the way borders are added to boxes, in favour of adding the border attributes directly to the boxes themselves.
   border property syntax: '<border-width> || <border-style> || <color>'
    - border-width: thin, medium, thick or standard CSS size (e.g. 2px, 0.5em)
    - border-style: none, hidden, dotted, dashed, solid, double, groove, ridge, inset, outset
    - color:        standard CSS color (e.g. grey #88ffee)"
  [& {:keys [size width height min-width min-height margin padding border l-border r-border t-border b-border radius child style]
      :or   {size "auto"}
      :as   args}]
  {:pre [(superset? border-args (keys args))]}
  (let [no-border      (every? nil? [border l-border r-border t-border b-border])
        default-border "1px solid lightgrey"]
    (box-base :class "rc-border"
              :f-child     true
              :f-container true
              :size        size
              ;:scroll      scroll
              ;:h-scroll    h-scroll
              ;:v-scroll    v-scroll
              :width       width
              :height      height
              :min-width   min-width
              :min-height  min-height
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
              :child       child
              :style       style)))
