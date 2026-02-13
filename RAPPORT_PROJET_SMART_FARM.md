# 📋 RAPPORT COMPLET DU PROJET SMART FARM

## 📌 Informations Générales

| Élément | Description |
|---------|-------------|
| **Nom du Projet** | Smart Farm - Gestion Agricole |
| **Technologie** | JavaFX 17 + MySQL |
| **Architecture** | MVC (Modèle-Vue-Contrôleur) |
| **IDE** | IntelliJ IDEA |
| **Build Tool** | Maven |
| **Base de données** | MySQL (smart_farm) |

---

## 🏗️ ARCHITECTURE DU PROJET

```
pidev/
├── src/main/java/org/example/pidev/
│   ├── controllers/        # Contrôleurs JavaFX (logique de l'interface)
│   ├── interfaces/          # Interfaces génériques (IService)
│   ├── models/              # Entités/Classes métier (Parcelle, Culture...)
│   ├── services/            # Services CRUD (accès base de données)
│   ├── test/                # Classes de lancement (mainFX, Launcher)
│   └── utils/               # Utilitaires (DBConnection)
├── src/main/resources/      # Fichiers FXML (interfaces graphiques)
└── pom.xml                  # Configuration Maven
```

---

## 🎨 CHARTE GRAPHIQUE

| Couleur | Code Hex | Utilisation |
|---------|----------|-------------|
| **Vert foncé** | `#2E7D32` | Barre de navigation, titres |
| **Vert clair** | `#4CAF50` | Boutons d'action (Ajouter) |
| **Marron foncé** | `#5D4037` | Labels, textes |
| **Marron clair** | `#8D6E63` | Bordures, bouton Annuler |
| **Blanc** | `#FFFFFF` | Fond des pages |
| **Orange** | `#FF9800` | Bouton Modifier |
| **Rouge** | `#F44336` | Bouton Supprimer |

---

# 📚 EXPLICATION DÉTAILLÉE DU CODE

---

## 1️⃣ DBConnection.java - Connexion à la Base de Données

### 📍 Localisation : `utils/DBConnection.java`

```java
package org.example.pidev.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ═══════════════════════════════════════════════════════════════
    // CONSTANTES DE CONNEXION
    // ═══════════════════════════════════════════════════════════════
    
    private final String URL = "jdbc:mysql://localhost:3306/smart_farm";
    // → URL de connexion JDBC vers MySQL
    // → localhost:3306 = serveur local, port par défaut MySQL
    // → smart_farm = nom de la base de données
    
    private final String USER = "root";
    // → Nom d'utilisateur MySQL (root par défaut)
    
    private final String PASSWORD = "";
    // → Mot de passe MySQL (vide pour XAMPP par défaut)

    // ═══════════════════════════════════════════════════════════════
    // PATTERN SINGLETON
    // ═══════════════════════════════════════════════════════════════
    
    private static Connection connection;
    // → Variable statique qui stocke l'unique connexion
    
    private static DBConnection instance;
    // → Instance unique de la classe (Singleton)

    // Constructeur PRIVÉ - empêche la création multiple d'instances
    private DBConnection() {
        try {
            // Établir la connexion avec la base de données
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("✅ Connected to database successfully");
        } catch (SQLException e) {
            // En cas d'erreur (BD non accessible, mauvais identifiants...)
            System.out.println("❌ Database connection error: " + e.getMessage());
        }
    }

    // Méthode pour obtenir l'instance unique (Singleton)
    public static DBConnection getInstance() {
        if (instance == null) {
            // Créer l'instance seulement si elle n'existe pas
            instance = new DBConnection();
        }
        return instance;
    }

    // Méthode pour obtenir la connexion
    public static Connection getConnection() {
        if (instance == null) {
            getInstance(); // Créer l'instance si nécessaire
        }
        return connection; // Retourner la connexion existante
    }
}
```

### 🔑 Points Clés - DBConnection :

1. **Pattern Singleton** : Une seule instance de connexion dans toute l'application
   - Économise les ressources
   - Évite les conflits de connexion
   
2. **Constructeur privé** : Empêche `new DBConnection()` depuis l'extérieur

3. **Méthode statique `getConnection()`** : Accessible partout via `DBConnection.getConnection()`

---

## 2️⃣ IService.java - Interface Générique CRUD

### 📍 Localisation : `interfaces/IService.java`

```java
package org.example.pidev.interfaces;

import java.util.List;

// Interface générique avec paramètre de type <T>
// T sera remplacé par Parcelle, Culture, etc.
public interface IService<T> {

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODES CRUD (Create, Read, Update, Delete)
    // ═══════════════════════════════════════════════════════════════

    boolean add(T t);
    // → CREATE : Ajouter un nouvel élément
    // → Retourne true si succès, false sinon

    void update(T t);
    // → UPDATE : Modifier un élément existant

    void delete(int id);
    // → DELETE : Supprimer par ID

    T getById(int id);
    // → READ : Récupérer un élément par son ID

    List<T> getAll();
    // → READ : Récupérer tous les éléments
}
```

### 🔑 Points Clés - IService :

1. **Généricité `<T>`** : Réutilisable pour n'importe quelle entité
2. **Contrat** : Toute classe qui implémente cette interface DOIT définir ces 5 méthodes
3. **CRUD complet** : Create, Read, Update, Delete

---

## 3️⃣ Parcelle.java - Modèle/Entité

### 📍 Localisation : `models/Parcelle.java`

```java
package org.example.pidev.models;

public class Parcelle {
    
    // ═══════════════════════════════════════════════════════════════
    // ATTRIBUTS (correspondent aux colonnes de la table SQL)
    // ═══════════════════════════════════════════════════════════════
    
    private int idParcelle;      // Clé primaire AUTO_INCREMENT
    private String nom;           // Nom de la parcelle (VARCHAR 100)
    private double superficie;    // Superficie en m² (DOUBLE)
    private String localisation;  // Adresse/coordonnées (VARCHAR 150)
    private String etat;          // active, repos, exploitée (VARCHAR 50)
    private int idUser;           // Clé étrangère → utilisateur

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTEURS
    // ═══════════════════════════════════════════════════════════════
    
    // Constructeur vide (requis pour JavaFX et certains frameworks)
    public Parcelle() {
    }

    // Constructeur complet (avec ID - pour récupération depuis BD)
    public Parcelle(int idParcelle, String nom, double superficie, 
                    String localisation, String etat, int idUser) {
        this.idParcelle = idParcelle;
        this.nom = nom;
        this.superficie = superficie;
        this.localisation = localisation;
        this.etat = etat;
        this.idUser = idUser;
    }

    // Constructeur sans ID (pour création - ID auto-généré par MySQL)
    public Parcelle(String nom, double superficie, String localisation, 
                    String etat, int idUser) {
        this.nom = nom;
        this.superficie = superficie;
        this.localisation = localisation;
        this.etat = etat;
        this.idUser = idUser;
    }

    // ═══════════════════════════════════════════════════════════════
    // GETTERS & SETTERS
    // ═══════════════════════════════════════════════════════════════
    
    public int getIdParcelle() { return idParcelle; }
    public void setIdParcelle(int idParcelle) { this.idParcelle = idParcelle; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public double getSuperficie() { return superficie; }
    public void setSuperficie(double superficie) { this.superficie = superficie; }
    
    public String getLocalisation() { return localisation; }
    public void setLocalisation(String localisation) { this.localisation = localisation; }
    
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    
    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    // ═══════════════════════════════════════════════════════════════
    // toString() - Pour affichage/debug
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public String toString() {
        return "Parcelle{" +
                "idParcelle=" + idParcelle +
                ", nom='" + nom + '\'' +
                ", superficie=" + superficie +
                ", localisation='" + localisation + '\'' +
                ", etat='" + etat + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
```

### 🔑 Points Clés - Modèle :

1. **Encapsulation** : Attributs privés + getters/setters publics
2. **Correspondance BD** : Chaque attribut = une colonne SQL
3. **2 Constructeurs** : Avec ID (lecture) et sans ID (création)

---

## 4️⃣ ParcelleService.java - Service CRUD

### 📍 Localisation : `services/ParcelleService.java`

```java
package org.example.pidev.services;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Parcelle;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParcelleService implements IService<Parcelle> {

    private Connection connection;

    // ═══════════════════════════════════════════════════════════════
    // CONSTANTES DE VALIDATION
    // ═══════════════════════════════════════════════════════════════
    
    private static final List<String> ETATS_VALIDES = 
        Arrays.asList("active", "repos", "exploitée");
    
    private static final int NOM_MIN_LENGTH = 2;
    private static final int NOM_MAX_LENGTH = 100;
    private static final double SUPERFICIE_MIN = 0.1;
    private static final double SUPERFICIE_MAX = 100000;

    // ═══════════════════════════════════════════════════════════════
    // CONSTRUCTEUR
    // ═══════════════════════════════════════════════════════════════
    
    public ParcelleService() {
        // Récupérer la connexion unique (Singleton)
        connection = DBConnection.getConnection();
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODES DE VALIDATION (Contrôle de saisie)
    // ═══════════════════════════════════════════════════════════════
    
    public void valider(Parcelle parcelle) throws IllegalArgumentException {
        validerNom(parcelle.getNom());
        validerSuperficie(parcelle.getSuperficie());
        validerLocalisation(parcelle.getLocalisation());
        validerEtat(parcelle.getEtat());
        validerIdUser(parcelle.getIdUser());
    }

    public void validerNom(String nom) throws IllegalArgumentException {
        if (nom == null || nom.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être vide.");
        }
        if (nom.trim().length() < NOM_MIN_LENGTH) {
            throw new IllegalArgumentException(
                "Le nom doit contenir au moins " + NOM_MIN_LENGTH + " caractères.");
        }
        if (nom.trim().length() > NOM_MAX_LENGTH) {
            throw new IllegalArgumentException(
                "Le nom ne peut pas dépasser " + NOM_MAX_LENGTH + " caractères.");
        }
        // Protection contre injection SQL et XSS
        if (nom.matches(".*[<>\"'%;()&+].*")) {
            throw new IllegalArgumentException("Caractères non autorisés.");
        }
    }

    public void validerSuperficie(double superficie) throws IllegalArgumentException {
        if (superficie < SUPERFICIE_MIN) {
            throw new IllegalArgumentException(
                "La superficie doit être supérieure à " + SUPERFICIE_MIN + " m².");
        }
        if (superficie > SUPERFICIE_MAX) {
            throw new IllegalArgumentException(
                "La superficie ne peut pas dépasser " + SUPERFICIE_MAX + " m².");
        }
    }

    public void validerEtat(String etat) throws IllegalArgumentException {
        if (!ETATS_VALIDES.contains(etat.toLowerCase())) {
            throw new IllegalArgumentException(
                "L'état doit être: " + ETATS_VALIDES);
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE ADD (CREATE)
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public boolean add(Parcelle parcelle) {
        // 1. Validation des données
        try {
            valider(parcelle);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur de validation: " + e.getMessage());
            return false;
        }

        // 2. Requête SQL préparée (protection injection SQL)
        String query = "INSERT INTO parcelle (nom, superficie, localisation, etat, id_user) " +
                       "VALUES (?, ?, ?, ?, ?)";
        
        try {
            // PreparedStatement : requête précompilée + sécurisée
            PreparedStatement pst = connection.prepareStatement(
                query, 
                Statement.RETURN_GENERATED_KEYS  // Pour récupérer l'ID auto-généré
            );
            
            // Remplir les paramètres (? dans la requête)
            pst.setString(1, parcelle.getNom().trim());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation().trim());
            pst.setString(4, parcelle.getEtat().toLowerCase());
            pst.setInt(5, parcelle.getIdUser());
            
            // Exécuter l'insertion
            pst.executeUpdate();

            // Récupérer l'ID auto-généré
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                parcelle.setIdParcelle(rs.getInt(1));
            }

            System.out.println("✅ Parcelle ajoutée (ID: " + parcelle.getIdParcelle() + ")");
            return true;
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur SQL: " + e.getMessage());
            return false;
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE UPDATE
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public void update(Parcelle parcelle) {
        // Validation
        try {
            valider(parcelle);
        } catch (IllegalArgumentException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
            return;
        }

        String query = "UPDATE parcelle SET nom = ?, superficie = ?, " +
                       "localisation = ?, etat = ?, id_user = ? " +
                       "WHERE id_parcelle = ?";
        
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setString(1, parcelle.getNom().trim());
            pst.setDouble(2, parcelle.getSuperficie());
            pst.setString(3, parcelle.getLocalisation().trim());
            pst.setString(4, parcelle.getEtat().toLowerCase());
            pst.setInt(5, parcelle.getIdUser());
            pst.setInt(6, parcelle.getIdParcelle()); // WHERE condition
            
            pst.executeUpdate();
            System.out.println("✅ Parcelle mise à jour");
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE DELETE
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public void delete(int id) {
        String query = "DELETE FROM parcelle WHERE id_parcelle = ?";
        
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("✅ Parcelle supprimée");
            
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE GET BY ID
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public Parcelle getById(int id) {
        String query = "SELECT * FROM parcelle WHERE id_parcelle = ?";
        
        try {
            PreparedStatement pst = connection.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // Construire l'objet Parcelle depuis le ResultSet
                return new Parcelle(
                    rs.getInt("id_parcelle"),
                    rs.getString("nom"),
                    rs.getDouble("superficie"),
                    rs.getString("localisation"),
                    rs.getString("etat"),
                    rs.getInt("id_user")
                );
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        return null;
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE GET ALL
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public List<Parcelle> getAll() {
        List<Parcelle> parcelles = new ArrayList<>();
        String query = "SELECT * FROM parcelle";
        
        try {
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery(query);
            
            // Parcourir tous les résultats
            while (rs.next()) {
                Parcelle parcelle = new Parcelle(
                    rs.getInt("id_parcelle"),
                    rs.getString("nom"),
                    rs.getDouble("superficie"),
                    rs.getString("localisation"),
                    rs.getString("etat"),
                    rs.getInt("id_user")
                );
                parcelles.add(parcelle);
            }
        } catch (SQLException e) {
            System.out.println("❌ Erreur: " + e.getMessage());
        }
        return parcelles;
    }
}
```

### 🔑 Points Clés - Service :

1. **Implémente IService<Parcelle>** : Contrat respecté
2. **Validation dans le service** : Pas dans le contrôleur ni le launcher
3. **PreparedStatement** : Protection contre injection SQL
4. **RETURN_GENERATED_KEYS** : Récupérer l'ID auto-increment

---

## 5️⃣ AjouterParcelleController.java - Contrôleur JavaFX

### 📍 Localisation : `controllers/AjouterParcelleController.java`

```java
package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AjouterParcelleController implements Initializable {

    // ═══════════════════════════════════════════════════════════════
    // ID UTILISATEUR CONNECTÉ (par défaut = 1)
    // ═══════════════════════════════════════════════════════════════
    
    private static final int CURRENT_USER_ID = 1;
    // → Simule l'utilisateur connecté
    // → Sera remplacé par un vrai système d'authentification

    // ═══════════════════════════════════════════════════════════════
    // ÉLÉMENTS FXML (liés au fichier ajouterparcelle.fxml)
    // ═══════════════════════════════════════════════════════════════
    
    @FXML
    private TextField tfNom;           // Champ nom
    
    @FXML
    private TextField tfSuperficie;    // Champ superficie
    
    @FXML
    private TextField tfLocalisation;  // Champ localisation
    
    @FXML
    private ComboBox<String> cbEtat;   // Liste déroulante états
    
    @FXML
    private Label lblError;            // Message d'erreur (rouge)
    
    @FXML
    private Label lblSuccess;          // Message succès (vert)

    private ParcelleService parcelleService;

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE initialize() - Appelée au chargement du FXML
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Créer le service
        parcelleService = new ParcelleService();

        // Remplir le ComboBox avec les états possibles
        cbEtat.setItems(FXCollections.observableArrayList(
            "active", "repos", "exploitée"
        ));

        // Effacer messages quand l'utilisateur tape
        tfNom.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfSuperficie.textProperty().addListener((obs, old, newVal) -> clearMessages());
        tfLocalisation.textProperty().addListener((obs, old, newVal) -> clearMessages());
        cbEtat.valueProperty().addListener((obs, old, newVal) -> clearMessages());
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE ajouterParcelle() - Liée au bouton "Ajouter"
    // ═══════════════════════════════════════════════════════════════
    
    @FXML
    void ajouterParcelle(ActionEvent event) {
        clearMessages();

        try {
            // 1. Récupérer les valeurs des champs
            String nom = tfNom.getText();
            String superficieStr = tfSuperficie.getText();
            String localisation = tfLocalisation.getText();
            String etat = cbEtat.getValue();

            // 2. Validation côté contrôleur (champs vides)
            if (nom == null || nom.trim().isEmpty()) {
                showError("Le nom de la parcelle est obligatoire.");
                return;
            }

            if (superficieStr == null || superficieStr.trim().isEmpty()) {
                showError("La superficie est obligatoire.");
                return;
            }

            if (etat == null || etat.trim().isEmpty()) {
                showError("Veuillez sélectionner un état.");
                return;
            }

            // 3. Conversion de la superficie (String → double)
            double superficie;
            try {
                superficie = Double.parseDouble(superficieStr.trim());
            } catch (NumberFormatException e) {
                showError("La superficie doit être un nombre valide.");
                return;
            }

            // 4. Créer l'objet Parcelle
            Parcelle parcelle = new Parcelle(
                nom.trim(), 
                superficie, 
                localisation.trim(), 
                etat, 
                CURRENT_USER_ID  // ID utilisateur connecté
            );

            // 5. Appeler le service (qui fait aussi la validation)
            parcelleService.add(parcelle);

            // 6. Afficher succès et vider les champs
            showSuccess("✅ Parcelle ajoutée avec succès !");
            clearFields();

        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // NAVIGATION ENTRE LES PAGES
    // ═══════════════════════════════════════════════════════════════
    
    @FXML
    void navigateToConsulterParcelle(ActionEvent event) {
        navigateTo("/consulterparcelle.fxml", "Liste des Parcelles");
    }

    @FXML
    void navigateToConsulterCulture(ActionEvent event) {
        navigateTo("/consulterculture.fxml", "Liste des Cultures");
    }

    @FXML
    void navigateToAjouterCulture(ActionEvent event) {
        navigateTo("/ajouterculture.fxml", "Ajouter une Culture");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            // 1. Charger le nouveau FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            // 2. Récupérer la fenêtre actuelle
            Stage stage = (Stage) tfNom.getScene().getWindow();
            
            // 3. Changer la scène
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - " + title);
            
        } catch (IOException e) {
            showError("Erreur de navigation: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODES UTILITAIRES
    // ═══════════════════════════════════════════════════════════════
    
    private void clearFields() {
        tfNom.clear();
        tfSuperficie.clear();
        tfLocalisation.clear();
        cbEtat.setValue(null);
    }

    private void clearMessages() {
        lblError.setText("");
        lblSuccess.setText("");
    }

    private void showError(String message) {
        lblError.setText(message);
        lblSuccess.setText("");
    }

    private void showSuccess(String message) {
        lblSuccess.setText(message);
        lblError.setText("");
    }
}
```

### 🔑 Points Clés - Contrôleur :

1. **@FXML** : Lie les éléments du fichier FXML au code Java
2. **Initializable** : Méthode `initialize()` appelée automatiquement
3. **ActionEvent** : Paramètre des méthodes liées aux boutons
4. **Navigation** : Changement de scène via FXMLLoader

---

## 6️⃣ ConsulterParcelleController.java - Liste avec Recherche

### Points Clés :

```java
// ═══════════════════════════════════════════════════════════════
// RECHERCHE EN TEMPS RÉEL avec FilteredList
// ═══════════════════════════════════════════════════════════════

private FilteredList<Parcelle> filteredParcelles;

// Dans initialize() :
tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> {
    filteredParcelles.setPredicate(parcelle -> {
        // Si champ vide → afficher tout
        if (newVal == null || newVal.trim().isEmpty()) {
            return true;
        }
        
        String filter = newVal.toLowerCase().trim();
        
        // Recherche dans nom, localisation, état
        if (parcelle.getNom().toLowerCase().contains(filter)) return true;
        if (parcelle.getLocalisation().toLowerCase().contains(filter)) return true;
        if (parcelle.getEtat().toLowerCase().contains(filter)) return true;
        
        return false;
    });
});
```

### 🔑 Points Clés :
- **FilteredList** : Liste qui se filtre dynamiquement
- **Predicate** : Condition de filtrage (true = afficher)
- **Listener** : Écoute les changements du champ de recherche

---

## 7️⃣ mainFX.java - Point d'Entrée de l'Application

### 📍 Localisation : `test/mainFX.java`

```java
package org.example.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainFX extends Application {

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE start() - Point d'entrée JavaFX
    // ═══════════════════════════════════════════════════════════════
    
    @Override
    public void start(Stage stage) throws Exception {
        // 1. Charger le fichier FXML
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/ajouterparcelle.fxml")
        );
        // → getResource() cherche dans src/main/resources/
        
        // 2. Créer l'arbre de composants
        Parent root = loader.load();
        // → Le contrôleur est automatiquement instancié

        // 3. Créer la scène avec le root
        Scene scene = new Scene(root);

        // 4. Configurer la fenêtre
        stage.setTitle("Smart Farm - Ajouter une Parcelle");
        stage.setScene(scene);
        stage.setResizable(true);
        
        // 5. Afficher la fenêtre
        stage.show();
    }

    // ═══════════════════════════════════════════════════════════════
    // MÉTHODE main() - Point d'entrée Java
    // ═══════════════════════════════════════════════════════════════
    
    public static void main(String[] args) {
        launch(args);
        // → Appelle start() après initialisation JavaFX
    }
}
```

---

## 8️⃣ Fichier FXML - Interface Graphique

### 📍 Exemple : `ajouterparcelle.fxml`

```xml
<?xml version="1.0" encoding="UTF-8"?>

<!-- Imports JavaFX -->
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.layout.*?>

<!-- Conteneur principal -->
<AnchorPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="org.example.pidev.controllers.AjouterParcelleController"
            prefHeight="500.0" prefWidth="700.0" 
            style="-fx-background-color: #FFFFFF;">
    
    <!-- 
        fx:controller : Lie ce FXML au contrôleur Java
        Tous les éléments avec fx:id sont accessibles dans le contrôleur
    -->

    <!-- Barre de navigation verte -->
    <HBox spacing="10" 
          style="-fx-background-color: #2E7D32; -fx-padding: 10;">
        
        <Button text="📋 Liste Parcelles" 
                onAction="#navigateToConsulterParcelle"/>
        <!-- 
            onAction="#methode" : Appelle la méthode du contrôleur
            Le # indique une référence à une méthode
        -->
    </HBox>

    <!-- Formulaire -->
    <GridPane hgap="15" vgap="18" alignment="CENTER">
        
        <!-- Champ Nom -->
        <Label text="Nom :" GridPane.rowIndex="0" GridPane.columnIndex="0"/>
        <TextField fx:id="tfNom" 
                   promptText="Nom de la parcelle" 
                   GridPane.rowIndex="0" GridPane.columnIndex="1"/>
        <!--
            fx:id="tfNom" : Correspond à @FXML TextField tfNom dans le contrôleur
            promptText : Texte indicatif grisé
        -->
        
    </GridPane>

    <!-- Bouton Ajouter -->
    <Button text="✓ Ajouter" 
            onAction="#ajouterParcelle"
            style="-fx-background-color: #4CAF50; -fx-text-fill: white;"/>
    
</AnchorPane>
```

### 🔑 Points Clés - FXML :

| Élément | Description |
|---------|-------------|
| `fx:controller` | Classe Java qui gère cette interface |
| `fx:id` | Identifiant pour accéder à l'élément en Java |
| `onAction="#methode"` | Méthode appelée lors du clic |
| `style` | CSS inline pour le style |

---

## 📊 SCHÉMA DE LA BASE DE DONNÉES

```sql
-- Table UTILISATEUR
CREATE TABLE utilisateur (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,  -- Admin, Agriculteur, ResponsableStock
    statut BOOLEAN DEFAULT TRUE,
    date_creation DATE DEFAULT CURRENT_DATE
);

-- Table PARCELLE
CREATE TABLE parcelle (
    id_parcelle INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    superficie DOUBLE NOT NULL,
    localisation VARCHAR(150),
    etat VARCHAR(50),  -- active, repos, exploitée
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user)
);

-- Table CULTURE
CREATE TABLE culture (
    id_culture INT AUTO_INCREMENT PRIMARY KEY,
    type_culture VARCHAR(100) NOT NULL,
    date_plantation DATE,
    date_recolte_prevue DATE,
    etat_croissance VARCHAR(50),  -- germination, croissance, floraison, mature, récolté
    id_parcelle INT,
    FOREIGN KEY (id_parcelle) REFERENCES parcelle(id_parcelle)
);
```

---

## 🔄 FLUX D'EXÉCUTION

```
┌─────────────────┐
│   mainFX.java   │  ← Point d'entrée
│    launch()     │
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  FXML Loader    │  ← Charge l'interface
│ ajouterparcelle │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  AjouterParcelleController  │  ← Contrôleur initialisé
│      initialize()           │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────┐
│  Utilisateur    │  ← Remplit le formulaire
│  clique Ajouter │
└────────┬────────┘
         │
         ▼
┌─────────────────────────────┐
│  ajouterParcelle()          │  ← Méthode du contrôleur
│  - Récupère les valeurs     │
│  - Valide les champs        │
│  - Crée objet Parcelle      │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────────────────┐
│  ParcelleService.add()      │  ← Service CRUD
│  - Validation complète      │
│  - PreparedStatement        │
│  - INSERT INTO parcelle     │
└────────┬────────────────────┘
         │
         ▼
┌─────────────────┐
│    MySQL        │  ← Base de données
│  smart_farm     │
└─────────────────┘
```

---

## ✅ FONCTIONNALITÉS IMPLÉMENTÉES

| Fonctionnalité | Parcelle | Culture |
|----------------|:--------:|:-------:|
| Ajouter | ✅ | ✅ |
| Modifier | ✅ | ✅ |
| Supprimer | ✅ | ✅ |
| Lister | ✅ | ✅ |
| Rechercher | ✅ | ✅ |
| Navigation | ✅ | ✅ |
| Validation | ✅ | ✅ |

---

## 🛡️ SÉCURITÉ IMPLÉMENTÉE

1. **PreparedStatement** : Protection contre injection SQL
2. **Validation des entrées** : Longueur, format, caractères
3. **Caractères interdits** : `< > " ' % ; ( ) & +`
4. **Messages d'erreur** : Informatifs sans détails techniques

---

## 📝 COMMANDES GIT UTILISÉES

```bash
# Initialiser le dépôt
git init

# Ajouter les fichiers
git add -A

# Créer un commit
git commit -m "Message descriptif"

# Créer une branche
git checkout -b saif

# Pousser vers GitHub
git push -u origin saif
```

---

## 🎯 CONCLUSION

Ce projet **Smart Farm** démontre :

1. **Architecture MVC** propre et maintenable
2. **Pattern Singleton** pour la connexion BD
3. **Interface générique** pour la réutilisabilité
4. **Validation côté service** (pas dans l'interface)
5. **JavaFX moderne** avec FXML
6. **Navigation fluide** entre les pages
7. **Recherche en temps réel** avec FilteredList
8. **Sécurité** contre les injections SQL

---

*Rapport généré le 13 Février 2026*
*Projet : Smart Farm - Gestion Agricole*
*Étudiant : Saif Eddine Yaakoubi*

