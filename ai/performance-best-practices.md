# Performance Best Practices

This guide covers performance optimization techniques specific to re-com's parts and theme system.

## Theme Composition Performance

### Critical: Mount-Time vs Render-Time

The most important performance rule is proper theme composition timing:

```clojure
;; ✅ CORRECT - Mount-time composition (Form-2)
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (let [composed-theme (theme/comp pre-theme theme)]  ; Once at mount
    (fn [& {:keys [...] :as props}]                   ; Render function
      (part ::wrapper {:theme composed-theme ...}))))

;; ❌ INCORRECT - Render-time composition  
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (let [composed-theme (theme/comp pre-theme theme)] ; Every render!
    (part ::wrapper {:theme composed-theme ...})))

;; ❌ ALSO INCORRECT - Theme composition inside render
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (fn [& {:as props}]
    (let [composed-theme (theme/comp pre-theme theme)] ; Every render!
      (part ::wrapper {:theme composed-theme ...}))))
```

**Why this matters**: Theme composition involves walking through multiple layers of functions, which is expensive. Doing it on every render can cause significant performance degradation.

## Parts System Optimization

### Memoization Architecture

The parts system is designed for performance through memoization:

```clojure
;; These are automatically memoized - no performance cost after first call
(def describe (memoize (fn [structure] ...)))        ; Parts description
(def top-level-args (memoize (fn [structure] ...)))  ; Top-level lookup
(def depth (memoize (fn [tree k] ...)))              ; Nesting calculation
```

**Best Practice**: Define `part-structure` at the namespace level so memoization is effective across all component instances.

### Part Lookup Efficiency

```clojure
;; ✅ EFFICIENT - Pre-compute partial function
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      (let [part (partial part/part part-structure props)] ; Once per render
        ;; Use 'part' function multiple times
        ))))

;; ❌ INEFFICIENT - Repeated partial creation
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      (part/part part-structure props ::wrapper {...})     ; Partial created each time
      (part/part part-structure props ::body {...})        ; Partial created each time
      )))
```

### Conditional Part Checking

Use `part/get-part` to avoid unnecessary rendering:

```clojure
;; ✅ EFFICIENT - Check before rendering
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      (let [heading-provided? (part/get-part part-structure props ::heading)]
        ;; Only render heading section if content is provided
        (when heading-provided?
          (part ::heading-section {...}))))))

;; ❌ INEFFICIENT - Always render, let part system decide
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      ;; Part system has to check this every time
      (part ::heading-section {...})))) 
```

## Reagent-Specific Optimizations

### Form-2 vs Form-3 Components

For components with theme/parts, Form-2 is usually optimal:

```clojure
;; ✅ OPTIMAL - Form-2 for theme composition
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)    ; Mount-time computation
        static-data (compute-expensive-data)] ; Other mount-time work
    (fn [& props]                             ; Render function
      ;; Fast render using pre-computed values
      )))

;; ❌ SUBOPTIMAL - Form-3 adds unnecessary complexity
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    {:component-did-mount (fn [...] ...)      ; Usually not needed
     :reagent-render 
     (fn [& props]
       ;; Same render logic as Form-2
       )}))
```

### Atom Dereferencing Patterns

```clojure
;; ✅ EFFICIENT - Dereference inside render
(defn my-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [model] :as props}]
      (let [current-value (deref-or-value model)] ; Inside render
        (part ::input {:value current-value ...})))))

;; ❌ INEFFICIENT - Dereference outside render  
(defn my-component [& {:keys [model] :as props}]
  (let [theme (theme/comp pre-theme theme)
        current-value (deref-or-value model)]     ; Won't react to changes
    (fn [& props]
      (part ::input {:value current-value ...}))))
```

## Memory and GC Optimization

### Avoid Creating Functions in Render

```clojure
;; ✅ EFFICIENT - Pre-define event handlers
(defn my-component [& {:keys [on-change] :as props}]
  (let [theme (theme/comp pre-theme theme)
        ;; Create handler once at mount
        handle-change (fn [e] (on-change (.. e -target -value)))]
    (fn [& props]
      (part ::input {:on-change handle-change ...}))))

;; ❌ INEFFICIENT - Create function every render
(defn my-component [& {:keys [on-change] :as props}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      (part ::input {:on-change #(on-change (.. % -target -value)) ; New fn each render
                     ...}))))
```

### Theme Function Optimization

```clojure
;; ✅ EFFICIENT - Use case/multimethod for theme dispatch
(defmulti my-theme :part)
(defmethod my-theme ::wrapper [props] ...)
(defmethod my-theme ::body [props] ...)
(defmethod my-theme :default [props] props)

;; ❌ LESS EFFICIENT - Nested conditionals  
(defn my-theme [props]
  (if (= (:part props) ::wrapper)
    (...)
    (if (= (:part props) ::body)  
      (...)
      props)))
```

## Large Component Optimization

### Lazy Part Rendering

For components with many parts, consider lazy evaluation:

```clojure
(defn complex-component [& props]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& props]
      ;; Only compute expensive parts when needed
      (let [sections (for [section-data (:sections props)
                          :when (:visible? section-data)]
                      ^{:key (:id section-data)}
                      (part ::section {:section section-data ...}))]
        (part ::wrapper {:children sections ...})))))
```

### Part Structure Optimization

```clojure
;; ✅ EFFICIENT - Minimal nesting when possible
(def part-structure
  [::wrapper
   [::header]
   [::body] 
   [::footer]])

;; ❌ OVER-ENGINEERED - Unnecessary nesting
(def part-structure  
  [::wrapper
   [::container
    [::inner-container
     [::header-section
      [::header-wrapper
       [::header]]]
     [::body-section
      [::body-wrapper
       [::body]]]]]])
```

## Development vs Production

### Debug Instrumentation

Development builds include extra instrumentation that affects performance:

```clojure
;; This is automatic - debug info only in development
(debug/instrument props component-props)  ; No-op in production
(validate-args-macro args-desc props)     ; No-op in production  
```

**Note**: Production builds automatically strip validation and debug overhead.

### Parts Description Performance

```clojure
;; ✅ EFFICIENT - Only generate in development
(def my-component-parts-desc
  (when include-args-desc?              ; False in production
    (part/describe part-structure)))    ; Only computed in dev

;; ❌ INEFFICIENT - Always computed
(def my-component-parts-desc
  (part/describe part-structure))       ; Computed even in production
```

## Performance Monitoring

### Identifying Bottlenecks

```clojure
;; Use React DevTools Profiler to identify slow components
;; Look for components with:
;; 1. High render times
;; 2. Frequent re-renders  
;; 3. Large component trees

;; Add temporary logging for theme composition:
(defn my-component [& props]
  (let [start-time (js/Date.now)
        theme (theme/comp pre-theme theme)
        composition-time (- (js/Date.now) start-time)]
    (when (> composition-time 1)  ; Log slow compositions
      (js/console.warn "Slow theme composition:" composition-time "ms"))
    (fn [& props] ...)))
```

## Summary Checklist

- [ ] ✅ Use Form-2 components with mount-time `theme/comp` 
- [ ] ✅ Create `part` partial function once per render
- [ ] ✅ Use `part/get-part` to conditionally render parts
- [ ] ✅ Define event handlers at mount time, not render time  
- [ ] ✅ Use `when include-args-desc?` for parts descriptions
- [ ] ✅ Prefer multimethods over nested conditionals in themes
- [ ] ✅ Dereference atoms inside render functions
- [ ] ✅ Use `^{:key ...}` for dynamic lists
- [ ] ✅ Minimize part structure nesting depth
- [ ] ✅ Profile components using React DevTools
- [ ] ❌ Never compose themes inside render functions
- [ ] ❌ Don't create functions inside render unless necessary
- [ ] ❌ Avoid complex computations in theme functions