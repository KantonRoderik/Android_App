# AI Manifest

## Használt eszközök
- **Google Gemini API (`gemini-1.5-flash`):** Az alkalmazáson belüli funkció (futásidőben hívva), hogy tápérték (MET és Makró) adatokat generáljon az ismeretlen ételekre és mozgásokra.
- **ChatGPT / Claude / Cursor (Fejlesztési fázis):** A projekt tervezése, architektúra döntések, boilerplate kód generálása és a tesztek megírásának felgyorsítása céljából.

## Felhasználási területek
- **Data Enrichment:** A `FoodFragment` és `ExerciseFragment` használja a Gemini-t, hogy strukturált JSON válaszokat kapjon ("Provide nutrition data for...").
- **Fejlesztés:** A `FirestoreRepository` alapjainak és a komplex BMR/TDEE matematikai formuláknak a legenerálása. A jelenlegi "Docs-as-Code" struktúra kialakítása.

## Tiltások és Guardrails
- **PII védelem:** Soha nem küldünk felhasználói azonosítót, testsúlyt vagy egészségügyi adatot a Gemini API-nak. A prompt kizárólag egy statikus szöveg + az étel neve.
- **Kliens-oldali JSON validáció:** A generált AI szöveget (Markdown formátum) a Gson library-val objektummá alakítjuk (`Gson().fromJson(cleanJson, ...)`). Ha az AI hibás vagy hiányos JSON-t ad, a rendszer fallbackel (hibaüzenetet dob), és nem omlasztja össze az appot.

## Kockázatok és Kezelésük
- **Hallucináció:** A modell olyan makrókat írhat, amik irreálisak. *Kezelés:* A felhasználó számára megjelenik az érték, de módosíthatja (vagy törölheti) azt.
- **API kvóta kimerülés:** Rate limit hiba 429. *Kezelés:* Hibaüzenet ("Hiba történt") jelenik meg az alkalmazásban, nem crashel.
