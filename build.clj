(ns build
  (:require [clojure.tools.build.api :as b]
            [shadow-git-inject.core :as gi]))

(def lib 'day8/re-com)
(def version (gi/git-status-to-version gi/default-config))
(def class-dir "target/classes")
(def jar-file (format "target/%s-%s.jar" (name lib) version))

;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis @basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src/re_com"]
               :target-dir (str class-dir "/re_com")})
  (b/copy-dir {:src-dirs ["run/resources/public/assets"]
               :target-dir (str class-dir "/public/assets")})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))
