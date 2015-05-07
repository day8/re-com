(require 'leiningen.core.eval)

(def fig-port   3449)
(def os         (leiningen.core.eval/get-os))
(def server-url (str "http://localhost:" fig-port "/index_dev.html"))
(def file-url   "run/resources/public/index_dev.html")
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

(defproject         re-com "0.5.4"
  :description      "Reusable UI components for Reagent"
  :url              "https://github.com/Day8/re-com.git"
  :license          {:name "MIT"}

  :dependencies     [[org.clojure/clojure         "1.6.0"]
                     [org.clojure/clojurescript   "0.0-3169"]
                     [reagent                     "0.5.0"]
                     [com.andrewmcveigh/cljs-time "0.3.2"]
                     [secretary                   "1.2.3"]]

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
                                               [figwheel                        "0.2.3-SNAPSHOT"]
                                               [spellhouse/clairvoyant          "0.0-48-gf5e59d3"]]
                                :plugins      [[lein-cljsbuild                  "1.0.4"]
                                               [lein-figwheel                   "0.2.3-SNAPSHOT"]
                                               [lein-shell                      "0.4.0"]
                                               [com.cemerick/clojurescript.test "0.3.3"]
                                               [lein-s3-static-deploy           "0.1.1-SNAPSHOT"]
                                               [lein-ancient                    "0.6.2"]]}
                     :dev-run  {:clean-targets ^{:protect false} ["run/resources/public/compiled_dev"]}
                     :prod-run {:clean-targets ^{:protect false} ["run/resources/public/compiled_prod"]}
                     :dev-test {:clean-targets ^{:protect false} ["run/test/compiled"]}}

  ;:jvm-opts         ^:replace ["-Xms2g" "-Xmx2g" "-server"]

  :source-paths    ["src" "dev"]
  :test-paths      ["test"]
  :resource-paths  ["run/resources"]
  ; :clean-targets   [:target-path]

  ;; Exclude the demo and compiled files from the output of either 'lein jar' or 'lein install'
  :jar-exclusions   [#"(?:^|\/)re_demo\/" #"(?:^|\/)compiled.*\/"]

  :cljsbuild {:builds [{:id           "demo"
                        :source-paths ["src" "dev"]
                        :compiler     {:output-to       "run/resources/public/compiled_dev/demo.js"
                                       :output-dir      "run/resources/public/compiled_dev/demo"
                                       :main            "figwheel-start.core"
                                       :asset-path      "compiled_dev/demo"
                                       :source-map      true
                                       :optimizations   :none
                                       :pretty-print    true}}
                       {:id           "prod"
                        :source-paths ["src"]
                        :compiler     {:output-to       "run/resources/public/compiled_prod/demo.js"
                                       ;:source-map      "run/resources/public/compiled_prod/demo.js.map"
                                       :output-dir      "run/resources/public/compiled_prod/demo"
                                       :closure-defines {:goog.DEBUG false}
                                       ;:source-map-path "js/out"                  ;; https://github.com/clojure/clojurescript/wiki/Source-maps#web-server-integration
                                       ;:main            "re-demo.core"            ;; Works but not required in this case becasue index_prod.html knows which function to call
                                       ;:asset-path      "compiled_prod/demo"
                                       ;:elide-asserts   true
                                       :optimizations   :advanced
                                       :pretty-print    false}}
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

  :aws {:access-key       ~(System/getenv "AWS_ACCESS_KEY_ID")
        :secret-key       ~(System/getenv "AWS_SECRET_ACCESS_KEY")
        :s3-static-deploy {:bucket     "re-demo"
                           :local-root "run/resources/public"}}

  :aliases          {;; *** DEMO ***

                     "run"        ["with-profile" "+dev-run" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "demo"]
                                   ~(get-command-for-os "launch-file-url")]

                     "debug"      ["with-profile" "+dev-run" "do"
                                   ["clean"]
                                   ~(get-command-for-os "launch-server-url")   ;; NOTE: run will initially fail, refresh browser once build complete
                                   ["figwheel" "demo"]]

                     ;; *** PROD ***

                     "run-prod"   ["with-profile" "+prod-run" "do"
                                   ["clean"]
                                   ["cljsbuild" "once" "prod"]
                                   ~(get-command-for-os "launch-prod-url")]

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
                                   ~(get-command-for-os "launch-test-url")]

                     "debug-test" ["with-profile" "+dev-test" "do"
                                   ["run-test"]
                                   ["cljsbuild" "auto" "test"]]}
  )
