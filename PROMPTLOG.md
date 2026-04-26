# Prompt Log

Ez a fájl tartalmazza a fejlesztés során használt kulcsfontosságú promptokat, az MI által generált kódokat és azokon végzett manuális módosításaimat, bizonyítva a mérnöki döntéshozatalt.

## Bejegyzések

### 1. Projekt alapozás és navigáció
**Dátum:** 2024-10-15
**Prompt:** "Hozz létre egy alap Android projektet ViewBinding-al és egy BottomNavigationView-val, ami 3 fragmentet kezel: Főoldal, Napló, Profil."
**Válasz összefoglalása:** Az MI legenerálta az alap Activity-t és a Fragment-eket egy alap NavGraph-al.
**Módosításom:** A generált navigációt kiegészítettem egy egyedi `NavOptions` beállítással, hogy az animációk gördülékenyebbek legyenek a fragmentek között, mert az alap kód túl darabos volt. Implementáltam a `StateRestoration`-t, hogy a fragmentek állapota megmaradjon váltáskor.

### 2. Firebase hitelesítés integrálása
**Dátum:** 2024-10-20
**Prompt:** "Generálj egy LoginActivity-t Firebase Auth email/jelszó bejelentkezéssel."
**Válasz összefoglalása:** Egy alapvető email-jelszó ellenőrzést kaptam, ahol a UI közvetlenül hívta a Firebase-t.
**Módosításom:** Áthelyeztem a logikát egy `AuthViewModel`-be az MVVM elvnek megfelelően. Hozzáadtam egy extra validációs réteget a jelszó erősségére (regex), és implementáltam a Google Sign-In opciót is, amit az MI eredetileg kihagyott.

### 3. Firestore adatmodell tervezése
**Dátum:** 2024-10-25
**Prompt:** "Hogyan strukturáljam a Firestore-t egy étkezési naplóhoz, ahol napi bontásban vannak az ételek?"
**Válasz összefoglalása:** Javaslat egy `users -> {userId} -> entries -> {date}` struktúrára.
**Módosításom:** Átalakítottam a struktúrát úgy, hogy a dátum ne csak egy dokumentum ID legyen, hanem egy field is (Timestamp), így könnyebb "range query"-ket futtatni a heti jelentésekhez. Bevezettem egy `subcollection`-t az ételeknek a napon belül a jobb skálázhatóság érdekében.

### 4. Barcode Scanning CameraX-szel
**Dátum:** 2024-11-02
**Prompt:** "Írj egy CameraX Analyzer-t, ami ML Kit segítségével vonalkódot olvas."
**Válasz összefoglalása:** Egy alapvető `ImageAnalysis.Analyzer` implementáció, ami minden frame-et feldolgoz.
**Módosításom:** Bevezettem egy "throttling" mechanizmust (500ms késleltetés a feldolgozások között), hogy ne terhelje túl a CPU-t. Kiegészítettem egy "auto-focus" funkcióval, és hozzáadtam egy rezgő visszajelzést (Vibrator) sikeres beolvasáskor.

### 5. Gemini AI integráció és Prompt Engineering
**Dátum:** 2024-11-10
**Prompt:** "Hogyan tudom a Google Gemini SDK-t használni Androidon, hogy táplálkozási tanácsot kérjek egy étellista alapján?"
**Válasz összefoglalása:** Megadta a `generativeModel.generateContent` hívás alapjait.
**Módosításom:** Bevezettem egy szigorú "system prompt" réteget, ami korlátozza a Gemini válaszait szakmai keretek közé. Megoldottam a JSON formátumú válaszok kényszerítését, hogy az app programozottan fel tudja dolgozni a tanácsokat (pl. kiemelt tápanyag-hiányok).

### 6. Retrofit és OpenFoodFacts API hiba kezelés
**Dátum:** 2024-11-15
**Prompt:** "Készíts egy Retrofit interfészt az OpenFoodFacts JSON API-hoz."
**Válasz összefoglalása:** Legenerálta a DTO osztályokat és az interfészt.
**Módosításom:** A generált kód nem kezelte a hálózati hibákat vagy az API limit túllépést. Implementáltam egy `Interceptor`-t, ami automatikusan újrapóbálkozik hiba esetén, és egy `Result` wrapper osztályt a ViewModel-ben a hibák elegáns megjelenítéséhez a UI-n.

### 7. MPAndroidChart adatvizualizáció
**Dátum:** 2024-11-20
**Prompt:** "Mutass egy példát PieChart használatára MPAndroidChart-tal a napi makrotápanyagokhoz."
**Válasz összefoglalása:** Alap diagram rajzolás fix adatokkal.
**Módosításom:** Összekötöttem a LiveData-val a Firestore-ból, és implementáltam egy egyedi `ValueFormatter`-t, hogy a százalékok mellett a gramm értékek is megjelenjenek. A színeket a Material 3 dinamikus színeihez igazítottam.

### 8. Biztonságos adattárolás (EncryptedSharedPreferences)
**Dátum:** 2024-11-25
**Prompt:** "Hogyan mentsem el a felhasználó alapbeállításait (pl. cél kalória) helyileg?"
**Válasz összefoglalása:** Simát `SharedPreferences` javaslat.
**Módosításom:** Biztonsági okokból (mivel egészségügyi adatokról van szó) áttértem a `Security` könyvtár `EncryptedSharedPreferences` megoldására. Ez garantálja, hogy a rootolt eszközökön se lehessen egyszerűen ellopni a felhasználói profil adatokat.

### 9. Edge-to-edge UI és Insets kezelése
**Dátum:** 2024-12-01
**Prompt:** "Hogyan tehetem az alkalmazást teljes kijelzőssé (edge-to-edge) Android 14-en?"
**Válasz összefoglalása:** `enableEdgeToEdge()` hívás javaslata.
**Módosításom:** A generált kód miatt a gombok a rendszersáv mögé kerültek. Manuálisan implementáltam a `ViewCompat.setOnApplyWindowInsetsListener`-t minden érintett layout-nál, hogy a UI elemek dinamikusan igazodjanak a notch-hoz és a navigációs sávhoz.

### 10. Unit tesztek és "Hostile Testing"
**Dátum:** 2024-12-05
**Prompt:** "Írj unit tesztet egy kalóriaszámoló segédfüggvényhez."
**Válasz összefoglalása:** Alap JUnit 4 teszt pozitív esetekre.
**Módosításom:** Alkalmaztam a "Hostile Tester" megközelítést: teszteltem extrém nagy számokkal (overflow védelem), negatív kalória értékekkel (input validáció) és null értékekkel. Kijavítottam egy bugot, ahol a függvény kerekítési hibát vétett nagyon kicsi fehérje adagoknál.

### 11. Custom View a Vízbevitelhez
**Dátum:** 2024-12-10
**Prompt:** "Hogyan készítsek egy egyedi animált hullám effektet egy ProgressBar-hoz a vízbevitel jelzésére?"
**Válasz összefoglalása:** Canvas rajzolási kód egy szinusz hullámhoz.
**Módosításom:** Az MI kódja túl sok CPU-t fogyasztott, mert minden frame-nél újraszámolta a hullámot. Optimalizáltam `Path` cache-eléssel és `ValueAnimator` használatával, így stabil 60 FPS-sel fut gyengébb hardveren is.

### 12. Többnyelvűség (Localization) támogatása
**Dátum:** 2024-12-12
**Prompt:** "Segíts lefordítani az app szövegeit angolra és magyarra, és hogyan váltsak nyelvet programozottan?"
**Válasz összefoglalása:** String resource fájlok és Locale beállítás kódja.
**Módosításom:** Az MI által javasolt `Locale.setDefault` módszer Android 13 felett már nem működik stabilan. Áttértem az új `AppCompatDelegate.setApplicationLocales()` API-ra, ami a rendszer szintjén kezeli a nyelvváltást.

### 13. Deep Link integráció
**Dátum:** 2024-12-15
**Prompt:** "Hogyan tudok Deep Linket csinálni, ami egy specifikus étel oldalára visz?"
**Válasz összefoglalása:** Intent filter beállítások a Manifest-ben.
**Módosításom:** Kiegészítettem a `Safe Args` használatával a Navigation Component-en belül, hogy a deep link-ből érkező ID-kat típusbiztosan kezeljem, és kezeltem azt az esetet is, ha a felhasználó nincs bejelentkezve (auth guard).

### 14. Performance Profiling és Memory Leak fix
**Dátum:** 2024-12-18
**Prompt:** "Miért omlik össze az app, ha sokszor nyitom meg a kamerát?"
**Válasz összefoglalása:** Általános tanácsok a memória kezelésről.
**Módosításom:** A LeakCanary segítségével rájöttem, hogy az MI által korábban generált `CameraProvider` nem lett megfelelően leállítva. Manuálisan implementáltam a `LifecycleObserver`-t, hogy garantáltan felszabaduljon a kamera erőforrás a fragment elhagyásakor.

### 15. Offline szinkronizáció Firestore-ral
**Dátum:** 2024-12-20
**Prompt:** "Hogyan működik az offline mód Firestore-ban?"
**Válasz összefoglalása:** "Alapból be van kapcsolva."
**Módosításom:** Ez nem volt elég. Implementáltam egy egyedi szinkronizációs állapotjelzőt a UI-ra, ami mutatja a felhasználónak, ha az adatai még csak helyben vannak mentve, és értesítést küld, ha a feltöltés sikeresen megtörtént (Background Task).
