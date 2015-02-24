(ns re-demo.modals
  (:require-macros [re-com.core            :refer [handler-fn]]
                   [cljs.core.async.macros :refer [go]])
  (:require [re-com.core     :refer [label spinner progress-bar]]
            [re-com.buttons  :refer [button]]
            [re-com.box      :refer [h-box v-box box gap]]
            [re-com.dropdown :refer [single-dropdown]]
            [re-com.popover  :refer [popover-content-wrapper popover-anchor-wrapper]]
            [re-com.modal    :refer [modal-window cancel-button looper domino-process]]
            [cljs.core.async :refer [<! >! chan close! put! take! alts! timeout]]
            [re-demo.utils   :refer [panel-title component-title]]
            [reagent.core    :as    reagent]))


(def demos [{:id 1 :label "Basic example"}
            {:id 2 :label "Other variations"}])

(def progress-percent (reagent/atom 0))                     ;; TODO: Remove this dirty global!

(defn cpu-delay
  [num-times]
  (let [waste-time  #(Math.tan (Math.tan (+ %1 %2)))         ;; tan is a time consuming operation
        iterations  (if (nil? num-times) 10 num-times)]
    (println "starting cpu-delay")
    (dotimes [n iterations]
      ;(doall (reduce waste-time n (range 1000000)))
      (reduce waste-time n (range 1000))
      (println "finished cpu-delay"))))



(defn modal-button
  "Render a button to launch a modal window"
  [& {:keys [label on-click]}]
  [button
   :label    label
   :on-click on-click
   :style {:margin "1px" :height "39px"}
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
  "Simulate the system's load-url functionality (will call back in 3 seconds)."
  [url callback]
  (let [err  nil
        data "<?xml version=\"1.0\" encoding=\"UTF-8\"?><data>here it is!</data>"]
    (js/setTimeout #(callback err data) 3000)))

;; ------------------------------------------------------------------------------------

(defn loading-url-modal
  "Show this modal window when loading a url"
  [url loading?]
  [modal-window
   :markup [:div {:style {:max-width "600px"}}
            [:p (str "Loading data from '" url "'...")]
            [spinner]
            [cancel-button #(reset! loading? false)]]])


(defn load-url
  "Load some data from the remote server"
  [url loading?]
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
  "Create a button to test the modal component for loading a url"
  []
  (let [loading? (reagent/atom false)
        url      "http://www.w3schools.com/xml/cd_catalog.xml"]
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
  "Simulate the system's load-url functionality (will call back in 3 seconds)."
  [path data callback]
  (let [err  "The file could not be saved - Disk Full!"
        data nil]
    (js/setTimeout #(callback err data) 3000)))

;; ------------------------------------------------------------------------------------

(defn writing-disk-modal
  "Show this modal window when saving to disk"
  [path writing?]
  [modal-window
   :markup [:div {:style {:max-width "600px"}}
            [:p (str "Saving '" path "'...")]
            [:div {:style {:display "flex"}}
             [:div {:style {:margin "auto"}}
              [:img {:src "resources/img/spinner.gif" :style {:margin-right "12px"}}]
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
  "Create a button to test the modal component for writing to disk"
  []
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
  "Create a button to test looper"
  []
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
  "Create a button to test the core.async version"
  []
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
      :on-change   (handler-fn (swap! form-data assoc :email (-> event .-target .-value)))}]
    ]
   [:div.form-group
    [:label {:for "pf-password"} "Password"]
    [:input#pf-password.form-control
     {:name        "password"
      :type        "password"
      :placeholder "Enter password"
      :style       {:width "250px"}
      :value       (:password @form-data)
      :on-change   (handler-fn (swap! form-data assoc :password (-> event .-target .-value)))}]
    ]
   [:div.checkbox
    [:label
     [:input
      {:name      "remember-me"
       :type      "checkbox"
       :checked   (:remember-me @form-data)
       :on-change (handler-fn (swap! form-data assoc :remember-me (-> event .-target .-checked)))}
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
  "Create a button to test the modal component for modal dialogs"
  []
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



(defn demo1
  []
  (let [show-modal-popover? (reagent/atom false)]
    [:div.container
     {:style {:width "100%" :margin-top "20px" :margin-left "0px" :margin-right "0px"}}
     [:div.row
      [::div.col-xs-10
       [:div {:style {:display "flex" :flex-flow "row" :flex-wrap "wrap"}} ;; Modal wrapper

        [popover-anchor-wrapper
         :showing? show-modal-popover?
         :position :left-center
         :anchor   [:input.btn.btn-info
                    {:style         {:margin "1px" :height "39px"}
                     :type          "button"
                     :value         "Long"
                     :on-mouse-over (handler-fn (reset! show-modal-popover? true))
                     :on-mouse-out  (handler-fn (reset! show-modal-popover? false))
                     :on-click      (handler-fn (cpu-delay 10))
                     #_#(chunk-runner
                       serious-process-1-chunk
                       serious-process-1-status
                       250)}]
         :popover  [popover-content-wrapper
                    :showing?         show-modal-popover?
                    :position         :left-center
                    :width            "300px"
                    :body             [:div
                                       [:p "Click on this button to launch a modal demo. The demo will start an intensive operation and..."]
                                       [:p "It will have a progress bar which looks something like this:"]
                                       [:div.progress
                                        [:div.progress-bar
                                         {:role  "progressbar"
                                          :style {:width "60%"}}
                                         "60%"]]]]]
        [test-load-url]          ;; 1 - Loading URL
        [test-write-disk]        ;; 2 - Writing to disk
        ;[test-calc-pivot-totals] ;; 3 - Calculating pivot totals
        ;[test-process-xml]       ;; 4 - Processing a large in-memory XML file (chunked)
        ;[test-mwi-steps-2]       ;; 5.X - MWI Enhancer modifying EDN in steps (multiple fn calls, not chunked)
        [test-core-async-looper] ;; 5.2 - core.async looper
        [test-core-async]        ;; 5.3 - core.async version of this
        ;[test-chunked-json]      ;; 6 - Creating large JSON data for writing (chunked), then writing (a type A I/O job).
        [test-modal-dialog]      ;; 7 - Arbitrarily complex input form
        ]]]]))


(defn demo2
  []
  [:span "*** TODO ***"])


(defn notes
  []
  [v-box
   :width    "500px"
   :children [[:div.h4 "General notes"]
              [:ul
               [:li "*** TODO ***"]]]])


(defn panel2
  []
  (let [selected-demo-id (reagent/atom 1)]
    (fn []
      [v-box
       :children [[panel-title "Modal Components"]
                  [h-box
                   :gap      "50px"
                   :children [[notes]
                              [v-box
                               :gap       "15px"
                               :size      "auto"
                               :min-width "500px"
                               :margin    "20px 0px 0px 0px"
                               :children  [[h-box
                                            :gap      "10px"
                                            :align    :center
                                            :children [[label :label "Select a demo"]
                                                       [single-dropdown
                                                        :choices   demos
                                                        :model     selected-demo-id
                                                        :width     "300px"
                                                        :on-change #(reset! selected-demo-id %)]]]
                                           [gap :size "0px"] ;; Force a bit more space here
                                           (case @selected-demo-id
                                             1 [demo1]
                                             2 [demo2])]]]]]])))


(defn panel   ;; Only required for Reagent to update panel2 when figwheel pushes changes to the browser
  []
  [panel2])
