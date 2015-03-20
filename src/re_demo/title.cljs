(ns re-demo.title
  (:require [re-com.core   :refer [h-box v-box box gap line title label checkbox hyperlink-href]]
            [re-com.text   :refer [title-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core  :as    reagent]))


(defn title-demo
  []
  (let [underline? (reagent/atom false)]
    (fn
      []
      (let [base-url (str "https://github.com/Day8/re-com/tree/" (if ^boolean js/goog.DEBUG "develop" "master") "/")]
        [v-box
         :size "auto"
         :gap "10px"
         :children [[panel-title [:span "[title ... ]"
                                  [github-hyperlink "Component Source" "src/re_com/text.cljs"]
                                  [github-hyperlink "Page Source" "src/re_demo/title.cljs"]]]
                    [h-box
                     :gap "50px"
                     :children [[v-box
                                 :gap "10px"
                                 :width "450px"
                                 :children [[component-title "Notes"]
                                            [status-text "Stable"]
                                            [paragraphs
                                             [:p "We use a four tier system of titles, the equivalent of h1 to h4."]
                                             [:p
                                              "If you actually use [:h1] to [:h4] then "
                                              [hyperlink-href
                                               :label "Bootstrap styles"
                                               :href "http://getbootstrap.com/css/#type"
                                               :target "_blank"]
                                              " will apply."]
                                             [:p
                                              "Re-com uses the "
                                              [hyperlink-href
                                               :label "Roboto"
                                               :href "http://www.google.com/fonts/specimen/Roboto"
                                               :target "_blank"]
                                              " as its default font. See "
                                              [hyperlink-href
                                               :label "re-com.css"
                                               :href (str base-url "run/resources/public/resources/css/re-com.css")
                                               :target "_blank"]
                                              "."]]
                                            [args-table title-args-desc]]]
                                [v-box
                                 :gap "10px"
                                 :children [[component-title "Demo"]
                                            [v-box
                                             :gap "10px"
                                             :children [[checkbox
                                                         :label [box :align :start :child [:code ":underline?"]]
                                                         :model underline?
                                                         :on-change #(reset! underline? %)]
                                                        [gap :size "40px"]
                                                        [title :level :level1 :underline? @underline? :label ":level1 - Light 42px"]
                                                        [title :level :level2 :underline? @underline? :label ":level2 - Light 26px"]
                                                        [title :level :level3 :underline? @underline? :label ":level3 - Semibold 15px"]
                                                        [title :level :level4 :underline? @underline? :label ":level4 - Semibold 15px"]]]]]]]]]))))


;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [title-demo])
