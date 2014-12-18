# Setting up figwheel

Figwheel is a "Leiningen plugin that pushes ClojureScript code changes to the client".

Basically it provides both auto-compile and auto-browser-update, which makes our development cycle faster and more efficient.It also has a nice "heads-up" display to display compiler warning and errors. 

Basic instructions for setting it up can be found on the home page: [bhauman/lein-figwheel](https://github.com/bhauman/lein-figwheel).

The following steps are required to get it going in a reagent environment:

## Get Latest Leiningen

At time of writing, the latest is v2.5.0. You can get it from here:

[https://github.com/technomancy/leiningen#installation](https://github.com/technomancy/leiningen#installation)

Depending on how you originally installed it (I used chocolatey and that was not a great decision in the end), 
you should just be able to use the following command:

    lein upgrade

This requires acces to either `curl` or `wget`, so one of them must be available in the path.


## Modify project.clj 

 - Make sure the ClojureScript module is `[org.clojure/clojurescript "0.0-2197"]` or later (in the root 
   level `:dependencies` section).
 - Make sure the cljsbuild module is `[lein-cljsbuild "1.0.3"]` or later (in the `:dev` profile `:plugins`section).
 - Add `[figwheel "0.1.7-SNAPSHOT"]` to both your `:dev` profile `:dependencies` and `:plugins` sections.

Example `:dev` profile:

    :profiles {:debug {:debug true}
              :dev    {:dependencies [[figwheel "0.1.7-SNAPSHOT"]]
                       :plugins      [[lein-cljsbuild "1.0.3"]
                                      [lein-figwheel "0.1.7-SNAPSHOT"]]}}

 - Note that figwheel will only work with builds that have `:optimization` set to `:none`.




## X

Blah