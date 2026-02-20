package org.example.pidev.controllers;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.pidev.models.Culture;
import org.example.pidev.models.Parcelle;
import org.example.pidev.services.CultureService;
import org.example.pidev.services.ParcelleService;
import org.example.pidev.services.WeatherService;
import org.example.pidev.services.GeoLocationService;
import org.example.pidev.utils.ActionHistoryService;
import org.example.pidev.utils.AnimationUtils;
import org.example.pidev.utils.ThemeManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Optional;
import java.util.ResourceBundle;

public class ConsulterParcelleController implements Initializable {

    @FXML private ListView<Parcelle> listViewParcelles;
    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbFiltre;
    @FXML private Label lblMessage;
    @FXML private Label lblResultats;
    @FXML private Label lblDateTime;

    // Statistiques
    @FXML private Label lblTotalParcelles;
    @FXML private Label lblSuperficieTotale;
    @FXML private Label lblActives;
    @FXML private Label lblRepos;
    @FXML private Label lblExploitees;

    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    // Météo
    @FXML private Label lblWeatherEmoji;
    @FXML private Label lblWeatherTemp;
    @FXML private Label lblWeatherDesc;
    @FXML private Label lblWeatherCity;
    @FXML private Label lblWeatherHumidity;
    @FXML private Label lblWeatherWind;
    @FXML private Label lblWeatherAdvice;
    @FXML private javafx.scene.web.WebView webViewMap;

    // Nouveaux éléments avancés
    @FXML private ToggleButton btnDarkMode;
    @FXML private VBox vboxAlerts;
    @FXML private ListView<String> lvHistorique;
    @FXML private Button btnSortNom;
    @FXML private Button btnSortSuperficie;
    @FXML private Button btnSortEtat;

    private ParcelleService parcelleService;
    private CultureService cultureService;
    private WeatherService weatherService;
    private GeoLocationService geoLocationService;
    private ObservableList<Parcelle> parcellesList;
    private FilteredList<Parcelle> filteredParcelles;

    // Tri
    private Comparator<Parcelle> currentComparator = null;
    private boolean sortAscending = true;
    private String activeSortField = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        parcelleService = new ParcelleService();
        cultureService = new CultureService();
        weatherService = new WeatherService();
        geoLocationService = new GeoLocationService();

        // Cell factory avec tooltips et drag & drop
        listViewParcelles.setCellFactory(param -> createParcelleCell());

        cbFiltre.setItems(FXCollections.observableArrayList("Tous", "Active", "Repos", "Exploitée"));
        cbFiltre.setValue("Tous");
        cbFiltre.setOnAction(e -> applyFilters());

        loadData();

        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        startClock();

        listViewParcelles.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && listViewParcelles.getSelectionModel().getSelectedItem() != null) {
                modifierParcelle(null);
            }
        });

        if (btnModifier != null) btnModifier.setDisable(true);
        if (btnSupprimer != null) btnSupprimer.setDisable(true);

        listViewParcelles.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean has = newSel != null;
            if (btnModifier != null) btnModifier.setDisable(!has);
            if (btnSupprimer != null) btnSupprimer.setDisable(!has);
            if (has) {
                loadWeatherForParcelle(newSel);
                loadMapForParcelle(newSel);
            }
        });

        loadDefaultWeather();

        if (webViewMap != null) {
            String defaultHtml = GeoLocationService.getMapHtml(36.8065, 10.1815, "Tunisie");
            webViewMap.getEngine().loadContent(defaultHtml);
        }

        // === Fonctionnalités avancées ===

        // Dark Mode
        if (btnDarkMode != null) {
            ThemeManager tm = ThemeManager.getInstance();
            btnDarkMode.setSelected(tm.isDarkMode());
            btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
            btnDarkMode.setOnAction(e -> {
                tm.toggleAndApply(listViewParcelles.getScene().getRoot());
                btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
            });
        }

        // Historique
        if (lvHistorique != null) {
            lvHistorique.setItems(ActionHistoryService.getInstance().getHistory());
        }

        // Alertes
        checkAlerts();
    }

    // ==================== CELL FACTORY avec TOOLTIP + DRAG & DROP ====================

    private ListCell<Parcelle> createParcelleCell() {
        ListCell<Parcelle> cell = new ListCell<>() {
            @Override
            protected void updateItem(Parcelle parcelle, boolean empty) {
                super.updateItem(parcelle, empty);
                if (empty || parcelle == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                    setTooltip(null);
                } else {
                    HBox container = new HBox(20);
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    container.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;");

                    VBox nomBox = new VBox(3);
                    Label lblNomTitle = new Label("📝 Nom");
                    lblNomTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af;");
                    Label lblNom = new Label(parcelle.getNom());
                    lblNom.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1a472a;");
                    nomBox.getChildren().addAll(lblNomTitle, lblNom);
                    nomBox.setPrefWidth(200);

                    VBox superficieBox = new VBox(3);
                    Label lblSupTitle = new Label("📐 Superficie");
                    lblSupTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af;");
                    Label lblSup = new Label(String.format("%.2f m²", parcelle.getSuperficie()));
                    lblSup.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #374151;");
                    superficieBox.getChildren().addAll(lblSupTitle, lblSup);
                    superficieBox.setPrefWidth(120);

                    VBox locBox = new VBox(3);
                    Label lblLocTitle = new Label("📍 Localisation");
                    lblLocTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af;");
                    Label lblLoc = new Label(parcelle.getLocalisation());
                    lblLoc.setStyle("-fx-font-size: 14px; -fx-text-fill: #374151;");
                    locBox.getChildren().addAll(lblLocTitle, lblLoc);
                    locBox.setPrefWidth(250);

                    VBox etatBox = new VBox(3);
                    Label lblEtatTitle = new Label("🏷️ État");
                    lblEtatTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: #9ca3af;");
                    Label lblEtat = new Label(parcelle.getEtat());
                    String etatStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 15;";
                    switch (parcelle.getEtat().toLowerCase()) {
                        case "active": etatStyle += "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;"; break;
                        case "repos": etatStyle += "-fx-background-color: #FFE0B2; -fx-text-fill: #E65100;"; break;
                        case "exploitée": etatStyle += "-fx-background-color: #BBDEFB; -fx-text-fill: #1565C0;"; break;
                        default: etatStyle += "-fx-background-color: #E0E0E0; -fx-text-fill: #616161;";
                    }
                    lblEtat.setStyle(etatStyle);
                    etatBox.getChildren().addAll(lblEtatTitle, lblEtat);
                    etatBox.setPrefWidth(120);

                    container.getChildren().addAll(nomBox, superficieBox, locBox, etatBox);
                    setGraphic(container);
                    setStyle("-fx-padding: 5 0; -fx-background-color: transparent;");

                    // === TOOLTIP ENRICHI ===
                    Tooltip tooltip = new Tooltip(
                        "🏡 " + parcelle.getNom() + "\n" +
                        "📐 Superficie: " + String.format("%.2f m²", parcelle.getSuperficie()) + "\n" +
                        "📍 Localisation: " + parcelle.getLocalisation() + "\n" +
                        "🏷️ État: " + parcelle.getEtat() + "\n" +
                        "👤 Utilisateur ID: " + parcelle.getIdUser()
                    );
                    tooltip.setShowDelay(Duration.millis(300));
                    tooltip.setShowDuration(Duration.seconds(15));
                    tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: rgba(26,71,42,0.95); -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8;");
                    setTooltip(tooltip);
                }
            }
        };

        // === DRAG & DROP ===
        cell.setOnDragDetected(event -> {
            if (cell.getItem() == null) return;
            Dragboard db = cell.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent cc = new ClipboardContent();
            cc.putString(String.valueOf(cell.getIndex()));
            db.setContent(cc);
            cell.setStyle("-fx-opacity: 0.5;");
            event.consume();
        });

        cell.setOnDragOver(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        cell.setOnDragEntered(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) {
                cell.setStyle("-fx-border-color: #667eea; -fx-border-width: 0 0 2 0; -fx-border-style: dashed;");
            }
        });

        cell.setOnDragExited(event -> {
            cell.setStyle("-fx-padding: 5 0; -fx-background-color: transparent;");
        });

        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int draggedIdx = Integer.parseInt(db.getString());
                int targetIdx = cell.getIndex();
                if (draggedIdx != targetIdx && draggedIdx < parcellesList.size() && targetIdx < parcellesList.size()) {
                    Parcelle dragged = parcellesList.remove(draggedIdx);
                    parcellesList.add(targetIdx, dragged);
                    listViewParcelles.getSelectionModel().select(targetIdx);
                    ActionHistoryService.getInstance().log("🔀 Réorganisation: " + dragged.getNom());
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });

        cell.setOnDragDone(event -> {
            cell.setStyle("-fx-padding: 5 0; -fx-background-color: transparent;");
        });

        return cell;
    }

    // ==================== TRI ====================

    @FXML
    void trierParNom(ActionEvent event) {
        sortBy("nom", Comparator.comparing(Parcelle::getNom, String.CASE_INSENSITIVE_ORDER));
    }

    @FXML
    void trierParSuperficie(ActionEvent event) {
        sortBy("superficie", Comparator.comparingDouble(Parcelle::getSuperficie));
    }

    @FXML
    void trierParEtat(ActionEvent event) {
        sortBy("etat", Comparator.comparing(Parcelle::getEtat, String.CASE_INSENSITIVE_ORDER));
    }

    private void sortBy(String field, Comparator<Parcelle> comparator) {
        if (field.equals(activeSortField)) {
            sortAscending = !sortAscending;
        } else {
            activeSortField = field;
            sortAscending = true;
        }
        currentComparator = sortAscending ? comparator : comparator.reversed();
        FXCollections.sort(parcellesList, currentComparator);

        updateSortButtonStyles();
        showMessage("🔤 Trié par " + field + (sortAscending ? " ↑" : " ↓"), "#1565C0");
    }

    private void updateSortButtonStyles() {
        String activeStyle = "-fx-background-color: linear-gradient(to right, #1a472a, #2d5a3f); -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand;";
        String inactiveStyle = "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #e5e7eb; -fx-border-radius: 8;";
        if (btnSortNom != null) btnSortNom.setStyle("nom".equals(activeSortField) ? activeStyle : inactiveStyle);
        if (btnSortSuperficie != null) btnSortSuperficie.setStyle("superficie".equals(activeSortField) ? activeStyle : inactiveStyle);
        if (btnSortEtat != null) btnSortEtat.setStyle("etat".equals(activeSortField) ? activeStyle : inactiveStyle);

        if (btnSortNom != null) btnSortNom.setText("nom".equals(activeSortField) ? ("Nom " + (sortAscending ? "↑" : "↓")) : "Nom ↕");
        if (btnSortSuperficie != null) btnSortSuperficie.setText("superficie".equals(activeSortField) ? ("Superficie " + (sortAscending ? "↑" : "↓")) : "Superficie ↕");
        if (btnSortEtat != null) btnSortEtat.setText("etat".equals(activeSortField) ? ("État " + (sortAscending ? "↑" : "↓")) : "État ↕");
    }

    // ==================== ALERTES ====================

    private void checkAlerts() {
        if (vboxAlerts == null) return;
        vboxAlerts.getChildren().clear();

        try {
            var cultures = cultureService.getAll();
            int alertCount = 0;
            for (Culture c : cultures) {
                if (c.getDateRecoltePrevue() == null) continue;
                long jours = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue());

                if (jours < 0) {
                    VBox alertCard = createAlertCard("🚨 RÉCOLTE DÉPASSÉE",
                        c.getTypeCulture() + " — dépassée de " + Math.abs(jours) + " jours",
                        "rgba(239,68,68,0.15)", "#ef4444");
                    vboxAlerts.getChildren().add(alertCard);
                    AnimationUtils.slideInFromLeft(alertCard, alertCount * 150);
                    alertCount++;
                } else if (jours <= 7) {
                    VBox alertCard = createAlertCard("⚠️ Récolte proche",
                        c.getTypeCulture() + " — dans " + jours + " jour(s)",
                        "rgba(245,158,11,0.15)", "#f59e0b");
                    vboxAlerts.getChildren().add(alertCard);
                    AnimationUtils.slideInFromLeft(alertCard, alertCount * 150);
                    alertCount++;
                }

                if (alertCount >= 5) break; // Max 5 alertes
            }

            if (alertCount == 0) {
                Label noAlert = new Label("✅ Aucune alerte");
                noAlert.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-padding: 8;");
                vboxAlerts.getChildren().add(noAlert);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Erreur alertes: " + e.getMessage());
        }
    }

    private VBox createAlertCard(String title, String message, String bgColor, String borderColor) {
        VBox card = new VBox(4);
        card.setStyle("-fx-background-color: " + bgColor + "; -fx-border-color: " + borderColor + "; -fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 10;");

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: " + borderColor + ";");

        Label lblMsg = new Label(message);
        lblMsg.setStyle("-fx-font-size: 10px; -fx-text-fill: rgba(255,255,255,0.8);");
        lblMsg.setWrapText(true);

        card.getChildren().addAll(lblTitle, lblMsg);
        return card;
    }

    // ==================== DARK MODE ====================

    @FXML
    void toggleDarkMode(ActionEvent event) {
        ThemeManager tm = ThemeManager.getInstance();
        tm.toggleAndApply(listViewParcelles.getScene().getRoot());
        if (btnDarkMode != null) {
            btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
        }
    }

    // ==================== EXISTING METHODS (clock, data, filters, stats) ====================

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
        listViewParcelles.setItems(filteredParcelles);
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

            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase().trim();
                matchesSearch = parcelle.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        parcelle.getLocalisation().toLowerCase().contains(lowerCaseFilter) ||
                        parcelle.getEtat().toLowerCase().contains(lowerCaseFilter);
            }

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
        checkAlerts();
    }

    @FXML
    void exporterListe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des parcelles en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("parcelles_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        File file = fileChooser.showSaveDialog(listViewParcelles.getScene().getWindow());
        if (file != null) {
            try {
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(46, 125, 50));
                Paragraph title = new Paragraph("🌱 Liste des Parcelles", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);

                Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
                Paragraph date = new Paragraph("Exporté le: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), dateFont);
                date.setAlignment(Element.ALIGN_CENTER);
                date.setSpacingAfter(20);
                document.add(date);

                Font statsFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL);
                double superficieTotale = parcellesList.stream().mapToDouble(Parcelle::getSuperficie).sum();
                Paragraph stats = new Paragraph(String.format("Total: %d parcelles | Superficie totale: %.2f m²", filteredParcelles.size(), superficieTotale), statsFont);
                stats.setAlignment(Element.ALIGN_CENTER);
                stats.setSpacingAfter(15);
                document.add(stats);

                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2.5f, 1.5f, 3f, 1.5f});

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

                Font dataFont = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
                boolean alternate = false;
                for (Parcelle p : filteredParcelles) {
                    BaseColor rowColor = alternate ? new BaseColor(245, 245, 245) : BaseColor.WHITE;

                    PdfPCell cellNom = new PdfPCell(new Phrase(p.getNom(), dataFont));
                    cellNom.setBackgroundColor(rowColor); cellNom.setPadding(6); table.addCell(cellNom);

                    PdfPCell cellSup = new PdfPCell(new Phrase(String.format("%.2f", p.getSuperficie()), dataFont));
                    cellSup.setBackgroundColor(rowColor); cellSup.setHorizontalAlignment(Element.ALIGN_CENTER); cellSup.setPadding(6); table.addCell(cellSup);

                    PdfPCell cellLoc = new PdfPCell(new Phrase(p.getLocalisation(), dataFont));
                    cellLoc.setBackgroundColor(rowColor); cellLoc.setPadding(6); table.addCell(cellLoc);

                    PdfPCell cellEtat = new PdfPCell(new Phrase(p.getEtat(), dataFont));
                    cellEtat.setBackgroundColor(rowColor); cellEtat.setHorizontalAlignment(Element.ALIGN_CENTER); cellEtat.setPadding(6); table.addCell(cellEtat);

                    alternate = !alternate;
                }

                document.add(table);

                Paragraph footer = new Paragraph("\n© Smart Farm - Gestion Agricole Intelligente", dateFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);

                document.close();
                showMessage("✅ PDF exporté: " + file.getName(), "#2E7D32");
                ActionHistoryService.getInstance().logExport("PDF", file.getName());

            } catch (Exception e) {
                showMessage("❌ Erreur d'export PDF: " + e.getMessage(), "#C62828");
            }
        }
    }

    @FXML
    void modifierParcelle(ActionEvent event) {
        Parcelle selected = listViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("⚠️ Veuillez sélectionner une parcelle à modifier.", "#FF9800"); return; }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierparcelle.fxml"));
            Parent root = loader.load();
            ModifierParcelleController controller = loader.getController();
            controller.setParcelle(selected);

            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listViewParcelles.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier la Parcelle");
            Scene popupScene = new Scene(root);
            addCss(popupScene);
            popupStage.setScene(popupScene);
            popupStage.setResizable(false);
            popupStage.showAndWait();
            loadData();
            checkAlerts();
        } catch (IOException e) {
            showMessage("❌ Erreur: " + e.getMessage(), "#C62828");
        }
    }

    @FXML
    void supprimerParcelle(ActionEvent event) {
        Parcelle selected = listViewParcelles.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("⚠️ Veuillez sélectionner une parcelle à supprimer.", "#FF9800"); return; }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la parcelle");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer \"" + selected.getNom() + "\" ?\n\nCette action est irréversible.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean success = parcelleService.delete(selected.getIdParcelle());
                if (success) {
                    parcellesList.remove(selected);
                    updateStatistics();
                    showMessage("✅ Parcelle supprimée avec succès !", "#2E7D32");
                    ActionHistoryService.getInstance().logDelete("Parcelle", selected.getNom());
                } else {
                    showMessage("❌ Erreur lors de la suppression.", "#C62828");
                }
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("foreign key constraint")) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Suppression impossible");
                    errorAlert.setHeaderText("❌ Cette parcelle ne peut pas être supprimée");
                    errorAlert.setContentText("La parcelle \"" + selected.getNom() + "\" contient des cultures associées.\n\n" +
                        "Supprimez d'abord les cultures de cette parcelle.");
                    errorAlert.showAndWait();
                    showMessage("⚠️ Suppression annulée - Cultures associées", "#FF9800");
                } else {
                    showMessage("❌ Erreur: " + msg, "#C62828");
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
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listViewParcelles.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter une Parcelle");
            Scene popupScene = new Scene(root);
            addCss(popupScene);
            popupStage.setScene(popupScene);
            popupStage.setResizable(false);
            popupStage.showAndWait();
            loadData();
            checkAlerts();
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
            Stage stage = (Stage) listViewParcelles.getScene().getWindow();
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene newScene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            addCss(newScene);
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - " + title);
            stage.setX(screenBounds.getMinX());
            stage.setY(screenBounds.getMinY());
            stage.setMaximized(true);
            ThemeManager.getInstance().applyTheme(root);
        } catch (IOException e) {
            showMessage("❌ Erreur de navigation: " + e.getMessage(), "#C62828");
        }
    }

    // ==================== MÉTÉO & CARTE ====================

    private void loadDefaultWeather() {
        setWeatherLoading();
        new Thread(() -> {
            WeatherService.WeatherData data = weatherService.getWeatherByCity("Tunis");
            Platform.runLater(() -> updateWeatherUI(data));
        }).start();
    }

    private void loadWeatherForParcelle(Parcelle parcelle) {
        if (parcelle == null || parcelle.getLocalisation() == null) return;
        setWeatherLoading();
        new Thread(() -> {
            WeatherService.WeatherData data = weatherService.getWeatherByLocation(parcelle.getLocalisation());
            Platform.runLater(() -> updateWeatherUI(data));
        }).start();
    }

    private void loadMapForParcelle(Parcelle parcelle) {
        if (parcelle == null || parcelle.getLocalisation() == null || webViewMap == null) return;
        new Thread(() -> {
            var results = geoLocationService.searchLocation(parcelle.getLocalisation(), 1);
            Platform.runLater(() -> {
                if (results != null && !results.isEmpty()) {
                    var loc = results.get(0);
                    webViewMap.getEngine().loadContent(GeoLocationService.getMapHtml(loc.getLatitude(), loc.getLongitude(), parcelle.getNom()));
                }
            });
        }).start();
    }

    private void setWeatherLoading() {
        if (lblWeatherEmoji != null) lblWeatherEmoji.setText("⏳");
        if (lblWeatherTemp != null) lblWeatherTemp.setText("...");
        if (lblWeatherDesc != null) lblWeatherDesc.setText("Chargement...");
        if (lblWeatherCity != null) lblWeatherCity.setText("");
        if (lblWeatherHumidity != null) lblWeatherHumidity.setText("...");
        if (lblWeatherWind != null) lblWeatherWind.setText("...");
        if (lblWeatherAdvice != null) lblWeatherAdvice.setText("");
    }

    private void updateWeatherUI(WeatherService.WeatherData data) {
        if (data != null) {
            if (lblWeatherEmoji != null) lblWeatherEmoji.setText(data.getWeatherEmoji());
            if (lblWeatherTemp != null) lblWeatherTemp.setText(data.getFormattedTemp());
            if (lblWeatherDesc != null) lblWeatherDesc.setText(data.getDescription());
            if (lblWeatherCity != null) lblWeatherCity.setText(data.getCityName());
            if (lblWeatherHumidity != null) lblWeatherHumidity.setText(data.getHumidity() + "%");
            if (lblWeatherWind != null) lblWeatherWind.setText(String.format("%.0f km/h", data.getWindSpeed()));
            if (lblWeatherAdvice != null) lblWeatherAdvice.setText(data.getAgricultureAdvice());
        } else {
            if (lblWeatherEmoji != null) lblWeatherEmoji.setText("⚠️");
            if (lblWeatherTemp != null) lblWeatherTemp.setText("N/A");
            if (lblWeatherDesc != null) lblWeatherDesc.setText("Météo indisponible");
            if (lblWeatherCity != null) lblWeatherCity.setText("");
            if (lblWeatherHumidity != null) lblWeatherHumidity.setText("N/A");
            if (lblWeatherWind != null) lblWeatherWind.setText("N/A");
            if (lblWeatherAdvice != null) lblWeatherAdvice.setText("⚠️ Vérifiez votre connexion internet");
        }
    }

    // ==================== HELPERS ====================

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
    }

    private void addCss(Scene scene) {
        try {
            String css = getClass().getResource("/styles/smartfarm.css").toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }
        } catch (Exception ignored) {}
    }
}
