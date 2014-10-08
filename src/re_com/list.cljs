(ns re-com.list
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [clojure.set          :refer [superset?]]
    [re-com.core          :refer [checkbox radio-button]]
    [re-com.box           :refer [box border scroller h-box v-box]]
    [re-com.util          :refer [fmap deref-or-value]]
    [reagent.core         :as    r]))

;; ----------------------------------------------------------------------------


(defn- as-checked
  [element selections on-change disabled label-fn]
  [:a {:class "list-group-item compact"}
   [checkbox
    :model       (selections element)
    :on-change   #(on-change (if % (conj selections element) (disj selections element)))
    :disabled    disabled
    :label-style {:flex "initial"}
    :label       (label-fn element)]])

(defn- radio-clicked
  [selections item]
  (if (selections item)
    selections  ;; we ignore unselect of radio button for now
    #{item}))

(defn- as-radio
  [element selections on-change disabled label-fn]
  [:a {:class "list-group-item compact"}
   [radio-button
    :model       (first selections)
    :value       element
    :on-change   #(on-change (radio-clicked selections %))
    :disabled    disabled
    :label-style {:flex "initial"}
    :label       (label-fn element)]])


;;TODO hide hover highlights for links when disabled
(defn- list-container
  [choices model on-change multi-select disabled hide-border label-fn]
  (let [selected (if multi-select model (-> model first vector set))
        items    (map (if multi-select
                        #(as-checked % selected on-change disabled label-fn)
                        #(as-radio   % selected on-change disabled label-fn))
                      choices)]
    ;; In single select mode force selections to one. This causes a second render
    (when-not (= selected model) (on-change selected))
    [border
     :radius "4px"
     :border (when hide-border "none")
     :size   "none"
     :child  (into [:div {:class "list-group"
                          :style {:max-width "400px" :max-height "115px"  ;;TODO height should be based on container
                                  :padding-left "5px" :padding-bottom "5px" :margin-top "5px" :margin-bottom "5px"
                                  :overflow-x "hidden" :overflow-y "auto" ;;TODO this should be handled by scroller later
                                  :-webkit-user-select "none"}}] items)]))


(defn- configure
  "Augment passed attributes with additional info/defaults and deref atoms."
  [attributes]
  (merge {:multi-select true
          :required     false
          :disabled     false
          :hide-border  false
          :label-fn     str}
         (fmap deref-or-value attributes)))


(def core-api
  #{:model         ; set of selected elements (atom supported).
    :choices       ; list of elements to be selected (atom supported).
    :multi-select  ; boolean (when true, items use check boxes otherwise radio buttons)
    :required      ; boolean (when true, at least one item must be selected) TODO:
    :on-change     ; function callback will be passed updated set of item(s)
    :disabled      ; optional boolean can be reagent/atom. When true, navigation is allowed but selection is disabled.
    :hide-border   ; boolean. Default false.
    :label-fn      ; optional function to call on element to get label string, default :label
    :width         ; optional CSS style value e.g. "250px"
    :height        ; optional CSS style value e.g. "150px"
    })


(defn inline-list
  [& {:as args}]
  {:pre [(superset? core-api (keys args))]}
  (fn
    [& {:as passthrough-args}]
    (let [passthrough-args (configure passthrough-args)]
      ;;TODO pass passthrough-args as named arguments for consistency
      [list-container
       (:choices      passthrough-args)
       (:model        passthrough-args)
       (:on-change    passthrough-args)
       (:multi-select passthrough-args)
       (:disabled     passthrough-args)
       (:hide-border  passthrough-args)
       (:label-fn     passthrough-args)])))