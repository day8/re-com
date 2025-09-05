(ns re-demo.info-button
  (:require [re-com.core    :refer [at h-box v-box box gap line info-button label input-text hyperlink-href p]]
            [re-com.buttons :refer [info-button-parts-desc info-button-args-desc]]
            [re-demo.utils  :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
            [re-com.util    :refer [px]]))

(defn info-button-demo
  []
  (let [info [v-box :src (at)
              :children [[:p.info-heading "Info Popup Heading"]
                         [:p "You can use the " [:span.info-bold "info-bold"] " class to make text bold."]
                         [:p "Use the " [:span.info-bold "code"] " element to display source code:"]
                         [:code
                          "(defn square [n] (* n n))" [:br]
                          "=> #'user/square" [:br]
                          "(square 45)" [:br]
                          "=> 2025" [:br]]
                         [:p.info-subheading "Sub heading"]
                         [:p "Note: Styles copied from"]
                         [hyperlink-href :src (at)
                          :label  "ClojureScript Cheatsheet"
                          :href   "http://cljs.info/cheatsheet"
                          :target "_blank"]]]]
    (fn []
      [v-box :src (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[info-button ... ]"
                   "src/re_com/buttons.cljs"
                   "src/re_demo/info_button.cljs"]

                  [h-box :src (at)
                   :gap "100px"
                   :children [[v-box :src (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]
                                          [p "A tiny information button, which is light grey and unobrusive. When clicked, displays a popup containing helpful information. "]
                                          [p "Designed to be used with input fields, to explain the purpose of the field."]
                                          [p "The popup has a dark theme, and uses white text. CSS classes for the text are available as follows:to format the text in the popover"]
                                          [:ul
                                           [:li [:span.semibold "info-heading"] " - heading/title style"]
                                           [:li [:span.semibold "info-subheading"] " - subheading style"]
                                           [:li [:span.semibold "info-bold"] " - bold style"]]
                                          [args-table info-button-args-desc]]]
                              [v-box :src (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [p "Notice the small round information icon above each input box. On hover, the icon become blue. On click, a popoover appears with artbitrary explanation."]
                                          [gap :src (at) :size "5px"]
                                          [v-box :src (at)
                                           :children [[h-box :src (at)
                                                       :gap      "4px"
                                                       :children [[:span.field-label "client"]
                                                                  [info-button :src (at)
                                                                   :info info]]]
                                                      [input-text :src (at)
                                                       :model       ""
                                                       :placeholder "Example input #1"
                                                       :on-change   #()]
                                                      [gap :src (at) :size "30px"]
                                                      [h-box :src (at)
                                                       :gap      "4px"
                                                       :children [[:span.field-label "product"]
                                                                  [info-button :src (at)
                                                                   :position :right-center
                                                                   :width    "370px"
                                                                   :info     info]]]
                                                      [input-text :src (at)
                                                       :model       ""
                                                       :placeholder "Example input #2"
                                                       :on-change   #()]
                                                      [gap :src (at) :size "30px"]
                                                      [h-box :src (at)
                                                       :gap      "4px"
                                                       :children [[:span.field-label "disabled"]
                                                                  [info-button :src (at)
                                                                   :position :right-center
                                                                   :width    "370px"
                                                                   :disabled? true
                                                                   :info     info]]]
                                                      [input-text :src (at)
                                                       :model       ""
                                                       :placeholder "Example input #3"
                                                       :disabled? true
                                                       :on-change   #()]]]]]]]
                  [parts-table "info-button" info-button-parts-desc]]])))

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [info-button-demo])
