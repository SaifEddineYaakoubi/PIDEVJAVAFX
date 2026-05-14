-- ============================================================================
-- Smart Farm Database Schema Alignment: Java ↔ Symfony
-- Migration script to align Java database with Symfony schema
-- ============================================================================
-- Run this script AFTER backing up your database!
-- This script adds missing columns and creates missing tables to match the
-- Symfony database schema.
-- ============================================================================

-- ============================================================================
-- STEP 1: Backup existing data (optional but recommended)
-- ============================================================================
-- Tables already exist in both databases, we're just adding/modifying columns

-- ============================================================================
-- STEP 2: Add missing columns to UTILISATEUR table
-- ============================================================================
ALTER TABLE `utilisateur` ADD COLUMN IF NOT EXISTS `face_descriptor` LONGTEXT DEFAULT NULL AFTER `date_creation`;
ALTER TABLE `utilisateur` ADD COLUMN IF NOT EXISTS `face_enabled` TINYINT(1) DEFAULT 0 AFTER `face_descriptor`;
ALTER TABLE `utilisateur` ADD COLUMN IF NOT EXISTS `profile_picture` VARCHAR(255) DEFAULT NULL AFTER `face_enabled`;
ALTER TABLE `utilisateur` ADD COLUMN IF NOT EXISTS `date_naissance` DATE DEFAULT NULL AFTER `profile_picture`;
ALTER TABLE `utilisateur` ADD COLUMN IF NOT EXISTS `sexe` VARCHAR(10) DEFAULT NULL AFTER `date_naissance`;

-- Rename old field if it exists for backward compatibility
-- Assuming Java version has `face_image_path` or `faceImagePath`
-- This is optional - commented out. Uncomment if needed.
-- ALTER TABLE `utilisateur` RENAME COLUMN `face_image_path` TO `profile_picture`;

-- ============================================================================
-- STEP 3: Update CLIENT table to match Symfony schema
-- ============================================================================
-- Add missing columns
ALTER TABLE `client` ADD COLUMN IF NOT EXISTS `badge` VARCHAR(20) DEFAULT NULL AFTER `id_user`;

-- Change email/contact field to match Symfony (contact field, not email)
-- The Symfony schema uses 'contact' field for email/phone
-- This is already compatible if Java version uses contact field

-- ============================================================================
-- STEP 4: Add missing columns to VENTE table
-- ============================================================================
ALTER TABLE `vente` ADD COLUMN IF NOT EXISTS `quantite` DOUBLE DEFAULT NULL AFTER `id_user`;
ALTER TABLE `vente` ADD COLUMN IF NOT EXISTS `id_produit` INT(11) DEFAULT NULL AFTER `quantite`;
ALTER TABLE `vente` ADD COLUMN IF NOT EXISTS `ville` VARCHAR(100) DEFAULT NULL AFTER `id_produit`;
ALTER TABLE `vente` ADD COLUMN IF NOT EXISTS `region` VARCHAR(100) DEFAULT NULL AFTER `ville`;
ALTER TABLE `vente` ADD COLUMN IF NOT EXISTS `frais_livraison` FLOAT DEFAULT NULL AFTER `region`;

-- Add foreign key for id_produit if it doesn't exist
ALTER TABLE `vente` ADD CONSTRAINT IF NOT EXISTS `FK_vente_produit`
    FOREIGN KEY (`id_produit`) REFERENCES `produit` (`id_produit`) ON DELETE CASCADE;

-- ============================================================================
-- STEP 5: Create FACE_IMAGES table (if not exists)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `face_images` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `face_path` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  CONSTRAINT `face_images_ibfk_user` FOREIGN KEY (`user_id`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================================
-- STEP 6: Create MESSAGE table (if not exists)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `message` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `content` LONGTEXT NOT NULL,
  `sent_at` DATETIME NOT NULL,
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `sender_id` INT(11) NOT NULL,
  `receiver_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_B6BD307FF624B39D` (`sender_id`),
  KEY `IDX_B6BD307FCD53EDB6` (`receiver_id`),
  CONSTRAINT `FK_message_sender` FOREIGN KEY (`sender_id`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE,
  CONSTRAINT `FK_message_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================================
-- STEP 7: Create RESET_PASSWORD_REQUEST table (if not exists)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `reset_password_request` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `selector` VARCHAR(20) NOT NULL,
  `hashed_token` VARCHAR(100) NOT NULL,
  `requested_at` DATETIME NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `user_id` INT(11) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `IDX_7CE748AA76ED395` (`user_id`),
  CONSTRAINT `FK_reset_password_user` FOREIGN KEY (`user_id`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================================
-- STEP 8: Create USER_BADGE table (if not exists)
-- ============================================================================
CREATE TABLE IF NOT EXISTS `user_badge` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `user_id` INT(11) NOT NULL,
  `badge_id` INT(11) NOT NULL,
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_badge` (`user_id`, `badge_id`),
  KEY `idx_badge_id` (`badge_id`),
  CONSTRAINT `FK_user_badge_user` FOREIGN KEY (`user_id`) REFERENCES `utilisateur` (`id_user`) ON DELETE CASCADE,
  CONSTRAINT `FK_user_badge_badge` FOREIGN KEY (`badge_id`) REFERENCES `badge` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ============================================================================
-- STEP 9: Ensure BADGE table columns match schema
-- ============================================================================
-- The badge table should have: id, nom, description, niveau
-- This is already correct in both schemas

-- ============================================================================
-- STEP 10: Add missing columns to UTILISATEUR_BADGE table if needed
-- ============================================================================
-- The utilisateur_badge table already exists in Java schema
-- Just ensure it has all required fields
ALTER TABLE `utilisateur_badge` ADD COLUMN IF NOT EXISTS `date_attribution` DATE DEFAULT NULL;

-- ============================================================================
-- STEP 11: Ensure indexes exist for performance
-- ============================================================================
CREATE INDEX IF NOT EXISTS `idx_utilisateur_email` ON `utilisateur` (`email`);
CREATE INDEX IF NOT EXISTS `idx_client_user` ON `client` (`id_user`);
CREATE INDEX IF NOT EXISTS `idx_vente_user` ON `vente` (`id_user`);
CREATE INDEX IF NOT EXISTS `idx_vente_client` ON `vente` (`id_client`);
CREATE INDEX IF NOT EXISTS `idx_vente_produit` ON `vente` (`id_produit`);
CREATE INDEX IF NOT EXISTS `idx_message_sender` ON `message` (`sender_id`);
CREATE INDEX IF NOT EXISTS `idx_message_receiver` ON `message` (`receiver_id`);

-- ============================================================================
-- VERIFICATION QUERIES (commented out - for testing)
-- ============================================================================
-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_NAME = 'utilisateur' ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_NAME = 'client' ORDER BY ORDINAL_POSITION;

-- SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_NAME = 'vente' ORDER BY ORDINAL_POSITION;

-- SHOW TABLES LIKE 'face_images';
-- SHOW TABLES LIKE 'message';
-- SHOW TABLES LIKE 'reset_password_request';
-- SHOW TABLES LIKE 'user_badge';

-- ============================================================================
-- COMPLETION MESSAGE
-- ============================================================================
-- Migration complete! The Java database should now match the Symfony schema.
-- Next steps:
-- 1. Verify all new columns and tables exist
-- 2. Test data integrity
-- 3. Update Java services to use new fields
-- 4. Rebuild the application
-- ============================================================================

