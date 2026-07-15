# Gestion des Étudiants — API REST CRUD (Spring Boot 4)

Projet réalisé dans le cadre du TP "Développement d'API REST CRUD avec Spring Boot"
(Dr Samba SIDIBE, ISEP-AT), dans le même style que le projet vu en classe (Spring Boot 4,
YAML, Lombok `@Data`, architecture en couches).

## 1. Architecture

```
Controller  ->  Service  ->  Repository  ->  Base de données
```

```
src/main/java/sn/isepat/gestionetudiants/
├── GestionEtudiantsApplication.java
├── config/OpenApiConfig.java          # config Swagger
├── entity/Etudiant.java               # entité JPA
├── repository/EtudiantRepository.java
├── service/EtudiantService.java
├── controller/EtudiantController.java # contrôles manuels + codes HTTP
└── dto/ApiError.java                  # format des erreurs {code, msg}
```

## 2. Prérequis

- Java 17+
- MySQL démarré en local
- **Pas besoin d'installer Maven** : le projet embarque le Maven Wrapper (`mvnw` / `mvnw.cmd`)

## 3. Créer la base de données (dev)

```sql
CREATE DATABASE IF NOT EXISTS isepat_dev;
```

Adapter si besoin `username` / `password` dans `application-dev.yml`.

## 4. Lancer l'application

**Sous Windows (PowerShell ou CMD) :**
```powershell
.\mvnw.cmd spring-boot:run
```

**Sous macOS/Linux :**
```bash
./mvnw spring-boot:run
```

Profil prod :
```powershell
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=prod"
```

> Si `.\mvnw.cmd` est refusé par PowerShell (politique d'exécution), lance plutôt :
> `powershell -ExecutionPolicy Bypass -File mvnw.cmd spring-boot:run`

## 5. Documentation Swagger

- Swagger UI : http://localhost:8080/swagger-ui.html
- JSON OpenAPI : http://localhost:8080/v3/api-docs

## 6. Endpoints

| Méthode | URL                                | Description                    |
|---------|--------------------------------------|---------------------------------|
| POST    | `/etudiants`                        | Ajouter un étudiant             |
| GET     | `/etudiants`                        | Lister les étudiants            |
| GET     | `/etudiants?tri=nom`                | Lister triés par nom (bonus)    |
| GET     | `/etudiants/{id}`                   | Rechercher par id               |
| GET     | `/etudiants/matricule/{matricule}`  | Rechercher par matricule (bonus)|
| PUT     | `/etudiants/{id}`                   | Modifier un étudiant            |
| DELETE  | `/etudiants/{id}`                   | Supprimer un étudiant           |

## 7. Codes HTTP

| Situation                  | Code |
|------------------------------|------|
| Création réussie            | 201  |
| Modification réussie        | 200  |
| Suppression réussie         | 204  |
| Champ obligatoire manquant  | 400  |
| Matricule/email déjà existant | 409 |
| Étudiant introuvable        | 404  |

Format des erreurs : `{ "code": 400, "msg": "Le matricule est obligatoire." }`

## 8. Scénarios de test (captures d'écran à faire sur Swagger UI)

**Test 1 — Ajout (201 Created)**
```json
POST /etudiants
{
  "matricule": "ET001",
  "prenom": "Amy",
  "nom": "Cisse",
  "email": "amy@universite.sn",
  "dateNaissance": "2003-04-15",
  "lieuNaissance": "Thiès",
  "nationalite": "Sénégalaise"
}
```

**Test 2 — Matricule déjà existant (409)** : rejouer avec le même matricule, email différent.
**Test 3 — Email déjà existant (409)** : rejouer avec le même email, matricule différent.
**Test 4 — Champ manquant (400)** : envoyer avec `"prenom": ""`.
**Test 5 — Recherche inexistante (404)** : `GET /etudiants/100`
**Test 6 — Suppression (204)** : `DELETE /etudiants/1`

## 9. En cas de crash mémoire au démarrage (OutOfMemoryError)

Si l'application plante au lancement avec une erreur mémoire (fichiers `hs_err_pid*.log`
générés), limite la mémoire utilisée par la JVM :
```powershell
$env:MAVEN_OPTS="-Xmx512m"
.\mvnw.cmd spring-boot:run
```
Et ferme les autres applications gourmandes en RAM (IDE, navigateur...) avant de lancer.
