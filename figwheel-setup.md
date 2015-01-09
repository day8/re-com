# Setting up figwheel for a Reagent application

Figwheel is a "Leiningen plugin that pushes ClojureScript code changes to the client".

Basically it provides both auto-compile and auto-browser-update, which makes our development cycle faster and more efficient. It also has a nice "heads-up" display to show compiler warning and errors. 

Basic instructions for setting it up can be found on the home page: [bhauman/lein-figwheel](https://github.com/bhauman/lein-figwheel).

The following steps are required to get it going in a Reagent environment:

## Get Latest Leiningen

At time of writing, the latest is v2.5.0. You can get it from here:

[https://github.com/technomancy/leiningen#installation](https://github.com/technomancy/leiningen#installation)

Depending on how you originally installed it (I used chocolatey and that was not a great decision in the end), 
you should just be able to use the following command:

    lein upgrade

This requires acces to either `curl` or `wget`, so one of them must be available in the path.


## Dependencies and plugins 

Modify project.clj:

 - Make sure the ClojureScript module is `[org.clojure/clojurescript "0.0-2197"]` or later (in the root 
   level `:dependencies` section).

 - Make sure the cljsbuild module is `[lein-cljsbuild "1.0.3"]` or later (in the `:dev` profile `:plugins` section).

 - Add `[figwheel "0.1.7-SNAPSHOT"]` to both your `:dev` profile `:dependencies` and `:plugins` sections.

Example `:dev` profile:

```Clojure
:profiles {:debug {:debug true}
           :dev   {:dependencies [[figwheel "0.1.7-SNAPSHOT"]]
                   :plugins      [[lein-cljsbuild "1.0.3"]
                                  [lein-figwheel "0.1.7-SNAPSHOT"]]}}
```


## cljsbuild spec

Modify project.clj:

 - Here is an example of a build spec that will work with figwheel:   

```Clojure
:cljsbuild {:builds [{:id "demo"
                      :source-paths ["src"]
                      :compiler     {:output-to     "run/resources/public/demo.js"
                                     :source-map    "run/resources/public/demo.js.map"
                                     :output-dir    "run/resources/public/demo"
                                     :optimizations :none
                                     :pretty-print  true}}]}
```

Notes:

 - Currently, because of the static web server limititation, figwheel requires that the output of the compile be in the `run/resources/public/{appname}` folder.

 - It also allows `dev-resources/public`. If you choose this, make sure you don't also use the one above in another build as they will conflict with each other.

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

Every time figwheel reloads new compiled code to the browser, it calls the function specified at `:jsload-callback`. Calling `reagent/force-update-all` will force Reagent to rerender everything.

Note: Here are some [tips](https://github.com/bhauman/lein-figwheel#writing-reloadable-code) for writing reloadable code.


## Watching/reloading CSS changes to the browser

figwheel can monitor changes to CSS files. It needs to be configured in the [Server configuration] section in project.clj.

```Clojure
:figwheel {:css-dirs ["run/resources/public/resources/css"]}
```

Every time figwheel reloads updated CSS to the browser, it calls the function specified at `:on-cssload`. So, you would need to add a callback fn to the `fw/start` function call, like so:

```Clojure
(fw/start {:jsload-callback (fn [] (reagent/force-update-all))
           :on-cssload      (fn [] (reagent/force-update-all))})
```

**HOWEVER, calling reagent/force-update-all at this point does NOT show the updated CSS in the browser and I have not worked out how to do it yet, so this is an outstanding tasks.** 


## Running your app under figwheel

Simply use the following command line command:

```
lein figwheel {build-id}
```

This will start a server that listens to port 3449 (can me modified in [Server configuration]).

You can [use your own server](https://github.com/bhauman/lein-figwheel#using-your-own-server) if required.

Finally, go to the browser and enter:

```
http://localhost:3449
```

which will run index.html by default. Add the name of your html file if it's different.

Now you have the luxury of seeing your code recompile and reload automatically every time you save it.

The other nice thing is that if you get a warning or an error, figwheel will inform you using the "heads-up" display and NOT load the code. 


## TODO

 - Override the `run/resources/public` folder.
	- Can only override the `public` part.
 - Reflect CSS changes immediately.
	- Figwheel does its part. 
	- Reagent doesn't update.
	- [garden](https://github.com/noprompt/garden) might help?
 - Prevent call to `fw/start` when not debugging. 
    - Project.clj variables? {:debug? false}


[Server configuration]:https://github.com/bhauman/lein-figwheel#server-configuration
