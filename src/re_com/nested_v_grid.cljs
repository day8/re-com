(ns re-com.nested-v-grid
  (:require
   [clojure.string :as str]
   [re-com.config :as config :refer [include-args-desc?]]
   [re-com.validate    :refer [vector-atom? ifn-or-nil? map-atom? parts? part? css-class?]]
   [re-com.util :as u]
   [re-com.nested-v-grid.util :as ngu]
   [re-com.nested-v-grid.parts :as ngp]
   [reagent.core :as r]
   [re-com.part :as part]
   [re-com.theme :as theme]
   [re-com.nested-v-grid.theme]))

(def cell-args-desc
  [{:name :row-path}
   {:name :column-path}
   {:name :value}
   {:name :children}])

(def part-structure
  [::wrapper
   [::corner-header-grid
    [::corner-header {:multiple? true}
     [::corner-header-label]]]
   [::row-header-grid
    [::row-header {:multiple? true}
     [::row-header-label {:impl ngp/row-header-label}]]]
   [::column-header-grid
    [::column-header {:multiple? true}
     [::column-header-label {:impl ngp/column-header-label}]]]
   [::cell-grid
    [::cell {:top-level-arg? true
             :multiple?      true
             :args-desc      cell-args-desc}
     [::cell-label]]]])

(def nested-v-grid-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def nested-v-grid-parts
  (when include-args-desc?
    (-> (map :name nested-v-grid-parts-desc) set)))

(def nested-v-grid-args-desc
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

(defn nested-v-grid [{:keys [row-tree column-tree
                             row-tree-depth column-tree-depth
                             row-header-widths column-header-heights
                             row-height column-width
                             row-header-width column-header-height
                             row-header-label column-header-label
                             show-row-branches? show-column-branches?
                             hide-root? cell-value
                             on-init-export-fn on-export-cell
                             on-export
                             on-export-row-header on-export-column-header
                             on-export-corner-header
                             theme pre-theme
                             virtualize?]
                      :or   {row-header-width 40 column-header-height 40
                             row-height       20 column-width         40
                             virtualize?      true
                             hide-root?       true
                             on-export        (fn on-export [{:keys [rows]}]
                                                (->> rows (map u/tsv-line) str/join u/clipboard-write!))}}]
  (let [[scroll-left scroll-top content-height content-width
         !wrapper-ref scroll-listener resize-observer overlay hide-resizers?]
        (repeatedly #(r/atom nil))
        wrapper-ref!                     (partial reset! !wrapper-ref)
        on-scroll!                       #(do (reset! scroll-left (.-scrollLeft (.-target %)))
                                              (reset! scroll-top (.-scrollTop (.-target %)))
                                              (when-let [timeout @hide-resizers?] (js/clearTimeout timeout))
                                              (reset! hide-resizers? (js/setTimeout (fn [] (reset! hide-resizers? nil)) 300)))
        on-resize!                       #(do (reset! content-height (.-height (.-contentRect (aget % 0))))
                                              (reset! content-width (.-width (.-contentRect (aget % 0)))))
        prev-row-tree                    (r/atom (u/deref-or-value row-tree))
        prev-column-tree                 (r/atom (u/deref-or-value column-tree))
        prev-row-header-widths           (r/atom (u/deref-or-value row-header-widths))
        prev-column-header-heights       (r/atom (u/deref-or-value column-header-heights))
        internal-row-tree                (r/atom (u/deref-or-value row-tree))
        internal-column-tree             (r/atom (u/deref-or-value column-tree))
        internal-row-header-widths       (r/atom (or (u/deref-or-value row-header-widths)
                                                     (vec (repeat (u/deref-or-value row-tree-depth) row-header-width))))
        internal-column-header-heights   (r/atom (or (u/deref-or-value column-header-heights)
                                                     (vec (repeat (u/deref-or-value column-tree-depth) column-header-height))))
        internal-cell-value              (r/atom (u/deref-or-value cell-value))
        internal-on-export               (r/atom (u/deref-or-value on-export))
        internal-on-export-cell          (r/atom (u/deref-or-value on-export-cell))
        internal-on-export-column-header (r/atom (u/deref-or-value on-export-column-header))
        internal-on-export-row-header    (r/atom (u/deref-or-value on-export-row-header))
        internal-on-export-corner-header (r/atom (u/deref-or-value on-export-corner-header))
        internal-row-header-label        (r/atom (u/deref-or-value row-header-label))
        internal-column-header-label     (r/atom (u/deref-or-value column-header-label))
        row-size-cache                   (volatile! {})
        column-size-cache                (volatile! {})
        column-depth                     (r/reaction (or (u/deref-or-value column-tree-depth)
                                                         (count (u/deref-or-value internal-column-header-heights))))
        row-depth                        (r/reaction (or (u/deref-or-value row-tree-depth)
                                                         (count (u/deref-or-value internal-row-header-widths))))
        row-traversal                    (r/reaction
                                          (ngu/window (cond-> {:header-tree        @internal-row-tree
                                                               :size-cache         row-size-cache
                                                               :show-branch-cells? show-row-branches?
                                                               :default-size       (u/deref-or-value row-height)
                                                               :hide-root?         hide-root?}
                                                        virtualize? (merge {:window-start (- (or @scroll-top 0) 20)
                                                                            :window-end   (+ @scroll-top @content-height)}))))
        column-traversal                 (r/reaction
                                          (ngu/window (cond-> {:header-tree        @internal-column-tree
                                                               :size-cache         column-size-cache
                                                               :show-branch-cells? show-column-branches?
                                                               :default-size       (u/deref-or-value column-width)
                                                               :hide-root?         hide-root?}
                                                        virtualize? (merge {:window-start (- (or @scroll-left 0) 20)
                                                                            :window-end   (+ @scroll-left @content-width 50)}))))
        complete-row-traversal           (r/reaction
                                          (ngu/window {:header-tree        @internal-row-tree
                                                       :size-cache         row-size-cache
                                                       :dimension          :row
                                                       :show-branch-cells? show-row-branches?
                                                       :default-size       (u/deref-or-value row-height)
                                                       :hide-root?         hide-root?
                                                       :skip-tail?         false}))
        complete-column-traversal        (r/reaction
                                          (ngu/window {:header-tree        @internal-column-tree
                                                       :size-cache         column-size-cache
                                                       :dimension          :column
                                                       :show-branch-cells? show-column-branches?
                                                       :default-size       (u/deref-or-value column-width)
                                                       :hide-root?         hide-root?
                                                       :skip-tail?         false}))
        corner-header-edges              (fn [{:keys [row-index column-index]
                                               rd    :row-depth cd :column-depth
                                               :or   {rd @row-depth cd @column-depth}}]
                                           (cond-> #{}
                                             (= row-index (if hide-root? 1 0))    (conj :top)
                                             (= row-index (dec cd))               (conj :bottom)
                                             (= column-index (if hide-root? 1 0)) (conj :left)
                                             (= column-index (dec rd))            (conj :right)))
        export-fn                        (fn export-fn []
                                           (let [{row-paths :header-paths}    @complete-row-traversal
                                                 {column-paths :header-paths} @complete-column-traversal
                                                 cell-value                   @internal-cell-value
                                                 on-export-cell               @internal-on-export-cell
                                                 on-export-column-header      @internal-on-export-column-header
                                                 on-export-row-header         @internal-on-export-row-header
                                                 on-export-corner-header      @internal-on-export-corner-header
                                                 row-header-label             @internal-row-header-label
                                                 column-header-label          @internal-column-header-label
                                                 row-headers                  (for [showing-row-path (cond-> row-paths hide-root? rest)
                                                                                    :let             [{:keys [leaf? show?]} (meta showing-row-path)]
                                                                                    :when            (or leaf? show?)
                                                                                    :let             [showing-row-path (cond-> showing-row-path hide-root? (subvec 1))
                                                                                                      this-depth (count showing-row-path)]]
                                                                                (for [i    (cond-> (range @row-depth) hide-root? rest)
                                                                                      :let [row-path (subvec showing-row-path 0 (min i this-depth))
                                                                                            {:keys [branch-end?]} (meta row-path)
                                                                                            props {:row-path    row-path
                                                                                                   :path        row-path
                                                                                                   :branch-end? branch-end?}
                                                                                            export-row-header (or on-export-row-header row-header-label #())]]
                                                                                  (export-row-header props)))
                                                 column-headers               (for [i (cond-> (range @column-depth) hide-root? rest)]
                                                                                (for [showing-column-path (cond-> column-paths hide-root? rest)
                                                                                      :let                [{:keys [leaf? show?]} (meta showing-column-path)]
                                                                                      :when               (or leaf? show?)
                                                                                      :let                [showing-column-path (cond-> showing-column-path hide-root? (subvec 1))
                                                                                                           this-depth (count showing-column-path)
                                                                                                           column-path (subvec showing-column-path 0 (min i this-depth))
                                                                                                           {:keys [branch-end?]} (meta column-path)
                                                                                                           props {:column-path column-path
                                                                                                                  :path        column-path
                                                                                                                  :branch-end? branch-end?}
                                                                                                           export-column-header (or on-export-column-header column-header-label #())]]
                                                                                  (export-column-header props)))
                                                 corner-headers               (for [row-index (cond-> (range @column-depth) hide-root? rest)]
                                                                                (for [column-index (cond-> (range @row-depth) hide-root? rest)
                                                                                      :let         [props {:row-index    (cond-> row-index hide-root? dec)
                                                                                                           :column-index (cond-> column-index hide-root? dec)
                                                                                                           :row-depth    (cond-> @row-depth hide-root? dec)
                                                                                                           :column-depth (cond-> @column-depth hide-root? dec)}
                                                                                                    props (merge props {:edge (corner-header-edges props)})
                                                                                                    export-corner-header (or on-export-corner-header #())]]
                                                                                  (export-corner-header props)))
                                                 cells                        (for [row-path row-paths
                                                                                    :when    ((some-fn :leaf? :show?) (meta row-path))
                                                                                    :let     [row-path (cond-> row-path hide-root? (subvec 1))]]
                                                                                (for [column-path column-paths
                                                                                      :when       ((some-fn :leaf? :show?) (meta column-path))
                                                                                      :let        [column-path (cond-> column-path hide-root? (subvec 1))
                                                                                                   props {:row-path    row-path
                                                                                                          :column-path column-path}
                                                                                                   props (cond-> props
                                                                                                           cell-value (merge {:cell-value cell-value
                                                                                                                              :value      (cell-value props)}))
                                                                                                   export-cell (or on-export-cell cell-value #())]]
                                                                                  (export-cell props)))]
                                             (on-export {:corner-headers corner-headers
                                                         :row-headers    row-headers
                                                         :column-headers column-headers
                                                         :cells          cells
                                                         :rows           (concat (map concat corner-headers column-headers)
                                                                                 (map concat row-headers cells))})))
        processed-column-header-heights  (r/reaction (cond->      (u/deref-or-value internal-column-header-heights)
                                                       hide-root? (#(into [0] (rest %)))
                                                       :do        vec))
        processed-row-header-widths      (r/reaction (cond-> (or
                                                              (u/deref-or-value internal-row-header-widths)
                                                              (repeat @row-depth row-header-width))
                                                       hide-root? (#(into [0] (rest %)))
                                                       :do        vec))
        column-header-height-total       (r/reaction (apply + @processed-column-header-heights))
        column-width-total               (r/reaction (:sum-size @column-traversal))
        column-paths                     (r/reaction (:header-paths @column-traversal))
        column-keypaths                  (r/reaction (:keypaths @column-traversal))
        column-sizes                     (r/reaction (:sizes @column-traversal))
        column-template                  (r/reaction (ngu/grid-template @column-traversal))
        column-cross-template            (r/reaction (ngu/grid-cross-template @processed-column-header-heights))
        row-header-width-total           (r/reaction (apply + @processed-row-header-widths))
        row-height-total                 (r/reaction (:sum-size @row-traversal))
        row-paths                        (r/reaction (:header-paths @row-traversal))
        row-keypaths                     (r/reaction (:keypaths @row-traversal))
        row-sizes                        (r/reaction (:sizes @row-traversal))
        row-template                     (r/reaction (ngu/grid-template @row-traversal))
        row-cross-template               (r/reaction (ngu/grid-cross-template @processed-row-header-widths))
        theme                            (theme/comp pre-theme theme)]
    (r/create-class
     {:component-did-mount
      #(do
         (when-let [init on-init-export-fn] (init export-fn))
         (reset! scroll-listener (.addEventListener @!wrapper-ref "scroll" on-scroll!))
         (reset! resize-observer (.observe (js/ResizeObserver. on-resize!) @!wrapper-ref)))
      :component-did-update
      (fn [this]
        (let [[_ & {:keys [row-tree column-tree cell-value
                           on-export on-export-cell on-export-row-header on-export-column-header on-export-corner-header
                           row-header-label column-header-label]}] (r/argv this)]
          (doseq [[external-prop prev-external-prop internal-prop] [[row-tree prev-row-tree internal-row-tree]
                                                                    [column-tree prev-column-tree internal-column-tree]
                                                                    [row-header-widths prev-row-header-widths internal-row-header-widths]
                                                                    [column-header-heights prev-column-header-heights internal-column-header-heights]]
                  :let                                             [external-value (u/deref-or-value external-prop)
                                                                    prev-external-value (u/deref-or-value prev-external-prop)]]
            (when (not= prev-external-value external-value)
              (reset! prev-external-prop external-value)
              (reset! internal-prop external-value)))
          (doseq [[external-prop internal-prop] {on-export               internal-on-export
                                                 cell-value              internal-cell-value
                                                 on-export-cell          internal-on-export-cell
                                                 on-export-row-header    internal-on-export-row-header
                                                 on-export-column-header internal-on-export-column-header
                                                 on-export-corner-header internal-on-export-corner-header
                                                 row-header-label        internal-row-header-label
                                                 column-header-label     internal-column-header-label}
                  :let                          [external-value (u/deref-or-value external-prop)]]
            (reset! internal-prop external-value))))
      :reagent-render
      (fn [{:keys
            [theme-cells? on-resize hide-root? cell-value style class resize-row-height?
             resize-column-header-height?]
            :as                         props
            :or
            {hide-root?                   true
             resize-row-height?           true
             resize-column-header-height? true
             on-resize                    (fn [{:keys [header-dimension size-dimension keypath size]}]
                                            (case [header-dimension size-dimension]
                                              [:column :height] (swap! internal-column-header-heights assoc-in keypath size)
                                              [:row :width]     (swap! internal-row-header-widths assoc-in keypath size)
                                              [:row :height]    (swap! internal-row-tree update-in keypath assoc :size size)
                                              [:column :width]  (swap! internal-column-tree update-in keypath assoc :size size)))}}]
        (let [ensure-reactivity u/deref-or-value
              external-keys     [:row-tree :row-header-widths :row-height
                                 :column-tree :column-header-heights :column-width
                                 :on-export :on-export-cell :on-export-header :on-export-corner-header
                                 :on-export-row-header :on-export-column-header]
              external-props    (map props external-keys)]
          (doseq [prop external-props]
            (ensure-reactivity prop)))
        (let [part
              (partial part/part part-structure props)

              resize!
              (fn [{:keys [keypath size-dimension header-dimension] :as props}]
                (when-let [tree (case [header-dimension size-dimension]
                                  [:row :height]   @internal-row-tree
                                  [:column :width] @internal-column-tree
                                  nil)]
                  (vswap! (case header-dimension :row row-size-cache :column column-size-cache)
                          ngu/evict! tree keypath))
                (on-resize props))

              row-width-resizers
              (for [i (range (if hide-root? 1 0) @row-depth)]
                ^{:key [::row-width-resizer i]}
                [ngp/resizer {:on-resize        resize!
                              :overlay          overlay
                              :header-dimension :row
                              :size-dimension   :width
                              :dimension        :row-header-width
                              :keypath          [i]
                              :index            i
                              :size             (get @internal-row-header-widths i row-header-width)}])

              column-height-resizers
              (for [i (range (if hide-root? 1 0) @column-depth)]
                ^{:key [::column-height-resizer i]}
                [ngp/resizer {:path             (get @column-paths i)
                              :on-resize        resize!
                              :overlay          overlay
                              :header-dimension :column
                              :size-dimension   :height
                              :dimension        :column-header-height
                              :keypath          [i]
                              :index            i
                              :size             (get @internal-column-header-heights i column-header-height)}])

              row-height-resizers
              (fn [& {:keys [offset]}]
                (for [i     (range (count @row-paths))
                      :let  [row-path (get @row-paths i)]
                      :when (and ((some-fn :leaf? :show?) (meta row-path))
                                 (map? (peek row-path)))]
                  ^{:key [::row-height-resizer i]}
                  [ngp/resizer {:path             row-path
                                :offset           offset
                                :on-resize        resize!
                                :overlay          overlay
                                :keypath          (get @row-keypaths i)
                                :size             (get @row-sizes i)
                                :header-dimension :row
                                :size-dimension   :height
                                :dimension        :row-height}]))

              column-width-resizers
              (fn [& {:keys [offset style]}]
                (for [i     (range (count @column-paths))
                      :let  [column-path (get @column-paths i)]
                      :when (and ((some-fn :leaf? :show?) (meta column-path))
                                 (map? (peek column-path)))]
                  ^{:key [::column-width-resizer i]}
                  [ngp/resizer {:path             column-path
                                :offset           offset
                                :style            style
                                :on-resize        resize!
                                :overlay          overlay
                                :keypath          (get @column-keypaths i)
                                :size             (get @column-sizes i)
                                :header-dimension :column
                                :size-dimension   :width
                                :dimension        :column-width}]))

              row-headers
              (for [i    (range (count @row-paths))
                    :let [row-path              (get @row-paths i)
                          path-ct               (count row-path)
                          end-path              (some #(when (= (count %) path-ct) %) ;;TODO make this more efficient.
                                                      (drop (inc i) @row-paths))
                          {:keys [branch-end?]} (meta row-path)
                          row-path-prop         (cond-> row-path hide-root? (subvec 1))
                          cross-size            (get @internal-row-header-widths (dec path-ct) row-header-width)
                          size                  (get @row-sizes i)]
                    :let [props {:part        ::row-header
                                 :row-path    row-path-prop
                                 :path        row-path-prop
                                 :keypath     (get @row-keypaths i)
                                 :branch-end? branch-end?
                                 :style       {:grid-row-start    (ngu/path->grid-line-name row-path)
                                               :cross-size  cross-size
                                               :grid-row-end      (ngu/path->grid-line-name end-path)
                                               :grid-column-start (cond-> (count row-path) branch-end? dec)
                                               :grid-column-end   -1}}
                          props (assoc props :children [(part ::row-header-label
                                                          {:props (assoc props
                                                                         :style {:width    (- cross-size 10)
                                                                                 :position :sticky
                                                                                 :top      @column-header-height-total})
                                                           :impl  ngp/row-header-label})])]]
                (part ::row-header
                  {:part  ::row-header
                   :props props
                   :key   row-path
                   :theme (when theme-cells? theme)}))

              column-headers
              (for [i         (range (count  @column-paths))
                    :let      [column-path           (get @column-paths i)
                               path-ct               (count column-path)
                               end-path              (some #(when (= (count %) path-ct) %)
                                                           (drop (inc i) @column-paths))
                               {:keys [branch-end?]} (meta column-path)
                               column-path-prop           (cond-> column-path hide-root? (subvec 1))]
                    #_#_:when (not branch-end?)
                    :let      [props {:part        ::column-header
                                      :column-path column-path-prop
                                      :path        column-path-prop
                                      :branch-end? branch-end?
                                      :keypath     (get @column-keypaths i)
                                      :style       {:grid-column-start (ngu/path->grid-line-name column-path)
                                                    :grid-column-end   (ngu/path->grid-line-name end-path)
                                                    :grid-row-start    (cond-> (count column-path) branch-end? dec)
                                                    :grid-row-end      -1}}
                               props (assoc props :children    [(part ::column-header-label
                                                                  {:props props
                                                                   :impl  ngp/column-header-label})])]]
                (part ::column-header
                  {:part  ::column-header
                   :theme (when theme-cells? theme)
                   :props props
                   :key   column-path}))

              corner-headers
              (for [column-index (cond-> (range @row-depth) hide-root? rest)
                    row-index    (cond-> (range @column-depth) hide-root? rest)
                    :let         [props {:part         ::corner-header
                                         :row-index    row-index
                                         :column-index column-index
                                         :style        {:grid-row-start    (inc row-index)
                                                        :grid-column-start (inc column-index)}}
                                  edge (corner-header-edges props)
                                  border-light "thin solid #ccc"
                                  props (merge props {:edge edge})
                                  props (update props :style merge {}
                                                (when (edge :top) {:border-top border-light})
                                                (when (edge :right) {:border-right border-light})
                                                (when (edge :bottom) {:border-bottom border-light})
                                                (when (edge :left) {:border-left border-light}))]]
                (u/part corner-header
                (part ::corner-header
                  {:part  ::corner-header
                   :theme (when true #_theme-cells? theme)
                   :props (cond-> props
                            hide-root? (merge {:row-index    (dec row-index)
                                               :column-index (dec column-index)}))
                   :key   [::corner-header row-index column-index]}))

              cells
              (for [row-path    @row-paths
                    column-path @column-paths
                    :when       (and ((some-fn :leaf? :show?) (meta row-path))
                                     ((some-fn :leaf? :show?) (meta column-path)))
                    :let        [props {:part        ::cell
                                        :row-path    (cond-> row-path hide-root? (subvec 1))
                                        :column-path (cond-> column-path hide-root? (subvec 1))
                                        :style       {:grid-row-start    (ngu/path->grid-line-name row-path)
                                                      :grid-column-start (ngu/path->grid-line-name column-path)}}
                                 props (cond-> props
                                         cell-value (merge {:cell-value cell-value
                                                            :children   [[cell-value props]]}))]]
                (part ::cell
                  {:part  ::cell
                   :props props
                   :theme (when theme-cells? theme)
                   :key   [row-path column-path]}))]
          (part ::wrapper
            {:part        ::wrapper
             :theme       theme
             :after-props {:style style
                           :class class}
             :props
             {:style {:height                300
                      :width                 500
                      :overflow              :auto
                      :flex                  "0 0 auto"
                      :display               :grid
                      :grid-template-rows    (ngu/grid-cross-template [@column-header-height-total @row-height-total])
                      :grid-template-columns (ngu/grid-cross-template [@row-header-width-total @column-width-total])}
              :attr  {:ref wrapper-ref!}
              :children
              [(part ::cell-grid
                 {:theme theme
                  :part  ::cell-grid
                  :props {:children (cond-> cells
                                      (not @hide-resizers?)
                                      (concat
                                       (when resize-row-height?
                                         (row-height-resizers {:offset -1}))
                                       (column-width-resizers {:style  {:grid-row-end -1}
                                                               :offset -1})))
                          :style    {:grid-template-rows    @row-template
                                     :grid-template-columns @column-template}}})
               (part ::column-header-grid
                 {:theme theme
                  :part  ::column-header-grid
                  :props {:children (cond-> column-headers
                                      (not @hide-resizers?)
                                      (concat (when resize-column-header-height?
                                                column-height-resizers)
                                              (column-width-resizers {:offset -1})))
                          :style    {:grid-template-rows    @column-cross-template
                                     :grid-template-columns @column-template}}})
               (part ::row-header-grid
                 {:theme theme
                  :part  ::row-header-grid
                  :props {:children (cond-> row-headers
                                      (not @hide-resizers?)
                                      (concat row-width-resizers
                                              (when resize-row-height?
                                                (row-height-resizers {:offset -1}))))
                          :style    {:grid-template-rows    @row-template
                                     :grid-template-columns @row-cross-template}}})
               (part ::corner-header-grid
                 {:theme theme
                  :part  ::corner-header-grid
                  :props {:children (cond-> corner-headers
                                      (not @hide-resizers?)
                                      (concat row-width-resizers
                                              (when resize-column-header-height?
                                                column-height-resizers)))
                          :style    {:grid-template-rows    @column-cross-template
                                     :grid-template-columns @row-cross-template}}})
               (u/deref-or-value overlay)]}})))})))
