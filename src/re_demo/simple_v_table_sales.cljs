(ns re-demo.simple-v-table-sales
  (:require
    [clojure.string        :as string]
    [goog.string           :as gstring]
    [goog.string.format]
    [reagent.core          :as reagent]
    [re-com.text           :refer [label]]
    [re-com.slider         :refer [slider]]
    [re-com.checkbox       :refer [checkbox]]
    [re-com.buttons        :refer [hyperlink-href]]
    [re-com.box            :refer [h-box v-box gap]]
    [re-com.simple-v-table :refer [simple-v-table]]
    [re-demo.utils         :refer [title2]]))

;; 50 randomly sampled names from most popular baby names in 2019. More efficient to just statically define some than
;; store a probability matrix for a Markov chain.
(def names
  ["Harris" "Jake" "Reece" "Aston" "Barry" "Oran" "Ritchie" "Crawford" "Raphael" "Clayton" "Johan" "Rhylen" "Caelin"
   "Calen" "Cassius" "Dakota" "Fabien" "Fraser-Lee" "Jonathin" "Khabat" "Lyotard" "Manpreet" "Mousa" "Rajvir" "Shadan"
   "Zygmunt" "Kayla" "Cassie" "Mabel" "Roslin" "Abiha" "Emma-Leigh" "Jacqueline" "Leena" "Lilianna" "Olina" "Orianna"
   "Sylvia" "Abbigale" "Allissa" "Alona" "Asira" "Aurora-Leigh" "Brea" "Chiarra" "Elouisa" "Emme-Leigh" "Frea" "Geena"
   "Hanifa"])

;; Generates 2000 random 'sales' rows.
(def sales
  (mapv
    (fn [n]
      (let [person (rand-nth names)]
        {:id     n
         :region (rand-nth ["North" "East" "South" "West"])
         :person person
         :method (rand-nth [:online :in-store])
         :email  (str (string/lower-case person) "@" (rand-nth ["widget.org" "example.com" "deck.com" "conversation.com" "response.com"]))
         :sales  (gstring/format "%.2f" (+ (* (rand) (- 10000 1000)) 1000))
         :units  (+ (* (rand-int 10) (- 1000 100)) 100)}))
    (range 2000)))

(defn store-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M20 4H4v2h16V4zm1 10v-2l-1-5H4l-1 5v2h1v6h10v-6h4v6h2v-6h1zm-9 4H6v-4h6v4z"}]])

(defn devices-icon
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M4 6h18V4H2v13H0v3h14v-3H4V6zm20 2h-8v12h8V8zm-2 9h-4v-7h4v7z"}]])

(defn demo
  []
  (let [max-rows?          (reagent/atom true)
        max-rows           (reagent/atom 8)
        max-width?         (reagent/atom false)
        max-width          (reagent/atom 630)
        fixed-column-count (reagent/atom 1)]
    (fn []
      [v-box
       :gap      "10px"
       :children [[title2 "Demo - Sales Table"]
                  [h-box
                   :children [[label :label [:code ":max-rows"]]
                              [gap :size "11px"]
                              [checkbox
                               :label     ""
                               :model     max-rows?
                               :on-change #(reset! max-rows? %)]
                              (when @max-rows?
                                [:<>
                                 [slider
                                  :model     max-rows
                                  :on-change #(reset! max-rows %)
                                  :min       8
                                  :max       100
                                  :step      1
                                  :width     "200px"]
                                 [gap :size "11px"]
                                 [label :label @max-rows]])]]
                  [h-box
                   :children [[label :label [:code ":max-width"]]
                              [gap :size "11px"]
                              [checkbox
                               :label     ""
                               :model     max-width?
                               :on-change #(reset! max-width? %)]
                              (when @max-width?
                                [:<>
                                 [slider
                                  :model     max-width
                                  :on-change #(reset! max-width %)
                                  :min       200
                                  :max       820
                                  :step      1
                                  :width     "200px"]
                                 [gap :size "11px"]
                                 [label :label @max-width]])]]
                  [h-box
                   :children [[label :label [:code ":fixed-column-count"]]
                              [gap :size "11px"]
                              [slider
                               :model     fixed-column-count
                               :on-change #(reset! fixed-column-count %)
                               :min       1
                               :max       3
                               :step      1
                               :width     "200px"]
                              [gap :size "11px"]
                              [label :label @fixed-column-count]]]
                  [simple-v-table
                   :fixed-column-count        @fixed-column-count
                   :fixed-column-border-color "#333"
                   :row-height                35
                   :max-rows                  (when @max-rows? @max-rows)
                   :max-width                 (when @max-width? (str @max-width "px"))
                   :cell-style                (fn [{:keys [sales] :as row} {:keys [id] :as column}]
                                                (when (= :sales id)
                                                  {:background-color (cond
                                                                       (> 2000 sales)      "#FF4136"
                                                                       (> 5000 sales 2000) "#FFDC00"
                                                                       (> 7500 sales 5000) "#01FF70"
                                                                       (> sales 7500)      "#2ECC40")}))
                   :columns                   [{:id :id     :header-label ""       :row-label-fn :id :width 60 :align "left"}
                                               {:id :region :header-label "Region" :row-label-fn :region :width 100 :align "left"}
                                               {:id :name   :header-label "Name"   :row-label-fn :person :width 100 :align "left"}
                                               {:id :email  :header-label "Email"  :row-label-fn (fn [row] [hyperlink-href :label (:email row) :href (str "mailto:" (:email row))]) :width 200 :align "left"}
                                               {:id :method :header-label "Method" :row-label-fn (fn [row] (case (:method row) :online [devices-icon] [store-icon])) :width 100 :align "right"}
                                               {:id :sales  :header-label "Sales"  :row-label-fn :sales :width 100 :align "right"}
                                               {:id :units  :header-label "Units"  :row-label-fn :units :width 100 :align "right"}]
                   :model                     sales]]])))