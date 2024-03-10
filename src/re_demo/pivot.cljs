(ns re-demo.pivot
  (:require [re-com.core   :refer [at h-box v-box box gap line label p p-span hyperlink-href]]
            [re-com.pivot :as pivot]
            [re-demo.utils :refer [panel-title title2 args-table github-hyperlink status-text]]))

(def arg-style {:style {:display     "inline-block"
                        :font-weight "bold"
                        :min-width   "140px"}})

(def header->icon {:spot     "❌"
                   :price    "💰"
                   :foreign  "🌍"
                   :domestic "🏠"
                   :kilo     "𝞙"
                   :ton      "𝞣"
                   :apple    "🍎"
                   :banana   "🍌"
                   :grape    "🍇"
                   :red      "🔴"
                   :white    "⚪"})

(defn cell-fn [{:keys [row-path column-path]}]
  (->> (concat column-path row-path)
       (map #(header->icon % (header->icon (get % :id))))
       (apply str)))

(def fruit {:dimension "fruit"})

(defn p-demo
  []
  [v-box :src (at)
   :size     "auto"
   :gap      "10px"
   :children
   [[panel-title "[pivot/table ... ]"
     "src/re_com/text.cljs"
     "src/re_demo/p.cljs"]
    [h-box :src (at)
     :gap      "100px"
     :children
     [[v-box :src (at)
       :gap      "10px"
       :width    "450px"
       :children
       [[title2 "Notes"]
        [status-text "alpha" {:color "red"}]
        [pivot/grid {:cell    cell-fn
                     :columns [{:id :fruit :hide-cells? true}
                               [{:id :red}
                                {:id :white}]]
                     :rows    [[:price
                                [:foreign
                                 [:kilo
                                  :ton]]
                                [:domestic
                                 [:kilo
                                  :ton]]]]}]]]]]]])

;; core holds a reference to panel, so need one level of indirection to get figwheel updates
(defn panel
  []
  [p-demo])
