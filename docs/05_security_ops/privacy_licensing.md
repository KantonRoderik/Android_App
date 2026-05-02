# Privacy & Licensing

## Adatkezelés (Privacy)

1. **Adatkategóriák:**
   - **Személyes azonosítók:** Email cím, Név, Google Auth azonosító.
   - **Szenzitív (Egészségügyi) adatok:** Súly, Magasság, Életkor, Nem, napi étel- és vízfogyasztás, mozgásnapló.

2. **Adatáramlás és Tárolás:**
   - Az adatokat a Google Cloud (Firebase Auth és Cloud Firestore) tárolja európai (vagy amerikai) szervereken.
   - A *Gemini AI API* felé **CSAK az étel vagy mozgás neve** kerül elküldésre (pl. "Rántott hús"). Felhasználóhoz köthető személyes adat (PII), testsúly vagy kor **NEM** kerül átadásra a Google LLM-jének promptként.

3. **Adatmegőrzés és Hozzáférés:**
   - Az adatokat a felhasználó fiókjának törléséig őrizzük.
   - Hozzáférés csak a tulajdonos (`auth.uid == resource.id`) számára engedélyezett a Firestore Security Rules alapján. Nincs admin felület.

## Licensing (Harmadik fél függőségek)

A projekt nyílt forráskódú könyvtárakat használ. A főbb függőségek:
- **Firebase SDK (Google):** Apache 2.0
- **Retrofit & OkHttp (Square):** Apache 2.0
- **Google Generative AI (Gemini):** Apache 2.0
- **MPAndroidChart:** Apache 2.0
- **ML Kit Barcode Scanning:** Google Terms of Service

A projekt licencelése (saját kód) nyílt forráskódú bemutató jelleggel készült.
