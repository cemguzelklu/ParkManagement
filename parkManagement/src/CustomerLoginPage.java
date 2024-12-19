import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class CustomerLoginPage {

    private Stage primaryStage;

    public CustomerLoginPage(Stage stage) {
        this.primaryStage = stage;
    }

    public VBox createLoginPage() {
        // Üye Ol Butonu
        Button registerButton = new Button("Üye Ol");
        registerButton.setOnAction(e -> {
            // Üye olma sayfasını aç
            RegisterPage registerPage = new RegisterPage(primaryStage);
            primaryStage.setScene(new Scene(registerPage.createRegisterPage(), 300, 250));
        });

        // Giriş Yap Butonu
        Button loginButton = new Button("Giriş Yap");
        loginButton.setOnAction(e -> {
            // Giriş yapma sayfasını aç
            LoginPage loginPage = new LoginPage(primaryStage);
            primaryStage.setScene(new Scene(loginPage.createLoginPage(), 300, 250));
        });

        // VBox düzeni
        VBox vbox = new VBox(10, registerButton, loginButton);
        vbox.setStyle("-fx-padding: 20;");

        return vbox;
    }
}
