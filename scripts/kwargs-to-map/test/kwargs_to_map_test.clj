(ns kwargs-to-map-test
  (:require [clojure.test :refer :all]
            [kwargs-to-map :refer :all]
            [rewrite-clj.zip :as z]))

(def namespace-form (z/of-string "
(ns sample-namespace.core
  (:require
    [re-com.core :refer [box title p v-box h-box md-circle-icon-button border] :as re-com]))"))

(def h-box-example (z/of-string "
[h-box :src (at)
 :size    \"1\"
 :children []
 :align    :center]"))

(def v-box-example (z/of-string "
[v-box :src (at)
 :size    \"1\"
 :justify  :center
 :children [[v-box :src (at)
             :size     \"1\"
             :children [[label :title \"Moved\"]]
             :align    :center]
            [h-box :src (at)
             :size     \"1\"
             :children [[label :title \"Moved\"]]
             :align    :center]]
 :align    :center]"))

(def box-example (z/of-string "
[box :src (at)
 :size \"1\"
 :child [label :title \"Moved\"]
 :align :center]"))

(def scroller-example (z/of-string "
[scroller
 :v-scroll :auto
 :height   \"300px\"
 :child    [some-component]]"))

(def border-example (z/of-string "
[border
 :border \"1px dashed red\"
 :child  [box :height \"100px\" :child \"Hello\"]]"))

(def modal-panel-example (z/of-string "
[modal-panel :src (at)
 :backdrop-color   \"grey\"
 :backdrop-opacity 0.4
 :style            {:font-family \"Consolas\"}
 :child            [box :height \"100px\" :child \"Hello\"]]"))

(def gap-example (z/of-string "
[gap :size \"5px\"]"))

(def line-example (z/of-string "
[line
 :size  \"3px\"
 :color \"red\"]"))

(def label-example (z/of-string "
[label
 :label  \"This is the label\"
 :style  {:color \"red\"}
 :class \"class-name\"]"))

(def title-example (z/of-string "
[title
 :label  \"This is the label\"
 :level  :level1
 :style  {:color \"red\"}
 :class \"class-name\"]"))

(def basic-button-example (z/of-string "
[button
 :label    \"This is the label\"
 :style    {:color \"red\"}
 :on-click nil
 :class    \"class-name\"]"))

(def row-button-example (z/of-string "
[row-button
 :md-icon-name  \"zmdi-plus\"
 :style         {:color \"red\"}
 :on-click      nil
 :class         \"class-name\"]"))

(def circle-icon-example (z/of-string "
[md-circle-icon-button
 :md-icon-name  \"zmdi-plus\"
 :style         {:color \"red\"}
 :on-click      nil
 :class         \"class-name\"]"))

(def md-icon-button-example (z/of-string "
[md-icon-button
 :md-icon-name  \"zmdi-plus\"
 :size          \"1\"
 :style         {:color \"red\"}
 :on-click      nil
 :class         \"class-name\"]"))

(def info-button-example (z/of-string "
[info-button
 :info  \"Some information to show\"
 :width \"200px\"
 :style {:color \"red\"}
 :class \"class-name\"]"))

(def hyperlink-example (z/of-string "
[hyperlink
 :label    \"Hyperlink text\"
 :on-click nil
 :style    {:color \"red\"}
 :class    \"class-name\"]"))

(def hyperlink-href-example (z/of-string "
[hyperlink-href
 :label    \"Hyperlink text\"
 :href     \"link.com\"
 :style    {:color \"red\"}
 :class    \"class-name\"]"))

(def alert-box-example (z/of-string "
[alert-box
  :src        (at)
  :alert-type :none
  :style      {:color             \"#222\"
               :background-color  \"rgba(255, 165, 0, 0.1)\"
               :border-top        \"none\"
               :border-right      \"none\"
               :border-bottom     \"none\"
               :border-left       \"4px solid rgba(255, 165, 0, 0.8)\"
               :border-radius     \"0px\"}
  :body       \"Alert with :body but no :heading (:padding set to 6px).\"
  :padding    \"6px\"
  :closeable? true
  :on-close   #(reset! show-alert6 false)]"))

(def popover-anchor-wrapper-example (z/of-string "
[popover-anchor-wrapper
  :showing? showing?
  :position :right-below
  :anchor   [button
             :label    \"Anchor\"
             :on-click #(swap! showing? not)]
  :popover  [popover-content-wrapper
             :title    \"Title\"
             :body     \"Popover body text\"]]"))

(def h-split-example (z/of-string "
[rc/h-split :src (at)
 :panel-1 [left-panel]
 :panel-2 [right-panel]
 :size    \"300px\"]"))

(def v-split-example (z/of-string "
[rc/v-split :src (at)
 :panel-1 [top-panel]
 :panel-2 [bottom-panel]
 :size    \"300px\"
 :initial-split \"25%\"]"))

(def p-example (z/of-string "
[p \"This will be ignored\"]"))

(def p-span-example (z/of-string "
[p-span \"This will be ignored\"]"))

(defn count-immediate-keyword-args
  "Given loc which is a re-com component in hiccup syntax, we will be finding the number
  of immediate children.
  For example, the component `h-box` below
  [h-box
  {:size \"1\"
   :margin \"20px 0 0 0\"}
  [child-component]] has two immediate children, the map and the vector, child component."
  [loc ]
  (let [loc (z/down loc)
        children-count (atom 0)]
    (loop [loc loc]
      (cond
        (nil? loc) @children-count
        :else
        (do
          (swap! children-count inc)
          (recur (z/right loc)))))))

(defn child-added?
  "Given loc which is a re-com component in hiccup syntax, checks if it has a child element
  which is conjoined after the map containing keyword args. Compoments include `box`,
  `modal-panel` and `border` E.g,
  ```
  [box {
  :class \"class-name\"
  :size \"1\" } [child-component]]
  ```"
  [loc f]
  (let [loc (z/down loc)
        map-leftmost (z/right loc)
        child (z/rightmost loc)]
    (is (= (-> map-leftmost z/string) "{"))
    (is (f child))))

(defn map-wrapped?
  "Given loc which is a re-com component in hiccup syntax, we will find if the component has
  all its children wrapped in a map. This will only work when the component has been edited
  using this script.
  Components which pass this test include `button` and `label` e.g.
  ```
  [label {
   :label \"This is a label\"
   :style {:color \"red\"}
   :class \"class-name\" } ]
  ```
  If a child is outside the map, this test will fail such as a `box` or `h-box`.
  "
  [loc]
  (let [loc (z/down loc)
        map-leftmost (z/right loc)
        map-rightmost (z/rightmost loc)]
    (is (= (-> map-leftmost z/string) "{"))
    (is (= (-> map-rightmost z/string) "}"))))

(deftest test-namespace
  (testing "Test getting referred components and alias"
    (let [{:keys [required-namespaces used-alias]} (parse-require-forms namespace-form true)]
      ;; 6 because `p` is automatically removed since it wont be formatted
      (is (= (count required-namespaces) 6) "Failed to get referred components")
      (is (= used-alias "re-com") "Failed to get referred components"))))

(deftest children-test
  (testing "Test components with a :children kwarg such as `h-box`."
    (let [edited-h-box  (kwags-to-map h-box-example {:verbose true})
          edited-v-box  (kwags-to-map v-box-example {:verbose true})]
      (is (= (count-immediate-keyword-args edited-v-box) 4))
      (is (= (count-immediate-keyword-args edited-h-box) 3)))))

(deftest child-test
  (testing "Test components with a :child kwarg such as `box`."
    (let [edited-box          (kwags-to-map box-example {:verbose true})
          edited-scroller     (kwags-to-map scroller-example {:verbose true})
          edited-border       (kwags-to-map border-example {:verbose true})
          edited-modal-panel  (kwags-to-map modal-panel-example {:verbose true})
          edited-alert-box    (kwags-to-map alert-box-example {:verbose true})]
      (child-added? edited-box z/vector?)
      (child-added? edited-scroller z/vector?)
      (child-added? edited-border z/vector?)
      (child-added? edited-modal-panel z/vector?)
      (child-added? edited-alert-box (fn [loc] (-> loc z/sexpr string?))))))

(deftest no-descendants-test
  (testing "Test components without children such as `label`."
    (let [edited-button          (kwags-to-map basic-button-example {:verbose true})
          edited-row-button      (kwags-to-map row-button-example {:verbose true})
          edited-circle-icon     (kwags-to-map circle-icon-example {:verbose true})
          edited-md-icon-button  (kwags-to-map md-icon-button-example {:verbose true})
          edited-info-button     (kwags-to-map info-button-example {:verbose true})
          edited-hyperlink       (kwags-to-map hyperlink-example {:verbose true})
          edited-hyperlink-href  (kwags-to-map hyperlink-href-example {:verbose true})
          edited-label           (kwags-to-map label-example {:verbose true})
          edited-title           (kwags-to-map title-example {:verbose true})
          edited-gap             (kwags-to-map gap-example {:verbose true})
          edited-line            (kwags-to-map line-example {:verbose true})]
      (map-wrapped? edited-button)
      (map-wrapped? edited-row-button)
      (map-wrapped? edited-circle-icon)
      (map-wrapped? edited-md-icon-button)
      (map-wrapped? edited-info-button)
      (map-wrapped? edited-hyperlink)
      (map-wrapped? edited-hyperlink-href)
      (map-wrapped? edited-label)
      (map-wrapped? edited-title)
      (map-wrapped? edited-gap)
      (map-wrapped? edited-line))))

(deftest ignore-test
  (testing "Test components that should not receive any editing such as `p` and `p-span`."
    (let [edited-p  (kwags-to-map p-example {:verbose true})
          edited-p-span (kwags-to-map p-span-example {:verbose true})]
      ;; the count will always be 2,
      ;; 1. The symbol e.g `p` and the text.
      (is (= (count-immediate-keyword-args edited-p) 2))
      (is (= (count-immediate-keyword-args edited-p-span) 2)))))

(deftest split-test
  (testing "Test split components such as `h-split`"
    (let [edited-h-split   (kwags-to-map h-split-example {:verbose true :alias "rc"})
          edited-v-split  (kwags-to-map v-split-example {:verbose true :alias "rc"})]
      (child-added? edited-h-split z/vector?)
      (child-added? edited-v-split z/vector?))))
