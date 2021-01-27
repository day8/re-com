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
    [re-com.box            :refer [box h-box v-box gap]]
    [re-com.checkbox       :refer [checkbox]]
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
     :class "list-group-item "
     :style {:padding "4px 8px 4px 8px"}
     :attr  {:on-click (handler-fn (when-not disabled?
                                     (on-change (check-clicked selections item-id (not (selections item-id)) required?))))}
     :child [checkbox
             :model       (some? (selections item-id))
             :on-change   #()                                 ;; handled by enclosing box
             :disabled?   disabled?
             :label-style (label-style (selections item-id) as-exclusions?)
             :label       (label-fn item)]]))


(defn text-tag
  []
  (let [over? (reagent/atom false)]
    (fn text-tag-render
      [& {:keys [tag-data on-click on-unselect tooltip label-fn description-fn width height hover-style disabled? class style attr]
          :or   {label-fn       :label}}]
      (let [clickable?    (and (some? on-click) (not disabled?))
            unselectable? (and (some? on-unselect) (not disabled?))
            placeholder?  (= (:id tag-data) :$placeholder$)
            border        (when placeholder? "1px dashed #828282")
            tag-label     (label-fn tag-data)
            tag-description (when description-fn (description-fn tag-data))]
        [v-box
         :align :start
         :children [[h-box
                     :align-self :start
                     :justify    (if placeholder? :end :center)

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
                                   :style {:color "hsl(194, 61%, 85%)"}
                                   :child  (gstring/unescapeEntities "&#9660;")]
                                  [box
                                   :style {:cursor (when clickable? "pointer")}
                                   :attr  {:on-click (handler-fn
                                                       (when clickable?
                                                         (on-click (:id tag-data)))
                                                       #_(.stopPropagation event))}
                                   :child (or tag-label "???")])
                                (when (and unselectable? (not placeholder?))
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
                                               :on-click    #(when unselectable?
                                                               (on-unselect (:id tag-data)))]]])]]
                    (when tag-description
                      [:span
                       {:style {:color "#586069"}}
                       tag-description])]]))))


(def tag-dropdown-parts
  #{:popover-anchor-wrapper})

(def tag-dropdown-args-desc
  [{:name :choices            :required true                      :type "vector of maps | atom"    :validate-fn validate/vector-of-maps?    :description [:span "Each map represents a choice. Values corresponding to id, label, short label and tag background color are extracted by the functions " [:code ":id"] ", " [:code ":label-fn"] " & " [:code ":short-label-fn"]  " & " [:code ":background-color"] ". See below."]}
   {:name :model              :required true                      :type "a set of ids | atom"                                               :description [:span "The set of the ids for currently selected choices. If nil or empty, see " [:code ":placeholder"] "."]}
   {:name :placeholder        :required false                     :type "string"                   :validate-fn string?                     :description "Background text when no selection"}
   {:name :on-change          :required true                      :type "id -> nil"                :validate-fn fn?                         :description [:span "This function is called whenever the selection changes. Called with one argument, the set of selected ids. See " [:code ":model"] "."]}
   {:name :on-tag-click       :required false                     :type "id -> nil"                :validate-fn fn?                         :description "This function is called when the user clicks a tag. Called with one argument, the tag id."}
   {:name :disabled?          :required false :default false      :type "boolean"                                                           :description "if true, no user selection is allowed"}
   {:name :unselect-buttons?  :required false :default false      :type "boolean"                                                           :description "When true, \"X\" buttons will be displayed on tags, allowing the user to unselect the tag."}
   {:name :label-fn           :required false :default ":label"   :type "map -> hiccup"            :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
   {:name :description-fn     :required false :default ":description" :type "map -> hiccup"        :validate-fn ifn?                        :description [:span "A function which can turn a choice into a displayable description. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
   {:name :abbrev-fn          :required false                     :type "choice -> hiccup"         :validate-fn ifn?                        :description [:span "A function which can turn a choice into an abbreviated label. Will be called for each element in " [:code ":choices"] ". Given one argument, a choice map, it returns a string or hiccup."]}
   {:name :abbrev-threshold   :required false                     :type "number"                   :validate-fn number?                     :description [:span "The text displayed for selected choices is obtained via either " [:code ":label-fn"] " or " [:code "abbrev-fn"] ". When the total number of characters displayed is less than this argument then " [:code ":label-fn"] " will be used, otherwise " [:code "abbrev-fn"] ". You should set this value taking into account the width of this component. If not set, only " [:code ":label-fn"] " is used."]}
   {:name :min-width          :required false                     :type "string"                   :validate-fn string?                     :description [:span "the CSS min-width, like \"100px\" or \"20em\". This is the natural display width of the Component. If sufficient choices are selected then a Component can grow horizontally to " [:code ":max-width"] "."]}
   {:name :max-width          :required false                     :type "string"                   :validate-fn string?                     :description "the CSS max-width, like \"500px\" or \"20em\". XXX should this default to `:min-width`? XXX What if both are omited. XXX Should `:min-width` be called `:width`?"}
   {:name :height             :required false :default "25px"     :type "string"                   :validate-fn string?                     :description "the specific height of the component"}
   {:name :tag-width          :required false                     :type "string"                   :validate-fn string?                     :description "the width of each individual tag"}
   {:name :tag-height         :required false                     :type "string"                   :validate-fn string?                     :description "the height of each individual tag"}
   {:name :style              :required false                     :type "map"                      :validate-fn map?                        :description "CSS styles to add or override"}
   {:name :tag-comp           :required false :default "text-tag" :type "function"                 :validate-fn ifn?                        :description "This function returns the hiccup to render a tag."}
   {:name :parts              :required false                     :type "map"                      :validate-fn (parts? tag-dropdown-parts) :description "See Parts section below."}])

(defn tag-dropdown
  [& {:keys [] :as args}]
  {:pre [(validate-args-macro tag-dropdown-args-desc args "tag-dropdown")]}
  (let [showing?      (reagent/atom false)]
    (fn tag-dropdown-render
      [& {:keys [choices model placeholder on-change on-tag-click unselect-buttons? abbrev-fn abbrev-threshold label-fn
                 description-fn min-width max-width height tag-width tag-height style disabled? tag-comp parts]
          :or   {label-fn          :label
                 description-fn    :description
                 height            "25px"
                 tag-comp          text-tag}
          :as   args}]
      {:pre [(validate-args-macro tag-dropdown-args-desc args "tag-dropdown")]}
      (let [choices            (deref-or-value choices)
            model              (deref-or-value model)
            abbrev-threshold   (deref-or-value abbrev-threshold)
            disabled?          (deref-or-value disabled?)
            unselect-buttons?  (deref-or-value unselect-buttons?)

            choices-num-chars  (reduce
                                 (fn [n choice]
                                   (if (contains? model (:id choice))
                                     (+ n (count (label-fn choice)))
                                     n))
                                 0
                                 choices)
            abbrev?            (and (>= choices-num-chars abbrev-threshold)
                                    (number? abbrev-threshold)
                                    (fn? abbrev-fn))

            placeholder-tag [tag-comp
                             :tag-data    {:id               :$placeholder$
                                           :label            ""
                                           :background-color "white"
                                           :width            (if abbrev? "20px" "40px")} ;; change this and you need to adjust :position-offset below
                             :on-click    #(reset! showing? true)
                             :tooltip     "Click to select tags"
                             :hover-style {:background-color "#eee"}]
            tag-list-body   [selection-list
                             :disabled?     disabled?
                             :parts         {:list-group-item {:style {:background-color "#F3F6F7"}}}
                             :choices       choices
                             :hide-border?  true
                             :label-fn      (fn [tag]
                                              [tag-comp
                                               :label-fn       label-fn
                                               :description-fn description-fn
                                               :tag-data       tag
                                               :width          tag-width
                                               :height         tag-height
                                               :style          style])
                             :item-renderer as-checked
                             :model         model
                             :on-change     #(on-change %)
                             :multi-select? true]
            tag-main        [h-box
                             :min-width min-width
                             :max-width max-width
                             :height    height
                             :align     :center
                             :padding   "0px 6px"
                             :class     (str "rc-tag-dropdown " (get-in parts [:main :class]))
                             :style     (merge {:background-color "white"
                                                :border           "1px solid lightgrey"
                                                :border-radius    "2px"
                                                :overflow         "hidden"
                                                :cursor           (if disabled? "default" "pointer")}
                                               (get-in parts [:main :style]))
                             :attr      (merge {}
                                               (when (not disabled?) {:on-click (handler-fn (reset! showing? true))})
                                               (get-in parts [:main :attr]))
                             :children  [(if (zero? (count model)) placeholder "")
                                         [h-box
                                          :class    (str "rc-tag-dropdown-tags " (get-in parts [:tags :class]))
                                          :size     "1" ;; This line will align the tag placeholder to the right
                                          :style    {:overflow "hidden"}
                                          :children (conj
                                                      (mapv (fn [tag]
                                                              (when (contains? model (:id tag))
                                                                [tag-comp
                                                                 :label-fn    (if abbrev? abbrev-fn label-fn)
                                                                 :tag-data    tag
                                                                 :tooltip     (:label tag)
                                                                 :disabled?   disabled?
                                                                 :on-click    (if on-tag-click
                                                                                #(on-tag-click (:id tag))
                                                                                #(reset! showing? true))      ;; Show dropdown

                                                                 :on-unselect (when unselect-buttons? #(on-change (disj model %)))
                                                                 :width       tag-width
                                                                 :height      tag-height
                                                                 :hover-style {:opacity "0.8"}
                                                                 :style       style]))
                                                            choices)
                                                      (when (not disabled?)
                                                        placeholder-tag)
                                                      [gap :size "20px"])]
                                         (when (and (not-empty model) (not disabled?))
                                           [close-button
                                            :on-click  #(on-change #{})])]]]
        [popover-anchor-wrapper
         :class    (str "rc-tag-dropdown-popover-anchor-wrapper " (get-in parts [:popover-anchor-wrapper :class]))
         :showing? showing?
         :position :below-left
         :anchor   tag-main
         :popover  [popover-content-wrapper
                    :position-offset (if abbrev? -10 -20)
                    :arrow-length    0
                    :arrow-width     0
                    :arrow-gap       1
                    :no-clip?        true
                    :on-cancel       #(reset! showing? false)
                    :padding         "19px 19px"
                    :body            tag-list-body]]))))