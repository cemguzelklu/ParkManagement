import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class LoginPage {

    private Stage primaryStage;

    public LoginPage(Stage stage) {
        this.primaryStage = stage;
    }

    public VBox createLoginPage() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı Adı");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifre");

        Button loginButton = new Button("Giriş Yap");

        // Giriş butonuna tıklama olayı
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Kullanıcı adı ve şifre boş olamaz!");
                return;
            }

            // Veritabanında kullanıcıyı doğrulama işlemi ve kullanıcı türüne göre giriş
            User user = authenticateUser(username, password);
            if (user != null) {
                System.out.println("Giriş başarılı, kullanıcı: " + username);

                // Kullanıcının rolüne göre Dashboard'u başlatma
                user.performRole(primaryStage);
            } else {
                showError("Geçersiz kullanıcı adı veya şifre.");
            }
        });

        VBox vbox = new VBox(10, new Label("Giriş Yap"), usernameField, passwordField, loginButton);
        vbox.setStyle("-fx-padding: 20;");

        return vbox;
    }

    // Kullanıcı doğrulama metodu ve kullanıcı türü alma
    private User authenticateUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null) {
                return null;
            }

            String query = "SELECT username, role FROM parkusers WHERE username = ? AND password_hash = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet resultSet = stmt.executeQuery();

                if (resultSet.next()) {
                    String role = resultSet.getString("role");
                    System.out.println("Kullanıcı Bulundu: " + username + ", Rol: " + role);
                    return UserFactory.createUser(role, username);
                } else {
                    System.out.println("Kullanıcı bulunamadı: " + username);
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
        return null;
    }


    // Hata mesajı gösterme
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
