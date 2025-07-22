# CLAUDE.md - re-com Development Guide

This file provides comprehensive guidance for working with the re-com ClojureScript UI component library.

## Quick Start: Essential Commands

### Development Commands (using Babashka)
```bash
# Install dependencies
bb install

# Start development server with hot reloading
bb watch           # Full dev server (http://localhost:3449/)
bb watch-demo      # Demo app only

# Run tests
bb test            # Full test suite with Karma
bb browser-test    # Browser test watching (http://localhost:8021/)

# Build & deploy
bb ci              # Run tests and release build
bb clean           # Clean compiled artifacts
```

### Making Changes: Core Development Loop
1. Run `bb watch` for hot reloading
2. Edit components in `src/re_com/`
3. View changes at http://localhost:3449/
4. Create/update demo page in `src/re_demo/`
5. Run `bb test` before committing

## Component Development: Standard Patterns

### Component Creation Template

Use this template for all new re-com components:

```clojure
(ns re-com.my-component
  (:require-macros
   [re-com.core :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   [re-com.config :refer [include-args-desc?]]
   [re-com.debug :refer [->attr]]
   [re-com.theme :as theme]
   [re-com.util :as u :refer [deref-or-value]]
   [re-com.validate :refer [string-or-hiccup? css-class? css-style? html-attr? parts?]]))

;; ------------------------------------------------------------------------------------
;; Parts System (for styling customization)
;; ------------------------------------------------------------------------------------

(def my-component-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-my-component-wrapper" :impl "[v-box]" :notes "Outer wrapper"}
     {:name :label   :level 1 :class "rc-my-component-label"   :impl "[label]" :notes "Text label"}
     {:name :input   :level 1 :class "rc-my-component-input"   :impl "[input]" :notes "Input field"}]))

(def my-component-parts
  (when include-args-desc?
    (-> (map :name my-component-parts-desc) set)))

;; ------------------------------------------------------------------------------------
;; Args Description (for validation and documentation)
;; ------------------------------------------------------------------------------------

(def my-component-args-desc
  (when include-args-desc?
    [{:name :model      :required true  :type "atom"           :validate-fn #(satisfies? IAtom %) :description "Current value atom"}
     {:name :on-change  :required true  :type "-> nil"         :validate-fn fn?                   :description "Called when value changes"}
     {:name :label      :required false :type "string|hiccup"  :validate-fn string-or-hiccup?     :description "Label text or hiccup"}
     {:name :placeholder :required false :type "string"        :validate-fn string?               :description "Placeholder text"}
     {:name :disabled?  :required false :type "boolean"        :validate-fn boolean?              :description "Disable input"}
     {:name :class      :required false :type "string"         :validate-fn css-class?            :description "CSS class names (applies to wrapper)"}
     {:name :style      :required false :type "CSS style map"  :validate-fn css-style?            :description "CSS styles (applies to wrapper)"}
     {:name :attr       :required false :type "HTML attr map"  :validate-fn html-attr?            :description "HTML attributes (applies to wrapper)"}
     {:name :parts      :required false :type "map"            :validate-fn (parts? my-component-parts) :description "Map of part names to styling"}
     {:name :src        :required false :type "map"            :validate-fn map?                  :description "Source code coordinates for debugging"}
     {:name :debug-as   :required false :type "map"            :validate-fn map?                  :description "Debug output masquerading"}]))

;; ------------------------------------------------------------------------------------
;; Component Function
;; ------------------------------------------------------------------------------------

(defn my-component
  "A brief description of what this component does."
  [& {:keys [model on-change label placeholder disabled? class style attr parts src debug-as]
      :as   args}]
  (or
   (validate-args-macro my-component-args-desc args)
   [:div
    (merge
     (->attr args)
     {:class (theme/merge-class "rc-my-component-wrapper" (get-in parts [:wrapper :class]) class)
      :style (merge (get-in parts [:wrapper :style]) style)}
     attr)
    (when label
      [:label
       {:class (theme/merge-class "rc-my-component-label" (get-in parts [:label :class]))
        :style (get-in parts [:label :style])}
       label])
    [:input
     {:type "text"
      :class (theme/merge-class "rc-my-component-input" (get-in parts [:input :class]))
      :style (get-in parts [:input :style])
      :value (deref-or-value model)
      :placeholder placeholder
      :disabled disabled?
      :on-change (handler-fn (on-change (-> % .-target .-value)))}]]))
```

### Core Component Elements

**Required for every component:**

1. **Parts Description** - Defines styleable sub-elements
2. **Args Description** - Defines props with validation
3. **Validation** - Use `validate-args-macro` for dev-time checking
4. **Standard Props** - `:class`, `:style`, `:attr`, `:parts`, `:src`, `:debug-as`
5. **Theme Integration** - Use `theme/merge-class` for styling
6. **Event Handlers** - Wrap in `handler-fn` for error handling
7. **Debug Support** - Use `(->attr args)` and `reflect-current-component`

## State Management Patterns

### Controlled Components (Preferred)

External state passed via props - the standard re-com pattern:

```clojure
;; Parent component manages state
(defonce app-state (r/atom {:value "hello"}))

;; Component receives state and change handler
[my-component
 :model (r/cursor app-state [:value])
 :on-change #(swap! app-state assoc :value %)]
```

### Uncontrolled Components

Internal state for component-local concerns only:

```clojure
(defn my-component []
  (let [internal-state (r/atom false)]  ; Local state
    (fn []
      [:button 
       {:on-click #(swap! internal-state not)}
       (if @internal-state "On" "Off")])))
```

### Hybrid Pattern (Advanced)

For complex components needing performance optimization:

```clojure
(defn complex-component [& {:keys [model on-change]}]
  (let [internal-model (r/atom (deref-or-value model))]
    (fn [& {:keys [model on-change] :as args}]
      ;; Sync external changes to internal state
      (when (not= @internal-model (deref-or-value model))
        (reset! internal-model (deref-or-value model)))
      
      (letfn [(update-state! [update-fn]
                (let [new-model (update-fn @internal-model)]
                  (when on-change (on-change new-model))))]
        ;; Use internal-model for rendering, update-state! for changes
        [:div "Complex component with " @internal-model]))))
```

## Event Handling Best Practices

### Always Use handler-fn

```clojure
;; GOOD: Proper error handling
:on-click (handler-fn (do-something arg))

;; BAD: Errors swallowed by React
:on-click #(do-something arg)
```

### Common Event Patterns

```clojure
;; Input changes
:on-change (handler-fn (on-change (-> % .-target .-value)))

;; Button clicks
:on-click (handler-fn (on-click))

;; With preventDefault
:on-submit (handler-fn (.preventDefault %) (submit-fn))
```

## Parts System: Granular Styling

### Defining Parts

```clojure
(def component-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-component"      :impl "[v-box]"}
     {:name :header  :level 1 :class "rc-component-header" :impl "[h-box]"}
     {:name :body    :level 1 :class "rc-component-body"   :impl "[v-box]"}]))
```

### Using Parts

```clojure
;; In component
:class (theme/merge-class "default-class" (get-in parts [:part-name :class]) class)
:style (merge default-style (get-in parts [:part-name :style]) style)

;; Consumer usage
[my-component 
 :parts {:wrapper {:class "custom-wrapper" :style {:background "blue"}}
         :header  {:class "custom-header"}}]
```

## Testing & Demo Development

### Creating Demo Pages

Every component needs a demo page in `src/re_demo/`:

```clojure
(ns re-demo.my-component
  (:require
   [re-com.core :as rc]
   [re-com.my-component :refer [my-component-args-desc my-component-parts-desc]]
   [re-demo.utils :refer [panel-title title2 args-table parts-table]]
   [reagent.core :as r]))

(defn panel []
  (let [model (r/atom "initial value")]
    (fn []
      [rc/v-box
       :children
       [[panel-title "My Component"]
        [title2 "Demo"]
        [my-component
         :model model
         :on-change #(reset! model %)]
        [title2 "Parameters"]
        [args-table my-component-args-desc]
        [title2 "Parts"]
        [parts-table my-component-parts-desc]]])))
```

### Testing Commands

```bash
# Run all tests
bb test

# Watch tests during development  
bb browser-test

# Test specific component (if test file exists)
# Tests in test/re_com/my_component_test.cljs
```

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

## CSS & Styling Integration

### Bootstrap 3.3.5 Base

Re-com uses Bootstrap 3.3.5 as the base CSS framework:

```clojure
;; Common Bootstrap classes
:class "container-fluid"
:class "row"
:class "col-md-6"
:class "btn btn-primary"
```

### Flexbox Layout System

Re-com provides comprehensive flexbox utilities via `box` components:

```clojure
[rc/h-box :children [...]]  ; Horizontal layout
[rc/v-box :children [...]]  ; Vertical layout
[rc/box :size "auto" :child component]  ; Flex container
```

### Theme System

```clojure
;; Always use theme/merge-class for styling
(theme/merge-class "default-class" parts-class user-class)

;; Supports theme switching and consistent styling
```

## Advanced Patterns

### Complex State Management

For components like `table-filter` with sophisticated state:

```clojure
(defn complex-component [& {:keys [model on-change]}]
  (let [internal-model (r/atom (or (deref-or-value model) default-state))]
    (fn [& {:keys [model on-change] :as args}]
      ;; Sync external to internal
      (let [current-ext-model (deref-or-value model)]
        (when (not= @internal-model current-ext-model)
          (reset! internal-model (or current-ext-model default-state))))
      
      ;; Bridge function for state updates
      (letfn [(update-state! [update-fn]
                (let [new-model (update-fn @internal-model)]
                  (when on-change (on-change new-model))))]
        
        ;; Render using internal state, update via bridge
        [render-component @internal-model update-state!]))))
```

### Performance Considerations

- Validation only runs in development builds
- `include-args-desc?` conditionally includes documentation
- Use `deref-or-value` for atom-or-value flexibility
- Prefer controlled components for better data flow

## Architecture Reference

### Key Dependencies

- **Reagent 1.1.0** - React wrapper for ClojureScript
- **Shadow-cljs 2.28.2** - Build tool with hot reloading  
- **cljs-time** - Date/time manipulation
- **core.async** - Asynchronous operations
- **Bootstrap 3.3.5** - Base CSS framework

### File Organization

```
src/
├── re_com/           # Core component library
│   ├── core.cljs     # Main API exports
│   ├── validate.cljs # Validation system
│   ├── theme.cljs    # Theming system
│   └── *.cljs        # Individual components
├── re_demo/          # Demo application
│   ├── core.cljs     # Demo app entry
│   └── *.cljs        # Component demo pages
test/
└── re_com/           # Component tests
    └── *_test.cljs   # Test files
```

### Build Configuration

- **shadow-cljs.edn** - Build targets and compiler options
- **bb.edn** - Babashka task definitions  
- **deps.edn** - Clojure dependencies

### Browser Support

- **Primary**: Chrome (main development target)
- **Focus**: Desktop browsers
- **Responsive**: Desktop-first design
- **CSS**: Modern flexbox with vendor prefixes

## Troubleshooting

### Common Issues

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

### Getting Help

- Demo app examples at http://localhost:3449/
- Check existing component implementations
- Refer to `docs/ai-docs.md` for Reagent-specific issues
- Use browser dev tools with source maps enabled
```

## Development Best Practices

### Functional Programming Notes

- **letfn Usage**:
  * Only use `letfn` when mutual recursion is required
  * Prefer `let` with anonymous functions for most scenarios
