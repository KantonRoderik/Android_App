# Prompt Log

Ebben a fájlban a projekt során használt kritikus fejlesztői és funkcionális promptok szerepelnek.

## P-01: Alap Firestore struktúra tervezése
- **Dátum:** 2024. Q2
- **Cél:** Egy szinkronizálható Firestore repository létrehozása.
- **Prompt:** "Írj egy FirestoreRepository Java osztályt Androidhoz, amely kezeli a bejelentkezett felhasználót, elmenti a napi elfogyasztott ételeket (ConsumedFood) egy napi dokumentumba, és automatikusan összesíti a kalóriákat WriteBatch használatával."
- **Eredmény:** A `FirestoreRepository` alapja.
- **Módosítás:** Az AI által adott verzióhoz manuálisan hozzáadtam a Víz és a Mozgás logolását és törlését.

## P-02: Gemini API strukturált JSON kimenet
- **Dátum:** 2024. Q2
- **Prompt:** "Act as a fitness and nutrition expert. Provide nutrition data for 100g and common units. Return ONLY a raw JSON object. Fields: name, calories, carbs, protein, fat, commonUnits (array)."
- **Eredmény:** A `FoodFragment` system instruction blokkja.

## P-03: Kliens-oldali JSON parsing hiba (Markdown backticks)
- **Dátum:** 2024. Q2
- **Prompt:** "A Gemini válasza backtickek között van. Hogyan tudom ezt Java-ban a legegyszerűbben kiparsolni, mielőtt a Gson-nak adom?"
- **Eredmény:** A regex tisztítás: `text.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();`

## P-04: Met értékek alapú kalória kalkulátor
- **Dátum:** 2024. Q2
- **Prompt:** "What is the standard formula to calculate calories burned using MET value, weight in kg, and duration in minutes? Write a Java utility method for it."
- **Eredmény:** A `NutritionCalculator.calculateExerciseCalories` metódus.

## P-05: Progress Bar nullával osztás hiba
- **Dátum:** 2024. Q3
- **Prompt:** "Van egy Android ProgressBar-om. Hogyan számoljam ki biztonságosan a százalékot két double értékből, elkerülve a nullával osztást vagy a 100% feletti túlcsordulást?"
- **Eredmény:** Az `UIUtils.calculateSafeProgress` segédfüggvény.

## P-06: Material 3 Glassmorphism
- **Dátum:** 2024. Q3
- **Prompt:** "Hogyan csináljak Glassmorphism stílusú félig átlátszó kártyát (CardView) Material 3 használatával Android XML-ben?"
- **Eredmény:** A kártyákhoz tartozó color hex kódok (`#CCFFFFFF` és társai) és a háttér XML paraméterek.

## P-07: OpenFoodFacts JSON adatmodell
- **Dátum:** 2024. Q3
- **Prompt:** "Generálj Retrofit 2 interfészt és DTO (Data Transfer Object) osztályokat ehhez a JSON válaszhoz az OpenFoodFacts API-ból: [beillesztett JSON minta]."
- **Eredmény:** Az `OpenFoodFactsApi.java` és a DTO struktúrák a helyes `@SerializedName` taggekkel.

## P-08: Diéta Sablonok (Dietary Templates)
- **Dátum:** 2024. Q3
- **Prompt:** "Milyen standard makrotápanyag arányok (fehérje, szénhidrát, zsír) vannak egy Kiegyensúlyozott, Alacsony Szénhidrátos, és Ketogén diétában? Készíts erre egy Java Enum-ot, ami kiszámolja a grammokat a TDEE alapján."
- **Eredmény:** A `DietaryTemplate.java` Enum az arányszámokkal és a formulákkal.

## P-09: Firestore Security Rules
- **Dátum:** 2024. Q4
- **Prompt:** "Írj egy alapvető Firestore Security Rules konfigurációt, ahol csak a bejelentkezett felhasználó olvashatja és írhatja a saját UID-je alatti 'users' és 'dailyEntries' dokumentumokat."
- **Eredmény:** A security rules alap váza, amely védi a felhasználói adatokat.

## P-10: Dátum formázás és napváltás
- **Dátum:** 2024. Q4
- **Prompt:** "Hogyan készítsek egy naptár navigátort Androidban, ami az 'Előző nap' / 'Következő nap' gombokra frissíti a Calendar példányt és formázza a dátumot yyyy-MM-dd formátumban?"
- **Eredmény:** A `MainActivity` napváltó logikája és a `SimpleDateFormat` inicializálása.
