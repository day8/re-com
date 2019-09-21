(require 'leiningen.core.eval)

(def http-port 3449)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "2.6.1-SNAPSHOT"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library]]
                 [thheller/shadow-cljs "2.8.55" :scope "provided"]
                 [reagent "0.8.1" :scope "provided"]
                 [org.clojure/core.async "0.4.500"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]]

  :plugins [[lein-shadow "0.1.5"]]

  :profiles {:dev      {:dependencies [[clj-stacktrace "0.2.8"]
                                       [binaryage/devtools "0.9.10"]]
                        :plugins      [[lein-shell "0.5.0"]
                                       [org.clojure/data.json "0.2.6"]
                                       [lein-ancient "0.6.15"]]}
             :demo     {:dependencies [[alandipert/storage-atom "2.0.1"]
                                       [com.cognitect/transit-cljs "0.8.256"] ;; Overrides version in storage-atom which prevents compiler warnings about uuid? and boolean? being replaced
                                       [clj-commons/secretary "1.2.4"]]}
             :dev-run  {:source-paths  ["dev-src"]}
             :prod-run {}
             :dev-test {:source-paths  ["dev-src"]}}

  :source-paths    ["src"]
  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :clean-targets ^{:protect false} [:target-path
                                    "shadow-cljs.edn"
                                    "package.json"
                                    "package-lock.json"
                                    "run/resources/public/compiled_dev"
                                    "run/resources/public/compiled_prod"
                                    "run/resources/public/compiled_test"]

  :deploy-repositories [["clojars"  {:sign-releases false
                                     :url "https://clojars.org/repo"
                                     :username :env/CLOJARS_USERNAME
                                     :password :env/CLOJARS_PASSWORD}]]

  ;; Exclude the demo and compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)demo\/" #"(?:^|\/)compiled.*\/" #"html$"]

  :shadow-cljs {:nrepl  {:port 7777}

                :builds {:demo         {:target   :browser
                                        :modules  {:demo {:init-fn  re-demo.core/mount-demo
                                                          :preloads [day8.app.dev-preload]}}
                                        :dev      {:asset-path       "/compiled_dev/demo"
                                                   :output-dir       "run/resources/public/compiled_dev/demo"
                                                   :compiler-options {:external-config {:devtools/config {:features-to-install [:formatters :hints]}}}}
                                        :release  {:output-dir "run/resources/public/compiled_prod/demo"}
                                        :devtools {:http-port ~http-port
                                                   :http-root "run/resources/public"}}

                         :browser-test {:target           :browser-test
                                        :ns-regexp        "-test$"
                                        :test-dir         "run/resources/public/compiled_test/demo"
                                        :compiler-options {:external-config {:devtools/config {:features-to-install [:formatters :hints]}}}
                                        :devtools         {:http-port 8021
                                                           :http-root "run/resources/public/compiled_test/demo"
                                                           :preloads  [day8.app.dev-preload]}}}}

  :release-tasks [["vcs" "assert-committed"]
                  ["change" "version" "leiningen.release/bump-version" "release"]
                  ["vcs" "commit"]
                  ["vcs" "tag" "v" "--no-sign"]
                  ["deploy" "clojars"]
                  ["change" "version" "leiningen.release/bump-version"]
                  ["vcs" "commit"]
                  ["vcs" "push"]]

  :shell {:commands {"open" {:windows ["cmd" "/c" "start"]
                             :macosx  "open"
                             :linux   "xdg-open"}}}

  :aliases          {;; *** DEV ***

                     "dev-auto"   ["with-profile" "+dev-run,+demo" "do"
                                   ["clean"]
                                   ~["shell" "open" (str "http://localhost:" http-port "/index_dev.html")]   ;; NOTE: run will initially fail, refresh browser once build complete
                                   ["shadow" "watch" "demo"]]

                     ;; *** PROD ***

                     "prod-once"  ["with-profile" "+prod-run,+demo,-dev" "do"
                                   ["clean"]
                                   ["shadow" "release" "demo"]]

                     "deploy-aws" ["with-profile" "+prod-run,+demo,-dev" "do"
                                   ["clean"]
                                   ["shadow" "release" "demo"]
                                   ~["shell" "aws" "s3" "--profile=day8" "sync" "run/resources/public" "s3://re-demo/" "--acl" "public-read" "--cache-control" "max-age=2592000,public"]]

                     ;; *** TEST ***

                     "test" ["do"
                             ["with-profile" "+dev-test" "do"
                              ["clean"]
                              ["shadow" "compile" "browser-test"]]
                             ["with-profile" "+prod-run,+demo,-dev" "do"
                              ["clean"]
                              ["shadow" "release" "demo"]]]

                     "test-auto"  ["with-profile" "+dev-test" "do"
                                    ["shadow" "watch" "browser-test"]]})

