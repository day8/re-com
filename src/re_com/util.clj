(ns re-com.util)

(defmacro assert* [val test]
  `(let [result# ~test]
     (when (not result#)
       (throw (js/Error. (str "Test failed: " (quote ~test) " for " (quote ~val) " = " ~val))))))
