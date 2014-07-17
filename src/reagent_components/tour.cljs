(ns reagent-components.tour
  (:require [reagent-components.util :as util]
            [reagent-components.popover :as popover]
            [reagent.core :as reagent]))


;;--------------------------------------------------------------------------------------------------
;; Component: tour
;; 
;;   Strings together 
;; 
;;   Notes/todo:
;;    - TBA
;;--------------------------------------------------------------------------------------------------

(defn tour
  "Blah.
  Parameters:
  - blah:       Blah"
  []

  (let [a 1]
    (fn []
      [:div
       [:p "This is a tour"]])))

	   
	   
;;(defn make-tour []
;;  ;; return an atom containing current-step + an atom for each step controlling show/hide
;;  )
;;
;;(defn tour-next-step [tour]
;;
;;  )
;;
;;(defn tour-previous-step [tour]
;;
;;  )
;;
;;(defn tour-nav [tour] ;; If first or last in tour, then only make one button
;;
;;  )
;;
;;(def tour (make-tour [:step1 :step3 :step3]))
;;
