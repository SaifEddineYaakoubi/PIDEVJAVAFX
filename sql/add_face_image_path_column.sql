-- Add face_image_path column to utilisateur table if it doesn't exist
ALTER TABLE utilisateur ADD COLUMN IF NOT EXISTS face_image_path VARCHAR(255) DEFAULT NULL COMMENT 'Path to the user face image for Face ID authentication';

-- Create index for faster lookups
CREATE INDEX IF NOT EXISTS idx_face_image_path ON utilisateur(face_image_path);

