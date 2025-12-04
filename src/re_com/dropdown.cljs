(ns re-com.dropdown
  (:require-macros
   [re-com.core     :refer [handler-fn at]])
  (:require
   [re-com.config   :refer [include-args-desc?]]
   [re-com.debug    :refer [->attr]]
   [re-com.theme    :as    theme]
   [re-com.util     :as    u :refer [deref-or-value position-for-id item-for-id]]
   [re-com.box      :refer [flex-child-style v-box h-box gap]]
   [re-com.validate :refer [vector-of-maps? css-style? css-class? html-attr? parts? number-or-string? log-warning
                            string-or-hiccup? position? position-options-list part?] :refer-macros [validate-args-macro]]
   [re-com.part     :as p]
   [clojure.string  :as    string]
   [reagent.core    :as    reagent]
   [goog.string     :as    gstring]
   [goog.string.format]
   re-com.dropdown.theme
   [re-com.dropdown.parts :as dp]
   [re-com.single-dropdown :as sd]
   re-com.single-dropdown.theme))

;;  Inspiration: http://alxlit.name/bootstrap-chosen
;;  Alternative: http://silviomoreto.github.io/bootstrap-select

(def dropdown-parts-desc
  (when include-args-desc?
    [{:impl "[v-box]"
      :level 0
      :name :wrapper
      :notes "Outer wrapper."}
     {:name :backdrop
      :impl "user-defined"
      :level 1
      :notes "Transparent, clickable backdrop. Shown when the dropdown is open."}
     {:name :anchor-wrapper
      :impl "[box]"
      :level 1
      :notes "Wraps the :anchor part. Opens or closes the dropdown when clicked."}
     {:name :anchor
      :impl "user-defined"
      :level 2
      :notes "Displays the :label or :placeholder."}
     {:name :indicator
      :impl "user-defined"
      :level 3
      :notes "Displays an arrow, indicating whether the dropdown is open."}
     {:name :body-wrapper
      :impl "[box]"
      :level 1
      :notes "Shown when the dropdown is open. Provides intelligent positioning."}
     {:name :body-header
      :impl "user-defined"
      :level 2}
     {:name :body-footer
      :impl "user-defined"
      :level 2}
     {:name :body
      :impl "user-defined"
      :level 2}]))

(def dropdown-parts
  (when include-args-desc?
    (-> (map :name dropdown-parts-desc) set)))

(def dropdown-args-desc
  (when include-args-desc?
    [{:description "True when the dropdown is open."
      :name        :model
      :required    false
      :type        "boolean | r/atom"}
     {:description
      "Called when the dropdown opens or closes."
      :name        :on-change
      :required    false
      :type        "boolean -> nil"
      :validate-fn fn?}
     {:name        :anchor
      :default     "re-com.dropdown/anchor"
      :type        "part"
      :validate-fn part?
      :required?   false
      :description
      [:span "String, hiccup or function. When a function, acceps keyword args "
       [:code ":placholder"] ", "
       [:code ":label"] ", "
       [:code ":theme"] ", "
       [:code ":parts"] ", "
       [:code ":state"] " "
       " and "
       [:code ":transition!"]
       ". Returns either a string or hiccup, which shows within the clickable dropdown box."]}
     {:name        :indicator
      :required?   false
      :default     "re-com.dropdown/indicator"
      :type        "part"
      :validate-fn part?
      :description "A triangle, indicating whether the dropdown is open or closed."}
     {:name        :backdrop
      :required?   false
      :default     "re-com.dropdown/backdrop"
      :type        "part"
      :validate-fn part?
      :description "Renders a visual overlay, behind the `:anchor` and `:body` parts, when the dropdown is open."}
     {:name :show-backdrop?
      :required? false
      :type "boolean"
      :validate-fn boolean?
      :description "When true, the `:backdrop` part will be rendered when the dropdown is open."}
     {:name        :body
      :required?   true
      :type        "part"
      :validate-fn part?
      :description (str "Displays when the dropdown is open. "
                        "Appears either above or below the :anchor, "
                        "depending on available screen-space. When a function, "
                        ":body is passed the same keyword arguments as :anchor.")}
     {:name        :body-header
      :type        "part"
      :validate-fn part?
      :description "Appears at the top of the :body part."}
     {:name        :body-footer
      :type        "part"
      :validate-fn part?
      :description "Appears at the bottom of the :body part."}
     {:name        :offset-x
      :type        "number"
      :description [:span [:code ":dropdown"] " adds this to the body's dynamically-generated x-position."]}
     {:name        :offset-y
      :type        "number"
      :description [:span [:code ":dropdown"] " adds this to the body's dynamically-generated y-position."]}
     {:name     :disabled?
      :required false
      :type     "boolean | r/atom"}
     {:name     :direction
      :required false
      :default  :toward-center
      :type     "keyword"
      :description [:span "Determines how to position the "
                    [:code ":body"] " part. " [:code ":toward-center"]
                    " dynamically re-positions it, while "
                    [:code ":up"] " and " [:code ":down"]
                    " force it toward a static direction."]}
     {:default     0
      :description "component's tabindex. A value of -1 removes from order"
      :name        :tab-index
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "height of the :anchor-wrapper part"
      :name        :anchor-height
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "width of the :anchor-wrapper part"
      :name        :anchor-width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "height of the :body-wrapper part"
      :name        :body-height
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "width of the :body-wrapper part"
      :name        :body-width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "height of the :body-wrapper part"
      :name        :height
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "min-height of the :body-wrapper part"
      :name        :min-height
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "max-height of the :body-wrapper part"
      :name        :max-height
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "width of the :anchor-wrapper and :body-wrapper parts"
      :name        :width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "height of the :body-wrapper part"
      :name        :width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "min-width of the :anchor-wrapper and :body-wrapper parts"
      :name        :min-width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description "max-width of the :anchor-wrapper and :body-wrapper parts"
      :name        :max-width
      :required    false
      :type        "integer | string"
      :validate-fn number-or-string?}
     {:description (str "passed as a prop to the :anchor part. The default :anchor "
                        "part will display :label inside a the clickable dropdown box.")
      :name        :label
      :required    false
      :type        "string | hiccup"}
     {:default     "\"Select an item\""
      :description (str "passed as a prop to the :anchor part. The default :anchor part will "
                        "show :placeholder in the clickable box if there is no :label.")
      :name        :placeholder
      :required    false
      :type        "string | hiccup"}
     {:description "See Parts section below."
      :name        :parts
      :required    false
      :type        "map"
      :validate-fn (parts? dropdown-parts)}
     {:description "See Parts section below."
      :name        :style
      :required    false
      :type        "map"}
     {:description "See Parts section below."
      :name        :class
      :required    false
      :type        "string | vector"
      :validate-fn (some-fn string? vector?)}
     {:name        :attr
      :required    false
      :type        "map"}
     {:name        :pre-theme
      :required    false}
     {:name        :theme
      :description "alpha"}]))

(defn anchor [{:keys [label placeholder style class attr transition!]}]
  [:span (merge {:style style :class class} attr)
   (or label placeholder)])

(defn backdrop [{:keys [attr class style]}]
  [:div (merge {:style style :class class} attr)])

(defn nearest [x a b]
  (if (< (Math/abs (- a x)) (Math/abs (- b x))) a b))

(defn optimize-position!
  "Returns an [x y] position for body, relative to anchor.
  Considers two possible vertical positions - above or below the anchor.
  If one vertical position clips outside the viewport, chooses the opposite position.
  If both vertical positions clip, picks the vertical position whose midpoint
  is nearest the viewport's midpoint.
  Calculates a left-justified horizontal position, constrained by the viewport width
  and the right edge of the anchor.

  In other words, the body slides left & right within the anchor width,
  and blinks up & down, to find the least cut-off position."
  [anchor-el body-el & {:keys [direction offset-y offset-x]}]
  (let [a-rect       (.getBoundingClientRect anchor-el)
        a-x          (.-x a-rect)
        a-y          (.-y a-rect)
        a-h          (.-height a-rect)
        a-w          (.-width a-rect)
        a-bot        (.-bottom a-rect)
        b-h          (.-offsetHeight body-el)
        b-w          (.-offsetWidth body-el)
        w-h          js/window.innerHeight
        v-mid-y      (/ w-h 2)
        lo-mid-y     (+ a-bot (/ b-w 2))
        hi-mid-y     (- a-y (/ b-h 2))
        bot-clipped? (< w-h (+ a-bot b-h))
        top-clipped? (neg? (- a-y b-h))
        top-best?    (= hi-mid-y (nearest v-mid-y lo-mid-y hi-mid-y))
        v-pos        (cond
                       (not (or top-clipped? bot-clipped?)) :low
                       (and top-clipped? bot-clipped?)      :low
                       bot-clipped?                         :high
                       top-clipped?                         :low
                       top-best?                            :high
                       :else                                :low)
        left-bound   (max (- a-x) 0)
        right-bound  (max (- a-w b-w) 0)
        hi-y         (- b-h)
        lo-y         a-h
        best-x       (min left-bound right-bound)
        best-y       (case v-pos :low lo-y :high hi-y)
        best-y       (case direction :up hi-y :down lo-y best-y)]
    (->> [best-x best-y]
         (map + [a-x a-y])
         (mapv + [offset-x offset-y]))))

(defn body-wrapper [{:keys [anchor-ref body-ref anchor-position direction parts offset-y offset-x]}]
  (let [set-body-ref!      #(reset! body-ref %)
        optimize-position! #(reset! anchor-position
                                    (optimize-position! @anchor-ref @body-ref
                                                        {:direction direction
                                                         :offset-y (u/deref-or-value offset-y)
                                                         :offset-x (u/deref-or-value offset-x)}))
        animation-id       (atom nil)
        start-loop!        (fn start-loop []
                             (reset! animation-id
                                     (js/requestAnimationFrame
                                      (fn []
                                        (optimize-position!)
                                        (start-loop)))))
        stop-loop!         #(when-let [id @animation-id]
                              (js/cancelAnimationFrame id)
                              (reset! animation-id nil))]
    (reagent/create-class
     {:component-did-mount    #(do (optimize-position!)
                                   (start-loop!))
      :component-will-unmount #(do (stop-loop!)
                                   (reset! anchor-position nil))
      :reagent-render
      (fn [{:keys [theme children post-props]}]
        (let [[left top] (deref anchor-position)]
          (p/part (get parts :body-wrapper (get parts ::body-wrapper))
            {:theme      theme
             :part       ::body-wrapper
             :props
             {:position    :fixed
              :anchor-top  top
              :anchor-left left
              :top         top
              :left        left
              :attr        {:ref set-body-ref!}
              :children    children}
             :post-props post-props})))})))

(defn click-outside? [element event]
  (let [target (.-target event)]
    (not (.contains element target))))

(defn dropdown
  "A clickable anchor above an openable, floating body."
  [& {:keys [model offset-x offset-y] :or {model (reagent/atom nil)}}]
  (let [default-model                                  model
        [focused? anchor-ref body-ref anchor-position] (repeatedly #(reagent/atom nil))
        anchor-ref!                                    #(reset! anchor-ref %)
        transitionable                                 (reagent/atom
                                                        (if (deref-or-value model) :in :out))]
    (fn dropdown-render
      [& {:keys [disabled? on-change tab-index direction
                 anchor-height anchor-width
                 body-height   body-width
                 model
                 show-backdrop?
                 label placeholder
                 parts theme pre-theme]
          :or   {placeholder "Select an item"
                 model       default-model
                 direction   :toward-center}
          :as   args}]
      (or (validate-args-macro dropdown-args-desc args)
          (let [state {:openable       (if (deref-or-value model) :open :closed)
                       :enable         (if disabled? :disabled :enabled)
                       :tab-index      tab-index
                       :focusable      (if (deref-or-value focused?) :focused :blurred)
                       :transitionable @transitionable}]
            (letfn [(open! []
                      (on-change true)
                      (.addEventListener js/document "click" on-document-click)
                      (transition! :enter))
                    (open-default! []
                      (reset! model true)
                      (.addEventListener js/document "click" on-document-click)
                      (transition! :enter))
                    (close! []
                      (on-change false)
                      (.removeEventListener js/document "click" on-document-click)
                      (transition! :exit))
                    (close-default! []
                      (reset! model false)
                      (.removeEventListener js/document "click" on-document-click)
                      (transition! :exit))
                    (transition! [k]
                      (case k
                        :toggle (if (-> state :openable (= :open))
                                  ((if on-change close! close-default!))
                                  ((if on-change open! open-default!)))
                        :open   ((if on-change open! open-default!))
                        :close  ((if on-change close! close-default!))
                        :focus  (reset! focused? true)
                        :blur   (reset! focused? false)
                        :enter  (do
                                  (reset! transitionable :entering)
                                  (js/setTimeout (fn [] (reset! transitionable :in)) 100))
                        :exit   (do
                                  (reset! transitionable :exiting)
                                  (js/setTimeout (fn [] (reset! transitionable :out)) 100))))
                    (on-document-click [event]
                      (when (and @anchor-ref
                                 @body-ref
                                 (click-outside? @anchor-ref event)
                                 (click-outside? @body-ref event))
                        (transition! :close)))]
              (let [part-props {:placeholder placeholder
                                :transition! transition!
                                :label       label
                                :theme       theme
                                :parts       parts
                                :state       state
                                :re-com      {:state state}
                                :indicator   dp/indicator}
                    theme      (theme/comp pre-theme theme)
                    parts      (merge parts
                                      (select-keys args [:body ::body
                                                         :anchor ::anchor
                                                         :wrapper ::wrapper
                                                         :backdrop ::backdrop
                                                         :anchor-wrapper ::anchor-wrapper
                                                         :body-header ::body-header
                                                         :body-footer ::body-footer]))
                    part       (fn [id & {:as opts}]
                                 (p/part (get parts id
                                              (get parts (keyword (name id))))
                                   (merge opts {:theme theme
                                                :part  id})))]
                (part ::wrapper
                  {:impl       v-box
                   :post-props {:class (:class args)
                                :style (:style args)
                                :attr  (:attr args)}
                   :props
                   {:src (at)
                    :children
                    [(when (and show-backdrop? (not= :out (:transitionable state)))
                       (part ::backdrop
                         {:theme theme
                          :props part-props
                          :impl  re-com.dropdown/backdrop}))
                     (part ::anchor-wrapper
                       {:impl       h-box
                        :props      {:src      (at)
                                     :re-com   {:state       state
                                                :transition! transition!}
                                     :attr     {:ref anchor-ref!}
                                     :children [(part ::anchor {:props part-props
                                                                :impl  re-com.dropdown/anchor})
                                                [gap :size "1"]
                                                [gap :size "5px"]
                                                (part ::indicator {:props part-props
                                                                   :impl  dp/indicator})]}
                        :post-props {:style (merge (select-keys args [:width :min-width :max-width])
                                                   (when anchor-height {:height anchor-height})
                                                   (when anchor-width {:width anchor-width}))}})
                     (when (= :open (:openable state))
                       [body-wrapper
                        {:anchor-ref      anchor-ref
                         :offset-x        offset-x
                         :offset-y        offset-y
                         :body-ref        body-ref
                         :anchor-position anchor-position
                         :direction       direction
                         :parts           parts
                         :theme           theme
                         :post-props      {:style (merge (select-keys args [:width :min-width #_:max-width
                                                                            :height :min-height :max-height])
                                                         (when body-height {:height body-height})
                                                         (when body-width {:width body-width}))}
                         :children        [(part ::body-header {:props part-props})
                                           (part ::body {:props part-props})
                                           (part ::body-footer {:props part-props})]}])]}}))))))))
