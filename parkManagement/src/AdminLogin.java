import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AdminLogin {

    private final String ADMIN_USERNAME = "admin";
    private final String ADMIN_PASSWORD = "1234";

    public void showAdminLogin(Stage primaryStage) {
        Stage loginStage = new Stage();  // Yeni bir sahne oluştur
        VBox layout = new VBox(10);

        // Bileşenler
        Label titleLabel = new Label("Yönetici Girişi");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı Adı");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifre");

        Button loginButton = new Button("Giriş Yap");

        // Giriş Butonu Tıklama Olayı
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
                loginStage.close();  // Giriş ekranını kapat
                new AdminDashboard().showDashboard(primaryStage); // Yönetici ekranını aç
            } else {
                showError("Hatalı kullanıcı adı veya şifre!");
            }
        });

        // Layout'a ekleme
        layout.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Scene ve Stage Ayarları
        loginStage.setScene(new Scene(layout, 300, 200));
        loginStage.setTitle("Yönetici Girişi");
        loginStage.show();
    }

    // Hata Mesajı Gösterme Metodu
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Giriş Hatası");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
