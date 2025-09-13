# Migration Guide: Modern Parts & Theme System

This guide covers how to migrate components from the old parts system to the modern parts & theme architecture.

## Overview

Recent updates to re-com have introduced a more sophisticated parts and theme system that provides better composition, theming support, and user customization. This guide shows how to migrate existing components.

## Migration Example: Alert Components

*This section will be updated with detailed migration steps based on the recent alert component refactor.*

## Key Changes

### Old Pattern
- Direct hiccup with manual parts handling
- Inline styling with `get-in parts [:part :class]`
- Manual CSS class concatenation

### New Pattern  
- `part/part` system with structured definitions
- Theme integration with `theme/comp`
- Automated parts handling and validation

## Migration Steps

*Detailed migration steps will be added based on the alert component refactor analysis.*

## Breaking Changes

*To be documented based on actual migration experience.*

## Benefits of Migration

- Better theme support
- More consistent parts handling
- Improved performance through memoization
- Enhanced user customization capabilities