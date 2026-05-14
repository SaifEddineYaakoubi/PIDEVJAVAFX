package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.FaceImage;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing face images in the face_images table
 * Handles storage and retrieval of face image references for users
 */
public class FaceImageService implements IService<FaceImage> {

    @Override
    public boolean add(FaceImage faceImage) {
        // First, delete existing face image for this user (unique constraint)
        deleteByUserId(faceImage.getUserId());

        String sql = "INSERT INTO face_images (user_id, face_path, created_at, updated_at) " +
                "VALUES (?, ?, NOW(), NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, faceImage.getUserId());
            ps.setString(2, faceImage.getFacePath());

            int result = ps.executeUpdate();
            System.out.println("✅ Face image added for user " + faceImage.getUserId());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding face image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(FaceImage faceImage) {
        String sql = "UPDATE face_images SET face_path=?, updated_at=NOW() WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, faceImage.getFacePath());
            ps.setInt(2, faceImage.getId());

            int rows = ps.executeUpdate();
            System.out.println("✅ Face image updated: " + rows + " row(s) affected");

        } catch (SQLException e) {
            System.err.println("❌ Error updating face image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM face_images WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("✅ Face image deleted: " + rows + " row(s) affected");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting face image: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public FaceImage getById(int id) {
        String sql = "SELECT * FROM face_images WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFaceImageFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting face image by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<FaceImage> getAll() {
        List<FaceImage> list = new ArrayList<>();
        String sql = "SELECT * FROM face_images ORDER BY id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FaceImage faceImage = extractFaceImageFromResultSet(rs);
                list.add(faceImage);
            }

            System.out.println("✅ Retrieved " + list.size() + " face image(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all face images: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get face image for a specific user
     *
     * @param userId User ID
     * @return FaceImage or null if not found
     */
    public FaceImage getByUserId(int userId) {
        String sql = "SELECT * FROM face_images WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFaceImageFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("⚠️ No face image found for user " + userId);
        }

        return null;
    }

    /**
     * Delete face image for a specific user
     *
     * @param userId User ID
     * @return true if deletion successful
     */
    public boolean deleteByUserId(int userId) {
        String sql = "DELETE FROM face_images WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Face image deleted for user " + userId);
            }

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("⚠️ Error deleting face image for user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if user has a face image
     *
     * @param userId User ID
     * @return true if face image exists
     */
    public boolean hasFaceImage(int userId) {
        return getByUserId(userId) != null;
    }

    /**
     * Get face path for a user
     *
     * @param userId User ID
     * @return Face image path or null
     */
    public String getFacePath(int userId) {
        FaceImage faceImage = getByUserId(userId);
        return faceImage != null ? faceImage.getFacePath() : null;
    }

    private FaceImage extractFaceImageFromResultSet(ResultSet rs) throws SQLException {
        FaceImage faceImage = new FaceImage();
        faceImage.setId(rs.getInt("id"));
        faceImage.setUserId(rs.getInt("user_id"));
        faceImage.setFacePath(rs.getString("face_path"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            faceImage.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            faceImage.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return faceImage;
    }
}

