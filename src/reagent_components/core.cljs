(ns reagent-components.core
  (:require [reagent-components.util              :as    util]
            [reagent-components.v-layout          :refer [v-layout]]
            [reagent-components.h-layout          :refer [h-layout]]
            [reagent-components.alert             :refer [closeable-alert alert-list add-alert]]
            [reagent-components.popover           :refer [popover make-button make-link]]
            [reagent-components.tour              :refer [make-tour start-tour make-tour-nav]]
            [reagent-components.modal             :refer [show-modal-window add-modal-alert
                                                          make-cancel-button make-spinner make-progress-bar
                                                          chunk-runner chunked-runner
                                                          modal-io-runner modal-single-chunk-runner modal-multi-chunk-runner
                                                          modal-dialog]]
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

(def progress-percent    (reagent/atom 0))

(def demo-tour (make-tour [:step1 :step2 :step3 :step4]))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING #1 - Long running with progress and cancel
;; ------------------------------------------------------------------------------------

(def serious-process-1-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn serious-process-1-modal-markup []
  [:div {:style {:max-width "300px"}}
   [:p "Doing some serious processing. This might take some time, so hang on a moment..."]
   ])

(defn serious-process-1-chunk [chunk-index chunks percent]
  (util/console-log (str "START serious-processing 1: " chunk-index " of " chunks " (" percent "%)"))
  (reset! progress-percent percent)
  (if (= @serious-process-1-status :running)

    ;; ACTUAL PROCESSING CODE - START
    (loop [i 1]
      (when (< i 1000000)
        (def a (* (Math/sqrt i) (Math/log i)))
        (recur (inc i))))
    ;; ACTUAL PROCESSING CODE - END

    (util/console-log "CANCELLED!")))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING #2 - Short running with progress only
;; ------------------------------------------------------------------------------------

(def serious-process-2-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn serious-process-2-modal-markup []
  [:div {:style {:max-width "600px"}}
   [:img.img-rounded.smooth.pull-right
    {:src   "img/Guru.jpg"
     :style {:width "145px" :margin "20px"}}]
   [:h4 "Modal Demo #2"]
   [:p "This is the second modal demo and it is different to the first one, in terms of message displayed, length of process and what controls are displayed (in this case you can't cancel it. This is the second modal demo and it is different to the first one, in terms of message displayed, length of process and what controls are displayed (in this case you can't cancel it. "]
   ])

(defn serious-process-2-chunk [chunk-index chunks percent]
  (util/console-log (str "START serious-processing 2: " chunk-index " of " chunks " (" percent "%)"))
  (reset! progress-percent percent)
  (if (= @serious-process-2-status :running)

    ;; ACTUAL PROCESSING CODE - START
    (loop [i 1]
      (when (< i 1000000)
        (def a (* (Math/sqrt i) (Math/log i)))
        (recur (inc i))))
    ;; ACTUAL PROCESSING CODE - END

    (util/console-log "CANCELLED!")))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING #3 - Fibonacci sequence
;; ------------------------------------------------------------------------------------

(def fib-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn fib-markup []
  [:div {:style {:max-width "200px"}}
   [:p "Calculating some Fibonacci numbers..."]
   ])

(defn fib [a b] (cons a (lazy-seq (fib b (+ b a)))))

(def p1 (atom 1))
(def p2 (atom 1))

(defn fibonacci []
  (let [chunks 5]

    (fn [fib-status]
      (let [res (take chunks (fib @p1 @p2))]
        (util/console-log (str "(fib " @p1 " "  @p2 ") = " res))
        (reset! p1 (+ (last res) (last (butlast res))))
        (reset! p2 (+ @p1 (last res)))
        (when (= @p1 420196140727489660) (reset! fib-status :finished))

        ;; DELAY - START
        (loop [i 1]
          (when (< i 5000000)
            (def a (* (Math/sqrt i) (Math/log i)))
            (recur (inc i))))
        ;; DELAY - END

        ))
    ))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 1 - Loading a URL
;; ------------------------------------------------------------------------------------

;; URL and simulated returned data
(def url-to-load "http://static.day8.com.au/locations.xml")
(def xml-data    "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data>here it is!</data>")

;; Simulate the system's load-url functionality (will call back in 3 seconds)
(defn system-load-url [url callback]
  (js/setTimeout #(callback nil xml-data) 3000))

;; ------------------------------------------------------------------------------------

(def load-url-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn load-url-markup [url]
  [:div {:style {:max-width "600px"}}
   [:p (str "Loading data from '" url "'...")]])

(defn load-url [url]
  (util/console-log (str "*** Loading data from: " url))
  (system-load-url
   url
   (fn [err data]
     (if err
       (do
         (util/console-log (str "*** ERROR: " err))

         ;; HANDLE ERROR HERE

         )
       (do
         (if (= @load-url-status :cancelled)
           (util/console-log "*** CANCELLED!")
           (do
             (util/console-log (str "*** Data returned: " data))

             ;; PROCESS THE RETURNED DATA HERE

             ))
         (reset! load-url-status :finished)
         ))
     ))
  )

;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 2 - Write to disk
;; ------------------------------------------------------------------------------------

;; Path to save to
(def mwi-file "C:\\Day8\\MWIEnhancer\\test.mwi")

;; Simulate the system's load-url functionality (will call back in 3 seconds)
(defn system-write-path [path data callback]
  (js/setTimeout #(callback "The file could not be saved - Disk Full! " nil) 3000))

;; ------------------------------------------------------------------------------------

(def write-disk-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn write-disk-markup [path]
  [:div {:style {:max-width "600px"}}
   [:p (str "Saving '" path "'...")]
   [:div {:style {:display "flex"}}
    [:div {:style {:margin "auto"}}
     [:img {:src "img/spinner.gif" :style {:margin-right "12px"}}]
     [:input.btn.btn-danger
      {:type "button"
       :value "STOP!"
       :on-click #(reset! write-disk-status :cancelled)
       }]]]])


(defn write-disk [path]
  (util/console-log (str "*** Saving data to: " path))
  (system-write-path
   path
   "data to write to path"
   (fn [err data]
     (if err
       (do
         (util/console-log (str "*** ERROR: " err))
         (add-modal-alert {:alert-type "danger"
                           :heading "File Error"
                           :body err})
         )
       (do
         (if (= @write-disk-status :cancelled)
           (util/console-log "*** CANCELLED!")
           (do
             (util/console-log (str "*** SAVED!"))

             ;; FURTHER PROCESSING HERE IF REQUIRED

             ))
         (reset! write-disk-status :finished)
         ))
     ))
  )


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 3 - Calculating pivot totals
;; ------------------------------------------------------------------------------------

(def calc-pivot-totals-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn calc-pivot-totals-markup []
  [:div {:style {:max-width "200px"}}
   [:p
    {:style {:text-align "center"}}
    [:strong "Calculating pivot totals"] [:br] "Please wait..."]
   ])

(defn calc-pivot-totals []

  ;; DELAY - START
  (util/console-log "calc-pivot-totals START")
  (loop [i 1]
    (when (< i 50000000)
      (def a (* (Math/sqrt i) (Math/log i)))
      (recur (inc i))))
  (util/console-log "calc-pivot-totals END")
  ;; DELAY - END

  (reset! calc-pivot-totals-status :finished))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 7 - Arbitrarily complex input form
;; ------------------------------------------------------------------------------------

(def test-form-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(def test-form-data (reagent/atom {:email       "gregg.ramsey@day8.com.au"
                              :password    "abc123"
                              :remember-me true}))

(defn test-form-submit [event]
  (reset! test-form-status :finished)
  (util/console-log-prstr "Submitted form: form-data" test-form-data)
  false) ;; Prevent default "GET" form submission to server

(defn form-cancel []
  ;; (reset! test-form-data @initial-test-form-data)
  (reset! test-form-status :cancelled)
  (util/console-log-prstr "Cancelled form (not implemented): form-data" test-form-data)
  false) ;; Prevent default "GET" form submission to server

(defn test-form-markup []
  [:div {:style {:padding "5px" :background-color "cornsilk" :border "1px solid #eee"}} ;; [:form {:name "pform" :on-submit ptest-form-submit}
   [:h3 "Welcome to MWI Enhancer"]
   [:div.form-group
    [:label {:for "pf-email"} "Email address"]
    [:input#pf-email.form-control
     {:name        "email"
      :type        "text"
      :placeholder "Type email"
      :style       {:width "250px"}
      :value       (:email @test-form-data)
      :on-change   #(swap! test-form-data assoc :email (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-password"} "Password"]
    [:input#pf-password.form-control
     {:name        "password"
      :type        "password"
      :placeholder "Type password"
      :style       {:width "250px"}
      :value       (:password @test-form-data)
      :on-change   #(swap! test-form-data assoc :password (-> % .-target .-value))}]
    ]
   [:div.checkbox
    [:label
     [:input
      {:name      "remember-me"
       :type      "checkbox"
       :checked   (:remember-me @test-form-data)
       :on-change #(swap! test-form-data assoc :remember-me (-> % .-target .-checked))}
      "Remember me"]]]
   [:hr {:style {:margin "10px 0 10px"}}]
   [:button.btn.btn-primary
    {:type     "button" ;; submit
     :on-click test-form-submit}
    "Sign in"]
   [:span " "]
   [:button.btn.btn-default
    {:type     "button"
     :on-click form-cancel}
    "Cancel"]
   ])


;; ------------------------------------------------------------------------------------
;;  TEST HARNESS MAIN
;; ------------------------------------------------------------------------------------

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


    [:div {:style {:margin-top "40px"}}]

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


    ;; Tour component

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

     ] ;; End of tour wrapper


    ;; Modal component

    [:div.container
     {:style {:width "100%" :margin-top "20px" :margin-left "0px" :margin-right "0px"}}
     [:div.row
      [:div.col-xs-2
       {:style {:text-align "right"}}
       [:h4 "Modals demos:"]]
      [::div.col-xs-10
       [:div {:style {:display "flex" :flex-flow "row" :flex-wrap "wrap"}} ;; Modal wrapper

        ;; MODAL - LONG

        [popover
         :right-center
         show-modal-popover?
         [:input.btn.btn-info
          {:style {:font-weight "bold" :color "red" :margin "1px" :height "39px"}
           :type "button"
           :value "Long"
           :on-mouse-over #(reset! show-modal-popover? true)
           :on-mouse-out  #(reset! show-modal-popover? false)
           :on-click      #(chunk-runner
                            serious-process-1-chunk
                            serious-process-1-status
                            250)}]
         {:body  [:div
                  [:p "Click on this button to launch a modal demo. The demo will start an intensive operation and..."]
                  [:p "It will have a progress bar which looks something like this:"]
                  [:div.progress
                   [:div.progress-bar
                    {:role "progressbar"
                     :style {:width "60%"}}
                    "60%"]]]
          :width 300}]
        (when (= @serious-process-1-status :running)
          [show-modal-window
           [serious-process-1-modal-markup]
           serious-process-1-status
           {:progress-bar true
            :cancel-button true}
           progress-percent]
          )

        ;; MODAL - SHORT

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "Short"
          :on-click #(chunk-runner
                      serious-process-2-chunk
                      serious-process-2-status
                      57)}]
        (when (= @serious-process-2-status :running)
          [show-modal-window
           [serious-process-2-modal-markup]
           serious-process-2-status
           {:progress-bar true}
           progress-percent]
          )

        ;; MODAL - FIBONACCI

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "Fib"
          :on-click #(chunked-runner fibonacci fib-status)}]
        ;; (when (= @fib-status :running)
          [show-modal-window
           [fib-markup]
           fib-status
           {:spinner       true
            :cancel-button true}]
          ;; )

        ;; MODAL - USE CASE 1 - Loading URL

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "1. I/O Load url"
          :on-click #(modal-io-runner
                      load-url
                      [url-to-load]
                      load-url-status)
          ;; Equivalent without calling modal-io-runner...
          ;; :on-click #(do
          ;;              (reset! load-url-status :running)
          ;;              (load-url url-to-load))
          }]
        ;; (when (= @load-url-status :running)
          [show-modal-window
           [load-url-markup url-to-load]
           load-url-status
           {:spinner       true
            :cancel-button true}] ;; NOTE: Using default cancel button
          ;; )

        ;; MODAL - USE CASE 2 - Writing to disk

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "2. I/O Save to disk"
          :on-click #(modal-io-runner
                      write-disk
                      [mwi-file]
                      write-disk-status)}]
        ;; (when (= @write-disk-status :running)
          [show-modal-window
           [write-disk-markup mwi-file]
           write-disk-status] ;; NOTE: NOT using default cancel button
          ;; )

        ;; MODAL - USE CASE 3 - Calculating pivot totals

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "3. CPU-S Pivot calc"
          :on-click #(modal-single-chunk-runner
                      calc-pivot-totals
                      []
                      calc-pivot-totals-status)}]
        ;; (when (= @calc-pivot-totals-status :running)
          [show-modal-window
           [calc-pivot-totals-markup]
           calc-pivot-totals-status]
          ;; )

;;        ;; MODAL - USE CASE 4 - Processing a large in-memory XML file (chunked).
;;
;;        [:input.btn.btn-info
;;         {:style {:font-weight "bold" :color "red" :margin "1px"}
;;          :type "button"
;;          :value "4. CPU-M Process XML (chunked)"
;;          :on-click #(modal-multi-chunk-runner
;;                      calc-pivot-totals
;;                      calc-pivot-totals-status)}]
;;        ;; (when (= @calc-pivot-totals-status :running)
;;          [show-modal-window
;;           [calc-pivot-totals-markup]
;;           calc-pivot-totals-status]
;;          ;; )
;;
;;        ;; MODAL - USE CASE 5 - MWI Enhancer modifying EDN in steps (multiple fn calls, not chunked).
;;
;;        [:input.btn.btn-info
;;         {:style {:font-weight "bold" :color "red" :margin "1px"}
;;          :type "button"
;;          :value "5. CPU-M Modify EDN in steps (unchunked)"
;;          :on-click #(modal-multi-chunk-runner
;;                      calc-pivot-totals
;;                      calc-pivot-totals-status)}]
;;        ;; (when (= @calc-pivot-totals-status :running)
;;          [show-modal-window
;;           [calc-pivot-totals-markup]
;;           calc-pivot-totals-status]
;;          ;; )
;;
;;        ;; MODAL - USE CASE 6 - Creating large JSON data for writing (chunked), then writing (a type A I/O job).
;;
;;        [:input.btn.btn-info
;;         {:style {:font-weight "bold" :color "red" :margin "1px"}
;;          :type "button"
;;          :value "6. CPU-M Create JSON (chunked) then write to disk"
;;          :on-click #(modal-multi-chunk-runner
;;                      calc-pivot-totals
;;                      calc-pivot-totals-status)}]
;;        ;; (when (= @calc-pivot-totals-status :running)
;;          [show-modal-window
;;           [calc-pivot-totals-markup]
;;           calc-pivot-totals-status]
;;          ;; )

        ;; MODAL - USE CASE 7 - Arbitrarily complex input form

        [:input.btn.btn-info
         {:style {:font-weight "bold" :color "red" :margin "1px"}
          :type "button"
          :value "7. Modal Dialog"
          :on-click #(modal-dialog
                      test-form-status)}]
        ;; (when (= @test-form-status :running)
          [show-modal-window
           [test-form-markup]
           test-form-status]
          ;; )

        ]  ;; End of modal wrapper
       ]]] ;; End of container/row



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
