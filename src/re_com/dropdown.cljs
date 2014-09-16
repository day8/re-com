(ns re-com.dropdown
  ;Extra
  (:require-macros [clairvoyant.core :refer [trace-forms]])
  (:require
    [re-com.util  :as util]
    [clojure.string :as string]
    [clairvoyant.core :refer [default-tracer]]
    [reagent.core :as reagent]))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select


(trace-forms {:tracer default-tracer}
             (defn find-option-index
               [options id]
               "In a vector of maps (where each map has an :id), return the index of the first map containing the id parameter
                Returns nil if id not found"
               (let [index-fn (fn [index item] (when (= (:id item) id) index))
                     index-of-id (first (keep-indexed index-fn options))]
                 index-of-id)))


(trace-forms
  {:tracer default-tracer}
  (defn move-to-new-option
    [options id offset]
    "In a vector of maps (where each map has an :id), return the first map containing the id parameter"
    (let [current-index (find-option-index options id)
          new-index (cond
                      (= offset :start) 0
                      (= offset :end) (dec (count options))
                      (nil? current-index) 0
                      :else (mod (+ current-index offset) (count options)))]
      (:id (nth options new-index)))))


(trace-forms {:tracer default-tracer}
             (defn find-option
               [options id]
               "In a vector of maps (where each map has an :id), return the first map containing the id parameter"
               (let [current-index (find-option-index options id)
                     _ (assert ((complement nil?) current-index) (str "Can't find model index '" id "' in options vector"))]
                 (nth options current-index))))


(defn options-with-headings
  [opts]
  "Converts the user specified data for the dropdown into a form that this code can better work with"
  (let [new-opts   (atom [])
        last-group (atom nil)]
    (doall
      (for [opt opts]
        (let [new-group (not= (:group opt) @last-group)
              _         (reset! last-group (:group opt))]
          (when new-group
            (swap! new-opts conj {:id    (str (:id opt) "##")
                                  :group (:group opt)}))
          (swap! new-opts conj {:id    (:id opt)
                                :label (:label opt)}))
        ))
    @new-opts))


(defn filter-options
  "Filter a list of options based on a filter string using plain string searches (case insensitive). Less powerful
   than regex's but no confusion with reserved characters."
  [options filter-text]
  (let [lower-filter-text (string/lower-case filter-text)
        filter-fn        (fn [opt]
                           (or
                             (>= (.indexOf (string/lower-case (:group opt)) lower-filter-text) 0)
                             (>= (.indexOf (string/lower-case (str (:label opt))) lower-filter-text) 0)))] ;; Need str for non-string labels like hiccup
    (filter filter-fn options)))


(defn filter-options-regex
  "Filter a list of options based on a filter string using regex's (case insensitive). More powerful but can cause
   confusion for users entering reserved characters such as [ ] * + . ( ) etc."
  [options filter-text]
  (let [re        (try
                    (js/RegExp. filter-text "i")
                    (catch js/Object e nil))
        filter-fn (partial (fn [re opt]
                             (when-not (nil? re)
                               (or (.test re (:group opt)) (.test re (:label opt)))))
                           re)]
    (filter filter-fn options)))


(defn option-group-heading
  [opt]
  "Render a group option item"
  [:li.group-result
   {:style {:user-select "none"} ;; Prevent user text selection
    :value (:value opt)}
   (:group opt)])


#_(defn option-item-base
  []
  "Render an option item and set up appropriate mouse events"
  (let [mouse-over? (reagent/atom false)]
    (fn [opt on-click model]
      (let [selected (= @model (:id opt))
            class    (if selected
                       " highlighted"
                       (when @mouse-over? " mouseover"))]
        [:li
         {:class         (str "active-result group-option" class)
          :style         {:-webkit-user-select "none"} ;; Prevent user text selection
          :on-mouse-over #(reset! mouse-over? true)
          :on-mouse-out  #(reset! mouse-over? false)
          :on-mouse-down #(on-click (:id opt))}
         (:label opt)]))))


#_(def option-item (with-meta option-item-base
                            {:component-did-mount #(let [node (reagent/dom-node %)]
                                                    (println "option-item-2 did-mount: " (.-innerText node)))
                             :component-did-update #(let [node (reagent/dom-node %)]
                                                     (println "option-item-2 did-update: " (.-innerText node)))
                             }))


(defn option-item
  [opt on-click model]
  "Render an option item and set up appropriate mouse events"
  (let [mouse-over? (reagent/atom false)]
    #_(println ">IN option-item" (:label opt))
    (reagent/create-class
      {:component-did-mount
        (fn [me]
          (let [node     (reagent/dom-node me)
                selected (= @model (:id opt))]
            (when selected (.scrollIntoView node false))
            #_(println "option-item - did-mount" (:label opt))))

       :component-did-update
        (fn [me old-argv]
          (let [node     (reagent/dom-node me)
                selected (= @model (:id opt))]
            #_(println "option-item - did-update" (:label opt))
            ;; TODO: Only options are to fix the element to the top or bottom of the window. Suggested solution is window.scrollTO() :-( See link below...
            ;; http://social.msdn.microsoft.com/Forums/vstudio/en-US/340637f1-835a-43ed-9724-6eb4b166fdf8/html-scrollintoview-question
            (when selected (.scrollIntoView node false))
            ))

       :render
        (fn [me]
          (let [selected (= @model (:id opt))
                class    (if selected
                           " highlighted"
                           (when @mouse-over? " mouseover"))]
            #_(println "option-item - render" (:label opt) (if selected "*SELECTED*" ""))
            [:li
             {:class         (str "active-result group-option" class)
              :style         {:user-select "none"} ;; Prevent user text selection
              :on-mouse-over #(reset! mouse-over? true)
              :on-mouse-out  #(reset! mouse-over? false)
              :on-mouse-down #(on-click (:id opt))} ;; on-click doesn't work because of blur event
             (:label opt)]))
       })
    ))


(trace-forms {:tracer default-tracer}
             (defn filter-text-box-base
               []
               (fn [filter-text key-handler drop-showing? tmp-model] ;; TODO: Remove tmp-model
                 [:div.chosen-search
                  [:input
                   {:type          "text"
                    :auto-complete "off"
                    :value         @filter-text
                    :on-change     #(reset! filter-text (-> % .-target .-value))
                    :on-focus      #(println @tmp-model @drop-showing? "txt.focus")
                    :on-blur       #(do
                                     (reset! drop-showing? false)
                                     (println @tmp-model @drop-showing? "txt.blur"))
                    :on-key-down   key-handler}]])))


(def filter-text-box
  (with-meta filter-text-box-base
             {:component-did-mount #(let [node (.-firstChild (reagent/dom-node %))]
                                     (println "filter-text-box did-mount:" (.-value node))
                                     (.focus node))
              :component-did-update #(let [node (.-firstChild (reagent/dom-node %))] ;; TODO: REMOVE - did-update not actually required
                                      (println "filter-text-box did-update:" (.-value node))
                                      (.focus node))
              }))


;; TODO: BUG: up/down cycles through full list of options instead of filtered list
;;       This is because the parameters (filter-text key-handler) change from time to time but render is using the
;;       initial values, when the component was first mounted.
;;       So, the question becomes, "how do we pass fresh versions of the parameters to the render function?
#_(defn filter-text-box
  [filter-text key-handler]
  (reagent/create-class
    {:component-did-mount
      (fn [me]
        (let [node (.-firstChild (reagent/dom-node me))]
          #_(println "filter-text-box - did-mount: " (.-value node))
          (.focus node)))

     :component-did-update
      (fn [me old-argv]
        (let [node (.-firstChild (reagent/dom-node me))]
          #_(println "filter-text-box - did-update: " (.-value node))
          (.focus node)))

     :render
      (fn [me]
        (let [argv (reagent/argv me)] ;; TODO: Test code...remove
          [:div.chosen-search
           [:input
            {:type          "text"
             :auto-complete "off"
             :value         @filter-text
             :on-change     #(reset! filter-text (-> % .-target .-value))
             :on-key-down   key-handler
             }]]))
     }))


(trace-forms {:tracer default-tracer}
             (defn dropdown-top-base
               []
               (let [ignore-click (atom false)]
                 (fn
                   [tmp-model options tab-index placeholder dropdown-click key-handler filter-box drop-showing?]
                   (let [_ (reagent/set-state (reagent/current-component) {:filter-box filter-box})]
                     [:a.chosen-single.chosen-default
                      {:style         {:user-select "none"} ;; Prevent user text selection
                       :href          "javascript:"   ;; Required to make this anchor appear in the tab order
                       :tab-index     (when tab-index tab-index)
                       :on-click      #(do
                                        (println @tmp-model @drop-showing? "a.click")
                                        (if @ignore-click
                                          (reset! ignore-click false)
                                          (dropdown-click)))
                       :on-mouse-down #(do
                                        (println @tmp-model @drop-showing? "a.mousedown")
                                        (when (and filter-box @drop-showing?) (reset! ignore-click true))) ;; Clicking anchor when filter-text-box is enabled and drop-down
                       :on-key-down   #(do            ;; is open closes then reopens because of txt.blur event
                                        (println @tmp-model @drop-showing? "a.key")
                                        (key-handler %)
                                        (reset! ignore-click true)) ;; Pressing enter on an anchor also triggers click event, which we don't want
                       :on-focus      #(println @tmp-model @drop-showing? "a.focus") ;; TODO: Remove
                       :on-blur       #(do
                                        (println @tmp-model @drop-showing? "a.blur")
                                        (when-not filter-box (reset! drop-showing? false)))
                       }
                      [:span
                       (if @tmp-model
                         (:label (find-option options @tmp-model))
                         placeholder)]
                      [:div [:b]]])))))               ;; This odd bit of markup produces the visual arrow on the right


(def dropdown-top
  (with-meta dropdown-top-base
             {
               ;:component-did-mount #(let [node       (reagent/dom-node %) ;; TODO: REMOVE - did-mount not actually required
               ;                            filter-box (:filter-box (reagent/state %))]
               ;                       (println "dropdown-top did-mount: " (.-text node))
               ;                       #_(when-not filter-box (.focus node)))
               :component-did-update #(let [node       (reagent/dom-node %)
                                            filter-box (:filter-box (reagent/state %))]
                                       (println "dropdown-top did-update: " (.-text node))
                                       (when-not filter-box (.focus node)))}))


(defn single-dropdown
  [& {:keys [model]}]
  "Render a bootstrap styled choosen"
  (let [tmp-model     (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model)) ;; Create a new atom from the model value passed in for use with keyboard actions
        drop-showing? (reagent/atom false)
        filter-text   (reagent/atom "")]
    (fn [& {:keys [options model on-select disabled filter-box regex-filter placeholder width max-height tab-index]}]
      (let [options          (if (satisfies? cljs.core/IDeref options) @options options)
            save-model       (reagent/atom (if (satisfies? cljs.core/IDeref model) @model model))
            disabled         (if (satisfies? cljs.core/IDeref disabled) @disabled disabled)
            changeable       (and on-select (not disabled))
            callback         #(do
                               (reset! tmp-model %)
                               (when changeable (on-select @tmp-model))
                               (reset! drop-showing? (not @drop-showing?)) ;; toggle to allow opening dropdown on Enter key
                               (reset! filter-text ""))
            cancel           #(do
                               (reset! drop-showing? false)
                               (reset! filter-text "")
                               (reset! tmp-model @save-model))
            dropdown-click   #(when-not disabled
                               (reset! drop-showing? (not @drop-showing?)))
            filtered-options (if regex-filter
                               (filter-options-regex options @filter-text)
                               (filter-options options @filter-text))
            key-handler      #(if (not disabled)
                               (case (.-which %)
                                 13 (do
                                      (println @tmp-model @drop-showing? "enter.key")
                                      (if disabled        ;; Enter key
                                        (cancel)
                                        (callback @tmp-model)))
                                 27 (do
                                      (println @tmp-model @drop-showing? "esc.key")
                                      (cancel))           ;; Esc key
                                 9 (do                   ;; Tab key
                                     (println @tmp-model @drop-showing? "tab.key")
                                     (if disabled
                                       (cancel)
                                       (do   ;; Was (callback @tmp-model) but needed a customised version
                                         (when changeable (on-select @tmp-model))
                                         (reset! drop-showing? false)
                                         (reset! filter-text "")))
                                     (reset! drop-showing? false))
                                 38 (do
                                      (println @tmp-model @drop-showing? "up.key")
                                      (if @drop-showing?  ;; Up arrow
                                        (reset! tmp-model (move-to-new-option filtered-options @tmp-model -1))
                                        (reset! drop-showing? true)))
                                 40 (do
                                      (println @tmp-model @drop-showing? "down.key")
                                      (if @drop-showing?  ;; Down arrow
                                        (reset! tmp-model (move-to-new-option filtered-options @tmp-model 1))
                                        (reset! drop-showing? true)))
                                 36 (when @drop-showing?  ;; Home key
                                      (reset! tmp-model (move-to-new-option filtered-options @tmp-model :start)))
                                 35 (when @drop-showing?  ;; End key
                                      (reset! tmp-model (move-to-new-option filtered-options @tmp-model :end)))
                                 true))]

        ;; TODO: Remove this comment
        ;; TODO: Remove this comment
        ;; TODO: Remove this comment

        [:div
         {:class (str "chosen-container chosen-container-single" (when @drop-showing? " chosen-container-active chosen-with-drop"))
          :style (if width
                   {:flex (str "0 0 " width) :width width}
                   {:flex "auto"})}
         [dropdown-top tmp-model options tab-index placeholder dropdown-click key-handler filter-box drop-showing?]
         (when (and @drop-showing? (not disabled))
           [:div.chosen-drop
            (when filter-box [filter-text-box filter-text key-handler drop-showing? tmp-model]) ;; TODO: Remove tmp-model
            [:ul.chosen-results
             (when max-height {:style {:max-height max-height}})
             (if (-> filtered-options count pos?)
               (for [opt (options-with-headings filtered-options)]
                 (if (:group opt)
                   ^{:key (:id opt)} [option-group-heading opt]
                   ^{:key (:id opt)} [option-item opt callback tmp-model]))
               [:li.no-results (str "No results match \"" @filter-text "\"")])]])]))))
