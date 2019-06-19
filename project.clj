(require 'leiningen.core.eval)

(def fig-port 3449)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "2.6.0-SNAPSHOT"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies     [[org.clojure/clojure         "1.10.1"]
                     [org.clojure/clojurescript   "1.10.439"]
                     [reagent                     "0.8.1"]
                     [org.clojure/core.async      "0.4.474"]
                     [com.andrewmcveigh/cljs-time "0.5.2"]]

  :profiles         {:dev       {:dependencies [[clj-stacktrace                  "0.2.8"]
                                                [binaryage/devtools              "0.9.10"]
                                                [binaryage/dirac                 "RELEASE"]]
                                 :repl-options {:port 8230
                                                :nrepl-middleware [dirac.nrepl/middleware]
                                                :init (do (require 'dirac.agent)
                                                          (dirac.agent/boot!))}
                                 :plugins      [[lein-cljsbuild                  "1.1.7"]
                                                [lein-figwheel                   "0.5.18"]
                                                [lein-shell                      "0.5.0"]
                                                [org.clojure/data.json           "0.2.6"]
                                                [lein-ancient                    "0.6.15"]]}
                     :demo      {:dependencies [[alandipert/storage-atom         "2.0.1"]
                                                [com.cognitect/transit-cljs      "0.8.239"] ;; Overrides version in storage-atom which prevents compiler warnings about uuid? and boolean? being replaced
                                                [clj-commons/secretary           "1.2.4"]]}
                     :dev-cider {:figwheel {:nrepl-port       7777
                                            :nrepl-middleware ["cider.nrepl/cider-middleware"
                                                               "cemerick.piggieback/wrap-cljs-repl"]}
                                 :dependencies [[com.cemerick/piggieback "0.2.2"]
                                                #_[figwheel-sidecar "0.5.12"]]}
                     :dev-run   {:clean-targets ^{:protect false} ["run/resources/public/compiled_dev"]}
                     :prod-run  {:clean-targets ^{:protect false} ["run/resources/public/compiled_prod"]}
                     :dev-test  {:clean-targets ^{:protect false} ["run/test/compiled"]}}

  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :deploy-repositories [["releases"  {:sign-releases false :url "https://clojars.org/repo"}]
                        ["snapshots" {:sign-releases false :url "https://clojars.org/repo"}]]

  ;; Exclude the demo and compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)demo\/" #"(?:^|\/)compiled.*\/" #"html$"]

  :cljsbuild {:builds [{:id           "demo"
                        :source-paths ["dev-src" "src"]
                        :figwheel     {:on-jsload     "re-demo.core/mount-demo"}
                        :compiler     {:preloads        [devtools.preload day8.app.dev-preload]
                                       :external-config {:devtools/config {:features-to-install [:formatters :hints]}}
                                       :output-to       "run/resources/public/compiled_dev/demo.js"
                                       :output-dir      "run/resources/public/compiled_dev/demo"
                                       :main            "re-demo.core"
                                       :asset-path      "compiled_dev/demo"
                                       :source-map      true
                                       :optimizations   :none
                                       :pretty-print    true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:output-to       "run/resources/public/compiled_prod/demo.js"
                                       :output-dir      "run/resources/public/compiled_prod/demo"
                                       :closure-defines {"goog.DEBUG" false}
                                       :optimizations   :advanced
                                       :pretty-print    false
                                       :pseudo-names    false}}
                       {:id           "test"
                        :source-paths ["dev-src" "src/re_com" "test"]
                        :compiler     {:preloads        [devtools.preload day8.app.dev-preload]
                                       :external-config {:devtools/config {:features-to-install [:formatters :hints]}}
                                       :output-to       "run/test/compiled/test.js"
                                       :output-dir      "run/test/compiled/test"
                                       :source-map      true
                                       :optimizations   :none
                                       :pretty-print    true}}]
              :test-commands   {}} ;; figwheel 0.5.2 required this for some reason

  :figwheel {:css-dirs    ["run/resources/public/assets/css"]
             :server-port ~fig-port
             :repl        false}

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :aliases          {;; *** DEV ***

                     "dev-once"   ["with-profile" "+dev-run,+demo" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "demo"]
                                   ["shell" "open" "run/resources/public/index_dev.html"]]

                     "dev-auto"   ["with-profile" "+dev-run,+demo" "do"
                                   ["clean"]
                                   ~["shell" "open" (str "http://localhost:" fig-port "/index_dev.html")]   ;; NOTE: run will initially fail, refresh browser once build complete
                                   ["figwheel" "demo"]]

                     ;; *** PROD ***

                     "prod-once"  ["with-profile" "+prod-run,+demo,-dev" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "prod"]
                                   ["shell" "open" "run/resources/public/index_prod.html"]]

                     "prod-auto"  ["with-profile" "+prod-run,+demo,-dev" "do"
                                   ["prod-once"]
                                   ["cljsbuild" "auto" "prod"]]

                     "deploy-aws" ["with-profile" "+prod-run,+demo,-dev" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "prod"]
                                   ~["shell" "aws" "s3" "--profile=day8" "sync" "run/resources/public" "s3://re-demo/" "--acl" "public-read" "--cache-control" "max-age=2592000,public"]]

                     ;; *** TEST ***

                     "test" ["do"
                             ["with-profile" "+dev-test" "do"
                              ["clean"]
                              ["cljsbuild" "once" "test"]]
                             ["with-profile" "+prod-run,+demo,-dev" "do"
                              ["clean"]
                              ["cljsbuild" "once" "prod"]]]
                     "test-once"  ["with-profile" "+dev-test" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "test"]
                                   ["shell" "open" "run/test/test.html"]]

                     "test-auto"  ["with-profile" "+dev-test" "do"
                                   ["test-once"]
                                   ["cljsbuild" "auto" "test"]]})

