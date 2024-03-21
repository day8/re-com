(ns re-demo.nested-grid
  (:require [re-com.core   :as rc :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
            [reagent.core :as r]
            [re-com.nested-grid  :as nested-grid :refer [nested-grid nested-grid-args-desc nested-grid-parts-desc]]
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
  (name (get-in color-mixer [color1 color2])))

(mix-colors :red :yellow)

(defn color-explainer []
  [rc/v-box
   :children
   [[p "Here's a grid with flat columns and rows."
     " The " [:code ":cell"] " function closes over some external business logic ("
     [:code "mix-colors"] ") to express a string value."
     " Since there is only one level of nesting, " [:code "column-path"]
     " contains a single " [:code "column"] " value - for instance, "
     [:code "[:red]"] "."]
    [:pre "[nested-grid
 :columns [:red :yellow :blue]
 :rows    [:red :yellow :blue]
 :cell    (fn color-cell [{:keys [column-path row-path]}]
             (mix-colors (last column-path)
                         (last row-path)))]"]]])

(defn color-demo []
  [nested-grid
   :columns [:red :yellow :blue]
   :rows    [:red :yellow :blue]
   :cell    (fn color-cell [{:keys [row-path column-path]}]
              (mix-colors (last row-path)
                          (last column-path)))])

(defn color-shade-explainer []
  [rc/v-box
   :children
   [[p "Since " [:code ":columns"] " is a vector tree with 2 levels of nesting,"
     " each " [:code ":column-path"] " is 2-long. For instance, "
     [:code "[:medium :yellow]"] ". "]
    [p [:code ":cell"] " returns a hiccup."]
    [p "Calling " [:code "(color-shade-cell {:column-path [:medium :yellow] :row-path [:blue]})"]
     "should return a " [:span {:style {:color "green"}} "green"] " hiccup."]

    [:pre "[nested-grid
   {:columns [:medium [:red :yellow :blue]
              :light  [:red :yellow :blue]
              :dark   [:red :yellow :blue]]
    :rows    [:red :yellow :blue]
    :cell    color-shade-cell}]"]]])

(defn color-shade-cell [{:keys [row-path column-path]}]
  (let [[row-hue]       row-path
        [shade col-hue] column-path
        hue             (mix-colors row-hue col-hue)
        shade           (when-not (= :medium shade) (name shade))
        color           (str shade hue)
        color           (get special-colors color color)]
    [:div {:style {:height           "100%"
                   :width            "100%"
                   :text-align       "center"
                   :background-color color}}
     [:span {:style {:font-size   "10px"
                     :color       "white"
                     :text-shadow "1px 1px 2px black"}}
      color]]))

(nested-grid/header-spec->header-paths
 [:medium [:red :yellow :blue]
  :light [:red :yellow :blue]
  :dark [:red :yellow :blue]])

(defn color-shade-demo []
  [nested-grid
   {:columns [:medium [:red :yellow :blue]
              :light [:red :yellow :blue]
              :dark [:red :yellow :blue]]
    :rows    [:red :yellow :blue]
    :cell    color-shade-cell}])

(def fruit {:dimension "fruit"})

(defn fruit-demo []
  [nested-grid {:columns [{:id :fruit :hide-cells? true}
                          [{:id :red}
                           {:id :white}]]
                :rows    [[:price
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

(def lookup-table [["🚓" "🛵" "🚲" "🛻" "🚚"]
                   ["🍐" "🍎" "🍌" "🥝" "🍇"]
                   ["🐈" "🐕" "🐟" "🐎" "🧸"]])

(def add {:operator + :label "Addition"})
(def multiply {:operator * :label "Multiplication"})
(def lookup {:operator (fn [l r] (get-in lookup-table [l r]))
             :label    "Lookup"})
(def one {:left 1 :label "1"})
(def two {:left 2 :label "2"})
(def three {:right 3 :label "3"})
(def four  {:right 4 :label "4"})

(defn notes-column []
  [v-box
   :children
   [[title2 "Notes"]
    [status-text "alpha" {:color "red"}]
    [new-in-version "v2.20.0"]
    [p [:code "nested-grid"] " provides a lean abstraction for viewing multidimensional "
     "tabular data, using "
     [:a {:href "https://www.w3schools.com/css/css_grid.asp"} "css grid"]
     " for layout."]
    [title3 "Cells are Functions"]
    [p "Each cell is a " [:i "function"] " of its grid position."]
    [nested-grid
     :columns [:a :b :c]
     :rows    [:x :y :z]
     :column-width 40
     :column-header-height 25
     :row-header-width 30
     :cell    (fn [{:keys [column-path row-path]}] (pr-str (concat column-path row-path)))]
    [title3 "Headers are Nested"]
    [p "You can declare a tree of nested header values. "]
    [nested-grid
     :columns [:a [:a1 :a2] :b [:b1 :b2]]
     :rows    [:x [:x1 :x2] :y [:y1 :y2]]
     :column-header-height 25
     :row-header-width 30
     :column-width 90
     :cell    (fn [{:keys [column-path row-path]}]
                (pr-str (list column-path row-path)))]
    [p [:code ":columns"] " is a tree of nested " [:code "column"] " values. For instance: "]
    [:pre ":columns [:a [:a1 :a2] :b [:b1 :b2]]
:rows    [:x [:x1 :x2] :y [:y1 :y2]]"]
    [p "That means each vertical partition you see is defined by a " [:code ":column-path"]
     "(not simply a " [:code "column"] "). "
     "For instance, " [:code "[:a :a1]"] " is the first " [:code ":column-path"] "."]
    [p "Same goes for rows. For instance, " [:code "[:y :y2]"] " is the last " [:code ":row-path"] "."]
    [title3 "Headers can be Richly Declarative"]

    [p "A " [:code ":column-path"] " is a vector of " [:code "column"] " values."]

    [p "Anything can be a " [:code "column"] " value, "
     [:i "except"] " a " [:code "list"] " or " [:code "vector"] " (those express nesting)."]

    [nested-grid
     :columns [add      [one two]
               multiply [one two]
               lookup   [one two]]
     :rows    [three four]
     :row-header (comp :label last :row-path)
     :column-header (comp :label last :column-path)
     :column-header-height 25
     :row-header-width 100
     :column-width 90
     :cell    (fn [{:keys [column-path row-path]}]
                (let [{:keys [operator left right]} (->> (into row-path column-path)
                                                         (apply merge))]
                  (operator left right)))]
    [:pre "(def lookup-table [[\"🚓\" \"🛵\" \"🚲\" \"🛻\" \"🚚\"]
                   [\"🍐\" \"🍎\" \"🍌\" \"🥝\" \"🍇\"]
                   [\"🐈\" \"🐕\" \"🐟\" \"🐎\" \"🧸\"]])
(def add      {:operator + :label \"Addition\"})
(def multiply {:operator * :label \"Multiplication\"})
(def lookup   {:operator (fn [l r] (get-in lookup-table [l r]))
               :label    \"Lookup\"})
(def one      {:left 1 :label \"1\"})
(def two      {:left 2 :label \"2\"})
(def three    {:right 3 :label \" 3 \"})
(def four     {:right 4 :label \" 4 \"})

[nested-grid
 :columns       [add      [one two]
                 multiply [one two]
                 lookup   [one two]]
 :rows          [three four]
 :column-header (comp :label last :column-path)
 :row-header    (comp :label last :row-path)
 :cell          (fn [{:keys [column-path row-path]}]
                  (let [{:keys [operator left right]} (->> column-path
                                                           (into row-path)
                                                           (apply merge))]
                    (operator left right)))]"]
    [title3 "Header Cells are Functions"]
    [p "Just like " [:code ":cell"] ", the " [:code ":column-header"] " and " [:code ":row-header"] " props "
     "are functions of paths."]
    [p "The difference is, a " [:code ":column-header"] " only has a " [:code ":column-path"]
     " and a " [:code ":row-header"] " only has a " [:code ":row-path"] "."]
    [title3 "Nested-grid + Domain Logic = Pivot Table"]
    [:i {:style {:max-width "400px"}} "A pivot table is a table of values which are aggregations of groups of individual values from a more extensive table...  within one or more discrete categories. (" [:a {:href "https://en.wikipedia.org/wiki/Pivot_table"} "Wikipedia"] ")"]
    [:br]
    [p "The pivot table is our driving use-case. "
     "By separating UI presentation from data presentation, we hope "
     [:code "nested-grid"] " makes it simple to build robust and flexible pivot tables."]
    [p "In particular, your " [:code ":cell"] " function can close over concepts like " [:i "aggregations, groups, categories, dimensions, measures,"] " etc."]
    [p "In the example above, " [:code "lookup-table"] "demonstrates the concept of "
     [:i "a more extensive table."]
     " Your " [:code ":columns"] " and " [:code ":rows"]
     " can declare the necessary domain concepts, and your " [:code ":cell"] " function can dispatch on each concept, "
     "accessing various " [:i "aggregations"] " and " [:i "groupings"] " of that table."]
    [p "We also envision building an interactive, configurable pivot table. "
     "By changing the value of " [:code ":columns"] " and " [:code ":rows"] ", you can reconfigure the UI presentation, and "
     "your data presentation can simply follow along. "
     "This can be done either programmatically or via a dedicated user interface."]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  (let [tab-defs        [{:id :note       :label "Notes"}
                         {:id :parameters :label "Parameters"}]
        selected-tab-id (r/atom (:id (first tab-defs)))]
    (fn []
      [v-box
       :src      (at)
       :size     "auto"
       :gap      "10px"
       :children
       [[panel-title "[nested-grid ... ]"
         "src/re_com/nested-grid.cljs"
         "src/re_demo/nested-grid.cljs"]
        [h-box
         :src      (at)
         :gap      "50px"
         :children
         [[v-box
           :src      (at)
           :children
           [[rc/horizontal-tabs
             :src       (at)
             :model     selected-tab-id
             :tabs      tab-defs
             :style     {:margin-top "12px"}
             :on-change #(reset! selected-tab-id %)]
            (case @selected-tab-id
              :note       [notes-column]
              :parameters [args-table
                           nested-grid-args-desc
                           {:total-width       "550px"
                            :name-column-width "180px"}])]]
          [v-box
           :src      (at)
           :children
           [[title2 "Demo #1"]
            [gap
             :src      (at)
             :size "15px"]
            [color-demo]
            [source-reference
             "for above nested-grid"
             "src/re_demo/nested-grid.cljs"]
            [color-explainer]
            [gap
             :src      (at)
             :size "40px"]
            [line :src      (at)]
            [title2 "Demo #2"]
            [gap
             :src      (at)
             :size "15px"]
            [color-shade-demo]
            [source-reference
             "for above nested-grid"
             "src/re_demo/nested-grid.cljs"]
            [color-shade-explainer]]]]]
        #_[parts-table "nested-grid" nested-grid-grid-parts-desc]]])))
