# ADR-008: Error Handling and User Feedback Strategy

## Context
When an API call fails, or the user enters invalid data, the app must respond predictably. Silent failures or generic crashes ruin the UX. We need a standardized way to handle errors across the UI.

## Decision
We chose a **Client-Side Validation and Toast/Dialog fallback strategy** coupled with Firebase's native offline error codes.

## Rationale
- **Immediate Feedback:** Client-side validation (e.g., checking if quantity is > 0) prevents unnecessary network calls and provides instant feedback via Toasts.
- **Graceful Degradation:** When an API (like Gemini) fails, instead of crashing, we catch the exception and show an `AlertDialog` explaining the issue.
- **Offline First:** Instead of showing an error when there's no network for saving data, we intercept `FirebaseFirestoreException.Code.UNAVAILABLE` and inform the user that the data is "Saved offline" rather than "Failed".

## Alternatives considered
- **Global Error Handler (UncaughtExceptionHandler):** Rejected. Too broad, doesn't allow for context-specific recovery.
- **Snackbars with Retry actions:** Considered, but Toast + Dialog combination was deemed simpler and sufficient for MVP.

## Consequences
- **Positive:** No app crashes on API timeout; user is always informed.
- **Negative:** Error strings must be maintained across multiple Activities.

## Verification
- Verified by turning off Wi-Fi and saving an entry. App successfully triggers the "Saved offline" fallback without crashing.
## Status
Accepted