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
import org.example.pidev.models.Utilisateur;
import org.example.pidev.services.utilisateur.UtilisateurService;
import org.example.pidev.utils.Session;

import java.io.IOException;
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

                    // Simple layout: Nom Prenom on the left, email and role in smaller text
                    Label nameLabel = new Label((nom + " " + prenom).trim());
                    nameLabel.getStyleClass().add("list-item-title");

                    Label metaLabel = new Label(email + "  —  " + role);
                    metaLabel.getStyleClass().add("list-item-subtitle");
                    metaLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: gray;");

                    VBox vbox = new VBox(nameLabel, metaLabel);
                    HBox.setHgrow(vbox, Priority.ALWAYS);

                    setGraphic(vbox);
                }
            }
        });
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usersList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Ouvre la fenêtre de profil pour l'utilisateur courant
    @FXML
    private void openProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/profile_view.fxml"));
            Parent root = loader.load();

            ProfileController controller = loader.getController();
            controller.setUser(Session.getCurrentUser());

            Stage stage = new Stage();
            stage.setTitle("Mon Profil");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(usersList.getScene().getWindow());
            Scene scene = new Scene(root);
            var css = getClass().getResource("/styles/smartfarm.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserForm.fxml"));
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
}
