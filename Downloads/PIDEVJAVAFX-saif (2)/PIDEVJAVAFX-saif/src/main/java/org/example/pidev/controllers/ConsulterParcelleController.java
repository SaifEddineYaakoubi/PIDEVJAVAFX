package org.example.pidev.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.io.FileOutputStream;
import java.io.IOException;
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

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

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

        // Désactiver les boutons Modifier et Supprimer par défaut
        if (btnModifier != null) btnModifier.setDisable(true);
        if (btnSupprimer != null) btnSupprimer.setDisable(true);

        // Activer/Désactiver les boutons selon la sélection
        tableViewParcelles.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            if (btnModifier != null) btnModifier.setDisable(!hasSelection);
            if (btnSupprimer != null) btnSupprimer.setDisable(!hasSelection);
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
        fileChooser.setTitle("Exporter la liste des parcelles en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("parcelles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        File file = fileChooser.showSaveDialog(tableViewParcelles.getScene().getWindow());
        if (file != null) {
            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Titre du document
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(46, 125, 50));
                Paragraph title = new Paragraph("🌱 Liste des Parcelles", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                // Date d'export
                Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
                Paragraph date = new Paragraph("Exporté le: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), dateFont);
                date.setAlignment(Element.ALIGN_CENTER);
                date.setSpacingAfter(20);
                document.add(date);

                // Statistiques
                Font statsFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                double superficieTotale = parcellesList.stream().mapToDouble(Parcelle::getSuperficie).sum();
                Paragraph stats = new Paragraph(String.format("Total: %d parcelles | Superficie totale: %.2f m²", filteredParcelles.size(), superficieTotale), statsFont);
                stats.setAlignment(Element.ALIGN_CENTER);
                stats.setSpacingAfter(15);
                document.add(stats);

                // Tableau
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2.5f, 1.5f, 3f, 1.5f});

                // En-têtes
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(46, 125, 50);

                String[] headers = {"Nom", "Superficie (m²)", "Localisation", "État"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    table.addCell(cell);
                }

                // Données
                Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
                boolean alternate = false;
                for (Parcelle p : filteredParcelles) {
                    BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;

                    PdfPCell cellNom = new PdfPCell(new Phrase(p.getNom(), dataFont));
                    cellNom.setBackgroundColor(rowColor);
                    cellNom.setPadding(6);
                    table.addCell(cellNom);

                    PdfPCell cellSup = new PdfPCell(new Phrase(String.format("%.2f", p.getSuperficie()), dataFont));
                    cellSup.setBackgroundColor(rowColor);
                    cellSup.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellSup.setPadding(6);
                    table.addCell(cellSup);

                    PdfPCell cellLoc = new PdfPCell(new Phrase(p.getLocalisation(), dataFont));
                    cellLoc.setBackgroundColor(rowColor);
                    cellLoc.setPadding(6);
                    table.addCell(cellLoc);

                    PdfPCell cellEtat = new PdfPCell(new Phrase(p.getEtat(), dataFont));
                    cellEtat.setBackgroundColor(rowColor);
                    cellEtat.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellEtat.setPadding(6);
                    table.addCell(cellEtat);

                    alternate = !alternate;
                }

                document.add(table);

                // Pied de page
                Paragraph footer = new Paragraph("\n© Smart Farm - Gestion Agricole Intelligente", dateFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);

                document.close();
                showMessage("✅ PDF exporté: " + file.getName(), "#2E7D32");

            } catch (Exception e) {
                showMessage("❌ Erreur d'export PDF: " + e.getMessage(), "#C62828");
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

            // Ouvrir dans une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableViewParcelles.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier la Parcelle");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            // Attendre la fermeture et recharger les données
            popupStage.showAndWait();
            loadData();

        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "#C62828");
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
            try {
                boolean success = parcelleService.delete(selected.getIdParcelle());
                if (success) {
                    parcellesList.remove(selected);
                    updateStatistics();
                    showMessage("✅ Parcelle supprimée avec succès !", "#2E7D32");
                } else {
                    showMessage("❌ Erreur lors de la suppression de la parcelle.", "#C62828");
                }
            } catch (RuntimeException e) {
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.contains("foreign key constraint")) {
                    // Afficher une alerte d'erreur explicative
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Suppression impossible");
                    errorAlert.setHeaderText("❌ Cette parcelle ne peut pas être supprimée");
                    errorAlert.setContentText(
                        "La parcelle \"" + selected.getNom() + "\" contient des cultures associées.\n\n" +
                        "Pour supprimer cette parcelle, vous devez d'abord :\n" +
                        "1. Aller dans la liste des cultures\n" +
                        "2. Supprimer toutes les cultures de cette parcelle\n" +
                        "3. Revenir supprimer la parcelle\n\n" +
                        "💡 Astuce : Utilisez le filtre par parcelle dans la liste des cultures."
                    );
                    errorAlert.showAndWait();
                    showMessage("⚠️ Suppression annulée - Cultures associées existantes", "#FF9800");
                } else {
                    showMessage("❌ Erreur: " + errorMessage, "#C62828");
                }
            }
        }
    }

    // ==================== NAVIGATION ====================

    @FXML
    void navigateToAjouterParcelle(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterparcelle.fxml"));
            Parent root = loader.load();

            // Ouvrir dans une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableViewParcelles.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter une Parcelle");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            // Attendre la fermeture et recharger les données
            popupStage.showAndWait();
            loadData();

        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void navigateToConsulterCulture(ActionEvent event) {
        navigateTo("/consulterculture.fxml", "Liste des Cultures");
    }


    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewParcelles.getScene().getWindow();

            // Obtenir les dimensions de l'écran
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();

            // Créer une nouvelle scène avec les dimensions de l'écran
            Scene newScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - " + title);

            // Positionner et maximiser
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setMaximized(true);
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
