package org.example.pidev.services.utilisateur;

import org.example.pidev.interfaces.IService;
import org.example.pidev.models.Message;
import org.example.pidev.utils.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for managing user messages
 * Handles message creation, retrieval, and status updates
 */
public class MessageService implements IService<Message> {

    @Override
    public boolean add(Message message) {
        String sql = "INSERT INTO message (content, sent_at, is_read, sender_id, receiver_id) " +
                "VALUES (?, NOW(), ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, message.getContent());
            ps.setBoolean(2, message.isRead());
            ps.setInt(3, message.getSenderId());
            ps.setInt(4, message.getReceiverId());

            int result = ps.executeUpdate();
            System.out.println("✅ Message sent from user " + message.getSenderId() + " to " + message.getReceiverId());
            return result > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void update(Message message) {
        String sql = "UPDATE message SET content=?, is_read=? WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, message.getContent());
            ps.setBoolean(2, message.isRead());
            ps.setInt(3, message.getId());

            int rows = ps.executeUpdate();
            System.out.println("✅ Message updated: " + rows + " row(s) affected");

        } catch (SQLException e) {
            System.err.println("❌ Error updating message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM message WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println("✅ Message deleted: " + rows + " row(s) affected");
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting message: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Message getById(int id) {
        String sql = "SELECT * FROM message WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMessageFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting message by id: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<Message> getAll() {
        List<Message> list = new ArrayList<>();
        String sql = "SELECT * FROM message ORDER BY sent_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Message message = extractMessageFromResultSet(rs);
                list.add(message);
            }

            System.out.println("✅ Retrieved " + list.size() + " message(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting all messages: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Get all messages between two users
     *
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return List of messages
     */
    public List<Message> getConversation(int userId1, int userId2) {
        List<Message> conversation = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE (sender_id=? AND receiver_id=?) OR (sender_id=? AND receiver_id=?) " +
                "ORDER BY sent_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    conversation.add(extractMessageFromResultSet(rs));
                }
            }

            System.out.println("✅ Retrieved " + conversation.size() + " message(s) in conversation");

        } catch (SQLException e) {
            System.err.println("❌ Error getting conversation: " + e.getMessage());
            e.printStackTrace();
        }

        return conversation;
    }

    /**
     * Get unread messages for a user
     *
     * @param userId Receiver user ID
     * @return List of unread messages
     */
    public List<Message> getUnreadMessages(int userId) {
        List<Message> unreadMessages = new ArrayList<>();
        String sql = "SELECT * FROM message WHERE receiver_id=? AND is_read=FALSE ORDER BY sent_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    unreadMessages.add(extractMessageFromResultSet(rs));
                }
            }

            System.out.println("✅ Found " + unreadMessages.size() + " unread message(s)");

        } catch (SQLException e) {
            System.err.println("❌ Error getting unread messages: " + e.getMessage());
            e.printStackTrace();
        }

        return unreadMessages;
    }

    /**
     * Mark a message as read
     *
     * @param messageId Message ID
     * @return true if successful
     */
    public boolean markAsRead(int messageId) {
        String sql = "UPDATE message SET is_read=TRUE WHERE id=?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, messageId);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error marking message as read: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get count of unread messages for a user
     *
     * @param userId User ID
     * @return Unread message count
     */
    public int getUnreadCount(int userId) {
        String sql = "SELECT COUNT(*) as cnt FROM message WHERE receiver_id=? AND is_read=FALSE";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting unread count: " + e.getMessage());
        }

        return 0;
    }

    private Message extractMessageFromResultSet(ResultSet rs) throws SQLException {
        Message message = new Message();
        message.setId(rs.getInt("id"));
        message.setSenderId(rs.getInt("sender_id"));
        message.setReceiverId(rs.getInt("receiver_id"));
        message.setContent(rs.getString("content"));
        message.setRead(rs.getBoolean("is_read"));

        Timestamp sentAt = rs.getTimestamp("sent_at");
        if (sentAt != null) {
            message.setSentAt(sentAt.toLocalDateTime());
        }

        return message;
    }
}

