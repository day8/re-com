## N.N.N (YYYY-MM-DD)

> Committed but unreleased changes are put here, at the top. Older releases are detailed chronologically below.

## 2.28.0 (2025-07-10)

#### Added

 - `table-filter`: New UI component for creating filters for tabular data 
 - `selection-list`: `:only-button` and `:show-counter` props
 - `tag-dropdown`: `:only-button` and `:show-counter` props

#### Fixed

 - `daterange`: Fix daterange availability in re-com public API

## 2.27.0 (2025-06-06)

#### Added

- Turned `nested-v-grid` into the one and only `nested-grid` (deleted the old `nested-grid`).
- `nested-grid`: `:show-zebra-stripes?` prop
- `nested-grid`: `:sticky-child?`, `:sticky-top` and `:sticky-left` props

## 2.26.2 (2025-06-05)

#### Changed

- `tree-select-dropdown` fix missing `:counter` part
- `nested-v-grid`: only allow vectors for `:column-tree` & `:row-tree`

#### Fixed

- `*-tabs`: Restored old behavior of the `:style` prop (it applies to an inner part, not the wrapper).

## 2.26.1 (2025-05-30)

#### Fixed

- `vertical-pill-tabs`: made them actually vertical, not horizontal

## 2.26.0 (2025-05-30)

#### Added

- Documentation for parts & themes in the [re-com demo](https://re-com.day8.com.au/#/customization)
- `*-tabs` components: full support for `:parts` and `:theme`

#### Changed

- `*-tabs` components: changed some of the "re-com-*" class names to be more standard

## 2.25.1 (2025-05-14)

#### Added 

- More developer docs, in `/docs`
- `tree-select-dropdown`: full `:parts` support for the `:counter` part.

#### Changed

- Deleted leiningen. Replaced with tools.build

## 2.25.0 (2025-05-14)

#### Added

- `dropdown`: `:offset-x` and `:offset-y` (number) props.

#### Changed

- `dropdown`: made `:style` & `:attr` props apply only to the wrapper part, not the anchor-wrapper.
- `tree-select-dropdown`: made `:style` & `:attr` props apply only to the wrapper part, not the dropdown-anchor-wrapper.

## 2.24.1 (2025-02-19)

#### Fixed

- `alert-box`: Fix styling derived from the `:alert-type` prop. #351 @cerr0s-loanpro

## 2.24.0 (2025-02-10)

#### Added

- All components can accept a vector of strings as their `:class` argument. Nested vectors work, as well (they get flattened).
- `tree-select-dropdown`: Added `:change-on-blur?` prop

## 2.23.1 (2025-01-07)

#### Fixed

- `nested-grid`: Fixed crash when exporting


## 2.23.0 (2025-01-07)

#### Changed

- `nested-grid`: Renamed :header-spacer to :corner-header
- `nested-grid`: Use available flex cross-size (don't shrink to 0)

#### Added

- `nested-grid`: Add `:on-export-corner-header` prop.
- `popover`: Add :optimize-position? prop
- `dropdown`: Add :chosen-single part
- `tree-select`: When multiple items with the same id are selected, the anchor label now only shows one.
- `nested-grid`: Add :on-init-export-fn
- `popover-content-wrapper`: Added `:optimize-position?` prop.

## 2.22.11 (2024-12-01)

#### Changed

- `dropdown`: Now uses fixed positioning to position the body

#### Fixed

- `popover`: was missing code to apply quite a number of :parts overrides, for both :style and :attr
- `nested-grid`: Fix ellipsis overflow for row-headers
- `error-modal`: Fix react errors when x-button is mounted

#### Added

- Argument validation now gives a warning when you use a camel-cased prop key.

## 2.22.8 (2024-10-10)

#### Changed

- `nested-grid`: Adjust flex-child style & internal layering

## 2.22.6 (2024-10-08)

#### Fixed

- `nested-grid`: Fixed overflow behavior when behaving as a flex item.
- `nested-grid`: Fixed `:remove-empty-column-space?` and `:remove-empty-row-space?` props

## 2.22.5 (2024-10-03)

#### Fixed

- `nested-grid`: Fixed sticky-top of row-headers when export-button is hidden


## 2.22.4 (2024-10-03)

#### Fixed

- `nested-grid`: restored missing style for the cell-wrapper part


## 2.22.3 (2024-10-03)

#### Fixed

- `nested-grid` alt-key debugging actually uses the alt-key (not ctrl).

## 2.22.2 (2024-10-03)

#### Added

- New closure-define, `re-com.config/debug-parts?` (boolean): overrides goog.DEBUG to enable some debug features.
  Currently, this only affects nested-grid. You can alt-click any cell to log its props.
- New closure-define, `re-com.config/log-format` (string): how to log. Options:
  - "js": js console (default)
  - "pr-str" plain-text, pr-str
  - "pretty" plain-text, pprint

#### Fixed

- `nested-grid` - Fix `:show-export-button?` prop.
- `nested-grid` - Fix alignment when declared in column-header
- `nested-grid` - Fix sticky-top when `:sticky?` is true

## 2.22.1 (2024-10-01)

#### Added

- `nested-grid` - alt-click a cell while in DEBUG mode to print its `:column-path` and `:row-path`.

## 2.22.0 (2024-09-28)

#### Changed

- `single-dropdown` - No longer aligns itself at the beginning of a flex container (i.e., removed ``align-self: flex-start`).
- `nested-grid` - Row-header labels are now sticky within their grid areas.

#### Added

- `nested-grid` - `:sticky?` prop


## 2.21.30 (2024-09-13)

#### Changed

- `nested-grid` is now a flex item, taking up as much vertical and horizontal space as possible.

#### Added

- `nested-grid` - `:remove-empty-row-space?` and `:remove-empty-column-space?` props.


## 2.21.29 (2024-09-12)

#### Fixed

- `nested-grid` - Improved scroll performance with large grids


## 2.21.28 (2024-09-09)

#### Changed

- `nested-grid` - now maximizes its vertical flex space by default.


## 2.21.27 (2024-09-05)

#### Fixed

- Reverted `tree-select-dropdown`. Identical to 2.21.25.


## 2.21.26 (2024-09-05)

#### Changed
- `tree-select-dropdown` - Dynamic sizing WIP.

## 2.21.25 (2024-09-05)

#### Changed

- `nested-grid` - Export button stays mounted, even when `:show-export-button?` is off.
  This is useful when automating the export behavior by targeting the button with a click event.
- `nested-grid` - Exports now make use of the `:cell-value` function by default.
- `nested-grid` - `:on-export-column-header` now accepts a `:column-path` prop (same for `:on-export-row-header`).


## 2.21.24 (2024-09-05)

#### Changed

- `tree-select-dropdown` - Renamed most of the parts (still unstable).

#### Fixed

- `nested-grid` - Fixed missing cell borders
- Fixed parts system not working in some alpha components.

## 2.21.23 (2024-09-02)

#### Added
- `nested-grid` - `:theme-cells?` prop, useful to optimize render performance.

#### Fixed
- `nested-grid` - Optimized performance.

## 2.21.22 (2024-08-29)

#### Fixed
- `nested-grid` - Header grid sizes are more correct.

## 2.21.21 (2024-08-22)

#### Fixed
- `nested-grid` - nested headers render correctly again.


## 2.21.20 (2024-08-21)

#### Added
- `nested-grid` - More parts, better themeing support
- `nested-grid` - optional `:align` key for column-specs.

#### Fixed

- `nested-grid` - resizing columns works again (without rendering every cell).

## 2.21.19 (2024-08-19)

#### Added
- `single-dropdown` - `:drop-direction` prop. Overrides any behavior which would position the body dynamically. `:up` or `:above` positions the body above the anchor, `:down`, `:dn` or `:below` positions it below.
- `nested-grid` - optional `:align-column` & `:align-column-header` keys for column-specs.

## 2.21.18 (2024-08-09)

#### Fixed
- `single-dropdown` - `:can-drop-above?` now works again.

## 2.21.17 (2024-08-07)

#### Added
- `dropdown` - New `:show-backdrop`? prop. Defaults to `nil`.

#### Changed

- `dropdown` - The `:backdrop` part is now purely visual. Clicking outside the anchor or body still closes the dropdown. Instead of `:backdrop`, a global event handler now handles this behavior.

## 2.21.16 (2024-08-05)

#### Added

- `datepicker-dropdown` - `:parts` can now target the `:anchor-label` part.
- `datepicker-dropdown` - Added an optional `:date-cell` argument. This is one of the new "part" arguments, acceping a string, a hiccup or a reagent component function.

## 2.21.15 (2024-07-26)

#### Changed

- `dropdown` - Removed `:line-height` style from the theme. `:line-height` was left-over from old bootstrap css. Bootstrap used it to achieve a semblance of vertical centering of text in the anchor. Since we use a re-com box, we can do vertical centering in a nicer way.

- `dropdown` - More consistent handling of width/height props. `:width` applies to both anchor and body, while `:height` applies only to the body. You can override this behavior for specific parts using `:anchor-width`, `:anchor-height`, `:body-width` and `:body-height`.

## 2.21.14 (2024-07-25)

#### Added

- `dropdown` - `:height`, `:body-height` & `:body-width` props

## 2.21.13 (2024-07-23)

#### Added

- New `error-modal` component (alpha).


## 2.21.12 (2024-07-20)

#### Fixed

- `dropdown` - Always drops down when not clipping. Before, this component would choose a body position closest to the vertical center of the viewport. Now, it chooses a position below the anchor, unless that would cause the body to clip.
- `simple-v-table` - Renamed `:row-export-fn` to `:on-export-row-label-fn`.

## 2.21.11 (2024-07-11)

#### Fixed

- `tree-select-dropdown` - Fixed a runtime error when `:expanded-groups` is not passed.

## 2.21.10 (2024-07-11)

#### Changed

- `tree-select-dropdown` - Now the anchor label uses a `:span`, and doesn't underline on hover. 


## 2.21.9 (2024-07-10)

#### Changed

- `tree-select-dropdown` - Tweaked styling, expanded the `:label` part (alpha).


## 2.21.8 (2024-07-09)

#### Added

- `tree-select-dropdown` - Added `:on-group-expand`, `:expanded-groups` & `:show-only-button?` props.

## 2.21.7 (2024-07-07)

#### Added

- `tree-select-dropdown` - Added `:required?`, `:show-reset-button?`, `:on-reset`, `:body-header` & `:body-footer` props.`


## 2.21.6 (2024-06-29)

#### Fixed

- `single-dropdown` - Fixed disabled style. Now disabled dropdowns appear greyed-out again.
- `tree-select` - Fixed expander buttons not showing up in some browsers. Now they use svg, not unicode chars.
- `single-dropdown` - Adjusted triangle indicator. Now uses svg, not a sprite.


## 2.21.5 (2024-06-10)

#### Fixed

- `tree-select` - Fixed non-responsive behavior when passing `:model` as value, (not a reagent/atom).


## 2.21.4 (2024-05-14)

#### Fixed

- `simple-v-table` - Removed default header-labels, since they show up badly in existing apps. (#340)


## 2.21.3 (2024-05-09)

#### Fixed

- Crash when declaring :sort-by for simple-v-table headers in top-left section

## 2.21.2 (2024-05-09)

#### Fixed

- Invisible icons & broken execution in simple-v-table headers (#339)


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
