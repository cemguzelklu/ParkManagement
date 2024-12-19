import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/parkmanagement";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    // Private constructor to prevent instantiation
    private DatabaseConnection() {}

    // Veritabanı bağlantısını her seferinde aç
    public static Connection getInstance() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Sürücüsü bulunamadı: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Bağlantı hatası: " + e.getMessage());
        }
        return null;
    }
}
