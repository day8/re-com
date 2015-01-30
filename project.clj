(require 'leiningen.core.eval)

(def fig-port   3449)
(def os         (leiningen.core.eval/get-os))
(def server-url (str "http://localhost:" fig-port "/index.html"))
(def file-url   "run/resources/public/index.html")
(def prod-url   "run/resources/public/index_prod.html")
(def test-url   "run/test/test.html")

(def command-lookups
  "Per os native commands"
  {"launch-server-url" {:windows ["shell" "cmd" "/c" "start" server-url]
                        :macosx  ["shell" "open"             server-url]
                        :linux   ["shell" "xdg-open"         server-url]}

   "launch-file-url"   {:windows ["shell" "cmd" "/c" "start" file-url]
                        :macosx  ["shell" "open"             file-url]
                        :linux   ["shell" "xdg-open"         file-url]}

   "launch-prod-url"   {:windows ["shell" "cmd" "/c" "start" prod-url]
                        :macosx  ["shell" "open"             prod-url]
                        :linux   ["shell" "xdg-open"         prod-url]}

   "launch-test-url"   {:windows ["shell" "cmd" "/c" "start" test-url]
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
                     [org.clojure/clojurescript   "0.0-2740"]
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

  :profiles         {:dev      {:dependencies [[clj-stacktrace                  "0.2.8"]
                                               [figwheel                        "0.2.2-SNAPSHOT"]
                                               [spellhouse/clairvoyant          "0.0-48-gf5e59d3"]]
                                :plugins      [[lein-cljsbuild                  "1.0.4"]
                                               [lein-figwheel                   "0.2.2-SNAPSHOT"]
                                               [lein-shell                      "0.4.0"]
                                               [com.cemerick/clojurescript.test "0.3.3"]]}
                     :dev-run  {:clean-targets ^{:protect false} ["run/resources/public/compiled"]}
                     :prod-run {:clean-targets ^{:protect false} ["run/resources/public/compiled-prod"]}
                     :dev-test {:clean-targets ^{:protect false} ["run/test/compiled"]}}

  ;:jvm-opts         ^:replace ["-Xms2g" "-Xmx2g" "-server"]

  :source-paths     ["src"]
  :test-paths       ["test"]
  :resource-paths   ["run/resources"]

  ;; Exclude the demo/compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)compiled\/"]

  ;:clean-targets ^{:protect false} ["run/resources/public/compiled"]

  :cljsbuild        {:builds [{:id "demo"
                               :source-paths   ["src"]
                               :compiler       {:output-to     "run/resources/public/compiled/demo.js"
                                                :source-map    "run/resources/public/compiled/demo.js.map"
                                                :output-dir    "run/resources/public/compiled/demo"
                                                :optimizations :none
                                                :pretty-print  true}}
                              {:id "prod"
                               :source-paths   ["src/re_com" "src/re_demo"]
                               :compiler       {:output-to     "run/resources/public/compiled-prod/prod.js"
                                                :output-dir    "run/resources/public/compiled-prod/prod"
                                                ;:preamble      ["reagent/react.min.js"]
                                                ;:elide-asserts true
                                                :optimizations :advanced
                                                :pretty-print  false
                                                }}
                              {:id "test"
                               :source-paths   ["src/re_com" "test"]
                               :compiler       {:output-to    "run/test/compiled/test.js"
                                                :source-map   "run/test/compiled/test.js.map"
                                                :output-dir   "run/test/compiled/test"
                                                :optimizations :none
                                                :pretty-print true}}]}

  :figwheel {:css-dirs    ["run/resources/public/resources/css"]
             :server-port ~fig-port
             :repl        true}

  :aliases          {;; *** DEMO ***

                     "run"              ["with-profile" "+dev-run" "do"
                                         ["clean"]
                                         ["cljsbuild" "once" "demo"]
                                         ~(get-command-for-os "launch-file-url")]

                     "debug"            ["with-profile" "+dev-run" "do"
                                         ["clean"]
                                         ~(get-command-for-os "launch-server-url")   ;; NOTE: run will initially fail, refresh browser once build complete
                                         ["figwheel" "demo"]]

                     ;; *** PROD ***

                     "run-prod"          ["with-profile" "+prod-run" "do"
                                         ["clean"]
                                         ["cljsbuild" "once" "prod"]
                                         ~(get-command-for-os "launch-prod-url")]

                     "debug-prod"       ["with-profile" "+prod-run" "do"
                                         ["run-prod"]
                                         ["cljsbuild" "auto" "prod"]]

                     ;; *** TEST ***

                     "run-test"         ["with-profile" "+dev-test" "do"
                                         ["clean"]
                                         ["cljsbuild" "once" "test"]
                                         ~(get-command-for-os "launch-test-url")]

                     "debug-test"       ["with-profile" "+dev-test" "do"
                                         ["run-test"]
                                         ["cljsbuild" "auto" "test"]]}
  )
