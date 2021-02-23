(ns re-com.v-table
  (:require-macros
    [reagent.ratom      :refer [reaction]]
    [re-com.core        :refer [handler-fn at reflect-current-component]]
    [re-com.validate    :refer [validate-args-macro]])
  (:require
    [reagent.core       :as    reagent]
    [re-com.config      :refer [debug? include-args-desc?]]
    [re-com.debug       :refer [->attr]]
    [re-com.box         :as    box]
    [re-com.util        :as    util :refer [deref-or-value px-n]]
    [re-com.validate    :refer [vector-atom? ifn-or-nil? map-atom? parts?]]
    [re-com.dmm-tracker :refer [make-dmm-tracker captureMouseMoves]]))

;; The public API for this component is called table (see last component in this file)

(def scrollbar-thickness 10)
(def scrollbar-margin    2)
(def scrollbar-tot-thick (+ scrollbar-thickness (* 2 scrollbar-margin)))

(def px (memoize util/px))


(defn show-row-data-on-alt-click
  "Make a call to this function in the click event of your row renderer, then every time they Alt+Click on a row,
  The raw cljs object used to render that row will be popped into DevTools :-)
  Here is what the line might look like:
  :on-click (handler-fn (v-table/show-row-data-on-alt-click row row-index event))"
  [row row-index event]
  (when  (.-altKey event)
    (js/console.log (str "ROW-INDEX[" row-index "]") row)))


(defn scrollbar
  "Render a horizontal or vertical scrollbar

  Arguments:
   - type           [keyword] scrollbar type (:horizontal or :vertical)
   - length         [number] px size of the long edge. If not specified, scrollbar will fill space provided
   - width          [optional number, default = 10] px size of the short edge
   - content-length [number] px length of the content this scrollbar will be in charge of
   - scroll-pos     [number] current px scroll position for the beginning of the scrollbar 'thumb'
   - on-change      [fn] called every time the thumb is dragged. Args: new-scroll-pos
   - style          [map] CSS style map
   "
  [& {:keys [type width on-change]
      :or   {width 10}}]
  (let [horizontal?           (= type :horizontal)
        radius                (px (/ width 2))
        scrollbar-color       "#eee" ;; "#f3f3f3"  "rgba(0,0,0,0.05)"  ;; These colors could be passed in as a single map,
        scrollbar-hover-color "#ccc" ;; "#cccccc"  "rgba(0,0,0,0.20)"  ;; or we could add :style and :thumb-style args (wouldn't work for hover colors)
        thumb-color           "#bbb" ;; "#b7b7b7"  "rgba(0,0,0,0.25)"
        thumb-hover-color     "#999" ;; "#9a9a9a"  "rgba(0,0,0,0.30)"
        thumb-drag-color      "#777" ;; "#707070"  "rgba(0,0,0,0.45)"
        mouse-over?           (reagent/atom false)
        dragging?             (reagent/atom false)
        pos-on-scrollbar      (reagent/atom 0)
        pos-on-thumb          (reagent/atom 0)
        tracker               (atom nil)
        calcs                 (atom {})
        on-drag-change        (fn on-drag-change
                                [_delta-x _delta-y curr-x curr-y _ctrlKey _shiftKey _event]
                                (let [curr-pos                (if horizontal? curr-x curr-y)
                                      pos-on-scrollbar        (- curr-pos @pos-on-scrollbar)
                                      new-internal-scroll-pos (- pos-on-scrollbar @pos-on-thumb)
                                      beginning-or-beyond?    (<= new-internal-scroll-pos 0)
                                      end-or-beyond?          (>= new-internal-scroll-pos (:max-scroll-pos @calcs))
                                      new-external-scroll-pos (.round js/Math (* new-internal-scroll-pos (:scrollbar-content-ratio @calcs)))]
                                  (cond
                                    beginning-or-beyond? (on-change 0)
                                    end-or-beyond?       (on-change (.round js/Math (* (:max-scroll-pos @calcs) (:scrollbar-content-ratio @calcs))))
                                    :else                (on-change new-external-scroll-pos))))
        on-drag-end           (fn on-drag-end
                                [_ctrlKey _shiftKey _event]
                                (reset! dragging? false)
                                (reset! tracker nil))
        on-mouse-enter        (handler-fn (reset! mouse-over? true))
        on-mouse-leave        (handler-fn (reset! mouse-over? false))
        scrollbar-mouse-down  (fn scrollbar-mouse-down
                                [event]
                                (let [target                  (-> event .-target)
                                      bounding-rect           (if (nil? target) {} (.getBoundingClientRect target))
                                      click-pos               (if horizontal?
                                                                (- (.-clientX event) (.-left bounding-rect))
                                                                (- (.-clientY event) (.-top  bounding-rect)))
                                      op                      (if (<= click-pos (:internal-scroll-pos @calcs)) - +)
                                      new-internal-scroll-pos (+ (:internal-scroll-pos @calcs) (op (/ (:length @calcs) (:thumb-ratio @calcs))))
                                      new-external-scroll-pos (op (:scroll-pos @calcs) (:length @calcs))
                                      beginning-or-beyond?    (<= new-internal-scroll-pos 0)
                                      end-or-beyond?          (>= new-internal-scroll-pos (:max-scroll-pos @calcs))]
                                  (cond
                                    beginning-or-beyond? (on-change 0)
                                    end-or-beyond?       (on-change (.round js/Math (* (:max-scroll-pos @calcs) (:scrollbar-content-ratio @calcs))))
                                    :else                (on-change new-external-scroll-pos))))
        thumb-mouse-down      (fn thumb-mouse-down
                                [event internal-scroll-pos]
                                (let [parent                  (-> event .-target .-parentNode) ;; TODO: Best way to move this fn up? (closes over internal-scroll-pos)
                                      bounding-rect           (if (nil? parent) {} (.getBoundingClientRect parent))]
                                  (reset! pos-on-scrollbar (if horizontal?
                                                             (.-left bounding-rect)
                                                             (.-top  bounding-rect)))
                                  (reset! pos-on-thumb     (if horizontal?
                                                             (- (.-clientX event) @pos-on-scrollbar internal-scroll-pos)
                                                             (- (.-clientY event) @pos-on-scrollbar internal-scroll-pos)))
                                  (reset! tracker (make-dmm-tracker on-drag-change on-drag-end))
                                  (captureMouseMoves @tracker event)
                                  (reset! dragging? true)
                                  (.stopPropagation event)))] ;; Prevents parent div getting this mouse-down as well
    (fn scrollbar-renderer
      [& {:keys [length width content-length scroll-pos style src]
          :or   {width 10}}]
      (let [thumb-ratio             (/ content-length length)
            thumb-length            (max (* 1.5 width) (/ length thumb-ratio))
            show?                   (> content-length length)
            max-scroll-pos          (- length thumb-length)
            scrollbar-content-ratio (/ (- content-length length) max-scroll-pos)
            internal-scroll-pos     (/ scroll-pos scrollbar-content-ratio)]
        (reset! calcs {:length                  length
                       :scroll-pos              scroll-pos
                       :thumb-ratio             thumb-ratio
                       :thumb-length            thumb-length
                       :max-scroll-pos          max-scroll-pos
                       :scrollbar-content-ratio scrollbar-content-ratio
                       :internal-scroll-pos     internal-scroll-pos})
        [box/box
         :src    src
         :width  (if horizontal?
                   (when length (px length))
                   (px width))
         :height (if horizontal?
                   (px width)
                   (when length (px length)))
         :class  (str (if horizontal? "horizontal" "vertical") "-scrollbar")
         :style  (merge {:background-color (when show? (if (or @mouse-over? @dragging?)
                                                         scrollbar-hover-color
                                                         scrollbar-color))
                         :border-radius    radius
                         :overflow         "hidden"}
                        style)
         :attr   {:on-mouse-enter on-mouse-enter
                  :on-mouse-leave on-mouse-leave
                  :on-mouse-down  (handler-fn (when show? (scrollbar-mouse-down event)))} ;; TODO: Best way to move this fn to outer fn? (closes over show?)
         :child  [box/box
                  :src    (at)
                  :width  (if horizontal?
                            (px (if show? thumb-length 0))
                            (px width))
                  :height (if horizontal?
                            (px width)
                            (px (if show? thumb-length 0)))
                  :style  {:background-color (if (or @mouse-over? @dragging?)
                                               (if @dragging? thumb-drag-color thumb-hover-color)
                                               thumb-color)
                           :cursor           "default"
                           :border-radius    radius
                           (if horizontal?
                             :margin-left
                             :margin-top)    (px internal-scroll-pos)}
                  :attr   {:on-mouse-down (handler-fn (thumb-mouse-down event internal-scroll-pos))} ;; TODO: Best way to move this fn to outer fn? (closes over internal-scroll-pos)
                  :child  ""]]))))


;; ================================================================================== SECTION 1 - top-left

(defn top-left-content
  "Render section 1 - the content component"
  [top-left-renderer column-header-height class style attr]
  [box/box ;; content component
   :src    (at)
   :class  (str "rc-v-table-top-left rc-v-table-content " class)
   :style  (merge {:overflow "hidden"}
                  style)
   :attr   attr
   :height (px (or column-header-height 0))
   :child  (if top-left-renderer [top-left-renderer] "")])


;; ================================================================================== SECTION 2 - row-headers

(defn row-header-content
  "The row-header section 'content' component. Takes a function that renders row-headers and draws all of
  them in section 2 (sections explained below).
  When in virtual? mode, only a screen-full of row-headers are passed to this component at any one time.
  This component is also responsible for setting the vertical scroll position of this section based on scroll-y

  Arguments:
   - row-header-renderer function that knows how to render row-headers (will be passed the 0-based row-index and row to get the data from)
   - key-fn              function/keyword that returns a unique id out of the row map/object, or nil to use the row's 0-based row-index
   - top-row-index       the 0-based index of the row that is currently at the top of the viewport (for virtual mode)
   - rows                a vector of row maps (or objects) to render the row-headers from
   - scroll-y            current horizontal scrollbar position in px
  "
  [row-header-renderer key-fn top-row-index rows scroll-y class style attr]
  [box/v-box
   :src      (at)
   :class    (str "rc-v-table-row-header-content rc-v-table-content " class)
   :style    (merge {:margin-top (px scroll-y :negative)}
                    style)
   :attr     attr
   :children (map
               (fn [index row]
                 ^{:key (if key-fn (key-fn row) index)} [row-header-renderer index row])
               (iterate inc top-row-index)
               rows)])


(defn row-header-viewport
  "Render section 2 - the viewport component (which renders the content component as its child)"
  [row-header-renderer key-fn top-row-index rows scroll-y
   row-header-selection-fn [selection-renderer on-mouse-down on-mouse-enter on-mouse-leave] selection-allowed?
   row-viewport-height content-rows-height
   class style attr
   sel-class sel-style sel-attr
   content-class content-style content-attr]
  [box/v-box ;; viewport component
   :src      (at)
   :class    (str "rc-v-table-row-headers rc-v-table-viewport " class)
   :style    (merge {:position   "relative"
                     :overflow   "hidden"
                     :max-height (px content-rows-height)}
                    style)
   :attr     (merge (when row-header-selection-fn
                      {:on-mouse-down  (handler-fn (on-mouse-down  :row-header row-header-selection-fn content-rows-height 0 event)) ;; TODO: width set to 0 because we don't have it - could probably measure it
                       :on-mouse-enter (handler-fn (on-mouse-enter :row-header))
                       :on-mouse-leave (handler-fn (on-mouse-leave :row-header))})
                    attr)
   :size     (if row-viewport-height "none" "auto")
   :height   (when row-viewport-height (px row-viewport-height))
   :children [(when selection-allowed?
                [selection-renderer sel-class sel-style sel-attr]) ;; selection rectangle component
              (if row-header-renderer
                [row-header-content row-header-renderer key-fn top-row-index rows scroll-y content-class content-style content-attr] ;; content component
                "")]])


;; ================================================================================== SECTION 3 - bottom-left

(defn bottom-left-content
  "Render section 3 - the content component"
  [bottom-left-renderer column-footer-height class style attr]
  [box/box ;; content component
   :src    (at)
   :class  (str "rc-v-table-bottom-left rc-v-table-content " class)
   :style  (merge {:overflow "hidden"}
                  style)
   :attr   attr
   :height (px (or column-footer-height 0))
   :child  (if bottom-left-renderer [bottom-left-renderer] "")])


;; ================================================================================== SECTION 4 - column-headers

(defn column-header-content
  "The column-header section 'content' component. Takes a function that renders column-headers and draws all of
  them in section 4 (sections explained below).
  This component is also responsible for setting the horizontal scroll position of this section based on scroll-x

  Arguments:
   - column-header-renderer function that knows how to render column-headers
   - scroll-x               current horizontal scrollbar position in px
  "
  [column-header-renderer scroll-x class style attr]
  [box/box
   :src   (at)
   :class (str "rc-v-table-column-header-content rc-v-table-content " class)
   :style (merge {:margin-left (px scroll-x :negative)}
                 style)
   :attr  attr
   :child [column-header-renderer]])


(defn column-header-viewport
  "Render section 4 - the viewport component (which renders the content component as its child)"
  [column-header-renderer scroll-x
   column-header-selection-fn [selection-renderer on-mouse-down on-mouse-enter on-mouse-leave] selection-allowed?
   row-viewport-width column-header-height content-rows-width
   class style attr
   sel-class sel-style sel-attr
   content-class content-style content-attr]
  [box/v-box ;; viewport component
   :src      (at)
   :class    (str "rc-v-table-column-headers rc-v-table-viewport " class)
   :style    (merge {:overflow "hidden"
                     :position "relative"}
                    style)
   :attr     (merge (when column-header-selection-fn
                      {:on-mouse-down  (handler-fn (on-mouse-down  :column-header column-header-selection-fn column-header-height content-rows-width event))
                       :on-mouse-enter (handler-fn (on-mouse-enter :column-header))
                       :on-mouse-leave (handler-fn (on-mouse-leave :column-header))})
                    attr)
   :width    (when row-viewport-width (px row-viewport-width))
   :height   (px (or column-header-height 0))
   :children [(when selection-allowed?
                [selection-renderer sel-class sel-style sel-attr]) ;; selection rectangle component
              (if column-header-renderer
                [column-header-content column-header-renderer scroll-x content-class content-style content-attr] ;; content component
                "")]])


;; ================================================================================== SECTION 5 - rows

(defn row-content
  "The rows section 'content' component. Takes a function that renders rows and draws all of them in section 5 (sections explained below).
  When in virtual? mode, only a screen-full of rows are passed to this component at any one time.
  This component is also responsible for setting the horizontal and vertical scroll position of this section based on scroll-x and scroll-y

  Arguments:
   - row-renderer  function that knows how to render rows (will be passed the 0-based row-index and row to render)
   - key-fn        function/keyword that returns a unique id out of the row map/object, or nil to use the row's 0-based row-index
   - top-row-index the 0-based index of the row that is currently at the top of the viewport (for virtual mode)
   - rows          a vector of row maps (or objects) to render
   - scroll-x      current horizontal scrollbar position in px
   - scroll-y      current horizontal scrollbar position in px
  "
  [row-renderer key-fn top-row-index rows scroll-x scroll-y class style attr]
  [box/v-box
   :src      (at)
   :class    (str "rc-v-table-row-content rc-v-table-content " class)
   :style    (merge {:margin-left (px scroll-x :negative)
                     :margin-top (px scroll-y :negative)}
                    style)
   :attr     attr
   :children (map
               (fn [index row]
                 ^{:key (if key-fn (key-fn row) index)} [row-renderer index row])
               (iterate inc top-row-index)
               rows)])


(defn row-viewport
  "Render section 5 - the viewport component (which renders the content component as its child)"
  [row-renderer key-fn top-row-index rows scroll-x scroll-y
   row-selection-fn [selection-renderer on-mouse-down on-mouse-enter on-mouse-leave] selection-allowed?
   row-viewport-height row-viewport-width row-viewport-id content-rows-height content-rows-width
   class style attr
   sel-class sel-style sel-attr
   content-class content-style content-attr]
  [box/v-box ;; viewport component
   :src      (at)
   :class    (str "rc-v-table-rows rc-v-table-viewport " class)
   :style    (merge {:overflow   "hidden"
                     :position   "relative"
                     :max-height (px content-rows-height)}
                    style)
   :attr     (merge (when row-selection-fn
                      {:on-mouse-down  (handler-fn (on-mouse-down  :row row-selection-fn content-rows-height content-rows-width event))
                       :on-mouse-enter (handler-fn (on-mouse-enter :row))
                       :on-mouse-leave (handler-fn (on-mouse-leave :row))})
                    attr
                    {:id row-viewport-id}) ;; Can't be overriding the internally generated id
   :size     (if row-viewport-height "none" "auto")
   :width    (when row-viewport-width (px row-viewport-width))
   :height   (when row-viewport-height (px row-viewport-height))
   :children [(when selection-allowed?
                [selection-renderer sel-class sel-style sel-attr]) ;; selection rectangle component
              [row-content row-renderer key-fn top-row-index rows scroll-x scroll-y content-class content-style content-attr]]]) ;; content component


;; ================================================================================== SECTION 6 - column-footers

(defn column-footer-content
  "The column-footer section 'content' component. Takes a function that renders column-footers and draws all of
  them in section 6 (sections explained below).
  This component is also responsible for setting the horizontal scroll position of this section based on scroll-x

  Arguments:
   - column-footer-renderer function that knows how to render column-footers
   - scroll-x            current horizontal scrollbar position in px
  "
  [column-footer-renderer scroll-x class style attr]
  [box/box
   :src   (at)
   :class (str "rc-v-table-column-footer-content rc-v-table-content " class)
   :style (merge {:margin-left (px scroll-x :negative)}
                 style)
   :attr  attr
   :child [column-footer-renderer]])


(defn column-footer-viewport
  "Render section 6 - the viewport component (which renders the content component as its child)"
  [column-footer-renderer scroll-x row-viewport-width column-footer-height
   class style attr
   content-class content-style content-attr]
  [box/box ;; viewport component
   :src    (at)
   :class  (str "rc-v-table-column-footers rc-v-table-viewport " class)
   :style  (merge {:overflow "hidden"}
                  style)
   :attr   attr
   :width  (when row-viewport-width (px row-viewport-width))
   :height (px (or column-footer-height 0))
   :child  (if column-footer-renderer
             [column-footer-content column-footer-renderer scroll-x content-class content-style content-attr] ;; content component
             "")])


;; ================================================================================== SECTION 7 - top-right

(defn top-right-content
  "Render section 7 - the content component"
  [top-right-renderer column-header-height class style attr]
  [box/box ;; content component
   :src    (at)
   :class  (str  "rc-v-table-top-right rc-v-table-content " class)
   :style  (merge {:overflow "hidden"}
                  style)
   :attr   attr
   :height (px (or column-header-height 0))
   :child  (if top-right-renderer [top-right-renderer] "")])


;; ================================================================================== SECTION 8 - row-footers

(defn row-footer-content
  "The row-footer section 'content' component. Takes a function that renders row-footers and draws all of
  them in section 8 (sections explained below).
  When in virtual? mode, only a screen-full of row-footers are passed to this component at any one time.
  This component is also responsible for setting the vertical scroll position of this section based on scroll-y

  Arguments:
   - row-footer-renderer function that knows how to render row-footers (will be passed the 0-based row-index and row to get the data from)
   - key-fn              function/keyword that returns a unique id out of the row map/object, or nil to use the row's 0-based row-index
   - top-row-index       the 0-based index of the row that is currently at the top of the viewport (for virtual mode)
   - rows                a vector of row maps (or objects) to render the row-footers from
   - scroll-y            current horizontal scrollbar position in px
  "
  [row-footer-renderer key-fn top-row-index rows scroll-y class style attr]
  [box/v-box
   :src      (at)
   :class    (str "rc-v-table-row-footer-content rc-v-table-content " class)
   :style    (merge {:margin-top (px scroll-y :negative)}
                    style)
   :attr     attr
   :children (map
               (fn [index row]
                 ^{:key (if key-fn (key-fn row) index)} [row-footer-renderer index row])
               (iterate inc top-row-index)
               rows)])


(defn row-footer-viewport
  "Render section 8 - the viewport component (which renders the content component as its child)"
  [row-footer-renderer key-fn top-row-index rows scroll-y
   row-viewport-height content-rows-height
   class style attr
   content-class content-style content-attr]
  [box/box ;; viewport component
   :src    (at)
   :class  (str "rc-v-table-row-footers rc-v-table-viewport " class)
   :style  (merge {:overflow   "hidden"
                   :max-height (px content-rows-height)}
                  style)
   :attr   attr
   :size   (if row-viewport-height "none" "auto")
   :height (when row-viewport-height (px row-viewport-height))
   :child  (if row-footer-renderer
             [row-footer-content row-footer-renderer key-fn top-row-index rows scroll-y content-class content-style content-attr] ;; content component
             "")])


;; ================================================================================== SECTION 9 - bottom-left

(defn bottom-right-content
  "Render section 9 - the content component"
  [bottom-right-renderer column-footer-height class style attr]
  [box/box ;; content component
   :src    (at)
   :class  (str "rc-v-table-bottom-right rc-v-table-content " class)
   :style  (merge {:overflow "hidden"}
                  style)
   :attr   attr
   :height (px (or column-footer-height 0))
   :child  (if bottom-right-renderer [bottom-right-renderer] "")])


;;============================ PUBLIC API ===================================

(def v-table-parts-desc
  (when include-args-desc?
    [{:name :wrapper                      :level 0 :class "rc-v-table-wrapper"                                  :impl "[v-table]" :notes "Outer container of the v-table"}
     {:name :left-section                 :level 1 :class "rc-v-table-left-section"                             :impl "[v-box]"   :notes "The left v-box container section of the table, containing sections 1,2,3"}
     {:name :top-left                     :level 2 :class "rc-v-table-top-left rc-v-table-content"              :impl "[box]"     :notes "Top left section (1)"}
     {:name :row-headers                  :level 2 :class "rc-v-table-row-headers rc-v-table-viewport"          :impl "[v-box]"   :notes "Row header viewport section (2)"}
     {:name :row-header-selection-rect    :level 3 :class "rc-v-table-selection"                                :impl "[:div]"    :notes "The row-header rectangle used for click+drag selection of row headers"}
     {:name :row-header-content           :level 3 :class "rc-v-table-row-header-content rc-v-table-content"    :impl "[v-box]"   :notes "The v-box containing one row header (row-header-render renders in here)"}
     {:name :bottom-left                  :level 2 :class "rc-v-table-bottom-left rc-v-table-content"           :impl "[box]"     :notes "Bottom left section (3)"}
     {:name :middle-section               :level 1 :class "rc-v-table-middle-section"                           :impl "[v-box]"   :notes "The middle v-box container section of the table, containing sections 4,5,6"}
     {:name :column-headers               :level 2 :class "rc-v-table-column-headers rc-v-table-viewport"       :impl "[v-box]"   :notes "Column header viewport section (4)"}
     {:name :column-header-selection-rect :level 3 :class "rc-v-table-selection"                                :impl "[:div]"    :notes "The column-header rectangle used for click+drag selection of column headers"}
     {:name :column-header-content        :level 3 :class "rc-v-table-column-header-content rc-v-table-content" :impl "[box]"     :notes "The box containing the column header (column-header-render renders in here)"}
     {:name :rows                         :level 2 :class "rc-v-table-rows rc-v-table-viewport"                 :impl "[v-box]"   :notes "Main row viewport section (5)"}
     {:name :row-selection-rect           :level 3 :class "rc-v-table-selection"                                :impl "[:div]"    :notes "The ROW rectangle used for click+drag selection of rows"}
     {:name :row-content                  :level 3 :class "rc-v-table-row-content rc-v-table-content"           :impl "[v-box]"   :notes "The v-box containing one row (row-render renders in here)"}
     {:name :column-footers               :level 2 :class "rc-v-table-column-footers rc-v-table-viewport"       :impl "[box]"     :notes "Column footer viewport section (6)"}
     {:name :column-footer-content        :level 3 :class "rc-v-table-column-footer-content rc-v-table-content" :impl "[box]"     :notes "The box containing the column footer (column-footer-render renders in here)"}
     {:name :h-scroll                     :level 2 :class "rc-v-table-h-scroll"                                 :impl "[box]"     :notes "The horizontal scrollbar"}
     {:name :right-section                :level 1 :class "rc-v-table-right-section"                            :impl "[v-box]"   :notes "The right container section v-box of the table, containing sections 7,8,9"}
     {:name :top-right                    :level 2 :class "rc-v-table-top-right rc-v-table-content"             :impl "[box]"     :notes "Top right section (7)"}
     {:name :row-footers                  :level 2 :class "rc-v-table-row-footers rc-v-table-viewport"          :impl "[box]"     :notes "Row footer viewport section (8)"}
     {:name :row-footer-content           :level 3 :class "rc-v-table-row-footer-content rc-v-table-content"    :impl "[v-box]"   :notes "The v-box containing one row footer (row-footer-render renders in here)"}
     {:name :bottom-right                 :level 2 :class "rc-v-table-bottom-right rc-v-table-content"          :impl "[box]"     :notes "Bottom right section (9)"}
     {:name :v-scroll-section             :level 1 :class "rc-v-table-v-scroll-section"                         :impl "[v-box]"   :notes "The v-box containing the vertical scrollbar:"}
     {:type :legacy                       :level 2 :name-label "-"                                              :impl "[box]"     :notes "Legacy"}
     {:name :v-scroll                     :level 3 :class "rc-v-table-v-scroll"                                 :impl "[box]"     :notes "The vertical scrollbar"}]))



(def v-table-parts
  (when include-args-desc?
    (-> (map :name v-table-parts-desc) set)))


(def v-table-args-desc
  (when include-args-desc?
    [{:name :model                      :required true                 :type "atom containing vec of maps" :validate-fn vector-atom?           :description [:span "One element for each row displayed in the table. Typically, a vector of maps, but can be a seq of anything, with your functions like " [:code ":key-fn"] " extracting values."]}
     {:name :key-fn                     :required false :default "nil" :type "map -> anything"             :validate-fn ifn-or-nil?            :description [:span "A function/keyword or nil. Given an element of " [:code ":model"] ", it should return its unique identifier which is used by Reagent as a unique id. If not specified or nil passed, the element's 0-based row-index will be used"]}
     {:name :virtual?                   :required false :default true  :type "boolean"                                                         :description [:span "when true, only those rows that are visible are rendered to the DOM. Otherwise DOM will be generated for all rows, which might be prohibitive if there are a large number of rows."]}

     {:name :row-height                 :required true                 :type "integer"                     :validate-fn number?                :description "px height of each row, in sections 2, 5 and 8."}
     {:name :column-header-height       :required false                :type "integer"                     :validate-fn number?                :description "px height of the column header. Impacts the upper sections 1, 4 and 7. If not provided, defaults to 0, meaning these three sections will not be visible."}
     {:name :column-footer-height       :required false                :type "integer"                     :validate-fn number?                :description "px height of the column footer. Impacts the lower sections 3, 6 and 9. If not provided, defaults to 0, meaning these three sections will not be visible."}
     {:name :row-content-width          :required true                 :type "integer"                     :validate-fn number?                :description [:span "px width of sections 4, 5, 6. The renderers for these sections are expected to return hiccup to fill these spaces."]}
     {:name :max-width                  :required false                :type "string"                      :validate-fn string?                :description "Standard CSS max-width setting of the entire table. If not provided, table will fill available space"}

     {:name :top-left-renderer          :required false                :type "-> hiccup"                   :validate-fn fn?                    :description [:span "A function taking no args which returns the hiccup for the top left (section 1). The hiccup should fill the height specified via "  [:code ":column-header-height"] ". The width of the three left sections is self-determined as the maximum of their own content."]}
     {:name :row-header-renderer        :required false                :type "row-index, row -> hiccup"    :validate-fn fn?                    :description [:span "A function. Given the 0-based row-index and an element of " [:code ":model"] ", it will return the hiccup for the row header (section 2)."]}
     {:name :bottom-left-renderer       :required false                :type "-> hiccup"                   :validate-fn fn?                    :description "A function taking no args which returns the hiccup for the bottom left (section 3)"}
     {:name :column-header-renderer     :required false                :type "-> hiccup"                   :validate-fn fn?                    :description "A function taking no args which returns the hiccup for the column header (section 4)."}
     {:name :row-renderer               :required true                 :type "row-index, row -> hiccup"    :validate-fn fn?                    :description [:span "A function. Given the 0-based row-index and an element of " [:code ":model"] ", it will return the hiccup for a single content row (section 5). This renderer is called once for each displayed row. As vertical scrolling occurs, more calls will be made."]}
     {:name :column-footer-renderer     :required false                :type "-> hiccup"                   :validate-fn fn?                    :description "A function taking no args which returns the hiccup for the entire column footer (section 6)."}
     {:name :top-right-renderer         :required false                :type "-> hiccup"                   :validate-fn fn?                    :description "A function taking no args which returns the hiccup for the top right (section 7)"}
     {:name :row-footer-renderer        :required false                :type "row-index, row -> hiccup"    :validate-fn fn?                    :description [:span "A function. Given the 0-based row-index and an element of " [:code ":model"] ", it will return the hiccup for the row footer (section 8)."]}
     {:name :bottom-right-renderer      :required false                :type "-> hiccup"                   :validate-fn fn?                    :description "A function taking no args which returns the hiccup for the bottom right (section 9)."}

     {:name :row-header-selection-fn    :required false                :type "(5 args) -> "                :validate-fn fn?                    :description "See v-table docstring for arg details. If present, this function will be called on mouse-down, mouse-move and mouse-up events, allowing you to capture user selection of cells, columns or rows in section 2."}
     {:name :column-header-selection-fn :required false                :type "(5 args) -> "                :validate-fn fn?                    :description "See v-table docstring for arg details. If present, this function will be called on mouse-down, mouse-move and mouse-up events, allowing you to capture user selection of cells, columns or rows in section 4."}
     {:name :row-selection-fn           :required false                :type "(5 args) -> "                :validate-fn fn?                    :description "See v-table docstring for arg details. If present, this function will be called on mouse-down, mouse-move and mouse-up events, allowing you to capture user selection of cells, columns or rows in section 5."}

     {:name :row-viewport-width         :required false                :type "integer"                     :validate-fn number?                :description "px width of the row viewport area (section 5). If not specified, the component takes all the horizontal space available."}
     {:name :row-viewport-height        :required false                :type "integer"                     :validate-fn number?                :description "px height of the row viewport area (section 5). If not specified,the component takes all the vertical space available."}
     {:name :max-row-viewport-height    :required false                :type "integer"                     :validate-fn number?                :description [:span "The " [:b [:i "maximum"]] " px height of the row viewport area (section 5), excluding height of sections 4 and 6 (and horizontal scrollbar). If not specified, value determined by parent height and number of rows"]}
     {:name :scroll-rows-into-view      :required false                :type "atom containing map"         :validate-fn map-atom?              :description [:span "Scrolls the table to a particular row range. Must be an atom. The map contains the keys " [:code ":start-row"] " and " [:code ":end-row"] " (row indexes)."]}
     {:name :scroll-columns-into-view   :required false                :type "atom containing map"         :validate-fn map-atom?              :description [:span "Scrolls the table of a particular column range. Must be an atom. Map that contains the keys " [:code ":start-col"] " and " [:code ":end-col"]  " in pixel units."]}
     {:name :remove-empty-row-space?    :required false :default true  :type "boolean"                                                         :description "If true, removes whitespace between the last row and the horizontal scrollbar. Useful for tables without many rows where otherwise there would be a big gap between the last row and the horizontal scrollbar at the bottom of the available space."}
     {:name :class                      :required false                :type "string"                      :validate-fn string?                :description "CSS class names, space separated (these are applied to the table's outer container)"}
     {:name :parts                      :required false                :type "map"                         :validate-fn (parts? v-table-parts) :description "See Parts section below."}
     {:name :src                        :required false                :type "map"                         :validate-fn map?                   :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as                   :required false                :type "map"                         :validate-fn map?                   :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))


(defn v-table
  "Renders a scrollable table with optional fixed column and row headers and footers, totalling nine addressable sections
  By default, it only displays rows that are visible, so is very efficient for large data structures
  The table supports click+drag selections within the rows section (5), row header section (2) and col header section (4)

  The table is laid out using an h-box for the outer component, with four v-box 'container
  sections' sitting next to each other:

  +-----+---------+-----+-+
  |     |         |     | |
  |     |         |     | |
  |     |         |     | |
  |LEFT | MIDDLE  |RIGHT|VS
  |     |         |     | |
  |     |         |     | |
  |     |         |     | |
  |- - -|- - - - -|- - -|-|
  +-----+---HS----+-----+-+

  The four 'container section' v-boxes are named:
   - LEFT:   contains the row headers (and corresponding two corner sections)
   - MIDDLE: contains the main content area (the rows), along with column headers and footers and the horizontal scrollbar (HS)
   - RIGHT:  contains the row footers (and corresponding two corner sections)
   - VS:     contains the vertical scrollbar

  Each container section holds 'sections' which are numbered:

  +-----+---------+-----+
  |  1  |    4    |  7  |
  +-----+---------+-----+-+
  |     |         |     | |
  |  2  |    5    |  8  |VS
  |     |         |     | |
  +-----+---------+-----+ +
  |  3  |    6    |  9  |
  +-----+---------+-----+
        +---HS----+

  The nine 'sections' are named:
   - 1: top-left
   - 2: row-headers
   - 3: bottom-left
   - 4: column-headers
   - 5: rows
   - 6: column-footers
   - 7: top-right
   - 8: row-footers
   - 9: bottom-right

  The corner sections (1, 3, 7, 9) are fixed (i.e. do not scroll) and consist of a single 'content' area

  The other sections are scrollable: (2, 8) vertical, (4, 6) horizontal and (5) vertical & horizontal as
  a 'viewport' onto their (potentially larger) 'content' area


  Arguments:

   - model                    [mandatory atom containing vector of maps (or other data structures)]
                              The data to be displayed, consisting of rows. Each row is normally a map but could be anything)
                              Rows MUST contain a unique id (specified via :key-fn arg)
                              They are passed to the row-renderer (section 5), row-header-renderer (section 2) and row-footer-renderer (section 8)
                              NOTE: data for sections 1, 3, 4, 6, 7 and 9 are not included in model

   - key-fn                   [optional fn or keyword to extract a unique id from the row data, or not specified/nil to indicate
                              that v-table should use the internally generated 0-based row-id]
                              A row is passed to key-fn and it returns the unique identifier for that row

   - virtual?                 [optional bool, default = true]
                              When true, use virtual feature where only a screen-full (viewport) of rows are rendered at any one time
                              Use true for tables with many rows to reduce initialisation time and resource usage
                              You can use false for smaller tables to improve performance of vertical scrolling

   - remove-empty-row-space?  [optional bool, default = true]
                              Specifies whether to remove empty row space (the space between the last row and the horizontal scrollbar)
                              for small tables that don't fill the space available to the v-table
                              This will cause the horizontal scrollbar section to be nestled against the last row, and whatever is
                              underneath the v-table to be brought up with it

   - max-width                [optional string, default = nil (table will fill available space)]
                              MAXIMUM width of the entire v-table
                              NOTE: This is specified as a normal CSS value, e.g. \"1024px\" or \"90%\"


     ========== SECTION 1 - top-left

   - top-left-renderer        [optional (fn [])]
                              Render the top left section
                              Height is determined by the :column-header-height arg
                              Width is determined by the component itself
                              Passed args: none


     ========== SECTION 2 - row-headers

   - row-header-renderer      [optional (fn [row-index row])]
                              Render a single row header
                              Height is determined by the row-height arg
                              Width is determined by the component itself
                              Passed args: row-index (0-based), row (a map, or other data structure from model)

   - row-header-selection-fn  [optional (fn [selection-event coords ctrlKey shiftKey event])]
                              If provided, indicates that the row header section is selectable via click+drag
                              Passed args: see row-selection-fn below for details
                              Use the :row-header-selection-rect style-part to style the selection rectangle


     ========== SECTION 3 - bottom-left

   - bottom-left-renderer     [optional (fn [])]
                              Render the bottom left section
                              Height is determined by the column-footer-height arg
                              Width is determined by the component itself
                              Passed args: none

                              NOTE: The width of the LEFT container section (encompassing sections 1, 2 and 3 above)
                                    is determined by the widest section


     ========== SECTION 4 - column-headers

   - column-header-renderer   [optional (fn [])]
                              Render the entire column header
                              Height is determined by the column-header-height arg
                              Width is determined by the width available to the v-table OR the row-viewport-width arg if specified
                              Passed args: none

   - column-header-height     [optional number, default = 0]
                              px height of the column header section

   - column-header-selection-fn  [optional (fn [selection-event coords ctrlKey shiftKey event])]
                              if provided, indicates that the column header section is selectable via click+drag
                              Passed args: see row-selection-fn below for details
                              Use the :column-header-selection-rect style-part to style the selection rectangle


     ========== SECTION 5 - rows (main content area)

   - row-renderer             [mandatory (fn [row-index row])]
                              Render a single content row
                              [DJ] Wants to state that columns are not virtual and all horizontal content is rendered
                              Height is determined by the row-height arg
                              Width is determined by the width available to the v-table OR the row-viewport-width arg if specified
                              Passed args: row-index (0-based), row (a map, or other data structure from model)

   - row-height               [mandatory number]
                              px height of each row

   - row-content-width        [mandatory number]
                              px width of the content rendered by row-renderer

   - row-viewport-width       [optional number, default = nil (take up all available width)]
                              px width of the row viewport area
                              If not specified, takes up all width available to it

   - row-viewport-height      [optional number, default = nil (take up all available height)]
                              px height of the row viewport area
                              If not specified, takes up all height available to it

   - max-row-viewport-height  [optional number, default = nil (determined by parent height and number of rows)]
                              MAXIMUM px height of the row viewport area
                              Conveniently excludes height of col header and footer and horizontal scrollbar
                              For this to be effective, the parent of the v-table component should have ':size none'

   - row-selection-fn         [optional (fn [selection-event coords ctrlKey shiftKey event])]
                              If provided, indicates that the row section is selectable via click+drag
                              The fn will be called (on mouse-down, mouse-move and mouse-up) with four positional args
                              Passed args:
                                    selection-event: One of :selection-start, :selecting or :selection-end
                                    coords:          {:start-row integer   ;; rows are returned as zero-based row numbers (except column-header which returns px)
                                                      :end-row   integer
                                                      :start-col integer   ;; cols are returned as px offsets
                                                      :end-col   integer}
                                    ctrlKey:         When true, Control key is currently pressed
                                    shiftKey:        When true, Shift key is currently pressed
                                    event            The original MouseEvent (https://developer.mozilla.org/en/docs/Web/API/MouseEvent)
                              Use the :selection-rect style-part to style the selection rectangle


     ========== SECTION 6 - column-footers

   - column-footer-renderer   [optional (fn [])]
                              Render the entire column footer
                              Height is determined by the column-footer-height arg
                              Width is determined by the width available to the v-table OR the row-viewport-width arg if specified
                              Passed args: none

   - column-footer-height     [optional number, default = 0]
                              px height of the column footer section


     ========== SECTION 7 - top right

   - top-right-renderer       [optional (fn [])]
                              Render the top right section
                              Height is determined by the column-header-height arg
                              Width is determined by the component itself
                              Passed args: none


     ========== SECTION 8 - row-footers

   - row-footer-renderer      [optional (fn [row-index row])]
                              Render a single row footer
                              Height is determined by the row-height arg
                              Width is determined by the component itself
                              Passed args: row-index (0-based), row (a map, or other data structure from model)


     ========== SECTION 9 - bottom-right

   - bottom-right-renderer    [optional (fn [])]
                              Render the bottom right section
                              Height is determined by the column-footer-height arg
                              Width is determined by the component itself
                              Passed args: none

                              NOTE: The width of the RIGHT container section (encompassing sections 7, 8 and 9 above)
                                    is determined by the widest section


     ========== Scrolling the table so that a block of rows/columns become visible

   - scroll-rows-into-view    [optional atom map]
                              Set this argument to scroll the table to a particular row range
                              map example:
                                {:start-row   12   ;; Start row number (zero-based) to be scrolled into view
                                 :end-row     14}  ;; End row number to be scrolled into view

   - scroll-columns-into-view [optional atom map]
                              Set this argument to scroll the table to a particular column range (in this case columns are pixels!)
                              map example:
                                {:start-col   200  ;; Start column px offset to be scrolled into view
                                 :end-col     300} ;; End column px offset to be scrolled into view


     ========== Styling different parts of the table (and setting attributes for those parts)

   - class                    Add extra class(es) to the outer container

   - parts                    [optional nested map]
                              Allows classes, styles and attributes (e.g. custom event handlers) to be specified for each part of the table

                              Keys can be:

                               - :wrapper                             The outer container of the table

                               - :left-section                        The left v-box container section of the table, containing:
                                  - :top-left                         Top left section (1)
                                  - :row-headers                      Row header viewport section (2)
                                     - :row-header-selection-rect     The row-header rectangle used for click+drag selection of row headers
                                     - :row-header-content            The v-box containing one row header (row-header-render renders in here)
                                  - :bottom-left                      Bottom left section (3)

                               - :middle-section                      The middle v-box container section of the table, containing:
                                  - :column-headers                   Column header viewport section (4)
                                     - :column-header-selection-rect  The column-header rectangle used for click+drag selection of column headers
                                     - :column-header-content         The box containing the column header (column-header-render renders in here)
                                  - :rows                             Main row viewport section (5)
                                     - :row-selection-rect            The ROW rectangle used for click+drag selection of rows
                                                                      Defaults to being above the rows (:z-index 1). Set to 0 to place it underneath rows
                                     - :row-content                   The v-box containing one row (row-render renders in here)
                                  - :column-footers                   Column footer viewport section (6)
                                     - :column-footer-content         The box containing the column footer (column-footer-render renders in here)
                                  - :h-scroll                         The horizontal scrollbar
                                                                      
                               - :right-section                       The right container section v-box of the table, containing:
                                  - :top-right                        Top right section (7)
                                  - :row-footers                      Row footer viewport section (8)
                                     - :row-footer-content            The v-box containing one row footer (row-footer-render renders in here)
                                  - :bottom-right                     Bottom right section (9)
                                                                      
                               - :v-scroll-section                    The v-box containing the vertical scrollbar:
                                  - :v-scroll                         The vertical scrollbar
   "
  ;; Suggestion: Ideally make the component work out row-content-width so it doesn't need to be passed (and column-header-height/column-footer-height if possible)

  [& {:keys [model virtual? row-height row-viewport-width row-viewport-height max-row-viewport-height src]
      :or   {virtual? true}
      :as   args}]
  (or
    (validate-args-macro v-table-args-desc args)
    (let [scroll-x                          (reagent/atom 0)              ;; px offset from left of header/content/footer sections (affected by changing scrollbar or scroll-wheel, or dragging selection box past screen edge)
          scroll-y                          (reagent/atom 0)              ;; px offset from top of header/content/footer sections (note: this value remains the same when virtual-mode? is both true and false)
         ;wheel-row-increment               (* 10 row-height)             ;; Could be an argument
         ;wheel-col-increment               (* 4 102)                     ;; Could be an argument - can't calculate this in here, needs to be passed
          content-rows-width                (reagent/atom 0)              ;; Total px width of the content rendered by row-renderer (passed in via the :row-content-width arg)
          content-rows-height               (reagent/atom 0)              ;; Total px height of all content rows rendered by row-renderer (calculated internally)
          row-viewport-id                   (gensym "row-viewport-")      ;; The resize listener will listen to this element's (the row-viewport component) resize behaviour
          row-viewport-element              (reagent/atom nil)            ;; This contains a js reference to the row-viewport component (being listened to for resize changes)
          rl-row-viewport-width             (reagent/atom 0)              ;; The current width of the row-viewport component (returned from the resize listener or overridden by the :row-viewport-width arg)
          rl-row-viewport-height            (reagent/atom (min (* row-height (count @model)) max-row-viewport-height)) ;; The current height of the row-viewport component (returned from the resize listener or overridden by the :row-viewport-height arg). Initialise to prevent that annoying cascading render effect
          internal-scroll-rows-into-view    (reagent/atom nil)            ;; Internal state for scrolling a particular row number (or range or rows) into view
          internal-scroll-columns-into-view (reagent/atom nil)            ;; Internal state for scrolling a px range of columns into view
          m-size                            (reaction (count @model))     ;; TODO/NOTE: This reaction was not always fired at the required time when creating virtual-rows after deleting a constraint. Could be an FRP glitch?
          rows-per-viewport                 (reaction (.round js/Math (/ @rl-row-viewport-height row-height)))      ;; The number of rows that can currently be displayed in the row-viewport component
          max-scroll-x                      (reaction (- @content-rows-width  @rl-row-viewport-width))              ;; The maximum number of pixels the content can be scrolled vertically so it stops at the very bottom of the content section
          max-scroll-y                      (reaction (- @content-rows-height @rl-row-viewport-height))             ;; The maximum number of pixels the content can be scrolled horizontally so it stops at the far right of the content section
          top-row-index                     (reaction (int (/ @scroll-y row-height)))                               ;; The row number (zero-based) of the row currently rendered at the top of the table
          bot-row-index                     (reaction (min (+ @top-row-index (dec @rows-per-viewport)) @m-size))    ;; The row number of the row currently rendered at the bottom of the table
          virtual-scroll-y                  (reaction (mod @scroll-y row-height))                                   ;; Virtual version of scroll-y but this is a very small number (between 0 and the row-height)
          virtual-rows                      (reaction (when (pos? @m-size)
                                                        (subvec @model
                                                                (min @top-row-index @m-size)
                                                                (min (+ @top-row-index @rows-per-viewport 2) @m-size))))

          on-h-scroll-change                #(reset! scroll-x %)    ;; The on-change handler for the horizontal scrollbar
          on-v-scroll-change                #(reset! scroll-y %)    ;; The on-change handler for the vertical scrollbar

          ;; When the resize listener detects a viewport area size change, this handler is fired
          on-viewport-resize                (fn on-viewport-resize
                                              [event]
                                              (let [target        (-> event .-target)
                                                    bounding-rect (if (nil? target) {} (.getBoundingClientRect target))]
                                                (reset! rl-row-viewport-width  (or row-viewport-width  (.-width  bounding-rect)))
                                                (reset! rl-row-viewport-height (or row-viewport-height (.-height bounding-rect)))
                                                (reset! scroll-x               (max 0 (min @max-scroll-x @scroll-x)))
                                                (reset! scroll-y               (max 0 (min @max-scroll-y @scroll-y)))))

          ;; When the mouse wheel is scrolled, this handler is called
          ;;     TODO: Wheel support not currently cross-browser (but works well in Chrome). References:
          ;;           http://stackoverflow.com/questions/5527601/normalizing-mousewheel-speed-across-browsers
          ;;           https://developer.mozilla.org/en-US/docs/Web/Events/wheel
          on-wheel                          (fn on-wheel
                                              [event]
                                              (let [delta-x (.-deltaX event)
                                                    new-delta-x delta-x  ;(cond ;; Disabled for now
                                                                         ;  (neg? delta-x) (- wheel-col-increment)
                                                                         ;  (pos? delta-x) wheel-col-increment
                                                                         ;  :else          0)
                                                    delta-y (.-deltaY event)
                                                    new-delta-y delta-y] ;(cond ;; Disabled for now
                                                                         ;  (neg? delta-y) (- wheel-row-increment)
                                                                         ;  (pos? delta-y) wheel-row-increment
                                                                         ;  :else          0)

                                                (reset! scroll-x (max 0 (min @max-scroll-x (+ @scroll-x new-delta-x))))
                                                (reset! scroll-y (max 0 (min @max-scroll-y (+ @scroll-y new-delta-y))))))

          dmm-tracker                       (atom nil)            ;; Holds a reference to the current dmm (DOM mouse-move) dmm-tracker object
          sel-parent-bounding-rect          (reagent/atom nil)    ;; left, right, top, bottom, width, height of div where the selection is being drawn in (in screen coordinates)
          sel-content-x-start               (reagent/atom 0)      ;; Original mouse-down x position of the content selection
          sel-content-y-start               (reagent/atom 0)      ;; Original mouse-down y position of the content selection
          sel-content-x-end                 (reagent/atom 0)      ;; Current mouse x drag position of the content selection
          sel-content-y-end                 (reagent/atom 0)      ;; Current mouse y drag position of the content selection

          ;; The selection rectangle component
          selection-renderer                (fn selection-renderer
                                              [class style attr]
                                              (let [selecting-down?  (> @sel-content-y-end @sel-content-y-start)
                                                    selecting-right? (> @sel-content-x-end @sel-content-x-start)
                                                    width            (if selecting-right?
                                                                       (- @sel-content-x-end @sel-content-x-start)
                                                                       (- @sel-content-x-start @sel-content-x-end))
                                                    height           (if selecting-down?
                                                                       (- @sel-content-y-end @sel-content-y-start)
                                                                       (- @sel-content-y-start @sel-content-y-end))
                                                    top              (if selecting-down?
                                                                       (- @sel-content-y-start @scroll-y)
                                                                       (- @sel-content-y-start @scroll-y height))
                                                    left             (if selecting-right?
                                                                       (- @sel-content-x-start @scroll-x)
                                                                       (- @sel-content-x-start @scroll-x width))]
                                                [:div
                                                 (merge
                                                   {:class (str "rc-v-table-selection " class)
                                                    :style (merge {:position         "absolute"
                                                                   :z-index          1
                                                                   :top              (px top)
                                                                   :left             (px left)
                                                                   :width            (px width)
                                                                   :height           (px height)
                                                                   :background-color "rgba(0,0,255,0.1)"
                                                                   :border           "1px solid rgba(0,0,255,0.4)"}
                                                                  style)}
                                                   attr)
                                                 ""]))

          coords-debug                      (reagent/atom nil)    ;; Handy when debugging - used to show selection coords on the left-hand debug section
          event-debug                       (reagent/atom nil)    ;; Handy when debugging - use this to display data from the event object on the left-hand debug section
          selection-target                  (reagent/atom nil)    ;; Indicates which section we're selecting in (one of :row, :row-header or :column-header)
          sel-max-content-rows-px           (reagent/atom 0)      ;; The maximum value that can be passed in the callback of px rows to be used for the selection callback
          sel-max-content-cols-px           (reagent/atom 0)      ;; The maximum number of px columns to be used for the selection callback

          ;; Calculates the map representing the selection dimensions that will be passed back to the caller (translates px to row numbers if required)
          selection-coords                  (fn selection-coords
                                              []
                                              (if @sel-parent-bounding-rect
                                                (let [selecting-down?      (> @sel-content-y-end @sel-content-y-start)
                                                      selecting-right?     (> @sel-content-x-end @sel-content-x-start)
                                                      use-rows-numbers?    (not= @selection-target :column-header)           ;; rows and row-headers return row numbers, column-headers return px values
                                                      start-row-px         (if selecting-down?  @sel-content-y-start @sel-content-y-end)
                                                      end-row-px           (if selecting-down?  @sel-content-y-end @sel-content-y-start)
                                                      start-col-px         (if selecting-right? @sel-content-x-start @sel-content-x-end)
                                                      end-col-px           (if selecting-right? @sel-content-x-end @sel-content-x-start)
                                                      start-row-px-clipped (max 0 (min @sel-max-content-rows-px start-row-px))
                                                      end-row-px-clipped   (max 0 (min @sel-max-content-rows-px end-row-px))
                                                      coords               {:start-row (if use-rows-numbers?
                                                                                         (int (/ start-row-px-clipped row-height))
                                                                                         start-row-px-clipped)
                                                                            :end-row   (if use-rows-numbers?
                                                                                         (int (/ end-row-px-clipped row-height))
                                                                                         end-row-px-clipped)
                                                                            :start-col (max 0 (min @sel-max-content-cols-px start-col-px))
                                                                            :end-col   (max 0 (min @sel-max-content-cols-px end-col-px))}]
                                                  (when debug? (reset! coords-debug coords))
                                                  coords)
                                                {}))

          dragging?                         (reagent/atom false)  ;; true when the mouse is down in a selectable section
          dragging-outside?                 (reagent/atom false)  ;; true when the mouse is down in a selectable section BUT is outside the section (causes scrolling and selection extension)

          ;; Whenever a mouse move is detected while dragging a selection, this handler is called by the dmm-tracker
          on-drag-change                    (fn on-drag-change
                                              [sel-fn _delta-x _delta-y curr-x curr-y ctrlKey shiftKey event]
                                              (let [top-offset     (.-top    @sel-parent-bounding-rect)
                                                    left-offset    (.-left   @sel-parent-bounding-rect)
                                                    bottom-offset  (.-bottom @sel-parent-bounding-rect)
                                                    right-offset   (.-right  @sel-parent-bounding-rect)
                                                    scroll-delta-y (if (and @dragging-outside? (not= @selection-target :column-header))
                                                                     (cond
                                                                       (< curr-y top-offset)    (- curr-y top-offset)
                                                                       (> curr-y bottom-offset) (- curr-y bottom-offset)
                                                                       :else                    0)
                                                                     0)
                                                    scroll-delta-x (if (and @dragging-outside? (not= @selection-target :row-header))
                                                                     (cond
                                                                       (< curr-x left-offset)  (- curr-x left-offset)
                                                                       (> curr-x right-offset) (- curr-x right-offset)
                                                                       :else                   0)
                                                                     0)]
                                                (reset! sel-content-x-end (+ curr-x (- left-offset) @scroll-x))
                                                (reset! sel-content-y-end (+ curr-y (- top-offset)  @scroll-y))
                                                (reset! scroll-x (max 0 (min @max-scroll-x (+ @scroll-x scroll-delta-x))))
                                                (reset! scroll-y (max 0 (min @max-scroll-y (+ @scroll-y scroll-delta-y))))
                                                (when debug? (reset! event-debug event))
                                                (sel-fn :selecting (selection-coords) ctrlKey shiftKey event))) ;; Call back to the app

          ;; When the mouse is released while dragging a selection, this handler is called by the dmm-tracker
          on-drag-end                       (fn on-drag-end
                                              [sel-fn ctrlKey shiftKey event]
                                              (when debug? (reset! coords-debug nil))
                                              (when debug? (reset! event-debug event))
                                              (sel-fn :selection-end (selection-coords) ctrlKey shiftKey event) ;; Call back to the app
                                              (reset! dragging? false)
                                              (reset! dragging-outside? false)
                                              (reset! sel-parent-bounding-rect nil)
                                              (reset! dmm-tracker nil))

          ;; This is called when the mouse is pressed in a selectable section to kick things off
          on-mouse-down                     (fn on-mouse-down
                                              [sel-target sel-fn max-rows-px max-cols-px event]
                                              (reset! selection-target sel-target)
                                              (reset! sel-max-content-rows-px (dec max-rows-px))
                                              (reset! sel-max-content-cols-px (dec max-cols-px))
                                              (reset! sel-parent-bounding-rect (.getBoundingClientRect (.-currentTarget event))) ;; Note: js->clj only works with Objects and this is a ClientRect
                                              (let [top-offset  (- (.-top   @sel-parent-bounding-rect))
                                                    left-offset (- (.-left  @sel-parent-bounding-rect))]
                                                (reset! sel-content-x-start (+ (.-clientX event) left-offset @scroll-x))
                                                (reset! sel-content-y-start (+ (.-clientY event) top-offset  @scroll-y))
                                                (reset! sel-content-x-end @sel-content-x-start)
                                                (reset! sel-content-y-end @sel-content-y-start)
                                                (when debug? (reset! event-debug event))
                                                (sel-fn :selection-start (selection-coords) (.-ctrlKey event) (.-shiftKey event) event) ;; Call back to the app
                                                (reset! dmm-tracker (make-dmm-tracker (partial on-drag-change sel-fn) (partial on-drag-end sel-fn)))
                                                (captureMouseMoves @dmm-tracker event)
                                                (reset! dragging? true)
                                                (reset! dragging-outside? false)
                                                #_(.stopPropagation event)))


          ;; Clears the dragging-outside? flag when the mouse returns to the selectable section
          on-mouse-enter                    (fn on-mouse-enter
                                              [sel-target]
                                              (when (and @dragging? (= @selection-target sel-target))
                                                (reset! dragging-outside? false)))

          ;; Sets the dragging-outside? flag when the mouse moves out of the selectable section
          on-mouse-leave                    (fn on-mouse-leave
                                              [sel-target]
                                              (when (and @dragging? (= @selection-target sel-target))
                                                (reset! dragging-outside? true)))
          selection-fns                     [selection-renderer
                                             on-mouse-down
                                             on-mouse-enter
                                             on-mouse-leave]]

      ;; Only render the table if the js resize listener code has been loaded
      (if-not (or (.hasOwnProperty js/window "addResizeListener") (.hasOwnProperty js/window "removeResizeListener"))
        (js/console.error "Your project is missing detect-element-resize.js or detect-element-resize-externs.js
         could not setup v-table. See https://re-com.day8.com.au/#/v-table requirements")

        ;; Here we are folks, the main event
        (reagent/create-class
          {:display-name "v-table"

           :component-did-mount
           (fn v-table-component-did-mount
             []
             (reset! row-viewport-element (.getElementById js/document row-viewport-id)) ;; TODO: [MT] Use refs?
             (.addResizeListener js/window @row-viewport-element on-viewport-resize))

           :component-will-unmount
           (fn v-table-component-will-unmount
             []
             (.removeResizeListener js/window @row-viewport-element on-viewport-resize)
             (reset! row-viewport-element nil))

           :reagent-render
           (fn v-table-render
             [& {:keys [virtual? remove-empty-row-space? key-fn max-width
                        ;; Section 1
                        top-left-renderer
                        ;; Section 2
                        row-header-renderer row-header-selection-fn
                        ;; Section 3
                        bottom-left-renderer
                        ;; Section 4
                        column-header-renderer column-header-height column-header-selection-fn
                        ;; Section 5
                        row-renderer row-height row-selection-fn row-viewport-width row-viewport-height max-row-viewport-height row-content-width
                        ;; Section 6
                        column-footer-renderer column-footer-height
                        ;; Section 7
                        top-right-renderer
                        ;; Section 8
                        row-footer-renderer
                        ;; Section 9
                        bottom-right-renderer
                        ;; Others
                        scroll-rows-into-view scroll-columns-into-view
                        class parts src debug-as]
                 :or   {virtual?                true
                        remove-empty-row-space? true
                        key-fn                  nil}
                 :as   args}]
             (or
               (validate-args-macro v-table-args-desc args)
               (do
                 (reset! content-rows-width row-content-width)
                 (reset! content-rows-height (* @m-size row-height))

                 ;; Scroll rows into view handling
                 (when (not= (deref-or-value scroll-rows-into-view) @internal-scroll-rows-into-view)
                   ;; TODO: Ideally allow non-atom nil but exception if it's not an atom when there's a value
                   (let [{:keys [start-row end-row]} (deref-or-value scroll-rows-into-view)
                         new-scroll-y (cond
                                        (and (nil? start-row)
                                             (nil? end-row))          nil
                                        (<= start-row @top-row-index) (* start-row row-height)
                                        (>= end-row   @bot-row-index) (+ (* end-row row-height)
                                                                         row-height
                                                                         (- @rl-row-viewport-height))
                                        :else                         nil)]
                     (when (some? new-scroll-y)
                       (reset! scroll-y (max 0 (min @max-scroll-y new-scroll-y))))
                     (reset! internal-scroll-rows-into-view (deref-or-value scroll-rows-into-view))))

                 ;; Scroll columns into view handling
                 (when (not= (deref-or-value scroll-columns-into-view) @internal-scroll-columns-into-view)
                   ;; TODO: Ideally allow non-atom nil but exception if it's not an atom when there's a value
                   (let [{:keys [start-col end-col]} (deref-or-value scroll-columns-into-view)
                         left-col-px  @scroll-x                     ;; Unnecessary but consistent
                         right-col-px (+ @scroll-x @rl-row-viewport-width -1)
                         new-scroll-x (cond
                                        (< start-col left-col-px)  start-col
                                        (> end-col   right-col-px) (- end-col @rl-row-viewport-width)
                                        :else                      nil)]
                     (when (some? new-scroll-x)
                       (reset! scroll-x (max 0 (min @max-scroll-x new-scroll-x))))
                     (reset! internal-scroll-columns-into-view (deref-or-value scroll-columns-into-view))))

                 ;; If model count has changed and now has less rows than before AND the current scroll-y is beyond the new max-scroll-y, reset to end of table
                 (when (> @scroll-y @max-scroll-y)
                   (reset! scroll-y (max 0 (min @max-scroll-y @scroll-y)))) ;; Might be more friendly to just reset to 0 ?

                 ;; Table sections by number
                 ;; 1  4  7
                 ;; 2  5  8
                 ;; 3  6  9

                 ;; TODO: [DJ] Suggested that the many merges below could be placed in the let above as reaction for performance improvements (readability would suffer a bit)

                 [box/h-box
                  :src      src
                  :debug-as (or debug-as (reflect-current-component))
                  :class    (str "rc-v-table " class " " (get-in parts [:wrapper :class]))
                  :style    (merge
                              {:max-width  max-width ;; Can't do equivalent of :max-height because we don't know column-header-width or column-footer-width
                               :max-height (when remove-empty-row-space?
                                             (+
                                               (or column-header-height 0)
                                               (or max-row-viewport-height (inc @content-rows-height)) ;; TODO: The inc prevents content scrollbar. Need to inc more if more than 1px borders specified
                                               (or column-footer-height 0)
                                               scrollbar-tot-thick))}

                               ;; TODO: Currently, scrolling a v-table with the mouse wheel also scrolls parent scrollbars (usually the one on the <body>)
                               ;; The solution seems to be to use CSS overscroll-behavior
                               ;; https://developers.google.com/web/updates/2017/11/overscroll-behavior
                               ;; The following should be in the right place but it makes no difference (also tried the block version)
                               ;; More research required to solve this

                               ;:overscroll-behavior "contain"
                               ;:overscroll-behavior-block "none"

                              (get-in parts [:wrapper :style]))
                  :attr     (merge {:on-wheel (handler-fn (on-wheel event))}
                                   (get-in parts [:wrapper :attr]))
                  :size     "auto"
                  :children [
                             ;; ========== LEFT SECTION (1, 2, 3) - row header area

                             [box/v-box
                              :src      (at)
                              :class    (str "rc-v-table-left-section " (get-in parts [:left-section :class]))
                              :style    (get-in parts [:left-section :style])
                              :attr     (get-in parts [:left-section :attr])
                              :children [
                                         ;; ========== SECTION 1 - top-left

                                         [top-left-content
                                          top-left-renderer
                                          ;-----------------
                                          column-header-height
                                          ;-----------------
                                          (get-in parts [:top-left :class])
                                          (get-in parts [:top-left :style])
                                          (get-in parts [:top-left :attr])]

                                         ;; ========== SECTION 2 - row-headers

                                         [row-header-viewport
                                          row-header-renderer
                                          key-fn
                                          @top-row-index
                                          (if virtual? @virtual-rows @model)           ;; rows
                                          (if virtual? @virtual-scroll-y @scroll-y)    ;; scroll-y
                                          ;-----------------
                                          row-header-selection-fn
                                          selection-fns
                                          (and row-header-selection-fn @sel-parent-bounding-rect (= @selection-target :row-header)) ;; selection-allowed?
                                          ;-----------------
                                          row-viewport-height
                                          @content-rows-height
                                          ;-----------------
                                          (get-in parts [:row-headers :class])
                                          (get-in parts [:row-headers :style])
                                          (get-in parts [:row-headers :attr])
                                          (get-in parts [:row-header-selection-rect :class])
                                          (get-in parts [:row-header-selection-rect :style])
                                          (get-in parts [:row-header-selection-rect :attr])
                                          (get-in parts [:row-header-content :class])
                                          (get-in parts [:row-header-content :style])
                                          (get-in parts [:row-header-content :attr])]

                                         ;; ========== SECTION 3 - bottom-left

                                         [bottom-left-content
                                          bottom-left-renderer
                                          ;-----------------
                                          column-footer-height
                                          ;-----------------
                                          (get-in parts [:bottom-left :class])
                                          (get-in parts [:bottom-left :style])
                                          (get-in parts [:bottom-left :attr])]

                                         [box/gap
                                          :src  (at)
                                          :size (px scrollbar-tot-thick)]]]

                             ;; ========== MIDDLE SECTION (4, 5, 6) - column header/footer and content area

                             [box/v-box
                              :src      (at)
                              :class    (str "rc-v-table-middle-section " (get-in parts [:middle-section :class]))
                              :style    (merge {:max-width (px @content-rows-width)}
                                               (get-in parts [:middle-section :style]))
                              :attr     (get-in parts [:middle-section :attr])
                              :size     (if row-viewport-width "none" "auto")
                              :children [
                                         ;; ========== SECTION 4 - column-headers

                                         [column-header-viewport
                                          column-header-renderer
                                          @scroll-x
                                          ;-----------------
                                          column-header-selection-fn
                                          selection-fns
                                          (and column-header-selection-fn @sel-parent-bounding-rect (= @selection-target :column-header)) ;; selection-allowed?
                                          ;-----------------
                                          row-viewport-width
                                          column-header-height
                                          @content-rows-width
                                          ;-----------------
                                          (get-in parts [:column-headers :class])
                                          (get-in parts [:column-headers :style])
                                          (get-in parts [:column-headers :attr])
                                          (get-in parts [:column-header-selection-rect :class])
                                          (get-in parts [:column-header-selection-rect :style])
                                          (get-in parts [:column-header-selection-rect :attr])
                                          (get-in parts [:column-header-content :class])
                                          (get-in parts [:column-header-content :style])
                                          (get-in parts [:column-header-content :attr])]

                                         ;; ========== SECTION 5 - rows (main content area)

                                         [row-viewport
                                          row-renderer
                                          key-fn
                                          @top-row-index
                                          (if virtual? @virtual-rows @model)           ;; rows
                                          @scroll-x
                                          (if virtual? @virtual-scroll-y @scroll-y)    ;; scroll-y
                                          ;-----------------
                                          row-selection-fn
                                          selection-fns
                                          (and row-selection-fn @sel-parent-bounding-rect (= @selection-target :row)) ;; selection-allowed?
                                          ;-----------------
                                          row-viewport-height
                                          row-viewport-width
                                          row-viewport-id
                                          @content-rows-height
                                          @content-rows-width
                                          ;-----------------
                                          (get-in parts [:rows :class])
                                          (get-in parts [:rows :style])
                                          (get-in parts [:rows :attr])
                                          (get-in parts [:row-selection-rect :class])
                                          (get-in parts [:row-selection-rect :style])
                                          (get-in parts [:row-selection-rect :attr])
                                          (get-in parts [:row-content :class])
                                          (get-in parts [:row-content :style])
                                          (get-in parts [:row-content :attr])]

                                         ;; ========== SECTION 6 - column-footers

                                         [column-footer-viewport
                                          column-footer-renderer
                                          @scroll-x
                                          ;-----------------
                                          row-viewport-width
                                          column-footer-height
                                          ;-----------------
                                          (get-in parts [:column-footers :class])
                                          (get-in parts [:column-footers :style])
                                          (get-in parts [:column-footers :attr])
                                          (get-in parts [:column-footer-content :class])
                                          (get-in parts [:column-footer-content :style])
                                          (get-in parts [:column-footer-content :attr])]

                                         ;; ========== Horizontal scrollbar section

                                         [scrollbar
                                          :src            (at)
                                          :class          (str "rc-v-table-h-scroll " (get-in parts [:h-scroll :class]))
                                          :type           :horizontal
                                          :length         @rl-row-viewport-width
                                          :width          scrollbar-thickness
                                          :content-length @content-rows-width
                                          :scroll-pos     @scroll-x
                                          :on-change      on-h-scroll-change
                                          :style          (merge {:margin (px-n scrollbar-margin 0)}
                                                                 (get-in parts [:h-scroll :style]))
                                          :attr           (get-in parts [:h-scroll :attr])]]]

                             ;; ========== Right section (7, 8, 9) - row footer area

                             [box/v-box
                              :src      (at)
                              :class    (str "rc-v-table-right-section " (get-in parts [:right-section :class]))
                              :style    (get-in parts [:right-section :style])
                              :attr     (get-in parts [:right-section :attr])
                              :children [
                                         ;; ========== SECTION 7 - top-right

                                         [top-right-content
                                          top-right-renderer
                                          ;-----------------
                                          column-header-height
                                          ;-----------------
                                          (get-in parts [:top-right :class])
                                          (get-in parts [:top-right :style])
                                          (get-in parts [:top-right :attr])]

                                         ;; ========== SECTION 8 - row-footers

                                         [row-footer-viewport
                                          row-footer-renderer
                                          key-fn
                                          @top-row-index
                                          (if virtual? @virtual-rows @model)            ;; rows
                                          (if virtual? @virtual-scroll-y @scroll-y)    ;; scroll-y
                                          ;-----------------
                                          row-viewport-height
                                          @content-rows-height
                                          ;-----------------
                                          (get-in parts [:row-footers :class])
                                          (get-in parts [:row-footers :style])
                                          (get-in parts [:row-footers :attr])
                                          (get-in parts [:row-footer-content :class])
                                          (get-in parts [:row-footer-content :style])
                                          (get-in parts [:row-footer-content :attr])]

                                         ;; ========== SECTION 9 - bottom-right

                                         [bottom-right-content
                                          bottom-right-renderer
                                          ;-----------------
                                          column-footer-height
                                          ;-----------------
                                          (get-in parts [:bottom-right :class])
                                          (get-in parts [:bottom-right :style])
                                          (get-in parts [:bottom-right :attr])]

                                         [box/gap
                                          :src  (at)
                                          :size (px scrollbar-tot-thick)]]]

                             ;; ========== Vertical scrollbar section

                             [box/v-box
                              :src      (at)
                              :class    (str "rc-v-table-v-scroll-section " (get-in parts [:v-scroll-section :class]))
                              :style    (get-in parts [:v-scroll-section :style])
                              :attr     (get-in parts [:v-scroll-section :attr])
                              :children [[box/gap
                                          :src  (at)
                                          :size (px (or column-header-height 0))]
                                         [box/box
                                          :src   (at)
                                          :size  "auto"
                                          :child [scrollbar
                                                  :src            (at)
                                                  :class          (str "rc-v-table-v-scroll " (get-in parts [:v-scroll :class]))
                                                  :type           :vertical
                                                  :length         @rl-row-viewport-height
                                                  :width          scrollbar-thickness
                                                  :content-length @content-rows-height
                                                  :scroll-pos     @scroll-y
                                                  :on-change      on-v-scroll-change
                                                  :style          (merge {:margin (px-n 0 scrollbar-margin)}
                                                                         (get-in parts [:v-scroll :style]))
                                                  :attr           (get-in parts [:v-scroll :attr])]]
                                         [box/gap
                                          :src  (at)
                                          :size (px (or column-footer-height 0))]
                                         [box/gap
                                          :src  (at)
                                          :size (px scrollbar-tot-thick)]]]

                             ;; ========== Debug section

                             #_[:pre
                                {:style {:font-size "11px"
                                         :min-width "220px"}}
                                (str
                                  "virtual?: "          virtual? "\n"
                                  "row-height: "        row-height "\n"
                                  "rows-per-viewport: " @rows-per-viewport "\n"
                                  "rows: "              (if virtual? (count @virtual-rows) (count @model)) " of " (count @model) "\n"
                                  "\n"

                                  "top-row-index: "     @top-row-index "\n"
                                  "bot-row-index: "     @bot-row-index "\n"
                                  "max-scroll-y: "      @max-scroll-y "\n"
                                  "scroll-y: "          @scroll-y "\n"
                                  "v-scroll-y: "        @virtual-scroll-y "\n"
                                  "\n"

                                  "left-col-px: "       @scroll-x "\n"
                                  "right-col-px: "      (+ @scroll-x @rl-row-viewport-width -1) "\n"
                                  "max-scroll-x: "      @max-scroll-x "\n"
                                  "scroll-x: "          @scroll-x "\n"
                                  "\n"

                                  "selection-target: "  (if @dragging? @selection-target "-") "\n"
                                  "sel-parent-l/t: "    (if @dragging? (str "(" (.-left @sel-parent-bounding-rect) "," (.-top @sel-parent-bounding-rect) ")") "-") "\n"
                                  "sel-parent-r/b: "    (if @dragging? (str "(" (.-right @sel-parent-bounding-rect) "," (.-bottom @sel-parent-bounding-rect) ")") "-") "\n"
                                  "sel-parent-w/h: "    (if @dragging? (str "(" (.-width @sel-parent-bounding-rect) "," (.-height @sel-parent-bounding-rect) ")") "-") "\n"
                                  "\n"

                                  "sel-x/y-start: "     (if @dragging? (str "(" @sel-content-x-start "," @sel-content-y-start ")") "-") "\n"
                                  "sel-x/y-end: "       (if @dragging? (str "(" @sel-content-x-end "," @sel-content-y-end ")") "-") "\n"
                                  "dragging-outside?: " @dragging-outside? "\n"
                                  "sel-rows: "          (if @dragging? (str "(" (:start-row @coords-debug) "," (:end-row @coords-debug) ")") "-") "\n"
                                  "sel-cols: "          (if @dragging? (str "(" (:start-col @coords-debug) "," (:end-col @coords-debug) ")") "-") "\n"
                                  "clientXY: "          (if @dragging? (str "(" (.-clientX @event-debug) "," (.-clientY @event-debug) ")") "-") "\n"

                                  "viewport-wh: "       (str "(" (.-innerWidth js/window) "," (.-innerHeight js/window) ")") "\n"
                                  "content-rows-wh: "   (str "(" @content-rows-width "," @content-rows-height ")") "\n")]]])))})))))

                                  ;"call-count: "       @call-count "\n"
