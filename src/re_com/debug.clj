(ns re-com.debug)

(defmacro src-coordinates
  []
  (select-keys (meta &form) [:file :line]))