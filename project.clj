(require 'leiningen.core.eval)

(def fig-port 3449)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "0.8.1"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies     [[org.clojure/clojure         "1.7.0"]
                     [org.clojure/clojurescript   "1.7.145"]
                     [reagent                     "0.5.1"]
                     [com.andrewmcveigh/cljs-time "0.3.14"]]

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

  :profiles         {:dev      {:dependencies [[clj-stacktrace                  "0.2.8"]
                                               [alandipert/storage-atom         "1.2.4" ]
                                               [figwheel                        "0.4.1"]
                                               [spellhouse/clairvoyant          "0.0-48-gf5e59d3"]
                                               [secretary                       "1.2.3"]]
                                :plugins      [[lein-cljsbuild                  "1.1.1-SNAPSHOT"]
                                               [lein-figwheel                   "0.4.1"]
                                               [lein-shell                      "0.5.0"]
                                               [com.cemerick/clojurescript.test "0.3.3"]
                                               [lein-s3-static-deploy           "0.1.1-SNAPSHOT"]
                                               [lein-ancient                    "0.6.2"]]}
                     :dev-run  {:clean-targets ^{:protect false} ["run/resources/public/compiled_dev"]}
                     :prod-run {:clean-targets ^{:protect false} ["run/resources/public/compiled_prod"]}
                     :dev-test {:clean-targets ^{:protect false} ["run/test/compiled"]}}

  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

  ;; Exclude the demo and compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)demo\/" #"(?:^|\/)compiled.*\/" #"html$"]

  :cljsbuild {:builds [{:id           "demo"
                        :source-paths ["src"]
                        :figwheel     {:on-jsload     "re-demo.core/mount-demo"}
                        :compiler     {:output-to     "run/resources/public/compiled_dev/demo.js"
                                       :output-dir    "run/resources/public/compiled_dev/demo"
                                       :main          "re-demo.core"
                                       :asset-path    "compiled_dev/demo"
                                       :source-map    true
                                       :optimizations :none
                                       :pretty-print  true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:output-to       "run/resources/public/compiled_prod/demo.js"
                                       :output-dir      "run/resources/public/compiled_prod/demo"
                                       :closure-defines {:goog.DEBUG false}
                                       :optimizations   :advanced
                                       :pretty-print    false
                                       :pseudo-names    false}}
                       {:id           "test"
                        :source-paths ["src/re_com" "test"]
                        :compiler     {:output-to     "run/test/compiled/test.js"
                                       :output-dir    "run/test/compiled/test"
                                       :source-map    true
                                       :optimizations :none
                                       :pretty-print  true}}]}

  :figwheel {:css-dirs    ["run/resources/public/assets/css"]
             :server-port ~fig-port
             :repl        true}

  :aws {:access-key       ~(System/getenv "DAY8_AWS_ACCESS_KEY_ID")
        :secret-key       ~(System/getenv "DAY8_AWS_SECRET_ACCESS_KEY")
        :s3-static-deploy {:bucket     "re-demo"
                           :local-root "run/resources/public"}}

  :release-tasks [["shell" "git" "checkout" "master"]
                  ["shell" "git" "pull" "--ff-only"]
                  ["shell" "git" "checkout" "develop"]
                  ["shell" "git" "flow" "release" "start" "lein-release${:version}"]
                  ["vcs" "assert-committed"]
                  ["change" "version"
                   "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "--no-sign"]
                  ["deploy"]
                  ["shell" "git" "flow" "release" "finish" "--notag" "--nopush"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]
                  ["shell" "git" "checkout" "master"]
                  ["shell" "git" "push"]
                  ["shell" "git" "checkout" "develop"]]

  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :aliases          {;; *** DEMO ***

                     "run"        ["with-profile" "+dev-run" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "demo"]
                                   ["shell" "open" "run/resources/public/index_dev.html"]]

                     "debug"      ["with-profile" "+dev-run" "do"
                                   ["clean"]
                                   ~["shell" "open" (str "http://localhost:" fig-port "/index_dev.html")]   ;; NOTE: run will initially fail, refresh browser once build complete
                                   ["figwheel" "demo"]]

                     ;; *** PROD ***

                     "run-prod"   ["with-profile" "+prod-run" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "prod"]
                                   ["shell" "open" "run/resources/public/index_prod.html"]]

                     "debug-prod" ["with-profile" "+prod-run" "do"
                                   ["run-prod"]
                                   ["cljsbuild" "auto" "prod"]]

                     "deploy-aws"  ["with-profile" "+prod-run" "do"
                                    ["clean"]
                                    ["cljsbuild" "once" "prod"]
                                    ["s3-static-deploy"]]

                     ;; *** TEST ***

                     "run-test"   ["with-profile" "+dev-test" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "test"]
                                   ["shell" "open" "run/test/test.html"]]

                     "debug-test" ["with-profile" "+dev-test" "do"
                                   ["run-test"]
                                   ["cljsbuild" "auto" "test"]]}
  )
