(ns re-com.stylesheet
  (:require [garden.core :as garden]))

(defn inject-css-stylesheet!
  "Inject a CSS text format stylesheet into the <head> section of the current html file
    - stylesheet: a standard CSS string which can contain one or more style definitions
                  e.g. body     {background-color: \"lightgrey\";}
                       .heading {font-size: \"26px\";}
    - id          the unique text id used for the id attribute of this stylesheet so that if it changes
                  (e.g. via figwheel), the existing sheet will be replaced rather than a duplicate created."
  [stylesheet id]
  (let [style-element (.getElementById js/document id)
        update-style! (fn [element stylesheet] (set! (.-innerHTML element) stylesheet))]
    (if style-element
      (update-style! style-element stylesheet)              ;; If stylesheet exists, replace it with this version
      (let [head        (.-head js/document)                ;; otherwise create it
            new-element (.createElement js/document "style")]
        (set! (.-id new-element) id)
        (set! (.-type new-element) "text/css")
        (update-style! new-element stylesheet)
        (.appendChild head new-element)))))


(defn inject-garden-stylesheet!
  "Inject a clj object stylesheet into the <head> section of the current html file
    - stylesheet: a sequence of clj/s vectors in garden format.
                  e.g. [[:body        {:background-color \"lightgrey\"}]
                        [\".heading\" {font-size: \"26px\"}]]
    - id          the unique text id used for the id attribute of this stylesheet so that if it changes
                  (e.g. via figwheel), the existing sheet will be replaced rather than a duplicate created."
  [stylesheet id]
  (inject-css-stylesheet! (apply garden/css stylesheet) id))