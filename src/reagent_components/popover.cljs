(ns reagent-components.popover
  (:require [reagent-components.util :as util]
            [reagent.core :as reagent]))


(defn point [x y] (str x "," y " "))


(defn px [val & negative] (str (when negative "-") val "px"))


(defn split-keyword [kw delimiter] ;; TODO: Possibly move to util
  "Takes a single keyword separated by a delimiter character string (usually "-") and makes two keywords from it
  Returns a vector with the two new keywords"

  (let [keywords (clojure.string/split (str kw) (re-pattern (str "[" delimiter ":]")))] ;; (:require [clojure.string :as cstr])
    [(keyword (keywords 1)) (keyword (keywords 2))]))


(defn make-close-button [popover-to-close?]
  [:button.close {:type "button"
                  :style {:font-size "36px" :margin-top "-8px"}
                  :on-click #(reset! popover-to-close? false)} "×"])


(defn calc-popover-pos [pop-id pop-orient pop-offset]
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
      #_(util/console-log (str "in calc-popover-pos: pop-offset=" pop-offset ", p-width=" p-width ", p-height=" p-height))
      {:left popover-left :top popover-top :right popover-right :bottom popover-bottom}
      )
    nil))


(defn make-popover-arrow [orientation pop-offset arrow-length arrow-width grey-arrow]
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


(defn make-popover [{:keys [show-popover? close-button? position title body width height arrow-length arrow-width backdrop-callback backdrop-transparency]
                       :or {close-button? false position :right-below body "{empty body}" width 250 arrow-length 11 arrow-width 22 backdrop-transparency 0.1}}]
  "Renders an element or control along with a Bootstrap popover
  Parameters:
  - popover-params map
  .  - :show-popover?     [nil           ] a reagent atom with boolean, which controls whether the popover is showing or not
  .  - :close-button?     [false         ] a boolean indicating whether a close button will be added to the popover title
  .  - :position          [:right-below  ] place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below
  .  - :title             [nil           ] popover title (nil for no title)
  .  - :body              ["{empty body}"] popover body (a string or a hiccup vector or function returning a hiccup vector)
  .  - :width             [250           ] a CSS string representing the popover width in pixels (or nil or omit parameter for auto)
  .  - :height            [auto          ] a CSS string representing the popover height in pixels (or nil or omit parameter for auto)
  .  - :arrow-length      [11            ] length in pixels of arrow (from pointy part to middle of arrow base)
  .  - :arrow-width       [22            ] length in pixels of arrow base
  .  - :backdrop-callback [nil           ] NOT YET IMPLEMENTED: if specified, add a backdrop div between the main screen (including element) and the popover.
  .                                        when clicked, this callback is called (usually to close the popover)
  .  - :backdrop-opacity  [0.1           ] NOT YET IMPLEMENTED: 0 = transparent, 1 = black (http://jsfiddle.net/Rt9BJ/1)"

  (let [rendered-once           (reagent/atom false)
        pop-id                  (gensym "popover-")
        [orientation arrow-pos] (split-keyword position "-")
        grey-arrow              (and title (or (= orientation :below) (= arrow-pos :below)))]

    #_(util/console-log (str "in popover ("
                           "orientation="  orientation
                           ", arrow-pos="  arrow-pos
                           ", grey-arrow=" grey-arrow
                           ", title="      title
                           ", w="          (if (nil? width) "auto" width)
                           ", h="          (if (nil? height) "auto" height)
                           ")"))
    (reagent/create-class
     {
      :component-did-mount
      (fn []
        #_(util/console-log "make-popover :component-did-mount")
        (reset! rendered-once true))

      :render
      (fn []
        #_(util/console-log "make-popover :render")
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
                          {:display "block" :max-width "none" :padding (px 0)})}

           [make-popover-arrow orientation pop-offset arrow-length arrow-width grey-arrow]
           (when title [:h3.popover-title [:div title (when close-button? [make-close-button show-popover?])]])
           [:div.popover-content body]]
          ))
      })))


(defn popover [position show-popover? anchor popover-content popover-options]
  "Renders an element or control along with a Bootstrap popover
  Parameters:
  - position              place popover relative to the anchor :above-left/center/right, :below-left/center/right, :left-above/center/below, :right-above/center/below
  - show-popover?         a reagent atom with boolean, which controls whether the popover is showing or not
  - anchor                the hiccup markup which the popover is attached to
  - popover-content map
  .  - :width             a CSS string representing the popover width in pixels (or nil or omit parameter for auto)
  .  - :height            a CSS string representing the popover height in pixels (or nil or omit parameter for auto)
  .  - :title             popover title (nil for no title)
  .  - :close-button?     a boolean indicating whether a close button will be added to the popover title
  .  - :body              popover body (a string or a hiccup vector or function returning a hiccup vector)
  - popover-options map
  .  - :arrow-length      length in pixels of arrow (from pointy part to middle of arrow base)
  .  - :arrow-width       length in pixels of arrow base
  .  - :backdrop-callback NOT YET IMPLEMENTED: if specified, add a backdrop div between the main screen (including element) and the popover.
  .                       when clicked, this callback is called (usually to close the popover)
  .  - :backdrop-opacity  NOT YET IMPLEMENTED: 0 = transparent, 1 = black (http://jsfiddle.net/Rt9BJ/1)"

  (let [[orientation arrow-pos] (split-keyword position "-")
        place-anchor-before?    (case orientation (:left :above) false true)
        flex-flow               (case orientation (:left :right) "row" "column")
        popover-params          (merge {:position position :show-popover? show-popover?} popover-content popover-options)
        backdrop-callback       (:backdrop-callback popover-params)
        backdrop-opacity        (:backdrop-opacity popover-params)]

    [:div {:style {:display "inline-block"}}
     (when (and @show-popover? backdrop-callback)
       [:div {:style {:position "fixed"
                      :left "0px"
                      :top "0px"
                      :width "100%"
                      :height "100%"
                      :background-color "black"
                      :opacity backdrop-opacity}
              :on-click backdrop-callback}])
     [:div {:style {:display "inline-flex" :flex-flow flex-flow :align-items "center"}}
      (when place-anchor-before? anchor)
      (when @show-popover?
        [:div {:style {:position "relative" :display "inline-block"}} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
         [make-popover popover-params]])
      (when-not place-anchor-before? anchor)
      ]]))


(defn make-button [show-popover? type text]
  [:input.btn
   {:class (str "btn-" type) ;; TODO: Needs validation
    :type "button"
    :value text
    ;; :style {} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
    :on-click #(reset! show-popover? (not @show-popover?))}])


(defn make-link [show-popover? toggle-on text]
  (let [show   #(reset! show-popover? true)
        hide   #(reset! show-popover? false)
        toggle #(reset! show-popover? (not @show-popover?))]
    [:a
     (merge {;; :value text
             ;; :style {} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
             }
            (if (= toggle-on :mouse)
              {:on-mouse-over show
               :on-mouse-out  hide}
              {:on-click      toggle}
              ))
     text]))
