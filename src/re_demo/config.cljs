(ns re-demo.config
  (:require-macros
   [re-com.core       :refer [src-coordinates]])
  (:require
   [re-com.core       :refer [h-box v-box gap line title p]]
   [re-demo.utils     :refer [panel-title title2]]))




(defn compiler-config
  []
  [v-box
   :src      (src-coordinates)
   :children [[title2 "Compiler Configuration"]
              [line]
              [p "To avoid the overhead of parameter validation in production, "
               "include the following in your project.clj for your production builds:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {:goog.DEBUG false}"]
              "XXXX others?"
              [:code "re-com.config/root-url-for-compiler-output"]]])


(defn config-page
  []
  [v-box
   :src      (src-coordinates)
   :gap      "10px"
   :children [[panel-title
               "Config"
               "project.clj"
               "src/re_demo/config.cljs"]

              [h-box
               :src      (src-coordinates)
               :gap      "100px"
               :children [[compiler-config]]]]])


;; core holds a reference to panel, so we need one level of indirection to get hot realoading to work 
(defn panel
  []
  [config-page])
