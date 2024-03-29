(ns re-demo.nested-grid
  (:require
   [clojure.string :as str]
   [re-com.core   :as rc :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
   [re-com.util :as u]
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
    [:ul {:style {:width 400}}
     [:li [p "A " [:code ":column-spec"] " describes a single column."]
      [:ul
       [:li "For instance, the " [:strong "Basic Demo"] " uses "
        [:code ":a"] " as a " [:code ":column-spec"] "."]
       [:li "Besides keywords, you can use " [:i "almost any"] " type of value."]
       [:li "You " [:i "can't"] " use vectors or lists (these are reserved for the "
        [:code ":column-tree"] ")."]
       [:li "At Day8, we tend to use maps. For instance, "
        [:pre "{:id :a
 :column-label \"A\"
 :special-business-logic ::xyz}"]]]]
     [:br]
     [:li "A " [:code ":column-tree"] "describes a nested arrangement of columns."
      [:ul
       [:li "There are parent columns, and they can have child columns, "
        "which can have their own child columns, etc..."]
       [:li "In practice, a " [:code ":column-tree"] " is a vector (or list) of "
        [:code ":column-spec"] "values."]
       [:li "The most basic case is a flat tree: " [:code "[:a :b :c]"] "."]
       [:li "A nested tree looks like: " [:code "[:a [1 2] :b [3 4]]"] "."]
       [:li "In that case: "
        [:ul
         [:li [:code ":a"] " and " [:code ":b"] " are siblings, each with two children."]
         [:li [:code "1"] " and " [:code "2"] " are siblings, both children of " [:code ":a"]]]]]]
     [:br]
     [:li "A " [:code ":column-path"] " describes a distinct location within a "
      [:code ":column-tree"] "."
      [:ul
       [:li ""]
       [:li "Given a " [:code ":column-tree"] ", " [:code ":nested-grid"]
        " derives all the " [:code ":column-path"] "s it contains, mapping the "
        [:code ":cell"] " function over all of them."]]]
     [:br]
     [:li [:code ":row-spec"] ", " [:code ":row-tree"] " and " [:code ":row-paths"]
      " have all the same properties as their column equivalents."]
     [:br]
     [:li "A " [:i "position"] " combines one " [:code ":row-path"]
      " with one " [:code ":column-path"] "."
      [:ul
       [:li "Rendering one cell means evaluating the " [:code ":cell"] "function, "
        "by passing in keyword arguments "
        [:code ":row-path"] " and " [:code ":column-path"]
        " (i.e., the " [:i "position"] ")."]]]]]])

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
    [p "Essentially, each cell has a unique" " " [:i "position"]
     " " "within the hierarchy." " " "The value of each cell is a"
     " " [:i "function"] " " "of its" " " [:i "position."]]
    [title2 "Characteristics"]
    [p "Unlike" " " [:code "v-table"] ", "
     [:code "nested-grid"] ":"
     [:ul {:style {:width 400}}
      [:li "Uses" " " [:a {:href "https://www.w3schools.com/css/css_grid.asp"} "css grid"]
       " " "for layout."]
      [:li "Has adjustible column & row widths."]
      [:li "Is optimized for tens or hundreds of rows, not millions."]
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

(defn header-spec-demo []
  [v-box
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
    [p "Here, we use " [:i "header specs"] " like " [:code "multiply"]
     " and " [:code "lookup"] " to build a multi-modal view of the source data."]
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
 :column-tree       [add      [one two]
                 multiply [one two]
                 lookup   [one two]]
 :row-tree          [three four]
 :column-header (comp :label last :column-path)
 :row-header    (comp :label last :row-path)
 :cell          (fn [{:keys [column-path row-path]}]
                  (let [{:keys [operator left right]} (->> column-path
                                                           (into row-path)
                                                           (apply merge))]
                    (operator left right)))]"]]])

(defn basic-demo []
  [v-box
   :children
   [[h-box
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
    [h-box
     :justify :between
     :children
     [[nested-grid
       :column-tree [:a [1 2] :b [3 4]]
       :row-tree    [:x [5 6] :y [7 8]]
       :column-header-height 25
       :row-header-width 30
       :column-width 65
       :cell (fn [{:keys [column-path row-path]}]
               [:i {:style {:color "grey"}}
                (str column-path row-path)])]
      [:pre {:style {:margin-top 19}}
       "[nested-grid
 :column-tree [:a [1 2] :b [3 4]]
 :row-tree    [:x [5 6] :y [7 8]]
 :cell (fn [{:keys [column-path row-path]}]
         [:i {:style {:color \"grey\"}}
          (str column-path row-path)])]"]]]]])

(defn header-demo []
  [:hi])

(defn internals-demo []
  [v-box
   :children
   [[p "This table demonstrates how " [:code "nested-grid"] " derives a vector of " [:code ":column-path"] "s from a " [:code ":column-tree"] ":"]
    [nested-grid
     :column-tree [{:id "Tree" :width 130}
                   {:id "Leaf Paths" :width 155}
                   {:id "All Paths" :width 180}]
     :row-tree    [{:label "Basic" :tree [:a :b :c]}
                   {:label "Nested" :tree [:a [:b :c]]}
                   {:label "Branching" :tree [:a [:b] :c [:d]]}
                   {:label "Explicit" :tree [[:a [:b :c]]
                                             [:d [:e :f]]]}
                   {:label "Typed" :tree [:kw 42 "str" {:k :map}]}]
     :cell (fn [{:keys [column-path] [{:keys [tree]}] :row-path}]
             (case (:id (last column-path))
               "Tree" (str tree)
               "Leaf Paths" (str (vec (nested-grid/leaf-paths
                                       (nested-grid/header-spec->header-paths tree))))
               "All Paths" (str (nested-grid/header-spec->header-paths tree))))]]])

(defn demos []
  (let [tabs [{:id :basic :label "Basic Demo" :view basic-demo}
              {:id :color :label "Color" :view color-demo}
              {:id :shade :label "Shade" :view color-shade-demo}
              {:id :internals  :label "Internals"    :view internals-demo}
              {:id :spec  :label "Spec"  :view header-spec-demo}]
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
    [title3 "Rendering Header Cells"]
    [p "Just like " [:code ":cell"] ", the "
     [:code ":column-header"] " and " [:code ":row-header"] " props "
     "are functions of their location."]
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
      [:li [:code ":width"]  ": sets the initial width."]
      [:li [:code ":height"] ": sets the initial height."]
      [:li [:code ":show?"]  ": show (" [:code "true"] ") or hide (" [:code "false"] ") cells, overriding any other context, settings, or branch/leaf position."]]]
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

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  (let [tabs    [{:id :intro      :label "Introduction" :view intro-column}
                 {:id :concepts   :label "Concepts"     :view concepts-column}
                 {:id :more       :label "More"         :view more-column}
                 {:id :parameters :label "Parameters"   :view args-column}]
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
        #_[parts-table "nested-grid" nested-grid-grid-parts-desc]]])))
