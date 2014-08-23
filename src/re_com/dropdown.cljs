(ns re-com.dropdown
  (:require
    [reagent.core      :as     reagent]))


;;  http://silviomoreto.github.io/bootstrap-select/
;;  http://alxlit.name/bootstrap-chosen/

;; Will need a multi dropdown and a single dropdown

;; allow clear button on right
;; disabled ???
;; style
;; options is   {::id id  :label "DDDD"  :group  "XXXX"  }
(defn single-drop-down
  [& {:keys [options model hint-text]}]
  "Render a bootstrap styled choosen"
  (let [id        (gensym "select_")
        has-focus (reagent/atom false)]
     [:select.selectpicker
      {:id id
       :on-change #(println %)}
      (for [o options]
         [:option (:label o)])
       ]
    ))


#_[single-drop-down
  :options  ["Brown Bear" "Polar Bear" "Giant Panda" "Sloth Bear" "Drop Bear"]   ;; a vector or map containing a vector
  :model  selected-index
  ]
