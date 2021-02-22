(ns re-demo.config
  (:require-macros
    [re-com.core       :refer [coords]])
  (:require
   [re-com.core       :refer [h-box v-box gap line title p hyperlink-href]]
   [re-demo.utils     :refer [panel-title title2 title3]]))




(defn compiler-config
  []
  [v-box
   :src      (coords)
   :children [[title2 "Compiler"]
              [line]
              [gap :size "20px"]
              [title3 "Production Builds"]
              [gap :size "20px"]
              [p "The overhead of parameter validation is elided in production builds. This is based on "
               [:code "js/goog.DEBUG"] " being set to " [:code "false"] "."]
              [p [:code "js/goog.DEBUG"] " is automatically set by " [:code "shadow-cljs"] " to " [:code "false"] " for "
               [:code "release"] " builds."]
              [p "Other build systems may require you to manually add a " [:code ":closure-defines"] " compiler option:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {goog.DEBUG false}"]
              [gap :size "20px"]
              [title3 "Source Code Links"]
              [gap :size "20px"]
              [p "When re-com produces validation errors or component stacks (such as via the " [:code "[stack-spy ...]"]
               " component) it tries to provide links to source code. For these links to be displayed it requires that you provide the root URL to the ClojureScript compiler output with source maps:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {re-com.config/root-url-for-compiler-output \"http://localhost:3449/compiled_dev/demo/cljs-runtime/\"}"]]])

(defn tools
  []
  [v-box
   :src      (coords)
   :children [[title2 "Tooling"]
              [line]
              [gap :size "20px"]
              [p "It is recommended you have "
               [hyperlink-href
                :href   "https://github.com/binaryage/cljs-devtools/blob/master/docs/installation.md"
                :label  "CLJS DevTools"
                :target "_blank"]
               " integrated with your project to enable correct formatting and navigation in re-com logging such as 'Parameters' in component stacks."]
              [title3 "Incorrect Formatting with Missing CLJS DevTools:"]
              [gap :size "10px"]
              [:img {:src "demo/logging-without-cljs-devtools.png"}]
              [title3 "Correct Formatting with CLJS DevTools:"]
              [gap :size "10px"]
              [:img {:src "demo/logging-with-cljs-devtools.png"}]]])

(defn config-page
  []
  [v-box
   :src      (coords)
   :gap      "10px"
   :children [[panel-title
               "Config"
               "project.clj"
               "src/re_demo/config.cljs"]

              [v-box
               :src      (coords)
               :gap      "100px"
               :children [[compiler-config]
                          [tools]]]]])


;; core holds a reference to panel, so we need one level of indirection to get hot realoading to work 
(defn panel
  []
  [config-page])
