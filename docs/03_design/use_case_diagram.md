# Fitness Tracker - Use Case Diagram

Az alábbi Use-Case (használati eset) diagram a rendszer aktorait (Vendég és Regisztrált felhasználó) és a hozzájuk tartozó funkciókat mutatja be, beleértve a kiterjesztés (extends) és tartalmazás (includes) kapcsolatokat a különböző rögzítési módszerek és az AI modulok között.

```mermaid
flowchart LR
    %% Aktorok
    Guest(["Vendég felhasználó"])
    RegisteredUser(["Regisztrált felhasználó"])

    %% Rendszer határ
    subgraph System ["Fitness Tracker Rendszer"]
        direction TB
        
        UC_Auth([Bejelentkezés / Regisztráció])
        UC_Profile([Profil és célok kezelése])
        
        UC_Food([Élelmiszer rögzítése])
        UC_Food_Manual([Manuális keresés])
        UC_Food_Barcode([Vonalkód-olvasás])
        UC_Food_AI([AI természetes nyelvi rögzítés])
        
        UC_Exercise([Mozgás rögzítése])
        UC_Exercise_AI([AI MET érték becslés])
        
        UC_Water([Vízfogyasztás rögzítése])
        UC_Dashboard([Napi Dashboard és Statisztikák megtekintése])
        UC_History([Napi napló előzmények kezelése - CRUD])
    end

    %% Vendég kapcsolatok
    Guest --> UC_Auth
    
    %% Regisztrált felhasználó kapcsolatok
    RegisteredUser --> UC_Profile
    RegisteredUser --> UC_Food
    RegisteredUser --> UC_Exercise
    RegisteredUser --> UC_Water
    RegisteredUser --> UC_Dashboard
    RegisteredUser --> UC_History

    %% Öröklődés (A regisztrált felhasználó is tud bejelentkezni)
    RegisteredUser -.->|öröklődés| Guest

    %% Extends / Includes kapcsolatok
    UC_Food_Manual -.->|extends| UC_Food
    UC_Food_Barcode -.->|extends| UC_Food
    UC_Food_AI -.->|extends| UC_Food
    
    UC_Exercise -.->|includes| UC_Exercise_AI
```

## A diagram értelmezése:

1. **Aktorok:**
   - **Vendég felhasználó:** Csak a publikus felületekhez fér hozzá (regisztráció és bejelentkezés).
   - **Regisztrált felhasználó:** Örökli a vendég képességeit, de hozzáfér a zárt rendszer összes funkciójához.

2. **Élelmiszer rögzítése (Extends kapcsolatok):**
   - Az élelmiszer rögzítése egy általános használati eset, amit 3 speciális folyamat terjeszthet ki (extends): *Vonalkód-olvasás*, *AI alapú rögzítés*, és a *Manuális keresés*.
   
3. **Mozgás rögzítése (Includes kapcsolat):**
   - Amikor egy felhasználó mozgást rögzít, az alkalmazás a háttérben kötelezően meghívja a Gemini AI-t a kalóriaégetés kiszámításához, ezért ez egy (includes) tartalmazó kapcsolat.
