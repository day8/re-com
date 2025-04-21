(ns re-com.nested-v-grid
  (:require-macros
   [re-com.core     :refer [at]]
   [re-com.validate :refer [validate-args-macro]])
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
    [::corner-header {:top-level-arg? true
                      :multiple? true}
     [::corner-header-label {:top-level-arg? true}]]]
   [::row-header-grid
    [::row-header {:top-level-arg? true
                   :multiple? true}
     [::row-header-label {:top-level-arg? true
                          :impl ngp/row-header-label}]]]
   [::column-header-grid
    [::column-header {:top-level-arg? true
                      :multiple? true}
     [::column-header-label {:top-level-arg? true
                             :impl ngp/column-header-label}]]]
   [::cell-grid
    [::cell {:top-level-arg? true
             :multiple?      true
             :args-desc      cell-args-desc}
     [::cell-label {:top-level-arg? true}]]]])

(def parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def part-names
  (when include-args-desc?
    (-> (map :name parts-desc) set)))

(def args-desc
  (let [special-args
        [{:name        :row-tree
          :default     "[]"
          :type        "vector of row-specs or (nested) row-trees"
          :validate-fn sequential?}

         {:name        :column-tree
          :required    true
          :type        "vector of column-specs or (nested) column-trees"
          :validate-fn sequential?}

         {:name        :row-tree-depth
          :type        "integer"
          :default     "false"
          :validate-fn integer?
          :required    false
          :description
          [:span "Necessary to lay out the headers. Since " [:code "nested-grid"]
           " only traverses the visible part of each  header-tree, it cannot know "
           "how deep the entire tree is. When the deepest part of the tree is not visible, "
           [:code "nested-grid"] " still needs to display a large enough grid area "
           "in the headers, so there is a place for it when it enters the view."]}

         {:name        :column-tree-depth
          :type        "boolean"
          :default     "false"
          :validate-fn integer?
          :required    false
          :description
          [:span "Necessary to lay out the headers. Since " [:code "nested-grid"]
           " only traverses the visible part of each  header-tree, it cannot know "
           "how deep the entire tree is. When the deepest part of the tree is not visible, "
           [:code "nested-grid"] " still needs to display a large enough grid area "
           "in the headers, so there is a place for it when it enters the view."]}

         {:name        :row-height
          :type        "integer"
          :validate-fn integer?
          :default     20
          :description
          [:span "Controls the default main-axis size (i.e. height) of every row. "
           [:code "nested-grid"] " overrides this size when a user resizes a row, "
           "or when the corresponding row-spec contains a " [:code ":size"] " key."]}

         {:name        :column-width
          :type        "integer"
          :validate-fn integer?
          :default     40
          :description
          [:span "Controls the default main-axis size (i.e. width) of every column. "
           [:code "nested-grid"] " overrides this size when a user resizes a column, "
           "or when the corresponding column-spec contains a " [:code ":size"] " key."]}

         {:name        :column-header-height
          :type        "integer"
          :validate-fn integer?
          :default     20
          :description
          [:span "Controls the default cross-axis size (i.e. width) of every column-header."]}

         {:name        :row-header-width
          :type        "integer"
          :validate-fn integer?
          :default     40
          :description
          [:span "Controls the default cross-axis size (i.e. width) of every row-header."]}

         {:name        :row-header-widths
          :type        "vector of integers | r/atom"
          :validate-fn (comp (partial every? integer?) u/deref-or-value-peek)
          :description
          [:span "Each integer corresponds to a level of nesting in the header-tree, "
           "Controlling the cross-axis size of every header at that level. "
           "Overrides " [:code "row-header-width"] "."]}

         {:name        :column-header-heights
          :type        "vector of integers | r/atom"
          :validate-fn (comp (partial every? integer?) u/deref-or-value-peek)
          :description
          [:span "Each integer corresponds to a level of nesting in the header-tree, "
           "Controlling the cross-axis size of every header at that level."
           "Overrides " [:code "column-header-height"] "."]}

         {:name        :show-row-branches?
          :type        "boolean"
          :default     "false"
          :validate-fn boolean?
          :description
          [:span "Displays a row of cells for every "
           [:code ":row-path"] ", not just those at the leaves of the tree. "
           "If a header has children, its path is a branch-path."
           "Otherwise, its path is a leaf-path."
           "For instance, the tree " [:code "[:a [:b [:c]]]"]
           " has one leaf path " [:code "[:a :b :c]"] " and two branch paths "
           [:code "[:a] [:a :b]"] "."]}

         {:name        :show-column-branches?
          :type        "boolean"
          :default     "false"
          :validate-fn boolean?
          :description
          [:span "Displays a row of cells for every "
           [:code ":column-path"] ", not just those at the leaves of the tree. "
           "If a header has children, its path is a branch-path."
           "Otherwise, its path is a leaf-path."
           "For instance, the tree " [:code "[:a [:b [:c]]]"]
           " has one leaf path " [:code "[:a :b [:c]]"] " and two branch paths "
           [:code "[:a] [:a :b]"] "."]}

         {:name        :show-root-headers?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span
           "When " [:code "false"] ", hides the root of each header-tree, and excludes that root-header "
           "from the " [:code ":row-path"] " and " [:code ":column-path"] " props "
           "which " [:code ":nested-grid"] "passes to various parts. "
           "Each header-tree has a single header at its root "
           "(its children make up the rest of the tree)."
           "In many cases, this root-header is not interesting to the user, so offer this prop to hide it."]}

         {:name        :on-init-export-fn
          :type        "fn"
          :validate-fn ifn?
          :description
          [:span [:code "nested-grid"] " calls this function once at mount-time, passing it an "
           "export function. We recommend storing the export function in an r/atom. "
           "This export function composes your passed-in "
           [:code ":on-export"] " and " [:code ":on-export-*"] " functions, which are required "
           "for this export function to work."]}

         {:name        :on-export
          :required    false
          :type        "function"
          :validate-fn ifn?
          :description
          [:span "Called whenever the export button is clicked. "
           "Can expect to be passed several keyword arguments. "
           "Each argument is a 2-dimensional vector of strings."
           [:ul
            [:li [:strong [:code ":rows"]] ": "
             "The entire grid laid out in rows."]
            [:li [:strong [:code ":cells"]] ": "
             "Just the cells, without any headers."]
            [:li [:strong [:code ":corner-headers"]] ": "
             "Just the spacers in the top-left corner."]
            [:li [:strong [:code ":row-headers"]] ": "
             "Just the row headers"]
            [:li [:strong [:code ":column-headers"]] ": "
             "Just the column headers"]]]}

         {:name        :on-export-cell
          :required    false
          :type        "{:keys [row-path column-path]} -> string"
          :validate-fn ifn?
          :description
          [:span "Similar to " [:code ":cell"] ", but its return value must be a string. "
           "At export time, " [:code "nested-grid"] " maps "
           " this function over the cells, passing the results to " [:code ":on-export"] ". "
           "See " [:code ":on-init-export-fn"] " for how to invoke the export."]}

         {:name        :on-export-row-header
          :required    false
          :type        "{:keys [row-path column-path]} -> string"
          :validate-fn ifn?
          :description
          [:span "Similar to " [:code ":row-header-label"] ", but its return value must be a string. "
           "At export time, " [:code "nested-grid"] " maps "
           "this function over the row-headers, passing the results to " [:code ":on-export"] ". "
           "See " [:code ":on-init-export-fn"] " for how to invoke the export."]}

         {:name        :on-export-column-header
          :required    false
          :type        "{:keys [row-path column-path]} -> string"
          :validate-fn ifn?
          :description
          [:span "Similar to " [:code ":column-header-label"] ", but its return value must be a string. "
           "At export time, " [:code "nested-grid"] " maps "
           "this function over the column-headers, passing the results to " [:code ":on-export"] ". "
           "See " [:code ":on-init-export-fn"] " for how to invoke the export."]}

         {:name        :on-export-corner-header
          :required    false
          :type        "{:keys [row-path corner-path]} -> string"
          :validate-fn ifn?
          :description
          [:span "Similar to " [:code ":corner-header-label"] ", but its return value must be a string. "
           "At export time, " [:code "nested-grid"] " maps "
           "this function over the corner-headers, passing the results to " [:code ":on-export"] ". "
           "See " [:code ":on-init-export-fn"] " for how to invoke the export."]}

         {:name    :virtualize?
          :type    "boolean"
          :default "true"
          :description
          [:span [:code "nested-grid"] "'s most difficult tasks are deriving paths from header-trees "
           "and rendering cells at each path intersection. Their complexity is proportional to "
           [:code "(* (size row-tree) (size column-tree))"] ". " "When " [:code ":virtualize?"]
           " is " [:code "false"] ", " [:code "nested-grid"] " does all the work on each render. "
           "As long as nothing triggers a re-render, this is fine. Scrolling does not trigger a re-render. "
           "That means scrolling is very smooth, but resizing, changing or moving a row or column "
           "could be painfully slow. When " [:code ":virtualize?"] " is " [:code "true"] ", "
           [:code "nested-grid"] " relies on a cached traversal. It still traverses both trees at mount time, "
           "But subsequent traversals are much faster. Rendering is faster as well, only running "
           "for the cells which appear in the visible scroll area. Scrolling " [:i "does"] " trigger a re-render, "
           " mounting any cells which enter the scroll area, and unmounting those which exit. "
           "That means scrolling may not be quite as smooth, but overall performance (e.g. resizing) "
           "can be better, especially for very large trees with millions of paths"]}

         {:name        :theme-cells?
          :type        "boolean"
          :default     "false"
          :validate-fn boolean?
          :description
          [:span "Improves performance by disabling the theme system on the following-parts: "
           [:ul
            [:li [:code ":row-header"]]
            [:li [:code ":column-header"]]
            [:li [:code ":cell"]]]
           "Those parts will not be passed any props other than row-path & column-path."
           "To style these parts, we recommend using css to target the descendents of container parts, such as "
           [:code ":row-header-grid"] ". This is currently done in " [:code "re-com.css"]]}

         {:name        :resize?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span "When " [:code "true"]
           ", display draggable resize buttons across all row & column boundaries. "
           "This can be overridden by the other " [:code ":resize-*?"] " props. "
           "NOTE: For a row or column to be resizable along its main axis, its spec must be a map."]}

         {:name        :resize-row-height?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span "When " [:code "true"]
           ", display draggable resize buttons across the main-axis dimension of row headers. "
           "NOTE: For a row-header to be resizable, its row-spec must be a map."]}

         {:name        :resize-column-header-height?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span "When " [:code "true"]
           ", display draggable resize buttons across the cross-axis dimension of column headers."]}

         {:name        :resize-row-header-height?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span "When " [:code "true"]
           ", display draggable resize buttons across the main-axis dimension of rows. "
           "NOTE: For a row to be resizable, its row-spec must be a map."]}

         {:name        :resize-column-width?
          :type        "boolean"
          :default     "true"
          :validate-fn boolean?
          :description
          [:span "When " [:code "true"]
           ", display draggable resize buttons across the cross-axis dimension of columns."
           "NOTE: For a column to be resizable, its column-spec must be a map."]}

         {:name        :on-resize
          :type        "fn"
          :default     "internal fn"
          :description "TBD"}

         {:name        :style
          :description [:span "Applies to the " [:code ":wrapper"] " part."]}

         {:name        :class
          :description [:span "Applies to the " [:code ":wrapper"] " part."]}]]
    (when include-args-desc?
      (vec
       (concat
        special-args
        theme/args-desc
        (part/describe-args part-structure))))))

(defn safe-assoc
  [v idx val]
  (if (< idx (count v))
    (assoc v idx val)
    (into (vec (concat v (repeat (- idx (count v)) nil))) [val])))

(defn nested-v-grid [{:keys [row-tree column-tree
                             row-tree-depth column-tree-depth
                             row-header-widths column-header-heights
                             row-height column-width
                             row-header-width column-header-height
                             show-row-branches? show-column-branches?
                             show-root-headers?
                             on-init-export-fn on-export-cell
                             on-export
                             on-export-row-header on-export-column-header
                             on-export-corner-header
                             theme pre-theme
                             virtualize?]
                      :or   {row-header-width   40 column-header-height 20
                             row-height         20 column-width         40
                             virtualize?        true
                             show-root-headers? true
                             on-export          (fn on-export [{:keys [rows]}]
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
        internal-on-export               (r/atom (u/deref-or-value on-export))
        internal-on-export-cell          (r/atom (u/deref-or-value on-export-cell))
        internal-on-export-column-header (r/atom (u/deref-or-value on-export-column-header))
        internal-on-export-row-header    (r/atom (u/deref-or-value on-export-row-header))
        internal-on-export-corner-header (r/atom (u/deref-or-value on-export-corner-header))
        row-size-cache                   (volatile! {})
        column-size-cache                (volatile! {})
        row-traversal                    (r/reaction
                                          (ngu/window (cond-> {:header-tree        @internal-row-tree
                                                               :size-cache         row-size-cache
                                                               :show-branch-cells? show-row-branches?
                                                               :default-size       (u/deref-or-value row-height)
                                                               :hide-root?         (not show-root-headers?)}
                                                        virtualize? (merge {:window-start (- (or @scroll-top 0) 20)
                                                                            :window-end   (+ @scroll-top @content-height)}))))
        column-traversal                 (r/reaction
                                          (ngu/window (cond-> {:header-tree        @internal-column-tree
                                                               :size-cache         column-size-cache
                                                               :show-branch-cells? show-column-branches?
                                                               :default-size       (u/deref-or-value column-width)
                                                               :hide-root?         (not show-root-headers?)}
                                                        virtualize? (merge {:window-start (- (or @scroll-left 0) 20)
                                                                            :window-end   (+ @scroll-left @content-width 50)}))))
        complete-row-traversal           (r/reaction
                                          (ngu/window {:header-tree        @internal-row-tree
                                                       :size-cache         row-size-cache
                                                       :dimension          :row
                                                       :show-branch-cells? show-row-branches?
                                                       :default-size       (u/deref-or-value row-height)
                                                       :hide-root?         (not show-root-headers?)
                                                       :skip-tail?         false}))
        complete-column-traversal        (r/reaction
                                          (ngu/window {:header-tree        @internal-column-tree
                                                       :size-cache         column-size-cache
                                                       :dimension          :column
                                                       :show-branch-cells? show-column-branches?
                                                       :default-size       (u/deref-or-value column-width)
                                                       :hide-root?         (not show-root-headers?)
                                                       :skip-tail?         false}))
        column-depth                     (r/reaction (or (u/deref-or-value column-tree-depth)
                                                         (cond-> (:depth @column-traversal)
                                                           (not show-root-headers?) dec)))
        row-depth                        (r/reaction (or (u/deref-or-value row-tree-depth)
                                                         (cond-> (:depth @row-traversal)
                                                           (not show-root-headers?) dec)))
        internal-row-header-widths       (r/atom (or (u/deref-or-value row-header-widths)
                                                     (vec (repeat @row-depth (u/deref-or-value row-header-width)))))
        internal-column-header-heights   (r/atom (or (u/deref-or-value column-header-heights)
                                                     (vec (repeat @row-depth (u/deref-or-value column-header-height)))))
        safe-column-header-heights       (r/reaction
                                          (->> (concat @internal-column-header-heights
                                                       (repeat (u/deref-or-value column-header-height)))
                                               (map #(or % column-header-height))
                                               (take @column-depth)
                                               vec))
        safe-row-header-widths           (r/reaction
                                          (->> (concat @internal-row-header-widths
                                                       (repeat (u/deref-or-value row-header-width)))
                                               (take @row-depth)
                                               (map #(or % row-header-width))
                                               vec))
        column-header-height-total       (r/reaction (apply + @safe-column-header-heights))
        column-width-total               (r/reaction (:sum-size @column-traversal))
        column-paths                     (r/reaction (:header-paths @column-traversal))
        column-keypaths                  (r/reaction (:keypaths @column-traversal))
        column-sizes                     (r/reaction (:sizes @column-traversal))
        column-template                  (r/reaction (ngu/grid-template @column-traversal))
        column-cross-template            (r/reaction (ngu/grid-cross-template @safe-column-header-heights))
        row-header-width-total           (r/reaction (apply + @safe-row-header-widths))
        row-height-total                 (r/reaction (:sum-size @row-traversal))
        row-paths                        (r/reaction (:header-paths @row-traversal))
        row-keypaths                     (r/reaction (:keypaths @row-traversal))
        row-sizes                        (r/reaction (:sizes @row-traversal))
        row-template                     (r/reaction (ngu/grid-template @row-traversal))
        row-cross-template               (r/reaction (ngu/grid-cross-template @safe-row-header-widths))
        corner-header-edges              (fn [{:keys [row-index column-index]
                                               rd    :row-depth cd :column-depth
                                               :or   {rd @row-depth cd @column-depth}}]
                                           (cond-> #{}
                                             (= row-index 0)           (conj :top)
                                             (= row-index (dec cd))    (conj :bottom)
                                             (= column-index 0)        (conj :left)
                                             (= column-index (dec rd)) (conj :right)))
        export-fn                        (fn export-fn []
                                           (let [{row-paths :header-paths}    @complete-row-traversal
                                                 {column-paths :header-paths} @complete-column-traversal
                                                 on-export-cell               @internal-on-export-cell
                                                 on-export-column-header      @internal-on-export-column-header
                                                 on-export-row-header         @internal-on-export-row-header
                                                 on-export-corner-header      @internal-on-export-corner-header
                                                 row-headers                  (for [showing-row-path (cond-> row-paths (not show-root-headers?) rest)
                                                                                    :let             [{:keys [leaf? show?]} (meta showing-row-path)]
                                                                                    :when            (or leaf? show?)
                                                                                    :let             [showing-row-path (cond-> showing-row-path (not show-root-headers?) (subvec 1))
                                                                                                      this-depth (count showing-row-path)]]
                                                                                (for [i    (range @row-depth)
                                                                                      :let [row-path (subvec showing-row-path 0 (min (inc i) this-depth))
                                                                                            {:keys [branch-end?]} (meta row-path)
                                                                                            props {:row-path    row-path
                                                                                                   :path        row-path
                                                                                                   :branch-end? branch-end?}]]
                                                                                  (on-export-row-header props)))
                                                 column-headers               (for [i (range @column-depth)]
                                                                                (for [showing-column-path (cond-> column-paths (not show-root-headers?) rest)
                                                                                      :let                [{:keys [leaf? show?]} (meta showing-column-path)]
                                                                                      :when               (or leaf? show?)
                                                                                      :let                [showing-column-path (cond-> showing-column-path (not show-root-headers?) (subvec 1))
                                                                                                           this-depth (count showing-column-path)
                                                                                                           column-path (subvec showing-column-path 0 (min (inc i) this-depth))
                                                                                                           {:keys [branch-end?]} (meta column-path)
                                                                                                           props {:column-path column-path
                                                                                                                  :path        column-path
                                                                                                                  :branch-end? branch-end?}]]
                                                                                  (on-export-column-header props)))
                                                 corner-headers               (for [row-index (range @column-depth)]
                                                                                (for [column-index (range @row-depth)
                                                                                      :let         [props {:row-index    row-index
                                                                                                           :column-index column-index
                                                                                                           :row-depth    @row-depth
                                                                                                           :column-depth @column-depth}
                                                                                                    props (merge props {:edge (corner-header-edges props)})]]
                                                                                  (on-export-corner-header props)))
                                                 cells                        (for [row-path row-paths
                                                                                    :when    ((some-fn :leaf? :show?) (meta row-path))
                                                                                    :let     [row-path (cond-> row-path (not show-root-headers?) (subvec 1))]]
                                                                                (for [column-path column-paths
                                                                                      :when       ((some-fn :leaf? :show?) (meta column-path))
                                                                                      :let        [column-path (cond-> column-path (not show-root-headers?) (subvec 1))
                                                                                                   props {:row-path    row-path
                                                                                                          :column-path column-path}]]
                                                                                  (on-export-cell props)))]
                                             (on-export {:corner-headers corner-headers
                                                         :row-headers    row-headers
                                                         :column-headers column-headers
                                                         :cells          cells
                                                         :rows           (concat (map concat corner-headers column-headers)
                                                                                 (map concat row-headers cells))})))
        theme                            (theme/comp pre-theme theme)]
    (r/create-class
     {:component-did-mount
      #(do
         (when-let [init on-init-export-fn] (init export-fn))
         (when-let [wrapper-ref @!wrapper-ref]
           (reset! scroll-listener (.addEventListener wrapper-ref "scroll" on-scroll!))
           (reset! resize-observer (.observe (js/ResizeObserver. on-resize!) wrapper-ref))))
      :component-did-update
      (fn [this]
        (let [[_ & {:keys [row-tree column-tree
                           on-export on-export-cell on-export-row-header on-export-column-header on-export-corner-header]}]
              (r/argv this)]
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
                                                 on-export-cell          internal-on-export-cell
                                                 on-export-row-header    internal-on-export-row-header
                                                 on-export-column-header internal-on-export-column-header
                                                 on-export-corner-header internal-on-export-corner-header}
                  :let                          [external-value (u/deref-or-value external-prop)]]
            (reset! internal-prop external-value))))
      :reagent-render
      (fn [{:keys
            [theme-cells? show-root-headers? style class
             on-resize
             resize? resize-row-height? resize-row-header-width?
             resize-column-width? resize-column-header-height?]
            :as props
            :or
            {show-root-headers?           true
             resize-row-height?           true
             resize-row-header-width?     true
             resize-column-width?         true
             resize-column-header-height? true
             resize?                      true
             on-resize                    (fn [{:keys [header-dimension size-dimension keypath size]}]
                                            (case [header-dimension size-dimension]
                                              [:column :height] (swap! internal-column-header-heights safe-assoc (first keypath) size)
                                              [:row :width]     (swap! internal-row-header-widths safe-assoc (first keypath) size)
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
        (or
         (validate-args-macro args-desc props)
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
               (for [i (range @row-depth)]
                 ^{:key [::row-width-resizer i]}
                 [ngp/resizer {:on-resize        resize!
                               :overlay          overlay
                               :header-dimension :row
                               :size-dimension   :width
                               :dimension        :row-header-width
                               :keypath          [i]
                               :index            i
                               :size             (get @safe-row-header-widths i)}])

               column-height-resizers
               (for [i (range @column-depth)]
                 ^{:key [::column-height-resizer i]}
                 [ngp/resizer {:path             (get @column-paths i)
                               :on-resize        resize!
                               :overlay          overlay
                               :header-dimension :column
                               :size-dimension   :height
                               :dimension        :column-header-height
                               :keypath          [i]
                               :index            i
                               :size             (get @safe-column-header-heights i)}])

               row-height-resizers
               (fn [& {:keys [offset]}]
                 (for [i     (range (dec (count @row-paths)))
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
               (for [i     (range (count @row-paths))
                     :let  [row-path                    (get @row-paths i)
                            path-ct                     (count row-path)
                            end-keypath                 (->> @row-paths
                                                             (drop (inc i))
                                                             (take-while #(> (count %) path-ct))
                                                             count
                                                             (+ i 1)
                                                             (#(get @row-keypaths %)))
                            {:keys [branch-end? leaf?]} (meta row-path)
                            row-path-prop               (cond-> row-path (not show-root-headers?) (subvec 1))
                            cross-size                  (get @safe-row-header-widths
                                                             (cond-> (dec path-ct) (not show-root-headers?) dec))
                            size                        (get @row-sizes i)
                            keypath (get @row-keypaths i)]
                     :when (or show-root-headers? (pos? i))
                     :let  [props {:part        ::row-header
                                   :row-path    row-path-prop
                                   :path        row-path-prop
                                   :keypath     keypath
                                   :branch-end? branch-end?
                                   :style       {:grid-row-start    (ngu/keypath->grid-line-name keypath)
                                                 :cross-size        cross-size
                                                 :grid-row-end      (if branch-end? "span 1"
                                                                        (ngu/keypath->grid-line-name end-keypath))
                                                 :grid-column-start (cond-> (count row-path)
                                                                      branch-end?              dec
                                                                      (not show-root-headers?) dec)
                                                 :grid-column-end   -1}}
                            props (assoc props :children [(part ::row-header-label
                                                            {:props (assoc props
                                                                           :style (merge {:height (- size 5)}
                                                                                         (when-not leaf?
                                                                                           {:position :sticky
                                                                                            :top      @column-header-height-total})
                                                                                         (when-not branch-end?
                                                                                           {:width (- cross-size 10)})))
                                                             :impl  ngp/row-header-label})])]]
                 (part ::row-header
                   {:part  ::row-header
                    :props props
                    :key   [::row-header keypath branch-end?]
                    :theme (when theme-cells? theme)}))

               column-headers
               (for [i         (range (count @column-paths))
                     :let      [column-path           (get @column-paths i)
                                path-ct               (count column-path)
                                end-keypath           (->> @column-paths
                                                           (drop (inc i))
                                                           (take-while #(> (count %) path-ct))
                                                           count
                                                           (+ i 1)
                                                           (#(get @column-keypaths %)))
                                {:keys [branch-end? branch? leaf?]} (meta column-path)
                                column-path-prop      (cond-> column-path (not show-root-headers?) (subvec 1))
                                keypath (get @column-keypaths i)]
                     #_#_:when (not branch-end?)
                     :when     (or show-root-headers? (pos? i))
                     :let      [props {:part        ::column-header
                                       :column-path column-path-prop
                                       :path        column-path-prop
                                       :branch-end? branch-end?
                                       :keypath     keypath
                                       :style       {:grid-column-start (ngu/keypath->grid-line-name keypath)
                                                     :grid-column-end   (cond
                                                                          end-keypath (ngu/keypath->grid-line-name end-keypath)
                                                                          leaf?       "span 1"
                                                                          :else       "-1")
                                                     :grid-row-start    (cond-> (count column-path)
                                                                          branch-end?              dec
                                                                          (not show-root-headers?) dec)
                                                     :grid-row-end      -1}}
                                props (assoc props :children    [(part ::column-header-label
                                                                   {:props props
                                                                    :impl  ngp/column-header-label})])]]
                 (part ::column-header
                   {:part  ::column-header
                    :theme (when theme-cells? theme)
                    :props props
                    :key   [::column-header keypath branch-end?]}))

               corner-headers
               (for [column-index (range @row-depth)
                     row-index    (range @column-depth)
                     :let         [props {:part         ::corner-header
                                          :row-index    row-index
                                          :column-index column-index
                                          :style        {:grid-row-start    (inc row-index)
                                                         :grid-column-start (inc column-index)}}
                                   edge (corner-header-edges props)
                                   border-light "thin solid #ccc"
                                   props (merge props {:edge edge})
                                   props (assoc props :children
                                                [(part ::corner-header-label
                                                   {:part  ::corner-header-label
                                                    :props props})])
                                   borders (merge {}
                                                  (when (edge :top) {:border-top border-light})
                                                  (when (edge :right) {:border-right border-light})
                                                  (when (edge :bottom) {:border-bottom border-light})
                                                  (when (edge :left) {:border-left border-light}))]]
                 (part ::corner-header
                   {:part  ::corner-header
                    :theme (when true #_theme-cells? theme)
                    :props (cond-> props
                             :do                (update :style merge borders)
                             show-root-headers? (merge {:row-index    (dec row-index)
                                                        :column-index (dec column-index)}))
                    :key   [::corner-header row-index column-index]}))

               cells
               (for [ri    (range (count @row-paths))
                     ci    (range (count @column-paths))
                     :let  [row-path    (nth @row-paths ri)
                            column-path (nth @column-paths ci)
                            row-keypath    (nth @row-keypaths ri)
                            column-keypath (nth @column-keypaths ci)
                            row-meta (meta row-path)
                            column-meta (meta column-path)]
                     :when (and ((some-fn :leaf? :show?) row-meta)
                                ((some-fn :leaf? :show?) column-meta))
                     :let  [props {:row-path    (cond-> row-path
                                                  (not show-root-headers?) (subvec 1)
                                                  (:branch-end? row-meta)  pop)
                                   :column-path (cond-> column-path
                                                  (not show-root-headers?)   (subvec 1)
                                                  (:branch-end? column-meta) pop)
                                   :row-meta    row-meta
                                   :column-meta column-meta
                                   :style       {:grid-row-start    (ngu/keypath->grid-line-name row-keypath)
                                                 :grid-column-start (ngu/keypath->grid-line-name column-keypath)}}
                            props (merge props
                                         {:children [(part ::cell-label {:props props})]})]]
                 (part ::cell
                   {:part  ::cell
                    :props props
                    :theme (when theme-cells? theme)
                    :key   [row-path column-path]}))]
           (part ::wrapper
             {:part       ::wrapper
              :theme      theme
              :post-props {:style style
                           :class class}
              :props
              {:style {:grid-template-rows    (ngu/grid-cross-template [@column-header-height-total @row-height-total])
                       :grid-template-columns (ngu/grid-cross-template [@row-header-width-total @column-width-total])}
               :attr  {:ref wrapper-ref!}
               :children
               [(part ::cell-grid
                  {:theme theme
                   :part  ::cell-grid
                   :props {:children (cond-> cells
                                       (and resize? (not @hide-resizers?))
                                       (concat
                                        (when resize-row-height?
                                          (row-height-resizers {:offset -1}))
                                        (when resize-column-width?
                                          (column-width-resizers {:style  {:grid-row-end -1}
                                                                  :offset -1}))))
                           :style    {:grid-template-rows    @row-template
                                      :grid-template-columns @column-template}}})
                (part ::column-header-grid
                  {:theme theme
                   :part  ::column-header-grid
                   :props {:children (cond-> column-headers
                                       (and resize? (not @hide-resizers?))
                                       (concat
                                        (when resize-column-header-height?
                                          column-height-resizers)
                                        (when resize-column-width?
                                          (column-width-resizers {:offset -1}))))
                           :style    {:grid-template-rows    @column-cross-template
                                      :grid-template-columns @column-template}}})
                (part ::row-header-grid
                  {:theme theme
                   :part  ::row-header-grid
                   :props {:children (cond-> row-headers
                                       (and resize? (not @hide-resizers?))
                                       (concat
                                        (when resize-row-header-width?
                                          row-width-resizers)
                                        (when resize-row-height?
                                          (row-height-resizers {:offset -1}))))
                           :style    {:grid-template-rows    @row-template
                                      :grid-template-columns @row-cross-template}}})
                (part ::corner-header-grid
                  {:theme theme
                   :part  ::corner-header-grid
                   :props {:children (cond-> corner-headers
                                       (and resize? (not @hide-resizers?))
                                       (concat
                                        (when resize-row-header-width?
                                          row-width-resizers)
                                        (when resize-column-header-height?
                                          column-height-resizers)))
                           :style    {:grid-template-rows    @column-cross-template
                                      :grid-template-columns @row-cross-template}}})
                (u/deref-or-value overlay)]}}))))})))
