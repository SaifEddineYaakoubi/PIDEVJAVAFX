package org.example.pidev.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.example.pidev.models.Recolte;
import org.example.pidev.services.RecolteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Contrôleur pour l'affichage et la modification détaillée d'une récolte
 */
public class RecolteDetailController {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField tfIdRecolte;
    @FXML
    private TextField tfQuantite;
    @FXML
    private DatePicker dpDate;
    @FXML
    private TextField tfQualite;
    @FXML
    private TextField tfTypeCulture;
    @FXML
    private TextField tfLocalisation;
    @FXML
    private TextArea taaNotes;

    @FXML
    private Label lblRendementMoyen;
    @FXML
    private Label lblNbRendements;
    @FXML
    private Label lblQuantiteTotale;
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

    private RecolteService recolteService;
    private Recolte recolteActuelle;
    private Runnable callbackAnnuler;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Initialisation du contrôleur
     */
    @FXML
    public void initialize() {
        System.out.println("✅ RecolteDetailController initialisé");
        try {
            recolteService = new RecolteService();
            setupButtonListeners();
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
     * Charge une récolte pour l'affichage détaillé
     * @param idRecolte L'ID de la récolte à afficher
     */
    public void loadRecolte(int idRecolte) {
        try {
            System.out.println("🔄 Chargement de la récolte ID: " + idRecolte);
            recolteActuelle = recolteService.getById(idRecolte);

            if (recolteActuelle != null) {
                System.out.println("✅ Récolte trouvée: " + recolteActuelle.toString());
                afficherRecolte(recolteActuelle);
                chargerStatistiques();
                updateStatus("✅ Récolte chargée (ID: " + idRecolte + ")");
            } else {
                System.err.println("❌ Récolte non trouvée pour l'ID: " + idRecolte);
                showError("Récolte non trouvée");
                updateStatus("❌ Récolte non trouvée");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur lors du chargement de la récolte: " + e.getMessage());
            updateStatus("❌ Erreur de chargement");
        }
    }

    /**
     * Affiche les informations de la récolte dans les champs
     */
    private void afficherRecolte(Recolte r) {
        System.out.println("📝 Affichage des données de la récolte");
        tfIdRecolte.setText(String.valueOf(r.getIdRecolte()));
        tfQuantite.setText(String.valueOf(r.getQuantite()));
        dpDate.setValue(r.getDateRecolte());
        tfQualite.setText(r.getQualite());
        tfTypeCulture.setText(r.getTypeCulture());
        tfLocalisation.setText(r.getLocalisation());
        taaNotes.setText(""); // Notes vides par défaut
        System.out.println("✅ Données affichées");
    }

    /**
     * Charge les statistiques de la récolte (rendements associés)
     */
    private void chargerStatistiques() {
        try {
            System.out.println("📊 Chargement des statistiques");
            // TODO: Charger les rendements associés à cette récolte
            // Pour l'instant, affichage de valeurs par défaut
            lblRendementMoyen.setText("85%");
            lblNbRendements.setText("1");
            lblQuantiteTotale.setText(String.format("%.2f kg", recolteActuelle.getQuantite()));
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

            // Créer une récolte avec les nouvelles valeurs
            Recolte recolteModifiee = new Recolte(
                    Integer.parseInt(tfIdRecolte.getText()),
                    Double.parseDouble(tfQuantite.getText()),
                    dpDate.getValue(),
                    tfQualite.getText().trim(),
                    tfTypeCulture.getText().trim(),
                    tfLocalisation.getText().trim()
            );

            System.out.println("📝 Récolte modifiée: " + recolteModifiee.toString());

            // Mettre à jour en base de données
            recolteService.update(recolteModifiee);
            System.out.println("✅ Récolte enregistrée avec succès");

            showSuccess("Récolte modifiée avec succès!");
            updateStatus("✅ Modifications enregistrées");

            // Recharger les données
            recolteActuelle = recolteModifiee;
            chargerStatistiques();

        } catch (NumberFormatException e) {
            System.err.println("❌ Erreur de format: " + e.getMessage());
            showError("Les champs Quantité doivent être des nombres");
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

        if (tfQuantite.getText().isEmpty()) {
            showError("La quantité est obligatoire");
            return false;
        }

        if (dpDate.getValue() == null) {
            showError("La date est obligatoire");
            return false;
        }

        if (tfQualite.getText().trim().isEmpty()) {
            showError("La qualité est obligatoire");
            return false;
        }

        if (tfTypeCulture.getText().trim().isEmpty()) {
            showError("Le type de culture est obligatoire");
            return false;
        }

        if (tfLocalisation.getText().trim().isEmpty()) {
            showError("La localisation est obligatoire");
            return false;
        }

        try {
            Double.parseDouble(tfQuantite.getText());
        } catch (NumberFormatException e) {
            showError("La quantité doit être un nombre");
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
            if (recolteActuelle != null) {
                loadRecolte(recolteActuelle.getIdRecolte());
                updateStatus("✅ Données actualisées");
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors de l'actualisation: " + e.getMessage());
            showError("Erreur lors de l'actualisation");
            updateStatus("❌ Erreur d'actualisation");
        }
    }

    /**
     * Supprime la récolte actuelle
     */
    @FXML
    private void handleSupprimer() {
        try {
            if (recolteActuelle == null) {
                showError("Aucune récolte à supprimer");
                return;
            }

            // Confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText("Supprimer la récolte");
            alert.setContentText("Êtes-vous sûr de vouloir supprimer la récolte ID: " + recolteActuelle.getIdRecolte() + "?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    System.out.println("🗑️  Suppression de la récolte ID: " + recolteActuelle.getIdRecolte());
                    recolteService.delete(recolteActuelle.getIdRecolte());
                    System.out.println("✅ Récolte supprimée");
                    showSuccess("Récolte supprimée!");
                    updateStatus("✅ Récolte supprimée");
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
     * Retourne à la liste des récoltes
     */
    @FXML
    private void handleRetour() {
        System.out.println("← Retour à la liste des récoltes");
        if (callbackAnnuler != null) {
            callbackAnnuler.run();
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
        this.callbackAnnuler = callback;
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

