# ADR-001: Firebase as Backend-as-a-Service

## Context
The application requires user authentication, cloud data storage, and analytics. We needed to choose between building a custom backend (e.g., Spring Boot or Node.js) or using a managed Backend-as-a-Service (BaaS) solution.

## Decision
We chose **Google Firebase** (Authentication, Firestore, and Analytics) as our backend infrastructure.

## Rationale
- **Rapid Development**: It eliminates the need to manage infrastructure or develop custom API endpoints for basic CRUD operations.
- **Real-time Synchronization**: Firestore provides native support for real-time updates, which is essential for a seamless nutritional logging experience.
- **Cost-Effectiveness**: The "Spark" free tier is sufficient for the scope of this project.
- **Security**: Firebase Auth provides industry-standard security for logins (Email/Password, Google Sign-in) without manual implementation of sensitive security logic.

## Alternatives considered
- **Custom Spring Boot Backend**: Rejected due to the significant overhead in development time and the complexity of hosting/maintenance for a single-person project.
- **AWS Amplify**: Evaluated but rejected as Firebase offers a more intuitive experience and better documentation for native Android development.

## Consequences
- **Vendor Lock-in**: The app becomes dependent on the Google ecosystem.
- **Query Limitations**: Complex queries (like full-text search) are harder to implement in Firestore compared to traditional relational databases.

## Status
Accepted
