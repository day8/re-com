 (ns re-demo.util
  (:require [reagent.core :as reagent]
            [re-com.box   :refer [gap h-box v-box box line]]))


(defn title
  [text]
  "An underlined, left justified, H3 Title"
  [box
   :child  [v-box
            :children [[h-box
                        :children [[:h3 text]]]
                       [line :size "1px"]]]])
