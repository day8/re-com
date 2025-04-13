(ns re-demo.utils)

(defmacro with-src [body]
  `[[rdu/zprint-code '~body]
      ~body])
