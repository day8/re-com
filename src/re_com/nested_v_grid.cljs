(ns re-com.nested-v-grid
  (:require
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
                             show-row-branches? show-column-branches?
                             hide-root?]
                      :or   {row-header-width 40 column-header-height 40
                             row-height       20 column-width         40
                             hide-root?       true}}]
  (let [[wx wy wh ww !wrapper-ref scroll-listener resize-observer overlay
         column-header-heights-internal row-header-widths-internal]
        (repeatedly #(r/atom nil))
        wrapper-ref!               (partial reset! !wrapper-ref)
        on-scroll!                 #(do (reset! wx (.-scrollLeft (.-target %)))
                                        (reset! wy (.-scrollTop (.-target %))))
        on-resize!                 #(do (reset! wh (.-height (.-contentRect (aget % 0))))
                                        (reset! ww (.-width (.-contentRect (aget % 0)))))
        internal-row-tree          (r/atom (u/deref-or-value row-tree))
        internal-column-tree       (r/atom (u/deref-or-value column-tree))
        size-cache                 (volatile! {})
        column-depth               (r/reaction (or (u/deref-or-value column-tree-depth)
                                                   (count (u/deref-or-value column-header-heights))))
        row-depth                  (r/reaction (or (u/deref-or-value row-tree-depth)
                                                   (count (u/deref-or-value row-header-widths))))
        row-traversal              (r/reaction
                                    (ngu/walk-size {:tree               @internal-row-tree
                                                    :window-start       (- (or @wy 0) 20)
                                                    :window-end         (+ @wy @wh)
                                                    :size-cache         size-cache
                                                    :dimension          :row
                                                    :show-branch-cells? show-row-branches?
                                                    :default-size       (u/deref-or-value row-height)
                                                    :hide-root?         hide-root?}))
        column-traversal           (r/reaction
                                    (ngu/walk-size {:tree               @internal-column-tree
                                                    :window-start       (- (or @wx 0) 20)
                                                    :window-end         (+ @wx @ww 50)
                                                    :size-cache         size-cache
                                                    :dimension          :column
                                                    :show-branch-cells? show-column-branches?
                                                    :default-size       (u/deref-or-value column-width)
                                                    :hide-root?         hide-root?}))
        column-header-heights      (r/reaction (cond-> (or
                                                        (u/deref-or-value column-header-heights-internal)
                                                        (u/deref-or-value column-header-heights)
                                                        (repeat @column-depth column-header-height))
                                                 hide-root? (#(into [0] (rest %)))
                                                 :do        vec))
        row-header-widths          (r/reaction (cond-> (or
                                                        (u/deref-or-value row-header-widths-internal)
                                                        (u/deref-or-value row-header-widths)
                                                        (repeat @row-depth row-header-width))
                                                 hide-root? (#(into [0] (rest %)))
                                                 :do        vec))
        column-header-height-total (r/reaction (apply + @column-header-heights))
        column-width-total         (r/reaction (:sum-size @column-traversal))
        column-paths               (r/reaction (:paths @column-traversal))
        column-keypaths            (r/reaction (:keypaths @column-traversal))
        column-sizes               (r/reaction (:sizes @column-traversal))
        column-template            (r/reaction (ngu/lazy-grid-template @column-traversal))
        column-header-template     (r/reaction (ngu/grid-template @column-header-heights))
        column-spans               (r/reaction (ngu/grid-spans @column-paths))
        row-header-width-total     (r/reaction (apply + @row-header-widths))
        row-height-total           (r/reaction (:sum-size @row-traversal))
        row-paths                  (r/reaction (:paths @row-traversal))
        row-keypaths               (r/reaction (:keypaths @row-traversal))
        row-sizes                  (r/reaction (:sizes @row-traversal))
        row-template               (r/reaction (ngu/lazy-grid-template @row-traversal))
        row-header-template        (r/reaction (ngu/grid-template @row-header-widths))
        row-spans                  (r/reaction (ngu/grid-spans @row-paths))]
    (r/create-class
     {:component-did-mount
      #(do (reset! scroll-listener
                   (.addEventListener @!wrapper-ref "scroll" on-scroll!))
           (reset! resize-observer
                   (.observe (js/ResizeObserver. on-resize!) @!wrapper-ref)))
      :component-did-update
      #(let [[_ {:keys [row-tree column-tree]}] (r/argv %)]
         (reset! internal-row-tree (u/deref-or-value row-tree))
         (reset! internal-column-tree (u/deref-or-value column-tree)))
      :reagent-render
      (fn [{:keys
           [row-tree column-tree theme-cells? on-resize hide-root?
            cell-value]
           {:keys
            [cell corner-header
                    row-header column-header
                    row-header-label column-header-label
                    row-header-grid column-header-grid
                    corner-header-grid cell-grid
                    row-height column-width]} :parts
            :as                               props
            :or
            {hide-root? true
             on-resize  (fn [{:keys [header-dimension size-dimension keypath size]}]
                          (case [header-dimension size-dimension]
                            [:column :height] (swap! column-header-heights-internal assoc-in keypath size)
                            [:row :width]     (swap! row-header-widths-internal assoc-in keypath size)))}}]
        (mapv u/deref-or-value [row-tree row-header-widths row-height
                                column-tree column-header-heights column-width])
        (let [theme
              (theme/defaults props {:user [(theme/<-props props {:part    ::wrapper
                                                                  :include [:style :class]})]})

              themed
              (fn [part props] (theme/apply props {:part part} theme))

              row-width-resizers
              (for [i (range (if hide-root? 1 0) @row-depth)]
                ^{:key [::row-width-resizer i]}
                [ngp/resizer {:on-resize        on-resize
                              :overlay          overlay
                              :header-dimension :row
                              :size-dimension   :width
                              :dimension        :row-header-width
                              :keypath          [i]
                              :index            i
                              :size             (get @row-header-widths i row-header-width)}])

              column-height-resizers
              (for [i (range (if hide-root? 1 0) @column-depth)]
                ^{:key [::column-height-resizer i]}
                [ngp/resizer {:path             (get @column-paths i)
                              :on-resize        on-resize
                              :overlay          overlay
                              :header-dimension :column
                              :size-dimension   :height
                              :dimension        :column-header-height
                              :keypath          [i]
                              :index            i
                              :size             (get @column-header-heights i column-header-height)}])

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
                                :on-resize        on-resize
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
                                :on-resize        on-resize
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
                                                    :grid-row-end      (str "span " (get @row-spans row-path))
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
                                                    :grid-column-end   (str "span " (get @column-spans column-path))
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
                                  props (themed ::corner-header
                                          {:row-index    row-index
                                           :column-index column-index
                                           :edge
                                           (cond-> #{}
                                             (= row-index
                                                (if hide-root? 1 0))           (conj :top)
                                             (= row-index (dec @column-depth)) (conj :bottom)
                                             (= column-index
                                                (if hide-root? 1 0))           (conj :left)
                                             (= column-index (dec @row-depth)) (conj :right))
                                           :style        {:grid-row-start    (inc row-index)
                                                          :grid-column-start (inc column-index)}})]]
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
              {:grid-template-rows    (ngu/grid-template [@column-header-height-total @row-height-total])
               :grid-template-columns (ngu/grid-template [@row-header-width-total @column-width-total])}
              :ref wrapper-ref!})
           (u/part cell-grid
                   (themed ::cell-grid
                     {:children (concat cells
                                        (row-height-resizers {:offset -1})
                                        (column-width-resizers {:style  {:grid-row-end -1}
                                                                :offset -1}))
                      :style    {:display               :grid
                                 :grid-column-start     2
                                 :grid-row-start        2
                                 :grid-template-rows    @row-template
                                 :grid-template-columns @column-template}}))
           (u/part column-header-grid
                   (themed ::column-header-grid
                     {:children (concat column-headers column-height-resizers (column-width-resizers {:offset -1}))
                      :style    {:grid-template-rows    @column-header-template
                                 :grid-template-columns @column-template}}))
           (u/part row-header-grid
                   (themed ::row-header-grid
                     {:children (concat row-headers row-width-resizers (row-height-resizers {:offset -1}))
                      :style    {:grid-template-rows    @row-template
                                 :grid-template-columns @row-header-template}}))
           (u/part corner-header-grid
                   (themed ::corner-header-grid
                     {:children (concat corner-headers row-width-resizers column-height-resizers)
                      :style    {:grid-template-rows    @column-header-template
                                 :grid-template-columns @row-header-template}}))
           (u/deref-or-value overlay)]))})))

