### WARNING
Do NOT use this script. It has been created in preparation for a future re-com change. No version of re-com 
(including master branch) is yet compatible with the changes made by this script.

This script will recursively traverse all the ClojureScript files in an existing codebase, formatting keyword 
args in every use of a re-com component to map/hiccup syntax.

So, existing code like this:
```clojure
[v-box
  :size     "auto"
  :gap      "10px"
  :children [...]]
```

will be changed to
```clojure
[v-box  {                       ;; <-- this is wrapped in a map
  :size     "auto"
  :gap      "10px" } [...]]     ;; <-- The child is added outside the map
```

(And, before you ask, no, we didn't find a way to preserve indentation after the formatting).

This script is clever enough to detect when a component is already in hiccup like structure and as a result, it can 
be run multiple times on a codebase.

### To Run This Script

1. Install [babashka](https://github.com/babashka/babashka) 0.3.2 or later by following [these instructions](https://github.com/babashka/babashka#installation).

2. Clone re-com's GitHub repository,

   ```
   git clone https://github.com/day8/re-com.git 
   ```

3. Navigate to the scripts location
   ```
   cd re-com/scripts/kwargs-to-map/ 
   ```

4. Run the script with babashka (aka `bb`)

   If the project using re-com had sources in `../my-project/src`, then run:
   ```
   bb kwargs_to_map.clj "../my-project/src" 
   ```

5. Inspect, the files in the `src` directory. Notice the updates made.

#### Tip

For run, (not test command in the next section) The `bb` command also takes the following extra command line 
arguments after the directory.

1. `--verbose` or `-v`. When this is passed, the changes the script makes are printed to console. Example command
   ```sh 
   bb kwargs-to-map.clj "../my-project/src" --verbose
   ```

2. `--testing` or `-t`. When this is passed, the files that the script edits are not saved to disk but printed to console
   ```sh 
   bb kwargs_to_map.clj "../my-project/src" --testing
   ```
   Note, When `-testing` is passed, `-verbose` is always true.

3. `--help` or `-h`. Print the help menu. Example command
   ```sh
   bb kwargs_to_map.clj --help
   ```

### Test script
1. Install [babashka](https://github.com/babashka/babashka) 0.3.2 or later by following [these instructions](https://github.com/babashka/babashka#installation).

2. Navigate to the scripts location
   ```
   cd re-com/scripts/kwargs-to-map/ 
   ```

3. Run the test script with babashka,
   ```
   bb test/test-runner.clj 
   ```
 
### Operation
The following section documents how the script edits your files
- The re-com components, `p` and `p-span` do not receive any editing
- The re-com components below are considered to have the child which is the indicated key
  - `modal-panel`        -> `:child`
  - `alert-box`          -> `:body`
  - `box`                -> `:child`
  - `scroller`           -> `:child`
  - `border`             -> `:child`
  - `checkbox`           -> `:label`

During editing, the keyword args are added to a map and then the child is conjoined after.
For example the `box` below,
```Clojure
[re-com/box :src (at)
  :size "1"
  :child [border
          :border "1px dashed red"
          :child  [box :height "100px" :child "Hello"]]
  :align :center]
```

is transformed to 
```Clojure
[re-com/box { :src (at)                                                        <== the map is added
  :size "1"
  :align :center } [border {                                                     <== child is conjoined after the map with the same effects
          :border "1px dashed red" } [box { :height "100px" } "Hello"]]]
```

The space is only preserved in the first child i.e (`box`). 

- The following re-com components are assumed to have the key `:children`
  - `h-box`
  - `v-box`

Their keyword arguments are added to a map and then the children are conjoined recursively after the map. 
For example the `v-box` below
```Clojure
[v-box :src (at)
 :size    "1"
 :justify  :center
 :children [[re-com/v-box :src (at)
             :size     "1"
             :children [[label :title "Moved"]]
             :align    :center]
```

is transformed to
```Clojure
[v-box { :src (at)                                                         <== map is added
 :size     "1"
 :justify  :center } [re-com/v-box { :src (at)                   <== children are added after the map
             :size     "1"
             :align    :center } [label :title "Moved"]]
```
Spacing is only preserved in the first child.


- The following are treated as splits
  - `h-split`
  - `v-split`

Their keys `:panel-1` and `panel-2` are conjoined in the right order outside the map.
For example consider the `h-split`
```Clojure
[rc/h-split :src (at)
 :panel-1 [left-panel]
 :panel-2 [right-panel]
 :size    "300px"]
```

It is transformed to
```Clojure
[rc/h-split { :src (at)                                                                      <== the map is added
 :size    "300px" } [left-panel] [right-panel]]                                 <== the panels are added after the map
```
Space is preserved within the map

- All other components are treated as to have no special reformatting and all their keyword arguments are wrapped in a map preserving whitespace.
For example a button like
```Clojure
[button
 :label    "This is the label"
 :style    {:color "red"}
 :on-click nil
 :class    "class-name"]
```

is edited as follows
```Clojure
[button {                                                        <== the map is added
 :label    "This is the label"
 :style    {:color "red"}
 :on-click nil
 :class    "class-name" } ]
```
