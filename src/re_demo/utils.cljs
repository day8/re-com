(ns re-demo.utils
  (:require [re-com.core :refer [h-box v-box box gap line title label hyperlink-href]]))


(defn panel-title
  "Title shown at the top of each Tab Panel"
  [panel-name style]
  [title
   :label      panel-name
   :level      :level1
   :underline? true
   :style      (merge {:margin-top "1px"
                       :margin-bottom "0px"}
                      style)])

(defn component-title
  "A title for a component like [something ... ]"
  [component-name style]
  [title
   :label component-name
   :level :level2
   :style style])

(defn status-text
  "given some status text, return a component that displays that status"
  [status]
  [:span
   [:span {:style {:font-weight "bold"}} "Status: "]
   status])

(defn material-design-hyperlink
  [text]
  [hyperlink-href
   :label  text
   :href   "http://zavoloklom.github.io/material-design-iconic-font/icons.html"
   :target "_blank"])

(defn github-hyperlink
  "given a label and a relative path, return a component which links to that fully qualified GitHub URL in a new tab"
  [label src-path]
  (let [base-url (str "https://github.com/Day8/re-com/tree/" (if ^boolean js/goog.DEBUG "develop" "master") "/")]
    [hyperlink-href
     :label  label
     :style  {:font-size    "13px"
              :margin       "0px 0px 0px 16px"}
     :href   (str base-url src-path)
     :target "_blank"]))

(defn arg-row
  "I show one argument in an args table."
  [name-width arg odd-row?]
  (let [required   (:required arg)
        default    (:default arg)
        arg-type   (:type arg)
        needed-vec (if (not required)
                     (if (nil? default)
                       [[:span.semibold.all-small-caps "optional"]]
                       [[:span.semibold.all-small-caps "default:"] [:span.semibold (str default)]])
                     [[:span.semibold.all-small-caps "required"]])]
    [h-box
     :style    { :background (if odd-row?  "#F4F4F4" "#FCFCFC" )}
     :children [[:span {:class  "semibold"
                        :style {:width name-width
                                :padding-left "15px"
                                :align-self :center
                               }}
                 (str (:name arg))]
                [line :size "1px" :color "white"]
                [v-box
                 :style {:padding "7px 15px 2px 15px"}
                 :gap  "4px"
                 :width "310px"
                 :children [[h-box
                             :gap   "4px"
                             :children (concat [[:span.semibold  arg-type]
                                                [gap :size "10px"]]
                                               needed-vec)]
                            [:p
                              {:font-size "smaller" :color "red"}
                              (:description arg)]
                            ]]]]))


(defn args-table
  "I display a component arguements in an easy to read format"
  [args]
  (let [name-width  "130px"]
    (fn
      []
      [v-box
       :children (concat
                   [[component-title "Named Parameters"]
                    [gap :size "10px"]]
                   (map (partial arg-row name-width)  args (cycle [true false])))])))


(defn paragraphs
  [& children]
  [v-box
   :width    "380px"
   :children (into [] children)])
