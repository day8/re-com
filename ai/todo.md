# TODO: Architecture & Migration Items

## MAYBE: Props Precedence for Component Parameters

**Status:** Under consideration - not yet decided if this is the right approach

**Issue:** Component parameters with defaults (like `backdrop-color`, `backdrop-opacity` in modal-panel) currently go directly to `:post-props`, which means they override user customization via `:parts`.

**Current Pattern:**
```clojure
;; Component parameter goes to :post-props
(part ::mp/backdrop
  {:post-props {:style {:background-color backdrop-color
                        :opacity backdrop-opacity}}})
```

**Problem:** User can't override via `:parts {:backdrop {:style {:background-color "red"}}}`

**Alternative Pattern (maybe better?):**
```clojure
;; Component parameter goes to :re-com :state
re-com-ctx {:state {:backdrop-color backdrop-color
                    :backdrop-opacity backdrop-opacity}}

;; Theme method applies as defaults
(defmethod base ::mp/backdrop [{{{:keys [backdrop-color backdrop-opacity]} :state} :re-com :as props}]
  (tu/style props {:background-color backdrop-color
                   :opacity backdrop-opacity}))
```

**Potential Benefit:** User could override theme defaults via `:parts` map, giving proper precedence hierarchy:
1. Theme applies component parameter defaults
2. User's `:parts` customization overrides theme
3. Top-level `:class/:style/:attr` have highest precedence via `:post-props`

**Concerns:**
- Adds complexity - now parameters can live in two places
- Would require reviewing all components for consistency
- Current pattern (post-props) is simpler and matches pre-migration behavior
- Need to establish clear guidelines for when to use each approach

**Decision:** TBD - need more experience with migrated components before committing to this pattern

---

## Future Architecture Items

### Rigorous Review of [:re-com :state] Structure

**Status:** Needs design & implementation

**Current Approach:** Flat key-value pairs in `:re-com :state`:
```clojure
re-com-ctx {:state {:disabled? false
                    :size :large
                    :showing? true}}
```

**Ideas for Enhancement:**
1. **Part-keyed states** - Each part could have its own state namespace
2. **Substates** - Support hierarchical/nested state structure
3. **More formal statechart patterns** - Explicit state machines with transitions

**Potential Benefits:**
- Better organization for complex components with many parts
- Clearer state ownership and scope
- More powerful state management patterns
- Better type safety and validation opportunities

**Examples to Explore:**
```clojure
;; Part-keyed states?
re-com-ctx {:state {::wrapper {:mode :expanded}
                    ::input {:validation :success}
                    ::label {:emphasis :strong}}}

;; Substates?
re-com-ctx {:state {:input {:state :focused
                            :validation {:state :validating
                                        :progress 50}}}}
```

**Action Items:**
- Review all existing [:re-com :state] usage across migrated components
- Establish patterns and conventions
- Consider impact on theme method access patterns
- Update documentation with guidelines
- Reference: See day8/re-fine for related state management patterns

(Add more items here as they come up)
