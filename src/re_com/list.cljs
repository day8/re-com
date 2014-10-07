(ns re-com.list
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [clojure.set          :refer [superset?]]
    [re-com.core          :refer [checkbox]]
    [re-com.box           :refer [box border scroller h-box v-box]]
    [re-com.util          :refer [deref-or-value]]
    [reagent.core         :as    r]))

;; ----------------------------------------------------------------------------


(defn- as-checked
  [element selections on-change disabled]
  [:a {:class "list-group-item compact"}
   [checkbox
    :model (selections element)
    :on-change #(on-change (if % (conj selections element) (disj selections element)))
    :label-style {:flex "initial"}
    :label (:label element)]])


(trace-forms {:tracer default-tracer}
(defn- main-div-with
  [elements selections on-change multi-select disabled hide-border]
  [(fn []
     (let [items (map #(as-checked % selections on-change disabled) elements)]
       [border
        :radius "4px"
        :size   "none"
        :child  (into [:div {:class "list-group"
                             :style {:max-width "400px" :max-height "115px"  ;;TODO height should be based on container
                                     :padding-left "5px" :padding-bottom "5px" :margin-top "5px" :margin-bottom "5px"
                                     :overflow-x "hidden" :overflow-y "auto" ;;TODO this should be handled by scroller later
                                     :-webkit-user-select "none"}}] items)
        ]))]))


(defn- configure
  "Augment passed attributes with extra info/defaults."
  [attributes]
  (merge attributes {}))

(def core-api
  #{:model         ; list of elements to be selected (atom supported).
    :selections    ; set of selected elements (atom supported).
    :multi-select  ; boolean (when true, items use check boxes otherwise radio buttons)
    :required      ; boolean (when true, at least one item must be selected)
    :on-change     ; function callback will be passed updated set of item(s)
    :disabled      ; optional boolean can be reagent/atom. When true, navigation is allowed but selection is disabled.
    :hide-border   ; boolean. Default false.
    })


;;TODO discuss with others if we want to use same named argument passing through all function layers
(defn single-select-list
  [& {:as args}]
  {:pre [(superset? core-api (keys args))]}
  (fn
    [& {:keys [model selections multi-select hide-border on-change disabled] :as properties}]
    (let [configuration (configure properties)
          elements (deref-or-value model)
          selected (deref-or-value selections)]
      (main-div-with
        elements
        selected
        on-change
        multi-select
        disabled
        hide-border))))