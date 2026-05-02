# ADR-005: Testing Strategy (JUnit and Mockito)

## Context
To ensure application stability and prevent regressions, we need a consistent strategy for verifying code quality. The app involves complex nutritional calculations and user interactions that must be reliable.

## Decision
We chose a strategy focusing primarily on **Local Unit and Integration Tests (JUnit/Mockito)** for business logic, relying on manual testing for UI edge cases in the MVP phase.

## Rationale
- **Efficiency**: JUnit tests are fast and run on the local JVM, allowing for quick verification of business logic (e.g., calorie calculations, model parsing).
- **Isolation**: Mockito allows us to isolate components, such as the `FirestoreRepository`, from the real Android framework and network during unit testing.
- **Immediate Value**: Validating the math and the DB triggers provides the highest ROI against silent data corruption bugs.

## Alternatives considered
- **Espresso (UI Testing)**: Put on hold (planned for later). UI changes frequently in MVP, leading to high test-maintenance overhead.
- **Manual Testing Only**: Rejected because it is time-consuming, prone to human error, and doesn't scale with feature growth.

## Consequences
- **Code Design**: Required refactoring some classes (like `FirestoreRepository`) to use Dependency Injection to make them testable.
- **Stability**: Significantly reduces the risk of breaking core features (like logging food) during future updates.

## Verification
- 30 automated tests written and verified via `./gradlew testDebugUnitTest`.
## Status
Accepted
