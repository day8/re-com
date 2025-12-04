(ns re-com.single-dropdown.parts
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [reagent.core    :as    reagent]
   [goog.string.format]
   re-com.single-dropdown.theme
   [re-com.single-dropdown :as-alias sd]))

(defn chosen-single
  [_]
  (let [ignore-click (atom false)]
    (fn
      [{:keys [tab-index dropdown-click key-handler filter-box? drop-showing? disabled?
               attr class style children]}]
      (let [_      (reagent/set-state (reagent/current-component) {:filter-box? filter-box?})
            anchor [:a
                    (-> {:re-com        {:state {:interaction (if disabled? :disabled :enabled)}}
                         :style         style
                         :class         class
                         :part          ::sd/chosen-single
                         :tab-index     (or tab-index 0)
                         :on-click      (handler-fn
                                         (if @ignore-click
                                           (reset! ignore-click false)
                                           (dropdown-click)))
                         ;; TODO: Hmmm, have a look at calling preventDefault (and stopProp?)
                         ;; and removing the ignore-click stuff
                         :on-mouse-down (handler-fn
                                         (when @drop-showing?
                                           (reset! ignore-click true)))
                         ;; Pressing enter on an anchor also triggers click event, which we don't want
                         :on-key-down   (handler-fn
                                         (key-handler event)
                                         (when (= (.-key event) "Enter")
                                           (reset! ignore-click true)))}
                        (merge attr))]]
        (into anchor children)))))

(defn filter-text-box
  "Render a filter text box"
  [_]
  (let [focus! #(when % (.focus %))]
    (fn [{:keys [filter-text key-handler drop-showing? set-filter-text filter-placeholder
               class style attr children]}]
      (into
       [:div.chosen-search
        [:input
         (merge
          {:type          "text"
           :class         class
           :style         style
           :auto-complete "off"
           :value         @filter-text
           :placeholder   filter-placeholder
           :on-change     (handler-fn (set-filter-text (-> event .-target .-value)))
           :on-key-down   (handler-fn (when-not (key-handler event)
                                        (.stopPropagation event)
                                        (.preventDefault event))) ;; When key-handler returns false, preventDefault
           :on-blur       (handler-fn (reset! drop-showing? false))
           :ref           focus!}
          attr)]]
       children))))
