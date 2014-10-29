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
      {:left popover-left :top popover-top :right popover-right :bottom popover-bottom})))


(defn- popover-arrow
  [orientation pop-offset arrow-length arrow-width grey-arrow?]
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
                 :style {:fill (if grey-arrow? "#f7f7f7" "white")
                         :stroke "rgba(0, 0, 0, .2)"
                         :stroke-width "1"}}]]))


(defn sum-scroll-offsets
  [node]
  "Given a DOM node, I traverse through all ascendant nodes (until I reach body), summing any scrollLeft and scrollTop values
   and return these sums in a map."
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

(def backdrop-args
  #{:opacity    ; The opacity of the backdrop (0 for transparent to 1 for fully opaque).
    :on-click   ; Callback function for when the backdrop is clicked.
    })


(defn backdrop
  [& {:keys [opacity on-click] :as args}]
  {:pre [(superset? backdrop-args (keys args))]}
  "Renders a backdrop dive which fills the entire page and responds to clicks on it. Can also specify how tranparent it should be."
  [:div {:class     "rc-backdrop"
         :style    {:position         "fixed"
                    :left             "0px"
                    :top              "0px"
                    :width            "100%"
                    :height           "100%"
                    :background-color "black"
                    :opacity          (if opacity opacity 0.0)}
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
    :padding          ;; Override the inner padding of the popover.
    :margin-left      ;; Horiztonal offset from anchor after position.
    :margin-top       ;; Vertical offset from anchor after position.
    :title            ;; Markup for a title. Can of course be a simple string.
    :children         ;; A vector of components.
    })


(defn popover-border
  [& {:keys [position width height arrow-length arrow-width padding margin-left margin-top title children]
      :or {position :right-below arrow-length 11 arrow-width 22}
      :as args}]
  {:pre [(superset? popover-border-args (keys args))]}
  "Renders an element or control along with a Bootstrap popover."
  (let [width                   (if (nil? width) 250 width) ;; Moved here from :or above as sometimes we pass width in as null and :or doesn't work in this case
        rendered-once           (reagent/atom false)
        pop-id                  (gensym "popover-")
        [orientation arrow-pos] (split-keyword position "-")
        grey-arrow?             (and title (or (= orientation :below) (= arrow-pos :below)))]
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
             [popover-arrow orientation pop-offset arrow-length arrow-width grey-arrow?]
             (when title title)
             (into [:div.popover-content {:style {:padding padding}}] children)]))})))


;;--------------------------------------------------------------------------------------------------
;; Component: popover-title
;;--------------------------------------------------------------------------------------------------

(def popover-title-args
  #{:title            ;; The title of the popover. Can be a string or markup. Defaul font size is 18px to make it stand out.
    :showing?         ;; The showing? atom used to show/hide the popover, required for close button.
                      ;; NOTE: Not required if close-button? set to false OR close-callback specified.
    :close-button?    ;; A boolean indicating whether to display the close button or now. Defaults to true.
    :close-callback   ;; Call this function if the close buton is pressed.
                      ;; NOTE: Not required if showing? atom passed in OR close-button? set to false.
    })


(defn popover-title
  [& {:keys [title showing? close-button? close-callback]
      :as args}]
  {:pre [(superset? popover-title-args (keys args))]}
  "Renders a title at the top of a popover with an optional close button on the far right."
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
;; Component: popover-content
;;--------------------------------------------------------------------------------------------------

(def popover-content-wrapper-args
  #{:showing?           ;; The atom used to dhow/hide the popover.
    :position           ;; Place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below.
    :no-clip            ;; Prevents clipping within a scroller (trade-off is that the popover remains fixed on screen when other elements scroll, move, resize)
    :width              ;; A CSS string representing the popover width in pixels or nil or omit parameter for auto (default 250px).
    :height             ;; A CSS string representing the popover height in pixels (or nil or omit parameter for auto)
    :backdrop-opacity   ;; A float number indicating the opacity of the backdrop  0 = transparent, 1 = black.
    :on-cancel          ;; The callback used when a cancel event is detected (both close-button pressed or backdrop clicked).
    :title              ;; Markup for a title. Can of course be a simple string.
    :close-button?      ;; A boolean indicating whether to display the close button or now. Defaults to true.
    :body               ;; Markup for the popover body. Must be a single component.
    })


(defn popover-content-wrapper
  [& {:keys [showing? position no-clip width height backdrop-opacity on-cancel title close-button? body]
      :or {position :right-below}
      :as args}]
  {:pre [(superset? popover-content-wrapper-args (keys args))]}
  "Abstracts several components to handle the 90% of cases for general popovers and dialog boxes."
  (assert ((complement nil?) showing?) "Must specify a showing? atom")
  (let [left-offset (reagent/atom 0)
        top-offset  (reagent/atom 0)]
    (reagent/create-class
      {:component-did-mount
        (fn [me]
          (when no-clip
            (let [offsets (sum-scroll-offsets (reagent/dom-node me))]
              (reset! left-offset (:left offsets))
              (reset! top-offset  (:top  offsets)))))

       :render
        (fn []
          [:div
           {:style (merge {:flex "inherit"}
                          (when no-clip {:position "fixed"
                                         :left     (px @left-offset)
                                         :top      (px @top-offset)}))}
           (when (and @showing? on-cancel)
             [backdrop
              :opacity backdrop-opacity
              :on-click on-cancel])
           [popover-border
            :position position
            :width width
            :height height
            :title (when title [popover-title
                                :title title
                                :showing? showing?
                                :close-button? close-button?
                                :close-callback on-cancel])
            :children [body]]])}))
  )

#_(defn popover-content-wrapper
  [& {:keys [showing? position no-clip width height backdrop-opacity on-cancel title close-button? body]
      :or {position :right-below}
      :as args}]
  {:pre [(superset? popover-content-wrapper-args (keys args))]}
  "Abstracts several components to handle the 90% of cases for general popovers and dialog boxes."
  (assert ((complement nil?) showing?) "Must specify a showing? atom")
  [:div
   {:style (merge {:flex "inherit"}
                  (when no-clip {:position "fixed"
                                 :left "20px"
                                 :top "20px"}))}
   (when (and @showing? on-cancel)
     [backdrop
      :opacity backdrop-opacity
      :on-click on-cancel])
   [popover-border
    :position position
    :width    width
    :height   height
    :title    (when title [popover-title
                           :title title
                           :showing? showing?
                           :close-button? close-button?
                           :close-callback on-cancel])
    :children [body]]])


;;--------------------------------------------------------------------------------------------------
;; Component: popover-anchor-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-anchor-wrapper-args
  #{:showing?   ; A reagent atom with boolean, which controls whether the popover is showing or not.
    :position   ; Place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below.
    :anchor     ; The markup which the popover is attached to.
    :popover    ; Popover body component.
    })


(defn popover-anchor-wrapper
  [& {:keys [showing? position anchor popover] :as args}]
  {:pre [(superset? popover-anchor-wrapper-args (keys args))]}
  "Renders an element or control along with a Bootstrap popover."
  (let [[orientation arrow-pos] (split-keyword position "-") ;; only need orientation here
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")]
    [:div {:class  "rc-popover"
            :style {:display "inline-flex"
                   :flex     "inherit"}}
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
                  :display  "inline-flex"
                  :flex     "auto"}}
         popover])
      (when-not place-anchor-before? anchor)]]))


;;--------------------------------------------------------------------------------------------------
;; Component: make-button
;;--------------------------------------------------------------------------------------------------

(def make-button-args
  #{:showing?   ; The atom used to hide/show the popover.
    :type       ; Button type (string): default, primary, success, info, warning, danger, link.
    :label      ; Label for the button.
    :style      ; Custom style for the button.
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
  #{:showing?   ; The atom used to hide/show the popover.
    :toggle-on  ; Determine how to show popover:
                ;  - :mouse  Make this a hover popover (tooltip)
                ;  - :click  Make it a click popover
    :label      ; Label for the link.
    :style      ; Custom style for the link.
    })


(defn make-link
  [& {:keys [showing? toggle-on label style] :as args}]
  {:pre [(superset? make-link-args (keys args))]}
  "Renders a link designed to go into a popover.
   It provides the functionality to either toggle the popover when the button is pressed or show/hide
   on houseover/mouseout."
  (let [show   #(reset! showing? true)
        hide   #(reset! showing? false)
        toggle #(reset! showing? (not @showing?))]
    [:a
     (merge {:class "rc-make-link"}
            {:style (merge {:flex "inherit"
                           :cursor (if (= toggle-on :mouse) "help" "pointer")}
                           style)}
            (if (= toggle-on :mouse)
              {:on-mouse-over show
               :on-mouse-out  hide}
              {:on-click      toggle}))
     label]))
