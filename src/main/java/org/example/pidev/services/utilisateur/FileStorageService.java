package org.example.pidev.services.utilisateur;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class FileStorageService {

    private static final String UPLOAD_DIR = "uploads/profiles/";

    public FileStorageService() {
        // Créer le dossier uploads s'il n'existe pas
        File directory = new File(UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public String saveProfileImage(File sourceFile) throws IOException {
        // Vérifier le type de fichier (optionnel)
        String fileName = sourceFile.getName().toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg") &&
                !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
            throw new IOException("Format de fichier non supporté. Utilisez JPG, PNG ou GIF.");
        }

        // Générer un nom unique
        String extension = getFileExtension(sourceFile.getName());
        String uniqueFileName = UUID.randomUUID().toString() + extension;

        // Chemin de destination
        Path destinationPath = Paths.get(UPLOAD_DIR + uniqueFileName);

        // Copier le fichier
        Files.copy(sourceFile.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFileName;
    }

    public File getProfileImage(String fileName) {
        return new File(UPLOAD_DIR + fileName);
    }

    public void deleteProfileImage(String fileName) {
        try {
            Files.deleteIfExists(Paths.get(UPLOAD_DIR + fileName));
        } catch (IOException e) {
            System.err.println("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }
}