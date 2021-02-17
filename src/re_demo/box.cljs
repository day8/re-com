(ns re-demo.box
  (:require-macros
    [re-com.debug  :refer [src-coordinates]])
  (:require
    [re-com.core   :refer [h-box v-box box gap hyperlink-href p]]
    [re-com.box    :refer [box-args-desc]]
    [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :src      (src-coordinates)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[box ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/box.cljs"]

              [h-box
               :src      (src-coordinates)
               :gap      "100px"
               :children [[v-box
                           :src      (src-coordinates)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p "If you need to introduce leaf nodes into a layout and they are not
                                      already correctly styled for use as flexbox items, then wrap them in (make them the child of) a " [:span.bold "[box ..]"] "."]
                                      [p "Leaf nodes which need this treatment are typically block level elements from outside of re-com."]
                                      [p "Back in the beginning, when we first put bootstrap styled buttons into re-com, they were streched. Alarmingly.
               It turned out their block display didn't play well with flex containers like h-box."]
                                      [p "We fixed that, but then there were other problems like they
                                      wouldn't justify correctly in a container. All these issues arose because they
                                      were never designed (styled) to be the children of flexbox containers. That's why we created [box]"]

                                      [args-table box-args-desc]]]
                          [v-box
                           :src      (src-coordinates)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "This space intentionally free of dark pixels."]]]]]
              [gap
               :src  (src-coordinates)
               :size "30px"]]])
