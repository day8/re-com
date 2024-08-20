(ns re-com.theme.template
  (:require
   [re-com.theme :as-alias theme]
   [re-com.theme.util :refer [merge-props]]))

(defn template? [{::theme/keys [template]}] template)

(defmulti template (fn [{::theme/keys [template]} props ctx] template))

(defmethod template ::theme/with-state
  [{[new-state] :args} props ctx]
  [props (update ctx :state merge new-state)])

(defmethod template ::theme/parts
  [{[part->props] :args} props {:keys [part]}]
  (if-let [v (or (get part->props part)
                 (get part->props (keyword (name part))))]
    (merge-props props v)
    props))

(defn remove-keys [m ks]
  (select-keys m (remove (set ks) (keys m))))

(defmethod template ::theme/<-props
  [{[outer-props
     & {:keys [part exclude include]
        :or   {include [:style :attr :class
                        :width :min-width :max-width
                        :height :min-height :max-height]
               exclude []}}] :args}

   props
   ctx]
  (let [outer-style-keys [:width  :min-width :max-width
                          :height :max-height :min-width :min-height]
        outer-attr-keys  [:tab-index]
        outer-props      (cond-> outer-props
                           (seq include) (select-keys include)
                           (seq exclude) (remove-keys exclude))]
    (cond-> props
      (= part (:part ctx))
      (-> (merge-props (remove-keys outer-props (concat outer-style-keys outer-attr-keys)))
          (update :style clojure.core/merge
                  (select-keys outer-props outer-style-keys))
          (update :attr clojure.core/merge
                  (select-keys outer-props outer-attr-keys))))))
