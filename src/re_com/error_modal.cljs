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

(defn close-button [props]
  (let [hover? (r/atom nil)]
    [:div {:on-mouse-enter (partial reset! hover? true)
           :on-mouse-leave (partial reset! hover? false)
           :style {:padding "12px 7px 7px 7px"}}
     [u/x-button (merge props {:width "12px"
                               :height "12px"
                               :hover? hover? :stroke-width "1.2px"})]]))

(defn error-modal
  [& {:keys [severity title
             what-happened what-happened-title
             implications implications-title
             what-to-do what-to-do-title
             details details-title
             error
             context
             action instructions
             proceedable?
             undone?
             backdrop-on-click on-close closeable?
             theme
             defaults?
             ask-to-report?
             engineers-notified?
             bug?
             header footer heading]
      :or   {title               "Sorry, you've hit a bug"
             what-happened-title "What Just Happened?"
             implications-title  "Implications"
             what-to-do-title    "What Should You Do Now?"
             details-title       "Low-level Details (for developers):"
             engineers-notified? true
             severity            :error
             closeable?          true
             defaults?           true
             bug?                true}
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
        error             (cond
                            (string? error)
                            (fn [props] [:div props error])
                            (map? error)
                            (fn [props] [:div props (pr-str error)])
                            :else
                            error)
        context           (cond
                            (string? context)
                            (fn [props] [:div props context])
                            (map? context)
                            (fn [props] [:div props (pr-str context)])
                            :else
                            context)
        details           (if (string? details)
                            (fn [props] [:div props details]) details)
        what-happened     (or what-happened
                              (when defaults?
                                [:span "Your app encountered an unexpected error. "
                                 "We currently don't have enough information to explain in more detail. "
                                 (when undone? "Your last action has been undone. ")
                                 (if engineers-notified?
                                   [:<>
                                    [:br]
                                    [:i "Our engineers have been notified. "]
                                    (when bug?
                                      [:span "This is probably a bug, so we're looking into it."])]
                                   (when bug? [:span "This is probably a bug."]))]))
        implications      (or implications
                              (when defaults?
                                "This app cannot continue on."))
        what-to-do        (or what-to-do
                              (when defaults?
                                (str
                                 (if proceedable?
                                   "You might be able to close this pop-up and continue on. Otherwise, restart this app."
                                   "You'll have to restart this app."))))]
    [mp/modal-panel
     (themed ::modal
       {:backdrop-on-click backdrop-on-click
        :parts             {:child-container {:style {:z-index 50}}}
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

                  (when (or details error context)
                    [:<>
                     [u/part heading
                      (themed ::sub-title {:label details-title :level :level4}) [:strong {:style {:font-size "12px"}} details-title]]
                     [u/part details args]
                     [u/part error (themed ::error args)]
                     [u/part context (themed ::context args)]])

                  (when footer
                    [u/part footer args])]})]]})]})]))
