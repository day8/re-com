

Things About Reagent I'd Like To Improve
============


If I set a style to nil  .... I got a completely incomprehensible error at run-time

If I include [] as markup I get an error, but not a lot of information about where it occurs




Things About CLJS I'd Like To Improve
============

I've figured out one puzzling thing about clojurescript    (when using "auto" compiles).

Every now and again, my program stops working completely, I get an exception and I'm puzzled.     (exception is  something like  "undefined doesn't have a call method")
Eventually, I "clean" and recompile, and bingo the compiler puts out an error.    I think to myself "Why didn't it do that in the first place".

Now, as far as I can tell its all to do with the incremental compiler and the way it only compiles one file at a time, and ONLY files that have changed (not the ones that depend on it).

Imagine that I have     namespace X,  uses namespace Y       (aka   X.cljs   and Y.cljs)

X depends on Y.       In effect,  within X.cljs there is a reference to     Y/some-func

And then you go to   Y.cljs    and change the name of the function from   "some-func"  to "my-func"

At this point   the code in  X   is wrong.  It still refers to    Y/some-func.

But the auto compile process won't pick that problem up.   Because it only recompiled  Y   when we changed it.
The auto compile doesn't know   "X depends on Y,  therefore I should also compile X when Y changes."  To catch the problem in X,  you'd have to recompile X  *but*  only Y is recompiled..

But when you do a "clean", it recompiles X and bingo you get to see the error.

lein
----

   If you make a change to project.cljs,  you'll have to restart `lein cljsbuild auto <name>`
   So a common trap is to add a dependency to project.cljs but still get errors.

Baffling Compile time
------

Leaving out the []  on a `defn` results in a compile-time error that is baffling and unhelpful.

Linux
====

By default you'll probably have an old version of lein.

Run `lein --version` and make sure it is at least 2.3.N
