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

  :clean-targets    ^{:protect false} ["run/resources/public/compiled"] ;; Removed "compiled" and :output-to

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

  :shell            {:commands {"runurl"      {:windows ["cmd" "/c" "start"]
                                               :linux   "xdg-open"}
                                }}

  :aliases          {;; *** DEMO ***
                     ;"build"          ["do"
                     ;                  ["clean"]
                     ;                  ;["figwheel" "demo"]         ;; Moved to debug below
                     ;                  ["cljsbuild" "once" "demo"]
                     ;                  ]

                     "run"            ["shell" "cmd" "/c" "start" "" "http://localhost:3449/index.html"] ;; TODO: Could report this as issue
                     "run-linux"      ["shell" "xdg-open" "http://localhost:3449/index.html"]

                     ;; TODO: Remove experiments
                     ;"run2"           ["shell" "runurl" "/c" "start" "http://localhost:3449/index.html"]
                     ;"run3"           ["shell" "runurl" "runurl-arg1" "runurl-arg2" "http://localhost:3449/index.html"]

                     ;"run-cb"         ["shell" "cmd" "/c" "start" "run/resources/public/index.html"]
                     ;"run-cb-linux"   ["shell" "xdg-open" "run/resources/public/index.html"]

                     "debug"          ["figwheel" "demo"]
                     ;"debug-cb"       ["cljsbuild" "auto" "demo"]

                     "build"           ["do"                ;; TODO: Linux version required
                                       ["clean"]
                                       ["run"]              ;; NOTE: run will initially fail, refresh browser once build complete
                                       ["debug"]]

                     ;"build-debug-cb" ["do"                 ;; TODO: Linux version required
                     ;                  ["build"]
                     ;                  ["run-cb"]
                     ;                  ["debug-cb"]]

                     ;; *** TEST - CLJSBUILD ***
                     "build-test"     ["do"
                                       ["clean"]
                                       ["cljsbuild" "once" "test"]]

                     "run-test"       ["shell" "cmd" "/c" "start" "run/resources/public/test.html"]
                     "run-test-linux" ["shell" "xdg-open" "run/resources/public/test.html"]

                     "debug-test"     ["cljsbuild" "auto" "test"]

                     "test"           ["do"                 ;; TODO: Linux version required
                                       ["build-test"]
                                       ["run-test"]
                                       ["debug-test"]]

                     ;; *** OTHERS ***
                     "build-all"      ["do"
                                       ["clean"]
                                       ["cljsbuild" "once"]]

                     "run-all"        ["do"                 ;; TODO: Linux version required
                                       ["run"]
                                       ["run-test"]]

                     "the-works"      ["do"
                                       ["build-all"]
                                       ["run-all"]
                                       ["debug"]
                                       ;["debug-test"]       ;; This command would only start running on exit from the above command (which is a loop)
                                       ]
                     }
  )
