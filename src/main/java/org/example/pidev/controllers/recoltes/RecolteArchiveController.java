package org.example.pidev.controllers.recoltes;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.pidev.models.RecolteArchive;
import org.example.pidev.services.recoltes.RecolteArchiveService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Contrôleur pour afficher et gérer l'archive des récoltes supprimées
 */
public class RecolteArchiveController {

    @FXML
    private TableView<RecolteArchive> archiveTable;
    @FXML
    private TableColumn<RecolteArchive, Integer> colIdArchive;
    @FXML
    private TableColumn<RecolteArchive, Integer> colIdOriginal;
    @FXML
    private TableColumn<RecolteArchive, Double> colQuantite;
    @FXML
    private TableColumn<RecolteArchive, LocalDate> colDateRecolte;
    @FXML
    private TableColumn<RecolteArchive, String> colQualite;
    @FXML
    private TableColumn<RecolteArchive, String> colTypeCulture;
    @FXML
    private TableColumn<RecolteArchive, String> colLocalisation;
    @FXML
    private TableColumn<RecolteArchive, String> colCauseSupression;
    @FXML
    private TableColumn<RecolteArchive, LocalDate> colDateArchivage;

    @FXML
    private Label lblTotalArchives;
    @FXML
    private Label lblTotalQuantite;

    @FXML
    private TextField txtFilterCulture;
    @FXML
    private TextField txtFilterCause;
    @FXML
    private Button btnRefresh;
    @FXML
    private Button btnDeleteArchive;
    @FXML
    private Button btnClearAll;

    private RecolteArchiveService archiveService;
    private ObservableList<RecolteArchive> archiveList;

    @FXML
    public void initialize() {
        archiveService = new RecolteArchiveService();
        setupTable();
        loadArchives();
        setupButtonHandlers();
    }

    /**
     * Configure les colonnes du tableau
     */
    private void setupTable() {
        colIdArchive.setCellValueFactory(new PropertyValueFactory<>("idArchive"));
        colIdOriginal.setCellValueFactory(new PropertyValueFactory<>("idRecolteOriginal"));
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDateRecolte.setCellValueFactory(new PropertyValueFactory<>("dateRecolte"));
        colQualite.setCellValueFactory(new PropertyValueFactory<>("qualite"));
        colTypeCulture.setCellValueFactory(new PropertyValueFactory<>("typeCulture"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colCauseSupression.setCellValueFactory(new PropertyValueFactory<>("causeSupression"));
        colDateArchivage.setCellValueFactory(new PropertyValueFactory<>("dateArchivage"));

        // Configurer la largeur des colonnes
        archiveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Charger les archives depuis la base de données
     */
    private void loadArchives() {
        List<RecolteArchive> archives = archiveService.getAllArchives();
        archiveList = FXCollections.observableArrayList(archives);
        archiveTable.setItems(archiveList);
        updateStatistics();
    }

    /**
     * Mettre à jour les statistiques
     */
    private void updateStatistics() {
        int total = archiveService.countArchives();
        lblTotalArchives.setText("Total archivé: " + total + " récoltes");

        // Calculer quantité totale
        double totalQuantite = 0;
        for (RecolteArchive archive : archiveList) {
            totalQuantite += archive.getQuantite();
        }
        lblTotalQuantite.setText(String.format("Quantité totale: %.2f kg", totalQuantite));
    }

    /**
     * Configurer les gestionnaires de boutons
     */
    private void setupButtonHandlers() {
        btnRefresh.setOnAction(e -> loadArchives());

        btnDeleteArchive.setOnAction(e -> deleteSelectedArchive());

        btnClearAll.setOnAction(e -> clearAllArchives());

        // Filtrage en temps réel
        txtFilterCulture.textProperty().addListener((obs, oldVal, newVal) -> filterArchives());
        txtFilterCause.textProperty().addListener((obs, oldVal, newVal) -> filterArchives());
    }

    /**
     * Filtrer les archives par culture et cause
     */
    private void filterArchives() {
        String culture = txtFilterCulture.getText().toLowerCase();
        String cause = txtFilterCause.getText().toLowerCase();

        List<RecolteArchive> filtered = archiveList.stream()
                .filter(archive -> archive.getTypeCulture().toLowerCase().contains(culture))
                .filter(archive -> archive.getCauseSupression().toLowerCase().contains(cause))
                .toList();

        archiveTable.setItems(FXCollections.observableArrayList(filtered));
    }

    /**
     * Supprimer l'archive sélectionnée
     */
    private void deleteSelectedArchive() {
        RecolteArchive selected = archiveTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("⚠️ Aucune sélection", "Veuillez sélectionner une archive à supprimer", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation");
        confirmation.setHeaderText("Supprimer définitivement cette archive?");
        confirmation.setContentText("Culture: " + selected.getTypeCulture() + "\n" +
                "Cause: " + selected.getCauseSupression());

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (archiveService.deleteArchive(selected.getIdArchive())) {
                showAlert("✅ Succès", "Archive supprimée définitivement", Alert.AlertType.INFORMATION);
                loadArchives();
            } else {
                showAlert("❌ Erreur", "Impossible de supprimer l'archive", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Vider complètement l'archive
     */
    private void clearAllArchives() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Attention");
        confirmation.setHeaderText("⚠️ Vider complètement l'archive?");
        confirmation.setContentText("Cette action est irréversible!\n" +
                "Toutes les " + archiveService.countArchives() + " archives seront supprimées définitivement.");

        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (archiveService.viderArchive()) {
                showAlert("✅ Succès", "Archive vidée complètement", Alert.AlertType.INFORMATION);
                loadArchives();
            } else {
                showAlert("❌ Erreur", "Impossible de vider l'archive", Alert.AlertType.ERROR);
            }
        }
    }

    /**
     * Afficher une alerte
     */
    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Exporter les archives en CSV
     */
    public void exportToCSV() {
        List<RecolteArchive> archives = archiveService.getAllArchives();
        if (archives.isEmpty()) {
            showAlert("⚠️ Archive vide", "Il n'y a aucune archive à exporter", Alert.AlertType.WARNING);
            return;
        }

        StringBuilder csv = new StringBuilder();
        csv.append("ID,ID_Original,Quantite,Date_Recolte,Qualite,Type_Culture,Localisation,Cause_Supression,Date_Archivage\n");

        for (RecolteArchive archive : archives) {
            csv.append(archive.getIdArchive()).append(",")
                    .append(archive.getIdRecolteOriginal()).append(",")
                    .append(archive.getQuantite()).append(",")
                    .append(archive.getDateRecolte()).append(",")
                    .append(archive.getQualite()).append(",")
                    .append(archive.getTypeCulture()).append(",")
                    .append(archive.getLocalisation()).append(",")
                    .append(archive.getCauseSupression()).append(",")
                    .append(archive.getDateArchivage()).append("\n");
        }

        System.out.println(csv);
        showAlert("✅ Export", "Données exportées en CSV", Alert.AlertType.INFORMATION);
    }
}

