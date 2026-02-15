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
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

    @FXML
    private Button btnModifier;

    @FXML
    private Button btnSupprimer;

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
                        case "mature":
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

        // Désactiver les boutons Modifier et Supprimer par défaut
        if (btnModifier != null) btnModifier.setDisable(true);
        if (btnSupprimer != null) btnSupprimer.setDisable(true);

        // Activer/Désactiver les boutons selon la sélection
        tableViewCultures.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
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
        long maturite = culturesList.stream().filter(c -> "mature".equalsIgnoreCase(c.getEtatCroissance())).count();

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
        fileChooser.setTitle("Exporter la liste des cultures en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("cultures_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        File file = fileChooser.showSaveDialog(tableViewCultures.getScene().getWindow());
        if (file != null) {
            try {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Titre du document
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(255, 111, 0));
                Paragraph title = new Paragraph("🌾 Liste des Cultures", titleFont);
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
                long germination = culturesList.stream().filter(c -> "germination".equalsIgnoreCase(c.getEtatCroissance())).count();
                long croissance = culturesList.stream().filter(c -> "croissance".equalsIgnoreCase(c.getEtatCroissance())).count();
                long floraison = culturesList.stream().filter(c -> "floraison".equalsIgnoreCase(c.getEtatCroissance())).count();
                long maturite = culturesList.stream().filter(c -> "mature".equalsIgnoreCase(c.getEtatCroissance())).count();

                Paragraph stats = new Paragraph(String.format("Total: %d cultures | Germination: %d | Croissance: %d | Floraison: %d | Maturité: %d",
                        filteredCultures.size(), germination, croissance, floraison, maturite), statsFont);
                stats.setAlignment(Element.ALIGN_CENTER);
                stats.setSpacingAfter(15);
                document.add(stats);

                // Tableau
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 1.5f, 1.5f, 1.2f, 1.5f, 2f});

                // En-têtes
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(255, 111, 0);

                String[] headers = {"Type", "Plantation", "Récolte Prévue", "Jours Rest.", "État", "Parcelle"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setBackgroundColor(headerColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(7);
                    table.addCell(cell);
                }

                // Données
                Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
                boolean alternate = false;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

                for (Culture c : filteredCultures) {
                    BaseColor rowColor = alternate ? new BaseColor(255, 248, 225) : BaseColor.WHITE;

                    // Type
                    PdfPCell cellType = new PdfPCell(new Phrase(c.getTypeCulture(), dataFont));
                    cellType.setBackgroundColor(rowColor);
                    cellType.setPadding(5);
                    table.addCell(cellType);

                    // Date Plantation
                    String datePlant = c.getDatePlantation() != null ? c.getDatePlantation().format(dateFormatter) : "-";
                    PdfPCell cellPlant = new PdfPCell(new Phrase(datePlant, dataFont));
                    cellPlant.setBackgroundColor(rowColor);
                    cellPlant.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellPlant.setPadding(5);
                    table.addCell(cellPlant);

                    // Date Récolte
                    String dateRec = c.getDateRecoltePrevue() != null ? c.getDateRecoltePrevue().format(dateFormatter) : "-";
                    PdfPCell cellRec = new PdfPCell(new Phrase(dateRec, dataFont));
                    cellRec.setBackgroundColor(rowColor);
                    cellRec.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellRec.setPadding(5);
                    table.addCell(cellRec);

                    // Jours restants
                    String joursRestants = "-";
                    if (c.getDateRecoltePrevue() != null) {
                        long jours = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue());
                        joursRestants = jours >= 0 ? jours + " j" : "Passé";
                    }
                    PdfPCell cellJours = new PdfPCell(new Phrase(joursRestants, dataFont));
                    cellJours.setBackgroundColor(rowColor);
                    cellJours.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellJours.setPadding(5);
                    table.addCell(cellJours);

                    // État
                    PdfPCell cellEtat = new PdfPCell(new Phrase(c.getEtatCroissance(), dataFont));
                    cellEtat.setBackgroundColor(rowColor);
                    cellEtat.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cellEtat.setPadding(5);
                    table.addCell(cellEtat);

                    // Parcelle
                    PdfPCell cellParcelle = new PdfPCell(new Phrase(c.getNomParcelle() != null ? c.getNomParcelle() : "-", dataFont));
                    cellParcelle.setBackgroundColor(rowColor);
                    cellParcelle.setPadding(5);
                    table.addCell(cellParcelle);

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

            // Ouvrir dans une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableViewCultures.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier la Culture");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            // Attendre la fermeture et recharger les données
            popupStage.showAndWait();
            loadParcellesMap();
            loadData();

        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "#C62828");
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
            try {
                boolean success = cultureService.delete(selected.getIdCulture());
                if (success) {
                    culturesList.remove(selected);
                    updateStatistics();
                    showMessage("✅ Culture supprimée avec succès !", "#2E7D32");
                } else {
                    showMessage("❌ Erreur lors de la suppression de la culture.", "#C62828");
                }
            } catch (RuntimeException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur de suppression");
                errorAlert.setHeaderText("❌ Erreur lors de la suppression");
                errorAlert.setContentText("Une erreur s'est produite lors de la suppression de la culture.\n\n" + e.getMessage());
                errorAlert.showAndWait();
                showMessage("❌ Erreur de suppression", "#C62828");
            }
        }
    }

    // ==================== NAVIGATION ====================

    @FXML
    void navigateToConsulterParcelle(ActionEvent event) {
        navigateTo("/consulterparcelle.fxml", "Liste des Parcelles");
    }

    @FXML
    void navigateToAjouterCulture(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterculture.fxml"));
            Parent root = loader.load();

            // Ouvrir dans une nouvelle fenêtre popup
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(tableViewCultures.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter une Culture");
            popupStage.setScene(new Scene(root));
            popupStage.setResizable(false);

            // Attendre la fermeture et recharger les données
            popupStage.showAndWait();
            loadParcellesMap();
            loadData();

        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "#C62828");
        }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tableViewCultures.getScene().getWindow();

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
