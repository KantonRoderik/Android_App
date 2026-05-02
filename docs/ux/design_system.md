# Design rendszer / Vizuális nyelv

## UI könyvtár
* **Alap:** Android Material Components (Material 3 stílusjegyekkel)
* **View réteg:** Native XML Layouts (ViewBinding)
* **Grafikonok:** MPAndroidChart

## Színpaletta
| Típus | Színkód | Megjegyzés |
| :--- | :--- | :--- |
| **Primary** | `#4CAF50` | `primary_green` (Dashboard gombok, fő akcentus) |
| **Secondary** | `#2196F3` | `secondary_blue` (Víz hozzáadása, kiegészítő elemek) |
| **Carbs** | `#FF9800` | Szénhidrát progress bar és jelölések |
| **Protein** | `#03A9F4` | Fehérje progress bar és jelölések |
| **Fat** | `#9C27B0` | Zsír progress bar és jelölések |
| **Water** | `#00BCD4` | Vízfogyasztás progress bar |
| **Surface** | `#CCFFFFFF` | `glass_white` (Glassmorphism kártyák háttér) |
| **Text** | `#000000` | `text_main` (Sötét szöveg a kártyákon) |

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
* **Top Margin:** Immersive Mode miatt biztonsági sáv a fejlécben.

## Ikonkészlet
* **Material Symbols:** Google Material Icons (Baseline stílus)

## Sötét mód
* **Támogatott:** Igen (NoActionBar téma, `values-night` színekkel optimalizálva)
