(ns re-com.tour
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [reagent.core   :as    reagent]
            [re-com.box     :refer [flex-child-style]]
            [re-com.buttons :refer [button]]))


;;--------------------------------------------------------------------------------------------------
;; Component: tour
;;
;;   Strings together
;;--------------------------------------------------------------------------------------------------

(defn make-tour
  "Returns a map containing
  - A reagent atom for each tour step controlling popover show/hide (boolean)
  - A standard atom holding the current step (integer)
  - A copy of the steps parameter passed in, to determine the order for prev/next functions
  It sets the first step atom to true so that it will be initially shown
  Sample return value:
  {:steps [:step1 :step2 :step3]
  :current-step (atom 0)
  :step1 (reagent/atom true)
  :step2 (reagent/atom false)
  :step3 (reagent/atom false)}"
  [tour-spec]
  (let [tour-map {:current-step (atom 0) :steps tour-spec}] ;; Only need normal atom

    (reduce #(assoc %1 %2 (reagent/atom false)) tour-map tour-spec))) ;; Old way: (merge {} (map #(hash-map % (reagent/atom false)) tour-spec))


(defn- initialise-tour
  "Resets all poover atoms to false"
  [tour]
  (doall (for [step (:steps tour)] (reset! (step tour) false))))


(defn start-tour
  "Sets the first popover atom in the tour to true"
  [tour]
  (initialise-tour tour)
  (reset! (:current-step tour) 0)
  (reset! ((first (:steps tour)) tour) true))


(defn finish-tour
  "Closes all tour popovers"
  [tour]
  (initialise-tour tour))


(defn- next-tour-step
  [tour]
  (let [steps     (:steps tour)
        old-step  @(:current-step tour)
        new-step  (inc old-step)]
    (when (< new-step (count (:steps tour)))
      (reset! (:current-step tour) new-step)
      (reset! ((nth steps old-step) tour) false)
      (reset! ((nth steps new-step) tour) true))))


(defn- prev-tour-step
  [tour]
  (let [steps    (:steps tour)
        old-step @(:current-step tour)
        new-step (dec old-step)]
    (when (>= new-step 0)
      (reset! (:current-step tour) new-step)
      (reset! ((nth steps old-step) tour) false)
      (reset! ((nth steps new-step) tour) true))))


(defn make-tour-nav
  "Generate the hr and previous/next buttons markup.
  If first button in tour, don't generate a Previous button.
  If last button in tour, change Next button to a Finish button"
  [tour]
  (let [on-first-button (= @(:current-step tour) 0)
        on-last-button  (= @(:current-step tour) (dec (count (:steps tour))))]
    [:div
     [:hr {:style (merge (flex-child-style "none")
                         {:margin "10px 0 10px"})}]
      (when-not on-first-button
        [button
         :label    "Previous"
         :on-click (handler-fn (prev-tour-step tour))
         :style    {:margin-right "15px"}
         :class     "btn-default"])
      [button
       :label    (if on-last-button "Finish" "Next")
       :on-click (handler-fn (if on-last-button
                               (finish-tour tour)
                               (next-tour-step tour)))
       :class     "btn-default"]]))
