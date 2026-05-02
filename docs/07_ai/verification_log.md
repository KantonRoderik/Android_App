# Verification Log

Ahol az AI megoldást javasolt (vagy egy algoritmust generált), ott ellenőriztük a működést.

| AI állítás / javaslat | Kockázat | Ellenőrzés módja | Eredmény (pass/fail) | Következtetés / Változás |
| :--- | :--- | :--- | :--- | :--- |
| BMR számítás (Mifflin-St Jeor) | Helytelen számítás miatt irreális napi célok. | Unit Test megírása (`NutritionCalculatorTest`) manuálisan kiszámolt referenciaértékekkel. | **PASS.** | Az algoritmus megfelelő, a kódban maradt. |
| Firebase WriteBatch az összegzéshez. | Konkurencia hiba vagy adatszivárgás, ha megszakad a net. | `FirestoreRepositoryTest` implementálása Mockito-val (verify batch metódusok). | **PASS.** | Biztonságos offline/tranzakcionális írás megoldva. |
| Gemini Markdown string tisztítása Regex-szel (`(?s)```...`). | Az app összeomlik a `Gson` parsolásánál. | Runtime debugolás a fizikai eszközön egy API hívással. | **PASS.** | Kivételkezelés (try-catch) hozzáadva az összeomlás elkerülésére. |
| Retrofit és OpenFoodFacts API implementáció. | Az URL felépítése vagy az objektum mapelés hibás. | Végpont manuális meghívása, majd `NetworkIntegrationTest` MockWebServerrel. | **PASS.** | Az integráció stabil, kikerült élesbe. |
| Kalória égetés számítása MET értékből. | Túlértékelt vagy alulértékelt edzési eredmények. | Szakirodalom (ACSM) és egy specifikus teszt bekerülése a `NutritionCalculatorTest`-be. | **PASS.** | Az `ExerciseFragment`-ben lecseréltük az elavult számítást a centralizált metódusra. |
| UI Progress % számítás (`UIUtils`). | Nullával való osztásnál Exception. | `UIUtilsTest.java` írása 0 és negatív értékekre. | **PASS.** | 0-t ad vissza biztonságosan. |
| Firestore Rules a saját uid-ra. | Mások adatainak megtekinthetősége. | Egy dummy fiókkal való bejelentkezés és manuális hálózati POST lekérés hiba ellenőrzése. | **PASS.** HTTP 403 Forbidden. | Biztonsági kapu aktív. |
| Material 3 Átlátszó színek (`#CC...`). | Szövegek olvashatatlanná válnak a Glassmorphismen. | Fizikai Android eszközön Accessibility Scanner alkalmazása a kontrasztra. | **PASS.** (Néhány betűméretet növelni kellett manuálisan). | Marad az átlátszó kártya dizájn. |
| Firebase Auth gyenge jelszó üzenet. | Csak backend logban jelenik meg a hiba. | Jelszó mező validálása kevesebb mint 6 karakterrel futásidőben. | **PASS.** Toast üzenet értesíti a felhasználót. | Integráció jó, az UI is reagál. |
| Diéta sablon arányok (pl. Keto = 70% Zsír). | A makrók nem adnak ki 100%-ot a kalóriából. | A `DailyGoalsSzerkesztes` Activity-ben a számok manuális újra-szorzása papíron. | **PASS.** | A sablon kiválasztó tökéletesen átírja az input mezőket. |
