(ns re-demo.p
  (:require [re-com.core   :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))

(def arg-style {:style {:display     "inline-block"
                        :font-weight "bold"
                        :min-width   "140px"}})

(defn p-demo
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children [[panel-title "[p ... ]"
               "src/re_com/text.cljs"
               "demo/re_demo/p.cljs"]
              [h-box :src (at)
               :gap      "100px"
               :children [[v-box :src (at)
                           :gap      "10px"
                           :width    "450px"
                           :children [[title2 "Notes"]
                                      [status-text "Stable"]
                                      [p-span "Acts like an html [:p]." [:br] [:br]

                                       "Creates a paragraph of body text, expected to have a font-szie of 14px or 15px, which should have limited width (450px)." [:br] [:br]

                                       "Why limited text width? See " [hyperlink-href :src (at)
                                                                       :label  "this article"
                                                                       :href   "http://baymard.com/blog/line-length-readability"
                                                                       :target "blank"] "." [:br] [:br]

                                       "The actual font-size is inherited." [:br] [:br]

                                       "At 14px, 450px will yield between 69 and 73 chars." [:br]
                                       "At 15px, 450px will yield about 66 to 70 chars." [:br]
                                       "So we're at the upper end of the prefered 50 to 75 char range." [:br] [:br]

                                       "If the first child is a map, it is interpreted as a map of attributes." [:br] [:br]

                                       "Note: This section is contained within a single [p]."]

                                      [title2 "Parameters"]
                                      [p [:span.bold "[p optional-attr-map & components]"] [:br] [:br]

                                       [:span arg-style "optional-attr-map"] "e.g. " [:code "{:style {:color \"red\"}}"] [:br] [:br]

                                       [:span arg-style "components"] "one or more hiccup components."]]]

                          [v-box :src (at)
                           :gap      "10px"
                           :children [[title2 "Demo"]
                                      [v-box :src (at)
                                       :children [[p "This is the simplest form of a p with no attribute map and only a single string. It wraps at 450px."]
                                                  [p {:style {:color "red"}} "This is a p with an optional attribute map. In this case, we're setting the color to red."]
                                                  [p {:style {:width "300px" :min-width "300px"}} "If you really feel the need to change with default width,
                                                  you can do it with the attribute map. 300px in this case."]]]]]]]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [p-demo])
