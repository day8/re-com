(require 'leiningen.core.eval)

(def http-port 3449)

;; ---------------------------------------------------------------------------------------

(defproject         re-com "lein-git-inject/version"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies [[org.clojure/clojure         "1.10.3"   :scope "provided"]
                 [org.clojure/clojurescript   "1.10.879" :scope "provided"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs        "2.15.2"   :scope "provided"]
                 [reagent                     "1.1.0"    :scope "provided"]
                 [org.clojure/core.async      "1.3.610"]
                 [com.andrewmcveigh/cljs-time "0.5.2"]]

  :plugins      [[day8/lein-git-inject    "0.0.14"]
                 [lein-shadow             "0.3.1"]
                 [com.github.liquidz/antq "RELEASE"]
                 [lein-shell              "0.5.0"]
                 [lein-pprint             "1.3.2"]]


  :middleware   [leiningen.git-inject/middleware]

  :antq     {}

  :profiles {:dev      {:source-paths ["dev-src"]
                        :dependencies [[clj-stacktrace        "0.2.8"]
                                       [binaryage/devtools    "1.0.3"]]
                        :plugins      [[org.clojure/data.json "2.0.1"]]}

             :demo     {:dependencies [[alandipert/storage-atom "2.0.1"]
                                       [com.cognitect/transit-cljs "0.8.264"] ;; Overrides version in storage-atom which prevents compiler warnings about uuid? and boolean? being replaced
                                       [clj-commons/secretary "1.2.4"]]}}

  :source-paths    ["src"]
  :test-paths      ["test"]
  :resource-paths  ["run/resources"]

  :clean-targets ^{:protect false} [:target-path
                                    "shadow-cljs.edn"
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

  :shadow-cljs {:nrepl  {:port 7777}

                :builds {:demo         {:target           :browser
                                        :modules          {:demo {:init-fn  re-demo.core/mount-demo
                                                                  :preloads [day8.app.dev-preload]}}
                                        :compiler-options {:closure-defines {re-com.config/version                  "lein-git-inject/version"
                                                                             ;; For production builds of the demo app, set goog.DEBUG
                                                                             ;; to be true so that the debugging demo page works as expected.
                                                                             goog.DEBUG                             true
                                                                             re-com.config/force-include-args-desc? true}
                                                           ;; For production builds of the demo app, keep the component name
                                                           ;; symbols for display in validation error logging.
                                                           :pseudo-names    true
                                                           :externs         ["externs/detect-element-resize-externs.js"]}
                                        :dev              {:asset-path       "/compiled_dev/demo"
                                                           :output-dir       "run/resources/public/compiled_dev/demo"
                                                           :compiler-options {:closure-defines {;; When re-com produces validation errors it tries to provide links
                                                                                                ;; to source code. These links require that you provide the root URL
                                                                                                ;; to the ClojureScript compiler output with source maps.
                                                                                                re-com.config/root-url-for-compiler-output "http://localhost:3449/compiled_dev/demo/cljs-runtime/"}
                                                                              :external-config {:devtools/config {:features-to-install [:formatters :hints]}}}}
                                        :release          {:output-dir "run/resources/public/compiled_prod/demo"
                                                           :compiler-options {:closure-defines {;; For production builds, such as the demo website, there is no source
                                                                                                ;; code to link to in validation errors or component stacks, so we set
                                                                                                ;; it to an empty string to cause links to not be displayed at all.
                                                                                                re-com.config/root-url-for-compiler-output ""}}}
                                        :devtools         {:http-port        ~http-port
                                                           :http-root        "run/resources/public"
                                                           :push-state/index "index_dev.html"}}

                         :browser-test {:target           :browser-test
                                        :ns-regexp        "-test$"
                                        :test-dir         "run/resources/public/compiled_test/demo"
                                        :compiler-options {:closure-defines {re-com.config/version "lein-git-inject/version"}
                                                           :externs         ["externs/detect-element-resize-externs.js"]
                                                           :external-config {:devtools/config {:features-to-install [:formatters :hints]}}}
                                        :devtools         {:http-port 8021
                                                           :http-root "run/resources/public/compiled_test/demo"
                                                           :preloads  [day8.app.dev-preload]}}
                         :karma-test   {:target           :karma
                                        :ns-regexp        ".*-test$"
                                        :output-to        "target/karma/test.js"
                                        :compiler-options {:pretty-print true
                                                           :closure-defines {re-com.config/version "lein-git-inject/version"}
                                                           :externs         ["externs/detect-element-resize-externs.js"]}}}}

  :release-tasks [["deploy" "clojars"]]

  :shell {:commands {"karma" {:windows         ["cmd" "/c" "karma"]
                              :default-command "karma"}
                     "open"  {:windows         ["cmd" "/c" "start"]
                              :macosx          "open"
                              :linux           "xdg-open"}}}

  :aliases          {;; *** DEV ***
                     "watch"   ["with-profile" "+dev,+demo" "do"
                                   ["clean"]
                                   ["shadow" "watch" "demo" "browser-test" "karma-test"]]

                     ;; *** PROD ***
                     "prod-once"  ["with-profile" "+demo,-dev" "do"
                                   ["clean"]
                                   ["shadow" "release" "demo"]]

                     "deploy-aws" ["with-profile" "+demo,-dev" "do"
                                   ["clean"]
                                   ["shadow" "release" "demo"]
                                   ~["shell" "aws" "s3" "sync" "run/resources/public" "s3://re-demo/" "--acl" "public-read" "--cache-control" "max-age=2592000,public"]]

                     ;; *** TEST ***
                     "build-report-ci" ["with-profile" "+demo,-dev" "do"
                                        ["clean"]
                                        ["shadow" "run" "shadow.cljs.build-report" "demo" "target/build-report.html"]]

                     "ci" ["do"
                             ["with-profile" "+dev" "do"
                              ["clean"]
                              ["shadow" "compile" "karma-test"]
                              ["shell" "karma" "start" "--single-run" "--reporters" "junit,dots"]]
                             ["with-profile" "+demo,-dev" "do"
                              ["clean"]
                              ["shadow" "release" "demo"]]]})


