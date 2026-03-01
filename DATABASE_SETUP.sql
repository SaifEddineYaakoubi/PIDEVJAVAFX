-- Script SQL pour créer les tables requises pour PIDEV Smart Farm
-- Exécutez ce script dans votre base de données MySQL

-- Créer la base de données
CREATE DATABASE IF NOT EXISTS smart_farm;
USE smart_farm;

-- Table Utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id_user INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(50),
    statut BOOLEAN DEFAULT true,
    date_creation DATE
) ENGINE=InnoDB;

-- Table Parcelle
CREATE TABLE IF NOT EXISTS parcelle (
    id_parcelle INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    superficie DOUBLE NOT NULL,
    localisation VARCHAR(200),
    etat VARCHAR(50),
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Culture
CREATE TABLE IF NOT EXISTS culture (
    id_culture INT PRIMARY KEY AUTO_INCREMENT,
    type_culture VARCHAR(100) NOT NULL,
    date_plantation DATE NOT NULL,
    date_recolte_prevue DATE NOT NULL,
    etat_croissance VARCHAR(50),
    id_parcelle INT NOT NULL,
    FOREIGN KEY (id_parcelle) REFERENCES parcelle(id_parcelle) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Client
CREATE TABLE IF NOT EXISTS client (
    id_client INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    contact VARCHAR(100),
    adresse VARCHAR(255)
) ENGINE=InnoDB;

-- Table Vente
CREATE TABLE IF NOT EXISTS vente (
    id_vente INT PRIMARY KEY AUTO_INCREMENT,
    date_vente DATE NOT NULL,
    montant_total DOUBLE NOT NULL,
    id_client INT NOT NULL,
    id_user INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Produit
CREATE TABLE IF NOT EXISTS produit (
    id_produit INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(150) NOT NULL,
    prix DOUBLE,
    quantite_stock INT,
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Ligne de Vente
CREATE TABLE IF NOT EXISTS ligne_vente (
    id_ligne INT PRIMARY KEY AUTO_INCREMENT,
    quantite DOUBLE NOT NULL,
    prix DOUBLE NOT NULL,
    id_vente INT NOT NULL,
    id_produit INT NOT NULL,
    FOREIGN KEY (id_vente) REFERENCES vente(id_vente) ON DELETE CASCADE,
    FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Récolte
CREATE TABLE IF NOT EXISTS recolte (
    id_recolte INT PRIMARY KEY AUTO_INCREMENT,
    date_recolte DATE,
    quantite DOUBLE,
    id_culture INT NOT NULL,
    FOREIGN KEY (id_culture) REFERENCES culture(id_culture) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Alerte
CREATE TABLE IF NOT EXISTS alerte (
    id_alerte INT PRIMARY KEY AUTO_INCREMENT,
    type_alerte VARCHAR(100),
    description VARCHAR(500),
    date_creation DATE,
    id_culture INT,
    FOREIGN KEY (id_culture) REFERENCES culture(id_culture) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table Stock
CREATE TABLE IF NOT EXISTS stock (
    id_stock INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100),
    quantite DOUBLE,
    localisation VARCHAR(150),
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Index pour optimiser les requêtes
CREATE INDEX idx_client_nom ON client(nom);
CREATE INDEX idx_vente_date ON vente(date_vente);
CREATE INDEX idx_vente_client ON vente(id_client);
CREATE INDEX idx_vente_user ON vente(id_user);
CREATE INDEX idx_culture_parcelle ON culture(id_parcelle);
CREATE INDEX idx_parcelle_user ON parcelle(id_user);

-- Confirmez la création
SELECT COUNT(*) as tables_created FROM information_schema.tables WHERE table_schema = 'smart_farm';

