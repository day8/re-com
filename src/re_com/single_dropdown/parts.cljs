(ns re-com.single-dropdown.parts
  (:require-macros
   [re-com.core     :refer [handler-fn]])
  (:require
   [clojure.string :as str]
   [reagent.core    :as    reagent]
   [goog.string.format]
   [goog.string     :as    gstring]
   [re-com.util :as u]
   [re-com.theme :as theme]
   re-com.single-dropdown.theme
   [re-com.single-dropdown :as-alias sd]))

(defn- insert
  "Return text after insertion in place of selection"
  [& {:keys [text sel-start sel-end ins]}]
  (str (when text (subs text 0 sel-start))
       ins
       (when text (subs text sel-end))))

(defn auto-complete
  "Return text/selection map after insertion in place of selection & completion"
  [& {:keys [choices text sel-start sel-end ins]}]
  (let [text' (insert :text text :sel-start sel-start :sel-end sel-end :ins ins)
        find  #(gstring/caseInsensitiveStartsWith % text')
        ret   (when-first [choice (filter find choices)]
                {:text      (str text' (subs choice (count text')))
                 :sel-start (+ sel-start (count ins))
                 :sel-end   (count choice)})]
    (when (and (not= (:sel-start ret) (:sel-end ret))
               (seq ins))
      ret)))

(defn handle-free-text-insertion
  [{:keys [event ins auto-complete? capitalize? choices internal-model free-text-sel-range free-text-change]}]
  (let [input             (.-target event)
        input-sel-start   (.-selectionStart input)
        input-sel-end     (.-selectionEnd input)
        ins'              (cond-> ins (and capitalize? (zero? input-sel-start)) u/capitalize-first-letter)
        auto-complete-ret (and auto-complete? (auto-complete :choices   choices
                                                             :text      @internal-model
                                                             :sel-start input-sel-start
                                                             :sel-end   input-sel-end
                                                             :ins       ins'))]
    (cond
      auto-complete-ret (let [{:keys [text sel-start sel-end]} auto-complete-ret]
                          (if (= @internal-model text)
                            (.setSelectionRange input sel-start sel-end)
                            (do
                              (reset! free-text-sel-range [sel-start sel-end])
                              (free-text-change text)))
                          (.preventDefault event))
      (not= ins ins')   (do
                          (reset! free-text-sel-range [(+ input-sel-start (count ins)) (+ input-sel-start (count ins))])
                          (free-text-change (insert :text      @internal-model
                                                    :sel-start input-sel-start
                                                    :sel-end   input-sel-end
                                                    :ins       ins'))
                          (.preventDefault event)))))

(defn free-text-dropdown-top-base
  "Base function (before lifecycle metadata) to render the top part of the dropdown (free-text),
  with the editable area and the up/down arrow"
  [{:keys [free-text-input select-free-text? free-text-focused? free-text-sel-range internal-model
           tab-index placeholder dropdown-click key-handler filter-box? drop-showing? cancel width
           free-text-change auto-complete? choices capitalize? disabled?
           class style attr]}]
  [:ul.chosen-choices
   [:li.search-field
    [:div.free-text
     {:style (when disabled?
               {:background-color "#EEE"})}
     [:input
      (-> 
       {:type          "text"
        :auto-complete "off"
        :class         class
        :style         (merge {:width width} style)
        :tab-index     tab-index
        :placeholder   placeholder
        :value         @internal-model
        :disabled      disabled?
        :on-change
        (handler-fn (let [value (-> event .-target .-value)]
                      (free-text-change (cond-> value capitalize? u/capitalize-first-letter))))
        :on-key-down
        (handler-fn (when-not (key-handler event)
                      (.stopPropagation event)
                      (.preventDefault event)))
        :on-key-press  (handler-fn
                         (let [ins (.-key event)]
                           (when (= (count ins) 1) ;; Filter out special keys (e.g. enter)
                             (handle-free-text-insertion
                              {:event               event
                               :ins                 ins
                               :auto-complete?      auto-complete?
                               :capitalize?         capitalize?
                               :choices             choices
                               :internal-model      internal-model
                               :free-text-sel-range free-text-sel-range
                               :free-text-change    free-text-change}))))
        :on-paste      (handler-fn
                         (let [ins (.getData (.-clipboardData event) "Text")]
                           (handle-free-text-insertion
                            {:event               event
                             :ins                 ins
                             :auto-complete?      auto-complete?
                             :capitalize?         capitalize?
                             :choices             choices
                             :internal-model      internal-model
                             :free-text-sel-range free-text-sel-range
                             :free-text-change    free-text-change})))
        :on-focus      (handler-fn
                         (reset! free-text-focused? true)
                         (reset! select-free-text? true))
        :on-blur       (handler-fn
                         (when-not filter-box?
                           (cancel))
                         (reset! free-text-focused? false))  ;; Set free-text-focused? after calling cancel to prevent re-focusing
        :on-mouse-down (handler-fn (when @drop-showing?
                                     (cancel)
                                     (.preventDefault event))) ;; Prevent text selection flicker (esp. with filter-box)
        :ref           #(reset! free-text-input %)}
       (merge attr))]
     [:span.b-wrapper
      {:on-mouse-down (handler-fn
                       (dropdown-click)
                       (when @free-text-focused?
                         (.preventDefault event)))}            ;; Prevent free-text input from loosing focus
      (when (not disabled?)
        [:b])]]]])

(def free-text-dropdown-top
  "Render the top part of the dropdown (free-text), with the editable area and the up/down arrow"
  (with-meta free-text-dropdown-top-base
    {:component-did-update
     #(let [[_ {:keys [free-text-input select-free-text? free-text-focused? free-text-sel-range]}] (reagent/argv %)]
        (when (and @free-text-input @select-free-text? @free-text-focused?)
          (.select @free-text-input))
        (when (and @free-text-input @free-text-sel-range)
          (.setSelectionRange @free-text-input (first @free-text-sel-range) (second @free-text-sel-range))
          (reset! free-text-sel-range nil)))}))

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
