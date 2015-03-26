# Setting up figwheel for a Reagent application

Figwheel is a "Leiningen plugin that pushes ClojureScript code changes to the client".

Basically it provides both auto-compile and auto-browser-update, which makes our development cycle faster and more efficient. It also has a nice "heads-up" display to show compiler warning and errors. 

Basic instructions for setting it up can be found on the home page: [bhauman/lein-figwheel](https://github.com/bhauman/lein-figwheel).

The following steps are required to get it running in a Reagent environment:

## Install Latest Leiningen

At time of writing, the latest is v2.5.1. You can get it from here:

[https://github.com/technomancy/leiningen#installation](https://github.com/technomancy/leiningen#installation)

Depending on how you originally installed it (I used chocolatey and that was not a great decision in the end), you should just be able to use the following command:

    lein upgrade

This requires access to either `curl` or `wget`, so one of them must be available in the path or, in my experience, it will fail silently.


## Dependencies and plugins 

Modify project.clj:

 - The ClojureScript module should be `[org.clojure/clojurescript "0.0-2665"]` or later (in the root 
   level `:dependencies` section).

 - The cljsbuild module should be `[lein-cljsbuild "1.0.4"]` or later (in the `:dev` profile `:plugins` section)

    - Note that on upgrading to 1.0.4, support for `lein cljsbuild clean` has been removed. Instead, use `lein clean`. 
    - This requires populating a `:clean-targets` vector in `project.clj` with the paths to clean.   
    - Also note that you may have to adjust the folder names (e.g. cleaning one folder above the original folder spec) as `lein` cleans differently to `cljsbuild`.

 - Add `[figwheel "0.2.1-SNAPSHOT"]` to your `:dev` profile `:dependencies` section.

 - Add `[lein-figwheel "0.2.1-SNAPSHOT"]` to your `:dev` profile `:plugins` section.

Example `:dev` profile:

```Clojure
:profiles {:debug {:debug true}
           :dev   {:dependencies [[figwheel "0.2.1-SNAPSHOT"]]
                   :plugins      [[lein-cljsbuild "1.0.4"]
                                  [lein-figwheel "0.2.1-SNAPSHOT"]]}}
```


## cljsbuild spec

Modify project.clj:

 - Here is an example of a build spec that will work with figwheel:   

```Clojure
:cljsbuild {:builds [{:id "demo"
                      :source-paths ["src"]
                      :compiler     {:output-to     "run/resources/public/compiled/demo.js"
                                     :source-map    "run/resources/public/compiled/demo.js.map"
                                     :output-dir    "run/resources/public/compiled/demo"
                                     :optimizations :none
                                     :pretty-print  true}}]}
```

Notes:

 - Currently, because of the static web server limititation, figwheel requires the output of the compile to be in the `run/resources/public` folder.

 - The `public` part of the folder path can be configued to another name if desired. See [Server configuration]. 

 - Note that figwheel will only work with builds that have `:optimization` set to `:none`.


## Watching/reloading code changes to the browser

The above changes will set up figwheel to watch for changes to any of your cljs/clj source files, whenever they are saved, however, some code needs to be added first:

Add figwheel as a require in your core module:

```Clojure
(:require [figwheel.client :as fw])
```

Then call the following just before your call to `reagent/render`:

```Clojure
(fw/start {:jsload-callback (fn [] (reagent/force-update-all))})
```

Every time figwheel reloads new compiled code to the browser, it calls the function specified at `:jsload-callback`. Calling `reagent/force-update-all` will force Reagent to rerender the changes just pushed to the browser.

Note: Here are some [tips](https://github.com/bhauman/lein-figwheel#writing-reloadable-code) for writing reloadable code.


## Watching/reloading CSS changes to the browser

figwheel can monitor changes to CSS files. It needs to be configured in the [Server configuration] section in `project.clj`.

```Clojure
:figwheel {:css-dirs ["run/resources/public/assets/css"]}
```

Every time figwheel reloads updated CSS to the browser, it calls the function specified at `:on-cssload`:

```Clojure
(fw/start {:jsload-callback (fn [] (reagent/force-update-all))
           :on-cssload      (fn [] (println "css reloaded"))})
```

NOTE that the browser does the reloading of CSS. Reagent does not need to be refreshed, so this setting can be left out altogether. In my experience, previous versions of figwheel did not refresh the browser as advertised.


## Compiling and Running your app under figwheel

Simply use the following command line command:

```
lein figwheel {build-id}
```

This will start a server that listens to port 3449 (can me modified in [Server configuration]).

You can [use your own server](https://github.com/bhauman/lein-figwheel#using-your-own-server) if required.

Finally, load the browser and enter:

```
http://localhost:3449
```

which will run index.html by default. Add the name of your html file if it's different.

Now you have the luxury of seeing your code and CSS changes recompile and reload automatically every time you save changes.

The other nice thing is that if you get a warning or an error, figwheel will inform you using the "heads-up" display and NOT load the code. 


## TODO

 - Override the `run/resources/public` folder.
	- Can only override the `public` part.
 - Prevent call to `fw/start` when not debugging. 
    - Project.clj variables? {:debug? false}


[Server configuration]:https://github.com/bhauman/lein-figwheel#server-configuration
