(ns re-com.pivot
  (:require
   [clojure.string :as str]
   [re-com.util :as u :refer [px]]
   [reagent.core :as r]
   [re-com.theme :as theme]
   [re-com.box :as box]
   [re-com.buttons :as buttons]))

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
(assert (= (spec->headers [[:a [:b] [:c]]])
           [[:a] [:a :b] [:a :c]]))
(assert (= (spec->headers [[:a :b [:c]]])
           [[:a] [:b] [:b :c]]))

(defn header-cross-span [group-path all-paths]
  (->> all-paths
       (filter (partial descendant? group-path))
       count
       inc))

(defn header-main-span [group-path all-paths]
  (->> all-paths (map count) (apply max) (+ (- (count group-path))) inc))

(defn drag-button [& {:as args}]
  (let [dragging?    (r/atom false)
        mouse-down-x (r/atom 0)
        last-drag-x  (r/atom 0)
        drag-x       (r/atom 0)
        hovering?    (r/atom nil)]
    (fn [& {:keys [on-resize column-index path]}]
      [:<>
       [:div {:on-mouse-enter #(reset! hovering? true)
                   :on-mouse-leave #(reset! hovering? false)
                   :on-mouse-down #(do (reset! dragging?    true)
                                       (reset! mouse-down-x (.-clientX %))
                                       (reset! drag-x       (.-clientX %))
                                       (reset! last-drag-x       (.-clientX %)))
                   :style         {:position         "absolute"
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
                :style         {:position         "fixed"
                                :z-index          99999
                                :width            "100%"
                                :height           "100%"
                                :top              0
                                :left             0
                                :font-size        100}}])])))

(defn path->grid-line-name [path]
  (str "line__" (hash path) "-start"))

(defn grid-template [tokens]
  (let [rf (fn [s group]
             (str s " "
                  (if (number? (first group))
                    (str/join " " (map px group))
                    (str "[" (str/join " " (map path->grid-line-name group)) "]"))))]
    (->> tokens
         (partition-by number?)
         (reduce rf ""))))

(defn cell-part [{:keys [column-path row-path]}]
  nil)

(defn cell-wrapper-part [{:keys [column-path row-path cell]
                  :as args}]
  [:div {:style {:grid-column (path->grid-line-name column-path)
                 :grid-row (path->grid-line-name row-path)
                 :background-color "#fff"
                 :padding "3px"
                 :text-align "center"
                 :border "0.5px solid #ccc"
                   :position "relative"}}
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
     [drag-button {:on-resize on-resize :path path}]]))

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
   :height "20px"
   :width "100%"
   :children
   [[box/gap :size "1"]
    (when (and show-export-button? @hover?)
      [buttons/row-button
       :md-icon-name    "zmdi zmdi-copy"
       :mouse-over-row? true
       :tooltip         (str "Copy table to clipboard.")
       :on-click on-export])]])

(defn grid [& {:keys [columns rows cell
                      cell-wrapper column-header-wrapper column-header row-header
                      show-branch-cells?
                      max-height column-width column-height row-width row-height]
               :or   {column-height      30
                      column-width       60
                      row-width          100
                      row-height         30
                      show-branch-cells? false}}]
  (let [column-state       (r/atom {})
        row-state          (r/atom {})
        hover?             (r/atom false)
        column-header-prop (fn [path k & [default]]
                             (or (some-> @column-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        row-header-prop    (fn [path k & [default]]
                             (or (some-> @row-state (get path) (get k))
                                 (get (meta (last path)) k)
                                 (get (last path) k)
                                 default))
        max-props          (fn [k default paths]
                             (->> paths
                                  (group-by level)
                                  (sort-by key)
                                  (map val)
                                  (map (fn [paths] (apply max (map #(column-header-prop % k default) paths))))))
        on-resize-cell     (fn [{:keys [distance path]}]
                             (swap! column-state update-in [path :width]
                                    #(+ distance (or % (column-header-prop path :width column-width)))))]
    (fn [& {:keys [columns rows cell
                   cell-wrapper column-header-wrapper column-header row-header
                   show-branch-cells?
                   max-height column-width column-height row-width row-height
                   show-export-button? on-export on-export-cell on-export-column-header on-export-row-header]
            :or   {column-height      30
                   column-width       60
                   row-width          100
                   row-height         30
                   show-export-button? true
                   show-branch-cells? false}}]
      (let [column-paths          (spec->headers* columns)
            column-leaf-paths     (reduce (fn [paths p] (remove #(descendant? % p) paths)) column-paths column-paths)
            leaf-column?          (set column-leaf-paths)
            column-widths         (map #(column-header-prop % :width column-width) column-paths)
            max-column-heights    (max-props :height column-height column-paths)
            column-depth          (count max-column-heights)
            row-paths             (spec->headers* rows)
            row-leaf-paths        (reduce (fn [paths p] (remove #(descendant? % p) paths)) row-paths row-paths)
            leaf-row?             (set row-leaf-paths)
            row-heights           (map #(column-header-prop % :height row-height) row-paths)
            max-row-widths        (max-props :width row-width row-paths)
            row-depth             (count max-row-widths)
            grid-template-columns (->> (mapcat
                                        (fn [path width]
                                          (cond-> [path]
                                            (or show-branch-cells?
                                                (leaf-column? path)) (conj width)))
                                        column-paths column-widths)
                                       (concat max-row-widths)
                                       grid-template)
            grid-template-rows    (->> (mapcat
                                        (fn [path height]
                                          (cond-> [path]
                                            (or show-branch-cells?
                                                (leaf-row? path)) (conj height)))
                                        row-paths row-heights)
                                       (concat max-column-heights)
                                       grid-template)
            get-header-rows       (fn get-header-rows []
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
            get-main-rows         (fn get-main-rows []
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
            default-on-export     (fn default-on-export [header-rows main-rows]
                                    (->> (concat header-rows main-rows)
                                         (map u/tsv-line)
                                         str/join
                                         u/clipboard-write!))]
        [:div {:on-mouse-enter #(reset! hover? true)
               :on-mouse-leave #(reset! hover? false)
               :style          {:width "fit-content"}}
         [controls {:show-export-button? show-export-button?
                    :hover?              hover?
                    :on-export           #((or on-export default-on-export)
                                           (get-header-rows) (get-main-rows))}]
        [:div {:style {:padding               "0px"
                       :max-height            max-height
                       :display "grid"
                       :overflow              "auto"
                       :grid-template-columns grid-template-columns
                       :grid-template-rows    grid-template-rows
                       :gap                   "0px"
                       :background-color      "transparent"}}
         (for [path column-paths
               :let [props {:path          path
                            :column-paths  column-paths
                            :on-resize     on-resize-cell
                            :column-header column-header
                            :show-branch-cells? show-branch-cells?
                             :leaf?              (leaf-column? path)}]]
           ^{:key [::column (or path (gensym))]}
           [u/part column-header-wrapper props column-header-wrapper-part])
         (for [path row-paths
               :let [props {:path               path
                            :row-paths          row-paths
                            :show-branch-cells? show-branch-cells?
                            :leaf?              (leaf-row? path)}]]
           ^{:key [::row (or path (gensym))]}
            [u/part row-header props row-header-wrapper-part])
         (for [column-path column-paths
               row-path    row-paths
               :let        [props {:column-path column-path
             :row-path    row-path
                                   :cell        cell}]]
           ^{:key [::cell (or [column-path row-path] (gensym))]}
            [u/part cell-wrapper props cell-wrapper-part])]]))))
