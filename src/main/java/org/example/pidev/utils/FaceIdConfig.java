package org.example.pidev.utils;

/**
 * Face ID Configuration
 * Centralized configuration for Face ID System
 */
public class FaceIdConfig {

    // ============================================================
    // Face Recognition Parameters
    // ============================================================

    /** Similarity threshold for face matching (0.0 - 1.0) */
    public static final double SIMILARITY_THRESHOLD = 0.75;

    /** Image capture timeout in milliseconds */
    public static final long CAPTURE_TIMEOUT_MS = 5000; // 5 seconds

    /** Image capture attempt count before giving up */
    public static final int MAX_CAPTURE_ATTEMPTS = 3;

    // ============================================================
    // Image Processing Parameters
    // ============================================================

    /** Camera frame width in pixels */
    public static final int CAMERA_WIDTH = 1280;

    /** Camera frame height in pixels */
    public static final int CAMERA_HEIGHT = 720;

    /** Camera FPS */
    public static final int CAMERA_FPS = 30;

    /** Image blur kernel size */
    public static final int BLUR_KERNEL = 5;

    /** Minimum image file size in bytes */
    public static final long MIN_IMAGE_SIZE = 1024; // 1 KB

    // ============================================================
    // Directory Configuration
    // ============================================================

    /** Directory for storing face images */
    public static final String FACES_DIRECTORY = "faces";

    /** Temporary images directory */
    public static final String TEMP_IMAGES_DIR = "faces/temp";

    /** Prefix for user face images */
    public static final String USER_FACE_PREFIX = "user_";

    /** Extension for saved images */
    public static final String IMAGE_EXTENSION = ".jpg";

    // ============================================================
    // Database Configuration
    // ============================================================

    /** Table name for face images */
    public static final String FACE_IMAGES_TABLE = "face_images";

    /** Enable auto-initialization of face database */
    public static final boolean AUTO_INIT_DATABASE = true;

    // ============================================================
    // OpenCV Configuration
    // ============================================================

    /** OpenCV Java library path for Windows */
    public static final String OPENCV_DLL_WINDOWS = "C:\\opencv\\build\\bin\\opencv_java490.dll";

    /** OpenCV Java library path for macOS */
    public static final String OPENCV_DYLIB_MAC = "/usr/local/Cellar/opencv@4.9/4.9.0_1/lib/libopencv_java490.dylib";

    /** OpenCV Java library path for Linux */
    public static final String OPENCV_SO_LINUX = "/usr/local/lib/libopencv_java490.so";

    // ============================================================
    // Logging Configuration
    // ============================================================

    /** Enable debug logging */
    public static final boolean DEBUG_MODE = true;

    /** Log level: TRACE, DEBUG, INFO, WARN, ERROR */
    public static final String LOG_LEVEL = "INFO";

    // ============================================================
    // Feature Flags
    // ============================================================

    /** Enable Face ID feature */
    public static final boolean ENABLE_FACE_ID = true;

    /** Require Face ID for admin users */
    public static final boolean REQUIRE_FACE_ID_ADMIN = false;

    /** Allow login with Face ID only (no password fallback) */
    public static final boolean FACE_ID_ONLY_LOGIN = false;

    /** Auto-register face on first login */
    public static final boolean AUTO_REGISTER_FACE = false;

    // ============================================================
    // Utility Methods
    // ============================================================

    /**
     * Get the face image path for a user
     * @param userId User ID
     * @return Full path to face image
     */
    public static String getUserFacePath(int userId) {
        return FACES_DIRECTORY + "/" + USER_FACE_PREFIX + userId + IMAGE_EXTENSION;
    }

    /**
     * Get temporary face image path
     * @return Full path to temporary face image
     */
    public static String getTempFacePath() {
        return FACES_DIRECTORY + "/temp_" + System.currentTimeMillis() + IMAGE_EXTENSION;
    }

    /**
     * Validate configuration
     * @return true if all configurations are valid
     */
    public static boolean validateConfig() {
        // Check threshold range
        if (SIMILARITY_THRESHOLD < 0 || SIMILARITY_THRESHOLD > 1) {
            System.err.println("[FaceIdConfig] ❌ SIMILARITY_THRESHOLD must be between 0 and 1");
            return false;
        }

        // Check timeout
        if (CAPTURE_TIMEOUT_MS <= 0) {
            System.err.println("[FaceIdConfig] ❌ CAPTURE_TIMEOUT_MS must be positive");
            return false;
        }

        // Check camera dimensions
        if (CAMERA_WIDTH <= 0 || CAMERA_HEIGHT <= 0) {
            System.err.println("[FaceIdConfig] ❌ Camera dimensions must be positive");
            return false;
        }

        System.out.println("[FaceIdConfig] ✅ Configuration validated successfully");
        return true;
    }

    /**
     * Print all configuration values
     */
    public static void printConfig() {
        System.out.println("\n[FaceIdConfig] Current Configuration:");
        System.out.println("  Similarity Threshold: " + SIMILARITY_THRESHOLD);
        System.out.println("  Capture Timeout: " + CAPTURE_TIMEOUT_MS + "ms");
        System.out.println("  Max Attempts: " + MAX_CAPTURE_ATTEMPTS);
        System.out.println("  Camera: " + CAMERA_WIDTH + "x" + CAMERA_HEIGHT + "@" + CAMERA_FPS + "fps");
        System.out.println("  Faces Directory: " + FACES_DIRECTORY);
        System.out.println("  Face ID Enabled: " + ENABLE_FACE_ID);
        System.out.println("  Debug Mode: " + DEBUG_MODE);
        System.out.println();
    }

    public static void main(String[] args) {
        printConfig();
        validateConfig();
    }
}

