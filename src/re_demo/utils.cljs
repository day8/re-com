(ns re-demo.utils
  (:require
    [re-com.core           :refer [title line label hyperlink-href align-style]]
    [re-com.box            :refer [box gap h-box v-box]]
    [re-com.text           :refer [p]]
    [re-com.util           :refer [px]]))

(defn github-hyperlink
  "given a label and a relative path, return a component which hyperlinks to the GitHub URL in a new tab"
  [label src-path]
  (let [base-url (str "https://github.com/day8/re-com/blob/master/")]
    [hyperlink-href
     :label  label
     ;:style  {:font-size    "13px"}
     :href   (str base-url src-path)
     :target "_blank"]))

(defn source-reference
  [description src]
  [h-box
   :class "all-small-caps"
   :gap    "7px"
   :align  :center
   :children [[label :label "source:"]
              [github-hyperlink description src]]])


(defn panel-title
  "Shown across the top of each page"
  [panel-name src1 src2]
  [v-box
   :children [[h-box
               :margin "0px 0px 9px 0px"
               :height "54px"
               :align :end
               :children [[title
                           :label         panel-name
                           :level         :level1
                           :margin-bottom "0px"
                           :margin-top    "2px"]
                          [gap :size "25px"]
                          (when src1 [h-box
                                      :class "all-small-caps"
                                      :gap    "7px"
                                      :align  :center
                                      :children [[label :label "source:"]
                                                 [github-hyperlink "component" src1]
                                                 [label :label "|"  :style {:font-size "12px"}]
                                                 [github-hyperlink "page" src2]]])]]
              [line]]])

(defn title2
  "2nd level title"
  [text style]
  [title
   :label text
   :level :level2
   :style style])

(defn title3
  "3rd level title"
  [text style]
  [title
   :label text
   :level :level3
   :style style])

(defn status-text
  "given some status text, return a component that displays that status"
  [status style]
  [:span
   [:span.bold "Status: "]
   [:span {:style style} status]])

(defn new-in-version
  "given some version text, return a component that displays 'new in version...'"
  [version style]
  [:span
   [:span.bold "New in "]
   [:span {:style style} version]])

(defn material-design-hyperlink
  [text]
  [hyperlink-href
   :label  text
   :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
   :target "_blank"])



(defn arg-row
  "I show one argument in an args table (which is itself a horizontal list of arguments)."
  [name-column-width arg odd-row?]
  (let [required   (:required arg)
        default    (:default arg)
        arg-type   (:type arg)
        needed-vec (if (not required)
                     (if (nil? default)
                       [[:span.semibold.all-small-caps "optional"]]
                       [[:span.semibold.all-small-caps "default:"] 
                        [:code {:style {:margin-right 0}} (str default)]])
                     [[:span.semibold.all-small-caps "required"]])]
    [h-box
     :style    {:background (if odd-row? "#F4F4F4" "#FCFCFC") 
                :border-left (when (not odd-row?) "1px solid #f4f4f4")
                :border-right (when (not odd-row?) "1px solid #f4f4f4")}
     :children [[:span {:class "semibold"
                        :style (merge (align-style :align-self :center)
                                      {:width        name-column-width
                                       :padding-left "15px"})}
                 (str (:name arg))]
                 [line :size "1px" :color (if odd-row? "white" "#f4f4f4")]
                 [v-box
                  :style {:padding "7px 15px 2px 15px"}
                  :gap  "4px"
                  :size  "1 1 0px"    ;; grow horizontally to fill the space
                  :children [[h-box
                              :gap   "4px"
                              :children (concat [[:span.semibold  arg-type]
                                                 [gap :size "1"]]
                                                 needed-vec)]
                             [line]
                             [:p
                              ; {:font-size "smaller" :color "red"}
                              (:description arg)]]]]]))

(defn args-table
  "I render component arguments in an easy to read format"
  [args  {:keys [total-width name-column-width title]
          :or   {name-column-width "130px" }}]
  (fn
    []
    ;(println total-width)
    [v-box
     :width    total-width
     :children (concat
                [[title2 (if title title "Parameters")]
                 [gap :size "10px"]]
                (map (partial arg-row name-column-width)  args (cycle [true false])))]))

(defn parts-header
  []
  [h-box
   :style {:background "#e8e8e8"}
   :children [[box
               :width "343px"
               :style {:padding "5px 12px"
                       :font-weight "bold"}
               :child [:span "Part"]]
              [line]
              [box
               :width "343px"
               :style {:padding "5px 12px"
                       :font-weight "bold"}
               :child [:span "CSS Class"]]
              [line]
              [box
               :width "212px"
               :style {:padding "5px 12px"
                       :font-weight "bold"}
               :child [:span "Implementation"]]
              [box
               :style {:padding "5px 12px"
                       :font-weight "bold"}
               :child [:span "Notes"]]]])

(defn parts-row
  [{:keys [type name-label name level class impl notes]} odd-row?]
  [h-box
   :align    :start
   :style    {:background (if odd-row? "#F4F4F4" "#FCFCFC")
              :border-left (when (not odd-row?) "1px solid #f4f4f4")
              :border-right (when (not odd-row?) "1px solid #f4f4f4")}
   :children [[h-box
               :width    "343px"
               :style {:padding "5px 12px"}
               :children [[gap :size (px (* level 19))]
                          (if (= :legacy type)
                            (if name-label
                              name-label
                              [:span "Use " [:code ":class"], [:code ":style"] " or " [:code ":attr"] " arguments instead."])
                            [:code (str name)])]]
              [line :color (if odd-row? "white" "#f4f4f4")]
              [box
               :width "343px"
               :style {:padding "5px 12px"}
               :child (if class [:code class] "")]
              [line :color (if odd-row? "white" "#f4f4f4")]
              [box
               :width "212px"
               :style {:padding "5px 12px"}
               :child [:code impl]]
              [line :color (if odd-row? "white" "#f4f4f4")]
              [box
               :style {:padding "5px 12px"}
               :child [:span notes]]]])

(defn parts-table
  [component-name parts]
  (let [name-of-first-part  (str (first (remove nil? (map :name parts))))
        code-example-spaces (reduce #(str % " ") "" (range (+ (count name-of-first-part) 13)))]
    [v-box
     :margin   "0px 20px 20px 0px"
     :children (concat
                 [[title2 "Parts"]
                  [p "This component is constructed from a hierarchy of HTML elements which we refer to as \"parts\"."]
                  [p "re-com gives each of these parts a unique CSS class, so that you can individually target them.
                        Also, each part is identified by a keyword for use in " [:code ":parts"] " like this:" [:br]]
                  [:pre "[" component-name "\n"
                   "   ...\n"
                   "   :parts {" name-of-first-part " {:class \"blah\"\n"
                   code-example-spaces ":style { ... }\n"
                   code-example-spaces ":attr  { ... }}}]"]
                  [title3 "Part Hierarchy"]
                  [gap :size "10px"]
                  [parts-header]]
                 (map parts-row parts (cycle [true false])))]))

(defn scroll-to-top
  [element]
  (set! (.-scrollTop element) 0))
