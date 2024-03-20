(ns re-com.pivot
  (:require
   [clojure.string :as str]
   [re-com.util :as u :refer [px deref-or-value]]
   [reagent.core :as r]
   [re-com.theme :as theme]
   [re-com.box :as box]
   [re-com.buttons :as buttons]))

(def pivot-grid-args-desc {})
(def pivot-grid-parts-desc {})

(defn descendant? [path-a path-b]
  (and (not= path-a path-b)
       (= path-a (vec (take (count path-a) path-b)))))

(def header? (complement (some-fn vector? seq?)))

(defn spec->headers ;; TODO simplify
  ([data] (spec->headers [] data))
  ([path data]
   (let [pairs
         (->> data
              (into [::stub]) ;; make sure 1st partition is always headers
              (partition-by header?)
              (partition-all 2))]
     (vec
      (apply concat
             (for [[headers branches] pairs
                   :let              [headers (remove #{::stub} headers)
                                      branch-path (into path (take-last 1 headers))]]
               (concat
                (map (partial conj path) headers)
                (mapcat (partial spec->headers branch-path) branches))))))))

(def spec->headers* (memoize spec->headers))

(assert (= (spec->headers [:a :b :c])
           [[:a] [:b] [:c]]))
(assert (= (spec->headers [:a [:b] [:c]])
           [[:a] [:a :b] [:a :c]]))
(assert (= (spec->headers [:a :b [:c]])
           [[:a] [:b] [:b :c]]))
(assert (= (spec->headers [[:a [:b :c]]])
           [[:a] [:a :b] [:a :c]]))
(assert (= (spec->headers [[:x [:b :c]]
                           [:y [:b :c]]])
           [[:x] [:x :b] [:x :c] [:y] [:y :b] [:y :c]]))

(defn header-cross-span [group-path all-paths]
  (->> all-paths
       (filter (partial descendant? group-path))
       count
       inc))

(defn header-main-span [group-path all-paths]
  (->> all-paths (map count) (apply max) (+ (- (count group-path))) inc))

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
                               :height           "100%"
                               :width            "25px"
                               :background-color "rgba(0,0,0,0.2)"}}]
       (when @dragging?
         [:div {:on-mouse-up   #(do (reset! dragging? false)
                                    (reset! hovering? true))
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

(defn column-header-part [{:keys [path]}]
  (str (get (last path) :id (last path))))

(theme/apply {} {:part ::column-header-wrapper} [])

(defn column-header-wrapper-part [{:keys [column-header path column-paths on-resize show-branch-cells? leaf? theme] :as props}]
  (let [hide? (and (not leaf?) (not show-branch-cells?))]
    [:div
     (-> {:style {:grid-column-start (path->grid-line-name path)
                  :grid-column-end   (str "span " (cond-> path
                                                    :do   (header-cross-span column-paths)
                                                    hide? dec))
                  :grid-row-start    (count path)}}
         (theme/apply {:state {} :part ::column-header-wrapper} theme))
     [u/part column-header props column-header-part]
     [resize-button {:on-resize on-resize :path path}]]))

;; Usage of :component-did-update

(defn row-header-wrapper-part [{:keys [path row-paths row-header show-branch-cells? leaf? theme] :as props}]
  (let [hide? (and (not leaf?) (not show-branch-cells?))]
    [:div
     (-> {:style {:grid-row-start    (path->grid-line-name path)
                  :grid-column-start (count path)
                  :grid-column-end   (str "span " (cond-> path
                                                    :do   (header-main-span row-paths)
                                                    hide? dec))}}
         (theme/apply {:state {} :part ::row-header-wrapper} theme))
     [u/part row-header props column-header-part]]))

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
      (fn [{:keys [selecting? grid-columns grid-rows mouse-x mouse-y mouse-down-x mouse-down-y]}]
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
                              :border            "4px solid dodgerblue"
                              :position "relative"}}
                [:div {:style {:position "absolute"
                               :background "white"
                               :border "2px solid grey"
                               :height 10
                               :width 10
                               :right -6
                               :bottom -6}}]]))
           (when @selecting?
             [:div {:on-mouse-up   #(reset! selecting? false)
                    :on-mouse-move #(do
                                      (.preventDefault %)
                                      (reset! mouse-x (.-clientX %))
                                      (reset! mouse-y (.-clientY %)))
                    :style         {:position "fixed"
                                    :top      0
                                    :left     0
                                    :z-index  2
                                    :height   "100%"
                                    :width    "100%"}}])])))))

(defn scroll-container [{:keys [scroll-top scroll-left width height]} child]
  [:div {:style {:max-height            height
                 :max-width             width
                 :overflow              "hidden"}}
   [:div {:style {:transform (str "translateX(" (- (deref-or-value scroll-left)) "px) "
                                  "translateY(" (- (deref-or-value scroll-top)) "px)")}}
    child]])

(defn grid [& {:keys [column-width]
               :or   {column-width 60}}]
  (let [column-state       (r/atom {})
        row-state          (r/atom {})
        hover?             (r/atom false)
        selecting?         (r/atom false)
        mouse-down-x       (r/atom 0)
        mouse-down-y       (r/atom 0)
        mouse-x            (r/atom 0)
        mouse-y            (r/atom 0)
        scroll-top         (r/atom 0)
        scroll-left        (r/atom 0)
        column-header-prop (fn [path k & [default]]
                             (or (some-> @column-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        header-prop        (fn [path k dimension & [default]]
                             (or (some-> (case dimension :row @row-state :column @column-state)
                                         (get path)
                                         (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        row-header-prop    (fn [path k & [default]]
                             (or (some-> @row-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        max-props          (fn [k dimension default paths]
                             (->> paths
                                  (group-by level)
                                  (sort-by key)
                                  (map val)
                                  (map (fn [paths] (apply max (map #(header-prop % k :row default) paths))))))
        on-resize-cell     (fn [{:keys [distance path]}]
                             (swap! column-state update-in [path :width]
                                    #(+ distance (or % (column-header-prop path :width column-width)))))]
    (fn [& {:keys [columns rows cell
                   cell-wrapper column-header-wrapper column-header row-header
                   show-branch-cells?
                   max-height column-width column-height row-width row-height
                   show-export-button? on-export on-export-success on-export-failure
                   on-export-cell on-export-column-header on-export-row-header]
            :or   {column-height       30
                   column-width        60
                   row-width           100
                   row-height          30
                   show-export-button? true
                   show-branch-cells?  false}}]
      (let [themed              (fn [part props] (theme/apply props {:part part} {}))
            column-paths        (spec->headers* columns)
            column-leaf-paths   (reduce (fn [paths p] (remove #(descendant? % p) paths)) column-paths column-paths)
            leaf-column?        (set column-leaf-paths)
            column-widths       (map #(column-header-prop % :width column-width) column-paths)
            max-column-heights  (max-props :row :height column-height column-paths)
            column-depth        (count max-column-heights)
            row-paths           (spec->headers* rows)
            row-leaf-paths      (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths)
            leaf-row?           (set row-leaf-paths)
            row-heights         (map #(column-header-prop % :height row-height) row-paths)
            max-row-widths      (max-props :row :width row-width row-paths)
            row-depth           (count max-row-widths)
            grid-columns        (mapcat (fn [path width]
                                          (if (or show-branch-cells? (leaf-column? path))
                                            [path width]
                                            [path]))
                                        column-paths
                                        column-widths)
            grid-rows           (mapcat (fn [path height]
                                          (if (or show-branch-cells? (leaf-row? path))
                                            [path height]
                                            [path]))
                                        row-paths
                                        row-heights)
            get-header-rows     (fn get-header-rows []
                                  (->> column-paths
                                       (mapcat (fn [path]
                                                 (if (leaf-column? path) [path]
                                                     (repeat
                                                      (dec (header-cross-span path column-paths))
                                                      path))))
                                       (group-by count)
                                       (into (sorted-map))
                                       vals
                                       (map #(map on-export-column-header %))
                                       (map #(concat (repeat row-depth nil) %))))
            get-main-rows       (fn get-main-rows []
                                  (let [ancestors
                                        #(->> % (iterate pop) (take-while seq) reverse)
                                        add-padding
                                        (fn [[paths :as coll]]
                                          (conj coll (repeat (- row-depth (count paths)) nil)))
                                        add-cell-values
                                        (fn [[paths padding]]
                                          (->> column-leaf-paths
                                               (map
                                                #(on-export-cell
                                                  {:column-path %
                                                   :row-path    (last paths)}))
                                               (conj [paths padding])))
                                        render-row-headers
                                        (fn [[paths padding cells]]
                                          (concat (map on-export-row-header paths)
                                                  padding
                                                  cells))]
                                    (->> row-leaf-paths
                                         (map ancestors)
                                         (map vector)
                                         (map add-padding)
                                         (map add-cell-values)
                                         (map render-row-headers))))
            default-on-export   (fn default-on-export [header-rows main-rows]
                                  (->> (concat header-rows main-rows)
                                       (map u/tsv-line)
                                       str/join
                                       u/clipboard-write!))
            control-panel       [controls {:show-export-button? show-export-button?
                                           :hover?              hover?
                                           :on-export           #(try
                                                                   (let [header-rows (get-header-rows)
                                                                         main-rows   (get-main-rows)]
                                                                     ((or on-export default-on-export) header-rows main-rows)
                                                                     (when on-export-success (on-export-success header-rows main-rows)))
                                                                   (catch :default e
                                                                     (when on-export-failure (on-export-failure e))))}]
            grid-container
            [:div {:on-scroll      #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                        (reset! scroll-left (.-scrollLeft (.-target %))))
                   :on-mouse-down  #(do
                                      (.preventDefault %)
                                      (reset! mouse-down-y (.-clientY %))
                                      (reset! mouse-down-x (.-clientX %))
                                      (reset! mouse-y (.-clientY %))
                                      (reset! mouse-x (.-clientX %))
                                      (reset! selecting? true))
                   :on-mouse-up    #(reset! selecting? false)
                   :on-mouse-moved nil
                   :style          {:padding               "0px"
                                    :max-height            max-height
                                    :display               "grid"
                                    :overflow              "auto"
                                    :grid-template-columns (grid-template grid-columns)
                                    :grid-template-rows    (grid-template grid-rows)
                                    :gap                   "0px"
                                    :background-color      "transparent"}}]
            column-header-cells (for [path column-paths
                                      :let [props {:path               path
                                                   :column-paths       column-paths
                                                   :on-resize          on-resize-cell
                                                   :column-header      column-header
                                                   :show-branch-cells? show-branch-cells?
                                                   :leaf?              (leaf-column? path)}]]
                                  ^{:key [::column (or path (gensym))]}
                                  [u/part column-header-wrapper props column-header-wrapper-part])
            row-header-cells    (for [path row-paths
                                      :let [props {:path               path
                                                   :row-paths          row-paths
                                                   :show-branch-cells? show-branch-cells?
                                                   :leaf?              (leaf-row? path)}]]
                                  ^{:key [::row (or path (gensym))]}
                                  [u/part row-header props row-header-wrapper-part])
            header-spacer-cells (for [y (range column-depth)
                                      x (range row-depth)]
                                  ^{:key [::header-spacer x y]}
                                  [:div (themed ::header-spacer
                                          {:style
                                           {:grid-column (inc x)
                                            :grid-row    (inc y)}})])
            cells               (for [column-path column-paths
                                      row-path    row-paths
                                      :let        [leaf? (and (leaf-column? column-path)
                                                              (leaf-row? row-path))
                                                   props {:column-path column-path
                                                          :row-path    row-path
                                                          :cell        cell}]
                                      :when       leaf?]
                                  ^{:key [::cell (or [column-path row-path] (gensym))]}
                                  [u/part cell-wrapper props cell-wrapper-part])
            zebra-stripes       (for [i (filter even? (range (inc column-depth)
                                                             (count row-paths)))]
                                  ^{:key [::zebra-stripe i]}
                                  [:div
                                   (themed ::zebra-stripe
                                     {:style
                                      {:grid-column-start (inc row-depth)
                                       :grid-column-end   "end"
                                       :grid-row          i
                                       :background-color  "cornflowerblue"
                                       :opacity           0.05
                                       :z-index           2}})])
            box-selector        [selection-part
                                 {:selecting?   selecting?
                                  :grid-columns grid-columns
                                  :grid-rows    grid-rows
                                  :mouse-x      mouse-x
                                  :mouse-y      mouse-y
                                  :mouse-down-x mouse-down-x
                                  :mouse-down-y mouse-down-y}]]
        [:div {:on-mouse-enter #(reset! hover? true)
               :on-mouse-leave #(reset! hover? false)
               :style          {:width                 "500px"
                                :height                "500px"
                                :grid-template-columns (grid-template [(px (apply + max-row-widths)) "1280px"])
                                :grid-template-rows    (grid-template ["20px" (px (apply + max-column-heights)) "400px"])
                                :display               "grid"}}
         control-panel
         [:div {:style {:display               "grid"
                        :grid-template-columns (grid-template max-row-widths)
                        :grid-template-rows    (grid-template max-column-heights)}}
          header-spacer-cells]
         [scroll-container {:scroll-left scroll-left
                            :width       "1280px"}
          [:div {:style {:display               "grid"
                         :grid-template-columns (grid-template grid-columns)
                         :grid-template-rows    (grid-template max-column-heights)}}
           column-header-cells]]
         [scroll-container {:scroll-top scroll-top
                            :height     max-height}
          [:div {:style {:display               "grid"
                         :grid-template-columns (grid-template max-row-widths)
                         :grid-template-rows    (grid-template grid-rows)}}
           row-header-cells]]
         (-> grid-container
             (into cells)
             (into zebra-stripes)
             #_(conj box-selector))]))))
