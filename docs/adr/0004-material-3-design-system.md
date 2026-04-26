# ADR 4: Material 3 (Material You) alkalmazása

## Status
Accepted

## Context
Az alkalmazás vizuális megjelenésének modernnek kell lennie, és támogatnia kell a legújabb Android funkciókat, mint a dinamikus színek.

## Decision
A Material Design 3 (M3) rendszert használtam az UI kialakításához.

## Rationale
- **Dinamikus színek:** Az alkalmazás képes alkalmazkodni a felhasználó háttérképéhez (Android 12+), ami személyre szabottabb élményt nyújt.
- **Modern komponensek:** Az M3 frissített kártyákat, gombokat és navigációs elemeket tartalmaz, amik jobban illeszkednek a modern Android ökoszisztémába.
- **Hozzáférhetőség:** Az M3 tervezésénél kiemelt szempont volt az olvashatóság és a megfelelő kontrasztarányok biztosítása.

## Consequences
- Néhány régebbi Material 2 komponens lecserélése vagy átdolgozása szükséges volt.
- A sötét/világos mód támogatása kötelezővé vált a konzisztencia érdekében.
