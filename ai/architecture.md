# Architecture Reference

This guide covers the architectural foundations of re-com.

## Key Dependencies

- **Reagent 1.1.0** - React wrapper for ClojureScript
- **Shadow-cljs 2.28.2** - Build tool with hot reloading  
- **cljs-time** - Date/time manipulation
- **core.async** - Asynchronous operations
- **Bootstrap 3.3.5** - Base CSS framework

## File Organization

```
src/
├── re_com/           # Core component library
│   ├── core.cljs     # Main API exports
│   ├── validate.cljs # Validation system
│   ├── theme.cljs    # Theming system
│   └── *.cljs        # Individual components
├── re_demo/          # Demo application
│   ├── core.cljs     # Demo app entry
│   └── *.cljs        # Component demo pages
test/
└── re_com/           # Component tests
    └── *_test.cljs   # Test files
```

## Build Configuration

- **shadow-cljs.edn** - Build targets and compiler options
- **bb.edn** - Babashka task definitions  
- **deps.edn** - Clojure dependencies

## Browser Support

- **Primary**: Chrome (main development target)
- **Focus**: Desktop browsers
- **Responsive**: Desktop-first design
- **CSS**: Modern flexbox with vendor prefixes

## Development Best Practices

### Functional Programming Notes

- **letfn Usage**:
  * Only use `letfn` when mutual recursion is required
  * Prefer `let` with anonymous functions for most scenarios