## Status

Still Alpha overall.  But many parts now stable.

Until we go beta, we're not taking patches or feature requests.

# re-com

A library of ClojureScript UI components, built on top of Dan Holmsand's brilliant
[Reagent](http://reagent-project.github.io)
which, in turn, is a layer over Facebook's trail blazing [React](http://facebook.github.io/react).

Re-com contains:

* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.  (in Reagent terms these are `components`)
* layout `components` which organise widgets vertically and horizontally, within splitters, etc. `components`
  which put borders around their children. Plus these can all nest, etc.
* a [Bootstrap](http://getbootstrap.com/) flavour, mixed in with
  some [Material Design Icons](http://zavoloklom.github.io/material-design-iconic-font/icons.html).

In short, the stuff you'd need to build a desktop-class app. But, it is a work in
progress - for a start some components are missing.

The layouts and components work harmoniously together (urmm, except for occasional bouts of
English-soccer-hooligan-like hostility, but that's a bug right?).

If you decide to use re-com, consider using re-frame (an SPA framework) as well.  The two
dovetail well, although re-com can certainly be used independently -- for example, the demo
program for re-com does not use re-frame.

## Are You Sure You Want To Be Here?

We are browser-tech neophytes, who've only spent a year with HTML5, JavaScript, ClojureScript,
and reactive programming.

We're actually displaced refugees from Flash/Flex and, before that, a long time ago in a galaxy far, far away tech
like QT, MFC, Smalltalk and Interviews.  As you can imagine, we've accumulated a menagerie of paper cuts developing
this library, and there's every chance we've made mistakes in both design and implementation.

For example, having the substrate of React and Reagent imparts great benefits, for sure, but it has also
posed us some serious challenges for things like Popups. Most JavaScript libs achieve
popovers by adding absolutely positioned `<div>s` directly to the `<body>` element. But we couldn't do
that - not if
we wanted to stay true to the GIU-as-a-function-of-the-data paradigm fostered by ClojureScript and React/Reagent
reactivity. We've come up with (ingenious? tortuous?) solutions for things like Popovers, but because
of our lack of experience, there might be better ways. We're all ears if there are, BTW.

Despite our inexperience, re-com does hang together fairly well, with only minor quirks. We're
using it to build production systems, so we've shaken out many bugs and moulded a better API as
we've gone.  But it is still early days, and your alternative usage patterns might yet unearth
hidden  dragons.


## No really, re-com Might Not Be For You

We build desktop-class apps which will run in chrome environments like
[node-webkit](https://github.com/rogerwang/node-webkit) 
and [atom-shell](https://github.com/atom/atom-shell). So we have only tested re-com in Chrome.

In theory, re-com should work on any modern browser, but there'd probably be teething
issues like correctly vendor-prefixing the CSS etc.

Here's a key thing:  the entire layout side of this library plus a couple of the widgets
rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/)
which [only works on modern browsers](http://caniuse.com/#feat=flexbox): Chrome, Firefox or IE11.

So, for the next year or two, this library would be a poor fit if you're targeting the
retail web, which is rife with Flexbox-less wastelands like IE10 and IE9.
 
I can also confirm that none of the components have been designed with mobile in mind, and
that there's no attempt to handle media queries.  It's just not that kind of widget library.

Neither have we been particularly concerned about code size.

Still here?  Good, in that case, you're going to love re-com.

## So, Without Ado Being Any Furthered ...

You do have Chrome handy, right?  Good. Start by looking at [the demo](http://re-demo.s3-website-ap-southeast-2.amazonaws.com).

The demo serves as: 

  - a way to visually showcase the components (widgets)
  - a demonstration of how to code using those components
  - a means to document the components (parameters etc.)
  - a test harness of sorts

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

Further, we have added a layer of parameter validation. Every component has an associated vector of maps which contains metadata for each parameter...info like the name, data type, whether it's required, default value and so on.

Each component parameter can also have a validation function which is called on each render, to make sure the data type is correct or the parameter value is contained in the list of expected values.

If a parameter name is not recognised, or if a required parameter is not specified, or the validation function fails, the validation spits out an exception to the console, explaining what went wrong.

This will save a lot of development time by catching common (but sometimes very subtle and hard to track down) mistakes.  

Incidentally, this vector is also used to create the "Named Parameter" tables in the demo app. 

Finally, we make use of the in-built goog.DEBUG variable (which is definable in `project.clj`) to deactivate this parameter validation for production code. Simply include the following line in the production build id:

```Clojure
:closure-defines {:goog.DEBUG false}
```

## Navigating The Source

Unsurprisingly, look in the `src` directory!!

Notice that it has two sub-directories:

  - re-com - the library
  - re-demo - the demo app

After you clone this repo, `cd` into the root directory and execute one of the following commands:

	lein run

This will run the demo, by doing: 
  - a clean 
  - a compile 
  - a load of the right `index.html` into your default browser

To debug the demo with [figwheel](https://github.com/bhauman/lein-figwheel):

	lein debug

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

To run the tests:

	lein run-test

This will:

- clean 
- compile the tests 
- load the required `test.html` into your default browser, so you can see the results.

Debug the tests with:

	lein debug-test

Unlike `debug` which uses figwheel, `debug-test` uses cljsbuild's `auto` for recompilation.

## Using re-com In Your Apps

To use re-com in your application, you'll need to add this to your dependencies in project.clj:

```Clojure
:dependencies [
  [org.clojure/clojurescript "0.0-XXXX"]
  ...
  [reagent "0.5.0"]
  [re-com "0.2.2"]
]
```

Note that Reagent comes bundled with the matching version of the React JavaScript library so you don't need to include that anywhere.

You will however, need to add Bootstrap to your html page. We normally grab it from a CDN:

```html
<link rel="stylesheet" href="http://cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.2.0/css/bootstrap.css">
```

and three CSS files included in the resources folder of the re-com library:

```html
<link rel="stylesheet" href="resources/css/material-design-iconic-font.min.css">
<link rel="stylesheet" href="resources/css/datepicker-bs3.css">
<link rel="stylesheet" href="resources/css/re-com.css">
```

TODO: Will need to expand on this when we get a resources solution. 

## Dependencies

These components make use of the following libraries:

 * [Reagent](http://reagent-project.github.io) is a ClojureScript wrapper for the [Facebook React](http://facebook.github.io/react) 
   library which is used to build web-based user interfaces.
 * [Bootstrap](http://getbootstrap.com) is a CSS/JavaScript library, but we're just using it for the CSS styling.

## Leaky Abstractions

The layout side of re-com is built on top of Flexbox, but our abstractions are leaky.  At some point
you're going to have to do the [Flexbox tutorials](http://css-tricks.com/snippets/css/a-guide-to-flexbox/) to understand what's going on. 

This is compounded by the viral nature of Flexbox. We've found that it's use viral. It's reach tends to spread.

## The Missing Parts

* tree  (not hard but haven't needed one yet)
* a grid. HTML is good at small grids, so no problem there. But when the number of 
rows gets huge, you need a widget that does virtual rows. Otherwise there's just too many DOM nodes.  Can we use [Fixed Data Tables for React](http://facebook.github.io/fixed-data-table)?
* Add a timed alert box which appears for a set period of time. This would probably be absolutely positioned over the UI and then fade away after the set time expires.
* drag and drop   (we have somewhat sorted this).
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move? 
* A testing story. 

## Future 

Todos:

* Use [GSS](http://gridstylesheets.org) for layout instead of Flexbox. Performance problems apparently.
* Create a re-com template. 

## Component Suggestions


## RFP background



https://gist.github.com/staltz/868e7e9bc2a7b8c1f754/
http://elm-lang.org/learn/What-is-FRP.elm

Javelin:
Watch:   http://www.infoq.com/presentations/ClojureScript-Javelin
Read:    https://github.com/tailrecursion/javelin


https://www.youtube.com/watch?v=i__969noyAM
https://speakerdeck.com/fisherwebdev/flux-react

