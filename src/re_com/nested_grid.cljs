(ns re-com.nested-grid
  (:require
   [clojure.string :as str]
   [re-com.util :as u :refer [px deref-or-value]]
   [reagent.core :as r]
   [re-com.config      :refer [debug? include-args-desc?]]
   [re-com.validate    :refer [vector-atom? ifn-or-nil? map-atom? parts? part?]]
   [re-com.theme :as theme]
   [re-com.box :as box]
   [re-com.buttons :as buttons]))

(def nested-grid-parts-desc {})

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
     {:name :header-spacer-wrapper
      :type "part"
      :validate-fn part?
      :description
      [:span "A wrapper responsible for positioning one " [:code ":header-spacer"]
       " within the css grid."]}
     {:name :show-branch-paths?
      :type "boolean"
      :default "false"
      :validate-fn boolean?
      :description
      [:span "When " [:code "true"] ", displays cells and headers for all "
       [:code ":column-paths"] " and " [:code ":row-paths"] ", not just the leaf paths."]}
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
      [:span "Called whenever the export button is clicked."
       " Expects keyword arguments "
       [:code ":header-rows"] " and " [:code ":main-rows"] "."]}
     {:name :on-export-cell
      :required false
      :type "{:keys [row-path column-path]} -> string"
      :validate-fn ifn?
      :description
      [:span "Similar to " [:code ":cell"] ", but it should return a string value only."
       " After the export button is clicked, " [:code "nested-grid"] " maps "
       [:code ":on-export-cell"] "over any cells marked for export, passing the "
       "results to " [:code ":on-export"] " via the " [:code ":main-rows"] " prop."]}
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
       [:code ":on-export-column-header"] "over any cells marked for export, passing the "
       "results to " [:code ":on-export"] " via the " [:code ":header-rows"] " prop."]}
     {:name :show-selection-box?
      :default false
      :type "boolean"
      :validate-fn boolean?
      :description
      [:span "when true, dragging the mouse causes an excel-style "
       "selection box to appear. When there is a selection box, any export behavior "
       "takes the bounds of that box into account. For instance, if 2 cells are "
       "selected, then only 2 cells are exported."]}]))

(defn descendant? [path-a path-b]
  (and (not= path-a path-b)
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

(defn root-level? [path paths]
  (let [min-count (apply min (map count paths))]
    ((->> paths
          (filter #(= min-count (count %)))
          set)
     path)))

(defn leaf-level? [path paths]
  (let [max-count (apply max (map count paths))]
    ((->> paths
          (filter #(= max-count (count %)))
          set)
     path)))

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
                              :width  "9px"
                              :right  "-4px"})
                           (when (= :row dimension)
                             {:left   0
                              :cursor "row-resize"
                              :height "9px"
                              :width  "100%"
                              :bottom "-4px"}))}])))

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

(defn cell-part [{:keys [column-path row-path]}]
  nil)

(defn cell-wrapper-part [{:keys [column-path row-path cell theme cell-value edge]
                          :as   props}]
  (let [props (cond-> props
                :do        (dissoc :cell)
                cell-value (assoc :value (cell-value props)))]
    [:div
     (-> {:style {:grid-column (path->grid-line-name column-path)
                  :grid-row    (path->grid-line-name row-path)}}
         (theme/apply {:state {:edge edge} :part ::cell-wrapper} theme))
     [u/part cell props :default cell-part]]))

(defn header-label [{:keys [path]}]
  (let [header (last path)]
    (str (or (:label header)
             (:id header)
             header))))

(defn column-header-part [{:keys [column-path]}]
  (header-label {:path column-path}))

(theme/apply {} {:part ::column-header-wrapper} [])

(defn column-header-wrapper-part
  [{:keys [column-header column-path column-paths theme show? edge resize-columns?]
    :as   props}]
  [:div
   {:style {:grid-column-start (path->grid-line-name column-path)
            :grid-column-end   (str "span " (cond-> column-path
                                              :do         (header-cross-span column-paths)
                                              (not show?) dec))
            :grid-row-start    (count column-path)
            :grid-row-end      (str "span " (cond-> column-path
                                              :do         (header-main-span column-paths)
                                              (not show?) dec))
            :position          "relative"}}
   [:div (theme/apply {} {:state {:edge edge} :part ::column-header-wrapper}
           theme)
    [u/part column-header props :default column-header-part]]
   (when (and resize-columns? show?)
     [resize-button (merge props {:dimension :column
                                  :path      column-path})])])

(defn row-header-part [{:keys [row-path]}]
  (header-label {:path row-path}))

(defn row-header-wrapper-part
  [{:keys [row-path row-paths row-header theme show? edge resize-rows?]
    :as props}]
  [:div
   {:style {:grid-row-start    (path->grid-line-name row-path)
            :grid-row-end      (str "span " (cond-> row-path
                                              :do (header-cross-span row-paths)
                                              (not show?) dec))
            :grid-column-start (count row-path)
            :grid-column-end   (str "span " (cond-> row-path
                                              :do         (header-main-span row-paths)
                                              (not show?) dec))
            :position          "relative"}}
   [:div (theme/apply {}
           {:state {:edge edge} :part ::row-header-wrapper}
           theme)
    [u/part row-header props :default row-header-part]]
   (when (and resize-rows? show?)
     [resize-button (merge props {:dimension :row
                                  :path      row-path})])])

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

(defn header-spacer-part [_] "")

(defn header-spacer-wrapper-part [{:keys [theme x y header-spacer edge]}]
  (let [props (-> {:style
                   {:grid-column (inc x)
                    :grid-row    (inc y)}}
                  (theme/apply {:state {:edge edge} :part ::header-spacer} theme))]
    [:div props
     [u/part header-spacer props :default header-spacer-part]]))

(defn scroll-container [{:keys [scroll-top scroll-left width height style]} child]
  [:div {:style (merge {:max-height height
                        :max-width  width
                        :overflow   "hidden"}
                       style)}
   [:div {:style {:transform (str "translateX(" (- (deref-or-value scroll-left)) "px) "
                                  "translateY(" (- (deref-or-value scroll-top)) "px)")}}
    child]])

(defn nested-grid [& {:keys [column-width row-height theme parts]
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
        resize-handler      (r/atom #())
        theme               {:user [theme (theme/parts parts)]}]
    (fn [& {:keys [column-tree row-tree
                   cell cell-value column-header row-header header-spacer
                   cell-wrapper column-header-wrapper row-header-wrapper header-spacer-wrapper
                   show-branch-paths?
                   max-height max-width
                   column-width column-header-height row-header-width row-height
                   show-export-button? on-export
                   on-export-cell on-export-column-header on-export-row-header
                   show-zebra-stripes?
                   show-selection-box? resize-columns? resize-rows?]
            :or   {column-header-height    25
                   column-width            55
                   row-header-width        80
                   row-height              25
                   show-export-button?     true
                   show-branch-paths?      false
                   show-selection-box?     false
                   show-zebra-stripes?     true
                   on-export-column-header header-label
                   on-export-row-header    header-label
                   resize-columns?         true
                   resize-rows?            false}}]
      (let [themed                (fn [part props] (theme/apply props {:part part} theme))
            column-paths          (spec->headers* column-tree)
            column-leaf-paths     (leaf-paths column-paths)
            leaf-column?          (set column-leaf-paths)
            row-paths             (spec->headers* row-tree)
            leaf-row?             (set (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths))
            leaf?                 (fn [path dimension]
                                    (case dimension
                                      :column (leaf-column? path)
                                      :row    (leaf-row? path)))
            show?                 (fn [path dimension]
                                    (let [show-prop (header-prop path :show? dimension)
                                          result    (and (not (false? show-prop))
                                                         (or (true? show-prop)
                                                             show-branch-paths?
                                                             (leaf? path dimension)))]
                                      result))
            showing-column-paths  (filter #(show? % :column) column-paths)
            showing-row-paths     (filter #(show? % :row) row-paths)
            showing-column-widths (map #(column-header-prop % :width column-width)
                                       showing-column-paths)
            showing-row-heights   (map #(column-header-prop % :height row-height)
                                       showing-row-paths)
            max-column-heights    (max-props :height :column column-header-height column-paths)
            max-row-widths        (max-props :width :row row-header-width row-paths)
            all-sections          (->> (vals (group-by first column-paths))
                                       (mapcat #(vals (group-by level %))))
            section-left?         (set (map first all-sections))
            section-right?        (set (map last all-sections))
            cell-sections         (->> (vals (group-by first showing-column-paths))
                                       (mapcat #(vals (group-by level %))))
            cell-section-left?    (set (map first cell-sections))
            cell-section-right?   (set (map last cell-sections))
            column-depth          (count max-column-heights)
            row-depth             (count max-row-widths)
            on-export-cell        (or on-export-cell (comp pr-str cell))
            default-on-export     (fn on-export [{:keys [rows]}]
                                    (->> rows (map u/tsv-line) str/join u/clipboard-write!))
            on-export             (or on-export default-on-export)
            cell-grid-columns     (->> column-paths
                                       (mapcat (fn [path]
                                                 (let [width (header-prop path :width :column column-width)]
                                                   (if (show? path :column)
                                                     [path width]
                                                     [path])))))
            cell-grid-rows        (->> row-paths
                                       (mapcat (fn [path]
                                                 (let [height (header-prop path :height :row row-height)]
                                                   (if (show? path :row)
                                                     [path height]
                                                     [path])))))
            spacer?               number?
            export-column-headers #(let [{:keys [grid-column-start
                                                 grid-column-end]}
                                         @selection-grid-spec
                                         selection? (and grid-column-start grid-column-end)
                                         crop       (fn [row]
                                                      (let [raw-row     (subvec row
                                                                                (dec grid-column-start)
                                                                                (dec grid-column-end))
                                                            last-header (some identity
                                                                              (reverse
                                                                               (take grid-column-start row)))]
                                                        (into [last-header] (rest raw-row))))
                                         y-size     column-depth
                                         x-size     (count (filter spacer? cell-grid-columns))
                                         result     (vec (repeat y-size (vec (repeat x-size nil))))
                                         ->y        (comp dec count)
                                         ->x        (reduce
                                                     (fn [m item]
                                                       (if (spacer? item)
                                                         (update m ::count inc)
                                                         (assoc m item (or (::count m) 0))))
                                                     {}
                                                     cell-grid-columns)
                                         insert     (fn [result path]
                                                      (assoc-in result
                                                                [(->y path) (->x path)]
                                                                (on-export-column-header {:path path})))]
                                     (cond->> column-paths
                                       :do        (reduce insert result)
                                       selection? (mapv crop)))
            export-row-headers    #(let [{:keys [grid-row-start
                                                 grid-row-end]}
                                         @selection-grid-spec
                                         selection? (and grid-row-start grid-row-end)
                                         crop       (fn [row]
                                                      (let [raw-row     (subvec row
                                                                                (dec grid-row-start)
                                                                                (dec grid-row-end))
                                                            last-header (some identity
                                                                              (reverse
                                                                               (take grid-row-start row)))]
                                                        (into [last-header] (rest raw-row))))
                                         transpose  (partial apply mapv vector)
                                         y-size     (count (filter spacer? cell-grid-rows))
                                         x-size     row-depth
                                         result     (vec (repeat y-size (vec (repeat x-size nil))))
                                         ->y        (reduce
                                                     (fn [m item]
                                                       (if (spacer? item)
                                                         (update m ::count inc)
                                                         (assoc m item (or (::count m) 0))))
                                                     {}
                                                     cell-grid-rows)
                                         ->x        (comp dec count)
                                         insert     (fn [result path]
                                                      (assoc-in result
                                                                [(->y path) (->x path)]
                                                                (on-export-row-header {:path path})))
                                         all        (reduce insert result row-paths)]
                                     (if-not selection?
                                       all
                                       (transpose (mapv crop (transpose all)))))
            export-cells          #(let [{:keys [grid-row-start grid-row-end grid-column-start grid-column-end]
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
            export-spacers        #(vec (repeat column-depth (vec (repeat row-depth nil))))
            control-panel         [:div {:style {:position         :relative
                                                 :margin-right     10
                                                 :background-color "white"
                                                 :width            (or max-width "1fr")}}
                                   [:div {:style {:position :absolute
                                                  :right    0}}
                                    (when show-export-button?
                                      [buttons/md-icon-button
                                       :md-icon-name    "zmdi zmdi-copy"
                                       :style {:height         "18px"
                                               :font-size      "18px"
                                               :line-height    "18px"
                                               :padding-bottom 0}
                                       :attr {:title "Copy to Clipboard"}
                                       :on-click        #(let [column-headers (export-column-headers)
                                                               row-headers    (export-row-headers)
                                                               spacers        (export-spacers)
                                                               cells          (export-cells)
                                                               header-rows    (mapv into spacers column-headers)
                                                               main-rows      (mapv into row-headers cells)
                                                               rows           (concat header-rows main-rows)]
                                                           (on-export
                                                            {:column-headers column-headers
                                                             :row-headers    row-headers
                                                             :spacers        spacers
                                                             :cells          cells
                                                             :header-rows    header-rows
                                                             :main-rows      main-rows
                                                             :rows           rows
                                                             :default        default-on-export}))])]]

            cell-grid-container    [:div
                                    (themed ::cell-grid-container
                                      {:on-scroll #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                                       (reset! scroll-left (.-scrollLeft (.-target %))))
                                       :style     {:max-height            max-height
                                                   :max-width             max-width
                                                   :display               "grid"
                                                   :grid-template-columns (grid-template cell-grid-columns)
                                                   :grid-template-rows    (grid-template cell-grid-rows)}})]
            column-header-cells    (doall
                                    (for [path column-paths
                                          :let [props {:column-path     path
                                                       :path            path
                                                       :column-header   column-header
                                                       :column-paths    column-paths
                                                       :show?           (show? path :column)
                                                       :on-resize       resize-column!
                                                       :mouse-down-x    mouse-down-x
                                                       :last-mouse-x    last-mouse-x
                                                       :mouse-x         mouse-x
                                                       :resize-handler  resize-handler
                                                       :resize-columns? resize-columns?
                                                       :drag            drag
                                                       :selection?      selection?
                                                       :edge            (cond-> #{}
                                                                          (start-branch? path column-paths) (conj :left)
                                                                          (end-branch? path column-paths)   (conj :right)
                                                                          (root-level? path column-paths)   (conj :top)
                                                                          (leaf-level? path column-paths)   (conj :bottom)
                                                                          (section-left? path)              (conj :column-section-left)
                                                                          (section-right? path)             (conj :column-section-right))}]]
                                      ^{:key [::column (or path (gensym))]}
                                      [u/part column-header-wrapper props :default column-header-wrapper-part]))
            row-header-cells       (doall
                                    (for [path row-paths
                                          :let [props {:row-path       path
                                                       :path           path
                                                       :row-header     row-header
                                                       :row-paths      row-paths
                                                       :show?          (show? path :row)
                                                       :on-resize      resize-row!
                                                       :mouse-down-y   mouse-down-y
                                                       :last-mouse-y   last-mouse-y
                                                       :mouse-y        mouse-y
                                                       :resize-handler resize-handler
                                                       :resize-rows?   resize-rows?
                                                       :drag           drag
                                                       :selection?     selection?
                                                       :edge           (cond-> #{}
                                                                         (start-branch? path row-paths) (conj :top)
                                                                         (end-branch? path row-paths)   (conj :bottom)
                                                                         (root-level? path row-paths)   (conj :left)
                                                                         (leaf-level? path row-paths)   (conj :right))}]]
                                      ^{:key [::row (or path (gensym))]}
                                      [u/part row-header-wrapper props :default row-header-wrapper-part]))
            header-spacer-cells    (for [y    (range column-depth)
                                         x    (range row-depth)
                                         :let [props {:theme         theme
                                                      :x             x
                                                      :y             y
                                                      :header-spacer header-spacer
                                                      :edge          (cond-> #{}
                                                                       (zero? y)                (conj :top)
                                                                       (zero? x)                (conj :left)
                                                                       (= y (dec column-depth)) (conj :bottom)
                                                                       (= x (dec row-depth))    (conj :right))}]]
                                     ^{:key [::header-spacer x y]}
                                     [u/part header-spacer-wrapper props :default header-spacer-wrapper-part])
            cells                  (doall
                                    (for [row-path showing-row-paths]
                                      (doall
                                       (for [column-path showing-column-paths
                                             :let        [props (merge {:column-path column-path
                                                                        :row-path    row-path
                                                                        :cell        cell
                                                                        :theme       theme
                                                                        :edge        (cond-> #{}
                                                                                       (= column-path (first showing-column-paths)) (conj :left)
                                                                                       (= column-path (last showing-column-paths))  (conj :right)
                                                                                       (= row-path (first showing-row-paths))       (conj :top)
                                                                                       (= row-path (last showing-row-paths))        (conj :bottom)
                                                                                       (cell-section-left? column-path)             (conj :column-section-left)
                                                                                       (cell-section-right? column-path)            (conj :column-section-right))}
                                                                       (when cell-value
                                                                         {:cell-value cell-value}))]]
                                         ^{:key [::cell-wrapper (or [column-path row-path] (gensym))]}
                                         [u/part cell-wrapper props :default cell-wrapper-part]))))
            zebra-stripes          (for [i (filter even? (range 1 (inc (count row-paths))))]
                                     ^{:key [::zebra-stripe i]}
                                     [:div
                                      (themed ::zebra-stripe
                                        {:style
                                         {:grid-column-start 1
                                          :grid-column-end   "end"
                                          :grid-row          i
                                          :background-color  "#999"
                                          :opacity           0.05
                                          :z-index           2
                                          :pointer-events    "none"}})])
            box-selector           [selection-part
                                    {:drag                drag
                                     :grid-columns        cell-grid-columns
                                     :grid-rows           cell-grid-rows
                                     :selection?          selection?
                                     :mouse-x             mouse-x
                                     :mouse-y             mouse-y
                                     :mouse-down-x        mouse-down-x
                                     :mouse-down-y        mouse-down-y
                                     :selection-grid-spec selection-grid-spec}]
            ;; FIXME This changes on different browsers - do we need to get it dynamically?
            ;; FIXME We should use :scrollbar-gutter (chrome>=94)
            native-scrollbar-width 10
            native-width           (apply +
                                          native-scrollbar-width
                                          showing-column-widths)
            native-height          (apply +
                                          native-scrollbar-width
                                          showing-row-heights)]
        [:div
         [:div {:on-mouse-enter #(reset! hover? true)
                :on-mouse-leave #(reset! hover? false)
                :style
                {:max-width             max-width
                 :overflow              :hidden
                 :display               "grid"
                 :grid-template-columns (grid-template [(px (apply + max-row-widths))
                                                        (if-not max-width
                                                          (str "minmax(0, " (+ 2 native-width) "px)")
                                                          (px (cond-> native-width
                                                                max-width
                                                                (min
                                                                 (parse-long
                                                                  (str/replace max-width
                                                                               "px"
                                                                               ""))))))])
                 :grid-template-rows    (grid-template (into (if show-export-button? ["25px"] [])
                                                             [showing-column-widths
                                                              (px (apply + max-column-heights))
                                                              (px (+ native-height 4))]))}}
          (when show-export-button? [:div])
          (when show-export-button? control-panel)
          [:div {:style {:display               "grid"
                         :box-sizing            "border-box"
                         #_#_:border-left       "medium solid #ccc"
                         #_#_:border-top        "medium solid #ccc"
                         :grid-template-columns (grid-template max-row-widths)
                         :grid-template-rows    (grid-template max-column-heights)}}
           header-spacer-cells]
          [scroll-container {:scroll-left scroll-left
                             :width       max-width
                             :style       {#_#_:border-top "medium solid #ccc"}}
           [:div {:style {:display               "grid"
                          :width                 :fit-content
                          :grid-template-columns (grid-template cell-grid-columns)
                          :grid-template-rows    (grid-template max-column-heights)}}
            column-header-cells]]
          [scroll-container {:scroll-top scroll-top
                             :height     max-height
                             :style      {#_#_:border-left "medium solid #ccc"}}
           [:div {:style {:display               "grid"
                          :grid-template-columns (grid-template max-row-widths)
                          :grid-template-rows    (grid-template cell-grid-rows)}}
            row-header-cells]]
          (-> cell-grid-container
              (into cells)
              (into (if (and show-zebra-stripes? (> (count showing-row-paths) 3))
                      zebra-stripes
                      []))
              (conj (when show-selection-box? box-selector)))]
         (when (= ::selection @drag)
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
                            :on-resize    resize-handler}])]))))
