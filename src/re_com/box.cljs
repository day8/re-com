(ns re-com.box
  (:require [clojure.string  :as    string]
            [re-com.validate :refer [justify-style? justify-options-list align-style? align-options-list
                                     scroll-style? scroll-options-list string-or-hiccup? css-style? html-attr?] :refer-macros [validate-args-macro]]))

(def debug false)


;; ------------------------------------------------------------------------------------
;;  Private Helper functions
;; ------------------------------------------------------------------------------------

(defn flex-child-style
  "Determines the value for the 'flex' attribute (which has grow, shrink and basis), based on the :size parameter.
   IMPORTANT: The term 'size' means width of the item in the case of flex-direction 'row' OR height of the item in the case of flex-direction 'column'.
   Flex property explanation:
    - grow    Integer ratio (used with other siblings) to determined how a flex item grows it's size if there is extra space to distribute. 0 for no growing.
    - shrink  Integer ratio (used with other siblings) to determined how a flex item shrinks it's size if space needs to be removed. 0 for no shrinking.
    - basis   Initial size (width, actually) of item before any growing or shrinking. Can be any size value, e.g. 60%, 100px, auto
              Note: auto will cause the initial size to be calculated to take up as much space as possible, in conjunction with it's siblings :flex settings.
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
    {:-webkit-flex flex
             :flex flex}))


(defn flex-flow-style
  "A cross-browser helper function to output flex-flow with all it's potential browser prefixes"
  [flex-flow]
  {:-webkit-flex-flow flex-flow
           :flex-flow flex-flow})

(defn justify-style
  "Determines the value for the flex 'justify-content' attribute.
   This parameter determines how children are aligned along the main axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#justify-content-property"
  [justify]
  (let [js (case justify
             :start   "flex-start"
             :end     "flex-end"
             :center  "center"
             :between "space-between"
             :around  "space-around")]
    {:-webkit-justify-content js
             :justify-content js}))


(defn align-style
  "Determines the value for the flex align type attributes.
   This parameter determines how children are aligned on the cross axis.
   The justify parameter is a keyword.
   Reference: http://www.w3.org/TR/css3-flexbox/#align-items-property"
  [attribute align]
  (let [attribute-wk (->> attribute name (str "-webkit-") keyword)
        as           (case align
                       :start    "flex-start"
                       :end      "flex-end"
                       :center   "center"
                       :baseline "baseline"
                       :stretch  "stretch")]
    {attribute-wk as
     attribute    as}))


(defn scroll-style
  "Determines the value for the 'overflow' attribute.
   The scroll parameter is a keyword.
   Because we're translating scroll into overflow, the keyword doesn't appear to match the attribute value"
  [attribute scroll]
  {attribute (case scroll
                  :auto  "auto"
                  :off   "hidden"
                  :on    "scroll"
                  :spill "visible")})


;; ------------------------------------------------------------------------------------
;;  Private Component: box-base (debug color: lightblue)
;; ------------------------------------------------------------------------------------

(defn- box-base
  "This should generally NOT be used as it is the basis for the box, scroller and border components"
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height max-width max-height justify align align-self
             margin padding border l-border r-border t-border b-border radius bk-color child class-name class style attr]}]
  (let [s (merge
            (flex-flow-style "inherit")
            (flex-child-style size)
            (when scroll      (scroll-style   :overflow scroll))
            (when h-scroll    (scroll-style   :overflow-x h-scroll))
            (when v-scroll    (scroll-style   :overflow-y v-scroll))
            (when width       {:width         width})
            (when height      {:height        height})
            (when min-width   {:min-width     min-width})
            (when min-height  {:min-height    min-height})
            (when max-width   {:max-width     max-width})
            (when max-height  {:max-height    max-height})
            (when justify     (justify-style  justify))
            (when align       (align-style    :align-items align))
            (when align-self  (align-style    :align-self align-self))
            (when margin      {:margin        margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
            (when padding     {:padding       padding})
            (when border      {:border        border})
            (when l-border    {:border-left   l-border})
            (when r-border    {:border-right  r-border})
            (when t-border    {:border-top    t-border})
            (when b-border    {:border-bottom b-border})
            (when radius      {:border-radius radius})
            (if bk-color
              {:background-color bk-color}
              (if debug {:background-color "lightblue"} {}))
            style)]
    [:div
     (merge
       {:class (str class-name "display-flex " class) :style s}
       attr)
     child]))


;; ------------------------------------------------------------------------------------
;;  Component: gap (debug color: chocolate)
;; ------------------------------------------------------------------------------------

(def gap-args-desc
  [{:name :size   :required true  :type "string"        :validate-fn string?    :description "the length of the whitespace.  Typically, an absolute CSS length like 10px or 10em, but can be a stretchy proportional amount like 2"}
   {:name :width  :required false :type "string"        :validate-fn string?    :description "a CSS width style"}
   {:name :height :required false :type "string"        :validate-fn string?    :description "a CSS height style"}
   {:name :class  :required false :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
   {:name :style  :required false :type "CSS style map" :validate-fn css-style? :description "CSS styles to add or override"}
   {:name :attr   :required false :type "HTML attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn gap
  "Returns a component which produces a gap between children in a v-box/h-box along the main axis"
  [& {:keys [size width height class style attr]
      :as   args}]
  {:pre [(validate-args-macro gap-args-desc args "gap")]}
  (let [s (merge
            (when size   (flex-child-style size))
            (when width  {:width width})
            (when height {:height height})
            (when debug  {:background-color "chocolate"})
            style)]
    [:div
     (merge
       {:class (str "rc-gap " class) :style s}
       attr)]))


;; ------------------------------------------------------------------------------------
;;  Component: line
;; ------------------------------------------------------------------------------------

(def line-args-desc
  [{:name :size  :required false :default "1px"       :type "string"        :validate-fn string?    :description "a CSS style for the thickness of the line. Usually px, % or em"}
   {:name :color :required false :default "lightgray" :type "string"        :validate-fn string?    :description "a CSS color"}
   {:name :class :required false                      :type "string"        :validate-fn string?    :description "CSS class names, space separated"}
   {:name :style :required false                      :type "CSS style map" :validate-fn css-style? :description "CSS styles to add or override"}
   {:name :attr  :required false                      :type "HTML attr map" :validate-fn html-attr? :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn line
  "Returns a component which produces a line between children in a v-box/h-box along the main axis.
   Specify size in pixels and a stancard CSS color. Defaults to a 1px lightgray line"
  [& {:keys [size color class style attr]
      :or   {size "1px" color "lightgray"}
      :as   args}]
  {:pre [(validate-args-macro line-args-desc args "line")]}
  (let [s (merge
            (flex-child-style (str "0 0 " size))
            {:background-color color}
            style)]
    [:div
     (merge
       {:class (str "rc-line " class) :style s}
       attr)]))


;; ------------------------------------------------------------------------------------
;;  Component: h-box (debug color: gold)
;; ------------------------------------------------------------------------------------

(def h-box-args-desc
  [{:name :children   :required true                    :type "vector"        :validate-fn sequential?    :description "a vector (or list) of components"}
   {:name :size       :required false :default "none"   :type "string"        :validate-fn string?        :description [:span "equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :width      :required false                   :type "string"        :validate-fn string?        :description "a CSS width style"}
   {:name :height     :required false                   :type "string"        :validate-fn string?        :description "a CSS height style"}
   {:name :min-width  :required false                   :type "string"        :validate-fn string?        :description "a CSS width style. The minimum width to which the box can shrink"}
   {:name :min-height :required false                   :type "string"        :validate-fn string?        :description "a CSS height style. The minimum height to which the box can shrink"}
   {:name :max-width  :required false                   :type "string"        :validate-fn string?        :description "a CSS width style. The maximum width to which the box can grow"}
   {:name :max-height :required false                   :type "string"        :validate-fn string?        :description "a CSS height style. The maximum height to which the box can grow"}
   {:name :justify    :required false :default :start   :type "keyword"       :validate-fn justify-style? :description [:span "equivalent to CSS style " [:span.bold "justify-content"] "." [:br] "One of " justify-options-list]}
   {:name :align      :required false :default :stretch :type "keyword"       :validate-fn align-style?   :description [:span "equivalent to CSS style " [:span.bold "align-items"]  "." [:br] " One of " align-options-list]}
   {:name :align-self :required false                   :type "keyword"       :validate-fn align-style?   :description [:span "equivalent to CSS style " [:span.bold "align-self"] "." [:br]  "Used when a child must override the parent's align-items setting."]}
   {:name :margin     :required false                   :type "string"        :validate-fn string?        :description "a CSS margin style"}
   {:name :padding    :required false                   :type "string"        :validate-fn string?        :description "a CSS padding style"}
   {:name :gap        :required false                   :type "string"        :validate-fn string?        :description "the amount of whitespace to put between each child. Typically, an absolute CSS length like 10px or 10em, but can be a stretchy proportional amount like 2"}
   {:name :class      :required false                   :type "string"        :validate-fn string?        :description "CSS class names, space separated"}
   {:name :style      :required false                   :type "CSS style map" :validate-fn css-style?     :description "CSS styles to add or override"}
   {:name :attr       :required false                   :type "HTML attr map" :validate-fn html-attr?     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn h-box
  "Returns hiccup which produces a horizontal box.
   It's primary role is to act as a container for components and lays it's children from left to right.
   By default, it also acts as a child under it's parent"
  [& {:keys [size width height min-width min-height max-width max-height justify align align-self margin padding gap children class style attr]
      :or   {size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(validate-args-macro h-box-args-desc args "h-box")]}
  (let [s        (merge
                   (flex-flow-style "row nowrap")
                   (flex-child-style size)
                   (when width      {:width      width})
                   (when height     {:height     height})
                   (when min-width  {:min-width  min-width})
                   (when min-height {:min-height min-height})
                   (when max-width  {:max-width  max-width})
                   (when max-height {:max-height max-height})
                   (justify-style justify)
                   (align-style :align-items align)
                   (when align-self (align-style :align-self align-self))
                   (when margin     {:margin     margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
                   (when padding    {:padding    padding})
                   (when debug      {:background-color "gold"})
                   style)
        gap-form (when gap [re-com.box/gap
                            :size  gap
                            :width gap]) ;; TODO: required to get around a Chrome bug: https://code.google.com/p/chromium/issues/detail?id=423112. Remove once fixed.
        children (if gap
                   (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                   children)]
    (into [:div
           (merge
             {:class (str "rc-h-box display-flex " class) :style s}
             attr)]
          children)))


;; ------------------------------------------------------------------------------------
;;  Component: v-box (debug color: antiquewhite)
;; ------------------------------------------------------------------------------------

(def v-box-args-desc
  [{:name :children   :required true                    :type "vector"        :validate-fn sequential?    :description "a vector (or list) of components"}
   {:name :size       :required false :default "none"   :type "string"        :validate-fn string?        :description [:span "equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :width      :required false                   :type "string"        :validate-fn string?        :description "a CSS width style"}
   {:name :height     :required false                   :type "string"        :validate-fn string?        :description "a CSS height style"}
   {:name :min-width  :required false                   :type "string"        :validate-fn string?        :description "a CSS width style. The minimum width to which the box can shrink"}
   {:name :min-height :required false                   :type "string"        :validate-fn string?        :description "a CSS height style. The minimum height to which the box can shrink"}
   {:name :max-width  :required false                   :type "string"        :validate-fn string?        :description "a CSS width style. The maximum width to which the box can grow"}
   {:name :max-height :required false                   :type "string"        :validate-fn string?        :description "a CSS height style. The maximum height to which the box can grow"}
   {:name :justify    :required false :default :start   :type "keyword"       :validate-fn justify-style? :description [:span "equivalent to CSS style " [:span.bold "justify-content"] "." [:br] "One of " justify-options-list]}
   {:name :align      :required false :default :stretch :type "keyword"       :validate-fn align-style?   :description [:span "equivalent to CSS style " [:span.bold "align-items"]  "." [:br] " One of " align-options-list]}
   {:name :align-self :required false                   :type "keyword"       :validate-fn align-style?   :description [:span "equivalent to CSS style " [:span.bold "align-self"] "." [:br]  "Used when a child must override the parent's align-items setting."]}
   {:name :margin     :required false                   :type "string"        :validate-fn string?        :description "a CSS margin style"}
   {:name :padding    :required false                   :type "string"        :validate-fn string?        :description "a CSS padding style"}
   {:name :gap        :required false                   :type "string"        :validate-fn string?        :description "the amount of whitespace to put between each child. Typically, an absolute CSS length like 10px or 10em, but can be a stretchy proportional amount like 2"}
   {:name :class      :required false                   :type "string"        :validate-fn string?        :description "CSS class names, space separated"}
   {:name :style      :required false                   :type "CSS style map" :validate-fn css-style?     :description "CSS styles to add or override"}
   {:name :attr       :required false                   :type "HTML attr map" :validate-fn html-attr?     :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn v-box
  "Returns hiccup which produces a vertical box.
   It's primary role is to act as a container for components and lays it's children from top to bottom.
   By default, it also acts as a child under it's parent"
  [& {:keys [size width height min-width min-height max-width max-height justify align align-self margin padding gap children class style attr]
      :or   {size "none" justify :start align :stretch}
      :as   args}]
  {:pre [(validate-args-macro v-box-args-desc args "v-box")]}
  (let [s        (merge
                   (flex-flow-style  "column nowrap")
                   (flex-child-style size)
                   (when width       {:width      width})
                   (when height      {:height     height})
                   (when min-width   {:min-width  min-width})
                   (when min-height  {:min-height min-height})
                   (when max-width   {:max-width  max-width})
                   (when max-height  {:max-height max-height})
                   (justify-style    justify)
                   (align-style      :align-items align)
                   (when align-self  (align-style :align-self align-self))
                   (when margin      {:margin     margin})       ;; margin and padding: "all" OR "top&bottom right&left" OR "top right bottom left"
                   (when padding     {:padding    padding})
                   (when debug       {:background-color "antiquewhite"})
                   style)
        gap-form (when gap [re-com.box/gap
                            :size   gap
                            :height gap]) ;; TODO: required to get around a Chrome bug: https://code.google.com/p/chromium/issues/detail?id=423112. Remove once fixed.
        children (if gap
                   (interpose gap-form (filter identity children)) ;; filter is to remove possible nils so we don't add unwanted gaps
                   children)]
    (into [:div
           (merge
             {:class (str "rc-v-box display-flex " class) :style s}
             attr)]
          children)))


;; ------------------------------------------------------------------------------------
;;  Component: box
;; ------------------------------------------------------------------------------------

(def box-args-desc
  [{:name :child      :required true                    :type "string | hiccup" :validate-fn string-or-hiccup? :description "a component (or string)"}
   {:name :size       :required false :default "none"   :type "string"          :validate-fn string?           :description [:span "equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :width      :required false                   :type "string"          :validate-fn string?           :description "a CSS width style"}
   {:name :height     :required false                   :type "string"          :validate-fn string?           :description "a CSS height style"}
   {:name :min-width  :required false                   :type "string"          :validate-fn string?           :description "a CSS width style. The minimum width to which the box can shrink"}
   {:name :min-height :required false                   :type "string"          :validate-fn string?           :description "a CSS height style. The minimum height to which the box can shrink"}
   {:name :max-width  :required false                   :type "string"          :validate-fn string?          :description "a CSS width style. The maximum width to which the box can grow"}
   {:name :max-height :required false                   :type "string"          :validate-fn string?          :description "a CSS height style. The maximum height to which the box can grow"}
   {:name :justify    :required false :default :start   :type "keyword"         :validate-fn justify-style?    :description [:span "equivalent to CSS style " [:span.bold "justify-content"] "." [:br] "One of " justify-options-list]}
   {:name :align      :required false :default :stretch :type "keyword"         :validate-fn align-style?      :description [:span "equivalent to CSS style " [:span.bold "align-items"]  "." [:br] " One of " align-options-list]}
   {:name :align-self :required false                   :type "keyword"         :validate-fn align-style?      :description [:span "equivalent to CSS style " [:span.bold "align-self"] "." [:br]  "Used when a child must override the parent's align-items setting."]}
   {:name :margin     :required false                   :type "string"          :validate-fn string?           :description "a CSS margin style"}
   {:name :padding    :required false                   :type "string"          :validate-fn string?           :description "a CSS padding style"}
   {:name :class      :required false                   :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style      :required false                   :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr       :required false                   :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn box
  "Returns hiccup which produces a box, which is generally used as a child of a v-box or an h-box.
   By default, it also acts as a container for further child compenents, or another h-box or v-box"
  [& {:keys [size width height min-width min-height max-width max-height justify align align-self margin padding child class style attr]
      :or   {size "none"}
      :as   args}]
  {:pre [(validate-args-macro box-args-desc args "box")]}
  (box-base :size        size
            :width       width
            :height      height
            :min-width   min-width
            :min-height  min-height
            :max-width   max-width
            :max-height  max-height
            :justify     justify
            :align       align
            :align-self  align-self
            :margin      margin
            :padding     padding
            :child       child
            :class-name  "rc-box "
            :class       class
            :style       style
            :attr        attr))


;; ------------------------------------------------------------------------------------
;;  Component: scroller
;; ------------------------------------------------------------------------------------

(def scroller-args-desc
  [{:name :child      :required true                    :type "string | hiccup" :validate-fn string-or-hiccup? :description "a component (or string)"}
   {:name :size       :required false :default "auto"   :type "string"          :validate-fn string?           :description [:span "equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :scroll     :required false :default "auto"   :type "keyword"         :validate-fn scroll-style?     :description [:span "Sets both h-scroll and v-scroll at once: " [:br]
                                                                                                                             [:code ":auto"] ": only show scroll bar(s) if the content is larger than the scroller" [:br]
                                                                                                                             [:code ":on"] ": always show scroll bars" [:br]
                                                                                                                             [:code ":off"] ": never show scroll bar(s). Content which is not in the bounds of the scroller can not be seen" [:br]
                                                                                                                             [:code ":spill"] ": never show scroll bar(s). Content which is not in the bounds of the scroller spills all over the place"]}
   {:name :h-scroll   :required false                   :type "keyword"         :validate-fn scroll-style?     :description [:span "see " [:code ":scroll"] ". Overrides that setting"]}
   {:name :v-scroll   :required false                   :type "keyword"         :validate-fn scroll-style?     :description [:span "see " [:code ":scroll"] ". Overrides that setting"]}
   {:name :width      :required false                   :type "string"          :validate-fn string?           :description "initial width"}
   {:name :height     :required false                   :type "string"          :validate-fn string?           :description "initial height"}
   {:name :min-width  :required false                   :type "string"          :validate-fn string?           :description "a CSS width style. The minimum width to which the box can shrink"}
   {:name :min-height :required false                   :type "string"          :validate-fn string?           :description "a CSS height style. The minimum height to which the box can shrink"}
   {:name :max-width  :required false                   :type "string"          :validate-fn string?           :description "a CSS width style. The maximum width to which the box can grow"}
   {:name :max-height :required false                   :type "string"          :validate-fn string?           :description "a CSS height style. The maximum height to which the box can grow"}
   {:name :justify    :required false :default :start   :type "keyword"         :validate-fn justify-style?    :description [:span "equivalent to CSS style " [:span.bold "justify-content"] "." [:br] "One of " justify-options-list]}
   {:name :align      :required false :default :stretch :type "keyword"         :validate-fn align-style?      :description [:span "equivalent to CSS style " [:span.bold "align-items"]  "." [:br] " One of " align-options-list]}
   {:name :align-self :required false                   :type "keyword"         :validate-fn align-style?      :description [:span "equivalent to CSS style " [:span.bold "align-self"] "." [:br]  "Used when a child must override the parent's align-items setting."]}
   {:name :margin     :required false                   :type "string"          :validate-fn string?           :description "a CSS margin style"}
   {:name :padding    :required false                   :type "string"          :validate-fn string?           :description "a CSS padding style"}
   {:name :class      :required false                   :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style      :required false                   :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr       :required false                   :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn scroller
  "Returns hiccup which produces a scoller component.
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
   Note:   If scroll is set, then setting h-scroll or v-scroll overrides the scroll value"
  [& {:keys [size scroll h-scroll v-scroll width height min-width min-height max-width max-height justify align align-self margin padding child class style attr]
      :or   {size "auto"}
      :as   args}]
  {:pre [(validate-args-macro scroller-args-desc args "scroller")]}
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
              :max-width  max-width
              :max-height max-height
              :justify    justify
              :align      align
              :align-self align-self
              :margin     margin
              :padding    padding
              :child      child
              :class-name "rc-scroller "
              :class      class
              :style      style
              :attr       attr)))


;; ------------------------------------------------------------------------------------
;;  Component: border
;; ------------------------------------------------------------------------------------

(def border-args-desc
  [{:name :child      :required true                                 :type "string | hiccup" :validate-fn string-or-hiccup? :description "a component (or string)"}
   {:name :border     :required false :default "1px solid lightgrey" :type "string"          :validate-fn string?           :description "a CSS border style. A convenience to describe all borders in one parameter"}
   {:name :l-border   :required false                                :type "string"          :validate-fn string?           :description [:span "a CSS border style for the left border. Overrides " [:code ":border"]]}
   {:name :r-border   :required false                                :type "string"          :validate-fn string?           :description [:span "a CSS border style for the right border. Overrides " [:code ":border"]]}
   {:name :t-border   :required false                                :type "string"          :validate-fn string?           :description [:span "a CSS border style for the top border. Overrides " [:code ":border"]]}
   {:name :b-border   :required false                                :type "string"          :validate-fn string?           :description [:span "a CSS border style for the bottom. Overrides " [:code ":border"]]}
   {:name :radius     :required false                                :type "string"          :validate-fn string?           :description "a CSS radius style eg.\"2px\""}
   {:name :size       :required false :default "none"                :type "string"          :validate-fn string?           :description [:span "equivalent to CSS style " [:span.bold "flex"] "." [:br]  "Examples: " [:code "initial"] ", " [:code "auto"] ", " [:code "none"]", " [:code "100px"] ", " [:code "2"] " or a generic triple of " [:code "grow shrink basis"]]}
   {:name :width      :required false                                :type "string"          :validate-fn string?           :description "a CSS style describing the initial width"}
   {:name :height     :required false                                :type "string"          :validate-fn string?           :description "a CSS style describing the initial height"}
   {:name :min-width  :required false                                :type "string"          :validate-fn string?           :description "a CSS width style. The minimum width to which the box can shrink"}
   {:name :min-height :required false                                :type "string"          :validate-fn string?           :description "a CSS height style. The minimum height to which the box can shrink"}
   {:name :max-width  :required false                                :type "string"          :validate-fn string?           :description "a CSS width style. The maximum width to which the box can grow"}
   {:name :max-height :required false                                :type "string"          :validate-fn string?           :description "a CSS height style. The maximum height to which the box can grow"}
   {:name :margin     :required false                                :type "string"          :validate-fn string?           :description "a CSS margin style"}
   {:name :padding    :required false                                :type "string"          :validate-fn string?           :description "a CSS padding style"}
   {:name :class      :required false                                :type "string"          :validate-fn string?           :description "CSS class names, space separated"}
   {:name :style      :required false                                :type "CSS style map"   :validate-fn css-style?        :description "CSS styles to add or override"}
   {:name :attr       :required false                                :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed"]}])

(defn border
  "Returns hiccup which produces a border component.
   This is the way borders are added to boxes, in favour of adding the border attributes directly to the boxes themselves.
   border property syntax: '<border-width> || <border-style> || <color>'
    - border-width: thin, medium, thick or standard CSS size (e.g. 2px, 0.5em)
    - border-style: none, hidden, dotted, dashed, solid, double, groove, ridge, inset, outset
    - color:        standard CSS color (e.g. grey #88ffee)"
  [& {:keys [size width height min-width min-height max-width max-height margin padding border l-border r-border t-border b-border radius child class style attr]
      :or   {size "none"}
      :as   args}]
  {:pre [(validate-args-macro border-args-desc args "border")]}
  (let [no-border      (every? nil? [border l-border r-border t-border b-border])
        default-border "1px solid lightgrey"]
    (box-base :size        size
              :width       width
              :height      height
              :min-width   min-width
              :min-height  min-height
              :max-width   max-width
              :max-height  max-height
              :margin      margin
              :padding     padding
              :border      (if no-border default-border border)
              :l-border    l-border
              :r-border    r-border
              :t-border    t-border
              :b-border    b-border
              :radius      radius
              :child       child
              :class-name  "rc-border "
              :class       class
              :style       style
              :attr        attr)))