(defproject reagent-components "0.1.0"
  :description  "ClojureScript Library Containing Day8's reusable UI components implemented using Reagent/React"

  :url          "https://github.com/Day8/reagent-components.git"

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2277"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [reagent "0.4.2"]]

  :plugins      [[lein-cljsbuild "1.0.3"]]

  :source-paths ["src/"]

  :cljsbuild    {:builds [{:source-paths ["src"]
                           :compiler {:optimizations :none
                                      :source-map    "core/out/reagent_components.js.map"
                                      :output-to     "core/out/reagent_components.js"
                                      :output-dir    "core/out/"
                                      :pretty-print  true}}]}
  )
