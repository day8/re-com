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
 
2. Clone re-com's GitHub repository,

   ```
   git clone https://github.com/day8/re-com.git 
   ```
  
3. Navigate to the scripts location
   ```
   cd re-com/scripts/add-at-macro
   ```

4. Run

   If the project using re-com had sources in `../my-project/src`, then run via babashka (aka `bb`):
   ```
   bb add_at_macro.clj "../my-project/src" 
   ```

5. Inspect, the files in the `src` directory. Notice the updates made.
   
   #### Tip
   
   For run, (not test command in the next section) The `bb` command also takes the following extra command line 
   arguments after the directory.
   1. `--verbose` or `-v`. When this is passed, the changes the script makes are printed to console. Example command
   ```sh 
   bb add_at_macro.clj "../my-project/src" --verbose
   ```
   
   2. `--testing` or `-t`. When this is passed, the files that the script edits are not saved to disk but printed to console
   ```sh 
   bb add_at_macro.clj "../my-project/src" --testing
   ```
   Note, When `-testing` is passed, `-verbose` is always true.
   
   3. `--help` or `-h`. Print the help menu. Example command
   ```sh
   bb add_at_macro.clj --help
   ```



### Running The Tests

1. Install [babashka](https://github.com/babashka/babashka) `0.3.2` or later by following [these instructions](https://github.com/babashka/babashka#installation).

2. Navigate to the home directory of this script
   ```
   cd re-com/scripts/add-at-macro
   ```
3. To run the tests via babashka run,
   ```sh
   bb test\test-runner.clj
   ```
 
