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
                                            [figwheel                        "0.2.1-SNAPSHOT"]
                                            [spellhouse/clairvoyant          "0.0-48-gf5e59d3"]]
                             :plugins      [[lein-cljsbuild                  "1.0.4"]             ;; previously 1.0.4, 1.0.4-SNAPSHOT was in a state of flux
                                            [lein-figwheel                   "0.2.1-SNAPSHOT"]
                                            [com.cemerick/clojurescript.test "0.3.3"]]}}

  :resource-paths ["run/resources"]
  ;:jvm-opts         ^:replace ["-Xms2g" "-Xmx2g" "-server"]

  :source-paths     ["src"]
  :test-paths       ["test"]

  :clean-targets    ^{:protect false} ["run/resources/public/compiled" "compiled/test"] ;; :output-to

  ;; Exclude the demo code from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/"]

  :cljsbuild        {:builds [{:id "demo"
                               :source-paths   ["src"]
                               :compiler       {:output-to     "run/resources/public/compiled/demo.js"
                                                :source-map    "run/resources/public/compiled/demo.js.map"
                                                :output-dir    "run/resources/public/compiled/demo"
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
                               :source-paths   ["src/re_com" "test"]
                               :compiler       {:output-to    "compiled/test.js"
                                                :source-map   "compiled/test.js.map"
                                                :output-dir   "compiled/test"
                                                :optimizations :none
                                                :pretty-print true}}]}

  :figwheel         {:http-server-root "public" ;; this will be in resources/
                     :server-port 3449          ;; default

                     ;; CSS reloading (optional)
                     ;; :css-dirs has no default value
                     ;; if :css-dirs is set figwheel will detect css file changes and
                     ;; send them to the browser
                     :css-dirs ["run/resources/public/resources/css"]

                     ;; Server Ring Handler (optional)
                     ;; if you want to embed a ring handler into the figwheel http-kit
                     ;; server
                     ;;;;; :ring-handler example.server/handler

                     ;; To be able to open files in your editor from the heads up display
                     ;; you will need to put a script on your path.
                     ;; that script will have to take a file path and a line number
                     ;; ie.
                     ;;
                     ;; #! /bin/sh
                     ;; emacsclient -n +$2 $1
                     ;;
                     ;;;;; :open-file-command "myfile-opener"
                     }

  :aliases          {"auto-demo"     ["do" "clean," "cljsbuild" "auto" "demo,"]
                     "auto-demo-fig" ["do" "clean," "figwheel" "demo,"]
                     "auto"          ["do" "cljsbuild" "auto" "demo,"]
                     "once"          ["do" "cljsbuild" "once" "demo,"]
                     "auto-test"     ["do" "cljsbuild" "auto" "test"]}
  )
