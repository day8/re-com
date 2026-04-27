(ns re-com.debug-test
  (:require [cljs.test     :refer-macros [is deftest testing use-fixtures]]
            [goog.object   :as gobj]
            [re-com.debug  :as debug]))

(defn- with-fake-rf-trace
  "Install a fake `re_frame.trace` ns on goog.global that mirrors what
   the real re-frame ships when compiled to JS: only `defn`/`def`
   exports are present. Notably, `finish_trace` is absent — it's a
   macro in re-frame.trace and never compiles into the JS namespace.
   Returns the fake `traces` atom so the test can assert against it."
  [{:keys [enabled?]}]
  (let [traces           (atom [])
        cb-calls         (atom [])
        next-id          (atom 0)
        start-trace      (fn [{:keys [operation op-type tags child-of]}]
                           {:id        (swap! next-id inc)
                            :operation operation
                            :op-type   op-type
                            :tags      tags
                            :child-of  child-of
                            :start     (.now js/performance)})
        run-tracing-cbs! (fn [now] (swap! cb-calls conj now))
        rf               #js {}
        rf-trace         #js {}]
    (gobj/set rf-trace "start_trace" start-trace)
    (gobj/set rf-trace "is_trace_enabled_QMARK_" (fn [] (boolean enabled?)))
    (gobj/set rf-trace "traces" traces)
    (gobj/set rf-trace "run_tracing_callbacks_BANG_" run-tracing-cbs!)
    (gobj/set rf "trace" rf-trace)
    (gobj/set js/goog.global "re_frame" rf)
    {:traces traces :cb-calls cb-calls}))

(defn- clear-fake-rf-trace! []
  (gobj/remove js/goog.global "re_frame")
  (reset! debug/rf-trace :unset))

(use-fixtures :each
  {:before clear-fake-rf-trace!
   :after  clear-fake-rf-trace!})

(deftest emit-render-src-trace-fires-without-finish-trace-export
  (testing "Regression: finish-trace is a CLJ-only macro and is NOT a JS
            export of re-frame.trace. Resolving it via gobj/get returns
            undefined, which previously caused emit-render-src-trace! to
            silently no-op for every consumer."
    (let [{:keys [traces cb-calls]} (with-fake-rf-trace {:enabled? true})
          src                       {:file "src/foo.cljs" :line 42}]
      (debug/emit-render-src-trace! "foo-component" src)
      (is (= 1 (count @traces))
          "exactly one :re-com/render trace lands in re-frame's traces atom")
      (let [t (first @traces)]
        (is (= :re-com/render (:op-type t)))
        (is (= "foo-component" (:operation t)))
        (is (= {:component-name "foo-component" :src src} (:tags t)))
        (is (number? (:start t)))
        (is (number? (:end t)))
        (is (number? (:duration t)))
        (is (>= (:duration t) 0)
            "duration uses the same clock at both endpoints"))
      (is (= 1 (count @cb-calls))
          "run-tracing-callbacks! is invoked exactly once per emit"))))

(deftest emit-render-src-trace-no-op-when-tracing-disabled
  (testing "Matches re-frame.trace's gating semantics: when
            (is-trace-enabled?) returns false, no trace is emitted."
    (let [{:keys [traces cb-calls]} (with-fake-rf-trace {:enabled? false})]
      (debug/emit-render-src-trace! "foo-component" {:file "x" :line 1})
      (is (zero? (count @traces)))
      (is (zero? (count @cb-calls))))))

(deftest emit-render-src-trace-no-op-when-rf-trace-not-loaded
  (testing "When re-frame.trace isn't on goog.global, resolution
            returns nil and emit-render-src-trace! is silently no-op
            (re-com stays decoupled from re-frame as a hard dep)."
    ;; clear-fake-rf-trace! has already removed re_frame from goog.global.
    (debug/emit-render-src-trace! "foo-component" {:file "x" :line 1})
    (is (nil? @debug/rf-trace)
        "cache resolves to nil when re-frame.trace is absent")))

(deftest resolve-rf-trace-requires-all-exports
  (testing "If any of the four exports is missing, resolution returns
            nil so the caller can skip cleanly. Notably this protects
            against a future re-frame.trace dropping or renaming one
            of (start-trace, is-trace-enabled?, traces,
            run-tracing-callbacks!)."
    (doseq [missing ["start_trace" "is_trace_enabled_QMARK_"
                     "traces" "run_tracing_callbacks_BANG_"]]
      (with-fake-rf-trace {:enabled? true})
      (gobj/remove (.. js/goog.global -re_frame -trace) missing)
      (reset! debug/rf-trace :unset)
      (is (nil? (debug/resolve-rf-trace!))
          (str "resolution fails when " missing " is absent"))
      (clear-fake-rf-trace!))))
