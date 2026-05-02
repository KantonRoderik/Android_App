# Siker definíció és Mérőszámok (Metrics)

Ahhoz, hogy tudjuk, az alkalmazás eléri-e a célját (automatizált, frusztrációmentes kalóriakövetés), az alábbi metrikákat definiáltuk. Jelen fázisban ezek elméleti metrikák (hogyan mérnénk), de egy éles termékben Firebase Analytics-szel volnának implementálva.

| Metrika típusa | Név | Definíció | Hogyan mérném? (Műszaki megvalósítás) |
| :--- | :--- | :--- | :--- |
| **North Star** | **Napi Sikeres Rögzítések (Daily Logs)** | Azon ételek és edzések átlagos száma felhasználónként egy nap alatt, amik bekerülnek a Firestore-ba. | Firebase Analytics custom event: `log_added`. Átlagolva az aktív felhasználók számára (DAU). |
| **Guardrail** | **AI Fallback Arány** | Az esetek százaléka, amikor a felhasználó elindít egy AI azonosítást, de az hibával tér vissza (Timeout, Parse error). | Custom event: `ai_error` osztva az `ai_request_started` eseményekkel. Elvárt cél: < 5%. |
| **Guardrail** | **Crash-Free Sessions** | A hibamentesen (összeomlás nélkül) lefutó munkamenetek aránya. | Beépített Firebase Crashlytics dashboard metrika. Cél: > 99.5%. |
| **Guardrail** | **Offline Mentés Arány** | Hányszor kerül egy adat offline cache-be közvetlen felhő szinkronizáció helyett (hálózati hiba miatt). | Ha kivétel típusa `FirebaseFirestoreException.Code.UNAVAILABLE`, logolunk egy `offline_save` eventet. |
