# Theme System

Re-com's theme system provides consistent styling and supports theme switching across components.

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

## Theme Integration Best Practices

1. **Always use `theme/merge-class`** instead of direct CSS class concatenation
2. **Combine classes in the right order**: default → parts → user classes
3. **Support theme switching** by using the theme system consistently
4. **Style merging** - use `merge` for combining style maps from different sources

## Example Usage

```clojure
;; Component with theme integration
[:div
 {:class (theme/merge-class "rc-my-component" (get-in parts [:wrapper :class]) class)
  :style (merge default-style (get-in parts [:wrapper :style]) style)}
 content]
```

## Future Theme Features

The theme system is designed to support:
- Theme switching at runtime
- Consistent styling across all components
- Customizable color schemes and styling
- CSS-in-JS integration