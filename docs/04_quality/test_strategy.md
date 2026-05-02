# Teszt Stratégia

## Teszt Piramis
A projekt a gyors visszajelzési hurok érdekében egy alul hangsúlyos teszt piramist alkalmaz.
1. **Unit tesztek (JVM):** A leggyorsabbak. Ezek tesztelik az üzleti logikát (pl. `NutritionCalculator`, `UIUtils`) és a `FirestoreRepository`-t (mockolva).
2. **Integrációs tesztek:** A hálózat és az API-k közötti kapcsolatot tesztelik (pl. `NetworkIntegrationTest`).
3. **E2E / Instrumented tesztek:** Az Android eszközön futó tesztek, amik a felhasználói felületet és az adatbázist együtt validálják (tervezett).

## Mit tesztelünk Unit szinten?
* Tisztán üzleti logika: BMR számítás, MET alapú kalória számítás, progress bar százalékos értékek kalkulálása. (Ezekhez nem kell Android Context).
* Repository: A Firebase hívások mockolásával ellenőrizzük, hogy a megfelelő adatbázis metódusok (`batch.set`, `collection.get`) meghívódnak-e.

## Mock / Stub Stratégia
* A tesztekben a **Mockito** frameworköt használjuk.
* A `FirebaseFirestore` és a `FirebaseAuth` osztályokat mockoljuk a `FirestoreRepositoryTest`-ben, hogy az adatbázis tesztelése offline, azonnal és izoláltan történjen.
* HTTP hívásokhoz (OpenFoodFacts) `MockWebServer`-t használunk az integrációs tesztekben.

## Minőségi Kapuk (Quality Gates)
* A projekt fordítási (Build) folyamata során a `./gradlew testDebugUnitTest` parancs biztosítja a tesztek lefutását.
* (CI/CD Pipeline jelenleg a GitHub Actions-ben beállítás alatt).
