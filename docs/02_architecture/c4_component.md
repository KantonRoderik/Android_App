# C4 Component Diagram

Ez a diagram az Android alkalmazás belső komponenseit és mappastruktúráját (Layer Boundaries) ábrázolja, bemutatva a felelősségi körök szétválasztását.

```mermaid
C4Component
    title Component Diagram for Fitness Tracker Android App

    Container_Boundary(app, "Android Application") {
        
        Component(ui, "UI Layer", "Activities/Fragments", "Felhasználói interakciók és ViewBinding (MainActivity, FoodFragment, Profile).")
        
        Component(helpers, "Helpers & Utils", "Java Classes", "Üzleti logika, számítások és transzformációk (NutritionCalculator, UIUtils).")
        
        Component(models, "Data Models", "POJO", "A rendszer adatszerkezetei (DailyGoals, ConsumedFood, Exercise).")
        
        Component(repo, "FirestoreRepository", "Singleton", "Egyetlen belépési pont az adateléréshez. Absztrahálja a Firebase hívásokat.")
        
        Component(network, "Network Layer", "Retrofit Interfaces", "Külső HTTP kommunikáció kezelése (OpenFoodFactsApi).")
    }

    System_Ext(firestore, "Firebase Cloud Firestore", "Database")
    System_Ext(gemini, "Gemini API", "LLM")
    System_Ext(off_api, "OpenFoodFacts API", "REST API")

    Rel(ui, models, "Használja")
    Rel(ui, helpers, "Delegálja a logikát")
    Rel(ui, repo, "Adatot kér/ír")
    Rel(ui, network, "Hívást indít")
    Rel(ui, gemini, "GenerativeModelFutures")

    Rel(repo, firestore, "Firebase SDK")
    Rel(repo, models, "Ment/Olvas")
    
    Rel(network, off_api, "GET JSON")
```

## Komponensek Leírása
* **UI Layer:** Itt találhatók a képernyők. Közvetlenül nem végeznek adatbázis műveletet és nem számolnak makrókat, hanem a `Helpers` és a `Repository` rétegre támaszkodnak.
* **Helpers:** Célja a kód újrahasználhatósága és a tesztelhetőség. A `NutritionCalculator` például tisztán kapott paraméterekkel dolgozik, így hálózat és kontextus nélkül tesztelhető.
* **Repository:** A "Single Source of Truth" mintát követve ez az egyetlen osztály, amely importálja a `FirebaseFirestore` objektumokat. Ha a jövőben adatbázist cserélnénk, csak ezt az egy osztályt kell átírni.
