-- Migration: Agrandir la colonne mot_de_passe pour supporter les hash BCrypt (60 caractères minimum)
-- Exécuter cette requête UNE SEULE FOIS sur la base de données smartfarm

ALTER TABLE utilisateur MODIFY COLUMN mot_de_passe VARCHAR(255) NOT NULL;

-- Vérification:
-- SELECT id_user, email, LENGTH(mot_de_passe) as mdp_length,
--        CASE WHEN mot_de_passe LIKE '$2%' THEN 'HACHÉ' ELSE 'CLAIR' END as statut_mdp
-- FROM utilisateur;

