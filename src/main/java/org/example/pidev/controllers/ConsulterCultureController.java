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
import java.util.*;

public class ConsulterCultureController implements Initializable {

    @FXML private ListView<Culture> listViewCultures;
    @FXML private TextField tfRecherche;
    @FXML private ComboBox<String> cbFiltreEtat;
    @FXML private ComboBox<String> cbFiltreParcelle;
    @FXML private Label lblMessage;
    @FXML private Label lblResultats;
    @FXML private Label lblDateTime;

    // Statistiques
    @FXML private Label lblTotalCultures;
    @FXML private Label lblGermination;
    @FXML private Label lblCroissance;
    @FXML private Label lblFloraison;
    @FXML private Label lblMaturite;
    @FXML private Label lblRecolteProche;

    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;

    // Nouveaux éléments avancés
    @FXML private ToggleButton btnDarkMode;
    @FXML private VBox vboxAlerts;
    @FXML private ListView<String> lvHistorique;
    @FXML private Button btnSortType;
    @FXML private Button btnSortDate;
    @FXML private Button btnSortEtat;

    private CultureService cultureService;
    private ParcelleService parcelleService;
    private ObservableList<Culture> culturesList;
    private FilteredList<Culture> filteredCultures;
    private Map<Integer, String> parcellesMap;

    // Tri
    private boolean sortAscending = true;
    private String activeSortField = "";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cultureService = new CultureService();
        parcelleService = new ParcelleService();

        loadParcellesMap();

        // Cell factory avec tooltips et drag & drop
        listViewCultures.setCellFactory(param -> createCultureCell());

        cbFiltreEtat.setItems(FXCollections.observableArrayList("Tous", "Germination", "Croissance", "Floraison", "Maturité"));
        cbFiltreEtat.setValue("Tous");
        cbFiltreEtat.setOnAction(e -> applyFilters());

        loadData();

        tfRecherche.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        startClock();

        listViewCultures.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && listViewCultures.getSelectionModel().getSelectedItem() != null) {
                modifierCulture(null);
            }
        });

        if (btnModifier != null) btnModifier.setDisable(true);
        if (btnSupprimer != null) btnSupprimer.setDisable(true);

        listViewCultures.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            boolean has = newSel != null;
            if (btnModifier != null) btnModifier.setDisable(!has);
            if (btnSupprimer != null) btnSupprimer.setDisable(!has);
        });

        // === Fonctionnalités avancées ===

        // Dark Mode
        if (btnDarkMode != null) {
            ThemeManager tm = ThemeManager.getInstance();
            btnDarkMode.setSelected(tm.isDarkMode());
            btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
            btnDarkMode.setOnAction(e -> {
                tm.toggleAndApply(listViewCultures.getScene().getRoot());
                btnDarkMode.setText(tm.isDarkMode() ? "☀️ Mode Clair" : "🌙 Mode Sombre");
                listViewCultures.refresh();
            });
        }

        // Historique
        if (lvHistorique != null) {
            lvHistorique.setItems(ActionHistoryService.getInstance().getHistory());
        }

        // Alertes
        checkAlerts();

        // Apply dark mode if already active (e.g. after navigation)
        javafx.application.Platform.runLater(() -> {
            if (listViewCultures.getScene() != null) {
                ThemeManager.getInstance().applyTheme(listViewCultures.getScene().getRoot());
            }
        });
    }

    // ==================== CELL FACTORY avec TOOLTIP + DRAG & DROP ====================

    private ListCell<Culture> createCultureCell() {
        ListCell<Culture> cell = new ListCell<>() {
            @Override
            protected void updateItem(Culture culture, boolean empty) {
                super.updateItem(culture, empty);
                if (empty || culture == null) {
                    setText(null); setGraphic(null); setStyle(""); setTooltip(null);
                } else {
                    ThemeManager tm = ThemeManager.getInstance();
                    boolean isDark = tm.isDarkMode();
                    String mutedColor = tm.getMutedTextFill();
                    String textColor = tm.getPrimaryTextFill();
                    String titleColor = tm.getCultureTitleColor();

                    HBox container = new HBox(15);
                    container.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    container.setStyle(tm.getCellCardStyle());

                    // Type
                    VBox typeBox = new VBox(3);
                    Label lblTypeTitle = new Label("🌱 Type");
                    lblTypeTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    Label lblType = new Label(culture.getTypeCulture());
                    lblType.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + titleColor + ";");
                    typeBox.getChildren().addAll(lblTypeTitle, lblType);
                    typeBox.setPrefWidth(150);

                    // Plantation
                    VBox plantBox = new VBox(3);
                    Label lblPlantTitle = new Label("📅 Plantation");
                    lblPlantTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    String datePlant = culture.getDatePlantation() != null ?
                        culture.getDatePlantation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
                    Label lblPlant = new Label(datePlant);
                    lblPlant.setStyle("-fx-font-size: 13px; -fx-text-fill: " + textColor + ";");
                    plantBox.getChildren().addAll(lblPlantTitle, lblPlant);
                    plantBox.setPrefWidth(100);

                    // Récolte prévue
                    VBox recolteBox = new VBox(3);
                    Label lblRecolteTitle = new Label("📆 Récolte Prévue");
                    lblRecolteTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    String dateRec = culture.getDateRecoltePrevue() != null ?
                        culture.getDateRecoltePrevue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-";
                    Label lblRecolte = new Label(dateRec);
                    lblRecolte.setStyle("-fx-font-size: 13px; -fx-text-fill: " + textColor + ";");
                    recolteBox.getChildren().addAll(lblRecolteTitle, lblRecolte);
                    recolteBox.setPrefWidth(110);

                    // Jours restants
                    VBox joursBox = new VBox(3);
                    Label lblJoursTitle = new Label("⏱️ Jours");
                    lblJoursTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    Label lblJours = new Label();
                    String joursStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 3 10; -fx-background-radius: 10;";
                    if (culture.getDateRecoltePrevue() != null) {
                        long jours = ChronoUnit.DAYS.between(LocalDate.now(), culture.getDateRecoltePrevue());
                        if (jours < 0) { lblJours.setText("Dépassée"); joursStyle += "-fx-background-color: #FFCDD2; -fx-text-fill: #C62828;"; }
                        else if (jours <= 7) { lblJours.setText(jours + " j ⚠️"); joursStyle += "-fx-background-color: #FFE0B2; -fx-text-fill: #E65100;"; }
                        else if (jours <= 30) { lblJours.setText(jours + " j"); joursStyle += "-fx-background-color: #FFF9C4; -fx-text-fill: #F9A825;"; }
                        else { lblJours.setText(jours + " j"); joursStyle += "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;"; }
                    } else { lblJours.setText("N/A"); joursStyle += "-fx-background-color: #E0E0E0; -fx-text-fill: #616161;"; }
                    lblJours.setStyle(joursStyle);
                    joursBox.getChildren().addAll(lblJoursTitle, lblJours);
                    joursBox.setPrefWidth(90);

                    // État
                    VBox etatBox = new VBox(3);
                    Label lblEtatTitle = new Label("🏷️ État");
                    lblEtatTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    Label lblEtat = new Label(culture.getEtatCroissance());
                    String etatStyle = "-fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 15;";
                    switch (culture.getEtatCroissance().toLowerCase()) {
                        case "germination": etatStyle += "-fx-background-color: #C8E6C9; -fx-text-fill: #2E7D32;"; break;
                        case "croissance": etatStyle += "-fx-background-color: #BBDEFB; -fx-text-fill: #1565C0;"; break;
                        case "floraison": etatStyle += "-fx-background-color: #F8BBD9; -fx-text-fill: #AD1457;"; break;
                        case "mature": etatStyle += "-fx-background-color: #FFE0B2; -fx-text-fill: #E65100;"; break;
                        default: etatStyle += "-fx-background-color: #E0E0E0; -fx-text-fill: #616161;";
                    }
                    lblEtat.setStyle(etatStyle);
                    etatBox.getChildren().addAll(lblEtatTitle, lblEtat);
                    etatBox.setPrefWidth(110);

                    // Parcelle
                    VBox parcelleBox = new VBox(3);
                    Label lblParcelleTitle = new Label("📍 Parcelle");
                    lblParcelleTitle.setStyle("-fx-font-size: 10px; -fx-text-fill: " + mutedColor + ";");
                    Label lblParcelle = new Label(culture.getNomParcelle() != null ? culture.getNomParcelle() : "-");
                    lblParcelle.setStyle("-fx-font-size: 13px; -fx-text-fill: " + textColor + ";");
                    parcelleBox.getChildren().addAll(lblParcelleTitle, lblParcelle);
                    parcelleBox.setPrefWidth(130);

                    container.getChildren().addAll(typeBox, plantBox, recolteBox, joursBox, etatBox, parcelleBox);
                    setGraphic(container);
                    setStyle("-fx-padding: 5 0; -fx-background-color: transparent;");

                    // === TOOLTIP ENRICHI ===
                    long joursRestants = culture.getDateRecoltePrevue() != null ?
                        ChronoUnit.DAYS.between(LocalDate.now(), culture.getDateRecoltePrevue()) : -999;
                    String joursInfo = joursRestants == -999 ? "N/A" : (joursRestants < 0 ? "Dépassée de " + Math.abs(joursRestants) + "j" : joursRestants + " jours restants");

                    Tooltip tooltip = new Tooltip(
                        "🌱 " + culture.getTypeCulture() + "\n" +
                        "📅 Plantation: " + datePlant + "\n" +
                        "📆 Récolte prévue: " + dateRec + "\n" +
                        "⏱️ " + joursInfo + "\n" +
                        "🏷️ État: " + culture.getEtatCroissance() + "\n" +
                        "📍 Parcelle: " + (culture.getNomParcelle() != null ? culture.getNomParcelle() : "-")
                    );
                    tooltip.setShowDelay(Duration.millis(300));
                    tooltip.setShowDuration(Duration.seconds(15));
                    tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: rgba(120,53,15,0.95); -fx-text-fill: white; -fx-padding: 12; -fx-background-radius: 8;");
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
            if (event.getGestureSource() != cell && event.getDragboard().hasString()) event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });
        cell.setOnDragEntered(event -> {
            if (event.getGestureSource() != cell && event.getDragboard().hasString())
                cell.setStyle("-fx-border-color: #f59e0b; -fx-border-width: 0 0 2 0; -fx-border-style: dashed;");
        });
        cell.setOnDragExited(event -> cell.setStyle("-fx-padding: 5 0; -fx-background-color: transparent;"));
        cell.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            if (db.hasString()) {
                int draggedIdx = Integer.parseInt(db.getString());
                int targetIdx = cell.getIndex();
                if (draggedIdx != targetIdx && draggedIdx < culturesList.size() && targetIdx < culturesList.size()) {
                    Culture dragged = culturesList.remove(draggedIdx);
                    culturesList.add(targetIdx, dragged);
                    listViewCultures.getSelectionModel().select(targetIdx);
                    ActionHistoryService.getInstance().log("🔀 Réorganisation: " + dragged.getTypeCulture());
                }
                event.setDropCompleted(true);
            }
            event.consume();
        });
        cell.setOnDragDone(event -> cell.setStyle("-fx-padding: 5 0; -fx-background-color: transparent;"));

        return cell;
    }

    // ==================== TRI ====================

    @FXML
    void trierParType(ActionEvent event) {
        sortBy("type", Comparator.comparing(Culture::getTypeCulture, String.CASE_INSENSITIVE_ORDER));
    }

    @FXML
    void trierParDate(ActionEvent event) {
        sortBy("date", Comparator.comparing(c -> c.getDateRecoltePrevue() != null ? c.getDateRecoltePrevue() : LocalDate.MAX));
    }

    @FXML
    void trierParEtat(ActionEvent event) {
        sortBy("etat", Comparator.comparing(Culture::getEtatCroissance, String.CASE_INSENSITIVE_ORDER));
    }

    private void sortBy(String field, Comparator<Culture> comparator) {
        if (field.equals(activeSortField)) { sortAscending = !sortAscending; }
        else { activeSortField = field; sortAscending = true; }
        FXCollections.sort(culturesList, sortAscending ? comparator : comparator.reversed());
        updateSortButtonStyles();
        showMessage("🔤 Trié par " + field + (sortAscending ? " ↑" : " ↓"), "#1565C0");
    }

    private void updateSortButtonStyles() {
        boolean isDark = ThemeManager.getInstance().isDarkMode();
        String active = "-fx-background-color: linear-gradient(to right, #78350f, #b45309); -fx-text-fill: white; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand;";
        String inactive = isDark ?
            "-fx-background-color: #1e293b; -fx-text-fill: #e0e0e0; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #0f3460; -fx-border-radius: 8;" :
            "-fx-background-color: #f3f4f6; -fx-text-fill: #374151; -fx-font-size: 11px; -fx-font-weight: bold; -fx-padding: 6 14; -fx-background-radius: 8; -fx-cursor: hand; -fx-border-color: #e5e7eb; -fx-border-radius: 8;";
        if (btnSortType != null) { btnSortType.setStyle("type".equals(activeSortField) ? active : inactive); btnSortType.setText("type".equals(activeSortField) ? ("Type " + (sortAscending ? "↑" : "↓")) : "Type ↕"); }
        if (btnSortDate != null) { btnSortDate.setStyle("date".equals(activeSortField) ? active : inactive); btnSortDate.setText("date".equals(activeSortField) ? ("Date " + (sortAscending ? "↑" : "↓")) : "Date ↕"); }
        if (btnSortEtat != null) { btnSortEtat.setStyle("etat".equals(activeSortField) ? active : inactive); btnSortEtat.setText("etat".equals(activeSortField) ? ("État " + (sortAscending ? "↑" : "↓")) : "État ↕"); }
    }

    // ==================== ALERTES ====================

    private void checkAlerts() {
        if (vboxAlerts == null) return;
        vboxAlerts.getChildren().clear();
        int alertCount = 0;
        for (Culture c : culturesList) {
            if (c.getDateRecoltePrevue() == null) continue;
            long jours = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue());
            if (jours < 0) {
                VBox card = createAlertCard("🚨 RÉCOLTE DÉPASSÉE", c.getTypeCulture() + " — dépassée de " + Math.abs(jours) + "j", "rgba(239,68,68,0.15)", "#ef4444");
                vboxAlerts.getChildren().add(card);
                AnimationUtils.slideInFromLeft(card, alertCount * 150);
                alertCount++;
            } else if (jours <= 7) {
                VBox card = createAlertCard("⚠️ Récolte proche", c.getTypeCulture() + " — dans " + jours + " jour(s)", "rgba(245,158,11,0.15)", "#f59e0b");
                vboxAlerts.getChildren().add(card);
                AnimationUtils.slideInFromLeft(card, alertCount * 150);
                alertCount++;
            }
            if (alertCount >= 5) break;
        }
        if (alertCount == 0) {
            Label noAlert = new Label("✅ Aucune alerte");
            noAlert.setStyle("-fx-text-fill: rgba(255,255,255,0.5); -fx-font-size: 11px; -fx-padding: 8;");
            vboxAlerts.getChildren().add(noAlert);
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

    // ==================== EXISTING METHODS ====================

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
        for (Culture c : culturesList) {
            String nomParcelle = parcellesMap.get(c.getIdParcelle());
            c.setNomParcelle(nomParcelle != null ? nomParcelle : "Parcelle #" + c.getIdParcelle());
        }
        filteredCultures = new FilteredList<>(culturesList, c -> true);
        listViewCultures.setItems(filteredCultures);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = culturesList.size();
        long germination = culturesList.stream().filter(c -> "germination".equalsIgnoreCase(c.getEtatCroissance())).count();
        long croissance = culturesList.stream().filter(c -> "croissance".equalsIgnoreCase(c.getEtatCroissance())).count();
        long floraison = culturesList.stream().filter(c -> "floraison".equalsIgnoreCase(c.getEtatCroissance())).count();
        long maturite = culturesList.stream().filter(c -> "mature".equalsIgnoreCase(c.getEtatCroissance())).count();
        long recolteProche = culturesList.stream()
                .filter(c -> c.getDateRecoltePrevue() != null)
                .filter(c -> { long j = ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue()); return j >= 0 && j <= 7; })
                .count();

        lblTotalCultures.setText(String.valueOf(total));
        lblGermination.setText(String.valueOf(germination));
        lblCroissance.setText(String.valueOf(croissance));
        lblFloraison.setText(String.valueOf(floraison));
        lblMaturite.setText(String.valueOf(maturite));
        lblRecolteProche.setText(String.valueOf(recolteProche));

        // Pulse animation sur récolte proche si > 0
        if (recolteProche > 0 && lblRecolteProche != null) {
            AnimationUtils.pulseNode(lblRecolteProche);
        }

        updateResultsLabel();
        showMessage("✅ Liste mise à jour - " + total + " culture(s)", "#2E7D32");
    }

    private void applyFilters() {
        String searchText = tfRecherche.getText();
        String filterEtat = cbFiltreEtat.getValue();
        String filterParcelle = cbFiltreParcelle.getValue();

        filteredCultures.setPredicate(culture -> {
            boolean matchesSearch = true, matchesEtat = true, matchesParcelle = true;
            if (searchText != null && !searchText.trim().isEmpty()) {
                String f = searchText.toLowerCase().trim();
                matchesSearch = culture.getTypeCulture().toLowerCase().contains(f) ||
                        culture.getEtatCroissance().toLowerCase().contains(f) ||
                        (culture.getNomParcelle() != null && culture.getNomParcelle().toLowerCase().contains(f));
            }
            if (filterEtat != null && !"Tous".equals(filterEtat)) {
                String etatDb = culture.getEtatCroissance().toLowerCase();
                String etatFiltre = filterEtat.toLowerCase();
                // "Maturité" dans le filtre correspond à "mature" dans la BD
                if ("maturité".equals(etatFiltre)) {
                    matchesEtat = "mature".equals(etatDb);
                } else {
                    matchesEtat = etatDb.equals(etatFiltre);
                }
            }
            if (filterParcelle != null && !"Toutes".equals(filterParcelle)) matchesParcelle = filterParcelle.equals(culture.getNomParcelle());
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
        checkAlerts();
    }

    @FXML
    void exporterListe(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter la liste des cultures en PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
        fileChooser.setInitialFileName("cultures_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf");

        File file = fileChooser.showSaveDialog(listViewCultures.getScene().getWindow());
        if (file != null) {
            try {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                Font titleFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, new BaseColor(255, 111, 0));
                Paragraph title = new Paragraph("🌾 Liste des Cultures", titleFont);
                title.setAlignment(Element.ALIGN_CENTER); title.setSpacingAfter(10); document.add(title);

                Font dateFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
                Paragraph date = new Paragraph("Exporté le: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), dateFont);
                date.setAlignment(Element.ALIGN_CENTER); date.setSpacingAfter(20); document.add(date);

                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);
                table.setWidths(new float[]{2f, 1.5f, 1.5f, 1.2f, 1.5f, 2f});

                Font headerFont = new Font(Font.FontFamily.HELVETICA, 11, Font.BOLD, BaseColor.WHITE);
                BaseColor headerColor = new BaseColor(255, 111, 0);
                String[] headers = {"Type", "Plantation", "Récolte Prévue", "Jours Rest.", "État", "Parcelle"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                    cell.setBackgroundColor(headerColor); cell.setHorizontalAlignment(Element.ALIGN_CENTER); cell.setPadding(7); table.addCell(cell);
                }

                Font dataFont = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
                boolean alternate = false;
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                for (Culture c : filteredCultures) {
                    BaseColor rowColor = alternate ? new BaseColor(255, 248, 225) : BaseColor.WHITE;

                    PdfPCell c1 = new PdfPCell(new Phrase(c.getTypeCulture(), dataFont));
                    c1.setBackgroundColor(rowColor); c1.setPadding(5); table.addCell(c1);

                    PdfPCell c2 = new PdfPCell(new Phrase(c.getDatePlantation() != null ? c.getDatePlantation().format(dateFormatter) : "-", dataFont));
                    c2.setBackgroundColor(rowColor); c2.setHorizontalAlignment(Element.ALIGN_CENTER); c2.setPadding(5); table.addCell(c2);

                    PdfPCell c3 = new PdfPCell(new Phrase(c.getDateRecoltePrevue() != null ? c.getDateRecoltePrevue().format(dateFormatter) : "-", dataFont));
                    c3.setBackgroundColor(rowColor); c3.setHorizontalAlignment(Element.ALIGN_CENTER); c3.setPadding(5); table.addCell(c3);

                    String jr = c.getDateRecoltePrevue() != null ? (ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue()) >= 0 ? ChronoUnit.DAYS.between(LocalDate.now(), c.getDateRecoltePrevue()) + " j" : "Passé") : "-";
                    PdfPCell c4 = new PdfPCell(new Phrase(jr, dataFont));
                    c4.setBackgroundColor(rowColor); c4.setHorizontalAlignment(Element.ALIGN_CENTER); c4.setPadding(5); table.addCell(c4);

                    PdfPCell c5 = new PdfPCell(new Phrase(c.getEtatCroissance(), dataFont));
                    c5.setBackgroundColor(rowColor); c5.setHorizontalAlignment(Element.ALIGN_CENTER); c5.setPadding(5); table.addCell(c5);

                    PdfPCell c6 = new PdfPCell(new Phrase(c.getNomParcelle() != null ? c.getNomParcelle() : "-", dataFont));
                    c6.setBackgroundColor(rowColor); c6.setPadding(5); table.addCell(c6);

                    alternate = !alternate;
                }
                document.add(table);
                Paragraph footer = new Paragraph("\n© Smart Farm - Gestion Agricole Intelligente", dateFont);
                footer.setAlignment(Element.ALIGN_CENTER); document.add(footer);
                document.close();
                showMessage("✅ PDF exporté: " + file.getName(), "#2E7D32");
                ActionHistoryService.getInstance().logExport("PDF", file.getName());
            } catch (Exception e) {
                showMessage("❌ Erreur d'export PDF: " + e.getMessage(), "#C62828");
            }
        }
    }

    @FXML
    void modifierCulture(ActionEvent event) {
        Culture selected = listViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("⚠️ Veuillez sélectionner une culture.", "#FF9800"); return; }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/modifierculture.fxml"));
            Parent root = loader.load();
            ModifierCultureController controller = loader.getController();
            controller.setCulture(selected);
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listViewCultures.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Modifier la Culture");
            Scene popupScene = new Scene(root);
            addCss(popupScene);
            popupStage.setScene(popupScene);
            popupStage.setResizable(false);
            popupStage.showAndWait();
            loadParcellesMap(); loadData(); checkAlerts();
        } catch (IOException e) { showMessage("❌ Erreur: " + e.getMessage(), "#C62828"); }
    }

    @FXML
    void supprimerCulture(ActionEvent event) {
        Culture selected = listViewCultures.getSelectionModel().getSelectedItem();
        if (selected == null) { showMessage("⚠️ Veuillez sélectionner une culture.", "#FF9800"); return; }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation"); alert.setHeaderText("Supprimer la culture");
        alert.setContentText("Supprimer \"" + selected.getTypeCulture() + "\" ?\n\nCette action est irréversible.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                if (cultureService.delete(selected.getIdCulture())) {
                    culturesList.remove(selected); updateStatistics();
                    showMessage("✅ Culture supprimée !", "#2E7D32");
                    ActionHistoryService.getInstance().logDelete("Culture", selected.getTypeCulture());
                    checkAlerts();
                } else { showMessage("❌ Erreur de suppression.", "#C62828"); }
            } catch (RuntimeException e) {
                Alert err = new Alert(Alert.AlertType.ERROR);
                err.setTitle("Erreur"); err.setHeaderText("❌ Erreur"); err.setContentText(e.getMessage());
                err.showAndWait();
            }
        }
    }

    // ==================== NAVIGATION ====================

    @FXML void navigateToConsulterParcelle(ActionEvent event) { navigateTo("/consulterparcelle.fxml", "Liste des Parcelles"); }

    @FXML
    void navigateToAjouterCulture(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouterculture.fxml"));
            Parent root = loader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            popupStage.initOwner(listViewCultures.getScene().getWindow());
            popupStage.setTitle("Smart Farm - Ajouter une Culture");
            Scene popupScene = new Scene(root);
            addCss(popupScene);
            popupStage.setScene(popupScene);
            popupStage.setResizable(false);
            popupStage.showAndWait();
            loadParcellesMap(); loadData(); checkAlerts();
        } catch (IOException e) { showMessage("❌ Erreur: " + e.getMessage(), "#C62828"); }
    }

    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) listViewCultures.getScene().getWindow();
            javafx.geometry.Rectangle2D sb = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene newScene = new Scene(root, sb.getWidth(), sb.getHeight());
            addCss(newScene);
            stage.setScene(newScene);
            stage.setTitle("Smart Farm - " + title);
            stage.setX(sb.getMinX()); stage.setY(sb.getMinY()); stage.setMaximized(true);
            ThemeManager.getInstance().applyTheme(root);
        } catch (IOException e) { showMessage("❌ Erreur: " + e.getMessage(), "#C62828"); }
    }

    private void addCss(Scene scene) {
        try {
            String css = getClass().getResource("/styles/smartfarm.css").toExternalForm();
            if (css != null) {
                scene.getStylesheets().add(css);
            }
        } catch (Exception ignored) {}
    }

    private void showMessage(String message, String color) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 12px; -fx-font-weight: bold;");
    }
}
