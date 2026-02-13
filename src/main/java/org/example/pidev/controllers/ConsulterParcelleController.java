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
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.ParcelleService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterParcelleController implements Initializable {

    @FXML
    private TableView<Parcelle> tableViewParcelles;

    @FXML
    private TableColumn<Parcelle, String> colNom;

    @FXML
    private TableColumn<Parcelle, Double> colSuperficie;

    @FXML
    private TableColumn<Parcelle, String> colLocalisation;

    @FXML
    private TableColumn<Parcelle, String> colEtat;

    @FXML
    private TextField tfRecherche;

    @FXML
    private ComboBox<String> cbFiltre;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblResultats;

    @FXML
    private Label lblDateTime;

    // Labels pour les statistiques
    @FXML
    private Label lblTotalParcelles;

    @FXML
    private Label lblSuperficieTotale;

    @FXML
    private Label lblActives;

    @FXML
    private Label lblRepos;

    @FXML
    private Label lblExploitees;

    private ParcelleService parcelleService;
    private ObservableList<Parcelle> parcellesList;
    private FilteredList<Parcelle> filteredParcelles;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parcelleService = new ParcelleService();

        // Configurer les colonnes
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colSuperficie.setCellValueFactory(new PropertyValueFactory<>("superficie"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colEtat.setCellValueFactory(new PropertyValueFactory<>("etat"));

        // Appliquer style aux cellules d'état
        colEtat.setCellFactory(column -> new TableCell<Parcelle, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item.toLowerCase()) {
                        case "active":
                            setStyle("-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "repos":
                            setStyle("-fx-background-color: #FFE0B2; -fx-text-fill: #E65100; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        case "exploitée":
                            setStyle("-fx-background-color: #BBDEFB; -fx-text-fill: #1565C0; -fx-font-weight: bold; -fx-alignment: CENTER;");
                            break;
                        default:
                            setStyle("-fx-alignment: CENTER;");
                    }
                }
            }
        });

        // Initialiser le filtre ComboBox
        cbFiltre.setItems(FXCollections.observableArrayList("Tous", "Active", "Repos", "Exploitée"));
        cbFiltre.setValue("Tous");
        cbFiltre.setOnAction(e -> applyFilters());

        // Charger les données
        loadData();

        // Configurer la recherche en temps réel
        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Démarrer l'horloge
        startClock();

        // Double-clic pour modifier
        tableViewParcelles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tableViewParcelles.getSelectionModel().getSelectedItem() != null) {
                modifierParcelle(null);
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

    private void loadData() {
        parcellesList = FXCollections.observableArrayList(parcelleService.getAll());
        filteredParcelles = new FilteredList<>(parcellesList, p -> true);
        tableViewParcelles.setItems(filteredParcelles);

        // Mettre à jour les statistiques
        updateStatistics();
    }

    private void updateStatistics() {
        int total = parcellesList.size();
        double superficieTotale = parcellesList.stream().mapToDouble(Parcelle::getSuperficie).sum();
        long actives = parcellesList.stream().filter(p -> "active".equalsIgnoreCase(p.getEtat())).count();
        long repos = parcellesList.stream().filter(p -> "repos".equalsIgnoreCase(p.getEtat())).count();
        long exploitees = parcellesList.stream().filter(p -> "exploitée".equalsIgnoreCase(p.getEtat())).count();

        lblTotalParcelles.setText(String.valueOf(total));
        lblSuperficieTotale.setText(String.format("%.0f m²", superficieTotale));
        lblActives.setText(String.valueOf(actives));
        lblRepos.setText(String.valueOf(repos));
        lblExploitees.setText(String.valueOf(exploitees));

        updateResultsLabel();
        showMessage("✅ Liste mise à jour - " + total + " parcelle(s)", "#2E7D32");
    }

    private void applyFilters() {
        String searchText = tfRecherche.getText();
        String filterEtat = cbFiltre.getValue();

        filteredParcelles.setPredicate(parcelle -> {
            boolean matchesSearch = true;
            boolean matchesFilter = true;

            // Filtre par recherche
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase().trim();
                matchesSearch = parcelle.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        parcelle.getLocalisation().toLowerCase().contains(lowerCaseFilter) ||
                        parcelle.getEtat().toLowerCase().contains(lowerCaseFilter);
            }

            // Filtre par état
            if (filterEtat != null && !"Tous".equals(filterEtat)) {
                matchesFilter = parcelle.getEtat().equalsIgnoreCase(filterEtat);
            }

            return matchesSearch && matchesFilter;
        });

        updateResultsLabel();
    }

    private void updateResultsLabel() {
        lblResultats.setText("Affichage: " + filteredParcelles.size() + " sur " + parcellesList.size());
    }

    @FXML
    void rafraichirListe(ActionEvent event) {
        tfRecherche.clear();
        cbFiltre.setValue("Tous");
        loadData();
    }

    @FXML
    void exporterListe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des parcelles");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier CSV", "*.csv"));
        fileChooser.setInitialFileName("parcelles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");

        File file = fileChooser.showSaveDialog(tableViewParcelles.getScene().getWindow());
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                // En-tête
                writer.println("Nom;Superficie (m²);Localisation;État");

                // Données
                for (Parcelle p : filteredParcelles) {
                    writer.println(String.format("%s;%.2f;%s;%s",
                            p.getNom(), p.getSuperficie(), p.getLocalisation(), p.getEtat()));
                }

                showMessage("✅ Export réussi: " + file.getName(), "#2E7D32");
            } catch (IOException e) {
                showMessage("❌ Erreur d'export: " + e.getMessage(), "#C62828");
            }
        }
    }

    @FXML
    void modifierParcelle(ActionEvent event) {
        Parcelle selected = tableViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une parcelle à modifier.", "#FF9800");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierparcelle.fxml"));
            Parent root = loader.load();

            // Passer la parcelle sélectionnée au contrôleur
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(selected);

            Stage stage = (Stage) tableViewParcelles.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Smart Farm - Modifier la Parcelle");
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void supprimerParcelle(ActionEvent event) {
        Parcelle selected = tableViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showMessage("⚠️ Veuillez sélectionner une parcelle à supprimer.", "#FF9800");
            return;
        }

        // Confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la parcelle");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la parcelle \"" + selected.getNom() + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            parcelleService.delete(selected.getIdParcelle());
            parcellesList.remove(selected);
            updateStatistics();
            showMessage("✅ Parcelle supprimée avec succès !", "#2E7D32");
        }
    }

    // ==================== NAVIGATION ====================

    @FXML
    void navigateToAjouterParcelle(ActionEvent event) {
        navigateTo("/ajouterparcelle.fxml", "Ajouter une Parcelle");
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewParcelles.getScene().getWindow();
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
