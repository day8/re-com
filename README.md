## Status

Still Alpha.  But most parts now stable.  Should be beta in a week or two.

Until we go beta, we're not taking patches or feature requests.

# re-com

A library of ClojureScript UI components. 

Built on top of Dan Holmsand's brilliant  [Reagent](http://holmsand.github.io/reagent) 
which, in turn, is built on Facebook's terrific [React](http://facebook.github.io/react). 

Re-com contains:
* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.  (in Reagent terms these are `components`)
* layout `components` which organise widgets vertically and horizontally, within splitters, etc. `components` which put borders around their children. Plus these can all nest, etc.

In short, the sort of stuff you'd need to build a desktop-class app. Some components are still missing, so it is still a work in progress.

The layouts and components work harmoniously together (umm, except for occasional bouts of English-soccer-hooligan-like hostility, but that's a bug right?).

If you decide to use re-com, consider using re-frame (an SPA framework) as well.  The two dovetail pretty well, although re-com can most certainly be used independently -- for example, the demo program for re-com does not use re-frame.

## Are You Sure You Want To Be Here?

We are browser-tech neophytes, who've only spent a year with HTML5, javascript, ClojureScript, and reactive programming.   We're actually disgruntled refuges from Flash/Flex and before that kingdoms like QT, MFC, Smalltalk and Interviews.  As you can imagine, we've accumulated a lot of papercuts developing this library, and there's every chance we've made mistakes of both design and implementation.

For example, having the substrate of React and Reagent bestows great benefits, for sure, but it has also posed us some serious challenges for things like Popups. Most javascript libs achieve 
popovers by adding absolutely positioned `<div>s` directly to the `<body>` element. But we couldn't do that - not if 
we wanted to stay in the GIU-as-a-function-of-the-data paradigm fostered by ClojureScript and React/Reagent reactivity. We've come up with (ingenious!) solutions for things like Popovers, but because of our lack of experience, there might be better ways. We're all ears.

Despite our inexperience, re-com does seem to hang together fairly well, with only minor quirks. We're using re-com to build production systems, so we've shaken out most of the bugs.  But it is still early days, and your usage patterns might well find other, hidden  dragons. 


## No really, This Almost Certainly Isn't For You

We made this library to build desktop-class apps which will run in chrome environments like 
[node-webkit](https://github.com/rogerwang/node-webkit) 
and [atom-shell](https://github.com/atom/atom-shell). So we have not taken testing further than chrome. 

In theory, re-com should work on any modern browser, but there'd probably be teething issues like correctly vendor-prefixing the CSS etc.

Here's a big thing:  the layout side of this library and some components (visual widgets) rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/) 
which [only works on modern browsers](http://caniuse.com/#feat=flexbox): Chrome, Firefox or IE11.

So for the next, year or two, this library would be a poor fit if you're targeting the retail web, which is rife with flexbox-less wastelands like IE10 and IE9. 
 
I can also confirm that none of the components have been designed with mobile in mind, and that there's no attempt to handle media queries.  Its just not that kind of widget library.

Still here?

## So, Without Ado Being Any Furthered ...

Start your review with [the demo](). Wait! You are using Chrome right? 

The demo serves as: 
  - a way to visually showcase the components (widgets)
  - a demonstration of how to code using those components
  - a means to document the components (parameters etc)
  - a test harness of sorts

## Named Parameters

re-com components have `named parameters`, rather than `positional parameters`. 

So, when using re-com components, you will *not* we asked to use positional parameters like this:
```
[greet-component 2 "hello"]
```

Instead, re-com requires `named parameters` more like this:
```
[greet-component
   :times 2
   :say   "hello"]
```

Notice how each parameter value has a short leading keyword name. The first version, using `positional parameters`, was more concise, the 2nd using `named parameters` is more explicit. 

Religious wars have been fought on these issues, but we believe using `named parameters` in the API of a library has compelling benefits: 
	1. the code using the library is clearly easier to read (despite being longer)
	2. as a result the code is more understandable - is there anything more important?
	2. optionality  -  not all parameters have to be supplied, defaults can be introduced
	3. flexibility - new parameters are easily added

Internally, re-com doesn't use `named parameters` all the time, but at the component API boundary, definitely. 

## Running/Debugging the Demo and Tests

The demo app is provided to assist understanding of how to use the components. Look in the `re_demo` folder for the code.

To run the demo, clone this repository then from it's root, enter the command:

```
lein run
```

This will do a clean compile of the demo and load the required URL into your default browser (starting it if necessary).

You can also debug the demo with the following: 


```
lein debug
```

This will do a clean compile of the demo, load the required URL into your default browser and use [figwheel](https://github.com/bhauman/lein-figwheel) for debugging, which also starts a ClojureScript browser REPL. Sweet!

NOTE: Because the figwheel step is an infinite loop and it starts the server required to display the demo, the demo page will not show when initially launched. Simply refresh the page once the compile has finished.

You can run the tests with this command:

```
lein run-test
```

This will do a clean compile of the tests and load the required URL into your default browser.

You can also debug the tests with the following: 


```
lein debug-test
```

Unlike the demo, the tests are debugged using the more traditional cljsbuild.

## Using It In Your Apps

To use re-com in your application, you'll need to add this to your dependencies in project.clj:

```clj
:dependencies [
  [org.clojure/clojurescript "0.0-XXXX"]
  ...
  [reagent "0.5.0"]
  [re-com "0.X.2"]
]
```


You also need to include react.js itself. One way to do this is to add

    :preamble ["reagent/react.js"]

to the *:compiler* section of project.clj, as shown in the examples
directory (or "reagent/react.min.js" in production). You could also
add

```html
    <script src="http://fb.me/react-0.9.0.js"></script>
```

directly to your html.

XXX What else?  Bootstrap??

## Dependencies

These components make use of the following libraries:

 * [Bootstrap](http://getbootstrap.com) is a CSS/JavaScript library, but we're just using it for the CSS styling.
 *  is a ClojureScript wrapper for the [Facebook React](http://facebook.github.io/react) 
   library which is used to build web-based user interfaces.

## Leaky Abstractions

Layout is built on flexbox, but our abstractions are leaky.  At some point 
you're going to have to do the flexbox tutorials to understand what's going on. 

This is compounded by the viral nature of flexbox. It's reach tends to spread.  

## The Missing Widgets

* tree  (not hard but haven't needed one yet)
* a grid. HTML is good at small grids, so no problem there. But when the number of 
rows gets huge, you need a widget that does virtual rows. Otherwise there's just too many DOM nodes 
* drag and drop
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move. Just using the 
* A testing story. 



## The Intended Architecture 


## One Of These Days 

Eventually:

* Use GSS for layout. Instead of flexbox.  Performance problems apparently. 

## Component Suggestions

* Add a timed alert box which appears for a set period of time. This would probably be absolutely positioned over the UI and then fade away after the set time expires.


## RFP background



https://gist.github.com/staltz/868e7e9bc2a7b8c1f754/
http://elm-lang.org/learn/What-is-FRP.elm

Javelin:
Watch:     http://www.infoq.com/presentations/ClojureScript-Javelin
Read:        https://github.com/tailrecursion/javelin


https://www.youtube.com/watch?v=i__969noyAM
https://speakerdeck.com/fisherwebdev/flux-react



