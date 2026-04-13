# Project Plan – Kalóriaszámláló alkalmazás


## Egy mondatos értékajánlat

Kezdőknek ad lehetőséget receptek keresésére és azoknak az ételeknek az elfogyasztását naplózni.

## Képességek


| Képesség | Kategória | Komplexitás | Miért nem triviális? |

|---|---|---|---|

| Firebase Auth & Google Login | Productization | M | OAuth 2.0 integráció, perzisztens bejelentkezés és a különböző provider-ek (Email vs Google) összekapcsolása. |

| Dinamikus Tápanyag-naplózás |	Value |	L |	NoSQL adatstruktúra tervezése (szülő-gyerek kapcsolat a napok és ételek közt), valós idejű szinkronizáció és aggregált adatszámítás. |

| Progress vizualizáció (ProgressBar) | Value |	UI/UX |	L	| Dinamikus százalékszámítás a napi limit és a fogyasztás arányában, egyéni UI elemek frissítése az adatok változásakor. |

| Receptböngésző és Kereső |	Value	| L |	Hatékony lekérdezés Firebase-ből (szűrés, keresés), képek aszinkron betöltése (Glide/Picasso) és cache-elés a sávszélesség kímélése érdekében. |

| BMI és Célkitűzés logika | Value	| M |	Felhasználói profil adatok (magasság, súly) kezelése és matematikai modellek implementálása az egyéni kalóriaigény meghatározásához. |

| Offline adatkezelés |	Productization | M	| Firebase offline persistence konfigurálása, hogy hálózati hiba esetén is rögzíthető legyen az elfogyasztott étel és ne vesszen el adat. |

| Automata tesztek | Productization | M | UI zöldágas tesztek megírása. |


**Kategória:** `Value` (felhasználó érzékeli) vagy `Productization` (minőséget garantál: auth, hibakezelés, tesztek, deploy) 

**Komplexitás:** `S` < 1 nap · `M` 2–5 nap · `L` 1+ hét


Minimum: 6 képesség, ebből 3 Productization, 2 L-es.


## A legnehezebb rész

- Jelenleg a Receptböngésző és Kereső miatt aggódok a legtöbbet és az ott megjelenített kép miatt. Nem tudom hogyan fogom a képeket és az ételeket kezelni, jelenleg egy más rendszer van implementálva és ez egy viszonylag új elképzelés

- UX kitalálása

- Automata Tesztek száma, nem tudom mennyi kell vagy tényleg elég-e csak a zöld ágas UI.


## Tech stack – indoklással

| Réteg | Technológia | Miért ezt és nem mást? |

|---|---|---|

| UI | XML | Az Android natív deklaratív leíró nyelve. Robusztus, kiforrott technológia, amelyhez széleskörű dokumentáció és stabil Layout Editor támogatás érhető el. |

| Backend / logika | Java | Tárgyorientált szemlélete (OOP) biztosítja a kód strukturáltságát és karbantarthatóságát. A választás alapja a mélyebb nyelvi ismeret, ami gyorsabb és biztonságosabb fejlesztést tesz lehetővé. |

| Adattárolás | Firebase - Firestore | Firestore	NoSQL alapú, valós idejű (Realtime) adatbázis, amely skálázható és beépített offline szinkronizációval rendelkezik, így hálózati hiba esetén is stabil marad az app. |

| Auth | Firebase - Authenticator | Magas szintű biztonságot nyújtó, felhőalapú megoldás, amely támogatja a többfaktoros hitelesítést és a harmadik fél általi (pl. Google) bejelentkezést anélkül, hogy jelszavakat kellene titkosítva tárolnom. |


## Ami kimarad (non-goals)

- Tervben van/volt mozgást naplózni, ez befolyásolná a naplózott kalória számát, de jelenleg hátul szerepel a prioritásaim között.

## Ami még nem tiszta

- Ez egy Android app, ha jól emlékszem kért egy leírást az elindításához. Itt ez hogy nézne ki?
