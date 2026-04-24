# Önértékelés - GUI/UX

| Szempont | Pontszám | Indoklás |
| :--- | :---: | :--- |
| Vizuális konzisztencia | 5 | Az összes képernyő egységes Glassmorphism kártyákat és színpalettát használ. |
| Információs hierarchia | 4 | A legfontosabb kalória adatok dominálnak a dashboardon, jól olvashatóak. |
| Visszajelzések | 4 | Toast üzenetek és progress bar animációk segítik a felhasználót. |
| Hibakezelés | 4 | A formok validálva vannak (üres mezők, jelszó egyezés), hibaüzeneteket adnak. |
| Mobil / asztal lefedettség | 5 | Natív mobilalkalmazásként tökéletesen illeszkedik a célplatformra. |
| Akadálymentesség (a11y) | 3 | Az alapvető TalkBack támogatás és kontraszt megvan, de még fejleszthető. |
| Onboarding élmény | 5 | A regisztráció utáni kényszerített profil kitöltés és auto-számítás kiváló. |
| Teljesítményérzet | 5 | A natív implementáció és a Firebase szinkronizáció gyors és sima. |

## Mire vagyok büszke?
A **Barcode Scanner** integrációra és az ehhez kapcsolódó **Onboarding** folyamatra. Nagyon profi érzést kelt, hogy a regisztráció után az app nem enged tovább, amíg ki nem számoljuk a tudományosan megalapozott napi célokat. A **Glassmorphism design** (félig átlátszó kártyák) pedig modern és prémium kinézetet ad az alkalmazásnak.

## Mit fejlesztenék tovább?
Szívesen bevezetnék több **mikro-animációt** (pl. a víz hozzáadásakor hullám effekt), és egy **sötét mód/világos mód kapcsolót** közvetlenül a profilban. Emellett a **Searchable Database** (kereshető étel adatbázis) még hiányzik a manuális hozzáadáshoz.

## Mit nem sikerült megvalósítani?
Az eredetileg tervezett **Social Leaderboard** (közösségi ranglista) részletes kidolgozása elmaradt, mivel a fókusz a stabil és modern alapvető mérési funkciókon volt.
