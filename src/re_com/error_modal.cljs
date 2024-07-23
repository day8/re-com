(ns re-com.error-modal
  (:require-macros
   [re-com.core :refer [handler-fn at]])
  (:require
   [reagent.core :as r]
   [re-com.box :as box]
   [re-com.modal-panel :as mp]
   [re-com.theme :as theme]
   [re-com.theme.blue-modern :as blue-modern]
   [re-com.buttons :as button]
   [re-com.text :as text]
   [re-com.util :as u :refer [deref-or-value px]]))

(swap! theme/registry update :main vector blue-modern/theme)

(defn close-button [props]
  (let [hover? (r/atom nil)]
    [:div {:on-mouse-enter (partial reset! hover? true)
           :on-mouse-leave (partial reset! hover? false)
           :style {:padding "12px 7px 7px 7px"}}
     [u/x-button (merge props {:hover? hover? :stroke-width "1.2px"})]]))

(defn error-modal
  [& {:keys [severity title
             what-happened what-happened-title
             implications implications-title
             what-to-do what-to-do-title
             details details-title
             error error-title
             action instructions
             proceedable?
             undone?
             backdrop-on-click on-close closeable?
             theme
             header footer heading]
      :or   {title               "Sorry, you've hit a bug"
             what-happened-title "What Happened?"
             implications-title  "Implications"
             what-to-do-title    "What Should I Do?"
             details-title       "Low-level Details (for developers):"
             severity            :error
             closeable?          true}
      :as   args}]
  (let [themed            (fn [part props]
                            (theme/apply props
                              {:part  part
                               :state {:severity severity}}
                              (theme/defaults args)))
        panel-padding     31
        arrow-midpoint    12
        arrow-side-length (* 2 arrow-midpoint)
        arrow-points      (str arrow-midpoint "," arrow-midpoint " "
                               arrow-side-length "," 0 " "
                               "0,0")
        error             (if (string? error)
                            (fn [props] [:div props error]) error)
        details           (if (string? details)
                            (fn [props] [:div props details]) details)]
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
                 [[text/title (themed ::title {:label title})]
                  (when closeable? [close-button {:on-click on-close
                                                  :height   "12px"
                                                  :width    "12px"}])]})]
             [:svg (themed ::triangle
                     {:style {:width     (px arrow-side-length)
                              :height    (px arrow-midpoint)
                              :transform (str "translateX("
                                              (-> panel-padding (- arrow-midpoint) px) ")")}})
              [:polygon {:points arrow-points}]]
             [box/v-box
              (themed ::body
                {:gap [:<>
                       [box/gap :size "19px"]
                       [box/line]
                       [box/gap :size "7px"]]
                 :children
                 [(when header
                    [:<>
                     [box/gap :size "19px"]
                     [u/part header args]])
                  [:<>
                   (when action
                     [u/part heading
                      (themed ::sub-title-2 {:label action :level :level2})
                      text/title])
                   (when instructions
                     [text/p instructions])
                   (when what-happened
                     [:<>
                      [u/part heading
                       (themed ::sub-title-2 {:label what-happened-title :level :level3}) text/title]
                      [u/part what-happened args]])

                   (when implications
                     [:<>
                      [u/part heading
                       (themed ::sub-title {:label implications-title :level :level3}) text/title]
                      [u/part implications args]])

                   (when what-to-do
                     [:<>
                      [u/part heading (themed ::sub-title {:label what-to-do-title :level :level3}) text/title]
                      [u/part what-to-do args]])]

                  (when (or details error)
                    [:<>
                     [u/part heading
                      (themed ::sub-title {:label details-title :level :level3}) text/title]
                     [u/part details args]

                     #p (u/part error (themed ::error args))])

                  (when footer
                    [u/part footer args])]})]]})]})]))
