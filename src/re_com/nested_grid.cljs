(ns re-com.nested-grid
  (:require-macros
   [re-com.core         :refer [handler-fn]])
  (:require
   [clojure.string :as str]
   [re-com.util :as u :refer [px deref-or-value]]
   [reagent.core :as r]
   [re-com.debug :as debug]
   [re-com.config      :as config :refer [include-args-desc?]]
   [re-com.validate    :refer [vector-atom? ifn-or-nil? map-atom? parts? part? css-class?]]
   [re-com.theme :as theme]
   [re-com.box :as box]
   re-com.nested-grid.theme
   [re-com.buttons :as buttons]))

(def nested-grid-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 1 :impl "[:div]"}
     {:name :export-button :level 2 :impl "[:div]"}
     {:name :outer-grid-container :level 2 :impl "[:div]"}
     {:name :corner-header-grid-container :level 3 :impl "[:div]"}
     {:name :corner-header-wrapper :level 4 :impl "[:div]"}
     {:name :corner-header :level 5 :impl "[:div]"}
     {:name :column-header-grid-container :level 3 :impl "[:div]"}
     {:name :column-header-wrapper :level 4 :impl "[:div]"}
     {:name :column-header :level 5 :impl "[:div]"}
     {:name :row-header-grid-container :level 3 :impl "[:div]"}
     {:name :row-header-wrapper :level 4 :impl "[:div]"}
     {:name :row-header :level 5 :impl "[:div]"}
     {:name :cell-grid-container :level 3 :impl "[:div]"}
     {:name :cell-wrapper :level 4 :impl "[:div]"}
     {:name :cell :level 5 :impl "[:div]"}
     {:name :zebra-stripe :level 5 :impl "[:div]"}]))

(def nested-grid-parts
  (when include-args-desc?
    (-> (map :name nested-grid-parts-desc) set)))

(def nested-grid-args-desc
  (when include-args-desc?
    [{:name :cell
      :default "constantly nil"
      :type "part"
      :validate-fn part?
      :description
      [:span "String, hiccup or function. When a function, acceps keyword args "
       [:code ":column-path"] " and " [:code ":row-path"]
       ". Returns either a string or hiccup, which will appear within a single grid cell."]}
     {:name :cell-value
      :type "function"
      :required false
      :validate-fn ifn?
      :description
      [:span "Before calling " [:code ":cell"] ", " [:code "nested-grid"] " evaluates "
       [:code ":cell-value"] "with the same arguments. It then passes the return value to "
       [:code ":cell"] ", via a " [:code ":value"] " prop."]}
     {:name :column-tree
      :default "[]"
      :type "vector or seq of column-specs or column-trees"
      :validate-fn seq?
      :description
      [:span "Describes a nested arrangement of " [:code ":column-spec"] "s. "
       "A spec's path derives from its depth within the hierarchy of vectors or seqs. "
       " When a non-vector A precedes a vector B, then the items of B are children of A."
       " When a non-vector C follows B, then C is a sibling of A."
       " This nesting can be arbitrarily deep."]}
     {:name :row-tree
      :default "[]"
      :type "vector or seq of row-specs or row-trees"
      :validate-fn seq?
      :description
      [:span "Describes a nested arrangement of " [:code ":row-spec"] "s. "
       "A spec's path derives from its depth within the hierarchy of vectors or seqs. "
       " When a non-vector A precedes a vector B, then the items of B are children of A."
       " When a non-vector C follows B, then C is a sibling of A."
       " This nesting can be arbitrarily deep."]}
     {:name :column-header
      :type "part"
      :validate-fn part?
      :description
      [:span "A string, hiccup, or function of " [:code "{:keys [column-path]}"] "."
       " By default, returns the " [:code ":label"] ", " [:code ":id"]
       ", or else a string of the entire value of the last item in "
       [:code ":column-path"] "."]}
     {:name :row-header
      :type "part"
      :validate-fn part?
      :description
      [:span "A string, hiccup, or function of " [:code "{:keys [row-path]}"] "."
       " By default, returns the " [:code ":label"] ", " [:code ":id"]
       ", or else a string of the entire value of the last item in "
       [:code ":row-path"] "."]}
     {:name :corner-header
      :type "part"
      :validate-fn part?
      :description
      [:span "A string, hiccup, or function of " [:code "{:keys [row-index column-index]}"] "."
       " Both row-index and column-index are integers. By default, returns " [:code "nil"] "."]}
     {:name :cell-wrapper
      :type "part"
      :validate-fn part?
      :description
      [:span "A wrapper div, responsible for positioning one " [:code ":cell"]
       " within the css grid."]}
     {:name :column-header-wrapper
      :type "part"
      :validate-fn part?
      :description
      [:span "A wrapper div, responsible for positioning one " [:code ":column-header"]
       " within the css grid."]}
     {:name :row-header-wrapper
      :type "part"
      :validate-fn part?
      :description
      [:span "A wrapper div, responsible for positioning one " [:code ":row-header"]
       " within the css grid."]}
     {:name :corner-header-wrapper
      :type "part"
      :validate-fn part?
      :description
      [:span "A wrapper responsible for positioning one " [:code ":corner-header"]
       " within the css grid."]}
     {:name :export-button
      :type "part"
      :validate-fn part?
      :description [:span "Receives an " [:code ":on-click"]
                    " prop, a function which calls " [:code ":on-export"] "."]}
     {:name :show-branch-paths?
      :type "boolean"
      :default "false"
      :validate-fn boolean?
      :description
      [:span "When " [:code "true"] ", displays cells and headers for all "
       [:code ":column-paths"] " and " [:code ":row-paths"] ", not just the leaf paths."]}
     {:name :theme-cells?
      :type "boolean"
      :default "true"
      :validate-fn boolean?
      :description
      [:span "When " [:code "false"] ", uses the " [:code ":cell"] " function directly, "
       " not wrapping or themeing it. That means your theme fn will not get called with a "
       [:code "part"] " value of " [:code "::cell"] " or " [:code "::cell-wrapper"] ". "
       "the " [:code "::cell-wrapper"] " part will not be used at all. Your "
       [:code ":cell"] " will be passed a " [:code ":style"] " prop, and "
       "it must return a div with that style applied (necessary for grid positioning). "
       "This improves performance for grids with a high number of cells."]}
     {:name :resize-columns?
      :type "boolean"
      :default "true"
      :validate-fn boolean?
      :description
      [:span "When " [:code "true"] ", display a draggable resize button on column-header grid lines."]}
     {:name :resize-rows?
      :type "boolean"
      :default "false"
      :validate-fn boolean?
      :description
      [:span "When " [:code "true"] ", display a draggable resize button on row-header grid lines."]}
     {:default true
      :description
      "If true removes whitespace between the last row and the horizontal scrollbar. Useful for tables without many rows where otherwise
 there would be a big gap between the last row and the horizontal scrollbar at the bottom of the available space."
      :name :remove-empty-row-space?
      :required false
      :type "boolean"}
     {:default true
      :description
      "If true removes whitespace between the last column and the vertical scrollbar. Useful for tables without many columns where otherwise
 there would be a big gap between the last column and the vertical scrollbar at the right of the available space."
      :name :remove-empty-column-space?
      :required false
      :type "boolean"}
     {:name :max-height
      :required false
      :type "string"
      :validate-fn string?
      :description [:span "standard CSS max-height setting of the entire grid. "
                    "Literally constrains the grid to the given width so that "
                    "if the grid is taller than this it will add scrollbars. "
                    "Ignored if value is larger than the combined width of "
                    "all the rendered grid rows."]}
     {:name :max-width
      :required false
      :type "string"
      :validate-fn string?
      :description
      [:span "standard CSS max-width setting of the entire grid. "
       "Literally constrains the grid to the given width so that "
       "if the grid is wider than this it will add scrollbars."
       " Ignored if value is larger than the combined width of all the rendered grid columns."]}
     {:name :column-header-height
      :default 30
      :type "number"
      :validate-fn number?
      :description
      [:span "The default height that a column-header will use. "
       "Can be overridden by a " [:code ":height"] "key in the "
       [:code ":column-spec"] ", or by component-local state."]}
     {:name :row-height
      :default 25
      :type "number"
      :validate-fn number?
      :description
      [:span "The default height that a row will use. "
       "Can be overridden by a " [:code ":height"] "key in the "
       [:code ":row-spec"] ", or by component-local state."]}
     {:name :column-width
      :default 30
      :type "number"
      :validate-fn number?
      :description
      [:span "The default width that a column of grid cells will use. "
       "Can be overridden by a " [:code ":height"] "key in the "
       [:code ":column-spec"] ", or by component-local state."]}
     {:name :row-header-width
      :default 30
      :type "number"
      :validate-fn number?
      :description
      [:span "The default width that a row-header will use. "
       "Can be overridden by a " [:code ":width"] "key in the "
       [:code ":row-spec"] ", or by component-local state."]}
     {:name :row-width
      :default 30
      :type "number"
      :validate-fn number?
      :description
      [:span "The default width that a row of grid cells will use. "
       "Can be overridden by a " [:code ":width"]
       "key in the " [:code ":row-spec"] ", or by component-local state."]}
     {:name :sticky?
      :default false
      :type "boolean"
      :description
      [:span "When true, disables scroll bars on the wrapper. "
       "In that case: "
       [:ul
        [:li "Header cells \"stick\" to the first ancestor which is a scroll container"]
        [:li [:code ":max-width"] " and " [:code ":max-height"] " have no effect"]
        [:li [:code ":sticky-top"] " and " [:code ":sticky-left"] " take effect"]]
       "See css sticky positioning for details. "]}
     {:name :sticky-top
      :default false
      :type "number"
      :validate-fn number?
      :description
      [:span "When " [:code ":sticky?"] " is true, "
       "header cells (and the top buttons) stick to an ancestor scroll container. "
       [:code ":sticky-top"] " Adds a pixel offset, making them stick higher or lower on the page."
       " Useful to prevent overlap, for instance, if the page header is sticky, absolute or fixed."]}
     {:name :sticky-left
      :default false
      :type "number"
      :validate-fn number?
      :description
      [:span "When " [:code ":sticky?"] " is true, "
       "header cells (and the top buttons) stick to an ancestor scroll container. "
       [:code ":sticky-left"] " Adds a pixel offset, making them stick further left or right on the page."
       " Useful to prevent overlap, for instance, if the page sidebar is sticky, absolute or fixed."]}
     {:name :show-export-button?
      :required false
      :default false
      :type "boolean"
      :description
      [:span "When non-nil, adds a hiccup of " [:code ":export-button-render"]
       " to the component tree."]}
     {:name :on-export
      :required false
      :type "function"
      :validate-fn ifn?
      :description
      [:span "Called whenever the export button is clicked. "
       "Can expect to be passed several keyword arguments. "
       "Each argument is a nested vector of cell values. "
       [:ul
        [:li [:strong [:code ":rows"]] ": "
         "Everything."]
        [:li [:strong [:code ":header-rows"]] ": "
         "Everything above the cells. Each row includes spacers for the top-left corner, "
         "followed by column headers."]
        [:li [:strong [:code ":main-rows"]] ": "
         "Includes first row of main cells and everything beneath it. "
         "Each row includes the row-headers, followed by the main cells."]
        [:li [:strong [:code ":cells"]] ": "
         "Just the cells, without any headers."]
        [:li [:strong [:code ":spacers"]] ": "
         "Just the spacers in the top-left corner."]
        [:li [:strong [:code ":row-headers"]] ": "
         "Just the row headers, no cells."]
        [:li [:strong [:code ":column-headers"]] ": "
         "Just the column headers, no cells."]
        [:li [:strong [:code ":default"]] ": "
         [:code "nested-grid"] "'s default function for " [:code ":on-export"] ". "
         "This joins the rows into a single string of tab-separated values, then "
         "writes that string to the clipboard."]]]}
     {:name :on-init-export-fn
      :type "function"
      :validate-fn ifn?
      :description "Called whenever nested-grid's internal export function changes."}
     {:name :on-export-cell
      :required false
      :type "{:keys [row-path column-path]} -> string"
      :validate-fn ifn?
      :description
      [:span "Similar to " [:code ":cell"] ", but its return value should be serializable. "
       "Returning a hiccup or render-fn is probably a bad idea. "
       "After the export button is clicked, " [:code "nested-grid"] " maps "
       [:code ":on-export-cell"] "over any cells marked for export, passing the "
       "results to " [:code ":on-export"] "."]}
     {:name :on-export-row-header
      :required false
      :type "{:keys [row-path]} -> string"
      :validate-fn ifn?
      :description
      [:span "Similar to " [:code ":row-header"]
       ", but it should return a string value only."
       " After the export button is clicked, " [:code "nested-grid"] " maps "
       [:code ":on-export-row-header"] "over any row headers marked for export, passing the "
       "results to " [:code ":on-export"] " via the " [:code ":main-rows"] " prop."]}
     {:name :on-export-column-header
      :required false
      :type "{:keys [column-path]} -> string"
      :validate-fn ifn?
      :description
      [:span "Similar to " [:code ":column-header"]
       ", but it should return a string value only."
       " After the export button is clicked, " [:code "nested-grid"] " maps "
       [:code ":on-export-column-header"] "over any column-headers marked for export, passing the "
       "results to " [:code ":on-export"] " via the " [:code ":header-rows"] " prop."]}
     {:name :on-export-corner-header
      :type "{:keys [row-index column-index]} -> string"
      :validate-fn ifn?
      :description
      [:span "Similar to " [:code ":corner-header"]
       ", but it should return a string value only."
       " After the export button is clicked, " [:code "nested-grid"] " maps "
       [:code ":on-export-column-header"] " over all the top-left corner cells, passing the "
       "results to " [:code ":on-export"] " via the " [:code ":header-rows"] " prop."]}
     {:name :show-selection-box?
      :default false
      :type "boolean"
      :validate-fn boolean?
      :description
      [:span "when true, dragging the mouse causes an excel-style "
       "selection box to appear. When there is a selection box, any export behavior "
       "takes the bounds of that box into account. For instance, if 2 cells are "
       "selected, then only 2 cells are exported."]}
     {:description
      [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys"
       [:code ":file"] "and" [:code ":line"] ". See 'Debugging'."]
      :name :src
      :required false
      :type "map"
      :validate-fn map?}]))

(defn descendant? [path-a path-b]
  (and (not (>= (count path-a) (count path-b)))
       (= path-a (vec (take (count path-a) path-b)))))

(defn ancestor? [path-a path-b]
  (descendant? path-b path-a))

(def spec? (some-fn vector? seq?))
(def item? (complement spec?))

(defn header-spec->header-paths
  ([spec]
   (header-spec->header-paths [] [] spec))
  ([path acc [left & [right :as remainder]]]
   (let [next-acc  (cond (item? left) (conj acc (conj path left))
                         (spec? left) (header-spec->header-paths path acc left))
         next-path (cond (and (item? left) (spec? right))            (conj path left)
                         (and (spec? left) (item? right) (seq path)) (pop path)
                         :else                                       path)]
     (if (empty? remainder)
       next-acc
       (recur next-path next-acc remainder)))))

(defn leaf-paths [paths]
  (reduce (fn [paths p] (remove (partial ancestor? p) paths)) paths paths))

(def spec->headers* (memoize header-spec->header-paths))

(assert (= (header-spec->header-paths [:a :b :c])
           [[:a] [:b] [:c]]))

(assert (= (header-spec->header-paths [:a [:b :c]])
           [[:a] [:a :b] [:a :c]]))

(assert (= (header-spec->header-paths [:a :b [:c]])
           [[:a] [:b] [:b :c]]))

(assert (= (header-spec->header-paths [[:a [:b :c]]])
           [[:a] [:a :b] [:a :c]]))

(assert (= (header-spec->header-paths [[:x [:b :c]]
                                       [:y [:b :c]]])
           [[:x] [:x :b] [:x :c] [:y] [:y :b] [:y :c]]))

(defn end-branch? [path paths]
  ((->> paths
        (group-by count)
        vals
        (map last)
        set)
   path))

(defn start-branch? [path paths]
  ((->> paths
        (group-by count)
        vals
        (map first)
        set)
   path))

(defn header-cross-span [path all-paths]
  (->> all-paths
       (filter (partial descendant? path))
       count
       inc))

(defn header-main-span [path all-paths]
  (->> all-paths (map count) (apply max) (+ (- (count path))) inc))

(defn resize-overlay [{:keys [drag mouse-x on-resize last-mouse-x mouse-y last-mouse-y]}]
  [:div {:on-mouse-up   #(do (reset! drag false)
                             #_(reset! hovering? false))
         :on-mouse-move (if-let [on-resize @on-resize]
                          #(do (.preventDefault %)
                               (let [x (.-clientX %)
                                     y (.-clientY %)]
                                 (reset! mouse-x x)
                                 (reset! mouse-y y)
                                 (on-resize {:x-distance (- x @last-mouse-x)
                                             :y-distance (- y @last-mouse-y)})
                                 (reset! last-mouse-x x)
                                 (reset! last-mouse-y y)))
                          #(do (.preventDefault %)
                               (let [x (.-clientX %)
                                     y (.-clientY %)]
                                 (reset! mouse-x x)
                                 (reset! last-mouse-x x)
                                 (reset! last-mouse-y y))))
         :style         {:position             "fixed"
                         :z-index              3
                         :width                "100%"
                         :height               "100%"
                         :top                  0
                         :left                 0
                         :font-size            100
                         :cursor               (case @drag
                                                 ::column "col-resize"
                                                 ::row    "row-resize"
                                                 nil)
                         #_#_:background-color "rgba(0,0,0,0.4"}}])

(defn resize-button [& {:keys [drag]}]
  (let [dragging? (r/atom false)
        hovering? (r/atom nil)]
    (fn [& {:keys [dimension on-resize path resize-handler selection?
                   edge
                   mouse-down-x last-mouse-x mouse-x
                   mouse-down-y last-mouse-y mouse-y]}]
      [:div {:on-mouse-enter #(reset! hovering? true)
             :on-mouse-leave #(reset! hovering? false)
             :on-mouse-down  (case dimension
                               :column
                               #(do
                                  (.preventDefault %)
                                  (reset! selection? nil)
                                  (reset! resize-handler
                                          (fn [props]
                                            (on-resize (merge {:path path} props))))
                                  (reset! drag    ::column)
                                  (reset! mouse-down-x (.-clientX %))
                                  (reset! mouse-x      (.-clientX %))
                                  (reset! last-mouse-x (.-clientX %)))
                               :row
                               #(do
                                  (.preventDefault %)
                                  (reset! selection? nil)
                                  (reset! resize-handler
                                          (fn [props]
                                            (on-resize (merge {:path path} props))))
                                  (reset! drag    ::row)
                                  (reset! mouse-down-y (.-clientY %))
                                  (reset! mouse-y      (.-clientY %))
                                  (reset! last-mouse-y (.-clientY %))))

             :style (merge {:position         "absolute"
                            :opacity          (if (or @hovering? @dragging?) 1 0)
                            :z-index          9999999
                            :background-color "rgba(0,0,0,0.1)"}
                           (when (= :column dimension)
                             {:top    0
                              :cursor "col-resize"
                              :height "100%"
                              :width  (if (get edge :right)
                                        "4px"
                                        "9px")
                              :right  (if (get edge :right)
                                        0
                                        "-4px")})
                           (when (= :row dimension)
                             {:left   0
                              :cursor "row-resize"
                              :height (if (get edge :bottom)
                                        "4px"
                                        "9px")
                              :width  "100%"
                              :bottom (if (get edge :bottom)
                                        0
                                        "-4px")}))}])))

(defn path->grid-line-name [path]
  (str "line__" (hash path) "-start"))

(defn grid-template
  ([tokens & more-tokens]
   (grid-template (apply concat tokens more-tokens)))
  ([tokens]
   (let [rf (fn [s group]
              (str s " "
                   (cond (number? (first group))
                         (str/join " " (map px group))
                         (string? (first group))
                         (str/join " " group)
                         :else
                         (str "[" (str/join " " (map path->grid-line-name group)) "]"))))]
     (str
      (->> tokens
           (partition-by (some-fn number? string?))
           (reduce rf ""))
      " [end]"))))

(defn cell [{:keys [value]}]
  (str value))

(defn cell-wrapper [{:keys [column-path row-path class style attr theme children] :as props}]
  (into
   [:div
    (-> {:on-click (debug/log-on-alt-click props)
         :class class
         :style (merge {:grid-column (path->grid-line-name column-path)
                        :grid-row    (path->grid-line-name row-path)}
                       style)}
        (merge attr))]
   children))

(defn header-label [{:keys [path]}]
  (let [header (last path)]
    (str (or (:label header)
             (:id header)
             header))))

(defn column-header [props]
  [u/default-part
   (merge props {:children [(header-label props)]})])

(defn row-header [props]
  [u/default-part
   (merge props {:children [(header-label props)]})])

(def level count)

(defn quantize [quanta threshold]
  (dec (count (take-while #(< % threshold)
                          (reductions + quanta)))))

(assert (= 1 (quantize [10 10 10] 29)))
(assert (= 1 (quantize [10 10 10] 30)))
(assert (= 2 (quantize [10 10 10] 31)))

(defn drag-overlay [{:keys [drag selection? mouse-x mouse-y]}]
  [:div {:on-mouse-up   #(reset! drag nil)
         :on-mouse-move #(do
                           (reset! selection? true)
                           (.preventDefault %)
                           (reset! mouse-x (.-clientX %))
                           (reset! mouse-y (.-clientY %)))
         :style         {:position             "fixed"
                         :top                  0
                         :left                 0
                         :z-index              2147483647
                         :height               "100%"
                         :width                "100%"
                         #_#_:background-color "rgba(255,0,0,0.4)"}}])

(defn selection-part [_]
  (fn [_]
    (let [!ref       (r/atom nil)
          reset-ref! (partial reset! !ref)]
      (fn [{:keys [drag selection?
                   grid-columns grid-rows
                   mouse-x mouse-y mouse-down-x mouse-down-y
                   selection-grid-spec]
            :as   props}]
        (let []
          [:<>
           [:div {:ref           reset-ref!
                  :style         {:position "absolute"
                                  :height   "100%"
                                  :width    "100%"
                                  :top      0
                                  :left     0}
                  :on-mouse-up   #(reset! drag false)
                  :on-mouse-down #(do
                                    (if-not @selection?
                                      (do (reset! drag ::selection)
                                          (reset! selection? true)
                                          (reset! mouse-down-y (.-clientY %))
                                          (reset! mouse-down-x (.-clientX %))
                                          (reset! mouse-y (.-clientY %))
                                          (reset! mouse-x (.-clientX %)))
                                      (do (reset! selection? false)
                                          (reset! drag false)
                                          (reset! selection-grid-spec {}))))}]
           (when @selection?
             (let [grid-columns  (filter number? grid-columns)
                   grid-rows     (filter number? grid-rows)
                   bounds        (.getBoundingClientRect @!ref)
                   origin-x      (.-x bounds)
                   origin-y      (.-y bounds)
                   column-begin  (quantize grid-columns (- @mouse-down-x origin-x))
                   column-finish (quantize grid-columns (- @mouse-x origin-x))
                   row-begin     (quantize grid-rows (- @mouse-down-y origin-y))
                   row-finish    (quantize grid-rows (- @mouse-y origin-y))
                   grid-spec     {:grid-column-start (+ 2 (min column-begin column-finish))
                                  :grid-column-end   (+ 3 (max column-begin column-finish))
                                  :grid-row-start    (+ 2 (min row-begin row-finish))
                                  :grid-row-end      (+ 3 (max row-begin row-finish))}
                   _             (reset! selection-grid-spec grid-spec)]
               [:div {:style (merge @selection-grid-spec
                                    {:border         "2px solid dodgerblue"
                                     :background     "rgba(127,127,255,.1)"
                                     :position       "relative"
                                     :pointer-events "none"})}
                [:div {:style {:position   "absolute"
                               :background "white"
                               :border     "2px solid grey"
                               :height     10
                               :width      10
                               :right      -6
                               :bottom     -6}}]]))])))))

(defn nested-grid [& {:keys [column-width row-height]
                      :or   {column-width 60
                             row-height   30}}]
  (let [column-state        (r/atom {})
        row-state           (r/atom {})
        hover?              (r/atom false)
        drag                (r/atom nil)
        selection?          (r/atom nil)
        mouse-down-x        (r/atom 0)
        mouse-down-y        (r/atom 0)
        last-mouse-x        (r/atom 0)
        last-mouse-y        (r/atom 0)
        mouse-x             (r/atom 0)
        mouse-y             (r/atom 0)
        scroll-top          (r/atom 0)
        scroll-left         (r/atom 0)
        selection-grid-spec (r/atom {})
        column-header-prop  (fn [path k & [default]]
                              (or (some-> @column-state (get path) (get k))
                                  (get (meta (last path)) k)
                                  (get (last path) k)
                                  default))
        header-prop         (fn [path k dimension & [default]]
                              (let [state (-> (case dimension
                                                :row    @row-state
                                                :column @column-state)
                                              (get path))]
                                (first
                                 (remove nil? [(get state k)
                                               (get (meta (last path)) k)
                                               (get (last path) k)
                                               default]))))
        max-props           (fn [k dimension default paths]
                              (->> paths
                                   (group-by level)
                                   (sort-by key)
                                   (map val)
                                   (map (fn [path-group]
                                          (apply max
                                                 (map #(header-prop % k dimension default)
                                                      path-group))))))
        resize-column!      (fn [{:keys [x-distance path]}]
                              (swap! column-state update-in [path :width]
                                     #(-> (or %
                                              (column-header-prop path :width column-width))
                                          (+ x-distance)
                                          (max 0))))
        resize-row!         (fn [{:keys [y-distance path]}]
                              (swap! row-state update-in [path :height]
                                     #(-> (or %
                                              (header-prop path :height :row row-height))
                                          (+ y-distance)
                                          (max 0))))
        resize-handler      (r/atom #())]
    (fn [& {:as passed-in-props}]
      (let [{:as   props
             :keys [column-tree row-tree
                    cell-value theme-cells?
                    pre-theme theme
                    show-branch-paths?
                    max-height max-width
                    remove-empty-row-space? remove-empty-column-space?
                    column-width column-header-height row-header-width row-height
                    show-export-button? on-export
                    on-export-cell on-export-column-header on-export-row-header on-export-corner-header
                    on-init-export-fn
                    show-zebra-stripes?
                    show-selection-box? resize-columns? resize-rows?
                    sticky? sticky-left sticky-top
                    debug-parts? parts
                    class style attr
                    src]
             :or   {column-header-height       25
                    column-width               55
                    row-header-width           80
                    row-height                 25
                    sticky?                    false
                    sticky-left                0
                    sticky-top                 0
                    remove-empty-row-space?    true
                    remove-empty-column-space? true
                    show-export-button?        true
                    show-branch-paths?         false
                    show-selection-box?        false
                    show-zebra-stripes?        true
                    on-export-column-header    header-label
                    on-export-row-header       header-label
                    on-export-corner-header    (constantly nil)
                    resize-columns?            true
                    resize-rows?               false
                    theme-cells?               true
                    debug-parts?               (or config/debug? config/debug-parts?)}}
            passed-in-props            #_(theme/top-level-part passed-in-props ::nested-grid)
            {:keys [export-button column-header column-header-wrapper row-header row-header-wrapper
                    corner-header corner-header-wrapper cell cell-wrapper zebra-stripe corner-header-grid-container
                    column-header-grid-container row-header-grid-container cell-grid-container wrapper outer-grid-container]}
            (merge passed-in-props parts)
            #_#_theme                  (theme/defaults
                                        props
                                        {:user [(theme/<-props props {:part    ::wrapper
                                                                      :include [:style :class]})]})
            theme                      (theme/comp pre-theme theme)
            column-paths               (spec->headers* column-tree)
            column-leaf-paths          (leaf-paths column-paths)
            leaf-column?               (set column-leaf-paths)
            row-paths                  (spec->headers* row-tree)
            leaf-row?                  (set (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths))
            leaf?                      (fn [path dimension]
                                         (case dimension
                                           :column (leaf-column? path)
                                           :row    (leaf-row? path)))
            show?                      (fn [path dimension]
                                         (let [show-prop (header-prop path :show? dimension)
                                               result    (and (not (false? show-prop))
                                                              (or (true? show-prop)
                                                                  show-branch-paths?
                                                                  (leaf? path dimension)))]
                                           result))
            showing-column-paths       (filter #(show? % :column) column-paths)
            showing-row-paths          (filter #(show? % :row) row-paths)
            showing-column-widths      (map #(column-header-prop % :width column-width)
                                            showing-column-paths)
            showing-row-heights        (map #(column-header-prop % :height row-height)
                                            showing-row-paths)
            max-column-heights         (max-props :height :column column-header-height column-paths)
            max-row-widths             (max-props :width :row row-header-width row-paths)
            column-header-total-width  (apply + showing-column-widths)
            column-header-total-height (apply + max-column-heights)
            row-header-total-height    (apply + showing-row-heights)
            row-header-total-width     (apply + max-row-widths)
            all-sections               (->> (vals (group-by first column-paths))
                                            (remove (comp #{1} count))
                                            (mapcat #(vals (group-by level %))))
            section-left?              (set (map first all-sections))
            section-right?             (set (map last all-sections))
            cell-sections              (->> (vals (group-by first showing-column-paths))
                                            (remove (comp #{1} count))
                                            (mapcat #(vals (group-by level %))))
            cell-section-left?         (set (map first cell-sections))
            cell-section-right?        (set (map last cell-sections))
            column-depth               (count max-column-heights)
            row-depth                  (count max-row-widths)
            on-export-cell             (or on-export-cell (comp pr-str cell-value))
            default-on-export          (fn on-export [{:keys [rows]}]
                                         (->> rows (map u/tsv-line) str/join u/clipboard-write!))
            on-export                  (or on-export default-on-export)
            cell-grid-columns          (->> column-paths
                                            (mapcat (fn [path]
                                                      (let [width (header-prop path :width :column column-width)]
                                                        (if (show? path :column)
                                                          [path width]
                                                          [path])))))
            cell-grid-rows             (->> row-paths
                                            (mapcat (fn [path]
                                                      (let [height (header-prop path :height :row row-height)]
                                                        (if (show? path :row)
                                                          [path height]
                                                          [path])))))
            ancestry                   (fn ancestry [path]
                                         (reverse
                                          (take (count path)
                                                (iterate pop path))))
            transpose                  (partial apply mapv vector)
            export-corner-headers      #(mapv (fn [row-index]
                                                (mapv (fn [column-index]
                                                        (on-export-corner-header {:row-index    row-index
                                                                                  :column-index column-index
                                                                                  :edge
                                                                                  (cond-> #{}
                                                                                    (= row-index 0)                  (conj :top)
                                                                                    (= row-index (dec column-depth)) (conj :bottom)
                                                                                    (= column-index 0)               (conj :left)
                                                                                    (= column-index (dec row-depth)) (conj :right))}))
                                                      (range row-depth)))
                                              (range column-depth))
            export-column-headers      #(let [export-path (fn [path]
                                                            (on-export-column-header {:path path :row-path path}))]
                                          (transpose (mapv (fn [path] (mapv export-path (ancestry path)))
                                                           showing-column-paths)))
            export-row-headers         #(let [export-path (fn [path]
                                                            (on-export-row-header {:path path :row-path path}))]
                                          (mapv (fn [path] (mapv export-path (ancestry path)))
                                                showing-row-paths))
            export-cells               #(let [{:keys [grid-row-start grid-row-end grid-column-start grid-column-end]
                                               :as   selection-grid-spec}
                                              @selection-grid-spec
                                              selection?   (seq selection-grid-spec)
                                              row-paths    (cond-> showing-row-paths
                                                             :do        vec
                                                             selection? (subvec (dec grid-row-start)
                                                                                (dec grid-row-end)))
                                              column-paths (cond-> showing-column-paths
                                                             :do        vec
                                                             selection? (subvec (dec grid-column-start)
                                                                                (dec grid-column-end)))]
                                          (->> row-paths
                                               (mapv (fn [row-path]
                                                       (mapv (fn [column-path]
                                                               (let [props {:row-path    row-path
                                                                            :column-path column-path}
                                                                     props (cond-> props cell-value (merge {:value (cell-value props)}))]
                                                                 (on-export-cell props)))
                                                             column-paths)))))
            default-export-button      (fn [{:keys [on-click]}]
                                         [buttons/md-icon-button
                                          {:md-icon-name "zmdi zmdi-copy"
                                           :style        {:height         "18px"
                                                          :font-size      "18px"
                                                          :line-height    "18px"
                                                          :padding-bottom 0}
                                           :attr         {:title "Copy to Clipboard"}
                                           :on-click     on-click}])
            export-fn                  #(let [column-headers (export-column-headers)
                                              row-headers    (export-row-headers)
                                              corner-headers (export-corner-headers)
                                              cells          (export-cells)
                                              header-rows    (mapv into corner-headers column-headers)
                                              main-rows      (mapv into row-headers cells)
                                              rows           (concat header-rows main-rows)]
                                          (on-export
                                           {:column-headers column-headers
                                            :row-headers    row-headers
                                            :corner-headers corner-headers
                                            :cells          cells
                                            :header-rows    header-rows
                                            :main-rows      main-rows
                                            :rows           rows
                                            :default        default-on-export}))
            _                          (when on-init-export-fn (on-init-export-fn export-fn))
            export-part                (u/part export-button
                                         {:part  ::export-button
                                          :theme theme
                                          :props {:style    {:position :fixed
                                                             :right    10}
                                                  :on-click export-fn}
                                          :impl  default-export-button})
            column-header-cells        (for [path column-paths
                                             :let [edge     (cond-> #{}
                                                              (start-branch? path column-paths) (conj :left)
                                                              (end-branch? path column-paths)   (conj :right)
                                                              (= 1 (count path))                (conj :top)
                                                              (= (count path) column-depth)     (conj :bottom)
                                                              (section-left? path)              (conj :column-section-left)
                                                              (section-right? path)             (conj :column-section-right))
                                                   show?    (show? path :column)
                                                   props    {:theme       theme
                                                             :selection?  selection?
                                                             :edge        edge
                                                             :column-path path
                                                             :path        path
                                                             :header-spec (last path)
                                                             :show?       show?
                                                             :sticky?     sticky?
                                                             :row-header-total-width
                                                             row-header-total-width}
                                                   children [(u/part column-header
                                                               {:part  ::column-header
                                                                :theme theme
                                                                :props props
                                                                :impl  re-com.nested-grid/column-header})]]]
                                         ^{:key [::column (or path (gensym))]}
                                         [:div {:style {:grid-column-start (path->grid-line-name path)
                                                        :grid-column-end   (str "span " (cond-> path
                                                                                          :do         (header-cross-span showing-column-paths)
                                                                                          (not show?) dec))
                                                        :grid-row-start    (count path)
                                                        :grid-row-end      (str "span " (cond-> path
                                                                                          :do         (header-main-span showing-column-paths)
                                                                                          (not show?) dec))
                                                        :position          "relative"}}
                                          (u/part column-header-wrapper
                                            {:part  ::column-header-wrapper
                                             :theme theme
                                             :props (merge props
                                                           {:children children
                                                            :attr     {:on-click (debug/log-on-alt-click props)}})})
                                          (when (and resize-columns? show?)
                                            [resize-button (merge props {:mouse-down-x    mouse-down-x
                                                                         :last-mouse-x    last-mouse-x
                                                                         :mouse-x         mouse-x
                                                                         :resize-handler  resize-handler
                                                                         :resize-columns? resize-columns?
                                                                         :on-resize       resize-column!
                                                                         :edge            edge
                                                                         :drag            drag
                                                                         :dimension       :column
                                                                         :path            path})])])
            row-header-cells
            (for [path row-paths
                  :let [edge (cond-> #{}
                               (start-branch? path row-paths)                (conj :top)
                               ;; TODO: incorrect when the final path is shallower than the tree
                               (end-branch? path row-paths)                  (conj :bottom)
                               (= 1 (count path))                            (conj :left)
                               (or (= (count path) row-depth)
                                   (= 1 (header-cross-span path row-paths))) (conj :right))
                        show? (show? path :row)
                        props {:theme       theme
                               :selection?  selection?
                               :style       {:top sticky-top}
                               :edge        edge
                               :row-path    path
                               :path        path
                               :header-spec (last path)
                               :show?       show?
                               :sticky?     sticky?
                               :sticky-top  (cond-> column-header-total-height
                                              :do                               (+ sticky-top)
                                              (and sticky? show-export-button?) (+ 25))}
                        children [(u/part row-header
                                    {:part  ::row-header
                                     :theme theme
                                     :props props
                                     :impl  re-com.nested-grid/row-header})]]]
              ^{:key [::row (or path (gensym))]}
              [:div {:style {:grid-row-start    (path->grid-line-name path)
                             :grid-row-end      (str "span " (cond-> path
                                                               :do         (header-cross-span showing-row-paths)
                                                               (not show?) dec))
                             :grid-column-start (count path)
                             :grid-column-end   (str "span " (cond-> path
                                                               :do         (header-main-span showing-row-paths)
                                                               (not show?) dec))
                             :position          "relative"}}
               (u/part row-header-wrapper
                 {:part  ::row-header-wrapper
                  :theme theme
                  :props (merge props {:children children
                                       :attr     {:on-click (debug/log-on-alt-click props)}})})
               (when (and resize-rows? show?)
                 [resize-button (merge props {:mouse-down-x   mouse-down-x
                                              :last-mouse-x   last-mouse-x
                                              :mouse-x        mouse-x
                                              :resize-handler resize-handler
                                              :resize-rows?   resize-rows?
                                              :on-resize      resize-row!
                                              :edge           edge
                                              :drag           drag
                                              :dimension      :row
                                              :path           path})])])
            corner-header-cells        (for [y    (range column-depth)
                                             x    (range row-depth)
                                             :let [props    {:edge          (cond-> #{}
                                                                              (zero? y)                (conj :top)
                                                                              (zero? x)                (conj :left)
                                                                              (= y (dec column-depth)) (conj :bottom)
                                                                              (= x (dec row-depth))    (conj :right))
                                                             :style         {:grid-column (inc x)
                                                                             :grid-row    (inc y)}
                                                             :attr          {:on-click (debug/log-on-alt-click props)}
                                                             :theme         theme
                                                             :column-index  x
                                                             :row-index     y
                                                             :x             x
                                                             :y             y
                                                             :corner-header corner-header}
                                                   children [(u/part corner-header {:part  ::corner-header
                                                                                    :theme theme
                                                                                    :props props})]
                                                   props    (merge props {:children children})]]
                                         (u/part corner-header-wrapper
                                           {:theme theme
                                            :part  ::corner-header-wrapper
                                            :props props}))
            cells                      (if-not theme-cells?
                                         (for [row-path    showing-row-paths
                                               column-path showing-column-paths]
                                           [cell (cond-> {:style       {:grid-column (path->grid-line-name column-path)
                                                                        :grid-row    (path->grid-line-name row-path)}
                                                          :row-path    row-path
                                                          :column-path column-path}
                                                   cell-value
                                                   (merge {:value (cell-value {:column-path column-path :row-path row-path})})
                                                   debug-parts?
                                                   (merge {:attr {:on-click (re-com.debug/log-on-alt-click props)}}))])
                                         (for [row-path    showing-row-paths
                                               column-path showing-column-paths
                                               :let        [edge (cond-> #{}
                                                                   (= column-path (first showing-column-paths)) (conj :left)
                                                                   (= column-path (last showing-column-paths))  (conj :right)
                                                                   (= row-path (first showing-row-paths))       (conj :top)
                                                                   (= row-path (last showing-row-paths))        (conj :bottom)
                                                                   (cell-section-left? column-path)             (conj :column-section-left)
                                                                   (cell-section-right? column-path)            (conj :column-section-right))
                                                            value (when cell-value (cell-value {:column-path column-path
                                                                                                :row-path    row-path}))
                                                            props (cond-> {:cell  cell
                                                                           :theme theme
                                                                           :edge        edge
                                                                           :column-path column-path
                                                                           :row-path    row-path}
                                                                    value (merge {:value value}))
                                                            children [(u/part cell
                                                                        {:part  ::cell
                                                                         :theme theme
                                                                         :props (dissoc props :cell)
                                                                         :impl  re-com.nested-grid/cell})]]]
                                           (u/part cell-wrapper
                                             {:part  ::cell-wrapper
                                              :theme theme
                                              :props (merge props {:children children})
                                              :impl  re-com.nested-grid/cell-wrapper})))
            zebra-stripes              (for [i (filter even? (range 1 (inc (count row-paths))))]
                                         ^{:key [::zebra-stripe i]}
                                         (u/part zebra-stripe
                                           {:theme theme
                                            :part  ::zebra-stripe
                                            :props {:style
                                                    {:grid-column-start 1
                                                     :grid-column-end   "end"
                                                     :grid-row          i
                                                     :background-color  "#999"
                                                     :opacity           0.05
                                                     :z-index           1
                                                     :pointer-events    "none"}}}))
            box-selector               [selection-part
                                        {:drag                drag
                                         :grid-columns        cell-grid-columns
                                         :grid-rows           cell-grid-rows
                                         :selection?          selection?
                                         :mouse-x             mouse-x
                                         :mouse-y             mouse-y
                                         :mouse-down-x        mouse-down-x
                                         :mouse-down-y        mouse-down-y
                                         :selection-grid-spec selection-grid-spec}]
            overlays                   [:<> (when (= ::selection @drag)
                                              [drag-overlay {:drag         drag
                                                             :grid-columns cell-grid-columns
                                                             :grid-rows    cell-grid-rows
                                                             :selection?   selection?
                                                             :mouse-x      mouse-x
                                                             :mouse-y      mouse-y
                                                             :mouse-down-x mouse-down-x
                                                             :mouse-down-y mouse-down-y}])
                                        (when (#{::column ::row} @drag)
                                          [resize-overlay {:drag         drag
                                                           :mouse-x      mouse-x
                                                           :mouse-y      mouse-y
                                                           :last-mouse-x last-mouse-x
                                                           :last-mouse-y last-mouse-y
                                                           :on-resize    resize-handler}])]
            ;; FIXME This changes on different browsers - do we need to get it dynamically?
            ;; FIXME We should use :scrollbar-gutter (chrome>=94)
            native-width               (+ u/scrollbar-tot-thick
                                          column-header-total-width
                                          row-header-total-width)
            native-height              (+ u/scrollbar-thickness
                                          column-header-total-height
                                          row-header-total-height)
            control-panel              [:div {:style (merge {:display          :flex
                                                             :max-width        native-width
                                                             :justify-content  :flex-end
                                                             :height           25
                                                             :background-color :white
                                                             :z-index          2}
                                                            (when sticky?
                                                              {:position :sticky
                                                               :top      sticky-top}))}
                                        [box/v-box {:align    :center
                                                    :justify  :center
                                                    :style    {:position         :sticky
                                                               :background-color :white
                                                               :right            0
                                                               :width            25
                                                               :height           25
                                                               :margin-right     10}
                                                    :children [export-part]}]]
            corner-headers             (u/part corner-header-grid-container
                                         {:part  ::corner-header-grid-container
                                          :theme theme
                                          :props {:style    {:display               :grid
                                                             :box-sizing            :border-box
                                                             :position              :sticky
                                                             :top                   (cond-> sticky-top (and sticky? show-export-button?) (+ 25))
                                                             :left                  (if sticky? sticky-left 0)
                                                             :grid-column-start     1
                                                             :grid-row-start        1
                                                             :z-index               3
                                                             :grid-template-columns (grid-template max-row-widths)
                                                             :grid-template-rows    (grid-template max-column-heights)}
                                                  :children corner-header-cells}})
            column-headers             (u/part column-header-grid-container
                                         {:part  ::column-header-grid-container
                                          :theme theme
                                          :props {:style    {:position              :sticky
                                                             :top                   (cond-> sticky-top (and sticky? show-export-button?) (+ 25))
                                                             :width                 :fit-content
                                                             :z-index               2
                                                             :display               :grid
                                                             :grid-column-start     2
                                                             :grid-row-start        1
                                                             :grid-template-columns (grid-template cell-grid-columns)
                                                             :grid-template-rows    (grid-template max-column-heights)}
                                                  :children column-header-cells}})
            row-headers                (u/part row-header-grid-container
                                         {:part  ::row-header-grid-container
                                          :theme theme
                                          :props
                                          {:style    {:position              :sticky
                                                      :left                  (if sticky? sticky-left 0)
                                                      :z-index               1
                                                      :display               :grid
                                                      :grid-column-start     1
                                                      :grid-row-start        2
                                                      :grid-template-columns (grid-template max-row-widths)
                                                      :grid-template-rows    (grid-template cell-grid-rows)}
                                           :children row-header-cells}})
            cells                      (u/part cell-grid-container
                                         {:part  ::cell-grid-container
                                                :theme theme
                                          :props {:style    {:max-height            max-height
                                                             :max-width             max-width
                                                             :display               :grid
                                                             :grid-column-start     2
                                                             :grid-row-start        2
                                                             :grid-template-columns (grid-template cell-grid-columns)
                                                             :grid-template-rows    (grid-template cell-grid-rows)}
                                                  :children (cond-> cells
                                                              (and show-zebra-stripes? (> (count showing-row-paths) 3))
                                                              (concat zebra-stripes)
                                                              show-selection-box?
                                                              (conj box-selector))}})]
        (conj
         (u/part wrapper
           {:theme theme
            :part  ::wrapper
            :post-props {:style style
                         :class class
                         :attr  attr}
            :props (merge {:src   src
                           :style (merge {:flex-direction :column}
                                         (when-not sticky?
                                           (merge {:flex    "0 0 auto"
                                                   :display :flex}
                                                  (when remove-empty-column-space?
                                                    {:max-width :fit-content})
                                                  (when remove-empty-row-space?
                                                    {:max-height :fit-content}))))
                           :children
                           [(when show-export-button? control-panel)
                            (u/part outer-grid-container
                              {:theme theme
                               :part  ::outer-grid-container
                               :props {:attr     {:on-mouse-enter #(reset! hover? true)
                                                  :on-mouse-leave #(reset! hover? false)}
                                       :style
                                       (merge
                                        {:position              :relative
                                         :display               :grid
                                         :grid-template-columns (grid-template [(px row-header-total-width)
                                                                                (px column-header-total-width)])
                                         :grid-template-rows    (grid-template [(px column-header-total-height)
                                                                                "1fr"])}
                                        (when-not sticky?
                                          {:max-width  (or max-width (when remove-empty-column-space? native-width))
                                           :max-height (or max-height
                                                           (when remove-empty-row-space? native-height))
                                           :flex       "1 1 auto"
                                           :overflow   :auto}))
                                       :children [corner-headers
                                                  column-headers
                                                  row-headers
                                                  cells]}})
                            overlays]}
                          (debug/->attr passed-in-props))}))))))
