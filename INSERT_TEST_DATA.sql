-- ============================================================
-- SCRIPT D'INSERTION DE DONNÉES DE TEST
-- Application: AgriFarm - Gestion des Ventes
-- Date: 15/02/2026
-- ============================================================

USE smart_farm;

-- ============================================================
-- 1. AJOUTER DES UTILISATEURS
-- ============================================================

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role) VALUES
('Ahmed', 'Admin', 'ahmed@agrifarm.com', 'password123', 'admin'),
('Saif', 'Manager', 'saif@agrifarm.com', 'password123', 'manager'),
('Fatima', 'Vendeur', 'fatima@agrifarm.com', 'password123', 'vendeur'),
('Karim', 'Vendeur', 'karim@agrifarm.com', 'password123', 'vendeur');

-- ============================================================
-- 2. AJOUTER DES CLIENTS
-- ============================================================

INSERT INTO client (nom, prenom, email, telephone, adresse, ville) VALUES
('Conseil', 'Agronomique', 'contact@conseil.com', '+216 71 234 567', 'Rue de l''Agriculture', 'Tunis'),
('Ferme', 'Al Khaleej', 'info@ferme.com', '+216 72 345 678', 'Route Principale', 'Sfax'),
('Coop', 'Agricole', 'coop@agricole.com', '+216 73 456 789', 'Avenue du Commerce', 'Sousse'),
('Société', 'Générale', 'sg@general.com', '+216 74 567 890', 'Boulevard Central', 'Monastir'),
('Entreprise', 'Agro', 'agro@entreprise.com', '+216 75 678 901', 'Chemin de la Ferme', 'Kairouan');

-- ============================================================
-- 3. AJOUTER DES PARCELLES
-- ============================================================

INSERT INTO parcelle (superficie, localisation, id_utilisateur) VALUES
(5.5, 'Nord - Secteur A', 1),
(3.2, 'Sud - Secteur B', 2),
(8.0, 'Est - Secteur C', 3),
(4.5, 'Ouest - Secteur D', 4);

-- ============================================================
-- 4. AJOUTER DES CULTURES
-- ============================================================

INSERT INTO culture (nom_culture, type, date_plantation) VALUES
('Blé', 'Céréale', '2025-10-15'),
('Orge', 'Céréale', '2025-10-20'),
('Maïs', 'Céréale', '2025-04-01'),
('Tomate', 'Légume', '2025-03-15'),
('Pomme de terre', 'Légume', '2025-02-01');

-- ============================================================
-- 5. AJOUTER DES RÉCOLTES
-- ============================================================

INSERT INTO recolte (date_recolte, quantite, id_parcelle, id_culture) VALUES
('2026-07-15', 1200.50, 1, 1),
('2026-08-01', 950.00, 2, 2),
('2026-09-10', 2100.75, 3, 3),
('2026-05-20', 850.25, 4, 4);

-- ============================================================
-- 6. AJOUTER DES PRODUITS
-- ============================================================

INSERT INTO produit (nom_produit, description, prix_unitaire, quantite_stock) VALUES
('Blé Premium', 'Blé de haute qualité', 1.50, 5000),
('Orge Standard', 'Orge pour alimentation animale', 1.20, 3000),
('Maïs Sucré', 'Maïs doux de première qualité', 2.00, 2000),
('Tomate Bio', 'Tomates cultivées biologiquement', 3.50, 500),
('Pommes de terre', 'Pommes de terre fraîches', 0.80, 1500);

-- ============================================================
-- 7. AJOUTER DES VENTES (EXEMPLES)
-- ============================================================

INSERT INTO vente (date_vente, montant_total, id_client, id_user) VALUES
('2026-02-10', 1500.00, 1, 1),
('2026-02-11', 2500.50, 2, 2),
('2026-02-12', 1200.75, 3, 3),
('2026-02-13', 3000.00, 4, 1),
('2026-02-14', 1750.25, 5, 2);

-- ============================================================
-- VÉRIFICATION
-- ============================================================

SELECT 'UTILISATEURS:' as Info;
SELECT COUNT(*) as Total FROM utilisateur;

SELECT 'CLIENTS:' as Info;
SELECT COUNT(*) as Total FROM client;

SELECT 'PARCELLES:' as Info;
SELECT COUNT(*) as Total FROM parcelle;

SELECT 'CULTURES:' as Info;
SELECT COUNT(*) as Total FROM culture;

SELECT 'RÉCOLTES:' as Info;
SELECT COUNT(*) as Total FROM recolte;

SELECT 'PRODUITS:' as Info;
SELECT COUNT(*) as Total FROM produit;

SELECT 'VENTES:' as Info;
SELECT COUNT(*) as Total FROM vente;

-- ============================================================
-- AFFICHER UN RÉSUMÉ
-- ============================================================

ECHO '========================================================';
ECHO 'Données de test importées avec succès!';
ECHO '========================================================';

SELECT 'Utilisateurs disponibles:' as Info;
SELECT id_utilisateur, nom, prenom, email FROM utilisateur LIMIT 5;

SELECT 'Clients disponibles:' as Info;
SELECT id_client, nom, prenom, email, telephone FROM client LIMIT 5;

SELECT 'Ventes disponibles:' as Info;
SELECT id_vente, date_vente, montant_total FROM vente ORDER BY date_vente DESC LIMIT 5;

