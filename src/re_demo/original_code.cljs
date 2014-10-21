(ns re-demo.original-code
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [re-com.util               :as    util]
            [re-com.core               :refer [button spinner progress-bar]]
            [re-com.layout             :refer [h-layout v-layout]]
            [re-com.alert              :refer [alert-box alert-list]]
            [re-com.popover            :refer [popover make-button make-link]]
            [re-com.tour               :refer [make-tour start-tour make-tour-nav]]
            [re-com.modal              :refer [modal-window cancel-button looper domino-process]]
            [re-com.tabs               :refer [horizontal-tabs horizontal-pills]]
            [re-demo.popover-form-demo :as    popover-form-demo]
            [cljs.core.async           :refer [<! >! chan close! put! take! alts! timeout]]
            [reagent.core              :as    reagent]))



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


(defn cpu-delay
  [num-times]
  (let [waste-time  #(Math.tan (Math.tan (+ %1 %2)))         ;; tan is a time consuming operation
        iterations  (if (nil? num-times) 10 num-times)]
    (dotimes [n iterations]
      (doall (reduce waste-time n (range 1000000))))))



(defn modal-button
  [& {:keys [label on-click]}]
  "Render a button to launch a modal window"
  [button
   :label    label
   :on-click on-click
   :style {:font-weight "bold" :color "red" :margin "1px" :height "39px"}
   :class "btn-info"])

;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING #1 - Long running with progress and cancel
;; ------------------------------------------------------------------------------------

(def serious-process-1-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn serious-process-1-modal-markup
  []
  [:div {:style {:max-width "300px"}}
   [:p "Doing some serious processing. This might take some time, so hang on a moment..."]])

(defn serious-process-1-chunk
  [chunk-index chunks percent]
  (println (str "START serious-processing 1: " chunk-index " of " chunks " (" percent "%)"))
  (reset! progress-percent percent)
  (if (= @serious-process-1-status :running)
    (cpu-delay 10)
    (println "CANCELLED!")))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING #2 - Short running with progress only
;; ------------------------------------------------------------------------------------

(def serious-process-2-status (reagent/atom nil)) ;; :running, :finished, :cancelled

(defn serious-process-2-modal-markup
  []
  [:div {:style {:max-width "600px"}}
   [:img.img-rounded.smooth.pull-right
    {:src   "img/Guru.jpg"
     :style {:width "145px" :margin "20px"}}]
   [:h4 "Modal Demo #2"]
   [:p "This is the second modal demo and it is different to the first one, in terms of message displayed, length of process and what controls are displayed (in this case you can't cancel it. This is the second modal demo and it is different to the first one, in terms of message displayed, length of process and what controls are displayed (in this case you can't cancel it. "]])

(defn serious-process-2-chunk
  [chunk-index chunks percent]
  (println (str "START serious-processing 2: " chunk-index " of " chunks " (" percent "%)"))
  (reset! progress-percent percent)
  (if (= @serious-process-2-status :running)
    (cpu-delay 10)
    (println "CANCELLED!")))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 1 - Loading a URL
;; ------------------------------------------------------------------------------------

(defn system-load-url
  [url callback]
  "Simulate the system's load-url functionality (will call back in 3 seconds)
  "
  (let [err  nil
        data "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data>here it is!</data>"]
    (js/setTimeout #(callback err data) 3000)))

;; ------------------------------------------------------------------------------------

(defn loading-url-modal
  [url loading?]
  "Show this modal window when loading a url"
  [modal-window
   :markup [:div {:style {:max-width "600px"}}
            [:p (str "Loading data from '" url "'...")]
            [spinner]
            [cancel-button #(reset! loading? false)]]])


(defn load-url
  [url loading?]
  "Load some data from the remote server"
  (println (str "*** Loading data from: " url))
  (system-load-url
    url
    (fn [err data]
      (if err
        (do
          (println (str "*** ERROR: " err))
          ;; ***** HANDLE ERROR HERE
          )
        (do
          (if @loading?
            (do
              (println (str "*** Data returned: " data))
              ;; ***** PROCESS THE RETURNED DATA HERE
              )
            (println "*** CANCELLED!"))
          (reset! loading? false))))))


(defn test-load-url
  []
  "Create a button to test the modal component for loading a url"
  (let [loading? (reagent/atom false)
        url      "http://static.day8.com.au/locations.xml"]
    (fn []
      [:div
       [modal-button
        :label  "1. I/O Load url"
        :on-click #(do
                    (reset! loading? true)
                    (load-url url loading?))]
       (when @loading?
         [loading-url-modal url loading?])])))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 2 - Write to disk
;; ------------------------------------------------------------------------------------

(defn system-write-path
  [path data callback]
  "Simulate the system's load-url functionality (will call back in 3 seconds)"
  (let [err  "The file could not be saved - Disk Full!"
        data nil]
    (js/setTimeout #(callback err data) 3000)))

;; ------------------------------------------------------------------------------------

(defn writing-disk-modal
  [path writing?]
  "Show this modal window when saving to disk"
  [modal-window
   :markup [:div {:style {:max-width "600px"}}
            [:p (str "Saving '" path "'...")]
            [:div {:style {:display "flex"}}
             [:div {:style {:margin "auto"}}
              [:img {:src "img/spinner.gif" :style {:margin-right "12px"}}]
              [button
               :label    "STOP!"
               :on-click #(reset! writing? false)
               :class    "btn-danger"]]]]])

(defn write-disk
  [path writing?]
  (println (str "*** Saving data to: " path))
  (system-write-path
    path
    "data to write to path"
    (fn [err data]
      (if err
        (do
          (println (str "*** ERROR: " err))
          ;; ***** PROCESS THE RETURNED DATA HERE
          (reset! writing? false)
          )
        (do
          (if writing?
            (do
              (println (str "*** SAVED!"))
              ;; FURTHER PROCESSING HERE IF REQUIRED
              )
            (println "*** CANCELLED!"))
          (reset! writing? false))))))


(defn test-write-disk
  []
  "Create a button to test the modal component for writing to disk"
  (let [writing? (reagent/atom false)
        mwi-file "C:\\Day8\\MWIEnhancer\\test.mwi"]
    (fn []
      [:div
       [modal-button
        :label    "2. I/O Save to disk"
        :on-click #(do
                    (reset! writing? true)
                    (write-disk mwi-file writing?))]
       (when @writing?
         [writing-disk-modal mwi-file writing?])
       ])
    ))

;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 3 - Calculating pivot totals
;; ------------------------------------------------------------------------------------

;(defn calcing-pivot-totals-modal
;  []
;  "Show this modal window when calculating pivot totals"
;  [modal-window
;   :markup [:div {:style {:max-width "200px"}}
;            [:p {:style {:text-align "center"}}
;             [:strong "Calculating pivot totals"] [:br] "Please wait..."]]])
;
;(defn calc-pivot-totals
;  [calculating?]
;  "Calculate pivot totals"
;  (println "calc-pivot-totals START")
;  (cpu-delay 500)
;  ;; ***** PROCESS THE RETURNED DATA HERE
;  (println "calc-pivot-totals END")
;  (reset! calculating? false))
;
;
;(defn test-calc-pivot-totals
;  []
;  "Create a button to test the modal component for calculating pivot totals"
;  (let [calculating? (reagent/atom false)]
;    (fn []
;      [:div
;       [modal-button
;        :label    "3. CPU-S Pivot calc"
;        :on-click #(do
;                     (reset! calculating? true)
;                     (start-cpu-intensive (fn [] (calc-pivot-totals calculating?)))
;                     )] ;; Delay call to allow modal to show
;       (when @calculating?
;         [calcing-pivot-totals-modal])])))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 4 - Processing a large in-memory XML file (chunked)
;; ------------------------------------------------------------------------------------

;(defn fib [a b] (cons a (lazy-seq (fib b (+ b a)))))
;
;(defn process-xml-modal
;  [calculating?]
;  "Show this modal window when chunking through an in memory XML file (actualy we're justing calcing fibs)"
;  [modal-window
;   :markup [:div {:style {:max-width "200px"}}
;            [:p {:style {:text-align "center"}}
;             [:strong "Processing large XML file"] [:br]
;             [:strong "(actually, just reusing fib)"] [:br]
;             "Please wait..."]
;            [spinner]
;            [cancel-button #(reset! calculating? false)]]])
;
;
;(defn process-xml
;  []
;  (let [chunks 5]
;
;    (fn [p1 p2]
;      (let [chunk-result (take chunks (fib p1 p2))
;            new-p1       (+ (last chunk-result) (last (butlast chunk-result)))
;            new-p2       (+ new-p1 (last chunk-result))
;            next-params  [new-p1 new-p2]]
;
;        (println (str "(fib " p1 " "  p2 ") = " chunk-result " next = " next-params))
;        ;; ***** PROCESS THE RETURNED DATA HERE
;        (cpu-delay 50)
;        (if (< new-p1 420196140727489660)
;          next-params
;          nil)))))
;
;
;(defn test-process-xml
;  []
;  "Create a button to test the modal component for calculating pivot totals"
;  (let [calculating? (reagent/atom false)]
;    (fn []
;      [:div
;       [modal-button
;        :label    "4. CPU-M Process XML (chunked)"
;        :on-click #(modal-multi-chunk-runner
;                    process-xml
;                    [1 1]
;                    calculating?)]
;       (when @calculating?
;         [process-xml-modal calculating?])])))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 5.2 - MWI Enhancer modifying EDN in steps (multiple fn calls, not chunked)
;;   - LOCAL ATOMS
;; ------------------------------------------------------------------------------------

;(defn mwi-step-2-1
;  []
;  (println "IN: mwi-step-2-1")
;  (cpu-delay 200)
;  true) ;; true to continue the process, false to cancel it
;
;(defn mwi-step-2-2
;  [p1 p2]
;  (println (str "IN: mwi-step-2-2. Params=" p1 "," p2))
;  (cpu-delay 300)
;  true) ;; continue
;
;(defn mwi-step-2-3
;  []
;  (println "IN: mwi-step-2-3")
;  (cpu-delay 400)
;  true) ;; continue
;
;(defn progress-msg-2 ;; msg doesn't update if it's placed directly in mwi-steps-modal-2
;  [progress-msg]
;  (println (str "progress-msg: " @progress-msg))
;  [:span @progress-msg])
;
;(defn mwi-steps-modal-2
;  [progress-msg progress-percent calculating?]
;  "Show this modal window when chunking through an in memory XML file (actualy we're justing calcing fibs)"
;  [modal-window
;   :markup [:div {:style {:max-width "500px"}}
;            [:p {:style {:text-align "center"}}
;             [:strong "Recalculating..."] [:br]
;             [:strong "Current step: "] [progress-msg-2 progress-msg]]
;            [progress-bar :model progress-percent]
;            [cancel-button #(reset! calculating? false)]]])
;
;(defn mwi-steps-2
;  []
;  (let [steps [{:fn mwi-step-2-1                   :msg "2. Performing step 1" :percent 0}
;               {:fn mwi-step-2-2 :params [1 "two"] :msg "2. Performing step 2" :percent 33}
;               {:fn mwi-step-2-3                   :msg "2. Performing step 3" :percent 67}]]
;
;    (fn [step-to-process progress-msg progress-percent ui-updated?]
;      (let [this-step (get steps step-to-process)]
;        (if-not @ui-updated?
;          (do
;            (swap! ui-updated? not)
;            (println (str "mwi-steps-2 UPDATE-UI step " step-to-process ": " (:msg this-step)))
;            (reset! progress-msg (:msg this-step))
;            (reset! progress-percent (:percent this-step))
;            [step-to-process progress-msg progress-percent ui-updated?]) ;; Go again, run SAME step
;          (do
;            (swap! ui-updated? not)
;            (println (str "mwi-steps-2 step " step-to-process ": " (:msg this-step)))
;            (let [step-result (apply (:fn this-step) (:params this-step))]
;              (if (and step-result (< step-to-process (dec (count steps))))
;                [(inc step-to-process) progress-msg progress-percent ui-updated?] ;; Go again, run next step
;                nil))))))))
;
;(defn test-mwi-steps-2
;  []
;  "Create a button to test the modal component for calculating multiple mwi steps"
;  (let [calculating?     (reagent/atom false)
;        progress-msg     (reagent/atom "")
;        progress-percent (reagent/atom 0)
;        ui-updated?      (atom false)]
;    (fn []
;      [:div
;       [modal-button
;        :label    "5.1. CPU-M Modify EDN in steps (unchunked)"
;        :on-click #(modal-multi-chunk-runner
;                    mwi-steps-2
;                    [0 progress-msg progress-percent ui-updated?]
;                    calculating?)]
;       (when @calculating?
;         [mwi-steps-modal-2
;          progress-msg
;          progress-percent
;          calculating?])])))


;; ------------------------------------------------------------------------------------
;;  core.async tests
;; ------------------------------------------------------------------------------------


(defn cancellable-step
  [continue? func]
  (fn [val]
    (if @continue?
      (func val)
      [false val])))


(defn fib-step
  [{:keys [step-num] :as params}]
  (cpu-delay step-num)
  [(< step-num 10)
   (assoc params :step-num (inc step-num))])


(defn test-core-async-looper
  []
  "Create a button to test looper"
  (let [calculating? (reagent/atom false)
        progress-percent (reagent/atom 0)]
    (fn []
      [:div
       [modal-button
        :label    "5.2. looper"
        ;#(looper {:params [1 1] :results []} fib-step calculating?)]
        :on-click #(do
                    (reset! calculating? true)
                    (looper
                      :initial-value {:params [1 1] :results []}
                      :func          (cancellable-step calculating? fib-step)
                      :when-done     (fn [] (reset! calculating? false)))
                    (println "FINISHED!!!!"))]
       (when @calculating?
         [modal-window
          :markup [:div {:style {:width "300px"}}
                   [:p {:style {:text-align "center"}}
                    [:strong "Calculating fibonacci numbers..."]]
                   [spinner]
                   [cancel-button #(reset! calculating? false)]]])])))


;; ------------------------------------------------------------------------------------


(defn do-stuff
  [payload]
  (println (str "do-stuff - payload = " payload))
  (cpu-delay 200)
  (assoc payload :do-stuff-result 100))

(defn do-more-stuff
  [payload]
  (println (str "do-more-stuff - payload = " payload))
  (cpu-delay 300)
  (assoc payload :do-more-stuff-result 200))

(defn do-even-more-stuff
  [payload]
  (println (str "do-even-more-stuff - payload = " payload))
  (cpu-delay 400)
  (assoc payload :do-even-more-stuff-result 300 :a 99 :b 99))

(defn do-processing
  [calculating? progress-msg progress-percent]
  (let [set-atoms (fn [msg percent]
                    (reset! progress-msg     msg)
                    (reset! progress-percent percent)
                    nil)]
    (reset! calculating? true)
    (domino-process
      {:a 1 :b 2}
      calculating?
      [#(set-atoms "Performing step 1" 0)
       do-stuff
       #(set-atoms "Performing step 2" 33)
       do-more-stuff
       #(set-atoms "Performing step 3" 67)
       do-even-more-stuff
       ;#(set-atoms "Performing step 4" 80)
       ;#(looper {:params [1 1] :results []} fib-step calculating?)
       ;#(set-atoms "Performing step 5" 90)
       ;#(looper {:params [1 1] :results []} fib-step calculating?)
       #(set-atoms "Finished" 100)
       #(do
         (reset! calculating? false)
         (println (str "RESULT = " %1))
         nil)])))

#_(defn do-processing
  [calculating? progress-msg progress-percent]
  (reset! calculating? true)
  (reset! progress-msg "Performing step 1")
  (reset! progress-percent 0)
  (go (let [pause  (<! (timeout 20))
            result (do-stuff {:a 1 :b 2})
            pause  (<! (timeout 20))]
        (when @calculating?
          (reset! progress-msg "Performing step 2")
          (reset! progress-percent 33)
          (let [pause  (<! (timeout 20))
                result (do-more-stuff result)
                pause  (<! (timeout 20))]
            (when @calculating?
              (reset! progress-msg "Performing step 3")
              (reset! progress-percent 67)
              (let [pause  (<! (timeout 20))
                    result (do-even-more-stuff result)]
                (println (str "RESULT = " result))
                (reset! calculating? false))))))))

(defn test-core-async
  []
  "Create a button to test the core.async version"
  (let [calculating? (reagent/atom false)
        progress-msg     (reagent/atom "{initial-value}")
        progress-percent (reagent/atom 0)]
    (fn []
      [:div
       [modal-button
        :label    "5.3. domino-process"
        :on-click #(do-processing calculating? progress-msg progress-percent)]
       (when @calculating?
         [modal-window
          :markup [:div {:style {:width "300px"}}
                   [:p {:style {:text-align "center"}}
                    [:strong "Recalculating..."] [:br]
                    [:strong "Current step: "] [(fn [] [:span @progress-msg])]]
                   [progress-bar
                    :model progress-percent]
                   [cancel-button #(reset! calculating? false)]]])])))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 6 - Creating large JSON data for writing (chunked), then writing (a type A I/O job).
;; ------------------------------------------------------------------------------------


;(def chunked-json-progress-msg     (reagent/atom ""))
;(def chunked-json-progress-percent (reagent/atom 0))
;
;(defn chunked-xml-in-sequence
;  []
;  (let [chunks 5]
;
;    (fn [p1 p2]
;      (let [chunk-result (take chunks (fib p1 p2))
;            new-p1       (+ (last chunk-result) (last (butlast chunk-result)))
;            new-p2       (+ new-p1 (last chunk-result))
;            next-params  [new-p1 new-p2]]
;
;        (println (str "(fib " p1 " "  p2 ") = " chunk-result " next = " next-params))
;        ;; ***** PROCESS THE RETURNED DATA HERE
;        (cpu-delay 50)
;        (if (< new-p1 420196140727489660)
;          next-params
;          nil)))
;    ))
;
;(defn write-disk-in-sequence
;  [path writing?]
;  (println (str "*** Saving data to: " path))
;  (system-write-path
;   path
;   "data to write to path"
;   (fn [err data]
;     (if err
;       (do
;         (println (str "*** ERROR: " err))
;         ;; ***** PROCESS THE RETURNED DATA HERE
;         (reset! writing? false)
;         )
;       (do
;         (if writing?
;           (do
;             (println (str "*** SAVED!"))
;             ;; FURTHER PROCESSING HERE IF REQUIRED
;             )
;           (println "*** CANCELLED!"))
;         (reset! writing? false))))))
;
;(defn update-chunked-json-ui
;  [msg percent]
;  (println (str "update-chunked-json-ui: " msg))
;  (reset! chunked-json-progress-msg msg)
;  (reset! chunked-json-progress-percent percent)
;  true) ;; continue
;
;(defn chunked-json-modal
;  [progress-msg progress-percent calculating?]
;  "Show this modal window when chunking through an in memory XML file (actualy we're justing calcing fibs)"
;  [modal-window
;   :markup [:div {:style {:max-width "500px"}}
;            [:p {:style {:text-align "center"}}
;             [:strong "Recalculating..."] [:br]
;             [:strong "Current step: "] [progress-msg progress-msg]]
;            [progress-bar :model progress-percent]
;            [cancel-button #(reset! calculating? false)]]])
;
;(defn chunked-json
;  []
;  (let [mwi-file "C:\\Day8\\MWIEnhancer\\test.mwi"
;        running? (reagent/atom false) ;; TODO!
;        steps    [{:fn update-chunked-json-ui   :params ["1. Creating JSON " 0]}
;                  {:fn modal-multi-chunk-runner :params [chunked-xml-in-sequence [1 1] running?]} ;; TODO: running?
;                  {:fn update-chunked-json-ui   :params ["2. Writing JSON" 50]}
;                  {:fn write-disk-in-sequence   :params [mwi-file running?]}]] ;; TODO: running?
;
;    (fn [step-to-process]
;      (let [this-step (get steps step-to-process)]
;        (println (str "chunked-json step " step-to-process ": " (:msg this-step)))
;        (let [step-result ((:fn this-step) (:params this-step))]
;          (if (and step-result (< step-to-process (dec (count steps))))
;            (inc step-to-process)
;            nil))))))
;
;(defn test-chunked-json
;  []
;  "Create a button to test the modal component for calculating multiple mwi steps
;  "
;  (let [calculating? (reagent/atom false)]
;    (fn []
;      [:div
;       [modal-button
;        :label    "[TODO] 6. CPU-M Create JSON (chunked), write to disk"
;        :on-click #(modal-multi-chunk-runner  chunked-json [0] calculating?)]
;       (when @calculating?
;         [chunked-json-modal
;          chunked-json-progress-msg
;          chunked-json-progress-percent
;          calculating?])])))


;; ------------------------------------------------------------------------------------
;;  MODAL PROCESSING USE CASE 7 - Arbitrarily complex input form
;; ------------------------------------------------------------------------------------

(defn test-form-markup
  [form-data process-ok process-cancel]
  [:div {:style {:padding "5px" :background-color "cornsilk" :border "1px solid #eee"}} ;; [:form {:name "pform" :on-submit ptest-form-submit}
   [:h3 "Welcome to MWI Enhancer"]
   [:div.form-group
    [:label {:for "pf-email"} "Email address"]
    [:input#pf-email.form-control
     {:name        "email"
      :type        "text"
      :placeholder "Enter email"
      :style       {:width "250px"}
      :value       (:email @form-data)
      :on-change   #(swap! form-data assoc :email (-> % .-target .-value))}]
    ]
   [:div.form-group
    [:label {:for "pf-password"} "Password"]
    [:input#pf-password.form-control
     {:name        "password"
      :type        "password"
      :placeholder "Enter password"
      :style       {:width "250px"}
      :value       (:password @form-data)
      :on-change   #(swap! form-data assoc :password (-> % .-target .-value))}]
    ]
   [:div.checkbox
    [:label
     [:input
      {:name      "remember-me"
       :type      "checkbox"
       :checked   (:remember-me @form-data)
       :on-change #(swap! form-data assoc :remember-me (-> % .-target .-checked))}
      "Remember me"]]]
   [:hr {:style {:margin "10px 0 10px"}}]
   [button
    :label    "Sign in"
    :on-click process-ok
    :class "btn-primary"]
   [:span " "]
   [button
    :label    "Cancel"
    :on-click process-cancel]])

(defn test-modal-dialog
  []
  "Create a button to test the modal component for modal dialogs"
  (let [showing?       (reagent/atom false)
        form-data      (reagent/atom {:email       "gregg.ramsey@day8.com.au"
                                      :password    "abc123"
                                      :remember-me true})
        save-form-data (reagent/atom nil)
        process-ok     (fn [event]
                         (reset! showing? false)
                         (println "Submitted form data: " @form-data)
                         ;; ***** PROCESS THE RETURNED DATA HERE
                         false) ;; Prevent default "GET" form submission (if used)
        process-cancel (fn [event]
                         (reset! form-data @save-form-data)
                         (reset! showing? false)
                         (println "Cancelled form data: " @form-data)
                         false)]
    (fn []
      [:div
       [modal-button
        :label    "7. Modal Dialog"
        :on-click #(do
                    (reset! save-form-data @form-data)
                    (reset! showing? true))]
       (when @showing? [modal-window
                        :markup [test-form-markup
                                 form-data
                                 process-ok
                                 process-cancel]])])))


(defn add-alert
  [alerts alerts-count alert-type {:keys [heading body]}]
  (let [id (swap! alerts-count inc)]
    (swap! alerts assoc id {:alert-type alert-type :heading heading :body body :padding "8px" :closeable true})))


;; ------------------------------------------------------------------------------------
;;  TEST HARNESS MAIN
;; ------------------------------------------------------------------------------------

(defn test-harness-func
  []
  (let [alerts (reagent/atom (sorted-map-by >))
        alerts-count (reagent/atom 0)]
    (add-alert alerts alerts-count "danger" {:heading "Unfortunately something bad happened" :body "Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care! Next time you should take more care!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})
    (add-alert alerts alerts-count "warning" {:heading "Hmmm, something might go wrong" :body "There be dragons!"})
    (add-alert alerts alerts-count "info" {:heading "Here's some info" :body "The rain in Spain falls mainly on the plain"})

    [:div.panel.panel-default {:style {:margin "8px"}}
     [:div.panel-body

      ;; Alert box list (popover :below-center)

      [popover
       :position :right-below
       :showing? show-alert-popover?
       :anchor [alert-list
                :alerts       alerts
                :on-close     #(swap! alerts dissoc %)
                :border-style "1px dashed lightgrey"]
       :popover {:width         300
                 :title         [:strong "Product Tour (1 of 5)"]
                 :close-button? true
                 :body          [:div "Welcome to the sample tour. Use the buttons below to move through the tour."
                                 [:hr {:style {:margin "10px 0 10px"}}]
                                 [button
                                  :label    "Next"
                                  :on-click #(do (reset! show-alert-popover? false) (reset! show-but5-popover? true))]
                                 ]}]


      [:div {:style {:margin-top "40px"}}]

      [:div {:style {:display "flex" :flex-flow "row"}}     ;; Flexbox button bar wrapper

                                                            ;; Button #2 - :above-right
       [popover
        :position :above-right
        :showing? show-but2-popover?
        :anchor [button
                 :label    ":above-right"
                 :on-click #(reset! show-but2-popover? (not @show-but2-popover?))
                 :class    "btn-success"]
        :popover {:body "Popover body without a title. Basically a tooltip"}
        :options {:arrow-width 33}]

                                                            ;; Button #3 - :left-above
       [popover
        :position :left-above
        :showing? show-but3-popover?
        :anchor [button
                 :label    ":left-above"
                 :on-click #(reset! show-but3-popover? (not @show-but3-popover?))
                 :class "btn-success"]
        :popover {:width 150
                  :title "Popover Title"
                  :body  "Popover body. Can be a simple string or in-line hiccup or a function returning hiccup"}]

                                                            ;; Popover form demo - :right-below
       [popover-form-demo/popover-form-demo]

                                                            ;; Button #4 - :below-left
       [popover
        :position :below-left
        :showing? show-but4-popover?
        :anchor [button
                 :label    ":below-left"
                 :on-click #(reset! show-but4-popover? (not @show-but4-popover?))
                 :class "btn-success"]
        :popover {:title "Popover Title"
                  :body  "Popover body. Can be a simple string or in-line hiccup or a function returning hiccup"}]

                                                            ;; Button #5 - :left-center
       [popover
        :position :left-center
        :showing? show-but5-popover?
        :anchor [button
                 :label    ":left-center"
                 :on-click #(reset! show-but5-popover? (not @show-but5-popover?))
                 :class "btn-success"]
        :popover {:title         [:strong "Product Tour (2 of 5)"]
                  :close-button? true
                  :body          [:div "This is a button you can click to show or hide this popover, but it's also part of the tour so you can click the buttons below to move through it."
                                  [:hr {:style {:margin "10px 0 10px"}}]
                                  [button
                                   :label    "Previous"
                                   :on-click #(do (reset! show-but5-popover? false) (reset! show-alert-popover? true))
                                   :style {:margin-right "15px"}] ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                                  [button
                                   :label    "Next"
                                   :on-click #(do (reset! show-but5-popover? false) (reset! show-red-popover? true))
                                   :style {:margin-right "15px"}]]} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
        :options {:backdrop-callback #(reset! show-but5-popover? false)
                  :backdrop-opacity  0.3}]


                                                            ;; Button popovers

       [popover
        :position :above-left
        :showing? show-but6-popover?
        :anchor [make-button
                 :showing? show-but6-popover?
                 :type     "info"
                 :label    ":above-left"]
        :popover {:title [:strong "BUTTON Popover Title"]
                  :body  "This was created using a call to create-button-popover"}]

       [popover
        :position :left-below
        :showing? show-but7-popover?
        :anchor [make-button
                 :showing? show-but7-popover?
                 :type     "default"
                 :label    ":left-below"]
        :popover {:title [:strong "BUTTON Popover Title"]
                  :body  "This is another button created using a call to create-button-popover"}]

       [popover
        :position :below-right
        :showing? show-but8-popover?
        :anchor [make-button
                 :showing? show-but8-popover?
                 :type     "link"
                 :label    ":below-right"]
        :popover {:title [:strong "BUTTON Popover Title"]
                  :body  "This is another button created using a call to create-button-popover"}]

       ]                                                    ;; End of flexbox button bar wrapper


      ;; Link popovers

      [:div {:style {:margin-top "1em" :display "flex"}}
       "Here is a FLEX div with text, and then here is a call to "
       [popover
        :position :above-center
        :showing? show-link1-popover?
        :anchor   [make-link
                   :showing?      show-link1-popover?
                   :toggle-on     :mouse
                   :label         "create-link-popover"]
        :popover  {:title         [:strong "LINK Popover Title"]
                   :close-button? false
                   :body          "This is the body of the link popover. This is the body of the link popover.
                                   This is the body of the link popover. This is the body of the link popover."}]
       " with " [:strong "mouseover/mouseout"] " used to show/hide the popover. "]

      [:div {:style {:margin-top "1em"}}
       "Here is a STANDARD div, and then here is a call to "
       [popover
        :position :above-center
        :showing? show-link2-popover?
        :anchor   [make-link
                   :showing?  show-link2-popover?
                   :toggle-on :click
                   :label     "create-link-popover"]
        :popover  {:title     [:strong "LINK Popover Title"]
                   :body      "This is the body of the link popover. This is the body of the link popover.
                               This is the body of the link popover. This is the body of the link popover."}]
       " with " [:strong "click"] " used to show/hide the popover. "]


      ;; Red, green, blue rectangles

      [:div {:style {:margin-top "20px"}}
       [:div {:style {:display "flex" :flex-flow "row" :align-items "center"}}
        [popover
         :position :above-center
         :showing? show-red-popover?
         :anchor [:div {:style {:background-color "red" :display "block" :width "200px" :height "100px" :margin "0 20px 0 20px"}}]
         :popover {:title         [:strong "Product Tour (3 of 5)"]
                   :close-button? true
                   :body          [:div "Here is a lovely red rectangle. It's a great warm colour and perfect for Winter."
                                   [:hr {:style {:margin "10px 0 10px"}}]
                                   [button
                                    :label    "Previous"
                                    :on-click #(do (reset! show-red-popover? false) (reset! show-but5-popover? true))
                                    :style {:margin-right "15px"}] ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                                   [button
                                    :label    "Next"
                                    :on-click #(do (reset! show-red-popover? false) (reset! show-green-popover? true))
                                    :style {:margin-right "15px"}]]} ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
         :options {:backdrop-callback #(reset! show-red-popover? false)
                   :backdrop-opacity  0.3}]

        [popover
         :position :below-center
         :showing? show-green-popover?
         :anchor [:div {:style {:background-color "green" :display "block" :width "200px" :height "150px" :margin "0 20px 0 20px"}}]
         :popover {:title         [:strong "Product Tour (4 of 5)"]
                   :close-button? true
                   :body          [:div "And now we move onto the green rectangle. Feels like Spring to me."
                                   [:hr {:style {:margin "10px 0 10px"}}]
                                   [button
                                    :label    "Previous"
                                    :on-click #(do (reset! show-green-popover? false) (reset! show-red-popover? true))
                                    :style {:margin-right "15px"}] ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                                   [button
                                    :label    "Next"
                                    :on-click #(do (reset! show-green-popover? false) (reset! show-blue-popover? true))
                                    :style    {:margin-right "15px"}]]}] ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"


        [popover
         :position :right-below
         :showing? show-blue-popover?
         :anchor [:div {:style    {:background-color "blue" :color "white" :cursor "pointer" :text-align "center" :display "block" :width "300px" :height "100px" :margin "0 0 0 20px"}
                        :on-click #(reset! show-alert-popover? true)} "CLICK HERE TO RESTART TOUR"]
         :popover {:title         [:strong "Product Tour (5 of 5)"]
                   :close-button? true
                   :body          [:div "Finally the blue rectagle. Summer at the beach, right?"
                                   [:hr {:style {:margin "10px 0 10px"}}]
                                   [button
                                    :label    "Previous"
                                    :on-click #(do (reset! show-blue-popover? false) (reset! show-green-popover? true))
                                    :style    {:margin-right "15px"}] ;; :flex-grow 0 :flex-shrink 1 :flex-basis "auto"
                                   [button
                                    :label    "Finish"
                                    :on-click #(reset! show-blue-popover? false)
                                    :style    {:margin-right "15px"}]]}]
        ]]


      ;; Tour component

      [:div {:style {:display "flex" :flex-flow "row" :margin-top "20px" :margin-left "20px"}} ;; Tour/modal wrapper
       [:h4 {:style {:margin-right "20px"}} "Here is a sample of the new tour component:"]

       [popover
        :position :above-center
        :showing? (:step1 demo-tour)
        :anchor [button
                 :label    "Start Tour!"
                 :on-click #(start-tour demo-tour)
                 :style    {:font-weight "bold" :color "yellow"}
                 :class    "btn-info"]
        :popover {:title         [:strong "Tour 1 of 4"]
                  :close-button? true
                  :body          [:div "So this is the first tour popover"
                                  [make-tour-nav demo-tour]]}]

       [popover
        :position :above-center
        :showing? (:step2 demo-tour)
        :anchor [make-button
                 :showing? (:step2 demo-tour)
                 :type     "info"
                 :label    "Tour 2"]
        :popover {:title         [:strong "Tour 2 of 4"]
                  :close-button? true
                  :body          [:div "And this is the second tour popover"
                                  [make-tour-nav demo-tour]]}]

       [popover
        :position :above-center
        :showing? (:step3 demo-tour)
        :anchor [make-button
                 :showing? (:step3 demo-tour)
                 :type     "info"
                 :label    "Tour 3"]
        :popover {:title         [:strong "Tour 3 of 4"]
                  :close-button? true
                  :body          [:div "Penultimate tour popover"
                                  [make-tour-nav demo-tour]]}]

       [popover
        :position :above-right
        :showing? (:step4 demo-tour)
        :anchor [make-button
                 :showing? (:step4 demo-tour)
                 :type     "info"
                 :label    "Tour 4"]
        :popover {:title         [:strong "Tour 4 of 4"]
                  :close-button? true
                  :body          [:div "Lucky last tour popover"
                                  [make-tour-nav demo-tour]]}]

       ]                                                    ;; End of tour wrapper


      ;; Modal component

      [:div.container
       {:style {:width "100%" :margin-top "20px" :margin-left "0px" :margin-right "0px"}}
       [:div.row
        [:div.col-xs-2
         {:style {:text-align "right"}}
         [:h4 "Modal demos:"]]
        [::div.col-xs-10
         [:div {:style {:display "flex" :flex-flow "row" :flex-wrap "wrap"}} ;; Modal wrapper

                                                                             ;; MODAL - LONG

          [popover
           :position :right-center
           :showing? show-modal-popover?
           :anchor   [:input.btn.btn-info
                      {:style         {:font-weight "bold" :color "red" :margin "1px" :height "39px"}
                       :type          "button"
                       :value         "Long"
                       :on-mouse-over #(reset! show-modal-popover? true)
                       :on-mouse-out  #(reset! show-modal-popover? false)
                       :on-click      #()
                       #_#(chunk-runner
                         serious-process-1-chunk
                         serious-process-1-status
                         250)}]
           :popover  {:body  [:div
                              [:p "Click on this button to launch a modal demo. The demo will start an intensive operation and..."]
                              [:p "It will have a progress bar which looks something like this:"]
                              [:div.progress
                               [:div.progress-bar
                                {:role  "progressbar"
                                 :style {:width "60%"}}
                                "60%"]]]
                      :width 300}]

          ;;  TODO: add back in a dialog
          ;; Test the use cases
          [test-load-url]                                   ;; 1 - Loading URL
          [test-write-disk]                                 ;; 2 - Writing to disk
          ;; [test-calc-pivot-totals] ;; 3 - Calculating pivot totals
          ;; [test-process-xml]       ;; 4 - Processing a large in-memory XML file (chunked)
          ;; [test-mwi-steps-2]       ;; 5.X - MWI Enhancer modifying EDN in steps (multiple fn calls, not chunked)
          [test-core-async-looper]                          ;; 5.2 - core.async looper
          [test-core-async]                                 ;; 5.3 - core.async version of this
          ;; [test-chunked-json]      ;; 6 - Creating large JSON data for writing (chunked), then writing (a type A I/O job).
          [test-modal-dialog]                               ;; 7 - Arbitrarily complex input form

          ]                                                 ;; End of modal wrapper
         ]]]                                                ;; End of container/row



      ;; Orange square - :right-center - no flex stuff added yet so doesn't work properly

      [popover
       :position :right-center
       :showing? show-div-popover?
       :anchor [:div {:style         {:background-color "coral"
                                      :display          "block"
                                      :margin-top       "20px"
                                      :width            "200px"
                                      :height           "200px"}
                      :on-mouse-over #(reset! show-div-popover? true)
                      :on-mouse-out  #(reset! show-div-popover? false)
                      :on-click      #(reset! show-div-popover? (not @show-div-popover?))}]
       :popover {:title "Rollover Popover"
                 :body  "This is basically a tooltip."}]
      ]]))
