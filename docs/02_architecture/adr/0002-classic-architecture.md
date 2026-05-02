# ADR-002: Classic Activity Architecture with Repository Pattern

## Context
The Android application needs a clear structure to handle data operations and UI updates. We need to decide how to organize code within Activities and how to interface with the database.

## Decision
We chose a **Classic Activity/Fragment architecture** enhanced with a **Repository Pattern** and **ViewBinding**.

## Rationale
- **Development Speed**: For this project, using standard Activities with direct ViewBinding allowed for faster prototyping and implementation of real-time Firestore listeners.
- **Simplicity**: The direct interaction between Activities and the `FirestoreRepository` reduced boilerplate code.
- **ViewBinding**: Ensures type-safe access to UI components.
- **Repository Pattern**: Centralizing data access in `FirestoreRepository` decouples business logic for database operations from the UI controllers.

## Alternatives considered
- **MVVM (Model-View-ViewModel)**: Evaluated but not fully implemented to keep the codebase simple and leverage direct Firestore snapshot listeners within the Activity lifecycle.

## Consequences
- **Easier**: Faster implementation of UI updates via direct listeners.
- **Harder**: Unit testing UI-coupled logic is more difficult compared to a pure MVVM approach.

## Verification
- Repository logic isolated and verified via `FirestoreRepositoryTest.java` using Mockito.
