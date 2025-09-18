(ns re-com.p.theme
  (:require
   [re-com.p :as-alias p]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::p/wrapper [props]
  (tu/style props {:flex          "none"
                   :width         "450px"
                   :min-width     "450px"
                   :margin-bottom "0.7em"}))

(defmethod bootstrap ::p/wrapper [props]
  (tu/class props "rc-p"))