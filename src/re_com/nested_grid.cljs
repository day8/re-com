(ns re-com.nested-grid
  (:require
   [clojure.string :as str]
   [re-com.util :as u :refer [px deref-or-value]]
   [reagent.core :as r]
   [re-com.theme :as theme]
   [re-com.box :as box]
   [re-com.buttons :as buttons]))

(def nested-grid-args-desc {})
(def nested-grid-parts-desc {})

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

(defn header-cross-span [path all-paths]
  (->> all-paths
       (filter (partial descendant? path))
       count
       inc))

(defn header-main-span [path all-paths]
  (->> all-paths (map count) (apply max) (+ (- (count path))) inc))

(defn resize-button [& {:as args}]
  (let [dragging?    (r/atom false)
        mouse-down-x (r/atom 0)
        last-drag-x  (r/atom 0)
        drag-x       (r/atom 0)
        hovering?    (r/atom nil)]
    (fn [& {:keys [on-resize column-index path]}]
      [:<>
       [:div {:on-mouse-enter #(reset! hovering? true)
              :on-mouse-leave #(reset! hovering? false)
              :on-mouse-down  #(do
                                 (.preventDefault %)
                                 (reset! dragging?    true)
                                 (reset! mouse-down-x (.-clientX %))
                                 (reset! drag-x       (.-clientX %))
                                 (reset! last-drag-x       (.-clientX %)))
              :style          {:position         "absolute"
                               :opacity          (if (or @hovering? @dragging?) 1 0)
                               :top              0
                               :right            0
                               :cursor           "col-resize"
                               :height           "100%"
                               :width            "25px"
                               :background-color "rgba(0,0,0,0.2)"}}]
       (when @dragging?
         [:div {:on-mouse-up   #(do (reset! dragging? false)
                                    (reset! hovering? false))
                :on-mouse-move #(do (.preventDefault %)
                                    (let [x (.-clientX %)]
                                      (reset! drag-x x)
                                      (when on-resize
                                        (on-resize {:distance (- x @last-drag-x)
                                                    :path     path}))
                                      (reset! last-drag-x x)))
                :style         {:position  "fixed"
                                :z-index   3
                                :width     "100%"
                                :height    "100%"
                                :top       0
                                :left      0
                                :font-size 100}}])])))

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

(defn cell-wrapper-part [{:keys [column-path row-path cell theme]
                          :as args}]
  [:div
   (-> {:style {:grid-column (path->grid-line-name column-path)
                :grid-row (path->grid-line-name row-path)
                :background-color "#fff"
                :padding "3px"
                :text-align "right"
                :border "0.5px solid #ccc"
                :position "relative"}}
       (theme/apply {:state {} :part ::cell-wrapper} theme))
   [u/part cell args cell-part]])

(defn column-header-part [{:keys [column-path]}]
  (let [column (last column-path)]
    (str (or (:label column)
             (:id column)
             column))))

(theme/apply {} {:part ::column-header-wrapper} [])

(defn column-header-wrapper-part [{:keys [column-header column-path column-paths on-resize theme show?] :as props}]
  [:div
   (-> {:style {:grid-column-start (path->grid-line-name column-path)
                :grid-column-end   (str "span " (cond-> column-path
                                                  :do         (header-cross-span column-paths)
                                                  (not show?) dec))
                :grid-row-start    (count column-path)
                :grid-row-end      (str "span " (header-main-span column-path column-paths))}}
       (theme/apply {:state {} :part ::column-header-wrapper} theme))
   [u/part column-header props column-header-part]
   [resize-button {:on-resize on-resize :path column-path}]])

;; Usage of :component-did-update

(defn row-header-part [{:keys [row-path]}]
  (let [column (last row-path)]
    (str (or (:label column)
             (:id column)
             column))))

(defn row-header-wrapper-part [{:keys [row-path row-paths row-header theme show?] :as props}]
  [:div
   (-> {:style {:grid-row-start    (path->grid-line-name row-path)
                :grid-row-end      (str "span" (cond-> row-path
                                                 :do (header-cross-span row-paths)))
                :grid-column-start (count row-path)
                :grid-column-end   (str "span " (cond-> row-path
                                                  :do         (header-main-span row-paths)
                                                  (not show?) dec))}}
       (theme/apply {:state {} :part ::row-header-wrapper} theme))
   [u/part row-header props row-header-part]])

(def level count)

(defn clipboard-export-button [{:keys [on-export]}])

(defn controls [{:keys [show-export-button? hover? on-export]}]
  [box/h-box
   :style {:grid-column-start 1
           :grid-column-end "end"
           :grid-row 1}
   :height "20px"
   :width "100%"
   :children
   [[box/gap :size "1"]
    (when (and show-export-button? @hover?)
      [buttons/row-button
       :md-icon-name    "zmdi zmdi-copy"
       :mouse-over-row? true
       :tooltip         (str "Copy table to clipboard.")
       :on-click        on-export])]])

(defn quantize [quanta threshold]
  (dec (count (take-while #(< % threshold)
                          (reductions + quanta)))))

(assert (= 1 (quantize [10 10 10] 29)))
(assert (= 1 (quantize [10 10 10] 30)))
(assert (= 2 (quantize [10 10 10] 31)))

(defn selection-part [_]
  (fn [_]
    (let [!ref       (r/atom nil)
          reset-ref! (partial reset! !ref)]
      (fn [{:keys [dragging-selection? selection?
                   grid-columns grid-rows
                   mouse-x mouse-y mouse-down-x mouse-down-y]}]
        (let []
          [:<>
           [:div {:ref   reset-ref!
                  :style {:grid-column 1
                          :grid-row    1}}]
           (when @!ref
             (let [grid-columns  (filter number? grid-columns)
                   grid-rows     (filter number? grid-rows)
                   bounds        (.getBoundingClientRect @!ref)
                   origin-x      (.-x bounds)
                   origin-y      (.-y bounds)
                   column-begin  (quantize grid-columns (- @mouse-down-x origin-x))
                   column-finish (quantize grid-columns (- @mouse-x origin-x))
                   row-begin     (quantize grid-rows (- @mouse-down-y origin-y))
                   row-finish    (quantize grid-rows (- @mouse-y origin-y))]
               [:div {:key   (gensym)
                      :style {:grid-column-start (+ 2 (min column-begin column-finish))
                              :grid-column-end   (+ 3 (max column-begin column-finish))
                              :grid-row-start    (+ 2 (min row-begin row-finish))
                              :grid-row-end      (+ 3 (max row-begin row-finish))
                              :z-index           1
                              :border            "2px solid dodgerblue"
                              :background        "rgba(127,127,255,.1)"
                              :position          "relative"}}
                [:div {:style {:position   "absolute"
                               :background "white"
                               :border     "2px solid grey"
                               :height     10
                               :width      10
                               :right      -6
                               :bottom     -6}}]]))
           (when @dragging-selection?
             [:div {:on-mouse-up   #(reset! dragging-selection? false)
                    :on-mouse-move #(do
                                      (reset! dragging-selection? true)
                                      (reset! selection? true)
                                      (.preventDefault %)
                                      (reset! mouse-x (.-clientX %))
                                      (reset! mouse-y (.-clientY %)))
                    :style         {:position "fixed"
                                    :top      0
                                    :left     0
                                    :z-index  2
                                    :height   "100%"
                                    :width    "100%"}}])])))))

(defn header-spacer-part [_] "")

(defn header-spacer-wrapper-part [{:keys [theme x y header-spacer]}]
  (let [props (-> {:style
                   {:grid-column (inc x)
                    :grid-row    (inc y)}}
                  (theme/apply {:part ::header-spacer} theme))]
    [:div props
     [u/part header-spacer props header-spacer-part]]))

(defn scroll-container [{:keys [scroll-top scroll-left width height]} child]
  [:div {:style {:max-height            height
                 :max-width             width
                 :overflow              "hidden"}}
   [:div {:style {:transform (str "translateX(" (- (deref-or-value scroll-left)) "px) "
                                  "translateY(" (- (deref-or-value scroll-top)) "px)")}}
    child]])

(defn nested-grid [& {:keys [column-width]
                      :or   {column-width 60}}]
  (let [column-state        (r/atom {})
        row-state           (r/atom {})
        hover?              (r/atom false)
        dragging-selection? (r/atom false)
        selection?          (r/atom nil)
        mouse-down-x        (r/atom 0)
        mouse-down-y        (r/atom 0)
        mouse-x             (r/atom 0)
        mouse-y             (r/atom 0)
        scroll-top          (r/atom 0)
        scroll-left         (r/atom 0)
        column-header-prop  (fn [path k & [default]]
                              (or (some-> @column-state (get path) (get k))
                                  (get (meta (last path)) k)
                                  (get (last path) k)
                                  default))
        header-prop         (fn [path k dimension & [default]]
                              (let [state (-> (case dimension
                                                :row @row-state
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
        on-resize-cell      (fn [{:keys [distance path]}]
                              (swap! column-state update-in [path :width]
                                     #(+ distance (or % (column-header-prop path :width column-width)))))]
    (fn [& {:keys [column-tree row-tree cell
                   cell-wrapper column-header-wrapper column-header row-header row-header-wrapper header-spacer-wrapper header-spacer
                   show-branch-paths?
                   max-height column-width column-header-height row-header-width row-height
                   show-export-button? on-export on-export-success on-export-failure
                   on-export-cell on-export-column-header on-export-row-header]
            :or   {column-header-height    30
                   column-width            60
                   row-header-width        100
                   row-height              30
                   show-export-button?     true
                   show-branch-paths?      false
                   on-export-column-header pr-str}}]
      (let [theme {}
            themed                 (fn [part props] (theme/apply props {:part part} theme))
            column-paths           (spec->headers* column-tree)
            column-leaf-paths      (leaf-paths column-paths)
            leaf-column?           (set column-leaf-paths)
            row-paths              (spec->headers* row-tree)
            leaf-row?              (set (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths))
            leaf?                  (fn [path dimension]
                                     (case dimension
                                       :column (leaf-column? path)
                                       :row    (leaf-row? path)))
            show?                  (fn [path dimension]
                                     (let [show-prop (header-prop path :show? dimension)
                                           result (and (not (false? show-prop))
                                                       (or (true? show-prop)
                                                           show-branch-paths?
                                                           (leaf? path dimension)))]
                                       result))
            showing-column-paths   (filter #(show? % :column) column-paths)
            showing-row-paths      (filter #(show? % :row) row-paths)
            showing-column-widths  (map #(column-header-prop % :width column-width)
                                        showing-column-paths)
            showing-row-heights    (map #(column-header-prop % :height row-height)
                                        showing-row-paths)
            max-column-heights     (max-props :height :column column-header-height column-paths)
            max-row-widths         (max-props :width :row row-header-width row-paths)
            column-depth           (count max-column-heights)
            row-depth              (count max-row-widths)
            default-on-export-column-header
            (comp pr-str last)
            default-on-export-row-header
            (comp pr-str last)
            default-on-export-cell
            (comp pr-str cell)
            get-header-rows        (fn get-header-rows []
                                     (->> column-paths
                                          (mapcat (fn [path]
                                                    (if (leaf-column? path)
                                                      [path]
                                                      (repeat (dec (header-cross-span path column-paths))
                                                              path))))
                                          (group-by count)
                                          (into (sorted-map))
                                          vals
                                          (map (partial map (or on-export-column-header
                                                                default-on-export-column-header)))
                                          (map #(concat (repeat row-depth nil) %))))
            get-main-rows          (fn get-main-rows []
                                     (let [ancestors
                                           #(->> % (iterate pop) (take-while seq) reverse)
                                           add-padding
                                           (fn [[paths :as coll]]
                                             (conj coll (repeat (- row-depth (count paths)) nil)))
                                           add-cell-values
                                           (fn [[paths padding]]
                                             (->> showing-column-paths
                                                  (map
                                                   #((or on-export-cell default-on-export-cell)
                                                     {:column-path %
                                                      :row-path    (last paths)}))
                                                  (conj [paths padding])))
                                           render-row-headers
                                           (fn [[paths padding cells]]
                                             (concat (map (or on-export-row-header
                                                              default-on-export-row-header)
                                                          paths)
                                                     padding
                                                     cells))]
                                       (->> showing-row-paths
                                            (map ancestors)
                                            (map vector)
                                            (map add-padding)
                                            (map add-cell-values)
                                            (map render-row-headers))))
            default-on-export      (fn default-on-export [header-rows main-rows]
                                     (->> (concat header-rows main-rows)
                                          (map u/tsv-line)
                                          str/join
                                          u/clipboard-write!))
            cell-grid-columns      (->> column-paths
                                        (mapcat (fn [path]
                                                  (let [width (header-prop path :width :column column-width)]
                                                    (if (show? path :column)
                                                      [path width]
                                                      [path])))))
            cell-grid-rows         (->> row-paths
                                        (mapcat (fn [path]
                                                  (let [height (header-prop path :height :row row-height)]
                                                    (if (show? path :row)
                                                      [path height]
                                                      [path])))))
            control-panel          [controls {:show-export-button? show-export-button?
                                              :hover?              hover?
                                              :on-export
                                              (fn [_] (let [header-rows (get-header-rows)
                                                            main-rows   (get-main-rows)]
                                                        ((or on-export default-on-export) header-rows main-rows)
                                                        (when on-export-success (on-export-success header-rows main-rows))))}]
            grid-container
            [:div {:on-scroll     #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                       (reset! scroll-left (.-scrollLeft (.-target %))))
                   :on-mouse-down #(do
                                     (if-not @selection?
                                       (do (reset! selection? true)
                                           (reset! dragging-selection? true)
                                           (reset! mouse-down-y (.-clientY %))
                                           (reset! mouse-down-x (.-clientX %))
                                           (reset! mouse-y (.-clientY %))
                                           (reset! mouse-x (.-clientX %)))
                                       (reset! selection? false)))
                   :style         {:padding               "0px"
                                   :cursor                "crosshair"
                                   :max-height            max-height
                                   :display               "grid"
                                   :overflow              "auto"
                                   :scrollbar-width       "thin"
                                   :grid-template-columns (grid-template cell-grid-columns)
                                   :grid-template-rows    (grid-template cell-grid-rows)
                                   :gap                   "0px"
                                   :background-color      "transparent"}}]
            column-header-cells    (doall
                                    (for [path column-paths
                                          :let [props {:column-path        path
                                                       :column-paths       column-paths
                                                       :on-resize          on-resize-cell
                                                       :column-header      column-header
                                                       :show?              (show? path :column)}]]
                                      ^{:key [::column (or path (gensym))]}
                                      [u/part column-header-wrapper props column-header-wrapper-part]))
            row-header-cells       (doall
                                    (for [path row-paths
                                          :let [props {:row-path           path
                                                       :row-header         row-header
                                                       :row-paths          row-paths
                                                       :show?              (show? path :row)}]]
                                      ^{:key [::row (or path (gensym))]}
                                      [u/part row-header-wrapper props row-header-wrapper-part]))
            header-spacer-cells    (for [y (range column-depth)
                                         x (range row-depth)]
                                     ^{:key [::header-spacer x y]}
                                     [u/part header-spacer-wrapper
                                      {:theme theme :x x :y y :header-spacer header-spacer}
                                      header-spacer-wrapper-part])
            cells                  (doall
                                    (for [column-path showing-column-paths
                                          row-path    showing-row-paths
                                          :let        [props {:column-path column-path
                                                              :row-path    row-path
                                                              :cell        cell}]]
                                      ^{:key [::cell (or [column-path row-path] (gensym))]}
                                      [u/part cell-wrapper props cell-wrapper-part]))
            zebra-stripes          (for [i (filter even? (range (count row-paths)))]
                                     ^{:key [::zebra-stripe i]}
                                     [:div
                                      (themed ::zebra-stripe
                                        {:style
                                         {:grid-column-start 1
                                          :grid-column-end   "end"
                                          :grid-row          i
                                          :background-color  "cornflowerblue"
                                          :opacity           0.05
                                          :z-index           2}})])
            box-selector           [selection-part
                                    {:dragging-selection? dragging-selection?
                                     :grid-columns        cell-grid-columns
                                     :grid-rows           cell-grid-rows
                                     :selection?          selection?
                                     :mouse-x             mouse-x
                                     :mouse-y             mouse-y
                                     :mouse-down-x        mouse-down-x
                                     :mouse-down-y        mouse-down-y}]
            ;; FIXME This changes on different browsers - do we need to get it dynamically?
            ;; FIXME We should use :scrollbar-gutter (chrome>=94)
            native-scrollbar-width 10]
        [:div {:on-mouse-enter #(reset! hover? true)
               :on-mouse-leave #(reset! hover? false)
               :style
               {:display               "grid"
                :grid-template-columns (grid-template [(px (apply + max-row-widths))
                                                       (px (+ native-scrollbar-width
                                                              (apply + showing-column-widths)))])
                :grid-template-rows    (grid-template ["20px" showing-column-widths
                                                       (px (apply + max-column-heights))
                                                       (px (apply + showing-row-heights))])}}
         control-panel
         [:div {:style {:display               "grid"
                        :grid-template-columns (grid-template max-row-widths)
                        :grid-template-rows    (grid-template max-column-heights)}}
          header-spacer-cells]
         [scroll-container {:scroll-left scroll-left}
          [:div {:style {:display               "grid"
                         :grid-template-columns (grid-template cell-grid-columns)
                         :grid-template-rows    (grid-template max-column-heights)}}
           column-header-cells]]
         [scroll-container {:scroll-top scroll-top
                            :height     max-height}
          [:div {:style {:display               "grid"
                         :grid-template-columns (grid-template max-row-widths)
                         :grid-template-rows    (grid-template cell-grid-rows)}}
           row-header-cells]]
         (-> grid-container
             (into cells)
             (into zebra-stripes)
             (conj (when @selection? box-selector)))]))))
