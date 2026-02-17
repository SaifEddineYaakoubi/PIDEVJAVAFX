-- =====================================================
-- SCRIPT SQL - MIGRATION DE LA TABLE RECOLTE
-- =====================================================
--
-- Description: Ajouter les colonnes manquantes à la table recolte
-- qui ont été supprimées lors de la restructuration du projet
-- (suppression de la liaison avec Culture et Parcelle)
--
-- =====================================================

-- Vérifier la structure actuelle (facultatif)
-- DESC recolte;

-- Ajouter les colonnes manquantes si elles n'existent pas
ALTER TABLE recolte
ADD COLUMN IF NOT EXISTS type_culture VARCHAR(100) NOT NULL DEFAULT 'Non spécifié',
ADD COLUMN IF NOT EXISTS localisation VARCHAR(100) NOT NULL DEFAULT 'Non spécifiée';

-- Supprimer la colonne id_culture si elle existe (ancienne liaison)
-- ALTER TABLE recolte DROP COLUMN id_culture;

-- Vérifier la structure mise à jour
-- DESC recolte;

-- =====================================================
-- ALTERNATIVE: Si vous préférez recréer la table complètement
-- =====================================================
--
-- DROP TABLE IF EXISTS recolte;
--
-- CREATE TABLE recolte (
--     id_recolte INT PRIMARY KEY AUTO_INCREMENT,
--     quantite DOUBLE NOT NULL,
--     date_recolte DATE NOT NULL,
--     qualite VARCHAR(100) NOT NULL,
--     type_culture VARCHAR(100) NOT NULL,
--     localisation VARCHAR(100) NOT NULL
-- );
--
-- CREATE TABLE rendement (
--     id_rendement INT PRIMARY KEY AUTO_INCREMENT,
--     surface_exploitee DOUBLE NOT NULL,
--     quantite_totale DOUBLE NOT NULL,
--     productivite DOUBLE NOT NULL,
--     id_recolte INT NOT NULL,
--     FOREIGN KEY (id_recolte) REFERENCES recolte(id_recolte) ON DELETE CASCADE
-- );

-- =====================================================
-- EXÉCUTION
-- =====================================================
--
-- Exécutez ce script dans votre client MySQL:
-- 1. mysql -u root -p -D votre_base < migration_recolte.sql
-- 2. Ou copiez-collez les commandes dans phpMyAdmin / MySQL Workbench
--
-- =====================================================

