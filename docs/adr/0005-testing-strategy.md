# ADR-005: Testing Strategy (JUnit and Espresso)

## Context
To ensure application stability and prevent regressions, we need a consistent strategy for verifying code quality. The app involves complex nutritional calculations and user interactions that must be reliable.

## Decision
We chose a combined strategy of **Local Unit Tests (JUnit/Mockito)** and **Instrumented UI Tests (Espresso)**.

## Rationale
- **Efficiency**: JUnit tests are fast and run on the local JVM, allowing for quick verification of business logic (e.g., calorie calculations, model parsing).
- **Isolation**: Mockito allows us to isolate components, such as the `FirestoreRepository`, from the real Android framework and network during unit testing.
- **Real-World Verification**: Espresso tests run on real devices or emulators, ensuring that the UI correctly interacts with the user and displays data from the backend.
- **Official Support**: Both tools are industry standards and natively integrated into Android Studio.

## Alternatives considered
- **Manual Testing Only**: Rejected because it is time-consuming, prone to human error, and doesn't scale with feature growth.
- **Robolectric**: Evaluated for unit tests requiring Android context, but Mockito was preferred for its simplicity in mocking system dependencies.

## Consequences
- **Code Design**: Required refactoring some classes (like `FirestoreRepository`) to use Dependency Injection to make them testable.
- **Maintenance**: Test suites must be updated whenever UI layouts or business logic change.
- **Stability**: Significantly reduces the risk of breaking core features (like logging food) during future updates.

## Status
Accepted
