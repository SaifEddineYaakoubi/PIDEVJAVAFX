-- ============================================================
-- Migration: Isolation des données par utilisateur (multi-tenant)
-- Chaque agriculteur ne voit que ses propres données
-- Chaque responsable stock est rattaché à un agriculteur
-- ============================================================

USE smartfarm;

-- 1. Ajouter id_user à la table recolte
ALTER TABLE recolte ADD COLUMN id_user INT DEFAULT NULL;
ALTER TABLE recolte ADD CONSTRAINT fk_recolte_user FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- 2. Ajouter id_user à la table produit
ALTER TABLE produit ADD COLUMN id_user INT DEFAULT NULL;
ALTER TABLE produit ADD CONSTRAINT fk_produit_user FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- 3. Ajouter id_user à la table stock
ALTER TABLE stock ADD COLUMN id_user INT DEFAULT NULL;
ALTER TABLE stock ADD CONSTRAINT fk_stock_user FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- 4. Ajouter id_user à la table client
ALTER TABLE client ADD COLUMN id_user INT DEFAULT NULL;
ALTER TABLE client ADD CONSTRAINT fk_client_user FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- 5. Ajouter id_user à la table recolte_archive
ALTER TABLE recolte_archive ADD COLUMN id_user INT DEFAULT NULL;
ALTER TABLE recolte_archive ADD CONSTRAINT fk_archive_user FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- 6. Ajouter id_agriculteur à utilisateur (pour lier RESPONSABLE_STOCK à un AGRICULTEUR)
ALTER TABLE utilisateur ADD COLUMN id_agriculteur INT DEFAULT NULL;
ALTER TABLE utilisateur ADD CONSTRAINT fk_user_agriculteur FOREIGN KEY (id_agriculteur) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- ============================================================
-- Mise à jour des données existantes pour affecter un id_user
-- ============================================================

-- Récoltes: affecter aux agriculteurs selon la localisation/parcelle
UPDATE recolte SET id_user = 2 WHERE localisation LIKE '%Mornag%' OR localisation LIKE '%Béja%';
UPDATE recolte SET id_user = 3 WHERE localisation LIKE '%Sfax%' OR localisation LIKE '%Hammamet%';
UPDATE recolte SET id_user = 4 WHERE localisation LIKE '%Tabarka%' OR localisation LIKE '%Kélibia%';
UPDATE recolte SET id_user = 6 WHERE localisation LIKE '%Sidi Bouzid%' OR localisation LIKE '%Tozeur%';
UPDATE recolte SET id_user = 8 WHERE localisation LIKE '%Kairouan%' OR localisation LIKE '%Grombalia%';
UPDATE recolte SET id_user = 9 WHERE localisation LIKE '%Bizerte%' OR localisation LIKE '%Menzel%';
UPDATE recolte SET id_user = 10 WHERE localisation LIKE '%Kasserine%' OR localisation LIKE '%Siliana%';

-- Produits: répartir entre les agriculteurs
UPDATE produit SET id_user = 2 WHERE id_produit IN (1, 3, 4, 19, 20);
UPDATE produit SET id_user = 3 WHERE id_produit IN (2, 6, 7);
UPDATE produit SET id_user = 4 WHERE id_produit IN (8, 15);
UPDATE produit SET id_user = 6 WHERE id_produit IN (5, 9, 10);
UPDATE produit SET id_user = 8 WHERE id_produit IN (11, 14, 16, 18);
UPDATE produit SET id_user = 9 WHERE id_produit IN (12, 13, 17);
UPDATE produit SET id_user = 10 WHERE id_produit NOT IN (1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18);

-- Stocks: affecter id_user selon le produit associé
UPDATE stock s JOIN produit p ON s.id_produit = p.id_produit SET s.id_user = p.id_user;

-- Clients: affecter selon les ventes existantes
UPDATE client c
JOIN (SELECT DISTINCT id_client, id_user FROM vente) v ON c.id_client = v.id_client
SET c.id_user = v.id_user;
-- Les clients sans vente → affecter à user 2 par défaut
UPDATE client SET id_user = 2 WHERE id_user IS NULL;

-- Archives: affecter à user 2 par défaut (données historiques)
UPDATE recolte_archive SET id_user = 2;

-- Lier les responsables stock aux agriculteurs
-- Sana (user 5) gère le stock de Yassine (user 2) et Fatma (user 3)
UPDATE utilisateur SET id_agriculteur = 2 WHERE id_user = 5;
-- Nour (user 7) gère le stock de Karim (user 4) et Ahmed (user 6)
UPDATE utilisateur SET id_agriculteur = 4 WHERE id_user = 7;

SELECT '✅ Migration multi-tenant terminée' AS status;

