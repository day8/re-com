(ns re-demo.title
  (:require [re-com.core   :refer [h-box v-box box gap line title label checkbox hyperlink-href p]]
            [re-com.text   :refer [title-args-desc]]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn title-demo
  []
  (let [underline? (reagent/atom false)]
    (fn
      []
      (let [base-url  (str "https://github.com/Day8/re-com/tree/" (if ^boolean js/goog.DEBUG "develop" "master") "/")
            para-text [p "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quod si ita est, sequitur id ipsum, quod te velle video, omnes semper beatos esse sapientes. Tamen a proposito, inquam, aberramus. "]]
        [v-box
         :size "auto"
         :gap "10px"
         :children [[panel-title "[title ... ]"
                                  "src/re_com/text.cljs"
                                  "src/re_demo/title.cljs"]
                    [h-box
                     :gap "100px"
                     :children [[v-box
                                 :gap "10px"
                                 :width "450px"
                                 :children [[title2 "Notes"]
                                            [status-text "Stable"]
                                            [p "We use a four tier system of titles, the equivalent of h1 to h4."]
                                            [p
                                             "If you actually use [:h1] to [:h4] then "
                                             [hyperlink-href
                                              :label "Bootstrap styles"
                                              :href "http://getbootstrap.com/css/#type"
                                              :target "_blank"]
                                             " will apply."]
                                            [p
                                             "Re-com uses "
                                             [hyperlink-href
                                              :label  "Segoe UI"
                                              :href   "https://www.microsoft.com/typography/fonts/family.aspx?FID=331"
                                              :target "_blank"]
                                             " as its default font (available on Windows) with a fallback to the public domain "
                                             [hyperlink-href
                                              :label  "Roboto"
                                              :href   "http://www.google.com/fonts/specimen/Roboto"
                                              :target "_blank"]
                                             " font. See "
                                             [hyperlink-href
                                              :label  "re-com.css"
                                              :href   (str base-url "run/resources/public/assets/css/re-com.css")
                                              :target "_blank"]
                                             "."]
                                            [args-table title-args-desc]]]
                                [v-box
                                 :gap "10px"
                                 :children [[title2 "Demo"]
                                            [v-box
                                             :children [[checkbox
                                                         :label [box :align :start :child [:code ":underline?"]]
                                                         :model underline?
                                                         :on-change #(reset! underline? %)]
                                                        [gap :size "40px"]
                                                        para-text
                                                        [title :level :level1 :underline? @underline? :label ":level1 - Light 42px"]
                                                        para-text
                                                        [title :level :level2 :underline? @underline? :label ":level2 - Light 26px"]
                                                        para-text
                                                        [title :level :level3 :underline? @underline? :label ":level3 - Semibold 15px"]
                                                        para-text
                                                        [title :level :level4 :underline? @underline? :label ":level4 - Semibold 15px"]
                                                        para-text]]]]]]]]))))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [title-demo])
