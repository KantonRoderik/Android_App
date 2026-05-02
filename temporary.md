# Projekt Mappastruktúra

Az alábbi fa-struktúra mutatja be a **végleges, tökéletesített mappaszerkezetet**. Kérlek, ellenőrizd a saját számítógépeden (a Windows Fájlkezelőben vagy az Android Studio Project nézetében), hogy pontosan így néz-e ki a fájlrendszered.

```text
Android_App/ (A projekt gyökérkönyvtára)
│
├── .github/
│   ├── dependabot.yml                      <-- Automata sebezhetőség figyelő
│   └── workflows/
│       └── ci.yml                          <-- GitHub Actions (CI pipeline)
│
├── app/                                    <-- Itt van a teljes Android Java kódod
│   ├── src/main/java/...
│   ├── src/main/res/...
│   └── build.gradle.kts
│
├── docs/                                   <-- A Docs-as-Code dokumentáció
│   │
│   ├── 00_index.md                         <-- A dokumentáció fő tartalomjegyzéke
│   │
│   ├── 01_product/                         <-- Termék fókuszú dokumentumok
│   │   ├── capability_map.md
│   │   ├── metrics.md
│   │   ├── scope_contract.md
│   │   └── vision.md
│   │
│   ├── 02_architecture/                    <-- Mérnöki döntések és ábrák
│   │   ├── c4_component.md
│   │   ├── c4_context_container.md
│   │   ├── quality_attributes.md
│   │   └── adr/                            <-- (Fontos, hogy az ADR-ek ITT legyenek)
│   │       ├── 0001-firebase-as-backend.md
│   │       ├── 0002-classic-architecture.md
│   │       ├── ...
│   │       └── 0008-error-handling-strategy.md
│   │
│   ├── 03_design/                          <-- Adatszerkezetek és hibakezelés
│   │   ├── api.md
│   │   ├── data_model.md
│   │   └── error_handling.md
│   │
│   ├── 04_quality/                         <-- Tesztelés és teljesítmény
│   │   ├── performance.md
│   │   ├── test_report.md
│   │   └── test_strategy.md
│   │
│   ├── 05_security_ops/                    <-- Biztonság és Üzemeltetés
│   │   ├── deploy_runbook.md
│   │   ├── observability.md
│   │   ├── privacy_licensing.md
│   │   └── threat_model.md
│   │
│   ├── 06_release/                         <-- Értékelés és hibaelemzés
│   │   ├── post_mortem.md
│   │   └── scorecard.md                    <-- A 100 pontos táblázatod!
│   │
│   ├── 07_ai/                              <-- AI mérnöki dokumentáció
│   │   ├── ai_manifest.md
│   │   ├── prompt_log.md
│   │   └── verification_log.md
│   │
│   ├── pdf/                                <-- A meglévő PDF fájljaid maradhatnak itt
│   │
│   └── ux/                                 <-- Felhasználói felület (GUI) dokumentációja
│       ├── README.md                       <-- Helyi tartalomjegyzék a UX mappához
│       ├── design_system.md
│       ├── journeys.md
│       ├── pageflow.mmd
│       ├── screens.md
│       └── screenshots/                    <-- Ide mentsd majd a 10 darab képernyőfotót
│           ├── S01_login.png
│           ├── ...
│           └── S10_scanner.png
│
├── .env.example                            <-- Referencia fájl az elvárt változókról
├── .gitignore                              <-- Verziókövetésből kizárt fájlok
├── build.gradle.kts                        <-- Gradle konfig
├── README.md                               <-- Fő belépési pont a repóhoz (Quickstart)
└── temporary.md                            <-- EZ A FÁJL (Nyugodtan töröld, ha átnézted)
```

## ❌ AMIT TÖRÖLNÖD KELL (Ha még nem tetted meg)
Hogy a struktúra pontosan megfeleljen a fentinek (és elkerüld a duplikációkat), a Windows fájlkezelőben **TÖRÖLD a következőket**:
1. `Android_App/PROMPTLOG.md` *(Mert már van a `docs/07_ai/` alatt)*
2. `Android_App/docs/api/` mappa a benne lévő fájlokkal együtt *(Mert átkerült a `docs/03_design/`-be)*
3. `Android_App/docs/adr/` mappa a benne lévő fájlokkal együtt *(Figyelj! A gyökér `docs/` alatti `adr/` mappát töröld, a `docs/02_architecture/adr/` MARADJON MEG, abban van a 8 jó fájlod!)*
4. `Android_App/docs/ux/self_assessment.md` *(Mert a Scorecard átvette a helyét)*
