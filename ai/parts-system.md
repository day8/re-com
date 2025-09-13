# Parts System: Granular Styling

The parts system allows granular customization of component styling by exposing internal sub-elements.

## Defining Parts

```clojure
(def component-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-component"      :impl "[v-box]"}
     {:name :header  :level 1 :class "rc-component-header" :impl "[h-box]"}
     {:name :body    :level 1 :class "rc-component-body"   :impl "[v-box]"}]))
```

## Using Parts

### In Component Implementation

```clojure
;; In component
:class (theme/merge-class "default-class" (get-in parts [:part-name :class]) class)
:style (merge default-style (get-in parts [:part-name :style]) style)
```

### Consumer Usage

```clojure
;; Consumer usage
[my-component 
 :parts {:wrapper {:class "custom-wrapper" :style {:background "blue"}}
         :header  {:class "custom-header"}}]
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