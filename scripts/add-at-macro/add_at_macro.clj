#!/usr/bin/env bb

(require '[babashka.deps :as deps])

(deps/add-deps
  '{:deps {docopt/docopt {:git/url "https://github.com/nubank/docopt.clj"
                          :sha     "12b997548381b607ddb246e4f4c54c01906e70aa"}}})

(ns add-at-macro
    (:require
      [clojure.java.io :as io]
      [rewrite-clj.zip :as z]
      [clojure.set :as clj-set]
      [docopt.core :as docopt])
    (:import (java.io File)))

(defn get-alias
  "Given `loc`, a rewrite-clj zipper for a `:require` vector such as `[re-com.core ... :as rc]`,
   returns the alias for `re-com.core` as a string.

   So, for `[re-com.core ... :as rc]` it will return \"rc\"."
  ([loc]
   (let [alias (if-let [as-kw (z/find-value loc z/next ':as)]
                 (-> as-kw z/next z/string))]
     alias)))

(defn find-at-loc
  "Given a `loc`, a rewrite-clj zipper for the `:refer` vector within a `:require`, for example [p h-box at ...],
  will return the rewrite-clj zipper of the `at` element if it is in the vector, otherwise `nil`.
  "
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (z/end? loc) nil
      (-> loc z/string (= "at")) loc
      :else
      (recur (z/right loc)))))

(defn find-require-macros-in-require
  "Given `loc`, a rewrite-clj zipper for a `:require` vector with `at` in the `:require-macros` section, for example
  `[re-com.core :as rc :require-macros [at]]`, will return a modified vector with `at` removed.

  For example, it will transform
    `[re-com.core :as rc :require-macros [x at z]]`
  to
    `[re-com.core :as rc :require-macros [x z]]`

  `loc` is returned unchanged if `at` is not found within `:require-macros`."
  [loc]
  (let [refer-macros  (z/find-value loc z/next ':refer-macros)
        refer-macros  (z/right refer-macros)
        at-macro      (when refer-macros
                        (find-at-loc refer-macros))]
    (if at-macro
      (-> at-macro z/remove z/up z/up)
      loc)))

(defn append-at-to-recom-require-form
  "Given `loc`, a rewrite-clj zipper for is a `:require` vector such as `[re-com.core :as rc ...]`,
  will return a map that contains:
    - the alias for `re-com.core` as a string such as `\"rc\"`,
    - a vector of the required symbols such as [p label box]
    - a modified vector with `at` required.

   For example, it would transform a `loc` from:
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

(defn remove-at-from-require-macros
  "Given `loc`, a rewrite-clj zipper for a `:require-macros` vector such as `[re-com.core :refer [at] ...]`
  in the `:require-macros` list, will return a modified vector with `at` removed.

  It might transform loc from:
    `[re-com.core :refer [at]]`
  to
    `[re-com.core :refer []]`" ;; TODO: actually remove empty :refer ?
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
  "Given `loc`, a rewrite-clj zipper for a `:require` vector which loads `re-com.core`
  such as `[re-com.core :as rc ...]`, will return the `loc` of the vector, if its parent
  is a `:require` list and `macros?` argument is false. Otherwise `nil`.

  For example, if `loc` is
    `[re-com.core :as rc ...]`
  and its parent is a `:require` list:
    `(:require
      [re-com.core :as rc ...]
      ...)`
  it will return the rewrite-clj zipper for:
    `[re-com.core :as rc ...]`.

  If the `macros?` argument is true, the function will do the same operation but check the `:require-macros`
  list instead.

  For example, if `loc` is
    `[re-com.core :as rc ...]`
  and its parent is a `:require-macros` list:
    `(:require-macros
      [re-com.core :as rc ...]
      ...)`
  it will return the rewrite-clj zipper for:
    `[re-com.core :as rc ...]`."
  [loc {:keys [macros?]}]
  (when (and (z/vector? loc)
             (-> loc z/down z/string (= "re-com.core"))
             (z/up loc)
             (-> loc z/up z/list?)
             (-> loc z/up z/down z/sexpr (= (if macros?
                                              :require-macros
                                              :require))))
    (if (clojure.string/includes? (z/string loc) "#_")
      (println "
 This `:re-com` vector seems to contain an uneval form with `#_`. \n" (z/node loc) "\n" "
 In case you have a component like `[re-com.core #_:as :as rc]` and wanted to get the alias, (`rc`),
 we would normally
 1. Search for `:as` with `(z/find-value loc z/next ':as)` and get the element next to it. Or;
 2. Loop through the children of the vector and find the element next to the child `:as`.
 However these methods will fail in the case of uneval forms since rewrite-clj zip reads them as code
 and not comments. For the component `[re-com.core #_:as :as rc]` rewrite-clj will find the loc `#_:as`
 and confuse it for both of the methods above which are instead looking for `:as`.
 As a result the script can't guess how many uneval forms exist before the intended location of the at macro.
 In case the form " (if macros? "imports the `at` macro, remove it manually."
                                "needs to import the `at` macro, import it manually."))
      loc)))

(defn fix-require-forms
  "Given `loc`, a rewrite-clj zipper for a namespace form such as `(ns ...)`,
  will return a map which contains:
    - a vector of referred vars such as [p box label],
    - the alias of `re-com.core` as a string such as \"rc\"
    - a modified list with `at` added in the re-com vector.
  It loops through the namespace list and can call functions to format the location
  of `at`.

  If the list contains a re-com vector in the `:require` section such as [re-com.core :as rc] the function
  `append-at-to-recom-require-form` is called with the vector which imports the `at` macro.
  If the list contains a re-com vector in the `:require-macros` section, the function `remove-at-from-require-macros`
  is called with the vector which checks that the `at` macro is not referred and removes the `at` macro if it exists.
  The macro is added again in the require vector in the :refer section."
  [loc verbose?]
  (let [required-namespaces-g (atom [])
        used-alias-g          (atom nil)]
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
          (recur (-> loc (remove-at-from-require-macros verbose?) z/next)))

        :else
        (recur (z/next loc))))))

(defn check-source-added-already?
  "Given `loc`, a rewrite-clj zipper for a hiccup vector for a re-com component,
  such as [box :src (at) :child [...]], will return true if it contains a `:src` argument."
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (nil? loc) false
      (-> loc z/string (= ":src")) true
      :else
      (recur (z/right loc)))))

(defn add-at-in-component
  "Given `loc`, a rewrite-clj zipper for a hiccup vector for a re-com component,
  such as `[box :child [...]]`, will return a modified vector with the `:src`
  argument added.

  It transforms
    `[box :child [...]]`
  to
    `[box :src (at) :child [...]]`

  Given a component with the src option already added such as
    `[box :src (at) :child [...]]
  it returns the vector unmodified."
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

(defn re-com-kwargs-component?
  "Given a string and an alias, finds if the string is referring to a re-com component that should not be
   added the `:src` annotations. i.e `p` or `p-span`.

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
  involving re-com components. For each one found, it calls `add-at-in-component` to add `:src`
  to the existing arguments.

  Note: as rewrite-clj searches, it doesn't skip uneval forms such as
  `#_[<re-com-component> ...]` and so they will also get :src annotations.

   If zipper is for a form such as
     `(ns ...)

      (defn x [] ...)

      (defn y [] ...)
      ...`
   we start looping at `(ns ...)` if `namespaced?` argument is true else at `(defn x [] ...)`. If zipper is a file
   (i.e it contains a `(ns ...)` form) make sure to pass `:namespaced?` as true in order to skip the `(ns ...)` form.

   The loc can also be the zipper of any clojure form such as `defn` and `def`. Just make sure that `:namespaced?` is
   false (default) to prevent skipping the first form.

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
             (not (clojure.string/starts-with? (-> loc z/down z/string) "#_")) ;; skip uneval forms
             (some required-namespaces (conj [] (z/sexpr (z/down loc)))))
        (recur (-> loc (add-at-in-component verbose?) z/next))

        ;; when re-com component ns is loaded with :as option
        (and (z/vector? loc) (seq (z/down loc))
             (not (clojure.string/starts-with? (-> loc z/down z/string) "#_")) ;; skip uneval forms
             (-> loc z/down z/string (re-com-kwargs-component? (:used-alias parsed-require))))
        (recur (-> loc (add-at-in-component verbose?) z/next))

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
   adding `:src` annotations to local variables that share names with re-com components. For example
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
  "Takes a file and loops through the main forms. We then classify the looped form as a namespace if it is a `(ns ...)`
   form and as a function if it is a `(defn ...)` form. Other forms have a general method.
   We need to know if the looped form is a namespace form `(ns ...)` in order to add the at macro in the `:require`
   section, and possibly remove the at macro in the `:require-macros` section.
   For `defn`, we need to know the arguments to prevent renaming arguments that share names with `re-com.core` namespace
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
   console. Or when `testing?` is true, it is the same as `verbose?` being true in that operations are printed to the
   console and additionally edits are written to the console instead of files.
   For testing purposes, you can pass a file as a string using the `test-file` argument. This is convenient for testing
   when the script is not editing a file correctly or for studying the behavior of the `add-at-macro` script. An example
   of how to do this can be found in the `add-at-macro-test` namespace at the test, `test-file`."
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
        edited-file (if namespaced?
                      (parse-file loc namespaced? verbose?))]
    (if namespaced?
      (do
        (if (or testing? test-file)
          (println edited-file "\n")
          (spit abs-path edited-file))
        (when-not test-file
          (println "Writing file: " abs-path "\n")))
      (when verbose?
        (println "This namespace does not have any dependencies, skipping.")))))

(defn run-script
  "Runs this script on the directory or file at the given absolute path.

   Checks that the absolute path exists, otherwise prints an error.

   In the case that the absolute path resolves to a directory, we find all
   files in that directory tree that have a `.cljs` extension and we run
   this script's modifications on those files."
  [abs-path {:keys [verbose? testing?]}]
  (let [directory          (io/file abs-path)
        exists?            (.exists directory)
        files              (when exists?
                             (file-seq directory))
        filter-valid-files (filter #(and (.exists %)
                                         (.isFile %)
                                         (clojure.string/includes? % ".cljs")) files)]
    (cond
      (not exists?) (println "The directory/file provided does not exist.")
      (not (seq filter-valid-files)) (println "No valid files were found in " abs-path ".")

      :else
      (doseq [file filter-valid-files]
        (read-write-file file {:verbose? verbose?
                               :testing? testing?})))))

(defn -main
  "Call this function with the directory path as an argument to run this script. This function
  is called after `bb add_at_macro.clj` with the arguments passed to bb.
  Extra options that can be passed to the command apart from the directory/file are
   1. `--verbose` - When this is passed, the changes the script makes are printed to console
   2. `--testing` - When this is passed, the files the script edits are not saved to disk with the new changes
      but printed to the console.
  When `--testing` is passed, `--verbose` is always true.  Also see `run-script` above."
  [directory {:keys [verbose? testing?]}]
  (if (seq directory)
    (run-script directory {:verbose? verbose?
                           :testing? testing?})
    (println "Directory/File not provided"))
  (System/exit 0))

(def usage "Add at Macro Script

Usage:
  add_at_macro <directory> [options]

Options:
  -v --verbose  Changes this script makes are printed to the console. Always true when `--testing` option is true
  -t --testing  Changes this script makes are not written to disk, only printed to the console.
  -h --help     Show this screen.
  ")

;; This section parses command line arguments
;; See `--main` function above
(docopt/docopt
 usage
 *command-line-args*
 (fn [arg-map]
   (let [verbose?  (get arg-map "--verbose")
         testing?  (get arg-map "--testing")
         directory (get arg-map "<directory>")]
     (when directory
       (-main directory {:verbose? verbose?
                         :testing? testing?})))))
