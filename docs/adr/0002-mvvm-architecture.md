# ADR-002: MVVM Architecture Pattern

## Context
The Android application code needs to be organized, testable, and maintainable. We need to decouple business logic from UI components (Activities/Fragments) to prevent "God Activities" and ensure high quality.

## Decision
We chose the **MVVM (Model-View-ViewModel)** architectural pattern, following Google's official recommendations.

## Rationale
- **Separation of Concerns**: The View handles only UI rendering, while the ViewModel manages data and business logic.
- **Lifecycle Awareness**: ViewModels survive configuration changes (like screen rotations), preventing data loss.
- **Testability**: ViewModels can be easily unit-tested without direct dependencies on the Android framework (like Context).

## Alternatives considered
- **MVC (Model-View-Controller)**: Rejected because it often leads to bloated Activities that are hard to test and maintain.
- **MVP (Model-View-Presenter)**: Evaluated but rejected because MVVM offers better integration with modern Android components like LiveData and DataBinding.

## Consequences
- **Code Overhead**: Requires more boilerplate (ViewModel factories, observers, and reactive data streams).
- **Learning Curve**: Slightly more complex for initial setup, but pays off in long-term maintenance.

## Status
Accepted
