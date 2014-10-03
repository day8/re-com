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


(defn- main-div-with
  [list-content hide-border]
  (let [toggle (r/atom false)]

  [border
   :radius "4px"
   :size   "none"
   :child  [:div {:class "list-group"
                    :style {:max-width "400px" :max-height "115px" ;;TODO This needs to be based on container
                            :padding-left "5px" :padding-bottom "5px" :margin-bottom "auto"
                            :overflow-x "hidden" :overflow-y "auto" ;;TODO this should be handled by scroller later
                            :-webkit-user-select "none"}}
              [:a {:class "list-group-item compact"}
               [checkbox
                :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"
                :model toggle :on-change #(reset! toggle %)]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              [:a {:class "list-group-item compact"}
               [checkbox :label-style {:flex "initial"}
                :label "Donec id elit non mi porta gravida at eget metus. Maecenas sed diam eget risus varius blandit"]]
              ]]))


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