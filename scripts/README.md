Script to import `at` macro and add it to re-com components.

### Prerequisites
This project uses [lein exec](https://github.com/kumarshantanu/lein-exec) which might need to be pre-installed.
To install, follow the directions in the README.md found in the project's GitHub repository.

### Run script
1. Clone re-com's GitHub repository,
```
git clone https://github.com/day8/re-com.git 
```
2. Navigate to the scripts location
```
cd re-com/scripts/ 
```
3. Say, for example you have a project that uses re-com with sources in `../my-project/src`
4. Run
```
lein run "../my-project/src" 
``` 
5. The files in the `src` directory will be scaned and modified with `:src (at)` annotations


### Test script
1. Assign the variable `(def directory "")` in `./test/scripts/core-test.clj` to the directory containing your
   source files. From step 3 above, say, `../my-project/src`.
2. Run the function `test-script` in tests which will print the changes to be made to console. While `:testing?`
   is true the changes will also not be saved to file which is good for checking changes without saving them.
3. To run the tests via the repl open a repl
```
lein repl
```   
4. Run the following at the repl
```clojure
(require '[at-macro.core-test :refer [runner]])
(runner <directory>)
```
