(ns reagent-components.core
  (:require [reagent-components.util :as util]
            [reagent-components.alert :as alert]
            [reagent-components.popover :refer [popover make-button make-link]]
            [reagent-components.tour :as tour]
            [reagent-components.v-layout :as v-layout]
            [reagent-components.popover-form-demo :as popover-form-demo]
            [reagent.core :as reagent]))


(def show-alert-popover?  (reagent/atom true))

(def show-but1-popover?   (reagent/atom false))
(def show-but2-popover?   (reagent/atom false))
(def show-but3-popover?   (reagent/atom false))
(def show-but4-popover?   (reagent/atom false))
(def show-but5-popover?   (reagent/atom false))
(def show-but6-popover?   (reagent/atom false))
(def show-but7-popover?   (reagent/atom false))
(def show-but8-popover?   (reagent/atom false))

(def show-link1-popover?  (reagent/atom false))
(def show-link2-popover?  (reagent/atom false))

(def show-red-popover?    (reagent/atom false))
(def show-green-popover?  (reagent/atom false))
(def show-blue-popover?   (reagent/atom false))

(def show-div-popover?    (reagent/atom false))


(defn test-harness []
  [:div.panel.panel-default {:style {:margin "8px"}}
   [:div.panel-body

    ;; Alert box list (popover :below-center)

    [popover
     :right-below
     show-alert-popover?
     [alert/alert-list]
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

    ;; Flexbox button bar wrapper

    [:div {:style {:display "flex" :flex-flow "row"}}

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
                         :on-click #(do (reset! show-but5-popover? false) (reset! show-red-popover? true))}]]}]


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
     ]


    ;; Link popovers

    [:div {:style {:margin-top "1em" :display "flex"}}
     "Here is a FLEX div with text, and then here is a call to "
     [popover
      :above-center
      show-link1-popover?
      [make-link show-link1-popover? :mouse "create-link-popover"]
      {:title [:strong "LINK Popover Title"]
       :body  "This is the body of the link popover. This is the body of the link popover. This is the body of the link popover. This is the body of the link popover."}]
     " with " [:strong "mouseover/mouseout"] " used to show/hide the popover. "
     ]


    [:div {:style {:margin-top "1em"}}
     "Here is a STANDARD div, and then here is a call to "
     [popover
      :above-center
      show-link2-popover?
      [make-link show-link2-popover? :click "create-link-popover"]
      {:title [:strong "LINK Popover Title"]
       :body "This is the body of the link popover. This is the body of the link popover. This is the body of the link popover. This is the body of the link popover."}]
     " with " [:strong "click"] " used to show/hide the popover. "
     ]


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
                        ]}]

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
                          :value "CLOSE"
                          :on-click #(reset! show-blue-popover? false)}]
                        ]}]
      ]]


    ;; Orange square - :right-center - no flex stuff added yet so doesn't work properly

    [popover
     :right-center
     show-div-popover?
     [:div {:style {:background-color "coral"
                    :display "block"
                    :margin-top "20px"
                    :width "200px"
                    :height "200px"}
            :on-mouse-over #(reset! show-div-popover? true)
            :on-mouse-out #(reset! show-div-popover? false)
            :on-click #(reset! show-div-popover? (not @show-div-popover?))
            }]
     {:title "Rollover Popover"
      :body  "This is basically a tooltip."}]
    ]])


(defn init []
  (alert/add-alert "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
  (alert/add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})
  (alert/add-alert "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
  (alert/add-alert "info" {:heading "Here's some info for you" :body "The rain in Spain falls mainly on the plain"})

  (reagent/render-component [test-harness] (util/get-element-by-id "app")))
