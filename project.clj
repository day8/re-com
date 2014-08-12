(defproject reagent-components "0.1.0"
  :description  "Reusable UI components for Reagent"

  :url          "https://github.com/Day8/re-com.git"


  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2280"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [reagent "0.4.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [com.cemerick/clojurescript.test "0.3.1"]]

  :source-paths ["src"]


  :cljsbuild { :builds [
                        {:id "demo"
                         :source-paths   ["src" "demo_src"]
                         :compiler       {:output-to     "run_demo/compiled/demo.js"
                                          :source-map    "run_demo/compiled/demo.js.map"
                                          :output-dir    "run_demo/compiled/demo"
                                          :optimizations :none
                                          :pretty-print  true}}


                        {:id "prod"
                         :source-paths   ["src"]
                         :compiler       {:output-to     "compiled/prod.js"
                                          :output-dir    "compiled/prod"
                                          :preamble      ["reagent/react.min.js"]
                                          :elide-asserts true
                                          :pretty-print false
                                          :optimizations :advanced}}]}
  )
