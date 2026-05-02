# Test Report

## Környezet
- **OS:** Windows 10/11 / GitHub Actions Ubuntu
- **Runtime/SDK:** Java 17 / Android API 35
- **Teszteszközök:** JUnit 4, Mockito, MockWebServer

## Teszt suite-ek és futtatás
- **Unit & Integration:** `./gradlew testDebugUnitTest`

## Legutolsó futás eredménye
- **Dátum:** 2024-05 (Aktuális)
- **Eredmény:** PASS (30 passed, 0 failed)
- **CI link:** [GitHub Actions - CI Pipeline](https://github.com/KantonRoderik/Android_App/actions/workflows/ci.yml)
- **Részletek:**
  - `NutritionCalculatorTest`: 10/10 PASS
  - `UIUtilsTest`: 5/5 PASS
  - `FirestoreRepositoryTest`: 4/4 PASS
  - `NetworkIntegrationTest`: 11/11 PASS

## Negatív Tesztek (Edge Cases)
- `calculateBMR_InvalidInput_ReturnsZero`: Negatív életkor vagy súly tesztelése.
- `calculateSafeProgress_ZeroOrNegativeGoal_ReturnsZero`: Nullával való osztás elleni védelem.
- (Tervezett) API hálózati timeout tesztelése MockWebServer-rel.

## Ismert hiányosságok
- **E2E / Instrumented tesztek (Espresso):** Jelenleg az UI tesztelés nagyrészt manuálisan történik a fizikai eszközön/emulátoron. Az Espresso tesztek bekötése a következő fázis.
