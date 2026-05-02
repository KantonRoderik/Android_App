# Post-Mortem: Gemini API JSON Parsing hiba (Crash)

**Dátum:** 2024. Q2
**Érintett Komponens:** Kliens Android App (FoodFragment) és Gemini AI integráció.

## 1. Az esemény leírása (Mi történt?)
A fejlesztés során az "AI Azonosítás" funkció tökéletesen működött az első néhány tucat tesztnél. Azonban egy ponton az alkalmazás hirtelen összeomlott (Crash), amikor a felhasználó megpróbálta felismertetni a "Pizza" szót. A logok alapján a `Gson` könyvtár `JsonSyntaxException`-t dobott.

## 2. A probléma oka (Root Cause)
Habár a Gemini promptjába (System Instruction) beleírtuk, hogy "Return ONLY a raw JSON object", az LLM modell időnként visszaváltott Markdown formázásra, és a választ ` ```json { ... } ``` ` blokkba csomagolta be. Mivel a kliens közvetlenül egy valid JSON stringet várt, a backtick karakterek (```) miatt a string parseolása azonnal elbukott. Mivel az eredmény feldolgozása nem volt becsomagolva dedikált hibakezelésbe, ez az alkalmazás bezáródásához vezetett.

## 3. Hatás (Impact)
Az alkalmazás "Hard Crash"-el leállt minden olyan esetben, amikor az AI formázott szöveget adott nyers string helyett.

## 4. Javítás (Resolution)
1. **Azonnali javítás:** Bekerült egy Regex tisztító sor (`text.replaceAll("(?s)```(?:json)?\\n?|```", "").trim();`), amely azonnal "leborotválja" a Markdown jelölőket a válasz elejéről és végéről, még a Gson parse előtt.
2. **Graceful Fallback:** A teljes AI feldolgozó logikát egy `try-catch` blokkba tettük. Ha bármilyen (akár új) szintaktikai hiba történik a jövőben, az alkalmazás többé nem omlik össze. Ehelyett letiltja a betöltést, és egy `AlertDialog` jelenik meg "Az AI válasza nem feldolgozható" szöveggel.

## 5. Prevenció
Bevezettük a szabályt a fejlesztésbe, hogy egyetlen külső API-ból (főleg LLM-ből) jövő nyers szöveget sem parse-olunk közvetlenül Try-Catch háló és tisztítás (sanitization) nélkül.
