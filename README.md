# re-com

A library of ClojureScript UI components. 

Built on top of Dan Holmsand's terrific [Reagent](http://holmsand.github.io/reagent) 
which, in turn, is built on Facebook's equally brilliant [React](http://facebook.github.io/react). 


## Overview

Re-com contains:
* familiar UI widgetry such as dropdowns, date pickers, popovers, tabs, etc.
* layout components which can stack widgets in vertical and horizontal
 ways, and which can nest.  Then there's dragable splitters, etc.  

In short, the sort of stuff you'd need to build a desktop-class app. Some components are still missing, 
so it is still a work in progress. 

The layouts and components work harmoniously together (except for occasional bouts of English-soccer-hooligan-like hostility).

## Are You Sure You Want To Be Here?

We're a bit new to a lot here including HTML5, javascipt, ClojureScript, and FRP (functional reactive programming). 
Yep.  Worried?  You have been warned.

Despite all that we'll probably talk with great authority and certainty, and hold strong opinions. 

A substrate of React and Reagent bestows great benefits, but it also posed us some
challenges. For example, most javascript libs we looked at do 
popovers by adding new absolutely positioned `<div>s` directly to the `<body>` element. We couldn't do that - not if 
we wanted to abide by the ClojureScript, React/Reagent FRP-ish, immutable, dataflow rules.   

We've done our best and 
it does seem to hang together fairly nicely, despite some quirks.  There may well be better ways.


## Warning: This Might Not Be For You

The layout side of this library and some widgets rely on [Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/) 
which [only works on modern browsers](http://caniuse.com/#feat=flexbox): Chrome, Firefox or IE11.
 
So for the next, say, two years, this library would be a poor fit if you're targeting the retail web, which is
rife with flexbox-less wastelands like IE10 and IE9.
 
I can also confirm that none of the components have been designed with mobile in mind, and that there's no attempt to 
handle media queries.  
 
To be clear: we made this library to build desktop-class apps which will run in environments like 
[node-webkit](https://github.com/rogerwang/node-webkit) 
and [atom-shell](https://github.com/atom/atom-shell). It would also be useful in 
Chrome app development OR if you are developing for a corporate Intranet setting
where you can mandate a modern browser.
  

## So, Without Ado being Any Furthered ...

[Here's a demo](). 

Wait! You'll be sure to run it on a modern browser, right? 

Actually, we tend to exclusively use Chrome, so for all we know there could be problems on other 
modern browsers too. Maybe. Dunno.  If so, supply a patch. 

The demo is deliberately simple minded. 


## To use it

To use re-com, you add this to your dependencies in project.clj:

```clj
:dependencies [
  [org.clojure/clojurescript "0.0-XXXX"]
  ...
  [reagent "0.4.2"]
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

## Dependencies

Stuff that's missing: 

* tree  (not hard but haven't needed one yet)
* a grid. A grid. HTML is excellent at small grids, so no problem there. But when the number of 
rows gets huge, you need a widget that does virtual rows. Otherwise there's just too many DOM nodes 
in the page, and performance suffers.
* drag and drop
* annimations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move. Just using the 
* Testing. 


## One Day 

Eventually:

* Use GSS for layout. Instead of flexbox.  Performance problems apparently. 
* Statecharts.  


## Component Suggestions

* Add a timed alert box which appears for a set period of time. This would probably be absolutely positioned over the UI and then fade away after the set time expires.


## Named Parameters



## The Intended Architecture 



## RFP background

Heraclitus (500 BC) said:  "Everything flows, nothing stands still". 

https://gist.github.com/staltz/868e7e9bc2a7b8c1f754/
http://elm-lang.org/learn/What-is-FRP.elm

Javelin:
Watch:     http://www.infoq.com/presentations/ClojureScript-Javelin
Read:        https://github.com/tailrecursion/javelin


https://www.youtube.com/watch?v=i__969noyAM
https://speakerdeck.com/fisherwebdev/flux-react


Signals are values that change over time. 

