(require 'leiningen.core.eval)

(def os         (leiningen.core.eval/get-os))
(def server-url "http://localhost:3449/index.html")
(def file-url   "run/resources/public/index.html")
(def test-url   "run/resources/public/test.html")

(def command-lookups
  "Per os native commands"
  {"launch-server-url"     {:windows ["shell" "cmd" "/c" "start" server-url]
                            :macosx  ["shell" "open"             server-url]
                            :linux   ["shell" "xdg-open"         server-url]}

   "launch-file-url"       {:windows ["shell" "cmd" "/c" "start" file-url]
                            :macosx  ["shell" "open"             file-url]
                            :linux   ["shell" "xdg-open"         file-url]}

   "launch-test-url"       {:windows ["shell" "cmd" "/c" "start" test-url]
                            :linux   ["shell" "xdg-open"         test-url]
                            :macosx  ["shell" "open"             test-url]}})

(defn get-command-for-os
      "Return the os-dependent command"
      [cmd]
      (get-in command-lookups [cmd os]))

;; ---------------------------------------------------------------------------------------

(defproject         re-com "0.1.6"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"

  :dependencies     [[org.clojure/clojure         "1.6.0"]
                     [org.clojure/clojurescript   "0.0-2665"]
                     [org.clojure/core.async      "0.1.346.0-17112a-alpha"]
                     [alandipert/storage-atom     "1.2.3"]
                     [reagent                     "0.5.0-alpha"]
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
                             :plugins      [[lein-cljsbuild                  "1.0.4"]
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

  :aliases          {;; *** DEMO ***

                     "run"              ["do"
                                         ["clean"]
                                         ["cljsbuild" "once" "demo"]
                                         ~(get-command-for-os "launch-file-url")]

                     "debug"            ["do"
                                         ["clean"]
                                         ~(get-command-for-os "launch-server-url")   ;; NOTE: run will initially fail, refresh browser once build complete
                                         ["figwheel" "demo"]]

                     ;; *** TEST ***

                     "run-test"         ["do"
                                         ["clean"]
                                         ["cljsbuild" "once" "test"]
                                         ~(get-command-for-os "launch-test-url")]

                     "debug-test"       ["do"
                                         ["run-test"]
                                         ["cljsbuild" "auto" "test"]]

                     ;; *** DEMO AND TEST ***
                     ;; If you ever need to debug both demo and test at the one time, there's a problem.
                     ;; If you run "lein debug" in one terminal then "lein debug-test" in another, the "clean" commands will clobber on another.
                     ;; Below is an attempt to resolve this:
                     ;;     1. In on terminal, run "lein debug-all".
                     ;;     2. In another termial, once that is complete, run "lein build-test-auto".

                     "build-test-auto"  ["cljsbuild" "auto" "test"]

                     "debug-all"        ["do"
                                         ["clean"]
                                         ["cljsbuild" "once"]
                                         ~(get-command-for-os "launch-server-url")
                                         ~(get-command-for-os "launch-test-url")
                                         ["figwheel" "demo"]]}
  )
