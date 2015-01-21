(defproject         re-com "0.1.6"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"

  :dependencies     [[org.clojure/clojure         "1.6.0"]
                     [org.clojure/clojurescript   "0.0-2665"]                 ;; previously 2371
                     [org.clojure/core.async      "0.1.346.0-17112a-alpha"]   ;; previously 0.1.338.0-5c5012-alpha
                     [alandipert/storage-atom     "1.2.3"]
                     [reagent                     "0.5.0-alpha"]              ;; previously 0.4.3 / 0.5.0-alpha
                     [com.andrewmcveigh/cljs-time "0.3.0"]]

  ;:plugins          [[lein-unpack-resources "0.1.1"]]
  ;
  ;:unpack-resources {:resource     [re-com "0.1.6"]
  ;                   :extract-path "run/resources2"}
  ; FUTURE VERSION SHOULD SUPPORT THE FOLLOWING SYNTAX
  ;:unpack-resources [{:resource     [re-com "0.1.6"]
  ;                    :extract-from "resources"
  ;                    :extract-path "run/resources-com"
  ;                    :overwrite    true}
  ;                   {:resource     [re-frame "0.1.1"]
  ;                    :extract-from "resources"
  ;                    :extract-path "run/resources-frame"}]

  :profiles         {:debug {:debug true}
                     :dev   {:dependencies [[clj-stacktrace                  "0.2.8"]
                                            [figwheel                        "0.2.2-SNAPSHOT"]
                                            [spellhouse/clairvoyant          "0.0-48-gf5e59d3"]]
                             :plugins      [[lein-cljsbuild                  "1.0.4"]             ;; previously 1.0.4, 1.0.4-SNAPSHOT was in a state of flux
                                            [lein-figwheel                   "0.2.2-SNAPSHOT"]
                                            [lein-shell                      "0.4.0"]
                                            [com.cemerick/clojurescript.test "0.3.3"]]}}

  :resource-paths ["run/resources"]
  ;:jvm-opts         ^:replace ["-Xms2g" "-Xmx2g" "-server"]

  :source-paths     ["src"]
  :test-paths       ["test"]

  :clean-targets    ^{:protect false} ["run/resources/public/compiled" "compiled"] ;; :output-to

  ;; Exclude the demo code from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/"]

  :cljsbuild        {:builds [{:id "demo"
                               :source-paths   ["src"]
                               :compiler       {:output-to     "run/resources/public/compiled/demo.js"
                                                :source-map    "run/resources/public/compiled/demo.js.map"
                                                :output-dir    "run/resources/public/compiled/demo"
                                                :optimizations :none
                                                :pretty-print  true}}
                              {:id "test"
                               :source-paths   ["src/re_com" "test"]
                               :compiler       {:output-to    "run/resources/public/compiled/test.js"
                                                :source-map   "run/resources/public/compiled/test.js.map"
                                                :output-dir   "run/resources/public/compiled/test"
                                                :optimizations :none
                                                :pretty-print true}}]}

  :figwheel         {:css-dirs ["run/resources/public/resources/css"]
                     :repl     true}

  :shell            {:commands {"runurl"      {:windows "cmd"
                                               :linux   "xdg-open"}
                                "runurl-arg1" {:windows "/c"
                                               :linux   ""}
                                "runurl-arg2" {:windows "start"
                                               :linux   ""}}}

  :aliases          {;; *** DEMO - FIGWHEEL ***
                     "build"       ["do"
                                    ["clean"]
                                    ["figwheel" "demo"]]

                     "run"         ["shell" "cmd" "/c" "start" "" "http://localhost:3449/index.html"]
                     "run-linux"   ["shell" "xdg-open" "http://localhost:3449/index.html"]
                     "run2"        ["shell" "runurl" "runurl-arg1" "runurl-arg2" "" "http://localhost:3449/index.html"]

                     "buildrun"    ["do"
                                    ["clean"]
                                    ["shell" "cmd" "/c" "start" "" "http://localhost:3449/index.html"]
                                    ["figwheel" "demo"]]

                     ;; *** DEMO - CLJSBUILD ***
                     "buildrun-cb" ["do"
                                    ["clean"]
                                    ["cljsbuild" "once" "demo"]
                                    ["shell" "cmd" "/c" "start" "run/resources/public/index.html"]
                                    ["cljsbuild" "auto" "demo"]]

                     ;; *** TEST - FIGWHEEL : !!!BROKEN!!! ***
                     "test"        ["do"
                                    ["clean"]
                                    ["shell" "cmd" "/c" "start" "" "http://localhost:3449/test.html"]
                                    ["figwheel" "test"]]

                     ;; *** TEST - CLJSBUILD ***
                     "test-cb"     ["do"
                                    ["clean"]
                                    ["cljsbuild" "once" "test"]
                                    ["shell" "cmd" "/c" "start" "run/resources/public/test.html"]
                                    ["cljsbuild" "auto" "test"]]
                     }
  )
