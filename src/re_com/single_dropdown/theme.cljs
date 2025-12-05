(ns re-com.single-dropdown.theme
  (:require-macros
   [re-com.core     :refer [handler-fn at]])
  (:require
   [re-com.single-dropdown :as-alias sd]
   [re-com.box      :refer [flex-child-style v-box h-box gap]]
   [goog.string     :as    gstring]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod bootstrap ::sd/tooltip [props]
  (tu/class props "rc-dropdown-tooltip"))

(defmethod base ::sd/chosen-container
  [{{:keys [transition!]
     {:keys [tooltip?]} :state} :re-com :as props}]
  (cond-> props
    tooltip? (tu/attr {:on-mouse-over (handler-fn (transition! :mouse-over))
                       :on-mouse-out  (handler-fn (transition! :mouse-out))})))

(defmethod bootstrap ::sd/chosen-container
  [{{{:keys [free-text? drop-showing? focused?]} :state} :re-com :as props}]
  (tu/class props
            "rc-dropdown"
            "chosen-container"
            (if free-text?
              "chosen-container-multi"
              "chosen-container-single")
            "noselect"
            (when (or drop-showing? focused?) "chosen-container-active")
            (when drop-showing? "chosen-with-drop")))

(defmethod base ::sd/chosen-single
  [{{{:keys [background-disabled]} :variables
     {:keys [interaction]}         :state} :re-com
    :as                            props}]
  (tu/style props
            {:display         "flex"
             :justify-content :space-between
             :width           "100%"}
            (when (= :disabled interaction)
              {:background-color background-disabled})))

(defmethod bootstrap ::sd/chosen-single [props]
  (tu/class props
            "rc-dropdown-chosen-single"
            "chosen-single"
            "chosen-default"))

(defmethod base ::sd/chosen-drop
  [{{{{:keys [position]} :chosen-drop} :state} :re-com
    :keys [top-height drop-height]
    :as props}]
  (cond-> props
    (= position :above)
    (tu/style {:transform (gstring/format
                           "translate3d(0px, -%ipx, 0px)"
                           (+ top-height drop-height -2))})))

(defmethod bootstrap ::sd/chosen-drop [props]
  (tu/class props "chosen-drop" "rc-dropdown-chosen-drop"))

(defmethod bootstrap ::sd/free-text-dropdown-top [props]
  (tu/class props "form-control"))

(defmethod base ::sd/chosen-search
  [{{{:keys [filter-box]} :state} :re-com :as props}]
  (let [invisible {:position "absolute"
                   :width    "0px"
                   :padding  "0px"
                   :border   "none"}]
    (cond-> props
      (= :hidden filter-box) (tu/style invisible))))

(defmethod bootstrap ::sd/group-heading [props]
  (tu/class props "group-result"))

(defmethod bootstrap ::sd/chosen-results [props]
  (tu/class props "chosen-results" "rc-dropdown-chosen-results"))

(defmethod bootstrap ::sd/choices-loading [props]
  (tu/class props "loading" "rc-dropdown-choices-loading"))

(defmethod bootstrap ::sd/choices-error [props]
  (tu/class props "error" "rc-dropdown-choices-error"))

(defmethod bootstrap ::sd/choices-no-results [props]
  (tu/class props "no-results" "rc-dropdown-choices-no-results"))
