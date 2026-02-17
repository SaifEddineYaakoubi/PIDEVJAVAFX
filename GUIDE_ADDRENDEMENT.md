# 📋 Guide d'Intégration - AddRendement.fxml

## ✨ Fichiers Créés

### 1. **AddRendement.fxml** (Interface)
- **Localisation**: `src/main/resources/AddRendement.fxml`
- **Fonctionnalités**:
  - Sélection de récolte via ComboBox
  - Saisie de la surface exploitée
  - Saisie de la quantité totale
  - Calcul automatique de la productivité
  - Affichage du résumé des résultats
  - Boutons Enregistrer, Réinitialiser, Fermer

### 2. **AddRendementController.java** (Contrôleur)
- **Localisation**: `src/main/java/org/example/pidev/controllers/AddRendementController.java`
- **Fonctionnalités**:
  - Charge les récoltes disponibles
  - Calcule automatiquement la productivité
  - Valide les données du formulaire
  - Enregistre le rendement en base de données
  - Gère les messages d'erreur et succès

---

## 🔧 Intégration dans votre Contrôleur Principal

### Étape 1: Ajouter la méthode d'ouverture

Ajoutez cette méthode dans votre contrôleur principal (ex: `RendementController.java`):

```java
/**
 * Ouvre la fenêtre d'ajout de rendement
 */
@FXML
private void onAjouterRendement() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendement.fxml"));
        Parent root = loader.load();
        
        AddRendementController controller = loader.getController();
        
        Stage stage = new Stage();
        stage.setTitle("Ajouter un Rendement");
        stage.setScene(new Scene(root, 800, 900));
        stage.initModality(Modality.APPLICATION_MODAL);
        
        // Définir le stage pour le contrôleur
        controller.setStage(stage);
        
        stage.showAndWait();
        
        // Rafraîchir la table après la fermeture
        refreshRendementTable();
        
    } catch (IOException e) {
        System.out.println("❌ Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout");
    }
}

/**
 * Rafraîchit la table des rendements
 */
private void refreshRendementTable() {
    // Charger les rendements et mettre à jour la table
    try {
        RendementService service = new RendementService();
        List<Rendement> rendements = service.getAll();
        // tableRendement.setItems(FXCollections.observableArrayList(rendements));
    } catch (Exception e) {
        System.out.println("❌ Erreur lors du rafraîchissement: " + e.getMessage());
    }
}

/**
 * Affiche une boîte de dialogue d'erreur
 */
private void showError(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
}
```

### Étape 2: Connecter le bouton dans le FXML

Dans votre fichier FXML principal (ex: `Rendement.fxml`):

```xml
<Button text="➕ Ajouter Rendement"
        fx:id="btnAjouterRendement"
        styleClass="button-primary"
        onAction="#onAjouterRendement"/>
```

### Étape 3: Ajouter les imports nécessaires

```java
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.controllers.AddRendementController;
import java.io.IOException;
```

---

## 📋 Structure du Formulaire

### Sections du Formulaire

1. **Navbar**
   - Titre: "📊 Smart Farm - Rendements"
   - Bouton Retour

2. **Sélection Récolte**
   - ComboBox avec les récoltes disponibles
   - Bouton Rafraîchir
   - Affichage des informations de la récolte sélectionnée

3. **Saisie des Données**
   - Surface Exploitée (0.01 - 10000 ha)
   - Quantité Totale (0 - 1,000,000 Kg)
   - Productivité (calcul automatique ou manuel)

4. **Résumé des Résultats**
   - Affichage formaté de:
     - Surface en ha
     - Quantité en Kg
     - Productivité en Kg/ha

5. **Boutons d'Action**
   - ✓ Enregistrer
   - 🔄 Réinitialiser
   - ✕ Fermer

6. **Barre d'État**
   - Messages de statut en temps réel

---

## 🎯 Fonctionnalités Principales

### 1. Calcul Automatique de Productivité

```
Productivité = Quantité Totale ÷ Surface Exploitée
Exemple: 500 Kg ÷ 10 ha = 50 Kg/ha
```

**Désactiver le calcul automatique**:
- Décocher la case "Calculer automatiquement"
- Entrer manuellement la productivité

### 2. Validation des Données

Le formulaire valide:
- ✅ Surface: 0.01 - 10000 ha
- ✅ Quantité: 0 - 1,000,000 Kg
- ✅ Productivité: 0 - 1,000,000 Kg/ha
- ✅ Récolte: Sélection obligatoire

### 3. Messages d'État

- **✓ Vert**: Opération réussie
- **⚠️ Orange**: Avertissement
- **❌ Rouge**: Erreur

---

## 💡 Exemple Complet d'Utilisation

```java
// Dans RendementController.java

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.controllers.AddRendementController;
import org.example.pidev.models.Rendement;
import org.example.pidev.services.RendementService;
import javafx.collections.FXCollections;

public class RendementController {
    
    @FXML
    private Button btnAjouterRendement;
    
    @FXML
    public void initialize() {
        btnAjouterRendement.setOnAction(e -> onAjouterRendement());
    }
    
    @FXML
    private void onAjouterRendement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendement.fxml"));
            Parent root = loader.load();
            
            AddRendementController controller = loader.getController();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Rendement");
            stage.setScene(new Scene(root, 800, 900));
            stage.initModality(Modality.APPLICATION_MODAL);
            
            controller.setStage(stage);
            stage.showAndWait();
            
            refreshRendementTable();
            
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }
    
    private void refreshRendementTable() {
        // Rafraîchir votre table ici
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

---

## 🎨 Personnalisation

### Changer la taille de la fenêtre

```java
stage.setScene(new Scene(root, 900, 1000)); // Largeur x Hauteur
```

### Changer le titre de la fenêtre

```java
stage.setTitle("Votre titre personnalisé");
```

### Ajouter une icône

```java
stage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
```

### Désactiver le redimensionnement

```java
stage.setResizable(false);
```

---

## ✅ Checklist d'Intégration

- [ ] Fichier `AddRendement.fxml` créé
- [ ] Fichier `AddRendementController.java` créé
- [ ] Méthode `onAjouterRendement()` ajoutée au contrôleur principal
- [ ] Bouton "Ajouter Rendement" connecté à la méthode
- [ ] Imports ajoutés au contrôleur
- [ ] Méthode `refreshRendementTable()` implémentée
- [ ] Compilation sans erreurs
- [ ] Test du formulaire

---

## 🐛 Dépannage

### Erreur: "AddRendement.fxml not found"
✅ Vérifier que le fichier est dans `src/main/resources/`

### Erreur: "AddRendementController not found"
✅ Vérifier le package et le nom de la classe
✅ Vérifier que le FXML pointe vers le bon contrôleur

### ComboBox vide
✅ Vérifier qu'il y a des récoltes en base de données
✅ Cliquer sur le bouton "Rafraîchir"

### Erreur lors de l'enregistrement
✅ Vérifier les messages d'erreur dans la console
✅ Vérifier la connexion à la base de données

---

## 📚 Ressources

- `AddRendement.fxml` - Interface FXML
- `AddRendementController.java` - Contrôleur JavaFX
- `RendementService.java` - Service de gestion des rendements
- `smartfarm.css` - Feuille de style

---

**Créé le**: 16 Février 2026
**Status**: ✅ Prêt à l'emploi

