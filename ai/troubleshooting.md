# Troubleshooting

Common issues and solutions when working with re-com.

## Common Issues

**Component not re-rendering:**
- Check atom dereferencing is inside render function
- Verify `handler-fn` usage for event handlers

**Styling not working:**
- Use `theme/merge-class` instead of direct CSS
- Check parts system implementation
- Verify Bootstrap 3.3.5 compatibility

**Validation errors:**
- Check args-desc definitions match actual props
- Ensure required parameters are provided
- Verify validation functions are correct

**Build issues:**
- Run `bb clean` then `bb install` 
- Check shadow-cljs.edn configuration
- Verify all dependencies in deps.edn

## Common Reagent Gotchas

### 1. Atom Dereferencing

```clojure
;; BAD: Component won't re-render when atom changes
(defn my-comp [my-atom]
  (let [val @my-atom]  ; Dereferenced outside render
    [:div val]))

;; GOOD: Component subscribes to atom changes
(defn my-comp [my-atom]
  [:div @my-atom])  ; Dereferenced inside render
```

### 2. Keys for Dynamic Lists

```clojure
;; BAD: React warnings and incorrect updates
(for [item items]
  [child-component item])

;; GOOD: Unique keys for React reconciliation
(for [item items]
  ^{:key (:id item)}  ; Critical for React performance
  [child-component item])
```

### 3. Event Handler Scope

```clojure
;; BAD: Event handlers without proper error handling
:on-click #(js/alert "clicked")

;; GOOD: Use handler-fn for error catching
:on-click (handler-fn (js/alert "clicked"))
```

## Debugging & Development Tools

### Debug Props

```clojure
[my-component
 :src (at)                           ; Auto file/line info
 :debug-as (reflect-current-component) ; Component name in debug
 ]
```

### Validation

Development builds include comprehensive validation:
- Argument type checking
- Required parameter validation  
- Parts validation
- Helpful error messages with source links

```clojure
;; Validation automatically strips in production
(validate-args-macro component-args-desc args)
```

## Getting Help

- Demo app examples at http://localhost:3449/
- Check existing component implementations
- Use browser dev tools with source maps enabled
- Follow the patterns established in existing components