package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.UserBadge;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user badges and achievements
 * Handles badge assignment, tracking, and rewards
 */
public class UserBadgeService implements IService<UserBadge> {

    @Override
    public boolean add(UserBadge userBadge) {
        String sql = "INSERT INTO user_badge (user_id, badge_id, created_at) " +
                "VALUES (?, ?, NOW())";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userBadge.getUserId());
            ps.setInt(2, userBadge.getBadgeId());

            int result = ps.executeUpdate();
            System.out.println("✅ Badge " + userBadge.getBadgeId() + " awarded to user " + userBadge.getUserId());
            return result > 0;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("⚠️ User already has this badge");
            } else {
                System.err.println("❌ Error awarding badge: " + e.getMessage());
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public void update(UserBadge userBadge) {
        // User badges are immutable, but we can handle it
        String sql = "UPDATE user_badge SET user_id=?, badge_id=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userBadge.getUserId());
            ps.setInt(2, userBadge.getBadgeId());
            ps.setInt(3, userBadge.getId());

            int rows = ps.executeUpdate();
            System.out.println("✅ User badge updated: " + rows + " row(s) affected");

        } catch (SQLException e) {
            System.err.println("❌ Error updating user badge: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM user_badge WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("✅ User badge deleted: " + rows + " row(s) affected");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting user badge: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public UserBadge getById(int id) {
        String sql = "SELECT * FROM user_badge WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting user badge by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<UserBadge> getAll() {
        List<UserBadge> list = new ArrayList<>();
        String sql = "SELECT * FROM user_badge ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(extractFromResultSet(rs));
            }

            System.out.println("✅ Retrieved " + list.size() + " badge assignment(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all user badges: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get all badges for a user
     *
     * @param userId User ID
     * @return List of UserBadge objects
     */
    public List<UserBadge> getBadgesByUser(int userId) {
        List<UserBadge> badges = new ArrayList<>();
        String sql = "SELECT * FROM user_badge WHERE user_id=? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    badges.add(extractFromResultSet(rs));
                }
            }

            System.out.println("✅ User " + userId + " has " + badges.size() + " badge(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting badges for user: " + e.getMessage());
            e.printStackTrace();
        }

        return badges;
    }

    /**
     * Check if a user has a specific badge
     *
     * @param userId  User ID
     * @param badgeId Badge ID
     * @return true if user has the badge
     */
    public boolean hasBadge(int userId, int badgeId) {
        String sql = "SELECT COUNT(*) as cnt FROM user_badge WHERE user_id=? AND badge_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, badgeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt") > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error checking if user has badge: " + e.getMessage());
        }

        return false;
    }

    /**
     * Remove a badge from a user
     *
     * @param userId  User ID
     * @param badgeId Badge ID
     * @return true if successful
     */
    public boolean removeBadge(int userId, int badgeId) {
        String sql = "DELETE FROM user_badge WHERE user_id=? AND badge_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, badgeId);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Badge " + badgeId + " removed from user " + userId);
            }

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error removing badge: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Count badges for a user
     *
     * @param userId User ID
     * @return Number of badges
     */
    public int getBadgeCount(int userId) {
        String sql = "SELECT COUNT(*) as cnt FROM user_badge WHERE user_id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error counting user badges: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Get all users with a specific badge
     *
     * @param badgeId Badge ID
     * @return List of user IDs
     */
    public List<Integer> getUsersWithBadge(int badgeId) {
        List<Integer> userIds = new ArrayList<>();
        String sql = "SELECT user_id FROM user_badge WHERE badge_id=? ORDER BY created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, badgeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    userIds.add(rs.getInt("user_id"));
                }
            }

            System.out.println("✅ Badge " + badgeId + " awarded to " + userIds.size() + " user(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting users with badge: " + e.getMessage());
        }

        return userIds;
    }

    private UserBadge extractFromResultSet(ResultSet rs) throws SQLException {
        UserBadge userBadge = new UserBadge();
        userBadge.setId(rs.getInt("id"));
        userBadge.setUserId(rs.getInt("user_id"));
        userBadge.setBadgeId(rs.getInt("badge_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            userBadge.setCreatedAt(createdAt.toLocalDateTime());
        }

        return userBadge;
    }
}

