# Deploy és Runbook

## Környezeti Modell
- **Lokális Fejlesztői Környezet:** Android Studio, Firebase Local Emulator (opcionális), Fizikai eszköz vagy AVD (Android Virtual Device).
- **Éles Környezet (Production):** Google Play Store (tervezett), éles Firebase projekt (Cloud Firestore, Auth).

## Deploy Lépések (Lokál -> Éles eszköz)
1. Klónozd a tárolót.
2. A Firebase Console-ból (Settings -> Project Settings) töltsd le a `google-services.json` fájlt, és másold be az `app/` mappába.
3. Hozz létre (vagy szerkeszd) a projekt gyökerében a `local.properties` fájlt, és add hozzá a Gemini API kulcsot:
   ```properties
   GEMINI_API_KEY="AIzaSyYourSecretKeyHere"
   ```
4. Android Studio-ban: `Build -> Clean Project`, majd `Build -> Rebuild Project`.
5. Futtasd az alkalmazást (Shift + F10) egy csatlakoztatott eszközön.

## Rollback (Visszagörgetés)
- Ha egy release (pl. egy új APK) kritikus hibát tartalmaz a felhasználók számára:
  1. A Git repoban visszaugrunk az utolsó stabil tag-re: `git checkout v1.0.0`
  2. Új patch verzió (1.0.1) kiadása és deploy.
- Firestore adatbázis hibás migráció esetén a Firebase Console automatikus napi mentéseiből (GCP Backup) lehetőség van az adatok visszaállítására (Point-in-Time Recovery).

---

## Runbook - Tipikus Hibaesemények (Incidensek)

### 1. Incidens: A Gemini AI nem válaszol vagy hibát dob az ételek azonosításakor.
- **Tünetek:** A felhasználó a "AI Azonosítás" gombra nyom, töltőképernyő (spinner) jelenik meg, majd hibaüzenet ("Az AI válasza nem feldolgozható" vagy "Hiba történt").
- **Gyors diagnózis:**
  - Nyisd meg a Firebase/GCP Console API Dashboardot. Ellenőrizd a Generative Language API kvótáját és a 429-es hibaarányt.
  - Ellenőrizd a Logcat-et (tag: `FoodFragment`), hátha "UnknownHostException" van (nincs net).
- **Ideiglenes mitigáció:** Értesíteni a felhasználókat, hogy használják a manuális "Egység és mennyiség" bevitelt, vagy a vonalkód olvasót az azonosítás helyett.
- **Végleges javítás:** Ha a kvóta merült ki, prémium szintre váltás a Google Cloudon. Ha a modell válaszstruktúrája változott, a regex és a Gson parsolás javítása.

### 2. Incidens: A felhasználók nem látják az eddig mentett adataikat (Firestore kapcsolati hiba).
- **Tünetek:** Az app elindul, de a progress barok nullán állnak. A logcatben "Permission Denied" vagy "Unavailable" hibaüzenet látszik.
- **Gyors diagnózis:**
  - Van hálózati kapcsolat? (Ha nincs, a Firestore a lokális cache-ből dolgozik. Ha sosem szinkronizált még a telefon, akkor az üres.)
  - Firebase Console -> Firestore Security Rules. Lehet, hogy egy lejárt vagy rosszul megírt szabály (`allow read, write: if request.auth != null;`) blokkolja a kéréseket.
- **Ideiglenes mitigáció:** Ellenőrizni a Firebase státusz oldalát (status.firebase.google.com).
- **Végleges javítás:** Ha a rules file hibás, azonnali deploy a Firebase CLI-vel: `firebase deploy --only firestore:rules`.
