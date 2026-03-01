package org.example.pidev.services;

import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;

public class GoogleDriveServiceSmokeTest {
    public static void main(String[] args) {
        try {
            Drive drive = GoogleDriveService.getDriveService();
            FileList result = drive.files().list().setPageSize(10).setFields("files(id,name)").execute();
            System.out.println("Found files: " + (result.getFiles() == null ? 0 : result.getFiles().size()));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Failed to create Drive client: " + e.getMessage());
            System.exit(1);
        }
    }
}

