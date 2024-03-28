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

(nested-grid/header-spec->header-paths
 [:medium [:red :yellow :blue]
  :light [:red :yellow :blue]
  :dark [:red :yellow :blue]])

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
                   ["🐕" "🐎" "🧸" "🐈" "🐟"]])

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

    [title3 "Headers are Nested"]
    [p "You can declare headers as a nested " [:i "configuration."]]

    [p "Each vertical partition you see is defined by a " [:code ":column-path"] "."
     "For instance, " [:code "[:a :a1]"] " is the first " [:code ":column-path"] "."]
    [p "Same goes for rows. For instance, " [:code "[:y :y2]"] " is the last " [:code ":row-path"] "."]
    [title3 "Cells are Views of Header Paths"]
    [p "Each cell is a function of its location."]
    [p "Specifically, the " [:code ":cell"] " prop accepts a function "
     "of two keyword arguments: " [:code ":column-path"] " and " [:code ":row-path"] "."]
    [p "The function counts as a "
     [:a {:href "https://github.com/reagent-project/reagent/blob/master/doc/CreatingReagentComponents.md"}
      "reagent component"] ", returning either a string or a hiccup."]
    [title3 "Header Cells are Views, Too"]
    [p "Just like " [:code ":cell"] ", the " [:code ":column-header"] " and " [:code ":row-header"] " props "
     "are functions of their location."]
    [p "The difference is, a " [:code ":column-header"] " only has a " [:code ":column-path"]
     " and a " [:code ":row-header"] " only has a " [:code ":row-path"] "."]
    [title3 "Headers are Richly Declarative"]
    [p "A " [:code ":column-path"] " is a vector of " [:i "header values."]]
    [p "Anything can be a " [:i "header value"] ", "
     [:i "except"] " a " [:code "list"] " or " [:code "vector"] " (those express " [:i "configuration"] ")."]
    [p "So far, we've looked at simple " [:i "header values"] ", like " [:code ":a"] " or " [:code "\"blue\""] "."]
    [p "Another common use-case is a map, like " [:code "{:id :a :label \"A\" :type :letter}"] "."
     "We consider a value like this to be a " [:i "header spec"] "."]
    [title3 "Nested-grid + Domain Logic = Pivot Table"]
    [:i {:style {:max-width "400px"}}
     "A pivot table is a table of values which are aggregations of groups of individual values from a more extensive table..."
     "within one or more discrete categories. (" [:a {:href "https://en.wikipedia.org/wiki/Pivot_table"} "Wikipedia"] ")"]
    [:br]
    [p "The pivot table is our driving use-case. "
     "By separating UI presentation from data presentation, we hope "
     [:code "nested-grid"] " makes it simple to build robust and flexible pivot tables."]
    [p "In " [:strong "Demo #3: Header Specifications"] ", " [:code "lookup-table"] "declares "
     [:i "\"a more extensive table,\""]
     " and the " [:code "lookup"] [:i "column spec"] " declares how to use that table."]
    [p
     "More generally:" [:br]
     [:ul
      [:li "Your " [:code ":columns"] " and " [:code ":rows"]
       " declare the necessary domain concepts, such as "
       [:i "\"aggregations\""] " and " [:i "\"groups.\""]]
      [:li "Your " [:code ":cell"] " function dispatches on each concept, "
       "deriving these " [:i "\"aggregations\""] " and " [:i "\"groups\""] " from "
       [:i "\"a more extensive table.\""]]]]
    [p "We also envision building an interactive, configurable pivot table. "
     "By changing " [:code ":columns"] " and " [:code ":rows"] ", you could reconfigure the UI presentation, and "
     "your data presentation would simply follow along. "
     "This could be done either programmatically or via a dedicated user interface."]]])

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
     :columns ["red" "yellow" "blue"]
     :rows    ["red" "yellow" "blue"]
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
 :columns [\"red\" \"yellow\" \"blue\"]
 :rows    [\"red\" \"yellow\" \"blue\"]
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
     {:columns [:medium [:red :yellow :blue]
                :light [:red :yellow :blue]
                :dark [:red :yellow :blue]]
      :rows    [:red :yellow :blue]
      :cell    color-shade-cell}]
    [source-reference
     "for above nested-grid"
     "src/re_demo/nested_grid.cljs"]
    [rc/v-box
     :children
     [[p "Here, " [:code ":columns"] "is a nested " [:i "configuration"] " of " [:i "header values."]]
      [p "Since the " [:i "configuration"] " has 2 levels of nesting,"
       " each " [:code ":column-path"] " is 2-long. For instance, "
       [:code "[:medium :yellow]"] ". "]
      [p [:code ":cell"] " returns a hiccup."]
      [p "Calling " [:code "(color-shade-cell {:column-path [:medium :yellow] :row-path [:blue]})"]
       "should return a " [:span {:style {:color "green"}} "green"] " hiccup."]
      [:pre "[nested-grid
 :columns [:medium [:red :yellow :blue]
           :light  [:red :yellow :blue]
           :dark   [:red :yellow :blue]]
 :rows    [:red :yellow :blue]
 :cell    color-shade-cell]"]]]]])

(defn header-spec-demo []
  [v-box
   :children
   [[nested-grid
     :columns [add      [one two]
               multiply [one two]
               lookup   [one two]]
     :rows    [three four]
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
                    (operator left right)))]"]]])

(defn basic-demo []
  [v-box
   :children
   [[h-box
     :justify :between
     :children
     [[nested-grid
       :columns [:a :b :c]
       :rows    [1 2 3]
       :column-width 40
       :column-header-height 25
       :row-header-width 30
       :cell (fn [{:keys [column-path row-path]}]
               (str column-path row-path))]
      [:pre {:style {:margin-top 19}} "[nested-grid
 :columns [:a :b :c]
 :rows    [1  2  3]
 :cell (fn [{:keys [column-path row-path]}]
         (str column-path row-path))]"]]]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :columns [:a :b :c]
       :rows    [1 [:x :y]
                 2 [:x :y]]
       :column-width 55
       :column-header-height 25
       :row-header-width 30
       :cell    (fn [{:keys [column-path row-path]}]
                  (str column-path row-path))]
      [:pre {:style {:margin-top 19}} "[nested-grid
 :columns [:a :b :c]
 :rows    [1 [:x :y]
           2 [:x :y]]
 :cell (fn [{:keys [column-path row-path]}]
         (str column-path row-path))]"]]]
    [h-box
     :justify :between
     :children
     [[nested-grid
       :columns [:a [1 2] :b [3 4]]
       :rows    [:x [5 6] :y [7 8]]
       :column-header-height 25
       :row-header-width 30
       :column-width 65
       :cell (fn [{:keys [column-path row-path]}]
               [:i {:style {:color "grey"}}
                (str column-path row-path)])]
      [:pre {:style {:margin-top 19}}
       "[nested-grid
 :columns [:a [1 2] :b [3 4]]
 :rows    [:x [5 6] :y [7 8]]
 :cell (fn [{:keys [column-path row-path]}]
         [:i {:style {:color \"grey\"}}
          (str column-path row-path)])]"]]]]])

(defn demos []
  (let [tabs [{:id :basic :label "Basic" :view basic-demo}
              {:id :color :label "Color" :view color-demo}
              {:id :shade :label "Shade" :view color-shade-demo}
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
          [title2 (str label " Demo")]
          [view]]]))))

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
          [demos]]]
        #_[parts-table "nested-grid" nested-grid-grid-parts-desc]]])))
