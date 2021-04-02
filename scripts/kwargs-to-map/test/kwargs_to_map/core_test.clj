(ns kwargs-to-map.core-test
  (:require [clojure.test :refer :all]
            [kwargs-to-map.core :refer :all]
            [rewrite-clj.zip :as z]))

(def file-example "
(ns sample-namespace.core
  (:require
    [re-com.core :refer [box title p v-box h-box md-circle-icon-button border popover-anchor-wrapper popover-content-wrapper alert-box] :as re-com]))

(def test [md-circle-icon-button
           :md-icon-name  \"zmdi-plus\"
           :style         {:color \"red\"}
           :on-click      nil
           :class         \"class-name\"])

(defn a-function
 []
 [h-box :src (at)
 :size    \"1\"
 :children [[v-box :src (at)
             :size    \"1\"
             :justify  :center
             :children [[re-com/v-box :src (at)
                         :size     \"1\"
                         :children [[label :title \"Moved\"]]
                         :align    :center]]
            [popover-anchor-wrapper
              :showing? showing?
              :position :right-below
              :anchor   [button
                         :label    \"Anchor\"
                         :on-click #(swap! showing? not)]
              :popover  [popover-content-wrapper
                         :title    \"Title\"
                         :body     \"Popover body text\"]]
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
              :on-close   #(reset! show-alert6 false)]
 :align    :center]]
 :align    :center])

(def test2 [re-com/box :src (at)
            :size \"1\"
            :child [border
                    :border \"1px dashed red\"
                    :child  [box :height \"100px\" :child \"Hello\"]]
            :align :center])
")

(deftest test-file
  (testing "Test full file"
    (read-write-file nil {:verbose?  true
                          :test-file file-example})))
(def namespace-form (z/of-string "
(ns sample-namespace.core
  (:require
    [re-com.core :refer [box title p v-box h-box md-circle-icon-button border] :as re-com]))"))

(deftest test-namespace
  (testing "Test getting referred components and alias"
    (let [{:keys [required-namespaces used-alias]} (parse-require-forms namespace-form true)]
      ;; 6 because `p` is automatically removed since it wont be formatted
      (is (= (count required-namespaces) 6) "Failed to get referred components")
      (is (= used-alias "re-com") "Failed to get referred components"))))

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

(deftest children-test
  (testing "Test components with a :children kwarg"
    (println (z/node (kwags-to-map h-box-example {:verbose true})))
    (println (z/node (kwags-to-map v-box-example {:verbose true})))))

(deftest child-test
  (testing "Test components with a :child kwarg"
    (kwags-to-map box-example {:verbose true})
    (kwags-to-map scroller-example {:verbose true})
    (kwags-to-map border-example {:verbose true})
    (kwags-to-map modal-panel-example {:verbose true})
    (kwags-to-map alert-box-example {:verbose true})))

(deftest no-descendants-test
  (testing "Test components without descendants"
    (kwags-to-map basic-button-example {:verbose true})
    (kwags-to-map row-button-example {:verbose true})
    (kwags-to-map circle-icon-example {:verbose true})
    (kwags-to-map md-icon-button-example {:verbose true})
    (kwags-to-map info-button-example {:verbose true})
    (kwags-to-map hyperlink-example {:verbose true})
    (kwags-to-map hyperlink-href-example {:verbose true})
    (kwags-to-map label-example {:verbose true})
    (kwags-to-map title-example {:verbose true})
    (kwags-to-map gap-example {:verbose true})
    (kwags-to-map line-example {:verbose true})))

(deftest ignore-test
  (testing "Test components without descendants"
    (kwags-to-map p-example {:verbose true})
    (kwags-to-map p-span-example {:verbose true})))

(deftest split-test
  (testing "Test split components"
    (kwags-to-map h-split-example {:verbose true :alias "rc"})
    (kwags-to-map v-split-example {:verbose true :alias "rc"})))

(deftest test-file
  (testing "Test effect of `add-at-macro` script on a string file."
    (read-write-file nil {:testing? true :test-file file-example})))

(def verbose? true)
(def directory "")

(deftest test-script
  (testing "Test the full script including reading files from disk"
    ;; When `:print?` is true, this translates to verbose? in the script which is a flag to tell the script to print
    ;; to the console the changes it will do. When the flag `:testing?` is true, the console does not save the changes
    ;; it makes to file but prints the file to console. When `:testing?` is true, `:verbose?` is always true
    (with-redefs [print? verbose?
                  testing? verbose?]
      (if (seq directory)
        (run-script directory)
        (println "Directory/File not provided")))))

(defn runner [directory]
  (with-redefs [directory directory]
    (test-script)))