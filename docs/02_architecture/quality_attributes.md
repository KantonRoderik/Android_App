# Quality Attributes (Minőségi Követelmények)

A rendszer kialakítása során a funkcionális elvárások mellett az alábbi minőségi attribútumokat tartottuk szem előtt.

## Fő Minőségi Attribútumok
1. **Rendelkezésre állás (Availability / Offline-first):** Az alkalmazás internetkapcsolat hiányában is használható maradjon.
2. **Teljesítmény (Performance):** A külső API-k (AI, OpenFoodFacts) késleltetése ne tegye "fagyottá" az alkalmazást.
3. **Biztonság (Security):** Felhasználói profiladatok szeparációja és az AI kulcs védelme.
4. **Karbantarthatóság (Maintainability):** Tesztelhető üzleti logika és egyszerűsített Activity felépítés.
5. **Használhatóság (Usability):** Kevés manuális gépelés, minél több automatizmus.

---

## Quality Attribute Scenarios (Konkrét forgatókönyvek)

### 1. Scenario: Offline Adatrögzítés (Rendelkezésre állás)
* **Forrás (Source):** Mobil felhasználó a metrón vagy természetben.
* **Stimulus:** Hozzá akar adni egy ételt ("Alma") a napi naplójához, de megszakad a hálózat.
* **Környezet:** Offline állapot, degraded network.
* **Artefakt:** `FirestoreRepository`, Firebase SDK.
* **Válasz (Response):** A Firebase SDK azonnal elmenti az adatot a lokális SQLite cache-be. A UI felé azt az eseményt küldi, hogy az adatrögzítés megtörtént (a SnapshotListener azonnal reagál a lokális változásra). Amint a hálózat visszatér, a háttérben szinkronizál. Hiba esetén a UI nem omlik össze, hanem "Mentve (offline)" Toast üzenetet ad.
* **Mérőszám (Measure):** Az adatrögzítési UI válaszideje offline módban < 200ms marad. Nincs Exception/Crash.

### 2. Scenario: AI Válaszidő (Teljesítmény)
* **Forrás (Source):** Normál felhasználó.
* **Stimulus:** Az "AI Azonosítás" gombra kattint egy ismeretlen ételnév megadásával.
* **Környezet:** Normál működés, 4G mobilhálózat.
* **Artefakt:** `GenerativeModel` (Gemini Flash), `FoodFragment`.
* **Válasz (Response):** A kérés aszinkron szálon indul el. A főszál (Main UI thread) nem blokkolódik. A UI egy ProgressBar-t (loading indicator) mutat a felhasználónak, a gomb inaktívvá válik, elkerülve a dupla kattintást.
* **Mérőszám (Measure):** A Main thread egyszer sem kap ANR (Application Not Responding) hibát. Az AI átlagos válaszideje (p95) < 3.0 másodperc. (Timeout esetén 15s után gracefully fail).
