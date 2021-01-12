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
  [{:name :model            :required true                   :type "atom"              :validate-fn vector-or-atom?          :description "text of the input (can be atom or value/nil)"}
   {:name :id-fn            :required false :default :id     :type "row -> anything"   :validate-fn ifn?                     :description [:span "given an row of " [:code ":model"] ", returns its unique identifier (aka id)"]}
   {:name :virtual?         :required false :default true    :type "boolean"                                                 :description [:span "when true, use virtual feature where only a screen-full (viewport) of rows are rendered at any one time"]}
   {:name :remove-empty-row-space? :required false :default true    :type "boolean"                                                 :description "Specifies whether to remove empty row space (the space between the last row and the horizontal scrollbar) for small tables that don't fill the space available to the v-table. This will cause the horizontal scrollbar section to be nestled against the last row, and whatever is underneath the v-table to be brought up with it"}
   {:name :max-table-width   :required false                  :type "string"            :validate-fn string?                  :description "standard CSS max-width setting of the entire v-table"}
   {:name :top-left-renderer :required false                  :type "-> nil"     :validate-fn fn?                      :description "Render the top left section. Height is determined by the col-header-height arg. Width is determined by the component itself."}
   {:name :row-header-renderer :required false                  :type "row -> hiccup"     :validate-fn fn?                      :description "Render a single row header. Height is determined by the row-height arg. Width is determined by the component itself."}
   {:name :row-header-selection-fn :required false                  :type "event -> "     :validate-fn fn?                      :description "If provided, indicates that the row header section is selectable via click+drag."}
   {:name :col-header-renderer :required false                  :type "-> hiccup"     :validate-fn fn?                      :description "Render the entire column header. Height is determined by the col-header-height arg. Width is determined by the width available to the v-table OR the row-viewport-width arg if specified."}
   {:name :col-header-height   :required false                  :type "integer"       :validate-fn number?                  :description "px height of the column header section"}
   {:name :col-header-selection-fn :required false              :type "event ->"      :validate-fn fn?                      :description "if provided, indicates that the column header section is selectable via click+drag"}
   {:name :row-renderer         :required true                 :type "row-index, row -> hiccup" :validate-fn fn?        :description "Render a single content row"}
   {:name :row-height           :required true                 :type "integer"        :validate-fn? number?             :description "px height of each row"}
   {:name :row-content-width    :required true                 :type "integer"        :validate-fn? number?             :description "px width of the content rendered by row-renderer"}
   {:name :row-viewport-width   :required false                :type "integer"        :validate-fn? number?             :description "px width of the row viewport area. If not specified, takes up all the width available to it."}
   {:name :row-viewport-height  :required false                :type "integer"        :validate-fn? number?             :description "px height of the row viewport area. If not specified, takes up all height available to it."}
   {:name :max-row-viewport-height :required false             :type "integer"        :validate-fn? number?             :description "Maximum px height of the row viewport area."}
   {:name :row-selection-fn    :required false                 :type "event -> "      :validate-fn? fn?             :description "If provided, indicates that the row section is selectable via click+drag"}
   {:name :scroll-rows-into-view :required false               :type "atom"           :validate-fn map-or-atom?    :description "Set this argument to scroll the table to a particular row range."}
   {:name :scroll-cols-into-view :required false               :type "atom"           :validate-fn map-or-atom?    :description "Set this argument to scroll the table of a particular column range."}
   {:name :class          :required false                 :type "string"                             :validate-fn string?         :description "CSS class names, space separated (applies to the outer container)"}
   {:name :parts          :required false                 :type "map"                                :validate-fn (parts? #{:wrapper :left-section :top-left :row-headers :row-header-selection-rect :row-header-content :bottom-left :middle-section :col-headers :col-header-selection-rect :col-header-content :rows :row-selection-rect :row-content :col-footers :col-footer-content :h-scroll :right-section :top-right :row-footers :row-footer-content :bottom-right :v-scroll-section :v-scroll}) :description "See Parts section below."}])

(defn table
  "Render a v-table and introduce the concept of columns (provide a spec for each).
  Of the nine possible sections of v-table, this table only supports four:
  top-left (1), row-headers (2), col-headers (4) and rows (5)
  Note that row-style and cell-style can either be a style map or functions which return a style map:
   - (row-style row)
   - (cell-style row col)
  where row is the data for that row and col is the definition map for that column
  "
  [& {:keys [model cols fixed-col-count on-click-row on-enter-row on-leave-row col-header-height row-height max-rows
             table-padding table-row-line-color fixed-column-border-color max-table-width
             wrapper-style header-renderer header-style row-style cell-style style-parts class]
      :or   {fixed-col-count           0
             table-padding             19               ;; Based on g/s-19
             max-rows                  8
             row-height                31               ;; Based on g/s-31
             col-header-height         31               ;; Based on g/s-31
             table-row-line-color      "#EAEEF1"
             fixed-column-border-color "#BBBEC0"
             header-renderer           render-header}}]
  (let [fcc-bounded            (min fixed-col-count (count cols))
        fixed-cols             (subvec cols 0 fcc-bounded)
        content-cols           (subvec cols fcc-bounded (count cols))
        fixed-content-width    (reduce #(+ %1 (:width %2)) 0 fixed-cols)
        content-width          (reduce #(+ %1 (:width %2)) 0 content-cols)
        table-border-style     (str "1px solid " table-row-line-color)
        fixed-col-border-style (str "1px solid " fixed-column-border-color)
        actual-table-width     (+ fixed-content-width
                                  (when (pos? fixed-col-count) 1) ;; 1 border width (for fixed-col-border)
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
             :style-parts             (merge {:v-table {:font-size "13px"
                                                        :cursor    "default"}}
                                             (when (pos? fixed-col-count)
                                               {:v-table-top-left    {:border-right fixed-col-border-style}
                                                :v-table-row-headers {:border-right fixed-col-border-style}})
                                             style-parts)]]))
