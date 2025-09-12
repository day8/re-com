((nil . ((cider-clojure-cli-aliases        . ":demo")
         (cider-preferred-build-tool       . clojure-cli)
         (cider-default-cljs-repl          . custom)
         (cider-custom-cljs-repl-init-form . "(do (require 'day8.dev) (day8.dev/cljs-repl :demo))")
         (eval . (progn
                   (make-variable-buffer-local 'cider-jack-in-nrepl-middlewares)
                   (add-to-list 'cider-jack-in-nrepl-middlewares
				"shadow.cljs.devtools.server.nrepl/middleware"))))))
