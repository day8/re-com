(ns re-com.tag-dropdown
  (:require-macros
    [reagent.ratom   :refer [reaction]]
    [re-com.core     :refer [handler-fn]]
    [re-com.validate :refer [validate-args-macro]])
  (:require
    [goog.string           :as gstring]
    [reagent.core          :as reagent]
    [re-com.util           :refer [deref-or-value]]
    [re-com.validate       :as validate :refer [parts?]]
    [re-com.box            :refer [box h-box gap]]
    [re-com.misc           :refer [checkbox]]
    [re-com.selection-list :refer [selection-list]]
    [re-com.close-button   :refer [close-button]]
    [re-com.popover        :refer [popover-content-wrapper popover-anchor-wrapper]]))

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


;; TODO: [GR] Ripped off from re-com.selection-list
(defn- check-clicked
  [selections item-id ticked? required?]
  (let [num-selected (count selections)
        only-item    (when (= 1 num-selected) (first selections))]
    (if (and required? (= only-item item-id))
      selections  ;; prevent unselect of last item
      (if ticked? (conj selections item-id) (disj selections item-id)))))


;; TODO: [GR] Ripped off from re-com.selection-list (added {:height "22px"} to override height in compact class
(defn- as-checked
  [item id-fn selections on-change disabled? label-fn required? as-exclusions?]
  ;;TODO: Do we really need an anchor now that bootstrap styles not realy being used ?
  (let [item-id (id-fn item)]
    [box
     :class "list-group-item compact"
     :style {:height "25px"}
             ;:background-color "#F3F6F7"}
     :attr {:on-click (handler-fn (when-not disabled?
                                    (on-change (check-clicked selections item-id (not (selections item-id)) required?))))}
     :child [checkbox
             :model (some? (selections item-id))
             :on-change #()                                 ;; handled by enclosing box
             :disabled? disabled?
             :label-style (label-style (selections item-id) as-exclusions?)
             :label (label-fn item)]]))


(defn text-tag
  []
  (let [over? (reagent/atom false)]
    (fn text-tag-render
      [& {:keys [tag-data on-click on-close tooltip label-fn width height hover-style class style attr]
          :or   {label-fn :label}}]
      (let [clickable?   (some? on-click)
            closeable?   (some? on-close)
            placeholder? (= (:id tag-data) :$placeholder$)
            border       (when placeholder? "1px dashed #828282")
            tag-label   (label-fn tag-data)]
        [h-box
         :align :center
         :justify (if placeholder? :end :center)

         ;:width    (if placeholder? (:width tag-data) width)

         :width width
         :min-width (when placeholder? (:width tag-data))

         :height height
         :padding "0px 4px"
         :margin (str "2px " (if placeholder? 0 6) "px 2px 0px")
         :class (str "noselect rc-text-tag " class)
         :style (merge
                  {:color            "white"
                   :background-color (:background-color tag-data)
                   :cursor           (if placeholder? "pointer" "default")
                   :font-size        "12px"
                   ;:font-weight      "bold"
                   :border           border
                   :border-radius    "3px"}
                  (when @over? hover-style)
                  style)
         :attr (merge
                 {:title          tooltip
                  :on-click       (handler-fn (when placeholder? (on-click (:id tag-data))))
                  :on-mouse-enter (handler-fn (reset! over? true))
                  :on-mouse-leave (handler-fn (reset! over? false))}
                 attr)
         :children [(if placeholder?
                      [box
                       :style {:color "hsl(194, 61%, 85%)"}
                       :child  (gstring/unescapeEntities "&#9660;")]
                      [box
                       :style {:cursor (when clickable? "pointer")}
                       :attr  {:on-click (handler-fn
                                           (when clickable?
                                             (on-click (:id tag-data)))
                                           #_(.stopPropagation event))}
                       :child (or tag-label "???")])
                    (when (and closeable? (not placeholder?))
                      [h-box
                       :align    :center
                       :children [[box
                                   :style {:margin-left "4px"
                                           :margin-right "3px"}
                                   :child "|"]
                                  [close-button
                                   :color       "white"
                                   :hover-color "#ccc"
                                   :div-size    13
                                   :font-size   13
                                   :top-offset  1
                                   :on-click    #(when closeable?
                                                   (on-close (:id tag-data)))]]])]]))))

(def tag-dropdown-parts
  #{:popover-anchor-wrapper})

(def tag-dropdown-args-desc
  [{:name :choices            :required true                      :type "vector of maps | atom"    :validate-fn validate/vector-of-maps?   :description [:span "Each map represents a choice. Values corresponding to id, label, short label and tag background color are extracted by the functions " [:code ":id"] ", " [:code ":label-fn"] " & " [:code ":short-label-fn"]  " & " [:code ":background-color"] ". See below."]}
   {:name :model              :required true                      :type "a set of ids | atom"                                              :description [:span "a set of the ids for currently selected choices. If nil, see " [:code ":placeholder"] "."]}
   {:name :placeholder        :required false                     :type "string"                   :validate-fn string?                    :description "background text when no selection"}
   {:name :on-change          :required true                      :type "id -> nil"                :validate-fn fn?                        :description [:span "a function that will be called when the selection changes. Passed the set of selected ids. See " [:code ":model"] "."]}
   {:name :on-tag-click       :required false                     :type "id -> nil"                :validate-fn fn?                        :description ""}
   {:name :close-buttons?     :required false :default false      :type "boolean"                                                          :description ""}
   {:name :short-label-fn     :required false :default ":short-label" :type "map -> hiccup"        :validate-fn ifn?                       :description ""}
   {:name :short-label-count  :required false :default 0          :type "integer"                  :validate-fn number?                    :description ""}
   {:name :width              :required false                     :type "string"                   :validate-fn string?                    :description ""}
   {:name :height             :required false :default "25px"     :type "string"                   :validate-fn string?                    :description ""}
   {:name :tag-width          :required false                     :type "string"                   :validate-fn string?                    :description ""}
   {:name :tag-height         :required false                     :type "string"                   :validate-fn string?                    :description ""}
   {:name :style              :required false                     :type "map"                      :validate-fn map?                       :description ""}
   {:name :disabled?          :required false :default false      :type "boolean"                                                          :description ""}
   {:name :tag-comp           :required false :default "text-tag" :type "function"                 :validate-fn ifn?                       :description ""}
   {:name :parts              :required false                     :type "map"                      :validate-fn (parts? tag-dropdown-parts) :description "See Parts section below."}])

(defn tag-dropdown
  [& {:keys [model short-label-count] :as args}]
  {:pre [(validate-args-macro tag-dropdown-args-desc args "tag-dropdown")]}
  (let [showing?      (reagent/atom false)
        short-labels? (reaction (>= (count @model) short-label-count))]
    (fn tag-dropdown-render
      [& {:keys [choices model placeholder on-change on-tag-click close-buttons? short-label-fn short-label-count width height tag-width tag-height style disabled? tag-comp parts]
          :or   {short-label-fn :short-label
                 height         "25px"
                 tag-comp       text-tag}
          :as   args}]
      {:pre [(validate-args-macro tag-dropdown-args-desc args "tag-dropdown")]}
      (let [close-buttons?  (deref-or-value close-buttons?)
            placeholder-tag [tag-comp
                             :tag-data    {:id               :$placeholder$
                                           :label            ""
                                           :background-color "white"
                                           :width            "40px"} ;; change this and you need to adjust :position-offset below
                             :on-click    #(reset! showing? true)
                             :tooltip     "Click to select tags"
                             :hover-style {:background-color "#eee"}]
            tag-list-body   [selection-list
                             :parts         {:list-group-item {:style {:background-color "#F3F6F7"}}}
                             :choices       choices
                             :hide-border?  true
                             :label-fn      (fn [tag]
                                              [tag-comp
                                               :tag-data tag
                                               :width    tag-width
                                               :height   tag-height
                                               :style    style])
                             :item-renderer as-checked
                             :model         model
                             :on-change     #(on-change %)
                             :multi-select? true]
            tag-main        [h-box
                             :width    width
                             :height   height
                             :align    :center
                             :padding  "0px 6px"
                             :class    (str "rc-tag-dropdown " (get-in parts [:main :class]))
                             :style    (merge {:background-color "white"
                                               :border           "1px solid lightgrey"
                                               :border-radius    "2px"
                                               :overflow          "hidden"
                                               :cursor            "pointer"}
                                              (get-in parts [:main :style]))
                             :attr     (merge {:on-click (handler-fn (reset! showing? true))}
                                              (get-in parts [:main :attr]))
                             :children [(if (zero? (count @model)) placeholder "")
                                        [h-box
                                         :class    (str "rc-tag-dropdown-tags " (get-in parts [:tags :class]))
                                         :size     "1" ;; This line will align the tag placeholder to the right
                                         :style    {:overflow "hidden"}
                                         :children (conj
                                                     (mapv (fn [tag]
                                                             (when (contains? @model (:id tag))
                                                               [tag-comp
                                                                :tag-data    tag
                                                                :label-fn    (if @short-labels? short-label-fn :label)
                                                                :tooltip     (:label tag)
                                                                :on-click    (if on-tag-click
                                                                               #(on-tag-click (:id tag))
                                                                               ;#(on-change (disj @model %)) ;; Delete this tag
                                                                               #(reset! showing? true))      ;; Show dropdown

                                                                :on-close    (when (and close-buttons? (not @short-labels?)) #(on-change (disj @model %)))
                                                                :width       tag-width
                                                                :height      tag-height
                                                                :hover-style {:opacity "0.8"}
                                                                :style       style]))
                                                           choices)
                                                     placeholder-tag)]
                                        [gap :size "6px"]
                                        (when-not (empty? @model)
                                          [close-button :on-click #(on-change #{})])]]]
        [popover-anchor-wrapper
         :class    (str "rc-tag-dropdown-popover-anchor-wrapper " (get-in parts [:popover-anchor-wrapper :class]))
         :showing? showing?
         :position :below-left
         :anchor   tag-main
         :popover  [popover-content-wrapper
                    ;:popover-color   "#F3F6F7"
                    :position-offset -20
                    :arrow-length    0
                    :arrow-width     0
                    :arrow-gap       1
                    :no-clip?        true
                    :on-cancel       #(reset! showing? false)
                    ;:tooltip-style?  true
                    :padding         "19px 19px"
                    :body            tag-list-body]]))))