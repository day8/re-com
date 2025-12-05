(ns re-com.tag-dropdown.theme
  (:require-macros
   [re-com.core :refer [handler-fn]])
  (:require
   [re-com.tag-dropdown :as-alias td]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [base main bootstrap]]))

(defmethod base ::td/popover-anchor-wrapper [props]
  (merge props {:position :below-center}))

(defmethod bootstrap ::td/main [props]
  (tu/class props "rc-tag-dropdown"))

(defmethod base ::td/popover-content-wrapper
  [{{:keys [transition!]} :re-com :as props}]
  (merge props {:no-clip?  true
                :on-cancel (handler-fn (transition! :close))}))

(defmethod main ::td/popover-content-wrapper [props]
  (merge props {:arrow-length 0
                :arrow-width  0
                :arrow-gap    1
                :padding      "19px 19px"}))

(defmethod main ::td/main
  [{{:keys                 [transition!]
     {:keys [interaction]} :state} :re-com
    :as                            props}]
  (-> props
      (merge {:align :center})
      (tu/style
       {:overflow "hidden"
        :cursor   (if (= interaction :enabled)
                    "pointer" "default")})
      (tu/attr
       {:on-click (handler-fn (transition! :open))})))

(defmethod base ::td/main
  [{{{:keys [white background-disabled]} :variables
     {:keys [interaction]}               :state} :re-com
    :as                                          props}]
  (tu/style props
            {:background-color (if (= interaction :enabled)
                                 white background-disabled)
             :color            "#BBB"
             :border           "1px solid lightgrey"
             :border-radius    "2px"
             :padding          "0px 6px"}))

(defmethod base ::td/tags [props]
  (-> props
      (merge {:size "1"})
      (tu/style {:overflow "hidden"})))

(defmethod bootstrap ::td/tags [props]
  (tu/class props "rc-tag-dropdown-tags"))

(defmethod base ::td/placeholder-tag
  [{{:keys                          [transition!]
     {:keys [background-disabled]} :variables} :re-com
    :as                                         props}]
  (-> props
      (merge {:tooltip     "Click to select tags"
              :hover-style {:background-color background-disabled}})
      (tu/attr {:on-click (handler-fn (transition! :open))})
      (merge {:size "1"})
      (tu/style {:overflow "hidden"})))

(defmethod bootstrap ::td/tag [props]
  (tu/class props "rc-tag-dropdown-tag" "noselect" "rc-text-tag"))

(defmethod bootstrap ::td/counter [props]
  (tu/class props "rc-tag-dropdown-counter"))

(defmethod main ::td/counter [props]
  (tu/style props {:color        "grey"
                   :font-size    "12px"
                   :margin-right "2px"}))

(defmethod bootstrap ::td/selection-list [props]
  (tu/class props "rc-tag-dropdown-selection-list"))
