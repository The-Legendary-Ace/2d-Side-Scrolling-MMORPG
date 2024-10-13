package io.jyberion.mmorpg.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseUtil {

    // Example method to insert channel registration data
    public static void saveChannel(String channelName, String host, int port) {
        String url = "jdbc:mysql://localhost:3306/mmorpg";  // Example connection string
        String user = "root";  // Database user
        String password = "";  // Database password

        String query = "INSERT INTO channels (channel_name, host, port) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, channelName);
            stmt.setString(2, host);
            stmt.setInt(3, port);

            stmt.executeUpdate();
            System.out.println("Channel registered in database: " + channelName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
