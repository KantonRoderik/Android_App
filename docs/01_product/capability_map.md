# Capability Map

| Capability | Kategória | Evidence (link) | Teszt (link) | Státusz |
| :--- | :--- | :--- | :--- | :--- |
| **Felhasználói hitelesítés (Auth)** | Productization | [Screen: S01, S02](../ux/screens.md) | `FirestoreRepositoryTest.java` | Done |
| **Offline-first adatszinkronizáció** | Productization | [Firestore config](../../app/src/main/java/com/example/szakdolgozat/helpers/FirestoreRepository.java) | `NetworkIntegrationTest.java` | Done |
| **Automatikus BMR/TDEE számítás** | Value | [NutritionCalculator](../../app/src/main/java/com/example/szakdolgozat/helpers/NutritionCalculator.java) | `NutritionCalculatorTest.java` | Done |
| **Vonalkód alapú termékazonosítás** | Value | [OpenFoodFactsApi](../../app/src/main/java/com/example/szakdolgozat/network/OpenFoodFactsApi.java) | `NetworkIntegrationTest.java` | Done |
| **AI alapú étel és MET azonosítás** | Value | [FoodFragment](../../app/src/main/java/com/example/szakdolgozat/UI/food/FoodFragment.java) | Manuális / API teszt | Done |
| **Kliens-oldali input validáció** | Productization | [UIUtils](../../app/src/main/java/com/example/szakdolgozat/helpers/UIUtils.java) | `UIUtilsTest.java` | Done |
| **Dinamikus UI (Progress bars)** | Value | [MainActivity](../../app/src/main/java/com/example/szakdolgozat/UI/main/MainActivity.java) | `UIUtilsTest.java` | Done |
| **Sötét mód (Dark Theme) támogatás** | Value | [values-night/themes.xml](../../app/src/main/res/values-night/themes.xml) | Manuális Teszt | Done |
| **E2E UI tesztelés (Espresso)** | Productization | N/A | N/A | Planned |
