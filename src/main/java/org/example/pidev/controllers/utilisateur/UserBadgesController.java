package org.example.pidev.controllers.utilisateur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.pidev.models.Badge;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.BadgeService;
import org.example.pidev.services.utilisateur.UtilisateurService;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserBadgesController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<VBox> userBadgesList;

    private final UtilisateurService utilisateurService = new UtilisateurService();
    private final BadgeService badgeService = new BadgeService();
    private final ObservableList<VBox> userBadgesItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("[UserBadgesController] === INITIALISATION ===");
        userBadgesList.setItems(userBadgesItems);
        loadUserBadges();
        System.out.println("[UserBadgesController] === INITIALISATION TERMINÉE ===");
    }

    private void loadUserBadges() {
        try {
            System.out.println("[UserBadgesController] Chargement des utilisateurs et badges...");
            userBadgesItems.clear();

            // Charger tous les utilisateurs
            List<Utilisateur> users = utilisateurService.getAll();

            for (Utilisateur user : users) {
                // Créer un item pour chaque utilisateur
                VBox userItem = createUserBadgeItem(user);
                userBadgesItems.add(userItem);
            }

            System.out.println("[UserBadgesController] " + userBadgesItems.size() + " utilisateurs chargés");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("[UserBadgesController] ERREUR: " + e.getMessage());
        }
    }

    private VBox createUserBadgeItem(Utilisateur user) {
        VBox userBox = new VBox(10);
        userBox.setStyle("-fx-border-color: #ddd; -fx-border-radius: 5; -fx-padding: 10;");
        userBox.setPadding(new Insets(10));

        // Nom utilisateur
        Label userLabel = new Label("👤 " + user.getNom() + " (" + user.getEmail() + ")");
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Badges assignés
        HBox badgesBox = new HBox(10);
        badgesBox.setPadding(new Insets(5));

        List<Badge> userBadges = getUserBadges(user.getIdUser());

        if (userBadges.isEmpty()) {
            Label noBadgesLabel = new Label("Aucun badge assigné");
            noBadgesLabel.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            badgesBox.getChildren().add(noBadgesLabel);
        } else {
            Label badgesLabel = new Label("Badges: ");
            badgesLabel.setStyle("-fx-font-weight: bold;");
            badgesBox.getChildren().add(badgesLabel);

            for (Badge badge : userBadges) {
                String icon = getIconForLevel(badge.getNiveau());
                Label badgeLabel = new Label(icon + " " + badge.getNom() + " (" + badge.getNiveau() + ")");
                badgeLabel.setStyle("-fx-background-color: #e0e0e0; -fx-padding: 5; -fx-border-radius: 3;");
                badgesBox.getChildren().add(badgeLabel);
            }
        }

        userBox.getChildren().addAll(userLabel, badgesBox);
        return userBox;
    }

    private List<Badge> getUserBadges(int userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.id, b.nom, b.description, b.niveau " +
                "FROM badge b " +
                "INNER JOIN user_badge ub ON b.id = ub.badge_id " +
                "WHERE ub.user_id = ?";

        try {
            org.example.pidev.utils.DBConnection dbConnection = org.example.pidev.utils.DBConnection.getInstance();
            Connection cnx = dbConnection.getConnection();

            if (cnx != null && !cnx.isClosed()) {
                PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Badge badge = new Badge();
                    badge.setId(rs.getInt("id"));
                    badge.setNom(rs.getString("nom"));
                    badge.setDescription(rs.getString("description"));
                    badge.setNiveau(rs.getString("niveau"));
                    badges.add(badge);
                }
                System.out.println("[UserBadgesController] User ID " + userId + " a " + badges.size() + " badge(s)");
            }
        } catch (SQLException e) {
            System.out.println("[UserBadgesController] ERREUR SQL: " + e.getMessage());
        }

        return badges;
    }

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

    @FXML
    void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            loadUserBadges();
        } else {
            // Filtrer les résultats
            ObservableList<VBox> filteredItems = FXCollections.observableArrayList();

            for (VBox item : userBadgesItems) {
                String itemText = item.toString().toLowerCase();
                if (itemText.contains(searchText)) {
                    filteredItems.add(item);
                }
            }

            userBadgesList.setItems(filteredItems);
        }
    }

    @FXML
    void refreshData(ActionEvent event) {
        System.out.println("[UserBadgesController] Rafraîchissement des données...");
        loadUserBadges();
    }

    @FXML
    void closeWindow(ActionEvent event) {
        Stage stage = (Stage) searchField.getScene().getWindow();
        stage.close();
    }
}

