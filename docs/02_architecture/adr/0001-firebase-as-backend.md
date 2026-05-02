# ADR-001: Choice of Firebase (Firestore & Auth) as the backend and primary data store

## Context
The application needs to handle user authentication and store nutrition/exercise logs that are synchronized across devices. We require a solution that supports offline-first usage (since users might log food without internet) and real-time updates for the dashboard.

## Decision
We have chosen **Firebase** (specifically **Cloud Firestore** for data and **Firebase Auth** for identity management) as the primary backend infrastructure.

## Rationale
- **Offline Persistence:** Firestore provides built-in support for local data persistence and automatic synchronization when the device regains internet access.
- **Development Speed:** As a serverless "Backend-as-a-Service" (BaaS), Firebase eliminates the need to build and maintain a custom REST API and database server.
- **Real-time Sync:** The Snapshot Listener mechanism allows the UI to update automatically whenever data changes in the cloud.
- **Security:** Firebase provides a robust security rules system to protect user data directly at the database level.
- **Integration:** Excellent first-party support for Android/Java development.

## Alternatives considered
- **PostgreSQL + Custom REST API:** Rejected due to the high overhead of building a synchronization engine for offline support and managing server infrastructure.
- **SQLite (Standalone):** Rejected because it lacks cloud backup and cross-device synchronization.

## Consequences
- **Easier:** Implementing authentication, data sync, and offline support is significantly faster.
- **Harder:** Complex analytical queries are more difficult than in SQL. We are subject to Firebase's pricing model and vendor lock-in.

## Verification
- Firebase offline sync verified via `NetworkIntegrationTest.java` (simulated).
- Real-time updates verified in `MainActivity` snapshot listeners.
