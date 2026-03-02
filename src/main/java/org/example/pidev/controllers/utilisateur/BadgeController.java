package org.example.pidev.controllers.utilisateur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.example.pidev.models.Badge;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.BadgeService;
import org.example.pidev.services.utilisateur.UtilisateurService;

import java.util.List;
import java.util.Optional;

public class BadgeController {

    @FXML
    private TextField tfNom;

    @FXML
    private TextField tfDescription;

    @FXML
    private ComboBox<String> cbNiveau;

    @FXML
    private ComboBox<Utilisateur> cbUsers;

    @FXML
    private TableView<Badge> tableBadge;

    @FXML
    private TableColumn<Badge, Integer> colId;

    @FXML
    private TableColumn<Badge, String> colNom;

    @FXML
    private TableColumn<Badge, String> colDescription;

    @FXML
    private TableColumn<Badge, String> colNiveau;

    @FXML
    private Label lblTargetUser;

    private final BadgeService service = new BadgeService();
    private final UtilisateurService utilisateurService = new UtilisateurService();

    private final ObservableList<Badge> badgeList = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> userList = FXCollections.observableArrayList();

    private Utilisateur targetUser = null;

    @FXML
    public void initialize() {
        // initialiser niveaux
        cbNiveau.getItems().clear();
        cbNiveau.getItems().addAll("Bronze", "Argent", "Or", "Platine");

        // config table
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colNiveau.setCellValueFactory(new PropertyValueFactory<>("niveau"));

        tableBadge.setItems(badgeList);

        // charger données
        loadBadges();
        loadUsers();

        // configure user ComboBox display
        cbUsers.setConverter(new StringConverter<>() {
            @Override
            public String toString(Utilisateur user) {
                if (user == null) return "";
                String name = (user.getNom() != null ? user.getNom() : "");
                String email = (user.getEmail() != null ? user.getEmail() : "");
                String display = name.isEmpty() ? email : name + " (" + email + ")";
                return display;
            }

            @Override
            public Utilisateur fromString(String string) {
                return null; // non utilisé
            }
        });


        // ensure lblTargetUser hidden by default
        if (lblTargetUser != null) {
            lblTargetUser.setVisible(false);
        }

        // selection table -> fill form
        tableBadge.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) {
                tfNom.setText(newV.getNom());
                tfDescription.setText(newV.getDescription());
                cbNiveau.setValue(newV.getNiveau());
            }
        });

    }

    private void loadBadges() {
        try {
            List<Badge> data = service.getAll();
            badgeList.clear();
            badgeList.addAll(data);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les badges: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            List<Utilisateur> data = utilisateurService.getAll();
            userList.clear();
            userList.addAll(data);
            cbUsers.setItems(userList);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    @FXML
    void ajouterBadge(ActionEvent event) {
        String nom = tfNom.getText();
        String desc = tfDescription.getText();
        String niveau = cbNiveau.getValue();

        if (nom == null || nom.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom du badge est requis.");
            return;
        }

        Badge b = new Badge();
        b.setNom(nom.trim());
        b.setDescription(desc != null ? desc.trim() : "");
        b.setNiveau(niveau != null ? niveau : "");

        boolean ok = service.add(b);
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge ajouté avec succès.");
            loadBadges();
            clearForm();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Echec lors de l'ajout du badge.");
        }
    }

    @FXML
    void modifierBadge(ActionEvent event) {
        Badge selected = tableBadge.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un badge à modifier.");
            return;
        }

        String nom = tfNom.getText();
        String desc = tfDescription.getText();
        String niveau = cbNiveau.getValue();

        if (nom == null || nom.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom du badge est requis.");
            return;
        }

        selected.setNom(nom.trim());
        selected.setDescription(desc != null ? desc.trim() : "");
        selected.setNiveau(niveau != null ? niveau : "");

        service.update(selected);
        showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge modifié.");
        loadBadges();
        clearForm();
    }

    @FXML
    void supprimerBadge(ActionEvent event) {
        Badge selected = tableBadge.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un badge à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le badge '" + selected.getNom() + "' ?");

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            boolean deleted = service.delete(selected.getId());
            if (deleted) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge supprimé.");
                loadBadges();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de supprimer le badge (vérifier la base de données).");
            }
        }
    }

    public void setSelectedUser(org.example.pidev.models.Utilisateur user) {
        if (user == null) return;
        this.targetUser = user;
        // hide combobox and show label
        if (cbUsers != null) cbUsers.setVisible(false);
        if (lblTargetUser != null) {
            lblTargetUser.setText((user.getNom() != null ? user.getNom() : "") + " (" + (user.getEmail() != null ? user.getEmail() : "") + ")");
            lblTargetUser.setVisible(true);
        }
    }

    @FXML
    void assignerBadge(ActionEvent event) {
        Badge selectedBadge = tableBadge.getSelectionModel().getSelectedItem();
        Utilisateur selectedUser = (targetUser != null) ? targetUser : cbUsers.getValue();

        if (selectedBadge == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un badge à assigner.");
            return;
        }
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner un utilisateur pour assigner le badge.");
            return;
        }

        // === NOUVELLE VÉRIFICATION ===
        if (userHasBadge(selectedUser.getIdUser(), selectedBadge.getId())) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "❌ L'utilisateur " + selectedUser.getNom() + " possède déjà le badge '" + selectedBadge.getNom() + "'.");
            return;
        }

        boolean ok = service.assignBadgeToUser(selectedBadge.getId(), selectedUser.getIdUser());
        if (ok) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge assigné à l'utilisateur.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'assigner le badge (vérifier la table user_badge en base).");
        }
    }

    // Ajoute cette méthode dans BadgeController.java
    private boolean userHasBadge(int userId, int badgeId) {
        String sql = "SELECT COUNT(*) FROM user_badge WHERE user_id = ? AND badge_id = ?";
        try {
            org.example.pidev.utils.DBConnection dbConnection = org.example.pidev.utils.DBConnection.getInstance();
            java.sql.Connection cnx = dbConnection.getConnection();
            java.sql.PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, badgeId);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (java.sql.SQLException e) {
            System.out.println("[BadgeController] Erreur lors de la vérification: " + e.getMessage());
        }
        return false;
    }

    private void clearForm() {
        tfNom.clear();
        tfDescription.clear();
        cbNiveau.setValue(null);
        tableBadge.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }
}
