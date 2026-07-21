# Ghid Pas cu Pas: Activare Vertex AI în Firebase

Pentru a putea folosi Gemini fără a expune un API Key, trebuie să activezi serviciul Vertex AI direct în proiectul tău Firebase. Urmează acești pași:

### 1. Accesează Consola Firebase
*   Deschide browserul și mergi la [console.firebase.google.com](https://console.firebase.google.com/).
*   Asigură-te că ești logat cu același cont Google pe care l-ai folosit pentru a crea aplicația Android.

### 2. Selectează Proiectul Tău
*   În lista de proiecte, caută și apasă pe **"Car Activity Log"** (sau numele pe care l-ai dat proiectului în Firebase).

### 3. Mergi la Secțiunea Vertex AI
*   În meniul din partea stângă, caută categoria **Build** (Construiește).
*   Sub **Build**, vei găsi **Vertex AI**. Apasă pe el.

### 4. Activează Vertex AI
*   Vei vedea un ecran de bun venit pentru Vertex AI in Firebase. Apasă pe butonul albastru **"Get Started"** (Începe).
*   **Important:** Firebase te va ghida prin 2-3 pași de configurare:
    1.  **Enable APIs**: Va trebui să apeși pe un buton pentru a activa API-urile necesare în Google Cloud (Vertex AI API).
    2.  **Location**: Dacă te întreabă de locație, alege o regiune apropiată de România (ex: `europe-west3` - Frankfurt sau `europe-west9` - Paris) pentru latență mică.

### 5. Verifică Planul Proiectului (Spark vs Blaze)
*   > [!NOTE]
    > Vertex AI face parte din planul **Blaze (Pay-as-you-go)**.
    > *   Dacă ești pe planul gratuit (Spark), Firebase îți va cere să faci upgrade la Blaze.
    > *   **Stai liniștit:** Există un **Free Tier** generos pentru Gemini 1.5 Flash. Atâta timp cât nu depășești un număr mare de scanări pe zi, nu vei fi taxat, dar cardul trebuie atașat pentru validare.

### 6. Gata!
Odată ce ai terminat acești pași în consolă, eu pot începe să scriu codul în Android Studio. Aplicația se va autentifica automat folosind fișierul `google-services.json` pe care îl ai deja în proiect.

---
**Când ai terminat de activat Vertex AI în consolă, spune-mi "Am activat Vertex AI" și trecem la treabă!**
