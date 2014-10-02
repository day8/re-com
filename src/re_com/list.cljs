(ns re-com.list
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [clojure.set          :refer [superset?]]
    [re-com.box           :refer [box border h-box v-box]]
    [re-com.util          :refer [deref-or-value]]
    [reagent.core         :as    reagent]))

;; ----------------------------------------------------------------------------


(defn- main-div-with
  [list-content hide-border]
  [h-box
   :children [[border
               :radius "4px"
               :size   "none"
               :border (when hide-border "none")
               :child [:div
                       {:class ""
                        ;; override inherrited body larger 14px font-size
                        ;; override position from css because we are inline
                        :style {:font-size "13px"
                                :position "static"
                                :-webkit-user-select "none" ;; only good on webkit/chrome what do we do for firefox etc
                                }}
                       list-content]]]])


(defn- selection-changed
  [selection change-callback]
  (change-callback selection))


(defn- configure
  "Augment passed attributes with extra info/defaults."
  [attributes]
  (merge attributes {}))

(def core-api
  #{:model         ; list of elements to be selected.
    :on-change     ; function callback will be passed list of selected items
    :disabled      ; optional boolean can be reagent/atom. When true, navigation is allowed but selection is disabled.
    :hide-border   ; boolean. Default false.
    })


(defn single-select-list
  [& {:keys [model] :as args}]
  {:pre [(superset? core-api (keys args))]}
  (let [elements (deref-or-value model)]
    (fn
      [& {:keys [model disabled hide-border on-change] :as properties}]
      (let [configuration (configure properties)]
        (main-div-with
          [v-box
            :gap "5px"
            :children [[:li "NOT YET IMPLEMENTED"]]]
          hide-border)))))