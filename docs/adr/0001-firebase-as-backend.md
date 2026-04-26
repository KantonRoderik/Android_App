# ADR 1: Firebase használata háttérszolgáltatásként

## Status
Accepted

## Context
Az alkalmazáshoz szükség van felhasználói hitelesítésre, felhő alapú adattárolásra és analitikára. Felmerült egy saját Spring Boot backend írása vagy egy "Backend-as-a-Service" (BaaS) megoldás használata.

## Decision
A Google Firebase platformja mellett döntöttem (Auth, Firestore, Analytics).

## Rationale
- **Gyors fejlesztés:** Nem kell infrastruktúrát üzemeltetni és API végpontokat manuálisan lefejleszteni az alap CRUD műveletekhez.
- **Valós idejű szinkronizáció:** A Firestore natívan támogatja a valós idejű adatfrissítést, ami javítja a felhasználói élményt a naplózásnál.
- **Költséghatékonyság:** A szakdolgozat léptékében a "Spark" (ingyenes) csomag bőségesen elegendő.
- **Biztonság:** A Firebase Auth bevált megoldásokat kínál (Google Sign-In, jelszókezelés), amiket nehéz lenne hiba nélkül saját kézzel implementálni.

## Consequences
- Függőség a Google ökoszisztémájától (Vendor lock-in).
- A komplexebb lekérdezések (pl.全文 keresés) nehezebbek Firestore-ban, mint egy relációs adatbázisban.
