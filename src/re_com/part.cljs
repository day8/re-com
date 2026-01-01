(ns re-com.part
  (:require
   [clojure.core :as clj]
   [clojure.set :as set]
   [clojure.string :as str]
   [re-com.theme.util :as tu]
   [re-com.validate :as validate]))

(def id first)

(defn children [[_ & [x & rest-children :as all-children]]]
  (if (map? x)
    rest-children
    all-children))

(def branch? sequential?)

(def unqualify (memoize (comp keyword clj/name)))

(def depth
  (memoize
   (fn depth [tree k]
     (let [walk (fn walk [node depth]
                  (cond
                    (= (unqualify k)
                       (unqualify (id node)))
                    depth
                    (branch? node)
                    (some #(walk % (inc depth)) (children node))))]
       (walk tree 1)))))

(defn props [[_ b]]
  (when (map? b) b))

(def top-level-args
  (fn [structure]
    (->> structure
         (tree-seq branch? children)
         (filter (comp :top-level-arg? props))
         (map id)
         (mapcat (fn [k]
                   [#_k ;;TODO support qualified keys?
                    (unqualify k)]))
         set)))

(defn css-class [part-id]
  (str "rc-"
       (some-> part-id namespace (subs 7))
       "-"
       (some-> part-id clj/name)))

(def css-class* (memoize css-class))

(def describe
  (memoize
   (fn describe [structure]
     (->> structure
          (tree-seq branch? children)
          (mapv (fn [node]
                  (let [part-id            (id node)
                        part-name          (unqualify part-id)
                        {:keys [tag impl]
                         :as   part-props} (props node)]
                    (merge
                     part-props
                     {:name  part-name
                      :class (css-class part-id)
                      :level (dec (depth structure part-name))
                      :impl  (str "["
                                  (or
                                   (when (string? impl) impl)
                                   (when impl
                                     (-> (ns-name impl)
                                         (str/replace "$" ".")
                                         (str/replace "_" "-")))
                                   tag
                                   ":div")
                                  "]")}))))))))

(def top-level-arg?
  (memoize
   (fn [structure k]
     (contains? (top-level-args structure) k))))

(defn unqualify-set [s] (set (map unqualify s)))

(defn args-valid? [part-structure args problems]
  (let [part-seq  (tree-seq branch? children part-structure)
        req-ks    (unqualify-set
                   (map id (filter (comp :part-required? props)
                                   part-seq)))
        ks        (unqualify-set (map id part-seq))
        top-ks    (unqualify-set (top-level-args part-structure))
        top-args  (set (filter top-ks (keys args)))
        part-args (set (filter ks (keys (:parts args))))
        top-level-collisions
        (->> (unqualify-set top-args)
             (set/intersection (unqualify-set part-args))
             (map #(do {:problem  :part-top-level-collision
                        :arg-name %})))
        top-level-unsupported-keys
        (->> (set (keys args))
             (set/intersection ks)
             (remove top-ks)
             (map #(do {:problem  :part-top-level-unsupported
                        :arg-name %})))
        missing-req-keys
        (->> (set/union top-args part-args)
             (set/difference req-ks)
             (map #(do {:problem   :part-req-missing
                        :arg-name %})))]
    (vec (concat problems
                 top-level-collisions
                 top-level-unsupported-keys
                 missing-req-keys))))

(def part? (some-fn map? string? vector? number? ifn? nil?))

(defn describe-args [structure]
  (into [{:name              :parts
          :required          false
          :type              "map"
          :validate-fn       (validate/parts?
                              (set (map :name (describe structure))))
          :parts-validate-fn (partial args-valid? structure)
          :description       [:span "See " [:a {:href "#/parts"} "Parts"]]}]
        (comp
         (filter (comp (top-level-args structure) :name))
         (map #(merge
                %
                {:validate-fn part?
                 :type        "re-com part"
                 :description
                 [:span "Overrides the " [:code (str (:name %))]
                  " key within " [:code ":parts"] "."
                  " See the parts section below for details."]})))
        (describe structure)))

(defn get-part [part-structure props k]
  (let [part-name (unqualify k)]
    (or (when (top-level-arg? part-structure part-name)
          (get props part-name))
        (get-in props [:parts part-name]))))

(defn default [{:keys [class style attr children tag]
                :or   {tag :div}}]
  (into [tag (merge {:class class :style style} attr)]
        children))

(def hiccup? vector?)

(def descend identity)

(defn part
  ([structure props k opts]
   (part (get-part structure props k)
         (assoc opts :part k)))
  ([part-value {:keys   [impl key theme post-props props]
                part-id :part
                :or     {impl default}}]
   (->
    (cond
      (hiccup? part-value) part-value
      (string? part-value) part-value
      (number? part-value) (str part-value)
      :else                (let [f        (cond (map? part-value) impl
                                                (ifn? part-value) part-value
                                                :else             impl)
                                 part-map (when (map? part-value) part-value)
                                 props
                                 (cond-> {:part part-id}
                                   :do        (merge props)
                                   theme      (theme f)
                                   part-map   (tu/merge-props part-map)
                                   post-props (tu/merge-props post-props))]
                             [f props]))
    (cond-> key (with-meta {:key key})))))
