(ns re-com.title.theme
  (:require
   [re-com.title :as-alias title]
   [re-com.theme.util :as tu]
   [re-com.box :refer [flex-child-style]]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::title/wrapper [props]
  (merge props {:size "auto"}))

(defmethod base ::title/label-wrapper [props]
  (let [{:keys [margin-top margin-bottom underline?]} (get-in props [:re-com :state])]
    (tu/style props
              (merge (flex-child-style "none")
                     {:margin-top margin-top}
                     {:line-height 1}
                     (when-not underline? {:margin-bottom margin-bottom})))))

(defmethod bootstrap ::title/wrapper [props]
  (let [{:keys [level]} (get-in props [:re-com :state])
        preset-class (if (nil? level) "" (name level))]
    (tu/class props preset-class)))

(defmethod bootstrap ::title/label-wrapper [props]
  (let [{:keys [level]} (get-in props [:re-com :state])
        preset-class (if (nil? level) "" (name level))]
    (tu/class props "display-flex" "rc-title" preset-class)))

(defmethod main ::title/underline [props]
  (let [{:keys [margin-bottom]} (get-in props [:re-com :state])]
    (tu/style props {:margin-bottom margin-bottom})))
