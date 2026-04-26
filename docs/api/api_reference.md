# API Reference

This project interacts with two main backend services: **Google Firebase (Firestore/Auth)** via the native Android SDK and **Open Food Facts** via Retrofit.

## 1. Internal Firebase Data Model (Firestore)
The app uses the Firebase native SDK, which maps to these logical endpoints within the `users` collection.

### Get User Profile
- **Logical Path**: `users/{userId}`
- **Method**: `GET` (SDK `get()`)
- **Authentication**: Yes (Firebase Auth)
- **Response**:
    - `200 OK`: JSON object with user fields (`email`, `nev`, `suly`, `dailyGoals`, etc.)
    - `403 Forbidden`: User not logged in or permission denied.

### Update Daily Entry
- **Logical Path**: `users/{userId}/dailyEntries/{date}`
- **Method**: `SET/UPDATE` (SDK `set(..., SetOptions.merge())`)
- **Authentication**: Yes
- **Request Body**:
  ```json
  {
    "totalCalories": 1500.0,
    "totalWater": 2.5,
    "consumedFoods": {
        "timestamp123": { "name": "Apple", "calories": 52 }
    }
  }
  ```
- **Response**:
    - `Success`: Local update with cloud sync.

---

## 2. External API: Open Food Facts
The app uses Retrofit to fetch product data by barcode.

### Get Product by Barcode
- **URL**: `GET https://world.openfoodfacts.org/api/v0/product/{barcode}.json`
- **Authentication**: No
- **Response (200 OK)**:
  ```json
  {
    "status": 1,
    "product": {
      "product_name": "Nutella",
      "nutriments": {
        "energy-kcal_100g": 539,
        "proteins_100g": 6.3,
        "fat_100g": 30.9,
        "carbohydrates_100g": 57.5
      }
    }
  }
  ```
- **Response (404 Not Found)**:
  ```json
  { "status": 0, "status_verbose": "product not found" }
  ```

---

## 3. External API: Google Gemini AI
Used for intelligent food recognition via the `GenerativeModel` SDK.

### Content Generation (Food Analysis)
- **Service**: Google AI Edge SDK
- **Authentication**: API Key (`GEMINI_API_KEY`)
- **Request**: Text prompt (e.g., "Analyze: 1 slice of pizza") or Image.
- **Example Success Response**:
  ```json
  {
    "candidates": [{
      "content": {
        "parts": [{ "text": "{ \"name\": \"Pizza\", \"calories\": 285 ... }" }]
      }
    }]
  }
  ```
