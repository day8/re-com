(ns re-demo.v-table-demo
  (:require [re-com.core   :refer [h-box gap v-box box v-table p label]]
            [re-demo.utils  :refer [github-hyperlink]]
            [re-demo.utils :refer [title2]]
            [reagent.core  :as reagent]))


(defn some-demo
  []
  (fn []
    [:div ""]))

(defn demo
  []
  [v-box
   :gap      "10px"
   :children [[title2 "Demo"]
              [p [:b [:i "First,"]] " the " [:b "Notes"] " part of this page contains two diagrams built using " [:code "v-table"] ". Start by looking at the "  [github-hyperlink "source code" "src/re_demo/v_table_sections.cljs"] 
                  " for "     [github-hyperlink "both of them" "src/re_demo/v_table_renderers.cljs"] 
                 ". They provide a bare bones introduction."]
              [p [:b [:i "Next,"]] " at some point look at " [:code "simple-v-table"]  " to understand what is possible."]

              [some-demo]]])
