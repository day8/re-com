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

(defn flex-child-style
  [size]
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
  ;; TODO: Could make initial/auto/none into keywords???
  (let [split-size      (str/split (str/trim size) #"\s+")                  ;; Split into words separated by whitespace
        split-count     (count split-size)
        _               (assert (contains? #{1 3} split-count) "Must pass either 1 or 3 words to flex-child-style")
        size-only       (when (= split-count 1) (first split-size))         ;; Contains value when only one word passed (e.g. auto, 60px)
        split-size-only (when size-only (str/split size-only #"(\d+)(.*)")) ;; Split into number + string
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


;; TODO - belongs in unittests, not here
(defn test-flex-child-style []
  (assert (= (:flex (flex-child-style "initial")) "initial"))
  (assert (= (:flex (flex-child-style "auto")) "auto"))
  (assert (= (:flex (flex-child-style "none")) "none"))
  (assert (= (:flex (flex-child-style "100px")) "0 0 100px"))
  (assert (= (:flex (flex-child-style "4.5em")) "0 0 4.5em"))
  (assert (= (:flex (flex-child-style "60%")) "60 1 0px"))
  (assert (= (:flex (flex-child-style "60")) "60 1 0px"))
  (assert (= (:flex (flex-child-style "5 4 0%")) "5 4 0%"))
  ;(assert (= (flex-child-style "a b") "EXCEPTION"))
  )


(defn justify-style
  [justify]
  "Determines the value for the flex 'justify-content' attribute.
   This parameter determines how children are aligned along the main axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#justify-content-property"
  {:justify-content (case justify
                      :start   "flex-start"
                      :end     "flex-end"
                      :center  "center"
                      :between "space-between"
                      :around  "space-around")})


(defn align-style
  [attribute align]
  "Determines the value for the flex align type attributes.
   This parameter determines how children are aligned on the cross axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#align-items-property"
  {attribute (case align
               :start    "flex-start"
               :end      "flex-end"
               :center   "center"
               :baseline "baseline"
               :stretch  "stretch")})


(defn scroll-style
  [attribute scroll]
  "Determines the value for the 'overflow' attribute.
   The scroll parameter is a keyword.
   Because we're translating scroll into overflow, the keyword doesn't appear to match the attribute value."
  {attribute (case scroll
                  :auto  "auto"
                  :off   "hidden"
                  :on    "scroll"
                  :spill "visible")})


;; ------------------------------------------------------------------------------------
;;  gap (debug colour: chocolate)
;; ------------------------------------------------------------------------------------

(defn gap
  [& {:keys [size width height]}]
  "Returns markup which produces a gap between children in a v-box/h-box along the main axis.
   Specify size in any sizing amount, usually px or % or perhaps em. Defaults to 20px."
  (let [g-style (when size {:flex (str "0 0 " size)})
        w-style (when width {:width width})
        h-style (when height {:height height})
        d-style (when debug {:background-color "chocolate"})
        s       (merge g-style w-style h-style d-style)]
    [:div {:class "rc-gap" :style s}]))


;; ------------------------------------------------------------------------------------
;;  line
;; ------------------------------------------------------------------------------------

(defn line
  [& {:keys [size color]
      :or {size "1px" color "lightgray"}}]
  "Returns markup which produces a line between children in a v-box/h-box along the main axis.
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
      :or   {f-child true size "none" justify :start align :stretch}}]
  "Returns markup which produces a horizontal box.
   It's primary role is to act as a container for child components and lays it's children from left to right.
   By default, it also acts as a child under it's parent."
  (let [flex-container {:display "flex" :flex-flow "row nowrap"}
        flex-child     (when f-child (flex-child-style size))
        w-style        (if width {:width width})
        h-style        (when height {:height height})
        mw-style       (when min-width {:min-width min-width})
        mh-style       (when min-height {:min-height min-height})
        j-style        (justify-style justify)
        a-style        (align-style :align-items align)
        m-style        (when margin {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
        p-style        (when padding {:padding padding})
        d-style        (when debug {:background-color "gold"})
        s              (merge flex-container flex-child w-style h-style mw-style mh-style j-style a-style m-style p-style d-style)
        gap-form       (when gap [re-com.box/gap :size gap :width gap])
        children       (if gap
                         (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                         children)]
    (into [:div {:class "rc-h-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  v-box (debug colour: antiquewhite)
;; ------------------------------------------------------------------------------------

(defn v-box
  [& {:keys [f-child size width height min-width min-height justify align margin padding gap children]
      :or   {f-child true size "none" justify :start align :stretch}}]
  "Returns markup which produces a vertical box.
   It's primary role is to act as a container for child components and lays it's children from top to bottom.
   By default, it also acts as a child under it's parent."
  (let [flex-container {:display "flex" :flex-flow "column nowrap"}
        flex-child     (when f-child    (flex-child-style size))
        w-style        (when width      {:width width})
        h-style        (if height
                         {:height height}
                         {:height "inherit"})
        mw-style       (when min-width  {:min-width min-width})
        mh-style       (when min-height {:min-height min-height})
        j-style        (justify-style justify)
        a-style        (align-style :align-items align)
        m-style        (when margin     {:margin margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
        p-style        (when padding    {:padding padding})
        d-style        (when debug      {:background-color "antiquewhite"})
        s              (merge flex-container flex-child w-style h-style mw-style mh-style j-style a-style m-style p-style d-style)
        gap-form       (when gap [re-com.box/gap :size gap :height gap])
        children       (if gap
                         (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                         children)]
    (into [:div {:class "rc-v-box" :style s}] children)))


;; ------------------------------------------------------------------------------------
;;  box-base (debug colour: lightblue)
;; ------------------------------------------------------------------------------------

(defn box-base
  [& {:keys [class f-child f-container size scroll h-scroll v-scroll width height min-width min-height justify align align-self
             margin padding border l-border r-border t-border b-border radius bk-color child]}]
  "This should generally NOT be used as it is the basis for the box, scroller and border components."
  (let [flex-child     (when f-child     (flex-child-style size))
        flex-container (when f-container {:display "flex" :flex-flow "inherit"})
        s-style        (when scroll      (scroll-style :overflow scroll))
        sh-style       (when h-scroll    (scroll-style :overflow-x h-scroll))
        sv-style       (when v-scroll    (scroll-style :overflow-y v-scroll))
        w-style        (when width       {:width width})
        h-style        (when height      {:height height})
        mw-style       (when min-width   {:min-width min-width})
        mh-style       (when min-height  {:min-height min-height})
        j-style        (when (and f-container justify) (justify-style justify))
        a-style        (when (and f-container align) (align-style :align-items align))
        as-style       (when align-self  (align-style :align-self align-self))
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
        s              (merge flex-child flex-container s-style sh-style sv-style w-style h-style mw-style mh-style j-style a-style as-style
                              m-style p-style b-style bl-style br-style bt-style bb-style r-style c-style)]
    [:div {:class class :style s}
     child]))


;; ------------------------------------------------------------------------------------
;;  box
;; ------------------------------------------------------------------------------------

(defn box
  [& {:keys [f-child f-container size width height min-width min-height justify align align-self margin padding child]
      :or   {f-child true f-container true size "none"}}]
  "Returns markup which produces a box, which is generally used as a child of a v-box or an h-box.
   By default, it also acts as a container for further child compenents, or another h-box or v-box."
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
            :child       child))


;; ------------------------------------------------------------------------------------
;;  scroller
;; ------------------------------------------------------------------------------------

(defn scroller
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height align-self margin padding child]
      :or   {size "auto" scroll :auto}}]
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
  (box-base :class       "rc-scroller"
            :f-child     true
            :f-container true
            :size        size
            :scroll      scroll
            :h-scroll    h-scroll
            :v-scroll    v-scroll
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
              :size        "auto"
              ;:scroll      scroll
              ;:h-scroll    h-scroll
              ;:v-scroll    v-scroll
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
