(defproject re-com "0.1.0"
  :description  "Reusable UI components for Reagent"

  :url          "https://github.com/Day8/re-com.git"


  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2280"]
                 [org.clojure/core.async "0.1.303.0-886421-alpha"]
                 [alandipert/storage-atom "1.2.3"]
                 [reagent "0.4.2"]]

  :plugins [[lein-cljsbuild "1.0.4-SNAPSHOT"]
            [com.cemerick/clojurescript.test "0.3.1"]]

  :source-paths ["src"]

  ;; Exclude the demo code from the output of either:
  ;;   - lein jar
  ;;   - lein install
  :jar-exclusions [#"(?:^|\/)re_demo\/"]

  :cljsbuild { :builds [
                        {:id "demo"
                         :source-paths   ["src/re_demo" "src/re_com"]
                         :compiler       {:output-to     "run/compiled/demo.js"
                                          :source-map    "run/compiled/demo.js.map"
                                          :output-dir    "run/compiled/demo"
                                          :optimizations :none
                                          :pretty-print  true}}


                        {:id "prod"
                         :source-paths   ["src/re_com"]
                         :compiler       {:output-to     "compiled/prod.js"
                                          :output-dir    "compiled/prod"
                                          :preamble      ["reagent/react.min.js"]
                                          :elide-asserts true
                                          :pretty-print  false
                                          :optimizations :none}}]}
   :aliases {"auto-demo" ["do" "clean," "cljsbuild" "clean," "cljsbuild" "auto" "demo,"]
            "auto-test" ["do" "clean," "cljsbuild" "auto" "test"]}
 )
