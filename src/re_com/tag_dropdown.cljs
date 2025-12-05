(ns re-com.tag-dropdown
  (:require-macros
   [re-com.core     :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [goog.string           :as gstring]
   [reagent.core          :as reagent]
   [re-com.core           :as-alias rc]
   [re-com.config         :refer [include-args-desc?]]
   [re-com.debug          :as debug]
   [re-com.theme          :as theme]
   [re-com.util           :refer [deref-or-value px-n assoc-in-if-empty]]
   [re-com.validate       :as validate :refer [parts?]]
   [re-com.box            :refer [box h-box v-box gap]]
   [re-com.selection-list :as    sl]
   [re-com.close-button   :refer [close-button]]
   [re-com.text           :refer [label]]
   [re-com.popover        :refer [popover-content-wrapper popover-anchor-wrapper]]
   re-com.tag-dropdown.theme
   [re-com.part           :as p]
   [re-com.tag-dropdown   :as-alias td]))

;; TODO: [GR] Ripped off from re-com.selection-list
(defn label-style
  ([selected? as-exclusions?]
   (label-style selected? as-exclusions? nil))

  ([selected? as-exclusions? selected-color]
    ;;TODO: margin-top required because currently checkbox & radio-button don't center label
   (let [base-style {:margin-top "1px"
                     :color "#333"}
         base-style (if (and selected? as-exclusions?)
                      (merge base-style {:text-decoration "line-through"})
                      base-style)
         base-style (if (and selected? selected-color)
                      (merge base-style {:color selected-color})
                      base-style)]
     base-style)))

(defn text-tag
  []
  (let [over? (reagent/atom false)]
    (fn text-tag-render
      [& {:keys [tag-data on-click on-unselect tooltip id-fn label-fn description-fn hover-style disabled? class style attr parts]
          :or   {id-fn          :id
                 label-fn       :label}}]
      (let [tag-id        (id-fn tag-data)
            tag-id-kw     (keyword tag-id)
            clickable?    (and (some? on-click) (not disabled?))
            unselectable? (and (some? on-unselect) (not disabled?))
            placeholder?  (= (:id tag-data) :$placeholder$)
            border        (when placeholder? "1px dashed #828282")
            tag-label     (label-fn tag-data)
            tag-description (when description-fn (description-fn tag-data))]
        [v-box
         :src      (at)
         :class    (str "rc-tag-dropdown-tag " (get-in parts [:tag :class]) " " (get-in parts [tag-id-kw :class]))
         :style    (merge (get-in parts [:tag :style]) (get-in parts [tag-id-kw :style]))
         :attr     (merge (get-in parts [:tag :attr]) (get-in parts [tag-id-kw :attr]))
         :align :start
         :children [[h-box
                     :src        (at)
                     :align-self :start
                     :justify    (if placeholder? :end :center)

                     ;:width    (if placeholder? (:width tag-data) width)

                     :min-width (when placeholder? (:width tag-data))

                     :padding "0px 4px"
                     :margin  (px-n 2 (if placeholder? 0 6) 2 0)
                     :class (theme/merge-class "noselect" "rc-text-tag" class)
                     :style (merge
                             {:color            "white"
                              :background-color (:background-color tag-data)
                              :cursor           (if (not  disabled?) "pointer" "default")
                              :font-size        "12px"
                               ;:font-weight      "bold"
                              :border           border
                              :border-radius    "3px"}
                             (when (and @over? (not disabled?)) hover-style)
                             style)
                     :attr (merge
                            {:title          tooltip
                             :on-click       (handler-fn (when (and placeholder? (not disabled?)) (on-click (:id tag-data))))
                             :on-mouse-enter (handler-fn (reset! over? true))
                             :on-mouse-leave (handler-fn (reset! over? false))}
                            attr)
                     :children [(if placeholder?
                                  [box
                                   :src   (at)
                                   :style {:color "hsl(194, 61%, 85%)"}
                                   :child (gstring/unescapeEntities "&#9660;")]
                                  [box
                                   :src   (at)
                                   :style {:cursor (when clickable? "pointer")}
                                   :attr  {:on-click (handler-fn
                                                      (when clickable?
                                                        (on-click (:id tag-data)))
                                                      #_(.stopPropagation event))}
                                   :child (or tag-label "???")])
                                (when (and unselectable? (not placeholder?))
                                  [h-box
                                   :src      (at)
                                   :align    :center
                                   :children [[box
                                               :src   (at)
                                               :style {:margin-left "4px"
                                                       :margin-right "3px"}
                                               :child "|"]
                                              [close-button
                                               :src         (at)
                                               :color       "white"
                                               :hover-color "#ccc"
                                               :div-size    13
                                               :font-size   13
                                               :top-offset  1
                                               :on-click    #(when unselectable?
                                                               (on-unselect (:id tag-data)))]]])]]
                    (when tag-description
                      [:span
                       {:style {:color "#586069"}}
                       tag-description])]]))))

(def part-structure
  [::td/popover-anchor-wrapper
   [::td/main {:class "rc-tag-dropdown" :impl `rc/h-box}
    [::td/tags {:impl `rc/h-box}
     [::td/tag {:notes [:span "Each individual tag can be independently targeted "
                        "with the keyword of its " [:code ":id"]]}]
     [::td/placeholder-tag]
     [::td/placeholder-message]]
    [::td/actions {:impl `rc/h-box}
     [::td/gap]
     [::td/counter {:impl `rc/label
                    :notes [:span "The selection counter displayed to the left "
                            "of the close button"]}]
     [::td/close-button {:impl `rc/close-button}]]]
   [::td/popover-content-wrapper
    [::td/selection-list {:impl `rc/selection-list}
     [::td/text-tag {:impl `td/text-tag}]]]])

(def parts-desc
  (when include-args-desc?
    (into
     (p/describe part-structure)
     (->>
      sl/parts-desc
      (remove #(= :legacy (:type %)))
      (map #(update % :level (comp inc inc)))))))

(def tag-dropdown-parts
  (when include-args-desc?
    (-> (map :name parts-desc) set)))

(def tag-dropdown-args-desc
  (when include-args-desc?
    [{:name :choices            :required true                          :type "vector of maps | r/atom" :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, label, short label and tag background color are extracted by the functions " [:code ":id"] ", " [:code ":label-fn"] " & " [:code ":short-label-fn"]  " & " [:code ":background-color"] ". See below."]}
     {:name :model              :required true                          :type "a set of ids | r/atom"                                            :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}
     {:name :placeholder        :required false                         :type "string"                  :validate-fn string?                     :description "Background text shown when there's no selection."}
     {:name :on-change          :required true                          :type "id -> nil"               :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. Called with one argument, the set of selected ids. See " [:code ":model"] "."]}
     {:name :disabled?          :required false :default false          :type "boolean"                                                          :description "if true, no user selection is allowed"}
     {:name :required?          :required false :default false          :type "boolean | r/atom"                                                 :description "when true, at least one item must be selected."}
     {:name :unselect-buttons?  :required false :default false          :type "boolean"                                                          :description "When true, \"X\" buttons will be added to the display of selected tags (on the right), allowing the user to directly unselect them."}
     {:name :show-only-button?  :required false :default false          :type "boolean"                                                          :description "When true, an 'Only' button will be displayed next to each item in the dropdown, allowing the user to select only that item."}
     {:name :show-counter?      :required false :default false          :type "boolean"                                                          :description "When true, a counter showing the number of selected items will be displayed in the anchor to the left of the close button."}
     {:name :label-fn           :required false :default ":label"       :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :description-fn     :required false :default ":description" :type "map -> hiccup"           :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable description. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :abbrev-fn          :required false                         :type "choice -> hiccup"        :validate-fn ifn?                        :description [:span "A function which can turn a choice into an abbreviated label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
     {:name :abbrev-threshold   :required false                         :type "number"                  :validate-fn number?                     :description [:span "The text displayed for selected choices is obtained via either " [:code ":label-fn"] " or " [:code "abbrev-fn"] ". When the total number of characters displayed is less than this argument then " [:code ":label-fn"] " will be used, otherwise " [:code "abbrev-fn"] ". You should set this value taking into account the width of this component. If not set, only " [:code ":label-fn"] " is used."]}
     {:name :min-width          :required false                         :type "string"                  :validate-fn string?                     :description [:span "the CSS min-width, like \"100px\" or \"20em\". This is the natural display width of the Component. It prevents the width from becoming smaller than the value specified, yet allows growth horizontally if sufficient choices are selected up to " [:code ":max-width"] " or unbounded growth if " [:code ":max-width"] " is not provided."]}
     {:name :max-width          :required false                         :type "string"                  :validate-fn string?                     :description "the CSS max-width, like \"100px\" or \"20em\". It prevents the width from becoming larger than the value specified. If sufficient choices are selected to go beyond the maximum then some choices will be hidden by overflow."}
     {:name :height             :required false :default "25px"         :type "string"                  :validate-fn string?                     :description "the specific height of the component"}
     {:name :max-height         :required false :default "380px"        :type "string"                  :validate-fn string?                     :description "the maximum height of the dropdown menu"}
     {:name :style              :required false                         :type "map"                     :validate-fn map?                        :description "CSS styles to add or override"}
     {:name :parts              :required false                         :type "map"                     :validate-fn (parts? tag-dropdown-parts) :description "See Parts section below."}
     {:name :src                :required false                         :type "map"                     :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging. Source code coordinates map containing keys" [:code ":file"] "and" [:code ":line"]  ". See 'Debugging'."]}
     {:name :debug-as           :required false                         :type "map"                     :validate-fn map?                        :description [:span "Used in dev builds to assist with debugging, when one component is used implement another component, and we want the implementation component to masquerade as the original component in debug output, such as component stacks. A map optionally containing keys" [:code ":component"] "and" [:code ":args"] "."]}]))

(defn tag-dropdown
  [& {:keys [pre-theme theme] :as args}]
  (or
   (validate-args-macro tag-dropdown-args-desc args)
   (let [showing?    (reagent/atom false)
         theme       (theme/comp pre-theme theme)
         transition! #(case %
                        :open  (reset! showing? true)
                        :close (reset! showing? false))]
     (fn tag-dropdown-render
       [& {:keys [choices model placeholder on-change unselect-buttons? required? show-only-button? show-counter? abbrev-fn abbrev-threshold label-fn
                  description-fn min-width max-width height max-height style disabled? parts src debug-as]
           :or   {label-fn          :label
                  description-fn    :description
                  height            "25px"
                  max-height        "380px"
                  show-only-button? false
                  show-counter?     false}
           :as   args}]
       (or
        (validate-args-macro tag-dropdown-args-desc args)
        (let [part              (partial p/part part-structure (p/descend args ::rc/tag-dropdown))
              choices           (deref-or-value choices)
              model             (deref-or-value model)
              abbrev-threshold  (deref-or-value abbrev-threshold)
              required?         (deref-or-value required?)
              disabled?         (deref-or-value disabled?)
              unselect-buttons? (deref-or-value unselect-buttons?)
              show-only-button? (deref-or-value show-only-button?)
              show-counter?     (deref-or-value show-counter?)
              re-com-ctx        {:transition! transition!
                                 :state       {:interaction
                                               (if disabled? :disabled :enabled)}}
              choices-num-chars (reduce
                                 (fn [n choice]
                                   (if (contains? model (:id choice))
                                     (+ n (count (label-fn choice)))
                                     n))
                                 0
                                 choices)
              abbrev?           (and (>= choices-num-chars abbrev-threshold)
                                     (number? abbrev-threshold)
                                     (fn? abbrev-fn))

              placeholder-tag
              (part ::td/placeholder-tag
                {:theme theme
                 :impl  text-tag
                 :props
                 {:re-com re-com-ctx
                  :tag-data
                  {:id               :$placeholder$
                   :label            ""
                   :background-color "white"
                   :width            (if abbrev? "20px" "40px")}}})
              tag-list-body
              (part ::td/selection-list
                {:theme theme
                 :impl  sl/selection-list
                 :props
                 {:src               (at)
                  :disabled?         disabled?
                  :required?         required?
                  :hide-border?      true
                  :multi-select?     true
                  :show-only-button? show-only-button?
                  :max-height        max-height
                  :choices           choices
                  :model             model
                  :on-change         #(on-change %)
                  :parts             (merge (select-keys parts sl/parts)
                                            {:label
                                             (fn [tag]
                                               (part ::td/text-tag
                                                 {:theme      theme
                                                  :impl       text-tag
                                                  :post-props {:style style}
                                                  :props      {:label-fn       label-fn
                                                               :description-fn description-fn
                                                               :tag-data       tag}}))})}})
              tag
              (fn [{:keys [id label] :as tag}]
                (when (contains? model id)
                  (part ::td/text-tag
                    {:theme      theme
                     :impl       text-tag
                     :post-props {:style style}
                     :props      {:label-fn    (if abbrev? abbrev-fn label-fn)
                                  :tag-data    tag
                                  :tooltip     label
                                  :disabled?   disabled?
                                  :on-click    #(reset! showing? true)      ;; Show dropdown
                                  :on-unselect (when (and unselect-buttons?
                                                          (not (and (= 1 (count model))
                                                                    required?)))
                                                 #(on-change (disj model %)))
                                  :hover-style {:opacity "0.8"}
                                  :parts       parts}})))
              tag-main
              (part ::td/main
                {:theme      theme
                 :impl       h-box
                 :post-props {:src (at)}
                 :props
                 {:re-com    re-com-ctx
                  :min-width min-width
                  :max-width max-width
                  :height    height
                  :children
                  [(part ::td/tags
                     {:theme      theme
                      :impl       h-box
                      :post-props {:src (at)}
                      :props
                      {:children
                       (conj
                        (mapv tag choices)
                        (when-not disabled? placeholder-tag)
                        [gap {:size "20px"}]
                        (when (and (zero? (count model)) placeholder)
                          (part ::td/placeholder-message
                            {:theme      theme
                             :impl       box
                             :post-props {:src (at)}
                             :props      {:child placeholder}})))}})
                   (part ::td/actions
                     {:theme theme
                      :impl  h-box
                      :props {:src   (at)
                              :align :center
                              :children
                              [(part ::td/gap
                                 {:theme theme
                                  :impl  gap
                                  :props {:size "8px"}})
                               (when (and show-counter? (not-empty model))
                                 (part ::td/counter
                                   {:theme theme
                                    :impl  label
                                    :props {:src   (at)
                                            :label (str (count model))}}))
                               (when (and (not-empty model) (not disabled?) (not required?))
                                 (part ::td/close-button
                                   {:theme theme
                                    :impl  close-button
                                    :props {:src      (at)
                                            :parts    {:wrapper {:style {:margin-left "5px"}}}
                                            :on-click #(on-change #{})}}))]}})]}})]
          (part ::td/popover-anchor-wrapper
            {:theme      theme
             :impl       popover-anchor-wrapper
             :post-props (-> {:src      src
                              :debug-as (or debug-as (reflect-current-component))}
                             (debug/instrument args))
             :props
             {:re-com   re-com-ctx
              :showing? showing?
              :anchor   tag-main
              :popover
              (part ::td/popover-content-wrapper
                {:theme theme
                 :impl  popover-content-wrapper
                 :props {:src    (at)
                         :re-com re-com-ctx
                         :body   tag-list-body}})}})))))))

