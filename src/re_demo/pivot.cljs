(ns re-demo.pivot
  (:require [re-com.core   :as rc :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
            [reagent.core :as r]
            [re-com.pivot  :as pivot :refer [pivot-grid-args-desc pivot-grid-parts-desc]]
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
    [:pre "[pivot/grid
 :columns [:red :yellow :blue]
 :rows    [:red :yellow :blue]
 :cell    (fn color-cell [{:keys [column-path row-path]}]
             (mix-colors (last column-path)
                         (last row-path)))]"]]])

(defn color-demo []
  [pivot/grid
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

    [:pre "[pivot/grid
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

(pivot/header-spec->header-paths
 [:medium [:red :yellow :blue]
  :light [:red :yellow :blue]
  :dark [:red :yellow :blue]])

(defn color-shade-demo []
  [pivot/grid
   {:columns [:medium [:red :yellow :blue]
              :light [:red :yellow :blue]
              :dark [:red :yellow :blue]]
    :rows    [:red :yellow :blue]
    :cell    color-shade-cell}])

(def fruit {:dimension "fruit"})

(defn fruit-demo []
  [pivot/grid {:columns [{:id :fruit :hide-cells? true}
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

(defn notes-column []
  [v-box
   :children
   [[title2 "Notes"]
    [status-text "alpha" {:color "red"}]
    [new-in-version "v2.20.0"]
    [p [:code "pivot-grid"]
     "provides a lean "
     [:a {:href "https://en.wikipedia.org/wiki/Pivot_table"} "pivot table"]
     " abstraction, using "
     [:a {:href "https://www.w3schools.com/css/css_grid.asp"} "css grid"]
     " for layout."]
    [title3 "Cells are Functions"]
    [p "Each cell is a " [:i "function"] " of its grid position."]
    [pivot/grid
     :columns [:a :b :c]
     :rows    [:x :y :z]
     :column-width 40
     :column-header-height 25
     :row-header-width 30
     :cell    (fn [{:keys [column-path row-path]}] (pr-str (concat column-path row-path)))]
    [title3 "Headers are Nested"]
    [p "You can declare a tree of nested header values. "]
    [pivot/grid
     :columns [:a [:a1 :a2] :b [:b1 :b2]]
     :rows    [:x [:x1 :x2] :y [:y1 :y2]]
     :column-header-height 25
     :row-header-width 30
     :column-width 90
     :cell    (fn [{:keys [column-path row-path]}]
                (pr-str (list column-path row-path)))]
    [p [:code ":columns"] " is a tree of " [:code "column"] " values. For instance: "]
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
    [pivot/grid
     :columns [{:operator + :label "Addition"}       [{:left 2 :label "2"}
                                                      {:left 3 :label "3"}]
               {:operator * :label "Multiplication"} [{:left 2 :label "2"}
                                                      {:left 3 :label "3"}]
               {:operator / :label "Division"}       [{:left 2 :label "2"}
                                                      {:left 3 :label "3"}]]
     :rows    [{:right 4 :label "4"}
               {:right 5 :label "5"}]
     :row-header (comp :label last :row-path)
     :column-header (comp :label last :column-path)
     :column-header-height 25
     :row-header-width 100
     :column-width 90
     :cell    (fn [{:keys [column-path row-path]}]
                (let [{:keys [operator left right]} (->> (into row-path column-path)
                                                         (apply merge))]
                  (operator left right)))]
    [:pre ":columns       [{:operator + :label \"Addition\"}       [{:left 2 :label \"2\"}
                                                       {:left 3 :label \"3\"}]
                {:operator * :label \"Multiplication\"} [{:left 2 :label \"2\"}
                                                       {:left 3 :label \"3\"}]
                {:operator / :label \"Division\"}       [{:left 2 :label \"2\"}
                                                       {:left 3 :label \"3\"}]]
:rows          [{:right 4 :label \"4\"}
                {:right 5 :label \"5\"}]
:column-header (comp :label last :column-path)
:row-header    (comp :label last :row-path)
:cell          (fn [{:keys [column-path row-path]}]
                 (let [{:keys [operator left right]} (->> column-path
                                                          (into row-path)
                                                          (apply merge))]
                   (operator left right)))"]
    [p "The " [:code ":column-header"] " and " [:code ":row-header"] " props "
     "work the same way as " [:code ":cell"] "."
     " (Except, a " [:code ":column-header"] " only has a " [:code ":column-path"]
     " and a " [:code ":row-header"] " only has a " [:code ":row-path"] ")."]]])

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
       [[panel-title "[pivot-grid ... ]"
         "src/re_com/pivot.cljs"
         "src/re_demo/pivot.cljs"]
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
                           pivot-grid-args-desc
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
             "for above pivot-grid"
             "src/re_demo/pivot.cljs"]
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
             "for above pivot-grid"
             "src/re_demo/pivot.cljs"]
            [color-shade-explainer]]]]]
        #_[parts-table "pivot-grid" pivot-grid-parts-desc]]])))
