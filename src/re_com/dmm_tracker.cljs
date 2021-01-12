(ns re-com.dmm-tracker)
;; Taken from: https://github.com/facebook/fixed-data-table/blob/master/src/vendor_upstream/dom/DOMMouseMoveTracker.js
;; From: https://github.com/facebook/fixed-data-table/blob/master/src/vendor_upstream/stubs/EventListener.js

(defn document-event-listener
  "Listen to DOM events during the bubble phase
     arg1   Event type, e.g. \"click\" or \"mouseover\"
     arg2   callback function
     return function to call to remove the event listener"
  [eventType callback]
  (let [target (.-documentElement js/document)]
    (if (.-addEventListener target)
      (do (.addEventListener target eventType callback false)
          ;(println "addEventListener" eventType)
          #(do (.removeEventListener target eventType callback false)))
               ;(println "removeEventListener" eventType)

      (throw (js/Error. "Couldn't find addEventListener method in document-event-listener")))))


(defprotocol IMouseMoveTracker
  (captureMouseMoves  [this event])
  (-releaseMouseMoves [this event])
  (-onMouseMove       [this event]))

(deftype MouseMoveTracker [on-change
                           on-drag-end
                           ^:mutable eventMoveToken
                           ^:mutable eventUpToken
                           ^:mutable isDragging?
                           ^:mutable x
                           ^:mutable y]
  IMouseMoveTracker
  (captureMouseMoves
    ;; This is to set up the listeners for listening to mouse move and mouse up signaling the movement has ended. Please note that these listeners are added at the document.body level. It takes in an event in order to grab inital state
    [this event]
    (when (and (not eventMoveToken) (not eventUpToken))
      (set! eventMoveToken (document-event-listener "mousemove" #(-onMouseMove this %)))
      (set! eventUpToken   (document-event-listener "mouseup"   #(-releaseMouseMoves this %))))
    (when-not isDragging?
      (set! isDragging? true)
      (set! x (.-clientX event))
      (set! y (.-clientY event)))
    #_(.preventDefault event))                              ;; [GR] This prevented getting access to the components in the popover

  (-releaseMouseMoves
    ; These releases all of the listeners on document.body
    [this event]
    (when eventMoveToken
      (eventMoveToken)
      (set! eventMoveToken nil))
    (when eventUpToken
      (eventUpToken)
      (set! eventUpToken nil))
    (when isDragging?
      (set! isDragging? false)
      (set! x nil)
      (set! y nil)
      (on-drag-end (.-ctrlKey event) (.-shiftKey event) event)))

  (-onMouseMove
    ;; Calls onMove passed into constructor and updates internal state
    [this event]
    (let [curr-x  (.-clientX event)
          curr-y  (.-clientY event)
          delta-x (- curr-x x)
          delta-y (- curr-y y)]
      (on-change delta-x delta-y curr-x curr-y (.-ctrlKey event) (.-shiftKey event) event)
      (set! x curr-x)
      (set! y curr-y)
      (.preventDefault event))))

(defn make-dmm-tracker
  [on-change on-drag-end]
  (->MouseMoveTracker on-change on-drag-end nil nil false nil nil))
