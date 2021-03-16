(ns scripts.core
  (:require [clojure.java.io :as io]
            [rewrite-clj.zip :as z]
            [clojure.set :as clj-set])
  (:import (java.io File FileNotFoundException)))


(defn get-alias
  "Gets re-com alias in require vector i.e `[re-com.core ... :as rc]
  returns the string rc. This is used to find re-com usages in a file."
  ([loc]
   (let [alias (if-let [as-kw (z/find-value loc z/next ':as)]
                 (-> as-kw z/next z/string))]
     alias)))

(defn find-at-loc
  "Takes a loc in the form of a vector containing re-com components i.e [p h-box at ...]
  and returns the loc if `at` macro is in the vector."
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (z/end? loc) nil
      (-> loc z/string (= "at")) loc
      :else
      (recur (z/right loc)))))

(defn find-require-macros-in-require
  "Finds `:refer-macros` in a re-com import form which is in the `:require` form, i.e
  (:require [re-com.core :refer-macros [] ...] ...). If found, we search if the `:refer-macros`
  option loads `at` macro and we remove it if true. The at-macro will be added to the `:refer`
  option."
  [loc]
  (let [refer-macros  (z/find-value loc z/next ':refer-macros)
        refer-macros  (z/right refer-macros)
        at-macro      (when refer-macros
                        (find-at-loc refer-macros))]
    (if at-macro
      (-> at-macro z/remove z/up z/up)
      loc)))

(defn append-at-to-recom-require-form
  "Checks if re-com import statement has :refer option, appends 'at' macro if True, and
  adds refer option with 'at' macro if not. Loc is [re-com.core ...]"
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
  "Checks if `at` macro is loaded in :require-macros form and removes it if true. "
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
  "Takes a loc and finds if the loc is a vector that imports re-com.core in the namespace. i.e
  [re-com.core ...]. This function by default checks in the :require form and ignores :require-macros
  unless `:macros?` option is true."
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
  "Checks if form loads recom.core. If re-com.core is loaded in :require-macros,
  it is checked to not contain 'at' macro"
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
  "Checks if :src option already exists in a vector (re-com component)"
  [loc]
  (loop [loc (z/down loc)]
    (cond
      (nil? loc) false
      (-> loc z/string (= ":src")) true
      :else
      (recur (z/right loc)))))

(defn add-at-in-component
  "Adds :src option in a vector, targeting re-com components."
  [loc verbose?]
  (if-not (check-source-added-already? loc)
    (do
      (when verbose?
        (println "Adding :src option at line " (first (z/position loc)) " column " (second (z/position loc))))
      (-> loc z/down
          (z/insert-right (z/node (z/of-string "(at)")))
          (z/insert-right (z/node (z/of-string ":src")))
          z/up))
    loc))     ;; :src option is already added

(defn find-recom-usages
  "Find usages of recom namespaces in the document body (Non header section)"
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
  "Takes a loc and determines if the file has a namespace form (ns ...)"
  [loc]
  (when (-> loc z/down z/string (= "ns"))
    loc))

(defn a-function?
  "Takes a loc and determines if the code is a function."
  [loc]
  (and (z/list? loc)
       (-> loc z/down z/string (= "defn"))))

(defn arguments
  "Takes a function and returns the arguments in the function, the arguments are important to prevent
   rename when the same name is used for a re-com namespace and a local variable."
  [loc]
  (loop [loc loc]
    (cond
      (z/end? loc) nil
      (z/vector? loc) loc
      :else
      (recur (z/right loc)))))

(defn parse-map-arguments
  "Takes a map that destructures an argument and tries to get the value in the `:keys` key. "
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
  "Takes a vector of function arguments (with symbols and maps) and builds a flat
  vector of the arguments."
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
  "Takes a file and loops through the main forms. We then classify the form as a
  namespace import form if of the form `(ns ...)` or as a function, `(defn ...)`.
  Other forms have a general method. For defn, we need to know the arguments to prevent
  renaming arguments that share names with re-com.core namespace components."
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
  "Reads and writes file in case of edits. When `verbose?` is true, operations that the script does
  are printed to the console. `testing?` supersedes `verbose?` with extra option of preventing the
  changes being printed to file. Convenient for testing as it prints the changes that would happen
  to file."
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

(try (require 'leiningen.exec)
     (when @(ns-resolve 'leiningen.exec '*running?*)
       (apply -main (rest *command-line-args*)))
     (catch FileNotFoundException e))