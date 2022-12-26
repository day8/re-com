(ns re-demo.simple-v-table-sales
  (:require-macros
    [re-com.core           :refer []])
  (:require
    [clojure.string        :as string]
    [goog.string           :as gstring]
    [goog.string.format]
    [reagent.core          :as reagent]
    [re-com.core           :refer [at label p slider hyperlink-href h-box v-box gap simple-v-table checkbox button]]
    [re-com.config         :refer [debug?]]
    [re-demo.utils         :refer [title3]]
    [re-com.util           :refer [px]]))

;; 50 randomly sampled names from most popular baby names in 2019. 
(def names
  ["Harris" "Jake" "Reece" "Aston" "Barry" "Oran" "Ritchie" "Crawford" "Raphael" "Clayton" "Johan" "Rhylen" "Caelin"
   "Calen" "Cassius" "Dakota" "Fabien" "Fraser-Lee" "Jonathin" "Khabat" "Lyotard" "Manpreet" "Mousa" "Rajvir" "Shadan"
   "Zygmunt" "Kayla" "Cassie" "Mabel" "Roslin" "Abiha" "Emma-Leigh" "Jacqueline" "Leena" "Lilianna" "Olina" "Orianna"
   "Sylvia" "Abbigale" "Allissa" "Alona" "Asira" "Aurora-Leigh" "Brea" "Chiarra" "Elouisa" "Emme-Leigh" "Frea" "Geena"
   "Hanifa"])

;; Generate between 0-100 random rows.
(defn generate-sales-rows
  [& [row-count]]
  (mapv
    (fn [n]
      (let [person (rand-nth names)]
        {:id     n
         :region (rand-nth ["North" "East" "South" "West"])
         :person person
         :method (rand-nth [:online :in-store])
         :email  (str (string/lower-case person) "@" (rand-nth ["widget.org" "example.com" "deck.com" "conversation.com" "response.com"]))
         :sales  (gstring/format "%.2f" (+ (* (rand) (- 10000 1000)) 1000))
         :units  (+ (* (rand-int 10) (- 1000 100)) 100)}))
    (range row-count)))

(defn store-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M20 4H4v2h16V4zm1 10v-2l-1-5H4l-1 5v2h1v6h10v-6h4v6h2v-6h1zm-9 4H6v-4h6v4z"}]])

(defn devices-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M4 6h18V4H2v13H0v3h14v-3H4V6zm20 2h-8v12h8V8zm-2 9h-4v-7h4v7z"}]])

(defn demo
  []
  (let [sales-rows          (reagent/atom (generate-sales-rows 100))
        max-rows?           (reagent/atom true)
        max-rows            (reagent/atom 8)
        max-width?          (reagent/atom false)
        max-width           (reagent/atom 630)
        fixed-column-count? (reagent/atom true)
        fixed-column-count  (reagent/atom 1)
        striped?            (reagent/atom false)
        parent-color        "#BEEDFF"
        parent-width?       (reagent/atom false)
        parent-width        (reagent/atom 600)
        parent-height?      (reagent/atom false)
        parent-height       (reagent/atom 600)
        how-many?           (reagent/atom false)
        how-many            (reagent/atom 100)
        spacing             (px 12)
        spacing7           (px 7)
        email-row-label-fn  (fn [row] [hyperlink-href :src (at) :label (:email row) :href (str "mailto:" (:email row))])
        method-row-label-fn (fn [row] (case (:method row) :online [devices-icon] [store-icon]))]
    (fn []
      [v-box
       :src      (at)
       :children [[title3 "Sales Table Demo"]
                  [gap :src (at) :size (px 5)]
                  [v-box
                   :src      (at)
                   :gap      (px 10)
                   :children [[v-box
                               :src      (at)
                               :width    "450px"
                               :children [[:p "This demo shows the primary use case - a table displaying entities in rows, and their attributes in columns. See it below in the blue box."]
                                          #_[:p  "This demo allows you to experiment with how the dimensions of a " [:code "simple-v-table"]" interact with those of its parent. "]]]

                              [v-box
                               :src      (at)
                               :gap      (px 5)
                               :children [[h-box
                                           :src      (at)
                                           :gap      spacing7
                                           :align    :center
                                           :children [[checkbox
                                                       :src      (at)
                                                       :model     fixed-column-count?
                                                       :on-change #(do (reset! fixed-column-count? %)
                                                                       (reset! fixed-column-count (if @fixed-column-count? 1 0)))]
                                                      [label :src (at) :label [:code ":fixed-column-count"]]
                                                      (when @fixed-column-count?
                                                        [:<>
                                                         [slider
                                                          :src      (at)
                                                          :model     fixed-column-count
                                                          :on-change #(reset! fixed-column-count %)
                                                          :min       0
                                                          :max       3
                                                          :step      1
                                                          :width     (px 200)]
                                                         [gap
                                                          :src      (at)
                                                          :size spacing7]
                                                         [label
                                                          :src      (at)
                                                          :label @fixed-column-count]])]]
                                          [h-box
                                           :src      (at)
                                           :gap      spacing7
                                           :align    :center
                                           :children [[checkbox
                                                       :src      (at)
                                                       :model     max-width?
                                                       :on-change #(reset! max-width? %)]
                                                      [label
                                                       :src      (at)
                                                       :label [:code ":max-width"]]
                                                      (when @max-width?
                                                        [:<>
                                                         [slider
                                                          :src      (at)
                                                          :model     max-width
                                                          :on-change #(reset! max-width %)
                                                          :min       200
                                                          :max       850
                                                          :step      1
                                                          :width     (px 200)]
                                                         [gap
                                                          :src      (at)
                                                          :size spacing7]
                                                         [label
                                                          :src      (at)
                                                          :label (px @max-width)]])]]
                                          [h-box
                                           :src      (at)
                                           :gap      spacing7
                                           :align    :center
                                           :children [[checkbox
                                                       :src      (at)
                                                       :model     max-rows?
                                                       :on-change (fn [new-max-rows?]
                                                                    (let [rc (count @sales-rows)]
                                                                      (if new-max-rows?
                                                                        (reset! max-rows? new-max-rows?)
                                                                        (if (or (<= rc 100) @parent-height?)
                                                                          (reset! max-rows? new-max-rows?)))))]
                                                      [label
                                                       :src      (at)
                                                       :label [:code
                                                               (when (and (> (count @sales-rows) 100) (not @parent-height?))
                                                                 {:title "Can't uncheck. Table would be too large.\nMust check 'parent height' before you can uncheck this"
                                                                  :style {:color "red"}})
                                                               ":max-rows"]]
                                                      (if @max-rows?
                                                        [:<>
                                                         [slider
                                                          :src      (at)
                                                          :model     max-rows
                                                          :on-change #(reset! max-rows %)
                                                          :min       0
                                                          :max       50
                                                          :step      1
                                                          :width     (px 200)]
                                                         [gap
                                                          :src      (at)
                                                          :size spacing]
                                                         [label
                                                          :src      (at)
                                                          :label @max-rows]
                                                         [gap
                                                          :src      (at)
                                                          :size spacing7]
                                                         [label
                                                          :src      (at)
                                                          :label (str "(this demo has " (count @sales-rows) " rows of data)")]]
                                                        [label
                                                         :src      (at)
                                                         :style {:background-color parent-color}
                                                         :label (str "when unset, the table's height is the parent's explicit height, or if that's unset, the number of rows of data (" (count @sales-rows) ")")])]]
                                          [h-box
                                            :src      (at)
                                            :gap      spacing7
                                            :align    :center
                                            :children [[checkbox
                                                        :src      (at)
                                                        :model     striped?
                                                        :on-change #(reset! striped? %)]
                                                       [label
                                                        :src      (at)
                                                        :label [:code ":striped?"]]]]
                                          [gap
                                           :src      (at)
                                           :size "0px"]
                                          [v-box
                                           :src      (at)
                                           :class    "parent-for-simple-v-table"
                                           :width    (when @parent-width?  (px @parent-width))
                                           :height   (when @parent-height? (px @parent-height))
                                           :style    {:padding          "8px"
                                                      :background-color parent-color}
                                           :gap      spacing
                                           :children [[label
                                                       :src      (at)
                                                       :label "This blue box is the table's (v-box) parent."]
                                                      [h-box
                                                       :src      (at)
                                                       :gap      spacing7
                                                       :align    :center
                                                       :children [[checkbox
                                                                   :src      (at)
                                                                   :model     parent-width?
                                                                   :on-change #(reset! parent-width? %)]
                                                                  [label
                                                                   :src      (at)
                                                                   :label [:code "parent width"]]
                                                                  (if @parent-width?
                                                                    [:<>
                                                                     [slider
                                                                      :src      (at)
                                                                      :model     parent-width
                                                                      :on-change #(reset! parent-width %)
                                                                      :min       500
                                                                      :max       1200
                                                                      :step      1
                                                                      :width     (px 200)]
                                                                     [gap
                                                                      :src      (at)
                                                                      :size spacing7]
                                                                     [label
                                                                      :src      (at)
                                                                      :label (px @parent-width)]]
                                                                    [label
                                                                     :src      (at)
                                                                     :label "unset: grows to table's natural extent (or the :max-width override)"])]]
                                                      [h-box
                                                       :src      (at)
                                                       :gap      spacing7
                                                       :align    :center
                                                       :children [[checkbox
                                                                   :src      (at)
                                                                   :model     parent-height?
                                                                   :on-change (fn [new-parent-height?]
                                                                                (let [rc (count @sales-rows)]
                                                                                  (if new-parent-height?
                                                                                    (reset! parent-height? new-parent-height?)
                                                                                    (if (or (<= rc 100) @max-rows?)
                                                                                      (reset! parent-height? new-parent-height?)))))]
                                                                  [label
                                                                   :src      (at)
                                                                   :label [:code
                                                                           (when (and (> (count @sales-rows) 100) (not @max-rows?))
                                                                             {:title "Can't uncheck. Table would be too large.\nMust check :max-rows before you can uncheck this"
                                                                              :style {:color "red"}})
                                                                           "parent height"]]
                                                                  (if @parent-height?
                                                                    [:<>
                                                                     [slider
                                                                      :src      (at)
                                                                      :model     parent-height
                                                                      :on-change #(reset! parent-height %)
                                                                      :min       500
                                                                      :max       1200
                                                                      :step      1
                                                                      :width     (px 200)]
                                                                     [gap
                                                                      :src      (at)
                                                                      :size spacing7]
                                                                     [label
                                                                      :src      (at)
                                                                      :label (px @parent-height)]]
                                                                    [label
                                                                     :src      (at)
                                                                     :label (str "unset: grows to table's natural extent (" (count @sales-rows) " rows of data) or the :max-rows override")])]]
                                                      (when debug?
                                                        [h-box
                                                         :src      (at)
                                                         :gap      spacing7
                                                         :align    :center
                                                         :children [[label
                                                                     :src      (at)
                                                                     :width "145px"
                                                                     :label [:span "Rows in table: " [:span.bold (count @sales-rows)]]]
                                                                    [button
                                                                     :src      (at)
                                                                     :label    (str "Generate " (if @how-many? @how-many "random") " rows")
                                                                     :style    {:width   "170px"
                                                                                :height  "24px"
                                                                                :padding "0px"}
                                                                     :on-click #(do (when (and @how-many? (> @how-many 100) (not @max-rows?) (not @parent-height?))
                                                                                      (reset! parent-height? true))
                                                                                    (reset! sales-rows (generate-sales-rows (if @how-many? @how-many (+ 5 (rand 95))))))]
                                                                    [checkbox
                                                                     :src      (at)
                                                                     :model     how-many?
                                                                     :on-change #(reset! how-many? %)]
                                                                    [label :src (at) :label [:code "how many?"]]
                                                                    (if @how-many?
                                                                      [:<>
                                                                       [slider
                                                                        :src      (at)
                                                                        :model     how-many
                                                                        :on-change #(reset! how-many %)
                                                                        :min       1000
                                                                        :max       1000000
                                                                        :step      1000
                                                                        :width     (px 200)]
                                                                       [gap :src (at) :size spacing7]
                                                                       [label :src (at) :label @how-many]]
                                                                      [label :src (at) :label "random number between 5 and 99"])]])


                                                      ;; vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv
                                                      ;; The simple-v-table demo starts here
                                                      [simple-v-table
                                                       :src                       (at)
                                                       :model                     sales-rows

                                                       ;; ===== Columns
                                                       :columns                   [{:id :id     :header-label "Code"   :row-label-fn :id                   :width 70  :align "center" :vertical-align "middle"}
                                                                                   {:id :region :header-label "Region" :row-label-fn :region               :width 100 :align "left"   :vertical-align "middle"}
                                                                                   {:id :name   :header-label "Name"   :row-label-fn :person               :width 100 :align "left"   :vertical-align "middle" :sort-by {}}
                                                                                   {:id :email  :header-label "Email"  :row-label-fn email-row-label-fn    :width 200 :align "left"   :vertical-align "middle"}
                                                                                   {:id :method :header-label "Method" :row-label-fn method-row-label-fn   :width 100 :align "center" :vertical-align "middle"}
                                                                                   {:id :sales  :header-label "Sales"  :row-label-fn #(str "$" (:sales %)) :width 100 :align "right"  :vertical-align "middle" :sort-by {:key-fn :sales}}
                                                                                   {:id :units  :header-label "Units"  :row-label-fn :units                :width 100 :align "right"  :vertical-align "middle" :sort-by true}]
                                                       :fixed-column-count        @fixed-column-count
                                                       :fixed-column-border-color "#333"

                                                       ;; ===== Rows
                                                       :row-height                35
                                                       :max-rows                  (when @max-rows? @max-rows)
                                                       :max-width                 (when @max-width? (px @max-width))

                                                       ;; ===== Styling
                                                       :striped?                  @striped?
                                                       :cell-style                (fn [{:keys [sales] :as row} {:keys [id] :as column}]
                                                                                    (when (= :sales id)
                                                                                      {:background-color (cond
                                                                                                           (> 2000 sales)      "#FF4136"
                                                                                                           (> 5000 sales 2000) "#FFDC00"
                                                                                                           (> 7500 sales 5000) "#01FF70"
                                                                                                           (> sales 7500)      "#2ECC40")}))]]]]]]]]])))
