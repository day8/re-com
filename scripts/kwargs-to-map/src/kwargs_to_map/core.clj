#!/usr/bin/env bb

(ns kwargs-to-map.core
  (:require [clojure.java.io :as io]
            [rewrite-clj.zip :as z]
            [clojure.set :as clj-set])
  (:import (java.io File FileNotFoundException)))

(defn get-alias
  "Given `loc`, a rewrite-clj zipper for a `:require` vector such as `[re-com.core ... :as rc]`,
   returns the alias for `re-com.core` as a string.

   So, for `[re-com.core ... :as rc]` it will return \"rc\"."
  ([loc]
   (let [alias (if-let [as-kw (z/find-value loc z/next ':as)]
                 (-> as-kw z/next z/string))]
     alias)))

(defn get-alias+referred-namespaces
  "Given `loc`, a rewrite-clj zipper for is a `:require` vector such as `[re-com.core :as rc ...]`,
  will return a map that contains:
    - the alias for `re-com.core` as a string such as `\"rc\"`,
    - a vector of the required symbols such as [p label box]."
  [loc verbose?]
  (let [loc                  (-> loc z/string z/of-string)
        refer-keyword        (z/find-value loc z/next ':refer)
        refer-namespaces-loc (if-not (nil? refer-keyword)
                               (-> refer-keyword z/next))
        referred-namespaces  (if refer-namespaces-loc
                               (z/sexpr refer-namespaces-loc) ;; Referred keywords
                               [])
        recom-alias          (get-alias loc)]
    (when (and (seq referred-namespaces)
               verbose?)
      (println "Found namespaces " referred-namespaces))
    {:used-alias          recom-alias
     :required-namespaces (into [] (remove #{'p 'p-span} referred-namespaces))}))

(defn parse-require-forms
  "Given `loc`, a rewrite-clj zipper for a namespace form such as `(ns ...)`,
  will return a map which contains:
    - a vector of referred vars such as [p box label],
    - the alias of `re-com.core` as a string such as \"rc\"."
  [loc verbose?]
  (let [loc                   (-> loc z/string z/of-string)
        required-namespaces-g (atom [])
        used-alias-g          (atom nil)]
    (loop [loc loc]
      (cond
        (z/end? loc) {:required-namespaces @required-namespaces-g
                      :used-alias          @used-alias-g}

        (and (z/vector? loc)
             (-> loc z/down z/string (= "re-com.core"))
             (z/up loc)
             (-> loc z/up z/list?)
             (-> loc z/up z/down z/sexpr (= :require)))
        (let [_                            (when verbose?
                                             (println "Found re-com.core in namespace, getting loaded components"))
              {:keys [used-alias
                      required-namespaces]} (-> loc (get-alias+referred-namespaces verbose?))]
          (when used-alias
            (reset! used-alias-g used-alias))
          (when required-namespaces
            (reset! required-namespaces-g required-namespaces))
          {:required-namespaces @required-namespaces-g
           :used-alias          @used-alias-g})

        :else
        (recur (z/next loc))))))

(defn find-key-in-keyword-args
  "Given loc which is a re-com component, we will be finding a key `k` in its keyword arguments. If the keyword
  exists, we will return it.
  For example,
  If loc is
  [border
   :border \"1px dashed red\"
   :child  [box :height \"100px\" :child \"Hello\"]]

  and `k` is `:border` we will return `:border` since it is a key in the component."
  [loc k]
  (let
    [k (str k)]
    (loop [loc (z/down loc)]
      (cond
        (nil? loc) false
        (-> loc z/string (= k)) loc
        :else (recur (z/right loc))))))

(defn conj-children
  "Given loc which is a rewrite-clj zipper of a re-com component such as `[h-box { :size \"1\" } ]`  conjoin
  in the vector the children recursively. e.g

  If children is
    `[[label :label \"A heading\"]
      [gap :size \"25px\"]]`
  It will transform loc,
    `[h-box { :size \"1\" } ]`
  to
    `[h-box { :size \"1\" } [label :label \"A heading\"] [gap :size \"25px\"]]`"
  [loc children first-iter?]
  (if (and (z/vector? children)
           (= (count (z/sexpr children)) 0))
    (-> loc (z/insert-right (z/node children)) z/root str z/of-string)
    (let [children (if (and first-iter? (z/vector? children))
                     (z/down children)
                     children)]
      (if (and (nil? children) (not first-iter?))
        (-> loc z/root str z/of-string)
        (let [loc (-> loc (z/insert-right (z/node children)) z/rightmost)]
          (conj-children loc (-> children z/right) false))))))

(defn format-kwargs-with-children
  "Given loc which is a component that contains the keyword `:children` such as a `h-box` or a `v-box` will
  add the keyword arguments to a map and then conjoin to the component the children.

  For example,
  If loc is
  [h-box :src (at)
   :size    \"1\"
   :children [[h-box :src (at)
               :size     \"1\"
               :children [[label :title \"Moved\"]]
               :align    :center]]
   :align    :center]
   :size    \"1\"]

   it will first be transformed by removing the children and adding arguments to a map
   [h-box { :src (at)
    :size    \"1\"
    :align    :center]
    :size    \"1\" } ]

   and then have its children conjoined
    [h-box { :src (at)
     :size    \"1\"
     :align    :center]
     :size    \"1\" } [[h-box :src (at)
                         :size     \"1\"
                         :children [[label :title \"Moved\"]]
                         :align    :center]]]
   "
  [loc {}]
  (let [loc            (-> loc z/string z/of-string)
        children-kw       (find-key-in-keyword-args loc :children)
        children       (when children-kw
                         (z/next children-kw))
        children-kw    (when (z/vector? children)
                         (-> children z/remove z/remove z/root str z/of-string))
        component-name (when children-kw
                         (-> children-kw z/down))
        component+     (if (symbol? (z/sexpr component-name))
                         (conj-children (-> component-name
                                            (z/insert-right (symbol "{"))
                                            z/rightmost
                                            (z/insert-right (symbol "}"))
                                            z/rightmost) children true)
                         loc)]
    component+))

(defn conj-child
  "Given loc which is a rewrite-clj zipper of a re-com component such as `[box :size \"1\"]` will add the
   keyword arguments to a map and then conjoin in the vector the child. e.g

  If child is
    `[label :label \"A heading\"]`
  It will transform loc,
    `[box :size \"1\"]`
  to
    `[box { :size \"1\" } ]`
  and then conj the child
    `[box { :size \"1\" } [label :label \"A heading\"]]`"
  [loc child]
  (let [component-name (-> loc z/down)]
    (if (symbol? (z/sexpr component-name))
      (-> component-name
          (z/insert-right (symbol "{"))
          z/rightmost
          (z/insert-right (symbol "}"))
          z/rightmost
          (z/insert-right (-> child z/node))
          z/up)
      loc)))

(defn add-keyword-args-to-map
  "Given loc which is a rewrite-clj zipper of a re-com component such as `[gap :size \"1\"]` will add the
   keyword arguments to a map. e.g

  It will transform loc,
    `[gap :size \"1\"]`
  to
    `[gap { :size \"1\" } ]`"
  [loc {}]
  (let [loc            (-> loc z/string z/of-string)
        component-name (z/down loc)
        component+     (if (symbol? (z/sexpr component-name))
                         (-> component-name
                             (z/insert-right (symbol "{"))
                             z/rightmost
                             (z/insert-right (symbol "}"))
                             z/up)
                         loc)]
    component+))

(defn format-kwargs-with-child
  "Given loc which is a rewrite-clj zipper of a re-com component such as `[box :child [label :label \"A title\"]]`
   will add the keyword arguments to a map and then conjoin in the vector the child. e.g

  It will transform loc,
    `[box :size \"1\" :child [label :label \"A title\"]]`
  to
    `[box { :size \"1\" } [label :label \"A title\"]]`"
  [loc {:keys [child optional?]}]
  (let [loc            (-> loc z/string z/of-string)
        child-kw       (find-key-in-keyword-args loc (or child :child))
        child          (when child-kw
                         (z/next child-kw))
        edited-loc     (when (or (z/vector? child)
                                 (z/string child))
                         (-> child z/remove z/remove z/root str z/of-string))]
    (if (and (not child) optional?)
      (add-keyword-args-to-map loc {})
      (if (z/vector? edited-loc)
        (conj-child edited-loc child)
        loc))))

(defn format-kwargs-in-splits
  "Given loc which is a re-com split, will format it and add its keyword arguments to a map and then conjoin
  `panel-1` and `panel-2` in order.

  For example it will transform
  [rc/v-split :src (at)
   :panel-1 [top-panel]
   :panel-2 [bottom-panel]
   :size    \"300px\"
   :initial-split \"25%\"]

   to
   [rc/v-split { :src (at)
   :size    \"300px\"
   :initial-split \"25%\" } [top-panel] [bottom-panel]]
   "
  [loc {}]
  (let [loc            (-> loc z/string z/of-string)
        panel1-kw      (find-key-in-keyword-args loc ':panel-1)
        panel1         (z/next panel1-kw)
        loc            (-> panel1-kw z/next z/remove z/remove z/root str z/of-string)
        panel2-kw      (find-key-in-keyword-args loc ':panel-2)
        panel2         (z/next panel2-kw)
        loc            (-> panel2-kw z/next z/remove z/remove z/root str z/of-string)
        component-name (z/down loc)
        component+     (if (symbol? (z/sexpr component-name))
                         (-> component-name
                             (z/insert-right (symbol "{"))
                             z/rightmost
                             (z/insert-right (symbol "}"))
                             z/rightmost
                             (z/insert-right (z/node panel2))
                             (z/insert-right (z/node panel1))
                             z/up)
                         loc)]
    component+))

(defn kwargs-matcher
  "Returns the appropriate function to format a re-com component. Component should be a string of the component free
  of the alias. "
  [component]
  (cond
    (or (= component "p")
        (= component "p-span"))
    nil

    (or (= component "modal-panel")
        (= component "alert-box")
        (= component "box")
        (= component "scroller")
        (= component "border")
        (= component "checkbox"))
    format-kwargs-with-child

    (or (= component "h-box")
        (= component "v-box"))
    format-kwargs-with-children

    (or (= component "h-split")
        (= component "v-split"))
    format-kwargs-in-splits

    :else
    add-keyword-args-to-map))

(defn kwags-to-map
  "Given loc which is a re-com component will format its keyword arguments to a map of hiccup like syntax."
  [loc {:keys [verbose alias]}]
  (let [component-name (-> loc z/down)
        comp-name-str  (if alias
                         (second (clojure.string/split (z/string component-name) #"/"))
                         (z/string component-name))
        ;; Prevent formatting if the component is formatted already
        not-mapped?    (-> component-name z/next z/map? not)
        func           (when (and (string? comp-name-str) not-mapped?)
                         (kwargs-matcher comp-name-str))]
    (when (and verbose func)
      (println "Formatting component " (z/sexpr component-name)))
    (if func
      (case comp-name-str
        "alert-box"             (func loc {:child     :body
                                           :optional? true})
        "checkbox"              (func loc {:child     :label
                                           :optional true})
        (func loc {}))
      loc)))

(defn re-com-kwargs-component?
  "Given a string and an alias, finds if the string is referring to a re-com component that should not be
   formatted to hiccup like syntax such as `p` or `p-span`.

   For example
   - Given the string `rc/p` and the alias `rc`, will return false
   - Given the string `rc/p-span` and the alias `rc`, will return false
   - Given any other string such as `rc/box` and an alias such as `rc` will return true"
  [s alias]
  (and (clojure.string/starts-with? s (str alias "/"))
       (not= s (str alias "/p"))
       (not= s (str alias "/p-span"))))

(defn find-recom-usages
  "Given `loc`, a rewrite-clj zipper for the body of a `defn`, it searches for hiccup vectors
  involving re-com components. For each one found, it calls `kwags-to-map` to format the keyword arguments to
  a map.

  Note: as rewrite-clj searches, it doesn't skip uneval forms such as
  `#_[<re-com-component> ...]` and so they will also be formatted.

   If zipper is for a form such as
     `(ns ...)

      (defn x [] ...)

      (defn y [] ...)
      ...`
   we start looping at `(ns ...)` if `namespaced?` argument is true else at `(defn x [] ...)`. If zipper is a file
   (i.e it contains a `(ns ...)` form) make sure to pass `:namespaced?` as true in order to skip the `(ns ...)` form.

   The loc can also be the zipper of any clojure form such as `defn` and `def`. Just make sure that `:namespaced?` is
   false (default) to prevent skipping the first form.

   It returns the modified loc with keyword arguments added to a map."
  [loc parsed-require {:keys [namespaced? verbose? arguments]}]
  (let [loc                 (if namespaced? (-> loc z/right) loc)  ;; skip the (ns ...) form
        required-namespaces (if arguments
                              (clj-set/difference (set (:required-namespaces parsed-require))
                                                  (set arguments))
                              (set (:required-namespaces parsed-require)))]
    (loop [loc loc]
      (cond
        (z/end? loc) (z/root loc)

        ;; when re-com component ns is loaded with :refer option
        (and (z/vector? loc) (seq (z/down loc))
             (not (clojure.string/starts-with? (-> loc z/down z/string) "#_")) ;; skip uneval forms
             (some required-namespaces (conj [] (z/sexpr (z/down loc)))))
        (recur (-> loc (z/replace (z/node (kwags-to-map loc {:verbose? verbose?}))) z/next))

        ;; when re-com component ns is loaded with :as option
        (and (z/vector? loc) (seq (z/down loc))
             (not (clojure.string/starts-with? (-> loc z/down z/string) "#_")) ;; skip uneval forms
             (-> loc z/down z/string (re-com-kwargs-component? (:used-alias parsed-require))))
        (recur (-> loc (z/replace (z/node (kwags-to-map loc {:verbose? verbose?
                                                     :alias    (:used-alias parsed-require)}))) z/next))

        :else
        (recur (z/next loc))))))

(defn require-form?
  "Given `loc` is a zipper, returns true if it represents a namespace form such as `(ns ...)`, else
  returns nil.

  Given `loc` is:
    `(ns ...)`
  returns true

  Given anything else, such as `(def ...)`, returns false"
  [loc]
  (when (-> loc z/down z/string (= "ns"))
    loc))

(defn a-function?
  "Given `loc` is a zipper for a top level form, returns true if it is a `defn`
   form, otherwise `nil`.

   Given loc as
   `(defn ...)`
   returns true

   If loc is anything else such as:
   `(ns ...)
   returns false"
  [loc]
  (and (z/list? loc)
       (-> loc z/down z/string (= "defn"))))

(defn arguments
  "Takes the loc of a function and returns the arguments of the function, the arguments are important to prevent
   formatting within variables that share names with re-com components. For example
     (defn blah
       [label arg2 arg3]      ;; <--- oops label is now shadowing a re-com component within this defn
       ...)
   will return
      [label arg2 arg3]"
  [loc]
  (loop [loc loc]
    (cond
      (z/end? loc) nil
      (z/vector? loc) loc
      :else
      (recur (z/right loc)))))

(defn parse-map-arguments
  "Given `loc` which is a function argument destructuring map, it returns a vector of the `:keys` keyword in the map.

  Given:
  {:keys [at p box]
   :or {...}}
   returns the vector:
  [at p box]

  Issue: Currently we can only get variables in the :keys section and do not support other forms of argument parsing
  such as this map
  {at :at box :box}
  which returns an empty vector."
  [loc]
  (let [vector-keys (if (map? (z/sexpr loc))
                      (:keys (z/sexpr loc))
                      [])]
    (if-not (nil? vector-keys)
      vector-keys
      (do
        (println "This style of map destructuring is not supported yet, this script only supports \n"
                 "argument destructuring in maps using `:keys [x y z]` style. If the argument map, \n"
                 (z/sexpr loc) " declares variables that don't share the same names with re-com.core \n"
                 "components, ignore this warning. Otherwise, check this function to ensure that the \n"
                 "script has formatted local variables to hiccup-like syntax thinking that they were \n"
                 "re-com.core components. \n")
        []))))

(defn parse-arguments
  "Given loc which is a vector of function arguments, returns a flattened vector of the same arguments.

   It transforms:
   [arg {:keys [at p box] ...} arg3]
   to
   [arg at p box arg3]

   The supported values in loc are strings/variables and maps."
  [loc]
  (let [loc (-> loc z/string (z/of-string {:track-position? true}) z/down)
        arguments (atom [])]
    (loop [loc loc]
      (cond
        (z/end? loc) @arguments

        (-> loc z/sexpr symbol?)
        (do
          (swap! arguments conj (-> loc z/sexpr))
          (recur (z/right loc)))

        (z/map? loc)
        (let [new-args (parse-map-arguments loc)
              new-args (concat @arguments new-args)
              new-args (into [] new-args)]
          (reset! arguments new-args)
          (recur (z/right loc)))

        :else
        (do
          (println "Unexpected form type, could not parse the function argument " (z/string loc) ". \n"
                   "Ignore this warning if the argument does not match a re-com.core component imported \n"
                   "in this file. In case the argument or an argument in it shares a name with a re-com.core \n"
                   "component that is imported in this namespace, usages of the argument in this function will \n"
                   "be formatted to map structure since this script thinks its a re-com component. Check this \n"
                   "function to prevent renaming. ")
          (recur (z/right loc)))))))

(defn parse-file
  "Takes a file and loops through the main forms. We then classify the looped form as a namespace if it is a `(ns ...)`
   form and as a function if it is a `(defn ...)` form. Other forms have a general method.
   We need to know if the looped form is a namespace form `(ns ...)` in order to get the alias for `re-com.core` and
   the referred components in `re-com.core`
   For `defn`, we need to know the arguments to prevent changing keyword args to maps within variables that share
   names with `re-com.core` namespace components.

   Given the file
   `(ns ...)                           <== namespace form

   (defn x [] ...)                     <== function form

   (def y [])                          <== general form
   ...`  this function will loop three times for the three main forms."
  [file-loc namespaced? verbose?]
  (let [parsed-require-g (atom {})
        edited?  (atom false)]
    (loop [loc file-loc]
      (when-not edited?
        (when verbose?
          (println "This file does not depend on re-com, skipping.")))
      (cond
        (z/end? loc) (z/root loc)

        ;; Loc, is a (ns ...) form
        (require-form? loc)
        (let [require-form   (-> loc z/string (z/of-string {:track-position? true}))
              parsed-require (parse-require-forms require-form verbose?)
              {required-namespaces :required-namespaces
               used-alias          :used-alias} parsed-require]
          (reset! parsed-require-g parsed-require)
          (when (or (seq required-namespaces) (seq used-alias))
            (reset! edited? true))
          (if namespaced?
            (recur (-> loc z/right))
            (recur (z/right loc))))

        ;; Loc, is a (defn x [] ...) form
        (a-function? loc)
        (let [new-loc    (-> loc z/string (z/of-string {:track-position? true}))
              arguments  (arguments (z/down new-loc))
              arguments  (when arguments
                           (parse-arguments arguments))
              edited     (find-recom-usages new-loc @parsed-require-g {:namespaced? false
                                                                       :verbose?    verbose?
                                                                       :arguments   arguments})
              last?      (nil? (z/right loc))]
          (if (and last? namespaced?)
            (-> loc (z/replace edited) z/root)
            (if namespaced?
              (recur (-> loc (z/replace edited) z/right))
              (recur (z/right loc)))))

        ;; Loc, is possibly any other thing in a file eg (def x ...) form
        :else
        (let [new-loc (-> loc z/string (z/of-string {:track-position? true}))
              edited  (find-recom-usages new-loc @parsed-require-g {:namespaced? false
                                                                    :verbose?    verbose?})]
          (if (nil? (z/right loc))
            (-> loc (z/replace edited) z/root)
            (recur (-> loc (z/replace edited) z/right))))))))

(defn read-write-file
  "Reads and writes file in case of edits. When `verbose?` is true, operations that the script does are printed to the
   console. Or when `testing?` is true, it is the same as `verbose?` being true in that operations are printed to the
   console and additionally edits are written to the console instead of files.
   For testing purposes, you can pass a file as a string using the `test-file` argument. This is convenient for testing
   when the script is not editing a file correctly or for studying the behavior of the `add-at-macro` script. An example
   of how to do this can be found in the `kwargs-to-map.core-test` namespace at the test, `test-file`."
  [file {:keys [verbose? testing? test-file]}]
  (let [abs-path    (when-not test-file
                      (.getAbsolutePath ^File file))
        _           (when abs-path
                      (println "Reading file: " abs-path))
        verbose?    (or testing? verbose?)
        loc         (try
                      (if test-file
                        (if (string? test-file)
                          (z/of-string test-file {:track-position? true})
                          (println "The file to test should be a string."))
                        (-> file slurp (z/of-string {:track-position? true})))
                      (catch Exception e (println "Error reading file: " e)))
        namespaced? (when loc
                      (-> loc z/leftmost z/down z/string (= "ns"))) ;; File contains (ns ...) at the beginning?
        edited-file (parse-file loc namespaced? verbose?)]
    (if namespaced?
      (do
        (if (or testing? test-file)
          (println edited-file "\n")
          (spit abs-path edited-file))
        (when-not test-file
          (println "Writing file: " abs-path "\n")))
      (when verbose?
        (println "This namespace does not have any dependencies, skipping.")))))

;; When print? is true, the changes this script makes are displayed to the console.
(def print? false)

;; When testing? is true, print above is true plus, the changes are not saved to file.
(def testing? false)

(defn run-script
  "Runs this script on the directory or file at the given absolute path.

   Checks that the absolute path exists, otherwise prints an error.

   In the case that the absolute path resolves to a directory, we find all
   files in that directory tree that have a `.cljs` extension and we run
   this script's modifications on those files."
  [abs-path]
  (let [directory          (io/file abs-path)
        exists?            (.exists directory)
        files              (if exists?
                             (file-seq directory))
        filter-valid-files (filter #(and (.exists %)
                                         (.isFile %)
                                         (clojure.string/includes? % ".cljs")) files)]
    (cond
      (not exists?) (println "The directory/file provided does not exist.")
      (not (seq filter-valid-files)) (println "No valid files were found in " abs-path ".")

      :else
      (doseq [file filter-valid-files]
        (read-write-file file {:verbose? print?
                               :testing? testing?})))))

(defn -main
  "Call this function with the directory path as an argument to run this script. This function
  is called after `lein run` with the arguments passed to lein run. Also see `run-script` above."
  [& args]
  (let [directory (str (ffirst args))]
    (if (seq directory)
      (run-script directory)
      (println "Directory/File not provided"))))


;; This section of the code is required for babashka to start our script at `-main`
(when (= *file* (System/getProperty "babashka.file"))
  (-main *command-line-args*))
