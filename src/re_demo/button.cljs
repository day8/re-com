(ns re-demo.button
  (:require [re-com.core    :refer [h-box v-box box gap line button label spinner hyperlink-href] :refer-macros [handler-fn]]
            [re-com.buttons :refer [button-args-desc]]
            [re-demo.utils  :refer [panel-title component-title args-table github-hyperlink status-text paragraphs]]
            [reagent.core   :as    reagent]))


(def state (reagent/atom
             {:outcome-index 0
              :see-spinner  false}))

(def click-outcomes
  [""   ;; start blank
   "Nuclear warhead launched."
   "Oops. Priceless Ming Vase smashed!!"
   "Diamonds accidentally flushed."
   "Toy disabled"])


(defn button-demo
  []
  (let [hover? (reagent/atom false)]
    (fn
      []
      [v-box
       :size     "auto"
       :gap      "10px"
       :children [[panel-title [:span "[button ... ]"
                                [github-hyperlink "Component Source" "src/re_com/buttons.cljs"]
                                [github-hyperlink "Page Source"      "src/re_demo/button.cljs"]]]

                  [h-box
                   :gap      "100px"
                   :children [[v-box
                               :gap      "10px"
                               :width    "450px"
                               :children [[component-title "Notes"]
                                          [status-text "Stable"]
                                          [paragraphs
                                           [:p "A button component with optional tooltip."]
                                           [:p "Styling to be provided via the " [:code ":class"] " attribute. Typically you'll be using Bootstrap CSS styles such as \"btn-info\"."]
                                           [:p "See "
                                            [hyperlink-href
                                             :label "Bootstrap Button Options"
                                             :href "http://getbootstrap.com/css/#buttons-options"
                                             :target "_blank"]
                                            " for information on available classes."]]
                                          [args-table button-args-desc]]]
                              [v-box
                               :gap      "10px"
                               :children [[component-title "Demo"]
                                          [h-box
                                           :children [[button
                                                       :label            "No Clicking!"
                                                       :tooltip          (when-not (= (:outcome-index @state) (dec (count click-outcomes))) "Seriously, NO CLICKING!")
                                                       :tooltip-position :below-center
                                                       :disabled?         (= (:outcome-index @state) (dec (count click-outcomes)))
                                                       :on-click          #(swap! state update-in [:outcome-index] inc)
                                                       :class             "btn-danger"]
                                                      [box
                                                       :align :center      ;; note: centered text wrt the button
                                                       :child  [label
                                                                :label (nth click-outcomes (:outcome-index @state))
                                                                :style {:margin-left "15px"}]]]]
                                          [gap :size "20px"]
                                          [h-box
                                           :height   "50px"
                                           :gap      "50px"
                                           :align    :center
                                           :children [[button
                                                       :label             (if (:see-spinner @state)  "Stop it!" "See Spinner")
                                                       :tooltip           "I'm a tooltip on the left"
                                                       :tooltip-position :left-center
                                                       :on-click          #(swap! state update-in [:see-spinner] not)]
                                                      (when (:see-spinner @state)  [spinner])]]
                                          [gap :size "20px"]
                                          [paragraphs
                                           [:p "This button is completely restyled (using the " [:code ":style"] " and " [:code ":attr"] " arguments) to look like a Microsoft Modern UI button and demonstrates the use of hiccup for the label."]
                                           [button
                                            :label    [:span "Microsoft Modern Button " [:i {:class "md-file-download"}]] ;;[:i {:class "glyphicon glyphicon glyphicon-download"}]
                                            :on-click #()
                                            :style    {:color            "white"
                                                       :background-color (if @hover? "#0072bb" "#4d90fe")
                                                       :font-size        "22px"
                                                       :font-weight      "300"
                                                       :border           "none"
                                                       :border-radius    "0px"
                                                       :padding          "20px 26px"}
                                            :attr     {:on-mouse-over (handler-fn (reset! hover? true))
                                                       :on-mouse-out  (handler-fn (reset! hover? false))}]]]]]]]])))


;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [button-demo])
