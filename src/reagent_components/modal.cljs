(ns reagent-components.modal
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent-components.util  :as util]
            ;; [cljs.core.async          :as async :refer [<! >! chan close! sliding-buffer put! alts!]]
            [reagent-components.alert :refer [closeable-alert]]
            [reagent.core             :as reagent]
            [goog.events              :as events]))


;; MODAL COMPONENT
;;
;; Use cases
;;  A. I/O work in background
;;      - EASIEST, single I/O action with callback(s).
;;      - User feedback and interaction (e.g. Cancel buttons) is easy
;;      - Use cases:
;;         1. Loading URL (succeeds)
;;             - Could fire off multiple (asynchronous) requests.
;;             - Task is :finished once the final request is completed.
;;         2. Writing to disk (fails!)
;;             - Only a single "thread" of activity.
;;             - Synchronous or asynchronous?
;;
;;  B. Long running/CPU intensive
;;      - User feedback and interaction is a CHALLENGE. We are saturating the JS Event loop.
;;
;;      - SINGLE chunk
;;        - Just need GUI to be locked while we do something.
;;        - Ideally get work done in user acceptable time-frame.
;;        - No need for user interaction (not possible anyway). i.e. NO spinner/progress/cancel
;;        - Use cases:
;;           3. Calculating pivot totals
;;
;;      - MULTI chunks
;;        - MOST complex case
;;        - Too much time taken for a single chunk.
;;        - We chunk the work so we hand back control to the event loop to update UI
;;          and process mouse/keyboard events.
;;        - NEED user interaction. i.e. one or more of spinner/progress/cancel.
;;        - Two types of MULTI:
;;           a. Incrementally processing a single job:
;;               - Break process into multiple fn calls (chunks) and schedule them one after the
;;                 other with a short gap inbetween.
;;               - Pass current progress state to each successive call.
;;               - Use cases:
;;                  4. Processing a large in-memory XML file (chunked).
;;           b. Calling a sequence of jobs which do different processes.
;;               - Associated with each job would be status text which would display on modal.
;;               - Each individual job could potentially be chunked.
;;               - Cancel button would only work between jobs unless each job chunked.
;;               - Spinner/progress performance would be poor unless each job chunked.
;;               - Use cases:
;;                  5. MWI Enhancer modifying EDN in steps (multiple fn calls, not chunked).
;;                      -
;;                  6. Creating large JSON data for writing (chunked), then writing (a type A I/O job).
;;                      -
;;
;;  C. Nothing happening in background
;;      - User interaction required, isolated from main screen.
;;      - Use cases:
;;         7. Arbitrarily complex input form
;;
;;  D. Errors
;;      - What to do when errors occur during modal processes.
;;      - Use cases:
;;         8. Display an alert box right inside the modal.
;;         9. Pass the error back to the caller.
;;
;; TODO:
;;  - Why doesn't GIF animate?
;;  - Would BS animated progress bar work?
;;  - Alternatives?
;;  - Gobble up backdrop clicks so they don't go to the main window
;;  - Possibly get rid of dependency on alert (unless we will ALWAYS have all components included)


(defn make-cancel-button [status type text]
  [:div {:style {:display "flex"}}
   [:input.btn
    {:class (str "btn-" type)
     :type "button"
     :value text
     :style {:margin "auto"}
     :on-click #(reset! status :cancelled)
     }]]
  )


(defn make-spinner []
  [:div {:style {:display "flex"
                 :margin "10px"}}
   [:img {:src "img/spinner.gif"
          :style {:margin "auto"}}]])


(defn make-progress-bar [progress-percent]
  [:div.progress
   [:div.progress-bar ;;.progress-bar-striped.active
    {:role "progressbar"
     :style {:width (str @progress-percent "%")
             :transition "none"}} ;; Default BS transitions cause the progress bar to lag behind
    (str @progress-percent "%")]])


;; CORE.ASYNC REMOVED
;; (defn listen [el type]
;;   (let [out (chan)]
;;     (events/listen el type (fn [e] (put! out e)))
;;     out))

(def modal-alert (reagent/atom nil))

(defn show-modal-window [markup status modal-options progress-percent]
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
  main window to prevent UI interactivity and place user focus on the modal window.
  Parameters:
  - markup            The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)
  - status            [optional] The atom used to indicate the process is running (:running, :finished, :cancelled) and that
  .                   the modal should be shown. Only required when :cancel-button specified.
  - modal-options     [optional] A map containing two options:
  .                    - :spinner        A boolean indicating whether to show a spinner or not
  .                    - :progress-bar   A boolean indicating whether to show a progress bar or not
  .                    - :cancel-button  A boolean indicating whether to show a cancel button or not
  - progress-percent  [optional] The integer atom used to hold the percentage through the process we are. Used in rendering the progress bar
  "
  (let [{:keys [spinner progress-bar cancel-button]} modal-options
        ;; cancel-id      (gensym "cancel-") ;; CORE.ASYNC REMOVED
        ]

    ;; (util/console-log "In show-modal-window")
    (reagent/create-class
     {
      :component-did-mount
      (fn []
        ;; CORE.ASYNC REMOVED
        ;; (when cancel-button
        ;;   (let [elem   (util/get-element-by-id cancel-id)
        ;;         clicks (listen elem "click")]
        ;;     (util/console-log (str "show-modal-window :component-did-mount - " (.-value elem)))
        ;;     (go (while true
        ;;           (<! clicks)
        ;;           (util/console-log "CANCEL CLICKED")
        ;;           (reset! status :cancelled false)
        ;;           ))))
        )

      :render
      (fn []
        ;; (util/console-log "show-modal-window :render")
        (if (= @status :running)
          [:div
           {:style {:display "flex"      ;; Semi-transparent backdrop
                    :position "fixed"
                    :left "0px"
                    :top "0px"
                    :width "100%"
                    :height "100%"
                    :background-color "rgba(0,0,0,0.85)"
                    :z-index 1020
                    :on-click #(util/console-log "clicked backdrop") ;; Gobble up clicks so they don't go to the main window (TODO: Doesn't work)
                    }}
           [:div                         ;; Modal window containing div
            {:style {:margin "auto"
                     :background-color "white"
                     :padding "16px"
                     :border-radius "6px"
                     :z-index 1020}}
            markup
            (when spinner       [make-spinner])
            (when progress-bar  [make-progress-bar progress-percent])
            (when cancel-button [make-cancel-button status "info" "Cancel"])
            (when @modal-alert  [:div {:style {:margin "12px"}}
                                 [closeable-alert
                                  @modal-alert
                                  (fn [_] (reset! modal-alert nil))]])
            ]]
          [:span])
        )
      })))


(defn add-modal-alert [alert-item]
  "Adds an alert box to the bottom of the modal window for when errors occur
  Parameters:
  - alert-item:       A map containing the definition of the alert:
  .   - :alert-type   A Bootstrap string determining the style. Either 'info', 'warning' or 'danger'
  .   - :heading      Hiccup markup or a string containing the heading text
  .   - :body         Hiccup markup or a string containing the body of the alert
  "
  (assoc alert-item :id 1) ;; Add the missing key
  (reset! modal-alert alert-item))


(defn chunk-runner [chunk-fn status chunks]
  "Split your long running process into bite-size chunks and use this function to schedule those execution
  chunks into the JavaScript event loop. It will also:
  - Manage the showing/hiding of the modal window
  - Calculate the percentage to be used in the progress bar if that's being shown
  Parameters:
  - chunk-fn  Your function that will be repeatedly called to perform a single chunk of processing.
  .           It will be passed the following parameters:
  .            - chunk-index  Chunk number 0 to chunks - 1
  .            - chunks       Number of chucks to be processed [TODO: Would we ever really need this?]
  .            - percent      Percentage through the process, rounded to nearest integer. This is important
  .                           as chunk-fn is responsible for updating the atom holding the percentage progress
  .                           used by the progress bar
  - status    The atom used to indicate the process is running (:running, :finished, :cancelled) and that
  .           the modal should be shown
  - chunks    How many times chunk-fn should be called (if omitted, defaults to 1)
  "
  (let [chunks (if chunks chunks 1)]
    (util/console-log (str "STARTING chunk-runner (" chunks ")"))
    (reset! modal-alert nil) ;; Clea any previous alerts
    (reset! status :running)   ;; Start by showing the modal
    (loop [chunk-index 0]
      (if (< chunk-index chunks)
        (let [percent (int (+ (* (/ chunk-index (- chunks 1)) 100) 0.5))] ;; OR: (-> chunk-index (/ (- chunks 1)) (* 100) (+ 0.5) int)
          (js/setTimeout #(chunk-fn chunk-index chunks percent) 0) ;; Schedule each chunk of work
          (recur (inc chunk-index)))
        (js/setTimeout #(reset! status :finished) 0)) ;; Schedule closing the modal window
      ))
  (util/console-log "FINISHED chunk-runner"))


(defn chunked-runner [fn-seq status]
  "A more generic version of splitting your function into chunks. Works fine when the number of calls to the
  worker function is not known as a lazy sequence of function calls is generated. Also, this lazy sequence
  could be comprised of one or more calls to the same function plus one or more calls to an numbers of other
  functions.
  Parameters:
  - fn-seq  A sequence of fucntion calls
  - status  A reagent atom which holds either :running, :finished or :cancelled
  "
  (let [schedule (fn reschedule []
                   (js/setTimeout
                    #(do
                       ((fn-seq) status)
                       (if (= @status :running) (reschedule))
                       )
                    10))] ;; 10ms should give enough time for UI events to be processed
    (reset! modal-alert nil)
    (reset! status :running)
    (schedule)
    ))


(defn modal-io-runner [fn fn-params status]
  "A modal runner for single processes that are not CPU intensive, typically I/O operations.
  Cancel buttons, spinners and progress bars work fine.
  Parameters:
  - fn         I/O function to be executed
  - fn-params  Parameters (in a vector) to be passed to the call to fn (or []/nil if none)
  - status     The status atom which shows/hides the modal window

  NOTE: Could use modal-single-chunk-runner in place of this.
  .     modal-single-chunk-runner does not actually stop you using cancel/spinner/progress.
  "
  (reset! modal-alert nil)
  (reset! status :running)
  (apply fn fn-params)
  )


(defn modal-single-chunk-runner [fn fn-params status]
  "A modal runner for single processes which ARE CPU intensive.
  Cancel buttons, spinners and progress bars will NOT work.
  Parameters:
  - fn         I/O function to be executed
  - fn-params  Parameters (in a vector) to be passed to the call to fn (or []/nil if none)
  - status     The status atom which shows/hides the modal window
  "
  (reset! modal-alert nil)
  (reset! status :running)
  (js/setTimeout #(apply fn fn-params) 10)
  )


(defn modal-multi-chunk-runner [fn-seq status]
  (let []

    (reset! status :running)
    (fn-seq)
    ))


(defn modal-dialog [status]
  "A runner for modal dialog boxes.
  Parameters:
  - status     The status atom which shows/hides the modal window
  "
  (reset! modal-alert nil)
  (reset! status :running))

