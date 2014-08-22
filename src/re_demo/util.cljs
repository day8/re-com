 (ns re-demo.util
  (:require [reagent.core :as reagent]
            [re-com.core  :refer [gap]]
            [re-com.box       :refer [h-box v-box box line]]))


(defn title
  [text]
  "An underlined, left justified, H3 Title"
  [box
   :size "auto"
   :child  [v-box
            :children [[h-box
                        :children [[:h3 text]]]
                       [line :size "1px"]]]])
