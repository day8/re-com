(ns re-com.popover
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util     :refer [get-element-by-id px deref-or-value sum-scroll-offsets]]
            [re-com.box      :refer [h-box v-box flex-child-style flex-flow-style align-style]]
            [re-com.validate :refer [position? position-options-list popover-status-type? popover-status-types-list number-or-string?
                                     string-or-hiccup? string-or-atom? vector-of-maps? css-style? html-attr?] :refer-macros [validate-args-macro]]
            [clojure.string  :as    string]
            [reagent.core    :as    reagent]))


(defn point
  [x y]
  (str x "," y " "))


(defn- split-keyword
  "I return the vector of the two keywords formed by splitting
   another keyword 'kw' on an internal delimiter (usually '-').
   (split-keyword  :above-left  \"-\")
   =>  [:above :left]"
  [kw delimiter] ;; TODO: Move to util?
  (let [keywords (string/split (str kw) (re-pattern (str "[" delimiter ":]")))]
    [(keyword (keywords 1)) (keyword (keywords 2))]))


(defn- close-button
  "A button with a big X in it, placed to the right of the popup"
  [showing? close-callback style]
  ;; Can't use [button] because [button] already uses [popover] which would be a circular dependency.
  [:button
   {:on-click (handler-fn
                (if close-callback
                  (close-callback)
                  (reset! showing? false)))
    :class    "close"
    :style    (merge {:font-size "34px"
                      :position  "absolute"
                      :top       "0px"
                      :right     "0px"}
                     style)}
   [:i {:class "md-close"}]])


(defn- calc-popover-pos
  [pop-orient p-width p-height pop-offset]
  (let [popover-left   (case pop-orient
                         :left           "initial" ;; TODO: Ultimately remove this (must have NO :left which is in Bootstrap .popover class)
                         :right          "100%"
                         (:above :below) (px (if pop-offset pop-offset (/ p-width 2)) :negative))
        popover-top    (case pop-orient
                         (:left :right)  (px (if pop-offset pop-offset (/ p-height 2)) :negative)
                         :above          "initial"
                         :below          "100%")
        popover-right  (case pop-orient
                         :left  (px 10) ;; "100%" TODO: Work out why we need 10px instead of 100%
                         :right nil
                         :above nil
                         :below nil)
        popover-bottom (case pop-orient
                         :left  nil
                         :right nil
                         :above (px 10) ;; "100%" TODO: Work out why we need 10px instead of 100%
                         :below nil)]
    {:left popover-left :top popover-top :right popover-right :bottom popover-bottom}))


(defn- popover-arrow
  [orientation pop-offset arrow-length arrow-width grey-arrow? no-border? popover-color]
  (let [half-arrow-width (/ arrow-width 2)
        arrow-shape {:left  (str (point 0 0)            (point arrow-length half-arrow-width) (point 0 arrow-width))
                     :right (str (point arrow-length 0) (point 0 half-arrow-width)            (point arrow-length arrow-width))
                     :above (str (point 0 0)            (point half-arrow-width arrow-length) (point arrow-width 0))
                     :below (str (point 0 arrow-length) (point half-arrow-width 0)            (point arrow-width arrow-length))}]
    [:svg {:class "popover-arrow"
           :style {:position "absolute"
                   (case orientation ;; Connect arrow to edge of popover
                     :left  :right
                     :right :left
                     :above :bottom
                     :below :top) (px arrow-length :negative)

                   (case orientation ;; Position the arrow at the top/left, center or bottom/right of the popover
                     (:left  :right) :top
                     (:above :below) :left) (if (nil? @pop-offset) "50%" (px @pop-offset))

                   (case orientation ;; Adjust the arrow position so it's center is attached to the desired position set above
                     (:left  :right) :margin-top
                     (:above :below) :margin-left) (px half-arrow-width :negative)

                   :width (px (case orientation ;; Arrow is rendered in a rectangle so choose the correct edge length
                                (:left  :right) arrow-length
                                (:above :below) arrow-width))

                   :height (px (case orientation ;; Same as :width comment above
                                 (:left  :right) arrow-width
                                 (:above :below) arrow-length))}}
     [:polyline {:points (arrow-shape orientation)
                 :style {:fill (if popover-color
                                 popover-color
                                 (if grey-arrow? "#f7f7f7" "white"))
                         :stroke (when-not no-border? "rgba(0, 0, 0, .2)")
                         :stroke-width "1"}}]]))

;;--------------------------------------------------------------------------------------------------
;; Component: backdrop
;;--------------------------------------------------------------------------------------------------

(def backdrop-args-desc
  [{:name :opacity  :required false :default 0.0 :type "double | string" :validate-fn number-or-string? :description [:span "opacity of backdrop from:" [:br] "0.0 (transparent) to 1.0 (opaque)"]}
   {:name :on-click :required false              :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the backdrop is clicked"}])

(defn- backdrop
  "Renders a backdrop dive which fills the entire page and responds to clicks on it. Can also specify how tranparent it should be"
  [& {:keys [opacity on-click] :as args}]
  {:pre [(validate-args-macro backdrop-args-desc args "backdrop")]}
  [:div {:class     "rc-backdrop noselect"
         :style    {:position         "fixed"
                    :left             "0px"
                    :top              "0px"
                    :width            "100%"
                    :height           "100%"
                    :background-color "black"
                    :opacity          (if opacity opacity 0.0)}
         :on-click (handler-fn (on-click))}])


;;--------------------------------------------------------------------------------------------------
;; Component: popover-border
;;--------------------------------------------------------------------------------------------------

(defn next-even-integer
  [num]
  (-> num inc (/ 2) int (* 2)))

(defn calc-pop-offset
  [arrow-pos p-width p-height]
  (case arrow-pos
    :center nil
    :right  20
    :below  20
    :left   (if p-width (- p-width 25) p-width)
    :above  (if p-height (- p-height 25) p-height)))

(def popover-border-args-desc
  [{:name :children       :required true                        :type "vector"           :validate-fn sequential?       :description "a vector of component markups"}
   {:name :position       :required false :default :right-below :type "keyword"          :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :width          :required false                       :type "string"           :validate-fn string?           :description "a CSS style describing the popover width"}
   {:name :height         :required false :default "auto"       :type "string"           :validate-fn string?           :description "a CSS style describing the popover height"}
   {:name :popover-color  :required false :default "white"      :type "string"           :validate-fn string?           :description "fill color of the popover"}
   {:name :arrow-length   :required false :default 11           :type "integer | string" :validate-fn number-or-string? :description "the length in pixels of the arrow (from pointy part to middle of arrow base)"}
   {:name :arrow-width    :required false :default 22           :type "integer | string" :validate-fn number-or-string? :description "the width in pixels of arrow base"}
   {:name :padding        :required false                       :type "string"           :validate-fn string?           :description "a CSS style which overrides the inner padding of the popover"}
   {:name :margin-left    :required false                       :type "string"           :validate-fn string?           :description "a CSS style describing the horiztonal offset from anchor after position"}
   {:name :margin-top     :required false                       :type "string"           :validate-fn string?           :description "a CSS style describing the vertical offset from anchor after position"}
   {:name :tooltip-style? :required false :default false        :type "boolean"                                         :description "setup popover styles for a tooltip"}
   {:name :title          :required false                       :type "string | markup"                                 :description "describes a title"}])

(defn popover-border
  "Renders an element or control along with a Bootstrap popover"
  [& {:keys [children position width height popover-color arrow-length arrow-width padding margin-left margin-top tooltip-style? title]
      :or {arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(validate-args-macro popover-border-args-desc args "popover-border")]}
  (let [rendered-once           (reagent/atom false)
        pop-id                  (gensym "popover-")
        [orientation arrow-pos] (split-keyword (if position position :right-below) "-")
        grey-arrow?             (and title (or (= orientation :below) (= arrow-pos :below)))
        p-width                 (reagent/atom 0)
        p-height                (reagent/atom 0)
        pop-offset              (reagent/atom 0)]
    (reagent/create-class
      {:component-did-mount
       (fn []
         (reset! rendered-once true))

       :component-did-update
       (fn []
         (let [popover-elem   (get-element-by-id pop-id)]
           (reset! p-width    (if popover-elem (next-even-integer (.-clientWidth  popover-elem)) 0)) ;; next-even-integer required to avoid wiggling popovers (width/height appears to prefer being even and toggles without this call)
           (reset! p-height   (if popover-elem (next-even-integer (.-clientHeight popover-elem)) 0))
           (reset! pop-offset (calc-pop-offset arrow-pos @p-width @p-height))))

       :component-function
       (fn
         [& {:keys [children position width height popover-color arrow-length arrow-width padding margin-left margin-top tooltip-style? title]
             :or {arrow-length 11 arrow-width 22}
             :as args}]
         {:pre [(validate-args-macro popover-border-args-desc args "popover-border")]}
         (let [popover-elem   (get-element-by-id pop-id)]
           (reset! p-width    (if popover-elem (next-even-integer (.-clientWidth  popover-elem)) 0)) ;; TODO: Duplicate from above but needs to be calculated here to prevent an annoying flicker (so make it a fn)
           (reset! p-height   (if popover-elem (next-even-integer (.-clientHeight popover-elem)) 0))
           (reset! pop-offset (calc-pop-offset arrow-pos @p-width @p-height))
           [:div.popover.fade.in
            {:id pop-id
             :class (case orientation :left "left" :right "right" :above "top" :below "bottom")
             :style (merge (if @rendered-once
                             (when pop-id (calc-popover-pos orientation @p-width @p-height @pop-offset))
                             {:top "-10000px" :left "-10000px"})
                           (if width  {:width  width})
                           (if height {:height height})
                           (if popover-color {:background-color popover-color})
                           (when tooltip-style?
                             {:border-radius "4px"
                              :box-shadow    "none"
                              :border        "none"})

                           ;; The popover point is zero width, therefore its absolute children will consider this width when deciding their
                           ;; natural size and in particular, how they natually wrap text. The right hand size of the popover is used as a
                           ;; text wrapping point so it will wrap, depending on where the child is positioned. The margin is also taken into
                           ;; consideration for this point so below, we set the margins to negative a lot to prevent
                           ;; this annoying wrapping phenomenon.
                           (case orientation
                             :left                  {:margin-left  "-2000px"}
                             (:right :above :below) {:margin-right "-2000px"})
                           ;; optional override offsets
                           (when margin-left {:margin-left margin-left})
                           (when margin-top  {:margin-top  margin-top})

                           ;; make it visible and turn off Bootstrap max-width and remove Bootstrap padding which adds an internal white border
                           {:display   "block"
                            :max-width "none"
                            :padding   (px 0)})}
            [popover-arrow orientation pop-offset arrow-length arrow-width grey-arrow? tooltip-style? popover-color]
            (when title title)
            (into [:div.popover-content {:style {:padding padding}}] children)]))})))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-title
;;--------------------------------------------------------------------------------------------------

(def popover-title-args-desc
  [{:name :showing?       :required true                 :type "boolean atom"                                   :description "an atom. When the value is true, the popover shows."}
   {:name :title          :required false                :type "string | hiccup" :validate-fn string-or-hiccup? :description "describes the title of the popover. Default font size is 18px to make it stand out"}
   {:name :close-button?  :required false  :default true :type "boolean"                                        :description "when true, displays the close button"}
   {:name :close-callback :required false                :type "-> nil"          :validate-fn fn?               :description [:span "a function which takes no params and returns nothing. Called when the close button is pressed. Not required if " [:code ":showing?"] " atom passed in OR " [:code ":close-button?"] " is set to false"]}])

(defn- popover-title
  "Renders a title at the top of a popover with an optional close button on the far right"
  [& {:keys [title showing? close-button? close-callback]
      :as args}]
  {:pre [(validate-args-macro popover-title-args-desc args "popover-title")]}
  (assert (or ((complement nil?) showing?) ((complement nil?) close-callback)) "Must specify either showing? OR close-callback")
  (let [close-button? (if (nil? close-button?) true close-button?)]
    [:h3.popover-title {:style (merge (flex-child-style "inherit")
                                      {:font-size "18px"})}
     [h-box
      :justify  :between
      :align    :center
      :children [title
                 (when close-button? [close-button showing? close-callback])]]]))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-content-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-content-wrapper-args-desc
  [{:name :showing?         :required true   :default false        :type "boolean atom"                                    :description "an atom. When the value is true, the popover shows."}
   {:name :position         :required true   :default :right-below :type "keyword"          :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :no-clip?         :required false  :default false        :type "boolean"                                         :description "when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped. By passing true for this parameter, re-com will use a different CSS method to show the popover. This method is slightly inferior because the popover can't track the anchor if it is repositioned"}
   {:name :width            :required false                        :type "string"           :validate-fn string?           :description "a CSS style representing the popover width"}
   {:name :height           :required false                        :type "string"           :validate-fn string?           :description "a CSS style representing the popover height"}
   {:name :backdrop-opacity :required false  :default 0.0          :type "double | string"  :validate-fn number-or-string? :description "indicates the opacity of the backdrop where 0.0=transparent, 1.0=opaque"}
   {:name :on-cancel        :required false                        :type "-> nil"           :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the popover is cancelled (e.g. user clicks away)"}
   {:name :title            :required false                        :type "string | hiccup"  :validate-fn string-or-hiccup? :description "describes the title of the popover. The default font size is 18px to make it stand out"}
   {:name :close-button?    :required false  :default true         :type "boolean"                                         :description "when true, displays the close button"}
   {:name :body             :required false                        :type "string | hiccup"  :validate-fn string-or-hiccup? :description "describes the popover body. Must be a single component"}
   {:name :tooltip-style?   :required false  :default false        :type "boolean"                                         :description "setup popover styles for a tooltip"}
   {:name :popover-color    :required false  :default "white"      :type "string"           :validate-fn string?           :description "fill color of the popover"}
   {:name :arrow-length     :required false  :default 11           :type "integer | string" :validate-fn number-or-string? :description "the length in pixels of the arrow (from pointy part to middle of arrow base)"}
   {:name :arrow-width      :required false  :default 22           :type "integer | string" :validate-fn number-or-string? :description "the width in pixels of arrow base"}
   {:name :padding          :required false                        :type "string"           :validate-fn string?           :description "a CSS style which overrides the inner padding of the popover"}
   {:name :style            :required false                        :type "CSS style map"    :validate-fn css-style?        :description "override component style(s) with a style map, only use in case of emergency"}])

(defn popover-content-wrapper
  "Abstracts several components to handle the 90% of cases for general popovers and dialog boxes"
  [& {:keys [showing? position no-clip? width height backdrop-opacity on-cancel title close-button? body tooltip-style? popover-color arrow-length arrow-width padding style]
      :or {arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(validate-args-macro popover-content-wrapper-args-desc args "popover-content-wrapper")]}
  (assert ((complement nil?) showing?) "Must specify a showing? atom")
  (let [left-offset (reagent/atom 0)
        top-offset  (reagent/atom 0)]
    (reagent/create-class
      {:component-did-mount
       (fn [this]
         (when no-clip?
           (let [node               (reagent/dom-node this)
                 offsets            (sum-scroll-offsets node)
                 popover-point-node (.-parentNode node)                  ;; Get reference to rc-popover-point node
                 point-left         (.-offsetLeft popover-point-node)    ;; offsetTop/Left is the viewport pixel offset of the point we want to point to (ignoring scrolls)
                 point-top          (.-offsetTop  popover-point-node)]
             (reset! left-offset (- point-left (:left offsets)))
             (reset! top-offset  (- point-top  (:top  offsets))))))

       :component-function
       (fn
         [& {:keys [showing? position no-clip? width height backdrop-opacity on-cancel title close-button? body tooltip-style? popover-color arrow-length arrow-width padding style]
             :or {arrow-length 11 arrow-width 22}
             :as args}]
         {:pre [(validate-args-macro popover-content-wrapper-args-desc args "popover-content-wrapper")]}
         [:div
          {:class "popover-content-wrapper"
           :style (merge (flex-child-style "inherit")
                         (when no-clip? {:position "fixed"
                                         :left      (px @left-offset)
                                         :top       (px @top-offset)})
                         style)}
          (when (and @showing? on-cancel)
            [backdrop
             :opacity  backdrop-opacity
             :on-click on-cancel])
          [popover-border
           :position       (if position position :right-below)
           :width          width
           :height         height
           :tooltip-style? tooltip-style?
           :popover-color  popover-color
           :arrow-length   arrow-length
           :arrow-width    arrow-width
           :padding        padding
           :title          (when title [popover-title
                                        :title          title
                                        :showing?       showing?
                                        :close-button?  close-button?
                                        :close-callback on-cancel])
           :children       [body]]])}))
  )

;;--------------------------------------------------------------------------------------------------
;; Component: popover-anchor-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-anchor-wrapper-args-desc
  [{:name :showing? :required true  :default false        :type "boolean atom"                                   :description "an atom. When the value is true, the popover shows"}
   {:name :position :required true  :default :right-below :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :anchor   :required true                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "the component the popover is attached to"}
   {:name :popover  :required true                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "the popover body component"}
   {:name :style    :required false                       :type "CSS style map"   :validate-fn css-style?        :description "override component style(s) with a style map, only use in case of emergency"}])

(defn popover-anchor-wrapper
  "Renders an element or control along with a Bootstrap popover"
  [& {:keys [showing? position anchor popover style] :as args}]
  {:pre [(validate-args-macro popover-anchor-wrapper-args-desc args "popover-anchor-wrapper")]}
  (let [[orientation arrow-pos] (split-keyword position "-") ;; only need orientation here
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")]
    [:div {:class  "rc-popover-anchor-wrapper display-inline-flex"
           :style (merge (flex-child-style "inherit")
                         style)}
     [:div                                ;; Wrapper around the anchor and the "point"
      {:class "rc-point-wrapper display-inline-flex"
       :style (merge (flex-child-style "auto")
                     (flex-flow-style flex-flow)
                     (align-style :align-items :center))}
      (when place-anchor-before? anchor)
      (when @showing?
        [:div                             ;; The "point" that connects the anchor to the popover
         {:class "rc-popover-point display-inline-flex"
          :style (merge (flex-child-style "auto")
                        {:position "relative"
                         :z-index  "4"})}
         popover])
      (when-not place-anchor-before? anchor)]]))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-tooltip
;;--------------------------------------------------------------------------------------------------

(def popover-tooltip-args-desc
  [{:name :label         :required true                         :type "string | hiccup | atom" :validate-fn string-or-hiccup?    :description "the text (or component) for the tooltip"}
   {:name :showing?      :required true  :default false         :type "boolean atom"                                             :description "an atom. When the value is true, the tooltip shows"}
   {:name :on-cancel     :required false                        :type "-> nil"                 :validate-fn fn?                  :description "a function which takes no params and returns nothing. Called when the popover is cancelled (e.g. user clicks away)"}
   {:name :close-button? :required false :default false         :type "boolean"                                                  :description "when true, displays the close button"}
   {:name :status        :required false                        :type "keyword"                :validate-fn popover-status-type? :description [:span "controls background color of the tooltip. " [:code "nil/omitted"] " for black or one of " popover-status-types-list]}
   {:name :anchor        :required true                         :type "hiccup"                 :validate-fn string-or-hiccup?    :description "the component the tooltip is attached to"}
   {:name :position      :required false :default :below-center :type "keyword"                :validate-fn position?            :description [:span "relative to this anchor. One of " position-options-list]}
   {:name :width         :required false                        :type "string"                 :validate-fn string?              :description "specifies width of the tooltip"}
   {:name :style         :required false                        :type "CSS style map"          :validate-fn css-style?           :description "override component style(s) with a style map, only use in case of emergency"}])

(defn popover-tooltip
  "Renders text as a tooltip in Bootstrap popover style"
  [& {:keys [label showing? on-cancel close-button? status anchor position width style] :as args}]
  {:pre [(validate-args-macro popover-tooltip-args-desc args "popover-tooltip")]}
  (let [label         (deref-or-value label)
        popover-color (case status
                        :warning "#f57c00"
                        :error   "#d50000"
                        :info    "#333333"
                        "black")]
    [popover-anchor-wrapper
     :showing? showing?
     :position position
     :anchor   anchor
     :style    style
     :popover [popover-content-wrapper
               :showing?       showing?
               :position       (if position position :below-center)
               :on-cancel      on-cancel
               :width          width
               :tooltip-style? true
               :popover-color  popover-color
               :padding        "3px 8px"
               :arrow-length   6
               :arrow-width    12
               :body           [v-box
                                :style (if (= status :info)
                                         {:color       "white"
                                          :font-size   "14px"
                                          :padding     "4px"}
                                         {:color       "white"
                                          :font-size   "12px"
                                          :font-weight "bold"
                                          :text-align  "center"})
                                :children [label (when close-button?
                                                   [close-button showing? on-cancel {:font-size   "20px"
                                                                                     :color       "white"
                                                                                     :text-shadow "none"
                                                                                     :right       "1px"}])]]]]))
