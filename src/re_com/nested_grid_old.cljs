(ns re-com.nested-grid-old)

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
        resize-handler      (r/atom #())]
    (fn [& {:as passed-in-props}]
      (let [{:as   props
             :keys [column-tree row-tree
                    cell cell-value column-header row-header header-spacer
                    cell-wrapper column-header-wrapper row-header-wrapper header-spacer-wrapper
                    theme-cells?
                    show-branch-paths?
                    max-height max-width
                    remove-empty-row-space? remove-empty-column-space?
                    column-width column-header-height row-header-width row-height
                    show-export-button? on-export export-button
                    on-export-cell on-export-column-header on-export-row-header
                    show-zebra-stripes?
                    show-selection-box? resize-columns? resize-rows?
                    sticky? sticky-left sticky-top
                    debug-parts?
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
                    resize-columns?            true
                    resize-rows?               false
                    theme-cells?               true
                    debug-parts?               (or config/debug? config/debug-parts?)}}
            (theme/top-level-part passed-in-props ::nested-grid)
            theme                      (theme/defaults
                                        props
                                        {:user [(theme/<-props props {:part    ::wrapper
                                                                      :include [:style :class]})]})
            themed                     (fn [part props] (theme/apply props {:part part} theme))
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
            spacer?                    number?
            export-column-headers      #(let [{:keys [grid-column-start
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
                                                                     (on-export-column-header {:path        path
                                                                                               :column-path path})))]
                                          (cond->> column-paths
                                            :do        (reduce insert result)
                                            selection? (mapv crop)))
            export-row-headers         #(let [{:keys [grid-row-start
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
                                                                     (on-export-row-header {:path     path
                                                                                            :row-path path})))
                                              all        (reduce insert result row-paths)]
                                          (if-not selection?
                                            all
                                            (transpose (mapv crop (transpose all)))))
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
            export-spacers             #(vec (repeat column-depth (vec (repeat row-depth nil))))
            default-export-button      (fn [{:keys [on-click]}]
                                         [buttons/md-icon-button
                                          {:md-icon-name "zmdi zmdi-copy"
                                           :style        {:height         "18px"
                                                          :font-size      "18px"
                                                          :line-height    "18px"
                                                          :padding-bottom 0}
                                           :attr         {:title "Copy to Clipboard"}
                                           :on-click     on-click}])
            export-button              (u/part export-button
                                               (themed ::export-button
                                                 {:style    {:position :fixed
                                                             :right    10}
                                                  :on-click #(let [column-headers (export-column-headers)
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
                                                                 :default        default-on-export}))})
                                               :default default-export-button)
            cell-grid-container        [:div
                                        (themed ::cell-grid-container
                                          {:style {:max-height            max-height
                                                   :max-width             max-width
                                                   :display               :grid
                                                   :grid-column-start     2
                                                   :grid-row-start        2
                                                   :grid-template-columns (ngu/grid-template cell-grid-columns)
                                                   :grid-template-rows    (ngu/grid-template cell-grid-rows)}})]
            column-header-cells        (for [path column-paths
                                             :let [edge (cond-> #{}
                                                          (start-branch? path column-paths) (conj :left)
                                                          (end-branch? path column-paths)   (conj :right)
                                                          (= 1 (count path))                (conj :top)
                                                          (= (count path) column-depth)     (conj :bottom)
                                                          (section-left? path)              (conj :column-section-left)
                                                          (section-right? path)             (conj :column-section-right))
                                                   show? (show? path :column)
                                                   state {:edge        edge
                                                          :column-path path
                                                          :path        path
                                                          :header-spec (last path)
                                                          :show?       show?
                                                          :sticky?     sticky?
                                                          :row-header-total-width
                                                          row-header-total-width}
                                                   theme (update theme :user-variables
                                                                 conj (theme/with-state state))
                                                   props (merge {:theme      theme
                                                                 :selection? selection?
                                                                 :edge       edge}
                                                                state)
                                                   children [(u/part
                                                              column-header
                                                              (theme/apply props {:part ::column-header} theme)
                                                              :default re-com.nested-grid/column-header)]]]
                                         ^{:key [::column (or path (gensym))]}
                                         [:div {:style {:grid-column-start (ngu/path->grid-line-name path)
                                                        :grid-column-end   (str "span " (cond-> path
                                                                                          :do         (header-cross-span column-paths)
                                                                                          (not show?) dec))
                                                        :grid-row-start    (count path)
                                                        :grid-row-end      (str "span " (cond-> path
                                                                                          :do         (header-main-span column-paths)
                                                                                          (not show?) dec))
                                                        :position          "relative"}}
                                          (u/part column-header-wrapper
                                                  (-> props
                                                      (merge {:children children})
                                                      (merge {:attr {:on-click (debug/log-on-alt-click props)}})
                                                      (theme/apply {:part ::column-header-wrapper} theme)))
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
            row-header-cells           (for [path row-paths
                                             :let [edge (cond-> #{}
                                                          (start-branch? path row-paths)                (conj :top)
                                                          ;; TODO: incorrect when the final path is shallower than the tree
                                                          (end-branch? path row-paths)                  (conj :bottom)
                                                          (= 1 (count path))                            (conj :left)
                                                          (or (= (count path) row-depth)
                                                              (= 1 (header-cross-span path row-paths))) (conj :right))
                                                   show? (show? path :row)
                                                   state {:edge        edge
                                                          :row-path    path
                                                          :path        path
                                                          :header-spec (last path)
                                                          :show?       show?
                                                          :sticky?     sticky?
                                                          :sticky-top  (cond-> column-header-total-height
                                                                         (and sticky? show-export-button?) (+ 25))}
                                                   theme (update theme :user-variables
                                                                 conj (theme/with-state state))
                                                   props (merge {:theme      theme
                                                                 :selection? selection?}
                                                                state)
                                                   children [(u/part row-header
                                                                     (theme/apply props {:part ::row-header} theme)
                                                                     :default re-com.nested-grid/row-header)]]]
                                         ^{:key [::row (or path (gensym))]}
                                         [:div {:style {:grid-row-start    (ngu/path->grid-line-name path)
                                                        :grid-row-end      (str "span " (cond-> path
                                                                                          :do         (header-cross-span showing-row-paths)
                                                                                          (not show?) dec))
                                                        :grid-column-start (count path)
                                                        :grid-column-end   (str "span " (cond-> path
                                                                                          :do         (header-main-span showing-row-paths)
                                                                                          (not show?) dec))
                                                        :position          "relative"}}
                                          (u/part row-header-wrapper
                                                  (-> props
                                                      (merge {:children children})
                                                      (merge {:attr {:on-click (debug/log-on-alt-click props)}})
                                                      (theme/apply {:part ::row-header-wrapper} theme)))
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
            header-spacer-cells        (for [y    (range column-depth)
                                             x    (range row-depth)
                                             :let [state {:edge (cond-> #{}
                                                                  (zero? y)                (conj :top)
                                                                  (zero? x)                (conj :left)
                                                                  (= y (dec column-depth)) (conj :bottom)
                                                                  (= x (dec row-depth))    (conj :right))}
                                                   theme (update theme :user-variables
                                                                 conj (theme/with-state state))
                                                   props (merge state
                                                                {:style         {:grid-column (inc x)
                                                                                 :grid-row    (inc y)}
                                                                 :attr          {:on-click (debug/log-on-alt-click props)}
                                                                 :theme         theme
                                                                 :x             x
                                                                 :y             y
                                                                 :header-spacer header-spacer})
                                                   children [(u/part header-spacer props)]]]
                                         (u/part header-spacer-wrapper
                                                 (theme/apply (merge props {:children children}) {:part ::header-spacer-wrapper} theme)))
            cells                      (if-not theme-cells?
                                         (for [row-path    showing-row-paths
                                               column-path showing-column-paths]
                                           [cell (cond-> {:style       {:grid-column (ngu/path->grid-line-name column-path)
                                                                        :grid-row    (ngu/path->grid-line-name row-path)}
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
                                                            state (cond-> {:edge        edge
                                                                           :column-path column-path
                                                                           :row-path    row-path}
                                                                    value (merge {:value value}))
                                                            theme (update theme :user-variables
                                                                          conj (theme/with-state state))
                                                            props (merge {:cell  cell
                                                                          :theme theme}
                                                                         state)
                                                            cell-props (merge {:theme theme}
                                                                              (when value {:value value})
                                                                              state)
                                                            children [(u/part cell
                                                                              (theme/apply cell-props {:state state :part ::cell} theme)
                                                                              :default re-com.nested-grid/cell)]]]
                                           (u/part cell-wrapper
                                                   (theme/apply (merge props {:children children}) {:part ::cell-wrapper} theme)
                                                   :default re-com.nested-grid/cell-wrapper)))
            zebra-stripes              (for [i (filter even? (range 1 (inc (count row-paths))))]
                                         ^{:key [::zebra-stripe i]}
                                         [:div
                                          (themed ::zebra-stripe
                                            {:style
                                             {:grid-column-start 1
                                              :grid-column-end   "end"
                                              :grid-row          i
                                              :background-color  "#999"
                                              :opacity           0.05
                                              :z-index           1
                                              :pointer-events    "none"}})])
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
                                                             :justify-content  :end
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
                                                    :children [export-button]}]]
            outer-grid-container       [:div
                                        (themed ::outer-grid-container
                                          {:on-mouse-enter #(reset! hover? true)
                                           :on-mouse-leave #(reset! hover? false)
                                           :style
                                           (merge
                                            {:position              :relative
                                             :display               :grid
                                             :grid-template-columns (ngu/grid-template [(px row-header-total-width)
                                                                                        (px column-header-total-width)])
                                             :grid-template-rows    (ngu/grid-template [(px column-header-total-height)
                                                                                        "1fr"])}
                                            (when-not sticky?
                                              {:max-width  (or max-width (when remove-empty-column-space? native-width))
                                               :max-height (or max-height
                                                               (when remove-empty-row-space? native-height))
                                               :flex       1
                                               :overflow   :auto}))})]
            header-spacers             (into [:div (themed ::header-spacer-grid-container
                                                     {:style {:display               :grid
                                                              :box-sizing            :border-box
                                                              :position              :sticky
                                                              :top                   (cond-> sticky-top (and sticky? show-export-button?) (+ 25))
                                                              :left                  (if sticky? sticky-left 0)
                                                              :grid-column-start     1
                                                              :grid-row-start        1
                                                              :z-index               3
                                                              :grid-template-columns (ngu/grid-template max-row-widths)
                                                              :grid-template-rows    (ngu/grid-template max-column-heights)}})]
                                             header-spacer-cells)
            column-headers             (into [:div (themed ::column-header-grid-container
                                                     {:style {:position              :sticky
                                                              :top                   (cond-> sticky-top (and sticky? show-export-button?) (+ 25))
                                                              :width                 :fit-content
                                                              :z-index               2
                                                              :display               :grid
                                                              :grid-column-start     2
                                                              :grid-row-start        1
                                                              :grid-template-columns (ngu/grid-template cell-grid-columns)
                                                              :grid-template-rows    (ngu/grid-template max-column-heights)}})]
                                             column-header-cells)
            row-headers                (into [:div (themed ::row-header-grid-container
                                                     {:style {:position              :sticky
                                                              :left                  (if sticky? sticky-left 0)
                                                              :z-index               1
                                                              :display               :grid
                                                              :grid-column-start     1
                                                              :grid-row-start        2
                                                              :grid-template-columns (ngu/grid-template max-row-widths)
                                                              :grid-template-rows    (ngu/grid-template cell-grid-rows)}})]
                                             row-header-cells)
            cells                      (-> cell-grid-container
                                           (into cells)
                                           (into (if (and show-zebra-stripes? (> (count showing-row-paths) 3))
                                                   zebra-stripes
                                                   []))
                                           (conj (when show-selection-box? box-selector)))]
        [:div (debug/->attr
               (themed ::wrapper
                 {:src src
                  :style (merge {:flex-direction :column}
                                (when-not sticky?
                                  (merge {:flex    "0 0 auto"
                                          :display :flex}
                                         (when remove-empty-column-space?
                                           {:max-width :fit-content})
                                         (when remove-empty-row-space?
                                           {:max-height :fit-content}))))}))
         (when show-export-button? control-panel)
         (conj
          outer-grid-container
          header-spacers
          column-headers
          row-headers
          cells)
         overlays]))))

