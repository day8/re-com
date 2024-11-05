(ns re-demo.nested-grid
  (:require
   [clojure.string :as str]
   [re-com.core   :as rc :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
   [re-com.util :as u]
   [re-com.theme :as theme]
   [re-com.nested-grid.util :as ngu]
   [reagent.core :as r]
   [re-com.nested-grid  :as grid :refer [nested-grid nested-grid-args-desc nested-grid-parts-desc]]
   [re-demo.utils :refer [source-reference panel-title title2 title3 args-table parts-table github-hyperlink status-text new-in-version]]))

(def arg-style {:style {:display     "inline-block"
                        :font-weight "bold"
                        :min-width   "140px"}})

(def header->icon {:spot     "❌"
                   :price    "💰"
                   :foreign  "🌍"
                   :domestic "🏠"
                   :kilo     "𝞙"
                   :ton      "𝞣"
                   :apple    "🍎"
                   :banana   "🍌"
                   :grape    "🍇"
                   :red      "🔴"
                   :white    "⚪"})

(defn fruit-demo []
  [nested-grid {:column-tree [{:id :fruit :hide-cells? true}
                              [{:id :red}
                               {:id :white}]]
                :row-tree    [[:price
                               [:foreign
                                [:kilo
                                 :ton]]
                               [:domestic
                                [:kilo
                                 :ton]]]]
                :cell    (fn fruit-cell [{:keys [row-path column-path]}]
                           (->> (concat column-path row-path)
                                (map #(header->icon % (header->icon (get % :id))))
                                (apply str)))}])

(defn concepts-column []
  [v-box
   :children
   [[title2 "Concepts"]
    [p "To use " [:code "nested-grid"]
     ", you’ll need to understand some key concepts - "
     [:code ":column-tree"] ","
     [:code ":column-spec"] ","
     [:code ":column-path"] ", etc..."]
    [title2 "Column Spec"]
    [p "A " [:code ":column-spec"] " describes a single column."]
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
    [p "A " [:code ":column-tree"] "describes a nested arrangement of columns."
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
    [p "A " [:code ":column-path"] " describes a distinct ancestry within a "
     [:code ":column-tree"] "."
     [:ul
      [:li "For the " [:code ":column-tree"] " above, " [:code "[:a [1 2] :b [3 4]]"] ", its " [:code ":column-path"] "s are: "
       [:pre "[[:a] [:a 1] [:a 2] [:b] [:b 3] [:b 4]]"]]]]
    [title2 "Row"]
    [p "Everything described above applies to rows, as well. " [:code ":row-spec"] ", " [:code ":row-tree"] " and " [:code ":row-path"]
     " have all the same properties as their column equivalents."]
    [title2 "Cell & Header Functions"]
    [p [:code "nested-grid"] " accepts " [:code ":column-header"] ", " [:code ":row-header"] ", " [:code ":header-spacer"] " and " [:code ":cell"] " props. Each is a function." [:code "nested-grid"] " calls each function to render the following locations:"
     [nested-grid
      :header-spacer (constantly [:div {:style {:color "grey"}} ":header-spacer"])
      :column-width 100
      :column-tree [":column-header" [{:id (gensym) :label ":column-header"}
                                      {:id (gensym) :label ":column-header"}]]
      :row-tree [":row-header" [{:id (gensym) :label ":row-header"}
                                {:id (gensym) :label "row-header"}]]
      :cell        (constantly ":cell")]
     [p "Each prop has a reasonable default, except for " [:code ":cell"] "."
      "Your " [:code ":cell"] " function will be passed two keyword arguments, "
      [:code ":column-path"] " and " [:code ":row-path"] ". It can return either a string or a hiccup."]]]])

(defn intro-column []
  [v-box
   :children
   [[title2 "Introduction"]
    [status-text "alpha" {:color "red"}]
    [new-in-version "v2.20.0"]
    [p [:code "nested-grid"]
     " " "provides a table with nested, hierarchical columns and rows."
     " " "The archetypical use-case would be to display a "
     [:a {:href "https://en.wikipedia.org/wiki/Pivot_table"} "pivot table"] "."
     " " "However, " [:code "nested-grid"] " provides a lean abstraction that could"
     " " "suit a variety of problems."]
    [p "Essentially, each cell has a unique pair of" " " [:i "paths"]
     " " "within the hierarchy." " " "The value of each cell is a"
     " " [:i "function"] " " "of its" " " [:i "paths"] "."]
    [title2 "Characteristics"]
    [p "Unlike" " " [:code "v-table"] ", "
     [:code "nested-grid"] ":"
     [:ul {:style {:width 400}}
      [:li "Uses" " " [:a {:href "https://www.w3schools.com/css/css_grid.asp"} "css grid"]
       " " "for layout."]
      [:li "Has adjustible column & row widths."]
      [:li "Is optimized for hundreds of cells, not millions."]
      [:li "Does not virtualize rows (" [:span {:style {:color "red"}} "...yet"] ")."
       " It renders everything in a single pass."]
      [:li "Does not re-render when you scroll or click. Even if that first render is expensive, "
       "the UI should be snappy once it completes."]]]
    [title2 "Quick Start"]
    [p "To use " [:code "nested-grid"] ", at a minimum, you must declare:"
     [:ul
      [:li [:code ":column-tree"] ": a vector describing the column structure."]
      [:li [:code ":row-tree"] ": a vector describing the row structure."]
      [:li [:code ":cell"] ": a function which, given a "
       [:code ":column-path"] " and a " [:code ":row-path"]
       ", renders one cell, either as a string or a hiccup."]]
     "See the " [:strong "Basic Demo"] " for examples,"
     " and the " [:strong "Concepts"] " section for in-depth explanations."]]])

(def color-mixer
  {:red    {:red    :red
            :blue   :purple
            :yellow :orange}
   :yellow {:red    :orange
            :blue   :green
            :yellow :yellow}
   :blue   {:red    :purple
            :blue   :blue
            :yellow :green}})

(def special-colors
  {"lightred"    "pink"
   "lightorange" "peachpuff"
   "lightpurple" "lavender"
   "darkpurple"  "brown"
   "darkyellow"  "gold"})

(defn mix-colors [color1 color2]
  (name (get-in color-mixer [(keyword color1) (keyword color2)])))

(defn color-demo []
  [rc/v-box
   :children
   [[nested-grid
     :column-tree ["red" "yellow" "blue"]
     :row-tree    ["red" "yellow" "blue"]
     :cell    (fn color-cell [{:keys [row-path column-path]}]
                (mix-colors (last row-path)
                            (last column-path)))]
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    [p "Here's a grid with flat columns and rows."
     " The " [:code ":cell"] " function closes over some external business logic ("
     [:code "mix-colors"] ") to express a string."
     " Since there is only one level of nesting, " [:code "column-path"]
     " contains a single " [:i "header value"] " - for instance, "
     [:code "[:red]"] "."]
    [:pre "[nested-grid
 :column-tree [\"red\" \"yellow\" \"blue\"]
 :row-tree    [\"red\" \"yellow\" \"blue\"]
 :cell    (fn color-cell [{:keys [column-path row-path]}]
             (mix-colors (last column-path)
                         (last row-path)))]"]]])

(defn color-shade-cell [{:keys [row-path column-path]}]
  (let [[hue-a]       row-path
        [shade hue-b] column-path
        hue           (mix-colors hue-a hue-b)
        shade         (when-not (= :medium shade) (name shade))
        color         (str shade hue)
        color         (get special-colors color color)]
    [:div {:style {:height           "100%"
                   :width            "100%"
                   :text-align       "center"
                   :background-color color}}
     [:span {:style {:font-size   "10px"
                     :color       "white"
                     :text-shadow "1px 1px 2px black"}}
      color]]))

(defn color-shade-demo []
  [v-box
   :children
   [[nested-grid
     {:column-tree [:medium [:red :yellow :blue]
                    :light [:red :yellow :blue]
                    :dark [:red :yellow :blue]]
      :row-tree    [:red :yellow :blue]
      :cell    color-shade-cell}]
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    [rc/v-box
     :children
     [[p "Here, " [:code ":column-tree"] "is a nested " [:i "configuration"] " of " [:i "header values."]]
      [p "Since the " [:i "configuration"] " has 2 levels of nesting,"
       " each " [:code ":column-path"] " is 2-long. For instance, "
       [:code "[:medium :yellow]"] ". "]
      [p [:code ":cell"] " returns a hiccup."]
      [p "Calling " [:code "(color-shade-cell {:column-path [:medium :yellow] :row-path [:blue]})"]
       "should return a " [:span {:style {:color "green"}} "green"] " hiccup."]
      [:pre "[nested-grid
 :column-tree [:medium [:red :yellow :blue]
           :light  [:red :yellow :blue]
           :dark   [:red :yellow :blue]]
 :row-tree    [:red :yellow :blue]
 :cell    color-shade-cell]"]]]]])

(def lookup-table [["🚓" "🛵" "🚲" "🛻" "🚚"]
                   ["🍐" "🍎" "🍌" "🥝" "🍇"]
                   ["🐕" "🐎" "🧸" "🐈" "🐟"]])

(def add {:operator + :label "Addition"})
(def multiply {:operator * :label "Multiplication"})
(def lookup {:operator (fn [l r] (get-in lookup-table [l r]))
             :label    "Lookup"})
(def one {:left 1 :label "1"})
(def two {:left 2 :label "2"})
(def three {:right 3 :label "3"})
(def four  {:right 4 :label "4"})

(defn multimodal-demo []
  [v-box
   :gap "12px"
   :children
   [[nested-grid
     :column-tree [add      [one two]
                   multiply [one two]
                   lookup   [one two]]
     :row-tree    [three four]
     :row-header (comp :label last :row-path)
     :column-header (comp :label last :column-path)
     :row-header 20
     :column-header-height 25
     :row-header-width 100
     :parts {:cell-wrapper {:style {:text-align "center"}}}
     :cell    (fn [{:keys [column-path row-path]}]
                (let [{:keys [operator left right]} (->> (into row-path column-path)
                                                         (apply merge))]
                  (operator left right)))]
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    [p "Here, we use " [:i "specs"] " like " [:code "multiply"]
     " and " [:code "lookup"] " to build a multi-modal view of the source data."
     " In other words, a " [:code ":column-spec"] " or " [:code ":row-spec"] " can express not just " [:i "what"] " to show in the cell, but also " [:i "how"] " to show it."]
    [:pre "(def lookup-table [[\"🚓\" \"🛵\" \"🚲\" \"🛻\" \"🚚\"]
                   [\"🍐\" \"🍎\" \"🍌\" \"🥝\" \"🍇\"]
                   [\"🐕\" \"🐎\" \"🧸\" \"🐈\" \"🐟\"]])
(def add      {:label \"Addition\"       :operator +})
(def multiply {:label \"Multiplication\" :operator *})
(def lookup   {:label \"Lookup\"
               :operator (fn [l r] (get-in lookup-table [l r]))})
(def one      {:label \"1\" :left 1})
(def two      {:label \"2\" :left 2})
(def three    {:label \"3\" :right 3})
(def four     {:label \"4\" :right 4})

[nested-grid
 :column-tree   [add      [one two]
                 multiply [one two]
                 lookup   [one two]]
 :row-tree      [three four]
 :column-header (comp :label last :column-path)
 :row-header    (comp :label last :row-path)
 :cell          (fn [{:keys [column-path row-path]}]
                  (let [{:keys [operator left right]} (->> column-path
                                                           (into row-path)
                                                           (apply merge))]
                    (operator left right)))]"]]])

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

       :re-com.nested-grid/header-spacer-wrapper
       {:style {:border-left      "none"
                :border-bottom    "none"
                :border-right     "none"
                :padding-left     "10px"
                :padding-top      1
                :background-color ($ :dark)
                :color            ($ :white)}})
     (theme/merge-props props))))

(def rf8-grid-parts
  {:re-com.nested-grid/nested-grid
   {:row-height           "20px"
    :column-header-height "20px"
    :row-header-width     0
    :show-zebra-stripes?  false}

   :re-com.nested-grid/cell-wrapper
   {:style {:border-left  "none"
            :border-right "none"}}

   :re-com.nested-grid/column-header-wrapper
   {:style {:border-right     "none"
            :padding-left     "10px"
            :padding-top      1
            :background-color "#768895"
            :color            :white}}

   :re-com.nested-grid/header-spacer-wrapper
   {:style {:border-right     "none"
            :padding-left     "10px"
            :padding-top      1
            :background-color "#768895"
            :color            :white}}})

(defn style-demo []
  [v-box
   :gap "12px"
   :children
   [[nested-grid
     {:theme                rf8-grid-theme
      #_#_:parts            rf8-grid-parts
      #_#_:row-header-width 0
      :header-spacer        (fn [{:keys [x]}]
                              (get ["Market" "Network"] x))
      :column-tree          (->> [{:id "Align Column" :width 120 :align-column :left}
                                  {:id "Default Alignment" :width 120}
                                  {:id "Align Column Header" :width 150 :align-column-header :right}]
                                 (map-indexed (fn [i item] (assoc item :index i)))
                                 vec)
      :cell-value           (fn [{:keys [row-path column-path]}]
                              (let [cell-values (some :cell-values (reverse row-path))
                                    index       (some :index (reverse column-path))]
                                (get cell-values index)))
      :show-branch-paths?   true
      :row-tree             [{:measure :market :market "Sydney" :label "Sydney"}
                             [{:measure     :station :station "TEN" :label "TEN"
                               :cell-values ["Lorem" "ipsum" "dolor"]}
                              {:measure     :station :station "ABC" :label "ABC"
                               :cell-values ["sit" "amet" " consectetur"]}
                              {:measure     :station :station "NINE" :label "NINE"
                               :cell-values (vec (range 1000 1003))}]]
      :cell                 (fn [{:keys [value]}] value)}]]])

(defn internals-demo []
  [v-box
   :gap "12px"
   :children
   [[nested-grid
     :column-tree [{:id "Tree" :width 130}
                   {:id "Leaf Paths" :width 150}
                   {:id "All Paths" :width 200}]
     :row-tree    [{:label "Basic" :tree [:a :b :c]}
                   {:label "Nested" :tree [:a [:b :c]]}
                   {:label "Branching" :tree [:a [:b] :c [:d]]}
                   {:label "Deep" :tree [1 [2 [3 [4 [5]]]]]}
                   {:label "Explicit" :tree [[:a [:b :c]]
                                             [:d [:e :f]]]}
                   {:label "Typed" :tree [:kw 42 "str" {:k :map}]}]
     :cell (fn [{:keys [column-path] [{:keys [tree]}] :row-path}]
             (case (:id (last column-path))
               "Tree"       (str tree)
               "Leaf Paths" (str (vec (grid/leaf-paths
                                       (grid/header-spec->header-paths tree))))
               "All Paths"  (str (grid/header-spec->header-paths tree))))]
    [p "This table demonstrates how " [:code "nested-grid"] " derives a vector of " [:code ":column-path"] "s from a " [:code ":column-tree"] "."]
    [line]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [:a :b :c]
       :show-branch-cells? true
       :row-tree    [1 2 3]
       :column-width 40
       :column-header-height 25
       :row-header-width 30
       :cell (fn [{:keys [column-path row-path]}]
               (str column-path row-path))]
      [:pre {:style {:margin-top 19}} "[nested-grid
 :column-tree [:a :b :c]
 :row-tree    [1  2  3]
 :cell (fn [{:keys [column-path row-path]}]
         (str column-path row-path))]"]]]
    [p "Here, the " [:code ":cell"] " function simply prints the " [:code ":column-path"] " and " [:code ":row-path"] " it gets passed."]
    [line]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [:a :b :c]
       :row-tree    [[1 [:x :y]]
                     [2 [:x :y]]]
       :column-width 55
       :column-header-height 25
       :row-header-width 30
       :cell    (fn [{:keys [column-path row-path]}]
                  (str column-path row-path))]
      [:pre {:style {:margin-top 19}} "[nested-grid
 :column-tree [:a :b :c]
 :row-tree    [1 [:x :y]
               2 [:x :y]]
 :cell (fn [{:keys [column-path row-path]}]
         (str column-path row-path))]"]]]
    [p "Same " [:code ":cell"] " function, but with a nested " [:code ":row-tree"] "."]
    [line]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [:a [1 2] :b [3 4]]
       :row-tree    [:x [5 6] :y [7 8]]
       :column-header-height 25
       :row-header-width 30
       :column-width 50
       :cell (fn [{:keys [column-path row-path]}]
               [:i {:style {:color     "grey"
                            :font-size 10}}
                (str column-path row-path)])]
      [:pre {:style {:margin-top 19}}
       "[nested-grid
 :column-tree [:a [1 2] :b [3 4]]
 :row-tree    [:x [5 6] :y [7 8]]
 :cell
 (fn [{:keys [column-path row-path]}]
   [:i {:style {:color \"grey\"
                :font-size 10}}
    (str column-path row-path)])]"]]]
    [p "This " [:code ":cell"] " function returns a hiccup, not just a string."]
    [line]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [:a :b :c]
       :row-tree    [[1 [:x :y]]
                     [2 [:x :y]]]
       :column-width 55
       :column-header-height 25
       :row-header-width 30
       :show-branch-paths? true
       :cell    (fn [{:keys [column-path row-path]}]
                  (str column-path row-path))]
      [:pre {:style {:margin-top 19}} "[nested-grid
 :show-branch-paths? true
 :column-tree [:a :b :c]
 :row-tree    [1 [:x :y]
               2 [:x :y]]
 :cell (fn [{:keys [column-path row-path]}]
         (str column-path row-path))]"]]]
    [p "When passed " [:code ":show-branch-paths? true"]
     ", more cells get rendered. " [:code "[1]"] " and " [:code "[2]"] " count as "
     "branch paths, since they have children in the " [:code ":row-tree"] ". By default, these are not shown."]]])

(defn basic-demo []
  [v-box
   :gap "12px"
   :children
   [[h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [2 4 6]
       :row-tree    [1 3 5]
       :cell (fn [{:keys [column-path row-path]}]
               (let [[column-spec] column-path
                     [row-spec]    row-path]
                 (* column-spec row-spec)))]
      [:pre {:style {:margin-top "19px"}} "[nested-grid
 :column-tree [2 4 6]
 :row-tree    [1 3 5]
 :cell (fn [{:keys [column-path row-path]}]
         (let [[column-spec] column-path
               [row-spec]    row-path]
           (* column-spec row-spec)))]"]]]
    [p "A simple times table. The " [:code ":cell"] " function gets called once for each cell, getting passed a "
     [:code ":column-path"] " and " [:code ":row-path"]
     ". In this case, each path is a vector of one number. For instance, "
     "the bottom cells each have a " [:code ":row-path"] " of " [:code "[5]"] "."]
    [line]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [0 1 2]
       :row-tree    [2 3 4]
       :cell (fn [{:keys [column-path row-path]}]
               (get-in lookup-table [(last column-path)
                                     (last row-path)]))]
      [:pre {:style {:margin-top "19px"}} "(def lookup-table [[\"🚓\" \"🛵\" \"🚲\" \"🛻\" \"🚚\"]
                   [\"🍐\" \"🍎\" \"🍌\" \"🥝\" \"🍇\"]
                   [\"🐕\" \"🐎\" \"🧸\" \"🐈\" \"🐟\"]])
[nested-grid
 :column-tree [0 1 2]
 :row-tree    [2 3 4]
 :cell (fn [{:keys [column-path row-path]}]
         (get-in lookup-table [(last column-path)
                               (last row-path)]))]"]]]
    [p "Here, instead of multiplying the path values, we use them to access an "
     "external lookup table. This is a common use-case: prepare a data frame independently from "
     [:code "nested-grid"] ", but with the intention of using "
     [:code "nested-grid"] " as a simple display layer."]]])

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

(def round #(cond-> % (number? %) (.toFixed % 2)))

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
     (round summary)]))

(defmethod multimodal-cell :bar-line
  [{:keys [column-path row-path]}]
  (let [summary (get-in @source-data [(last column-path)
                                      (last row-path)])]
    [h-box
     :children
     [[:div {:style {:position "absolute"}} (round summary)]
      [box
       :size (str (* 100 summary) "%")
       :style {:background-color "cyan"
               :overflow-x "visible"}
       :child " "]
      [box
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
  [{:keys [column-path row-path]}]
  [sparkline (get @source-data (last column-path))])

(defmethod multimodal-cell :button
  [{:keys [column-path row-path]}]
  [:button {:style {:z-index 99}
            :on-click #(swap! source-data assoc (last column-path)
                              [(rand) (rand) (rand) (rand)])}
   "Refresh"])

(defn app-demo []
  [v-box
   :gap "12px"
   :children
   [[nested-grid
     :show-selection-box? false
     :row-header-width 85
     :column-tree [1 2 3]
     :row-tree    [{:mode :button :label "Button"}
                   {:mode :heatmap :label "Heat Map"} [1 2 3]
                   {:mode :bar-line :label "Bar Line"} [1 2 3]
                   {:mode :spark-line :label "Spark Line"}]
     :cell multimodal-cell]
    [source-reference "for above nested-grid" "src/re_demo/nested_grid.cljs"]
    [p "Unlike many spreadsheet libraries, " [:code "nested-grid"] " has no concept"
     " of a \"heat map\" or \"spark line\". "
     "There's less for you to use, but also less you need to learn."]
    [p
     " As long as you understand the concepts behind "
     [:code ":column-tree"] " and " [:code ":cell"]
     ", then the full power of reagent is at your disposal."
     " Your cell can:"
     [:ul
      [:li "render complex graphics and UI"]
      [:li "efficiently update (by dereferencing a reagent atom)"]
      [:li "flexibly serialize its value with " [:code ":on-export-cell"]]]]
    [line]
    [nested-grid
     {:column-tree        ["Longest" "Shortest" "Median" "Random"]
      :show-branch-cells? true
      :column-width       85
      :row-tree           ["Capitalize" "Emphasize" "Colorize"]
      :cell               (fn [{:keys [row-path value]}]
                            (case (last row-path)
                              "Capitalize" [:span {:style {:text-transform "capitalize"}}
                                            value]
                              "Emphasize"  [:i [:strong value]]
                              "Colorize"   [:span {:style {:color (rand-color)}}
                                            value]))
      :cell-value         (fn [{:keys [column-path]}]
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
    [p "The " [:code ":cell-value"] " prop offers a semantic separation of data processing from rendering. "
     [:code ":cell-value"] " should be a function, with the same signature as " [:code ":cell"] ". "
     "When " [:code ":cell-value"] " is provided, " [:code "nested-grid"]
     " passes its return value to " [:code ":cell"]
     " via a " [:code ":value"] " prop."]
    [p
     "In this case, " [:code ":cell-value"]
     " is responsible for choosing an item of the source data, and "
     [:code ":cell"] " is responsible for styling the resulting " [:code ":value."]]]])

(defn demos []
  (let [tabs [{:id :basic      :label "Basic Demo" :view basic-demo}
              {:id :internals  :label "Internals"  :view internals-demo}
              {:id :multimodal :label "Multimodal" :view multimodal-demo}
              {:id :app        :label "Applications" :view app-demo}
              #_{:id :style      :label "Style" :view style-demo}]
        !tab-id  (r/atom (:id (first tabs)))
        !tab    (r/reaction (u/item-for-id @!tab-id tabs))]
    (fn []
      (let [{:keys [view label]} @!tab]
        [v-box
         :children
         [[rc/horizontal-tabs
           :src       (at)
           :model     !tab-id
           :tabs      tabs
           :style     {:margin-top "12px"}
           :on-change #(reset! !tab-id %)]
          [title2 label]
          [view]]]))))

(defn args-column []
  [args-table
   nested-grid-args-desc
   {:total-width       "550px"
    :name-column-width "180px"}])

(defn algorithm-column []
  [v-box
   :children
   []])

(defn more-column []
  [v-box
   :children
   [[title2 "More"]
    [title3 "Debugging"]
    [p "When " [:code "goog.DEBUG"] " is true, alt-clicking any cell will print its "
     [:code ":row-path"] " and " [:code ":header-path"] " to the console."]
    [title3 "Rendering Header Cells"]
    [p "Just like " [:code ":cell"] ", the "
     [:code ":column-header"] " and " [:code ":row-header"] " props "
     "are functions of their paths."]
    [p "The difference is, they can only expect to be passed a single path. "
     [:code ":column-header"] " only expects a " [:code ":column-path"]
     ", and "
     [:code ":row-header"] " only expects a " [:code ":row-path"] "."]
    [p "If you don't pass any " [:code ":column-header"] " prop,"
     " then it's handled by this default behavior:"
     [:ul
      [:li "take the last " [:code ":column-spec"] " in the " [:code ":column-path"] "."]
      [:li "if it's a map, get the " [:code ":label"] " key, or the  " [:code ":id"] " key."]
      [:li "if that doesn't work, stringify the whole item."]]]

    [title3 "Branch paths can have cells, too"]
    [p "Consider this " [:code ":column-tree"] ":"
     [:pre "[:plant [:fruit [:apple :banana] :vegetable [:potato]]]"]]
    [p "Normally, " [:code "nested-column"] " would derive 3 " [:code ":column-path"] "s. "
     "More specifically, these are the 3 " [:i "leaf paths"] ":"]
    [nested-grid
     :column-header-height 20
     :row-height 60
     :column-tree    [:plant [:fruit [:apple :banana] :vegetable [:potato]]]
     :cell       (comp str :column-path)]
    [:br]
    [p "However: if you pass an optional " [:code ":show-branch-paths?"]
     " key, then 6 paths will be derived - 3 " [:i "leaf paths"] " and 3 " [:i "branch paths"] ":"]
    [nested-grid
     :column-header-height 20
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
                    (str column-path)])]
    [title3 "Special keys"]
    [p "If your " [:code ":column-spec"] " or " [:code ":row-spec"] " is a map, you can include a few special keys. "
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
    [p "Here's the first table, but instead of the column-spec " [:code ":fruit"] ", we use a map with special keys. This lets us show a single branch path, while the others remain hidden:"
     [:pre "[:plant [{:id :fruit :show? true :width 200} [:apple :banana] :vegetable [:potato]]]"]]
    [nested-grid
     :column-header-height 20
     :column-tree    [:plant [{:id :fruit :show? true :width 200} [:apple :banana] :vegetable [:potato]]]]
    [:br]
    [p "If you prefer to separate concerns, you can instead include these keys in the metadata of your column- or row-spec:"
     [:pre "[:plant [^{:show? true :width 200} {:id :fruit} [:apple :banana] :vegetable [:potato]]]"]]
    [title3 "Paths have state"]
    [p "Within an atom, " [:code "nested-grid"] " stores a map for each "
     [:code ":column-path"] " and each " [:code ":row-path"] "."]
    [p "Keys in this map will override any settings, whether declared in the props, or in a column- or row-spec."]
    [p "So far (" [:span {:style {:color :red}} "alpha"]
     "), we only store a " [:code ":width"] " key. "
     "Each column header has a draggable button, allowing you to update a column's width by hand."]]])

#_(defn panel
    []
    (let [tabs [{:id :intro :label "Introduction" :view intro-column}
                {:id :concepts :label "Concepts" :view concepts-column}
                {:id :more :label "More" :view more-column}
                {:id :parameters :label "Parameters" :view args-column}]
          !tab-id (r/atom (:id (first tabs)))
          !tab    (r/reaction (u/item-for-id @!tab-id tabs))]
      (fn []
        [v-box
         :src      (at)
         :size     "auto"
         :gap      "10px"
         :children
         [[panel-title "[nested-grid ... ]"
           "src/re_com/nested_grid.cljs"
           "src/re_demo/nested_grid.cljs"]
          [h-box
           :src      (at)
           :gap      "50px"
           :children
           [[v-box
             :src      (at)
             :children
             [[rc/horizontal-tabs
               :src       (at)
               :model     !tab-id
               :tabs      tabs
               :style     {:margin-top "12px"}
               :on-change #(reset! !tab-id %)]
              [(:view @!tab)]]]
            [demos]]]
          [parts-table "nested-grid" nested-grid-parts-desc]]])))

(def row-seq (r/atom '()))

(def rows-loaded (r/atom 0))

(defn data-chunk [& {:keys [dimension index-offset size with-loader?] :or {size 10 with-loader? true}}]
  (for [chunk-index (range size)]
    (cond->
     {:index       (+ chunk-index index-offset)
      :chunk-index chunk-index
      :size        size
      :id          (gensym)
      :cell-size   (+ 25 (rand-int 75))
      :dimension   dimension}
      (and with-loader? (= chunk-index 0)) (assoc :loader? true))))

(defn load-row-chunk! [& {:keys [size] :or {size 10}}]
  (swap! row-seq concat (data-chunk {:size         size
                                     :index-offset @rows-loaded
                                     :dimension    :row}))
  (swap! rows-loaded + size))

(def column-seq (r/atom (data-chunk {:dimension :column :size 100})))

(defn test-cell [{:keys [row-path column-path]}]
  (let [{:keys           [loader?]
         row-index       :index
         row-size        :size
         row-chunk-index :chunk-index}    (peek row-path)
        {column-index       :index
         column-chunk-index :chunk-index} (peek column-path)
        loader?                           (and loader? (= 0 column-chunk-index))
        loaded?                           (r/reaction (pos? (- @rows-loaded row-index row-size)))
        background-color                  (r/atom "#cceeff")
        init-background!                  #(reset! background-color "#fff")]
    (r/create-class
     {:component-did-mount
      #(do (when (and loader? (not @loaded?))
             (load-row-chunk!))
           (init-background!))
      :reagent-render
      (fn [{:keys [children column-path row-path]}]
        [:div {:style {:grid-column      (ngu/path->grid-line-name column-path)
                       :grid-row         (ngu/path->grid-line-name row-path)
                       :padding          5
                       :font-size        10
                       :transition       "background-color 0.5s ease-in"
                       :background-color @background-color
                       :border           "thin solid black"
                       :border-top       (if (= 0 row-chunk-index)
                                           "thick solid black"
                                           "thin solid black")}}
         (str row-index " // " column-index)])})))

(defn linear-search-infinite-scroll-test [{:keys [cell row-height column-seq row-seq row-heights column-width column-widths] :as props}]
  (let [_ (load-row-chunk!)
        cell-container-ref  (r/atom nil)
        cell-container-ref! (partial reset! cell-container-ref)
        scroll-top          (r/atom 0)
        scroll-left         (r/atom 0)
        container-height    (r/atom nil)
        container-width     (r/atom nil)
        container-right     (r/reaction (+ @scroll-left @container-width))
        container-bottom    (r/reaction (+ @scroll-top @container-height))
        on-scroll!          #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                 (reset! scroll-left (.-scrollLeft (.-target %))))
        on-resize!          #(do (reset! container-height (.-height (.-contentRect (aget % 0))))
                                 (reset! container-width (.-width (.-contentRect (aget % 0)))))
        path-fn             vector
        size-fn             :cell-size
        column-v-margin     100
        row-v-margin        100
        left-bound          (r/reaction (max 0 (- @scroll-left column-v-margin)))
        right-bound         (r/reaction (+ @container-right column-v-margin))
        top-bound           (r/reaction (max 0 (- @scroll-top row-v-margin)))
        bottom-bound        (r/reaction (+ @container-bottom row-v-margin))
        column-window       (r/reaction (ngu/cumulative-sum-window @left-bound @right-bound size-fn (u/deref-or-value column-seq)))
        row-window          (r/reaction (ngu/cumulative-sum-window @top-bound @bottom-bound size-fn (u/deref-or-value row-seq)))]
    (r/create-class
     {:component-did-mount
      (fn [_]
        (.addEventListener @cell-container-ref "scroll" on-scroll!)
        (.observe (js/ResizeObserver. on-resize!) @cell-container-ref))
      :reagent-render
      (fn [{:keys [row-seq column-seq row-tree column-tree row-height column-width max-height max-width]}]

        (let [[column-num-left column-space-left columns-left
               column-num-within column-space-within columns-within
               column-num-right column-space-right columns-right] @column-window
              [row-num-top row-space-top rows-top
               row-num-within row-space-within rows-within
               row-num-bottom row-space-bottom rows-bottom]       @row-window
              grid-container                                      [:div {:ref   cell-container-ref!
                                                                         :style {:max-height            max-height
                                                                                 :max-width             max-width
                                                                                 :min-width             100
                                                                                 :min-height            100
                                                                                 :overflow              :auto
                                                                                 :width                 :fit-content
                                                                                 :display               :grid
                                                                                 :grid-template-columns (ngu/grid-template (concat [column-space-left]
                                                                                                                                   (interleave (map path-fn columns-within)
                                                                                                                                               (map size-fn columns-within))
                                                                                                                                   [column-space-right]))
                                                                                 :grid-template-rows    (ngu/grid-template (concat [row-space-top]
                                                                                                                                   (interleave (map path-fn rows-within)
                                                                                                                                               (map size-fn rows-within))
                                                                                                                                   [row-space-bottom]))}}]]
          (into grid-container
                (for [column-path (map path-fn columns-within)
                      row-path    (map path-fn rows-within)
                      :let        [props {:row-path    row-path
                                          :column-path column-path}]]
                  ^{:key [column-path row-path]}
                  [cell props]))))})))

(defn node->div [node {:keys [traversal path] :or {path []}}]
  (let [style {:border-top       "thin solid black"
               :border-left      "thin solid black"
               :margin-left      50
               :background-color :lightgreen}]
    (cond
      (ngu/leaf? node)   (let [leaf-path (conj path node)]
                           [:div {:style (merge style
                                                {:height     (ngu/leaf-size node)
                                                 :background (if (contains? (set (some-> traversal deref :windowed-paths)) leaf-path)
                                                               :lightgreen
                                                               :white)})}
                            (str leaf-path)])
      (ngu/branch? node) (let [[own-node & children] node
                               this-path             (conj path (first node))]
                           (into [:div {:style (merge style
                                                      {:position   :relative
                                                       :height     :fit-content
                                                       :background (if (contains? (set (some-> traversal deref :windowed-paths)) this-path)
                                                                     :lightgreen
                                                                     :white)})}
                                  [:div {:style {:height (ngu/leaf-size own-node)}}
                                   (str this-path)]]
                                 (map #(do [node->div % {:traversal traversal :path (conj path own-node)}])
                                      children))))))

(def scroll-top (r/atom 0))
(def scroll-left (r/atom 0))

(defn window-search-test [{:keys [tree]}]
  (let [container-ref      (r/atom nil)
        set-container-ref! (partial reset! container-ref)
        #_#_scroll-top         (r/atom nil)
        #_#_scroll-left        (r/atom nil)
        on-scroll!         #(do (reset! scroll-top (.-scrollTop (.-target %)))
                                (reset! scroll-left (.-scrollLeft (.-target %))))
        window-size        100
        window-ratio       0.5
        window-start       (r/reaction (* 2 @scroll-top))
        window-end         (r/reaction (+ window-size (* 2 @scroll-top)))
        height-cache       (volatile! {})
        path-seq           (def node-seq (:windowed-paths (ngu/walk-size {:window-start 0
                                                                          :window-end   999999
                                                                          :tree         tree
                                                                          :size-cache   (volatile! {})})))
        {:keys [sum-size]} (ngu/walk-size {:window-start 0
                                           :window-end   100000
                                           :tree         tree
                                           :size-cache   (volatile! {})})
        traversal          (r/reaction (ngu/walk-size {:window-start @window-start
                                                       :window-end   @window-end
                                                       :tree         tree
                                                       :size-cache   height-cache}))]
    (r/create-class
     {:component-did-mount
      (fn [_] (.addEventListener @container-ref "scroll" on-scroll!))
      :reagent-render (fn []
                        [:div
                         [:div {:ref   set-container-ref!
                                :style {:width      400
                                        :overflow-y :auto
                                        :position   :relative
                                        :height     sum-size}}
                          [:div {:style {:position           :fixed
                                         :margin-left        420
                                         :display            :grid
                                         :grid-template-rows (str/join " " (->> path-seq
                                                                                (map last)
                                                                                (map ngu/leaf-size)
                                                                                (map u/px)))}}
                           (node->div tree {:traversal traversal})]
                          [:div {:style {:position   :fixed
                                         :height     window-size
                                         :width      220
                                         :margin-left "470px"
                                         :border-top "thick solid red"
                                         :border-bottom "thick solid red"
                                         :margin-top (* 2 @scroll-top)
                                         :background "rgba(0,0,1,0.2)"}}
                           (str (* 2 @scroll-top))]
                          [:div {:style {:width      600
                                         :height     (* sum-size (+ 1 window-ratio))}}
                           (str sum-size)]]
                         [:br]
                         [:pre
                          (str @traversal)]])})))

(defn differences [v]
  (into [(first v)]
        (map (fn [[a b]] (- b a)) (partition 2 1 v))))

(def        wx                   (r/reaction @scroll-left))
(def        ww                   (r/atom 100))
(def        wy                   (r/reaction @scroll-top))
(def        wh                   (r/atom 100))

(defn new-nested-grid [{:keys [row-tree column-tree]}]
  (let [internal-row-tree    (r/atom (u/deref-or-value row-tree))
        internal-column-tree (r/atom (u/deref-or-value column-tree))

        row-traversal    (r/reaction (ngu/walk-size {:tree         @internal-row-tree
                                                     :window-start (* 2 @wy)
                                                     :window-end   (+ (* 2 @wy) @wh)}))
        column-traversal (r/reaction (ngu/walk-size {:tree         @internal-column-tree
                                                     :window-start @wx
                                                     :window-end   (+ @wx @ww)}))]
    (r/create-class
     {:component-did-update
      #(let [[_ {:keys [row-tree column-tree]}] (r/argv %)]
         (reset! internal-row-tree (u/deref-or-value row-tree))
         (reset! internal-column-tree (u/deref-or-value column-tree)))
      :reagent-render
      (fn [{:keys [row-tree column-tree cell row-header-width column-header-height]
            :or   {row-header-width     40
                   column-header-height 25
                   cell                 (fn [{:keys [row-path column-path]}]
                                          [:div {:style {:border            "thin solid grey"
                                                         :grid-row-start    (ngu/path->grid-line-name row-path)
                                                         :grid-column-start (ngu/path->grid-line-name column-path)}}])}}]
        (u/deref-or-value row-tree)
        (u/deref-or-value column-tree)
        (let [{row-depth               :depth
               row-space               :level->space
               row-height-total        :sum-size
               windowed-row-paths      :windowed-paths
               windowed-row-leaf-paths :windowed-leaf-paths}
              @row-traversal
              {column-depth               :depth
               column-space               :level->space
               column-width-total         :sum-size
               windowed-column-paths      :windowed-paths
               windowed-column-leaf-paths :windowed-leaf-paths}
              @column-traversal
              column-header-heights      (repeat column-depth column-header-height)
              column-header-height-total (apply + column-header-heights)
              row-header-widths          (repeat row-depth row-header-width)
              row-header-width-total     (apply + row-header-widths)
              row-tokens                 (ngu/lazy-grid-tokens @row-traversal)
              row-template               (ngu/lazy-grid-template row-tokens)
              row-spans                  (ngu/grid-spans row-tokens)
              column-tokens              (ngu/lazy-grid-tokens @column-traversal)
              column-template            (ngu/lazy-grid-template column-tokens)
              column-spans               (ngu/grid-spans column-tokens)
              spacer-container           [:div {:style {:border "thin solid lightblue"}}]
              row-header-container       [:div {:style {:display               :grid
                                                        :grid-template-rows    row-template
                                                        :grid-template-columns (ngu/grid-template row-header-widths)}}]
              row-header-cells           (for [path windowed-row-paths]
                                           [:div {:style {:grid-row-start    (ngu/path->grid-line-name path)
                                                          :grid-row-end      (str "span " (get row-spans path))
                                                          :grid-column-start (count path)
                                                          :grid-column-end   (str "span " (+ 1 (- row-depth (count path))))
                                                          :border-top        "thin solid green"
                                                          :border-left       "thin solid green"
                                                          :overflow          :hidden
                                                          :font-size         8}}
                                            (pr-str path)])
              column-header-container    [:div {:style {:display               :grid
                                                        :grid-template-rows    (ngu/grid-template column-header-heights)
                                                        :grid-template-columns column-template}}]
              column-header-cells        (for [path windowed-column-paths]
                                           [:div {:style {:grid-column-start (ngu/path->grid-line-name path)
                                                          :grid-column-end   (str "span " (get column-spans path))
                                                          :grid-row-start    (count path)
                                                          :grid-row-end      (str "span " (+ 1 (- column-depth (count path))))
                                                          :border-top        "thin solid green"
                                                          :border-left       "thin solid green"
                                                          :overflow          :hidden
                                                          :font-size         8}}
                                            (pr-str path)])
              main-container             [:div
                                          {:style {:flex                  "0 0 auto"
                                                   :border                "2px solid grey"
                                                   :display               :grid
                                                   :grid-template-rows    (ngu/grid-template [column-header-height-total row-height-total])
                                                   :grid-template-columns (ngu/grid-template [row-header-width-total column-width-total])}}]
              cell-grid-container        [:div {:style {:display               :grid
                                                        :grid-template-rows    row-template
                                                        :grid-template-columns column-template}}]
              cells                      (for [row-path    windowed-row-leaf-paths
                                               column-path windowed-column-leaf-paths]
                                           (u/part cell {:row-path row-path :column-path column-path}))]
          [rc/v-box
           :style {:position :relative}
           :children
           [(conj main-container
                  spacer-container
                  (into column-header-container column-header-cells)
                  (into row-header-container row-header-cells)
                  (into cell-grid-container cells))
            [:div {:style {:position :absolute
                           :top      (+ (* 2 @wy) column-header-height-total)
                           :left     (+ @wx row-header-width-total)
                           :width    @ww
                           :height   @wh
                           :border   "2px solid red"}}]]]))})))

(def row-tree (r/atom ngu/test-tree))

(defn panel []

  [rc/h-box
   :gap "50px"
   :children
   [[rc/v-box
     :children
     [[new-nested-grid {:row-tree    row-tree
                        :column-tree [:a [:b 30 :d 40] [:e 30 :g 35] [:h 10 :j] [:k 20]]}]
      "Window width"
      [rc/slider {:model ww :on-change (partial reset! ww) :min 50 :max 200}]
      "Window height"
      [rc/slider {:model wh :on-change (partial reset! wh) :min 50 :max 200}]]]
    [rc/box
     :style {:margin-top 50}
     :size "400px"
     :child [window-search-test {:tree ngu/test-tree}]]
    #_[:<> [linear-search-infinite-scroll-test
            {:row-height   25
             :column-width 100
             :max-height   "80vh"
             :max-width    "80vw"
             :row-seq      row-seq
             :column-seq   column-seq
             :cell         test-cell}]
       [:div "rows loaded:" @rows-loaded]]]])
