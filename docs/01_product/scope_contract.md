# Scope Contract

## MVP User Story-k és Elfogadási Kritériumok

### 1. Regisztráció és Onboarding
**Story:** Új felhasználóként regisztrálni akarok, hogy elmenthessem a testadataimat és napi célokat kaphassak.
**Elfogadási kritériumok:**
- [x] Given a user on the Register screen, When valid email/password is entered, Then Firebase account is created.
- [x] Given a new user, When entering height, weight, age and gender, Then BMR and TDEE are automatically calculated.

### 2. Napi makrók követése (Dashboard)
**Story:** Felhasználóként látni akarom a napi céljaimat és az eddigi fogyasztásomat.
**Elfogadási kritériumok:**
- [x] Given the Dashboard, Then progress bars for Calories, Carbs, Fat, Protein, and Water must be displayed.
- [x] Given offline status, When opening the Dashboard, Then cached Firestore data is shown.

### 3. Étel rögzítése (Manuális és AI)
**Story:** Felhasználóként ételt akarok rögzíteni AI azonosítással vagy manuális kereséssel.
**Elfogadási kritériumok:**
- [x] Given the Add Food screen, When tapping "AI Azonosítás" with a food name, Then Gemini API returns a JSON with nutrition facts.
- [x] When saving the food, Then the total calories and macros on the Dashboard are updated immediately.

### 4. Vonalkódos rögzítés (OpenFoodFacts)
**Story:** Felhasználóként vonalkóddal akarok bolti termékeket rögzíteni.
**Elfogadási kritériumok:**
- [x] Given the Barcode Scanner, When scanning an EAN code, Then product data is fetched via OpenFoodFacts API.

### 5. Mozgás rögzítése
**Story:** Felhasználóként rögzíteni akarom az edzéseimet, hogy lássam az elégetett kalóriát.
**Elfogadási kritériumok:**
- [x] Given the Add Exercise screen, When entering a duration and selecting an exercise, Then calories burned are calculated using MET value * weight * duration.

## Kész definíciója (Definition of Done)
* A funkció implementálva van és UI-n keresztül elérhető.
* Firestore adatbázisba szinkronizálódik (offline perzisztenciával).
* Legalább egy Unit teszt (logika) fedi.
* A kód mentes a hardcoded API kulcsoktól.
