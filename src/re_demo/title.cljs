(ns re-demo.title
  (:require [re-com.core   :refer [h-box v-box box gap line title label checkbox hyperlink-href]]
            [re-com.text   :refer [title-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table github-hyperlink status-text]]
            [reagent.core  :as    reagent]))


(defn title-demo
  []
  (let [underline? (reagent/atom false)]
    (fn
      []
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
                                          [status-text "Alpha"]
                                          [:p "Because re-com uses a combination of Bootstrap and Material Design, we support typography from both. It's your choice. Or design your own from scratch."]
                                          [:p
                                           "Here is a link to the Material Design Typography page: "
                                           [hyperlink-href
                                            :label  "click here"
                                            :href   "https://www.google.com/design/spec/style/typography.html"
                                            :target "_blank"]
                                           "."]
                                          [:p
                                           "Here is a link to the Boostrap Typography page: "
                                           [hyperlink-href
                                            :label  "click here"
                                            :href   "http://getbootstrap.com/css/#type"
                                            :target "_blank"]
                                           "."]
                                          [:p "Note that the Roboto font is used throughout."]
                                          [args-table title-args-desc]]]
                              [v-box
                               :gap "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :children [[checkbox
                                                       :label     ":underline?"
                                                       :model     underline?
                                                       :on-change #(reset! underline? %)]

                                                      [gap :size "20px"]
                                                      [title :label "This Is The Default Title Style (:title)" :underline? @underline?]

                                                      [gap :size "40px"]
                                                      [title :label "Material Design type styles (:md-style argument)" :underline? true]
                                                      ;[title :md-style :display4    :underline? @underline? :label ":display4 - Light 112px"]
                                                      [title :md-style :display3    :underline? @underline? :label ":display3 - Regular 56px"]
                                                      [title :md-style :display2    :underline? @underline? :label ":display2 - Regular 45px"]
                                                      [title :md-style :display1    :underline? @underline? :label ":display1 - Regular 34px"]
                                                      [title :md-style :headline    :underline? @underline? :label ":headline - Regular 24px"]
                                                      [title :md-style :title       :underline? @underline? :label ":title    - Medium 20px"]
                                                      [title :md-style :subhead     :underline? @underline? :label ":subHead  - Regular 15px"]
                                                      [title :md-style :body-text2  :underline? @underline? :label ":body2    - Medium 13px"]
                                                      [title :md-style :body-text1  :underline? @underline? :label ":body1    - Regular 13px"]
                                                      [title :md-style :caption     :underline? @underline? :label ":caption  - Regular 12px"]
                                                      [title :md-style :button-text :underline? @underline? :label ":button2  - Medium 14px (All Caps)"]

                                                      [gap :size "40px"]
                                                      [title :label "Bootstrap type styles (:bs-style argument)" :underline? true]
                                                      [title :bs-style :h1 :underline? @underline? :label ":h1 - Medium 36px"]
                                                      [title :bs-style :h2 :underline? @underline? :label ":h2 - Medium 30px"]
                                                      [title :bs-style :h3 :underline? @underline? :label ":h3 - Medium 24px"]
                                                      [title :bs-style :h4 :underline? @underline? :label ":h4 - Medium 18px"]
                                                      [title :bs-style :h5 :underline? @underline? :label ":h5 - Medium 14px"]
                                                      [title :bs-style :h6 :underline? @underline? :label ":h6 - Medium 12px"]

                                                      [gap :size "40px"]
                                                      [title :label "Playing around with Bootstrap [REMOVE!]" :underline? true]
                                                      [:h2 "h1. Bootstrap heading " [:small "Secondary text"]]
                                                      [:p "Press " [:kbd "Ctrl+Alt+Delete"] " to kill all Kilingons!"]
                                                      [:p "You can " [:mark "mark some text"] " to make it stand out a bit"]
                                                      [:p.text-left "This is left aligned text"]
                                                      [:p.text-center "This is center aligned text"]
                                                      [:p.text-right "This is right aligned text"]
                                                      [:p "Here is a word marked up as an " [:abbr {:title "This is the popup message explaining what abbreviation is all about"} "abbreviation"] ". Hover over it."]

                                                      ;; [gap :size "20px"]
                                                      ;; [:span {:style {:font-weight "300"}} "Font family: Roboto - Weight: 300"]
                                                      ;; [:span {:style {:font-weight "400"}} "Font family: Roboto - Weight: 400"]
                                                      ;; [:span {:style {:font-weight "500"}} "Font family: Roboto - Weight: 500"]
                                                      ;; [:span {:style {:font-weight "700"}} "Font family: Roboto - Weight: 700"]
                                                      ;; [:span {:style {:font-family "Roboto Condensed" :font-weight "300"}} "Font family: Roboto Condensed - Weight: 300"]
                                                      ;; [:span {:style {:font-family "Roboto Condensed" :font-weight "400"}} "Font family: Roboto Condensed - Weight: 400"]
                                                      ;;
                                                      ;; [gap :size "20px"]
                                                      ;; [:span {:style {:font-weight "300" :font-size "36px"}} "Font family: Roboto - Weight: 300"]
                                                      ;; [:span {:style {:font-weight "400" :font-size "36px"}} "Font family: Roboto - Weight: 400"]
                                                      ;; [:span {:style {:font-weight "500" :font-size "36px"}} "Font family: Roboto - Weight: 500"]
                                                      ;; [:span {:style {:font-weight "700" :font-size "36px"}} "Font family: Roboto - Weight: 700"]
                                                      ;; [:span {:style {:font-family "Roboto Condensed" :font-weight "300" :font-size "36px"}} "Font family: Roboto Condensed - Weight: 300"]
                                                      ;; [:span {:style {:font-family "Roboto Condensed" :font-weight "400" :font-size "36px"}} "Font family: Roboto Condensed - Weight: 400"]

                                                      [gap :size "100px"]
                                                      ]]]]]]]])))


(defn panel    ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [title-demo])
