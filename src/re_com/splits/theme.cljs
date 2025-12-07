(ns re-com.splits.theme
  (:require
   [re-com.splits :as-alias splits]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base bootstrap]]
   [re-com.h-split   :as-alias hs]
   [re-com.v-split   :as-alias vs]))

(defmethod bootstrap ::hs/wrapper [props]
  (tu/class props "rc-h-split" "display-flex"))

(defmethod bootstrap ::vs/wrapper [props]
  (tu/class props "rc-v-split" "display-flex"))

(defmethod bootstrap ::hs/left [props]
  (tu/class props
            "rc-h-split-top" ;; for backwards compatibility
            "display-flex"))

(defmethod bootstrap ::hs/right [props]
  (tu/class props
            "rc-h-split-bottom" ;; for backwards compatibility
            "display-flex"))

(defmethod bootstrap ::vs/top [props]
  (tu/class props "display-flex"))

(defmethod bootstrap ::vs/bottom [props]
  (tu/class props "display-flex"))

(defmethod bootstrap ::hs/splitter [props]
  (tu/class props "display-flex"))

(defmethod base ::hs/splitter
  [{{{:keys [hover]} :state} :re-com :as props}]
  (tu/style props
            {:cursor "col-resize"}
            (when (= hover :active) {:background-color "#f8f8f8"})))

(defmethod bootstrap ::vs/splitter [props]
  (tu/class props "display-flex"))

(defmethod base ::vs/splitter
  [{{{:keys [hover]} :state} :re-com :as props}]
  (tu/style props
            {:cursor "row-resize"}
            (when (= hover :active) {:background-color "#f8f8f8"})))

(defmethod bootstrap ::hs/handle [props]
  (tu/class props "rc-h-split-handle" "display-flex"))

(defmethod base ::hs/handle [props]
  (merge props
         {:size "auto"}
         (tu/style props {:flex-flow "row nowrap"
                          :width "8px"
                          :height "20px"
                          :margin "auto"})))

(defmethod bootstrap ::hs/handle-bar-1 [props]
  (tu/class props "rc-h-split-handle-bar-1"))

(defmethod base ::hs/handle-bar-1
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [color (case hover :active "#999" "#ccc")]
    (tu/style props {:width "3px" :height "20px" :border-right (str "solid 1px " color)})))

(defmethod bootstrap ::hs/handle-bar-2 [props]
  (tu/class props "rc-h-split-handle-bar-2"))

(defmethod base ::hs/handle-bar-2
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [color (case hover :active "#999" "#ccc")]
    (tu/style props {:width "3px" :height "20px" :border-right (str "solid 1px " color)})))

(defmethod bootstrap ::vs/handle [props]
  (tu/class props "rc-v-split-handle" "display-flex"))

(defmethod base ::vs/handle [props]
  (merge props
         {:size "auto"}
         (tu/style props {:flex-flow "column nowrap"
                          :width "20px"
                          :height "8px"
                          :margin "auto"})))

(defmethod bootstrap ::vs/handle-bar-1 [props]
  (tu/class props "rc-v-split-handle-bar-1"))

(defmethod base ::vs/handle-bar-1
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [color (case hover :active "#999" "#ccc")]
    (tu/style props {:width "20px" :height "3px" :border-bottom (str "solid 1px " color)})))

(defmethod bootstrap ::vs/handle-bar-2 [props]
  (tu/class props "rc-v-split-handle-bar-2"))

(defmethod base ::vs/handle-bar-2
  [{{{:keys [hover]} :state} :re-com :as props}]
  (let [color (case hover :active "#999" "#ccc")]
    (tu/style props {:width         "20px"
                     :height        "3px"
                     :border-bottom (str "solid 1px " color)})))
