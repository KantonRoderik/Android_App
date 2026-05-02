# 100 pontos készültségi scorecard

Ez a dokumentum a szakdolgozói leadandó csomag (v1.2) alapján értékeli a projekt jelenlegi állapotát.

*Állapotok: 0.0 = Nincs (Not started) | 0.5 = Részben kész (Partial) | 1.0 = Kész (Done) | N/A = Nem releváns*

## A) Product és scope (12 / 12 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 2 | Vision kész és konkrét: célfelhasználó/persona, értékajánlat, non-goals, top kockázatok | 1.0 | 2.0 | [vision.md](../01_product/vision.md) |
| 2 | Scope Contract: MVP story-k + elfogadási kritériumok + scope fegyelem | 1.0 | 2.0 | [scope_contract.md](../01_product/scope_contract.md) |
| 2 | Capability Map kitöltve 6+ képességgel, státusszal és evidence linkekkel | 1.0 | 2.0 | [capability_map.md](../01_product/capability_map.md) |
| 2 | UX flow-k dokumentáltak: 2-3 fő flow + error/empty state | 1.0 | 2.0 | [journeys.md](../ux/journeys.md) és [screens.md](../ux/screens.md) |
| 2 | Mérőszámok/metrics: mit mérnél és miért | 1.0 | 2.0 | [metrics.md](../01_product/metrics.md) |
| 2 | Ismert korlátok + roadmap/tech debt: mi maradt kompromisszum és mi a következő lépés | 1.0 | 2.0 | [self_assessment.md](../ux/self_assessment.md) |

## B) Képesség-szélesség (10.0 / 10 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 2 | Legalább 6 capability, ebből minimum 3 'termékesítő' | 1.0 | 2.0 | [capability_map.md](../01_product/capability_map.md) |
| 2 | Minden Done capability-hez van konkrét evidence link | 1.0 | 2.0 | [capability_map.md](../01_product/capability_map.md) |
| 2 | Minden Done capability-hez van kapcsolt teszt vagy teszt bizonyíték | 1.0 | 2.0 | [capability_map.md](../01_product/capability_map.md) |
| 2 | Edge case-ek és hibák is capability szinten kezeltek | 1.0 | 2.0 | "Kliens-oldali input validáció" capability. |
| 2 | A capability map-ben a Planned/Partial elemek is őszintén jelöltek | 1.0 | 2.0 | A táblázatban fel van tüntetve a Dark Mode és az E2E tesztelés mint Planned funkció. |

## C) Architektúra és döntések (13.0 / 13 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 3 | C4 Context + Container diagram naprakészen | 1.0 | 3.0 | [c4_context_container.md](../02_architecture/c4_context_container.md) |
| 3 | Component/modul nézet: modulhatárok, felelősségek | 1.0 | 3.0 | [c4_component.md](../02_architecture/c4_component.md) Component diagrammal ábrázolva. |
| 3 | Minimum 8 ADR: döntés, alternatívák, következmények, verifikáció | 1.0 | 3.0 | [adr/](../02_architecture/adr) 8 db teljes, verifikált ADR megvan. |
| 2 | Quality attributes + legalább 2 quality scenario | 1.0 | 2.0 | [quality_attributes.md](../02_architecture/quality_attributes.md) |
| 2 | Deployment view: hol fut mi, környezetek, komponensek kapcsolata | 1.0 | 2.0 | [deploy_runbook.md](../05_security_ops/deploy_runbook.md) |

## D) Engineering minőség (15.0 / 15 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 3 | Kódszerkezet és moduláris felépítés: rétegek, dependency irányok | 1.0 | 3.0 | Mappastruktúra (UI, models, helpers, network) és Repository pattern. |
| 3 | Egységes hibakezelés + input validáció + konzisztens hibamodel | 1.0 | 3.0 | [error_handling.md](../03_design/error_handling.md) |
| 2 | Konfiguráció és környezetek: beállítások különválasztása | 1.0 | 2.0 | `local.properties` és `google-services.json` használata. |
| 3 | Teljesítmény baseline + 1 szűk keresztmetszet mérése | 1.0 | 3.0 | Késleltetések (Gemini API bottleneck) mérése és aszinkron javítása a [performance.md](../04_quality/performance.md)-ben leírva. |
| 2 | Statikus minőségi kapuk (lint/format) és CI-ban fut | 1.0 | 2.0 | A `ci.yml` beállításra került a GitHub Actions-hez. |
| 2 | Platform-specifikus minőség (reszponzivitás/a11y) | 1.0 | 2.0 | Material 3, Glassmorphism, TalkBack alap támogatás. |

## E) Tesztelés és minőségi kapuk (11.0 / 15 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 2 | Teszt stratégia és teszt riport kitöltve, futtatás leírva | 1.0 | 2.0 | [test_strategy.md](../04_quality/test_strategy.md) és [test_report.md](../04_quality/test_report.md) |
| 4 | 30+ automata teszt értelmes mixben, legalább 5 negatív teszttel | 1.0 | 4.0 | 30 PASS teszt JVM szinten (Unit + Integration MockWebServerrel). |
| 3 | CI gating: a tesztek kötelezően futnak, main branch zöld | 1.0 | 3.0 | [GitHub Actions - CI Pipeline](https://github.com/KantonRoderik/Android_App/actions/workflows/ci.yml) |
| 2 | Integrációs tesztek valós függőségekkel | 1.0 | 2.0 | `NetworkIntegrationTest.java` (MockWebServer). |
| 2 | E2E/contract jellegű teszt a core flow-ra | 0.0 | 0.0 | Espresso UI tesztek még hiányoznak. |
| 2 | Plusz minőségi teszt (performance/security) | 0.0 | 0.0 | Nincs implementálva. |

## F) DevOps és üzemeltetés (12.0 / 15 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 3 | CI pipeline komplett: build + lint + teszt | 1.0 | 3.0 | [GitHub Actions - CI Pipeline](https://github.com/KantonRoderik/Android_App/actions/workflows/ci.yml) konfigurálva. |
| 2 | Reprodukálható build/dependency kezelés | 1.0 | 2.0 | Gradle (Kotlin DSL) és versions toml. |
| 3 | Deploy leírás + környezetek | 1.0 | 3.0 | [deploy_runbook.md](../05_security_ops/deploy_runbook.md) |
| 2 | Rollback terv (leírva) | 1.0 | 2.0 | [deploy_runbook.md](../05_security_ops/deploy_runbook.md) |
| 3 | Observability baseline (log + healthcheck + 3 metrika) | 0.5 | 1.5 | Részben kész. Logolás definiálva, metrikák javasoltak. |
| 2 | Runbook: 2 incident scenario | 1.0 | 2.0 | [deploy_runbook.md](../05_security_ops/deploy_runbook.md) |

## G) Security, privacy, licenc (11.0 / 10 pont - Max: 10)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 3 | Threat model 6+ tétellel + konkrét mitigációk | 1.0 | 3.0 | [threat_model.md](../05_security_ops/threat_model.md) |
| 2 | Secret hygiene rendben | 1.0 | 2.0 | Nincs titok a repoban, `.env.example` és `local.properties` működik. |
| 2 | AuthN/AuthZ modell dokumentált és tesztelt | 1.0 | 2.0 | Firebase Auth implementálva és letesztelve. |
| 2 | Dependency vulnerability ellenőrzés | 1.0 | 2.0 | A `.github/dependabot.yml` beállításra került az automata auditokhoz. |
| 1 | Privacy + licensing | 1.0 | 1.0 | [privacy_licensing.md](../05_security_ops/privacy_licensing.md) |

## H) AI engineering érettség (10.0 / 10 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link |
| :--- | :--- | :---: | :---: | :--- |
| 2 | AI manifest: eszközök és használati területek | 1.0 | 2.0 | [ai_manifest.md](../07_ai/ai_manifest.md) |
| 2 | Prompt log: 10-20 kulcsprompt | 1.0 | 2.0 | 10+ kulcsprompt dokumentálva. |
| 3 | Verification log: legalább 10 verifikáció | 1.0 | 3.0 | 10 kidolgozott bejegyzés és ellenőrzés. |
| 2 | AI output integráció: kód teszt+review után mehet be | 1.0 | 2.0 | Szabályrendszer lefektetve az AI Manifestben. |
| 1 | Tanulságok: hol tévedett az AI, milyen guardrail-ek kellenek | 1.0 | 1.0 | Regex JSON tisztítás a [post_mortem.md](post_mortem.md)-ben. |

## Bónusz (6.0 / 10 pont)

| Max | Követelmény | Állapot | Számított | Evidence / link                                      |
| :--- | :--- | :---: | :---: |:-----------------------------------------------------|
| 2 | Rövid post-mortem egy valós hibáról | 1.0 | 2.0 | [post_mortem.md](post_mortem.md) fájlban elkészítve. |
| 2 | Offline/queue/retry stratégia | 1.0 | 2.0 | Firestore offline persistence a mobil appban.        |
| 2 | Accessibility vagy i18n extra | 1.0 | 2.0 | 4 nyelvűség, natív TalkBack támogatás.               |

---

## Összegzés

**Elért alappontszám: 94.0 / 100 pont (Bónuszokkal együtt: 100.0 pont)**

### Értékelés a csomag alapján: "95-100 pont: kifejezetten érett, production-minded megoldás"
Az utolsó hiányzó rétegek (Dependabot biztonsági audit, Performance mérés és a Component diagram) pótlásával a dokumentáció **elérte a maximális, 100 pontos eredményt**.
