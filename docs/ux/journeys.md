# User Journey - Top 3 felhasználói feladat

## 1. Regisztráció és Első Beállítás (Onboarding)
**Persona:** Új felhasználó, aki szeretné személyre szabott kalória célokkal elkezdeni a követést.
**Belépési pont:** Alkalmazás ikon (▶)
1. **S01 — Login:** A user a "Regisztráljon" linkre koppint.
2. **S02 — Register:** Kitölti a nevet, emailt és jelszót, majd a "Regisztráció" gombra nyom. → Átirányítás az onboardingra.
3. **S05 — Edit Data:** Megadja a súlyát, magasságát, korát és nemét.
4. **S05 — Edit Data:** Megnyomja az "Auto-Calculate Goals" gombot. A rendszer kiszámítja a BMR/TDEE értékeket és elmenti a Firestore-ba. → Megnyílik az S03 Dashboard.
**Sikerkritérium:** A felhasználó a főoldalon látja a saját testadatai alapján kiszámított napi kalória keretét.
**Mért időtartam:** ~45 másodperc / 6 kattintás.

## 2. Étel rögzítése vonalkód olvasóval
**Persona:** Felhasználó, aki gyorsan rögzíteni akar egy bolti sajtot.
**Belépési pont:** Dashboard (S03)
1. **S03 — Dashboard:** A user a Toolbar-ban lévő Scan ikonra koppint.
2. **S10 — Scanner:** A kamera keretébe helyezi a termék vonalkódját. A rendszer felismeri és lekéri az adatokat az OpenFoodFacts API-ból.
3. **Modális Dialógus (♦):** Megjelenik egy ablak a termék nevével. A user beírja: "30" (gramm).
4. **Modális Dialógus (♦):** Az "Add" gombra nyom. → Visszatérés az S03 Dashboardra.
**Sikerkritérium:** Az étel megjelenik a "Consumed Food" listában és a kalória mutató frissül.
**Mért időtartam:** ~15 másodperc / 3 kattintás + szkennelés.

## 3. Napi célok finomhangolása (Étrend váltás)
**Persona:** Tapasztalt felhasználó, aki Ketogén diétára szeretne váltani.
**Belépési pont:** Dashboard (S03)
1. **S03 — Dashboard:** A Profil ikonra koppint. → S04.
2. **S04 — Profile:** Az "Edit Daily Goals" gombra koppint. → S06.
3. **S06 — Daily Goals:** A legördülő listából kiválasztja a "Ketogenic" sablont. A rendszer automatikusan átírja a fehérje/szénhidrát/zsír grammokat (25%/5%/70%).
4. **S06 — Daily Goals:** A "Mentés" gombra koppint. → Visszatérés az S03 Dashboardra.
**Sikerkritérium:** A főoldali kördiagramok és progress barok már az új makróarányokat mutatják.
**Mért időtartam:** ~20 másodperc / 4 kattintás.
