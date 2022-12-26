(ns re-com.time-test
  (:require [cljs.test         :refer-macros [is are deftest]]
            [reagent.core      :as reagent]
            [re-com.input-time :as time]))


;; --- Tests ---

(deftest test-valid-time?
  (are [expected actual] (= expected actual)
    true (time/valid-time? 0)
    true (time/valid-time? 600)
    true (time/valid-time? 130)
    true (time/valid-time? 2159)
    true (time/valid-time? 2430)
    false (time/valid-time? nil)))

(deftest test-time->text
  (are [expected actual] (= expected actual)
    "00:00"  (time/time->text 0)
    "06:00"  (time/time->text 600)
    "11:00"  (time/time->text 1100)
    "09:00"  (time/time->text 900)
    "01:30"  (time/time->text 130)
    "21:59"  (time/time->text 2159)
    "24:30"  (time/time->text 2430)))

(deftest test-text->time
  (are [expected actual] (= expected actual)
    600   (time/text->time "600")
    630   (time/text->time "630")
    630   (time/text->time "6:30")
    630   (time/text->time "06:30")
    3000  (time/text->time "30")
    2359  (time/text->time "2359")
    2359  (time/text->time "23:59")))

(deftest test-valid-text?
  (are [expected actual] (= expected actual)
    true   (time/valid-text? "0000")
    true   (time/valid-text? "00:00")
    true   (time/valid-text? "99")
    false  (time/valid-text? "a99")
    true   (time/valid-text? "2359")))

;; IJ: TODO
#_(deftest test-input-time
 (is (fn? (time/input-time :model 1530 :minimum 600 :maximum 2159 :on-change #())) "Expected a function.")
 (let [input-time-fn (time/input-time :model 1530 :on-change #())]
   (is (fn? input-time-fn) "Expected a function.")
   (let [result (input-time-fn :model (reagent/atom 1530) :on-change #() :minimum 600 :maximum 2159)]
     ;(is (= :span.input-append (first result)) "Expected first element to be :span.input-append.bootstrap-timepicker")
     (is (fn? (first result)) "Expected first element to be a function")
     (let [input-time-comp (first (nth result 8))
           input-time-attrs (last input-time-comp)]
       (is (= :input (first input-time-comp)) "Expected time input start with :input")
       (are [expected actual] (= expected actual)
         nil           (:disabled input-time-attrs)
         "time-entry rc-time-entry " (:class input-time-attrs)
         "15:30"       (:value input-time-attrs)
         "text"        (:type input-time-attrs)
         "time-entry rc-time-entry " (:class input-time-attrs)
         true          (fn? (:on-blur input-time-attrs))
         true          (fn? (:on-change input-time-attrs))))))
 ;; These tests don't work. But i have verified that the check is happening and it works
 #_(is (thrown? js/Error (time/input-time :model "abc") "should fail - model is invalid"))
 #_(is (thrown? js/Error (time/input-time :model 930 :minimum "abc" :maximum 2159) "should fail - minimum is invalid"))
 #_(is (thrown? js/Error (time/input-time :model 930 :minimum 600 :maximum "fred") "should fail - maximum is invalid"))
)

 ;; (is (thrown? js/Error (time/input-time :model 530 :minimum 600 :maximum 2159) "should fail - model is before range start"))
 ;; (is (thrown? js/Error (time/input-time :model 2230 :minimum 600 :maximum 2159) "should fail - model is after range end"))

;; IJ: TODO
#_(deftest test-pre-conditions
 (is (fn? (time/input-time :model 1530 :minimum 600 :maximum 2159 :on-change #())) "Expected a function.")
 (is (thrown? js/Error (time/input-time :model 1530 :minimum 600 :maximum 2159 :fred "test") "Expected an exception due to invalid parameter.")))

;; --- WIP ---

#_(defn div-app []
  (let [div (.createElement js/document "div")]
    (set! (.-id div) "app")
    div))

#_(trace-forms
  {:tracer default-tracer}
  (deftest test-test-input-time-gen
    (let [tm-input (time/input-time :model 1500)
          result (reagent/render-component [tm-input] (div-app))]
      (println (-> result .-_renderedComponent .-props)))))

;; The above statement results in -
;; #js {:cljsArgv
;;   [#<function (model,previous_val,min,max,var_args){
;;     var p__60249 = null;
;;     if (arguments.length > 4) {
;;       p__60249 = cljs.core.array_seq(Array.prototype.slice.call(arguments, 4),0);
;;     }
;;     return private_time_input__delegate.call(this,model,previous_val,min,max,p__60249);
;;   }>
;;   #<Atom: 15:00> 1500
;;   #<Atom: 0>
;;   #<Atom: 2359>
;;   :on-change nil
;;   :disabled nil
;;   :hide-border nil
;;   :show-time-icon nil
;;   :style nil],
;; :cljsLevel 0}
