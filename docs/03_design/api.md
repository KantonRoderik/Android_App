# API Reference

This project primarily uses **Firebase (Firestore & Auth)** as a serverless backend. External data is fetched via the **OpenFoodFacts REST API** and the **Google Gemini API**.

---

## 1. External API: OpenFoodFacts

The application uses Retrofit to fetch product data by barcode.

### Get Product by Barcode
- **URL:** `https://world.openfoodfacts.org/api/v0/product/{barcode}.json`
- **Method:** `GET`
- **Authentication required:** No
- **Path Parameters:**
    - `barcode`: The EAN/UPC barcode of the product.
- **Success Response:**
    - **Code:** 200 OK
    - **Content:**
      ```json
      {
        "status": 1,
        "product": {
          "product_name": "String",
          "nutriments": {
            "energy-kcal_100g": 0.0,
            "carbohydrates_100g": 0.0,
            "fat_100g": 0.0,
            "proteins_100g": 0.0
          }
        }
      }
      ```

---

## 2. External API: Google Gemini (Generative AI SDK)

Az alkalmazás a hivatalos natív SDK-t használja a Gemini hívásokhoz, mely a háttérben RPC hívásokat generál. Az elvárt kimenet minden esetben szigorú JSON struktúra.

- **Authentication:** Igen (`GEMINI_API_KEY` a `local.properties` fájlban).
- **Modell:** `gemini-1.5-flash`

### Étel Azonosítás (FoodFragment)
- **Cél:** Egy felhasználó által beírt étel (pl. "Rántott sajt") tápérték adatainak legenerálása 100g-ra vonatkoztatva.
- **System Instruction:**
  *"Act as a fitness and nutrition expert. Provide nutrition data for 100g and common units. Return ONLY a raw JSON object. Fields: name, calories, carbs, protein, fat, commonUnits (array)."*
- **Dynamic Prompt:**
  `Provide nutrition data for: {Étel_neve}`
- **Expected Response (Parsed to DTO):**
  ```json
  {
    "name": "Rántott sajt",
    "calories": 310,
    "carbs": 22.5,
    "protein": 14.0,
    "fat": 18.0,
    "commonUnits": [{"name": "adag", "weightG": 150}]
  }
  ```

### Mozgás és MET Azonosítás (ExerciseFragment)
- **Cél:** Egy beírt edzésforma (pl. "Fűnyírás") MET (Metabolic Equivalent of Task) szorzójának kinyerése, amit utána a rendszer kliens-oldalon kalóriává konvertál.
- **System Instruction:**
  *"Act as a fitness expert. Provide MET (Metabolic Equivalent of Task) value for exercises. Return ONLY a single raw JSON object. Example format: {"name": "Futás", "met_value": 8.0, "category": "Kardió"}. Do not include any other text or markdown outside the JSON."*
- **Dynamic Prompt:**
  `Provide MET data for: {Mozgás_neve}`
- **Expected Response (Parsed to DTO):**
  ```json
  {
    "name": "Fűnyírás",
    "met_value": 5.5,
    "category": "Házimunka"
  }
  ```

---

## 3. Internal Data Structure (Firestore)

While accessed via the Firebase SDK (not HTTP), the following collections represent the data "endpoints".

### Users Collection
- **Path:** `/users/{uid}`
- **Authentication:** Igen (Firebase Auth, `request.auth.uid == resource.id`)
- **Fields:** `email`, `nev`, `suly`, `magassag`, `kor`, `nem`, `dailyGoals`, `onboarding_complete`

### Daily Entries Collection
- **Path:** `/users/{uid}/dailyEntries/{date}`
- **Authentication:** Igen
- **Format:** `date` is `YYYY-MM-DD`
- **Fields:**
    - `consumedFoods`: Map of timestamps to `ConsumedFood` objects.
    - `exercisesDone`: Map of timestamps to `Exercise` objects.
    - `totalCalories`, `totalCarbs`, `totalFat`, `totalProtein`, `totalWater`, `totalCaloriesBurned`

### Global Catalogs (Cached)
- **Path:** `/foods/{foodId}` & `/exercises/{exerciseId}`
- **Authentication:** Igen (Read-only for users, or created by AI if not exists).
