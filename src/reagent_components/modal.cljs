(ns reagent-components.modal
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [reagent-components.util  :as util]
            [reagent-components.core  :refer [button spinner progress-bar]]
            [cljs.core.async          :as async :refer [<! >! chan close! sliding-buffer put! alts! timeout]]
            ;; [reagent-components.alert :refer [closeable-alert]]
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
;;               - Cancel button would only work between jobs unless a job was chunked.
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


;; ------------------------------------------------------------------------------------
;;  cancel-button
;; ------------------------------------------------------------------------------------

(defn cancel-button ;; TODO: Only currently used in modal
  [callback]
  "Render a cancel button"
  [:div {:style {:display "flex"}}
   [button "Cancel" callback
    :style {:margin "auto"}
    :class "btn-info"]])


;; ------------------------------------------------------------------------------------
;;  modal-window-OLD  TODO: TO BE REMOVED
;; ------------------------------------------------------------------------------------

;; CORE.ASYNC REMOVED
;; (defn listen [el type]
;;   (let [out (chan)]
;;     (events/listen el type (fn [e] (put! out e)))
;;     out))

(def modal-alert (reagent/atom nil))


(defn modal-window-OLD [markup status modal-options progress-percent]
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
  main window to prevent UI interactivity and place user focus on the modal window.
  Parameters:
  - markup            The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)
  - status            [optional] The atom used to indicate the process is running (:running, :finished, :cancelled) and that
  .                   the modal should be shown. Only required when :cancel-button specified.
  - modal-options     [optional] A map containing two options:
  .                    - :spinner       A boolean indicating whether to show a spinner or not
  .                    - :progress-bar? A boolean indicating whether to show a progress bar or not
  .                    - :cancel-button A boolean indicating whether to show a cancel button or not
  - progress-percent  [optional] The integer atom used to hold the percentage through the process we are. Used in rendering the progress bar
  "
  (let [spinner?       (:spinner       modal-options)
        progress-bar?  (:progress-bar  modal-options)
        cancel-button? (:cancel-button modal-options)
        ;; cancel-id      (gensym "cancel-") ;; CORE.ASYNC REMOVED
        ]

    ;; (util/console-log "In show-modal-window")
    (reagent/create-class
     {
      :component-did-mount
      (fn []
        ;; CORE.ASYNC REMOVED
        ;; (when cancel-button?
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
          (when spinner?       [spinner])
          (when progress-bar?  [progress-bar progress-percent])
          (when cancel-button? [cancel-button #(reset! status :cancelled)])
          #_(when @modal-alert   [:div {:style {:margin "12px"}}
                                [closeable-alert
                                 @modal-alert
                                 (fn [id] (reset! modal-alert nil))]])
          ]]
        )
      })))


;; ------------------------------------------------------------------------------------
;;  modal-window
;; ------------------------------------------------------------------------------------

(defn modal-window
  [& {:keys [markup]}]
  "Renders a modal window centered on screen. A dark transparent backdrop sits between this and the underlying
  main window to prevent UI interactivity and place user focus on the modal window.
  Parameters:
  - markup  The message to display in the modal (a string or a hiccup vector or function returning a hiccup vector)
  "
  (reagent/create-class
   {
    :component-did-mount
    (fn []
      ;; CORE.ASYNC REMOVED
      )

    :render
    (fn []
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
        markup]])}))


;; ------------------------------------------------------------------------------------
;;  chunk-runner  TODO: TO BE REMOVED
;; ------------------------------------------------------------------------------------

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


(defn start-cpu-intensive
  [func]
  "Run a CPU intensive function with a slight delay to allow the UI to updated (and show modal window changes)
  "
  (js/setTimeout func 10))


;; ------------------------------------------------------------------------------------
;;  modal-multi-chunk-runner   TODO: TO BE REMOVED
;; ------------------------------------------------------------------------------------

(defn modal-multi-chunk-runner
  [func initial-state running?]
  "...
  Parameters:
  - func           A function to repeatedly call. On each call, something else happens, could be the
  .                same funciton, could be a different function.
  - initial-state  The initial state to be passed to the first function call.
  .                After that, each successive function call is responsible for returning the parameters
  .                to be used for the subsequent function call and so on.
  - running?       A reagent boolean atom indicating if the processing is running
  "
  (let [schedule (fn reschedule [state]
                   (js/setTimeout
                    #(let [next-state (apply (func) state)]
                       (util/console-log (str "IN reschedule: " state))
                       (when-not next-state (reset! running? false))
                       (when @running? (reschedule next-state)))
                    20))] ;; 20ms should give enough time for UI events to be processed
    (reset! running? true)
    (schedule initial-state)))


;; ------------------------------------------------------------------------------------
;;  looper
;; ------------------------------------------------------------------------------------

(defn looper
  [& {:keys [initial-value func when-done]}]
  (go (loop [pause (<! (timeout 20))
             val   initial-value]
        (let [[continue? out]  (func val)]
          (if continue?
            (recur (<! (timeout 20)) out)
            (when-done out))))))


(defn looper-OLD
  [initial-value func continue?]
  (reset! continue? true)
  (go (loop [pause (<! (timeout 20))
             val   initial-value]
        (let [out  ((func continue?) val)]
          (if @continue?
            (recur (<! (timeout 20)) out)
            out)))))


;; ------------------------------------------------------------------------------------
;;  domino-process
;; ------------------------------------------------------------------------------------

(defn domino-step
  [continue-fn? in-chan func]
  (let [out-chan (chan)]
    (go (let [in    (<! in-chan)
              pause (<! (timeout 20))
              out   (if (continue-fn?) (func in) in)]
          (>! out-chan (if (nil? out) in out))))
    out-chan))

(defn domino-process
  ([initial-value funcs]
   (domino-process initial-value (atom true) funcs))
  ([initial-value continue? funcs]
  (assert ((complement nil?) initial-value) "Initial value can't be nil because that causes channel problems")
  (let [continue-fn? (fn [] @continue?)
        in-chan   (chan)
        out-chan  (clojure.core/reduce (partial domino-step continue-fn?) in-chan funcs)]
    (put! in-chan initial-value)
    out-chan)))
