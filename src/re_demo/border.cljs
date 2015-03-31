(ns re-demo.border
  (:require [re-com.core   :refer [h-box v-box box gap border]]
            [re-com.box    :refer [border-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[border ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/border.cljs"]]]

              [h-box
               :gap      "100px"
              :children [[v-box
                          :gap      "10px"
                          :width    "450px"
                          :children [[component-title "Notes"]
                                     [status-text "Stable"]
                                     [paragraphs
                                      [:p "Wraps a child component in a border."]
                                      [:p "The border can be used at any level in a box heirarchy. For example, it could be a child of an h-box and it's child could be a v-box."]]
                                     [args-table border-args-desc]]]
                         [v-box
                          :gap      "10px"
                          ;:align    :start
                          :children [[component-title "Demo"]
                                     [paragraphs
                                      [:span "Here is some sample code..."]]
                                     [:pre
                                      {:style {:width "40em"}}
"[border
 :border \"1px dashed red\"
 :size   \"100px\"
 :child  [box :child \"Hello\"]]"]
                                     [paragraphs
                                      [:p "Here is the result..."]]
                                     [border
                                      :border "1px dashed red"
                                      :size   "100px"
                                      :child  [box :child "Hello"]]]]]]
              [gap :size "30px"]]])
