# Prompt Log

Ez a fájl tartalmazza a fejlesztés során használt kulcsfontosságú promptokat, az MI által generált kódokat és azokon végzett manuális módosításaimat, bizonyítva a mérnöki döntéshozatalt.

## Bejegyzések

### 1. Projekt alapozás és navigáció
**Prompt:** "Hozz létre egy alap Android projektet ViewBinding-al és egy BottomNavigationView-val, ami 3 fragmentet kezel: Főoldal, Napló, Profil."
**Válasz összefoglalása:** Az MI legenerálta az alap Activity-t és a Fragment-eket egy alap NavGraph-al.
**Módosításom:** A generált navigációt kiegészítettem egy egyedi `NavOptions` beállítással, hogy az animációk gördülékenyebbek legyenek a fragmentek között, mert az alap kód túl darabos volt. Implementáltam a `StateRestoration`-t, hogy a fragmentek állapota megmaradjon váltáskor.

### 2. Firebase hitelesítés integrálása
**Prompt:** "Generálj egy LoginActivity-t Firebase Auth email/jelszó bejelentkezéssel."
**Válasz összefoglalása:** Egy alapvető email-jelszó ellenőrzést kaptam, ahol a UI közvetlenül hívta a Firebase-t.
**Módosításom:** Áthelyeztem a logikát egy `AuthViewModel`-be az MVVM elvnek megfelelően. Hozzáadtam egy extra validációs réteget a jelszó erősségére (regex), és implementáltam a Google Sign-In opciót is, amit az MI eredetileg kihagyott.

### 3. Firestore adatmodell tervezése
**Prompt:** "Hogyan strukturáljam a Firestore-t egy étkezési naplóhoz, ahol napi bontásban vannak az ételek?"
**Válasz összefoglalása:** Javaslat egy `users -> {userId} -> entries -> {date}` struktúrára.
**Módosításom:** Átalakítottam a struktúrát úgy, hogy a dátum ne csak egy dokumentum ID legyen, hanem egy field is (Timestamp), így könnyebb "range query"-ket futtatni a heti jelentésekhez. Bevezettem egy `subcollection`-t az ételeknek a napon belül a jobb skálázhatóság érdekében.

### 4. Barcode Scanning CameraX-szel
**Prompt:** "Írj egy CameraX Analyzer-t, ami ML Kit segítségével vonalkódot olvas."
**Válasz összefoglalása:** Egy alapvető `ImageAnalysis.Analyzer` implementáció, ami minden frame-et feldolgoz.
**Módosításom:** Bevezettem bir "throttling" mechanizmust (500ms késleltetés a feldolgozások között), hogy ne terhelje túl a CPU-t. Kiegészítettem egy "auto-focus" funkcióval, és hozzáadtam egy rezgő visszajelzést (Vibrator) sikeres beolvasáskor.

### 5. Gemini AI integráció és Prompt Engineering
**Prompt:** "Hogyan tudom a Google Gemini SDK-t használni Androidon, hogy táplálkozási tanácsot kérjek egy étellista alapján?"
**Válasz összefoglalása:** Megadta a `generativeModel.generateContent` hívás alapjait.
**Módosításom:** Bevezettem egy szigorú "system prompt" réteget, ami korlátozza a Gemini válaszait szakmai keretek közé. Megoldottam a JSON formátumú válaszok kényszerítését, hogy az app programozottan fel tudja dolgozni a tanácsokat (pl. kiemelt tápanyag-hiányok).

### 6. Retrofit és OpenFoodFacts API hiba kezelés
**Prompt:** "Készíts bir Retrofit interfészt az OpenFoodFacts JSON API-hoz."
**Válasz összefoglalása:** Legenerálta a DTO osztályokat és az interfészt.
**Módosításom:** A generált kód nem kezelte a hálózati hibákat vagy az API limit túllépést. Implementáltam egy `Interceptor`-t, ami automatikusan újrapóbálkozik hiba esetén, és egy `Result` wrapper osztályt a ViewModel-ben a hibák elegáns megjelenítéséhez a UI-n.

### 7. MPAndroidChart adatvizualizáció
**Prompt:** "Mutass bir példát PieChart használatára MPAndroidChart-tal a napi makrotápanyagokhoz."
**Válasz összefoglalása:** Alap diagram rajzolás fix adatokkal.
**Módosításom:** Összekötöttem a LiveData-val a Firestore-ból, és implementáltam egy egyedi `ValueFormatter`-t, hogy a százalékok mellett a gramm értékek is megjelenjenek. A színeket a Material 3 dinamikus színeihez igazítottam.

### 8. Biztonságos adattárolás (EncryptedSharedPreferences)
**Prompt:** "Hogyan mentsem el a felhasználó alapbeállításait (pl. cél kalória) helyileg?"
**Válasz összefoglalása:** Simát `SharedPreferences` javaslat.
**Módosításom:** Biztonsági okokból (mivel egészségügyi adatokról van szó) áttértem a `Security` könyvtár `EncryptedSharedPreferences` megoldására. Ez garantálja, hogy a rootolt eszközökön se lehessen egyszerűen ellopni a felhasználói profil adatokat.

### 9. Edge-to-edge UI és Insets kezelése
**Prompt:** "Hogyan tehetem az alkalmazást teljes kijelzőssé (edge-to-edge) Android 14-en?"
**Válasz összefoglalása:** `enableEdgeToEdge()` hívás javaslata.
**Módosításom:** A generált kód miatt a gombok a rendszersáv mögé kerültek. Manuálisan implementáltam a `ViewCompat.setOnApplyWindowInsetsListener`-t minden érintett layout-nál, hogy a UI elemek dinamikusan igazodjanak a notch-hoz és a navigációs sávhoz.

### 10. Unit tesztek és "Hostile Testing"
**Prompt:** "Írj unit tesztet bir kalóriaszámoló segédfüggvényhez."
**Válasz összefoglalása:** Alap JUnit 4 teszt pozitív esetekre.
**Módosításom:** Alkalmaztam a "Hostile Tester" megközelítést: teszteltem extrém nagy számokkal (overflow védelem), negatív kalória értékekkel (input validáció) és null értékekkel. Kijavítottam bir bugot, ahol a függvény kerekítési hibát vétett nagyon kicsi fehérje adagoknál.

### 11. Custom View a Vízbevitelhez
**Prompt:** "Hogyan készítsek bir egyedi animált hullám effektet bir ProgressBar-hoz a vízbevitel jelzésére?"
**Válasz összefoglalása:** Canvas rajzolási kód bir szinusz hullámhoz.
**Módosításom:** Az MI kódja túl sok CPU-t fogyasztott, mert minden frame-nél újraszámolta a hullámot. Optimalizáltam `Path` cache-eléssel és `ValueAnimator` használatával, így stabil 60 FPS-sel fut gyengébb hardveren is.

### 12. Többnyelvűség (Localization) támogatása
**Prompt:** "Segíts lefordítani az app szövegeit angolra és magyarra, és hogyan váltsak nyelvet programozottan?"
**Válasz összefoglalása:** String resource fájlok és Locale beállítás kódja.
**Módosításom:** Az MI által javasolt `Locale.setDefault` módszer Android 13 felett már nem működik stabilan. Áttértem az új `AppCompatDelegate.setApplicationLocales()` API-ra, ami a rendszer szintjén kezeli a nyelvváltást.

### 13. Deep Link integráció
**Prompt:** "Hogyan tudok Deep Linket csinálni, ami bir specifikus étel oldalára visz?"
**Válasz összefoglalása:** Intent filter beállítások a Manifest-ben.
**Módosításom:** Kiegészítettem a `Safe Args` használatával a Navigation Component-en belül, hogy a deep link-ből érkező ID-kat típusbiztosan kezeljem, és kezeltem azt az esetet is, ha a felhasználó nincs bejelentkezve (auth guard).

### 14. Performance Profiling és Memory Leak fix
**Prompt:** "Miért omlik össze az app, ha sokszor nyitom meg a kamerát?"
**Válasz összefoglalása:** Általános tanácsok a memória kezelésről.
**Módosításom:** A LeakCanary segítségével rájöttem, hogy az MI által korábban generált `CameraProvider` nem lett megfelelően leállítva. Manuálisan implementáltam a `LifecycleObserver`-t, hogy garantáltan felszabaduljon a kamera erőforrás a fragment elhagyásakor.

### 15. Offline szinkronizáció Firestore-ral
**Prompt:** "Hogyan működik az offline mód Firestore-ban?"
**Válasz összefoglalása:** "Alapból be van kapcsolva."
**Módosításom:** Ez nem volt elég. Implementáltam bir egyedi szinkronizációs állapotjelzőt a UI-ra, ami mutatja a felhasználónak, ha az adatai még csak helyben vannak mentve, és értesítést küld, ha a feltöltés sikeresen megtörtént (Background Task).

### 16. Projekt architektúra dokumentáció (ADR)
**Prompt:** "Segíts létrehozni az ADR (Architecture Decision Records) mappát és dokumentáld a legfontosabb döntéseket (Firebase, MVVM, Gemini, Material 3)."
**Válasz összefoglalása:** Az MI javaslatot tett 4 ADR fájlra és a hozzájuk tartozó tartalomra.
**Módosításom:** Kiegészítettem a "Rationale" részeket saját érvekkel (pl. a Spark csomag ingyenessége, a Material 3 dinamikus színeinek fontossága az akadálymentesítésben), hogy ne csak technikai leírás legyen, hanem indoklás is.

### 17. Refaktorálás a tesztelhetőség érdekében
**Prompt:** "Hogyan tudom a MainActivity-ben lévő számítási logikát (BMR, progress bár százalék, szöveg formázás) kiszervezni, hogy unit tesztelhető legyen?"
**Válasz összefoglalása:** Javaslat a segédfüggvények static helper osztályokba (NutritionCalculator, UIUtils) mozgatására.
**Módosításom:** Nem csak átmozgattam a kódot, hanem egységesítettem is a BMR számítást a két helyen használt (Main és Setup) kód között, és bevezettem bir biztonságos progress számítást, ami garantálja a 0-100% közötti értékeket (edge case kezelés).

### 18. Átfogó Unit Teszt készlet és Bugfix
**Prompt:** "Írj unit teszteket a NutritionCalculator és UIUtils osztályokhoz, különös tekintettel a szélsőséges bemenetekre."
**Válasz összefoglalása:** JUnit tesztesetek generálása alapvető adatokkal.
**Módosításom:** Felfedeztem bir hibát a tesztelés során: a nem-felismerő logika (parseGender) a \"unknown\" stringet tartalmazó szavakal (pl. \"unknown\") nőnek (nő) érzékelte. Kijavítottam a logikát pontosabb szövegegyezésre, és kiterjesztettem a teszteket 21 esetre, lefedve a modelleket (DailyEntry, ConsumedFood) és a segédosztályokat is.

### 19. Integrációs tesztelés MockWebServer-rel és Mockito-val
**Prompt:** "Készíts integrációs teszteket az OpenFoodFactsApi válaszának szimulálására és a FirestoreRepository mockolására."
**Válasz összefoglalása:** MockWebServer beállítása a hálózati réteghez és Mockito a Firestore hívásokhoz.
**Módosításom:** Implementáltam a hálózati JSON parse-olás validációját valódi API válasz-példákkal, és reflexió segítségével injektáltam a mockokat a singleton Repository-ba, biztosítva a rétegek közötti izolált tesztelhetőséget.

### 20. Espresso UI tesztek a kritikus folyamatokhoz
**Prompt:** "Generálj Espresso UI teszteket a 'Food Search' és 'Settings' flow-khoz ActivityScenario használatával."
**Válasz összefoglalása:** Espresso kód az UI elemek (EditText, Button) interakciójához.
**Módosításom:** Részletes kommentekkel láttam el a View azonosítókat a bíráló számára, és kiegészítettem a teszteket a billentyűzet automatikus bezárásával és a várakozási idők kezelésével a stabilabb futtatás érdekében.

### 21. Negatív tesztelés és 10 hiba-szcenárió elemzése
**Prompt:** "Listázd a 10 legvalószínűbb hiba-szcenáriót a MainActivity kapcsán, és írj teszteket az AI hibás válaszaira."
**Válasz összefoglalása:** Hibalista készítése és teszt metódusok a rosszul formázott AI válaszokra.
**Módosításom:** Összeállítottam a top 10-es hibalistát (pl. hálózati timeout, Firestore jogosultsági hiba, AI hallucináció), és implementáltam a `FoodItem.fromAiJson` metódusban bir robusztus hibakezelést, ami `null`-t ad vissza üres vagy érvénytelen JSON esetén, megelőzve az app összeomlását.

### 22. CI/CD Pipeline GitHub Actions és Jacoco segítségével
**Prompt:** "Segíts beállítani a CI folyamatot GitHub Actions-szel és Jacoco coverage riporttal."
**Válasz összefoglalása:** YAML workflow konfiguráció és Jacoco Gradle taskok.
**Módosításom:** Beállítottam bir managed Android emulátort a CI-be, hogy az Espresso tesztek is lefussanak minden push után. Integráltam bir Jacoco riport generálót, ami vizuálisan mutatja a kódlefedettséget, biztosítva a projekt folyamatos minőségellenőrzését.

### 23. Build hibák javítása és dokumentáció frissítése
**Goal:** Megszüntetni a build/sync hibákat (ViewInjector), megoldani a unit tesztek mockolási problémáit (FirestoreRepository), és egységesíteni a projekt dokumentációját.
**Tool:** Android Studio AI Assistant
**What the AI proposed:** A `build.gradle.kts` fájlban a `$` karakterek eszképelését javasolta a Kotlin DSL stringekben, a Repository osztály refaktorálását Dependency Injection támogatáshoz, valamint technikai dokumentációs sablonok generálását (README, ADR, API Reference).
**What I accepted/changed:** Alkalmaztam az eszképelési javítást. A Repository-nál `protected` konstruktort és `setInstance` metódust vezettem be, hogy a unit tesztek során valódi Firebase SDK hívások nélkül lehessen mockokat használni. A dokumentációt testreszabtam a projekt tényleges állapotához (pl. Gemini AI használata).
**Validation:** Sikeres Gradle Sync és futó unit tesztek; a generált dokumentáció ellenőrizve a fájlrendszerben.
