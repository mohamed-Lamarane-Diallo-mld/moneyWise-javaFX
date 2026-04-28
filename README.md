Voici le README.md mis à jour avec tous les nouveaux fichiers et fonctionnalités :

```markdown
# MoneyWise - Application de Gestion Financière

**MoneyWise** est une application JavaFX complète de gestion financière personnelle. Elle permet aux utilisateurs de gérer leurs transactions, budgets, catégories, alertes et exporter leurs données.

---

## Table des Matières

- [Aperçu du Projet](#aperçu-du-projet)
- [Caractéristiques Principales](#caractéristiques-principales)
- [Architecture Technique](#architecture-technique)
- [Structure du Projet](#structure-du-projet)
- [Dépendances](#dépendances)
- [Installation et Configuration](#installation-et-configuration)
- [Guide d'Utilisation](#guide-dutilisation)
- [Scripts et Commandes](#scripts-et-commandes)

---

## Aperçu du Projet

**MoneyWise** est une solution centralisée pour la gestion des finances personnelles ou entreprise avec les fonctionnalités suivantes :

- **Authentification sécurisée** avec hashage de mot de passe (BCrypt)
- **Gestion des transactions** (ajout, modification, suppression, affichage)
- **Gestion des budgets** avec alertes de dépassement
- **Gestion des catégories** de dépenses/revenus
- **Système d'alertes** personnalisées
- **Statistiques et rapports** graphiques (personnels et globaux)
- **Gestion des utilisateurs** (Admin et Utilisateur)
- **Export de données** (PDF, Excel)
- **Journalisation des activités** (Logs)
- **Questions de sécurité** pour récupération de compte

---

## Caractéristiques Principales

### Authentification
- Système de login/inscription sécurisé
- Hashage BCrypt des mots de passe
- Questions de sécurité pour la récupération de compte
- Profil utilisateur personnalisable

### Gestion des Transactions
- Créer, modifier, supprimer des transactions
- Catégoriser les transactions (revenus/dépenses)
- Filtrer par date, catégorie, montant
- Vue détaillée des transactions
- Transactions personnelles pour chaque utilisateur
- Vue globale pour l'administrateur

### Budget et Alertes
- Définir des budgets par catégorie
- Alertes de dépassement automatiques
- Types d'alertes (Email, Système)
- Historique des alertes

### Statistiques
- Graphiques des dépenses par catégorie (Camembert)
- Évolution temporelle des transactions (Graphique à barres)
- Résumés mensuels/annuels
- Comparaisons budgétaires
- Statistiques personnelles pour tous les utilisateurs
- Statistiques globales pour l'administrateur

### Administration
- Gestion des utilisateurs (activation/désactivation/suppression)
- Gestion des catégories système
- Visualisation des logs d'activité
- **Vue globale de toutes les transactions** (admin uniquement)
- **Statistiques globales** (admin uniquement)

### Export
- Export en PDF
- Export en Excel
- Rapports formatés (personnels et globaux)

---

## Architecture Technique

### Stack Technologique
- **Langage** : Java 21
- **Framework UI** : JavaFX 21.0.2
- **Base de Données** : MySQL
- **Gestion des Projets** : Maven
- **Sécurité** : BCrypt (hashage des mots de passe)
- **PDF** : PDFBox 3.0.1
- **Excel** : Apache POI
- **Icônes** : Ikonli FontAwesome5
- **Connecteur DB** : MySQL Connector J 8.3.0

### Architecture MVC
```
Controller (JavaFX Controllers)
     ↓
Model (Entités métier)
     ↓
DAO (Data Access Objects - MySQL)
     ↓
Base de Données
```

---

## Structure du Projet

```
moneyWise-javaFX/
│
├── README.md                          # Documentation du projet
│
└── moneywise/                         # Racine du module Maven
    │
    ├── pom.xml                        # Configuration Maven
    │
    └── src/
        │
        ├── main/
        │   ├── java/com/project/
        │   │   ├── App.java                              # Point d'entrée JavaFX
        │   │   ├── module-info.java                      # Configuration modulaire Java
        │   │   │
        │   │   ├── controller/                           # Contrôleurs JavaFX
        │   │   │   ├── AdminCategoriesController.java    # Gestion des catégories (Admin)
        │   │   │   ├── AdminLogsController.java          # Visualisation des logs (Admin)
        │   │   │   ├── AdminUtilisateursController.java  # Gestion des utilisateurs (Admin)
        │   │   │   ├── AlertesController.java            # Gestion des alertes
        │   │   │   ├── BudgetModalController.java        # Modal pour budgets
        │   │   │   ├── CategorieModalController.java     # Modal pour catégories
        │   │   │   ├── HomeController.java               # Écran d'accueil
        │   │   │   ├── InscriptionController.java        # Page d'inscription
        │   │   │   ├── LoginController.java              # Page de connexion
        │   │   │   ├── ProfilController.java             # Profil utilisateur
        │   │   │   ├── RecuperationCompteController.java # Récupération de compte
        │   │   │   ├── SidebarController.java            # Barre de navigation latérale
        │   │   │   ├── StatistiqueController.java        # Graphiques et statistiques (personnel)
        │   │   │   ├── TransactionController.java        # Gestion des transactions (personnel)
        │   │   │   ├── TransactionModalController.java   # Modal pour transactions
        │   │   │   ├── UsersTransactionsController.java  # Vue globale des transactions (Admin)
        │   │   │   └── UsersStatistiqueController.java   # Statistiques globales (Admin)
        │   │   │
        │   │   ├── dao/                                  # Data Access Objects (Base de Données)
        │   │   │   ├── DatabaseConnection.java           # Connexion MySQL
        │   │   │   ├── AlerteDAO.java                    # CRUD Alertes
        │   │   │   ├── BudgetDAO.java                    # CRUD Budgets
        │   │   │   ├── CategorieDAO.java                 # CRUD Catégories
        │   │   │   ├── ExportDAO.java                    # Export de données
        │   │   │   ├── JournalDAO.java                   # Journalisation/Logs
        │   │   │   ├── QuestionSecuriteDAO.java          # Questions de sécurité
        │   │   │   ├── TransactionDAO.java               # CRUD Transactions
        │   │   │   └── UtilisateurDAO.java               # CRUD Utilisateurs
        │   │   │
        │   │   ├── enums/                                # Énumérations
        │   │   │   ├── FormatExport.java                 # Formats d'export (PDF, EXCEL)
        │   │   │   ├── TypeAlerte.java                   # Types d'alertes
        │   │   │   └── TypeTransaction.java              # Types de transactions (Entrée, Sortie)
        │   │   │
        │   │   ├── model/                                # Modèles métier
        │   │   │   ├── Alerte.java                       # Alerte
        │   │   │   ├── Budget.java                       # Budget
        │   │   │   ├── Categorie.java                    # Catégorie
        │   │   │   ├── Journal.java                      # Journal d'activité
        │   │   │   ├── QuestionSecurite.java             # Question de sécurité
        │   │   │   ├── Transaction.java                  # Transaction
        │   │   │   └── Utilisateur.java                  # Utilisateur
        │   │   │
        │   │   └── utils/                                # Utilitaires
        │   │       ├── AlerteHelper.java                 # Gestion des alertes
        │   │       ├── DateHelper.java                   # Manipulation des dates
        │   │       ├── NavigationHelper.java             # Navigation entre les vues
        │   │       ├── ResponsiveHelper.java             # Gestion responsive
        │   │       └── SessionManager.java               # Gestion de session
        │   │
        │   └── resources/com/project/
        │       ├── fonts/                                # Polices de caractères
        │       │   └── DejaVuSans.ttf                    # Police principale
        │       ├── images/                               # Ressources images (icônes, logos)
        │       ├── style/                                # Feuilles CSS
        │       │   ├── global.css                        # Styles globaux
        │       │   ├── alertes.css                       # Styles alertes
        │       │   ├── auth.css                          # Styles authentification
        │       │   ├── home.css                          # Styles accueil
        │       │   ├── modal.css                         # Styles modals
        │       │   ├── profil.css                        # Styles profil
        │       │   ├── sidebar.css                       # Styles barre latérale
        │       │   ├── statistique.css                   # Styles statistiques
        │       │   └── transaction.css                   # Styles transactions
        │       │
        │       └── view/                                 # Fichiers FXML (Interface UI)
        │           ├── AdminCategories.fxml              # Vue Admin Catégories
        │           ├── AdminLogs.fxml                    # Vue Admin Logs
        │           ├── AdminUtilisateurs.fxml            # Vue Admin Utilisateurs
        │           ├── Alertes.fxml                      # Vue Alertes
        │           ├── BudgetModal.fxml                  # Modal Budgets
        │           ├── CategorieModal.fxml               # Modal Catégories
        │           ├── Home.fxml                         # Vue Accueil
        │           ├── Inscription.fxml                  # Vue Inscription
        │           ├── Login.fxml                        # Vue Connexion
        │           ├── Profil.fxml                       # Vue Profil
        │           ├── RecuperationCompte.fxml           # Vue Récupération
        │           ├── Sidebar.fxml                      # Vue Barre latérale
        │           ├── Statistique.fxml                  # Vue Statistiques (personnel)
        │           ├── Transaction.fxml                  # Vue Transactions (personnel)
        │           ├── TransactionModal.fxml             # Modal Transactions
        │           ├── UsersTransactions.fxml            # Vue globale transactions (Admin)
        │           └── UsersStatistique.fxml             # Vue globale statistiques (Admin)
        │
        └── target/                                       # Output Maven (compilé)

```

---

## Dépendances

### JavaFX (Interface Utilisateur)
```xml
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>21.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-fxml</artifactId>
    <version>21.0.2</version>
</dependency>
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-graphics</artifactId>
    <version>21.0.2</version>
</dependency>
```

### Base de Données
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.3.0</version>
</dependency>
```

### Sécurité & Cryptographie
```xml
<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>
```

### Export PDF
```xml
<dependency>
    <groupId>org.apache.pdfbox</groupId>
    <artifactId>pdfbox</artifactId>
    <version>3.0.1</version>
</dependency>
```

### Export Excel
```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### Icônes
```xml
<dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-javafx</artifactId>
    <version>12.3.1</version>
</dependency>
<dependency>
    <groupId>org.kordamp.ikonli</groupId>
    <artifactId>ikonli-fontawesome5-pack</artifactId>
    <version>12.3.1</version>
</dependency>
```

---

## Installation et Configuration

### Prérequis
- **Java JDK 21** ou supérieur
- **MySQL Server** (5.7+)
- **Maven 3.6+**
- **IDE** : IntelliJ IDEA, Eclipse, ou VS Code avec extensions Java

### Étapes d'Installation

#### Cloner/Récupérer le Projet
```bash
git clone [repository-url]
cd moneyWise-javaFX
```

#### Configurer la Base de Données
1. Créer une base de données MySQL :
```sql
CREATE DATABASE moneywise_db;
```

2. Importer le schéma de base de données (s'il existe un fichier SQL) :
```bash
mysql -u root -p moneywise_db < database.sql
```

3. Configurer les paramètres de connexion dans `DatabaseConnection.java` :
```java
String url = "jdbc:mysql://localhost:3306/moneywise_db";
String user = "root";
String password = "votre_mot_de_passe";
```

#### Compiler le Projet
```bash
cd moneywise
mvn clean compile
```

#### Exécuter l'Application
```bash
mvn javafx:run
```

ou via l'IDE :
- Exécuter la classe `App.java` en tant qu'application JavaFX

---

## Guide d'Utilisation

### Authentification
1. **Première visite** : Cliquez sur "S'inscrire"
   - Remplissez le formulaire d'inscription
   - Choisissez des questions de sécurité
   - Acceptez les conditions

2. **Accès existant** : Connectez-vous avec vos identifiants
   - Email et mot de passe
   - Mot de passe oublié ? Utilisez la récupération par questions de sécurité

### Gestion des Transactions
- **Vue Transactions** : Consultez l'historique de vos transactions
- **Ajouter** : Cliquez sur "Nouvelle Transaction" → Sélectionnez le type → Confirmez
- **Modifier** : Cliquez sur une transaction → Modifiez → Sauvegardez
- **Supprimer** : Sélectionnez → Supprimez (confirmation requise)
- **Filtres** : Recherchez par date, catégorie, type, ou mot-clé

### Budgets
- **Ajouter un budget** : Catégorie + Limite mensuelle
- **Alertes** : Recevez des notifications si le budget est dépassé
- **Historique** : Consultez les budgets passés
- **Suivi** : Visualisez la progression des budgets avec des barres colorées

### Statistiques
- **Graphique camembert** : Visualisez vos dépenses par catégorie
- **Graphique à barres** : Évolution mensuelle des entrées/sorties
- **Périodes** : Consultez les statistiques par mois, trimestre ou année
- **Analyse rapide** : Indicateurs clés (revenus, dépenses, épargne, taux d'épargne)
- **Export** : Générez des rapports PDF ou Excel

### Administration (Utilisateur Admin)
L'administrateur a accès à des fonctionnalités supplémentaires dans le menu "ADMINISTRATION" :

- **Utilisateurs** : Activer/Désactiver/Supprimer des comptes utilisateurs
- **Catégories** : Ajouter/Modifier/Supprimer des catégories système
- **Journal** : Consulter l'historique des activités de tous les utilisateurs
- **Toutes transactions** : Vue globale de toutes les transactions du système
- **Toutes statistiques** : Statistiques globales sur l'ensemble des utilisateurs

### Export
- Sélectionnez la période (mois/trimestre/année)
- Choisissez le format (PDF ou Excel)
- Téléchargez le fichier généré
- L'admin peut exporter des rapports personnels ou globaux

---

## Scripts et Commandes Maven

### Compilation
```bash
mvn clean compile               # Nettoyer + Compiler
mvn compile                     # Compiler uniquement
```

### Exécution
```bash
mvn javafx:run                 # Exécuter l'application
```

### Packaging
```bash
mvn package                    # Créer un JAR
mvn clean package              # Nettoyer + Packager
```

### Nettoyage
```bash
mvn clean                      # Supprimer le dossier target/
```

### Installation Dépendances
```bash
mvn install                    # Télécharger et installer dépendances
```

---

## Détails des Fichiers Clés

### `App.java` - Point d'Entrée Principal
- Initialise l'application JavaFX
- Charge la scène de login
- Établit la connexion à la base de données
- Dimensions d'écran : 1200x750 (min 900x600)

### `DatabaseConnection.java`
- Gère la connexion MySQL
- Connexion unique partagée (Singleton)
- Méthodes utilitaires d'accès DB

### Controllers
- **TransactionController** : Gère les transactions personnelles de l'utilisateur
- **UsersTransactionsController** : Vue admin de toutes les transactions
- **StatistiqueController** : Statistiques personnelles
- **UsersStatistiqueController** : Statistiques globales (admin)
- **SidebarController** : Navigation latérale dynamique selon le rôle

### DAOs (Data Access Objects)
- Encapsulent l'accès à la base de données
- Méthodes CRUD standardisées
- Requêtes SQL optimisées
- Méthodes spécifiques pour les vues admin (recherche globale)

### Utilitaires
- **SessionManager** : Gère la session utilisateur (ID, rôle, données)
- **NavigationHelper** : Centralise la navigation entre les vues
- **AlerteHelper** : Gère les alertes et notifications
- **DateHelper** : Manipulation des dates et périodes

### Énumérations
- **TypeTransaction** : ENTRÉE, SORTIE
- **TypeAlerte** : EMAIL, SYSTÈME, PUSH
- **FormatExport** : PDF, EXCEL

### Ressources CSS
- Styles globaux centralisés dans `global.css`
- Styles spécifiques par vue
- Thème cohérent et responsive

---

## Structure des Rôles

| Rôle | Pages accessibles |
|------|-------------------|
| **Utilisateur normal** | Dashboard, Transactions, Statistiques, Profil, Alertes |
| **Administrateur** | Toutes les pages utilisateur + Utilisateurs, Catégories, Journal, Toutes transactions, Toutes statistiques |

---

## Dépannage

### Erreur de Connexion Database
- Vérifier que MySQL est démarré : `mysql -u root -p`
- Vérifier l'URL, l'utilisateur et le mot de passe dans `DatabaseConnection.java`
- Vérifier que la base de données `moneywise_db` existe

### Erreur JavaFX Runtime
- S'assurer que JavaFX est correctement configuré dans Maven
- Vérifier la version Java (21+)
- Réinstaller les dépendances : `mvn clean install`

### Fichiers FXML non trouvés
- Vérifier que les fichiers FXML sont dans `resources/com/project/view/`
- Vérifier le chemin de chargement dans les contrôleurs
- Nettoyer et recompiler : `mvn clean compile`

### La sidebar déborde
- Réduire la hauteur de la fenêtre ou agrandir l'écran
- La sidebar est optimisée pour une hauteur minimale de 650px

---

## Notes de Développement

- **Modules Java** : Le projet utilise `module-info.java` pour la modularité Java 9+
- **Maven** : Configuré pour Java 21, UTF-8 encoding
- **Sécurité** : Tous les mots de passe sont hashés avec BCrypt
- **Logs** : Activités enregistrées dans la base de données
- **UI** : Responsive, conçue pour 1200x750 minimum 900x600
- **Séparation des rôles** : Les vues admin sont séparées des vues utilisateur

---

## Licence

[À compléter selon votre licence]

---

## Auteur & Contribution

**Projet** : MoneyWise
**Créé** : 2026

Pour contribuer ou signaler un bug, veuillez créer une issue ou pull request.

---

**Dernière mise à jour** : Avril 2026
```

## Résumé des modifications du README :

1. **Ajout des nouveaux contrôleurs** :
   - `UsersTransactionsController.java`
   - `UsersStatistiqueController.java`

2. **Ajout des nouvelles vues FXML** :
   - `UsersTransactions.fxml`
   - `UsersStatistique.fxml`

3. **Ajout des dépendances** :
   - Apache POI pour Excel
   - Ikonli pour les icônes

4. **Mise à jour des fonctionnalités** :
   - Transactions personnelles vs globales
   - Statistiques personnelles vs globales
   - Structure des rôles (admin vs utilisateur)

5. **Ajout du modèle de données** :
   - Liste complète des entités

6. **Ajout des utilitaires** :
   - SessionManager, NavigationHelper, etc.