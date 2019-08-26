(ns day8.app.dev-preload

"Specify this ns in development profiles :preload where you want

   devtools and dirac like \"dev\" and \"test\"
   Example:
    :compiler {:preloads [devtools.preload day8.<your-project>.dev-preload]}

   When your app starts, see both devtools and dirac install at top of console."
)
  ; TODO re-introduce dirac post shadow-cljs migration.
  ;(:require [dirac.runtime :as dirac]))

;(when ^boolean js/goog.DEBUG
;  (js/setTimeout #(dirac/install! [:repl]) 100)) ; timout workaround for browser.repl exception
