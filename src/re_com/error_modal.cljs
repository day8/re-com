(ns re-com.error-modal
  (:require-macros
   [re-com.core :refer [at]])
  (:require
   [reagent.core :as r]
   [re-com.box :as box]
   [re-com.part :as part]
   [re-com.modal-panel :as mp]
   [re-com.theme :as theme]
   re-com.error-modal.theme
   [re-com.text :as text]
   [re-com.util :as u :refer [px]]))

(defn close-button [_]
  (let [hover? (r/atom nil)]
    (fn [{:keys [on-click] :as props}]
      [:div {:on-mouse-enter (partial reset! hover? true)
             :on-mouse-leave (partial reset! hover? false)
             :on-click       on-click
             :style          {:padding "12px 7px 7px 7px"}}
       [u/x-button (merge {:width        "12px"
                           :height       "12px"
                           :hover?       hover?
                           :stroke       (if @hover? "#000000" "#ffffff")
                           :stroke-width "1.2px"}
                          props)]])))

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
             pre-theme theme
             defaults?
             ask-to-report?
             engineers-notified?
             bug?
             header footer heading
             title-wrapper
             modal inner-wrapper top-bar
             triangle body text code]
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
  (let [theme             (theme/comp pre-theme theme)
        ;;TODO use both ns and raw keywords for parts
        part              (fn [part-value & {:keys [props] :as opts}]
                            (part/part
                              part-value
                              (merge opts
                                     {:theme theme
                                      :props (merge {:re-com {:error-modal {:severity severity}}}
                                                    props)})))
        panel-padding     31
        arrow-midpoint    12
        arrow-side-length (* 2 arrow-midpoint)
        arrow-points      (str arrow-midpoint "," arrow-midpoint " "
                               arrow-side-length "," 0 " "
                               "0,0")
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
    (part modal
          {:part ::modal
           :impl mp/modal-panel
           :props
           {:backdrop-on-click backdrop-on-click
            :parts             {:child-container {:style {:z-index 50}}}
            :child
            (part inner-wrapper
                  {:part ::inner-wrapper
                   :impl box/v-box
                   :props
                   {:src (at)
                    :children
                    [(part top-bar
                           {:part ::top-bar
                            :impl box/h-box
                            :props
                            {:src (at)
                             :children
                             [(part title-wrapper
                                    {:part  ::title-wrapper
                                     :impl  text/title
                                     :props {:label (part title {:part ::title})
                                             :level :level2}})
                              (when closeable? [close-button {:on-click on-close
                                                              :height   "12px"
                                                              :width    "12px"}])]}})
                     (part triangle
                           {:part  ::triangle
                            :props {:tag      :svg
                                    :style    {:width     (px arrow-side-length)
                                               :height    (px arrow-midpoint)
                                               :transform (str "translateX("
                                                               (-> panel-padding (- arrow-midpoint) px) ")")}
                                    :children [[:polygon {:points arrow-points}]]}})
                     (part body
                           {:part  ::body
                            :impl  box/v-box
                            :props {:gap [:<>
                                          [box/gap :size "19px"]
                                          [box/line]
                                          [box/gap :size "7px"]]
                                    :children
                                    [(when header
                                       [:<>
                                        [box/gap :size "19px"]
                                        (part header
                                              {:part ::header})])
                                     [:<>
                                      (when action
                                        (part heading
                                              {:part  ::heading
                                               :impl  text/title
                                               :props {:level :level2
                                                       :label (part action {:part ::action})}}))
                                      (when instructions
                                        (part text
                                              {:part  ::text
                                               :impl  text/p
                                               :props {:children [(part instructions
                                                                        {:part ::instructions})]}}))
                                      (when what-happened
                                        [:<>
                                         (when what-happened-title
                                           (part heading
                                                 {:part  ::heading
                                                  :impl  text/title
                                                  :props {:label (part what-happened-title
                                                                       {:part ::what-happened-title})
                                                          :level :level3}}))
                                         (when what-happened
                                           (part text
                                                 {:part  ::text
                                                  :impl  text/p
                                                  :props {:children [(part what-happened
                                                                           {:part ::what-happened})]}}))])
                                      (when implications
                                        [:<>
                                         (part heading
                                               {:part  ::heading
                                                :impl  text/title
                                                :props {:label (part implications-title
                                                                     {:part ::implications-title
                                                                      :impl [:<> implications-title]})
                                                        :level :level3}})
                                         (part text
                                               {:part  ::text
                                                :impl  text/p
                                                :props {:children [(part implications
                                                                         {:part ::implications
                                                                          :impl [:<> implications]})]}})])
                                      (when what-to-do
                                        [:<>
                                         (part heading
                                               {:part  ::heading
                                                :impl  text/title
                                                :props {:label (part what-to-do-title
                                                                     {:part ::what-to-do-title})
                                                        :level :level3}})
                                         (part text
                                               {:part  ::text
                                                :impl  text/p
                                                :props {:children [(part what-to-do
                                                                         {:part ::what-to-do
                                                                          :impl [:<> what-to-do]})]}})])]
                                     (when (or details error context)
                                       [:<>
                                        (part heading
                                              {:part  ::heading
                                               :props {:label    (part details-title
                                                                       {:part ::details-title})
                                                       :level    :level4
                                                       :children [[:strong {:style {:font-size 12}}
                                                                   (part details-title
                                                                         {:part ::details-title})]]}})
                                        (part code
                                              {:part ::code
                                               :props
                                               {:children
                                                [(when details
                                                   (part details
                                                         {:part ::details}))
                                                 (when error
                                                   (part error
                                                         {:part ::error}))
                                                 (when context
                                                   (part context
                                                         {:part ::context}))]}})])
                                     (when footer
                                       (part footer
                                             {:part ::footer}))]}})]}})}})))
