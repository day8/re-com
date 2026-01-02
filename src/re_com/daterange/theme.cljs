(ns re-com.daterange.theme
  (:require
   [re-com.daterange :as-alias dr]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::dr/wrapper [props]
  (merge props {:align-self :stretch}))

(defmethod base ::dr/border [props]
  (merge props {:radius "5px"
                :size   "none"}))

(defmethod base ::dr/left-panel [props]
  (merge props {:gap "10px"}))

(defmethod base ::dr/right-panel [props]
  (merge props {:gap "10px"}))

(defmethod base ::dr/prev-nav [props]
  (merge props {:align-self :stretch}))

(defmethod base ::dr/next-nav [props]
  (merge props {:align-self :stretch}))

(defmethod base ::dr/container [props]
  (merge props {:gap     "60px"
                :padding "15px"}))

(defmethod bootstrap ::dr/wrapper [props]
  (tu/class props "rc-daterange-wrapper"))

(defmethod bootstrap ::dr/border [props]
  (let [{:keys [hide-border?]} (get-in props [:re-com :state])]
    (cond-> (tu/class props "rc-daterange-border" "noselect")
      hide-border? (assoc :border "none"))))

(defmethod bootstrap ::dr/prev-nav [props]
  (tu/class props "rc-daterange-prev-nav"))

(defmethod bootstrap ::dr/next-nav [props]
  (tu/class props "rc-daterange-next-nav"))

(defmethod bootstrap ::dr/prev-year [props]
  (tu/class props "rc-daterange-nav-button"))

(defmethod bootstrap ::dr/prev-month [props]
  (tu/class props "rc-daterange-nav-button"))

(defmethod bootstrap ::dr/next-year [props]
  (tu/class props "rc-daterange-nav-button"))

(defmethod bootstrap ::dr/next-month [props]
  (tu/class props "rc-daterange-nav-button"))

(defmethod bootstrap ::dr/prev-year-icon [props]
  (tu/class props "rc-daterange-nav-icon"))

(defmethod bootstrap ::dr/prev-month-icon [props]
  (tu/class props "rc-daterange-nav-icon"))

(defmethod bootstrap ::dr/next-year-icon [props]
  (tu/class props "rc-daterange-nav-icon"))

(defmethod bootstrap ::dr/next-month-icon [props]
  (tu/class props "rc-daterange-nav-icon"))

(defmethod bootstrap ::dr/month-title [props]
  (tu/class props "rc-daterange-month-title"))

(defmethod bootstrap ::dr/year-title [props]
  (tu/class props "rc-daterange-year-title"))

(defmethod bootstrap ::dr/table [props]
  (tu/class props "rc-daterange-table"))

(defmethod bootstrap ::dr/day-title [props]
  (tu/class props "daterange-day-title"))

(defmethod bootstrap ::dr/date [props]
  (tu/class props "rc-daterange-td-basic"))

(defmethod main ::dr/year-title [props]
  (tu/style props {:width "49px"
                   :align-self :end}))
