package org.example.pidev.controllers.utilisateur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.pidev.models.Badge;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.BadgeService;
import org.example.pidev.services.utilisateur.UtilisateurService;

import java.util.List;

public class BadgeAssignController {

    @FXML
    private TextField tfBadgeName;

    @FXML
    private ComboBox<String> cbBadgeNiveau;

    @FXML
    private ComboBox<Utilisateur> cbUsers;

    @FXML
    private ListView<Badge> listBadges;


    private final BadgeService badgeService = new BadgeService();
    private final UtilisateurService utilisateurService = new UtilisateurService();

    private final ObservableList<Badge> badgeList = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> userList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("[BadgeAssignController] === INITIALISATION ===");

        // Initialiser les niveaux de badge
        cbBadgeNiveau.getItems().addAll("Bronze", "Argent", "Or", "Platine");

        // === CONFIGURATION IMPORTANTE DE LA LISTVIEW ===
        // 1. Lier la ListView à la liste observable
        listBadges.setItems(badgeList);

        // 2. Configurer comment afficher chaque badge dans la liste
        listBadges.setCellFactory(listView -> new ListCell<Badge>() {
            @Override
            protected void updateItem(Badge badge, boolean empty) {
                super.updateItem(badge, empty);
                if (empty || badge == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Afficher le nom du badge avec son niveau
                    String display = badge.getNom() + " (" + badge.getNiveau() + ")";
                    setText(display);

                    // Optionnel : ajouter une icône selon le niveau
                    String icon = getIconForLevel(badge.getNiveau());
                    setText(icon + " " + display);
                }
            }
        });

        // Charger les utilisateurs et badges
        System.out.println("[BadgeAssignController] Chargement des données...");
        loadUsers();
        loadBadges();
        System.out.println("[BadgeAssignController] Chargement terminé");

        // Configurer l'affichage du ComboBox utilisateurs
        cbUsers.setConverter(new StringConverter<>() {
            @Override
            public String toString(Utilisateur user) {
                if (user == null) return "";
                String name = (user.getNom() != null ? user.getNom() : "");
                String email = (user.getEmail() != null ? user.getEmail() : "");
                return name.isEmpty() ? email : name + " (" + email + ")";
            }

            @Override
            public Utilisateur fromString(String string) {
                return null;
            }
        });
        System.out.println("[BadgeAssignController] === INITIALISATION TERMINÉE ===");
    }


    // Retourner une icône basée sur le niveau du badge
    private String getIconForLevel(String niveau) {
        if (niveau == null) return "⭕";
        return switch(niveau.toLowerCase()) {
            case "bronze" -> "🥉";
            case "argent" -> "🥈";
            case "or" -> "🥇";
            case "platine" -> "💎";
            default -> "⭐";
        };
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

    private void loadBadges() {
        try {
            System.out.println("[BadgeAssignController] Chargement des badges...");
            List<Badge> data = badgeService.getAll();
            System.out.println("[BadgeAssignController] Badges trouvés: " + data.size());

            // Afficher chaque badge dans la console
            for (Badge b : data) {
                System.out.println("  - ID: " + b.getId() + ", Nom: " + b.getNom() + ", Niveau: " + b.getNiveau());
            }

            // Remplir DIRECTEMENT la liste observable (SANS Platform.runLater)
            badgeList.clear();
            badgeList.addAll(data);

            // Forcer le rafraîchissement
            listBadges.refresh();

            System.out.println("[BadgeAssignController] Liste mise à jour avec " + badgeList.size() + " badges");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger les badges: " + e.getMessage());
        }
    }
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
            System.out.println("[BadgeAssignController] Erreur lors de la vérification: " + e.getMessage());
        }
        return false;
    }
    @FXML
    void assignerBadge(ActionEvent event) {
        Utilisateur selectedUser = cbUsers.getValue();
        Badge selectedBadge = listBadges.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un utilisateur.");
            return;
        }

        if (selectedBadge == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez sélectionner un badge.");
            return;
        }

        System.out.println("[BadgeAssignController] Assignation: Badge ID=" + selectedBadge.getId() + ", User ID=" + selectedUser.getIdUser());

        // === NOUVELLE VÉRIFICATION ===
        if (userHasBadge(selectedUser.getIdUser(), selectedBadge.getId())) {
            showAlert(Alert.AlertType.WARNING, "Attention",
                    "❌ L'utilisateur " + selectedUser.getNom() + " possède déjà le badge '" + selectedBadge.getNom() + "'.");
            return;
        }

        boolean success = badgeService.assignBadgeToUser(selectedBadge.getId(), selectedUser.getIdUser());

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès",
                    "✅ Badge '" + selectedBadge.getNom() + "' assigné à " + selectedUser.getNom() + ".");
            System.out.println("[BadgeAssignController] Assignation réussie!");
            // Réinitialiser les sélections
            listBadges.getSelectionModel().clearSelection();
            cbUsers.setValue(null);
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur",
                    "❌ Impossible d'assigner le badge. Vérifier que la table 'user_badge' existe en base.");
            System.out.println("[BadgeAssignController] Assignation échouée!");
        }
    }

    @FXML
    void fermer(ActionEvent event) {
        Stage stage = (Stage) cbUsers.getScene().getWindow();
        stage.close();
    }

    @FXML
    void creerBadge(ActionEvent event) {
        String nom = tfBadgeName.getText().trim();
        String niveau = cbBadgeNiveau.getValue();

        System.out.println("[BadgeAssignController] Création de badge: " + nom);

        if (nom.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Le nom du badge est requis.");
            return;
        }

        if (niveau == null) {
            showAlert(Alert.AlertType.WARNING, "Validation", "Veuillez choisir un niveau.");
            return;
        }

        Badge newBadge = new Badge();
        newBadge.setNom(nom);
        newBadge.setDescription(""); // Pas de description
        newBadge.setNiveau(niveau);

        boolean success = badgeService.add(newBadge);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Badge '" + nom + "' créé avec succès.");
            System.out.println("[BadgeAssignController] Badge créé: " + nom);

            // Nettoyer les champs
            tfBadgeName.clear();
            cbBadgeNiveau.setValue(null);

            // Recharger la liste et forcer le rafraîchissement
            loadBadges();

            // Assurer que la ListView est bien active
            if (listBadges != null) {
                listBadges.getSelectionModel().clearSelection();
                System.out.println("[BadgeAssignController] ListView prête pour sélection");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de créer le badge. Vérifier la console pour plus d'infos.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

