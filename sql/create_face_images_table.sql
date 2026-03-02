-- Create face_images table for Face ID Recognition System
-- Run this SQL in your database

CREATE TABLE IF NOT EXISTS face_images (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL UNIQUE,
    face_path VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES utilisateur(id_user) ON DELETE CASCADE
);

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_face_images_user_id ON face_images(user_id);

-- Verify the table was created
SELECT 'face_images table created successfully' AS status;

-- Check if any existing users have face paths in the utilisateur table
SELECT id_user, nom, email FROM utilisateur LIMIT 5;

-- You can also migrate existing face paths if needed:
-- INSERT INTO face_images (user_id, face_path)
-- SELECT id_user, face_image_path FROM utilisateur
-- WHERE face_image_path IS NOT NULL AND face_image_path != '';

