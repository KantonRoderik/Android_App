# SZAKDOLGOZAT - Kalóriaszámláló (Calorie Tracker)
An Android application for beginners to track daily nutrition and discover recipes.

## Main Features
- **Firebase Auth**: Secure email/password and Google Sign-in.
- **Dynamic Logging**: Track food intake, water, and weight with real-time Firestore sync.
- **AI Food Recognition**: Integrated Gemini AI to calculate nutrition from text.
- **Progress Visualization**: Dynamic progress bars for calories, macros, and daily streaks.
- **Barcode Scanner**: Quickly add food items using the ML Kit barcode scanner.
- **Offline Support**: Firestore persistence ensures data is saved without internet.

## Tech Stack
- **Frontend**: Native Android (Java) with XML layouts.
- **Database/Backend**: Firebase Firestore (NoSQL), Firebase Authentication.
- **AI/ML**: Google Gemini AI (Generative AI SDK), ML Kit Barcode Scanning.
- **Libraries**: MPAndroidChart, Retrofit, Glide.

## Architecture
- **Pattern**: MVVM (Model-View-ViewModel)
- **Data Layer**: Repository pattern for decoupling Firestore logic.
- **View Binding**: Type-safe access to layout components.

## Quickstart (Local)
1. **Clone**: `git clone https://github.com/KantonRoderik/Android_App.git`
2. **Setup SDK**: Open in Android Studio (Ladybug or newer recommended).
3. **Firebase**: Place your `google-services.json` in the `app/` folder.
4. **API Key**: Add `GEMINI_API_KEY=your_key` to `local.properties`.
5. **Sync & Run**: Click "Sync Project with Gradle Files", then "Run" (Shift+F10).
   - *Expected output*: App launches on emulator/device showing Login screen.

## Test Run
- **All Tests**: `./gradlew check`
- **Unit Tests**: `./gradlew :app:testDebugUnitTest`
- **Instrumented Tests**: `./gradlew :app:connectedDebugAndroidTest`

## Configuration
- **GEMINI_API_KEY**: Required in `local.properties` for AI features.
- **google-services.json**: Required for Firebase Auth and Firestore.
- **Java Version**: Uses JDK 17 (set in `build.gradle.kts`).

## Demo / Try It
- **Test User**: `testuser@example.com` / `Password123!` (If pre-seeded in Firebase).
- **Offline Mode**: Disable Wi-Fi to test local persistence. AI and Barcode scanner not available during offline mode.

## Troubleshooting
- **Error: `Unresolved reference: ViewInjector`**: Escape `$` in `build.gradle.kts` as `\$`.
- **Error: `Method myPid in android.os.Process not mocked`**: Ensure unit tests use the `FirestoreRepository(db, auth)` constructor with mocks.
- **Firebase Not Working**: Verify `google-services.json` matches your Firebase project.
- **Gradle Sync Failure**: Ensure `JAVA_HOME` points to JDK 17.
- **AI Recognition Fails**: Check if `GEMINI_API_KEY` is valid and has "Generative AI API" enabled.
