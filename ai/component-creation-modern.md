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
  [& {:keys [pre-theme theme] :as props}]
  ;; Mount-time: Compose theme once
  (let [theme (theme/comp pre-theme theme)]
    ;; Render function: Called on every render
    (fn [& {:keys [model on-change placeholder disabled?] :as props}]
      (or
       (validate-args-macro my-component-args-desc props)
       (let [part            (partial part/part part-structure props)
             label-provided? (part/get-part part-structure props ::my-component/label)
             input-provided? (part/get-part part-structure props ::my-component/input)
             ;; Build :re-com context with dereferenced state
             re-com-ctx      {:state {:disabled? (deref-or-value disabled?)
                                     :has-label? label-provided?}}]

         (part ::my-component/wrapper
           {:impl       v-box
            :theme      theme
            :post-props (-> props
                            (select-keys [:class :style :attr])
                            (debug/instrument props))
            :props      {:re-com re-com-ctx
                         :children
                         [(when label-provided?
                            (part ::my-component/label-section
                              {:impl  h-box
                               :theme theme
                               :props {:re-com re-com-ctx
                                       :children [(part ::my-component/label {:theme theme})]}}))

                          (part ::my-component/input-section
                            {:theme theme
                             :props {:re-com re-com-ctx
                                     :children
                                     [(when input-provided?
                                        (part ::my-component/input
                                          {:theme      theme
                                           :post-props {:placeholder placeholder
                                                        :disabled    (deref-or-value disabled?)
                                                        :value       (deref-or-value model)
                                                        :on-change   (handler-fn (on-change (-> % .-target .-value)))}
                                           :props      {:re-com re-com-ctx}}))]}})]}}))))))
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
  (let [{:keys [disabled?]} (get-in props [:re-com :state])]
    (tu/class props "rc-my-component-input" "form-control"
              (when disabled? "disabled"))))

;; Main theme - component-specific defaults
(defmethod main ::mc/label-section [props]
  (let [{:keys [has-label?]} (get-in props [:re-com :state])]
    (tu/style props (when has-label? {:margin-bottom "8px"}))))
```

## Props Architecture Patterns

### `:re-com` Context Structure

Every part should receive a structured `:re-com` context:

```clojure
;; Build :re-com context in component function
re-com-ctx {:state       {:disabled? (deref-or-value disabled?)
                          :size      size
                          :showing?  @showing?}    ; Always deref atoms
            :transition! transition-fn}            ; Optional

;; Pass to parts
:props {:re-com re-com-ctx
        :other-prop "value"}
```

### Theme Method Access Patterns

Theme methods read `:re-com :state` for conditional styling:

```clojure
(defmethod bootstrap ::my-component/button [props]
  (let [{:keys [disabled? size]} (get-in props [:re-com :state])]
    (tu/class props "btn"
              (when disabled? "disabled")
              (case size :large "btn-lg" :small "btn-sm" ""))))
```

### Props Flow Rules

1. **Theme methods receive all props** - including component-specific props
2. **`:re-com` contains only** - `:part`, `:state` (dereferenced), `:transition!`
3. **Performance atoms passed separately** when needed for reactivity
4. **Final components handle validation** - themes don't filter props

```clojure
;; Component function pattern
(part ::my-component/input
  {:theme      theme
   :post-props {:placeholder placeholder        ; Direct props
                :disabled    (deref-or-value disabled?)  ; Deref'd
                :value       (deref-or-value model)}     ; Deref'd
   :props      {:re-com {:state {:disabled? disabled-val}} ; Deref'd for themes
                :model  model-atom}})                    ; Raw atom for performance

;; Rare case: Performance atom in :state with * suffix
(part ::my-component/input
  {:theme theme
   :props {:re-com {:state {:disabled? false
                            :model*    model-atom}}}}) ; Atom with * suffix
```

## Core Component Elements

**Required for every component:**

1. **Parts Description** - Defines styleable sub-elements
2. **Args Description** - Defines props with validation
3. **Validation** - Use `validate-args-macro` for dev-time checking
4. **Standard Props** - `:class`, `:style`, `:attr`, `:parts`, `:src`, `:debug-as`
5. **Theme Integration** - Use `theme/comp` at mount time, pass to all parts
6. **Event Handlers** - Wrap in `handler-fn` for error handling
7. **Debug Support** - Use `(debug/instrument props)` and `reflect-current-component`
8. **`:re-com` Context** - Structure state for theme access
9. **Props Dereferencing** - Always deref atoms before putting in `:state`

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

### Threading Macro Conventions

For cleaner, more readable code, consider putting the larger form first in threading macros:

```clojure
;; PREFERRED: More compact, easier to read
(-> (select-keys props [:class :style :attr])
    (debug/instrument props))

;; AVOID: Unnecessary line break for simple first argument
(-> props
    (select-keys [:class :style :attr])
    (debug/instrument props))
```

This pattern saves lines and improves readability when the first argument is simple, as long as it doesn't make the line too long.

### HTML Attribute Values

Prefer keywords over strings for fixed HTML attribute values, as they better represent enumerated constants:

```clojure
;; PREFERRED: Keywords for fixed values
{:type      :checkbox
 :role      :button
 :disabled  true}

;; AVOID: Strings suggest the value might be manipulated
{:type      "checkbox"
 :role      "button"
 :disabled  true}
```

Reagent converts keywords to strings during rendering, so there's no performance difference. Keywords better communicate that these are fixed enumerated values, not strings meant for manipulation.

### HTML Attributes in Parts System

**Critical:** When using `post-props` in the modern parts system, HTML attributes must be properly structured. Many re-com components accept "shortcut" top-level parameters that internally merge into `:attr`, but the parts system requires explicit structure.

```clojure
;; ✅ CORRECT: HTML attributes in :attr map
(part ::my-component/input
  {:post-props {:attr {:type      :text
                       :disabled  disabled?
                       :checked   (boolean model)
                       :on-change (handler-fn (callback-fn))}}})

;; ❌ INCORRECT: Top-level HTML attributes won't be recognized
(part ::my-component/input
  {:post-props {:type      :text        ; Won't work!
                :disabled  disabled?    ; Won't work!
                :checked   (boolean model)
                :on-change (handler-fn (callback-fn))}})
```

**Why this matters:** The parts system's `default` component function expects HTML attributes in the `:attr` key. Top-level shortcuts that work in legacy components don't automatically translate to the parts system.

**Components particularly affected:** `input`, `textarea`, `button`, and other HTML form elements where many attributes have shortcut parameters.

### The `:tag` Prop in Parts System

The `:tag` prop is specifically needed by the `re-com.part/default` component to determine what HTML element to render:

```clojure
;; ✅ CORRECT: :tag tells part/default what element to create
(part ::my-component/input
  {:props      {:tag :input}           ; part/default creates <input>
   :post-props {:attr {:type :checkbox}}})

(part ::my-component/label
  {:props      {:tag :span}            ; part/default creates <span>
   :post-props {:on-click handler}})

(part ::my-component/container
  {:props {:children [...]}})          ; No :tag needed - defaults to <div>

;; ❌ WITHOUT :tag, part/default creates generic <div>
(part ::my-component/input
  {:post-props {:attr {:type :checkbox}}}) ; Creates <div>, not <input>!

;; ❌ REDUNDANT: Don't specify :tag :div (it's the default)
(part ::my-component/container
  {:props {:tag :div}})                ; Unnecessary - div is default
```

**Important**: The `:tag` prop is **only** used by `re-com.part/default`. If your part uses a different `:impl` (like `'re-com.core/h-box` or `'re-com.core/input-text`), the `:tag` prop is ignored because those components create their own specific elements.

**When you need `:tag`:**
- Parts that don't specify `:impl` in part-structure (defaults to `re-com.part/default`)
- Parts with `:impl "empty"` in part-structure
- When you want `part/default` to create a specific HTML element like `:input`, `:span`, `:button`, etc.
- **Note**: `:div` is the default, so omit `:tag` for div elements

### Critical: Part Structure vs Part Call Implementation

**IMPORTANT**: The `:impl` in `part-structure` is for documentation only - you must specify `:impl` in the actual `part` call:

```clojure
;; Part structure (documentation)
(def part-structure
  [::my-component/wrapper {:impl 're-com.core/h-box}])  ; <- Documentation only!

;; ❌ WRONG: Missing :impl in part call
(part ::my-component/wrapper
  {:theme theme :props {...}})  ; Uses part/default, not h-box!

;; ✅ CORRECT: Specify :impl in part call
(part ::my-component/wrapper
  {:impl h-box                   ; <- Actually uses h-box!
   :theme theme :props {...}})
```

**Why this matters**: Without `:impl` in the part call, you get `part/default` (a generic div) instead of the intended component (like h-box with proper flexbox styling). This causes missing CSS classes, wrong styling, and incorrect component behavior.

**Common symptoms of missing `:impl`:**
- Missing `rc-h-box display-flex` classes on containers
- No flexbox styling (flex-flow, justify-content, etc.)
- Generic `<div>` instead of intended HTML element
- Layout/alignment issues

### Theme Method Conventions

Only define theme methods that actually modify props. Avoid empty pass-through methods:

```clojure
;; ✅ GOOD: Method adds value
(defmethod bootstrap ::my-component/label
  [props]
  (tu/class props "rc-my-component-label"))

;; ❌ AVOID: Empty method that does nothing
(defmethod base ::my-component/label
  [props]
  props)
```

The theme system will automatically pass props through when no matching method exists, so empty methods are unnecessary clutter.

### File Formatting

Always end code files with a newline character for better git diffs and POSIX compliance.
