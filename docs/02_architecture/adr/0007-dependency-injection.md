# ADR-007: Dependency Injection (Manual via Singleton)

## Context
Various parts of the app (Activities, Fragments) need access to the `FirestoreRepository` and other helper classes. We need a way to provide these dependencies without tightly coupling everything to concrete implementations, which would hurt testability.

## Decision
We chose **Manual Dependency Injection via Singletons** (e.g., `FirestoreRepository.getInstance()`) over a dedicated DI framework.

## Rationale
- **Simplicity:** The project scope is relatively small. Introducing a heavy framework like Dagger or Hilt adds unnecessary build time overhead and learning curve.
- **Testability:** By adding a visible `setInstance` method (or package-private constructor), we can still mock the Singleton during Unit Tests (as seen in `FirestoreRepositoryTest`).

## Alternatives considered
- **Hilt (Dagger)**: The standard for Android. Rejected for now because the app doesn't have a complex dependency graph (mostly just one main Repository).
- **Koin**: Rejected as the project is primarily written in Java, and Koin is heavily Kotlin-focused.

## Consequences
- **Positive:** Fast build times, zero DI framework boilerplate.
- **Negative:** If the app grows significantly, managing lifecycles of manual singletons might become error-prone.

## Verification
- Verified by successfully mocking `FirebaseFirestore` and injecting it into the repository inside our unit test suite.
## Status
Accepted