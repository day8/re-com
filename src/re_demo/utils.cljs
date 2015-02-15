(ns re-demo.utils
  (:require [re-com.core :refer [title label]]
            [re-com.box  :refer [h-box v-box box gap line]]))


(defn re-com-title
  []
  [title
   :label    "Re-com"
   :style    {:font-size "36px"
              :font-family "'Roboto Condensed', sans-serif"
              :font-weight 300
              }])

(def panel-title-style {
    :font-family "'Roboto Condensed', sans-serif;"
    :font-size   "24px"
    :font-weight 300
   })

(defn panel-title
  "Title shown at the top of each Tab Panel"
  [text style]
  [title
   :label    text
   :style    (merge panel-title-style style)])


(defn component-title
  "A title for a component like [something ... ]"
  [component-name style]
  [title
   :style {
      :font-family "'Roboto Condensed', sans-serif;"
      :font-size   "24px"
      :font-weight 300}
   :label      component-name
   :underline? false
   ])



(defn arg-row
  "I show one argument in an args table."
  [name-width arg odd-row?]
  (let [required   (:required arg)
        default    (:default arg)
        arg-type   (:type arg)
        needed-vec (if (not required)
                     (if (nil? default)
                       [[label :label "optional" :class "small-caps"]]
                       [[label :label "default:" :class "small-caps"] [label :label (str default)]])
                     [[label :label "required" :class "small-caps"]])]
    [h-box
     :style    { :background (if odd-row?  "#F4F4F4" "#FCFCFC" )}
     :children [[:span {:style {:width name-width
                                :font-weight 700;
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
                             :children (concat [[label :label arg-type]
                                                [gap :size "10px"]]
                                               needed-vec)]
                            [:span  (:description arg)]
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
