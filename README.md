# SZAKDOLGOZAT - Fitness Tracker

An AI-powered Android application for tracking daily nutrition and exercise, featuring offline-first synchronization.

## Main Features
- **AI Food & Exercise Identification**: Gemini API integration to estimate nutritional values and calculate MET for unknown foods and workouts.
- **Barcode Scanner**: Quickly add store-bought products using the camera and the OpenFoodFacts API.
- **Dynamic Daily Log**: Live tracking of calories, macros (carbs, protein, fat), and water intake with progress bars.
- **Diet Templates & BMR Calculation**: Automatically generate daily goals based on body metrics and predefined templates (e.g., Keto, High Protein).
- **Offline Mode**: Firestore persistence ensures you can log data even without an internet connection.
- **Firebase Auth**: Secure email/password and google Sign-in.
- **Progress Visualization**: Dynamic progress bars for calories, macros and daily streaks.

## Tech Stack
- **Frontend**: Android (Java), Material 3 Design, View Binding
- **Backend**: Firebase Authentication, Cloud Firestore
- **AI/API**: Google Gemini 1.5 Flash SDK, ML Kit, Retrofit 2, OkHttp
- **Architecture**: Classic Activity + Repository Pattern

## Quickstart (Local Run)
1. Clone the repository: `git clone https://github.com/KantonRoderik/Android_App.git`
2. Open the project folder in Android Studio (Iguana / Ladybug version recommended).
3. Place your downloaded `google-services.json` file into the `app/` directory.
4. Create a `local.properties` file in the root directory and add your Gemini API key:
   `GEMINI_API_KEY="your_api_key_here"`
5. Sync Project with Gradle Files, then click the **Run 'app'** button.
   *Expected output:* The app launches on your emulator or physical device showing the Login screen.

## Test Run
The project contains Unit and Integration tests utilizing Mockito and MockWebServer.
To run tests via the command line (Terminal):
```bash
./gradlew testDebugUnitTest
```
*Expected output:* `BUILD SUCCESSFUL` (~30 tests passing without errors).

## Configuration
- `google-services.json`: Required for Firebase Auth and Firestore to work. Download it from the Firebase Console.
- `GEMINI_API_KEY`: Required environment variable in `local.properties`. Without it, the application will fail to compile.
- **Environment:** Java 17 and Android SDK 35. No external server ports need to be opened.

## Documentation
The complete engineering and UX documentation is located in the `docs` folder using a Docs-as-Code approach.
👉 **[Engineering Documentation Index](docs/00_index.md)**

## Troubleshooting
- **Error: `google-services.json is missing`**: Download the file from your Firebase console and ensure it is placed exactly in the `app` folder.
- **Error: `AI response cannot be processed`**: Check if your API key in `local.properties` is valid and if you have available quota in Google AI Studio.
- **Progress bars stuck at zero after login**: No network connection. The Firebase offline cache is empty; please ensure internet access for the initial data sync.

## Workflow
- **Error: `google-services.json is missing`**: Download the file from your Firebase console and ensure it is placed exactly in the `app` folder.
- **Error: `AI response cannot be processed`**: Check if your API key in `local.properties` is valid and if you have available quota in Google AI Studio.
- **Progress bars stuck at zero after login**: No network connection. The Firebase offline cache is empty; please ensure internet access for the initial data sync.