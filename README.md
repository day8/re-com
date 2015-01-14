## Status

Still Alpha.  But most parts now stable.  Should be beta in a week or two.

Until we go beta, we're not taking patches or feature requests.

# re-com

A library of ClojureScript UI components. 

Built on top of Dan Holmsand's terrific [Reagent](http://holmsand.github.io/reagent) 
which, in turn, is built on Facebook's brilliant [React](http://facebook.github.io/react). 

Re-com contains:
* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.  (in Reagent terms these are `components`)
* layout `components` which organise widgets verticaly and horizontaly, within splitters, etc. `components` which put borders around their children. Plus these can all nest, etc.

In short, the sort of stuff you'd need to build a desktop-class app. Some components are still missing, so it is still a work in progress. 

The layouts and components work harmoniously together (umm, except for occasional bouts of English-soccer-hooligan-like hostility, but that's a bug right?).

## Are You Sure You Want To Be Here?

We're a bit new to HTML5, javascipt, ClojureScript, and FRP.   We're disgruntled refuges from Flash/Flex and before that places like QT, MFC, Smalltalk and Interviews. Without Guru status in modern browser technologies, should you be trusting us?

Having the substrate of React and Reagent bestows great benefits, for sure, but it has also posed us some serious challenges. For example, most javascript libs achieve 
popovers by adding absolutely positioned `<div>s` directly to the `<body>` element. But we couldn't do that - not if 
we wanted to abide by the ClojureScript, React/Reagent FRP-ish, immutable, dataflow rules.   

We've done our best and 
it does seem to hang together fairly nicely, with only minor quirks.  But there may hidden dragons, and there could be better ways. We're all ears.

But despite all of the above, we'll probably talk with great authority and certainty, and hold strong opinions.

## No really, This Probably Isn't For You

We made this library to build desktop-class apps which will run in chrome environments like 
[node-webkit](https://github.com/rogerwang/node-webkit) 
and [atom-shell](https://github.com/atom/atom-shell). So we have not taken testing further than chrome. 

In theory, it should work on any modern browser, but there'd probably be teething issues like correctly vendor-prefixing the CSS etc.

The layout side of this library and some components (visual widgets) rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/) 
which [only works on modern browsers](http://caniuse.com/#feat=flexbox): Chrome, Firefox or IE11.

So for the next, say, year or so, this library would be a poor fit if you're targeting the retail web, which is rife with flexbox-less wastelands like IE10 and IE9. 
 
I can also confirm that none of the components have been designed with mobile in mind, and that there's been no attempt to handle media queries.  Its just not that kind of widget library.

Still here?

## So, Without Ado Being Any Furthered ...

Start your review with [the demo](). Wait! You are using Chrome right? 

The demo serves as: 
  - a way to showcase visually the components (widgets)
  - a demonstration of how to code using the components
  - a means to document the components (parameters are given)
  - a test harness

## Using It

The demo app to understand how to use the components. Look in the `demo` folder for the code.

To use re-com, you'll need to add this to your dependencies in project.clj:

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
* annimations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move. Just using the 
* A testing story. 


## Named Parameters

re-com makes extensive use of `named parameters` (as apposed to `positional parameters`).  

When using re-com components, you will *not* we asked to use positional parameters like this:
```
(greet 2 "hello")
```

Instead, re-com would require `named parameters`:
```
(greet
   :times 2
   :say   "hello")
```

Notice that the each parameter value has a leading name. 

While more verbose, we believe `named parameters` have a huge benifits (on the API boundary of a library): 
1. the code using the library is clearly easier to read
2. as a result the code is more understandable - is there anything more important?
2. optionality  -  not all parameters have to be supplied, defaults can be applied
3. flexibility - new parameters can be easily added

## The Intended Architecture 



## One Of These Days 

Eventually:

* Use GSS for layout. Instead of flexbox.  Performance problems apparently. 

## RFP background



https://gist.github.com/staltz/868e7e9bc2a7b8c1f754/
http://elm-lang.org/learn/What-is-FRP.elm

Javelin:
Watch:     http://www.infoq.com/presentations/ClojureScript-Javelin
Read:        https://github.com/tailrecursion/javelin


https://www.youtube.com/watch?v=i__969noyAM
https://speakerdeck.com/fisherwebdev/flux-react

