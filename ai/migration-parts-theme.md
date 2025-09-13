# Migration Guide: Modern Parts & Theme System

This guide covers how to migrate components from the old parts system to the modern parts & theme architecture, based on the recent alert component refactor (commit 41d0546e).

## Overview

Recent updates to re-com have introduced a more sophisticated parts and theme system that provides better composition, theming support, and user customization. This migration transforms components from manual parts handling to a structured, theme-integrated approach.

## Migration Example: Alert Components

The alert component migration (commit 41d0546e) demonstrates the complete transformation from old to new patterns.

### 1. Part Structure Definition

**Old Approach:**
```clojure
;; Manual parts description
(def alert-box-parts-desc
  (when include-args-desc?
    [{:name :wrapper  :level 0 :class "rc-alert-wrapper"  :impl "[alert-box]"}
     {:name :heading  :level 1 :class "rc-alert-heading"  :impl "[h-box]"}
     {:name :h4       :level 2 :class "rc-alert-h4"       :impl "[:h4]"}
     {:name :body     :level 1 :class "rc-alert-body"     :impl "[h-box]"}]))
```

**New Approach:**
```clojure
;; Structured part-structure with hierarchy  
(def part-structure
  [::ab/wrapper {:impl 're-com.core/alert-box :type :legacy}
   [::ab/top-section {:impl 're-com.core/h-box}
    [::ab/heading-wrapper {:tag :h4}
     [::ab/heading {:top-level-arg? true :impl "empty"}]]
    [::ab/close-button {:impl 're-com.close-button/close-button}]]
   [::ab/body-section {:impl 're-com.core/h-box}
    [::ab/body-wrapper
     [::ab/body {:top-level-arg? true :impl "empty"}]]]])

;; Auto-generated from structure
(def alert-box-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))
```

### 2. Component Function Signature

**Old Approach:**
```clojure
(defn alert-box
  [& {:keys [id alert-type heading body padding closeable? on-close class style attr parts src]
      :as   props}])
```

**New Approach:**
```clojure  
(defn alert-box
  [& {:keys [id alert-type body padding closeable? on-close pre-theme theme]
      :as   props}])
```

**Key Changes:**
- Added `:pre-theme` and `:theme` parameters
- Removed explicit `:heading` parameter (now handled via parts)
- Removed `:class`, `:style`, `:attr` (handled via parts system)

### 3. Implementation Pattern

**Old Approach:**
```clojure
;; Manual hiccup with get-in parts lookups
(let [close-alert [close-button
                   :class (str "rc-alert-close-button " (get-in parts [:close-button :class]))
                   :style (get-in parts [:close-button :style])]
      alert-class (alert-type {:info "alert-success" ...})]
  [:div
   (merge
    {:class (theme/merge-class "rc-alert" "alert" alert-class class)
     :style (merge (flex-child-style "none") {:padding padding} style)}
    attr)
   ;; Manual conditional rendering and parts lookup
   (when heading
     [h-box
      :class (str "rc-alert-heading " (get-in parts [:heading :class]))
      :children [[:h4 heading] close-alert]])])
```

**New Approach:**
```clojure
;; Structured part/part system with theme integration
;; IMPORTANT: theme/comp called at mount time, not render time
(defn alert-box [& {:as props}]
  (let [theme (theme/comp pre-theme theme)]  ; Called once at mount time
    (fn [& {:keys [id alert-type body padding closeable? on-close] :as props}]
      (let [part              (partial part/part part-structure props)
            heading-provided? (part/get-part part-structure props ::ab/heading)
            body-provided?    (part/get-part part-structure props ::ab/body)]
        
        (part ::ab/wrapper
          {:post-props (-> props
                           (cond-> padding (tu/style {:padding padding}))
                           (select-keys [:class :style :attr])
                           (update :class theme/merge-class alert-class)
                           (debug/instrument props))
           :theme      theme  ; Pre-composed theme passed to parts
           :props
           {:children
            [(when heading-provided?
               (part ::ab/top-section
                 {:impl       h-box
                  :theme      theme
                  :props      {:children [(part ::ab/heading-wrapper {...})
                                          (when closeable? close-alert)]}}))]}}))))
```

### 4. Theme Integration

**New Feature - Theme Files:**

Components now have dedicated theme files supporting multiple theme layers:

```clojure
;; src/re_com/alert_box/theme.cljs
(ns re-com.alert-box.theme
  (:require
   [re-com.alert-box :as-alias ab]
   [re-com.theme.util :as tu]
   [re-com.theme.default :refer [variables pre-user base main bootstrap user]]))

(defmethod base ::ab/wrapper [props]
  (tu/style props (flex-child-style "none")))

(defmethod bootstrap ::ab/wrapper [props]  
  (tu/class props "rc-alert" "alert" "fade" "in"))

(defmethod bootstrap ::ab/top-section [props]
  (tu/class props "rc-alert-heading"))

(defmethod main ::ab/close-button [props]
  (tu/class props "rc-alert-close-button"))
```

**Available theme methods**: `variables`, `pre-user`, `base`, `main`, `bootstrap`, `user`

## Critical: Theme Composition Timing

**⚠️ Important**: `theme/comp` should be called at **mount time** (outside render functions), not at **render time**:

```clojure
;; ✅ CORRECT - Form-2 component with theme composed at mount
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (let [composed-theme (theme/comp pre-theme theme)]  ; Mount time - once
    (fn [& {:as props}]  ; Render function
      ;; Use composed-theme here
      )))

;; ❌ INCORRECT - Theme composed on every render  
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (let [composed-theme (theme/comp pre-theme theme)]  ; Render time - every time
    ;; This causes performance issues
    ))
```

This is crucial for performance since theme composition is expensive and should only happen once when the component mounts.

## Migration Steps

### Step 1: Create Part Structure
1. Define hierarchical `part-structure` using qualified keywords
2. Mark top-level args with `:top-level-arg? true`
3. Replace manual `parts-desc` with `(part/describe part-structure)`

### Step 2: Add Theme Support
1. Add `:pre-theme` and `:theme` to component args-desc
2. Create theme file in component subdirectory
3. Define theme methods for appropriate layers (`variables`, `pre-user`, `base`, `main`, `bootstrap`, `user`)

### Step 3: Update Implementation
1. Convert to form-2 component if not already
2. Call `theme/comp` at mount time in outer function
3. Replace manual hiccup with `part/part` calls in render function
4. Use `part/get-part` to check for provided parts
5. **CRITICAL**: Apply composed theme to **every** part via `:theme` parameter

**Common mistake**: Forgetting to pass `:theme` to parts:
```clojure
;; ❌ WRONG: Theme not passed to parts
(part ::my-component/wrapper
  {:impl h-box
   :props {...}})

;; ✅ CORRECT: Theme passed to all parts
(part ::my-component/wrapper
  {:impl  h-box
   :theme composed-theme  ; <- Essential!
   :props {...}})
```

### Step 4: Handle Legacy Support
1. Mark legacy parts with `:type :legacy` 
2. Maintain backward compatibility for existing props
3. Use `post-props` to apply user styling to appropriate parts

## Key Changes Summary

| Aspect | Old Pattern | New Pattern |
|--------|-------------|-------------|
| **Parts Definition** | Manual arrays | `part-structure` hierarchy |
| **Parts Description** | Hand-written | Auto-generated via `part/describe` |
| **Theme Support** | Manual `theme/merge-class` | Integrated theme layers |
| **Theme Composition** | Per-render or manual | Once at mount via `theme/comp` |
| **Implementation** | Direct hiccup + `get-in` | `part/part` system |
| **Styling** | Inline merging | Theme methods + post-props |
| **Validation** | Manual parts validation | Automatic via part structure |

## Breaking Changes

1. **Component signatures** - `:pre-theme` and `:theme` added, some props moved to parts
2. **Parts structure** - Hierarchical instead of flat
3. **Theme files** - Required for full theme support  
4. **Component form** - Must be form-2 for proper theme composition timing
5. **Import requirements** - Need `re-com.part` and theme namespaces

## Wrapper-Centric Styling Approach

**Recommended Pattern**: Consolidate styling to the wrapper component for simpler, more maintainable themes:

### Before: Individual Part Styling
```clojure
;; Each part needs individual styling
(defmethod base ::my-component/input [props]
  (tu/style props {:flex "0 0 auto" :cursor "default"}))

(defmethod base ::my-component/label [props]
  (tu/style props {:flex "0 0 auto" :padding-left "8px" :cursor "default"}))
```

### After: Wrapper-Centric Styling
```clojure
;; Wrapper handles layout and shared properties
(defmethod base ::my-component/wrapper [props]
  (-> props
      (merge {:align :start :gap "8px"})        ; Layout via h-box
      (tu/style {:cursor "default"})))          ; Shared properties

;; Individual parts only need their specific classes
(defmethod bootstrap ::my-component/input [props]
  (tu/class props "rc-my-component-input"))

(defmethod bootstrap ::my-component/label [props]
  (tu/class props "rc-my-component-label"))
```

### Benefits
- **Fewer theme methods** - Most styling centralized on wrapper
- **Natural layout** - Use h-box `:gap` instead of manual padding
- **Shared properties** - Cursor, colors, etc. inherited from wrapper
- **User-friendly** - Label parts can accept strings and still get themed
- **Maintainable** - Layout changes in one place

### When to Use Individual Part Styling
- **Visual distinctions** - Different colors, fonts, borders per part
- **Specific behaviors** - Individual hover states, animations
- **Override scenarios** - When wrapper styling isn't sufficient

## Benefits of Migration

- **Better theme support** - Consistent theming across all parts with multiple layers
- **More flexible parts** - Support for hiccup, function, and map parts
- **Improved performance** - Memoized theme composition and part lookups
- **Enhanced user customization** - Top-level part args and theme layers
- **Maintainable code** - Structured approach vs manual property handling
- **Simplified styling** - Wrapper-centric approach reduces theme complexity
- **Future-proof** - Aligned with re-com's architectural direction

## Migration Checklist

- [ ] Define `part-structure` with qualified keywords
- [ ] Create theme file with appropriate theme method layers
- [ ] Update component args to include `:pre-theme` and `:theme`
- [ ] Convert to form-2 component structure
- [ ] Call `theme/comp` at mount time (outer function)
- [ ] Replace manual hiccup with `part/part` calls in render function
- [ ] Use `theme/comp` and `part/get-part` utilities
- [ ] Test theme integration and parts customization
- [ ] Verify legacy compatibility for existing users
- [ ] Update demo page to showcase new capabilities