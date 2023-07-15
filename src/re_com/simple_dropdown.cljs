(ns re-com.simple-dropdown
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.dropdown :as    dropdown]
   [re-com.box      :refer [align-style flex-child-style]]
   [re-com.validate :refer [log-warning]]
   [re-com.style    :as    style]
   [re-com.state    :as st]
   [re-com.util     :refer [item-for-id]]
   [clojure.string  :as    string]
   [reagent.core    :as    reagent]
   [reagent.dom     :as    rdom]
   [goog.string     :as    gstring]
   [goog.string.format]))

(def simple-parts-desc
  (when include-args-desc?
    [{:name ::tooltip            :level 0 :class "rc-dropdown-tooltip"            :impl "[popover-tooltip]" :notes "Tooltip for the dropdown, if enabled." :states #{:on :off}}
     {:type ::base               :level 1 :class "rc-dropdown"                    :impl "[:div]"            :notes "The container for the rest of the dropdown."}
     {:name ::chosen-drop        :level 2 :class "rc-dropdown-chosen-drop"        :impl "[:div]"}
     {:name ::chosen-results     :level 3 :class "rc-dropdown-chosen-results"     :impl "[:ul]"}
     {:name ::choices-loading    :level 4 :class "rc-dropdown-choices-loading"    :impl "[:li]"}
     {:name ::choices-error      :level 4 :class "rc-dropdown-choices-error"      :impl "[:li]"}
     {:name ::choices-no-results :level 4 :class "rc-dropdown-choices-no-results" :impl "[:li]"}]))

(defn dropdown-top
  "Render the top part of the dropdown, with the clickable area and the up/down arrow"
  []
  (let [ignore-click (atom false)]
    (fn
      [internal-model choices id-fn label-fn tab-index placeholder dropdown-click _ filter-box? drop-showing? title? disabled?]
      (let [_    (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})
            text (if (some? @internal-model)
                   (label-fn (item-for-id @internal-model choices :id-fn id-fn))
                   placeholder)]
        [:a.chosen-single.chosen-default
         {:style         (when disabled?
                           {:background-color "#EEE"})
          :tab-index     (or tab-index 0)
          :on-click      (handler-fn
                          (if @ignore-click
                            (reset! ignore-click false)
                            (dropdown-click)))
          :on-mouse-down (handler-fn
                          (when @drop-showing?
                            (reset! ignore-click true)))}
         [:span (when title?
                  {:title text})
          text]
         (when (not disabled?)
           [:div [:b]])]))))

(defn- choice-item
  "Render a choice item and set up appropriate mouse events"
  [_ _ _ _]
  (let [mouse-over? (reagent/atom false)]
    (fn [id label on-click chosen?]
      (let [class (if chosen? "highlighted" (when @mouse-over? "mouseover"))]
        [:li
         {:class         ["active-result" "group-option" class]
          :on-mouse-over (handler-fn (reset! mouse-over? true))
          :on-mouse-out  (handler-fn (reset! mouse-over? false))
          :on-mouse-down (handler-fn (on-click id) (.preventDefault event))}
         label]))))

(defn make-choice-item
  [id-fn render-fn on-click chosen? opt]
  (let [id (id-fn opt)
        markup (render-fn opt)]
    ^{:key (str id)} [choice-item id markup on-click chosen?]))

(defn simple-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [choices init]}]
  (let [choices-state    (reagent/atom {:loading? (fn? choices)
                                        :error    nil
                                        :choices  []
                                        :id       0
                                        :timer    nil})
        node              (reagent/atom nil)
        focus-anchor      #(some-> @node (.getElementsByClassName "chosen-single") (.item 0) (.focus))
        state-chart {::st/states ^:& {::dropdown #{::closed ::open}
                                      ::choice   (set choices)}
                     ::st/init {::dropdown ::closed
                                ::choice init}
                     ::st/transitions {::->choose {::dropdown {::open   ::closed}}
                                       ::->open   {::dropdown {::closed ::open}}
                                       ::->cancel {::dropdown {::open   ::closed}}}
                     ::st/actions {::->choose (fn [{old ::choice}
                                                   {new ::choice}
                                                   [value {:keys [on-change repeat-change?]}]]
                                                (when (and on-change (or repeat-change? (not= old new)))
                                                  (on-change new))
                                                (focus-anchor))}}
        machine          (st/machine state-chart)
        some-state       (partial st/some-state machine)
        open?            (some-state ::open)
        transition!      (partial st/transition! machine state-chart)]
    (fn simple-render
      [& {:keys [choices id-fn label-fn render-fn placeholder title? can-drop-above? est-item-height i18n width max-height tab-index class style attr parts]
          :or   {id-fn :id label-fn :label est-item-height 30}
          :as   args}]
      (let [est-drop-height  #(let [items-height  (* (count choices) est-item-height)
                                    drop-margin   12
                                    maxh          (cond
                                                    (not max-height)                    240
                                                    (string/ends-with? max-height "px") (js/parseInt max-height 10)
                                                    :else                               (do (log-warning "max-height is not in pxs, using 240px for estimation")
                                                                                            240))]
                                (min (+ items-height drop-margin)
                                     maxh))
            drop-height      (reagent/track
                              #(if-let [drop-node (and @node
                                                       (.item (.getElementsByClassName @node "chosen-drop") 0))]
                                 (-> drop-node .getBoundingClientRect .-height)
                                 (est-drop-height)))
            top-height       34
            drop-above?      (reagent/track
                              #(when (and can-drop-above? @node)
                                 (let [node-top      (-> @node .getBoundingClientRect .-top)
                                       window-height (-> js/document .-documentElement .-clientHeight)]
                                   (> (+ node-top top-height @drop-height)
                                      window-height))))
            with-part        (partial style/with-part parts)]
        [:div
         (merge
          {:class (str "rc-dropdown chosen-container chosen-container-single noselect "
                       (when @open? "chosen-container-active chosen-with-drop ")
                       class)
           :style (merge (flex-child-style (if width "0 0 auto" "auto"))
                         (align-style :align-self :start)
                         {:width width}
                         style)
           :ref   #(reset! node %)}
          (->attr args)
          attr)
         [dropdown-top (::choice @machine) choices id-fn label-fn tab-index placeholder #(transition! ::->open) (constantly nil) false open? title? false]
         (when (#{::open} (::dropdown @machine))
           [:div
            (merge
             {:class (str "chosen-drop rc-dropdown-chosen-drop " (get-in parts [:chosen-drop :class]))
              :style (merge (when @drop-above? {:transform (gstring/format "translate3d(0px, -%ipx, 0px)" (+ top-height @drop-height -2))})
                            (get-in parts [:chosen-drop :style]))})
            [:ul
             (with-part :chosen-results
               {:class ["chosen-results" "rc-dropdown-chosen-results"]
                :style (when max-height {:max-height max-height})})
             (cond
               (and false (:loading? @choices-state))
               [:li
                (merge
                 {:class (str "loading rc-dropdown-choices-loading " (get-in parts [:choices-loading :class]))
                  :style (get-in parts [:choices-loading :style] {})}
                 (get-in parts [:choices-loading :attr]))
                (get i18n :loading "Loading...")]
               (and false (:error @choices-state))
               [:li
                (merge
                 {:class (str "error rc-dropdown-choices-error " (get-in parts [:choices-error :class]))
                  :style (get-in parts [:choices-error :style] {})}
                 (get-in parts [:choices-error :attr]))
                (:error @choices-state)]
               (-> choices count pos?)
               (let [make-a-choice (partial make-choice-item id-fn render-fn #(transition! :->choose % args))]
                 (map make-a-choice choices))
               :else
               [:li
                (merge
                 {:class (str "no-results rc-dropdown-choices-no-results " (get-in parts [:choices-no-results :class]))
                  :style (get-in parts [:choices-no-results :style] {})}
                 (get-in parts [:choices-no-results :attr]))
                (gstring/format (or (:no-results-match i18n)
                                    "No results match \"%s\"")
                                "")])]])]))))
