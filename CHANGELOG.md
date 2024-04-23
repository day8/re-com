## N.N.N (YYYY-MM-DD)

> Committed but unreleased changes are put here, at the top. Older releases are detailed chronologically below. 

## 2.21.0 (2024-04-24)

#### Added

- New tooling, using `bb.edn` and `deps.edn`.


## 2.20.0 (2024-04-23)

#### Added

- `nested-grid` (alpha) - new component for multidimensional tabular data viewing.
- `dropdown` (alpha) - generic dropdown component.
- `:theme` (alpha) - a general solution for themeing components.

## 2.19.0 (2024-03-07)

#### Added

- `popover-tooltip` - added a prop for the color of each status.

## 2.18.1 (2024-02-16)

#### Added

- `v-table`, `simple-v-table` fixed performance drop when scrolling through sorted rows.


## 2.18.0 (2024-02-02)

#### Added

- `simple-v-table` - `:on-export`, `:export-button-renderer` and `:show-export-button?`

## 2.17.1 (2024-01-24)

#### Fixed

- `tree-select` - `:parts` prop should work now. (#335)

## 2.17.0 (2024-01-23)

#### Changed

- `simple-v-table` - Shift-click multiple columns to sort hierarchically.

## 2.16.4 (2024-01-19)

#### Fixed

- `simple-v-table` column headers now are now :vertical-align "center"

## 2.16.3 (2024-01-19)

#### Fixed

- `simple-v-table` column headers now :align correctly. Also, :vertical-align now only affects cells, not headers.

## 2.16.2 (2024-01-16)

#### Fixed

- Replace deprecated `r/dom-node` with ref callbacks. (#329, #334) Thanks [RolT](https://github.com/RolT) for PR.

## 2.16.1 (2024-01-16)

#### Fixed

- Cleanup & fixes for the new tree-select component.

## 2.16.0 (2024-01-16)

#### Added

- Tree-select component (alpha)

## 2.14.0 (2023-12-21)

#### Changed

- Change colour of `:focus` outline on non-native components such as `button` and `single-dropdown`
- `simple-v-table` sortable column icons now only appear on hover. **Requires copying updated `re-com.css` to your project.**

#### Added

- Add parts to tag-dropdown for internal selection-list
- Add simple-v-table striped? argument
- Expose tag-dropdown/text-tag via re-com.core/text-tag
- :disabled? prop for tabs & tab pickers (#331)
- :filter-fn prop for multi-select (#304)

#### Fixed

- Selected tabs & radio buttons no longer trigger their on-change handler (#333)
- Use alert-circle for input-text error state, not spinner (#325)
- Stop using deprecated keycodes. This fixes some edge cases with keyboard layouts. (#197)
- Props which are non-reactive atoms are now caught in validation. (#291)

## 2.13.2 (2021-02-24)

#### Fixed

- Fix `re-com.core/at` macro availability via `:refer` so you can now simply do:
```clojure
(:require
  [re-com.core :refer [at]])
```
- Fix `re-com.core/at` macro eliding (i.e. return nil) in production builds.
- Fix logging of source urls in validation errors or `stack-spy` when no
  `re-com.config/root-url-for-compiler-output` Closure Define is provided in
  the compiler configuration; i.e. do not log the URL as missing the required
  information to create a valid URL.

## 2.13.1 (2021-02-24)

#### Fixed

- Fix `datepicker-dropdown` exception. Regression in 2.13.0. 

## 2.13.0 (2021-02-23)

When upgrading any app to use this version, please update the following:
   - `re-com.css`
   - `css/material-design-iconic-font.min.css`
   - `fonts/Material-Design-Iconic-Font.eot`
   - `fonts/Material-Design-Iconic-Font.svg`
   - `fonts/Material-Design-Iconic-Font.ttf`
   - `fonts/Material-Design-Iconic-Font.woff`
   - `fonts/Material-Design-Iconic-Font.woff2`

#### Added

- Add new debugging features, including the new `:src` parameter. [The debug page](https://re-com.day8.com.au/#/debug) explains more.  Note: where previously parameter validation errors raised exceptions, now components instead draw themselves as a red, placeholder box, and write error information to the devtools console. This is a gentler, more informative approach - exceptions are too jarring and result in a blizzard of confusing React stack traces in the console.
- Add a new compiler configuration `re-com.config/root-url-for-compiler-output`. [The config page](https://re-com.day8.com.au/#/config) explains more.
- Add column sorting feature to `[simple-v-table]`. Column specifications can now, optionally include a `:sort-by` key.
- Add new arguments to `[tag-dropdown]` including `:required?` ,`:min-width`, `:max-width`, `:abbrev-fn` and `:abbrev-characters`.
- Add the argument `:split-is-px?` to `[splitter]`.  See [#178](https://github.com/day8/re-com/issues/178)
- Add year navigation buttons to `[datepicker]`. See [#109](https://github.com/day8/re-com/issues/109#issuecomment-770462387) **Breaking** the HTML structure for the `datepicker` navigation section was changed and
  certain CSS classes have been renamed/changed. If you have custom CSS selectors targeting these parts, you'll need to edit as follows:
   - `available` to `rc-datepicker-selectable`
   - `disabled` to `rc-datepicker-disabled`
   - `off` to `rc-datepicker-unselectable` (for unselectable days) or `rc-datepicker-out-of-focus` (for days not in the current month)
   - `selected` to `rc-datepicker-selected`
   - `today` to `rc-datepicker-today`.

#### Fixed

- Previously, the argument and parts description data structures (for each component) were incorrectly included into production builds. This is now fixed. 
  (i.e. when `js/GOOG.debug` is false).
- `[datepicker]` week number calculation with arbitrary `:start-of-week` argument. See [#159](https://github.com/day8/re-com/issues/159)
- `[tag-dropdown]` popover alignment is now centered under the component, rather than off to the left.
- Fix the `disabled? true` state styling of `[tag-dropdown]`
- Fix the `disabled? true` state styling of `[datepicker]`
- `[alert-list]` no longer ignores individual alert `:style` argument. See [#83](https://github.com/day8/re-com/issues/83)

#### Changed

- Upgrade Material Design Iconic Font to [2.2.0](https://github.com/zavoloklom/material-design-iconic-font/releases/tag/2.2.0). See [#253](https://github.com/day8/re-com/pull/253). Thanks to [@esnyder](https://github.com/esnyder).

##### `[text/p]`

- `[text/p]` is now an alias to `[text/p-span]`.  Externally this means no change. But interally, it is implemented using `[:span]` instead of `[:p]` (allowing you to embed `boxes` etc).  **Breaking** Because of the change in HTML elements used, your custom CSS selectors targetting `p` elements will have to be changed to target `span.rc-p`. 
  
##### `[tag-dropdown]`

- **Breaking**
  - Removed the `tag-dropdown` arguments `:tag-width`, `:tag-height`, `:tag-comp` and `:on-tag-click`. To fix, remove
  use of these arguments from your code.
  - Changed default of `:unselect-buttons?` to `false`, if you want to maintain the old behaviour then add
  `:unselect-buttons? true` to the arguments passed to the component.

##### `[v-table]` and `[simple-v-table]`

Both of these components are still `Alpha`. Iterative improvements have continued
causing some breaking changes.

- **Breaking**:
  - Argument renames. This will break any CLJS code that pass the arguments to the components. To fix, change the arg
  to use the new name:
    - `:header-renderer` arg of `simple-v-table` to `:column-header-renderer`
    - `:max-table-width` arg of `v-table` and `simple-v-table` to `:max-width`
    - `:scroll-cols-into-view` arg of `v-table`  to `:scroll-cols-into-view`
    - `:col-header-renderer` arg of `v-table`  to `:column-header-renderer`
    - `:col-header-height` arg of `v-table`  to `:column-header-height`
    - `:col-header-selection-fn` arg of `v-table`  to `:column-header-selection-fn`
    - `:parts` args that match  `:v-table-*` to `:*` (in other words, remove `v-table-`)
    - `:parts` args that match  `:*-col-*` to `:*-column-*`
    - `:id-fn` arg has been renamed `:key-fn` and its default has been changed from `:id` to `nil`. If you leave it blank or pass nil, it will use the row's internally generated 0-based row-index instead of :key-fn
    - `:style-parts` arg of `v-table`  to `:parts`
      - Then wrap any style maps in a map with a key of `:style`. For example:
        ```clojure
           :style-parts {:v-table {:background-color "lightgrey"}}

           ;; becomes...

           :parts {:wrapper {:style {:background-color "lightgrey"}}}

           ;; notice, the above also included a :v-table => :wrapper conversion 
        ```

  - Rename `:valign` in `simple-v-table` `:columns` specification to `:vertical-align` to
match the associated CSS property. Fix the documentation and demos of the same.
  - `:attr-parts` arg of `v-table` has been removed. You need to incorporate it into the `:parts` arg. For example:
    ```clojure
    :attr-parts {:v-table-top-left {:on-click (handler-fn ...)}}
    
    ;; becomes...
    
    :parts {:top-left {:attr {:on-click (handler-fn ...)}}}
    
    ;; notice, the above also included a :v-table-top-left => :top-left conversion 
    ```

## 2.12.0 (2021-01-23)

#### Fixed

- Fix v-table as `:model` deref was broken in last release.
- Fix `[multi-select]` and `[selection-list]` scrolling when `disabled?` 
- Fix `[datepicker]` styling of disabled/unselectable vs days out of the current month

#### Removed

- **Breaking**
  - Remove `re-com.misc` ns. Replaced by `re-com.checkbox`, `re-com.input-text`, `re-com.radio-button`, `re-com.slider`,
  `re-com.progress-bar` and `re-com.slider`. If you require `re-com.misc` directly in your code, instead of using the
  aliases in `re-com.core`, then you will need to change that to the appropriate new namespace reference(s).

## 2.11.0 (2021-01-23)

#### Added

- Add `:parts` argument to all components that are constructed from a hierarchy of elements. See 'Parts' section of 
  component pages at [https://re-com.day8.com.au/](https://re-com.day8.com.au/). 
- Add `multi-select`. See [https://re-com.day8.com.au/#/multi-select](https://re-com.day8.com.au/#/multi-select).
- Add `v-table` and `simple-v-table`. See [https://re-com.day8.com.au/#/v-table](https://re-com.day8.com.au/#/v-table) and
  [https://re-com.day8.com.au/#/simple-v-table](https://re-com.day8.com.au/#/simple-v-table).
- Add `tag-dropdown`. See [https://re-com.day8.com.au/#/tag-dropdown](https://re-com.day8.com.au/#/tag-dropdown).
- Add optional 2-arity variant of `on-change` to `input-text`. Fixes [#219](https://github.com/day8/re-com/issues/219).

#### Changed

- Improved `disabled?` styling of many components.

## 2.10.1 (2020-12-22)

#### Changed

- Change popover-anchor-wrapper to allow the use of values in `showing?`. See [#153](https://github.com/day8/re-com/pull/153)

## 2.10.0 (2020-12-21)

#### Added

- Add 15 new attributes and 1 fix to single-dropdown. See [#202](https://github.com/day8/re-com/pull/202)
- Add datepicker i18n and ability to set its width. See [#201](https://github.com/day8/re-com/pull/201)
- Add :on-alter to input-text-base. See [#200](https://github.com/day8/re-com/pull/200)
- Add tooltip to horizontal-bar-tabs & :validate?. See [#199](https://github.com/day8/re-com/pull/199)

#### Fixed

- Remove reset! of external-model in input-text-base event handlers. Fixes [#219](https://github.com/day8/re-com/issues/219)
  Caused regression of [#187](https://github.com/day8/re-com/issues/187). Fixed in 2.11.0.
- Fix consistency of `disabled?` arg
- Fix CSS comment syntax. See [#222](https://github.com/day8/re-com/pull/222).
- Fix typeahead not taking external changes into account. See [#206](https://github.com/day8/re-com/pull/206)

## 2.9.0 (2020-08-11)

#### Changed

- Upgrade ClojureScript to [1.10.773](https://github.com/clojure/clojurescript/blob/master/changes.md)
- Upgrade shadow-cljs to 2.10.19
- Upgrade core.async to 1.3.610

#### Fixed

- Fix Reagent deprecation warning
- Fix #212 - Warning/future error in React 16 (also improved the tab-index demo)
- Fix bug where single-dropdown was optimistically updating the model and ignoring any model validation/changes made by the caller (same bug was fixed in input-text a while back: commit ebad92bc)
- Fix invalid markup in dropdown async demo
- Fix "validateDOMNesting(...): div cannot appear as a descendant of p" warning in info-button demo
- Fix #185. Also added other new html attributes and new css styles
- Remove ^:const references as this compile error was appearing in the demo: https://github.com/thheller/shadow-cljs/issues/708
- Fix potential 'Cannot find local Karma!' error on GitHub Actions

## 2.8.0 (2020-03-08)

#### Changed

- Upgrade reagent to 0.10.0

#### Fixed

- Use `reagent.impl.component/component-name` instead of `component-path` which
  has been removed upstream.
- Use `reagent.dom/dom-node` instead of deprecated `reagent.core/dom-node`.

## 2.7.0 (2020-02-14)

#### Fixed

- Default index for `lein dev-auto` HTTP server is now `index_dev.html` instead
  of a 404 error. Thanks to [@mmower](https://github.com/mmower)'s report on
  Clojurians.

#### Changed

- Upgrade reagent to 0.9.1
- Upgrade shadow-cljs to 2.8.80
- Upgrade ClojureScript to 1.10.597
- Upgrade karma to 4.4.1
- Upgrade org.clojure/core.async to 0.7.559
- Upgrade binaryage/devtools to 1.0.0

## 2.6.0 (2019-09-12)

#### Fixed

- Fix typeahead does not take external model changes into account anymore.
  See [#205](https://github.com/day8/re-com/issues/205).

#### Changed

- Migrate to [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html) and
  [lein-shadow](https://gitlab.com/nikperic/lein-shadow)
