(ns re-demo.info-button
  (:require [re-com.core      :refer [label input-text]]
            [re-com.buttons   :refer [info-button info-button-args-desc hyperlink-href]]
            [re-com.box       :refer [h-box v-box box gap line]]
            [re-demo.utils    :refer [panel-title component-title args-table]]))


(defn info-button-demo
  []
  (let [info [v-box
              :gap      "7px"
              :children [[:span.info-heading "Info Popup Heading"]
                         [:span "You can use the " [:span.info-bold "info-bold"] " class to make text bold."]
                         [:span "Use the " [:span.info-bold "code"] " element to display source code:"]
                         [:code
                          "(defn square [n] (* n n))" [:br]
                          "=> #'user/square" [:br]
                          "(square 45)" [:br]
                          "=> 2025" [:br]]
                         [:span.info-subheading "Sub heading"]
                         [:span
                          "Note: Styles copied from "
                          [hyperlink-href
                           :label "ClojureScript Cheatsheet"
                           :href "http://cljs.info/cheatsheet"
                           :target "_blank"]
                          "."]]]]
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "[info-button ... ]"]

                  [h-box
                   :gap "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [:span "A tiny information button, which is light grey and unobrusive. When pressed, displays a popup contining helpful information. "]
                                          [:span "Designed to be used with input fields, to explain the purpose of the field."]
                                          [:span "The popup has a dark theme, and uses white text. CSS classes for the text are
                                           available as follows:to format the text in the popover"]
                                          [:ul
                                           [:li [:span.semibold "info-heading"] " - heading/title style"]
                                           [:li [:span.semibold"info-subheading"] " - subheading style"]
                                           [:li [:span.semibold "info-bold"] " - bold style"]]
                                          [args-table info-button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [v-box
                                           :children [[:span "Click on the buttons below to see a popup info panel."]
                                                      [:span "Click away to cause a pop-down."]
                                                      [gap :size "15px"]
                                                      [h-box
                                                       :gap      "4px"
                                                       :children [[:span.small-caps "client"]
                                                                  [info-button
                                                                   :info info]]]
                                                      [input-text
                                                       :model       ""
                                                       :placeholder "Example input #1"
                                                       :on-change   #()]
                                                      [gap :size "15px"]
                                                      [h-box
                                                       :gap      "4px"
                                                       :children [[:span.small-caps "product"]
                                                                  [info-button
                                                                   :position :right-center
                                                                   :width    "370px"
                                                                   :info     info]]]
                                                      [input-text
                                                       :model       ""
                                                       :placeholder "Example input #2"
                                                       :on-change   #()]]]]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [info-button-demo])
