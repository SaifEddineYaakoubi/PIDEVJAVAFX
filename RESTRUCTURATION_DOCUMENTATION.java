/**
 * DOCUMENTATION - RESTRUCTURATION DU PROJET
 *
 * ENTITÉS PRINCIPALES :
 * =====================
 * 1. RECOLTE
 *    - Attributs : idRecolte (PK), quantite, dateRecolte, qualite, typeCulture, localisation
 *    - Pas de lien avec Culture ou Parcelle
 *    - Service : RecolteService (CRUD complète)
 *
 * 2. RENDEMENT
 *    - Attributs : idRendement (PK), surfaceExploitee, quantiteTotale, productivite, idRecolte (FK)
 *    - Relation : Un-à-Plusieurs (Une Récolte -> Plusieurs Rendements)
 *    - Service : RendementService (CRUD complète)
 *
 * CHANGEMENTS EFFECTUÉS :
 * =======================
 *
 * ✅ Classe Recolte.java
 *    - Supprimé : idCulture (FK vers Culture)
 *    - Ajouté : typeCulture (String), localisation (String)
 *    - Raison : Rendre Recolte indépendante de Culture et Parcelle
 *
 * ✅ RecolteService.java
 *    - Mis à jour : valider() - suppression de validerIdCulture
 *    - Ajouté : validerTypeCulture(), validerLocalisation()
 *    - Mis à jour : add() - nouvelle requête SQL
 *    - Mis à jour : update() - nouvelle requête SQL
 *    - Mis à jour : getById() - nouvelle requête SQL
 *    - Mis à jour : getAll() - nouvelle requête SQL
 *    - Base de données : Colonnes (id_recolte, quantite, date_recolte, qualite, type_culture, localisation)
 *
 * ✅ Rendement.java
 *    - Structure complète avec constructeurs et getters/setters
 *    - Attributs : idRendement, surfaceExploitee, quantiteTotale, productivite, idRecolte
 *
 * ✅ RendementService.java (NOUVEAU)
 *    - Implémente IService<Rendement>
 *    - CRUD complète : add, update, delete, getById, getAll
 *    - Validations robustes pour tous les attributs
 *    - Méthodes utilitaires : getByIdRecolte(), getProductiviteMoyenne()
 *    - Base de données : Colonnes (id_rendement, surface_exploitee, quantite_totale, productivite, id_recolte)
 *
 * MODÈLES NON MODIFIÉS (À NETTOYER OPTIONNELLEMENT) :
 * =====================================================
 * - Culture.java
 * - Parcelle.java
 * - Client.java
 * - Utilisateur.java
 * - Produit.java
 * - Vente.java
 * - LigneVente.java
 * - Stock.java
 * - Alerte.java
 *
 * SCHÉMA BD RECOMMANDÉ :
 * ======================
 * CREATE TABLE recolte (
 *     id_recolte INT PRIMARY KEY AUTO_INCREMENT,
 *     quantite DOUBLE NOT NULL,
 *     date_recolte DATE NOT NULL,
 *     qualite VARCHAR(100) NOT NULL,
 *     type_culture VARCHAR(100) NOT NULL,
 *     localisation VARCHAR(100) NOT NULL
 * );
 *
 * CREATE TABLE rendement (
 *     id_rendement INT PRIMARY KEY AUTO_INCREMENT,
 *     surface_exploitee DOUBLE NOT NULL,
 *     quantite_totale DOUBLE NOT NULL,
 *     productivite DOUBLE NOT NULL,
 *     id_recolte INT NOT NULL,
 *     FOREIGN KEY (id_recolte) REFERENCES recolte(id_recolte) ON DELETE CASCADE
 * );
 *
 * UTILISATION DANS LE CODE :
 * ==========================
 *
 * // Créer une récolte
 * RecolteService recolteService = new RecolteService();
 * Recolte recolte = new Recolte(quantite, date, qualite, typeCulture, localisation);
 * recolteService.add(recolte);
 *
 * // Créer un rendement pour une récolte
 * RendementService rendementService = new RendementService();
 * Rendement rendement = new Rendement(surface, qte, productivite, idRecolte);
 * rendementService.add(rendement);
 *
 * // Récupérer tous les rendements d'une récolte
 * List<Rendement> rendements = rendementService.getByIdRecolte(idRecolte);
 *
 * // Calculer la productivité moyenne
 * double moyenne = rendementService.getProductiviteMoyenne(idRecolte);
 */

