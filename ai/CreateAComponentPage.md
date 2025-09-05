# Creating a Demo App Page

If you create a new component, you'll want to add a new panel to the re-com demo app, demonstrating its functionality and specifying its arguments. Here's how to do that. 

## 1. Create the Namespace

The demo app has one namespace per component under `src/re-demo`. Place a new file in `src/re_demo` which mirrors the component name, and declare a namespace like:

```clojure
(ns re-demo.my-component
  (:require-macros [re-com.core :refer [handler-fn]])
  (:require [re-com.core  :refer [at h-box v-box box gap line label title]]
            [re-demo.utils :refer [panel-title title2 args-table]]
            [reagent.core :as reagent]))
```

You may `require` additional namespaces as needed, typically the component being demonstrated and utility functions.

## 2. Define Demo State and Helpers

Most pages define one or more functions (often named `*—demo` or `panel*`) that hold state atoms and return a hiccup for the interactive example. Keep page-specific state inside these functions using `reagent/atom`.

## 3. Compose the Demo Layout

Arrange explanatory text, controls and the demo component using `h-box`, `v-box`, `gap`, `line` and other layout helpers. Display the component's argument and part tables via `args-table` and `parts-table` from `re-demo.utils` and the component namespace.

A typical page has a structure like:

```clojure
(defn button-demo []
  (let [clicked? (reagent/atom false)]
    (fn []
      [v-box
       :src      (at)
       :gap      "10px"
       :children [[panel-title "[button ...]" "src/re_com/buttons.cljs" "src/re_demo/button.cljs"]
                  ;; notes column on the left
                  [h-box
                   :children [...explanatory widgets...]]
                  ;; demo column on the right
                  [h-box
                   :children [...demo widgets...]]]])))
```

## 4. Provide a `panel` Wrapper

The router in `core.cljs` expects each page namespace to expose a no‑arg `panel` function. It should merely call your demo function so that shadow hot reloading can replace the code correctly:

```clojure
(defn panel []
  [button-demo])
```

## 5. Register the Page

Open `src/re_demo/core.cljs` and add an entry to `tabs-definition`. This entry links a `:label` and optional `:id` to your new `panel` function. Once added, the page will appear in the demo navigation.

```clojure
{:id :my-component :level :minor :label "My Component" :panel my-component/panel}
```

## 6. Test the Page

Run the demo with:

```bash
npx shadow-cljs watch demo
```

Navigate to your new page in the browser to ensure it appears and behaves as expected.
