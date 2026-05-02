# Observability (Megfigyelhetőség)

Mivel az alkalmazás egy vastag kliens (mobil applikáció) és közvetlenül a Firebase szolgáltatásait használja, a hagyományos szerveroldali metrikák helyett a Firebase Analytics, a Logcat és a Firebase Crashlytics adja a megfigyelhetőség alapját.

## 1. Logolás (Logging)
* **Szintek:**
  * `Log.e (ERROR):` Csak kritikus hibák (pl. Firebase tranzakció elbukik, Gson parsing exception).
  * `Log.w (WARN):` Nem várt, de kezelt állapotok (pl. hálózati timeout, offline mentés fallbackje).
  * `Log.i (INFO):` Főbb életciklus események (pl. Sikeres bejelentkezés).
* **PII (Személyes adat) védelem:** Szigorúan tilos jelszavakat, tokeneket, vagy specifikus felhasználói adatokat (testsúly, bevitt egyéni nevek) a konzolra vagy a Crashlytics felé logolni.

## 2. Healthcheck (Egészségügyi állapot)
A rendszer egészségét közvetlenül a Google szolgáltatások státusza határozza meg.
* Kliens oldalon: Alkalmazás induláskor a Firebase Auth SDK ellenőrzi a tokent (`auth.getCurrentUser()`). Ha `null` vagy lejárt, a felhasználó a Login (S01) képernyőre kerül.
* Ha nincs internet, a Firebase Firestore SDK transzparensen kezeli a hálózati állapotot, és "offline cache" módból dolgozik. Ezt a `FoodFragment`-ben a `FirebaseFirestoreException.Code.UNAVAILABLE` exception elkapása bizonyítja.

## 3. Metrikák (Tervezett / Firebase Console alapú)
* **Auth Success Rate:** Sikeres és sikertelen (hibás jelszó) login kísérletek aránya.
* **AI API Latency (p95):** Mennyi idő telik el a "AI Azonosítás" gomb lenyomása és az eredmény (Gson parse) között. Ez kritikus a UX szempontjából (ha > 5 másodperc, a user türelmetlen lesz).
* **Crash Free Sessions:** Naponta a hibamentes alkalmazás-futtatások aránya (Cél: > 99.5%).

## 4. Debugging Guide
1. **Lokális tesztelésnél:** Android Studio `Logcat` ablak használata. Szűrés: `package:com.example.szakdolgozat level:error`.
2. **Hálózati kérések (OpenFoodFacts):** Az OkHttp hívásokhoz `HttpLoggingInterceptor` bekötése (csak `DEBUG` build type esetén engedélyezett).
3. **Firestore adatok debugolása:** Firebase Emulator Suite használata, vagy a Firebase Console > Firestore Data tab megnyitása a teszt user UID-je alatt (`/users/{uid}/dailyEntries`).
