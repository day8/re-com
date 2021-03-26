(ns add-at-macro.core-test
  (:require [clojure.test :refer :all]
            [add-at-macro.core :refer :all]
            [rewrite-clj.zip :as z]))

(def ^:private verbose? true)

(def recom-a (z/of-string "[re-com.core :refer [p h-box]]"))
(def recom-b (z/of-string "[re-com.core :refer [p at h-box]]"))
(def recom-c (z/of-string "[re-com.core :as rc]"))
(def recom-d (z/of-string "[re-com.core :refer-macros [at] :refer [p h-box]]"))
(def recom-e (z/of-string "[p at h-box]"))

(deftest test-get-alias
  (testing "Test getting alias from a re-com import form"
    (is (= (get-alias recom-c) "rc") "Couldn't find alias")))

(deftest test-get-at-loc
  (testing "Test getting loc of the `at` macro in a re-com import form"
    (is (= (-> (find-at-loc recom-e) z/sexpr) 'at) "Couldn't find loc of at macro")))

(deftest test-getting-require-macros-in-require
  (testing "Test getting `:refer-macros` options from a re-com import form"
    (is (-> (find-require-macros-in-require recom-d)
            (z/find-value z/next ':refer-macros)
            z/right
            z/vector?) "Couldn't find refer-macros in the form")))

(deftest test-append-at-to-refer-option
  (testing "Test appending at macro to require import form `[re-com.core :refer [] ...]`"
    (let [require-macro-a recom-a
          edited-require  (append-at-to-recom-require-form require-macro-a verbose?)
          namespace       (-> edited-require :current-loc z/down z/string)
          referred-vars   (-> edited-require :current-loc (z/find-value z/next ':refer))
          referred-vars-a (when referred-vars
                            (z/next referred-vars))
          referred-vars   (when referred-vars-a
                            (into [] (z/sexpr referred-vars-a)))]
      (is (-> namespace (= "re-com.core")) "Looks like there was a problem parsing the form.")
      (is (and (not (nil? referred-vars-a))
               (z/vector? referred-vars-a)) "Refer option was not added")
      (is (and referred-vars
               (some #{'at} referred-vars)) "At macro was not added to the refer vector."))))

(deftest test-add-refer-option
  (testing "Test adding refer option with at macro to require statement"
    (with-redefs [recom-a recom-c]
      (test-append-at-to-refer-option))))

(deftest test-remove-at-from-required-macros
  (testing "Test removing at macro from re-com import form in require-macros `[re-com.core ...]`"
    (let [require-macro-a recom-b
          edited-require  (remove-at-from-required-macros require-macro-a verbose?)
          namespace       (-> edited-require z/down z/string)
          referred-vars   (-> edited-require (z/find-value z/next ':refer))
          referred-vars-a (when referred-vars
                            (z/next referred-vars))
          referred-vars   (when referred-vars-a
                            (into [] (z/sexpr referred-vars-a)))]
      (is (-> namespace (= "re-com.core")) "Looks like there was a problem parsing the form.")
      (is (if (seq referred-vars) (not (some #{'at} referred-vars)) true) "At macro was not successfully deleted"))))

(def import-form-a (z/of-string
                     "(ns re-demo.alert-box
                       (:require-macros
                         [reagent.debug :refer [dbg prn println log dev? warn warn-unless]]
                         [re-com.core   :refer [at]])
                       (:require
                         [re-com.core   :refer [h-box v-box box line gap title label alert-box alert-list p]]
                         [re-com.alert  :refer [alert-box-parts-desc alert-box-args-desc alert-list-args-desc]]
                         [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
                         [re-com.util   :refer [px]]
                         [reagent.core  :as    reagent]))"))

(def import-form-b (z/of-string
                     "(ns re-demo.alert-box
                       (:require-macros
                         [reagent.debug :refer [dbg prn println log dev? warn warn-unless]])
                       (:require
                         [re-com.core   :refer [h-box v-box box line gap title label alert-box alert-list p]]
                         [re-com.alert  :refer [alert-box-parts-desc alert-box-args-desc alert-list-args-desc]]
                         [re-demo.utils :refer [panel-title title2 title3 parts-table args-table github-hyperlink status-text]]
                         [re-com.util   :refer [px]]
                         [reagent.core  :as    reagent]))"))

(deftest test-require-form?
  (testing "Test checking if a form is a (ns ...) form."
    (is (require-form? import-form-a) "This is not a (ns ...) form")))

(deftest test-fix-require-forms
  (testing "Fix namespace form; loading at macro and removing at macro in require-macros"
    (let [fixed-require-form (fix-require-forms import-form-a verbose?)
          {:keys [required-namespaces
                  used-alias
                  edit-require]}       fixed-require-form
          edit-require                 (-> edit-require .string (z/of-string {:track-position? true}))]
      (loop [loc edit-require]
        (cond
          (z/end? loc) nil
          (find-re-com-in-require loc {}) (with-redefs [recom-a loc]
                                            (test-append-at-to-refer-option))
          :else
          (recur (z/next loc))))
      (loop [loc edit-require]
        (cond
          (z/end? loc) nil
          (find-re-com-in-require loc {:macros? true}) (with-redefs [recom-a loc]
                                                         (test-remove-at-from-required-macros))
          :else
          (recur (z/next loc)))))))

(def component-source-a (z/of-string "
[h-box
 :src (at)
 :children []]" {:track-position? true}))

(def component-source-b (z/of-string "
[h-box
 :align :center
 :children []]" {:track-position? true}))

(deftest test-check-source-already-added?
  (testing "Testing function that checks for :src option in source code"
    (let [added-y? (check-source-added-already? component-source-a)
          added-n? (check-source-added-already? component-source-b)]
      (is added-y?)
      (is (not added-n?)))))

(deftest test-add-src-in-component
  (testing "Testing function that adds `:src (at)` in re-com component"
    (let [source-aa (add-at-in-component component-source-a verbose?)
          source-b  (add-at-in-component component-source-b verbose?)]
      (with-redefs [component-source-a source-aa]
        (test-check-source-already-added?))
      (with-redefs [component-source-a source-b]
        (test-check-source-already-added?)))))

(def source-component (z/of-string "
(defn new-in-version
  \"given some version text, return a component that displays 'new in version...'\"
  [version style]
  [:span
   [:span.bold \"New in \"]
   [:span {:style style} version]])"))


(deftest test-function-form?
  (testing "Test checking if a form is a function definition form."
    (is (a-function? source-component) "This is not a (ns ...) form")))

(deftest test-require-form?
  (testing "Test getting function arguments."
    (is (z/vector? (arguments (z/down source-component))) "Could not get the function arguments.")))

(def p-component (z/of-string "
[rc/p-span        ;; also passes for `p`
 :align :center
 :children []]" {:track-position? true}))

(deftest test-re-com-component?
  (testing "Testing if this is a re-com component"
    (is (not (re-com-component? (-> p-component z/down z/string) "rc")) "This is not a valid re-com component")))

(def directory "")

(def file-example "
(ns sample-namespace.core
  (:require
    [re-com.core :refer [box title p v-box] :as re-com]))

(def test [re-com/title
            :label \"Title\"])

(defn a-function
 [title]
 [p \"Should not change.\"]
 [title
  :label \"Should not change.\"]
 [v-box
  :align :center
  :children [[re-com/title
              :label \"Title\"]]])

(def test2 [title               <== Src will be added here.
            :label \"Title\"])
")

(deftest test-file
  (testing "Testing effect of at macro script on a file."
    (read-write-file nil {:testing? true :test-file file-example})))


(deftest test-script
  (testing "Test the full script in general"
    ;; When `:print?` is true, this translates to verbose? in the script which is a flag to tell the script to print
    ;; to the console the changes it will do. When the flag `:testing?` is true, the console does not save the changes
    ;; it makes to file but prints the file to console. When `:testing?` is true, `:verbose?` is always true
    (with-redefs [print? verbose?
                  testing? verbose?]
      (if (seq directory)
        (run-script directory)
        (println "Directory/File not provided")))))

(defn runner [directory]
  (with-redefs [directory directory]
    (test-script)))