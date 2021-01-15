(ns re-com.basic-v-table
  (:require-macros  [re-com.core :refer [handler-fn]])
  (:require
    [re-com.box     :as box]
    [re-com.util    :refer [px]]
    [re-com.validate :refer [vector-or-atom? map-or-atom? parts?]]
    [re-com.v-table :as v-table]))


(defn render-header
  "Render the table header"
  [cols header-style]
  (vec (concat
         [:div.bvt-header-wrapper
          {:style    {:padding     "4px 0px"
                      :overflow    "hidden"
                      :white-space "nowrap"}
           :on-click (handler-fn (v-table/show-row-data-on-alt-click cols 0 event))}]
         (for [col cols]
           ^{:key {:id col}}
           [:div {:style (merge {:display        "inline-block"
                                 :padding        (str "0px " "12px")
                                 :width          (px (:width col))
                                 :height         (px (:height col))
                                 :font-weight    "bold"
                                 :text-align     (:align col)
                                 :vertical-align (:valign col)
                                 :white-space    "nowrap"
                                 :overflow       "hidden"
                                 :text-overflow  "ellipsis"}
                                header-style)}
            (:header-label col)]))))


(defn render-row
  "Render a single row of the table data"
  [cols on-click-row on-enter-row on-leave-row row-height row-style cell-style table-row-line-color row-index row]
  (vec (concat
         [:div.bvt-row-wrapper
          {:style          (merge {:padding     "4px 0px"
                                   :overflow    "hidden"
                                   :white-space "nowrap"
                                   :height      (px row-height)
                                   :border-top  (str "1px solid " table-row-line-color)
                                   :cursor      (when on-click-row "pointer")}
                                  (if (fn? row-style)
                                    (row-style row)
                                    row-style))
           :on-click       (handler-fn (do (v-table/show-row-data-on-alt-click row row-index event)
                                           (when on-click-row (on-click-row row-index))))
           :on-mouse-enter (when on-enter-row (handler-fn (on-enter-row row-index)))
           :on-mouse-leave (when on-leave-row (handler-fn (on-leave-row row-index)))}]
         (for [col cols]
           ^{:key {:id col}}
           [:div {:style (merge {:display        "inline-block"
                                 :padding        (str "0px " "12px")
                                 :width          (px (:width col))
                                 :height         (px (:height col))
                                 :text-align     (:align col)
                                 :vertical-align (:valign col)
                                 :white-space    "nowrap"
                                 :overflow       "hidden"
                                 :text-overflow  "ellipsis"}
                                (if (fn? cell-style)
                                  (cell-style row col)
                                  cell-style))}
            ((:row-label-fn col) row)]))))

(def table-args-desc
  [{:name :model                     :required true                     :type "vector of maps | atom"    :validate-fn vector-or-atom?              :description "one element for each row in the table."}
   {:name :cols                      :required true                     :type "vector of maps"           :validate-fn vector-or-atom?              :description "one element for each column in the table. May contain :id, :header-label, :row-label-fn, :width, :align and :v-align."}
   {:name :fixed-left-col-count      :required false :default 0         :type "integer"                  :validate-fn number?                      :description "the number of non-scrolling columns fixed on the left."}
   ;; TODO descriptions. 3 following put an event handler on the row...
   {:name :on-click-row              :required false                    :type "function"                 :validate-fn ifn?                         :description ""}
   {:name :on-enter-row              :required false                    :type "function"                 :validate-fn ifn?                         :description ""}
   {:name :on-leave-row              :required false                    :type "function"                 :validate-fn ifn?                         :description ""}
   {:name :col-header-height         :required false :default 31        :type "integer"                  :validate-fn number?                      :description "px height of the column header section"}
   {:name :row-height                :required false :default 31        :type "integer"                  :validate-fn? number?                     :description "px height of each row"}
   {:name :max-rows                  :required false :default 8         :type "integer"                  :validate-fn? number?                     :description "The maximum number of rows to display in the table without scrolling."}
   {:name :table-padding             :required false :default 19        :type "integer"                  :validate-fn? number?                     :description "Padding in pixels for the table."}
   {:name :table-row-line-color      :required false :default "#EAEEF1" :type "string"                   :validate-fn? string?                     :description "The CSS color of the line between rows."}
   {:name :fixed-column-border-color :required false :default "#BBBEC0" :type "string"                   :validate-fn? string?                     :description "The CSS color of the column border."}
   {:name :max-table-width           :required false                    :type "string"                   :validate-fn string?                      :description "standard CSS max-width setting of the entire table"}
   {:name :wrapper-style             :required false                    :type "map"                      :validate-fn map?                         :description "CSS styles to add or override on the outer container."}
   {:name :header-renderer           :required false                    :type "function"                 :validate-fn ifn?                         :description ""}
   {:name :header-style              :required false                    :type "map | function"           :validate-fn #(or (ifn? %) (map? %))      :description "CSS styles to add or override on the header."}
   {:name :row-style                 :required false                    :type "map | function"           :validate-fn #(or (ifn? %) (map? %))      :description "CSS styles to add or override on each row."}
   {:name :cell-style                :required false                    :type "map | function"           :validate-fn #(or (ifn? %) (map? %))      :description "CSS styles to add or override on each cell."}
   {:name :class                     :required false                    :type "string"                   :validate-fn string?                      :description "CSS class names, space separated (applies to the outer container)."}
   {:name :parts                     :required false                    :type "map"                      :validate-fn (parts? v-table/table-parts) :description "See Parts section below."}])

(defn table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [model cols fixed-left-col-count on-click-row on-enter-row on-leave-row col-header-height row-height max-rows
             table-padding table-row-line-color fixed-column-border-color max-table-width
             wrapper-style header-renderer header-style row-style cell-style class parts]
      :or   {fixed-left-col-count      0
             table-padding             19               ;; Based on g/s-19
             max-rows                  8
             row-height                31               ;; Based on g/s-31
             col-header-height         31               ;; Based on g/s-31
             table-row-line-color      "#EAEEF1"
             fixed-column-border-color "#BBBEC0"
             header-renderer           render-header}}]
  (let [fcc-bounded            (min fixed-left-col-count (count cols))
        fixed-cols             (subvec cols 0 fcc-bounded)
        content-cols           (subvec cols fcc-bounded (count cols))
        fixed-content-width    (reduce #(+ %1 (:width %2)) 0 fixed-cols)
        content-width          (reduce #(+ %1 (:width %2)) 0 content-cols)
        table-border-style     (str "1px solid " table-row-line-color)
        fixed-col-border-style (str "1px solid " fixed-column-border-color)
        actual-table-width     (+ fixed-content-width
                                  (when (pos? fixed-left-col-count) 1) ;; 1 border width (for fixed-col-border)
                                  content-width
                                  v-table/scrollbar-tot-thick
                                  (* 2 table-padding)
                                  2)]                   ;; 2 border widths
    [box/box
     :class "basic-v-table-wrapper"
     :style (merge {:background-color "white"
                    :padding          (px table-padding)
                    :max-width        (px (or max-table-width actual-table-width)) ;; Removing actual-table-width would make the table stretch to the end of the page
                    :border           table-border-style
                    :border-radius    "3px"}
                   wrapper-style)
     :child [v-table/table
             :virtual?                true
             :model                   model

             :row-height              row-height
             :row-content-width       content-width
             :row-header-renderer     (partial render-row fixed-cols   on-click-row on-enter-row on-leave-row row-height row-style cell-style table-row-line-color)
             :row-renderer            (partial render-row content-cols on-click-row on-enter-row on-leave-row row-height row-style cell-style table-row-line-color)

             :col-header-height       col-header-height
             :top-left-renderer       (partial header-renderer fixed-cols   header-style)
             :col-header-renderer     (partial header-renderer content-cols header-style)

             :max-row-viewport-height (when max-rows (* max-rows row-height))
             ;:max-table-width         (px (or max-table-width (+ fixed-content-width content-width v-table/scrollbar-tot-thick)))

             :class                   class
             ;; TODO do we need to fix nested merging w/ [:parts :name :style] etc ?
             :parts                   (merge {:wrapper {:style {:font-size "13px"
                                                                :cursor    "default"}}}
                                             (when (pos? fixed-left-col-count)
                                               {:top-left    {:style {:border-right fixed-col-border-style}}
                                                :row-headers {:style {:border-right fixed-col-border-style}}})
                                             parts)]]))
