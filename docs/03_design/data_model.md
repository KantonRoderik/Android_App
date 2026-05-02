# Adatmodell (Data Model)

Az alkalmazás adatperzisztenciát használ a Cloud Firestore-ban. A séma dokumentum-orientált (NoSQL), a gyors és egyszerű lekérdezésekre optimalizálva.

## 1. Entitások és Kollekciók

### `users` kollekció
- **Cél:** A felhasználó profiladatainak és napi céljainak tárolása.
- **Kulcs (ID):** Firebase Auth UID (A biztonsági szabályok ez alapján védenek).
- **Mezők:**
  - `email` (String)
  - `nev` (String)
  - `kor`, `suly`, `magassag`, `nem` (String/Number)
  - `onboarding_complete` (Boolean)
  - `selectedTemplate` (String - pl. "KETOGENIC")
  - `dailyGoals` (Map/Object)
    - `calories`, `carbs`, `fat`, `protein`, `water` (Double)

### `dailyEntries` (Subcollection a `users` alatt)
- **Cél:** A napi fogyasztások és edzések tárolása adott dátumra.
- **Path:** `/users/{uid}/dailyEntries/{date}`
- **Kulcs (ID):** ISO Dátum formátum (pl. `2024-05-15`).
- **Mezők:**
  - `date` (String)
  - `totalCalories`, `totalCarbs`, `totalFat`, `totalProtein`, `totalWater`, `totalCaloriesBurned` (Double)
  - `consumedFoods` (Map<String, ConsumedFood>): Kulcs a timestamp (`System.currentTimeMillis()`), az érték az étel objektum.
  - `exercisesDone` (Map<String, Exercise>): Kulcs a timestamp, az érték a mozgás objektum.

### `foods` és `exercises` kollekciók (Globális katalógus)
- **Cél:** Gyorskereső (Autocomplete) működtetése. Ide mentjük be azokat az ételeket/mozgásokat, amiket az AI már egyszer legenerált, vagy a felhasználó fixen bevitt.
- **Kulcs (ID):** Sanitizált név (pl. `Rántott hús`).

## 2. Kapcsolatok és Integritás
- **1:N kapcsolat:** A `users` dokumentum és a hozzá tartozó `dailyEntries` subcollection.
- **Integritás:** A tranzakciók egyidejű frissítése érdekében (pl. új étel hozzáadásakor nő a `totalCalories` és bekerül az étel a map-be) **Firestore WriteBatch**-et használunk. Ha az egyik művelet elbukik, az egész visszavonódik.

## 3. Adatélettartam és Privacy
- Az adatokat addig tároljuk, amíg a felhasználó fiókja létezik.
- Ha a felhasználó kéri a fiókja törlését, a GDPR elvek alapján egy Cloud Function (jövőbeli terv) törli a teljes `users/{uid}` dokumentum-fát.
- PII (személyes adatok) sosem kerülnek globális kollekciókba (`foods`), oda csak anonim adatok (pl. 100g rizs makrói) mennek.
