(ns add-at-macro.core
  (:require [clojure.java.io :as io]
            [rewrite-clj.zip :as z]
            [clojure.set :as clj-set])
  (:import (java.io File FileNotFoundException)))

(defn get-alias
  "Given loc which is a require vector such as `[re-com.core ... :as rc]` will
   returns the alias as a string.

   Given `[re-com.core ... :as rc]` will return 'rc'."
  ([loc]
   (let [alias (if-let [as-kw (z/find-value loc z/next ':as)]
                 (-> as-kw z/next z/string))]
     alias)))

(defn find-at-loc
  "Given a loc in the form of a `:refer` vector containing re-com components such as [p h-box at ...]
  will return a rewrite-clj zipper of the `at` macro if it is in the vector, otherwise returning `nil`.

  Given [p h-box at ...] will return `at`"
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (z/end? loc) nil
      (-> loc z/string (= "at")) loc
      :else
      (recur (z/right loc)))))

(defn find-require-macros-in-require
  "Given loc which is a require vector in the `:require` section such as
  `[re-com.core :as rc :require-macros [at]] will return a modified vector with the `at` macro removed.
  The `at` macro will be added in the `:refer` option.

  It will transform
  `[re-com.core :as rc :require-macros [x at z]]`
  to
  `[re-com.core :as rc :require-macros [x z]]`

  The original loc is returned if the `at` macro is not imported."
  [loc]
  (let [refer-macros  (z/find-value loc z/next ':refer-macros)
        refer-macros  (z/right refer-macros)
        at-macro      (when refer-macros
                        (find-at-loc refer-macros))]
    (if at-macro
      (-> at-macro z/remove z/up z/up)
      loc)))

(defn append-at-to-recom-require-form
  "Give loc which is a require vector such as `[re-com.core :as rc ...]` will return map that contains: the alias as
   a string, a vector of the required namespaces such as [p label box] and a modified vector with at macro imported.

   It might transform loc from:
   `[re-com.core :as rc]`
   to
   `[re-com.core :as rc :refer [at]]`,

   And also from:
   `[re-com.core :as rc :refer [x y z]]`
   to
   `[re-com.core :as rc :refer [at x y z]]`"
  [loc verbose?]
  (let [loc                  (-> loc z/string z/of-string)
        loc                  (-> loc find-require-macros-in-require)
        refer-keyword        (z/find-value loc z/next ':refer)
        refer-namespaces-loc (if-not (nil? refer-keyword)
                               (-> refer-keyword z/next))
        referred-namespaces  (if refer-namespaces-loc
                               (into [] (z/sexpr refer-namespaces-loc)) ;; Referred keywords
                               [])
        has-at?              (some #{'at} referred-namespaces) ;; If at macro is already loaded.
        at+namespaces        (into [] (cons 'at referred-namespaces))
        recom-alias          (get-alias loc)]
    {:used-alias          recom-alias
     :required-namespaces (into [] (remove #{'p 'p-span} referred-namespaces))
     :current-loc         (if (z/vector? refer-namespaces-loc)
                            (if-not has-at?
                              (do
                                (when verbose?
                                  (println "Appending at macro."))
                                (-> refer-namespaces-loc
                                    (z/replace at+namespaces)
                                    z/up))
                              (do
                                (when verbose?
                                  (println "Found at macro."))
                                loc))   ;; recom form already loads at macro
                            (do
                              (when verbose?
                                (println "Adding refer option."))
                              (-> loc
                                  (z/append-child (z/sexpr (z/of-string ":refer")))
                                  (z/append-child (z/sexpr (z/of-string "[at]"))))))}))

(defn remove-at-from-required-macros
  "Give loc which is a require vector such as `[re-com.core :refer [at p box] ...]` in the `:require-macros` section
  will return a modified vector with at macro removed.

  It might transform loc from:
  `[re-com.core :refer [p at box]`
  to
  `[re-com.core :refer [p box]`"
  [loc verbose?]
  (let [refer-keyword       (z/find-value loc z/next :refer)
        referred-namespaces (if refer-keyword
                              (z/right refer-keyword))]
    (if-let [at-loc (and refer-keyword
                         (z/vector? referred-namespaces)
                         (find-at-loc referred-namespaces))]
      (do
        (when verbose?
          (println "Removing at macro from :require-macros. This script will add it again to :require"))
        (-> at-loc z/remove z/up z/up))
      loc)))

(defn find-re-com-in-require
  "Given loc which is a require vector such as [re-com.core :as rc ...]` will return the
  loc if it is found in the `:require` section.

  If loc exists in :require such as
  `(:require
    [re-com.core :as rc ...]
    ...)`
  it will return the rewrite-clj zipper:
  `[re-com.core :as rc ...]`.

  If the `macros?` argument is true, the function will do the same operation but check the `:require-macros`
  section instead.
  If macros? option is true, the above example returns nil and returns the zipper iff the require vector exists in
  :refer-macros section such as:
  `(:require-macros
    [re-com.core :as rc ...]
    ...)`"
  [loc {:keys [macros?]}]
  (when (and (z/vector? loc)
             (-> loc z/down z/string (= "re-com.core"))
             (z/up loc)
             (-> loc z/up z/list?)
             (-> loc z/up z/down z/sexpr (= (if macros?
                                              :require-macros
                                              :require))))
    loc))

(defn fix-require-forms
  "Given loc which is a namespace list such as `(ns ...)` will return a map which contains: a vector of the required
  namespaces such as [p box label], the alias as a string such as 'rc' and a modified list with the at macro
  imported in the re-com vector. It loops through the namespace list and can call functions to format the location
  of the `at` macro.

  If the list contains a re-com vector in the `:require` section such as [re-com.core :as rc] the function
  `append-at-to-recom-require-form` is called with the vector which imports the `at` macro.
  If the list contains a re-com vector in the `:require-macros` section, the function `remove-at-from-required-macros`
  is called with the vector which checks that the `at` macro is not loaded and removes the `at` macro if it exists.
  The macro is added again in the require vector in the :refer section."
  [loc verbose?]
  (let [required-namespaces-g (atom [])
        used-alias-g (atom nil)]
    (loop [loc loc]
      (cond
        (z/end? loc) {:required-namespaces @required-namespaces-g
                      :used-alias          @used-alias-g
                      :edit-require        (-> loc z/root)}

        (find-re-com-in-require loc {})
        (let [_                            (when verbose?
                                             (println "Found re-com.core in namespace, checking for at macro"))
              {:keys [current-loc
                      used-alias
                      required-namespaces]} (-> loc (append-at-to-recom-require-form verbose?))]
          (when used-alias
            (reset! used-alias-g used-alias))
          (when required-namespaces
            (reset! required-namespaces-g required-namespaces))
          (recur (-> loc (z/replace (z/node current-loc)) z/next)))

        (find-re-com-in-require loc {:macros? true})
        (do
          (when verbose?
            (println "Found re-com.core in :require-macros, checking if `at` macro is referred."))
          (recur (-> loc (remove-at-from-required-macros verbose?) z/next)))

        :else
        (recur (z/next loc))))))

(defn check-source-added-already?
  "Given loc which is a re-com component and a vector such as [box :src (at) :child [...]], returns true if the `:src` option exists."
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (nil? loc) false
      (-> loc z/string (= ":src")) true
      :else
      (recur (z/right loc)))))

(defn add-at-in-component
  "Given a re-com component which is a vector such as `[box :child [...]]` returns a modified vector with the `:src`
  option added.

  It transforms
  `[box :child [...]]`
  to
  `[box :src (at) :child [...]]`

  Given a component with the src option already added such as
  `[box :src (at) :child [...]]
  it returns the component unedited."
  [loc verbose?]
  (if-not (check-source-added-already? loc)                 ;; Check if :src option is added already
    (do
      (when verbose?
        (println "Adding :src option at line " (first (z/position loc)) " column " (second (z/position loc))))
      (-> loc z/down
          (z/insert-right (z/node (z/of-string "(at)")))
          (z/insert-right (z/node (z/of-string ":src")))
          z/up))
    loc))

(defn find-recom-usages
  "Given loc which is a rewrite-clj zipper, it loops through the zipper and finds re-com components. Once a re-com
   component is found, it calls the function `add-at-in-component` with the component to add `:src` annotations to the
   component.

   If zipper is a file such as
   `(ns ...)

   (defn x [] ...)

   (defn y [] ...)
   ...`
   we start looping at `(ns ...)` if `namespaced?` argument is true else at `(defn x [] ...)`. If zipper is a file make
   sure to pass `:namespaced` as true in order to skip the `(ns ...)` form.

   The loc can also be the zipper of any clojure form such as `defn` and `def`. Just make sure that `:namespaced` is
   false(default) to prevent skipping the first form.

   It returns the modified loc with `:src` annotations added to its re-com components."
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
             (some required-namespaces (conj [] (z/sexpr (z/down loc)))))
        (recur (-> loc (add-at-in-component verbose?) z/next))

        ;; when re-com component ns is loaded with :as option
        (and (z/vector? loc) (seq (z/down loc))
             (-> loc z/down z/string (clojure.string/starts-with? (str (:used-alias parsed-require) "/"))))
        (recur (-> loc (add-at-in-component verbose?) z/next))

        :else
        (recur (z/next loc))))))

(defn require-form?
  "Given loc which can be the zipper of anything, determines if the loc is a namespace form such as (ns ...) and
  returns true if so. Else, it returns nil.

  Given loc is:
  `(ns ...)`
  returns true

  Given anything else such as `(def ...)` returns false"
  [loc]
  (when (-> loc z/down z/string (= "ns"))
    loc))

(defn a-function?
  "Given loc which is the zipper of anything, determines if loc is a clojure `defn` function.

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
   adding `:src` annotations to local variables that share names with re-com components. For example
   (defn blah
     [label arg2 agr3]      ;; <--- oops label is now shadowing a re-com component within this defn
     ...)"
  [loc]
  (loop [loc loc]
    (cond
      (z/end? loc) nil
      (z/vector? loc) loc
      :else
      (recur (z/right loc)))))

(defn parse-map-arguments
  "Given loc which is a map that parses function arguments, it returns a vector of the `:keys` keyword in the map.

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
                 "\"(z/sexpr loc)\" declares variables that don't share the same names with re-com.core \n"
                 "components, ignore this warning. Otherwise, check this function to ensure that the \n"
                 "script has not added `:src` annotations to local variables thinking that they were \n"
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
                   "get `:src` annotation added since the script thinks its a re-com component. Check this \n"
                   "function to prevent renaming. ")
          (recur (z/right loc)))))))

(defn parse-file
  "Takes a file and loops through the main forms. We then classify the looped form as a namespace if it exists as
  `(ns ...)` and as a function if it exists as, `(defn ...)`. Other forms have a general method.
  We need to know if the looped form is a namespace form `(ns ...)` in order to add the at macro in the `:require`
  section.
  For `defn`, we need to know the arguments to prevent renaming arguments that share names with re-com.core namespace
  components.

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
              parsed-require (fix-require-forms require-form verbose?)
              {required-namespaces :required-namespaces
               used-alias          :used-alias
               edited-require      :edit-require} parsed-require]
          (reset! parsed-require-g parsed-require)
          (when (or (seq required-namespaces) (seq used-alias))
            (reset! edited? true))
          (if namespaced?
            (recur (-> loc (z/replace edited-require) z/right))
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
   console. `testing?` supersedes `verbose?` with the extra option that the changes being printed to file. testing? is
   convenient for testing as it prints the changes to console instead of saving them to disk."
  [file {:keys [verbose? testing?]}]
  (let [abs-path    (.getAbsolutePath ^File file)
        _           (println "Reading file: " abs-path)
        verbose?    (or testing? verbose?)
        loc         (try
                      (-> file slurp (z/of-string {:track-position? true}))
                      (catch Exception e (println "Error reading file: " e)))
        namespaced? (when loc
                      (-> loc z/leftmost z/down z/string (= "ns"))) ;; File contains (ns ...) at the beginning?
        edited-file (if namespaced?
                      (parse-file loc namespaced? verbose?))]
    (if namespaced?
      (do
        (if testing?
          (println edited-file "\n")
          (spit abs-path edited-file))
        (println "Writing file: " abs-path "\n"))
      (when verbose?
        (println "This namespace does not have any dependencies, skipping.")))))

;; When print? is true, the changes this script makes are displayed to the console.
(def print? false)

;; When testing? is true, print above is true plus, the changes are not saved to file.
(def testing? false)

(defn run-script
  "Checks that the absolute path exists. In case it resolves to a directory, we check
  that the files in that directory are all valid files and have the .cljs extension"
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

(defn -main [& args]
  "Call this function with the directory path as an argument to run this script. This function
  is called after `lein run` with the arguments passed to lein run."
  (let [directory (str (first args))]
    (if (seq directory)
      (run-script (str (first args)))
      (println "Directory/File not provided"))))


;; This section of the code is required for lein exec to start our script at `-main`
(try (require 'leiningen.exec)
     (when @(ns-resolve 'leiningen.exec '*running?*)
       (apply -main (rest *command-line-args*)))
     (catch FileNotFoundException e))