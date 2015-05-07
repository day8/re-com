# re-com

A ClojureScript library of UI components.

It is built on top of Dan Holmsand's terrific
[Reagent](http://reagent-project.github.io)
which, in turn, is a layer over Facebook's trailblazing [React](http://facebook.github.io/react).

Just to be clear: this library is 100% ClojureScript. We're not wrapping jQuery plugins here.

Re-com has:

* familiar UI widgetry **components** such as dropdowns, date pickers, popovers, tabs, etc.
* layout **components**, which arrange widgets vertically and horizontally, within
  splitters, etc. Plus components
  which put borders around their children. These various pieces can be arbitrarily nested
  to create sophisticated layouts.
* a mostly [Bootstrap](http://getbootstrap.com/) look, mixed with
  some [Material Design Icons](http://zavoloklom.github.io/material-design-iconic-font/icons.html).

In short, re-com attempts to provide the kind of UI basics you'd need to build a desktop-class app.



## Warning: re-com Might Not Be For You (just yet)

We build desktop-class apps to run in controlled browser environments
like [atom-shell](https://github.com/atom/atom-shell).  So, we know we're dealing with Chrome.

If you are similar, or if you work on Intranet apps where you can mandate a modern browser,
re-com could be ideal for you, right now.

On the other hand, if you target the retail web, you might have to wait till early 2016
(10 months away, at the time of writing).

Why?  **Well, here's the thing:**  the entire layout side of this library plus a few of the widgets
rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/)
which only works on [modern browsers](http://caniuse.com/#feat=flexbox), and specifically not IE 9 and 10.


Now, the grinding pain and longevity of IE6 has conditioned many to
expect 8, 9 and 10 to hang around forever too.  But, this time around, there's
quite a different dynamic. Microsoft
itself is very actively forcing their demise -
[come Jan 12th 2016 corporates will have to be on IE11](http://blogs.msdn.com/b/ie/archive/2014/08/07/stay-up-to-date-with-internet-explorer.aspx)

So, by Q1 2016, the market share of IE9 and IE10 will have diminished sufficiently
that they could be ignored. Probably. Maybe.
If so, a modern flexbox implementation will be available on all the browsers you then care about.
**So that's surprisingly soon, but not now!**

But, even when it comes to modern browsers, there will be teething issues. Based on 5 minutes of
testing once a month, re-com appears to work reasonably on IE11 and Safari. On the other hand,
Firefox has all the speed of a snail
on performance reducing drugs.
So, yeah, "teething issues".  (Update: Firefox 38, due May 2015,
[fixes](https://bugzilla.mozilla.org/show_bug.cgi?id=1149339) the performance problems caused by nested flexboxes.)

I can also confirm that none of the components have been designed with mobile in mind, and
that there's no attempt to handle media queries.  I said we had a desktop app focus, right?

Neither have we been worried too much about code size because other design goals have
taken precedence.  Our main demo app which includes every component, plus all demo
code and plenty of yadda yadda, comes to about 167K compressed when
using `:optimizations :advanced` (700K pre-compress).
That number includes ReactJS plus the ClojureScript libs and runtime. So, everything.

## So, Without Ado Being Any Furthered ...

Still here?  Good. I'm glad we got all that negative stuff out the way.  I think you're
going to like re-com.

Start by [looking at the demo](http://re-demo.s3-website-ap-southeast-2.amazonaws.com).


## Navigating The Source

When you are running the demo app, you'll see hyperlinks, to the right of page titles, which
take you to the associated source code.  That's a convenient way to navigate to either
the components themselves or the demo code.

When browsing more generally, look in the `src` directory or this repo, you'll notice
two sub-directories:

  - re-com - the library itself - the components
  - re-demo - the demo app, which shows how to use the components

## Useful Commands

1. Getting The Repo


   ```shell
   git clone https://github.com/Day8/re-com.git
   ```

   ```shell
   cd re-com
   ```

2. Compiling And Running The Demo


   ```shell
   lein run
   ```

  This will run the demo, by doing: 
  - a clean 
  - a compile 
  - a load of the right `index.html` into your default browser


3. Debugging The Demo

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

  Your part to play in the process:

  - the initial load of `index.html` will fail because the figwheel compile hasn't yet finished. 
  - be patient - the initial compile might take anything from 10 seconds to 3 mins depending on how many dependencies need to be downloaded (how many are not yet in your local Maven repo).
  - keep an eye on the terminal started by figwheel, waiting for a green `Successfully compiled` message, at which point, figwheel will immediately move on and try to start the repl.  
  - In response, you should refresh the HTML page. This refresh is needed for figwheel to complete the repl kick-off.
  - to quit figwheel and stop the server/compiler, type ` :cljs/quit` into the repl started by figwheel.


4. Run The (erm, modest) Tests
 
   ```shell
   lein run-test
   ```

  This will:

  - clean
  - compile the tests
  - load the required `test.html` into your default browser, so you can see the results.


5. Debug the tests:

   ```shell
   lein debug-test
   ```

  Unlike `debug` which uses figwheel, `debug-test` uses cljsbuild's `auto` for
  recompilation.  This probably isn't a good idea, but that's the way it is right now.


6. Deploy The Demo App To S3 bucket

   This will only work if you have the right credentials in your env:
   ```shell
   lein deploy-aws
   ```


## Using re-com

re-com is available from clojars. Add it to your project.clj dependencies:

[![Clojars Project](http://clojars.org/re-com/latest-version.svg)](http://clojars.org/re-com)

You'll then need to include these asset folders in your app:
https://github.com/Day8/re-com/tree/master/run/resources/public/assets

As far as your `index.html` is concerned, take inspiration from here:
https://github.com/Day8/re-com/tree/master/run/resources/public

In particular, you'll need bootstrap (assumedly via a CDN):
```html
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.css">
```

And a reference to these two CSS files:

```html
<link rel="stylesheet" href="assets/css/material-design-iconic-font.min.css">
<link rel="stylesheet" href="assets/css/re-com.css">
```

And a reference to the Roboto fonts (but this can be overridden relatively easily):

```html
<link href="http://fonts.googleapis.com/css?family=Roboto:300,400,500,700,400italic" rel="stylesheet" type="text/css">
<link href='http://fonts.googleapis.com/css?family=Roboto+Condensed:400,300' rel='stylesheet' type='text/css'>
```

Reagent comes bundled with a matching version of ReactJS,
so you don't need to include it explicitly.


## MVC

If you decide to use re-com, consider also using [re-frame](https://github.com/Day8/re-frame)
(an MVC-ish framework).

Although both `re-frame` and `re-com` can be used independently of each other, they dovetail well.

## Lein Template


See @gadfly361's [reagent-seed](https://github.com/gadfly361/reagent-seed)


## The Missing Components

* tree  (not hard, just haven't needed one yet)
* menus - there's a dropdown, but no cascading menus
* accordion
* maybe a dockable LHS navbar
* virtual grid. Straight v-box is good enough at small grids, so no problem there. But when the number of
rows gets huge, you need a widget which does virtual rows, otherwise there's just too much DOM
and there's performance problems.
Can we use [Fixed Data Tables for React](http://facebook.github.io/fixed-data-table)?
* drag and drop.
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move?

## Helping

1. Where the docs are wrong or fall short, write up something better. Because
   our docs take the form of an app written in ClojureScrip using re-com, you're actually
   exercising your knowledge of re-com as you do this.
1. See the list of missing components above. You'll have to produce the
   component itself, including a params spec, plus the extra page in the demo app.
1. Test re-com on new browsers and iron out any quirks.  Our focus is strictly Chrome.

When creating new components, we have found it useful to use the CSS from existing
javascript projects (assuming their licence is compatible with MIT) and then
replace the javascript with ClojureScript. Reagent really is is very nice.


### License

Copyright © 2015 Michael Thompson

Distributed under The MIT License (MIT) - See LICENSE.txt

