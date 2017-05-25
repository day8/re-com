(require 'leiningen.core.eval)

(def fig-port 3449)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "2.1.0"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies     [[org.clojure/clojure         "1.8.0"]
                     [org.clojure/clojurescript   "1.9.456"]
                     [reagent                     "0.6.0"]
                     [org.clojure/core.async "0.2.395"]
                     [com.andrewmcveigh/cljs-time "0.4.0"]] ;; TODO: Bump this to 0.5.0 as soon as it's available (https://github.com/Day8/re-com/pull/98/files)

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

  :profiles         {:dev       {:dependencies [[clj-stacktrace                  "0.2.8"]
                                                [figwheel                        "0.5.9"]
                                                [binaryage/devtools              "0.9.0"]
                                                [binaryage/dirac                 "RELEASE"]]
                                 :repl-options {:port 8230
                                                :nrepl-middleware [dirac.nrepl/middleware]
                                                :init (do (require 'dirac.agent)
                                                          (dirac.agent/boot!))}
                                 :plugins      [[lein-cljsbuild                  "1.1.5"]
                                                [lein-figwheel                   "0.5.9"]
                                                [lein-shell                      "0.5.0"]
                                                [org.clojure/data.json           "0.2.6"]
                                                [lein-s3-static-deploy           "0.1.1-SNAPSHOT"]
                                                [lein-ancient                    "0.6.10"]]}
                     :demo {:dependencies [[alandipert/storage-atom "2.0.1"]
                                           [com.cognitect/transit-cljs "0.8.239"] ;; Overrides version in storage-atom which prevents compiler warnings about uuid? and boolean? being replaced
                                           [secretary "1.2.3"]]}
                     :dev-cider {:figwheel {:nrepl-port       7777
                                            :nrepl-middleware ["cider.nrepl/cider-middleware"
                                                               "cemerick.piggieback/wrap-cljs-repl"]}
                                 :dependencies [[com.cemerick/piggieback "0.2.1"]
                                                [figwheel-sidecar "0.5.9"]]}
                     :dev-run   {:clean-targets ^{:protect false} ["run/resources/public/compiled_dev"]}
                     :prod-run  {:clean-targets ^{:protect false} ["run/resources/public/compiled_prod"]}
                     :dev-test  {:clean-targets ^{:protect false} ["run/test/compiled"]}}

  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]

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
                                   ["s3-static-deploy"]]

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

