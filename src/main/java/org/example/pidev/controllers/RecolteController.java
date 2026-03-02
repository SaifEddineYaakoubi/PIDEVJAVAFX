package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.ButtonType;
import javafx.geometry.Insets;
import org.example.pidev.models.Recolte;
import org.example.pidev.services.RecolteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RecolteController {

    @FXML
    private TableView<Recolte> tableRecoltes;
    @FXML
    private TableColumn<Recolte, Integer> colId;
    @FXML
    private TableColumn<Recolte, Double> colQuantite;
    @FXML
    private TableColumn<Recolte, LocalDate> colDate;
    @FXML
    private TableColumn<Recolte, String> colQualite;
    @FXML
    private TableColumn<Recolte, String> colType;
    @FXML
    private TableColumn<Recolte, String> colLocalisation;
    @FXML
    private TableColumn<Recolte, Void> colAction;

    @FXML
    private TextField searchField;
    @FXML
    private Label lblTotal;
    @FXML
    private Label lblStatus;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnActualiser;
    @FXML
    private Button btnChercher;

    private RecolteService recolteService;
    private ObservableList<Recolte> recolteList;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        System.out.println("✅ RecolteController initialisé");
        try {
            recolteService = new RecolteService();
            setupTableColumns();
            loadRecoltes();
            setupButtonListeners();
            updateStatus("✅ Données chargées");
        } catch (Exception e) {
            showError("Erreur d'initialisation: " + e.getMessage());
        }
    }

    private void setupTableColumns() {
        System.out.println("🔧 Configuration des colonnes de la table...");
        try {
            colId.setCellValueFactory(new PropertyValueFactory<>("idRecolte"));
            System.out.println("  ✅ Colonne ID configurée");

            // Quantité formatée
            colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
            colQuantite.setCellFactory(col -> new TableCell<Recolte, Double>() {
                @Override
                protected void updateItem(Double item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(String.format("%.2f", item));
                    }
                }
            });
            System.out.println("  ✅ Colonne Quantité configurée");

            // Date formatée
            colDate.setCellValueFactory(new PropertyValueFactory<>("dateRecolte"));
            colDate.setCellFactory(col -> new TableCell<Recolte, LocalDate>() {
                @Override
                protected void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.format(DATE_FORMAT));
                    }
                }
            });
            System.out.println("  ✅ Colonne Date configurée");

            colQualite.setCellValueFactory(new PropertyValueFactory<>("qualite"));
            System.out.println("  ✅ Colonne Qualité configurée");

            colType.setCellValueFactory(new PropertyValueFactory<>("typeCulture"));
            System.out.println("  ✅ Colonne Type configurée");

            colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
            System.out.println("  ✅ Colonne Localisation configurée");

            // Colonne d'actions - ne pas lier à une propriété car elle génère les boutons
            colAction.setCellFactory(col -> new TableCell<Recolte, Void>() {
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setGraphic(null);
                    } else {
                        Recolte r = getTableRow().getItem();
                        HBox h = new HBox(6);

                        Button bEdit = new Button("✏️ Modifier");
                        bEdit.getStyleClass().add("button-edit");
                        bEdit.setOnAction(e -> handleModifier(r));

                        Button bDel = new Button("🗑️ Supprimer");
                        bDel.getStyleClass().add("button-delete");
                        bDel.setOnAction(e -> handleSupprimer(r));

                        h.getChildren().addAll(bEdit, bDel);
                        setGraphic(h);
                    }
                }
            });
            System.out.println("  ✅ Colonne Actions configurée");
            System.out.println("✅ Toutes les colonnes sont configurées");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la configuration des colonnes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadRecoltes() {
        try {
            System.out.println("🔄 Chargement des récoltes en cours...");
            List<Recolte> list = recolteService.getAll();
            System.out.println("✅ Nombre de récoltes récupérées: " + list.size());
            for (Recolte r : list) {
                System.out.println("   - " + r.toString());
            }

            recolteList = FXCollections.observableArrayList(list);
            System.out.println("✅ ObservableList créée avec " + recolteList.size() + " éléments");

            if (tableRecoltes != null) {
                System.out.println("✅ Table trouvée, affectation des données...");
                tableRecoltes.setItems(recolteList);
                System.out.println("✅ Données affectées à la table");
            } else {
                System.out.println("❌ ERREUR: tableRecoltes est null");
            }

            if (lblTotal != null) lblTotal.setText("Total: " + list.size() + " récoltes");
            updateStatus("✅ " + list.size() + " récoltes chargées");
        } catch (Exception e) {
            System.err.println("❌ Exception lors du chargement: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement: " + e.getMessage());
            updateStatus("❌ Erreur de chargement");
        }
    }

    private void setupButtonListeners() {
        if (btnAjouter != null) btnAjouter.setOnAction(evt -> handleAjouter());
        if (btnActualiser != null) btnActualiser.setOnAction(evt -> handleActualiser());
        if (btnChercher != null) btnChercher.setOnAction(evt -> handleChercher());

        // Double-clic pour ouvrir le détail
        if (tableRecoltes != null) {
            tableRecoltes.setRowFactory(tv -> {
                TableRow<Recolte> row = new TableRow<Recolte>() {
                    @Override
                    protected void updateItem(Recolte r, boolean empty) {
                        super.updateItem(r, empty);
                    }
                };

                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Recolte recolte = row.getItem();
                        handleDetailRecolte(recolte);
                    }
                });

                return row;
            });
        }
    }

    /**
     * Ouvre la fenêtre détail pour afficher et modifier une récolte
     */
    private void handleDetailRecolte(Recolte recolte) {
        try {
            System.out.println("📖 Ouverture du détail pour la récolte ID: " + recolte.getIdRecolte());

            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/RecolteDetail.fxml")
            );

            javafx.scene.Parent root = loader.load();
            RecolteDetailController controller = loader.getController();

            // Définir le callback pour recharger la liste après fermeture
            controller.setCallbackRetour(() -> {
                System.out.println("📋 Rechargement de la liste");
                loadRecoltes();
            });

            // Charger les données de la récolte
            controller.loadRecolte(recolte.getIdRecolte());

            // Créer et afficher la fenêtre
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Détail de la Récolte");
            stage.setScene(new javafx.scene.Scene(root));
            stage.setWidth(800);
            stage.setHeight(700);
            stage.setMinWidth(600);
            stage.setMinHeight(500);

            // Rendre la fenêtre modale relative à la fenêtre principale
            javafx.stage.Stage mainStage = (javafx.stage.Stage) tableRecoltes.getScene().getWindow();
            stage.initOwner(mainStage);
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);

            stage.showAndWait();

            System.out.println("✅ Fenêtre détail fermée");

        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'ouverture du détail: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du détail: " + e.getMessage());
        }
    }

    @FXML
    private void handleAjouter() {
        // Simple dialog to collect fields and add
        Dialog<Recolte> dialog = new Dialog<>();
        dialog.setTitle("Ajouter une récolte");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));

        TextField tfQuantite = new TextField();
        DatePicker dpDate = new DatePicker(LocalDate.now());
        TextField tfQualite = new TextField();
        TextField tfType = new TextField();
        TextField tfLocal = new TextField();

        grid.add(new Label("Quantité (kg):"), 0, 0);
        grid.add(tfQuantite, 1, 0);
        grid.add(new Label("Date:"), 0, 1);
        grid.add(dpDate, 1, 1);
        grid.add(new Label("Qualité:"), 0, 2);
        grid.add(tfQualite, 1, 2);
        grid.add(new Label("Type:"), 0, 3);
        grid.add(tfType, 1, 3);
        grid.add(new Label("Localisation:"), 0, 4);
        grid.add(tfLocal, 1, 4);

        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                try {
                    double q = Double.parseDouble(tfQuantite.getText());
                    LocalDate d = dpDate.getValue();
                    System.out.println("📝 Création d'une nouvelle récolte:");
                    System.out.println("   Quantité: " + q);
                    System.out.println("   Date: " + d);
                    System.out.println("   Qualité: " + tfQualite.getText());
                    System.out.println("   Type: " + tfType.getText());
                    System.out.println("   Localisation: " + tfLocal.getText());

                    Recolte r = new Recolte(q, d, tfQualite.getText(), tfType.getText(), tfLocal.getText());
                    System.out.println("🔍 Avant ajout en base");
                    if (recolteService.add(r)) {
                        System.out.println("✅ Récolte ajoutée avec succès (ID: " + r.getIdRecolte() + ")");
                        updateStatus("✅ Récolte ajoutée");
                        return r;
                    } else {
                        System.out.println("❌ Erreur lors de l'ajout en base");
                        showError("Erreur lors de l'ajout en base");
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Exception: " + ex.getMessage());
                    ex.printStackTrace();
                    showError("Valeurs invalides: " + ex.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(r -> {
            System.out.println("✅ Dialog fermé, réchargement des récoltes...");
            loadRecoltes();
        });
    }

    @FXML
    private void handleActualiser() {
        updateStatus("⏳ Actualisation en cours...");
        loadRecoltes();
    }

    @FXML
    private void handleChercher() {
        String s = (searchField == null) ? "" : searchField.getText().trim().toLowerCase();
        if (s.isEmpty()) { loadRecoltes(); return; }
        if (recolteList == null) return;
        ObservableList<Recolte> filtered = FXCollections.observableArrayList();
        for (Recolte r : recolteList) {
            if ((r.getTypeCulture() != null && r.getTypeCulture().toLowerCase().contains(s)) ||
                    (r.getLocalisation() != null && r.getLocalisation().toLowerCase().contains(s)) ||
                    (r.getQualite() != null && r.getQualite().toLowerCase().contains(s)) ||
                    String.valueOf(r.getQuantite()).contains(s)) {
                filtered.add(r);
            }
        }
        if (tableRecoltes != null) tableRecoltes.setItems(filtered);
        updateStatus("🔍 " + filtered.size() + " résultats trouvés");
    }

    private void handleModifier(Recolte r) {
        System.out.println("✏️  Modification de la récolte ID: " + r.getIdRecolte());
        handleDetailRecolte(r);
    }

    private void handleSupprimer(Recolte r) {
        // Dialogue pour saisir la cause de suppression
        TextInputDialog causeDialog = new TextInputDialog();
        causeDialog.setTitle("Cause de suppression");
        causeDialog.setHeaderText("Suppression de la récolte");
        causeDialog.setContentText("Veuillez saisir la cause de suppression:");
        causeDialog.showAndWait().ifPresent(cause -> {
            // Si une cause est saisie, afficher une confirmation
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Supprimer une récolte");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette récolte ?" +
                    "\n\nCause de suppression: " + cause);
            confirmAlert.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.OK) {
                    // Archiver la récolte avant de la supprimer
                    System.out.println("📚 Archivage de la récolte ID: " + r.getIdRecolte() + " avec cause: " + cause);
                    // L'archivage se fait côté service avant suppression
                    recolteService.delete(r.getIdRecolte()); // la suppression archive automatiquement
                    showSuccess("Récolte supprimée et archivée");
                    loadRecoltes();
                }
            });
        });
    }

    private void updateStatus(String msg) {
        if (lblStatus != null) lblStatus.setText(msg);
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Erreur");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Succès");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
