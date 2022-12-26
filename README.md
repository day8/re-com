<!--  [![CI](https://github.com/day8/re-com/workflows/ci/badge.svg)](https://github.com/day8/re-com/actions?workflow=ci)
[![CD](https://github.com/day8/re-com/workflows/cd/badge.svg)](https://github.com/day8/re-com/actions?workflow=cd)
[![GitHub tag (latest by date)](https://img.shields.io/github/v/tag/day8/re-com?style=for-the-badge)](https://github.com/day8/re-com/tags) -->
[![Clojars Project](https://img.shields.io/clojars/v/re-com.svg?style=for-the-badge&logo=clojure&logoColor=fff)](https://clojars.org/re-com)
[![GitHub issues](https://img.shields.io/github/issues-raw/day8/re-com?style=for-the-badge&logo=github)](https://github.com/day8/re-com/issues)
[![GitHub pull requests](https://img.shields.io/github/issues-pr/day8/re-com?style=for-the-badge&logo=github)](https://github.com/day8/re-com/pulls)
[![License](https://img.shields.io/github/license/day8/re-com.svg?style=for-the-badge)](license.txt)

# re-com

A ClojureScript library of UI components for [Reagent](http://reagent-project.github.io). 

re-com provides:

* familiar UI widgetry **components** such as dropdowns, date pickers, popovers, tabs, etc.
* layout **components**, which arrange widgets vertically and horizontally, within
  splitters, etc. Plus components
  which put borders around their children. These various pieces can be arbitrarily nested
  to create sophisticated layouts.
* a mostly [Bootstrap](http://getbootstrap.com/) look, mixed with
  some [Material Design Icons](http://zavoloklom.github.io/material-design-iconic-font/icons.html).

In short, re-com attempts to provide the kind of UI basics you'd need to build a desktop-class SPA app.

## It Uses Flexbox

The entire layout side of this library relies on 
[Flexbox](http://css-tricks.com/snippets/css/a-guide-to-flexbox/)

Now, pretty much every modern browser
[has support for Flexbox](http://caniuse.com/#feat=flexbox) so 
this is probably a non-issue these days, unless you need to 
support old versions of IE.

We build desktop-class apps to run in controlled browser environments
like [Electron](https://electron.atom.io).  So, we know we're dealing with Chrome.

## Warning: No Mobile Focus

None of the components have been designed with mobile in mind. We said we had a desktop app focus, right?

Neither have we been worried too much about code size because other design goals have
taken precedence. To give you some idea, our main demo app which includes every component, plus all demo
code and plenty of yadda yadda explanatory strings, comes to about 167K compressed when
using `:optimizations :advanced` (700K uncompressed).
That number includes ReactJS plus the ClojureScript libs and runtime. So, everything.
Note:  these numbers no longer match the demo app. We wanted to show off some of the debug features in our demo app, 
so we backed away from fully advanced, minified compilation. 

## So, Without Ado Being Any Furthered ...

Start by [looking at the demo](https://re-com.day8.com.au).

## Navigating The Source

When you are running the demo app, look to the the right of each page's titles, and you'll see hyperlinks
which will take you to the associated source code.  That's a convenient way to navigate to either
the components themselves or the demo code.

When browsing more generally, look in the `src` directory or this repo, you'll notice
two sub-directories:

  - re-com - the library itself - the components
  - re-demo - the demo app, which shows how to use the components

## Useful Commands

1. Getting The Repo


   ```shell
   git clone https://github.com/day8/re-com.git
   ```

   ```shell
   cd re-com
   ```

2. Compiling And Running The Demo
   
   ```shell
   lein watch
   ```
   
   This will run the demo, by doing:
     - a clean
     - a compile
   
   Wait until `[:demo] Build completed.` is displayed in the console indicating
   the dev HTTP server is ready.
    
   Now you can open [`http://localhost:3449/`](http://localhost:3449/) in your
   browser.

4. Run The (erm, modest) Tests
   
   ```shell
   lein ci
   ```
   
   This will:
     - clean
     - compile the tests
     - compile in release mode as a basic optimized build check

5. Run or Debug the tests:
   
   ```shell
   lein watch
   ```

6. Deploy The Demo App To S3 bucket
   
   This will only work if you have the right credentials in your env:
   ```shell
   lein deploy-aws
   ```


## Using re-com

re-com is available from clojars. Add it to your project.clj dependencies:

[![Clojars Project](https://img.shields.io/clojars/v/re-com.svg)](https://clojars.org/re-com)

You should now be able to require the `re-com.core` namespace, which exposes all of the API functions documented in the `re-demo` example app.

You'll then need to include these asset folders in your app:
https://github.com/day8/re-com/tree/master/run/resources/public/assets

As far as your `index.html` is concerned, take inspiration from here:
https://github.com/day8/re-com/tree/master/run/resources/public

In particular, you'll need bootstrap (assumedly via a CDN):
```html
<link rel="stylesheet" href="//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.3.5/css/bootstrap.css">
```

And a reference to these two CSS files (make sure `re-com.css` appears after `bootstrap.css`):

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

If you decide to use re-com, consider also using [re-frame](https://github.com/day8/re-frame)
(an MVC-ish framework).

Although both `re-frame` and `re-com` can be used independently of each other, they dovetail well.

## Lein Template


See [re-frame-template](https://github.com/day8/re-frame-template).


## The Missing Components

* tree  (not hard, just haven't needed one yet)
* menus - there's a dropdown, but no cascading menus
* accordion
* maybe a dockable LHS navbar
* <s> virtual grid. Straight v-box is good enough at small grids, so no problem there. But when the number of
rows gets huge, you need a widget which does virtual rows, otherwise there's just too much DOM
and there's performance problems.
Can we use [Fixed Data Tables for React](https://github.com/facebookarchive/fixed-data-table)?  </s>
* drag and drop.
* animations / transitions.  We have ideas.  They seem clunky.
* Focus management - When the user presses tab, to which field does focus move?

## Helping

1. Where the docs are wrong or fall short, write up something better. Because
   our docs take the form of an app written in ClojureScript using re-com, you're actually
   exercising your knowledge of re-com as you do this.
2. See the list of missing components above. You'll have to produce the
   component itself, including a params spec, plus the extra page in the demo app.
3. Test re-com on new browsers and iron out any quirks.  Our focus is strictly Chrome.

When creating new components, we have found it useful to use the CSS from existing
JavaScript projects (assuming their licence is compatible with MIT) and then
replace the JavaScript with ClojureScript. Reagent really is very nice.

Also, please refer to [CONTRIBUTING.md](https://github.com/day8/re-com/blob/master/CONTRIBUTING.md) for further 
details on creating issues and pull requests.


### License

Copyright © 2015-2021 Michael Thompson

Distributed under The MIT License (MIT) - See LICENSE.txt

