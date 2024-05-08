(ns re-com.simple-v-table
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [reagent.core    :as    reagent]
   [re-com.buttons  :refer [hyperlink row-button]]
   [re-com.config   :refer [include-args-desc?]]
   [re-com.box      :refer [box h-box v-box gap]]
   [re-com.util     :as u :refer [px deref-or-value assoc-in-if-empty ->v position-for-id item-for-id remove-id-item clipboard-write! table->tsv]]
   [re-com.text     :refer [label]]
   [re-com.theme    :as    theme]
   [re-com.validate :refer [vector-of-maps? vector-atom? parts?]]
   [re-com.v-table  :as    v-table]))

(def default-sort-criterion {:keyfn :label :order :asc})

(defn update-sort-criteria
  [criteria new-criterion]
  (let [{:keys [id order] :as new-criterion} (merge default-sort-criterion new-criterion)
        this?          (comp #{id} :id)
        this-criterion (item-for-id id criteria)
        operation      (cond
                         (nil? this-criterion)             :add
                         (= order (:order this-criterion)) :flip
                         :else                             :drop)
        flip           #(update % :order {:asc :desc :desc :asc})]
    (case operation
      :flip (mapv #(cond-> % (this? %) flip) criteria)
      :drop (remove-id-item id criteria)
      :add  (vec (conj criteria (merge default-sort-criterion new-criterion))))))

(defn sort-icon
  [{:keys [size fill]
    :or   {size "24px"
           fill "black"}}]
  [:svg {:width   size
         :height  size
         :viewBox "0 0 24 24"}
   [:path {:fill fill
           :d    "M3 18h6v-2H3v2zM3 6v2h18V6H3zm0 7h12v-2H3v2z"}]])

(defn arrow-down-icon
  [{:keys [size fill]
    :or   {size "24px"
           fill "black"}}]
  [:svg {:width   size
         :height  size
         :viewBox "0 0 24 24"}
   [:path {:fill fill
           :d    "M7 10l5 5 5-5H7z"}]])

(defn arrow-up-icon
  [{:keys [size fill]
    :or   {size "24px"
           fill "black"}}]
  [:svg {:width   size
         :height  size
         :viewBox "0 0 24 24"}
   [:path {:fill fill
           :d    "M7 14l5-5 5 5H7z"}]])

(def align->justify
  {:left :start
   :right :end
   :center :center})

(defn column-header-item
  [& _]
  (fn [{:keys [parts sort-by-column hover? column-header-height]
        {:keys [id row-label-fn width height align header-label sort-by]} :column}]
    (let [header-label             (or header-label (name id))
          sort-by                  (cond (true? sort-by) {} :else sort-by)
          default-sort-by          {:key-fn row-label-fn :comp compare :id id :order :asc}
          ps                       (position-for-id id @sort-by-column)
          {current-order :order}   (item-for-id id @sort-by-column)
          add-criteria!            #(swap! sort-by-column update-sort-criteria (merge default-sort-by sort-by))
          replace-criteria!        #(reset! sort-by-column [(merge default-sort-by sort-by)])
          on-click                 #(if (or (.-shiftKey %) (empty? (remove (clojure.core/comp #{id} :id) @sort-by-column)))
                                      (add-criteria!)
                                      (replace-criteria!))
          justify                  (get align->justify (keyword align) :start)
          multiple-columns-sorted? (> (count @sort-by-column) 1)]
      [v-box
       :children
       [[h-box
         :class    (str "rc-simple-v-table-column-header-item " (get-in parts [:simple-column-header-item :class]))
         :width    (px width)
         :justify  justify
         :align    :center
         :style    (merge
                    {:padding       "0px 12px"
                     :min-height    "24px"
                     :height        (px height)
                     :font-weight   "bold"
                     :white-space   "nowrap"
                     :overflow      "hidden"
                     :text-overflow "ellipsis"}
                    (when sort-by
                      {:cursor "pointer"})
                    (get-in parts [:simple-column-header-item :style]))
         :attr     (merge
                    (when sort-by {:on-click on-click})
                    (get-in parts [:simple-column-header-item :attr]))
         :children [header-label
                    (when sort-by
                      [h-box
                       :min-width "35px"
                       :justify :center
                       :style {:opacity 0.5}
                       :align :center
                       :children
                       (if-not (or @hover? current-order)
                         []
                         [[(case current-order :asc  arrow-up-icon :desc arrow-down-icon sort-icon)
                           {:size (or height "16px")
                            :fill "#777"}]
                          (when ps
                            [label :style {:visibility (when-not multiple-columns-sorted? "hidden")} :label (inc ps)])])])]]]])))

(defn column-header-renderer
  ":column-header-renderer AND :top-left-renderer - Render the table header"
  [{:keys [columns parts sort-by-column column-header-height hover?]}]
  [h-box
   :class    (str "rc-simple-v-table-column-header noselect " (get-in parts [:simple-column-header :class]))
   :style    (merge {:padding     "4px 0px"
                     :overflow    "hidden"
                     :white-space "nowrap"}
                    (get-in parts [:simple-column-header :style]))
   :attr     (merge {:on-mouse-enter (when hover? (handler-fn (reset! hover? true)))
                     :on-mouse-leave (when hover? (handler-fn (reset! hover? false)))
                     :on-click (handler-fn (v-table/show-row-data-on-alt-click columns 0 event))}
                    (get-in parts [:simple-column-header :attr]))
   :children (into []
                   (for [column columns]
                     [column-header-item {:column-header-height column-header-height :column column :parts parts :sort-by-column sort-by-column :hover? hover?}]))])

(defn row-item
  "Render a single row item (column) of a single row"
  [row {:keys [id width height align vertical-align row-label-fn] :as column} cell-style parts]
  [:div
   (merge
    {:class (str "rc-simple-v-table-row-item " (get-in parts [:simple-row-item :class]))
     :style (merge {:display        "inline-block"
                    :padding        (str "0px " "12px")
                    :width          (px width)
                    :height         (px height)
                    :text-align     align
                    :vertical-align vertical-align
                    :white-space    "nowrap"
                    :overflow       "hidden"
                    :text-overflow  "ellipsis"}
                   (get-in parts [:simple-row-item :style])
                   (if (fn? cell-style)
                     (cell-style row column)
                     cell-style))}
    (get-in parts [:simple-row-item :attr]))
   ((or row-label-fn (comp str id)) row)])

(defn row-renderer
  ":row-renderer AND :row-header-renderer: Render a single row of the table data"
  [{:keys [columns on-click-row on-enter-row on-leave-row striped? row-height row-style cell-style parts table-row-line-color row-index row]}]
  (into
   [:div
    (merge
     {:class          (str "rc-simple-v-table-row " (get-in parts [:simple-row :class]))
      :style          (merge {:padding     "4px 0px"
                              :overflow    "hidden"
                              :white-space "nowrap"
                              :height      (px row-height)
                              :border-top  (str "1px solid " table-row-line-color)
                              :cursor      (when on-click-row "pointer")}
                             (when (and striped? (odd? row-index))
                               {:background-color "#f2f2f2"})
                             (get-in parts [:simple-row :style])
                             (if (fn? row-style)
                               (row-style row)
                               row-style))
      :on-click       (handler-fn (do (v-table/show-row-data-on-alt-click row row-index event)
                                      (when on-click-row (on-click-row row-index))))
      :on-mouse-enter (when on-enter-row (handler-fn (on-enter-row row-index)))
      :on-mouse-leave (when on-leave-row (handler-fn (on-leave-row row-index)))}
     (get-in parts [:simple-row :attr]))]
   (for [column columns]
     [row-item row column cell-style parts])))

(def simple-v-table-exclusive-parts-desc
  (when include-args-desc?
    [{:name :simple-wrapper             :level 0 :class "rc-simple-v-table-wrapper"             :impl "[simple-v-table]" :notes "Outer container of the simple-v-table"}
     {:name :simple-column-header       :level 5 :class "rc-simple-v-table-column-header"       :impl "[:div]"           :notes "Simple-v-table's container for column headers (placed under v-table's :column-header-content/:top-left)"}
     {:name :simple-column-header-item  :level 6 :class "rc-simple-v-table-column-header-item"  :impl "[:div]"           :notes "Individual column header item/cell components"}
     {:name :simple-row                 :level 5 :class "rc-simple-v-table-row"                 :impl "[:div]"           :notes "Simple-v-table's container for rows (placed under v-table's :row-content/:row-header-content)"}
     {:name :simple-row-item            :level 6 :class "rc-simple-v-table-row-item"            :impl "[:div]"           :notes "Individual row item/cell components"}]))

(def simple-v-table-exclusive-parts
  (when include-args-desc?
    (-> (map :name simple-v-table-exclusive-parts-desc) set)))

(def simple-v-table-parts-desc
  (when include-args-desc?
    (into
     simple-v-table-exclusive-parts-desc
     (map #(update % :level inc) v-table/v-table-parts-desc))))

(def simple-v-table-parts
  (when include-args-desc?
    (-> (map :name simple-v-table-parts-desc) set)))

(def simple-v-table-args-desc
  (when include-args-desc?
    [{:name :model                     :required true                     :type "r/atom containing vec of maps"    :validate-fn vector-atom?                   :description "one element for each row in the table."}
     {:name :columns                   :required true                     :type "vector of maps"                   :validate-fn vector-of-maps?                :description [:span "one element for each column in the table. Must contain " [:code ":id"] "," [:code ":header-label"] "," [:code ":row-label-fn"] "," [:code ":width"] ", and " [:code ":height"] ". Optionally contains " [:code ":sort-by"] ", " [:code ":align"] " and " [:code ":vertical-align"] ". " [:code ":sort-by"] " can be " [:code "true"] " or a map optionally containing " [:code ":key-fn"] " and " [:code ":comp"] " ala " [:code "cljs.core/sort-by"] "."]}
     {:name :fixed-column-count        :required false :default 0         :type "integer"                          :validate-fn number?                        :description "the number of fixed (non-scrolling) columns on the left."}
     {:name :fixed-column-border-color :required false :default "#BBBEC0" :type "string"                           :validate-fn string?                        :description [:span "The CSS color of the horizontal border between the fixed columns on the left, and the other columns on the right. " [:code ":fixed-column-count"] " must be > 0 to be visible."]}
     {:name :column-header-height      :required false :default 31        :type "integer"                          :validate-fn number?                        :description [:span "px height of the column header section. Typically, equals " [:code ":row-height"] " * number-of-column-header-rows."]}
     {:name :column-header-renderer    :required false                    :type "cols parts sort-by-col -> hiccup" :validate-fn ifn?                           :description [:span "You can provide a renderer function to override the inbuilt renderer for the columns headings"]}
     {:name :show-export-button?       :required false :default false     :type "boolean" :description [:span "When non-nil, adds a hiccup of " [:code ":export-button-render"] " to the component tree."]}
     {:name :on-export                 :required false                    :type "columns, sorted-rows -> nil"             :validate-fn ifn?                           :description "Called whenever the export button is clicked."}
     {:name :export-button-renderer    :required false                    :type "{:keys [columns rows on-export]} -> hiccup" :validate-fn ifn?                 :description [:span "Pass a component function to override the built-in export button. Declares a hiccup of your component in the " [:code ":top-right-renderer"] "position of the underlying " [:code "v-table"] "."]}
     {:name :max-width                 :required false                    :type "string"                           :validate-fn string?                        :description "standard CSS max-width setting of the entire table. Literally constrains the table to the given width so that if the table is wider than this it will add scrollbars. Ignored if value is larger than the combined width of all the columns and table padding."}
     {:name :max-rows                  :required false                    :type "integer"                          :validate-fn number?                        :description "The maximum number of rows to display in the table without scrolling. If not provided will take up all available vertical space."}
     {:name :row-height                :required false :default 31        :type "integer"                          :validate-fn number?                        :description "px height of each row."}
     {:name :table-padding             :required false :default 19        :type "integer"                          :validate-fn number?                        :description "Padding in pixels for the entire table."}
     {:name :table-row-line-color      :required false :default "#EAEEF1" :type "string"                           :validate-fn string?                        :description "The CSS color of the lines between rows."}
     {:name :on-click-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user clicks a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :on-enter-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user's mouse pointer enters a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :on-leave-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user's mouse pointer leaves a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :striped?                  :required false :default false     :type "boolean"                                                                      :description "when true, adds zebra-striping to the table's rows."}
     {:name :row-style                 :required false                    :type "map | function"                   :validate-fn #(or (fn? %) (map? %))         :description "Style each row container either statically by passing a CSS map or dynamically by passing a function which receives the data for that row."}
     {:name :cell-style                :required false                    :type "map | function"                   :validate-fn #(or (fn? %) (map? %))         :description "Style each cell in a row either statically by passing a CSS map or dynamically by passing a function which receives the data for that row and the cell definition from the columns arg."}
     {:name :class                     :required false                    :type "string"                           :validate-fn string?                        :description "CSS class names, space separated (applies to the outer container)."}
     {:name :parts                     :required false                    :type "map"                              :validate-fn (parts? simple-v-table-parts)  :description "See Parts section below."}
     {:name :src                       :required false                    :type "map"                              :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as                  :required false                    :type "map"                              :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn criteria-compare [a b {:keys [key-fn comp-fn order]
                             :or {key-fn :label order :asc comp-fn compare}}]
  (cond-> (comp-fn (key-fn a) (key-fn b))
    (= :desc order) -))

(defn multi-comparator [criteria]
  (fn [a b]
    (or (->> criteria
             (map (partial criteria-compare a b))
             (remove zero?)
             (first)) 0)))

(defn clipboard-export-button [{:keys [columns rows on-export]}]
  [row-button :src (at)
   :md-icon-name    "zmdi zmdi-copy"
   :mouse-over-row? true
   :tooltip         (str "Copy " (count rows) " rows, " (count columns) " columns to clipboard.")
   :on-click        on-export])

(def default-args {:column-header-renderer    column-header-renderer
                   :export-button-renderer    clipboard-export-button
                   :fixed-column-border-color "#BBBEC0"
                   :fixed-column-count        0
                   :on-export                 (fn [{:keys [columns rows]}]
                                                (-> (remove (comp false? :export?) columns)
                                                    (table->tsv rows)
                                                    clipboard-write!))
                   :row-height                31
                   :show-export-button?       false
                   :table-padding             19
                   :table-row-line-color      "#EAEEF1"
                   :column-header-height      31})

(defn simple-v-table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [src] :as static-args}]
  (or
   (validate-args-macro simple-v-table-args-desc static-args)
   (let [sort-by-column (reagent/atom nil)
         header-hover?  (reagent/atom nil)]
     (fn simple-v-table-render
       [& {:as dynamic-args}]
       (or
        (validate-args-macro simple-v-table-args-desc dynamic-args)
        (let [{:keys [model columns fixed-column-count fixed-column-border-color
                      column-header-height column-header-renderer
                      max-width max-rows row-height table-padding table-row-line-color
                      on-click-row on-enter-row on-leave-row
                      show-export-button? on-export export-button-renderer
                      striped? row-style class parts src debug-as]
               :as   args}           (merge default-args dynamic-args)
              fcc-bounded            (min fixed-column-count (count columns))
              fixed-cols             (subvec columns 0 fcc-bounded)
              content-cols           (subvec columns fcc-bounded (count columns))
              fixed-content-width    (->> fixed-cols (map #(:width % 25)) (reduce + 0))
              content-width          (->> content-cols (map #(:width % 25)) (reduce + 0))
              table-border-style     (str "1px solid " table-row-line-color)
              fixed-col-border-style (str "1px solid " fixed-column-border-color)
              actual-table-width     (+ fixed-content-width
                                        (when (pos? fixed-column-count) 1) ;; 1 border width (for fixed-col-border)
                                        content-width
                                        v-table/scrollbar-tot-thick
                                        (* 2 table-padding)
                                        2)]  ;; 2 border widths
          [box
           :src      src
           :debug-as (or debug-as (reflect-current-component))
           :class    (str "rc-simple-v-table-wrapper " (get-in parts [:simple-wrapper :class]))
           :style    (merge {;; :flex setting
                             ;; When max-rows is being used:
                             ;;  - "0 1 auto" allows shrinking within parent but not growing (to prevent vertical spill)
                             ;; Otherwise:
                             ;;  - "100%" used instead of 1 to resolve conflicts when simple-v-table is the anchor of a popover (e.g. the periodic table demo)
                             :flex             (if max-rows "0 1 auto" "100%")
                             :background-color "white" ;; DEBUG "salmon"
                             :padding          (px table-padding)
                             :max-width        (or max-width (px actual-table-width)) ;; Removing actual-table-width would make the table stretch to the end of the page
                             :border           table-border-style
                             :border-radius    "3px"}
                            (get-in parts [:simple-wrapper :style]))
           :attr     (get-in parts [:simple-wrapper :attr])
           :child    [v-table/v-table
                      :src                     (at)
                      :model                   model
                      :sort-comp               (multi-comparator (->v @sort-by-column))
                        ;; ===== Column header (section 4)
                      :column-header-renderer  #(do [column-header-renderer (into args {:columns content-cols :hover? header-hover? :sort-by-column sort-by-column})])
                      :column-header-height    column-header-height
                        ;; ===== Row header (section 2)
                      :row-header-renderer     #(fn [i row] [row-renderer (into args {:columns fixed-cols :row row :row-index i})])
                        ;; ===== Rows (section 5)
                      :row-renderer            #(fn [i row] [row-renderer (into args {:columns content-cols :row row :row-index i})])
                      :row-content-width       content-width
                      :row-height              row-height
                      :max-row-viewport-height (when max-rows (* max-rows row-height))
                      ;:max-width               (px (or max-width (+ fixed-content-width content-width v-table/scrollbar-tot-thick))) ; :max-width handled by enclosing parent above
                        ;; ===== Corners (section 1, 3)
                      :top-left-renderer       (fn [i row] [column-header-renderer {:columns        fixed-cols
                                                                                   :hover?         header-hover?
                                                                                   :parts          parts
                                                                                   :sort-by-column sort-by-column}]) ;; Used when there are fixed columns
                      :top-right-renderer      (when show-export-button?
                                                 #(let [rows           (deref-or-value model)
                                                        columns        (deref-or-value columns)
                                                        sort-by-column (deref-or-value sort-by-column)]
                                                    [export-button-renderer {:rows      rows
                                                                             :columns   columns
                                                                             :on-export (fn [_] (on-export {:columns columns
                                                                                                           :rows    (cond->> rows
                                                                                                                      sort-by-column (sort (multi-comparator (->v sort-by-column))))}))}]))
                      ;; ===== Styling
                      :class                   class
                      :parts                   (cond-> {:wrapper {:style {:font-size "13px"
                                                                          :cursor "default"}}}
                                                 (pos? fixed-column-count) (theme/merge-props {:top-left    {:style {:border-right fixed-col-border-style}}
                                                                                               :row-headers {:style {:border-right fixed-col-border-style}}})
                                                 :do                       (theme/merge-props (apply dissoc parts simple-v-table-exclusive-parts)))]]))))))

(defn nested-column
  [& _]
  (fn [{:keys [parts sort-by-column hover? column-header-height rows]
        {:keys [id row-label-fn width height align header-label sort-by]} :column}]
    (let [header-label             (or header-label (name id))
          sort-by                  (cond (true? sort-by) {} :else sort-by)
          default-sort-by          {:key-fn row-label-fn :comp compare :id id :order :asc}
          ps                       (position-for-id id @sort-by-column)
          {current-order :order}   (item-for-id id @sort-by-column)
          add-criteria!            #(swap! sort-by-column update-sort-criteria (merge default-sort-by sort-by))
          replace-criteria!        #(reset! sort-by-column [(merge default-sort-by sort-by)])
          on-click                 #(if (or (.-shiftKey %) (empty? (remove (clojure.core/comp #{id} :id) @sort-by-column)))
                                      (add-criteria!)
                                      (replace-criteria!))
          justify                  (get align->justify (keyword align) :start)
          multiple-columns-sorted? (> (count @sort-by-column) 1)]
      [v-box
       :children
       (-> [[h-box
             :class    (str "rc-simple-v-table-column-header-item " (get-in parts [:simple-column-header-item :class]))
             :width    (px width)
             :justify  justify
             :align    :center
             :style    (merge
                        {:padding       "0px 12px"
                         :min-height    "24px"
                         :height        (px height)
                         :font-weight   "bold"
                         :white-space   "nowrap"
                         :overflow      "hidden"
                         :text-overflow "ellipsis"}
                        (when sort-by
                          {:cursor "pointer"})
                        (get-in parts [:simple-column-header-item :style]))
             :attr     (merge
                        (when sort-by {:on-click on-click})
                        (get-in parts [:simple-column-header-item :attr]))
             :children [header-label
                        (when sort-by
                          [h-box
                           :class (str "rc-simple-v-table-column-header-sort-label " (when current-order "rc-simple-v-table-column-header-sort-active"))
                           :min-width "35px"
                           :style (when current-order {:opacity 0.3})
                           :justify :center
                           :align :center
                           :children
                           (if-not (or hover? current-order)
                             []
                             [[(case current-order :asc  arrow-up-icon :desc arrow-down-icon sort-icon)
                               {:size (or height "16px")
                                :fill "#777"}]
                              (when ps
                                [label :style {:visibility (when-not multiple-columns-sorted? "hidden")} :label (inc ps)])])])]]]
           (into [[:div (str rows)]]))])))

(defn column-node [{:keys [column column-model]}]
  [:div "HI"])

(defn descendant? [group-a group-b]
  (or (empty? group-a)
      (= group-a (vec (take (count group-a) group-b)))))

(defn nested-columns
  ":column-header-renderer AND :top-left-renderer - Render the table header"
  [{:keys [columns parts sort-by-column column-header-height hover? column-model ancestry]
    :as   args}]
  (let [level            (count ancestry)
        [this-level next-level] (->> (group-by (comp count key) @column-model)
                                     ((juxt #(get % level) #(get % (inc level)))))
        descendants (filter #(descendant? ancestry (key %)) next-level)]
    [v-box
     :children
     [[:div {:style {:font-size 7}} (str ancestry)]
      [h-box
       :children
       (for [[path state] descendants]
         [nested-columns (merge args {:ancestry path})])]]]))

#_(into []
        (for [column columns]
          [nested-column {:column-header-height column-header-height
                          :column column
                          :parts parts
                          :sort-by-column sort-by-column
                          :hover? @hover?}]))

(defn tree-v-table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
  x   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [src] :as static-args}]
  (or
   #_(validate-args-macro simple-v-table-args-desc static-args)
   (let [sort-by-column (reagent/atom nil)
         header-hover?  (reagent/atom nil)]
     (fn tree-v-table-render
       [& {:as dynamic-args}]
       (or
        #_(validate-args-macro simple-v-table-args-desc dynamic-args)
        (let [{:keys [model columns fixed-column-count fixed-column-border-color
                      column-header-height column-header-renderer
                      max-width max-rows row-height table-padding table-row-line-color
                      on-click-row on-enter-row on-leave-row
                      show-export-button? on-export export-button-renderer
                      striped? row-style class parts src debug-as]
               :as   args}           (merge default-args dynamic-args)
              fcc-bounded            (min fixed-column-count (count columns))
              fixed-cols             (subvec columns 0 fcc-bounded)
              content-cols           (subvec columns fcc-bounded (count columns))
              fixed-content-width    (->> fixed-cols (map #(:width % 25)) (reduce + 0))
              content-width          (->> content-cols (map #(:width % 25)) (reduce + 0))
              table-border-style     (str "1px solid " table-row-line-color)
              fixed-col-border-style (str "1px solid " fixed-column-border-color)
              actual-table-width     (+ fixed-content-width
                                        (when (pos? fixed-column-count) 1) ;; 1 border width (for fixed-col-border)
                                        content-width
                                        v-table/scrollbar-tot-thick
                                        (* 2 table-padding)
                                        2)]  ;; 2 border widths
          [box
           :src      src
           :debug-as (or debug-as (reflect-current-component))
           :class    (str "rc-simple-v-table-wrapper " (get-in parts [:simple-wrapper :class]))
           :style    (merge {:flex             (if max-rows "0 1 auto" "100%")
                             :background-color "white"
                             :padding          (px table-padding)
                             :max-width        (or max-width (px actual-table-width)) ;; Removing actual-table-width would make the table stretch to the end of the page
                             :border           table-border-style
                             :border-radius    "3px"}
                            (get-in parts [:simple-wrapper :style]))
           :attr     (get-in parts [:simple-wrapper :attr])
           :child    [v-table/v-table
                      :src                     (at)
                      :model                   model
                      :sort-comp               (multi-comparator (->v @sort-by-column))
                        ;; ===== Column header (section 4)
                      :column-header-renderer  #(do [nested-columns (into args {:columns content-cols :hover? header-hover? :sort-by-column sort-by-column})])
                      :column-header-height    column-header-height
                        ;; ===== Row header (section 2)
                      :row-header-renderer     #(fn [i row] [row-renderer (into args {:columns fixed-cols :row row :row-index i})])
                        ;; ===== Rows (section 5)
                      :row-renderer            (constantly nil)
                      :row-content-width       content-width
                      :row-height              row-height
                      :max-row-viewport-height (when max-rows (* max-rows row-height))
                      ;:max-width               (px (or max-width (+ fixed-content-width content-width v-table/scrollbar-tot-thick))) ; :max-width handled by enclosing parent above
                        ;; ===== Corners (section 1, 3)
                      :top-left-renderer       (fn [i row] [column-header-renderer {:columns        fixed-cols
                                                                                    :parts          parts
                                                                                    :sort-by-column sort-by-column}]) ;; Used when there are fixed columns
                      :top-right-renderer      (when show-export-button?
                                                 #(let [rows    (deref-or-value model)
                                                        columns (deref-or-value columns)
                                                        sort-by-column (deref-or-value sort-by-column)]
                                                    [export-button-renderer {:rows rows
                                                                             :columns columns
                                                                             :on-export (fn [_] (on-export {:columns columns
                                                                                                            :rows (cond->> rows
                                                                                                                    sort-by-column (sort (multi-comparator (->v sort-by-column))))}))}]))
                      ;; ===== Styling
                      :class                   class
                      :parts                   (cond-> {:wrapper {:style {:font-size "13px"
                                                                          :cursor "default"}}}
                                                 (pos? fixed-column-count) (theme/merge-props {:top-left    {:style {:border-right fixed-col-border-style}}
                                                                                               :row-headers {:style {:border-right fixed-col-border-style}}})
                                                 :do                       (theme/merge-props (apply dissoc parts simple-v-table-exclusive-parts)))]]))))))
