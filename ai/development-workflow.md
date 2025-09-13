# Development Workflow

This guide covers the core development workflow for working with re-com.

## Essential Commands

### Development Commands (using Babashka)
```bash
# Install dependencies
bb install

# Start development server with hot reloading
bb watch           # Full dev server (http://localhost:3449/)
bb watch-demo      # Demo app only

# Run tests
bb test            # run tests
bb browser-test    # Browser test watching (http://localhost:8021/)

# Build & deploy
bb test            # Run tests
bb clean           # Clean compiled artifacts
```

## Core Development Loop

### Making Changes
1. Run `bb watch` for hot reloading
2. Edit components in `src/re_com/`
3. View changes at http://localhost:3449/
4. Create/update demo page in `src/re_demo/`
5. Run `bb test` before committing

### Testing & Demo Development

Every component needs a demo page in `src/re_demo/`:

```clojure
(ns re-demo.my-component
  (:require
   [re-com.core :as rc]
   [re-com.my-component :refer [my-component-args-desc my-component-parts-desc]]
   [re-demo.utils :refer [panel-title title2 args-table parts-table]]
   [reagent.core :as r]))

(defn panel []
  (let [model (r/atom "initial value")]
    (fn []
      [rc/v-box
       :children
       [[panel-title "My Component"]
        [title2 "Demo"]
        [my-component
         :model model
         :on-change #(reset! model %)]
        [title2 "Parameters"]
        [args-table my-component-args-desc]
        [title2 "Parts"]
        [parts-table my-component-parts-desc]]])))
```

### Testing Commands

```bash
# Run all tests
bb test

# Watch tests during development  
bb browser-test

# Test specific component (if test file exists)
# Tests in test/re_com/my_component_test.cljs
```

## Committing Changes

When ready to commit:

1. **VERY IMPORTANT**: Run lint and typecheck commands (eg. npm run lint, npm run typecheck, ruff, etc.) with Bash if they were provided to ensure your code is correct
2. NEVER commit changes unless the user explicitly asks you to
3. Run `bb test` to ensure all tests pass
4. Only commit when explicitly requested by the user

## Browser Support

- **Primary**: Chrome (main development target)
- **Focus**: Desktop browsers
- **Responsive**: Desktop-first design
- **CSS**: Modern flexbox with vendor prefixes

## Creating Demo Pages

If you create a new component, you'll want to add a new panel to the re-com demo app, demonstrating its functionality and specifying its arguments.

### 1. Create the Namespace

The demo app has one namespace per component under `src/re-demo`. Place a new file in `src/re_demo` which mirrors the component name, and declare a namespace like:

```clojure
(ns re-demo.my-component
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.core  :refer [at h-box v-box box gap line label title]]
            [re-demo.utils :refer [panel-title title2 args-table]]
            [reagent.core :as reagent]))
```

### 2. Define Demo State and Helpers

Most pages define one or more functions (often named `*-demo` or `panel*`) that hold state atoms and return a hiccup for the interactive example. Keep page-specific state inside these functions using `reagent/atom`.

### 3. Compose the Demo Layout

Arrange explanatory text, controls and the demo component using `h-box`, `v-box`, `gap`, `line` and other layout helpers. Display the component's argument and part tables via `args-table` and `parts-table` from `re-demo.utils` and the component namespace.

### 4. Provide a `panel` Wrapper

The router in `core.cljs` expects each page namespace to expose a no-arg `panel` function:

```clojure
(defn panel []
  [button-demo])
```

### 5. Register the Page

Open `src/re_demo/core.cljs` and add an entry to `tabs-definition`. This entry links a `:label` and optional `:id` to your new `panel` function:

```clojure
{:id :my-component :level :minor :label "My Component" :panel my-component/panel}
```