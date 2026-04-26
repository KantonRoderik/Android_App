# ADR-004: Material Design 3 (Material You)

## Context
The application needs a modern, visually appealing, and accessible user interface. It should also support the latest Android features, such as dynamic color themes (Material You), to provide a personalized experience for users.

## Decision
We chose **Material Design 3 (M3)** as the primary design system for the application.

## Rationale
- **Dynamic Color Support**: M3 allows the app to adapt to the user's wallpaper colors (Android 12+), creating a more integrated and personalized feel.
- **Modern Components**: M3 provides updated buttons, cards, and navigation elements that follow current mobile design trends.
- **Accessibility**: The system is built with high contrast ratios and readability in mind, ensuring a better experience for all users.
- **Consistency**: Using a standard system ensures a familiar UX pattern for Android users.

## Alternatives considered
- **Material Design 2**: Rejected because it lacks dynamic color support and feels dated compared to the latest Android OS versions.
- **Custom Design System**: Rejected due to the significant time and effort required to build and maintain custom components from scratch.

## Consequences
- **Modern Look**: The app feels like a first-class citizen on modern Android devices.
- **Refactoring**: Existing layouts using older components required updates to align with M3 specifications.
- **Dark Mode**: Support for both light and dark themes became a priority to maintain design consistency.

## Status
Accepted
