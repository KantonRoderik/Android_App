# Error Handling and State Management

## Cél
Az alkalmazás determinisztikusan viselkedjen hiba esetén, és a felhasználó mindig érthető visszajelzést kapjon (ne stack trace-t vagy összeomlást tapasztaljon).

## Hibakategóriák és Kezelésük

1. **Hálózati / Offline hibák**
   * *Tünet:* Nincs internet kapcsolat API hívás (Gemini, OpenFoodFacts) vagy első Firestore lekérés során.
   * *Kezelés:* A Firestore beépített offline cache-t használ (a `SetOptions.merge()` hívások lokálisan tárolódnak). API hívások esetén Toast üzenet értesít az internet szükségességéről.

2. **Validációs hibák (Input)**
   * *Tünet:* A felhasználó negatív számot, üres mezőt vagy rossz jelszót ad meg.
   * *Kezelés:* Kliens-oldali validáció a mentés előtt. *Példa:* `AddFoodActivity` nem enged üres mennyiséget. Visszajelzés: `Toast.makeText(..., R.string.error_invalid_number, ...)`.

3. **Autentikációs hibák**
   * *Tünet:* Rossz jelszó, foglalt email cím.
   * *Kezelés:* Firebase Auth exception elkapása, magyar nyelvű barátságos hibaüzenet (S01/S02 képernyőkön).

4. **Külső API / AI Hibák (Hallucináció / Timeout)**
   * *Tünet:* A Gemini API nem tud JSON-t generálni, vagy az OpenFoodFacts nem találja a vonalkódot.
   * *Kezelés:* Timeout vagy parse exception esetén `showErrorDialog` hívódik meg ("Hiba történt a feldolgozás során"). A válasz validálása (pl. negatív MET érték kiszűrése).

## User-facing üzenetek elvei
A hibaüzenetek nyelve megegyezik a UI nyelvével (Hu/En), és a `strings.xml`-ből kerülnek betöltésre (pl. `@string/error_generic`).

## Logolás
Hibák esetén a fejlesztői konzolra (Logcat) a `Log.e(TAG, "message", exception)` formátumban kerülnek bejegyzések, amik tartalmazzák a stack trace-t, de **nem tartalmaznak PII-t** (Személyes azonosításra alkalmas adatot, pl. email címet vagy testsúlyt).
