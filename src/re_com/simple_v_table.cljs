(ns re-com.simple-v-table
  (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [reagent.core    :as    reagent]
    [re-com.config   :refer [include-args-desc?]]
    [re-com.box      :refer [box]]
    [re-com.util     :refer [px deref-or-value assoc-in-if-empty]]
    [re-com.validate :refer [vector-of-maps? vector-atom? parts?]]
    [re-com.v-table  :as    v-table]))


(defn swap!-sort-by-column
  [{:keys [key-fn order]} new-key-fn new-comp]
  (if (= key-fn new-key-fn)
    (if (= :asc order)
      {:key-fn key-fn
       :comp   new-comp
       :order  :desc}
      nil)
    {:key-fn new-key-fn
     :comp   new-comp
     :order :asc}))


(defn sort-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M3 18h6v-2H3v2zM3 6v2h18V6H3zm0 7h12v-2H3v2z"}]])

(defn arrow-down-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M7 10l5 5 5-5H7z"}]])


(defn arrow-up-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M7 14l5-5 5 5H7z"}]])


(defn column-header-item
  [{:keys [id row-label-fn width height align vertical-align header-label sort-by] :as column} parts sort-by-column]
  (let [{:keys [key-fn comp] :or {key-fn row-label-fn comp compare}} sort-by
        {current-key-fn :key-fn order :order} @sort-by-column]
    (let [on-click #(swap! sort-by-column swap!-sort-by-column key-fn comp)]
      [:<>
       [:div
        (merge
          {:class (str "rc-simple-v-table-column-header-item " (get-in parts [:simple-column-header-item :class]))
           :style (merge {:display        "inline-block"
                          :padding        "0px 12px"
                          :width          (px (if sort-by (- width 24) width))
                          :min-height     "24px"
                          :height         (px height)
                          :font-weight    "bold"
                          :text-align     align
                          :vertical-align vertical-align
                          :white-space    "nowrap"
                          :overflow       "hidden"
                          :text-overflow  "ellipsis"}
                         (when sort-by
                           {:cursor "pointer"})
                         (get-in parts [:simple-column-header-item :style]))}
          (when sort-by
            {:on-click on-click})
          (get-in parts [:simple-column-header-item :attr]))
        header-label]
       (when sort-by
         [:div
          {:style {:cursor         "pointer"
                   :display        "inline-block"
                   :width          "24px"
                   :height         "24px"
                   :text-align     align
                   :vertical-align vertical-align}
           :on-click on-click}
          (if-not (= current-key-fn key-fn)
            [sort-icon]
            (if (= order :desc)
              [arrow-down-icon]
              [arrow-up-icon]))])])))


(defn column-header-renderer
  ":column-header-renderer AND :top-left-renderer - Render the table header"
  [columns parts sort-by-column]
  (into
    [:div
     (merge
       {:class    (str "rc-simple-v-table-column-header noselect " (get-in parts [:simple-column-header :class]))
        :style    (merge {:padding     "4px 0px"
                          :overflow    "hidden"
                          :white-space "nowrap"}
                         (get-in parts [:simple-column-header :style]))
        :on-click (handler-fn (v-table/show-row-data-on-alt-click columns 0 event))}
       (get-in parts [:simple-column-header :attr]))]
    (for [column columns]
      [column-header-item column parts sort-by-column])))


(defn row-item
  "Render a single row item (column) of a single row"
  [row {:keys [width height align vertical-align row-label-fn] :as column} cell-style parts]
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
   (row-label-fn row)])


(defn row-renderer
  ":row-renderer AND :row-header-renderer: Render a single row of the table data"
  [columns on-click-row on-enter-row on-leave-row row-height row-style cell-style parts table-row-line-color row-index row]
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
     {:name :column-header-renderer    :required false                    :type "cols parts sort-by-col -> hiccup" :validate-fn ifn?                           :description "You can provide a renderer function to override the inbuilt renderer for the columns headings"}
     {:name :max-width                 :required false                    :type "string"                           :validate-fn string?                        :description "standard CSS max-width setting of the entire table. Literally constrains the table to the given width so that if the table is wider than this it will add scrollbars. Ignored if value is larger than the combined width of all the columns and table padding."}
     {:name :max-rows                  :required false                    :type "integer"                          :validate-fn number?                        :description "The maximum number of rows to display in the table without scrolling. If not provided will take up all available vertical space."}
     {:name :row-height                :required false :default 31        :type "integer"                          :validate-fn number?                        :description "px height of each row."}
     {:name :table-padding             :required false :default 19        :type "integer"                          :validate-fn number?                        :description "Padding in pixels for the entire table."}
     {:name :table-row-line-color      :required false :default "#EAEEF1" :type "string"                           :validate-fn string?                        :description "The CSS color of the lines between rows."}
     {:name :on-click-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user clicks a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :on-enter-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user's mouse pointer enters a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :on-leave-row              :required false                    :type "function"                         :validate-fn ifn?                           :description "This function is called when the user's mouse pointer leaves a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
     {:name :row-style                 :required false                    :type "map | function"                   :validate-fn #(or (fn? %) (map? %))         :description "Style each row container either statically by passing a CSS map or dynamically by passing a function which receives the data for that row."}
     {:name :cell-style                :required false                    :type "map | function"                   :validate-fn #(or (fn? %) (map? %))         :description "Style each cell in a row either statically by passing a CSS map or dynamically by passing a function which receives the data for that row and the cell definition from the columns arg."}
     {:name :class                     :required false                    :type "string"                           :validate-fn string?                        :description "CSS class names, space separated (applies to the outer container)."}
     {:name :parts                     :required false                    :type "map"                              :validate-fn (parts? simple-v-table-parts)  :description "See Parts section below."}
     {:name :src                       :required false                    :type "map"                              :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as                  :required false                    :type "map"                              :validate-fn map?                           :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))


(defn simple-v-table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [src] :as args}]
  (or
    (validate-args-macro simple-v-table-args-desc args)
    (let [sort-by-column         (reagent/atom nil)]
      (fn simple-v-table-render
        [& {:keys [model columns fixed-column-count fixed-column-border-color column-header-height column-header-renderer
                   max-width max-rows row-height table-padding table-row-line-color on-click-row on-enter-row on-leave-row
                   row-style cell-style class parts src debug-as]

            :or   {column-header-height      31
                   row-height                31
                   fixed-column-count        0
                   table-padding             19
                   table-row-line-color      "#EAEEF1"
                   fixed-column-border-color "#BBBEC0"
                   column-header-renderer    column-header-renderer}
            :as   args}]
        (or
          (validate-args-macro simple-v-table-args-desc args)
          (let [fcc-bounded            (min fixed-column-count (count columns))
                fixed-cols             (subvec columns 0 fcc-bounded)
                content-cols           (subvec columns fcc-bounded (count columns))
                fixed-content-width    (->> fixed-cols (map :width) (reduce + 0))
                content-width          (->> content-cols (map :width) (reduce + 0))
                table-border-style     (str "1px solid " table-row-line-color)
                fixed-col-border-style (str "1px solid " fixed-column-border-color)
                actual-table-width     (+ fixed-content-width
                                          (when (pos? fixed-column-count) 1) ;; 1 border width (for fixed-col-border)
                                          content-width
                                          v-table/scrollbar-tot-thick
                                          (* 2 table-padding)
                                          2) ;; 2 border widths
                internal-model         (reagent/track
                                         (fn []
                                           (if-let [{:keys [key-fn comp order] :or {comp compare}} @sort-by-column]
                                             (do
                                               (let [sorted (sort-by key-fn comp (deref-or-value model))]
                                                 (if (= order :desc)
                                                   (vec (reverse sorted))
                                                   (vec sorted))))
                                             (deref-or-value model))))]
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
                        :model                   internal-model

                        ;; ===== Column header (section 4)
                        :column-header-renderer  (partial column-header-renderer content-cols parts sort-by-column)
                        :column-header-height    column-header-height

                        ;; ===== Row header (section 2)
                        :row-header-renderer     (partial row-renderer fixed-cols on-click-row on-enter-row on-leave-row row-height row-style cell-style parts table-row-line-color)

                        ;; ===== Rows (section 5)
                        :row-renderer            (partial row-renderer content-cols on-click-row on-enter-row on-leave-row row-height row-style cell-style parts table-row-line-color)
                        :row-content-width       content-width
                        :row-height              row-height
                        :max-row-viewport-height (when max-rows (* max-rows row-height))
                        ;:max-width               (px (or max-width (+ fixed-content-width content-width v-table/scrollbar-tot-thick))) ; :max-width handled by enclosing parent above

                        ;; ===== Corners (section 1)
                        :top-left-renderer       (partial column-header-renderer fixed-cols parts sort-by-column) ;; Used when there are fixed columns

                        ;; ===== Styling
                        :class                   class
                        :parts                   (cond-> (->
                                                           ;; Remove the parts that are exclusive to simple-v-table, or v-table part
                                                           ;; validation will fail:
                                                           (apply dissoc (into [parts] simple-v-table-exclusive-parts))
                                                           ;; Inject styles, if not set already, into parts. merge is not safe as it is not
                                                           ;; recursive so e.g. simply setting :attr would delete :style map.

                                                           ;(assoc-in-if-empty [:wrapper :style :background-color] "antiquewhite") ;; DEBUG
                                                           (assoc-in-if-empty [:wrapper :style :font-size] "13px")
                                                           (assoc-in-if-empty [:wrapper :style :cursor] "default"))

                                                         (pos? fixed-column-count)
                                                         (->
                                                           (assoc-in-if-empty [:top-left :style :border-right] fixed-col-border-style)
                                                           (assoc-in-if-empty [:row-headers :style :border-right] fixed-col-border-style)))]]))))))
