 (ns re-com.box
  (:require [clojure.set    :refer [superset?]]
            [reagent.core :as reagent]
            [clojure.string :as string]
            [re-com.util :refer [validate-arguments]]))

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
    - basis   Initial size (width, actually) of item before any growing or shrinking. Can be any size value, e.g. 60%, 100px, auto
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
   Diagram:   http://www.w3.org/TR/css3-flexbox/#flex-container
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

(def box-base-args-desc
  [{:name :size         :required false :default "auto"                 :type "string"   :description "a Flex size. Refer flex-child-style."}
   {:name :scroll       :required false :default "auto"                 :type "keyword"  :description "Sets both h-scroll and v-scroll at once. One of -
<code>:auto</code> - only show scroll bar(s) if the content is larger than the scroller.
<code>:on</code> - always show scroll bar(s).
<code>:off</code> - never show scroll bar(s). Content which is not in the bounds of the scroller can not be seen.
<code>:spill</code> - never show scroll bar(s). Content which is not in the bounds of the scroller spills all over the place."}
   {:name :h-scroll     :required false                                 :type "keyword"  :description "see <code>:scroll</code>. Overrides that setting."}
   {:name :v-scroll     :required false                                 :type "keyword"  :description "see <code>:scroll</code>. Overrides that setting."}
   {:name :width        :required false                                 :type "string"   :description "a CSS style describing the initial width."}
   {:name :height       :required false                                 :type "string"   :description "a CSS style describing the initial height."}
   {:name :min-width    :required false                                 :type "string"   :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height   :required false                                 :type "string"   :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :justify      :required false :default :start                 :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:between</code> and <code>:around</code>."}
   {:name :align        :required false :default :stretch               :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:baseline</code> and <code>:stretch</code>."}
   {:name :align-self   :required false                                 :type "boolean"  :description "use to override parent alignment setting for this component."}
   {:name :margin       :required false                                 :type "string"   :description "a CSS margin style."}
   {:name :padding      :required false                                 :type "string"   :description "a CSS padding style."}
   {:name :border       :required false :default "1px solid lightgrey"  :type "string"   :description "a CSS border style. A convenience to describe all borders in one parameter."}
   {:name :l-border     :required false :default :border                :type "string"   :description "a CSS border style for the left border. Overrides <code>:border</code>."}
   {:name :r-border     :required false :default :boder                 :type "string"   :description "a CSS border style for the right border. Overrides <code>:border</code>."}
   {:name :t-border     :required false :default :border                :type "string"   :description "a CSS border style for the top border. Overrides <code>:border</code>."}
   {:name :b-border     :required false :default :boder                 :type "string"   :description "a CSS border style for the bottom. Overrides <code>:border</code>."}
   {:name :radius       :required false                                 :type "string"   :description "a CSS radius style eg.\"2px\"."}
   ;;{:name :bk-color   :required false                                 :type "string"   :description "a CSS colour."} ;; TODO is this used?
   {:name :child        :required false                                 :type "markup"   :description "a child component markup."}
   {:name :class        :required false                                 :type "string"   :description "additional CSS classes required."}
   {:name :style        :required false                                 :type "map"      :description "CSS styles to add or override."}
   {:name :attr         :required false                                 :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def box-base-args
  (set (map :name box-base-args-desc)))

(defn- box-base
  "This should generally NOT be used as it is the basis for the box, scroller and border components."
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height justify align align-self
             margin padding border l-border r-border t-border b-border radius bk-color child class style attr]
      :as   args}]
  {:pre [(validate-arguments box-base-args (keys args))]}
  (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
  (let [s (merge
            {:display "flex" :flex-flow "inherit"}
            (flex-child-style size)

            (when scroll      (scroll-style :overflow scroll))
            (when h-scroll    (scroll-style :overflow-x h-scroll))
            (when v-scroll    (scroll-style :overflow-y v-scroll))
            (when width       {:width width})
            (when height      {:height height})
            (when min-width   {:min-width min-width})
            (when min-height  {:min-height min-height})

            ;(when (and f-container justify) (justify-style justify))
            ;(when (and f-container align) (align-style :align-items align))
            (when justify (justify-style justify))
            (when align (align-style :align-items align))

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
    [:div
     (merge
       {:class class :style s}
       attr)
     child]))


;; ------------------------------------------------------------------------------------
;;  Component: gap (debug colour: chocolate)
;; ------------------------------------------------------------------------------------

(def gap-args-desc
  [{:name :size            :required true   :type "string"   :description "a CSS style to specify size in any sizing amount, usually px, % or em."}
   {:name :width           :required false  :type "string"   :description "<code>:width</code> will overrise size, but best to use size as it knows if it should be width or height."}
   {:name :height          :required false  :type "string"   :description "as per width above."}
   {:name :class           :required false  :type "string"   :description "additional CSS classes required."}
   {:name :style           :required false  :type "map"      :description "CSS styles to add or override."}
   {:name :attr            :required false  :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def gap-args
  (set (map :name gap-args-desc)))

(defn gap
  "Returns markup which produces a gap between children in a v-box/h-box along the main axis."
  [& {:keys [size width height class style attr]
      :as   args}]
  {:pre [(validate-arguments gap-args (keys args))]}
  (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
  (let [s (merge
            ;(when size {:flex (str "0 0 " size)})
            (when size (flex-child-style size))
            (when width {:width width})
            (when height {:height height})
            (when debug {:background-color "chocolate"})
            style)]
    [:div
     (merge
       {:class (str "rc-gap " class) :style s}
       attr)]))


;; ------------------------------------------------------------------------------------
;;  Component: line
;; ------------------------------------------------------------------------------------

(def line-args-desc
  [{:name :size            :required true                       :type "string"   :description "a CSS style to specify size in any sizing amount, usually px, % or em."}
   {:name :color           :required false :default "lightgray" :type "string"   :description "a colour using CSS colour methods."}
   {:name :class           :required false                      :type "string"   :description "additional CSS classes required."}
   {:name :style           :required false                      :type "map"      :description "CSS styles to add or override."}
   {:name :attr            :required false                      :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def line-args
  (set (map :name line-args-desc)))

(defn line
  "Returns markup which produces a line between children in a v-box/h-box along the main axis.
   Specify size in pixels and a stancard CSS colour. Defaults to a 1px red line."
  [& {:keys [size color class style attr]
      :or   {size "1px" color "lightgray"}
      :as   args}]
  {:pre [(validate-arguments line-args (keys args))]}
  (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
  (let [s (merge
            {:flex (str "0 0 " size)}
            {:background-color color}
            style)]
    [:div
     (merge
       {:class (str "rc-line " class) :style s}
       attr)]))


;; ------------------------------------------------------------------------------------
;;  Component: h-box (debug colour: gold)
;; ------------------------------------------------------------------------------------

(def h-box-args-desc
  [{:name :size            :required false :default "none"   :type "string"    :description "a Flex size. Refer flex-child-style."}
   {:name :width           :required false                   :type "string"    :description "a CSS width style."}
   {:name :height          :required false                   :type "string"    :description "a CSS height style."}
   {:name :min-width       :required false                   :type "string"    :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height      :required false                   :type "string"    :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :justify         :required false :default :start   :type "keyword"   :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:between</code> and <code>:around</code>."}
   {:name :align           :required false :default :stretch :type "keyword"   :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:baseline</code> and <code>:stretch</code>."}
   {:name :margin          :required false                   :type "string"    :description "a CSS margin style."}
   {:name :padding         :required false                   :type "string"    :description "a CSS padding style."}
   {:name :gap             :required false                   :type "string"    :description "a CSS size style. See gap component."}
   {:name :children        :required false                   :type "vector"    :description "a vector of child component markup."}
   {:name :class           :required false                   :type "string"    :description "additional CSS classes required."}
   {:name :style           :required false                   :type "map"       :description "CSS styles to add or override."}
   {:name :attr            :required false                   :type "map"       :description "html attributes to add or override (:class/:style not allowed)."}])

(def h-box-args
  (set (map :name h-box-args-desc)))

(defn h-box
  "Returns markup which produces a horizontal box.
   It's primary role is to act as a container for child components and lays it's children from left to right.
   By default, it also acts as a child under it's parent."
  [& {:keys [size width height min-width min-height justify align margin padding gap children class style attr]
      :or   {size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(validate-arguments h-box-args (keys args))]}
  (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
  (let [s        (merge
                   {:display "flex" :flex-flow "row nowrap"}
                   (flex-child-style size)
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
    (into [:div
           (merge
             {:class (str "rc-h-box " class) :style s}
             attr)]
          children)))


;; ------------------------------------------------------------------------------------
;;  Component: v-box (debug colour: antiquewhite)
;; ------------------------------------------------------------------------------------

(def v-box-args-desc
  [{:name :size            :required false :default "none"   :type "string"    :description "a Flex style. Refer flex-child-style."}
   {:name :width           :required false                   :type "string"    :description "a CSS width style."}
   {:name :height          :required false                   :type "string"    :description "a CSS height style."}
   {:name :min-width       :required false                   :type "string"    :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height      :required false                   :type "string"    :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :justify         :required false :default :start   :type "keyword"   :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:between</code> and <code>:around</code>."}
   {:name :align           :required false :default :stretch :type "keyword"   :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:baseline</code> and <code>:stretch</code>."}
   {:name :margin          :required false                   :type "string"    :description "a CSS margin style."}
   {:name :padding         :required false                   :type "string"    :description "a CSS padding style."}
   {:name :gap             :required false                   :type "string"    :description "a CSS size style. See gap component."}
   {:name :children        :required false                   :type "vector"    :description "a vector of child component markup."}
   {:name :class           :required false                   :type "string"    :description "additional CSS classes required."}
   {:name :style           :required false                   :type "map"       :description "CSS styles to add or override."}
   {:name :attr            :required false                   :type "map"       :description "html attributes to add or override (:class/:style not allowed)."}])

(def v-box-args
  (set (map :name v-box-args-desc)))

(defn v-box
  "Returns markup which produces a vertical box.
   It's primary role is to act as a container for child components and lays it's children from top to bottom.
   By default, it also acts as a child under it's parent."
  [& {:keys [size width height min-width min-height justify align margin padding gap children class style attr]
      :or   {size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(validate-arguments v-box-args (keys args))]}
  (assert (not-any? #(contains? #{:style :class} (first %)) attr) ":attr cannot contain :class or :style members")
  (let [s        (merge
                   {:display "flex" :flex-flow "column nowrap"}
                   (flex-child-style size)
                   (when width      {:width width})
                   (when height     {:height height})
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
    (into [:div
           (merge
             {:class (str "rc-v-box " class) :style s}
             attr)]
          children)))


;; ------------------------------------------------------------------------------------
;;  Component: box
;; ------------------------------------------------------------------------------------

(def box-args-desc
  [{:name :size            :required false :default "none"   :type "string"   :description "a Flex size. Refer flex-child-style."}
   {:name :width           :required false                   :type "string"   :description "a CSS width style."}
   {:name :height          :required false                   :type "string"   :description "a CSS height style."}
   {:name :min-width       :required false                   :type "string"   :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height      :required false                   :type "string"   :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :justify         :required false :default :start   :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:between</code> and <code>:around</code>."}
   {:name :align           :required false :default :stretch :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:baseline</code> and <code>:stretch</code>."}
   {:name :align-self      :required false                   :type "boolean"  :description "use to override parent alignment setting for this component."}
   {:name :margin          :required false                   :type "string"   :description "a CSS margin style."}
   {:name :padding         :required false                   :type "string"   :description "a CSS padding style."}
   {:name :child           :required false                   :type "markup"   :description "a child component markup."}
   {:name :class           :required false                   :type "string"   :description "additional CSS classes required."}
   {:name :style           :required false                   :type "string"   :description "CSS styles to add or override."}
   {:name :attr            :required false                   :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def box-args
  (set (map :name box-args-desc)))


(defn box
  "Returns markup which produces a box, which is generally used as a child of a v-box or an h-box.
   By default, it also acts as a container for further child compenents, or another h-box or v-box."
  [& {:keys [size width height min-width min-height justify align align-self margin padding child class style attr]
      :or   {size "none"}
      :as   args}]
  {:pre [(validate-arguments box-args (keys args))]}
  (box-base :size        size
            :width       width
            :height      height
            :min-width   min-width
            :min-height  min-height
            :justify     justify
            :align       align
            :align-self  align-self
            :margin      margin
            :padding     padding
            :child       child
            :class       (str "rc-box " class)
            :style       style
            :attr        attr))


;; ------------------------------------------------------------------------------------
;;  Component: scroller
;; ------------------------------------------------------------------------------------

(def scroller-args-desc
  [{:name :size            :required false :default "auto"   :type "string"   :description "a Flex size. Refer flex-child-style."}
   {:name :scroll          :required false :default "auto"   :type "keyword"  :description "Sets both h-scroll and v-scroll at once. One of -
<code>:auto</code> - only show scroll bar(s) if the content is larger than the scroller.
<code>:on</code> - always show scroll bar(s).
<code>:off</code> - never show scroll bar(s). Content which is not in the bounds of the scroller can not be seen.
<code>:spill</code> - never show scroll bar(s). Content which is not in the bounds of the scroller spills all over the place."}
   {:name :h-scroll        :required false                   :type "keyword"  :description "see <code>:scroll</code>. Overrides that setting."}
   {:name :v-scroll        :required false                   :type "keyword"  :description "see <code>:scroll</code>. Overrides that setting."}
   {:name :width           :required false                   :type "string"   :description "Initial width."}
   {:name :min-width       :required false                   :type "string"   :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height      :required false                   :type "string"   :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :justify         :required false :default :start   :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:between</code> and <code>:around</code>."}
   {:name :align           :required false :default :stretch :type "keyword"  :description "one of <code>:start</code>, <code>:end</code>, <code>:center</code>, <code>:baseline</code> and <code>:stretch</code>."}
   {:name :align-self      :required false                   :type "boolean"  :description "use to override parent alignment setting for this component."}
   {:name :margin          :required false                   :type "string"   :description "a CSS margin style."}
   {:name :padding         :required false                   :type "string"   :description "a CSS padding style."}
   {:name :child           :required false                   :type "markup"   :description "a child component markup."}
   {:name :class           :required false                   :type "string"   :description "additional CSS classes required."}
   {:name :style           :required false                   :type "map"      :description "CSS styles to add or override."}
   {:name :attr            :required false                   :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def scroller-args
  (set (map :name scroller-args-desc)))

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
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height align-self margin padding child class style attr]
      :or   {size "auto"}
      :as   args}]
  {:pre [(validate-arguments scroller-args (keys args))]}
  (let [not-v-or-h (and (nil? v-scroll) (nil? h-scroll))
        scroll     (if (and (nil? scroll) not-v-or-h) :auto scroll)]
    (box-base :size       size
              :scroll     scroll
              :h-scroll   h-scroll
              :v-scroll   v-scroll
              :width      width
              :height     height
              :min-width  min-width
              :min-height min-height
              :align-self align-self
              :margin     margin
              :padding    padding
              :child      child
              :class      (str "rc-scroller " class)
              :style      style
              :attr        attr)))


;; ------------------------------------------------------------------------------------
;;  Component: border
;; ------------------------------------------------------------------------------------

(def border-args-desc
  [{:name :size         :required false :default "auto"                 :type "string"   :description "a Flex size. Refer flex-child-style."}
   {:name :width        :required false                                 :type "string"   :description "a CSS style describing the initial width."}
   {:name :height       :required false                                 :type "string"   :description "a CSS style describing the initial height."}
   {:name :min-width    :required false                                 :type "string"   :description "a CSS width style. The minimum width to which the box can shrink."}
   {:name :min-height   :required false                                 :type "string"   :description "a CSS height style. The minimum height to which the box can shrink."}
   {:name :margin       :required false                                 :type "string"   :description "a CSS margin style."}
   {:name :padding      :required false                                 :type "string"   :description "a CSS padding style."}
   {:name :border       :required false :default "1px solid lightgrey"  :type "string"   :description "a CSS border style. A convenience to describe all borders in one parameter."}
   {:name :l-border     :required false :default :border                :type "string"   :description "a CSS border style for the left border. Overrides <code>:border</code>."}
   {:name :r-border     :required false :default :boder                 :type "string"   :description "a CSS border style for the right border. Overrides <code>:border</code>."}
   {:name :t-border     :required false :default :border                :type "string"   :description "a CSS border style for the top border. Overrides <code>:border</code>."}
   {:name :b-border     :required false :default :boder                 :type "string"   :description "a CSS border style for the bottom. Overrides <code>:border</code>."}
   {:name :radius       :required false                                 :type "string"   :description "a CSS radius style eg.\"2px\"."}
   {:name :child        :required false                                 :type "markup"   :description "a child component markup."}
   {:name :class        :required false                                 :type "string"   :description "additional CSS classes required."}
   {:name :style        :required false                                 :type "map"      :description "CSS styles to add or override."}
   {:name :attr         :required false                                 :type "map"      :description "html attributes to add or override (:class/:style not allowed)."}])

(def border-args
  (set (map :name border-args-desc)))

(defn border
  "Returns markup which produces a border component.
   This is the way borders are added to boxes, in favour of adding the border attributes directly to the boxes themselves.
   border property syntax: '<border-width> || <border-style> || <color>'
    - border-width: thin, medium, thick or standard CSS size (e.g. 2px, 0.5em)
    - border-style: none, hidden, dotted, dashed, solid, double, groove, ridge, inset, outset
    - color:        standard CSS color (e.g. grey #88ffee)"
  [& {:keys [size width height min-width min-height margin padding border l-border r-border t-border b-border radius child class style attr]
      :or   {size "auto"}
      :as   args}]
  {:pre [(validate-arguments border-args (keys args))]}
  (let [no-border      (every? nil? [border l-border r-border t-border b-border])
        default-border "1px solid lightgrey"]
    (box-base :size        size
              :width       width
              :height      height
              :min-width   min-width
              :min-height  min-height
              :margin      margin
              :padding     padding
              :border      (if no-border default-border border)
              :l-border    l-border
              :r-border    r-border
              :t-border    t-border
              :b-border    b-border
              :radius      radius
              :child       child
              :class       (str "rc-border " class)
              :style       style
              :attr        attr)))
