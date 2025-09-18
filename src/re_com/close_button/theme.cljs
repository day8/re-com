(ns re-com.close-button.theme
  (:require
   [re-com.close-button :as-alias cb]
   [re-com.theme.util :as tu]
   [re-com.util :refer [px]]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::cb/wrapper [props]
  (let [{:keys [div-size disabled?]} (get-in props [:re-com :state])]
    (tu/style props {:display          "inline-block"
                     :position         "relative"
                     :width            (px div-size)
                     :height           (px div-size)
                     :pointer-events   (when disabled? "none")})))

(defmethod base ::cb/button [props]
  (let [{:keys [font-size div-size top-offset left-offset disabled? hover? color hover-color]}
        (get-in props [:re-com :state])]
    (tu/style props {:position  "absolute"
                     :cursor    (when-not disabled? "pointer")
                     :font-size (px font-size)
                     :color     (if hover? hover-color color)
                     :top       (px (- (/ (- font-size div-size) 2) (or top-offset 0))  :negative)
                     :left      (px (- (/ (- font-size div-size) 2) (or left-offset 0)) :negative)})))

(defmethod bootstrap ::cb/wrapper [props]
  (tu/class props "rc-close-button"))

(defmethod bootstrap ::cb/icon [props]
  (tu/class props "rc-close-button-icon" "zmdi" "zmdi-hc-fw-rc" "zmdi-close"))