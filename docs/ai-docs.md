# [Misunderstanding something about :key prop passing](https://github.com/reagent-project/reagent/issues/34)

In Reagent, how you call a component like `(defn my-comp [arg] [:div "Hello " arg])` significantly affects where `:key` props are needed, especially when rendering lists of items. There are two main ways:
*   Calling with parentheses `(my-comp "world")`: This inlines the component's output (e.g., the `[:div "Hello " arg]` becomes `[:div "Hello world"]`) directly into the surrounding Hiccup. If this inlined Hiccup (like `[:div ...]`) has a `:key`, React uses that key.
*   Calling with square brackets `[my-comp "world"]`: This creates a distinct Reagent component instance for `my-comp`. If this `[my-comp ...]` form is itself an item in a list that React is managing, then *this vector form* needs the `:key` (e.g., `[my-comp {:key "some-unique-id"} "world"]`). A key inside what `my-comp` returns (e.g., on the `[:div]`) won't serve this purpose for the list.

The crucial point for avoiding issues is this: when rendering a dynamic list of components, React requires a unique `:key` on each *immediate child element* in that list. This allows React to efficiently update, add, or remove items.
*   If you use the bracket form `[my-comp ...]` for items in a list, ensure that vector itself receives the `:key`: `[my-comp {:key "unique-id"} arg-1 ...]`.
*   If you use the parenthesis form `(my-comp ...)` for items in a list, and `my-comp` returns a single Hiccup element like `[:li {:key "unique-id"} ...]`, then that element's key is what React sees and uses at that list level.
Confusing these two scenarios or omitting keys where React expects them on list items can lead to React warnings, incorrect UI updates, or loss of component state.

# [Updating app-data Ratom succeeds, but page rerenders with original data](https://github.com/reagent-project/reagent/issues/35)

The main problem faced was that the user interface (UI) wasn't updating even though the underlying application data, stored in a Reagent atom, was being correctly changed. For instance, when text was typed into an input field, the atom reflected this new text, but the screen stubbornly continued to display the old value. This can be very confusing because you can see the data is right, but the display is wrong.

The core reason this happens in Reagent is related to how Reagent knows *when* to re-render a component. Reagent components automatically re-render when the Reagent atoms they *depend on* change. For Reagent to establish this dependency, it needs to see your component *accessing* (or "dereferencing" with `@`) the atom *inside* the component's rendering logic. In the problematic code, the atom was dereferenced *outside* the main component, like this: `[MyComponent (:some-data @app-atom)]`. Here, `@app-atom` is evaluated, and `MyComponent` just receives the plain data value. Reagent doesn't know `MyComponent`'s rendering depends on `app-atom` itself.

To avoid this, you need to ensure that the atom dereference happens *within* a Reagent component's render function. The fix was to introduce a new top-level component, say `RootComponent`, and call the atom from inside it: `(defn RootComponent [] [MyComponent (:some-data @app-atom)])`. Then, you'd render `[RootComponent]`. Now, Reagent sees `RootComponent` dereferencing `app-atom`, so when `app-atom` changes, `RootComponent` (and consequently `MyComponent` with the new data) will re-render. Always make sure your components that need to react to atom changes are set up so Reagent can track their usage of those atoms.
