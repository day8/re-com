(ns re-demo.scroller
  (:require [re-com.core   :refer [h-box v-box gap scroller p]]
            [re-com.box    :refer [scroller-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[scroller ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/scroller.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "Wraps a child component in scroll bars."]
                                      [p "The scroller can be used at any level in a box hierarchy. For example, it could be a child of an h-box and it's child could be a v-box."]
                                      [args-table scroller-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [p "Here is some sample code..."]
                                      [:pre
                                       {:style {:width "40em"}}
"[scroller
 :v-scroll :auto
 :height   \"300px\"
 :child    [some-component]]"]
                                      [p "Notes:"]
                                      [:ul {:style {:width "450px"}}
                                       [:li "In this example, if the height of [some-component] is greater than 300px, a vertical scroll bar will be added."]]]]]]
              [gap :size "30px"]]])
