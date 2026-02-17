# 🎨 Guide d'Utilisation du CSS Amélioré

## ✨ Améliorations Apportées

### 1. Sidebar Dynamique

Le sidebar change automatiquement de couleur selon la section active:

#### Vert pour Recolte
```css
.sidebar-recolte {
    -fx-background-color: linear-gradient(to bottom, #2e7d32, #4caf50);
}
```

#### Marron pour Rendement
```css
.sidebar-rendement {
    -fx-background-color: linear-gradient(to bottom, #6d4c41, #8d6e63);
}
```

### 2. Conteneurs de Totaux

Deux nouveaux conteneurs pour afficher:
- **Total de Recoltes** (vert)
- **Total de Rendements** (marron)

---

## 📋 Utilisation en JavaFX

### Structure HTML/FXML Recommandée

```xml
<!-- SIDEBAR RECOLTE -->
<VBox styleClass="sidebar,sidebar-recolte">
    <Label styleClass="sidebar-title" text="RECOLTE"/>
    <Button styleClass="sidebar-button,sidebar-button-active" text="Ajouter"/>
    <!-- autres boutons -->
</VBox>

<!-- SIDEBAR RENDEMENT -->
<VBox styleClass="sidebar,sidebar-rendement">
    <Label styleClass="sidebar-title" text="RENDEMENT"/>
    <Button styleClass="sidebar-button,sidebar-button-active" text="Ajouter"/>
    <!-- autres boutons -->
</VBox>
```

### Conteneurs de Totaux

```xml
<!-- CONTENEUR RECOLTE -->
<VBox styleClass="total-container,total-container-recolte">
    <HBox styleClass="total-header">
        <Label styleClass="total-icon" text="🌾"/>
        <Label styleClass="total-label" text="Total Récoltes"/>
    </HBox>
    <Label styleClass="total-value" text="42"/>
    <Label styleClass="total-subtitle" text="récoltes enregistrées"/>
</VBox>

<!-- CONTENEUR RENDEMENT -->
<VBox styleClass="total-container,total-container-rendement">
    <HBox styleClass="total-header">
        <Label styleClass="total-icon" text="📊"/>
        <Label styleClass="total-label" text="Total Rendements"/>
    </HBox>
    <Label styleClass="total-value" text="28"/>
    <Label styleClass="total-subtitle" text="rendements enregistrés"/>
</VBox>
```

---

## 🎯 Classes CSS Disponibles

### Sidebar
- `.sidebar` - Conteneur principal
- `.sidebar-recolte` - Style vert pour Recolte
- `.sidebar-rendement` - Style marron pour Rendement
- `.sidebar-title` - Titre du sidebar
- `.sidebar-button` - Bouton standard
- `.sidebar-button-active` - Bouton actif
- `.sidebar-button:hover` - État hover

### Conteneurs de Totaux
- `.total-container` - Conteneur de base
- `.total-container-recolte` - Conteneur vert
- `.total-container-rendement` - Conteneur marron
- `.total-header` - En-tête avec icône
- `.total-label` - Libellé
- `.total-value` - Grande valeur numérique
- `.total-subtitle` - Sous-texte
- `.total-icon` - Icône
- `.totals-container` - Conteneur parent

---

## 🎨 Palette de Couleurs

### Vert (Recolte)
```
- Vert foncé: #2e7d32
- Vert clair: #4caf50
- Vert très clair: #c8e6c9
- Vert très très clair: #e8f5e9
```

### Marron (Rendement)
```
- Marron clair: #8d6e63
- Marron moyen: #6d4c41
- Marron foncé: #4e342e
- Marron header: #d7ccc8
```

---

## 💡 Exemple Complet d'Utilisation

```java
// En JavaFX/FXML
VBox sidebarRecoltee = new VBox();
sidebarRecoltee.getStyleClass().addAll("sidebar", "sidebar-recolte");

// Changement dynamique du sidebar
if (isRecolteMode) {
    sidebarRecolte.getStyleClass().clear();
    sidebarRecolte.getStyleClass().addAll("sidebar", "sidebar-recolte");
} else {
    sidebarRecolte.getStyleClass().clear();
    sidebarRecolte.getStyleClass().addAll("sidebar", "sidebar-rendement");
}

// Conteneur de totaux
VBox totalContainer = new VBox();
totalContainer.getStyleClass().addAll("total-container", "total-container-recolte");

Label totalValue = new Label("42");
totalValue.getStyleClass().add("total-value");

totalContainer.getChildren().add(totalValue);
```

---

## ✅ Checklist de Mise en Œuvre

- [ ] Appliquer `sidebar-recolte` ou `sidebar-rendement` au sidebar
- [ ] Ajouter les conteneurs avec `total-container-recolte`
- [ ] Ajouter les conteneurs avec `total-container-rendement`
- [ ] Utiliser les couleurs appropriées
- [ ] Tester les transitions de couleur
- [ ] Vérifier l'affichage des icônes
- [ ] Tester le hover effect
- [ ] Vérifier le responsive

---

## 🔍 Résolution des Problèmes

### Le sidebar ne change pas de couleur?
✅ Vérifier que la classe CSS est appliquée correctement
✅ Utiliser `getStyleClass().clear()` avant d'appliquer la nouvelle classe

### Les conteneurs ne s'affichent pas?
✅ Vérifier que le conteneur parent a une taille définie
✅ Utiliser `setMinWidth()` ou `setPrefWidth()`
✅ Ajouter du padding au parent

### Les transitions ne sont pas fluides?
✅ Les transitions CSS JavaFX ont des limitations
✅ Utiliser `Timeline` ou `FadeTransition` pour plus de contrôle

---

## 📚 Ressources

- [JavaFX CSS Reference](https://docs.oracle.com/javase/8/javafx/api/javafx/scene/doc-files/cssref.html)
- [JavaFX Styling with CSS](https://docs.oracle.com/javase/8/javafx/get-started-tutorial/css.htm)
- [Color Picker](https://coolors.co/)

---

**Créé le**: 16 Février 2026
**Status**: ✅ Guide complet

