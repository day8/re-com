(defproject re-com "0.1.3"
  :description  "Reusable UI components for Reagent"

  :url          "https://github.com/Day8/re-com.git"


  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2322"]
                 [org.clojure/core.async "0.1.338.0-5c5012-alpha"]
                 [alandipert/storage-atom "1.2.3"]
                 [reagent "0.4.2"]
                 [com.andrewmcveigh/cljs-time "0.1.6"]]

  :profiles     {:debug {:debug true}
                 :dev {:dependencies [[clj-stacktrace "0.2.8"]
                                      [spellhouse/clairvoyant "0.0-39-gb64d916"]]
                       :plugins      [[lein-cljsbuild "1.0.4-SNAPSHOT"]
                                      [com.cemerick/clojurescript.test "0.3.1"]]}}

  ;:jvm-opts     ^:replace ["-Xms2g" "-Xmx2g" "-server"]

  :source-paths ["src"]
  :test-paths   ["test"]

  ;; Exclude the demo code from the output of either:
  ;;   - lein jar
  ;;   - lein install
  :jar-exclusions [#"(?:^|\/)re_demo\/"]

  :cljsbuild    {:builds [{:id "demo"
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
                                            :optimizations :advanced
                                            :pretty-print  false}}
                          {:id "test"
                           :source-paths ["src/re_com" "test"]
                           :compiler        {:output-to "compiled/test.js"
                                             :output-dir "compiled/test"
                                             :optimizations :none
                                             :pretty-print true
                                             :source-map "compiled/test.js.map"}}]}

  :aliases      {"auto-demo" ["do" "clean," "cljsbuild" "clean," "cljsbuild" "auto" "demo,"]
                 "auto"      ["do" "cljsbuild" "auto" "demo,"]
                 "once"      ["do" "cljsbuild" "once" "demo,"]
                 "auto-test" ["do" "clean," "cljsbuild" "auto" "test"]}
  )
