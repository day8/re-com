(ns re-com.simple-dropdown
  (:require-macros
   [re-com.core     :refer [handler-fn at]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.dropdown :as    dropdown]
   [re-com.box      :refer [align-style flex-child-style]]
   [re-com.validate :refer [log-warning]]
   [re-com.style    :as    style]
   [re-com.popover  :refer [popover-tooltip]]
   [clojure.string  :as    string]
   [reagent.core    :as    reagent]
   [goog.string     :as    gstring]
   [goog.string.format]))

(def simple-parts-desc
  (when include-args-desc?
    [{:name :tooltip            :level 0 :class "rc-dropdown-tooltip"            :impl "[popover-tooltip]" :notes "Tooltip for the dropdown, if enabled." :states #{:on :off}}
     {:type :legacy             :level 1 :class "rc-dropdown"                    :impl "[:div]"            :notes "The container for the rest of the dropdown."}
     {:name :chosen-drop        :level 2 :class "rc-dropdown-chosen-drop"        :impl "[:div]"}
     {:name :chosen-results     :level 3 :class "rc-dropdown-chosen-results"     :impl "[:ul]"}
     {:name :choices-loading    :level 4 :class "rc-dropdown-choices-loading"    :impl "[:li]"}
     {:name :choices-error      :level 4 :class "rc-dropdown-choices-error"      :impl "[:li]"}
     {:name :choices-no-results :level 4 :class "rc-dropdown-choices-no-results" :impl "[:li]"}]))

(defn simple-dropdown
  "Render a single dropdown component which emulates the bootstrap-choosen style. Sample choices object:
     [{:id \"AU\" :label \"Australia\"      :group \"Group 1\"}
      {:id \"US\" :label \"United States\"  :group \"Group 1\"}
      {:id \"GB\" :label \"United Kingdom\" :group \"Group 1\"}
      {:id \"AF\" :label \"Afghanistan\"    :group \"Group 2\"}]"
  [& {:keys [choices model just-drop?]
      :as   args}]
  (let [external-model   (reagent/atom model)
        internal-model   (reagent/atom model)
        showing?         (reagent/atom (boolean just-drop?))
        choices-state    (reagent/atom {:loading? (fn? choices)
                                        :error    nil
                                        :choices  []
                                        :id       0
                                        :timer    nil})
        over?            (reagent/atom false)
        popover-showing? (reagent/track #(and (not @showing?) @over?))
        node             (reagent/atom nil)
        focus-anchor     #(some-> @node (.getElementsByClassName "chosen-single") (.item 0) (.focus))
        state            (reagent/reaction {:chosen-drop :chosen})]
    (fn simple-render
      [& {:keys [choices model on-change id-fn label-fn group-fn render-fn disabled? filter-box? placeholder title? can-drop-above? est-item-height repeat-change? i18n on-drop width max-height tab-index tooltip tooltip-position class style attr parts]
          :or   {id-fn :id label-fn :label group-fn :group render-fn label-fn est-item-height 30}
          :as   args}]
      (let [enabled?         (not disabled?)
            latest-ext-model (reagent/atom model)
            new-choice?      (not= @internal-model @latest-ext-model)
            _                (when new-choice?
                               (reset! external-model @latest-ext-model)
                               (reset! internal-model @latest-ext-model))
            on-choice        #(do
                                (reset! internal-model %)
                                (when (and on-change enabled? (or new-choice? repeat-change?))
                                  (reset! external-model @internal-model)
                                  (on-change @internal-model))
                                (when @showing? (focus-anchor)))
            cancel           #(do
                                (reset! showing? false)
                                (reset! internal-model @external-model))
            dropdown-click   #(when enabled?
                                (if @showing?
                                  (cancel)
                                  (reset! showing? true)))
            est-drop-height  #(let [items-height  (* (count choices) est-item-height)
                                    drop-margin   12
                                    filter-height 32
                                    maxh          (cond
                                                    (not max-height)                    240
                                                    (string/ends-with? max-height "px") (js/parseInt max-height 10)
                                                    :else                               (do (log-warning "max-height is not in pxs, using 240px for estimation")
                                                                                            240))]
                                (min (+ items-height drop-margin (if filter-box? filter-height 0))
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
            key-handler      (constantly nil)
            _                (when tooltip (add-watch showing? :tooltip #(reset! over? false)))
            _                (when on-drop (add-watch showing? :on-drop #(when (and (not %3) %4) (on-drop))))
            with-part        (partial style/with-part parts)
            dropdown         [:div
                              (merge
                               {:class (str "rc-dropdown chosen-container chosen-container-single noselect "
                                            (when @showing? "chosen-container-active chosen-with-drop ")
                                            class)
                                :style (merge (flex-child-style (if width "0 0 auto" "auto"))
                                              (align-style :align-self :start)
                                              {:width width}
                                              style)
                                :ref   #(reset! node %)}
                               (when tooltip
                                 {:on-mouse-over (handler-fn (reset! over? true))
                                  :on-mouse-out  (handler-fn (reset! over? false))})
                               (->attr args)
                               attr)
                              [dropdown/dropdown-top internal-model choices id-fn label-fn tab-index placeholder dropdown-click key-handler filter-box? showing? title? disabled?]
                              (when (and @showing? enabled?)
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
                                    (let [[group-names group-opt-lists] (dropdown/choices-with-group-headings choices group-fn)
                                          make-a-choice                 (partial dropdown/make-choice-item id-fn render-fn on-choice internal-model)
                                          make-choices                  #(map make-a-choice %1)
                                          make-h-then-choices           (fn [h opts]
                                                                          (cons (dropdown/make-group-heading h)
                                                                                (make-choices opts)))
                                          has-no-group-names?           (nil? (:group (first group-names)))]
                                      (if (and (= 1 (count group-opt-lists)) has-no-group-names?)
                                        (make-choices (first group-opt-lists))
                                        (apply concat (map make-h-then-choices group-names group-opt-lists))))
                                    :else
                                    [:li
                                     (merge
                                      {:class (str "no-results rc-dropdown-choices-no-results " (get-in parts [:choices-no-results :class]))
                                       :style (get-in parts [:choices-no-results :style] {})}
                                      (get-in parts [:choices-no-results :attr]))
                                     (gstring/format (or (:no-results-match i18n)
                                                         "No results match \"%s\"")
                                                     "")])]])]]
        (if tooltip
          [popover-tooltip
           :src      (at)
           :label    tooltip
           :position (or tooltip-position :below-center)
           :showing? popover-showing?
           :anchor   dropdown
           :class    (str "rc-dropdown-tooltip " (get-in parts [:tooltip :class]))
           :style    (get-in parts [:tooltip :class])
           :attr     (get-in parts [:tooltip :attr])]
          dropdown)))))
