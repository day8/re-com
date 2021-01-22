(ns re-com.simple-v-table
  (:require-macros  [re-com.core :refer [handler-fn]])
  (:require
    [re-com.box     :as box]
    [re-com.util    :refer [px]]
    [re-com.validate :refer [vector-or-atom? map-or-atom? parts?]]
    [re-com.v-table :as v-table]))


(defn render-header
  "Render the table header"
  [cols parts]
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
                                (get-in parts [:simple-header :style]))}
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

(def simple-v-table-parts
  (conj v-table/v-table-parts
        :simple-wrapper
        :simple-header))

;; :cols
;; :fixed-column-count
;; :col-header-height

(def simple-v-table-args-desc
  [{:name :model                     :required true                     :type "vector of maps | atom"    :validate-fn vector-or-atom?              :description "one element for each row in the table."}
   {:name :columns                   :required true                     :type "vector of maps"           :validate-fn vector-or-atom?              :description [:span "one element for each column in the table. Must contain " [:code ":id"] "," [:code ":header-label"] "," [:code ":row-label-fn"] "," [:code ":width"] ", and " [:code ":height"] ". Optionally contains " [:code ":align"] " and " [:code ":v-align"] "."]}
   {:name :fixed-column-count        :required false :default 0         :type "integer"                  :validate-fn number?                      :description "the number of fixed (non-scrolling) columns on the left."}
   {:name :fixed-column-border-color :required false :default "#BBBEC0" :type "string"                   :validate-fn? string?                     :description [:span "The CSS color of the horizontal border between the fixed columns on the left, and the other columns on the right. " [:code ":fixed-column-count"] " must be > 0 to be visible."]}
   {:name :column-header-height      :required false :default 31        :type "integer"                  :validate-fn number?                      :description [:span "px height of the column header section. Typically, equals " [:code ":row-height"] " * number-of-column-header-rows."]}
   {:name :max-table-width           :required false                    :type "string"                   :validate-fn string?                      :description "standard CSS max-width setting of the entire table. Literally constrains the table to the given width so that if the table is wider than this it will add scrollbars. Ignored if value is larger than the combined width of all the columns and table padding."}
   {:name :max-rows                  :required false                    :type "integer"                  :validate-fn? number?                     :description "The maximum number of rows to display in the table without scrolling. If not provided will take up all available vertical space."}
   {:name :table-row-line-color      :required false :default "#EAEEF1" :type "string"                   :validate-fn? string?                     :description "The CSS color of the lines between rows."}
   {:name :on-click-row              :required false                    :type "function"                 :validate-fn ifn?                         :description "This function is called when the user clicks a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
   {:name :on-enter-row              :required false                    :type "function"                 :validate-fn ifn?                         :description "This function is called when the user's mouse pointer enters a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
   {:name :on-leave-row              :required false                    :type "function"                 :validate-fn ifn?                         :description "This function is called when the user's mouse pointer leaves a row. Called with the row index. Do not use for adjusting row styles, use styling instead."}
   {:name :row-height                :required false :default 31        :type "integer"                  :validate-fn? number?                     :description "px height of each row."}
   {:name :table-padding             :required false :default 19        :type "integer"                  :validate-fn? number?                     :description "Padding in pixels for the entire table."}
   {:name :header-renderer           :required false                    :type "cols -> hiccup"           :validate-fn ifn?                         :description "This function returns the hiccup to render the column headings."}
   {:name :row-style                 :required false                    :type "map | function"           :validate-fn #(or (ifn? %) (map? %))      :description "CSS styles to add or override on each row."}
   {:name :cell-style                :required false                    :type "map | function"           :validate-fn #(or (ifn? %) (map? %))      :description "CSS styles to add or override on each cell."}
   {:name :class                     :required false                    :type "string"                   :validate-fn string?                      :description "CSS class names, space separated (applies to the outer container)."}
   {:name :parts                     :required false                    :type "map"                      :validate-fn (parts? simple-v-table-parts)  :description "See Parts section below."}])

(defn simple-v-table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [model columns fixed-column-count on-click-row on-enter-row on-leave-row column-header-height row-height max-rows
             table-padding table-row-line-color fixed-column-border-color max-table-width
             header-renderer row-style cell-style class parts]
      :or   {fixed-column-count      0
             table-padding             19               ;; Based on g/s-19
             row-height                31               ;; Based on g/s-31
             column-header-height         31               ;; Based on g/s-31
             table-row-line-color      "#EAEEF1"
             fixed-column-border-color "#BBBEC0"
             header-renderer           render-header}}]
  (let [fcc-bounded            (min fixed-column-count (count columns))
        fixed-cols             (subvec columns 0 fcc-bounded)
        content-cols           (subvec columns fcc-bounded (count columns))
        fixed-content-width    (reduce #(+ %1 (:width %2)) 0 fixed-cols)
        content-width          (reduce #(+ %1 (:width %2)) 0 content-cols)
        table-border-style     (str "1px solid " table-row-line-color)
        fixed-col-border-style (str "1px solid " fixed-column-border-color)
        actual-table-width     (+ fixed-content-width
                                  (when (pos? fixed-column-count) 1) ;; 1 border width (for fixed-col-border)
                                  content-width
                                  v-table/scrollbar-tot-thick
                                  (* 2 table-padding)
                                  2)]                   ;; 2 border widths
    [box/box
     :class (str "simple-v-table-wrapper " (get-in parts [:simple-wrapper :class]))
     :style (merge {:background-color "white"
                    :padding          (px table-padding)
                    :max-width        (px (or max-table-width actual-table-width)) ;; Removing actual-table-width would make the table stretch to the end of the page
                    :border           table-border-style
                    :border-radius    "3px"}
                   (get-in parts [:simple-wrapper :style]))
     :attr  (get-in parts [:simple-wrapper :attr])
     :child
     (cond->
       [v-table/v-table
        :virtual?                true
        :model                   model

        :row-height              row-height
        :row-content-width       content-width
        :row-header-renderer     (partial render-row fixed-cols   on-click-row on-enter-row on-leave-row row-height row-style cell-style table-row-line-color)
        :row-renderer            (partial render-row content-cols on-click-row on-enter-row on-leave-row row-height row-style cell-style table-row-line-color)

        :col-header-height       column-header-height
        ;; For fixed columns:
        :top-left-renderer       (partial header-renderer fixed-cols   parts)
        ;; Only for non-fixed columns:
        :col-header-renderer     (partial header-renderer content-cols parts)

        ;:max-table-width         (px (or max-table-width (+ fixed-content-width content-width v-table/scrollbar-tot-thick)))

        :class                   class
        ;; TODO do we need to fix nested merging w/ [:parts :name :style] etc ?
        :parts                   (merge {:wrapper {:style {:font-size "13px"
                                                           :cursor    "default"}}}
                                        (when (pos? fixed-column-count)
                                          {:top-left    {:style {:border-right fixed-col-border-style}}
                                           :row-headers {:style {:border-right fixed-col-border-style}}})
                                        parts)]

       ;; TODO :max-row-viewport-height nil if max-rows not provided, then grow the table vertical.
       (number? max-rows)
       (conj :max-row-viewport-height (when max-rows (* max-rows row-height))))]))
