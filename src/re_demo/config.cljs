(ns re-demo.config
  (:require-macros
    [re-com.core       :refer [coords]])
  (:require
   [re-com.core       :refer [h-box v-box gap line title p]]
   [re-demo.utils     :refer [panel-title title2]]))




(defn compiler-config
  []
  [v-box
   :src      (coords)
   :children [[title2 "Compiler"]
              [line]             
              [gap :size "10px"]
              [p "To avoid the overhead of parameter validation in production, 
                 include the following in your project.clj for your production builds:"]
              [:pre
               {:style {:width "450px"}}
               ":closure-defines {:goog.DEBUG false}"]
              [gap :size "20px"]
              [:code "re-com.config/root-url-for-compiler-output"]
              [gap :size "20px"]
              "XXXX other?"]])


(defn config-page
  []
  [v-box
   :src      (coords)
   :gap      "10px"
   :children [[panel-title
               "Config"
               "project.clj"
               "src/re_demo/config.cljs"]

              [h-box
               :src      (coords)
               :gap      "100px"
               :children [[compiler-config]]]]])


;; core holds a reference to panel, so we need one level of indirection to get hot realoading to work 
(defn panel
  []
  [config-page])
