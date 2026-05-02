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

@ai/development-workflow.md - Commands, testing, and development loop
@ai/component-creation-modern.md - Modern component creation patterns and templates  
@ai/parts-system.md - Granular styling and customization system
@ai/theme-system.md - Theming integration and CSS handling
@ai/performance-best-practices.md - Optimization techniques for parts & theme system
@ai/architecture.md - Dependencies, file structure, and technical foundations
@ai/troubleshooting.md - Common issues and Reagent gotchas
@ai/migration-parts-theme.md - Migrating to modern parts & theme system

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

<!-- BEGIN BEADS INTEGRATION v:1 profile:minimal hash:ca08a54f -->
## Beads Issue Tracker

This project uses **bd (beads)** for issue tracking. Run `bd prime` to see full workflow context and commands.

### Quick Reference

```bash
bd ready              # Find available work
bd show <id>          # View issue details
bd update <id> --claim  # Claim work
bd close <id>         # Complete work
```

### Rules

- Use `bd` for ALL task tracking — do NOT use TodoWrite, TaskCreate, or markdown TODO lists
- Run `bd prime` for detailed command reference and session close protocol
- Use `bd remember` for persistent knowledge — do NOT use MEMORY.md files

## Session Completion

**When ending a work session**, you MUST complete ALL steps below. Work is NOT complete until `git push` succeeds.

**MANDATORY WORKFLOW:**

1. **File issues for remaining work** - Create issues for anything that needs follow-up
2. **Run quality gates** (if code changed) - Tests, linters, builds
3. **Update issue status** - Close finished work, update in-progress items
4. **PUSH TO REMOTE** - This is MANDATORY:
   ```bash
   git pull --rebase
   bd dolt push
   git push
   git status  # MUST show "up to date with origin"
   ```
5. **Clean up** - Clear stashes, prune remote branches
6. **Verify** - All changes committed AND pushed
7. **Hand off** - Provide context for next session

**CRITICAL RULES:**
- Work is NOT complete until `git push` succeeds
- NEVER stop before pushing - that leaves work stranded locally
- NEVER say "ready to push when you are" - YOU must push
- If push fails, resolve and retry until it succeeds
<!-- END BEADS INTEGRATION -->
