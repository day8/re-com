# Theme System

A theme is a pattern for **how** to draw UI, independent of **what** and **where**. Re-com's theme system provides consistent styling and supports theme switching across components.

## What is a Theme?

A theme is a function that takes a props map and returns a new props map. In this way, it wraps the props which re-com passes to each **part** (i.e., the second item of each hiccup).

## Basic Theme Usage

### Simple Theme Function

```clojure
(defn orange-theme [props]
  (update props :style merge {:background "orange"}))

;; Apply to component
[rc/dropdown {:parts {:body my-body} :theme orange-theme}]
```

### Part-Specific Theme

Themes can target specific parts using the `:part` prop:

```clojure
(defn precise-theme [props]
  (case (:part props)
    :re-com.dropdown/body
    (update props :style merge {:background "orange"})
    :re-com.dropdown/anchor
    (update props :class conj "italic")
    props))

[rc/dropdown {:parts {:body my-body} :theme precise-theme}]
```

### Multimethod Themes

You can use multimethods for cleaner part-specific theming:

```clojure
(defmulti my-theme :part)

(defmethod my-theme :re-com.dropdown/body [props]
  (update props :style merge {:background "orange"}))

(defmethod my-theme :re-com.dropdown/anchor [props]
  (update props :class conj "italic"))

(defmethod my-theme :default [props] props)
```

## Global Themes

You can register a theme globally with `reg-theme`:

```clojure
(re-com.core/reg-theme precise-theme)

;; Now all components use this theme by default
[rc/dropdown {:parts {:body my-body}}]  ; Uses precise-theme automatically
```

By default, this replaces the function at the `:user` layer. You can pass two arguments—a layer-id and a theme-function—to replace a different layer.

## Theme Layers (Detailed Composition)

To fully determine the props for a part, re-com composes multiple theme functions **in order**. Each layer can modify the props map before passing it to the next layer:

### Layer Order & Purpose

1. **`:variables`** 
   - **Purpose**: Adds static design tokens
   - **Location**: `[:re-com :variables]` path in props
   - **Content**: Color palettes, spacing units, breakpoints, etc.
   - **Registrable**: Yes, via `(reg-theme :variables theme-fn)`
   - **Example**:
   ```clojure
   (defmethod variables ::my-component/wrapper [props]
     (assoc-in props [:re-com :variables] 
               {:primary-color "#007bff"
                :spacing-unit  "8px"
                :border-radius "4px"}))
   ```

2. **`:pre-user`**
   - **Purpose**: Global theme modifications that should apply before user customizations
   - **Registrable**: Yes, via `(reg-theme :pre-user theme-fn)`
   - **Use case**: Implementing design systems, dark mode, or accessibility themes
   - **Example**:
   ```clojure
   (defmethod pre-user ::my-component/wrapper [props]
     (let [vars (get-in props [:re-com :variables])]
       (tu/style props {:background (if dark-mode? (:dark-bg vars) (:light-bg vars))})))
   ```

3. **`:pre-theme`**
   - **Purpose**: Component-specific theme that applies before other themes
   - **Registrable**: No - only via `:pre-theme` argument to components
   - **Use case**: Conditional theming, A/B testing, contextual styling
   - **Example**:
   ```clojure
   [my-component :pre-theme (fn [props]
                              (if urgent?
                                (tu/class props "urgent-styling")
                                props))]
   ```

4. **`:base`**
   - **Purpose**: Essential functionality and behavior
   - **Content**: Box model, event handling, core positioning
   - **Registrable**: Yes, but **dangerous** - can break components
   - **Warning**: Only modify if you know what you're doing
   - **Example**:
   ```clojure
   (defmethod base ::my-component/wrapper [props]
     (merge props {:size "auto"            ; Flexbox sizing
                   :justify :start         ; Layout behavior
                   :on-click-capture ...})) ; Event handling
   ```

5. **`:main`** 
   - **Purpose**: Default visual styling for components
   - **Content**: Colors, fonts, spacing, borders - the "look" of re-com
   - **Registrable**: Yes, to override re-com's default appearance
   - **Example**:
   ```clojure
   (defmethod main ::my-component/wrapper [props]
     (tu/style props {:padding "12px"
                      :border "1px solid #ddd"
                      :background-color "#f8f9fa"}))
   ```

6. **`:user`**
   - **Purpose**: Global user customizations
   - **Default**: Empty by default
   - **Registrable**: Yes, this is the **primary layer** for `reg-theme`
   - **Use case**: App-wide theming, brand customization
   - **Example**:
   ```clojure
   ;; This goes to :user layer by default:
   (reg-theme (fn [props]
                (case (:part props)
                  :re-com.button/wrapper (tu/class props "my-brand-button")
                  props)))
   ```

7. **`:re-com-meta`**
   - **Purpose**: Debug and development metadata
   - **Content**: `rc-component-name` class, `data-rc` attributes
   - **Registrable**: No - managed internally by re-com
   - **Automatic**: Applied to all components for debugging

8. **`:theme`**
   - **Purpose**: Component-instance specific theming
   - **Registrable**: No - only via `:theme` argument to components  
   - **Use case**: One-off styling, component-specific overrides
   - **Example**:
   ```clojure
   [my-component :theme (fn [props]
                          (tu/style props {:border "2px solid red"}))]
   ```

### Theme Composition Process

```clojure
;; Conceptual flow - actual implementation is more complex
(defn compose-theme [layers]
  (reduce (fn [props layer-fn]
            (layer-fn props))          ; Each layer transforms props
          initial-props
          [variables-fn pre-user-fn pre-theme-fn base-fn 
           main-fn user-fn re-com-meta-fn theme-fn]))
```

### Advanced: Layer Registration

```clojure
;; Register to specific layer
(reg-theme :main my-main-theme)      ; Override default styling
(reg-theme :pre-user my-global-theme) ; Global app theme
(reg-theme :variables my-tokens)     ; Design system tokens

;; Default registration goes to :user
(reg-theme my-user-theme)            ; Same as (reg-theme :user my-user-theme)
```

### Using Variables Layer

```clojure
(defn dark-mode [props]
  (update-in props [:re-com :variables] merge
             {:background "black"
              :foreground "white"}))

[rc/dropdown {:parts     {:body my-body}
              :pre-theme dark-mode}]
```

## Theme Reactivity

**Important**: A re-com component composes the theme function **once** when it mounts. It will not react to changes in the passed-in `:theme` or `:pre-theme` arguments. This makes themes more performant.

### Instead of this:
```clojure
[rc/dropdown {:theme (if (deref night-mode?) dark-theme light-theme)}]
```

### Do this:
```clojure
[rc/dropdown {:theme (fn [props] 
                       (if (deref night-mode?)
                         (dark-theme props)
                         (light-theme props)))}]
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

## Theme Method Props Contract

### What Props Do Theme Methods Receive?

Theme methods receive **all props** that will be passed to the final component, including:

1. **Styling props** - `:class`, `:style`, `:attr` (always safe to modify)
2. **Component-specific props** - `:width`, `:disabled?`, `:gap`, etc. (depends on target component)
3. **`:re-com` metadata** - Structured context for theme decisions

```clojure
;; Example props received by theme method
{:part        ::my-component/wrapper
 :re-com      {:state {:disabled? true :size :large}
               :transition! #(swap! showing? not)}
 :class       "user-class"
 :style       {:color "red"}
 :attr        {:on-click handler}
 :disabled?   true
 :width       "200px"
 :gap         "8px"}
```

### Theme Method Responsibilities

1. **Read `:re-com :state`** for conditional styling decisions
2. **Modify any props** as needed (styling, behavior, etc.)
3. **Trust final component** to handle or ignore unknown props
4. **Return transformed props map**

### Safe Props Patterns

```clojure
(defmethod bootstrap ::my-component/wrapper [props]
  (let [{:keys [disabled? size]} (get-in props [:re-com :state])]
    (-> props
        ;; Always safe - universal styling props
        (tu/class "my-component"
                  (when disabled? "disabled")
                  (case size :large "large" :small "small" ""))
        ;; Safe when you know the :impl (e.g., h-box accepts :gap)
        (assoc :gap "8px" :align :center)
        ;; Component will ignore unknown props
        (assoc :custom-prop "value"))))
```

### Implementation-Specific Props

Since part declarations specify `:impl`, theme methods can safely add implementation-specific props:

```clojure
;; Part structure declares h-box implementation
[::wrapper {:impl 're-com.core/h-box}]

;; Theme can confidently use h-box props
(defmethod base ::wrapper [props]
  (merge props {:size "auto"      ; h-box understands :size
                :gap "8px"        ; h-box understands :gap
                :justify :center  ; h-box understands :justify
                :align :start}))  ; h-box understands :align
```

### Props Architecture Benefits

1. **Simple mental model** - Themes get everything, transform as needed
2. **No validation conflicts** - Components handle their own prop validation
3. **Implementation flexibility** - Themes can add any props for known implementations
4. **User wrapping supported** - Users can wrap strict components with prop filtering

## Theme Integration with Parts

Themes synergize well with both map and function parts. Hiccup and string parts aren't affected by themes since re-com doesn't control their props.

```clojure
;; Theme applies to all parts, even defaults
[rc/dropdown {:parts {:body   my-body
                      :anchor {:style {:color "white"}}}  ; Composes with theme
              :theme orange-theme}]
```

The theme function applies to every part—including parts you don't specify. Even default parts get themed.

## Component Support

**Note**: Not all components support themes yet. The effort to bring full support to all components is [ongoing](https://github.com/day8/re-com/issues/352).