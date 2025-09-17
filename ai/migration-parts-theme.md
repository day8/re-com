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

### Theme Efficiency Best Practice

**Auto-generated CSS Classes**: The `re-com.theme/re-com-meta` layer automatically adds CSS classes based on part names using the pattern `"rc-{namespace-suffix}-{part-name}"`.

**When to skip `bootstrap` theme methods**:
- ✅ **Skip** when auto-generated class matches exactly what you want
- ❌ **Need** when you want Bootstrap built-in classes (`"progress"`, `"progress-bar"`, `"btn"`, etc.)
- ❌ **Need** when auto-generated class doesn't match desired legacy class name

**Examples**:
```clojure
;; ❌ REDUNDANT - re-com-meta auto-generates "rc-progress-bar-wrapper"
(defmethod bootstrap ::progress-bar/wrapper [props]
  (tu/class props "rc-progress-bar-wrapper"))

;; ✅ NEEDED - adds Bootstrap classes + legacy class
(defmethod bootstrap ::progress-bar/container [props]
  (tu/class props "progress" "rc-progress-bar"))

;; ✅ NEEDED - auto-generated would be "rc-progress-bar-progress-portion"
;;             but we want legacy "rc-progress-bar-portion"
(defmethod bootstrap ::progress-bar/portion [props]
  (tu/class props "progress-bar" "rc-progress-bar-portion"))
```

This keeps theme files clean and avoids duplicate class application.

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

**Part Structure Metadata Conventions:**

- **`:impl`** - Only for actual component functions (e.g., `'re-com.core/h-box`, `'re-com.close-button/close-button`)
- **`:tag`** - For HTML tags when using `part/default` (e.g., `:tag :div`, `:tag :span`, `:tag :input`)
- **No metadata** - When using `part/default` with default `<div>` tag

```clojure
;; ✅ CORRECT - Clear distinction
[::wrapper {:impl 're-com.core/h-box}        ; Uses h-box component
 [::container {:tag :div}                     ; Uses part/default with <div>
  [::input {:tag :input}]]                    ; Uses part/default with <input>
 [::label]]                                   ; Uses part/default with default <div>

;; ❌ OLD STYLE - Confusing mix
[::wrapper {:impl 're-com.core/h-box}
 [::container {:impl "div"}                   ; Unclear - is this a component?
  [::input {:impl "input"}]]]                 ; Unclear - is this a component?
```

**Namespace Alias Strategy:**

Each component should use a dedicated namespace alias for its parts, even if the namespace doesn't physically exist:

```clojure
;; ✅ CORRECT - Dedicated alias per component
[re-com.button :as-alias btn]
[re-com.md-circle-icon-button :as-alias ci-btn]

(def part-structure
  [::btn/wrapper {:impl 're-com.box/box}])     ; Regular button parts

(def md-circle-icon-button-part-structure
  [::ci-btn/wrapper {:impl 're-com.box/box}]) ; MD circle button parts

;; ❌ AVOID - Shared alias with long prefixes
[re-com.button :as-alias btn]

(def part-structure
  [::btn/wrapper {:impl 're-com.box/box}])

(def md-circle-icon-button-part-structure
  [::btn/md-circle-wrapper {:impl 're-com.box/box}]) ; Long, unclear names
```

**In part calls:** Always specify `:impl` explicitly, and `:tag` when using `part/default`:
```clojure
(part ::wrapper {:impl h-box ...})            ; Component function
(part ::input {:props {:tag :input} ...})     ; HTML tag via part/default
```

**Part Renaming Best Practices:**

When migrating, clean up part names by removing redundant/obsolete semantics:
- ✅ `::progress-container` → `::container` (remove redundant namespace prefix)
- ✅ `::alert-heading` → `::heading` (namespace already indicates it's alert-related)

For parts with no `:name` in old `parts-desc`, choose names using this priority:
1. **`wrapper`** - if it's the outermost part and wrapper is appropriate
2. **Re-com component name** - use the name portion (not namespace) when `:impl` uses another re-com component
   - `{:impl 're-com.core/h-box}` → `::h-box` or contextual name like `::header`
3. **Semantic name** - make up the best descriptive name when other strategies don't work

**Examples:**
```clojure
;; Old redundant names
[::progress-bar/progress-container  ; redundant "progress-"
 [::progress-bar/progress-portion]] ; redundant "progress-"

;; New clean names
[::container  ; clean, semantic
 [::portion]] ; clean, semantic

;; Component-based naming
[::wrapper {:impl 're-com.core/v-box}     ; outer container
 [::header {:impl 're-com.core/h-box}     ; uses h-box impl
  [::title {:impl "empty"}]]]             ; semantic content name
```

**⚠️ Important**: When renaming parts, verify CSS class compatibility:

```clojure
;; Old part name: ::progress-container
;; Old CSS class: "rc-progress-bar" (from legacy manual assignment)
;; New part name: ::container
;; Auto-generated: "rc-progress-bar-container" (from re-com-meta)

;; Since they differ, add bootstrap theme method:
(defmethod bootstrap ::progress-bar/container [props]
  (tu/class props "progress" "rc-progress-bar"))  ; Apply legacy class
```

Always check: **auto-generated class** (`"rc-{namespace-suffix}-{part-name}"`) vs **legacy class** - if they differ, add the legacy class in the `bootstrap` theme layer.

## ⚠️ Critical: User Styling Precedence & Target Compatibility

### User Styling Must Always Win (Highest Precedence)

**Rule**: Top-level user styling props must **always** override theme and parts styling. This is achieved by applying them via `:post-props` on the target part:

```clojure
;; ✅ CORRECT - User props in :post-props override theme styling
(part ::my-component/input
  {:theme      theme                           ; Applied first (lowest precedence)
   :post-props {:class "user-class"            ; Applied last (HIGHEST precedence)
                :style {:color "red"}          ; Overrides any theme color
                :max-height "200px"}})         ; Overrides any theme max-height

;; ❌ WRONG - User props in :props get overridden by theme
(part ::my-component/input
  {:props      {:class "user-class"}           ; Theme can override this!
   :theme      theme})                         ; Applied after :props
```

### Props That Must Go to :post-props

**All styling-related top-level props** must go to `:post-props` to maintain precedence:

- **Universal styling**: `:class`, `:style`, `:attr`
- **Component-specific styling**: `:padding`, `:max-height`, `:border-style`, `:width`, `:gap`, etc.
- **Behavior overrides**: `:disabled?`, `:placeholder`, etc.

### Target Part Compatibility

**⚠️ Important**: User styling target must match old implementation:

The old manual implementation applied `:class`, `:style`, `:attr` (and sometimes other arguments) selectively to specific inner hiccup elements, not always to the outermost wrapper. The modern migration must preserve this exact behavior for backward compatibility.

```clojure
;; Example: Button component
;; OLD: Applied user styling to the <button> element, not wrapper
[:div {:class "rc-button-wrapper"}
 [:button {:class class :style style :attr attr}]]

;; NEW: Must match - apply to ::button part, not ::wrapper
(part ::wrapper {...})
(part ::button
  {:post-props (select-keys props [:class :style :attr])})

;; Example: Input-text component
;; OLD: Applied user styling to the <input>, other props to wrapper
[h-box {:width width :class "rc-input-text"}
 [:div
  [:input {:class class :style style :attr attr :placeholder placeholder}]]]

;; NEW: Must match - styling to ::input, other props to ::wrapper
(part ::wrapper
  {:post-props (cond-> {} width (tu/style {:width width}))})
(part ::input
  {:post-props {:class class :style style
                :attr (merge {:placeholder placeholder} attr)}})
```

**Migration Rule**: Examine the old implementation carefully to see exactly which hiccup element received each user argument, then apply those same arguments to the corresponding part in the modern implementation. This ensures existing user code continues to work identically.

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

**Implementation Best Practices:**

**Let-bind reused parts** - If the original component reused hiccup in multiple places, create let-bound part variables:

```clojure
;; ✅ CORRECT - Let-bind parts used multiple times
(let [part      (partial part/part part-structure props)
      icon-part (part ::icon {:theme theme :post-props {...}})
      btn-part  (part ::button {:theme theme :props {:children [icon-part]}})]
  ;; Use btn-part in multiple places (tooltip anchor, direct child, etc.)
  (part ::wrapper {...}))

;; ❌ INEFFICIENT - Duplicate part creation
(part ::wrapper
  {:child (if tooltip?
            (part ::tooltip {:anchor (part ::button {...})}) ; Creates button part
            (part ::button {...}))})                         ; Creates button part again
```

**Separate concerns cleanly** - Component functions handle Reagent state/reactions, parts handle rendering:

```clojure
;; ✅ CORRECT - Clean separation
(let [disabled?  (deref-or-value disabled?)    ; Reagent state management
      showing?   (reagent/atom false)          ; Local component state
      part       (partial part/part ...)       ; Part creation helper
      ;; Pass pure values to theme via :re-com context
      btn-part   (part ::button
                   {:theme theme
                    :post-props (select-keys props [:class :style :attr])
                    :props {:re-com {:disabled? disabled?
                                     :showing?  @showing?
                                     :tooltip?  tooltip?}}})]
  ;; Theme methods handle conditional styling based on :re-com context
  )

;; ❌ MIXED CONCERNS - Styling logic in component body
(part ::button
  {:post-props {:class (str "base-class"
                           (when disabled? " disabled-class")  ; Styling in component
                           (when @showing? " active-class"))   ; Styling in component
   :style (merge base-style
                 (when disabled? disabled-style))}            ; Styling in component
```

## Step 4: Implement New Props Architecture

### Core Principle: Simplified Props Flow

The new architecture eliminates the validation confusion between theme methods and component implementations:

1. **Theme methods receive ALL props** - no complex filtering needed
2. **Themes can add ANY props** - including component-specific props like `:width`, `:disabled?`
3. **Final components handle validation** - they ignore unknown props or validate as needed

### Standardized `:re-com` Structure

All components now use a structured `:re-com` namespace for theme metadata:

```clojure
;; NEW STANDARD: Structured :re-com context
{:re-com {:part        ::my-component/wrapper  ; Required - theme dispatch
          :state       {...}                   ; Optional - dereferenced state
          :transition! fn}                     ; Optional - state functions
 ;; All other props flow to final component
 :class     "user-class"
 :style     {:color "red"}
 :attr      {:on-click handler}
 :disabled? true
 :width     "200px"}
```

### Migration Pattern: `:re-com` Context

**Old Pattern:**
```clojure
;; Mixed styling logic in component function
(part ::button
  {:post-props {:class (str "base-class"
                           (when disabled? " disabled-class"))
                :style (when active? active-style)}})
```

**New Pattern:**
```clojure
;; Component function - compute state, build context
(let [re-com-ctx {:state {:disabled? (deref-or-value disabled?)
                          :size      size
                          :active?   @showing?}}]      ; Always deref atoms
  (part ::button
    {:theme theme
     :props {:re-com re-com-ctx}
     :post-props (select-keys props [:class :style :attr])}))

;; Theme method - handle conditional styling
(defmethod bootstrap ::my-component/button [props]
  (let [{:keys [disabled? size active?]} (get-in props [:re-com :state])]
    (tu/class props "btn"
              (case size :small "btn-sm" :large "btn-lg" "")
              (when disabled? "disabled")
              (when active? "active"))))
```

### State Handling Rules

**Critical**: `:state` always contains dereferenced values, never atoms:

```clojure
;; ✅ CORRECT - Always deref before putting in :state
:re-com {:state {:disabled? (deref-or-value disabled?)
                 :showing?  @showing-atom}}

;; ❌ INCORRECT - Never put atoms in :state
:re-com {:state {:disabled? disabled-atom    ; Confusing for theme authors
                 :showing?  showing-atom}}

;; ✅ Performance atoms passed separately when needed
:showing? showing-atom                       ; Raw atom for reactivity
:re-com   {:state {:showing? @showing-atom}} ; Deref'd value for themes

;; ✅ RARE EXCEPTION - Performance atom in :state with * suffix
:re-com {:state {:disabled? false
                 :model*    model-atom}}     ; * suffix indicates atom
```

### Theme Method Benefits

With the new architecture, theme methods become much simpler:

```clojure
;; Theme methods get predictable, structured props
(defmethod bootstrap ::my-component/wrapper [props]
  (let [{:keys [disabled? size]} (get-in props [:re-com :state])]
    (-> props
        ;; Always safe - universal styling
        (tu/class "my-component"
                  (when disabled? "disabled"))
        ;; Safe when you know the :impl (e.g., h-box accepts :gap)
        (assoc :gap "8px" :align :center)
        ;; Component will ignore unknown props
        (assoc :custom-prop "value"))))
```

This keeps the component function focused on state management and the theme focused on presentation logic.

**Understanding `:attr` vs `:props` vs `:post-props`** - Critical distinction for proper HTML attributes:

- **`:attr`** - What you as a caller think should go directly onto the hiccup as HTML attributes:
  - If the component creates an HTML element (like `[:div {...}]`), these become HTML attributes
  - If the component uses another component, these get passed as `:attr` to that component

- **`:props` and `:post-props`** - Add top-level keys to the hiccup of the component you're using:
  - The component may use them or ignore them entirely - not guaranteed to be treated as HTML attributes
  - **`:post-props`** specifically means "post-theme and post-parts" - applied after the theme system processes props
  - Used to implement "convenience" arguments that many components provide

**What are "convenience" arguments?** - Top-level shortcuts that components provide for common styling patterns:

```clojure
;; Convenience: :width as top-level shortcut
[re-com.core/h-box :width "200px" ...]
;; Is internally converted to:
[re-com.core/h-box :style {:width "200px"} ...]

;; Convenience: :disabled? as top-level shortcut
[re-com.core/input-text :disabled? true ...]
;; Is internally converted to:
[re-com.core/input-text :attr {:disabled true} ...]

;; Convenience: :gap as top-level shortcut
[re-com.core/h-box :gap "8px" ...]
;; Is internally converted to:
[re-com.core/h-box :style {:gap "8px"} ...]
```

```clojure
;; ✅ CORRECT: HTML attributes via :attr
(part ::my-component/icon
  {:props {:tag  :svg
           :attr {:width "11" :height "11"}}})    ; Becomes <svg width="11" height="11">

;; ❌ INCORRECT: Assuming top-level convenience
(part ::my-component/icon
  {:post-props {:width "11" :height "11"}})      ; re-com.part/default ignores these entirely

;; ✅ CORRECT: Using convenience props (when component supports them)
(part ::my-component/h-box
  {:post-props {:width "200px"                   ; h-box provides this convenience
                :gap   "8px"}})                  ; converted to {:style {:width "200px" :gap "8px"}}

;; ✅ CORRECT: Direct styling when no convenience exists
(part ::my-component/element
  {:post-props {:style {:width "200px"           ; Direct CSS styling
                        :gap   "8px"}}})         ; when convenience isn't available
```

**Key insight**: `re-com.part/default` (used with `:tag`) doesn't provide convenience top-level props - you must use `:attr` for HTML attributes and `:style` for CSS. Other re-com components like `h-box`, `input-text`, etc. provide many convenience shortcuts that get converted internally.

**Args building pattern** - Use `into` for clean args-desc composition:

```clojure
;; ✅ CORRECT - Clean composition with into
(def component-args-desc
  (when include-args-desc?
    (into [{:name :my-prop :required true ...}
           {:name :another-prop :required false ...}]
          (part/describe-args part-structure))))

;; ❌ VERBOSE - Manual concatenation
(def component-args-desc
  (when include-args-desc?
    (concat
     [{:name :my-prop :required true ...}
      {:name :another-prop :required false ...}]
     (part/describe-args part-structure))))
```

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
- **No validation conflicts** - Themes can add any props without breaking components
- **Future-proof** - Aligned with re-com's architectural direction

## Migration Checklist

### Part Structure & Theme Setup
- [ ] Define `part-structure` with qualified keywords
- [ ] Create theme file with appropriate theme method layers
- [ ] Update component args to include `:pre-theme` and `:theme`

### Component Implementation
- [ ] Convert to form-2 component structure
- [ ] Call `theme/comp` at mount time (outer function)
- [ ] Replace manual hiccup with `part/part` calls in render function
- [ ] Use `part/get-part` to check for provided parts

### Props Architecture
- [ ] Implement standardized `:re-com` structure (`:part`, `:state`, `:transition!`)
- [ ] Always deref atoms before putting in `:state`
- [ ] Pass `:re-com` context to all parts via `:props`
- [ ] Move conditional styling logic from component to theme methods
- [ ] **CRITICAL**: Apply ALL user styling props via `:post-props` (never `:props`)
- [ ] **CRITICAL**: Apply user props to same target parts as old implementation
- [ ] Use `(select-keys props [:class :style :attr])` for universal styling
- [ ] Apply component-specific props (`:padding`, `:max-height`, etc.) via `:post-props`

### Testing & Validation
- [ ] Test theme integration and parts customization
- [ ] Verify user styling applies to correct parts (maintain legacy behavior)
- [ ] Test with various prop combinations (including unknown props)
- [ ] Verify legacy compatibility for existing users
- [ ] Update demo page to showcase new capabilities
