package org.example.pidev.utils;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import java.io.File;

public class FaceRecognitionUtil {

    private static boolean openCvLoaded = false;
    private static String loadError = null;

    static {
        // Ne pas essayer de recharger OpenCV, juste vérifier s'il est disponible
        checkOpenCVStatus();
    }

    private static void checkOpenCVStatus() {
        try {
            // Vérifier si OpenCV est déjà chargé en tentant d'accéder à une classe
            Class.forName("org.opencv.core.Core");

            // Vérifier la version pour confirmer
            String version = org.opencv.core.Core.VERSION;
            openCvLoaded = true;
            System.out.println("✅ FaceRecognitionUtil: OpenCV " + version + " est déjà chargé");

        } catch (ClassNotFoundException e) {
            openCvLoaded = false;
            loadError = "OpenCV classes non trouvées";
            System.err.println("⚠️ FaceRecognitionUtil: OpenCV n'est pas disponible");
        } catch (UnsatisfiedLinkError e) {
            openCvLoaded = false;
            loadError = e.getMessage();
            System.err.println("⚠️ FaceRecognitionUtil: OpenCV chargé mais erreur de liaison: " + e.getMessage());
        } catch (Exception e) {
            openCvLoaded = false;
            loadError = e.getMessage();
            System.err.println("⚠️ FaceRecognitionUtil: Exception: " + e.getMessage());
        }
    }

    private static CascadeClassifier faceCascade;
    private static CascadeClassifier eyeCascade;

    static {
        if (openCvLoaded) {
            initializeCascades();
        }
    }

    private static void initializeCascades() {
        try {
            faceCascade = new CascadeClassifier();
            eyeCascade = new CascadeClassifier();

            // Charger les classificateurs pré-entraînés depuis les ressources
            String faceCascadePath = "src/main/resources/haarcascades/haarcascade_frontalface_default.xml";
            String eyeCascadePath = "src/main/resources/haarcascades/haarcascade_eye.xml";

            File faceFile = new File(faceCascadePath);
            File eyeFile = new File(eyeCascadePath);

            if (faceFile.exists()) {
                faceCascade.load(faceCascadePath);
                System.out.println("✅ Face cascade loaded");
            } else {
                System.out.println("⚠️ Face cascade file not found: " + faceCascadePath);
            }

            if (eyeFile.exists()) {
                eyeCascade.load(eyeCascadePath);
                System.out.println("✅ Eye cascade loaded");
            } else {
                System.out.println("⚠️ Eye cascade file not found: " + eyeCascadePath);
            }

            System.out.println("[FaceRecognition] Cascade classifiers initialized");
        } catch (Exception e) {
            System.err.println("[FaceRecognition] Error initializing cascades: " + e.getMessage());
        }
    }

    public static boolean captureImage(String path) {
        return captureImage(path, 5000);
    }

    /**
     * Capture image from camera with face detection
     * @param path Destination path
     * @param timeoutMs Timeout in milliseconds
     * @return true if image captured successfully
     */
    public static boolean captureImage(String path, long timeoutMs) {
        if (!openCvLoaded) {
            System.err.println("❌ OpenCV not loaded - Face recognition unavailable");
            return false;
        }

        VideoCapture camera = new VideoCapture(0);
        Mat frame = new Mat();

        try {
            if (!camera.isOpened()) {
                System.err.println("❌ Camera not detected or not accessible");
                return false;
            }

            // Configure camera for better quality
            camera.set(org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH, 640);
            camera.set(org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT, 480);
            camera.set(org.opencv.videoio.Videoio.CAP_PROP_FPS, 30);

            long startTime = System.currentTimeMillis();
            boolean captured = false;

            while (System.currentTimeMillis() - startTime < timeoutMs && !captured) {
                if (camera.read(frame) && !frame.empty()) {
                    // Sauvegarder l'image
                    boolean saved = Imgcodecs.imwrite(path, frame);
                    if (saved) {
                        captured = true;
                        System.out.println("✅ Image captured and saved: " + path);
                    }
                }
                Thread.sleep(100);
            }

            return captured;

        } catch (Exception e) {
            System.err.println("❌ Error during image capture: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (frame != null) frame.release();
            if (camera != null) camera.release();
        }
    }

    /**
     * Compare two images for face similarity
     * @param imagePath1 First image path
     * @param imagePath2 Second image path
     * @return Similarity score (0-1)
     */
    public static double compareImages(String imagePath1, String imagePath2) {
        if (!openCvLoaded) {
            System.err.println("❌ OpenCV not loaded - comparison unavailable");
            return 0.0;
        }

        Mat img1 = null;
        Mat img2 = null;
        Mat resized1 = null;
        Mat resized2 = null;
        Mat gray1 = null;
        Mat gray2 = null;
        Mat diff = null;

        try {
            img1 = Imgcodecs.imread(imagePath1);
            img2 = Imgcodecs.imread(imagePath2);

            if (img1.empty() || img2.empty()) {
                System.err.println("❌ One or both images could not be loaded");
                return 0.0;
            }

            // Redimensionner pour la comparaison
            resized1 = new Mat();
            resized2 = new Mat();

            Imgproc.resize(img1, resized1, new Size(320, 240));
            Imgproc.resize(img2, resized2, new Size(320, 240));

            // Convertir en gris
            gray1 = new Mat();
            gray2 = new Mat();

            Imgproc.cvtColor(resized1, gray1, Imgproc.COLOR_BGR2GRAY);
            Imgproc.cvtColor(resized2, gray2, Imgproc.COLOR_BGR2GRAY);

            // Améliorer le contraste avec CLAHE
            org.opencv.imgproc.CLAHE clahe = Imgproc.createCLAHE(2.0, new Size(8, 8));
            Mat enhanced1 = new Mat();
            Mat enhanced2 = new Mat();
            clahe.apply(gray1, enhanced1);
            clahe.apply(gray2, enhanced2);

            // Calculer la différence
            diff = new Mat();
            Core.absdiff(enhanced1, enhanced2, diff);

            // Normaliser la différence pour éviter les problèmes d'échelle
            diff.convertTo(diff, CvType.CV_32F);
            Core.divide(diff, new Scalar(255.0), diff);

            // Calculer MSE sur la matrice normalisée
            Scalar mean = Core.mean(diff);
            double mse = mean.val[0];

            // Meilleure formule de similitude
            // Penaliser les grandes différences mais moins que avant
            double similarity = Math.max(0, 1 - (mse * 2)); // Multiplier par 2 au lieu de diviser par 10000

            System.out.println("[FaceRecognition] MSE: " + String.format("%.6f", mse) + ", Similarity: " + String.format("%.4f", similarity));

            enhanced1.release();
            enhanced2.release();

            return Math.min(1.0, similarity); // Limiter à 1.0

        } catch (Exception e) {
            System.err.println("❌ Error comparing images: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        } finally {
            if (img1 != null) img1.release();
            if (img2 != null) img2.release();
            if (resized1 != null) resized1.release();
            if (resized2 != null) resized2.release();
            if (gray1 != null) gray1.release();
            if (gray2 != null) gray2.release();
            if (diff != null) diff.release();
        }
    }

    /**
     * Check if image contains a valid face
     * @param imagePath Path to image
     * @return true if face detected
     */
    public static boolean isFaceDetected(String imagePath) {
        if (!openCvLoaded) {
            System.err.println("[FaceRecognition] OpenCV not loaded, allowing image");
            // If OpenCV not available, do basic check
            try {
                File f = new File(imagePath);
                return f.exists() && f.length() > 1024;
            } catch (Exception e) {
                return false;
            }
        }

        Mat image = null;
        Mat grayImage = null;
        try {
            image = Imgcodecs.imread(imagePath);
            if (image.empty()) {
                System.err.println("[FaceRecognition] Image file is empty: " + imagePath);
                return false;
            }

            // Basic size check
            if (image.cols() < 100 || image.rows() < 100) {
                System.err.println("[FaceRecognition] Image too small: " + image.cols() + "x" + image.rows());
                return false;
            }

            // Try to detect face using cascade classifier
            if (faceCascade == null || faceCascade.empty()) {
                System.err.println("[FaceRecognition] Face cascade not loaded, allowing image");
                return true; // Allow if cascade not available
            }

            grayImage = new Mat();
            Imgproc.cvtColor(image, grayImage, Imgproc.COLOR_BGR2GRAY);

            MatOfRect faces = new MatOfRect();
            faceCascade.detectMultiScale(grayImage, faces, 1.1, 4, 0, new Size(30, 30), new Size());

            int faceCount = faces.toArray().length;
            System.out.println("[FaceRecognition] Faces detected: " + faceCount);

            return faceCount > 0;

        } catch (Exception e) {
            System.err.println("[FaceRecognition] Error detecting face: " + e.getMessage());
            return false;
        } finally {
            if (image != null) image.release();
            if (grayImage != null) grayImage.release();
        }
    }

    /**
     * Ensure face image directory exists
     */
    public static void ensureFaceDirectory() {
        try {
            File faceDir = new File("faces");
            if (!faceDir.exists()) {
                faceDir.mkdirs();
                System.out.println("✅ Face directory created: " + faceDir.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating face directory: " + e.getMessage());
        }
    }

    /**
     * Check if OpenCV is available
     * @return true if OpenCV is loaded and available
     */
    public static boolean isOpenCvAvailable() {
        return openCvLoaded;
    }

    /**
     * Get OpenCV load error if any
     * @return error message or null
     */
    public static String getOpenCvLoadError() {
        return loadError;
    }

    public static void main(String[] args) {
        ensureFaceDirectory();
        System.out.println("FaceRecognitionUtil initialized - OpenCV available: " + isOpenCvAvailable());
    }
}