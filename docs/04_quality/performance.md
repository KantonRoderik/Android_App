# Teljesítmény (Performance) Baseline

Egy modern Android alkalmazás esetén a teljesítmény (sebesség és akadásmentesség) kritikus a jó felhasználói élményhez. Bár az alkalmazás nem végez extrém számításigényes műveleteket lokálisan, a hálózati kommunikáció és a vizuális elemek frissítése okozhat szűk keresztmetszeteket.

## 1. Szűk Keresztmetszet (Bottleneck): Hálózati Késleltetés
A rendszer legkritikusabb teljesítmény-tényezője a külső API-k válaszideje.

* **OpenFoodFacts API (Vonalkód):** Átlagos válaszidő: ~400-800 ms.
* **Google Gemini API (AI Azonosítás):** Átlagos válaszidő (p95): ~2.0 - 4.5 másodperc (modell leterheltségtől függően).

**Mérés és Javítás:**
Ha az AI hívás a főszálon (Main UI Thread) futna, az alkalmazás "lefagyna" (ANR - Application Not Responding hiba).
* *Javítás (Implementálva):* A `GenerativeModelFutures` használata biztosítja, hogy a hívás a háttérben fut aszinkron módon (`ListenableFuture`). 
* *UX védelem:* A kérés elindításakor a UI felületen egy `ProgressBar` (Loading Spinner) jelenik meg, és a beküldő gomb inaktívvá válik (`setEnabled(false)`), megakadályozva a türelmetlen felhasználó általi többszörös beküldést, ami API kvóta kimerülést (Rate Limit 429) okozna.

## 2. Vizuális Teljesítmény (UI Rendering)
* **Kihívás:** Az egyedi betűtípusok, a Glassmorphism effektek (átlátszóság) és az animált Progress Bar-ok renderelése.
* **Baseline:** Az alkalmazás megcélozza a 60 FPS (képkocka/másodperc) sebességet, ami azt jelenti, hogy a UI-nak ~16 ms alatt kell kirajzolódnia.
* **Megoldás:** A `MainActivity` progress bar animációihoz `ObjectAnimator`-t és `DecelerateInterpolator`-t használunk hardveres gyorsítással, és a hierarchiát laposan tartjuk a `ConstraintLayout` segítségével.

## 3. Adatbázis Teljesítmény (Firestore)
A Firestore lekérdezések (Read/Write) teljesítménye kiváló, mivel az SDK automatikusan kezeli az offline cache-t. A felhasználó felé a mentés gomb lenyomása szinte **azonnal (0 ms)** megtörténik a UI-on, mert a Firestore a helyi SQLite adatbázisba ír, majd a háttérszálon (késleltetés nélkül) szinkronizál a felhővel.
