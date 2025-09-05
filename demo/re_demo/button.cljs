(ns re-demo.button
  (:require-macros
   [re-com.core    :refer [handler-fn]])
  (:require
   [re-com.core    :refer [at h-box v-box box gap line button label throbber hyperlink-href p p-span]]
   [re-com.buttons :refer [button-parts-desc button-args-desc]]
   [re-demo.utils  :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
   [re-com.util    :refer [px]]
   [reagent.core   :as    reagent]))

(def state (reagent/atom
            {:outcome-index 0
             :see-throbber  false}))

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
       :src      (at)
       :size     "auto"
       :gap      "10px"
       :children [[panel-title "[button ... ]"
                   "src/re_com/buttons.cljs"
                   "src/re_demo/button.cljs"]

                  [h-box
                   :src      (at)
                   :gap      "100px"
                   :children [[v-box
                               :src      (at)
                               :gap      "10px"
                               :width    "450px"
                               :children [[title2 "Notes"]
                                          [status-text "Stable"]

                                          [p "A button component with optional tooltip."]
                                          [p "Styling to be provided via the " [:code ":class"] " attribute. Typically you'll be using Bootstrap CSS styles such as \"btn-info\"."]
                                          [p-span "See "
                                           [hyperlink-href
                                            :src    (at)
                                            :label  "Bootstrap Button Options"
                                            :href   "http://getbootstrap.com/css/#buttons-options"
                                            :target "_blank"]
                                           " for information on available classes."]
                                          [args-table button-args-desc]]]
                              [v-box
                               :src      (at)
                               :gap      "10px"
                               :children [[title2 "Demo"]
                                          [h-box
                                           :src      (at)
                                           :children [[button
                                                       :src              (at)
                                                       :label            "No Clicking!"
                                                       :tooltip          (when-not (= (:outcome-index @state) (dec (count click-outcomes))) "Seriously, NO CLICKING!")
                                                       :tooltip-position :below-center
                                                       :disabled?         (= (:outcome-index @state) (dec (count click-outcomes)))
                                                       :on-click          #(swap! state update-in [:outcome-index] inc)
                                                       :class             "btn-danger"]
                                                      [box
                                                       :src    (at)
                                                       :align  :center      ;; note: centered text wrt the button
                                                       :child  [label
                                                                :src   (at)
                                                                :label (nth click-outcomes (:outcome-index @state))
                                                                :style {:margin-left "15px"}]]]]
                                          [gap
                                           :src  (at)
                                           :size "20px"]
                                          [h-box
                                           :src      (at)
                                           :height   "50px"
                                           :gap      "50px"
                                           :align    :center
                                           :children [[button
                                                       :src               (at)
                                                       :label             (if (:see-throbber @state)  "Stop it!" "See Throbber")
                                                       :tooltip           "I'm a tooltip on the left"
                                                       :tooltip-position :left-center
                                                       :on-click          #(swap! state update-in [:see-throbber] not)]
                                                      (when (:see-throbber @state) [throbber :src (at)])]]
                                          [gap
                                           :src  (at)
                                           :size "20px"]

                                          [p "The two buttons above are styled using Bootstrap. For the " [:code ":class"] " parameter, we passed in the name of a standard Bootstrap class, like \"btn-default\"."]
                                          [p "But the button below was created by supplying inline styles via the " [:code ":style"] " and " [:code ":attr"] " parameters. To see the code, click the \"Page Source\" hyperlink at the top."]
                                          [button
                                           :src       (at)
                                           :label    [:span "Microsoft Modern Button " [:i.zmdi.zmdi-hc-fw-rc.zmdi-download]]
                                           :on-click #()
                                           :style    {:color            "white"
                                                      :background-color (if @hover? "#0072bb" "#4d90fe")
                                                      :font-size        "22px"
                                                      :font-weight      "300"
                                                      :border           "none"
                                                      :border-radius    "0px"
                                                      :padding          "20px 26px"}
                                           :attr     {:on-mouse-over (handler-fn (reset! hover? true))
                                                      :on-mouse-out  (handler-fn (reset! hover? false))}]]]]]
                  [parts-table "button" button-parts-desc]]])))

;; core holds onto references, so need one level of indirection to get figwheel updates
(defn panel
  []
  [button-demo])
