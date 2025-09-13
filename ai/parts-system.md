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

## Best Practices

1. **Always define parts-desc** for components with multiple styleable elements
2. **Use consistent naming** - prefer :wrapper for outer container, :body for main content
3. **Document implementation** - specify which re-com component each part uses
4. **Validate parts usage** - include parts validation in args-desc
5. **Theme integration** - use `theme/merge-class` to combine default, parts, and user classes