(ns re-demo.box
  (:require [re-com.core   :refer [h-box v-box box gap hyperlink-href]]
            [re-com.box    :refer [box-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title [:span "[box ... ]"
                            [github-hyperlink "Component Source" "src/re_com/box.cljs"]
                            [github-hyperlink "Page Source"      "src/re_demo/box.cljs"]]]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[component-title "Notes"]
                                      [status-text "Stable"]

                                      [paragraphs
                                      [:p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers like h-box."]
                                       [:p "But once we fixed that, there were other problem like they wouldn't justify correctly in a container."]
                                       [:p "So we created the " [:span.bold "[box ...]"] " component which allows you to wrap
               non-flex \"leaf\" elements for use in flex containers."]
                                      [:p "You probably won't need to use it directly."]]
                                      [args-table box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[component-title "Demo"]
                                      [:span.all-small-caps "This space intentionally left free of dark pixels. "]]]]]
              [gap :size "30px"]]])
