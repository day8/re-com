(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def lib 're-com/re-com)
(def version (System/getenv "DAY8_RELEASE_TAG"))
(def main-ns 're-com.core)
(def class-dir "target/classes")
(def basis (delay (b/create-basis {})))
(def jar-file (format "target/%s-%s.jar" lib version))

(defn- build-opts [opts]
  (merge opts
         {:lib        lib
          :version    version
          :jar-file   jar-file
          :basis      basis
          :class-dir  class-dir
          :src-dirs   ["src"]
          :pom-data   [[:licenses
                        [:license
                         [:name "MIT"]
                         [:url "https://opensource.org/licenses/MIT"]]]]
          :scm        {:connection          "scm:git:git://github.com/day8/re-com.git"
                       :developerConnection "scm:git:ssh://git@github.com/day8/re-com.git"
                       :url                 "https://github.com/day8/re-com"
                       :tag                 version}
          :ns-compile [main-ns]}))

(defn jar [opts]
  (b/delete {:path "target"})
  (let [opts (build-opts opts)]
    (println "Writing pom.xml...")
    (b/write-pom opts)
    (b/copy-dir {:src-dirs   ["src"]
                 :include    "re_com/**"
                 :target-dir class-dir})
    (b/copy-file {:src    "src/deps.cljs"
                  :target (str class-dir "/deps.cljs")})
    (b/copy-dir {:src-dirs   ["run/resources"]
                 :include    "public/assets/**"
                 :target-dir class-dir})
    (b/copy-file {:src    "README.md"
                  :target (str class-dir "/META-INF/README.md")})
    (b/copy-file {:src    "license.txt"
                  :target (str class-dir "/META-INF/license.txt")})
    (println "Building JAR...")
    (b/jar opts))
  opts)

(defn clojars [opts]
  (jar opts)
  ((requiring-resolve 'deps-deploy.deps-deploy/deploy)
   (merge {:installer :remote
           :artifact  jar-file
           :pom-file  (b/pom-path {:lib lib :class-dir class-dir})}
          opts))
  opts)

