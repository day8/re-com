(ns re-com.state
  (:require [reagent.core :as reagent]))

;; I'd rather have machine be the atom and chart be in the meta.
(defn machine [chart]
  (reagent/atom (::init chart)))

(defn some-state [machine path k]
  (reagent/reaction (#{k} (get-in @machine path))))

(defn transition! [machine chart k & args]
  (when-let [tx (-> chart ::transitions k)]
    (swap! machine tx))
  (when-let [a (-> chart ::actions k)]
    (apply a args)))
