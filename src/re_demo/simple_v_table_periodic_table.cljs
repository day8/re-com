(ns re-demo.simple-v-table-periodic-table
  (:require
    [reagent.core          :as reagent]
    [re-com.text           :refer [p]]
    [re-com.box            :refer [v-box]]
    [re-com.popover        :refer [popover-anchor-wrapper popover-content-wrapper]]
    [re-com.simple-v-table :refer [simple-v-table]]
    [re-demo.utils         :refer [title2]]))

(def elements
  (map-indexed
    #(assoc %2 :atomic-number (inc %1))
    [{:symbol "H"  :element "Hydrogen"      :group 1  :period 1 :atomic-weight 1 :block :s}
     {:symbol "He" :element "Helium"        :group 18 :period 1 :atomic-weight 4.0026 :block :s}

     {:symbol "Li" :element "Lithium"       :group 1  :period 2 :atomic-weight 6.94 :block :s}
     {:symbol "Be" :element "Berylium"      :group 2  :period 2 :atomic-weight 9.0122 :block :s}
     {:symbol "B"  :element "Boron"         :group 13 :period 2 :atomic-weight 10.81 :block :p}
     {:symbol "C"  :element "Carbon"        :group 14 :period 2 :atomic-weight 12.011 :block :p}
     {:symbol "N"  :element "Nitrogen"      :group 15 :period 2 :atomic-weight 14.007 :block :p}
     {:symbol "O"  :element "Oxygen"        :group 16 :period 2 :atomic-weight 15.999 :block :p}
     {:symbol "F"  :element "Fluorine"      :group 17 :period 2 :atomic-weight 18.9984 :block :p}
     {:symbol "Ne" :element "Neon"          :group 18 :period 2 :atomic-weight 20.1797 :block :p}

     {:symbol "Na" :element "Sodium"        :group 1  :period 3 :atomic-weight 22.9897 :block :s}
     {:symbol "Mg" :element "Magnesium"     :group 2  :period 3 :atomic-weight 24.305 :block :s}
     {:symbol "Al" :element "Aluminium"     :group 13 :period 3 :atomic-weight 26.9815 :block :p}
     {:symbol "Si" :element "Silicon"       :group 14 :period 3 :atomic-weight 28.085 :block :p}
     {:symbol "P"  :element "Phosphorus"    :group 15 :period 3 :atomic-weight 30.9738 :block :p}
     {:symbol "S"  :element "Sulfur"        :group 16 :period 3 :atomic-weight 32.06 :block :p}
     {:symbol "Cl" :element "Chlorine"      :group 17 :period 3 :atomic-weight 35.45 :block :p}
     {:symbol "Ar" :element "Argon"         :group 18 :period 3 :atomic-weight 39.95 :block :p}

     {:symbol "K"  :element "Potassium"     :group 1  :period 4 :atomic-weight 39.0983 :block :s}
     {:symbol "Ca" :element "Calcium"       :group 2  :period 4 :atomic-weight 40.078 :block :s}
     {:symbol "Sc" :element "Scandium"      :group 3  :period 4 :atomic-weight 44.9559 :block :d}
     {:symbol "Ti" :element "Titanium"      :group 4  :period 4 :atomic-weight 47.867 :block :d}
     {:symbol "V"  :element "Vanadium"      :group 5  :period 4 :atomic-weight 50.9415 :block :d}
     {:symbol "Cr" :element "Chromium"      :group 6  :period 4 :atomic-weight 51.9961 :block :d}
     {:symbol "Mn" :element "Manganese"     :group 7  :period 4 :atomic-weight 54.9380 :block :d}
     {:symbol "Fe" :element "Iron"          :group 8  :period 4 :atomic-weight 55.845 :block :d}
     {:symbol "Co" :element "Cobalt"        :group 9  :period 4 :atomic-weight 58.9331 :block :d}
     {:symbol "Ni" :element "Nickel"        :group 10 :period 4 :atomic-weight 58.6934 :block :d}
     {:symbol "Cu" :element "Copper"        :group 11 :period 4 :atomic-weight 63.546 :block :d}
     {:symbol "Zn" :element "Zinc"          :group 12 :period 4 :atomic-weight 65.38 :block :d}
     {:symbol "Ga" :element "Gallium"       :group 13 :period 4 :atomic-weight 69.723 :block :p}
     {:symbol "Ge" :element "Germanium"     :group 14 :period 4 :atomic-weight 72.630 :block :p}
     {:symbol "As" :element "Arsenic"       :group 15 :period 4 :atomic-weight 74.9215 :block :p}
     {:symbol "Se" :element "Selenium"      :group 16 :period 4 :atomic-weight 78.971 :block :p}
     {:symbol "Br" :element "Bromine"       :group 17 :period 4 :atomic-weight 79.904 :block :p}
     {:symbol "Kr" :element "Krypton"       :group 18 :period 4 :atomic-weight 83.798 :block :p}

     {:symbol "Rb" :element "Rubidium"      :group 1  :period 5 :atomic-weight 85.4678 :block :s}
     {:symbol "Sr" :element "Strontium"     :group 2  :period 5 :atomic-weight 87.62 :block :s}
     {:symbol "Y"  :element "Yttrium"       :group 3  :period 5 :atomic-weight 88.9058 :block :d}
     {:symbol "Zr" :element "Zirconium"     :group 4  :period 5 :atomic-weight 91.224 :block :d}
     {:symbol "Nb" :element "Niobium"       :group 5  :period 5 :atomic-weight 92.9063 :block :d}
     {:symbol "Mo" :element "Molybdenum"    :group 6  :period 5 :atomic-weight 95.95 :block :d}
     {:symbol "Tc" :element "Technetium"    :group 7  :period 5 :atomic-weight "[98]" :block :d}
     {:symbol "Ru" :element "Ruthenium"     :group 8  :period 5 :atomic-weight 101.07 :block :d}
     {:symbol "Rh" :element "Rhodium"       :group 9  :period 5 :atomic-weight 102.9055 :block :d}
     {:symbol "Pd" :element "Palladium"     :group 10 :period 5 :atomic-weight 106.42 :block :d}
     {:symbol "Ag" :element "Silver"        :group 11 :period 5 :atomic-weight 107.8682 :block :d}
     {:symbol "Cd" :element "Cadmium"       :group 12 :period 5 :atomic-weight 112.414 :block :d}
     {:symbol "In" :element "Indium"        :group 13 :period 5 :atomic-weight 114.818 :block :p}
     {:symbol "Sn" :element "Tin"           :group 14 :period 5 :atomic-weight 118.710 :block :p}
     {:symbol "Sb" :element "Antimony"      :group 15 :period 5 :atomic-weight 121.760 :block :p}
     {:symbol "Te" :element "Tellurium"     :group 16 :period 5 :atomic-weight 127.60 :block :p}
     {:symbol "I"  :element "Iodine"        :group 17 :period 5 :atomic-weight 126.9044 :block :p}
     {:symbol "Xe" :element "Xenon"         :group 18 :period 5 :atomic-weight 131.293 :block :p}

     {:symbol "Cs" :element "Caesium"       :group 1  :period 6 :atomic-weight 132.9054 :block :s}
     {:symbol "Ba" :element "Barium"        :group 2  :period 6 :atomic-weight 137.327 :block :s}

     {:symbol "La" :element "Lanthanum"     :group 3  :period 8 :atomic-weight 138.9055 :block :f}
     {:symbol "Ce" :element "Cerium"        :group 4  :period 8 :atomic-weight 140.116 :block :f}
     {:symbol "Pr" :element "Praseodymium"  :group 5  :period 8 :atomic-weight 140.9076 :block :f}
     {:symbol "Nd" :element "Neodymium"     :group 6  :period 8 :atomic-weight 144.242 :block :f}
     {:symbol "Pm" :element "Promethium"    :group 7  :period 8 :atomic-weight "[145]" :block :f}
     {:symbol "Sm" :element "Samarium"      :group 8  :period 8 :atomic-weight 150.36 :block :f}
     {:symbol "Eu" :element "Europium"      :group 9  :period 8 :atomic-weight 151.964 :block :f}
     {:symbol "Gd" :element "Gadolinium"    :group 10 :period 8 :atomic-weight 157.25 :block :f}
     {:symbol "Tb" :element "Terbium"       :group 11 :period 8 :atomic-weight 158.9253 :block :f}
     {:symbol "Dy" :element "Dysprosium"    :group 12 :period 8 :atomic-weight 162.500 :block :f}
     {:symbol "Ho" :element "Holmium"       :group 13 :period 8 :atomic-weight 164.9303 :block :f}
     {:symbol "Er" :element "Erbium"        :group 14 :period 8 :atomic-weight 167.259 :block :f}
     {:symbol "Tm" :element "Thulium"       :group 15 :period 8 :atomic-weight 168.934 :block :f}
     {:symbol "Yb" :element "Ytterbium"     :group 16 :period 8 :atomic-weight 173.045 :block :f}
     {:symbol "Lu" :element "Lutetium"      :group 17 :period 8 :atomic-weight 174.9668 :block :d}

     {:symbol "Hf" :element "Hafnium"       :group 4  :period 6 :atomic-weight 178.49 :block :d}
     {:symbol "Ta" :element "Tantalum"      :group 5  :period 6 :atomic-weight 180.94788 :block :d}
     {:symbol "W"  :element "Tungsten"      :group 6  :period 6 :atomic-weight 183.84 :block :d}
     {:symbol "Re" :element "Rhenium"       :group 7  :period 6 :atomic-weight 186.207 :block :d}
     {:symbol "Os" :element "Osmium"        :group 8  :period 6 :atomic-weight 190.23 :block :d}
     {:symbol "Ir" :element "Iridium"       :group 9  :period 6 :atomic-weight 192.217 :block :d}
     {:symbol "Pt" :element "Platinum"      :group 10 :period 6 :atomic-weight 195.084 :block :d}
     {:symbol "Au" :element "Gold"          :group 11 :period 6 :atomic-weight 196.9666 :block :d}
     {:symbol "Hg" :element "Mercury"       :group 12 :period 6 :atomic-weight 200.592 :block :d}
     {:symbol "Tl" :element "Thallium"      :group 13 :period 6 :atomic-weight 204.38 :block :p}
     {:symbol "Pb" :element "Lead"          :group 14 :period 6 :atomic-weight 207.2 :block :p}
     {:symbol "Bi" :element "Bismuth"       :group 15 :period 6 :atomic-weight 208.9804 :block :p}
     {:symbol "Po" :element "Polonium"      :group 16 :period 6 :atomic-weight "[209]" :block :p}
     {:symbol "At" :element "Astatine"      :group 17 :period 6 :atomic-weight "[210]" :block :p}
     {:symbol "Rn" :element "Radon"         :group 18 :period 6 :atomic-weight "[222]" :block :p}
     {:symbol "Fr" :element "Francium"      :group 1  :period 7 :atomic-weight "[223]" :block :s}
     {:symbol "Ra" :element "Radium"        :group 2  :period 7 :atomic-weight "[226]" :block :s}

     {:symbol "Ac" :element "Actinium"      :group 3  :period 9 :atomic-weight "[227]" :block :f}
     {:symbol "Th" :element "Thorium"       :group 4  :period 9 :atomic-weight 232.0377 :block :f}
     {:symbol "Pa" :element "Protactinium"  :group 5  :period 9 :atomic-weight 231.03588 :block :f}
     {:symbol "U"  :element "Uranium"       :group 6  :period 9 :atomic-weight 238.0289 :block :f}
     {:symbol "Np" :element "Neptunium"     :group 7  :period 9 :atomic-weight "[237]" :block :f}
     {:symbol "Pu" :element "Plutonium"     :group 8  :period 9 :atomic-weight "[244]" :block :f}
     {:symbol "Am" :element "Americium"     :group 9  :period 9 :atomic-weight "[243]" :block :f}
     {:symbol "Cm" :element "Curium"        :group 10 :period 9 :atomic-weight "[247]" :block :f}
     {:symbol "Bk" :element "Berkelium"     :group 11 :period 9 :atomic-weight "[251]" :block :f}
     {:symbol "Cf" :element "Californium"   :group 12 :period 9 :atomic-weight "[251]" :block :f}
     {:symbol "Es" :element "Einsteinium"   :group 13 :period 9 :atomic-weight "[252]" :block :f}
     {:symbol "Fm" :element "Fermium"       :group 14 :period 9 :atomic-weight "[257]" :block :f}
     {:symbol "Md" :element "Mendelevium"   :group 15 :period 9 :atomic-weight "[258]" :block :f}
     {:symbol "No" :element "Nobelium"      :group 16 :period 9 :atomic-weight "[259]" :block :f}
     {:symbol "Lr" :element "Lawrencium"    :group 17 :period 9 :atomic-weight "[226]" :block :d}

     {:symbol "Rf" :element "Rutherfordium" :group 4  :period 7 :atomic-weight "[267]" :block :d}
     {:symbol "Db" :element "Dubnium"       :group 5  :period 7 :atomic-weight "[268]" :block :d}
     {:symbol "Sg" :element "Seaborgium"    :group 6  :period 7 :atomic-weight "[269]" :block :d}
     {:symbol "Bh" :element "Bohrium"       :group 7  :period 7 :atomic-weight "[270]" :block :d}
     {:symbol "Hs" :element "Hassium"       :group 8  :period 7 :atomic-weight "[270]" :block :d}
     {:symbol "Mt" :element "Meitnerium"    :group 9  :period 7 :atomic-weight "[278]" :block :d}
     {:symbol "Ds" :element "Darmstadtium"  :group 10 :period 7 :atomic-weight "[281]" :block :d}
     {:symbol "Rg" :element "Roentgenium"   :group 11 :period 7 :atomic-weight "[282]" :block :d}
     {:symbol "Cn" :element "Copernicium"   :group 12 :period 7 :atomic-weight "[285]" :block :d}
     {:symbol "Nh" :element "Nihonium"      :group 13 :period 7 :atomic-weight "[286]" :block :p}
     {:symbol "Fl" :element "Flerovium"     :group 14 :period 7 :atomic-weight "[289]" :block :p}
     {:symbol "Mc" :element "Moscovium"     :group 15 :period 7 :atomic-weight "[290]" :block :p}
     {:symbol "Lv" :element "Livermorium"   :group 16 :period 7 :atomic-weight "[293]" :block :p}
     {:symbol "Ts" :element "Tennessine"    :group 17 :period 7 :atomic-weight "[294]" :block :p}
     {:symbol "Og" :element "Oganesson"     :group 18 :period 7 :atomic-weight "[294]" :block :p}]))

(defn down-arrow
  []
  [:svg {:height "24" :viewBox "0 0 24 24" :width "24"}
   [:path {:d "M20 12l-1.41-1.41L13 16.17V4h-2v12.17l-5.58-5.59L4 12l8 8 8-8z"}]])

(defn demo
  []
  (let [;; rows are generated from the raw data of elements. Each row represents a period (1-7, plus two broken out rows
        ;; for 6 and 7).
        model            (mapv
                           (fn [elements-in-period]
                             (reduce
                               (fn [row {:keys [period group] :as element}]
                                 (assoc row
                                   :0                    period
                                   :id                   (keyword (str period))
                                   (keyword (str group)) element))
                               {}
                               elements-in-period))
                           (partition-by :period (sort-by :period elements)))

        ;; column specifications are generated from a sequence of 0 to 18 (inclusive) representing the row header (0)
        ;; and all the groups (1-18).
        columns          (mapv (fn [group]
                                 {:id           group
                                  :header-label (if (= group 0) "" (str group))
                                  :row-label-fn (if (zero? group)
                                                  (fn [{:keys [id] :as row}]
                                                    ;; Hide period labels for 8 and 9 (as these are extensions of periods 6 and 7)
                                                    (if (or (= id :8) (= id :9))
                                                      ""
                                                      (:0 row)))
                                                  (fn [{:keys [id] :as row}]
                                                    (let [element (get row (keyword (str group)))
                                                          {:keys [atomic-number symbol element atomic-weight]} element]
                                                      (if (and (or (= id :6) (= id :7))
                                                               (= group 3))
                                                        [v-box
                                                         :align    :center
                                                         :justify  :center
                                                         :height   "72px"
                                                         :style    {:background-color "#2ECC40"}
                                                         :children [[down-arrow]]]
                                                        [v-box
                                                         :children [[:span atomic-number]
                                                                    [:span
                                                                     {:style {:font-weight "bold"}}
                                                                     symbol]
                                                                    [:span element]
                                                                    [:span atomic-weight]]]))))
                                  :width        80
                                  :align        "middle"})
                               (range 19))

        ;; cell-style is used to change the colour of the cell background according to the block of the element:
        cell-style-fn    (fn [row {:keys [id] :as column}]
                           (when (not (zero? id))
                             (let [k     (keyword (str id))
                                   block (get-in row [k :block])]
                               {:background-color
                                (case block
                                  :s "#FF4136"
                                  :f "#2ECC40"
                                  :d "#7FDBFF"
                                  :p "#FFDC00"
                                  "white")})))

        ;; on-enter-row and on-leave-row events are used to display the period description popover:
        current-period   (reagent/atom 1)
        showing-popover? (reagent/atom false)
        on-enter-row     (fn [index]
                           (let [period (inc index)]
                             (reset! current-period period)
                             (when (< period 8)
                               (reset! showing-popover? true))))
        on-leave-row      #(reset! showing-popover? false)]
    (fn []
      [v-box
       :gap      "10px"
       :children [[title2 "Demo - Periodic Table"]
                  [popover-anchor-wrapper
                   :showing? showing-popover?
                   :position :left-center
                   :popover  [popover-content-wrapper
                              :width "450px"
                              :body [p (case @current-period
                                         1 "The first period contains fewer elements than any other, with only two, hydrogen and helium."
                                         2 "Period 2 elements involve the 2s and 2p orbitals. They include the biologically most essential elements besides hydrogen: carbon, nitrogen, and oxygen."
                                         3 "All period three elements occur in nature and have at least one stable isotope. All but the noble gas argon are essential to basic geology and biology."
                                         4 "Period 4 includes the biologically essential elements potassium and calcium, and is the first period in the d-block with the lighter transition metals. These include iron, the heaviest element forged in main-sequence stars and a principal component of the Earth, as well as other important metals such as cobalt, nickel, and copper. Almost all have biological roles."
                                         5 "Period 5 has the same number of elements as period 4 and follows the same general structure but with one more post transition metal and one fewer nonmetal. Of the three heaviest elements with biological roles, two (molybdenum and iodine) are in this period; tungsten, in period 6, is heavier, along with several of the early lanthanides. Period 5 also includes technetium, the lightest exclusively radioactive element."
                                         6 "Period 6 is the first period to include the f-block, with the lanthanides (also known as the rare earth elements), and includes the heaviest stable elements. Many of these heavy metals are toxic and some are radioactive, but platinum and gold are largely inert."
                                         7 "All elements of period 7 are radioactive. This period contains the heaviest element which occurs naturally on Earth, plutonium. All of the subsequent elements in the period have been synthesized artificially. Whilst five of these (from americium to einsteinium) are now available in macroscopic quantities, most are extremely rare, having only been prepared in microgram amounts or less. Some of the later elements have only ever been identified in laboratories in quantities of a few atoms at a time."
                                         "")]]
                   :anchor   [simple-v-table
                              :fixed-column-count        1
                              :fixed-column-border-color "#333"
                              :row-height                80
                              :on-enter-row              on-enter-row
                              :on-leave-row              on-leave-row
                              :cell-style                cell-style-fn
                              :columns                   columns
                              :model                     model]]]])))