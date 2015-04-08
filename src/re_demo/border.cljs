(ns re-demo.border
  (:require [re-com.core   :refer [h-box v-box box gap border p]]
            [re-com.box    :refer [border-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title  "[border ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/border.cljs"]

              [h-box
               :gap      "100px"
              :children [[v-box
                          :gap      "10px"
                          :width    "450px"
                          :children [[title2 "Notes"]
                                     [status-text "Stable"]
                                     [p "Wraps a child component in a border."]
                                     [p "The border can be used at any level in a box heirarchy. For example, it could be a child of an h-box and it's child could be a v-box."]
                                     [args-table border-args-desc]]]
                         [v-box
                          :gap      "10px"
                          ; :size     "0 0 auto"
                          ;:align    :start
                          :children [[title2 "Demo"]
                                     [p "Here is some sample code..."]
                                     [:pre
                                      {:style {:width "40em"}}
"[border
 :border \"1px dashed red\"
 :child  [box :height \"100px\" :child \"Hello\"]]"]

                                     [p "Here is the result..."]
                                     [border
                                      :border "1px dashed red"
                                      :child  [box :height "100px" :child "Hello"]]]]]]
              [gap :size "30px"]]])
