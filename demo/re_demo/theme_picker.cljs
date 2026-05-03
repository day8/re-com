(ns re-demo.theme-picker
  (:require
   [reagent.core        :as reagent]
   [re-com.core         :as rc :refer [single-dropdown at]]
   [re-com.theme.modern :as modern-theme]))

(defonce active-theme-id (reagent/atom :classic))

(defn switching-theme
  "Top-level :user theme registered with reg-theme. Derefs active-theme-id
   inside the render path, so changing the atom causes Reagent to re-render
   any component whose theme is composed and reapplied per render. The
   theme/comp pipeline captures this function at mount time, but the deref
   inside it remains reactive."
  [props]
  (case @active-theme-id
    :modern (modern-theme/theme props)
    props))

(defonce _register-switching-theme
  (rc/reg-theme switching-theme))

(def theme-options
  [{:id :classic :label "Classic"}
   {:id :modern  :label "Modern"}])

(defn theme-picker []
  [single-dropdown
   :src       (at)
   :width     "110px"
   :choices   theme-options
   :model     active-theme-id
   :on-change #(reset! active-theme-id %)])
