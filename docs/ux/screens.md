# Képernyő-leírás táblázat

| id | nev | cel | belepesi_pont | auth_szukseges | fo_interakciok | adatforrasok | validaciok | kezelt_allapotok | a11y |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| S01 | Login | Felhasználói hitelesítés | induló | nem | Email/Jelszó mezők; Login gomb; Regisztráció link; Google bejelentkezés | Firebase Auth | Üres mező; Formátum ellenőrzés | error (hibás adatok); success | TalkBack támogatás; Nagy kontrasztú gombok |
| S02 | Register | Új fiók létrehozása | S01 | nem | Név/Email/Jelszó mezők; Register gomb; Login link; Google regisztráció | Firebase Auth; Firestore | Jelszó hossz; Jelszó egyezés; Email validáció | error; success | Logikus fókusz sorrend |
| S03 | Dashboard | Napi haladás vizualizálása | S00, S01, S05 | igen | Dátum váltás; Navigációs ikonok; Étel törlése | Firestore (users, dailyEntries) | - | loading; empty (nincs fogyasztás) | Progress bar leírások |
| S04 | Profile | Felhasználói adatok megtekintése | S03 | igen | Szerkesztés gombok; Vissza gomb | Firestore (users) | - | loading; success | Olvasható betűméret |
| S05 | Edit Data | Testadatok megadása | S02, S04 | igen | Mezők kitöltése; Auto-Calculate gomb; Mentés | Firestore (users); NutritionCalculator | Súly/Magasság/Kor tartomány validáció | success; error | Input típusok (number pad) |
| S06 | Daily Goals | Napi makró célok szerkesztése | S04 | igen | Template spinner; Manuális bevitel; Mentés | Firestore (users); DietaryTemplate | Számformátum ellenőrzés | success; error | Legördülő lista címkék |
| S07 | Statistics | Heti/Havi trendek elemzése | S03 | igen | Grafikon interakciók; Vissza gomb | Firestore (dailyEntries) | - | loading; empty (nincs elég adat) | Grafikon színek elkülönítése |
| S08 | Add Food | Étel manuális rögzítése | S03 | igen | Spinner választás; Mennyiség bevitel; Mentés; Vissza | Firestore (foods, dailyEntries) | Mennyiség > 0 | success; error | Kereshető lista (tervezett) |
| S09 | Add Water | Vízfogyasztás rögzítése | S03 | igen | Gyorsgombok (200ml, 500ml); Egyéni bevitel; Mentés | Firestore (dailyEntries) | Pozitív szám | success; error | Egyértelmű ikonok |
| S10 | Scanner | Vonalkódos ételkeresés | S03 | igen | Kamera keretezés; Mennyiség dialógus; Vissza | ML Kit; OpenFoodFacts API | Vonalkód felismerés | error (nem található); loading (API hívás) | Haptikus visszajelzés (tervezett) |
