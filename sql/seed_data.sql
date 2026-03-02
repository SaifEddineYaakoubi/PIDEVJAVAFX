-- ============================================================
-- SMART FARM - Données réalistes pour la base de données
-- Base: smartfarm | Charset: utf8mb4
-- Contexte: Exploitation agricole en Tunisie
-- ============================================================

SET FOREIGN_KEY_CHECKS = 0;
SET NAMES utf8mb4;

-- ============================================================
-- 1. STRUCTURE DES TABLES (CREATE IF NOT EXISTS)
-- ============================================================

CREATE DATABASE IF NOT EXISTS smartfarm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE smartfarm;

-- Table utilisateur
CREATE TABLE IF NOT EXISTS utilisateur (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) DEFAULT '',
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role ENUM('ADMIN','AGRICULTEUR','RESPONSABLE_STOCK') NOT NULL DEFAULT 'AGRICULTEUR',
    statut BOOLEAN NOT NULL DEFAULT TRUE,
    date_creation DATE NOT NULL,
    face_image_path VARCHAR(255) DEFAULT NULL,
    id_agriculteur INT DEFAULT NULL
) ENGINE=InnoDB;

-- Table parcelle
CREATE TABLE IF NOT EXISTS parcelle (
    id_parcelle INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    superficie DOUBLE NOT NULL,
    localisation VARCHAR(255) NOT NULL,
    etat VARCHAR(50) NOT NULL,
    id_user INT NOT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table culture
CREATE TABLE IF NOT EXISTS culture (
    id_culture INT AUTO_INCREMENT PRIMARY KEY,
    type_culture VARCHAR(100) NOT NULL,
    date_plantation DATE NOT NULL,
    date_recolte_prevue DATE,
    etat_croissance VARCHAR(50) NOT NULL,
    id_parcelle INT NOT NULL,
    FOREIGN KEY (id_parcelle) REFERENCES parcelle(id_parcelle) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table recolte
CREATE TABLE IF NOT EXISTS recolte (
    id_recolte INT AUTO_INCREMENT PRIMARY KEY,
    quantite DOUBLE NOT NULL,
    date_recolte DATE NOT NULL,
    qualite VARCHAR(50) NOT NULL,
    type_culture VARCHAR(100) NOT NULL,
    localisation VARCHAR(255) NOT NULL,
    id_user INT DEFAULT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table rendement
CREATE TABLE IF NOT EXISTS rendement (
    id_rendement INT AUTO_INCREMENT PRIMARY KEY,
    surface_exploitee DOUBLE NOT NULL,
    quantite_totale DOUBLE NOT NULL,
    productivite DOUBLE NOT NULL,
    id_recolte INT NOT NULL,
    FOREIGN KEY (id_recolte) REFERENCES recolte(id_recolte) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table recolte_archive
CREATE TABLE IF NOT EXISTS recolte_archive (
    id_archive INT AUTO_INCREMENT PRIMARY KEY,
    id_recolte_original INT NOT NULL,
    quantite DOUBLE NOT NULL,
    date_recolte DATE NOT NULL,
    qualite VARCHAR(50) NOT NULL,
    type_culture VARCHAR(100) NOT NULL,
    localisation VARCHAR(255) NOT NULL,
    cause_supression VARCHAR(255) NOT NULL,
    date_archivage DATE NOT NULL,
    id_user INT DEFAULT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table produit
CREATE TABLE IF NOT EXISTS produit (
    id_produit INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    unite VARCHAR(30) NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    id_user INT DEFAULT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table stock
CREATE TABLE IF NOT EXISTS stock (
    id_stock INT AUTO_INCREMENT PRIMARY KEY,
    quantite DOUBLE NOT NULL,
    date_entree DATE NOT NULL,
    date_expiration DATE,
    id_produit INT NOT NULL,
    id_user INT DEFAULT NULL,
    FOREIGN KEY (id_produit) REFERENCES produit(id_produit) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table client
CREATE TABLE IF NOT EXISTS client (
    id_client INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    contact VARCHAR(150),
    adresse VARCHAR(255),
    id_user INT DEFAULT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE SET NULL
) ENGINE=InnoDB;

-- Table vente
CREATE TABLE IF NOT EXISTS vente (
    id_vente INT AUTO_INCREMENT PRIMARY KEY,
    date_vente DATE NOT NULL,
    montant_total DOUBLE NOT NULL,
    id_client INT NOT NULL,
    id_user INT NOT NULL,
    FOREIGN KEY (id_client) REFERENCES client(id_client) ON DELETE CASCADE,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Table badge
CREATE TABLE IF NOT EXISTS badge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    niveau VARCHAR(50) DEFAULT 'BRONZE'
) ENGINE=InnoDB;

-- Table utilisateur_badge
CREATE TABLE IF NOT EXISTS utilisateur_badge (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT NOT NULL,
    id_badge INT NOT NULL,
    date_attribution DATE NOT NULL,
    FOREIGN KEY (id_user) REFERENCES utilisateur(id_user) ON DELETE CASCADE,
    FOREIGN KEY (id_badge) REFERENCES badge(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- Agrandir mot_de_passe pour BCrypt
ALTER TABLE utilisateur MODIFY COLUMN mot_de_passe VARCHAR(255) NOT NULL;

-- ============================================================
-- 2. NETTOYAGE DES DONNÉES EXISTANTES
-- ============================================================

DELETE FROM utilisateur_badge;
DELETE FROM vente;
DELETE FROM stock;
DELETE FROM rendement;
DELETE FROM recolte_archive;
DELETE FROM recolte;
DELETE FROM culture;
DELETE FROM parcelle;
DELETE FROM client;
DELETE FROM produit;
DELETE FROM badge;
DELETE FROM utilisateur;

-- Reset auto-increment
ALTER TABLE utilisateur AUTO_INCREMENT = 1;
ALTER TABLE parcelle AUTO_INCREMENT = 1;
ALTER TABLE culture AUTO_INCREMENT = 1;
ALTER TABLE recolte AUTO_INCREMENT = 1;
ALTER TABLE rendement AUTO_INCREMENT = 1;
ALTER TABLE recolte_archive AUTO_INCREMENT = 1;
ALTER TABLE produit AUTO_INCREMENT = 1;
ALTER TABLE stock AUTO_INCREMENT = 1;
ALTER TABLE client AUTO_INCREMENT = 1;
ALTER TABLE vente AUTO_INCREMENT = 1;
ALTER TABLE badge AUTO_INCREMENT = 1;
ALTER TABLE utilisateur_badge AUTO_INCREMENT = 1;

-- ============================================================
-- 3. UTILISATEURS (mdp en clair pour le seed — seront hachés au 1er login)
-- ============================================================

INSERT INTO utilisateur (nom, prenom, email, mot_de_passe, role, statut, date_creation) VALUES
('Ben Ahmed',    'Mohamed',  'admin@smartfarm.tn',       'admin123',       'ADMIN',              TRUE, '2025-01-10'),
('Trabelsi',     'Yassine',  'yassine.trabelsi@mail.tn', 'yassine123',     'AGRICULTEUR',        TRUE, '2025-02-15'),
('Bouzid',       'Fatma',    'fatma.bouzid@mail.tn',     'fatma123',       'AGRICULTEUR',        TRUE, '2025-03-01'),
('Gharbi',       'Karim',    'karim.gharbi@mail.tn',     'karim123',       'AGRICULTEUR',        TRUE, '2025-03-20'),
('Maatoug',      'Sana',     'sana.maatoug@mail.tn',     'sana123',        'RESPONSABLE_STOCK',  TRUE, '2025-04-05'),
('Jebali',       'Ahmed',    'ahmed.jebali@mail.tn',     'ahmed123',       'AGRICULTEUR',        TRUE, '2025-04-18'),
('Hamdi',        'Nour',     'nour.hamdi@mail.tn',       'nour123',        'RESPONSABLE_STOCK',  TRUE, '2025-05-10'),
('Saifi',        'Amine',    'amine.saifi@mail.tn',      'amine123',       'AGRICULTEUR',        TRUE, '2025-06-01'),
('Khlifi',       'Rim',      'rim.khlifi@mail.tn',       'rim123',         'AGRICULTEUR',        TRUE, '2025-06-25'),
('Ben Slimane',  'Omar',     'omar.benslimane@mail.tn',  'omar123',        'AGRICULTEUR',        TRUE, '2025-07-10');

-- Ajouter la FK id_agriculteur après insertion des utilisateurs
ALTER TABLE utilisateur ADD CONSTRAINT fk_user_agriculteur FOREIGN KEY (id_agriculteur) REFERENCES utilisateur(id_user) ON DELETE SET NULL;

-- Lier les responsables stock aux agriculteurs
-- Sana (user 5) gère le stock de Yassine (user 2)
UPDATE utilisateur SET id_agriculteur = 2 WHERE id_user = 5;
-- Nour (user 7) gère le stock de Karim (user 4)
UPDATE utilisateur SET id_agriculteur = 4 WHERE id_user = 7;

-- ============================================================
-- 4. PARCELLES (localisations réelles en Tunisie)
-- ============================================================

INSERT INTO parcelle (nom, superficie, localisation, etat, id_user) VALUES
('Domaine El Ferdaous',    12.5,  'Mornag, Ben Arous',          'Active',     2),
('Champ Nord Béja',        25.0,  'Béja Nord, Béja',            'Active',     2),
('Oliveraie Sfax',          8.3,  'Sfax Sud, Sfax',             'Active',     3),
('Verger Nabeul',           5.0,  'Hammamet, Nabeul',           'Active',     3),
('Parcelle Jendouba',      18.7,  'Tabarka, Jendouba',          'Active',     4),
('Terre Cap Bon',          15.2,  'Kélibia, Nabeul',            'Active',     4),
('Champ Sidi Bouzid',      30.0,  'Sidi Bouzid Centre',         'Active',     6),
('Prairie Zaghouan',        9.8,  'Zaghouan Ville, Zaghouan',   'En repos',   6),
('Exploitation Kairouan',  22.0,  'Kairouan Sud, Kairouan',     'Active',     8),
('Vignoble Grombalia',      6.5,  'Grombalia, Nabeul',          'Active',     8),
('Domaine Bizerte',        14.0,  'Menzel Bourguiba, Bizerte',  'Active',     9),
('Palmeraie Tozeur',       11.0,  'Tozeur Centre, Tozeur',      'Active',     9),
('Champ Siliana',          20.0,  'Siliana Centre, Siliana',     'En repos',  10),
('Parcelle Kasserine',     16.5,  'Kasserine Nord, Kasserine',  'Active',    10);

-- ============================================================
-- 5. CULTURES (cultures typiques tunisiennes)
-- ============================================================

INSERT INTO culture (type_culture, date_plantation, date_recolte_prevue, etat_croissance, id_parcelle) VALUES
-- Céréales
('Blé dur',             '2025-11-15', '2026-06-20', 'En croissance',  1),
('Orge',                '2025-11-20', '2026-05-25', 'En croissance',  2),
('Blé tendre',          '2025-12-01', '2026-06-15', 'Germination',    5),
-- Oliviers
('Olivier',             '2023-03-10', '2025-11-15', 'Mature',         3),
('Olivier Chemlali',    '2022-02-20', '2025-12-01', 'Mature',         12),
-- Agrumes
('Oranges Maltaises',   '2024-01-15', '2026-02-28', 'Floraison',      4),
('Citrons Beldi',       '2024-03-10', '2026-03-15', 'Fructification', 6),
('Clémentines',         '2024-04-05', '2026-01-20', 'Fructification', 10),
-- Maraîchage
('Tomates',             '2026-01-10', '2026-05-15', 'En croissance',  7),
('Piments',             '2026-01-20', '2026-06-01', 'Germination',    7),
('Pommes de terre',     '2025-12-15', '2026-04-10', 'En croissance',  9),
('Oignons',             '2025-11-01', '2026-04-20', 'En croissance',  13),
-- Dattes
('Deglet Nour',         '2020-05-01', '2025-10-15', 'Mature',         12),
-- Vignes
('Muscat de Tunisie',   '2023-02-15', '2026-08-20', 'En croissance',  10),
-- Légumineuses
('Fèves',               '2025-10-25', '2026-03-30', 'Floraison',      11),
('Pois chiches',        '2025-11-10', '2026-05-10', 'En croissance',  14),
-- Arboriculture
('Amandier',            '2022-01-20', '2026-08-15', 'Floraison',      8),
('Grenadier',           '2023-03-25', '2026-09-10', 'En croissance',  14);

-- ============================================================
-- 6. RÉCOLTES (données historiques réalistes — kg)
-- ============================================================

INSERT INTO recolte (quantite, date_recolte, qualite, type_culture, localisation, id_user) VALUES
-- Saison 2025
(4500.0,  '2025-06-20', 'Excellente',  'Blé dur',            'Mornag, Ben Arous',       2),
(8200.0,  '2025-06-15', 'Bonne',       'Orge',               'Béja Nord, Béja',         2),
(3100.0,  '2025-11-20', 'Excellente',  'Olivier',            'Sfax Sud, Sfax',          3),
(2800.0,  '2025-02-10', 'Bonne',       'Oranges Maltaises',  'Hammamet, Nabeul',        3),
(6500.0,  '2025-05-20', 'Moyenne',     'Tomates',            'Sidi Bouzid Centre',      6),
(1200.0,  '2025-10-20', 'Excellente',  'Deglet Nour',        'Tozeur Centre, Tozeur',   9),
(3800.0,  '2025-06-10', 'Bonne',       'Blé tendre',         'Tabarka, Jendouba',       4),
(1950.0,  '2025-03-05', 'Bonne',       'Citrons Beldi',      'Kélibia, Nabeul',         4),
(4200.0,  '2025-04-15', 'Excellente',  'Pommes de terre',    'Kairouan Sud, Kairouan',  8),
(2600.0,  '2025-05-10', 'Moyenne',     'Pois chiches',       'Kasserine Nord, Kasserine', 10),
-- Saison 2026 (début)
(1500.0,  '2026-01-25', 'Excellente',  'Clémentines',        'Grombalia, Nabeul',       8),
(800.0,   '2026-02-15', 'Bonne',       'Oranges Maltaises',  'Hammamet, Nabeul',        3),
(2100.0,  '2026-02-28', 'Bonne',       'Fèves',              'Menzel Bourguiba, Bizerte', 9);

-- ============================================================
-- 7. RENDEMENTS (liés aux récoltes)
-- ============================================================

INSERT INTO rendement (surface_exploitee, quantite_totale, productivite, id_recolte) VALUES
(12.5, 4500.0,  360.0,  1),   -- Blé dur: 360 kg/ha
(25.0, 8200.0,  328.0,  2),   -- Orge: 328 kg/ha
( 8.3, 3100.0,  373.5,  3),   -- Olives: 373 kg/ha
( 5.0, 2800.0,  560.0,  4),   -- Oranges: 560 kg/ha
(15.0, 6500.0,  433.3,  5),   -- Tomates: 433 kg/ha
(11.0, 1200.0,  109.1,  6),   -- Dattes: 109 kg/ha
(18.7, 3800.0,  203.2,  7),   -- Blé tendre: 203 kg/ha
(15.2, 1950.0,  128.3,  8),   -- Citrons: 128 kg/ha
(22.0, 4200.0,  190.9,  9),   -- Pommes de terre: 191 kg/ha
(16.5, 2600.0,  157.6,  10),  -- Pois chiches: 158 kg/ha
( 6.5, 1500.0,  230.8,  11),  -- Clémentines: 231 kg/ha
( 5.0,  800.0,  160.0,  12),  -- Oranges 2026: 160 kg/ha
(14.0, 2100.0,  150.0,  13);  -- Fèves: 150 kg/ha

-- ============================================================
-- 8. ARCHIVES RÉCOLTES (anciennes récoltes archivées)
-- ============================================================

INSERT INTO recolte_archive (id_recolte_original, quantite, date_recolte, qualite, type_culture, localisation, cause_supression, date_archivage, id_user) VALUES
(100, 3200.0, '2024-06-18', 'Moyenne',    'Blé dur',     'Mornag, Ben Arous',       'Fin de saison — données consolidées',     '2024-12-31', 2),
(101, 5800.0, '2024-06-10', 'Bonne',      'Orge',        'Béja Nord, Béja',         'Clôture exercice annuel 2024',            '2024-12-31', 2),
(102, 2500.0, '2024-11-15', 'Excellente', 'Olivier',     'Sfax Sud, Sfax',          'Archivage saison oléicole 2024',          '2025-01-15', 3),
(103, 4800.0, '2024-05-20', 'Moyenne',    'Tomates',     'Sidi Bouzid Centre',      'Récolte abîmée partiellement — archivée', '2024-07-01', 6),
(104,  900.0, '2024-10-10', 'Bonne',      'Deglet Nour', 'Tozeur Centre, Tozeur',   'Saison terminée',                         '2025-01-10', 9);

-- ============================================================
-- 9. PRODUITS (produits agricoles tunisiens)
-- ============================================================

INSERT INTO produit (nom, type, unite, prix_unitaire, id_user) VALUES
('Huile d\'olive extra vierge',  'Huile',        'Litre',     18.50, 2),
('Huile d\'olive courante',      'Huile',        'Litre',     12.00, 3),
('Blé dur',                      'Céréale',      'Kg',         0.85, 2),
('Orge',                         'Céréale',      'Kg',         0.65, 2),
('Dattes Deglet Nour',           'Fruit sec',    'Kg',        15.00, 6),
('Oranges Maltaises',            'Agrume',       'Kg',         2.50, 3),
('Citrons Beldi',                'Agrume',       'Kg',         3.00, 4),
('Clémentines',                  'Agrume',       'Kg',         2.80, 8),
('Tomates fraîches',             'Légume',       'Kg',         1.80, 6),
('Piments forts',                'Légume',       'Kg',         4.50, 6),
('Pommes de terre',              'Légume',       'Kg',         1.20, 8),
('Oignons',                      'Légume',       'Kg',         1.00, 9),
('Fèves fraîches',               'Légumineuse',  'Kg',         2.20, 9),
('Pois chiches secs',            'Légumineuse',  'Kg',         5.50, 10),
('Harissa traditionnelle',       'Condiment',    'Kg',         8.00, 4),
('Amandes décortiquées',         'Fruit sec',    'Kg',        32.00, 8),
('Grenades',                     'Fruit',        'Kg',         3.50, 9),
('Vin Muscat de Tunisie',        'Boisson',      'Bouteille', 22.00, 8),
('Engrais NPK 15-15-15',        'Intrant',      'Kg',         1.80, 2),
('Semences blé certifiées',      'Intrant',      'Kg',         2.50, 2);

-- ============================================================
-- 10. STOCKS (entrées réalistes)
-- ============================================================

INSERT INTO stock (quantite, date_entree, date_expiration, id_produit, id_user) VALUES
-- Huiles
(520.0,   '2025-12-10', '2027-12-10', 1,  2),
(800.0,   '2025-12-15', '2027-06-15', 2,  3),
-- Céréales
(4200.0,  '2025-06-25', '2026-06-25', 3,  2),
(3500.0,  '2025-06-20', '2026-06-20', 4,  2),
-- Dattes
(950.0,   '2025-10-25', '2026-10-25', 5,  6),
-- Agrumes
(1200.0,  '2026-02-01', '2026-03-15', 6,  3),
(680.0,   '2026-01-20', '2026-03-01', 7,  4),
(450.0,   '2026-01-25', '2026-02-28', 8,  8),
-- Légumes
(2800.0,  '2025-05-25', '2025-06-15', 9,  6),
(350.0,   '2025-06-05', '2025-07-05', 10, 6),
(3100.0,  '2026-01-10', '2026-04-10', 11, 8),
(1800.0,  '2025-11-15', '2026-03-15', 12, 9),
-- Légumineuses
(900.0,   '2026-02-28', '2026-04-15', 13, 9),
(1500.0,  '2025-05-15', '2026-05-15', 14, 10),
-- Condiments
(200.0,   '2025-09-01', '2026-09-01', 15, 4),
-- Fruits secs
(180.0,   '2025-08-20', '2026-08-20', 16, 8),
(600.0,   '2025-09-15', '2026-01-15', 17, 9),
-- Vin
(300.0,   '2025-08-25', '2028-08-25', 18, 8),
-- Intrants
(5000.0,  '2025-10-01', '2027-10-01', 19, 2),
(2000.0,  '2025-10-15', '2026-10-15', 20, 2);

-- ============================================================
-- 11. CLIENTS (clients tunisiens réalistes)
-- ============================================================

INSERT INTO client (nom, contact, adresse, id_user) VALUES
('Bouazizi Mehdi',       'mehdi.bouazizi@gmail.com',      '12 Rue de la Liberté, Tunis',                    2),
('Mansouri Leila',       'leila.mansouri@yahoo.fr',       '45 Avenue Habib Bourguiba, Sousse',              2),
('Chaabane Walid',       'walid.chaabane@hotmail.com',    '8 Rue Ibn Khaldoun, Sfax',                       3),
('Ferchichi Amira',      'amira.ferchichi@mail.tn',       '23 Rue de France, Bizerte',                      4),
('Dridi Sofiane',        'sofiane.dridi@gmail.com',       'Route de Gafsa Km5, Kasserine',                  6),
('Haddad Ines',          'ines.haddad@outlook.com',       '56 Avenue Mohamed V, Tunis',                     2),
('Ben Romdhane Nabil',   'nabil.benromdhane@mail.tn',     '3 Rue de l\'Indépendance, Nabeul',               8),
('Mejri Sarra',          'sarra.mejri@gmail.com',         '17 Boulevard de l\'Environnement, Gabès',        6),
('Karray Bilel',         'bilel.karray@yahoo.fr',         '90 Avenue Farhat Hached, Sfax',                  3),
('Ayari Hana',           'hana.ayari@mail.tn',            '5 Place de la République, Tozeur',                4),
('Sassi Mohamed',        'mohamed.sassi@gmail.com',       '28 Rue de Marseille, Tunis',                     8),
('Brahmi Sonia',         'sonia.brahmi@hotmail.com',      '14 Avenue du 1er Juin, Sousse',                  4),
('Chaari Youssef',       'youssef.chaari@mail.tn',        '61 Rue de Carthage, Kairouan',                   6),
('Hammami Rania',        'rania.hammami@gmail.com',       '33 Avenue de la Liberté, Tunis',                 2),
('Mbarki Tarek',         'tarek.mbarki@yahoo.fr',         '7 Rue de Jérusalem, Nabeul',                     8);

-- ============================================================
-- 12. VENTES (transactions réalistes en DT — Dinar Tunisien)
-- ============================================================

INSERT INTO vente (date_vente, montant_total, id_client, id_user) VALUES
-- Ventes de Yassine (user 2 — agriculteur)
('2025-07-10',  2500.00,  1,  2),
('2025-08-22',  1800.00,  3,  2),
('2025-09-15',  3200.00,  6,  2),
('2025-10-05',   950.00,  4,  2),
('2025-11-20',  4500.00,  9,  2),
('2025-12-10',  1200.00,  2,  2),
('2026-01-08',  2800.00, 14,  2),
('2026-02-14',  1650.00,  7,  2),
-- Ventes de Fatma (user 3 — agricultrice)
('2025-07-25',  5200.00,  3,  3),
('2025-09-01',  3100.00,  1,  3),
('2025-10-18',  1750.00,  5,  3),
('2025-12-02',  8500.00,  9,  3),
('2026-01-20',  2200.00, 11,  3),
('2026-02-10',  4100.00, 14,  3),
-- Ventes de Karim (user 4)
('2025-08-05',  1900.00,  2,  4),
('2025-09-28',  6700.00,  6,  4),
('2025-11-12',  2300.00, 10,  4),
('2026-01-15',  3500.00, 12,  4),
('2026-02-22',  1100.00,  8,  4),
-- Ventes d'Ahmed (user 6)
('2025-08-30',  4200.00,  1,  6),
('2025-10-14',  2800.00,  5,  6),
('2025-12-20',  9200.00, 14,  6),
('2026-02-01',  1500.00, 13,  6),
-- Ventes d'Amine (user 8)
('2025-09-10',  3600.00,  7,  8),
('2025-11-05',  5100.00, 11,  8),
('2025-12-28',  2400.00, 15,  8),
('2026-01-30',  7800.00,  9,  8),
('2026-02-25',  1850.00,  4,  8);

-- ============================================================
-- 13. BADGES
-- ============================================================

INSERT INTO badge (nom, description, niveau) VALUES
('Première Récolte',       'Attribué après la première récolte enregistrée',       'BRONZE'),
('Cultivateur Expert',     'Plus de 10 cultures gérées avec succès',               'ARGENT'),
('Maître des Olives',      'Spécialiste de la culture oléicole',                   'OR'),
('Champion du Rendement',  'Meilleur rendement de la saison',                      'OR'),
('Gardien du Stock',       'Gestion exemplaire du stock pendant 6 mois',           'ARGENT'),
('Vendeur Étoile',         'Plus de 50 000 DT de ventes réalisées',                'OR'),
('Pionnier Digital',       'Premier à utiliser l\'assistant IA agricole',           'BRONZE'),
('Ambassadeur Vert',       'Pratiques agricoles durables certifiées',              'PLATINE');

-- Attribution de quelques badges
INSERT INTO utilisateur_badge (id_user, id_badge, date_attribution) VALUES
(2, 1, '2025-07-01'),
(2, 2, '2025-10-15'),
(2, 6, '2026-01-20'),
(3, 1, '2025-08-10'),
(3, 3, '2025-12-01'),
(4, 1, '2025-09-05'),
(4, 7, '2025-11-20'),
(6, 1, '2025-09-01'),
(6, 4, '2025-12-15'),
(8, 1, '2025-10-01'),
(8, 2, '2026-01-10'),
(5, 5, '2025-11-01'),
(7, 5, '2025-12-20'),
(9, 1, '2025-07-15');

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- VÉRIFICATION
-- ============================================================

SELECT '✅ Utilisateurs'     AS entite, COUNT(*) AS total FROM utilisateur
UNION ALL
SELECT '✅ Parcelles',       COUNT(*) FROM parcelle
UNION ALL
SELECT '✅ Cultures',        COUNT(*) FROM culture
UNION ALL
SELECT '✅ Récoltes',        COUNT(*) FROM recolte
UNION ALL
SELECT '✅ Rendements',      COUNT(*) FROM rendement
UNION ALL
SELECT '✅ Archives',        COUNT(*) FROM recolte_archive
UNION ALL
SELECT '✅ Produits',        COUNT(*) FROM produit
UNION ALL
SELECT '✅ Stocks',          COUNT(*) FROM stock
UNION ALL
SELECT '✅ Clients',         COUNT(*) FROM client
UNION ALL
SELECT '✅ Ventes',          COUNT(*) FROM vente
UNION ALL
SELECT '✅ Badges',          COUNT(*) FROM badge
UNION ALL
SELECT '✅ Badges attribués', COUNT(*) FROM utilisateur_badge;

