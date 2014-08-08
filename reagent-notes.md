reagent notes
=============

This document provides some important information about how to effectively use
reagent, so that any reagent/atom values

The reagent documentation state that *"Any component that dereferences a reagent.core/atom will be automatically re-rendered."*

Note that the text says "component". Quite often we write components which can take hiccup as a parameter.

This can lead to a subtle bug

However, there are some cases where this will not work. Take the following example code:

	(defn green-box
	  [hiccup]
	  [:div {:width "100px" :height "100px" :background-color "green" :margin "10px"}
	   [hiccup]])

	(defn green-message-box-bad
	  [msg]
	  [:div
	   [:h3 "Here is a component"]
	   [green-box [:p "Message: " [:span @msg]]]]
	  )

	(defn green-message-box-good
	  [msg]
	  [:div
	   [:h3 "Here is a component"]
	   [green-box [:p "Message: " [(fn [] [:span @msg])]]]]
	  )

	(defn display-green-messages
	  []
	  (let [msg (reagent/atom "initial text")]
		[:div
		 [green-message-box-bad  msg]
		 [green-message-box-good msg]]
		))


The difference between [] and ()
================================

You can render code


Summary
=======

Learnings:

 - If you pass hiccup markup as a parameter to a component function, any dereferenced atoms in that markup
   will not be watched/updated because that component will have simply received the atom's value at the
   time of the call.


References
==========

The following articles helped in the preparation of this document:

 - [Misunderstanding something about :key prop passing](https://github.com/holmsand/reagent/issues/34)
 - [Updating app-data Ratom succeeds, but page rerenders with original data](https://github.com/holmsand/reagent/issues/35)

 More information

  - [Reagent home page](http://holmsand.github.io/reagent)
  - [react on GitHub](https://github.com/holmsand/reagent)
