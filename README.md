# re-com

A ClojureScript library of UI components, built on top of Dan Holmsand's terrific
[Reagent](http://reagent-project.github.io)
which, in turn, is a layer over Facebook's trailblazing [React](http://facebook.github.io/react).

Confirming: that's 100% ClojureScript. We're not wrapping jquery plugins here.

Re-com has:

* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.
  (in Reagent terms these are `components`)
* layout `components` which arrange widgets vertically and horizontally, within
  splitters, etc. Plus `components`
  which put borders around their children. These various pieces can be arbitrarily nested
  to create sophisticated layouts.
* a largely [Bootstrap](http://getbootstrap.com/) looked, mixed with
  some [Material Design Icons](http://zavoloklom.github.io/material-design-iconic-font/icons.html).

In short, re-com attempts to provide the kind of basics you'd need to build a desktop-class app.

If you decide to use re-com, consider also using [re-frame](https://github.com/Day8/re-frame)
(an MVC-ish framework).  Although both can be used independently, they dovetail well.


## Warning: re-com Might Not Be For You (just yet)

We build desktop-class apps to run in controlled browser environments
like [atom-shell](https://github.com/atom/atom-shell).  We know we're dealing with Chrome.

If you are similar, or if you work on Intranet apps where you can mandate a modern browser,
re-com could be ideal for you, right now.

On the other hand, if you target the retail web, you might have to wait till early 2016
(10 months away, at the time of writing).

Why?  **Well, here's the thing:**  the entire layout side of this library plus a few of the widgets
rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/)
which [only works on modern browsers](http://caniuse.com/#feat=flexbox).


Now, the grinding pain and longevity of IE6 has conditioned many to
expect 8, 9 and 10 to hang around forever too.  But, this time around, there's
quite a different dynamic. Microsoft
itself is very actively forcing the pace -
[come Jan 12th 2016 corporates will have to be on IE11](http://blogs.msdn.com/b/ie/archive/2014/08/07/stay-up-to-date-with-internet-explorer.aspx)

So, by Q1 2016, the market share of IE9 and IE10 will have diminished sufficiently that they could be ignored. Probably.
In which case, a modern flexbox implementation will be available on all the browsers you then care about.
**So that's soon, but not now!**

But, even when it comes to modern browsers, there will be teething issues. Based on 5 minutes of
testing once a month, re-com appears to work reasonably on IE11 and Safari. On the other hand,
Firefox has all the speed of a snail
on performance reducing drugs.
So, yeah, "teething issues".  (Update: Firefox 38, due May 2015
[fixes the performance problems](https://bugzilla.mozilla.org/show_bug.cgi?id=1149339) caused by nested flexboxes.)

I can also confirm that none of the components have been designed with mobile in mind, and
that there's no attempt to handle media queries.  I said we had a desktop app focus, right?

Neither have we been worried too much about code size because other design goals have
taken precedence.  Our main demo app which includes every component, plus all demo
code and a bit of yadda yadda, comes to about 167K compressed when using `:optimzations` `:advanced` (700K pre-compress).
That number includes ReactJS plus the ClojureScript libs and runtime. Everything.

## So, Without Ado Being Any Furthered ...

Still here?  Good. I'm glad we got all that negative stuff out the way.  You're
going to like re-com.

Start by looking
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

When you are running the demo app, you'll see hyperlinks (top of most pages) which
take you to the associated source code.  That's a nice way to navigate to either
the components themselves or the demo code.

When browsing more generally, look in the `src` directory or this repo, you'll notice
two sub-directories:

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

   We love using [figwheel](https://github.com/bhauman/lein-figwheel) to debug.

   To begin a debug session, do this:
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
  - be patient - the initial compile might take anything from 10 seconds to 3 mins depending on how many dependencies need to be downloaded (how many are not yet in your local Maven repo).
  - keep an eye on the terminal started by figwheel, waiting for a green `Successfully compiled` message, at which point, figwheel will immediately move on and try to start the repl.  
  - In response, you should refresh the HTML page. This refresh is needed for figwheel to complete the repl kick-off.
  - to quit figwheel and stop the server/compiler, type ` :cljs/quit` into the repl started by figwheel.


1. Run The (erm, modest) Tests
 
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

  Unlike `debug` which uses figwheel, `debug-test` uses cljsbuild's `auto` for
  recompilation.  This probably isn't a good idea, but that's the way it is right now.

## Using re-com In Your App

First, add these dependencies in your project.clj:

```Clojure
:dependencies [
  ...
  [reagent "0.5.0"]
  [re-com "0.2.2"]
]
```

Note that Reagent comes bundled with the matching version of the React JavaScript library
so you don't need to include React anywhere.

As far as your `index.html` is concerned, take inspiration from here:
https://github.com/Day8/re-com/tree/master/run/resources/public

In particular, you'll need bootstrap (assumidly via a CDN):
```html
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.css">
```

You'll then need these assets, including the re-com.css:
https://github.com/Day8/re-com/tree/master/run/resources/public/assets

```html
<link rel="stylesheet" href="resources/css/material-design-iconic-font.min.css">
<link rel="stylesheet" href="resources/css/re-com.css">
```


## The Missing Components

* tree  (not hard, just haven't needed one yet)
* accordion
* virtual grid. HTML is good at small grids, so no problem there. But when the number of
rows gets huge, you need a widget which does virtual rows, otherwise there's just too much DOM
and there's performance problems.
Can we use [Fixed Data Tables for React](http://facebook.github.io/fixed-data-table)?
* drag and drop.
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move?

## Helping

1. A lein template is needed.
2. Where our docs are wrong or fall short, write up the alternative.
3. See the list of missing things above
4. Test re-com on new browsers and iron out any quirks.  Our focus is strictly Chrome.


### License

Copyright © 2015 Michael Thompson

Distributed under The MIT License (MIT) - See LICENSE.txt

