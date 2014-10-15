(ns re-com.selection-list
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [clairvoyant.core     :refer [default-tracer]]
    [clojure.set          :refer [superset?]]
    [re-com.core          :refer [checkbox radio-button]]
    [re-com.box           :refer [box border scroller h-box v-box]]
    [re-com.util          :refer [fmap deref-or-value]]
    [reagent.core         :as    r]))

;; ----------------------------------------------------------------------------
(defn label-style [selected? as-exclusions?]
  ;;TODO: margin-top required because currently checkbox & radio-button don't center label
  (let [base-style {:margin-top "1px" :width "100%"}]
    (if (and selected? as-exclusions?)
      (merge base-style {:text-decoration "line-through"})
      base-style)))

  (defn- check-clicked
  [selections item ticked required]
  (let [num-selected (count selections)
        only-item    (when (= 1 num-selected) (first selections))]
  (if (and required (= only-item item))
    selections  ;; prevent unselect of last item
    (if ticked (conj selections item) (disj selections item)))))

(defn- as-checked
  [item selections on-change disabled label-fn required as-exclusions]
  ;;TODO: Do we realy need an anchor now that bootstrap styles not realy being used ?
  [:a {:class "list-group-item compact"}
   [checkbox
    :model       (selections item)
    :on-change   #(on-change (check-clicked selections item % required))
    :disabled    disabled
    :label-style (label-style (selections item) as-exclusions)
    :label       (label-fn item)]])


(defn- radio-clicked
  [selections item required]
  (if (and required (selections item))
    selections  ;; prevent unselect of radio
    (if (selections item) #{} #{item})))

(defn- as-radio
  [item selections on-change disabled label-fn required as-exclusions]
  ;;TODO: Do we realy need an anchor now that bootstrap styles not realy being used ?
  [:a {:class "list-group-item compact"}
   [radio-button
    :model       (first selections)
    :value       item
    :on-change   #(on-change (radio-clicked selections % required))
    :disabled    disabled
    :label-style (label-style (selections item) as-exclusions)
    :label       (label-fn item)]])


(def list-style ^:private
  ;;TODO: These should be in CSS resource
  {:overflow-x     "hidden"
   :overflow-y     "auto" ;;TODO this should be handled by scroller later
   :-webkit-user-select "none"})

(def spacing-bordered ^:private
  {:padding-top         "0px"
   :padding-bottom      "0px"
   :padding-left        "5px"
   :padding-right       "5px"
   :margin-top          "5px"
   :margin-bottom       "5px"})

(def spacing-unbordered ^:private
  {:padding-left   "0px"
   :padding-right  "5px"
   :padding-top    "0px"
   :padding-bottom "0px"
   :margin-top     "0px"
   :margin-bottom  "0px"})


;;TODO hide hover highlights for links when disabled
(defn- list-container
  [{:keys [choices model on-change multi-select disabled hide-border label-fn required as-exclusions] :as args}]
  (let [selected (if multi-select model (-> model first vector set))
        items    (map (if multi-select
                        #(as-checked % selected on-change disabled label-fn required as-exclusions)
                        #(as-radio   % selected on-change disabled label-fn required as-exclusions))
                      choices)
        bounds   (select-keys args [:width :height :max-height])
        spacing  (if hide-border spacing-unbordered spacing-bordered)]
    ;; In single select mode force selections to one. This causes a second render
    (when-not (= selected model) (on-change selected))
    [border
     :radius "4px"
     :border (when hide-border "none")
     :child  (into [:div {:class "list-group" :style (merge list-style bounds spacing)}] items)]))


(defn- configure
  "Augment passed attributes with defaults and deref any atoms."
  [attributes]
  (merge {:multi-select  true
          :as-exclusions false
          :required      false
          :disabled      false
          :hide-border   false
          :label-fn      (partial str)}
         (fmap deref-or-value attributes)))


(def selection-list-args
  #{:model         ; set of selected elements (atom supported).
    :choices       ; list of elements to be selected (atom supported).
    :multi-select  ; boolean (when true, items use check boxes otherwise radio buttons)
    :as-exclusions ; boolean (when true, selected item labels use strikeout style)
    :required      ; boolean (when true, at least one item must be selected)
    :on-change     ; function callback will be passed updated set of item(s)
    :disabled      ; optional boolean. When true, navigation is allowed but selection is disabled.
    :hide-border   ; boolean. Default false.
    :label-fn      ; optional function to call on element to get label string, default :label
    :width         ; optional CSS style value e.g. "250px"
    :height        ; optional CSS style value e.g. "150px"
    :max-height    ; optional CSS style value e.g. "150px" Height will shrink/grow based on elements up to this, then scroll
    })

(defn selection-list
  "Produce a list box with items arranged vertically"
  [& {:as args}]
  {:pre [(superset? selection-list-args (keys args))]}
  ;;NOTE: Consumer has complete control over what is selected or not. A current design tradeoff
  ;;      causes all selection changes to trigger a complete list re-render as a result of on-change callback.
  ;;      this approach may be not ideal for very large list choices.
  (fn [& {:as args}]
    [list-container (configure args)]))