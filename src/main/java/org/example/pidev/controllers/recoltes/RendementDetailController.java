package org.example.pidev.controllers.recoltes;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.pidev.models.Rendement;
import org.example.pidev.models.Recolte;
import org.example.pidev.services.recoltes.RendementService;
import org.example.pidev.services.recoltes.RecolteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

/**
 * Contrôleur pour l'affichage et la modification détaillée d'un rendement
 */
public class RendementDetailController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField tfIdRendement;
    @FXML
    private TextField tfSurface;
    @FXML
    private TextField tfQuantite;
    @FXML
    private TextField tfProductivite;
    @FXML
    private CheckBox chkAutoCalcul;
    @FXML
    private ComboBox<Recolte> comboRecolte;
    @FXML
    private TextArea taaNotes;

    @FXML
    private Label lblRendementMoyen;
    @FXML
    private Label lblProductiviteCalculee;
    @FXML
    private Label lblSurfaceTotale;
    @FXML
    private Label lblStatus;

    @FXML
    private Button btnRetour;
    @FXML
    private Button btnRetour2;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Button btnActualiser;
    @FXML
    private Button btnSupprimer;

    private RendementService rendementService;
    private RecolteService recolteService;
    private Rendement rendementActuel;
    private Runnable callbackRetour;

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("✅ RendementDetailController initialisé");
        try {
            rendementService = new RendementService();
            recolteService = new RecolteService();
            setupButtonListeners();
            loadRecoltes();
            setupListeners();
            updateStatus("✅ Interface détail prête");
        } catch (Exception e) {
            showError("Erreur d'initialisation: " + e.getMessage());
        }
    }

    /**
     * Configure les écouteurs de boutons
     */
    private void setupButtonListeners() {
        btnRetour.setOnAction(evt -> handleRetour());
        btnRetour2.setOnAction(evt -> handleRetour());
        btnEnregistrer.setOnAction(evt -> handleEnregistrer());
        btnActualiser.setOnAction(evt -> handleActualiser());
        btnSupprimer.setOnAction(evt -> handleSupprimer());
    }

    /**
     * Charge les récoltes dans le ComboBox
     */
    private void loadRecoltes() {
        try {
            List<Recolte> recoltes = recolteService.getAll();
            ObservableList<Recolte> observableRecoltes = FXCollections.observableArrayList(recoltes);
            comboRecolte.setItems(observableRecoltes);
            System.out.println("✅ Récoltes chargées: " + recoltes.size());
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement des récoltes: " + e.getMessage());
        }
    }

    /**
     * Configure les écouteurs pour le calcul automatique de productivité
     */
    private void setupListeners() {
        tfSurface.textProperty().addListener((obs, oldVal, newVal) -> updateProductivity());
        tfQuantite.textProperty().addListener((obs, oldVal, newVal) -> updateProductivity());
        chkAutoCalcul.selectedProperty().addListener((obs, oldVal, newVal) -> {
            tfProductivite.setDisable(newVal);
            if (newVal) {
                updateProductivity();
            }
        });
    }

    /**
     * Met à jour la productivité automatiquement
     */
    private void updateProductivity() {
        if (!chkAutoCalcul.isSelected()) return;

        try {
            String surfaceStr = tfSurface.getText().trim();
            String quantiteStr = tfQuantite.getText().trim();

            if (!surfaceStr.isEmpty() && !quantiteStr.isEmpty()) {
                double surface = Double.parseDouble(surfaceStr);
                double quantite = Double.parseDouble(quantiteStr);

                if (surface > 0) {
                    double productivite = quantite / surface;
                    tfProductivite.setText(String.format("%.2f", productivite));
                    lblProductiviteCalculee.setText(String.format("%.2f kg/ha", productivite));
                }
            }
        } catch (NumberFormatException e) {
            System.err.println("⚠️  Format invalide pour le calcul: " + e.getMessage());
        }
    }

    /**
     * Charge un rendement pour l'affichage détaillé
     * @param idRendement L'ID du rendement à afficher
     */
    public void loadRendement(int idRendement) {
        try {
            System.out.println("🔄 Chargement du rendement ID: " + idRendement);
            rendementActuel = rendementService.getById(idRendement);

            if (rendementActuel != null) {
                System.out.println("✅ Rendement trouvé: " + rendementActuel.toString());
                afficherRendement(rendementActuel);
                chargerStatistiques();
                updateStatus("✅ Rendement chargé (ID: " + idRendement + ")");
            } else {
                System.err.println("❌ Rendement non trouvé pour l'ID: " + idRendement);
                showError("Rendement non trouvé");
                updateStatus("❌ Rendement non trouvé");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement du rendement: " + e.getMessage());
            updateStatus("❌ Erreur de chargement");
        }
    }

    /**
     * Affiche les informations du rendement dans les champs
     */
    private void afficherRendement(Rendement r) {
        System.out.println("📝 Affichage des données du rendement");
        tfIdRendement.setText(String.valueOf(r.getIdRendement()));
        tfSurface.setText(String.valueOf(r.getSurfaceExploitee()));
        tfQuantite.setText(String.valueOf(r.getQuantiteTotale()));
        tfProductivite.setText(String.valueOf(r.getProductivite()));

        // Sélectionner la récolte associée
        for (Recolte recolte : comboRecolte.getItems()) {
            if (recolte.getIdRecolte() == r.getIdRecolte()) {
                comboRecolte.setValue(recolte);
                break;
            }
        }

        taaNotes.setText(""); // Notes vides par défaut
        System.out.println("✅ Données affichées");
    }

    /**
     * Charge les statistiques du rendement
     */
    private void chargerStatistiques() {
        try {
            System.out.println("📊 Chargement des statistiques");
            lblRendementMoyen.setText(String.format("%.2f kg/ha", rendementActuel.getProductivite()));
            lblProductiviteCalculee.setText(String.format("%.2f kg/ha", rendementActuel.getProductivite()));
            lblSurfaceTotale.setText(String.format("%.2f ha", rendementActuel.getSurfaceExploitee()));
            System.out.println("✅ Statistiques chargées");
        } catch (Exception e) {
            System.err.println("⚠️  Erreur lors du chargement des statistiques: " + e.getMessage());
        }
    }

    /**
     * Gère l'enregistrement des modifications
     */
    @FXML
    private void handleEnregistrer() {
        try {
            System.out.println("💾 Enregistrement des modifications");

            // Validation des champs
            if (!validerChamps()) {
                return;
            }

            // Récupérer les valeurs
            double surface = Double.parseDouble(tfSurface.getText().trim());
            double quantite = Double.parseDouble(tfQuantite.getText().trim());
            double productivite = Double.parseDouble(tfProductivite.getText().trim());
            int idRecolte = comboRecolte.getValue().getIdRecolte();

            // Créer un rendement avec les nouvelles valeurs
            Rendement rendementModifie = new Rendement(
                    surface,
                    quantite,
                    productivite,
                    idRecolte
            );
            rendementModifie.setIdRendement(Integer.parseInt(tfIdRendement.getText()));

            System.out.println("📝 Rendement modifié: " + rendementModifie.toString());

            // Mettre à jour en base de données
            rendementService.update(rendementModifie);
            System.out.println("✅ Rendement enregistré avec succès");

            showSuccess("Rendement modifié avec succès!");
            updateStatus("✅ Modifications enregistrées");

            // Recharger les données
            rendementActuel = rendementModifie;
            chargerStatistiques();

        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur de format: " + e.getMessage());
            showError("Les champs doivent être des nombres valides");
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de l'enregistrement: " + e.getMessage());
            updateStatus("❌ Erreur d'enregistrement");
        }
    }

    /**
     * Valide les champs du formulaire
     */
    private boolean validerChamps() {
        System.out.println("🔍 Validation des champs");

        if (tfSurface.getText().isEmpty()) {
            showError("La surface est obligatoire");
            return false;
        }

        if (tfQuantite.getText().isEmpty()) {
            showError("La quantité est obligatoire");
            return false;
        }

        if (tfProductivite.getText().isEmpty()) {
            showError("La productivité est obligatoire");
            return false;
        }

        if (comboRecolte.getValue() == null) {
            showError("Une récolte doit être sélectionnée");
            return false;
        }

        try {
            Double.parseDouble(tfSurface.getText());
            Double.parseDouble(tfQuantite.getText());
            Double.parseDouble(tfProductivite.getText());
        } catch (NumberFormatException e) {
            showError("Les champs doivent être des nombres");
            return false;
        }

        System.out.println("✅ Tous les champs sont valides");
        return true;
    }

    /**
     * Actualise les données depuis la base de données
     */
    @FXML
    private void handleActualiser() {
        try {
            System.out.println("🔄 Actualisation des données");
            if (rendementActuel != null) {
                loadRendement(rendementActuel.getIdRendement());
                updateStatus("✅ Données actualisées");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'actualisation: " + e.getMessage());
            showError("Erreur lors de l'actualisation");
            updateStatus("❌ Erreur d'actualisation");
        }
    }

    /**
     * Supprime le rendement actuel
     */
    @FXML
    private void handleSupprimer() {
        try {
            if (rendementActuel == null) {
                showError("Aucun rendement à supprimer");
                return;
            }

            // Confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer le rendement");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer le rendement ID: " + rendementActuel.getIdRendement() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("🗑️  Suppression du rendement ID: " + rendementActuel.getIdRendement());
                    rendementService.delete(rendementActuel.getIdRendement());
                    System.out.println("✅ Rendement supprimé");
                    showSuccess("Rendement supprimé!");
                    updateStatus("✅ Rendement supprimé");
                    handleRetour(); // Retourner à la liste
                }
            });
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de la suppression: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors de la suppression: " + e.getMessage());
            updateStatus("❌ Erreur de suppression");
        }
    }

    /**
     * Retourne à la liste des rendements
     */
    @FXML
    private void handleRetour() {
        System.out.println("← Retour à la liste des rendements");
        if (callbackRetour != null) {
            callbackRetour.run();
        }
        closeWindow();
    }

    /**
     * Ferme la fenêtre détail
     */
    private void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    /**
     * Définit le callback pour retourner à la liste
     */
    public void setCallbackRetour(Runnable callback) {
        this.callbackRetour = callback;
    }

    /**
     * Met à jour le message de statut
     */
    private void updateStatus(String message) {
        if (lblStatus != null) {
            lblStatus.setText(message);
        }
    }

    /**
     * Affiche un message d'erreur
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche un message de succès
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
