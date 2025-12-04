# Parts System: Granular Styling

The parts system allows granular customization of component styling and behavior by exposing internal sub-elements as customizable "parts".

## What are Parts?

While an ordinary Reagent component simply returns a tree of hiccup, a re-com component identifies each meaningful hiccup as a **part**. This gives you control over the details of each hiccup in the tree: its component function, its props, and its children.

## Types of Part Specifications

Parts can be customized using different data types in the `:parts` map:

### 1. Hiccup Parts

A part-spec can be a string or hiccup vector. The value is placed directly into the hiccup tree:

```clojure
[rc/dropdown
 {:parts {:anchor "Open"
          :body   [:div "Sesame"]}}]
```

### 2. Function Parts

A part-spec can be a component function that replaces re-com's default component function for that part:

```clojure
[rc/dropdown
 {:parts {:anchor (fn [props]
                    [:span "I am " (get-in props [:re-com :state :openable])])
          :body   (fn [props]
                    [:ul
                     [:li "I am a " [:code (str (get props :part))]]
                     [:li "Class: " [:code (str (get props :class))]]])}}]
```

**Function parts receive these props:**
- `:part` - keyword with namespace and part name (e.g., `:re-com.dropdown/anchor`)
- `:re-com` - map with component context (`:state`, theme information)
- All standard props (`:class`, `:style`, `:attr`, etc.)

### 3. Map Parts

A part-spec can be a map for controlling visual characteristics without re-implementing the whole component:

```clojure
[rc/dropdown
 {:parts {:body   "Sesame"
          :anchor {:style {:color "pink"}
                   :class "italic"}}}]
```

## Defining Parts (Component Implementation)

### Legacy Approach (Pre-2025)

```clojure
(def component-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-component"      :impl "[v-box]"}
     {:name :header  :level 1 :class "rc-component-header" :impl "[h-box]"}
     {:name :body    :level 1 :class "rc-component-body"   :impl "[v-box]"}]))
```

### Modern Approach (2025+)

Components now use `part-structure` and the `part/part` system:

```clojure
(def part-structure
  [::my-component/wrapper {:impl 're-com.core/v-box}
   [::my-component/header {:impl 're-com.core/h-box}
    [::my-component/title {:top-level-arg? true}]]
   [::my-component/body {:impl 're-com.core/v-box}]])

(def my-component-parts-desc
  (when include-args-desc?
    (part/describe part-structure)))
```

## Parts Description Structure

Each part in `parts-desc` contains:

- `:name` - keyword identifier for the part
- `:level` - nesting level for documentation display
- `:class` - default CSS class name
- `:impl` - implementation description (e.g., "[v-box]", "[h-box]")
- `:notes` - optional description for documentation

## Parts Validation

Components should include parts validation in their args-desc:

```clojure
{:name :parts :required false :type "map" :validate-fn (parts? my-component-parts) :description "Map of part names to styling"}
```

## Parts System Architecture

### How `part/part` Works

The `part/part` function is the core of the modern parts system. It handles the polymorphic nature of part specifications:

```clojure
(defn part
  ([structure props k opts]
   (part (get-part structure props k)     ; Lookup part value
     (assoc opts :part k)))               ; Add part metadata
  ([part-value opts]
   (cond
     (hiccup? part-value) part-value      ; Direct hiccup insertion
     (string? part-value) part-value      ; Direct string insertion
     :else                                ; Map or function handling
     (let [component (cond (map? part-value) impl
                           (ifn? part-value) part-value
                           :else             impl)
           part-map  (when (map? part-value) part-value)
           props     (cond-> {:part part-id}
                       :do        (merge props)
                       theme      (theme component)      ; Apply theme
                       part-map   (tu/merge-props part-map)
                       post-props (tu/merge-props post-props))]
       [component props]))))
```

### Part Lookup Process

1. **`get-part`** - Checks both top-level args and `:parts` map:
   ```clojure
   (defn get-part [part-structure props k]
     (let [part-name (unqualify k)]
       (or (when (top-level-arg? part-structure part-name)
             (get props part-name))           ; Check top-level first
           (get-in props [:parts part-name])))) ; Then :parts map
   ```

2. **Top-level args** - Parts marked with `:top-level-arg? true` can be passed directly:
   ```clojure
   [my-component :label "Direct label" :parts {:wrapper {...}}]
   ;; Equivalent to:
   [my-component :parts {:label "Direct label" :wrapper {...}}]
   ```

### Part Structure Processing

The `part-structure` is processed at compile time to generate:

1. **Parts description** via `part/describe` - for documentation tables
2. **Parts validation** via `parts?` - for argument validation  
3. **Top-level args** via `top-level-args` - for direct parameter support

```clojure
;; Memoized tree walking functions
(def describe (memoize (fn [structure] ...)))  ; Generate parts-desc
(def top-level-args (memoize (fn [structure] ...)))  ; Find top-level parts
(def depth (memoize (fn [tree k] ...)))  ; Calculate nesting levels
```

### CSS Class Generation

Part IDs automatically generate CSS classes:

```clojure
(defn css-class [part-id]
  (str "rc-"
       (subs (namespace part-id) 7)    ; Remove "re-com." prefix
       "-"
       (name part-id)))

;; Example: ::my-component/wrapper -> "rc-my-component-wrapper"
```

### Performance Optimizations

1. **Memoization** - All tree-walking functions are memoized
2. **Compile-time processing** - `part-structure` processed once per component definition
3. **Lookup caching** - Part lookups use efficient map operations
4. **Theme composition** - Themes composed once at mount time

## Validation Architecture

The parts system includes comprehensive validation:

```clojure
(defn args-valid? [part-structure args problems]
  (let [part-seq  (tree-seq children part-structure)
        ks        (unqualify-set (map id part-seq))
        top-ks    (unqualify-set (top-level-args part-structure))
        ;; Check for conflicts between top-level args and :parts
        top-level-collisions (...)
        ;; Check for unsupported top-level keys
        top-level-unsupported-keys (...)]
    ;; Return validation problems
    ))
```

## Props Architecture in Parts/Theme System

### Core Principle: Simple Props Flow

The parts/theme system uses a straightforward approach to props handling:

1. **Theme methods receive all props** - No complex filtering or validation concerns
2. **Themes can add/modify any props** - Including component-specific props like `:width`, `:disabled?`
3. **Final component responsibility** - Components handle or ignore props as appropriate

### Standardized `:re-com` Structure

The `:re-com` key provides a structured namespace for theme-relevant metadata:

```clojure
{:re-com {:part        ::my-component/wrapper  ; Required - theme dispatch key
          :state       {...}                   ; Optional - component state for themes
          :transition! fn}                     ; Optional - state transition functions
 ;; All other props flow to final component
 :class     "user-class"
 :style     {:color "red"}
 :attr      {:on-click handler}
 :disabled? true
 :width     "200px"}
```

### `:re-com` Key Definitions

- **`:part`** - Required keyword for theme method dispatch (e.g., `::my-component/wrapper`)
- **`:state`** - Map of dereferenced component state values that themes need for conditional styling
- **`:transition!`** - Functions for managing component state transitions (e.g., show/hide)

### State Handling Rules

**Important**: `:state` always contains dereferenced values, never atoms:

```clojure
;; ✅ CORRECT - Always deref before putting in :state
:re-com {:state {:disabled? (deref-or-value disabled?)
                 :size      size
                 :showing?  @showing?}}

;; ❌ INCORRECT - Never put atoms in :state
:re-com {:state {:disabled? disabled-atom    ; Confusing for theme authors
                 :showing?  showing-atom}}

;; ✅ Performance atoms passed separately when needed
:showing? showing-atom                       ; Raw atom for reactivity
:re-com   {:state {:showing? @showing-atom}} ; Deref'd value for themes
```

### Statechart-Style State (Preferred Pattern)

**Prefer named states over booleans** - Use a statechart approach where every state has a distinct keyword name:

```clojure
;; ✅ PREFERRED - Statechart style with named states
:re-com {:state {:wrap :nicely}}            ; or :wrap :default
:re-com {:state {:status :validating}}      ; or :success, :error, :warning
:re-com {:state {:mode :edit}}              ; or :view, :preview

;; Theme methods match on keywords
(defmethod base ::my-component/wrapper [{{{:keys [wrap]} :state} :re-com :as props}]
  (tu/style props (when (= wrap :nicely)
                    {:background-color :white
                     :padding "16px"})))

;; ❌ AVOID - Boolean flags (unless truly binary with no future expansion)
:re-com {:state {:wrap-nicely? true}}       ; Less extensible
:re-com {:state {:is-validating? true}}     ; Harder to add more states

;; Component implementation
(let [re-com-ctx {:state {:wrap (if wrap-nicely? :nicely :default)}}]
  ...)
```

**Benefits of statechart approach:**
- **Extensible** - Easy to add more states later (e.g., `:wrap :compact`)
- **Clearer** - Named states are self-documenting
- **Type-safe** - Can use `case` instead of nested `if/when`
- **Explicit** - Every possible state has a name

### Exception: Performance Atoms in `:state`

For rare performance cases, atoms can be passed in `:state` using the `*` suffix convention:

```clojure
;; ✅ RARE CASE - Performance atom in :state with * suffix
:re-com {:state {:disabled? false           ; Normal dereferenced value
                 :model*    model-atom}}    ; Atom marked with * suffix

;; Theme authors know * suffix means atom
(defmethod bootstrap ::my-component/input [props]
  (let [{:keys [disabled? model*]} (get-in props [:re-com :state])
        current-value (when model* @model*)]  ; Theme derefs when needed
    (tu/class props "input"
              (when disabled? "disabled")
              (when (and model* (empty? current-value)) "empty"))))
```

**Use sparingly**: Only use `*` suffix when proven performance bottleneck exists.

### Theme Method Contract

Theme methods receive a predictable props structure:

```clojure
(defmethod bootstrap ::my-component/button [props]
  (let [{:keys [disabled? size]} (get-in props [:re-com :state])]
    (-> props
        (tu/class "btn"
                  (when disabled? "disabled")
                  (case size :large "btn-lg" :small "btn-sm" ""))
        ;; Can add component-specific props
        (assoc :width "auto"))))
```

### Implementation Pattern

```clojure
(defn my-component [& {:keys [pre-theme theme] :as props}]
  (let [theme (theme/comp pre-theme theme)]
    (fn [& {:keys [disabled? size on-click] :as props}]
      (let [part       (partial part/part part-structure props)
            ;; Build :re-com context with dereferenced state
            re-com-ctx {:state {:disabled? (deref-or-value disabled?)
                               :size      size}}]

        (part ::my-component/wrapper
          {:impl       h-box
           :theme      theme
           :props      {:re-com re-com-ctx}
           :post-props (select-keys props [:class :style :attr])})))))
```

## Best Practices

1. **Always define part-structure** for components with multiple styleable elements
2. **Use qualified keywords** - `::my-component/part-name` for proper namespacing
3. **Mark top-level args** - Use `:top-level-arg? true` for commonly-used parts
4. **Document implementation** - Specify `:impl` for each part in the structure
5. **Theme integration** - Ensure each part receives and applies theme
6. **Performance-conscious** - Remember theme composition happens at mount time
7. **Validate comprehensively** - Use generated validation from part structure
8. **Standardize `:re-com`** - Use only `:part`, `:state`, `:transition!` keys
9. **Deref state values** - Always dereference atoms before putting in `:state`
10. **Trust theme methods** - Let themes add any props, leave validation to final components