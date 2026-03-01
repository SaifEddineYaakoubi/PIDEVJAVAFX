-- ============================================
-- SMART FARM - Script de Création Base de Données
-- ============================================
-- Exécutez ce script avec: mysql -u root < DATABASE_SETUP.sql
-- Ou copiez-collez dans MySQL Workbench / phpMyAdmin

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS smart_farm DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smart_farm;

-- ============================================
-- Table: utilisateur
-- ============================================
CREATE TABLE IF NOT EXISTS utilisateur (
    id_user INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    statut BOOLEAN DEFAULT true,
    date_creation DATE,
    CHARSET utf8mb4
) ENGINE=InnoDB;

-- ============================================
-- Table: parcelle
-- ============================================
CREATE TABLE IF NOT EXISTS parcelle (
    id_parcelle INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    superficie DOUBLE NOT NULL,
    localisation VARCHAR(200),
    etat VARCHAR(50),
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- Table: culture
-- ============================================
CREATE TABLE IF NOT EXISTS culture (
    id_culture INT PRIMARY KEY AUTO_INCREMENT,
    type_culture VARCHAR(100) NOT NULL,
    date_plantation DATE NOT NULL,
    date_recolte_prevue DATE NOT NULL,
    etat_croissance VARCHAR(50),
    id_parcelle INT NOT NULL,
    FOREIGN KEY (id_parcelle) REFERENCES parcelle(id_parcelle) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- Table: client
-- ============================================
CREATE TABLE IF NOT EXISTS client (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    telephone VARCHAR(20),
    adresse VARCHAR(255),
    statut_fidelite VARCHAR(50) DEFAULT 'Standard',
    total_achats DOUBLE DEFAULT 0,
    date_creation DATE DEFAULT CURDATE()
) ENGINE=InnoDB;

-- ============================================
-- Table: vente
-- ============================================
CREATE TABLE IF NOT EXISTS vente (
    id_vente INT PRIMARY KEY AUTO_INCREMENT,
    date_vente DATE NOT NULL,
    montant_total DOUBLE NOT NULL,
    id_client INT NOT NULL,
    id_user INT NOT NULL,
    description_produit VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- Table: remise
-- ============================================
CREATE TABLE IF NOT EXISTS remise (
    id_remise INT PRIMARY KEY AUTO_INCREMENT,
    id_client INT NOT NULL,
    type_remise VARCHAR(50),
    valeur_remise DOUBLE,
    date_creation DATE,
    FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================
-- Données de Test
-- ============================================

-- Insérer des utilisateurs
INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut, date_creation)
VALUES
('Admin', 'SMART FARM', 'admin@smartfarm.com', 'admin123', 'ADMIN', true, CURDATE()),
('Responsable', 'Ventes', 'ventes@smartfarm.com', 'ventes123', 'USER', true, CURDATE());

-- Insérer des clients
INSERT INTO client (nom, prenom, email, telephone, adresse, statut_fidelite, total_achats, date_creation)
VALUES
('Dupont', 'Jean', 'jean.dupont@mail.com', '06 12 34 56 78', '123 Rue de Paris, 75000 Paris', 'Standard', 500.00, CURDATE()),
('Martin', 'Marie', 'marie.martin@mail.com', '06 87 65 43 21', '456 Avenue des Champs, 75008 Paris', 'Fidèle', 2500.00, CURDATE()),
('Bernard', 'Pierre', 'pierre.bernard@mail.com', '06 55 44 33 22', '789 Boulevard Saint-Germain, 75006 Paris', 'VIP', 7500.00, CURDATE());

-- Insérer des ventes
INSERT INTO vente (date_vente, montant_total, id_client, id_user, description_produit)
VALUES
('2026-02-25', 250.00, 1, 1, 'Légumes frais'),
('2026-02-24', 1500.00, 2, 1, 'Fruits biologiques'),
('2026-02-23', 3000.00, 3, 2, 'Panier premium');

-- ============================================
-- Index pour Optimisation
-- ============================================
CREATE INDEX idx_client_email ON client(email);
CREATE INDEX idx_vente_date ON vente(date_vente);
CREATE INDEX idx_vente_client ON vente(id_client);
CREATE INDEX idx_utilisateur_email ON utilisateur(email);

-- ============================================
-- Afficher les tables créées
-- ============================================
SHOW TABLES;
SELECT COUNT(*) as 'Nombre de clients' FROM client;
SELECT COUNT(*) as 'Nombre de ventes' FROM vente;

-- ============================================
-- FIN DU SCRIPT
-- ============================================

