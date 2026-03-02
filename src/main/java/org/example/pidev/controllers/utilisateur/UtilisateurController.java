package org.example.pidev.controllers.utilisateur;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.example.pidev.models.Badge;
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.utils.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

public class UtilisateurController {

    @FXML private ListView<Utilisateur> usersList;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private Label currentUserLabel;

    private final UtilisateurService service = new UtilisateurService();
    private final ObservableList<Utilisateur> userList = FXCollections.observableArrayList();

    // Listes pour filtrage/sorting
    private FilteredList<Utilisateur> filteredList;
    private SortedList<Utilisateur> sortedList;

    @FXML
    public void initialize() {
        System.out.println("=== INITIALISATION DU CONTROLEUR ===");

        // Vérifier que les composants FXML sont bien injectés
        checkFXMLComponents();

        // Configuration de l'affichage du ListView
        configureListCellFactory();

        // Préparer le filtered/sorted list
        filteredList = new FilteredList<>(userList, p -> true);
        sortedList = new SortedList<>(filteredList);

        // Assigner la liste triée/filtrée au ListView
        if (usersList != null) usersList.setItems(sortedList);

        // Initialiser roleFilter
        if (roleFilter != null) {
            roleFilter.getItems().clear();
            roleFilter.getItems().addAll("TOUS", "ADMIN", "AGRICULTEUR", "RESPONSABLE_STOCK");
            roleFilter.setValue("TOUS");
        }

        // Listeners pour recherche et filtre
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter());
        }
        if (roleFilter != null) {
            roleFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilter());
        }

        // Charger les données
        loadUsers();

        // Afficher l'utilisateur courant (si présent dans la Session)
        try {
            Utilisateur cur = Session.getCurrentUser();
            if (currentUserLabel != null) {
                if (cur != null) {
                    String name = (cur.getNom() != null ? cur.getNom() : "") + (cur.getPrenom() != null && !cur.getPrenom().isEmpty() ? " " + cur.getPrenom() : "");
                    currentUserLabel.setText("Connecté en tant que: " + name.trim() + " (" + (cur.getRole() != null ? cur.getRole().name() : "n/a") + ")");
                } else {
                    currentUserLabel.setText("");
                }
            }
        } catch (Exception e) {
            System.err.println("Impossible d'afficher l'utilisateur courant: " + e.getMessage());
        }

        System.out.println("=== INITIALISATION TERMINÉE ===");
    }

    private void checkFXMLComponents() {
        System.out.println("Vérification des composants FXML:");
        System.out.println("  usersList: " + (usersList != null ? "OK" : "NULL"));
        System.out.println("  searchField: " + (searchField != null ? "OK" : "NULL"));
        System.out.println("  roleFilter: " + (roleFilter != null ? "OK" : "NULL"));
    }

    private void configureListCellFactory() {
        if (usersList == null) return;

        usersList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Utilisateur user, boolean empty) {
                super.updateItem(user, empty);
                if (empty || user == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String nom = user.getNom() != null ? user.getNom() : "";
                    String prenom = user.getPrenom() != null ? user.getPrenom() : "";
                    String email = user.getEmail() != null ? user.getEmail() : "";
                    String role = user.getRole() != null ? user.getRole().name() : "";

                    // Créer le label avec nom et prénom
                    Label nameLabel = new Label((nom + " " + prenom).trim());
                    nameLabel.getStyleClass().add("list-item-title");

                    // Créer le label avec email et rôle
                    Label metaLabel = new Label(email + "  —  " + role);
                    metaLabel.getStyleClass().add("list-item-subtitle");
                    metaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

                    // Charger les badges de cet utilisateur
                    HBox badgesBox = new HBox(5);
                    List<Badge> userBadges = getUserBadgesForList(user.getIdUser());

                    if (!userBadges.isEmpty()) {
                        Label badgesLabel = new Label("Badges: ");
                        badgesLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");
                        badgesBox.getChildren().add(badgesLabel);

                        for (Badge badge : userBadges) {
                            String icon = getIconForLevel(badge.getNiveau());
                            Label badgeLabel = new Label(icon);
                            badgeLabel.setStyle("-fx-font-size: 14px;");
                            badgesBox.getChildren().add(badgeLabel);
                        }
                    }

                    VBox vbox = new VBox(nameLabel, metaLabel, badgesBox);
                    vbox.setSpacing(3);
                    HBox.setHgrow(vbox, Priority.ALWAYS);

                    setGraphic(vbox);
                }
            }
        });
    }

    // Récupérer les badges d'un utilisateur
    private List<Badge> getUserBadgesForList(int userId) {
        List<Badge> badges = new ArrayList<>();
        String sql = "SELECT b.id, b.nom, b.niveau FROM badge b " +
                "INNER JOIN user_badge ub ON b.id = ub.badge_id " +
                "WHERE ub.user_id = ?";
        try {
            org.example.pidev.utils.DBConnection dbConnection = org.example.pidev.utils.DBConnection.getInstance();
            java.sql.Connection cnx = dbConnection.getConnection();

            if (cnx != null && !cnx.isClosed()) {
                java.sql.PreparedStatement ps = cnx.prepareStatement(sql);
                ps.setInt(1, userId);
                java.sql.ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    Badge badge = new Badge();
                    badge.setId(rs.getInt("id"));
                    badge.setNom(rs.getString("nom"));
                    badge.setNiveau(rs.getString("niveau"));
                    badges.add(badge);
                }
            }
        } catch (Exception e) {
            // Silencieusement ignorer les erreurs (les badges sont optionnels)
        }

        return badges;
    }

    // Helper pour obtenir l'icône basée sur le niveau
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

    public void loadUsers() {
        System.out.println("CHARGEMENT DES UTILISATEURS...");
        try {
            List<Utilisateur> data = service.getAll();
            System.out.println("Nombre d'utilisateurs trouvés: " + data.size());

            // Afficher les données brutes pour debug
            System.out.println("Données reçues du service:");
            for (Utilisateur u : data) {
                System.out.println("  ID: " + u.getIdUser());
                System.out.println("  Nom: '" + u.getNom() + "'");
                System.out.println("  Prénom: '" + u.getPrenom() + "'");
                System.out.println("  Email: '" + u.getEmail() + "'");
                System.out.println("  Rôle: " + u.getRole());
                System.out.println("  ---");
            }

            // Vider et remplir la liste observable (filteredList s'adapte automatiquement)
            userList.clear();
            userList.addAll(data);

            // Forcer le rafraîchissement
            if (usersList != null) usersList.refresh();

            System.out.println("Liste mise à jour avec " + userList.size() + " éléments");
            System.out.println("ListView contient maintenant: " + (usersList != null ? usersList.getItems().size() : 0) + " éléments");

            // Ré-appliquer le filtre courant pour s'assurer que l'affichage est à jour
            applyFilter();

        } catch (Exception e) {
            System.err.println("ERREUR de chargement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Appliquer le filtre en fonction du texte de recherche et du rôle sélectionné
    private void applyFilter() {
        if (filteredList == null) return;

        String search = (searchField != null && searchField.getText() != null) ? searchField.getText().trim().toLowerCase(Locale.ROOT) : "";
        String role = (roleFilter != null && roleFilter.getValue() != null) ? roleFilter.getValue().trim() : "TOUS";

        boolean isAll = role.equalsIgnoreCase("TOUS") || role.equalsIgnoreCase("Tous") || role.equalsIgnoreCase("ALL");

        Predicate<Utilisateur> predicate = user -> {
            if (user == null) return false;

            boolean matchesRole = true;
            if (!isAll) {
                if (user.getRole() == null) matchesRole = false;
                else matchesRole = user.getRole().name().equalsIgnoreCase(role);
            }

            boolean matchesSearch = true;
            if (!search.isEmpty()) {
                String nom = user.getNom() != null ? user.getNom().toLowerCase(Locale.ROOT) : "";
                String prenom = user.getPrenom() != null ? user.getPrenom().toLowerCase(Locale.ROOT) : "";
                String email = user.getEmail() != null ? user.getEmail().toLowerCase(Locale.ROOT) : "";
                matchesSearch = nom.contains(search) || prenom.contains(search) || email.contains(search);
            }

            return matchesRole && matchesSearch;
        };

        filteredList.setPredicate(predicate);

        // Forcer le rafraîchissement de la ListView pour s'assurer que l'UI est mise à jour
        try {
            if (usersList != null) usersList.refresh();
            System.out.println("[Filter] ListView.refresh() appelé");
        } catch (Exception e) {
            System.err.println("[Filter] Erreur lors du refresh de la ListView: " + e.getMessage());
        }

        // Debug: afficher info
        long matched = filteredList.stream().count();
        System.out.println("[Filter] role='" + role + "' (isAll=" + isAll + "), search='" + search + "' => " + matched + " / " + userList.size() + " éléments affichés");

        // Debug détaillé: lister les utilisateurs et si ils correspondent
        StringBuilder sb = new StringBuilder();
        sb.append("[Filter details]\n");
        for (Utilisateur u : userList) {
            boolean m = predicate.test(u);
            sb.append("  ID:").append(u.getIdUser())
              .append(" email='").append(u.getEmail()).append("' role=")
              .append(u.getRole() != null ? u.getRole().name() : "null")
              .append(" -> ").append(m ? "MATCH" : "NO_MATCH").append("\n");
        }
        System.out.println(sb.toString());
    }

    @FXML
    private void showAddModal() {
        openModal(null, "Ajouter Utilisateur");
    }

    @FXML
    private void showEditModal() {
        Utilisateur selected = usersList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            openModal(selected, "Modifier Utilisateur");
        } else {
            showAlert("Veuillez sélectionner un utilisateur");
        }
    }

    @FXML
    private void handleDeleteUser() {
        Utilisateur selected = usersList.getSelectionModel().getSelectedItem();
        if (selected != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setHeaderText("Supprimer " + selected.getNom() + " " + selected.getPrenom() + " ?");

            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    service.delete(selected.getIdUser());
                    loadUsers();
                }
            });
        } else {
            showAlert("Veuillez sélectionner un utilisateur");
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Session.clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setMaximized(false);
            Scene scene = new Scene(root, 600, 500);
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Smart Farm - Connexion");
            stage.setResizable(false);
            stage.setWidth(600);
            stage.setHeight(500);
            stage.centerOnScreen();
            stage.setOnCloseRequest(evt -> {
                javafx.application.Platform.exit();
                System.exit(0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ouvre la fenêtre de profil pour l'utilisateur courant
    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/profile_view.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(Session.getCurrentUser());

            Stage stage = new Stage();
            stage.setTitle("Mon Profil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersList.getScene().getWindow());
            Scene scene = new Scene(root);
            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            // Après fermeture, rafraîchir la liste pour refléter d'éventuelles modifications
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openModal(Utilisateur user, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/UserForm.fxml"));
            Parent root = loader.load();

            UserFormController controller = loader.getController();
            controller.setParentController(this);

            if (user != null) {
                controller.setUserData(user);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersList.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Forcer le rafraîchissement après la fermeture du modal (sécurité)
            System.out.println("[UtilisateurController] Modal fermé, rafraîchissement de la liste...");
            loadUsers();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        applyFilter();
    }

    @FXML
    private void handleFilterChange() {
        applyFilter();
    }

    @FXML
    private void openBadges() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/BadgeView.fxml"));
            Parent root = loader.load();

            // Passer l'utilisateur sélectionné si présent
            BadgeController badgeController = loader.getController();
            Utilisateur selected = usersList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                badgeController.setSelectedUser(selected);
            }

            Stage stage = new Stage();
            stage.setTitle("Gestion des Badges");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersList.getScene().getWindow());
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/smartfarm.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            // rafraîchir éventuellement après fermeture
            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openBadgeAssign() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/BadgeAssignView.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Attribution de Badges");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersList.getScene().getWindow());
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/smartfarm.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            stage.setScene(scene);
            stage.setResizable(false);
            stage.showAndWait();

            loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void openChatbot() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/utilisateur/chatbot_view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usersList.getScene().getWindow();
            Scene scene = new Scene(root);

            var css1 = getClass().getResource("/styles/smartfarm.css");
            if (css1 != null) scene.getStylesheets().add(css1.toExternalForm());
            var css2 = getClass().getResource("/recoltes/smartfarmm.css");
            if (css2 != null) scene.getStylesheets().add(css2.toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Smart Farm - Assistant Agricole");
            stage.setResizable(true);
            stage.setMinWidth(900);
            stage.setMinHeight(600);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'assistant agricole: " + e.getMessage());
        }
    }

    // Add this helper method for alerts
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}
