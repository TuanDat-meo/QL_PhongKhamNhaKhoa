package connect;

import java.io.InputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class connectMySQL {
    private static String URL;
    private static String USER;
    private static String PASSWORD;
    private static String DRIVER;

    static {
        try (InputStream input = connectMySQL.class.getClassLoader().getResourceAsStream("database.properties")) { 
            if (input == null) {
                System.err.println("Lỗi khi đọc file cấu hình: Không tìm thấy file database.properties trong classpath!");
                System.err.println("Đường dẫn kiểm tra getResourceAsStream: " + connectMySQL.class.getClassLoader().getResourceAsStream("database.properties"));
                System.err.println("Đường dẫn kiểm tra getResource: " + connectMySQL.class.getClassLoader().getResource("database.properties"));
                throw new IOException("Không tìm thấy file database.properties trong classpath!");
            }
            Properties properties = new Properties();
            properties.load(input);

            URL = properties.getProperty("url");
            USER = properties.getProperty("username");
            PASSWORD = properties.getProperty("password");
            DRIVER = properties.getProperty("driver");

            if (DRIVER != null) {
                Class.forName(DRIVER);
            } else {
                System.err.println("DRIVER is null. Không thể tải driver MySQL.");
            }

        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file cấu hình: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Không tìm thấy driver MySQL: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Đóng kết nối
    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Đóng kết nối thành công.");
            }
        } catch (SQLException e) {
            System.err.println("Lỗi khi đóng kết nối: " + e.getMessage());
        }
    }
}
