# Design rendszer / Vizuális nyelv

## UI könyvtár
* **Alap:** Android Material Components (Material 3 stílusjegyekkel)
* **View réteg:** Native XML Layouts (ViewBinding)
* **Grafikonok:** MPAndroidChart

## Színpaletta
| Típus | Színkód | Megjegyzés |
| :--- | :--- | :--- |
| **Primary** | `#4CAF50` | Fő zöld (Dashboard, Gombok) |
| **Secondary** | `#2196F3` | Kék (Szerkesztés, Víz) |
| **Accent** | `#FFC107` | Szénhidrát progress bar |
| **Success** | `#81C784` | Mentés és siker visszajelzések |
| **Error** | `#F44336` | Hibaüzenetek, Törlés gomb |
| **Surface** | `#CCFFFFFF` | Glassmorphism kártyák (semi-transparent white) |
| **Text** | `#000000` | Sötét szöveg a világos kártyákon |
| **Background** | - | `login_background` (Custom drawable gradiens) |

## Tipográfia
* **Font család:** Roboto (alapértelmezett Android)
* **Méret-skála:**
    * Címek: 28sp - 32sp (Bold)
    * Alcímek: 20sp - 22sp (Bold)
    * Törzsszöveg: 16sp - 18sp
    * Kisegítő szöveg: 12sp - 14sp

## Spacing / Grid
* **Alap egység:** 8dp
* **Padding:** Általános képernyő padding: 24dp; Kártya belső padding: 16dp-20dp
* **Top Margin:** Immersive Mode miatt 24dp - 48dp biztonsági sáv a fejlécben.

## Ikonkészlet
* **Material Symbols:** Google Material Icons (Baseline stílus)

## Sötét mód
* **Támogatott:** Igen (NoActionBar téma, `values-night` színekkel optimalizálva)
