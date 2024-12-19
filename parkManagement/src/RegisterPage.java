import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class RegisterPage {

    private Stage primaryStage;

    public RegisterPage(Stage stage) {
        this.primaryStage = stage;
    }

    public VBox createRegisterPage() {
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı Adı");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifre");

        Button registerButton = new Button("Üye Ol");

        // Üye ol butonuna tıklama olayı
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showError("Kullanıcı adı ve şifre boş olamaz!");
                return;
            }

            // Kullanıcıyı veritabanına kaydetme
            registerUser(username, password);
        });

        VBox vbox = new VBox(10, new Label("Üye Ol"), usernameField, passwordField, registerButton);
        vbox.setStyle("-fx-padding: 20;");

        return vbox;
    }

    // Kullanıcıyı veritabanına kaydetme
    private void registerUser(String username, String password) {
        try (Connection conn = DatabaseConnection.getInstance()) {
            if (conn == null) {
                showError("Veritabanı bağlantısı sağlanamadı.");
                return;
            }

            String query = "INSERT INTO parkusers (username, password_hash) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password); // Şifreyi hash'lemek iyi bir güvenlik pratiğidir!
                stmt.executeUpdate();
                System.out.println("Kullanıcı başarıyla kaydedildi!");

                // Üye olma işlemi başarılı, giriş sayfasına yönlendirelim
                LoginPage loginPage = new LoginPage(primaryStage);
                primaryStage.setScene(new Scene(loginPage.createLoginPage(), 300, 250));
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
            showError("Kullanıcı kaydedilirken bir hata oluştu.");
        } catch (Exception e) {
            System.err.println("Genel bir hata oluştu: " + e.getMessage());
            showError("Bir hata oluştu, lütfen tekrar deneyin.");
        }
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
