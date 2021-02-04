## Unreleased

**IMPORTANT:** You must upgrade your copy of `re-com.css` when upgrading to this release.

#### Added

- Add `datepicker` year navigation. **Breaking** changes to `datepicker` navigation layout. See 'Parts' section of
  https://re-com.day8.com.au/#/date
- Add `re-com.config` ns
- Add `re-com.config/debug?` as an alias for the closure define `js/GOOG.debug`
- Add `re-com.config/force-include-args-desc?` as a way to force inclusion of arg description data structures in
  release builds for the demo site documentation generation
- Add `re-com.config/include-args-desc?` and use it to elide arg description data structures. This should lead to
  smaller production builds for user's of re-com, and faster app startup.

#### Changed

The following `v-table` and `simple-v-table` breaking changes are only relevant to the Day8 team...

- **Breaking**: Rename `:max-table-width` arg of `v-table` and `simple-v-table` to `:max-width`.
- **Breaking**: Rename `:valign` in `simple-v-table` `:columns` specification to `:vertical-align` to
match the associated CSS property. Fix the documentation and demos of the same.
- **Breaking**: Rename `:scroll-cols-into-view` arg of `v-table`  to `:scroll-cols-into-view`.
- **Breaking**: Rename `:col-header-renderer` arg of `v-table`  to `:column-header-renderer`.
- **Breaking**: Rename `:col-header-height` arg of `v-table`  to `:column-header-height`.
- **Breaking**: Rename `:col-header-selection-fn` arg of `v-table`  to `:column-header-selection-fn`.
- **Breaking**: Rename `:style-parts` arg of `v-table`  to `:parts`.
   - Then wrap any style maps in a map with a key of `:style` 
   - For example: 
```clojure
:style-parts {:v-table {:background-color "lightgrey"}}

;; becomes...

:parts {:wrapper {:style {:background-color "lightgrey"}}}

;; notice, the above also included a :v-table => :wrapper conversion 
```
- **Breaking**: `:attr-parts` arg of `v-table` has been removed.
   - You need to incorporate it into the `:parts` arg
   - For example:
```clojure
:attr-parts {:v-table-top-left {:on-click (handler-fn ...)}}

;; becomes...

:parts {:top-left {:attr {:on-click (handler-fn ...)}}}

;; notice, the above also included a :v-table-top-left => :top-left conversion 
```
- **Breaking**: Rename any `:parts` args that match  `:v-table-*` to `:*` (in other words, remove `v-table-`).
- **Breaking**: Rename any `:parts` args that match  `:*-col-*` to `:*-column-*`.


#### Removed

- Remove the `tag-dropdown` arguments `:tag-width`, `:tag-height`, `:tag-comp` and `:on-tag-click`.

## 2.12.0 (2012-01-23)

#### Fixed

- Fix v-table as `:model` deref was broken in last release.

#### Removed

- Remove `re-com.misc` ns. Replaced by `re-com.checkbox`, `re-com.input-text`, `re-com.radio-button`, `re-com.slider`,
  `re-com.progress-bar` and `re-com.slider`.

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
