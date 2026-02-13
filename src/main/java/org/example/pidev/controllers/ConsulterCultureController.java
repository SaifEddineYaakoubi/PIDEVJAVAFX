package org.example.pidev.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterCultureController implements Initializable {

    @FXML
    private TableView<Culture> tableViewCultures;

    @FXML
    private TableColumn<Culture, String> colType;

    @FXML
    private TableColumn<Culture, LocalDate> colDatePlantation;

    @FXML
    private TableColumn<Culture, LocalDate> colDateRecolte;

    @FXML
    private TableColumn<Culture, String> colJoursRestants;

    @FXML
    private TableColumn<Culture, String> colEtat;

    @FXML
    private TableColumn<Culture, String> colParcelle;

    @FXML
    private TextField tfRecherche;

    @FXML
    private ComboBox<String> cbFiltreEtat;

    @FXML
    private ComboBox<String> cbFiltreParcelle;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblResultats;

    @FXML
    private Label lblDateTime;

    // Labels pour les statistiques
    @FXML
    private Label lblTotalCultures;

    @FXML
    private Label lblGermination;

    @FXML
    private Label lblCroissance;

    @FXML
    private Label lblFloraison;

    @FXML
    private Label lblMaturite;

    @FXML
    private Label lblRecolteProche;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private ObservableList<Culture> culturesList;
    private FilteredList<Culture> filteredCultures;
    private Map<Integer, String> parcellesMap;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();

        // Charger les parcelles dans une map pour récupérer les noms
        loadParcellesMap();

        // Configurer les colonnes
        colType.setCellValueFactory(new PropertyValueFactory<>("typeCulture"));
        colDatePlantation.setCellValueFactory(new PropertyValueFactory<>("datePlantation"));
        colDateRecolte.setCellValueFactory(new PropertyValueFactory<>("dateRecoltePrevue"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etatCroissance"));
        colParcelle.setCellValueFactory(new PropertyValueFactory<>("nomParcelle"));

        // Colonne jours restants personnalisée
        colJoursRestants.setCellFactory(column -> new TableCell<Culture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else {
                    Culture culture = getTableView().getItems().get(getIndex());
                    if (culture.getDateRecoltePrevue() != null) {
                        long jours = ChronoUnit.DAYS.between(LocalDate.now(), culture.getDateRecoltePrevue());
                        if (jours < 0) {
                            setText("Dépassée");
                            setStyle("-fx-background-color: #FFCDD2; -fx-text-fill: #C62828; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else if (jours <= 7) {
                            setText(jours + " j ⚠️");
                            setStyle("-fx-background-color: #FFE0B2; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else if (jours <= 30) {
                            setText(jours + " j");
                            setStyle("-fx-background-color: #FFF9C4; -fx-text-fill: #F9A825; -fx-font-weight: bold; -fx-alignment: CENTER;");
                        } else {
                            setText(jours + " j");
                            setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-alignment: CENTER;");
                        }
                    } else {
                        setText("N/A");
                        setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Style pour la colonne état
        colEtat.setCellFactory(column -> new TableCell<Culture, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "germination":
                            setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "croissance":
                            setStyle("-fx-background-color: #BBDEFB; -fx-text-fill: #1565C0; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "floraison":
                            setStyle("-fx-background-color: #F8BBD9; -fx-text-fill: #AD1457; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "maturité":
                            setStyle("-fx-background-color: #FFE0B2; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        default:
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Initialiser les filtres ComboBox
        cbFiltreEtat.setItems(FXCollections.observableArrayList("Tous", "Germination", "Croissance", "Floraison", "Maturité"));
        cbFiltreEtat.setValue("Tous");
        cbFiltreEtat.setOnAction(e -> applyFilters());

        // Charger les données
        loadData();

        // Configurer la recherche en temps réel
        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Démarrer l'horloge
        startClock();

        // Double-clic pour modifier
        tableViewCultures.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewCultures.getSelectionModel().getSelectedItem() != null) {
                modifierCulture(null);
            }
        });
    }

    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            lblDateTime.setText("📅 " + LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    private void loadParcellesMap() {
        parcellesMap = new HashMap<>();
        ObservableList<String> parcelleNames = FXCollections.observableArrayList("Toutes");
        for (Parcelle p : parcelleService.getAll()) {
            parcellesMap.put(p.getIdParcelle(), p.getNom());
            parcelleNames.add(p.getNom());
        }
        cbFiltreParcelle.setItems(parcelleNames);
        cbFiltreParcelle.setValue("Toutes");
        cbFiltreParcelle.setOnAction(e -> applyFilters());
    }

    private void loadData() {
        culturesList = FXCollections.observableArrayList(cultureService.getAll());

        // Associer le nom de parcelle à chaque culture
        for (Culture c : culturesList) {
            String nomParcelle = parcellesMap.get(c.getIdParcelle());
            c.setNomParcelle(nomParcelle != null ? nomParcelle : "Parcelle #" + c.getIdParcelle());
        }

        filteredCultures = new FilteredList<>(culturesList, c -> true);
        tableViewCultures.setItems(filteredCultures);

        // Mettre à jour les statistiques
        updateStatistics();
    }

    private void updateStatistics() {
        int total = culturesList.size();
        long germination = culturesList.stream().filter(c -> "germination".equalsIgnoreCase(c.getEtatCroissance())).count();
        long croissance = culturesList.stream().filter(c -> "croissance".equalsIgnoreCase(c.getEtatCroissance())).count();
        long floraison = culturesList.stream().filter(c -> "floraison".equalsIgnoreCase(c.getEtatCroissance())).count();
        long maturite = culturesList.stream().filter(c -> "maturité".equalsIgnoreCase(c.getEtatCroissance())).count();

        // Compter les cultures avec récolte proche (<=7 jours)
        long recolteProche = culturesList.stream()
                .filter(c -> c.getDateRecoltePrevue() != null)
                .filter(c -> {
                    long jours = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue());
                    return jours >= 0 && jours <= 7;
                })
                .count();

        lblTotalCultures.setText(String.valueOf(total));
        lblGermination.setText(String.valueOf(germination));
        lblCroissance.setText(String.valueOf(croissance));
        lblFloraison.setText(String.valueOf(floraison));
        lblMaturite.setText(String.valueOf(maturite));
        lblRecolteProche.setText(String.valueOf(recolteProche));

        updateResultsLabel();
        showMessage("✅ Liste mise à jour - " + total + " culture(s)", "#2E7D32");
    }

    private void applyFilters() {
        String searchText = tfRecherche.getText();
        String filterEtat = cbFiltreEtat.getValue();
        String filterParcelle = cbFiltreParcelle.getValue();

        filteredCultures.setPredicate(culture -> {
            boolean matchesSearch = true;
            boolean matchesEtat = true;
            boolean matchesParcelle = true;

            // Filtre par recherche
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase().trim();
                matchesSearch = culture.getTypeCulture().toLowerCase().contains(lowerCaseFilter) ||
                        culture.getEtatCroissance().toLowerCase().contains(lowerCaseFilter) ||
                        (culture.getNomParcelle() != null && culture.getNomParcelle().toLowerCase().contains(lowerCaseFilter));
            }

            // Filtre par état
            if (filterEtat != null && !"Tous".equals(filterEtat)) {
                matchesEtat = culture.getEtatCroissance().equalsIgnoreCase(filterEtat);
            }

            // Filtre par parcelle
            if (filterParcelle != null && !"Toutes".equals(filterParcelle)) {
                matchesParcelle = filterParcelle.equals(culture.getNomParcelle());
            }

            return matchesSearch && matchesEtat && matchesParcelle;
        });

        updateResultsLabel();
    }

    private void updateResultsLabel() {
        lblResultats.setText("Affichage: " + filteredCultures.size() + " sur " + culturesList.size());
    }

    @FXML
    void rafraichirListe(ActionEvent event) {
        tfRecherche.clear();
        cbFiltreEtat.setValue("Tous");
        cbFiltreParcelle.setValue("Toutes");
        loadParcellesMap();
        loadData();
    }

    @FXML
    void exporterListe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des cultures");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
        fileChooser.setInitialFileName("cultures_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        File file = fileChooser.showSaveDialog(tableViewCultures.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // En-tête
                writer.println("Type;Date Plantation;Date Récolte Prévue;État;Parcelle");

                // Données
                for (Culture c : filteredCultures) {
                    writer.println(String.format("%s;%s;%s;%s;%s",
                            c.getTypeCulture(),
                            c.getDatePlantation() != null ? c.getDatePlantation().toString() : "",
                            c.getDateRecoltePrevue() != null ? c.getDateRecoltePrevue().toString() : "",
                            c.getEtatCroissance(),
                            c.getNomParcelle() != null ? c.getNomParcelle() : ""));
                }

                showMessage("✅ Export réussi: " + file.getName(), "#2E7D32");
            } catch (IOException e) {
                showMessage("❌ Erreur d'export: " + e.getMessage(), "#C62828");
            }
        }
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        Culture selected = tableViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une culture à modifier.", "#FF9800");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierculture.fxml"));
            Parent root = loader.load();

            // Passer la culture sélectionnée au contrôleur
            ModifierCultureController controller = loader.getController();
            controller.setCulture(selected);

            Stage stage = (Stage) tableViewCultures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - Modifier la Culture");
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void supprimerCulture(ActionEvent event) {
        Culture selected = tableViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une culture à supprimer.", "#FF9800");
            return;
        }

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la culture");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la culture \"" + selected.getTypeCulture() + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            cultureService.delete(selected.getIdCulture());
            culturesList.remove(selected);
            updateStatistics();
            showMessage("✅ Culture supprimée avec succès !", "#2E7D32");
        }
    }

    // ==================== NAVIGATION ====================

    @FXML
    void navigateToConsulterParcelle(ActionEvent event) {
        navigateTo("/consulterparcelle.fxml", "Liste des Parcelles");
    }

    @FXML
    void navigateToAjouterParcelle(ActionEvent event) {
        navigateTo("/ajouterparcelle.fxml", "Ajouter une Parcelle");
    }

    @FXML
    void navigateToAjouterCulture(ActionEvent event) {
        navigateTo("/ajouterculture.fxml", "Ajouter une Culture");
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewCultures.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - " + title);
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    // ==================== HELPERS ====================

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
    }
}
