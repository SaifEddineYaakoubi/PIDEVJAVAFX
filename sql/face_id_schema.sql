-- ============================================================
-- Face ID System Database Schema
-- ============================================================

-- Create face_images table to store user face paths
CREATE TABLE IF NOT EXISTS face_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    face_path VARCHAR(255) NOT NULL COMMENT 'Path to the stored face image file',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'When the face was registered',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    FOREIGN KEY (user_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Stores face image paths for biometric authentication';

-- Create index for faster lookups
CREATE INDEX idx_user_id ON face_images(user_id);
CREATE INDEX idx_created_at ON face_images(created_at);

-- ============================================================
-- Sample data (optional)
-- ============================================================

-- TRUNCATE TABLE face_images;

-- ============================================================
-- Useful queries
-- ============================================================

-- Get face path for a user
-- SELECT face_path FROM face_images WHERE user_id = 1;

-- Check if user has registered face
-- SELECT COUNT(*) as has_face FROM face_images WHERE user_id = 1;

-- Get all users with registered faces
-- SELECT u.id_user, u.nom, u.email, f.face_path FROM face_images f
-- JOIN utilisateur u ON f.user_id = u.id_user;

-- Delete face for a user
-- DELETE FROM face_images WHERE user_id = 1;

-- ============================================================
-- Statistics
-- ============================================================

-- Total users with Face ID
-- SELECT COUNT(*) FROM face_images;

-- Users without Face ID
-- SELECT COUNT(*) FROM utilisateur WHERE id_user NOT IN (SELECT DISTINCT user_id FROM face_images);

