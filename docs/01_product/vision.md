# Product Vision

## 1. Probléma leírás
A hagyományos kalóriaszámláló alkalmazások sok manuális adatbevitelt igényelnek, ami frusztráló a felhasználók számára. Gyakran nehéz pontosan megbecsülni egy-egy házi étel vagy éttermi fogás kalóriatartalmát, a mozgásformák kalóriaégetésének kiszámítása (MET értékek alapján) pedig túlságosan tudományos az átlagembernek.

## 2. Célfelhasználó (Persona)
**"Egészségtudatos Elfoglalt" (Péter, 30):** Szeretné nyomon követni a makrotápanyagait és a vízfogyasztását, de nincs ideje minden összetevőt külön grammra pontosan kikeresni. Olyan megoldást keres, ami "gondolkodik helyette".

## 3. Értékajánlat (Value Proposition)
Egy AI-val támogatott (Gemini 2.5 Flash) okos fitnesz napló, amely automatikusan megbecsüli az ételek tápértékét és a mozgásformák MET értékét, kiegészítve egy vonalkódolvasóval (OpenFoodFacts) a bolti termékek gyors rögzítéséhez.

## 4. Siker definíció (Metrikák)
* **North Star Metric:** Napi sikeresen rögzített bejegyzések (étel + mozgás) száma felhasználónként.
* **Guardrail Metric 1:** AI API hívások hibaaránya (alacsonyan tartása).
* **Guardrail Metric 2:** Alkalmazás indítási ideje és offline mód elérhetősége.

## 5. Non-goals (Mit nem csinálunk)
* Nem adunk orvosi diétás tanácsokat (az alkalmazás csak számol).
* Nem építünk közösségi funkciókat (Social Leaderboard, barátok).
* Nincs okosóra (WearOS) integráció a jelenlegi fázisban.

## 6. Kockázatok és bizonytalanságok
* **LLM Hallucináció:** Az AI pontatlan tápértékeket adhat vissza. *Mitigáció:* A felhasználónak lehetősége van felülírni a kapott adatokat, és egyértelmű jelölést kapnak az AI által generált tételek.
* **Hálózat hiánya:** Mobil app lévén gyakori az offline állapot. *Mitigáció:* Firestore offline perzisztencia használata.
