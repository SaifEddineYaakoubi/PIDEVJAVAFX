package org.example.pidev.services.recoltes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.UUID;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class VoiceRSSService {
    private static final String API_KEY = "80b4e2363d1c4a96801bdef53b88573b";
    private static final String BASE_URL = "https://api.voicerss.org/?key=%s&hl=fr-fr&c=MP3&src=%s";

    public static void speak(String texte) {
        new Thread(() -> {
            try {
                String urlStr = String.format(BASE_URL, API_KEY, URLEncoder.encode(texte, "UTF-8"));
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();
                int responseCode = conn.getResponseCode();
                String contentType = conn.getContentType();
                System.out.println("VoiceRSS HTTP response: " + responseCode + ", Content-Type: " + contentType);
                if (responseCode == 200 && contentType != null && contentType.toLowerCase().contains("audio")) {
                    // Sauvegarder sur le bureau pour test
                    String userHome = System.getProperty("user.home");
                    File mp3File = new File(userHome + "/Desktop/tts_voicerss_" + UUID.randomUUID() + ".mp3");
                    try (InputStream in = conn.getInputStream(); FileOutputStream out = new FileOutputStream(mp3File)) {
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("Fichier audio sauvegardé: " + mp3File.getAbsolutePath());
                    Platform.runLater(() -> {
                        try {
                            Media media = new Media(mp3File.toURI().toString());
                            MediaPlayer player = new MediaPlayer(media);
                            player.setVolume(1.0);
                            player.setOnError(() -> System.out.println("Erreur MediaPlayer: " + player.getError()));
                            player.play();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    // Lire le texte d'erreur retourné par l'API (souvent quota ou clé API)
                    try (InputStream in = conn.getInputStream()) {
                        StringBuilder sb = new StringBuilder();
                        int c;
                        while ((c = in.read()) != -1) sb.append((char)c);
                        System.out.println("VoiceRSS API error: " + responseCode + "\nMessage: " + sb.toString());
                    } catch (Exception ex) {
                        System.out.println("VoiceRSS API error: " + responseCode + " (pas de message lisible)");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
