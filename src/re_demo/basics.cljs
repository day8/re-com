(ns re-demo.basics
  (:require [re-com.core     :refer [label spinner progress-bar title
                                     button button-args-desc
                                     md-circle-icon-button md-circle-icon-button-args-desc
                                     row-button row-button-args-desc
                                     checkbox checkbox-args-desc
                                     radio-button radio-button-args-desc
                                     input-text input-text-args-desc
                                     hyperlink hyperlink-args-desc
                                     hyperlink-href hyperlink-href-args-desc
                                     slider slider-args-desc
                                     inline-tooltip inline-tooltip-args-desc]]
            [re-com.box      :refer [h-box v-box box gap line]]
            [re-com.tabs     :refer [horizontal-bar-tabs vertical-bar-tabs]]
            [re-demo.utils   :refer [panel-title component-title args-table]]
            ;[re-com.dropdown :refer [single-dropdown]]      ;; Experimental
            ;[re-com.time     :refer [input-time]]           ;; Experimental
            [reagent.core    :as    reagent]))


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
  [h-box
   :gap      "50px"
   :children [[v-box
               :gap      "10px"
               :style    {:font-size "small"}
               :children [[component-title "[button ... ]"]
                          [args-table button-args-desc]]]
              [v-box
               :children [[component-title "Demo"]
                          [v-box
                           :children [[h-box
                                       :children [[button
                                                   :label    "No Clicking!"
                                                   :disabled? (= (:outcome-index @state) (dec (count click-outcomes)))
                                                   :on-click #(swap! state update-in [:outcome-index] inc)
                                                   :class    "btn-danger"]
                                                  [box
                                                   :align :center      ;; note: centered text wrt the button
                                                   :child  [label
                                                            :label (nth click-outcomes (:outcome-index @state))
                                                            :style {:margin-left "15px"}]]]]

                                      [gap :size "20px"]
                                      [h-box
                                       :gap "50px"
                                       :children [[button
                                                   :label    (if (:see-spinner @state)  "Stop it!" "See Spinner")
                                                   ;:disabled? true
                                                   :on-click #(swap! state update-in [:see-spinner] not)]
                                                  (when (:see-spinner @state)  [spinner])]]]]]]]])


(def icons
  [{:id "md-add"            :label [:i {:class "md-add"}]}
   {:id "md-add-box"        :label [:i {:class "md-add-box"}]}
   {:id "md-add-circle"     :label [:i {:class "md-add-circle"}]}
   {:id "md-delete"         :label [:i {:class "md-delete"}]}
   {:id "md-undo"           :label [:i {:class "md-undo"}]}
   {:id "md-settings"       :label [:i {:class "md-settings"}]}
   {:id "md-error"          :label [:i {:class "md-error"}]}
   {:id "md-warning"        :label [:i {:class "md-warning"}]}
   {:id "md-home"           :label [:i {:class "md-home"}]}
   {:id "md-person"         :label [:i {:class "md-person"}]}])

(defn md-circle-icon-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :children [[component-title "[md-circle-icon-button ... ]"]
                              [args-table md-circle-icon-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "40px"
                               :children [[h-box
                                           :align :center
                                           :gap "8px"
                                           :children [[label :label "Example icons:"]
                                                      [horizontal-bar-tabs
                                                       :model     selected-icon
                                                       :tabs      icons
                                                       :on-change #(reset! selected-icon %)]
                                                      [label :label @selected-icon]]]
                                          [label :label "Hover over the buttons below to see a tooltip."]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "States:"]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :emphasise?   true
                                                       :tooltip      "This button has :emphasise? set to true"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is the default button"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button has :disabled? set to true"
                                                       :disabled?    true
                                                       :on-click     #()]]]
                                          [h-box
                                           :gap      "20px"
                                           :align    :center
                                           :children [[label :label "Sizes:"]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :smaller button"
                                                       :size         :smaller
                                                       :on-click #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This button does not specify a :size"
                                                       :on-click     #()]
                                                      [md-circle-icon-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip      "This is a :larger button"
                                                       :size         :larger
                                                       :on-click #()]]]]]]]]])))


(defn row-button-demo
  []
  (let [selected-icon (reagent/atom (:id (first icons)))]
    (fn []
      [h-box
       :gap "50px"
       :children [[v-box
                   :gap "10px"
                   :style {:font-size "small"}
                   :children [[component-title "[row-button ... ]"]
                              [args-table row-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "40px"
                               :children [[h-box
                                           :align :center
                                           :gap "8px"
                                           :children [[label :label "Example icons:"]
                                                      [horizontal-bar-tabs
                                                       :model selected-icon
                                                       :tabs icons
                                                       :on-change #(reset! selected-icon %)]
                                                      [label :label @selected-icon]]]
                                          [label :label "Hover over the buttons below to see a tooltip."]
                                          [h-box
                                           :gap      "2px"
                                           :align    :center
                                           :children [[label :label "States:"]
                                                      [row-button
                                                       :md-icon-name @selected-icon
                                                       :state        :invisible
                                                       :tooltip      "This button is :invisible"
                                                       :on-click     #()]
                                                      [row-button
                                                       :md-icon-name @selected-icon
                                                       :state        :semi-visible
                                                       :tooltip      "This button is :semi-visible"
                                                       :on-click     #()]
                                                      [row-button
                                                       :md-icon-name @selected-icon
                                                       :state        :visible
                                                       :tooltip      "This button is :visible"
                                                       :on-click     #()]
                                                      [row-button
                                                       :md-icon-name @selected-icon
                                                       :tooltip "This button has :disabled? set to true"
                                                       :disabled? true
                                                       :on-click #()]]]
                                          ]]]]]])))


(defn right-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "0" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "20,6 20,14 25,10" :style {:stroke "#888" :fill "#888"}}]])


(defn left-arrow
  []
  [:svg
   {:height 20  :width 25  :style {:display "flex" :align-self "center"} }
   [:line {:x1 "5" :y1 "10" :x2 "20" :y2 "10"
           :style {:stroke "#888"}}]
   [:polygon {:points "5,6 5,14 0,10" :style {:stroke "#888" :fill "#888"}}]])


(defn checkboxes-demo
  []
  (let [; always-false (reagent/atom false)
        disabled?    (reagent/atom false)
        ticked?      (reagent/atom false)
        something1?  (reagent/atom false)
        something2?  (reagent/atom true)
        all-for-one? (reagent/atom true)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[checkbox ... ]"]
                              [args-table checkbox-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :gap "15px"
                               :children [#_[checkbox
                   :label "always ticked (state stays true when you click)"
                   :model (= 1 1)]    ;; true means always ticked

                                          #_[checkbox
                                           :label "untickable (state stays false when you click)"
                                           :model always-false]

                                           [h-box
                                            :gap "10px"
                                            :children [[checkbox
                                                        :label     "tick me  "
                                                        :model     ticked?
                                                        :on-change #(reset! ticked? %)]
                                                       (when @ticked? [left-arrow])
                                                       (when @ticked? [label :label " is ticked"])]]

                                           [h-box
                                            :gap "1px"
                                            :children [[checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)]
                                                       [checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)]
                                                       [checkbox  :model all-for-one?   :on-change #(reset! all-for-one? %)  :label  "all for one, and one for all.  "]]]

                                           [h-box
                                            :gap "15px"
                                            :children [[checkbox
                                                        :label     "tick this one, to \"disable\""
                                                        :model     disabled?
                                                        :on-change #(reset! disabled? %)]
                                                       [right-arrow]
                                                       [checkbox
                                                        :label       (if @disabled? "now disabled" "enabled")
                                                        :model       something1?
                                                        :disabled?   disabled?
                                                        :label-style (if @disabled?  {:color "#888"})
                                                        :on-change   #(reset! something1? %)]]]

                                           [h-box
                                            :gap "1px"
                                            :children [[checkbox
                                                        :model     something2?
                                                        :on-change #(reset! something2? %)]
                                                       [gap :width "50px"]
                                                       [left-arrow]
                                                       [gap :width "5px"]
                                                       [label
                                                        :label "no label on this one"]]]]]]]]])))


(defn radios-demo
  []
  (let [colour (reagent/atom "green")]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[radio-button ... ]"]
                              [args-table radio-button-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [v-box
                               :children [(doall (for [c ["red" "green" "blue"]]    ;; Notice the ugly "doall"
                                                   ^{:key c}                        ;; key should be unique within this compenent
                                                   [radio-button
                                                    :label       c
                                                    :value       c
                                                    :model       colour
                                                    :label-style (if (= c @colour) {:background-color c  :color "white"})
                                                    :on-change   #(reset! colour c)]))]]]]]]

      )))


(defn inputs-demo
  []
  (let [text-val        (reagent/atom nil)
        regex           (reagent/atom nil)
        status          (reagent/atom nil)
        status-icon?    (reagent/atom false)
        status-tooltip  (reagent/atom "")
        disabled?       (reagent/atom false)
        change-on-blur? (reagent/atom true)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[input-text ... ]"]
                              [args-table input-text-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "40px"
                               :children [[input-text
                                           :model            text-val
                                           :status           @status
                                           :status-icon?     @status-icon?
                                           :status-tooltip   @status-tooltip
                                           :width            "200px"
                                           :placeholder      "placeholder message"
                                           :on-change        #(reset! text-val %)
                                           :validation-regex @regex
                                           :change-on-blur?  change-on-blur?
                                           :disabled?        disabled?]
                                          [v-box
                                           :gap      "15px"
                                           :children [[label
                                                       :label (str "external :model is currently: '" (if @text-val @text-val "nil") "'")
                                                       :style {:margin-top "8px"}]
                                                      [label :label "parameters:"]
                                                      [v-box
                                                       :children [[label :label ":change-on-blur?"]
                                                                  [radio-button
                                                                   :label     "false - Call on-change on every keystroke"
                                                                   :value     false
                                                                   :model     @change-on-blur?
                                                                   :on-change #(reset! change-on-blur? false)
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label     "true - Call on-change only on blur or Enter key (Esc key resets text)"
                                                                   :value     true
                                                                   :model     @change-on-blur?
                                                                   :on-change #(reset! change-on-blur? true)
                                                                   :style     {:margin-left "20px"}]]]
                                                      [v-box
                                                       :children [[label :label ":status"]
                                                                  [radio-button
                                                                   :label     "nil/omitted - normal input state"
                                                                   :value     nil
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                (reset! status nil)
                                                                                (reset! status-tooltip ""))
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label     ":warning - Warning status"
                                                                   :value     :warning
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                (reset! status :warning)
                                                                                (reset! status-tooltip "Warning tooltip"))
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label     ":error - Error status"
                                                                   :value     :error
                                                                   :model     @status
                                                                   :on-change #(do
                                                                                (reset! status :error)
                                                                                (reset! status-tooltip "Error tooltip"))
                                                                   :style     {:margin-left "20px"}]]]
                                                      [checkbox
                                                       :label     ":status-icon?"
                                                       :model     status-icon?
                                                       :on-change (fn [val]
                                                                    (reset! status-icon? val))]
                                                      [checkbox
                                                       :label     (if @regex
                                                                    ":validation-regex - set to format '99.9'"
                                                                    ":validation-regex - nil (no character validation)")
                                                       :model     regex
                                                       :on-change (fn [val]
                                                                    (reset! regex (when val #"^(\d{0,2})$|^(\d{0,2}\.\d{0,1})$")))]
                                                      [checkbox
                                                       :label     ":disabled?"
                                                       :model     disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]
                  ]]
      )))


(defn hyperlink-demo
  []
  (let [disabled?   (reagent/atom false)
        click-count (reagent/atom 0)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[hyperlink ... ]"]
                              [args-table hyperlink-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "30px"
                               :children [[box
                                           :width "200px"
                                           :child [hyperlink
                                                   :label     (if @disabled? "Now disabled" "Call back")
                                                   :on-click  #(swap! click-count inc)
                                                   :disabled? disabled?]]
                                          [v-box
                                           :gap "15px"
                                           :children [[label :label (str "click count = " @click-count)]
                                                      [label :label "parameters:"]
                                                      [checkbox
                                                       :label ":disabled?"
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]]])))


(defn hyperlink-href-demo
  []
  (let [target    (reagent/atom "_blank")
        href?     (reagent/atom true)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[hyperlink-href ... ]"]
                              [args-table hyperlink-href-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "40px"
                               :children [[box
                                           :width "200px"
                                           :child [hyperlink-href
                                                   :label     "Launch Google"
                                                   :href      (when href? "http://google.com")
                                                   :target    (when href? target)]]
                                          [v-box
                                           :gap "15px"
                                           :children [[label :label "parameters:"]
                                                      (when @href?
                                                        [v-box
                                                         :children [[label :label ":target"]
                                                                    [radio-button
                                                                     :label "_self - load link into same tab"
                                                                     :value "_self"
                                                                     :model @target
                                                                     :on-change #(reset! target "_self")
                                                                     :style {:margin-left "20px"}]
                                                                    [radio-button
                                                                     :label "_blank - load link inot new tab"
                                                                     :value "_blank"
                                                                     :model @target
                                                                     :on-change #(reset! target "_blank")
                                                                     :style {:margin-left "20px"}]]])]]]]]]]])))


(defn slider-demo
  []
  (let [slider-val  (reagent/atom 0)
        slider-min  (reagent/atom 0)
        slider-max  (reagent/atom 100)
        slider-step (reagent/atom 1)
        disabled?   (reagent/atom false)]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[slider ... ]"]
                              [args-table slider-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "40px"
                               :children [[v-box
                                           :gap      "10px"
                                           :children [[slider
                                                       :model     slider-val
                                                       :min       slider-min
                                                       :max       slider-max
                                                       :step      slider-step
                                                       :width     "200px"
                                                       :on-change #(reset! slider-val %)
                                                       :disabled? disabled?]]]
                                          [v-box
                                           :gap      "15px"
                                           :children [[label :label "parameters:"]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[label
                                                                   :label ":model"
                                                                   :style {:width "60px"}]
                                                                  [input-text
                                                                   :model           slider-val
                                                                   :width           "70px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-val %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[label
                                                                   :label ":min"
                                                                   :style {:width "60px"}]
                                                                  [input-text
                                                                   :model           slider-min
                                                                   :width           "70px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-min %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[label
                                                                   :label ":max"
                                                                   :style {:width "60px"}]
                                                                  [input-text
                                                                   :model           slider-max
                                                                   :width           "70px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-max %)
                                                                   :change-on-blur? false]]]
                                                      [h-box
                                                       :gap      "10px"
                                                       :align    :center
                                                       :children [[label
                                                                   :label ":step"
                                                                   :style {:width "60px"}]
                                                                  [input-text
                                                                   :model           slider-step
                                                                   :width           "70px"
                                                                   :height          "26px"
                                                                   :on-change       #(reset! slider-step %)
                                                                   :change-on-blur? false]]]
                                                      [checkbox
                                                       :label ":disabled?"
                                                       :model disabled?
                                                       :on-change (fn [val]
                                                                    (reset! disabled? val))]]]]]]]]])))


(defn inline-tooltip-demo
  []
  (let [pos          (reagent/atom :below)
        status       (reagent/atom nil)
        tooltip-text (reagent/atom "This is a tooltip")]
    (fn
      []
      [h-box
       :gap      "50px"
       :children [[v-box
                   :gap      "10px"
                   :style    {:font-size "small"}
                   :children [[component-title "[inline-tooltip ... ]"]
                              [:p {:style {:width "400px" }}
                               "An inline tooltip that doesn't actually hover over anything.
                                It's actually embeded in the markup.
                                Useful for permanetly pointing to things that are a problem, without the need for
                                something hovering over other widgets and possibly obscuring them."]
                              [args-table inline-tooltip-args-desc]]]
                  [v-box
                   :children [[component-title "Demo"]
                              [h-box
                               :gap "40px"
                               :children [[v-box
                                           :gap      "10px"
                                           :children [[h-box
                                                       :gap   "5px"
                                                       :align :center
                                                       :children [[label :label "Tooltip text:"]
                                                                  [input-text
                                                                   :model           tooltip-text
                                                                   :status          @status
                                                                   :status-icon?    true
                                                                   :change-on-blur? false
                                                                   :on-change       #(reset! tooltip-text %)]]]
                                                      [inline-tooltip
                                                       :label    @tooltip-text
                                                       :position @pos
                                                       :status   @status]]]
                                          [v-box
                                           :gap "15px"
                                           :children [[label :label "parameters:"]
                                                      [v-box
                                                       :children [[label :label ":position"]
                                                                  [radio-button
                                                                   :label ":left"
                                                                   :value :left
                                                                   :model @pos
                                                                   :on-change #(reset! pos :left)
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label ":right"
                                                                   :value :right
                                                                   :model @pos
                                                                   :on-change #(reset! pos :right)
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label ":above"
                                                                   :value :above
                                                                   :model @pos
                                                                   :on-change #(reset! pos :above)
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label ":below"
                                                                   :value :below
                                                                   :model @pos
                                                                   :on-change #(reset! pos :below)
                                                                   :style {:margin-left "20px"}]
                                                                  ]]
                                                      [v-box
                                                       :children [[label :label ":status"]
                                                                  [radio-button
                                                                   :label     "nil/omitted - normal input state"
                                                                   :value     nil
                                                                   :model     @status
                                                                   :on-change #(reset! status nil)
                                                                   :style {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label     ":warning - Warning status"
                                                                   :value     :warning
                                                                   :model     @status
                                                                   :on-change #(reset! status :warning)
                                                                   :style     {:margin-left "20px"}]
                                                                  [radio-button
                                                                   :label     ":error - Error status"
                                                                   :value     :error
                                                                   :model     @status
                                                                   :on-change #(reset! status :error)
                                                                   :style     {:margin-left "20px"}]]]]]]]]]]])))


;; =====================================================================================================================
;;  START EXPERIMENTAL
;; =====================================================================================================================

#_(def demos [{:id 1 :label "Simple dropdown"}
            {:id 2 :label "Dropdown with grouping"}
            {:id 3 :label "Dropdown with filtering"}
            {:id 4 :label "Keyboard support"}
            {:id 5 :label "Other parameters"}
            {:id 6 :label "Two dependent dropdowns"}])


#_(defn h-box-demo
  []
  (let [selected-demo-id (reagent/atom 1)
        an-int-time      (reagent/atom 900)]
    (fn
      []
      [h-box
       ;:size     "600px"
       :height   "200px"
       ;:width    "600px"
       :gap      "5px"
       :style    {:border "1px dashed lightgrey"}
       :children [[button
                   :style {:border "1px dashed red"}
                   :label "Button"]
                  [hyperlink
                   :style {:border "1px dashed red"}
                   :label "Hyperlink"]
                  [label
                   :style {:border "1px dashed red"}
                   :label "Label"]
                  [gap
                   :style {:border "1px dashed red"}
                   :size  "10px"]
                  [line
                   :size "2px"]
                  [input-text
                   :width "150px"
                   :style {:border "1px dashed red"}
                   :on-change #()]
                  [checkbox
                   :label-style {:border "1px dashed red"}
                   :label       "Checkbox"
                   :on-change   #()]
                  [radio-button
                   :label-style {:border "1px dashed red"}
                   :label       "Radio"
                   :on-change   #()]
                  [single-dropdown
                   :choices   demos
                   :model     selected-demo-id
                   ;:width     "300px"
                   ;:style {:border "1px dashed red"}
                   :on-change #(reset! selected-demo-id %)]
                  [input-time
                   :model an-int-time
                   :on-change #(reset! an-int-time %)
                   :style {:border "1px dashed red"}
                   :show-icon? true]
                  ]])))


#_(defn v-box-demo
  []
  (let [selected-demo-id (reagent/atom 1)
        an-int-time      (reagent/atom 900)]
    (fn
      []
      [v-box
       ;:size     "600px"
       ;:height   "600px"
       :width    "200px"
       :gap      "5px"
       :style    {:border "1px dashed lightgrey"}
       :children [[button
                   :style {:border "1px dashed red"}
                   :label "Button"]
                  [hyperlink
                   :style {:border "1px dashed red"}
                   :label "Hyperlink"]
                  [label
                   :style {:border "1px dashed red"}
                   :label "Label"]
                  [gap
                   :style {:border "1px dashed red"}
                   :size  "10px"]
                  [line
                   :size "2px"]
                  [input-text
                   :width "150px"
                   :style {:border "1px dashed red"}
                   :on-change #()]
                  [checkbox
                   :label-style {:border "1px dashed red"}
                   :label       "Checkbox"
                   :on-change   #()]
                  [radio-button
                   :label-style {:border "1px dashed red"}
                   :label       "Radio"
                   :on-change   #()]
                  [single-dropdown
                   :choices   demos
                   :model     selected-demo-id
                   ;:width     "300px"
                   ;:style {:border "1px dashed red"}
                   :on-change #(reset! selected-demo-id %)]
                  [input-time
                   :model an-int-time
                   :on-change #(reset! an-int-time %)
                   :style {:border "1px dashed red"}
                   :show-icon? true]
                  ]])))


;; =====================================================================================================================
;;  END EXPERIMENTAL
;; =====================================================================================================================


(def demos [{:id  0 :label "button"                :component button-demo}
            {:id  1 :label "md-circle-icon-button" :component md-circle-icon-button-demo}
            {:id  2 :label "row-button"            :component row-button-demo}
            {:id  3 :label "checkbox"              :component checkboxes-demo}
            {:id  4 :label "radio-button"          :component radios-demo}
            {:id  5 :label "input-text"            :component inputs-demo}
            {:id  6 :label "hyperlink"             :component hyperlink-demo}
            {:id  7 :label "hyperlink-href"        :component hyperlink-href-demo}
            {:id  8 :label "slider"                :component slider-demo}
            {:id  9 :label "inline-tooltip"        :component inline-tooltip-demo}
            ;{:id 90 :label "h-box"                 :component h-box-demo} ;; Experimental
            ;{:id 91 :label "v-box"                 :component v-box-demo} ;; Experimental
            ])

(defn panel
  []
  (let [selected-demo-id (reagent/atom 0)]
    (fn []
      [v-box
       :gap "10px"
       :children [[panel-title "Basic Components" ]
                  [h-box
                   :gap      "50px"
                   :children [[vertical-bar-tabs
                               :model     selected-demo-id
                               :tabs      demos
                               :on-change #(reset! selected-demo-id %)]
                              [(get-in demos [@selected-demo-id :component])]]]]])))
