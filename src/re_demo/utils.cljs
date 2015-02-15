(ns re-demo.utils
  (:require [re-com.core :refer [title label]]
            [re-com.box  :refer [h-box v-box box gap line]]))


(def ubuntu-font {              ;; TOD: move this into class
  :font-family "Ubuntu"
  :font-weight "300" })


(def panel-title-style {        ;; TOD: move this into class
    :font-size   "24px"
    :color       "#FFF"
    :background-color  "#888"
    :height      "50px"
   })

(defn panel-title
  "Title shown at the top of each Tab Panel"
  [text style]
  [title
   :label    text
   :style    (merge ubuntu-font  style)])


(defn component-title
  "A title for a component like [something ... ]"
  [component-name style]
  [title
   :h          :h4   ;; [:h4 .. ]
   :label      component-name
   :style      (merge {:color "#555"}  style)
   :underline? false
   ])



;; -- Args Table --------------------------------------------------------------

#_(defn arg-row
  "I show one argument in an args table."
  [name-ems arg]                       ;; TODO: make name-ems overriddable
  (let [required   (:required arg)
        default    (:default arg)
        arg-type   (:type arg)
        needed-vec (if (not required)
                     (if (nil? default)
                       [[label :label "optional" :class "small-caps"]]
                       [[label :label "default:" :class "small-caps"] [label :label (str default)]])
                     [[label :label "required" :class "small-caps"]])]
    [h-box
     :style   {:font-size "small"}
     :gap      "20px"
     :children [[:div {:style {:width name-ems}} [:div {:class "re-code"} (str (:name arg))]]
                [v-box
                 :width "300px"
                 :children [[h-box
                             :gap   "4px"
                             :children (concat [[label :label arg-type]
                                                [gap :size "10px"]]
                                               needed-vec)]
                            [:p  (:description arg)]
                            (when-not (:last-arg? arg) [line])]]]]))


#_(defn- add-flag-to-last
  "I add a :last? key to the last item in vector (of maps) 'v'"
  [v]
  (assoc-in v [(dec (count v)) :last-arg?]  true))


#_(defn args-table
  "I display a component arguements in an easy to read format"
  [args]
  (let [;; max-chars  (->> args
        ;;                 (map (comp count str :name))
        ;;                 (apply max))
        ;; max-ems    (str (* max-chars 0.55) "em")    ;; about how many ems will we need to show the longest name.  Very approximately.
        max-ems    "130px"
        args       (add-flag-to-last args)]
    (fn
      []
      [v-box
       :gap       "3px"
       :children (concat
                   [[label :class "small-caps" :label "named parameters:"]
                   #_[label
                     :label "Named Parameters:"]
                    [gap :size "10px"]]
                   (map (partial arg-row max-ems)  args))])))


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
     :style    { :background (if odd-row?  "#F8F8F8" "#FCFCFC" )}
     :children [[:span {:style {:width name-width
                               :font-size "15px"
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
                   [[label
                     :class "small-caps"
                     :label "named parameters:"]
                    [gap :size "10px"]]
                   (map (partial arg-row name-width)  args (cycle [true false])))])))
