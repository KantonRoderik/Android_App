# ADR 2: MVVM architektúra alkalmazása

## Status
Accepted

## Context
Az Android alkalmazás kódjának átláthatónak, tesztelhetőnek és fenntarthatónak kell lennie. El kell különíteni az üzleti logikát a megjelenítéstől.

## Decision
Az MVVM (Model-View-ViewModel) tervezési mintát alkalmaztam a Google ajánlása (Architecture Components) alapján.

## Rationale
- **Szétválasztás (Separation of Concerns):** A UI (Activity/Fragment) csak a megjelenítéssel foglalkozik, a ViewModel kezeli az adatokat és az üzleti logikát.
- **Életciklus kezelés:** A ViewModel túlélConfiguration change-eket (pl. képernyő elforgatás), így az adatok nem vesznek el.
- **Tesztelhetőség:** A ViewModel-ek könnyebben tesztelhetőek egységtesztekkel, mivel nincsenek közvetlen Android framework függőségeik (pl. Context).

## Consequences
- Több boilerplate kód (ViewModel gyárak, LiveData/StateFlow megfigyelők).
- Kezdőknek bonyolultabb lehet a kezdeti beállítás, de hosszabb távon kifizetődik.
