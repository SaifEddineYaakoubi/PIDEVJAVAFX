package org.example.pidev.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.pidev.models.Recolte;
import org.example.pidev.models.Rendement;
import org.example.pidev.services.RecolteService;
import org.example.pidev.services.RendementService;

public class AddRendementController {

    @FXML
    private TextField txtSurface;
    @FXML
    private TextField txtQuantite;
    @FXML
    private ComboBox<Recolte> comboRecolte;  // <-- remplacer le TextField
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;
    private Stage stage;
    private RendementService rendementService = new RendementService();
    private RecolteService recolteService = new RecolteService();  // service pour récupérer les récoltes

    public void setStage(Stage stage) { this.stage = stage; }

    @FXML
    public void initialize() {
        loadRecoltes();  // charger les récoltes dans le combo box
        btnSave.setOnAction(e -> saveRendement());
        btnCancel.setOnAction(e -> stage.close());
    }

    private void loadRecoltes() {
        try {
            ObservableList<Recolte> list = FXCollections.observableArrayList(recolteService.getAll());
            comboRecolte.setItems(list);
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors du chargement des récoltes: " + e.getMessage()).showAndWait();
        }
    }
    private void saveRendement() {
        try {
            double surface = Double.parseDouble(txtSurface.getText());
            double quantite = Double.parseDouble(txtQuantite.getText());

            Recolte selectedRecolte = comboRecolte.getSelectionModel().getSelectedItem();
            if (selectedRecolte == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner une récolte").showAndWait();
                return;
            }

            double productivite = quantite / surface;
            Rendement rendement = new Rendement();
            rendement.setSurfaceExploitee(surface);
            rendement.setQuantiteTotale(quantite);
            rendement.setProductivite(productivite);
            rendement.setIdRecolte(selectedRecolte.getIdRecolte());
            rendementService.add(rendement);

            new Alert(Alert.AlertType.INFORMATION, "Rendement ajouté avec succès").showAndWait();
            stage.close();

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Veuillez entrer des valeurs numériques valides").showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout: " + e.getMessage()).showAndWait();
        }
    }
}
