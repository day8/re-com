(ns re-com.nested-v-grid
  (:require
   [clojure.string :as str]
   [re-com.util :as u]
   [re-com.nested-v-grid.util :as ngu]
   [re-com.nested-v-grid.parts :as ngp]
   [reagent.core :as r]
   [re-com.theme :as theme]
   [re-com.nested-v-grid.theme]))

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
                             on-export-corner-header]
                      :or   {row-header-width 40 column-header-height 40
                             row-height       20 column-width         40
                             hide-root?       true
                             on-export        (fn on-export [{:keys [rows]}]
                                                (->> rows (map u/tsv-line) str/join u/clipboard-write!))}}]
  (let [[wx wy wh ww !wrapper-ref scroll-listener resize-observer overlay]
        (repeatedly #(r/atom nil))
        wrapper-ref!                     (partial reset! !wrapper-ref)
        on-scroll!                       #(do (reset! wx (.-scrollLeft (.-target %)))
                                              (reset! wy (.-scrollTop (.-target %))))
        on-resize!                       #(do (reset! wh (.-height (.-contentRect (aget % 0))))
                                              (reset! ww (.-width (.-contentRect (aget % 0)))))
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
                                          (ngu/window {:header-tree        @internal-row-tree
                                                       :window-start       (- (or @wy 0) 20)
                                                       :window-end         (+ @wy @wh)
                                                       :size-cache         row-size-cache
                                                       :show-branch-cells? show-row-branches?
                                                       :default-size       (u/deref-or-value row-height)
                                                       :hide-root?         hide-root?}))
        column-traversal                 (r/reaction
                                          (ngu/window {:header-tree        @internal-column-tree
                                                       :window-start       (- (or @wx 0) 20)
                                                       :window-end         (+ @wx @ww 50)
                                                       :size-cache         column-size-cache
                                                       :show-branch-cells? show-column-branches?
                                                       :default-size       (u/deref-or-value column-width)
                                                       :hide-root?         hide-root?}))
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
        corner-header-edges              (fn [{:keys [row-index column-index row-depth column-depth]}]
                                           (cond-> #{}
                                             (= row-index (if hide-root? 1 0))    (conj :top)
                                             (= row-index (dec column-depth))     (conj :bottom)
                                             (= column-index (if hide-root? 1 0)) (conj :left)
                                             (= column-index (dec row-depth))     (conj :right)))
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
                                                 props                        (when cell-value {:cell-value cell-value})
                                                 row-headers                  (for [showing-row-path (cond-> row-paths hide-root? rest)
                                                                                    :let             [{:keys [leaf? show?]} (meta showing-row-path)]
                                                                                    :when            (or leaf? show?)
                                                                                    :let             [this-depth (count showing-row-path)]]
                                                                                (for [i    (cond-> (range @row-depth) hide-root? rest)
                                                                                      :let [row-path (subvec showing-row-path 0 (min (inc i) this-depth))
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
                                                                                      :let                [this-depth (count showing-column-path)
                                                                                                           column-path (subvec showing-column-path 0 (min (inc i) this-depth))
                                                                                                           {:keys [branch-end?]} (meta column-path)
                                                                                                           props {:column-path column-path
                                                                                                                  :path        column-path
                                                                                                                  :branch-end? branch-end?}
                                                                                                           export-column-header (or on-export-column-header column-header-label #())]]
                                                                                  (export-column-header props)))
                                                 corner-headers               (for [row-index (cond-> (range @column-depth) hide-root? rest)]
                                                                                (for [column-index (cond-> (range @row-depth) hide-root? rest)
                                                                                      :let         [props {:row-index    row-index
                                                                                                           :column-index column-index
                                                                                                           :row-depth    @row-depth
                                                                                                           :column-depth @column-depth}
                                                                                                    props (merge props {:edge (corner-header-edges props)})
                                                                                                    export-corner-header (or on-export-corner-header #())]]
                                                                                  (export-corner-header props)))
                                                 cells                        (for [row-path row-paths
                                                                                    :when    ((some-fn :leaf? :show?) (meta row-path))]
                                                                                (for [column-path column-paths
                                                                                      :when       ((some-fn :leaf? :show?) (meta column-path))
                                                                                      :let        [props (merge props
                                                                                                                {:row-path    row-path
                                                                                                                 :column-path column-path})
                                                                                                   export-cell (or on-export-cell cell-value #())]]
                                                                                  (export-cell props)))]
                                             (on-export {:corner-headers corner-headers
                                                         :row-headers    row-headers
                                                         :column-headers column-headers
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
        column-spans                     (r/reaction (ngu/grid-spans @column-paths))
        row-header-width-total           (r/reaction (apply + @processed-row-header-widths))
        row-height-total                 (r/reaction (:sum-size @row-traversal))
        row-paths                        (r/reaction (:header-paths @row-traversal))
        row-keypaths                     (r/reaction (:keypaths @row-traversal))
        row-sizes                        (r/reaction (:sizes @row-traversal))
        row-template                     (r/reaction (ngu/grid-template @row-traversal))
        row-cross-template               (r/reaction (ngu/grid-cross-template @processed-row-header-widths))
        row-spans                        (r/reaction (ngu/grid-spans @row-paths))]
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
            [theme-cells? on-resize hide-root? cell-value]
            {:keys
             [cell corner-header
              row-header column-header
              row-header-label column-header-label
              row-header-grid column-header-grid
              corner-header-grid cell-grid
              row-height column-width]} :parts
            :as                         props
            :or
            {hide-root? true
             on-resize  (fn [{:keys [header-dimension size-dimension keypath size]}]
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
        (let [theme
              (theme/defaults props {:user [(theme/<-props props {:part    ::wrapper
                                                                  :include [:style :class]})]})

              themed
              (fn [part props] (theme/apply props {:part part} theme))

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
                      :let  [row-path (get @row-paths i)
                             size (get @row-sizes i)]
                      :when (and (pos? size)
                                 (map? (peek row-path)))]
                  ^{:key [::row-height-resizer i]}
                  [ngp/resizer {:path             row-path
                                :offset           offset
                                :on-resize        resize!
                                :overlay          overlay
                                :keypath          (get @row-keypaths i)
                                :size             size
                                :header-dimension :row
                                :size-dimension   :height
                                :dimension        :row-height}]))

              column-width-resizers
              (fn [& {:keys [offset style]}]
                (for [i     (range (count @column-paths))
                      :let  [column-path (get @column-paths i)]
                      :when (map? (peek column-path))]
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
              (for [i         (range (count @row-paths))
                    :let      [row-path (get @row-paths i)
                               {:keys [branch-end?]} (meta row-path)]
                    #_#_:when (not branch-end?)
                    :let      [props {:row-path    row-path
                                      :path        row-path
                                      :keypath     (get @row-keypaths i)
                                      :branch-end? branch-end?
                                      :style       {:grid-row-start    (ngu/path->grid-line-name row-path)
                                                    :grid-row-end      (str "span " (cond-> (get @row-spans row-path)
                                                                                      (not show-row-branches?) dec))
                                                    :grid-column-start (cond-> (count row-path) branch-end? dec)
                                                    :grid-column-end   -1}}
                               props (assoc props :children [(u/part row-header-label props
                                                                     :default ngp/row-header-label)])
                               props (themed ::row-header props)]]
                (u/part row-header props {:key row-path}))

              column-headers
              (for [i         (range (count  @column-paths))
                    :let      [column-path         (get @column-paths i)
                               {:keys [branch-end?]} (meta column-path)]
                    #_#_:when (not branch-end?)
                    :let      [props {:column-path column-path
                                      :path        column-path
                                      :branch-end? branch-end?
                                      :keypath     (get @column-keypaths i)
                                      :style       {:grid-column-start (ngu/path->grid-line-name column-path)
                                                    :grid-column-end   (str "span " (cond-> (get @column-spans column-path)
                                                                                      (not show-column-branches?) dec))
                                                    :grid-row-start    (cond-> (count column-path) branch-end? dec)
                                                    :grid-row-end      -1
                                                    :overflow          :hidden}}
                               props (assoc props :children    [(u/part column-header-label props
                                                                        :default ngp/column-header-label)])
                               props (themed ::column-header props)]]
                (u/part column-header props {:key column-path}))

              corner-headers
              (for [column-index (range @row-depth)
                    row-index    (range @column-depth)
                    :let         [#_#_#_#_column-index (cond-> column-index hide-root? dec)
                                      row-index (cond-> row-index hide-root? dec)
                                  props {:row-index    row-index
                                         :column-index column-index
                                         :row-depth    @row-depth
                                         :column-depth @column-depth
                                         :style        {:grid-row-start    (inc row-index)
                                                        :grid-column-start (inc column-index)}}
                                  props (merge props {:edge (corner-header-edges props)})
                                  props (themed ::corner-header props)]]
                (u/part corner-header props {:key [::corner-header row-index column-index]}))

              cells
              (for [row-path    @row-paths
                    column-path @column-paths
                    :when       (and ((some-fn :leaf? :show?) (meta row-path))
                                     ((some-fn :leaf? :show?) (meta column-path)))
                    :let        [props {:row-path    row-path
                                        :column-path column-path}
                                 props (cond-> props
                                         cell-value   (merge {:cell-value cell-value})
                                         theme-cells? (->> (theme ::cell)))]]
                (u/part cell props {:key     [row-path column-path]
                                    :default ngp/cell}))]
          [:div
           (themed ::wrapper
             {:style
              {:grid-template-rows    (ngu/grid-cross-template [@column-header-height-total @row-height-total])
               :grid-template-columns (ngu/grid-cross-template [@row-header-width-total @column-width-total])}
              :ref wrapper-ref!})
           (u/part cell-grid
                   (themed ::cell-grid
                     {:children (concat cells
                                        (row-height-resizers {:offset -1})
                                        (column-width-resizers {:style  {:grid-row-end -1}
                                                                :offset -1}))
                      :style    {:grid-template-rows    @row-template
                                 :grid-template-columns @column-template}}))
           (u/part column-header-grid
                   (themed ::column-header-grid
                     {:children (concat column-headers column-height-resizers (column-width-resizers {:offset -1}))
                      :style    {:grid-template-rows    @column-cross-template
                                 :grid-template-columns @column-template}}))
           (u/part row-header-grid
                   (themed ::row-header-grid
                     {:children (concat row-headers row-width-resizers (row-height-resizers {:offset -1}))
                      :style    {:grid-template-rows    @row-template
                                 :grid-template-columns @row-cross-template}}))
           (u/part corner-header-grid
                   (themed ::corner-header-grid
                     {:children (concat corner-headers row-width-resizers column-height-resizers)
                      :style    {:grid-template-rows    @column-cross-template
                                 :grid-template-columns @row-cross-template}}))
           (u/deref-or-value overlay)]))})))

