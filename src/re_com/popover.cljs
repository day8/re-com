(ns re-com.popover
  (:require [clojure.set    :refer [superset?]]
            [re-com.util    :as    util]
            [re-com.core    :refer [button]]
            [clojure.string :as    string]
            [reagent.core   :as    reagent]))


(defn point
  [x y]
  (str x "," y " "))


(defn px
  [val & negative]
  (str (when negative "-") val "px"))


(defn- split-keyword
  [kw delimiter] ;; TODO: Move to util?
  "I return the vector of the two keywords formed by splitting
   another keyword 'kw' on an internal delimiter (usually '-').
   (split-keyword  :above-left  \"-\")
   =>  [:above :left]"
  (let [keywords (string/split (str kw) (re-pattern (str "[" delimiter ":]")))]
    [(keyword (keywords 1)) (keyword (keywords 2))]))


(defn- close-button
  [showing? close-callback]
  "A button with a big X in it, placed to the right of the popup."
  [button
   :label    "×"
   :on-click #(if close-callback
               (close-callback)
               (reset! showing? false))
   :class    "close"
   :style    {:font-size "36px" :height "26px" :margin-top "-8px"}])


(defn- calc-popover-pos
  [pop-id pop-orient pop-offset]
  (if-let [popover-elem (util/get-element-by-id pop-id)]
    (let [p-width        (.-clientWidth popover-elem)
          p-height       (.-clientHeight popover-elem)
          popover-left   (case pop-orient
                           :left           "initial" ;; TODO: Remove this pollution (must have NO :left which is in Bootstrap .popover class)
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
      {:left popover-left :top popover-top :right popover-right :bottom popover-bottom})))


(defn- popover-arrow
  [orientation pop-offset arrow-length arrow-width grey-arrow]
  (let [half-arrow-width (/ arrow-width 2)
        arrow-shape {:left  (str (point 0 0)            (point arrow-length half-arrow-width) (point 0 arrow-width))
                     :right (str (point arrow-length 0) (point 0 half-arrow-width)            (point arrow-length arrow-width))
                     :above (str (point 0 0)            (point half-arrow-width arrow-length) (point arrow-width 0))
                     :below (str (point 0 arrow-length) (point half-arrow-width 0)            (point arrow-width arrow-length))}]
    [:svg {:style {:position "absolute"
                   (case orientation ;; Connect arrow to edge of popover
                     :left  :right
                     :right :left
                     :above :bottom
                     :below :top) (px arrow-length :negative)

                   (case orientation ;; Position the arrow at the top/left, center or bottom/right of the popover
                     (:left  :right) :top
                     (:above :below) :left) (if (nil? pop-offset) "50%" (px pop-offset))

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
                 :style {:fill (if grey-arrow "#f7f7f7" "white")
                         :stroke "rgba(0, 0, 0, .2)"
                         :stroke-width "1"}}]]))


(defn- make-popover
  ;TODO: Args being passed but not used: anchor backdrop-callback backdrop-opacity
  ;TODO: Args being used but not declared: padding margin-left margin-top
  [{:keys [showing? close-button? position title body width height close-callback arrow-length arrow-width]
    :or {close-button? true position :right-below body "{empty body}" width 250 arrow-length 11 arrow-width 22}
    :as popover-params}]
  "Renders an element or control along with a Bootstrap popover
   Parameters:
    - popover-params map
       - :showing?          [nil           ] a reagent atom with boolean, which controls whether the popover is showing or not
       - :close-button?     [false         ] a boolean indicating whether a close button will be added to the popover title
       - :position          [:right-below  ] place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below
       - :title             [nil           ] popover title (nil for no title)
       - :body              ['{empty body}'] popover body (a string or a hiccup vector or function returning a hiccup vector)
       - :width             [250           ] a CSS string representing the popover width in pixels (or nil or omit parameter for auto)
       - :height            [auto          ] a CSS string representing the popover height in pixels (or nil or omit parameter for auto)
       - :padding           [nil           ] override the inner padding of the popover
       - :margin-left       [nil           ] horiztonal offset from anchor after position
       - :margin-top        [nil           ] vertical offset from anchor after position
       - :close-callback    [nil           ] function called when the close button is pressed (overrides the default close behaviour)
       - :arrow-length      [11            ] length in pixels of arrow (from pointy part to middle of arrow base)
       - :arrow-width       [22            ] length in pixels of arrow base"
  (let [rendered-once           (reagent/atom false)
        pop-id                  (gensym "popover-")
        [orientation arrow-pos] (split-keyword position "-")
        grey-arrow              (and title (or (= orientation :below) (= arrow-pos :below)))]
    (reagent/create-class
     {:component-did-mount
      (fn []
        (reset! rendered-once true))

      :render
      (fn []
        (let [popover-elem   (util/get-element-by-id pop-id)
              p-height       (if popover-elem (.-clientHeight popover-elem) 0) ;; height is optional (with no default) so we need to calculate it
              pop-offset     (case arrow-pos
                               :center nil
                               :right  20
                               :below  20
                               :left   (if width (- width 25) width)
                               :above  (if p-height (- p-height 25) p-height)
                               )]

          [:div.popover.fade.in
           {:id pop-id
            :class (case orientation :left "left" :right "right" :above "top" :below "bottom")
            :style (merge (if @rendered-once
                            (calc-popover-pos pop-id orientation pop-offset)
                            {:top (px -10000) :left (px -10000)})
                          (if width {:width width})
                          (if height {:height height})
                          {(case orientation
                             (:left  :right) :margin-left
                             (:above :below) :margin-top) (px (case orientation
                                                                :left           (str "-" (+ arrow-length width))
                                                                :above          (str "-" (+ arrow-length p-height))
                                                                (:right :below) arrow-length))}
                          ;; make it visible and turn off BS max-width and remove BS padding which adds an internal white border
                          {:display "block" :max-width "none" :padding (px 0)}
                          ;; optional override offsets
                          (select-keys popover-params [:margin-left :margin-top]))}
           [popover-arrow orientation pop-offset arrow-length arrow-width grey-arrow]
           ;(when title [:h3.popover-title [:div title (when close-button? [close-button showing? close-callback])]])
           (when title [:h3.popover-title
                        [:div {:style {:display "flex" :flex-flow "row nowrap" :justify-content "space-between" :align-items "center"}}
                         title
                         (when close-button? [close-button showing? close-callback])]])
           [:div.popover-content {:style (select-keys popover-params [:padding])} body]]))})))


;;--------------------------------------------------------------------------------------------------
;; Component: popover
;;--------------------------------------------------------------------------------------------------

(def popover-args
  #{:position   ; Place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below
    :showing?   ; A reagent atom with boolean, which controls whether the popover is showing or not
    :anchor     ; The hiccup markup which the popover is attached to
    :popover    ; Content map:
                ;  - :width             a CSS string representing the popover width in pixels (or nil or omit parameter for auto)
                ;  - :height            a CSS string representing the popover height in pixels (or nil or omit parameter for auto)
                ;  - :title             popover title (nil for no title)
                ;  - :close-button?     a boolean indicating whether a close button will be added to the popover title
                ;  - :body              popover body (a string or a hiccup vector or function returning a hiccup vector)
    :options    ; Options map:
                ;  - :arrow-length      length in pixels of arrow (from pointy part to middle of arrow base)
                ;  - :arrow-width       length in pixels of arrow base
                ;  - :padding           override the inner padding of the popover
                ;  - :margin-left       horiztonal offset from anchor after position
                ;  - :margin-top        vertical offset from anchor after position
                ;  - :backdrop-callback if specified, add a backdrop div between the main screen (including element) and the popover.
                ;                       when clicked, this callback is called (usually to close the popover)
                ;  - :close-callback    function called when the close button is pressed (overrides the default close behaviour)
                ;  - :backdrop-opacity  0 = transparent, 1 = black (http://jsfiddle.net/Rt9BJ/1)
    })


(defn popover
  [& {:keys [position showing? anchor popover options]
      :or {}
      :as args}]
  {:pre [(superset? popover-args (keys args))]}
  "Renders an element or control along with a Bootstrap popover."
  (let [[orientation arrow-pos] (split-keyword position "-") ;; only need orientation here
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")
        popover-params          (merge {:position position :showing? showing?} popover options)
        backdrop-callback       (:backdrop-callback popover-params)
        backdrop-opacity        (:backdrop-opacity popover-params)]

    [:div {:class  "rc-popover"
            :style {:display "inline-flex"
                   :flex     "inherit"}}
     (when (and @showing? backdrop-callback)
       [:div {:style    {:position         "fixed"
                         :left             "0px"
                         :top              "0px"
                         :width            "100%"
                         :height           "100%"
                         :background-color "black"
                         :opacity          backdrop-opacity}
              :on-click backdrop-callback}])
     [:div {:style {:display "inline-flex" :flex-flow flex-flow :align-items "center"}}
      (when place-anchor-before? anchor)
      (when @showing?
        [:div {:style {:position "relative" :display "inline-flex"}} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
         [make-popover popover-params]])
      (when-not place-anchor-before? anchor)]]))


;;--------------------------------------------------------------------------------------------------
;; Component: backdrop
;;--------------------------------------------------------------------------------------------------

(def backdrop-args
  #{:opacity    ; The opacity of the backdrop (0 for transparent to 1 for fully opaque)
    :on-click   ; Callback function for when the backdrop is clicked
    })


(defn backdrop
  [& {:keys [opacity on-click] :as args}]
  {:pre [(superset? backdrop-args (keys args))]}
  "Renders a backdrop............."                         ;; TODO
  [:div {:class     "rc-backdrop"
         :style    {:position         "fixed"
                    :left             "0px"
                    :top              "0px"
                    :width            "100%"
                    :height           "100%"
                    :background-color "black"
                    :opacity          opacity}
         :on-click on-click}])


;;--------------------------------------------------------------------------------------------------
;; Component: popover-border
;;--------------------------------------------------------------------------------------------------

(def popover-border-args
  #{:position         ;; Place popover relative to the anchor :above-left/center/right,
                      ;; :below-left/center/right, :left-above/center/below, :right-above/center/below (default is :right-below).
    :width            ;; A CSS string representing the popover width in pixels or nil or omit parameter for auto (default 250px).
    :height           ;; A CSS string representing the popover height in pixels (or nil or omit parameter for auto (default "auto").
    :arrow-length     ;; Length in pixels of arrow (from pointy part to middle of arrow base).
    :arrow-width      ;; Length in pixels of arrow base.
    :padding          ;; Override the inner padding of the popover
    :margin-left      ;; Horiztonal offset from anchor after position
    :margin-top       ;; Vertical offset from anchor after position
    :title            ;; Markup for a title, should be a call to [popover-title ... ] ;; TODO: Probably should be general markup
    :children         ;; A vector of components.
    })


(defn popover-border
  [& {:keys [position width height arrow-length arrow-width padding margin-left margin-top title children]
      :or {position :right-below width 250 arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(superset? popover-border-args (keys args))]}
  "Renders an element or control along with a Bootstrap popover."
  (let [rendered-once           (reagent/atom false)
        pop-id                  (gensym "popover-")
        [orientation arrow-pos] (split-keyword position "-")
        grey-arrow              (and title (or (= orientation :below) (= arrow-pos :below)))]
    (reagent/create-class
      {:component-did-mount
        (fn []
          (reset! rendered-once true))

       :render
        (fn []
          (let [popover-elem   (util/get-element-by-id pop-id)
                p-height       (if popover-elem (.-clientHeight popover-elem) 0) ;; height is optional (with no default) so we need to calculate it
                pop-offset     (case arrow-pos
                                 :center nil
                                 :right  20
                                 :below  20
                                 :left   (if width (- width 25) width)
                                 :above  (if p-height (- p-height 25) p-height))]
            [:div.popover.fade.in
             {:id pop-id
              :class (case orientation :left "left" :right "right" :above "top" :below "bottom")
              :style (merge (if @rendered-once
                              (calc-popover-pos pop-id orientation pop-offset)
                              {:top (px -10000) :left (px -10000)})
                            (if width {:width width})
                            (if height {:height height})
                            {(case orientation
                               (:left  :right) :margin-left
                               (:above :below) :margin-top) (px (case orientation
                                                                  :left           (str "-" (+ arrow-length width))
                                                                  :above          (str "-" (+ arrow-length p-height))
                                                                  (:right :below) arrow-length))}
                            ;; make it visible and turn off BS max-width and remove BS padding which adds an internal white border
                            {:display "block" :max-width "none" :padding (px 0)}
                            ;; optional override offsets
                            {:margin-left margin-left :margin-top margin-top})}
             [popover-arrow orientation pop-offset arrow-length arrow-width grey-arrow]
             (when title title)
             ;(into [:div.popover-content {:style {:padding padding}}] children)
             [:div.popover-content {:style {:padding padding}} children]
             ]))})))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-title
;;--------------------------------------------------------------------------------------------------

(def popover-title-args
  #{:title            ;; ;; TODO: fill this in AND validate that either close-callback is passed or showing? is passed (otherwise error)
    :showing?         ;; ;; TODO: If close-button is true then validate that ONE of the options is passed
    :close-button?    ;;
    :close-callback   ;;
    })


(defn popover-title
  [& {:keys [title showing? close-button? close-callback]
      :or {close-button? true}
      :as args}]
  {:pre [(superset? popover-title-args (keys args))]}
  "Renders a title at the top of a popover with an optional close button on the far right."
  [:h3.popover-title
   [:div {:style {:display "flex" :flex-flow "row nowrap" :justify-content "space-between" :align-items "center"}}
    title
    (when close-button? [close-button showing? close-callback])]])


;;--------------------------------------------------------------------------------------------------
;; Component: popover-content
;;--------------------------------------------------------------------------------------------------

(def popover-content-args
  #{:showing?    ;;
    :position   ;;
    :width      ;;
    :on-cancel  ;;
    :title      ;;
    :body       ;;
    })


(defn popover-content
  [& {:keys [showing? position width on-cancel title body] :as args}]
  {:pre [(superset? popover-content-args (keys args))]}
  "..............................................."         ;; TODO:
  [:div
   [backdrop
    :opacity  0.3
    :on-click on-cancel]
   [popover-border
    :position      position
    :width         width
    :title         [popover-title
                    :title          title
                    :showing?       showing?
                    :close-button?  true
                    :close-callback on-cancel]
    ;; TODO: Rename to :child in popover-border ?
    ;:children       [(fn [] body)]
    :children       body
    ]])


;;--------------------------------------------------------------------------------------------------
;; Component: popover-anchor-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-anchor-wrapper-args
  #{:position   ; Place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below
    :showing?   ; A reagent atom with boolean, which controls whether the popover is showing or not
    :anchor     ; The markup which the popover is attached to
    :popover    ; Popover body component
    })


(defn popover-anchor-wrapper
  [& {:keys [position showing? anchor popover] :as args}]
  {:pre [(superset? popover-anchor-wrapper-args (keys args))]}
  "Renders an element or control along with a Bootstrap popover."
  (let [[orientation arrow-pos] (split-keyword position "-") ;; only need orientation here
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")]
    (println "RENDER: popover-anchor-wrapper")
    [:div {:class  "rc-popover"
            :style {:display "inline-flex"
                   :flex     "inherit"}}
     [:div {:class "rc-point-wrapper"
            :style {:display "inline-flex"
                    :flex-flow flex-flow
                    :align-items "center"}} ;; Wrapper around the anchor and the "point"
      (when place-anchor-before? anchor)
      (when @showing?
        [:div {:class "re-popover-point"
               :style {:position "relative" :display "inline-flex"}} ;; This is the "point" that connects the anchor to the popover
         popover])
      (when-not place-anchor-before? anchor)]]))


;;--------------------------------------------------------------------------------------------------
;; Component: make-button
;;--------------------------------------------------------------------------------------------------

(def make-button-args
  #{:showing?   ; The atom used to hide/show the popover
    :type       ; Button type (string): default, primary, success, info, warning, danger, link
    :label      ; Label for the button
    :style      ; Custom style for the button
    })


(defn make-button
  [& {:keys [showing? type label style] :as args}]
  {:pre [(superset? make-button-args (keys args))]}
  "Renders a button designed to go into a popover.
   It provides the functionality to toggle the popover when the button is pressed."
  [button
   :label    label
   :on-click #(reset! showing? (not @showing?))
   :style    (merge {:margin-left "2px"} style)
   :class    (str "rc-make-button btn-" type)])


;;--------------------------------------------------------------------------------------------------
;; Component: make-link
;;--------------------------------------------------------------------------------------------------

(def make-link-args
  #{:showing?   ; The atom used to hide/show the popover
    :toggle-on  ; Determine how to show popover:
                ;  - :mouse  Make this a hover popover (tooltip)
                ;  - :click  Make it a click popover
    :label      ; Label for the link
    :style      ; Custom style for the link
    })


(defn make-link
  [& {:keys [showing? toggle-on label style] :as args}]
  {:pre [(superset? make-link-args (keys args))]}
  "Renders a link designed to go into a popover.
   It provides the functionality to toggle the popover when the button is pressed."
  (let [show   #(reset! showing? true)
        hide   #(reset! showing? false)
        toggle #(reset! showing? (not @showing?))]
    [:a
     (merge {:class "rc-make-link"}
            {:style (merge {:flex "inherit"} style)}
            (if (= toggle-on :mouse)
              {:on-mouse-over show
               :on-mouse-out  hide}
              {:on-click      toggle}))
     label]))
