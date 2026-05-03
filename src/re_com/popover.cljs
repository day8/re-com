(ns re-com.popover
  (:require-macros
   [re-com.core         :refer [handler-fn at reflect-current-component]]
   [re-com.validate     :refer [validate-args-macro]])
  (:require
   [re-com.args         :as args]
   [re-com.box          :refer [box h-box v-box flex-child-style flex-flow-style align-style]]
   [re-com.close-button :refer [close-button]]
   [re-com.config       :refer [include-args-desc?]]
   [re-com.debug        :as debug]
   [re-com.part         :as part]
   re-com.popover.theme
   [re-com.popover-title :as-alias pt]
   [re-com.popover-border :as-alias pb]
   [re-com.popover-content-wrapper :as-alias pcw]
   [re-com.popover-anchor-wrapper :as-alias paw]
   [re-com.popover-tooltip :as-alias ptip]
   [re-com.theme        :as theme]
   [re-com.theme.util   :as tu]
   [re-com.util         :refer [get-element-by-id px deref-or-value]]
   [re-com.validate     :refer [position? position-options-list popover-status-type? popover-status-types-list number-or-string?
                                string-or-hiccup? css-style? html-attr? css-class?]]
   [clojure.string      :as    string]
   [reagent.core        :as    reagent]
   [reagent.ratom       :refer-macros [reaction]]))

(defn point
  [x y]
  (str x "," y " "))

(defn- split-keyword
  "Return the vector of the two keywords formed by splitting another keyword 'kw' on an internal delimiter (usually '-')
   (split-keyword  :above-left  \"-\") => [:above :left]"
  [kw delimiter]
  (let [keywords (string/split (str kw) (re-pattern (str "[" delimiter ":]")))]
    [(keyword (keywords 1)) (keyword (keywords 2))]))

(defn- calc-popover-pos
  [pop-orient p-width p-height pop-offset arrow-length arrow-gap]
  (let [total-offset   (+ arrow-length arrow-gap)
        popover-left   (case pop-orient
                         :left           "initial"
                         :right          (px total-offset)
                         (:above :below) (px (or pop-offset (/ p-width 2)) :negative))
        popover-top    (case pop-orient
                         (:left :right)  (px (or pop-offset (/ p-height 2)) :negative)
                         :above          "initial"
                         :below          (px total-offset))
        popover-right  (case pop-orient
                         :left           (px total-offset)
                         :right          nil
                         :above          nil
                         :below          nil)
        popover-bottom (case pop-orient
                         :left           nil
                         :right          nil
                         :above          (px total-offset)
                         :below          nil)]
    {:left popover-left :top popover-top :right popover-right :bottom popover-bottom}))

(defn calculate-optimal-position
  [[x y]]
  (let [w (.-innerWidth   js/window)
        h (.-innerHeight  js/window)
        h-threshold-left  (quot w 3)
        h-threshold-cent  (* 2 h-threshold-left)
        h-position        (cond
                            (< x h-threshold-left) "right"
                            (< x h-threshold-cent) "center"
                            :else "left")
        v-threshold       (quot h 2)
        v-position        (if (< y v-threshold) "below" "above")]
    (keyword (str v-position \- h-position))))

(defn calc-element-midpoint
  [node]
  (let [bounding-rect (.getBoundingClientRect node)]
    [(/ (+ (.-right  bounding-rect) (.-left bounding-rect)) 2)
     (/ (+ (.-bottom bounding-rect) (.-top  bounding-rect)) 2)]))

;;--------------------------------------------------------------------------------------------------
;; Component: backdrop  (private helper; not on the modern parts/theme system)
;;--------------------------------------------------------------------------------------------------

(def backdrop-args-desc
  (when include-args-desc?
    [{:name :opacity  :required false :default 0.0 :type "double | string" :validate-fn number-or-string? :description [:span "opacity of backdrop from:" [:br] "0.0 (transparent) to 1.0 (opaque)"]}
     {:name :on-click :required false              :type "-> nil"          :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the backdrop is clicked"}
     {:name :class    :required false              :type "string"          :validate-fn css-class?        :description "CSS class names, space separated"}
     {:name :style    :required false              :type "CSS style map"   :validate-fn css-style?        :description "override component style(s) with a style map, only use in case of emergency (applies to the outer container)"}
     {:name :attr     :required false              :type "HTML attr map"   :validate-fn html-attr?        :description [:span "HTML attributes, like " [:code ":on-mouse-move"] [:br] "No " [:code ":class"] " or " [:code ":style"] "allowed (applies to the outer container)"]}
     args/src
     args/debug-as]))

(defn- backdrop
  "Renders a backdrop div which fills the entire page and responds to clicks on it. Can also specify how transparent it should be"
  [& {:keys [opacity on-click class style attr] :as args}]
  (or
   (validate-args-macro backdrop-args-desc args)
   [:div
    (merge
     {:class    (str "noselect rc-backdrop " class)
      :style    (merge
                 {:position         "fixed"
                  :left             "0px"
                  :top              "0px"
                  :width            "100%"
                  :height           "100%"
                  :background-color "black"
                  :opacity          (or opacity 0.0)}
                 style)
      :on-click (handler-fn (on-click))}
     (debug/->attr args)
     attr)]))

;;--------------------------------------------------------------------------------------------------
;; Component: popover-title
;;--------------------------------------------------------------------------------------------------

(def popover-title-part-structure
  [::pt/wrapper {:tag :h3}
   [::pt/container {:impl 're-com.core/h-box}
    [::pt/title {:top-level-arg? true}]
    [::pt/close-button {:impl 're-com.close-button/close-button}]]])

(def popover-title-parts-desc
  (when include-args-desc?
    (part/describe popover-title-part-structure)))

(def popover-title-parts
  (when include-args-desc?
    (-> (map :name popover-title-parts-desc) set)))

(def popover-title-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :showing?       :required true                 :type "boolean r/atom" :description "an r/atom. When the value is true, the popover shows."}
       {:name :close-button?  :required false  :default true :type "boolean"        :description "when true, displays the close button"}
       {:name :close-callback :required false                :type "-> nil"         :validate-fn fn? :description [:span "a function which takes no params and returns nothing. Called when the close button is pressed. Not required if " [:code ":showing?"] " atom passed in OR " [:code ":close-button?"] " is set to false"]}
       args/class
       args/style
       args/attr
       (args/parts popover-title-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args popover-title-part-structure)))))

(defn popover-title
  "Renders a title at the top of a popover with an optional close button on the far right"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn popover-title-render
      [& {:keys [showing? close-button? close-callback]
          :or   {close-button? true}
          :as   args}]
      (or
       (validate-args-macro popover-title-args-desc args)
       (let [part   (partial part/part popover-title-part-structure args)
             title? (part/get-part popover-title-part-structure args ::pt/title)
             re-com {:state {:close-button? close-button?}}]
         (part ::pt/wrapper
           {:theme      theme
            :post-props (-> args
                            (select-keys [:class :style :attr])
                            (debug/instrument args))
            :props {:tag      :h3
                    :re-com   re-com
                    :children
                    [(part ::pt/container
                       {:impl  h-box
                        :theme theme
                        :post-props {:src (at)}
                        :props {:re-com   re-com
                                :children
                                [(when title?
                                   (part ::pt/title
                                     {:theme theme
                                      :props {:re-com re-com}}))
                                 (when close-button?
                                   (part ::pt/close-button
                                     {:impl       close-button
                                      :theme      theme
                                      :post-props {:src (at)}
                                      :props      {:re-com      re-com
                                                   :on-click    #(if close-callback
                                                                   (close-callback)
                                                                   (reset! showing? false))
                                                   :div-size    0
                                                   :font-size   26
                                                   :top-offset  -1
                                                   :left-offset -5}}))]}})]}}))))))

;;--------------------------------------------------------------------------------------------------
;; Component: popover-border
;;--------------------------------------------------------------------------------------------------

(defn next-even-integer
  [num]
  (-> num inc (/ 2) int (* 2)))

(defn calc-pop-offset
  [arrow-pos position-offset p-width p-height]
  (case arrow-pos
    :center nil
    :right  (+ 20 position-offset)
    :below  (+ 20 position-offset)
    :left   (if p-width (- (- p-width 25) position-offset) p-width)
    :above  (if p-height (- (- p-height 25) position-offset) p-height)))

(defn popover-clipping
  [node]
  (let [viewport-width  (.-innerWidth   js/window)
        viewport-height (.-innerHeight  js/window)
        bounding-rect   (.getBoundingClientRect node)
        left            (.-left   bounding-rect)
        right           (.-right  bounding-rect)
        top             (.-top    bounding-rect)
        bottom          (.-bottom bounding-rect)
        clip-left       (when (< left 0) (- left))
        clip-right      (when (> right viewport-width) (- right viewport-width))
        clip-top        (when (< top 0) (- top))
        clip-bottom     (when (> bottom viewport-height) (- bottom viewport-height))]
    (or (some? clip-left) (some? clip-right) (some? clip-top) (some? clip-bottom))))

(def popover-border-part-structure
  [::pb/wrapper
   [::pb/arrow {:tag :svg}]
   [::pb/content {:tag :div}]])

(def popover-border-parts-desc
  (when include-args-desc?
    (part/describe popover-border-part-structure)))

(def popover-border-parts
  (when include-args-desc?
    (-> (map :name popover-border-parts-desc) set)))

(def popover-border-args-desc
  (when include-args-desc?
    (vec
     (concat
      [args/children
       {:name :position             :required true                        :type "keyword r/atom"   :validate-fn position?                     :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :optimize-position?   :required false :default true         :type "boolean"                                                     :description "When true, dynamically repositions the popover body to fit within the available viewport space."}
       {:name :position-offset      :required false                       :type "integer"          :validate-fn number?                       :description [:span "px offset of the arrow from its default " [:code ":position"] " along the popover border. Is ignored when " [:code ":position"] " is one of the " [:code ":xxx-center"] " variants. Positive numbers slide the popover toward its center"]}
       {:name :width                :required false                       :type "string"           :validate-fn string?                       :description "a CSS style describing the popover width"}
       {:name :height               :required false :default "auto"       :type "string"           :validate-fn string?                       :description "a CSS style describing the popover height"}
       {:name :popover-color        :required false :default "white"      :type "string"           :validate-fn string?                       :description "fill color of the popover"}
       {:name :popover-border-color :required false                       :type "string"           :validate-fn string?                       :description "color of the popover border, including the arrow"}
       {:name :arrow-length         :required false :default 11           :type "integer | string" :validate-fn number-or-string?             :description "the length in pixels of the arrow (from pointy part to middle of arrow base)"}
       {:name :arrow-width          :required false :default 22           :type "integer | string" :validate-fn number-or-string?             :description "the width in pixels of arrow base"}
       {:name :arrow-gap            :required false :default -1           :type "integer"          :validate-fn number?                       :description "px gap between the anchor and the arrow tip. Positive numbers push the popover away from the anchor"}
       {:name :padding              :required false                       :type "string"           :validate-fn string?                       :description "a CSS style which overrides the inner padding of the popover"}
       {:name :margin-left          :required false                       :type "string"           :validate-fn string?                       :description "a CSS style describing the horiztonal offset from anchor after position"}
       {:name :margin-top           :required false                       :type "string"           :validate-fn string?                       :description "a CSS style describing the vertical offset from anchor after position"}
       {:name :tooltip-style?       :required false :default false        :type "boolean"                                                     :description "setup popover styles for a tooltip"}
       {:name :title                :required false                       :type "string | markup"                                             :description "describes a title"}
       args/class
       args/style
       args/attr
       (args/parts popover-border-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args popover-border-part-structure)))))

(defn popover-border
  "Renders an element or control along with a Bootstrap popover"
  [& {:keys [position position-offset optimize-position? pre-theme theme]
      :or   {optimize-position? true}
      :as   args}]
  (or
   (validate-args-macro popover-border-args-desc args)
   (let [theme          (theme/comp pre-theme theme)
         pop-id         (gensym "popover-")
         rendered-once  (reagent/atom false)
         ready-to-show? (reagent/atom false)
         p-width        (reagent/atom 0)
         p-height       (reagent/atom 0)
         pop-offset     (reagent/atom 0)
         found-optimal  (reagent/atom false)
         !pop-border    (atom nil)
         ref!           (partial reset! !pop-border)
         calc-metrics   (fn [pos]
                          (let [popover-elem            (get-element-by-id pop-id)
                                [orientation arrow-pos] (split-keyword pos "-")
                                grey-arrow?             (and (:title args) (or (= orientation :below) (= arrow-pos :below)))]
                            (reset! p-width    (if popover-elem (next-even-integer (.-clientWidth  popover-elem)) 0))
                            (reset! p-height   (if popover-elem (next-even-integer (.-clientHeight popover-elem)) 0))
                            (reset! pop-offset (calc-pop-offset arrow-pos position-offset @p-width @p-height))
                            [orientation grey-arrow?]))]
     (reagent/create-class
      {:display-name "popover-border"

       :component-did-mount
       (fn []
         (reset! rendered-once true))

       :component-did-update
       (fn [_this]
         (let [clipped?    (popover-clipping @!pop-border)
               anchor-node (-> @!pop-border .-parentNode .-parentNode .-parentNode)]
           (when (and clipped? (deref-or-value optimize-position?) (not @found-optimal))
             (reset! position (calculate-optimal-position (calc-element-midpoint anchor-node)))
             (reset! found-optimal true))
           (calc-metrics @position)
           (reset! ready-to-show? true)))

       :reagent-render
       (fn popover-border-render
         [& {:keys [children position width height popover-color popover-border-color arrow-length
                    arrow-width arrow-gap padding margin-left margin-top tooltip-style? title]
             :or {arrow-length 11 arrow-width 22 arrow-gap -1}
             :as args}]
         (or
          (validate-args-macro popover-border-args-desc args)
          (let [part                      (partial part/part popover-border-part-structure args)
                [orientation grey-arrow?] (calc-metrics @position)
                half-arrow-width          (/ arrow-width 2)
                arrow-shape               {:left  (str (point 0 0)            (point arrow-length half-arrow-width) (point 0 arrow-width))
                                           :right (str (point arrow-length 0) (point 0 half-arrow-width)            (point arrow-length arrow-width))
                                           :above (str (point 0 0)            (point half-arrow-width arrow-length) (point arrow-width 0))
                                           :below (str (point 0 arrow-length) (point half-arrow-width 0)            (point arrow-width arrow-length))}
                re-com                    {:state {:orientation     orientation
                                                   :tooltip-style?  tooltip-style?
                                                   :grey-arrow?     grey-arrow?
                                                   :popover-color   popover-color}}]
            (part ::pb/wrapper
              {:theme      theme
               :post-props (-> args
                               (select-keys [:class :style :attr])
                               (assoc-in [:attr :id] pop-id)
                               (tu/style (if @rendered-once
                                           (when pop-id (calc-popover-pos orientation @p-width @p-height @pop-offset arrow-length arrow-gap))
                                           {:top "-10000px" :left "-10000px"}))
                               (cond-> width                (tu/style {:width width})
                                       height               (tu/style {:height height})
                                       popover-color        (tu/style {:background-color popover-color})
                                       popover-border-color (tu/style {:border-color popover-border-color})
                                       tooltip-style?       (tu/style {:border-radius "4px"
                                                                       :box-shadow    "none"
                                                                       :border        "none"}))
                               (tu/style (case orientation
                                           :left                  {:margin-left  "-2000px"}
                                           (:right :above :below) {:margin-right "-2000px"}))
                               (cond-> margin-left (tu/style {:margin-left margin-left})
                                       margin-top  (tu/style {:margin-top  margin-top}))
                               (tu/style {:display   "block"
                                          :opacity   (if @ready-to-show? "1" "0")
                                          :max-width "none"
                                          :padding   "0px"})
                               (debug/instrument args)
                               (assoc-in [:attr :ref] ref!))
               :props      {:re-com   re-com
                            :children
                            [(part ::pb/arrow
                               {:theme theme
                                :props {:tag      :svg
                                        :re-com   re-com
                                        :attr     {:width  (px (case orientation
                                                                 (:left  :right) arrow-length
                                                                 (:above :below) arrow-width))
                                                   :height (px (case orientation
                                                                 (:left  :right) arrow-width
                                                                 (:above :below) arrow-length))}
                                        :style    (merge
                                                   {:position "absolute"
                                                    (case orientation
                                                      :left  :right
                                                      :right :left
                                                      :above :bottom
                                                      :below :top) (px arrow-length :negative)
                                                    (case orientation
                                                      (:left  :right) :top
                                                      (:above :below) :left) (if (nil? @pop-offset) "50%" (px @pop-offset))
                                                    (case orientation
                                                      (:left  :right) :margin-top
                                                      (:above :below) :margin-left) (px half-arrow-width :negative)})
                                        :children [[:polyline {:points (arrow-shape orientation)
                                                               :style  {:fill         (if popover-color
                                                                                        popover-color
                                                                                        (if grey-arrow? "#f7f7f7" "white"))
                                                                        :stroke       (or popover-border-color (when-not tooltip-style? "rgba(0, 0, 0, .2)"))
                                                                        :stroke-width "1"}}]]}})
                             (when title title)
                             (part ::pb/content
                               {:theme      theme
                                :post-props {:style (when padding {:padding padding})}
                                :props      {:tag      :div
                                             :re-com   re-com
                                             :children children}})]}}))))}))))

;;--------------------------------------------------------------------------------------------------
;; Component: popover-content-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-content-wrapper-part-structure
  [::pcw/wrapper
   [::pcw/backdrop {:impl 're-com.popover/backdrop}]
   [::pcw/border {:impl 're-com.popover/popover-border}
    [::pcw/title-bar {:impl 're-com.popover/popover-title}]]])

(def popover-content-wrapper-parts-desc
  (when include-args-desc?
    (part/describe popover-content-wrapper-part-structure)))

(def popover-content-wrapper-parts
  (when include-args-desc?
    (-> (map :name popover-content-wrapper-parts-desc) set)))

(def popover-content-wrapper-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :showing-injected?    :required true                         :type "boolean r/atom"                                  :description [:span "an atom or value. When the value is true, the popover shows." [:br] [:strong "NOTE: "] "When used as direct " [:code ":popover"] " arg in popover-anchor-wrapper, this arg will be injected automatically by popover-anchor-wrapper. If using your own popover function, you must add this yourself"]}
       {:name :position-injected    :required true                         :type "keyword r/atom"   :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list [:br] [:strong "NOTE: "] "See above NOTE for " [:code ":showing-injected?"] ". Same applies"]}
       {:name :optimize-position?   :required false :default true          :type "boolean"                                         :description "When true, dynamically repositions the popover body to fit within the available viewport space."}
       {:name :position-offset      :required false                        :type "integer"          :validate-fn number?           :description [:span "px offset of the arrow from its default " [:code ":position"] " along the popover border."]}
       {:name :no-clip?             :required false  :default false        :type "boolean"                                         :description "when an anchor is in a scrolling region (e.g. scroller component), the popover can sometimes be clipped."}
       {:name :width                :required false                        :type "string"           :validate-fn string?           :description "a CSS style representing the popover width"}
       {:name :height               :required false                        :type "string"           :validate-fn string?           :description "a CSS style representing the popover height"}
       {:name :backdrop-opacity     :required false  :default 0.0          :type "double | string"  :validate-fn number-or-string? :description "indicates the opacity of the backdrop where 0.0=transparent, 1.0=opaque"}
       {:name :on-cancel            :required false                        :type "-> nil"           :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the popover is cancelled (e.g. user clicks away)"}
       {:name :title                :required false                        :type "string | hiccup"  :validate-fn string-or-hiccup? :description "describes the title of the popover."}
       {:name :close-button?        :required false  :default true         :type "boolean"                                         :description "when true, displays the close button"}
       {:name :body                 :required false                        :type "string | hiccup"  :validate-fn string-or-hiccup? :description "describes the popover body. Must be a single component"}
       {:name :tooltip-style?       :required false  :default false        :type "boolean"                                         :description "setup popover styles for a tooltip"}
       {:name :popover-color        :required false  :default "white"      :type "string"           :validate-fn string?           :description "fill color of the popover"}
       {:name :popover-border-color :required false                        :type "string"           :validate-fn string?           :description "color of the popover border, including the arrow"}
       {:name :arrow-length         :required false  :default 11           :type "integer | string" :validate-fn number-or-string? :description "the length in pixels of the arrow"}
       {:name :arrow-width          :required false  :default 22           :type "integer | string" :validate-fn number-or-string? :description "the width in pixels of arrow base"}
       {:name :arrow-gap            :required false  :default -1           :type "integer"          :validate-fn number?           :description "px gap between the anchor and the arrow tip"}
       {:name :padding              :required false                        :type "string"           :validate-fn string?           :description "a CSS style which overrides the inner padding of the popover"}
       args/class
       args/style
       args/attr
       (args/parts popover-content-wrapper-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args popover-content-wrapper-part-structure)))))

(defn popover-content-wrapper
  "Abstracts several components to handle the 90% of cases for general popovers and dialog boxes"
  [& {:keys [no-clip? pre-theme theme] :as args}]
  (or
   (validate-args-macro popover-content-wrapper-args-desc args)
   (let [theme                    (theme/comp pre-theme theme)
         left-offset              (reagent/atom 0)
         top-offset               (reagent/atom 0)
         !ref                     (atom nil)
         ref!                     (partial reset! !ref)
         position-no-clip-popover (fn position-no-clip-popover [_this]
                                    (when no-clip?
                                      (let [popover-point-node (.-parentNode @!ref)
                                            bounding-rect      (.getBoundingClientRect popover-point-node)]
                                        (reset! left-offset (.-left bounding-rect))
                                        (reset! top-offset  (.-top  bounding-rect)))))]
     (reagent/create-class
      {:display-name "popover-content-wrapper"

       :component-did-mount
       (fn [this]
         (position-no-clip-popover this))

       :component-did-update
       (fn [this]
         (position-no-clip-popover this))

       :reagent-render
       (fn popover-content-wrapper-render
         [& {:keys [showing-injected? position-injected position-offset no-clip? width height backdrop-opacity on-cancel
                    title close-button? body tooltip-style? popover-color popover-border-color arrow-length arrow-width
                    arrow-gap padding optimize-position?]
             :or   {arrow-length       11
                    arrow-width        22
                    arrow-gap          -1
                    optimize-position? true}
             :as   args}]
         (or
          (validate-args-macro popover-content-wrapper-args-desc args)
          (do
            @position-injected
            (let [part   (partial part/part popover-content-wrapper-part-structure args)
                  re-com {:state {:no-clip?       no-clip?
                                  :tooltip-style? tooltip-style?
                                  :showing?       (deref-or-value showing-injected?)}}]
              (part ::pcw/wrapper
                {:theme      theme
                 :post-props (-> args
                                 (select-keys [:class :style :attr])
                                 (cond-> no-clip? (tu/style {:position "fixed"
                                                             :left     (px @left-offset)
                                                             :top      (px @top-offset)}))
                                 (debug/instrument args)
                                 (assoc-in [:attr :ref] ref!))
                 :props      {:re-com re-com
                              :children
                              [(when (and (deref-or-value showing-injected?) on-cancel)
                                 (part ::pcw/backdrop
                                   {:impl       backdrop
                                    :theme      theme
                                    :post-props {:src (at)}
                                    :props      {:re-com   re-com
                                                 :opacity  backdrop-opacity
                                                 :on-click on-cancel}}))
                               (part ::pcw/border
                                 {:impl       popover-border
                                  :theme      theme
                                  :post-props {:src (at)}
                                  :props      {:re-com               re-com
                                               :position             position-injected
                                               :position-offset      position-offset
                                               :width                width
                                               :height               height
                                               :tooltip-style?       tooltip-style?
                                               :popover-color        popover-color
                                               :popover-border-color popover-border-color
                                               :optimize-position?   optimize-position?
                                               :arrow-length         arrow-length
                                               :arrow-width          arrow-width
                                               :arrow-gap            arrow-gap
                                               :padding              padding
                                               :title                (when title
                                                                       (part ::pcw/title-bar
                                                                         {:impl       popover-title
                                                                          :theme      theme
                                                                          :post-props {:src (at)}
                                                                          :props      {:re-com         re-com
                                                                                       :title          title
                                                                                       :showing?       showing-injected?
                                                                                       :close-button?  close-button?
                                                                                       :close-callback on-cancel}}))
                                               :children             [body]}})]}})))))}))))

;;--------------------------------------------------------------------------------------------------
;; Component: popover-anchor-wrapper
;;--------------------------------------------------------------------------------------------------

(def popover-anchor-wrapper-part-structure
  [::paw/wrapper
   [::paw/point-wrapper
    [::paw/point]]])

(def popover-anchor-wrapper-parts-desc
  (when include-args-desc?
    (part/describe popover-anchor-wrapper-part-structure)))

(def popover-anchor-wrapper-parts
  (when include-args-desc?
    (-> (map :name popover-anchor-wrapper-parts-desc) set)))

(def popover-anchor-wrapper-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :showing? :required true                        :type "boolean r/atom"                                 :description "an atom or value. When the value is true, the popover shows"}
       {:name :position :required true                        :type "keyword"         :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :anchor   :required true                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "the component the popover is attached to"}
       {:name :popover  :required true                        :type "string | hiccup" :validate-fn string-or-hiccup? :description "the popover body component"}
       args/class
       args/style
       args/attr
       (args/parts popover-anchor-wrapper-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args popover-anchor-wrapper-part-structure)))))

(defn popover-anchor-wrapper
  "Renders an element or control along with a Bootstrap popover"
  [& {:keys [showing? position pre-theme theme] :as args}]
  (or
   (validate-args-macro popover-anchor-wrapper-args-desc args)
   (let [theme             (theme/comp pre-theme theme)
         external-position (reagent/atom position)
         internal-position (reagent/atom @external-position)
         reset-on-hide     (reaction (when-not (deref-or-value showing?) (reset! internal-position @external-position)))]
     (reagent/create-class
      {:display-name "popover-anchor-wrapper"

       :reagent-render
       (fn popover-anchor-wrapper-render
         [& {:keys [showing? position anchor popover] :as args}]
         (or
          (validate-args-macro popover-anchor-wrapper-args-desc args)
          (do
            @reset-on-hide
            (when (not= @external-position position)
              (reset! external-position position)
              (reset! internal-position @external-position))
            (let [part                      (partial part/part popover-anchor-wrapper-part-structure args)
                  [orientation _arrow-pos]  (split-keyword @internal-position "-")
                  place-anchor-before?      (case orientation (:left :above) false true)
                  flex-flow                 (case orientation (:left :right) "row" "column")
                  popover-fn                (first popover)
                  re-com                    {:state {:orientation orientation
                                                     :showing?    (deref-or-value showing?)}}]
              (part ::paw/wrapper
                {:theme      theme
                 :post-props (-> args
                                 (select-keys [:class :style :attr])
                                 (debug/instrument args))
                 :props      {:re-com   re-com
                              :children
                              [(part ::paw/point-wrapper
                                 {:theme theme
                                  :post-props {:style (flex-flow-style flex-flow)}
                                  :props {:re-com re-com
                                          :children
                                          [(when place-anchor-before? anchor)
                                           (when (deref-or-value showing?)
                                             (part ::paw/point
                                               {:theme theme
                                                :props {:re-com re-com
                                                        :children
                                                        [(cond
                                                           (keyword? (get popover 1))
                                                           (into popover [:showing-injected? showing?
                                                                          :position-injected internal-position])
                                                           (map? (get popover 1))
                                                           [popover-fn (merge (get popover 1)
                                                                              {:showing-injected? showing?
                                                                               :position-injected internal-position})]
                                                           :else
                                                           (into popover [:showing-injected? showing?
                                                                          :position-injected internal-position]))]}}))
                                           (when-not place-anchor-before? anchor)]}})]}})))))}))))

;;--------------------------------------------------------------------------------------------------
;; Component: popover-tooltip
;;--------------------------------------------------------------------------------------------------

(def popover-tooltip-part-structure
  [::ptip/wrapper {:impl 're-com.popover/popover-anchor-wrapper}
   [::ptip/content-wrapper {:impl 're-com.popover/popover-content-wrapper}
    [::ptip/v-box {:impl 're-com.box/v-box}
     [::ptip/close-button-container {:impl 're-com.box/box}
      [::ptip/close-button {:impl 're-com.close-button/close-button}]]]]])

(def popover-tooltip-parts-desc
  (when include-args-desc?
    (part/describe popover-tooltip-part-structure)))

(def popover-tooltip-parts
  (when include-args-desc?
    (-> (map :name popover-tooltip-parts-desc) set)))

(def popover-tooltip-args-desc
  (when include-args-desc?
    (vec
     (concat
      [{:name :label         :required true                         :type "string | hiccup | r/atom" :validate-fn string-or-hiccup? :description "the text (or component) for the tooltip"}
       {:name :showing?      :required true                         :type "boolean r/atom"                                          :description "an atom. When the value is true, the tooltip shows"}
       {:name :on-cancel     :required false                        :type "-> nil"                   :validate-fn fn?               :description "a function which takes no params and returns nothing. Called when the popover is cancelled (e.g. user clicks away)"}
       {:name :close-button? :required false :default false         :type "boolean"                                                 :description "when true, displays the close button"}
       {:name :status        :required false                        :type "keyword"                  :validate-fn popover-status-type? :description [:span "controls background color of the tooltip. " [:code "nil/omitted"] " for black or one of " popover-status-types-list]}
       {:name :anchor        :required true                         :type "hiccup"                   :validate-fn string-or-hiccup? :description "the component the tooltip is attached to"}
       {:name :position      :required false :default :below-center :type "keyword"                  :validate-fn position?         :description [:span "relative to this anchor. One of " position-options-list]}
       {:name :no-clip?      :required false :default true          :type "boolean"                                                 :description "when an anchor is in a scrolling region the popover can sometimes be clipped."}
       {:name :width         :required false                        :type "string"                   :validate-fn string?           :description "specifies width of the tooltip"}
       {:name :popover-color :required false :default "black"       :type "string"                   :validate-fn string?           :description "default fill color when status is nil."}
       {:name :warning-color :required false :default "#f57c00"     :type "string"                   :validate-fn string?           :description "default fill color for the warning status."}
       {:name :error-color   :required false :default "#d50000"     :type "string"                   :validate-fn string?           :description "default fill color for the error status."}
       {:name :info-color    :required false :default "#333333"     :type "string"                   :validate-fn string?           :description "default fill color for the info status."}
       {:name :success-color :required false :default "#13C200"     :type "string"                   :validate-fn string?           :description "default fill color for the success status."}
       args/class
       args/style
       args/attr
       (args/parts popover-tooltip-parts)
       args/src
       args/debug-as]
      theme/args-desc
      (part/describe-args popover-tooltip-part-structure)))))

(defn popover-tooltip
  "Renders text as a tooltip in Bootstrap popover style"
  [& {:keys [pre-theme theme]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn popover-tooltip-render
      [& {:keys [label showing? on-cancel close-button? status anchor position no-clip? width
                 popover-color warning-color error-color info-color success-color src debug-as]
          :or   {no-clip?      true
                 popover-color "black"
                 warning-color "#f57c00"
                 error-color   "#d50000"
                 info-color    "#333333"
                 success-color "#13C200"}
          :as   args}]
      (or
       (validate-args-macro popover-tooltip-args-desc args)
       (let [part           (partial part/part popover-tooltip-part-structure args)
             label          (deref-or-value label)
             popover-color  (case status
                              :warning warning-color
                              :error   error-color
                              :info    info-color
                              :success success-color
                              popover-color)
             re-com         {:state {:status        status
                                     :close-button? close-button?
                                     :info?         (= status :info)}}]
         (part ::ptip/wrapper
           {:impl       popover-anchor-wrapper
            :theme      theme
            :post-props (-> args
                            (select-keys [:class :style :attr])
                            (debug/instrument args))
            :props
            {:re-com   re-com
             :src      src
             :debug-as (or debug-as (reflect-current-component))
             :showing? showing?
             :position (or position :below-center)
             :anchor   anchor
             :popover
             (part ::ptip/content-wrapper
               {:impl       popover-content-wrapper
                :theme      theme
                :post-props {:src (at)}
                :props
                {:re-com         re-com
                 :no-clip?       no-clip?
                 :on-cancel      on-cancel
                 :width          width
                 :tooltip-style? true
                 :popover-color  popover-color
                 :padding        "3px 8px"
                 :arrow-length   6
                 :arrow-width    12
                 :arrow-gap      4
                 :body
                 (part ::ptip/v-box
                   {:impl       v-box
                    :theme      theme
                    :post-props {:src   (at)
                                 :style (if (= status :info)
                                          {:color     "white"
                                           :font-size "14px"
                                           :padding   "4px"}
                                          {:color       "white"
                                           :font-size   "12px"
                                           :font-weight "bold"
                                           :text-align  "center"})}
                    :props
                    {:re-com   re-com
                     :children
                     [(when close-button?
                        (part ::ptip/close-button-container
                          {:impl       box
                           :theme      theme
                           :post-props {:src (at)}
                           :props      {:re-com     re-com
                                        :align-self :end
                                        :child
                                        (part ::ptip/close-button
                                          {:impl       close-button
                                           :theme      theme
                                           :post-props {:src (at)}
                                           :props      {:re-com      re-com
                                                        :on-click    #(if on-cancel
                                                                        (on-cancel)
                                                                        (reset! showing? false))
                                                        :div-size    15
                                                        :font-size   20
                                                        :left-offset 5}})}}))
                      label]}})}})}}))))))
