# re-com

A ClojureScript library of UI components, built on top of Dan Holmsand's wonderful
[Reagent](http://reagent-project.github.io)
which, in turn, is a layer over Facebook's trail blazing [React](http://facebook.github.io/react).


Re-com has:

* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.
  (in Reagent terms these are `components`)
* layout `components` which organise widgets vertically and horizontally, within
  splitters, etc. Plus `components`
  which put borders around their children. Layouts can nest. Layouts are based on flexbox.
* a largely [Bootstrap](http://getbootstrap.com/) looked, mixed with
  some [Material Design Icons](http://zavoloklom.github.io/material-design-iconic-font/icons.html).

In short, re-com has the stuff you'd need to build a desktop-class app.

If you decide to use re-com, consider also using [re-frame](https://github.com/Day8/re-frame)
(an MVC-ish framework).  The two dovetail well, although both can be used independently --
for example, re-com's demo program does not use re-frame.


## Warning: re-com Probably Isn't For You (yet)

We build desktop-class apps to run in controlled chrome environments like
[node-webkit](https://github.com/rogerwang/node-webkit) 
and [atom-shell](https://github.com/atom/atom-shell).

If you are like us, or you work on Intranet apps where you can mandate a modern browser,
re-com could be ideal for you. Otherwise you might have to wait 9 months.


**Here's the thing:**  the entire layout side of this library plus a few of the widgets
rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/)
which [only works on modern browsers](http://caniuse.com/#feat=flexbox).

And even when it comes to modern browsers there might be teething issues. Based on 5 minutes of
testing once a month, re-com seems to work on IE11 and Safari,
but Firefox has the performance of a snail on performance reducing drugs. Nested flexbox
containers bring Firefox to its knees.  So, yeah, "teething issues".

So, for the next year, this library would be a poor fit if you're targeting the
retail web, which is rife with flexbox-less wastelands like IE10 and IE9. Having said that,
the end is coming for various old IE browsers much more quickly than in the
past - [come Jan 12th 2016 most corporates will be forced across onto IE11](http://blogs.msdn.com/b/ie/archive/2014/08/07/stay-up-to-date-with-internet-explorer.aspx)
 
I can also confirm that none of the components have been designed with mobile in mind, and
that there's no attempt to handle media queries.  I said we had a desktop focus, right?

Neither have we been focused on code size because other design goals have
taken precedence.  Our main demo app which includes every component, plus all demo
code comes to about 167K compressed and using `:optimzations` `:advanced` (700K pre compress).
That number includes ReactJS plus the ClojureScript libs and runtime. Everything.

## So, Without Ado Being Any Furthered ...

Still here?  Good. I'm glad we got all that negative stuff out the way.  You're
going to like re-com.

So, you do have Chrome handy, right?  Start by looking
at [the demo](http://re-demo.s3-website-ap-southeast-2.amazonaws.com).

## Named Parameters

re-com components take `named parameters`, rather than `positional parameters`.

So, when you use a re-com component like `checkbox`, you **will not** be asked to use positional parameters like this:

```Clojure
[checkbox "Show Status Icon?" status-icon?  (fn [new-val] (reset! status-icon? new-val))]
```

**Instead**, re-com requires `named parameters` like this:

```Clojure
[checkbox
  :label     "Show Status Icon?"
  :model     status-icon?      ; a ratom
  :on-change (fn [new-val] (reset! status-icon? new-val))]
```

Notice how each parameter value has a short, leading keyword name. The first version, using `positional parameters`,
was more concise, the 2nd using `named parameters` is more explicit. Both have their merits - a situation which
invariably leads to highly contested Religious wars.  We've gone with `named parameters` in the API because:

1. the code using the library seems easier to read (despite being longer)
2. as a result the code seems more understandable - something we value above all other considerations.
3. optionality  -  not all parameters have to be supplied, defaults can be introduced
4. flexibility - new parameters are easily added

Read a further analysis [here](https://clojurefun.wordpress.com/2012/08/13/keyword-arguments-in-clojure/)

## Parameter Validation

We often make mistakes and get our re-com parameters wrong. Our sausage fingers type  `onmouseover` instead of `on-mouse-over`, or `centre` rather than `center`, or we pass in a string when it should have been a keyword. 

re-com tries to catch these kinds of mistakes. Every re-com component has a spec which contains details on each parameter, like name, data type, validation fucntions, default value and so on.

In dev mode, all component parameters are validated. In production, that overhead is removed. 

If a problem is found, helpful (we hope) messages are displayed in the console. 

re-com uses `goog.DEBUG` to determine dev builds from production builds.  For dev builds, do nothing.  For production builds, in your `project.clj` ensure we have this in the build target:

```Clojure
:closure-defines {:goog.DEBUG false}
```

## Navigating The Source

If you look in the `src` directory, you'll notice that it has two sub-directories:

  - re-com - the library
  - re-demo - the demo app

1. Getting And Running The Demo

   ```shell
   git clone https://github.com/Day8/re-com.git
   ```
   
   ```shell
   cd re-com 
   ```
   
   ```shell
   lein run
   ```

  This will run the demo, by doing: 
  - a clean 
  - a compile 
  - a load of the right `index.html` into your default browser


1. Debugging The Demo

   Via  [figwheel](https://github.com/bhauman/lein-figwheel):
   ```shell
   lein debug
   ```

  This will:

 - clean 
 - start the [figwheel](https://github.com/bhauman/lein-figwheel) server & compiler  (a terminal window will be started)
 - load the right `index.html` (specialised for figwheel use)
 - start a ClojureScript repl in the terminal window (actually, figwheel does this for you)

  Your Process:

  - the initial load of `index.html` will fail because the figwheel compile hasn't yet finished. 
  - be patient - the initial compile might take anything from 20sec to 3 mins depending on how many dependencies need to be downloaded (how many are not yet in your local Maven repo).
  - keep an eye on the terminal started by figwheel, waiting for a green `Successfully compiled` message, at which point, figwheel will immediately move on and try to start the repl.  
  - In response, you should refresh to HTML page. This refresh is needed for figwheel to complete the repl kick-off.
  - to quit figwheel and stop the server/compiler, type ` :cljs/quit` into the repl. 


1. Run The (Modest) Tests
 
   ```shell
   lein run-test
   ```

  This will:

- clean 
- compile the tests 
- load the required `test.html` into your default browser, so you can see the results.

1. Debug the tests:

   ```shell
   lein debug-test
   ```

  Unlike `debug` which uses figwheel, `debug-test` uses cljsbuild's `auto` for recompilation.

## Using re-com In Your Apps

First, add these dependencies in your project.clj:

```Clojure
:dependencies [
  ...
  [reagent "0.5.0"]
  [re-com "0.2.2"]
]
```

Note that Reagent comes bundled with the matching version of the React JavaScript library so you don't need to include that anywhere.

In your index.html:

Add Bootstrap, normally via a CDN:

```html
<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.css">
```

Add the two CSS files needed by re-com (they are in this repo):
```html
<link rel="stylesheet" href="resources/css/material-design-iconic-font.min.css">
<link rel="stylesheet" href="resources/css/re-com.css">
```

TODO: expand this. Check. XXX

## Leaky Abstractions

The layout side of re-com is built on top of Flexbox, but our abstractions are leaky.  At some point
you're going to have to do the [Flexbox tutorials](http://css-tricks.com/snippets/css/a-guide-to-flexbox/) to understand what's going on.

This is compounded by the viral nature of Flexbox. We've found that it's use viral. It's reach tends to spread.

## The Missing Parts

* tree  (not hard but haven't needed one yet)
* accordion
* big grid. HTML is good at small grids, so no problem there. But when the number of
rows gets huge, you need a widget that does virtual rows. Otherwise there's just too many DOM nodes.  Can we use [Fixed Data Tables for React](http://facebook.github.io/fixed-data-table)?
* Add a timed alert box which appears for a set period of time. This would probably be absolutely positioned over the UI and then fade away after the set time expires.
* drag and drop   (we have somewhat sorted this).
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move?


### License

Copyright © 2015 Michael Thompson

Distributed under The MIT License (MIT) - See LICENSE.txt

