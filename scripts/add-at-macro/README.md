You should run this [babashka](https://github.com/babashka/babashka) script on a legacy codebase which uses `re-com`. 

Version 2.13.0 of `re-com` introduced a new `:src` 
debugging feature described here: https://re-com.day8.com.au/#/debug

This script will recursively traverse all the ClojureScript files in an existing codebase, adding `:src (at)` to every 
use of a `re-com` component. Where necessary, it will also modify namespace `requires` to add the `at` macro.

So, existing code like this:
```clojure
[v-box
  :size     "auto"
  :gap      "10px"
  :children [...]]
```

will be changed to
```clojure
[v-box   :src  (at)      ;; <-- this is new
  :size     "auto"
  :gap      "10px"
  :children [...]]
```

(And, before you ask, no, we did not find a way to put the added code on the next line with correct indentation).

This script is clever enough to detect when a component already has an existing `:src (at)` argument, and it will not 
add duplicates. It is also clever enough to not add a duplicate requires for `at`. As a result, it can be run multiple 
times on a codebase.

### To Run This Script

1. Install [babashka](https://github.com/babashka/babashka) `0.3.2` or later by following [these instructions](https://github.com/babashka/babashka#installation).
 
1. Clone re-com's GitHub repository,

   ```
   git clone https://github.com/day8/re-com.git 
   ```
  
2. Navigate to the scripts location
   ```
   cd re-com/scripts/add-at-macro/src/add_at_macro
   ```

3. Run

   If the project using re-com had sources in `../my-project/src`, then run via babashka (aka `bb`):
   ```
   bb core.clj "../my-project/src" 
   ```

4. Inspect, the files in the `src` directory. Notice the updates made. 


### Running The Tests

1. Install [babashka](https://github.com/babashka/babashka) `0.3.2` or later by following [these instructions](https://github.com/babashka/babashka#installation).

1. Assign the variable `(def directory "")` in `./test/add-at-macro/core-test.clj` to the directory containing your
   source files. From step 3 above, say, `../my-project/src`.

2. Run the function `test-script` in tests which will print the changes to be made to console. While `:testing?`
   is true the changes will also not be saved to file which is good for checking changes without saving them.

3. Navigate to the home directory of this script
   ```
   cd re-com/scripts/add-at-macro
   ```
4. To run the tests via babashka run,
   ```sh
   bb test\add_at_macro\test-runner.clj
   ```
 
