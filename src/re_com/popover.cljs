(ns re-com.popover
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.util    :refer [validate-arguments get-element-by-id px deref-or-value]]
            [clojure.string :as    string]
            [reagent.core   :as    reagent]))


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
  "A button with a big X in it, placed to the right of the popup."
  [showing? close-callback]
  ;; Can't use [button] because [button] already uses [popover] which would be a circular dependency.
  [:button
   {:on-click (handler-fn
                (if close-callback
                  (close-callback)
                  (reset! showing? false)))
    :class    "close"
    :style    {:font-size "36px" :height "26px" :margin-top "-8px"}}
   "×"])


(defn- calc-popover-pos
  [pop-id pop-orient p-width p-height pop-offset]
  (if-let [popover-elem (get-element-by-id pop-id)]  ;; TODO: Not required any more, remove carefully as this is an if-let!
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
      (println {:left popover-left :top popover-top :right popover-right :bottom popover-bottom})
      {:left popover-left :top popover-top :right popover-right :bottom popover-bottom})))


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


(defn sum-scroll-offsets
  "Given a DOM node, I traverse through all ascendant nodes (until I reach body), summing any scrollLeft and scrollTop values
   and return these sums in a map."
  [node]
  (let [popover-point-node (.-parentNode node)                  ;; Get reference to rc-popover-point node
        point-left         (.-offsetLeft popover-point-node)    ;; offsetTop/Left is the viewport pixel offset of the point we want to point to (ignoring scrolls)
        point-top          (.-offsetTop  popover-point-node)]
    (loop [current-node    popover-point-node
           sum-scroll-left 0
           sum-scroll-top  0]
      (if (not= (.-tagName current-node) "BODY")
        (recur (.-parentNode current-node)
               (+ sum-scroll-left (.-scrollLeft current-node))
               (+ sum-scroll-top  (.-scrollTop  current-node)))
        {:left (- point-left sum-scroll-left)
         :top  (- point-top  sum-scroll-top)}))))


;;--------------------------------------------------------------------------------------------------
;; Component: backdrop
;;--------------------------------------------------------------------------------------------------

;; TODO this is going to be difficult to include in the demo help text
(def backdrop-args-desc
  [{:name :opacity         :required false :default 0.0     :type "double"   :description "the opacity of the backdrop (0.0 for transparent to 1.0 for fully opaque)."}
   {:name :on-click        :required false                  :type "function" :description "the callback (no arguments) for when the backdrop is clicked."}])

(def backdrop-args
  (set (map :name backdrop-args-desc)))

(defn backdrop
  "Renders a backdrop dive which fills the entire page and responds to clicks on it. Can also specify how tranparent it should be."
  [& {:keys [opacity on-click] :as args}]
  {:pre [(validate-arguments backdrop-args (keys args))]}
  [:div {:class     "rc-backdrop"
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

;; TODO this is going to be difficult to include in the demo help text
(def popover-border-args-desc
  [{:name :position       :required false :default ":right-below" :type "keyword"       :description "a keyword specifying the popover's position relative to the anchor. See the demo to the right for the values."}
   {:name :children       :required true                          :type "vector"        :description "a vector of component markups."}
   {:name :width          :required false                         :type "string"        :description "a CSS style describing the popover width."}
   {:name :height         :required false :default "auto"         :type "string"        :description "a CSS style describing the popover height."}
   {:name :popover-color  :required false :default "white"        :type "string"        :description "Fill color of the popover."}
   {:name :arrow-length   :required false :default 11             :type "integer"       :description "the length in pixels of the arrow (from pointy part to middle of arrow base)."}
   {:name :arrow-width    :required false :default 22             :type "integer"       :description "the width in pixels of arrow base."}
   {:name :padding        :required false                         :type "string"        :description "a CSS style which overrides the inner padding of the popover."}
   {:name :margin-left    :required false                         :type "string"        :description "a CSS style describing the horiztonal offset from anchor after position."}
   {:name :margin-top     :required false                         :type "string"        :description "a CSS style describing the vertical offset from anchor after position."}
   {:name :tooltip-style? :required false                         :type "boolean"       :description "setup popover styles for a tooltip."}
   {:name :title          :required false                         :type "string|markup" :description "describes a title"}
   ])

(def popover-border-args
  (set (map :name popover-border-args-desc)))

(defn popover-border
  "Renders an element or control along with a Bootstrap popover."
  [& {:keys [position width height popover-color arrow-length arrow-width padding margin-left margin-top tooltip-style? title children]
      :or {arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(validate-arguments popover-border-args (keys args))]}
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
         (do (println ":component-did-mount height =" (.-clientHeight (get-element-by-id pop-id)))
             (reset! rendered-once true)))

       :component-did-update
       (fn []
         (let [popover-elem   (get-element-by-id pop-id)]
           (reset! p-width    (if popover-elem (.-clientWidth  popover-elem) 0))
           (reset! p-height   (if popover-elem (.-clientHeight popover-elem) 0))
           (reset! pop-offset (case arrow-pos
                                :center nil
                                :right  20
                                :below  20
                                :left   (if @p-width (- @p-width 25) @p-width)
                                :above  (if @p-height (- @p-height 25) @p-height)))
           (println ":component-did-update height =" (.-clientHeight (get-element-by-id pop-id)))))

       :component-function
       (fn
         [& {:keys [position width height popover-color arrow-length arrow-width padding margin-left margin-top tooltip-style? title children]
             :or {arrow-length 11 arrow-width 22}
             :as args}]
         {:pre [(validate-arguments popover-border-args (keys args))]}
         (let [popover-elem   (get-element-by-id pop-id)]
           (reset! p-width    (if popover-elem (.-clientWidth  popover-elem) 0)) ;; TODO: Duplicate from above but needs to be calculated here to prevent an annoying flicker (so make it a fn)
           (reset! p-height   (if popover-elem (.-clientHeight popover-elem) 0))
           (reset! pop-offset (case arrow-pos
                                :center nil
                                :right  20
                                :below  20
                                :left   (if @p-width (- @p-width 25) @p-width)
                                :above  (if @p-height (- @p-height 25) @p-height)))
           [:div.popover.fade.in
            {:id pop-id
             :class (case orientation :left "left" :right "right" :above "top" :below "bottom")
             :style (merge (if @rendered-once
                             (do
                               (println "POPOVER-BORDER: rendered-once? =" @rendered-once
                                        ": p-width =" @p-width
                                        ": p-height =" @p-height
                                        ": pop-offset =" @pop-offset)
                               ;(reset! rendered-once false)
                               ;(reset! updated-once false)
                               (calc-popover-pos pop-id orientation @p-width @p-height @pop-offset))
                             {:top "-10000px" :left "-10000px"})
                           (if width  {:width  width})
                           (if height {:height height})
                           (if popover-color {:background-color popover-color})
                           (when tooltip-style?
                             {:border-radius "4px"
                              :box-shadow    "none"
                              :border        "none"})

                           ;; TODO: Seems this code is not required, remove when confirmed
                           #_{(case orientation
                              (:left  :right) :margin-left
                              (:above :below) :margin-top) (px (case orientation
                                                                 :left           (str "-" (+ arrow-length @p-width))
                                                                 :above          (str "-" (+ arrow-length @p-height))
                                                                 (:right :below) arrow-length))}

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

;; TODO this is going to be difficult to include in the demo help text
(def popover-title-args-desc
  [{:name :showing?       :required true                    :type "atom"          :description "when the value is true, the popover shows.."}
   {:name :title          :required false                   :type "string|markup" :description "describes the title of the popover. Default font size is 18px to make it stand out."}
   {:name :close-button?  :required false  :default true    :type "boolean"       :description "when true, displays the close button."}
   {:name :close-callback :required false  :default 11      :type "function"      :description "callback taking no parameters, used when the close button is pressed. Not required if <code>:showing?</code> atom passed in OR <code>:close-button?</code> is set to false."}])

(def popover-title-args
  (set (map :name popover-title-args-desc)))

(defn popover-title
  "Renders a title at the top of a popover with an optional close button on the far right."
  [& {:keys [title showing? close-button? close-callback]
      :as args}]
  {:pre [(validate-arguments popover-title-args (keys args))]}
  (assert (or ((complement nil?) showing?) ((complement nil?) close-callback)) "Must specify either showing? OR close-callback")
  (let [close-button? (if (nil? close-button?) true close-button?)]
    [:h3.popover-title {:style {:font-size "18px"
                                :flex      "inherit"}}
     [:div {:style {:display         "flex"
                    :flex-flow       "row nowrap"
                    :justify-content "space-between"
                    :align-items     "center"}}
      title
      (when close-button? [close-button showing? close-callback])]]))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-content-wrapper
;;--------------------------------------------------------------------------------------------------

;; TODO this is going to be difficult to include in the demo help text
(def popover-content-wrapper-args-desc
  [{:name :showing?         :required true                         :type "atom"          :description "an atom. when the value is true, the popover shows.."}
   {:name :position         :required true   :default :right-below :type "keyword"       :description "specifies the popover's position relative to the anchor. See the demo to the right for the values."}
   {:name :no-clip?         :required false  :default false        :type "boolean"       :description "when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped. By passing true for this parameter, re-com will use a different CSS method to show the popover. This method is slightly inferior because the popover can't track the anchor if it is repositioned."}
   {:name :width            :required false                        :type "string"        :description "a CSS style representing the popover width."}
   {:name :height           :required false  :default "auto"       :type "string"        :description "a CSS style representing the popover height."}
   {:name :backdrop-opacity :required false  :default 0.0          :type "float"         :description "indicates the opacity of the backdrop where 0.0=transparent, 1.0=opaque."}
   {:name :on-cancel        :required false  :default 0.0          :type "function"      :description "a callback taking no parameters, invoked when the popover is cancelled (e.g. user clicks away)."}
   {:name :title            :required false                        :type "string|markup" :description "describes the title of the popover. The default font size is 18px to make it stand out."}
   {:name :close-button?    :required false  :default true         :type "boolean"       :description "when true, displays the close button."}
   {:name :body             :required false                        :type "markup"        :description "describes the popover body. Must be a single component."}
   {:name :tooltip-style?   :required false                        :type "boolean"       :description "setup popover styles for a tooltip."}
   {:name :popover-color    :required false  :default "white"      :type "string"        :description "Fill color of the popover."}
   {:name :arrow-length     :required false  :default 11           :type "integer"       :description "the length in pixels of the arrow (from pointy part to middle of arrow base)."}
   {:name :arrow-width      :required false  :default 22           :type "integer"       :description "the width in pixels of arrow base."}
   {:name :padding          :required false                        :type "string"        :description "a CSS style which overrides the inner padding of the popover."}
   {:name :style            :required false                        :type "map"           :description "override component style(s) with a style map, only use in case of emergency."}
   ])

(def popover-content-wrapper-args
  (set (map :name popover-content-wrapper-args-desc)))

(defn popover-content-wrapper
  "Abstracts several components to handle the 90% of cases for general popovers and dialog boxes."
  [& {:keys [showing? position no-clip? width height backdrop-opacity on-cancel title close-button? body tooltip-style? popover-color arrow-length arrow-width padding style]
      :or {arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(validate-arguments popover-content-wrapper-args (keys args))]}
  (assert ((complement nil?) showing?) "Must specify a showing? atom")
  (let [left-offset (reagent/atom 0)
        top-offset  (reagent/atom 0)]
    (reagent/create-class
      {:component-did-mount
       (fn [event]
         (when no-clip?
           (let [offsets (sum-scroll-offsets (reagent/dom-node event))]
             (reset! left-offset (:left offsets))
             (reset! top-offset  (:top  offsets)))))

       :component-function
       (fn
         [& {:keys [showing? position no-clip? width height backdrop-opacity on-cancel title close-button? body tooltip-style? popover-color arrow-length arrow-width padding style]
             :or {arrow-length 11 arrow-width 22}
             :as args}]
         {:pre [(validate-arguments popover-content-wrapper-args (keys args))]}
         [:div
          {:class "popover-content-wrapper"
           :style (merge {:flex "inherit"}
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
  [{:name :showing?         :required true                         :type "atom"     :description "when the value is true, the popover shows."}
   {:name :position         :required true   :default :right-below :type "keyword"  :description "specifies the popover's position relative to the anchor. See the demo to the right for the values."}
   {:name :anchor           :required true                         :type "markup"   :description "the component the popover is attached to."}
   {:name :popover          :required false                        :type "markup"   :description "the popover body component."}
   {:name :style            :required false                        :type "map"      :description "override component style(s) with a style map, only use in case of emergency."}
   ])

(def popover-anchor-wrapper-args
  (set (map :name popover-anchor-wrapper-args-desc)))

(defn popover-anchor-wrapper
  "Renders an element or control along with a Bootstrap popover."
  [& {:keys [showing? position anchor popover style] :as args}]
  {:pre [(validate-arguments popover-anchor-wrapper-args (keys args))]}
  (let [[orientation arrow-pos] (split-keyword position "-") ;; only need orientation here
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")]
    [:div {:class  "rc-popover-anchor-wrapper"
           :style (merge {:display "inline-flex"
                          :flex    "inherit"}
                         style)}
     [:div                                ;; Wrapper around the anchor and the "point"
      {:class "rc-point-wrapper"
       :style {:display     "inline-flex"
               :flex-flow   flex-flow
               :align-items "center"}}
      (when place-anchor-before? anchor)
      (when @showing?
        [:div                             ;; The "point" that connects the anchor to the popover
         {:class "rc-popover-point"
          :style {:position "relative"
                  :z-index  "4"
                  :display  "inline-flex"
                  :flex     "auto"}}
         popover])
      (when-not place-anchor-before? anchor)]]))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-tooltip
;;--------------------------------------------------------------------------------------------------

(def popover-tooltip-args-desc
  [{:name :label      :required true                            :type "string"   :description "the text for the tooltip."}
   {:name :showing?   :required true                            :type "atom"     :description "when the value is true, the tooltip shows."}
   {:name :status     :required false                           :type "keyword"  :description "controls background colour of the tooltip. Values: nil= black, :warning = orange, :error = red)."}
   {:name :anchor     :required true                            :type "markup"   :description "the component the tooltip is attached to."}
   {:name :position   :required false  :default ":below-center" :type "keyword"  :description "specifies the tooltip's position relative to the anchor. Same as for main popover component."}
   {:name :width      :required false                           :type "string"   :description "specifies width of the tooltip."}
   {:name :style      :required false                           :type "map"      :description "override component style(s) with a style map, only use in case of emergency."}
   ])

(def popover-tooltip-args
  (set (map :name popover-tooltip-args-desc)))

(defn popover-tooltip
  "Renders text as a tooltip in Bootstrap popover style."
  [& {:keys [label showing? status anchor position width style] :as args}]
  {:pre [(validate-arguments popover-tooltip-args (keys args))]}
  (let [label         (deref-or-value label)
        popover-color (case status
                        :warning "#f57c00"
                        :error   "#d50000"
                        "black")]
    [popover-anchor-wrapper
     :showing? showing?
     :position position
     :anchor   anchor
     :style    style
     :popover [popover-content-wrapper
               :showing?       showing?
               :position       (if position position :below-center)
               :width          width
               :tooltip-style? true
               :popover-color  popover-color
               :padding        "3px 8px"
               :arrow-length   6
               :arrow-width    12
               :body           [:div
                                {:style {:color       "white"
                                         :font-size   "12px"
                                         :font-weight "bold"
                                         :text-align  "center"
                                         :line-height "16px"}}
                                label]]]))
