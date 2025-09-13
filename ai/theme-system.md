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

## Theme Layers

To fully determine the props for a part, re-com composes multiple theme functions in order:

1. **`:variables`** - Adds static data under `[:re-com :variables]`. Includes color palettes, spacing units, and other standard values.
2. **`:pre-user`** - Empty by default. For implementing spacing or color schemes by changing values within `[:re-com :variables]`.
3. **`:pre-theme`** - Cannot be registered. Contains any function you pass as the `:pre-theme` argument to a component.
4. **`:base`** - Contains re-com's essential functionality (box-model positioning, event handling). Replace at your own risk.
5. **`:main`** - Contains re-com's default visual styling for all components.
6. **`:user`** - Empty by default. Calling `reg-theme` replaces this layer unless you specify a different layer.
7. **`:re-com-meta`** - Cannot be registered. Adds `rc-component-name` class and `data-rc` HTML attribute.
8. **`:theme`** - Cannot be registered. Contains any function you pass as the `:theme` argument to a component.

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