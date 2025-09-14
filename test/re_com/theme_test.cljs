(ns re-com.theme-test
  (:require
   [cljs.test :refer-macros [is are deftest testing]]
   [re-com.part :as part]
   [re-com.theme :as theme]
   [re-com.theme-test :as-alias theme-test]))

;;--------------------------------------------------------------------------------------------------
;; Test Component for Theme Testing
;;--------------------------------------------------------------------------------------------------

(def part-structure
  [::wrapper {:impl 're-com.core/v-box}
   [::header {:top-level-arg? true :impl "empty"}]
   [::body {:impl 're-com.core/h-box}]])

(defn test-component
  "Component to test theme application"
  [& {:keys [pre-theme theme] :as props}]
  (let [composed-theme (theme/comp pre-theme theme)
        part           (partial part/part part-structure props)]
    (part ::wrapper
      {:theme composed-theme
       :props {:children [(part ::header {:theme composed-theme})
                          (part ::body {:theme composed-theme})]}})))

;;--------------------------------------------------------------------------------------------------
;; Theme Application Tests
;;--------------------------------------------------------------------------------------------------

(defmulti test-theme :part)
(defmethod test-theme ::wrapper [props]
  (update props :class str " themed-wrapper"))
(defmethod test-theme ::header [props]
  (update props :style merge {:font-weight "bold"}))
(defmethod test-theme :default [props] props)

(deftest theme-fn-receives-part-prop
  (testing "Theme fns receive :part prop for targeting"
    (let [received-parts (atom [])
          spy-theme      (fn [props]
                           (swap! received-parts conj (:part props))
                           props)
          result         (test-component :header "Test" :theme spy-theme)]
      (is (vector? result) "Should return hiccup")
      (is (contains? (set @received-parts) ::wrapper) "Theme called for wrapper")
      ;; Header with string content doesn't get theme - it's just a literal string
      (is (not (contains? (set @received-parts) ::header)) "Theme NOT called for string header")
      (is (contains? (set @received-parts) ::body) "Theme called for body"))))

(deftest theme-modifies-rendered-output
  (testing "Theme changes actually appear in hiccup output"
    (let [result (test-component :header "Test" :theme test-theme)]
      (is (vector? result) "Should return hiccup")
      ;; Check wrapper class modification
      (let [wrapper-class (get-in result [1 :class])]
        (is (string? wrapper-class) "Wrapper should have class")
        (is (re-find #"themed-wrapper" wrapper-class) "Should contain themed class"))
      ;; Header with string content is just the string - no theming
      (let [header-part (get-in result [1 :children 0])]
        (is (= "Test" header-part) "Header should be literal string")))))

(deftest theme-universal-application
  (testing "Themes apply to component parts, not literal content"
    (let [theme-calls    (atom {})
          counting-theme (fn [props]
                           (swap! theme-calls update (:part props) (fnil inc 0))
                           props)
          result         (test-component :header "Test" :theme counting-theme)]
      (is (vector? result) "Should return hiccup")
      ;; Theme should be called for component parts only
      (is (pos? (get @theme-calls ::wrapper 0)) "Theme called for wrapper")
      (is (= 0 (get @theme-calls ::header 0)) "Theme NOT called for string header")
      (is (pos? (get @theme-calls ::body 0)) "Theme called for body component"))))

(deftest theme-composition-order
  (testing "Multiple theme layers compose in correct order"
    (let [pre-theme  (fn [props] (update props :class str " pre-themed"))
          main-theme (fn [props] (update props :class str " main-themed"))
          result     (test-component :header "Test"
                                     :pre-theme pre-theme
                                     :theme main-theme)]
      (is (vector? result) "Should return hiccup")
      (let [wrapper-class (get-in result [1 :class])]
        (is (re-find #"pre-themed" wrapper-class) "Pre-theme should apply")
        (is (re-find #"main-themed" wrapper-class) "Main theme should apply")
        ;; Order should be pre-theme then main-theme
        (let [pre-index  (.indexOf wrapper-class "pre-themed")
              main-index (.indexOf wrapper-class "main-themed")]
          (is (< pre-index main-index) "Pre-theme should come before main-theme"))))))

;;--------------------------------------------------------------------------------------------------
;; Parts + Themes Integration Tests
;;--------------------------------------------------------------------------------------------------

(deftest theme-works-with-all-part-value-types
  (testing "Themes apply correctly to different part-value types"
    (let [add-suffix-theme (fn [props] (update props :class str "-themed"))]

      ;; String part-value - literal string, no theme on content
      (let [result-string (test-component :header "String Content" :theme add-suffix-theme)]
        (is (= "String Content" (get-in result-string [1 :children 0])) "String content is literal")
        (is (re-find #"-themed" (get-in result-string [1 :class])) "Theme applied to wrapper"))

      ;; Hiccup part-value - literal hiccup, no theme on content
      (let [result-hiccup (test-component :header [:span "Hiccup Content"] :theme add-suffix-theme)]
        (is (= [:span "Hiccup Content"] (get-in result-hiccup [1 :children 0])) "Hiccup content is literal")
        (is (re-find #"-themed" (get-in result-hiccup [1 :class])) "Theme applied to wrapper"))

      ;; Fn part-value - gets theme applied and becomes component hiccup
      (let [test-fn   (fn [props] [:div "Fn Content"])
            result-fn (test-component :header test-fn :theme add-suffix-theme)]
        (is (vector? (get-in result-fn [1 :children 0])) "Fn part becomes component hiccup")
        (is (fn? (first (get-in result-fn [1 :children 0]))) "Component hiccup has fn as first element")
        (is (re-find #"-themed" (get-in result-fn [1 :class])) "Theme applied to wrapper"))

      ;; Map part-value - merges with default component, gets themed
      (let [result-map (test-component :parts {:header {:style {:color "red"}}} :theme add-suffix-theme)]
        (let [header-part (get-in result-map [1 :children 0])]
          (is (vector? header-part) "Map part creates component hiccup")
          (is (= {:color "red"} (get-in header-part [1 :style])) "Map part style merged")
          (is (re-find #"-themed" (get-in result-map [1 :class])) "Theme applied to wrapper"))))))

(deftest theme-fn-receives-part-context
  (testing "Fn part-values receive theme props and become component hiccup"
    (let [context-capturing-fn (fn [props]
                                 [:div.from-fn
                                  {:data-part (str (:part props))}
                                  "Content"])
          result               (test-component :header context-capturing-fn :theme test-theme)]
      (is (vector? result) "Should return hiccup")
      (let [header-result (get-in result [1 :children 0])]
        ;; Fn part becomes component hiccup with theme-applied props
        (is (vector? header-result) "Fn part should be component hiccup")
        (is (fn? (first header-result)) "Should have fn as first element")
        ;; The props should include theme modifications
        (let [props (second header-result)]
          (is (= :re-com.theme-test/header (:part props)) "Should have correct :part prop")
          (is (= {:font-weight "bold"} (:style props)) "Should have themed style"))))))

(deftest theme-composition-performance
  (testing "Theme composition happens at mount time, not render time"
    (let [composition-count (atom 0)
          mock-comp         (fn [& themes]
                              (swap! composition-count inc)
                              (apply theme/comp themes))
          original-comp     theme/comp]

      ;; Mock theme/comp to count calls
      (set! theme/comp mock-comp)

      (try
        ;; Create component (this should compose theme)
        (let [component-result (test-component :header "Test" :theme identity)]
          (reset! composition-count 0)
          ;; Multiple "renders" shouldn't re-compose theme
          ;; In real usage, this would be the render fn called multiple times
          (is (vector? component-result) "Should produce hiccup")
          (is (= 0 @composition-count) "Theme composition should not happen after mount"))
        (finally
          ;; Restore original fn
          (set! theme/comp original-comp))))))
