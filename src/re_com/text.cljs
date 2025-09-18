 (ns re-com.text
   (:require-macros
    [re-com.core     :refer [handler-fn at reflect-current-component]]
    [re-com.validate :refer [validate-args-macro]])
   (:require
    re-com.label.theme
    re-com.title.theme
    [re-com.label :as-alias l]
    [re-com.title :as-alias title]
    [re-com.args     :as args]
    [re-com.config   :refer [include-args-desc?]]
    [re-com.debug    :as debug]
    [re-com.box      :refer [v-box box line flex-child-style]]
    [re-com.theme    :as    theme]
    [re-com.part     :as    part]
    [re-com.util     :as    u :refer [deep-merge]]
    [re-com.validate :refer [title-levels-list title-level-type? css-style? html-attr? parts? string-or-hiccup? css-class?]]))

;; ------------------------------------------------------------------------------------
;;  Component: label
;; ------------------------------------------------------------------------------------

(def label-part-structure
  [::wrapper {:impl 're-com.box/box}
   [::label-wrapper {:tag :span}
    [::label {:top-level-arg? true}]]])

(def label-parts-desc
  (when include-args-desc?
    (part/describe label-part-structure)))

(def label-parts
  (when include-args-desc?
    (-> (map :name label-parts-desc) set)))

(def label-args-desc
  (when include-args-desc?
    (into
     [{:name :on-click :required false :type "-> nil"        :validate-fn fn?     :description "a function which takes no params and returns nothing. Called when the label is clicked"}
      {:name :width    :required false :type "string"        :validate-fn string? :description "a CSS width"}
      args/class
      args/style
      args/attr
      (args/parts label-parts)
      args/src
      args/debug-as]
     (concat theme/args-desc
             (part/describe-args label-part-structure)))))

(defn label
  "Returns markup for a basic label"
  [& {:keys [pre-theme theme debug-as]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [on-click width]
            :as   args}]
      (or
       (validate-args-macro label-args-desc args)
       (let [part   (partial part/part label-part-structure args)
             re-com {:state {:width    width
                             :on-click on-click}}]
         (part ::l/wrapper
           {:impl       box
            :theme      theme
            :post-props (-> (when width {:width width})
                            (assoc :debug-as (or debug-as (reflect-current-component)))
                            (debug/instrument args))
            :props      {:re-com re-com
                         :src    (at)
                         :child  (part ::l/label-wrapper
                                   {:theme      theme
                                    :post-props (-> args
                                                    (select-keys [:class :style :attr])
                                                    (cond-> on-click (assoc :on-click (handler-fn (on-click)))))
                                    :props      {:re-com   re-com
                                                 :src      (at)
                                                 :tag      :span
                                                 :children [(part ::l/label
                                                              {:src   (at)
                                                               :theme theme})]}})}}))))))

;; ------------------------------------------------------------------------------------
;;  Component: title
;; ------------------------------------------------------------------------------------

(def title-part-structure
  [::title/wrapper {:impl 're-com.box/v-box}
   [::title/label-wrapper {:tag :span}
    [::title/label {:top-level-arg? true :impl "empty"}]]
   [::title/underline {:impl 're-com.box/line}]])

(def title-parts-desc
  (when include-args-desc?
    (part/describe title-part-structure)))

(def title-parts
  (when include-args-desc?
    (-> (map :name title-parts-desc) set)))

(def title-args-desc
  (when include-args-desc?
    (into
     [{:name :level         :required false                   :type "keyword"         :validate-fn title-level-type?    :description [:span "one of " title-levels-list ". If not provided then style the title using " [:code ":class"] " or " [:code ":style"]]}
      {:name :underline?    :required false  :default false   :type "boolean"                                           :description "if true, the title is underlined"}
      {:name :margin-top    :required false  :default "0.6em" :type "string"          :validate-fn string?              :description "CSS size for space above the title"}
      {:name :margin-bottom :required false  :default "0.3em" :type "string"          :validate-fn string?              :description "CSS size for space below the title"}
      args/class
      args/style
      args/attr
      (args/parts title-parts)
      args/src
      args/debug-as]
     (concat theme/args-desc
             (part/describe-args title-part-structure)))))

(defn title
  "A title with four preset levels"
  [& {:keys [pre-theme theme debug-as]}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [level underline? margin-top margin-bottom]
            :or   {margin-top "0.6em" margin-bottom "0.3em"}
            :as   args}]
      (or
       (validate-args-macro title-args-desc args)
       (let [part   (partial part/part title-part-structure args)
             re-com {:state {:level         level
                             :underline?    underline?
                             :margin-top    margin-top
                             :margin-bottom margin-bottom}}]
         (part ::title/wrapper
           {:impl       v-box
            :theme      theme
            :post-props (-> {:debug-as (or debug-as (reflect-current-component))}
                            (debug/instrument args))
            :props      {:re-com   re-com
                         :src      (at)
                         :children [(part ::title/label-wrapper
                                      {:theme      theme
                                       :post-props (-> (select-keys args [:class :style :attr]))
                                       :props      {:re-com   re-com
                                                    :src      (at)
                                                    :tag      :span
                                                    :children [(part ::title/label
                                                                 {:theme theme
                                                                  :props {:re-com re-com}
                                                                  :src   (at)})]}}),
                                    (when underline?
                                      (part ::title/underline
                                        {:impl  line
                                         :theme theme
                                         :props {:re-com re-com
                                                 :src    (at)
                                                 :size   "1px"}}))]}}))))))

;; ------------------------------------------------------------------------------------
;;  Component: p
;; ------------------------------------------------------------------------------------

(def standard-impl-keys [:attr :children :part :re-com :state :theme])

(defn p
  "acts like [:p ] but uses a [:span] in place of the [:p] and adds bottom margin of 0.7ems which
  produces the same visual result.

  Creates a paragraph of body text, expected to have a font-size of 14px or 15px,
  which should have limited width.

  Why limited text width?  See http://baymard.com/blog/line-length-readability

  The actual font-size is inherited.

  At 14px, 450px will yield between 69 and 73 chars.
  At 15px, 450px will yield about 66 to 70 chars.
  So we're at the upper end of the preferred 50 to 75 char range.

  If the first child is a map, it is interpreted as a map of styles / attributes.

  The map can include a :children key. If you pass a sequence of hiccups for :children,
  its items will appear before the rest of the arguments. For instance:

  [rc/p {:children [\"Hello\"]} \" World\"]

  This will show a \"Hello World\" paragraph on the page.

  This component uses [:span] because React has become more unforgiving about nesting [:div]s under [:p]s and dumps
  a big red warning message in DevTools.

  By adding, for example, a [hyperlink] component within your `[:p]` (which contains a [:div]), you can get this warning message"
  [& args]
  (let [arg1                     (first args)    ;; it might be a map of attributes, including styles
        [m hiccup-children]      (if (map? arg1)
                                   [arg1 (rest args)]
                                   [{}   args])
        {:keys [children] :as m} (deep-merge {:style {:flex          "none"
                                                      :width         "450px"
                                                      :min-width     "450px"
                                                      :margin-bottom "0.7em"}}
                                             m)]
    [:span.rc-p
     (apply dissoc m standard-impl-keys)
     (into [:span] (concat children hiccup-children))]))

;; Alias for backwards compatibility; p and p-span used to be different implementations.
(def p-span p)
