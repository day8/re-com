# CLAUDE.md - re-com Development Guide

This file provides comprehensive guidance for working with the re-com ClojureScript UI component library.

## Quick Reference

### Essential Commands
```bash
bb install       # Install dependencies
bb watch         # Full dev server (http://localhost:3449/)
bb test          # Run tests
bb clean         # Clean compiled artifacts
```

### Core Development Loop
1. Run `bb watch` for hot reloading
2. Edit components in `src/re_com/`
3. View changes at http://localhost:3449/
4. Create/update demo page in `src/re_demo/`
5. Run `bb test` before committing

## Detailed Guides

- [Development Workflow](./ai/development-workflow.md) - Commands, testing, and development loop
- [Component Creation](./ai/component-creation-modern.md) - Modern component creation patterns and templates
- [Parts System](./ai/parts-system.md) - Granular styling and customization system  
- [Theme System](./ai/theme-system.md) - Theming integration and CSS handling
- [Architecture](./ai/architecture.md) - Dependencies, file structure, and technical foundations
- [Troubleshooting](./ai/troubleshooting.md) - Common issues and Reagent gotchas
- [Migration Guide](./ai/migration-parts-theme.md) - Migrating to modern parts & theme system

## Important Notes

### Code Standards
- **DO NOT ADD COMMENTS** unless explicitly requested
- Always prefer editing existing files over creating new ones
- Never create documentation files unless explicitly requested
- Follow existing code conventions and patterns
- Use `handler-fn` for all event handlers
- Always validate component arguments with `validate-args-macro`

### Security & Best Practices
- Never expose or log secrets/keys
- Check that libraries are already available in the codebase before using
- Look at existing components for patterns before implementing new ones
- Run lint/typecheck commands before committing (when available)
- Only commit when explicitly requested

### Library Integration
- **Reagent 1.1.0** - React wrapper for ClojureScript
- **Bootstrap 3.3.5** - Base CSS framework
- **Shadow-cljs** - Build tool with hot reloading
- Desktop-focused, Chrome-optimized development