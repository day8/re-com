(ns reagent-components.core
  (:require [reagent-components.util              :as    util]
            [reagent-components.v-layout          :refer [v-layout]]
            [reagent-components.h-layout          :refer [h-layout]]
            [reagent-components.alert             :refer [closeable-alert alert-list add-alert]]
            [reagent-components.popover           :refer [popover make-button make-link]]
            [reagent-components.tour              :refer [make-tour start-tour make-tour-nav]]
            [reagent-components.modal             :refer [modal-dialog]]
            [reagent-components.popover-form-demo :as    popover-form-demo]
            [reagent.core                         :as    reagent]))


(def show-alert-popover? (reagent/atom true))

(def show-but1-popover?  (reagent/atom false))
(def show-but2-popover?  (reagent/atom false))
(def show-but3-popover?  (reagent/atom false))
(def show-but4-popover?  (reagent/atom false))
(def show-but5-popover?  (reagent/atom false))
(def show-but6-popover?  (reagent/atom false))
(def show-but7-popover?  (reagent/atom false))
(def show-but8-popover?  (reagent/atom false))

(def show-link1-popover? (reagent/atom false))
(def show-link2-popover? (reagent/atom false))

(def show-red-popover?   (reagent/atom false))
(def show-green-popover? (reagent/atom false))
(def show-blue-popover?  (reagent/atom false))

(def show-modal-popover? (reagent/atom false))

(def show-div-popover?   (reagent/atom false))

(def demo-tour (make-tour [:step1 :step2 :step3 :step4]))


(def show-processing-modal? (reagent/atom false))
(def progress-percent (reagent/atom 0))

(defn processing-modal-markup []
  (let [percent (str @progress-percent "%")]
    [:div
     [:p "Doing some serious processing, this might take some time"]
     [:div.progress
      [:div.progress-bar
       {:role "progressbar"
        :style {:width (str @progress-percent "%")
                :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
       (str @progress-percent "%")]]
     [:div {:style {:display "flex"}}
      [:input#cancelbutton.btn.btn-info  ;; TODO: Hard coded ID
       {:type "button"
        :value "Cancel"
        :style {:margin "auto"}
        :on-click #(reset! show-processing-modal? false)
        }]]
     ]))


;; (defn serious-processing [iterations]
;;   (util/console-log "START serious-processing")
;;   (loop [i 1]
;;     (when (= (mod i 1000000) 0)
;;       (util/console-log "do-events")
;;       (js/setTimeout #(do
;;                         (util/console-log "in setTimeout")
;;                         (when-not @show-processing-modal?
;;                           (util/console-log "OUTTA HERE!"))) 0))
;;     (if @show-processing-modal?
;;       (when (< i iterations)
;;         (def a (* (Math/sqrt i) (Math/log i)))
;;         ;; (when (= i 1000000) (reset! show-processing-modal? false))
;;         (recur (inc i)))
;;       (util/console-log (str "Cancel clicked at i=" i))
;;       ))
;;   (reset! show-processing-modal? false)
;;   (util/console-log "END serious-processing")
;;   )


(defn serious-processing-chunk [iterations percent]
  (util/console-log (str "START serious-processing: " percent))
  (reset! progress-percent percent)
  (if @show-processing-modal?
    (loop [i 1]
      (when (< i iterations)
        (def a (* (Math/sqrt i) (Math/log i)))
        (recur (inc i))))
    (util/console-log "CANCELLED!"))
  )


(defn do-some-serious-processing []
  (util/console-log "STARTING do-some-serious-processing")
  (reset! show-processing-modal? true)

  ;; (serious-processing 40000000)

  ;; (js/setTimeout #(serious-processing 40000000) 0)

  (let [chunks 200]
    (loop [i 0]
      (if (< i chunks)
        (let [percent (int (+ (* (/ i chunks) 100) 0.5))]
          (js/setTimeout #(serious-processing-chunk 1000000 percent) 0) ;; Schedule each chunk of work
          (recur (inc i)))
        (js/setTimeout #(reset! show-processing-modal? false) 0)) ;; Schedule closing the modal
      ))

  (util/console-log "FINISHED do-some-serious-processing")
  )


(defn test-harness []
  [:div.panel.panel-default {:style {:margin "8px"}}
   [:div.panel-body

    ;; Alert box list (popover :below-center)

    [popover
     :right-below
     show-alert-popover?
     [alert-list]
     {:width         300
      :title         [:strong "Product Tour (1 of 5)"]
      :close-button? true
      :body          [:div "Welcome to the sample tour. Use the buttons below to move through the tour."
                      [:hr {:style {:margin "10px 0 10px"}}]
                      [:input.btn.btn-default
                       {:type "button"
                        :value "Next"
                        :on-click #(do (reset! show-alert-popover? false) (reset! show-but5-popover? true))}]
                      ]}]


    [:div {:style {:margin-top "90px"}}]

    [:div {:style {:display "flex" :flex-flow "row"}} ;; Flexbox button bar wrapper

     ;; Button #1 - :right-below
     [popover
      :right-below
      show-but1-popover?
      [:input.btn.btn-success
       {:type "button"
        :value ":right-below"
        :on-click #(reset! show-but1-popover? (not @show-but1-popover?))}]
      {:title        "Popover Title"
       :body         "Popover body. Can be a simple string or in-line hiccup or a function returning hiccup"}
      {:arrow-length 30}]

     ;; Button #2 - :above-right
     [popover
      :above-right
      show-but2-popover?
      [:input.btn.btn-success
       {:type "button"
        :value ":above-right"
        :on-click #(reset! show-but2-popover? (not @show-but2-popover?))}]
      {:body        "Popover body without a title. Basically a tooltip"}
      {:arrow-width 33}]

     ;; Button #3 - :left-above
     [popover
      :left-above
      show-but3-popover?
      [:input.btn.btn-success
       {:type "button"
        :value ":left-above"
        :on-click #(reset! show-but3-popover? (not @show-but3-popover?))}]
      {:width 150
       :title "Popover Title"
       :body  "Popover body. Can be a simple string or in-line hiccup or a function returning hiccup"}]

     ;; Popover form demo - :right-below
     [popover-form-demo/show]

     ;; Button #4 - :below-left
     [popover
      :below-left
      show-but4-popover?
      [:input.btn.btn-success
       {:type "button"
        :value ":below-left"
        :on-click #(reset! show-but4-popover? (not @show-but4-popover?))}]
      {:title "Popover Title"
       :body  "Popover body. Can be a simple string or in-line hiccup or a function returning hiccup"}]

     ;; Button #5 - :left-center
     [popover
      :left-center
      show-but5-popover?
      [:input.btn.btn-success
       {:type "button"
        :value ":left-center"
        :on-click #(reset! show-but5-popover? (not @show-but5-popover?))}]
      {:title         [:strong "Product Tour (2 of 5)"]
       :close-button? true
       :body          [:div "This is a button you can click to show or hide this popover, but it's also part of the tour so you can click the buttons below to move through it."
                       [:hr {:style {:margin "10px 0 10px"}}]
                       [:input.btn.btn-default
                        {:type "button"
                         :value "Previous"
                         :style {:margin-right "15px"} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                         :on-click #(do (reset! show-but5-popover? false) (reset! show-alert-popover? true))}]
                       [:input.btn.btn-default
                        {:type "button"
                         :value "Next"
                         :on-click #(do (reset! show-but5-popover? false) (reset! show-red-popover? true))}]]}
      {:backdrop-callback #(reset! show-but5-popover? false)
       :backdrop-opacity .3}]


     ;; Button popovers

     [popover
      :above-left
      show-but6-popover?
      [make-button show-but6-popover? "info" ":above-left"]
      {:title [:strong "BUTTON Popover Title"]
       :body  "This was created using a call to create-button-popover"}]

     [popover
      :left-below
      show-but7-popover?
      [make-button show-but7-popover? "default" ":left-below"]
      {:title [:strong "BUTTON Popover Title"]
       :body  "This is another button created using a call to create-button-popover"}]

     [popover
      :below-right
      show-but8-popover?
      [make-button show-but8-popover? "link" ":below-right"]
      {:title [:strong "BUTTON Popover Title"]
       :body  "This is another button created using a call to create-button-popover"}]

     ] ;; End of flexbox button bar wrapper


    ;; Link popovers

    [:div {:style {:margin-top "1em" :display "flex"}}
     "Here is a FLEX div with text, and then here is a call to "
     [popover
      :above-center
      show-link1-popover?
      [make-link show-link1-popover? :mouse "create-link-popover"]
      {:title [:strong "LINK Popover Title"]
       :body  "This is the body of the link popover. This is the body of the link popover. This is the body of the link popover. This is the body of the link popover."}]
     " with " [:strong "mouseover/mouseout"] " used to show/hide the popover. "]

    [:div {:style {:margin-top "1em"}}
     "Here is a STANDARD div, and then here is a call to "
     [popover
      :above-center
      show-link2-popover?
      [make-link show-link2-popover? :click "create-link-popover"]
      {:title [:strong "LINK Popover Title"]
       :body "This is the body of the link popover. This is the body of the link popover. This is the body of the link popover. This is the body of the link popover."}]
     " with " [:strong "click"] " used to show/hide the popover. "]


    ;; Red, green, blue rectangles

    [:div {:style {:margin-top "20px"}}
     [:div {:style {:display "flex" :flex-flow "row" :align-items "center"}}
      [popover
       :above-center
       show-red-popover?
       [:div {:style {:background-color "red" :display "block" :width "200px" :height "100px" :margin "0 20px 0 20px"}}]
       {:title         [:strong "Product Tour (3 of 5)"]
        :close-button? true
        :body          [:div "Here is a lovely red rectangle. It's a great warm colour and perfect for Winter."
                        [:hr {:style {:margin "10px 0 10px"}}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Previous"
                          :style {:margin-right "15px"} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                          :on-click #(do (reset! show-red-popover? false) (reset! show-but5-popover? true))}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Next"
                          :on-click #(do (reset! show-red-popover? false) (reset! show-green-popover? true))}]
                        ]}
       {:backdrop-callback #(reset! show-red-popover? false)
        :backdrop-opacity .3}]

      [popover
       :below-center
       show-green-popover?
       [:div {:style {:background-color "green" :display "block" :width "200px" :height "150px" :margin "0 20px 0 20px"}}]
       {:title         [:strong "Product Tour (4 of 5)"]
        :close-button? true
        :body          [:div "And now we move onto the green rectangle. Feels like Spring to me."
                        [:hr {:style {:margin "10px 0 10px"}}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Previous"
                          :style {:margin-right "15px"} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                          :on-click #(do (reset! show-green-popover? false) (reset! show-red-popover? true))}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Next"
                          :on-click #(do (reset! show-green-popover? false) (reset! show-blue-popover? true))}]
                        ]}]


      [popover
       :right-below
       show-blue-popover?
       [:div {:style {:background-color "blue" :color "white" :cursor "pointer" :text-align "center" :display "block" :width "300px" :height "100px" :margin "0 0 0 20px"}
              :on-click #(reset! show-alert-popover? true)} "CLICK HERE TO RESTART TOUR"]
       {:title         [:strong "Product Tour (5 of 5)"]
        :close-button? true
        :body          [:div "Finally the blue rectagle. Summer at the beach, right?"
                        [:hr {:style {:margin "10px 0 10px"}}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Previous"
                          :style {:margin-right "15px"} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                          :on-click #(do (reset! show-blue-popover? false) (reset! show-green-popover? true))}]
                        [:input.btn.btn-default
                         {:type "button"
                          :value "Finish"
                          :on-click #(reset! show-blue-popover? false)}]
                        ]}]
      ]]


    ;; Tour component PLUS modal component

    [:div {:style {:display "flex" :flex-flow "row" :margin-top "20px" :margin-left "20px"}} ;; Tour/modal wrapper
     [:h4 {:style {:margin-right "20px"}} "Here is a sample of the new tour component:"]

     [popover
      :above-center
      (:step1 demo-tour)
      [:input.btn.btn-info ;; Can't use make-button as we need a custom on-click
       {:style {:font-weight "bold" :color "yellow"}
        :type "button"
        :value "Start Tour"
        :on-click #(start-tour demo-tour)}]
      {:title [:strong "Tour 1 of 4"]
       :close-button? true
       :body          [:div "So this is the first tour popover"
                       [make-tour-nav demo-tour]]}]

     [popover
      :above-center
      (:step2 demo-tour)
      [make-button (:step2 demo-tour) "info" "Tour 2"]
      {:title [:strong "Tour 2 of 4"]
       :close-button? true
       :body          [:div "And this is the second tour popover"
                       [make-tour-nav demo-tour]]}]

     [popover
      :above-center
      (:step3 demo-tour)
      [make-button (:step3 demo-tour) "info" "Tour 3"]
      {:title [:strong "Tour 3 of 4"]
       :close-button? true
       :body          [:div "Penultimate tour popover"
                       [make-tour-nav demo-tour]]}]

     [popover
      :above-right
      (:step4 demo-tour)
      [make-button (:step4 demo-tour) "info" "Tour 4"]
      {:title [:strong "Tour 4 of 4"]
       :close-button? true
       :body          [:div "Lucky last tour popover"
                       [make-tour-nav demo-tour]]}]

     [popover
      :right-center
      show-modal-popover?
      [:input.btn.btn-info
       {:style {:font-weight "bold" :color "red" :margin-left "10px"}
        :type "button"
        :value "Modal Demo"
        :on-mouse-over #(reset! show-modal-popover? true)
        :on-mouse-out  #(reset! show-modal-popover? false)
        :on-click      #(do-some-serious-processing)}]
      {:body  [:div
               [:p "Click on this button to launch a modal demo. The demo will start an intensive operation and..."]
               [:p "It will have a progress bar which looks something like this:"]
               [:div.progress
                [:div.progress-bar
                 {:role "progressbar"
                  :style {:width "60%"}}
                 "60%"]]]
       :width 300}]
     (when @show-processing-modal? [modal-dialog processing-modal-markup "cancelbutton" show-processing-modal?])


     ] ;; End of tour/modal wrapper


    ;; Orange square - :right-center - no flex stuff added yet so doesn't work properly

    [popover
     :right-center
     show-div-popover?
     [:div {:style {:background-color "coral"
                    :display          "block"
                    :margin-top       "20px"
                    :width            "200px"
                    :height "200px"}
            :on-mouse-over #(reset! show-div-popover? true)
            :on-mouse-out  #(reset! show-div-popover? false)
            :on-click      #(reset! show-div-popover? (not @show-div-popover?))}]
     {:title "Rollover Popover"
      :body  "This is basically a tooltip."}]
    ]])


(defn init []
  (add-alert "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
  (add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})
  (add-alert "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
  (add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})

  (reagent/render-component [test-harness] (util/get-element-by-id "app")))
