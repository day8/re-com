(ns re-com.single-dropdown.parts
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [reagent.core    :as    reagent]
   [goog.string.format]
   [goog.string     :as    gstring]
   [re-com.util :as u]
   [re-com.theme :as theme]
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
                    (-> {:style         style
                         :class         class
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

(defn chosen-search
  "Render a filter text box"
  [_]
  (let [focus! #(when % (.focus %))]
    (fn [{:keys [model key-handler drop-showing? on-change filter-placeholder
                 class style attr children]}]
      (into
       [:div.chosen-search
        [:input
         (merge
          {:type          "text"
           :class         class
           :style         style
           :auto-complete "off"
           :value         (u/deref-or-value model)
           :placeholder   filter-placeholder
           :on-change     (handler-fn (on-change (-> event .-target .-value)))
           :on-key-down   (handler-fn (when-not (key-handler event)
                                        (.stopPropagation event)
                                        (.preventDefault event)))
           :on-blur       (handler-fn (reset! drop-showing? false))
           :ref           focus!}
          attr)]]
       children))))

(defn show-selected-item
  [node]
  (let [item-offset-top       (.-offsetTop node)
        item-offset-bottom    (+ item-offset-top (.-clientHeight node))
        parent                (.-parentNode node)
        parent-height         (.-clientHeight parent)
        parent-visible-top    (.-scrollTop parent)
        parent-visible-bottom (+ parent-visible-top parent-height)
        new-scroll-top        (cond
                                (> item-offset-bottom parent-visible-bottom) (max (- item-offset-bottom parent-height) 0)
                                (< item-offset-top parent-visible-top)       item-offset-top)]
    (when new-scroll-top (set! (.-scrollTop parent) new-scroll-top))))

(defn choice-item
  "Render a choice item and set up appropriate mouse events"
  [{:keys [id model]}]
  (let [mouse-over? (reagent/atom false)
        !ref        (atom nil)
        ref!        (partial reset! !ref)
        show!       #(when (= @model id) (show-selected-item @!ref))]
    (reagent/create-class
     {:component-did-mount  show!
      :component-did-update show!
      :display-name         "choice-item"
      :reagent-render
      (fn [{:keys [id label on-click model]}]
        (let [selected (= @model id)
              class    (if selected
                         "highlighted"
                         (when @mouse-over? "mouseover"))]
          [:li
           {:class         (str "active-result group-option " class)
            :ref           ref!
            :on-mouse-over (handler-fn (reset! mouse-over? true))
            :on-mouse-out  (handler-fn (reset! mouse-over? false))
            :on-mouse-down (handler-fn
                            (on-click id)
                            (.preventDefault event))}         ;; Prevent free-text input as well as the normal dropdown from losing focus
           label]))})))
