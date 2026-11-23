(ns re-com.input-text-test
  "Regression for #385: since 2.29.0 the base theme took the status icon's
   opacity from the tooltip's showing? atom, which input-text creates afresh
   on every render, so the icon was always invisible. Dereferencing that atom
   during input-text's render also meant hovering re-rendered input-text,
   replacing the atom, so a :status-tooltip could not stay open. Up to 2.28 the
   opacity came from :status and :status-icon?, and the icon sat beside the
   field in the wrapper h-box, not inside ::inner under it.
   Same hiccup-walk technique as re-com.checkbox-test; no DOM required."
  (:require
   [cljs.test :refer-macros [is deftest testing]]
   [reagent.ratom :as ratom]
   [re-com.input-text :as it :refer [input-text]]
   [re-com.popover :refer [popover-tooltip]]))

(defn find-part
  "Walk a part-rendered hiccup tree (parts are [impl-fn props] vectors whose
   props carry :part) for the props map of part k. Descends map values so
   parts nested under :children, :child or :anchor are reached."
  [x k]
  (cond
    (and (vector? x)
         (map? (second x))
         (= k (:part (second x))))
    (second x)

    (or (vector? x) (seq? x))
    (some #(find-part % k) x)

    (map? x)
    (some #(find-part % k) (vals x))

    :else nil))

(defn render-tree [& {:as args}]
  (let [args   (merge {:model "x" :on-change identity} args)
        render (apply input-text (mapcat identity args))]
    (apply render (mapcat identity args))))

(deftest status-icon-is-visible
  (testing "with :status and :status-icon? the icon shows"
    (let [props (find-part (render-tree :status :error :status-icon? true) ::it/status-icon)]
      (is (some? props))
      (is (= "1" (get-in props [:style :opacity])))))
  (testing "with a :status-tooltip too, the icon shows before any hover"
    (let [props (find-part (render-tree :status :error :status-icon? true
                                        :status-tooltip "Bad link")
                           ::it/status-icon)]
      (is (some? props))
      (is (= "1" (get-in props [:style :opacity]))))))

(deftest status-icon-sits-beside-the-field
  ;; Up to 2.28 the icon was the wrapper h-box's second child, to the right of
  ;; the field. 2.29 put it inside ::inner, under the field, where it wrapped
  ;; onto the next line.
  (doseq [extra [[] [:status-tooltip "Bad link"]]]
    (testing (str "args " extra)
      (let [tree    (apply render-tree :status :error :status-icon? true extra)
            wrapper (find-part tree ::it/wrapper)
            inner   (find-part tree ::it/inner)]
        (is (some? (find-part tree ::it/status-icon)) "the icon is rendered")
        (is (nil? (find-part (:children inner) ::it/status-icon))
            "the icon is not inside ::inner")
        (is (some? (find-part (rest (:children wrapper)) ::it/status-icon))
            "the icon follows ::inner in the wrapper's children")))))

(defn find-vector-headed-by
  "The first hiccup vector in x whose head is f."
  [x f]
  (cond
    (and (vector? x) (identical? f (first x))) x
    (or (vector? x) (seq? x))                  (some #(find-vector-headed-by % f) x)
    (map? x)                                   (some #(find-vector-headed-by % f) (vals x))
    :else                                      nil))

(deftest render-does-not-track-the-tooltip-state
  ;; If input-text's render reads the tooltip's showing? atom, hovering the
  ;; icon re-renders input-text, which makes a new atom and closes the tooltip.
  (let [tree    (atom nil)
        r       (ratom/make-reaction
                 #(reset! tree (render-tree :status :error :status-icon? true
                                            :status-tooltip "Bad link"))
                 :auto-run true)
        _       @r
        tooltip (find-vector-headed-by @tree popover-tooltip)
        showing (:showing? (apply hash-map (rest tooltip)))]
    (is (some? showing) "the status tooltip gets a showing? atom")
    (is (seq (.-watching ^js r)) "the reaction captured the render's reads, such as its model")
    (is (not-any? #(identical? showing %) (.-watching ^js r))
        "rendering input-text doesn't read the tooltip's showing? atom")
    (ratom/dispose! r)))
