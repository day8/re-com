(ns re-demo.alert-box
  (:require-macros [re-com.core :refer [defn-meta add-meta]])
  (:require [re-com.box    :refer [h-box v-box box line gap]]
            [re-com.alert  :refer [alert-box alert-box-args-desc
                                   alert-list alert-list-args-desc]]
            [re-demo.utils :refer [panel-title component-title args-table]]
            [reagent.debug :refer-macros [dbg prn println log dev? warn warn-unless]]
            [reagent.core  :as    reagent]))

(defn alert-box-demo
  []
  (let [show-alert  (reagent/atom true)
        show-alert1 (reagent/atom true)
        show-alert2 (reagent/atom true)]
    (fn []
      [v-box
       :gap      "10px"
       :children [[panel-title "[alert-box ... ]"]

                  [h-box
                   :gap      "50px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [#_[component-title "Notes"]
                                          #_[label :label "A component which renders a single alert-box."]
                                          [args-table alert-box-args-desc]]]
                              [v-box
                               :width    "500px"
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          (if @show-alert
                                            [alert-box      ;(alert-box-meta alert-box)
                                             :id         1
                                             :alert-type "info"
                                             :heading    "This Is An Alert Heading"
                                             :body       [:p "This is an alert body. This alert has an :alert-type of 'info' which makes it blue, and it includes a :heading, a :body and a close button. Click the x to close it."]
                                             :closeable? true
                                             :on-close   #(reset! show-alert false)

                                             ;; TODO: For testing only - remove!
                                             ;:style      {:width "900px" :hieght "250px"}
                                             ;:attr       {:alt "alternate text" :style {} :onwheel #()}
                                             :attr       {:data-ns   (:ns   (meta #'re-demo.alert-box/alert-box-demo))
                                                          :data-name (:name (meta #'re-demo.alert-box/alert-box-demo))
                                                          :data-file (:file (meta #'re-demo.alert-box/alert-box-demo))
                                                          :data-line (:line (meta #'re-demo.alert-box/alert-box-demo))}
                                             ]
                                            [:p {:style {:text-align "center" :margin "30px"}} "[You closed me]"])
                                          [gap :size "50px"]
                                          [:p "Further Variations ..."]
                                          (when @show-alert1
                                            [:div
                                             [alert-box
                                              :alert-type "info"
                                              :heading    "Alert with :heading but no :body"
                                              :closeable? true
                                              :on-close   #(reset! show-alert1 false)]])
                                          (when @show-alert2
                                            [:div
                                             [alert-box
                                              :alert-type "warning"
                                              :body       "Alert with :body but no :heading (:padding set to 6px)."
                                              :padding    "6px"
                                              :closeable? true
                                              :on-close   #(reset! show-alert2 false)]])
                                          [alert-box
                                           :alert-type "danger"
                                           :heading    ":alert-type is \"danger\""
                                           :body       [:span "This is the :body of an danger-styled alert with :closeable? omitted (defaults to false). "
                                                        [:a {:href "http://google.com" :target "_blank"} "Link to Google"] "."]]]]]]]])))



(defn panel   ;; Introduce a level of naming indirection so that figwheel updates work
  []
  [alert-box-demo])

;; TODO: For testing only - remove!
(println "METATDATA for alert-box-demo:" (meta #'alert-box-demo))
(println "goog.DEBUG-1:" ^boolean (.-DEBUG js/goog))
(println "goog.DEBUG-2:" ^boolean js/goog.DEBUG)
(when js/goog.DEBUG (println "It's TRUE"))
(when ^boolean js/goog.DEBUG (println "It's TRUE"))

(println "ADD-META-1: " (meta (add-meta {:aa "hello"})))

(def aa (add-meta {:bb "goodbye"}))

(println "ADD-META-2: file = '" (:file (meta aa)) "', line = " (:line (meta aa)))

;(dbg alert-box-demo)
(dbg 'alert-box-demo)
(dbg #'alert-box-demo)

;(set! (.-DEBUG js/goog) false)
;(println "goog.DEBUG-2:" js/goog.DEBUG)
