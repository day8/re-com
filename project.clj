(require 'leiningen.core.eval)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "lein-git-inject/version"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies [[org.clojure/clojure         "1.11.1"   :scope "provided"]
                 [org.clojure/clojurescript   "1.11.132" :scope "provided"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs        "2.28.2"   :scope "provided"]
                 [reagent                     "1.1.0"    :scope "provided"]
                 [org.clojure/core.async      "1.3.618"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]
                 [day8/shadow-git-inject "0.0.5"]
                 [hashp/hashp "0.2.2"]]

  :plugins      [[day8/lein-git-inject    "0.0.15"]
                 [com.github.liquidz/antq "RELEASE"]
                 [lein-shell              "0.5.0"]
                 [lein-pprint             "1.3.2"]]

  :middleware   [leiningen.git-inject/middleware]

  :antq     {}

  :profiles {:dev      {:source-paths ["dev-src"]
                        :dependencies [[clj-stacktrace        "0.2.8"]
                                       [binaryage/devtools    "1.0.3"]]
                        :plugins      [[org.clojure/data.json "2.4.0"]]}

             :demo     {:dependencies [[alandipert/storage-atom "2.0.1"]
                                       [com.cognitect/transit-cljs "0.8.264"] ;; Overrides version in storage-atom which prevents compiler warnings about uuid? and boolean? being replaced
                                       [clj-commons/secretary "1.2.4"]]}}

  :source-paths    ["src"]
  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :clean-targets ^{:protect false} [:target-path
                                    "node_modules"
                                    "run/resources/public/compiled_dev"
                                    "run/resources/public/compiled_prod"
                                    "run/resources/public/compiled_test"]

  :deploy-repositories [["clojars"  {:sign-releases false
                                     :url "https://clojars.org/repo"
                                     :username :env/CLOJARS_USERNAME
                                     :password :env/CLOJARS_TOKEN}]]

  ;; Exclude the demo and compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)demo\/" #"(?:^|\/)compiled.*\/" #"html$"]

  :release-tasks [["deploy" "clojars"]]

  :shell {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows         ["cmd" "/c" "start"]
                              :macosx          "open"
                              :linux           "xdg-open"}}}

  :aliases          {;; *** DEV ***
                     "watch"   ["with-profile" "+dev,+demo" "do"
                                ["clean"]
                                ["shell" "npm" "install"]
                                ["shell" "npx" "shadow-cljs" "watch" "demo" "browser-test" "karma-test"]]

                     ;; *** PROD ***
                     "prod-once"  ["with-profile" "+demo,-dev" "do"
                                   ["clean"]
                                   ["shell" "npm" "install"]
                                   ["shell" "npx" "shadow-cljs" "release" "demo"]]

                     "deploy-aws" ["with-profile" "+demo,-dev" "do"
                                   ["clean"]
                                   ["shell" "npm" "install"]
                                   ["shell" "npx" "shadow-cljs" "release" "demo"]
                                   ~["shell" "aws" "s3" "sync" "run/resources/public" "s3://re-demo/" "--acl" "public-read" "--cache-control" "max-age=2592000,public"]]

                     ;; *** TEST ***
                     "build-report-ci" ["with-profile" "+demo,-dev" "do"
                                        ["clean"]
                                        ["shell" "npm" "install"]
                                        ["shell" "npx" "shadow-cljs" "clj-run" "shadow.cljs.build-report" "demo" "target/build-report.html"]]

                     "ci" ["do"
                           ["with-profile" "+dev" "do"
                            ["clean"]
                            ["shell" "npm" "install"]
                            ["shell" "npx" "shadow-cljs" "compile" "karma-test"]
                            ["shell" "npx" "karma" "start" "--single-run" "--reporters" "junit,dots"]]
                           ["with-profile" "+demo,-dev" "do"
                            ["clean"]
                            ["shell" "npm" "install"]
                            ["shell" "npx" "shadow-cljs" "release" "demo"]]]})
