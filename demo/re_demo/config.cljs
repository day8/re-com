(ns re-demo.config
  (:require-macros
   [re-com.core       :refer []])
  (:require
   [re-com.core       :refer [at h-box v-box gap line title p hyperlink-href]]
   [re-demo.utils     :refer [panel-title title2 title3]]))

(defn compiler-config
  []
  [v-box
   :src      (at)
   :children [[title2 "Compiler"]
              [line :src (at)]
              [gap :src (at) :size "20px"]
              [title3 "Production Builds"]
              [gap :src (at) :size "20px"]
              [p "The overhead of parameter validation is elided in production builds. This is based on "
               [:code "js/goog.DEBUG"] " being set to " [:code "false"] "."]
              [p [:code "js/goog.DEBUG"] " is automatically set by " [:code "shadow-cljs"] " to " [:code "false"] " for "
               [:code "release"] " builds."]
              [p "Other build systems may require you to manually add a " [:code ":closure-defines"] " compiler option:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {goog.DEBUG false}"]
              [gap :src (at) :size "20px"]
              [title3 "Source Code Links"]
              [gap :src (at) :size "20px"]
              [p "When re-com produces validation errors or component stacks (such as via the " [:code "[stack-spy ...]"]
               " component) it tries to provide links to source code. For these links to be displayed it requires that you provide the root URL to the ClojureScript compiler output with source maps:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {re-com.config/root-url-for-compiler-output \"http://localhost:3449/compiled_dev/demo/cljs-runtime/\"}"]]])

(defn tools
  []
  [v-box
   :src      (at)
   :children [[title2 "Tooling"]
              [line :src (at)]
              [gap :src (at) :size "20px"]
              [p "It is essential you have "
               [hyperlink-href :src (at)
                :href   "https://github.com/binaryage/cljs-devtools/blob/master/docs/installation.md"
                :label  "CLJS DevTools"
                :target "_blank"]
               " integrated with your project to enable correct formatting and navigation in re-com logging such as 'Parameters' in component stacks."]]])

(defn config-page
  []
  [v-box
   :src      (at)
   :gap      "10px"
   :children [[panel-title
               "Config"
               "project.clj"
               "src/re_demo/config.cljs"]

              [v-box
               :src      (at)
               :gap      "100px"
               :children [[compiler-config]
                          [tools]]]]])

;; core holds a reference to panel, so we need one level of indirection to get hot realoading to work 
(defn panel
  []
  [config-page])
