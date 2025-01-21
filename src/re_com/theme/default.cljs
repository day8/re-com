(ns re-com.theme.default
  (:require
   [re-com.util :as ru :refer [px]]
   [re-com.dropdown :as-alias dropdown]
   [re-com.error-modal :as-alias error-modal]
   [re-com.tree-select :as-alias tree-select]))

(def golden-section-50
  {:sm-1   "1px"
   :sm-2   "2px"
   :sm-3   "3px"
   :sm-4   "5px"
   :sm-5   "7px"
   :sm-6   "12px"
   :md-1   "19px"
   :md-2   "31px"
   :md-3   "50px"
   :md-4   "81px"
   :md-5   "131px"
   :md-6   "212px"
   :lg-1   "343px"
   :lg-2   "554px"
   :lg-3   "897px"
   :lg-4   "1452px"
   :lg-5   "2349px"
   :lg-6   "3800px"
   :half   "25px"
   :double "100px"})

(def colors
  {:primary             "#0d6efd"
   :secondary           "#6c757d"
   :success             "#198754"
   :info                "#0dcaf0"
   :error               "#bf1010"
   :warning             "#ffc107"
   :danger              "#dc3545"
   :light               "#f8f9fa"
   :white               "#ffffff"
   :dark                "#212529"
   :black               "#000000"
   :neutral             "#555555"
   :foreground          "#767a7c"
   :light-background    "#eee"
   :light-foreground    "#ccc"
   :background          "white"
   :background-disabled "#EEE"
   :border              "#cccccc"
   :border-dark         "#aaa"
   :shadow              "rgba(0, 0, 0, 0.2)"})

(def font-sizes
  {:font-size/xx-small (px 11)
   :font-size/x-small  (px 12)
   :font-size/small    (px 13)
   :font-size/medium   (px 14)
   :font-size/large    (px 15)
   :font-size/x-large  (px 16)
   :font-size/xx-large (px 17)})

(def static-variables (merge colors golden-section-50 font-sizes))

(defn variables [props]
  (assoc-in props [:re-com :variables] static-variables))

(defmulti base :part)

(defmethod base :default [props] props)

(defmulti main :part)

(defmethod main :default [props] props)

(defmethod base :default [props] props)
