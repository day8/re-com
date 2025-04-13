(ns re-demo.nested-v-grid
  (:require
   [clojure.string :as str]
   [re-com.core :as rc]
   ["@faker-js/faker" :as faker]
   [zprint.core :as zprint]
   [re-com.util :as u]
   [re-com.theme :as theme]
   [reagent.core :as r]
   [re-com.nested-v-grid  :as nvg :refer [nested-v-grid]]
   [re-demo.utils :as rdu :refer [source-reference panel-title title2 title3
                                  args-table parts-table status-text new-in-version
                                  prop-slider prop-checkbox]]))

(defn number-format [n & {:keys [precision]}]
  (if-not (number? n)
    n
    (.format (js/Intl.NumberFormat. "en-US" #js {:minimumFractionDigits precision
                                                 :maximumFractionDigits precision})
             n)))

(def lookup-table [["🚓" "🛵" "🚲" "🛻" "🚚"]
                   ["🍐" "🍎" "🍌" "🥝" "🍇"]
                   ["🐕" "🐎" "🧸" "🐈" "🐟"]])

(def fake-name!  (.-fullName (.-person faker/faker)))

(def fake-email! (.-email (.-internet faker/faker)))

(def fake-company! (.-name (.-company faker/faker)))

(def fake-companies (repeatedly 20 fake-company!))

(defn fake-person! []
  {:name    (fake-name!)
   :email   (fake-email!)
   :company (rand-nth fake-companies)})

(def fake-banter
  (into
   (mapv #(do {:country "Australia" :banter %})
         ["Mate, this is a proper feed!"
          "Tim Tams > Whittaker’s, change my mind."
          "Who even drinks Long Blacks?"])
   (mapv  #(do {:country "New Zealand" :banter %})
          ["Vegemite is just wrong."
           "Speight’s > Fosters, no contest."
           "Flat Whites were invented here, you know."])))

(def fake-people (repeatedly 400 fake-person!))

(def fake-products
  (into
   (mapv #(do {:country "Australia" :product %})
         ["Vegemite" "Flat White" "Tim Tams" "Fosters Beer" "Meat Pie"])
   (mapv #(do {:country "New Zealand" :product %})
         ["Marmite" "Long Black" "Whittaker's Chocolate" "Speight’s Beer" "Hangi"])))

(defn nested-group-tree [ks coll]
  (letfn [(group [kvs items]
            (if (empty? kvs)
              items
              (mapv (fn [[k v]]
                      (into [{:grouping k (first kvs) k}] (group (rest kvs) v)))
                    (group-by (first kvs) items))))]
    (group ks coll)))

(defn fake-sale! []
  (merge (rand-nth fake-people)
         (rand-nth fake-products)
         {:price   (* 10 (rand))}))

(def fake-sales (repeatedly 10000 fake-sale!))

(defn group [dimension value table]
  (filter #(= value (get % dimension)) table))

(def group* (memoize group))

(defn basic-demo []
  [rc/v-box
   :gap "12px"
   :children
   [[rc/h-box
     :justify :between
     :children
     [[nested-v-grid
       {:column-tree ["Even" 2 4 6]
        :row-tree    ["Odd" 1 3 5]
        :cell-label  (fn [{:keys [column-path row-path]}]
                       (let [column-spec (last column-path)
                             row-spec    (last row-path)]
                         (* column-spec row-spec)))}]
      [rdu/zprint-code
       '[nested-v-grid
         {:column-tree ["Even" 2 4 6]
          :row-tree    ["Odd" 1 3 5]
          :cell-label  (fn [{:keys [column-path row-path]}]
                         (let [column-spec (last column-path)
                               row-spec    (last row-path)]
                           (* column-spec row-spec)))}]]]]
    [rc/p "A simple times table. The " [:code ":cell"] " function gets called once for each cell, getting passed a "
     [:code ":column-path"] " and " [:code ":row-path"]
     ". In this case, each path is a vector of one number. For instance, "
     "the bottom cells each have a " [:code ":row-path"] " of " [:code "[5]"] "."]
    [rc/line]
    [rc/h-box
     :justify :between
     :gap "10px"
     :children
     [[nested-v-grid
       {:column-tree ["x" 0 1 2]
        :row-tree    ["y" 2 3 4]
        :cell-label  (fn [{:keys [column-path row-path]}]
                       (get-in lookup-table [(last column-path)
                                             (last row-path)]))}]
      [rdu/zprint-code
       '(def lookup-table [["🚓" "🛵" "🚲" "🛻" "🚚"]
                           ["🍐" "🍎" "🍌" "🥝" "🍇"]
                           ["🐕" "🐎" "🧸" "🐈" "🐟"]])
       '[nested-v-grid
         {:column-tree ["x" 0 1 2]
          :row-tree    ["y" 2 3 4]
          :cell-label  (fn [{:keys [column-path row-path]}]
                         (get-in lookup-table [(last column-path)
                                               (last row-path)]))}]]]]
    [rc/p "Here, instead of multiplying the path values, we use them to access an "
     "external lookup table. This is a common use-case: prepare a data frame independently from "
     [:code "nested-grid"] ", but with the intention of using "
     [:code "nested-grid"] " as a simple display layer."]]])

(defn concepts-column []
  [rc/v-box
   :children
   [[title2 "Concepts"]
    [rc/p "To use " [:code "nested-grid"]
     ", you’ll need to understand some key concepts - "
     [:code ":column-tree"] ","
     [:code ":column-spec"] ","
     [:code ":column-path"] ", etc..."]
    [title2 "Column Spec"]
    [rc/p "A " [:code ":column-spec"] " describes a single column."]
    [:ul
     [:li "For instance, the " [:strong "Basic Demo"] " uses "
      [:code "2"] " as a " [:code ":column-spec"] "."]
     [:li "You can use " [:i "almost any"] " type of value."]
     [:li "You " [:i "can't"] " use vectors or lists (these are reserved for the "
      [:code ":column-tree"] ")."]
     [:li "At Day8, we tend to use maps. For instance, "
      [:pre {:style {:width 400}} "{:id :a
 :column-label \"A\"
 :special-business-logic {::xyz \"abc\"}}"]]]
    [title2 "Column Tree"]
    [rc/p "A " [:code ":column-tree"] "describes a nested arrangement of columns."
     [:ul
      [:li "In practice, a " [:code ":column-tree"] " is a vector (or list) of "
       [:code ":column-spec"] "values."]
      [:li "There are parent columns, and they can have child columns, "
       "which can have their own child columns, etc..."]
      [:li "The most basic case is a flat tree: " [:code "[:a :b :c]"] "."]
      [:li "A nested tree looks like: " [:code "[:a [1 2] :b [3 4]]"] "."]
      [:li "In that case: "
       [:ul
        [:li [:code ":a"] " and " [:code ":b"] " are siblings, each with two children."]
        [:li [:code "1"] " and " [:code "2"] " are siblings, both children of " [:code ":a"]]]]]]
    [title2 "Column Path"]
    [rc/p "A " [:code ":column-path"] " describes a distinct ancestry within a "
     [:code ":column-tree"] "."
     [:ul
      [:li "For the " [:code ":column-tree"] " above, " [:code "[:a [1 2] :b [3 4]]"] ", its " [:code ":column-path"] "s are: "
       [:pre "[[:a] [:a 1] [:a 2] [:b] [:b 3] [:b 4]]"]]]]
    [title2 "Row"]
    [rc/p "Everything described above applies to rows, as well. " [:code ":row-spec"] ", " [:code ":row-tree"] " and " [:code ":row-path"]
     " have all the same properties as their column equivalents."]
    [title2 "Cell & Header Functions"]
    [rc/p [:code "nested-grid"] " accepts " [:code ":column-header"] ", " [:code ":row-header"] ", " [:code ":corner-header"] " and " [:code ":cell"] " props. Each is a function." [:code "nested-grid"] " calls each function to render the following locations:"
     [rc/nested-v-grid
      {:corner-header (constantly [:div {:style {:color "grey"}} ":corner-header"])
       :column-width 100
       :column-tree [":column-header"
                     {:id (gensym) :label ":column-header"}
                     {:id (gensym) :label ":column-header"}]
       :row-tree [":row-header"
                  {:id (gensym) :label ":row-header"}
                  {:id (gensym) :label "row-header"}]
       :cell-label        (constantly ":cell")}]
     [rc/p "Each prop has a reasonable default, except for " [:code ":cell"] "."
      "Your " [:code ":cell"] " function will be passed two keyword arguments, "
      [:code ":column-path"] " and " [:code ":row-path"] ". It can return either a string or a hiccup."]]]])

(defn intro-column []
  [rc/v-box
   :children
   [[title2 "Introduction"]
    [status-text "alpha" {:color "red"}]
    [new-in-version "v2.20.0"]
    [rc/p [:code "nested-grid"]
     " " "provides a grid with nested, hierarchical columns and rows."
     " " "The archetypical use-case would be to display a "
     [:a {:href "https://en.wikipedia.org/wiki/Pivot_table"} "pivot table"] "."
     " " "However, " [:code "nested-grid"] " provides a lean abstraction that could"
     " " "suit a variety of problems."]
    [rc/p "Essentially, each cell has a unique pair of" " " [:i "header paths"]
     " " "within the hierarchy." " " "The value of each cell is a"
     " " [:i "function"] " " "of its" " " [:i "header paths"] "."]
    [title2 "Characteristics"]
    [rc/p "Unlike" " " [:code "v-table"] ", "
     [:code "nested-grid"] ":"
     [:ul {:style {:width 400}}
      [:li "Uses" " " [:a {:href "https://www.w3schools.com/css/css_grid.asp"} "css grid"]
       " " "for layout."]
      [:li "Has adjustible column & row sizes."]
      [:li "Can virtualize columns (not just rows)."]]]
    [title2 "Quick Start"]
    [rc/p "To use " [:code "nested-grid"] ", at a minimum, you must declare:"
     [:ul
      [:li [:strong [:code ":column-tree"]] ": a vector describing the column structure."]
      [:li [:strong [:code ":row-tree"]] ": a vector describing the row structure."]
      [:li [:strong [:code ":cell-label"]] ": a function which, given a "
       [:code ":column-path"] " and a " [:code ":row-path"]
       ", renders one cell, either as a string or a hiccup."]]
     "See the " [:strong "Basic Demo"] " for examples,"
     " and the " [:strong "Concepts"] " section for in-depth explanations."]]])

(def operators
  {:add      {:operator + :label "Addition"}
   :multiply {:operator * :label "Multiplication"}
   :lookup   {:operator (fn [l r] (get-in lookup-table [l r]))
              :label    "Lookup"}})

(def operands {:one {:left 1 :label "1"}
               :two {:left 2 :label "2"}
               :three {:right 3 :label "3"}
               :four  {:right 4 :label "4"}})

(def lorem-ipsum ["Lorem" "ipsum" "dolor" "sit" "amet" " consectetur" "adipiscing" "elit" " sed"
                  "do" "eiusmod" "tempor" "incididunt" "ut" "labore" "et" "dolore" "magna"
                  "aliqua."])

(defn rand-color [] (str "rgb(" (* 255 (rand)) "," (* 255 (rand)) "," (* 255 (rand)) ")"))

(defn rf8-grid-theme [props {:keys [part state] $ :variables}]
  (let [$ (merge $ {:border-light "#ccc"
                    :dark         "#768895"
                    :neutral      "#ccc"})]
    (->>
     nil
     (case part
       :re-com.nested-grid/nested-grid
       {:row-height           "20px"
        :column-header-height "20px"
        #_#_:row-header-width 0
        :show-zebra-stripes?  false}

       :re-com.nested-grid/cell-wrapper
       {:style {:border-left  "none"
                :border-right "none"}}

       :re-com.nested-grid/column-header-wrapper
       {:style {:border-right     "none"
                :padding-left     "10px"
                :padding-top      1
                :background-color ($ :dark)
                :color            ($ :white)}}

       :re-com.nested-grid/row-header-wrapper
       {:style {:border-left      "none"
                :border-bottom    "none"
                :border-right     "none"
                :padding-left     "10px"
                :padding-top      1
                :background-color "#99a"
                :color            ($ :white)}}

       :re-com.nested-grid/corner-header-wrapper
       {:style {:border-left      "none"
                :border-bottom    "none"
                :border-right     "none"
                :padding-left     "10px"
                :padding-top      1
                :background-color ($ :dark)
                :color            ($ :white)}})
     (theme/merge-props props))))

(defn internals-demo []
  [rc/v-box
   :gap "12px"
   :children
   [[nested-v-grid
     {:column-tree  [:a [:b [:e] [:f]] [:c [:g] [:h]] [:d]]
      :row-tree     [:u [:v [:x] [:y]] [:w [:z] [:n]]]
      :row-height   50
      :column-width 60
      :cell-label   (fn [{:keys [row-path column-path]}]
                      [:div (pr-str column-path)
                       [:br]
                       (pr-str row-path)])
      :style        {:width  500
                     :height 600}}]
    [rdu/zprint-code
     '[nested-v-grid
       {:column-tree  [:a [:b [:e] [:f]] [:c [:g] [:h]] [:d]]
        :row-tree     [:u [:v [:x] [:y]] [:w [:z] [:n]]]
        :row-height   50
        :column-width 60
        :cell-label   (fn [{:keys [row-path column-path]}]
                        [:div (pr-str column-path)
                         [:br]
                         (pr-str row-path)])
        :style        {:width  500
                       :height 600}}]]
    #_[rdu/code
       "[nested-v-grid
 {:column-tree  [:a
                 [:b
                  [:e]
                  [:f]]
                 [:c
                  [:g]
                  [:h]]
                 [:d]]
  :row-tree     [:u
                 [:v
                  [:x]
                  [:y]]
                 [:w
                  [:z]
                  [:n]]]
  :row-height   50
  :column-width 60
  :cell-label   (fn [{:keys [row-path column-path]}]
                  [:div (pr-str column-path)
                   [:br]
                   (pr-str row-path)])}]"]
    [nested-v-grid
     {:column-tree           [:a [:b [:e] [:f]] [:c [:g] [:h]] [:d]]
      :row-tree              [:u [:v [:x] [:y]] [:w [:z] [:n]]]
      :show-row-branches?    true
      :show-column-branches? true
      :row-height            50
      :column-width          60
      :cell-label            (fn [{:keys [row-path column-path]}]
                               [:div (pr-str column-path)
                                [:br]
                                (pr-str row-path)])
      :style                 {:width  500
                              :height 600}}]]])

(defn paths-example [& {:as props}]
  [nested-v-grid
   (merge
    {:column-tree  [:a [:b [:e] [:f]] [:c [:g] [:h]] [:d]]
     :row-tree     [:u [:v [:x] [:y]] [:w [:z] [:n]]]
     :row-height   50
     :column-width 60
     :cell-label   (fn [{:keys [row-path column-path]}]
                     [:div (pr-str column-path)
                      [:br]
                      (pr-str row-path)])
     :style        {:width  500
                    :height 600}}
    props)])

(defn paths-example-code [& {:as props}]
  [rdu/zprint-code
   `[nested-v-grid
     {:column-tree  [:a [:b [:e] [:f]] [:c [:g] [:h]] [:d]]
      :row-tree     [:u [:v [:x] [:y]] [:w [:z] [:n]]]
      :row-height   50
      :column-width 60
      :cell-label   (fn [{:keys [row-path column-path]}]
                      [:div (pr-str column-path)
                       [:br]
                       (pr-str row-path)])
      :style        {:width  500
                     :height 600}}]])

(defn virtualization-example-code [props]
  [rdu/zprint-code
   '"See virtualization demo for more detailed source-code"
   '[nested-v-grid
     {:column-tree (into [{:label "Products"}]
                         (nested-group-tree [:country] fake-products))
      :row-tree    (into [{:label "Vendors"}]
                         (nested-group-tree [:company] fake-people))
      :cell        cell
      :parts       {:wrapper {:style {:max-height 400
                                      :max-width  640}}}}]])

(defn virtualization-example [props]
  [nested-v-grid
   (-> {:column-tree           (into [{:label "Products"}]
                                     (nested-group-tree [:country] fake-products))
        :row-tree              (into [{:label "Vendors"}]
                                     (nested-group-tree [:company] fake-people))
        :cell                  (fn [_]
                                 (let [hover? (r/atom nil)]
                                   (fn [{:keys [column-path row-path column-meta row-meta style]}]
                                     (let [{:keys [name product company country]}
                                           (->> (into column-path row-path)
                                                (apply merge))
                                           total-sales           (cond->> fake-sales
                                                                   country (group* :country country)
                                                                   product (group* :product product)
                                                                   company (group* :company company)
                                                                   name    (group* :name name)
                                                                   :do     (map :price)
                                                                   :do     (apply +))
                                           label                 (str "$" (number-format total-sales
                                                                                         {:precision 2}))
                                           {:keys [banter]}      (some-> (group* :country country fake-banter)
                                                                     seq
                                                                     rand-nth)
                                           {:keys [branch-end?]} (merge column-meta row-meta)]
                                       [:div {:title (str (or name company)
                                                          " sold " label (cond product (str " of " product)
                                                                               country (str " in " country))
                                                          ". "
                                                          banter)
                                              :style          (merge style
                                                                     (when (or @hover? branch-end?)
                                                                       {:background-color "#eef"}))
                                              :on-mouse-enter #(reset! hover? true)
                                              :on-mouse-leave #(reset! hover? nil)}
                                        label]))))
        :parts                 {:wrapper {:style {:max-height 400
                                                  :max-width  640}}}
        :column-header-label   (fn [{:keys [path style]}]
                                 [:div {:style style}
                                  (str ((some-fn :product :grouping) (peek path)))])
        :row-header-label      (fn [{:keys [path style]}]
                                 [:div {:style style}
                                  (str ((some-fn :name :grouping) (peek path)))])}
       (merge props))])

(defn options-demo []
  (let [props-db (r/atom {})
        props    (r/reaction
                  (into {} (filter (comp some? second)) @props-db))]
    (fn []
      [rc/v-box
       :gap "12px"
       :children
       [[rc/h-box
         :gap "12px"
         :children
         [(with-meta
            (if (:virtualize? @props)
              [virtualization-example @props]
              [paths-example @props])
            {:key @props})
          (if (:virtualize? @props)
            [virtualization-example-code @props]
            [paths-example-code @props])]]
        [rc/v-box
         :gap "12px"
         :style {:min-width        "550px"
                 :max-width        "fit-content"
                 :padding          "15px"
                 :border-top       "1px solid #DDD"
                 :background-color "#f7f7f7"}
         :children
         [[prop-checkbox {:db props-db
                          :id :virtualize?}]
          [prop-checkbox {:db props-db
                          :id :show-root-headers?}]
          [prop-checkbox {:db props-db
                          :id :show-row-branches?}]
          [prop-checkbox {:db props-db
                          :id :show-column-branches?}]
          [prop-slider {:db          props-db
                        :id          :row-tree-depth
                        :default     3
                        :default-on? false
                        :min         0
                        :max         10}]
          [prop-slider {:db          props-db
                        :id          :column-tree-depth
                        :default     3
                        :default-on? false
                        :min         0
                        :max         10}]
          [prop-slider {:db          props-db
                        :id          :row-height
                        :default     20
                        :default-on? false
                        :min         10
                        :max         100}]
          [prop-slider {:db          props-db
                        :id          :column-width
                        :default     40
                        :default-on? false
                        :min         10
                        :max         100}]
          [prop-slider {:db          props-db
                        :id          :row-header-width
                        :default     40
                        :default-on? false
                        :min         10
                        :max         100}]
          [prop-checkbox {:db      props-db
                          :id      :row-header-widths
                          :default nil
                          :value   [80 30 200]}]
          [prop-slider {:db          props-db
                        :id          :column-header-height
                        :default     20
                        :default-on? false
                        :min         10
                        :max         100}]
          [prop-checkbox {:db      props-db
                          :id      :column-header-heights
                          :default nil
                          :value   [90 20 30]}]
          [prop-checkbox {:db props-db
                          :id :resize-row-height?}]
          [prop-checkbox {:db props-db
                          :id :resize-column-header-height?}]]]]])))

(defn make-source-data []
  [[(rand) (rand) (rand) (rand)]
   [(rand) (rand) (rand) (rand)]
   [(rand) (rand) (rand) (rand)]
   [(rand) (rand) (rand) (rand)]])

(def source-data (r/atom (make-source-data)))

(defn lerp-red-green [val & [lower-bound upper-bound]]
  (let [lerp (fn [a b t] (+ a (* (- b a) t)))
        lower-bound (or lower-bound 0)
        upper-bound (or upper-bound 1)
        mid-point (/ (+ lower-bound upper-bound) 2)]
    (if (<= val mid-point)
      (let [t (/ (- val lower-bound)
                 (- mid-point lower-bound))]
        [1 (lerp 0 1 t) 0])
      (let [t (/ (- val mid-point)
                 (- upper-bound mid-point))]
        [(lerp 1 0 t) 1 0]))))

(defmulti multimodal-cell (comp :mode first :row-path))

(defmethod multimodal-cell :heatmap
  [{:keys [column-path row-path]}]
  (let [summary (get-in @source-data [(last column-path)
                                      (last row-path)])]
    [:div {:style {:text-align "center"
                   :background-color
                   (->> (lerp-red-green summary)
                        (map (partial * 255))
                        (str/join ", ")
                        (#(str "rgb(" % ")")))}}
     (number-format summary {:precision 2})]))

(defmethod multimodal-cell :bar-line
  [{:keys [column-path row-path]}]
  (let [summary (get-in @source-data [(last column-path)
                                      (last row-path)])]
    [rc/h-box
     :style {:position :relative}
     :children
     [[:div {:style {:position "absolute"}} (number-format summary {:precision 2})]
      [rc/box
       :size (str (* 100 summary) "%")
       :style {:background-color "cyan"
               :overflow-x "visible"}
       :child " "]
      [rc/box
       :size "auto"
       :style {:background-color "lightgrey"}
       :child ""]]]))

(defn sparkline [data]
  (let [max-val (apply max data)
        min-val (apply min data)
        scale-x (fn [index] (* index (quot 100 (dec (count data)))))
        scale-y (fn [val] (- 100 (* (/ (- val min-val) (- max-val min-val)) 100)))]
    [:svg {:viewBox             "0 0 110 110"
           :preserveAspectRatio "none"
           :width               "100%"
           :height              "100%"}
     [:polyline {:points          (str/join " "
                                            (map (fn [index]
                                                   (str (scale-x index)
                                                        ","
                                                        (scale-y (nth data index))))
                                                 (range (count data))))
                 :stroke          "black"
                 :stroke-width    2
                 :stroke-linejoin "round"
                 :stroke-linecap  "round"
                 :vector-effect   "non-scaling-stroke"
                 :fill            "none"}]]))

(defmethod multimodal-cell :spark-line
  [{:keys [column-path]}]
  [sparkline (get @source-data (last column-path))])

(defmethod multimodal-cell :button
  [{:keys [column-path]}]
  [:button {:style {:z-index 99}
            :on-click #(swap! source-data assoc (last column-path)
                              [(rand) (rand) (rand) (rand)])}
   "Refresh"])

(defn app-demo []
  [rc/v-box
   :gap "12px"
   :children
   [(let [{:keys [add multiply lookup]} operators
          {:keys [one two three four]}  operands]
      [nested-v-grid
       {:column-tree [{:id "left"}
                      [add      [one] [two]]
                      [multiply [one] [two]]
                      [lookup   [one] [two]]]
        :row-tree    [{:id "right"} [three] [four]]
        :parts       {:cell {:style {:text-align "center"}}}
        :cell-label  (fn [{:keys [column-path row-path]}]
                       (let [{:keys [operator left right]}
                             (->> (into row-path column-path)
                                  (apply merge))]
                         (operator left right)))}])
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    [rc/p "Here, we use " [:i "specs"] " like " [:code "multiply"]
     " and " [:code "lookup"] " to build a multi-modal view of the source data."
     " In other words, a " [:code ":column-spec"] " or " [:code ":row-spec"] " can express not just " [:i "what"] " to show in the cell, but also " [:i "how"] " to show it."]
    [rdu/code
     "(def lookup-table
  [[\"🚓\" \"🛵\" \"🚲\" \"🛻\" \"🚚\"] [\"🍐\" \"🍎\" \"🍌\" \"🥝\" \"🍇\"]
   [\"🐕\" \"🐎\" \"🧸\" \"🐈\" \"🐟\"]])"
     "(def operators
  {:add      {:operator + :label \"Addition\"}
   :multiply {:operator * :label \"Multiplication\"}
   :lookup   {:operator (fn [l r]
                          (get-in lookup-table [l r]))
              :label    \"Lookup\"}})"
     "(def operands
  {:one   {:left 1 :label \"1\"}
   :two   {:left 2 :label \"2\"}
   :three {:right 3 :label \"3\"}
   :four  {:right 4 :label \"4\"}})"
     "(let [{:keys [add multiply lookup]} operators
      {:keys [one two three four]}  operands]
  [nested-v-grid
   {:column-tree [{:id \"left\"}
                  [add      [one] [two]]
                  [multiply [one] [two]]
                 [lookup [one] [two]]]
    :row-tree    [{:id \"right\"} [three] [four]]
    :parts       {:cell {:style {:text-align \"center\"}}}
    :cell-label  (fn [{:keys [column-path row-path]}]
                   (let [{:keys [operator left right]}
                           (->> (into row-path column-path)
                                (apply merge))]
                     (operator left right)))}])"]
    [rc/line]
    [nested-v-grid
     {:row-header-width   85
      :show-root-headers? false
      :column-tree        [:root 1 2 3]
      :row-tree           [:root
                           [{:mode :button :label "Button"}]
                           [{:mode :heatmap :label "Heat Map"} 1 2 3]
                           [{:mode :bar-line :label "Bar Line"} 1 2 3]
                           [{:mode :spark-line :label "Spark Line"}]]
      :cell-label         multimodal-cell}]
    [source-reference "for above nested-grid" "src/re_demo/nested_grid.cljs"]
    [rc/p "Unlike many spreadsheet libraries, " [:code "nested-grid"] " has no concept"
     " of a \"heat map\" or \"spark line\". "
     "There's less for you to use, but also less you need to learn."]
    [rc/p
     " As long as you understand the concepts behind "
     [:code ":column-tree"] " and " [:code ":cell"]
     ", then the full power of reagent is at your disposal."
     " Your cell can:"
     [:ul
      [:li "render complex graphics and UI"]
      [:li "efficiently update (by dereferencing a reagent atom)"]
      [:li "flexibly serialize its value with " [:code ":on-export-cell"]]]]
    [rc/line]
    [nested-v-grid
     {:column-tree           [:root "Longest" "Shortest" "Median" "Random"]
      :row-tree              [:root "Capitalize" "Emphasize" "Colorize"]
      :show-root-headers?    false
      :show-row-branches?    true
      :show-column-branches? true
      :column-width          85
      :cell                  (fn [{:keys [row-path value class style attr children]}]
                               [:div (merge {:class class :style style} attr)
                                (into (case (last row-path)
                                        "Capitalize" [:span {:style {:text-transform "capitalize"}}
                                                      value]
                                        "Emphasize"  [:i [:strong value]]
                                        "Colorize"   [:span {:style {:color (rand-color)}}
                                                      value])
                                      children)])
      :cell-label            (fn [{:keys [column-path]}]
                               (case (last column-path)
                                 "Longest"  (last (sort-by count lorem-ipsum))
                                 "Shortest" (first (sort-by count lorem-ipsum))
                                 "Median"   (nth (vec (sort-by count lorem-ipsum))
                                                 (/ (count lorem-ipsum) 2))
                                 "Random"   (rand-nth lorem-ipsum)))}]
    [:pre {:style {:margin-top 19}} "[nested-grid
  :column-tree [\"Longest\" \"Shortest\" \"Median\" \"Random\"]
  :row-tree    [\"Capitalize\" \"Emphasize\" \"Colorize\"]
  :cell        (fn [{:keys [row-path value]}]
                 (case (last row-path)
                   \"Capitalize\" [:span {:style {:text-transform
                                                \"capitalize\"}}
                                 value]
                   \"Emphasize\"  [:i [:strong value]]
                   \"Colorize\"   [:span {:style {:color (rand-color)}}
                                 value]))
  :cell-value  (fn [{:keys [column-path]}]
                 (case (last column-path)
                   \"Longest\"  (last (sort-by count lorem-ipsum))
                   \"Shortest\" (first (sort-by count lorem-ipsum))
                   \"Median\"   (nth (vec (sort-by count lorem-ipsum))
                                   (/ (count lorem-ipsum) 2))
                   \"Random\"   (rand-nth lorem-ipsum)))]"]
    [rc/p "You can compose the " [:code ":cell"] " and " [:code ":cell-label"]
     " to semantically separate a semantic separation of data processing from rendering. "
     " Both parts accept the same arguments, except "
     "When " [:code ":cell-value"] " is provided, " [:code "nested-grid"]
     " passes its return value to " [:code ":cell"]
     " via a " [:code ":value"] " prop."]
    [rc/p
     "In this case, " [:code ":cell-value"]
     " is responsible for choosing an item of the source data, and "
     [:code ":cell"] " is responsible for styling the resulting " [:code ":value."]]]])

(defn args-column []
  [args-table
   nvg/args-desc
   {:total-width       "550px"
    :name-column-width "180px"}])

(defn more-column []
  [rc/v-box
   :children
   [[title2 "More"]
    [title3 "Debugging"]
    [rc/p "When " [:code "goog.DEBUG"] " is true, alt-clicking any cell will print its "
     [:code ":row-path"] " and " [:code ":header-path"] " to the console."]
    [title3 "Rendering Header Cells"]
    [rc/p "Just like " [:code ":cell"] ", the "
     [:code ":column-header"] " and " [:code ":row-header"] " props "
     "are functions of their paths."]
    [rc/p "The difference is, they only accept a single " [:code ":path"] " argument."]
    [rc/p "When unspecified, these parts have default behavior:"
     [:ul
      [:li "take the last item in the " [:code ":path"] "."]
      [:li "if it's a map, get the " [:code ":label"] " key, or the  " [:code ":id"] " key."]
      [:li "if that fails, stringify the whole item."]]]

    [title3 "Branch paths can have cells, too"]
    [rc/p "Consider this " [:code ":column-tree"] ":"
     [:pre "[:plant [:fruit [:apple :banana] :vegetable [:potato]]]"]]
    [rc/p "Normally, " [:code "nested-column"] " would derive 3 " [:code ":column-path"] "s. "
     "More specifically, these are the 3 " [:i "leaf paths"] ":"]
    [rc/nested-v-grid
     {:column-header-height 20
      :row-height 60
      :column-tree    [:plant [:fruit [:apple :banana] :vegetable [:potato]]]
      :cell       (comp str :column-path)}]
    [:br]
    [rc/p "However: if you pass an optional " [:code ":show-branch-paths?"]
     " key, then 6 paths will be derived - 3 " [:i "leaf paths"] " and 3 " [:i "branch paths"] ":"]
    [rc/nested-v-grid
     {:column-header-height 20
      :row-height 60
      :show-branch-paths? true
      :column-tree    [:plant [:fruit [:apple :banana] :vegetable [:potato]]]
      :cell       (fn [{:keys [column-path]}]
                    [:div
                     (if (= 3 (count column-path))
                       [:div {:style {:font-size 10
                                      :color "green"}} "leaf!"]
                       [:div {:style {:font-size 10
                                      :color "brown"}} "branch!"])
                     (str column-path)])}]
    [title3 "Special keys"]
    [rc/p "If your " [:code ":column-spec"] " or " [:code ":row-spec"] " is a map, you can include a few special keys. "
     "These will cause " [:code "nested-grid"] " to handle your column or row with special behavior."
     [:ul
      [:li [:strong [:code ":width"]]  ": sets the initial width."]
      [:li [:strong [:code ":height"]] ": sets the initial height."]
      [:li [:strong [:code ":align"]]  ": when declard in a " [:code ":column-spec"] ", aligns a column header and all its corresponding cells. Can be either "
       [:code ":right"] ", " [:code ":left"] " or " [:code ":center"] ". This tends to work out of the box whenever your " [:code ":cell"] " function returns a string. "
       "If your " [:code ":cell"] " fn returns a hiccup, you may be better off controlling alignment within that hiccup."]
      [:li [:strong [:code ":align-column"]]  ": like " [:code ":align"] ", but more explicit."]
      [:li [:strong [:code ":align-column-header"]]  ": like " [:code ":align"] ", but only for the column-header cells."]
      [:li [:strong [:code ":show?"]]  ": show (" [:code "true"] ") or hide (" [:code "false"] ") cells, overriding any other context, settings, or branch/leaf position."]]]
    [rc/p "Here's the first table, but instead of the column-spec " [:code ":fruit"] ", we use a map with special keys. This lets us show a single branch path, while the others remain hidden:"
     [:pre "[:plant [{:id :fruit :show? true :width 200} [:apple :banana] :vegetable [:potato]]]"]]
    [rc/nested-v-grid
     {:column-header-height 20
      :column-tree    [:plant [{:id :fruit :show? true :width 200} [:apple :banana] :vegetable [:potato]]]}]
    [:br]
    [rc/p "If you prefer to separate concerns, you can instead include these keys in the metadata of your column- or row-spec:"
     [:pre "[:plant [^{:show? true :width 200} {:id :fruit} [:apple :banana] :vegetable [:potato]]]"]]
    [title3 "Paths have state"]
    [rc/p "Within an atom, " [:code "nested-grid"] " stores a map for each "
     [:code ":column-path"] " and each " [:code ":row-path"] "."]
    [rc/p "Keys in this map will override any settings, whether declared in the props, or in a column- or row-spec."]
    [rc/p "So far (" [:span {:style {:color :red}} "alpha"]
     "), we only store a " [:code ":width"] " key. "
     "Each column header has a draggable button, allowing you to update a column's width by hand."]]])

(def header-tree
  [{:id :z :label "ZZ" :show? false}
   [:g
    [{:id :x :label "HIHI" :size 99}
     {:label "something" :size 20}]
    [{:id :y :label "HIHI"}
     [{:label "sometihng-else" :size 40}]]
    [:z {:size 20}]]
   [:h
    [:x {:id 20}]
    [:y 40]
    [:z 20]]
   [:i
    [:x 20]
    [:y 40]
    [:z 20]]
   [:j
    [:x 20]
    [:y 40]
    [:z 20]]])

(def header-tree-big
  (into header-tree
        (repeatedly 1000 #(do [(keyword (gensym))
                               [:x 20]
                               [:y 40]
                               [:z 20]]))))

(def header-tree-huge
  (into header-tree
        [(into [:hhh]
               (repeatedly 10000 #(do [(keyword (gensym))
                                       [:x 20]
                                       [:y 40]
                                       [:z 20]
                                       [:h 10]])))]))

(def ww (r/atom 500))
(def wh (r/atom 500))

(def row-header-widths (r/atom [20 30 40 50]))
(def column-header-heights (r/atom [20 50 70]))
(def row-tree (r/atom header-tree-big))
(def column-tree (r/atom [{:id :a :size 120}
                          [{:id :n :size 100} {:id :d :size 89} {:id :e :size 89}
                           {:id :f :size 89} {:id :g :size 89} {:id :h :size 89}]]))

(def export-fn (r/atom #()))

(defn export-cell [{:keys [row-path column-path row-index column-index]}]
  (let [label #(get (peek %) :id (peek %))]
    (str/join " " (filter some? [row-index column-index (label row-path) (label column-path)]))))

(defn virtualization-demo []
  [rc/v-box
   :children
   [[rc/button
     {:on-click @export-fn
      :style    {:width 81}
      :label    "export"}]
    [nested-v-grid
     {:column-tree           (into [{:label "Products"}]
                                   (nested-group-tree [:country] fake-products))
      :row-tree              (into [{:label "Vendors"}]
                                   (nested-group-tree [:company] fake-people))
      :cell                  (fn [_]
                               (let [hover? (r/atom nil)]
                                 (fn [{:keys [column-path row-path column-meta row-meta style]}]
                                   (let [{:keys [name product company country]}
                                         (->> (into column-path row-path)
                                              (apply merge))
                                         total-sales           (cond->> fake-sales
                                                                 country (group* :country country)
                                                                 product (group* :product product)
                                                                 company (group* :company company)
                                                                 name    (group* :name name)
                                                                 :do     (map :price)
                                                                 :do     (apply +))
                                         label                 (str "$" (number-format total-sales
                                                                                       {:precision 2}))
                                         {:keys [banter]}      (rand-nth
                                                                (group* :country country fake-banter))
                                         {:keys [branch-end?]} (merge column-meta row-meta)]
                                     [:div {:style          (merge style
                                                                   (when (or @hover? branch-end?)
                                                                     {:background-color "#eef"}))
                                            :on-mouse-enter #(reset! hover? true)
                                            :on-mouse-leave #(reset! hover? nil)}
                                      [rc/popover-anchor-wrapper
                                       :showing? hover?
                                       :position :below-center
                                       :anchor label
                                       :popover [rc/popover-content-wrapper
                                                 :no-clip? true
                                                 :body (str (or name company)
                                                            " sold " label (cond product (str " of " product)
                                                                                 country (str " in " country))
                                                            ". "
                                                            banter)]]]))))
      :parts                 {:wrapper {:style {:max-height 400
                                                :max-width  640}}}
      :show-root-headers?    false
      :show-column-branches? true
      :show-row-branches?    true
      :column-header-label   (fn [{:keys [path style]}]
                               [:div {:style style}
                                (str ((some-fn :product :grouping) (peek path)))])
      :row-header-label      (fn [{:keys [path style]}]
                               [:div {:style style}
                                (str ((some-fn :name :grouping) (peek path)))])
      :row-height            30
      :column-width          80
      :row-header-width      80}]
    #_[nested-v-grid {:row-tree                row-tree
                      :column-tree             column-tree
                      #_#_:row-tree-depth      4
                      :row-header-widths       row-header-widths
                      :column-header-heights   column-header-heights
                      #_#_:column-tree-depth   3
                      :show-row-branches?      true
                      :show-column-branches?   true
                      #_#_:hide-root?          false
                      :cell-label              #(str (gensym))
                      :on-init-export-fn       (fn [f] (reset! export-fn f))
                      :on-export-cell          export-cell
                      :on-export-row-header    export-cell
                      :on-export-column-header export-cell
                      :on-export-corner-header export-cell
                      :on-resize               (fn [{:keys [header-dimension size-dimension keypath size]}]
                                                 (case [header-dimension size-dimension]
                                                   [:column :height]
                                                   (swap! column-header-heights assoc-in keypath size)
                                                   [:row :width]
                                                   (swap! row-header-widths assoc-in keypath size)
                                                   [:row :height]
                                                   (swap! row-tree update-in keypath assoc :size size)
                                                   [:column :width]
                                                   (swap! column-tree update-in keypath assoc :size size)))
                      :parts                   {:wrapper {:style {:height @wh
                                                                  :width  @ww}}

                                                :row-header-label
                                                (fn [{:keys [row-path style]}]
                                                  [:div {:style style}
                                                   (let [{:keys [is-after?]} (meta row-path)
                                                         row-spec            (peek row-path)
                                                         the-label           (->> "placeholder"
                                                                                  (get row-spec :id)
                                                                                  (get row-spec :label))]
                                                     (str the-label (when is-after? " (Total)")))])
                                                :corner-header
                                                (fn [{:keys [edge row-index column-index style class attr] :as props}]
                                                  [:div (merge {:style style :class class} attr)
                                                   (when (= 2 row-index)
                                                     (get ["apple" "banan" "grapefruit" "coconut" "lemon"] column-index))])}}]
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    "Window width"
    [rc/slider {:model ww :on-change (partial reset! ww) :min 200 :max 800}]
    "Window height"
    [rc/slider {:model wh :on-change (partial reset! wh) :min 200 :max 800}]
    [rc/title :level :level2 :label "Key differences:"]
    [:div {:style {:width 500}}
     [rc/title :level :level3 :label "Trees are hiccup-like."]
     [:p
      " The tree " [:code "[:a :b :c]"]
      "does " [:i "not"] " represent three siblings. Instead, " [:code ":a"]
      " is the parent, and " [:code ":b :c"] " are children. Explicitly, "
      "the branch function is " [:code "sequential?"]
      " and the children function is " [:code "rest"] "."]
     [rc/title :level :level3 :label "Root headers are hidden by default."]
     [:p
      " For instance, " [:code ":row-tree [:a [:b 1 2] [:c 8 9]]"]
      " displays " [:code ":b :c"] " as two top-level headers, each with two children."
      "The root header, " [:code ":a"] ", does not appear."]
     [rc/title :level :level3 :label "Header main-size can only declared in the tree."]
     [:code ":row-height"] " and " [:code ":column-width"]
     " are the main-sizes."
     " For instance: " [:code ":row-tree [{:id :a} {:id :b} {:id :c  :size 45}]"]
     " makes three rows. The first two have a default height, and the third has "
     "a height of 45."
     [rc/title :level :level3 :label "Header cross-size can be declared as a prop."]
     [:p [:code ":row-header-width"] " and " [:code ":column-header-height"]
      " are the cross-sizes. To control the default cross-size, pass an integer for either key. "]
     [:p
      "There are also plural props, "
      [:code ":row-header-widths"] " and " [:code ":column-header-heights"] ". "
      "To control each header's cross-size individually, pass a vector of integers (or a reagent/atom). "
      "Each vector must be as long (or longer) than the corresponding maximum tree-depth. "]
     [:p
      "For instance, " [:code ":row-tree [:apple [:banana 1 2] [:coconut 8 9]]"] " has a max depth of 3. "
      "Note that keywords appear at tree depths 1 and 2, and numbers at a depth of 3. "
      "In this case, you can pass " [:code " :row-header-widths [40 40 20]"] ". "
      "This would make the keyword headers 40-wide, and the number headers 20-wide."]
     [rc/title :level :level3 :label [:span "To handle header size changes, pass a function to " [:code ":on-resize"] "."]]
     [:p [:code ":on-resize"] " takes keyword arguments:"]
     [:ul
      [:li [:code ":header-dimension"] " - either " [:code ":row"] " or " [:code ":column"]]
      [:li [:code ":size-dimension"] " - either " [:code ":width"] " or " [:code ":height"]]
      [:li [:code ":cross-size?"] " - True when you change column-header height or row-header width."]
      [:li [:code ":keypath"] " - Vector of indices. Points to a location in a header-tree when resizing a main-size."
       " Points to a location in " [:code ":row-header-widths"] " or " [:code ":column-header-heights"]
       " when resizing a cross-size."]]]]])

(defn demos []
  (let [tabs    [{:id :options :label "Options" :view options-demo}
                 {:id :basic :label "Basics" :view basic-demo}
                 {:id :app :label "Applications" :view app-demo}]
        !tab-id (r/atom (:id (first tabs)))
        !tab    (r/reaction (u/item-for-id @!tab-id tabs))]
    (fn []
      (let [{:keys [view label]} @!tab]
        [rc/v-box
         :children
         [[rc/horizontal-tabs
           :src       (rc/at)
           :model     !tab-id
           :tabs      tabs
           :style     {:margin-top "12px"}
           :on-change #(reset! !tab-id %)]
          [title2 label]
          [view]]]))))

(defn panel
  []
  (let [tabs [{:id :intro :label "Introduction" :view intro-column}
              #_{:id :concepts :label "Concepts" :view concepts-column}
              #_{:id :more :label "More" :view more-column}
              {:id :parameters :label "Parameters" :view args-column}]
        !tab-id (r/atom (:id (first tabs)))
        !tab    (r/reaction (u/item-for-id @!tab-id tabs))]
    (fn []
      [rc/v-box
       :src      (rc/at)
       :size     "auto"
       :gap      "10px"
       :children
       [[panel-title "[nested-v-grid ... ]"
         "src/re_com/nested_v_grid.cljs"
         "src/re_demo/nested_v_grid.cljs"]
        [rc/h-box
         :src      (rc/at)
         :gap      "50px"
         :children
         [[rc/v-box
           :src      (rc/at)
           :children
           [[rc/horizontal-tabs
             :src       (rc/at)
             :model     !tab-id
             :tabs      tabs
             :style     {:margin-top "12px"}
             :on-change #(reset! !tab-id %)]
            [(:view @!tab)]]]
          [demos]]]
        #_[parts-table "nested-grid" nested-grid-parts-desc]
        [parts-table "nested-v-grid" nvg/parts-desc]]])))
