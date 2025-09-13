# Creating a re-com Component (Legacy - See component-creation-modern.md)

**Note: This file is kept for reference but is outdated. Please refer to [component-creation-modern.md](./component-creation-modern.md) for the current approach using the modern parts and theme system.**

## Legacy Approach (Pre-2025)

This guide describes the older approach to creating re-com components. 

## 1. Namespace setup

Create a new namespace under `src/re_com`.  

Components generally require several macros and utilities, so the namespace should start like this (modify `my-component` as necessary):

```clojure
(ns re-com.my-component
  (:require-macros [re-com.core     :refer [handler-fn at reflect-current-component]]
                   [re-com.validate :refer [validate-args-macro]])
  (:require [re-com.debug    :refer [->attr]]
            [re-com.theme    :as    theme]
            [re-com.util     :refer [deref-or-value]]
            [re-com.config   :refer [include-args-desc?]]))
```

* `handler-fn` ensures event handlers do not accidentally return `false`, which React interprets specially.  See the commentary in `core.clj` lines 3‑32 for details.
* `at` and `reflect-current-component` supply debugging metadata such as source coordinates and the current component name.
* `validate-args-macro` performs argument validation when `goog.DEBUG` is enabled.

## 2. Specify Component Parts 

Components document their internal structure with a vector of maps known as `parts-desc` and a derived set `parts`.  These are only included when `include-args-desc?` is true so that production builds remain lean.

An example from `buttons.cljs` lines 20‑28 illustrates the idea:

```clojure
(def button-parts-desc
  (when include-args-desc?
    [{:name :wrapper :level 0 :class "rc-button-wrapper" :impl "[button]" :notes "Outer wrapper of the button, tooltip (if any), everything."}
     {:name :tooltip :level 1 :class "rc-button-tooltip" :impl "[popover-tooltip]" :notes "Tooltip, if enabled."}
     {:type :legacy  :level 1 :class "rc-button"         :impl "[:button]"         :notes "The actual button."}]))

(def button-parts
  (when include-args-desc?
    (-> (map :name button-parts-desc) set)))
```

Consumers can customise a component via the optional `:parts` argument by providing classes, styles or attrs keyed by these part names.

## 3. Specify Component Arguments

Each component declares an `args-desc` that specifies expected/allowed arguments, default values and validation functions.  Again from `buttons.cljs` lines 30‑42:

```clojure
(def button-args-desc
  (when include-args-desc?
    [{:name :label            :required true  :type "string | hiccup" :validate-fn string-or-hiccup?     :description "label for the button"}
     {:name :on-click         :required false :type "-> nil"          :validate-fn fn?                   :description "called when the button is clicked"}
     {:name :tooltip          :required false :type "string | hiccup" :validate-fn string-or-hiccup?     :description "what to show in the tooltip"}
     ...]))
```

One map for each argument, and each map contain keys:
  - `:name` - a keyword - used to identify the argument
  - `:required` - boolean - is the argument required or not (ie. optional)?
  - `:default` - if omitted, what is the default value
  - `:type` - a description of the type - this is largely descriptive (the validation function does the enforcing)
  - `:validate-fn` - a function to validate this argument. Validation helpers live in `re-com.validate` 
  - `:description` - a user-friendly description shown in documentation 


## 4. Component Implementation

A component function is usually Reagent form‑2 or form‑3.  It begins with `validate-args-macro` to check its arguments and then produces Hiccup markup.  The button component demonstrates the pattern in lines 44‑91:

```clojure
(defn button []
  (let [showing? (reagent/atom false)]
    (fn [& {:keys [label on-click tooltip tooltip-position disabled? class style attr parts src debug-as]
            :or   {class "btn-default"}
            :as   args}]
      (or
       (validate-args-macro button-args-desc args)
       (do
         (when-not tooltip (reset! showing? false))
         (let [disabled? (deref-or-value disabled?)
               the-button [:button
                            (merge
                             {:class    (str "rc-button btn " class)
                              :style    (merge (flex-child-style "none") style)
                              :disabled disabled?
                              :on-click (handler-fn
                                         (when (and on-click (not disabled?))
                                           (on-click event)))}
                             (when tooltip
                               {:on-mouse-over (handler-fn (reset! showing? true))
                                :on-mouse-out  (handler-fn (reset! showing? false))})
                             attr)
                            label]]
           [box
            :src      src
            :debug-as (or debug-as (reflect-current-component))
            :class    (str "rc-button-wrapper display-inline-flex " (get-in parts [:wrapper :class]))
            :style    (get-in parts [:wrapper :style])
            :attr     (get-in parts [:wrapper :attr])
            :align    :start
            :child    (if tooltip
                        [popover-tooltip
                         :src      (at)
                         :label    tooltip
                         :position (or tooltip-position :below-center)
                         :showing? showing?
                         :anchor   the-button
                         :class    (str "rc-button-tooltip " (get-in parts [:tooltip :class]))
                         :style    (get-in parts [:tooltip :style])
                         :attr     (get-in parts [:tooltip :attr])]
                        the-button)]))))))
```

Key takeaways:

* `validate-args-macro` returns a diagnostic component if validation fails.
* `handler-fn` wraps event handlers to avoid accidentally returning `false`.
* `deref-or-value` handles parameters that might be plain values or atoms.
* `->attr` injects debugging attributes (`data-rc` and source coordinates) into the root element.

## 5. Exposing Your Component

To make the component part of the public API, add a simple `def` in `re_com/core.cljs` that re-exports it.  For example, lines 64‑71 show how various button helpers are exposed:

```clojure
(def button                     buttons/button)
(def md-circle-icon-button      buttons/md-circle-icon-button)
(def md-icon-button             buttons/md-icon-button)
(def info-button                buttons/info-button)
(def row-button                 buttons/row-button)
(def hyperlink                  buttons/hyperlink)
```

Consumers then `require` `re-com.core` and call your function by its public name.

## 6. Validation internals

The validation system is implemented in `re_com.validate`.  The `validate-args` function (lines 138‑170) performs several checks:

1. Unknown or misspelled argument names.
2. Missing required arguments.
3. Custom validation functions per argument.

If any problems are found, a special component is rendered in place of your component, and error details are logged to the console.

```clojure
(defn validate-args [arg-defs passed-args]
  (if-not debug?
    nil
    (let [{:keys [parts-validate-fn]} arg-defs
          passed-arg-keys (set (remove #{:theme :re-com :part} (set (keys passed-args))))
          problems        (as-> [] pvec
                           (if-not parts-validate-fn
                             pvec
                             (parts-validate-fn passed-args pvec))
                           (arg-names-known? (:arg-names arg-defs) passed-arg-keys pvec)
                           (required-args?   (:required-args arg-defs) passed-arg-keys pvec)
                           (validate-fns?    (:validated-args arg-defs) passed-args pvec)
                           (remove nil? pvec))]
      (when-not (empty? problems)
        [debug/validate-args-error
         :problems  problems
         :args      passed-args
         :component (debug/short-component-name (component/component-name (reagent/current-component)))]))))
```

## 7. Create a Page In the demo app 

All compenents have a page in the demo app.   


## 8. Putting it all together

1. **Create** a namespace and require the helper macros and functions.
2. **Define** `parts-desc`, `parts` and `args-desc` to describe your API.
3. **Write** the component using Reagent hiccup, starting with `(validate-args-macro ...)`.
4. **Use** `handler-fn`, `deref-or-value`, `->attr` and other utilities as needed.
5. **Expose** the component via `re_com.core` for library consumers.
6. **Demo** add a page to the demo app

Following these conventions keeps new components consistent with the rest of `re-com` and ensures a good debugging experience.
