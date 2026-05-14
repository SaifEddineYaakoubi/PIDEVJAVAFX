package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.ResetPasswordRequest;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing password reset requests
 * Handles token generation, validation, and cleanup
 */
public class ResetPasswordService implements IService<ResetPasswordRequest> {

    @Override
    public boolean add(ResetPasswordRequest resetRequest) {
        String sql = "INSERT INTO reset_password_request (selector, hashed_token, requested_at, expires_at, user_id) " +
                "VALUES (?, ?, NOW(), ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetRequest.getSelector());
            ps.setString(2, resetRequest.getHashedToken());
            ps.setTimestamp(3, Timestamp.valueOf(resetRequest.getExpiresAt()));
            ps.setInt(4, resetRequest.getUserId());

            int result = ps.executeUpdate();
            System.out.println("✅ Password reset request created for user " + resetRequest.getUserId());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error creating reset request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(ResetPasswordRequest resetRequest) {
        String sql = "UPDATE reset_password_request SET selector=?, hashed_token=?, expires_at=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, resetRequest.getSelector());
            ps.setString(2, resetRequest.getHashedToken());
            ps.setTimestamp(3, Timestamp.valueOf(resetRequest.getExpiresAt()));
            ps.setInt(4, resetRequest.getId());

            int rows = ps.executeUpdate();
            System.out.println("✅ Reset request updated: " + rows + " row(s) affected");

        } catch (SQLException e) {
            System.err.println("❌ Error updating reset request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM reset_password_request WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("✅ Reset request deleted: " + rows + " row(s) affected");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting reset request: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public ResetPasswordRequest getById(int id) {
        String sql = "SELECT * FROM reset_password_request WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting reset request by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<ResetPasswordRequest> getAll() {
        List<ResetPasswordRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM reset_password_request ORDER BY requested_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }

            System.out.println("✅ Retrieved " + list.size() + " reset request(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all reset requests: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get reset request by selector token
     *
     * @param selector Token selector
     * @return ResetPasswordRequest or null
     */
    public ResetPasswordRequest getBySelector(String selector) {
        String sql = "SELECT * FROM reset_password_request WHERE selector=? LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, selector);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting reset request by selector: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get latest reset request for a user
     *
     * @param userId User ID
     * @return Latest ResetPasswordRequest or null
     */
    public ResetPasswordRequest getLatestForUser(int userId) {
        String sql = "SELECT * FROM reset_password_request WHERE user_id=? ORDER BY requested_at DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting latest reset request for user: " + e.getMessage());
        }

        return null;
    }

    /**
     * Check if token is valid (exists and not expired)
     *
     * @param selector Token selector
     * @return true if token is valid
     */
    public boolean isTokenValid(String selector) {
        ResetPasswordRequest request = getBySelector(selector);
        return request != null && !request.isExpired();
    }

    /**
     * Delete expired reset requests
     *
     * @return Number of deleted requests
     */
    public int deleteExpiredRequests() {
        String sql = "DELETE FROM reset_password_request WHERE expires_at < NOW()";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            int deleted = cs.executeUpdate();
            System.out.println("✅ Deleted " + deleted + " expired reset request(s)");
            return deleted;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting expired requests: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Delete all reset requests for a user
     *
     * @param userId User ID
     * @return true if successful
     */
    public boolean deleteForUser(int userId) {
        String sql = "DELETE FROM reset_password_request WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting reset requests for user: " + e.getMessage());
            return false;
        }
    }

    private ResetPasswordRequest extractFromResultSet(ResultSet rs) throws SQLException {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setId(rs.getInt("id"));
        request.setUserId(rs.getInt("user_id"));
        request.setSelector(rs.getString("selector"));
        request.setHashedToken(rs.getString("hashed_token"));

        Timestamp requestedAt = rs.getTimestamp("requested_at");
        if (requestedAt != null) {
            request.setRequestedAt(requestedAt.toLocalDateTime());
        }

        Timestamp expiresAt = rs.getTimestamp("expires_at");
        if (expiresAt != null) {
            request.setExpiresAt(expiresAt.toLocalDateTime());
        }

        return request;
    }
}

