(ns re-demo.box
  (:require [re-com.core   :refer [h-box v-box box gap hyperlink-href p]]
            [re-com.box    :refer [box-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))


(defn panel
  []
  [v-box
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[box ... ]"
                            "src/re_com/box.cljs"
                            "src/re_demo/box.cljs"]

              [h-box
               :gap      "100px"
               :children [[v-box
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]

                                      [p "When we first put bootstrap buttons into re-com, they were streched.
               It turned out their block display didn't play well with flex containers like h-box."]
                                      [p "We fixed that, but then there were other problem like they wouldn't justify correctly in a container."]
                                      [p "So we created the " [:span.bold "[box ...]"] " component which allows you to wrap
               non-flex \"leaf\" elements for use in flex containers."]
                                      [p "You probably won't need to use it directly."]
                                      [args-table box-args-desc]]]
                          [v-box
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [:span.all-small-caps "This space intentionally left free of dark pixels. "]]]]]
              [gap :size "30px"]]])
