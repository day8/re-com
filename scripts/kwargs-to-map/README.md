You should run this script on a legacy codebase to convert keyword arguments in re-com components to a map.


This script will recursively traverse all the ClojureScript files in an existing codebase, formatting order of keyword 
args in every use of a re-com component.

So, existing code like this:
```clojure
[v-box
  :size     "auto"
  :gap      "10px"
  :children [...]]
```

will be changed to
```clojure
[v-box  { :src  (at)      ;; <-- this is new
  :size     "auto"
  :gap      "10px" } [...]]
```

(And, before you ask, no, we did not find a way to put the added code on the next line with correct indentation).

This script is clever enough to detect when a component is already in hiccup like structure and as a result, it can 
be run multiple times on a codebase.

### Prerequisites

This project uses [lein exec](https://github.com/kumarshantanu/lein-exec) which might need to be pre-installed.
To install, follow the directions in the README.md found in the project's GitHub repository.

### To Run This Script

1. Clone re-com's GitHub repository,

   ```
   git clone https://github.com/day8/re-com.git 
   ```

2. Navigate to the scripts location
   ```
   cd re-com/scripts/kwargs-to-map/ 
   ```

3. Run

   If the project using re-com had sources in `../my-project/src`, then run:
   ```
   lein run "../my-project/src" 
   ```

4. Inspect, the files in the `src` directory. Notice the updates made.


### Test script
1. Assign the variable `(def directory "")` in `./test/kwargs-to-map/core-test.clj` to the directory containing your
   source files. From step 3 above, say, `../my-project/src`.

2. Run the function `test-script` in tests which will print the changes to be made to console. While `:testing?`
   is true the changes will also not be saved to file which is good for checking changes without saving them.

3. To run the tests via the repl open a repl
   ```sh
   lein repl
   ```
4. Run the following at the repl
   ```clojure
   (require '[kwargs-to-map.core-test :refer [runner]])
   (runner <directory>)
   ```
 
