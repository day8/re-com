(ns re-com.error-modal
  (:require-macros
   [re-com.core :refer [handler-fn at]])
  (:require
   [reagent.core :as r]
   [re-com.box :as box]
   [re-com.modal-panel :as mp]
   [re-com.theme :as theme]
   [re-com.buttons :as button]
   [re-com.text :as text]
   [re-com.util :as u :refer [deref-or-value px]]))

(defn error-modal
  [& {:keys [severity title
             what-happened what-happened-label
             implications implications-label
             what-to-do what-to-do-label
             action instructions
             backdrop-on-click on-close closeable?
             header footer heading]
      :or   {title               "Sorry, you've hit a bug"
             what-happened-label "What Happened?"
             implications-label  "Implications"
             what-to-do-label    "What Should I Do?"
             severity            :error
             closeable?          true}
      :as   args}]
  (let [themed            (fn [part & [props]]
                            (theme/apply props
                              {:part  part
                               :state {:severity severity}}
                              (theme/defaults args)))
        panel-padding     31
        arrow-midpoint    12
        arrow-side-length (* 2 arrow-midpoint)
        arrow-points      (str arrow-midpoint "," arrow-midpoint " "
                               arrow-side-length "," 0 " "
                               "0,0")]
    [mp/modal-panel
     (themed ::modal
       {:backdrop-on-click backdrop-on-click
        :child
        [box/v-box
         (themed ::inner-wrapper
           {:src (at)
            :children
            [[box/h-box
              (themed ::title-wrapper
                {:src (at)
                 :children
                 [[text/title (themed ::title {:level :level2 :label title})]
                  (when closeable? [u/x-button {:on-click on-close}])]})]
             [:svg (themed ::triangle {:style {:width     (px arrow-side-length)
                                               :height    (px arrow-midpoint)
                                               :transform (str "translateX(" (-> panel-padding (- arrow-midpoint) px) ")")}})
              [:polygon {:points arrow-points}]]
             [box/v-box
              (themed ::body
                {:children
                 [[u/part header args]
                  (when what-happened
                    [u/part heading (themed ::heading {:label what-happened-label :level :level2}) text/title])
                  [u/part what-happened args]
                  (when implications
                    [u/part heading (themed ::heading {:label implications-label :level :level2}) text/title])
                  [u/part implications args]
                  (when what-to-do
                    [u/part heading (themed ::heading {:label what-to-do-label :level :level2}) text/title])
                  [u/part what-to-do args]
                  [u/part footer args]]})]]})]})]))
