## Unreleased

#### Fixed

- Default index for `lein dev-auto` HTTP server is now `index_dev.html` instead
  of a 404 error. Thanks to [@mmower](https://github.com/mmower)'s report on
  Clojurians.

#### Changed

- Upgrade reagent to 0.9.1
- Upgrade shadow-cljs to 2.8.80
- Upgrade ClojureScript to 1.10.597
- Upgrade karma to 4.4.1
- Upgrade org.clojure/core.async to 0.6.532
- Upgrade binaryage/devtools to 1.0.0

## 2.6.0 (2019-09-12)

#### Fixed

- Fix typeahead does not take external model changes into account anymore.
  See [#205](https://github.com/day8/re-com/issues/205).

#### Changed

- Migrate to [shadow-cljs](https://shadow-cljs.github.io/docs/UsersGuide.html) and
  [lein-shadow](https://gitlab.com/nikperic/lein-shadow)
