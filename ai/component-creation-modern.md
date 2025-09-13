# Creating re-com Components (Modern Approach)

This guide covers the modern approach to creating re-com components with the current parts and theme system.

## Component Creation Template (Modern Approach)

Use this template for all new re-com components using the modern parts & theme system:

### Main Component File

```clojure
(ns re-com.my-component
  (:require-macros
   [re-com.core :refer [handler-fn at reflect-current-component]]
   [re-com.validate :refer [validate-args-macro]])
  (:require
   re-com.my-component.theme                   ; Import theme definitions
   [re-com.config :refer [include-args-desc?]]
   [re-com.debug :as debug]
   [re-com.part :as part]
   [re-com.theme :as theme]
   [re-com.theme.util :as tu]
   [re-com.util :refer [deref-or-value]]
   [re-com.validate :refer [string-or-hiccup? css-class? css-style? html-attr? parts?]]))

;; ------------------------------------------------------------------------------------
;; Modern Parts System (hierarchical structure)
;; ------------------------------------------------------------------------------------

(def part-structure
  [::my-component/wrapper {:impl 're-com.core/v-box}
   [::my-component/label-section {:impl 're-com.core/h-box}
    [::my-component/label {:top-level-arg? true :impl "empty"}]]
   [::my-component/input-section
    [::my-component/input {:top-level-arg? true :impl 're-com.core/input-text}]]])

(def my-component-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))

(def my-component-parts
  (when include-args-desc?
    (-> (map :name my-component-parts-desc) set)))

;; ------------------------------------------------------------------------------------
;; Args Description (includes modern theme support)
;; ------------------------------------------------------------------------------------

(def my-component-args-desc
  (when include-args-desc?
    (concat
     [{:name :model       :required true  :type "atom"           :validate-fn #(satisfies? IAtom %) :description "Current value atom"}
      {:name :on-change   :required true  :type "-> nil"         :validate-fn fn?                   :description "Called when value changes"}
      {:name :placeholder :required false :type "string"         :validate-fn string?               :description "Placeholder text"}
      {:name :disabled?   :required false :type "boolean"        :validate-fn boolean?              :description "Disable input"}
      {:name :pre-theme   :required false :type "map -> map"     :validate-fn fn?                   :description "Pre-theme function"}
      {:name :theme       :required false :type "map -> map"     :validate-fn fn?                   :description "Theme function"}
      {:name :class       :required false :type "string"         :validate-fn css-class?            :description "CSS class names (applies to wrapper)"}
      {:name :style       :required false :type "CSS style map"  :validate-fn css-style?            :description "CSS styles (applies to wrapper)"}
      {:name :attr        :required false :type "HTML attr map"  :validate-fn html-attr?            :description "HTML attributes (applies to wrapper)"}
      {:name :parts       :required false :type "map"            :validate-fn (parts? my-component-parts) :description "Map of part names to styling"}
      {:name :src         :required false :type "map"            :validate-fn map?                  :description "Source code coordinates for debugging"}
      {:name :debug-as    :required false :type "map"            :validate-fn map?                  :description "Debug output masquerading"}]
     ;; Auto-generate top-level part args
     (part/describe-args part-structure))))

;; ------------------------------------------------------------------------------------
;; Component Function (Form-2 with proper theme composition timing)
;; ------------------------------------------------------------------------------------

(defn my-component
  "A brief description of what this component does."
  [& {:keys [model on-change placeholder disabled? pre-theme theme] :as props}]
  ;; Mount-time: Compose theme once
  (let [composed-theme (theme/comp pre-theme theme)]
    ;; Render function: Called on every render
    (fn [& {:keys [model on-change placeholder disabled?] :as props}]
      (or
       (validate-args-macro my-component-args-desc props)
       (let [part         (partial part/part part-structure props)
             label-provided? (part/get-part part-structure props ::my-component/label)
             input-provided? (part/get-part part-structure props ::my-component/input)]
         
         (part ::my-component/wrapper
           {:post-props (-> props
                            (select-keys [:class :style :attr])
                            (debug/instrument props))
            :theme      composed-theme
            :props
            {:children
             [(when label-provided?
                (part ::my-component/label-section
                  {:impl       h-box
                   :theme      composed-theme
                   :props      {:children [(part ::my-component/label
                                             {:theme composed-theme
                                              :impl  (constantly nil)})]}}))
              
              (part ::my-component/input-section
                {:theme      composed-theme
                 :props      {:children
                              [(when input-provided?
                                 (part ::my-component/input
                                   {:theme      composed-theme
                                    :post-props {:placeholder placeholder
                                                 :disabled    disabled?
                                                 :value       (deref-or-value model)
                                                 :on-change   (handler-fn (on-change (-> % .-target .-value)))}
                                    :impl       (constantly nil)}))]}})]}}))))))
```

### Theme File (Required)

Create `src/re_com/my_component/theme.cljs`:

```clojure
(ns re-com.my-component.theme
  (:require
   [re-com.my-component :as-alias mc]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [variables pre-user base main bootstrap user]]))

;; Base functionality - essential styles
(defmethod base ::mc/wrapper [props]
  (merge props {:size "auto"}))

(defmethod base ::mc/input-section [props]  
  (tu/style props {:margin-top "5px"}))

;; Bootstrap styling - visual appearance
(defmethod bootstrap ::mc/wrapper [props]
  (tu/class props "rc-my-component"))

(defmethod bootstrap ::mc/label [props]
  (tu/class props "rc-my-component-label"))

(defmethod bootstrap ::mc/input [props]
  (tu/class props "rc-my-component-input" "form-control"))

;; Main theme - component-specific defaults
(defmethod main ::mc/label-section [props]
  (tu/style props {:margin-bottom "8px"}))
```

## Core Component Elements

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

## Following Conventions

When making changes to files, first understand the file's code conventions. Mimic code style, use existing libraries and utilities, and follow existing patterns.

- **NEVER assume that a given library is available**, even if it is well known. Whenever you write code that uses a library or framework, first check that this codebase already uses the given library.
- **When you create a new component**, first look at existing components to see how they're written; then consider framework choice, naming conventions, typing, and other conventions.
- **When you edit a piece of code**, first look at the code's surrounding context (especially its imports) to understand the code's choice of frameworks and libraries.
- **Always follow security best practices**. Never introduce code that exposes or logs secrets and keys. Never commit secrets or keys to the repository.

## Code Style

- **IMPORTANT: DO NOT ADD ANY COMMENTS** unless asked
- Only use emojis if the user explicitly requests it