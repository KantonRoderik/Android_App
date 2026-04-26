# ADR 3: Google Gemini AI integrálása táplálkozási tanácsadáshoz

## Status
Accepted

## Context
Az alkalmazás egyik célja, hogy intelligens visszajelzést adjon a felhasználó étkezési szokásairól. Szükség volt egy nagy nyelvi modellre (LLM), ami képes elemezni a bevitt adatokat és kontextuális tanácsokat adni.

## Decision
A Google Gemini API-t (gemini-2.5-flash) választottam.

## Rationale
- **Natív Android SDK:** A Google biztosít közvetlen Kotlin SDK-t, így nincs szükség köztes szerveroldali proxy-ra a prototípus fázisban.
- **Sebesség:** A Flash modell alacsony késleltetést biztosít, ami fontos a mobilalkalmazásoknál.
- **Költség:** A fejlesztői szinten ingyenesen használható bizonyos korlátok között.
- **Multimodalitás:** Lehetőséget biztosít a jövőben képek (pl. ételfotók) elemzésére is.

## Consequences
- Az API kulcs biztonságos tárolása kritikus (helyi titkosítás vagy Backend proxy szükséges a végleges verzióban).
- A modell válaszai néha pontatlanok lehetnek ("hallucináció"), ezért fontos a megfelelő prompt engineering és a felhasználó figyelmeztetése.
