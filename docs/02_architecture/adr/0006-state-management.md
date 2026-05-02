# ADR-006: State Management Strategy (Firestore Snapshot Listeners)

## Context
The app needs to display up-to-date daily consumption and burned calories on the Dashboard. If a user adds an exercise or food on another screen, the Dashboard must reflect this change immediately.

## Decision
We chose **Firebase Real-time Snapshot Listeners** as the Single Source of Truth for state management, bypassing intermediate local state holders (like Room or complex ViewModels with StateFlows).

## Rationale
- **Simplicity:** Attaching a snapshot listener directly updates the UI when the underlying Firestore document changes.
- **Offline Capability:** Firestore’s SDK caches writes locally and fires the snapshot listener immediately, even without network connection, providing a seamless UX.
- **Consistency:** Ensures that what the user sees is exactly what is stored (or queued to be stored) in the database.

## Alternatives considered
- **ViewModel + Local Room DB + Sync Worker:** Rejected. Over-engineering for this scale. Firebase already handles offline persistence and sync out of the box.
- **Intent Extras / Activity Results:** Returning data via Intents to update the Dashboard manually. Rejected because it's error-prone and doesn't handle multi-device sync.

## Consequences
- **Positive:** Less boilerplate code; real-time sync across devices.
- **Negative:** UI updates are tightly coupled to Firestore models.
- **What to watch:** Ensure listeners are detached in `onStop`/`onDestroy` to prevent memory leaks.

## Verification
- Verified by adding food while offline and observing immediate UI updates via the local cache hit in `MainActivity.java`.
## Status
Accepted