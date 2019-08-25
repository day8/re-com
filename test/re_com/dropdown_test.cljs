(ns re-com.dropdown-test
  (:require [cljs.test       :refer-macros [is are deftest]]
            [reagent.core    :as reagent]
            [re-com.dropdown :as dropdown]))

(deftest auto-complete
  (are [arg-map expected]
    (let [{:keys [text sel ins]} arg-map]
      (= (dropdown/auto-complete :choices   ["bar" "baz baz" "FOoo" "FOoooO"]
                                 :text      text
                                 :sel-start (first sel)
                                 :sel-end   (second sel)
                                 :ins       ins)
         (some-> expected
                 (dissoc :sel)
                 (assoc :sel-start (first (:sel expected)))
                 (assoc :sel-end (second (:sel expected))))))
    {:text ""     :sel [0 0] :ins "a"}  nil
    {:text ""     :sel [0 0] :ins "b"}  {:text "bar"     :sel [1 3]}
    {:text "bar"  :sel [1 3] :ins "a"}  {:text "bar"     :sel [2 3]}
    {:text "bar"  :sel [2 3] :ins "r"}  nil
    {:text "bar"  :sel [3 3] :ins "r"}  nil
    {:text "bar"  :sel [2 3] :ins "z"}  {:text "baz baz" :sel [3 7]}
    {:text "bar"  :sel [2 3] :ins "x"}  nil
    {:text ""     :sel [0 0] :ins "f"}  {:text "fOoo"    :sel [1 4]}
    {:text "fOoo" :sel [1 4] :ins "o"}  {:text "fooo"    :sel [2 4]}
    {:text "fooo" :sel [2 4] :ins "O"}  {:text "foOo"    :sel [3 4]}
    {:text "foOo" :sel [3 4] :ins "O"}  nil
    {:text "foOO" :sel [4 4] :ins "O"}  {:text "foOOOO"  :sel [5 6]}
    {:text "foo"  :sel [0 3] :ins "F"}  {:text "FOoo"    :sel [1 4]}
    {:text "bar"  :sel [1 2] :ins "a"}  {:text "bar"     :sel [2 3]}
    {:text ""     :sel [0 0] :ins "Fo"} {:text "Fooo"    :sel [2 4]}
    {:text nil    :sel [0 0] :ins "b"}  {:text "bar"     :sel [1 3]}
    {:text ""     :sel [0 0] :ins nil}  nil
    {:text ""     :sel [0 0] :ins ""}   nil))


(deftest capitalize-first-letter
  (are [argument expected] (= (dropdown/capitalize-first-letter argument)
                              expected)
        nil      nil
        ""       ""
        "foOo"   "FoOo"))
