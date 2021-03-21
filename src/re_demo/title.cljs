(ns re-demo.title
  (:require [re-com.core   :refer [at h-box v-box box gap line title label checkbox hyperlink-href p p-span]]
            [re-com.text   :refer [title-parts-desc title-args-desc]]
            [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util   :refer [px]]
            [reagent.core  :as    reagent]))

(defn title-demo
  []
  (let [underline? (reagent/atom false)]
    (fn
      []
      (let [para-text [p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quod si ita est, sequitur id ipsum, quod te velle video, omnes semper beatos esse sapientes. Tamen a proposito, inquam, aberramus. "]]
        [v-box :src (at)
         :size "auto"
         :gap "10px"
         :children [[panel-title "[title ... ]"
                                  "src/re_com/text.cljs"
                                  "src/re_demo/title.cljs"]
                    [h-box :src (at)
                     :gap "100px"
                     :children [[v-box :src (at)
                                 :gap "10px"
                                 :width "450px"
                                 :children [[title2 "Notes"]
                                            [status-text "Stable"]
                                            [p "We use a four tier system of titles, the equivalent of h1 to h4."]
                                            [p-span
                                             "If you actually use [:h1] to [:h4] then "
                                             [hyperlink-href :src (at)
                                              :label "Bootstrap styles"
                                              :href "http://getbootstrap.com/css/#type"
                                              :target "_blank"]
                                             " will apply."]
                                            [p-span
                                             "Re-com uses "
                                             [hyperlink-href :src (at)
                                              :label  "Segoe UI"
                                              :href   "https://www.microsoft.com/typography/fonts/family.aspx?FID=331"
                                              :target "_blank"]
                                             " as its default font (available on Windows) with a fallback to the public domain "
                                             [hyperlink-href :src (at)
                                              :label  "Roboto"
                                              :href   "http://www.google.com/fonts/specimen/Roboto"
                                              :target "_blank"]
                                             " font. See "
                                             [hyperlink-href :src (at)
                                              :label  "re-com.css"
                                              :href   "https://github.com/day8/re-com/tree/master/run/resources/public/assets/css/re-com.css"
                                              :target "_blank"]
                                             "."]
                                            [args-table title-args-desc]]]
                                [v-box :src (at)
                                 :gap "10px"
                                 :children [[title2 "Demo"]
                                            [v-box :src (at)
                                             :children [[checkbox :src (at)
                                                         :label [box :src (at) :align :start :child [:code ":underline?"]]
                                                         :model underline?
                                                         :on-change #(reset! underline? %)]
                                                        [gap :src (at) :size "40px"]
                                                        para-text
                                                        [title :src (at) :level :level1 :underline? @underline? :label ":level1 - Light 42px"]
                                                        para-text
                                                        [title :src (at) :level :level2 :underline? @underline? :label ":level2 - Light 26px"]
                                                        para-text
                                                        [title :src (at) :level :level3 :underline? @underline? :label ":level3 - Semibold 15px"]
                                                        para-text
                                                        [title :src (at) :level :level4 :underline? @underline? :label ":level4 - Semibold 15px"]
                                                        para-text]]]]]]
                    [parts-table "title" title-parts-desc]]]))))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [title-demo])
