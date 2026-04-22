import java.util.*;
import java.sql.*;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:tenderfood.db";

    static {
        initializeDatabase();
    }

    private static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name VARCHAR(100) UNIQUE, " +
                    "role VARCHAR(20), " +
                    "address TEXT, " +
                    "lat DECIMAL(10, 8), lng DECIMAL(11, 8), " +
                    "video_url TEXT, created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Products (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "seller_id INTEGER REFERENCES Users(id), " +
                    "name VARCHAR(100), " +
                    "base_price INTEGER, " +
                    "is_ai_priced BOOLEAN DEFAULT 0, " +
                    "tags TEXT)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Orders (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "buyer_id INTEGER REFERENCES Users(id), " +
                    "seller_id INTEGER REFERENCES Users(id), " +
                    "total_price INTEGER, " +
                    "status VARCHAR(20) DEFAULT 'PENDING', " +
                    "address TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

            stmt.execute("CREATE TABLE IF NOT EXISTS OrderItems (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "order_id INTEGER REFERENCES Orders(id), " +
                    "name VARCHAR(100), " +
                    "quantity INTEGER, " +
                    "price_at_time INTEGER)");

            stmt.execute("CREATE TABLE IF NOT EXISTS Messages (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "thread_id VARCHAR(50), " +
                    "sender_id INTEGER REFERENCES Users(id), " +
                    "message TEXT, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        } catch (SQLException e) {
            System.err.println("DB Init Error: " + e.getMessage());
        }
    }

    private static int getOrCreateUserId(Connection conn, String name, String role, String address) throws SQLException {
        try (PreparedStatement check = conn.prepareStatement("SELECT id FROM Users WHERE name = ?")) {
            check.setString(1, name);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return rs.getInt("id");
        }
        try (PreparedStatement insert = conn.prepareStatement("INSERT INTO Users (name, role, address) VALUES (?, ?, ?)")) {
            insert.setString(1, name);
            insert.setString(2, role);
            insert.setString(3, address);
            insert.executeUpdate();
            ResultSet rs = insert.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
        }
        return -1;
    }

    public static void saveOrder(String item, String winner, int price, String address) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            int buyerId = getOrCreateUserId(conn, "The King (Buyer)", "BUYER", address);
            int sellerId = getOrCreateUserId(conn, winner, "SELLER", null);

            try (PreparedStatement insertOrder = conn.prepareStatement(
                    "INSERT INTO Orders (buyer_id, seller_id, total_price, address, status) VALUES (?, ?, ?, ?, 'PAID')")) {
                insertOrder.setInt(1, buyerId);
                insertOrder.setInt(2, sellerId);
                insertOrder.setInt(3, price);
                insertOrder.setString(4, address);
                insertOrder.executeUpdate();
                ResultSet rsOrder = insertOrder.getGeneratedKeys();
                
                if (rsOrder.next()) {
                    int orderId = rsOrder.getInt(1);
                    try (PreparedStatement insertItem = conn.prepareStatement(
                            "INSERT INTO OrderItems (order_id, name, quantity, price_at_time) VALUES (?, ?, 1, ?)")) {
                        insertItem.setInt(1, orderId);
                        insertItem.setString(2, item);
                        insertItem.setInt(3, price);
                        insertItem.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving order: " + e.getMessage());
        }
    }

    public static List<String> getTopRequests(int limit) {
        List<String> top = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT name, COUNT(*) as freq FROM OrderItems GROUP BY name ORDER BY freq DESC LIMIT ?")) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                top.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return top;
    }

    public static List<String> getOrdersHistory() {
        List<String> history = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT o.created_at, i.name as item, u.name as winner, o.total_price, o.address " +
                     "FROM Orders o " +
                     "JOIN OrderItems i ON o.id = i.order_id " +
                     "JOIN Users u ON o.seller_id = u.id " +
                     "ORDER BY o.created_at ASC")) {
            
            while (rs.next()) {
                String timestamp = rs.getString("created_at");
                String item = rs.getString("item");
                String winner = rs.getString("winner");
                int price = rs.getInt("total_price");
                String address = rs.getString("address");
                history.add(String.format("%s|%s|%d|%s|%s", item, winner, price, address, timestamp));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }
}
