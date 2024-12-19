import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Müşteri Girişi Butonu
        Button customerLoginButton = new Button("Müşteri Girişi");
        customerLoginButton.setOnAction(e -> {
            // Müşteri giriş ekranını aç
            CustomerLoginPage customerLoginPage = new CustomerLoginPage(primaryStage);
            primaryStage.setScene(new Scene(customerLoginPage.createLoginPage(), 300, 250));
        });

        // Yönetici Girişi Butonu
        Button adminLoginButton = new Button("Yönetici Girişi");
        adminLoginButton.setOnAction(e -> {
            // Yönetici girişi işlemi yapılabilir
            System.out.println("Yönetici Girişi");

            // Yönetici ekranını aç
            AdminDashboard adminDashboard = new AdminDashboard();
            adminDashboard.showDashboard(primaryStage);  // Yönetici ekranını aç
        });

        // VBox düzeni
        VBox vbox = new VBox(10, customerLoginButton, adminLoginButton);
        vbox.setStyle("-fx-padding: 20;");

        // Scene ve Stage ayarları
        Scene scene = new Scene(vbox, 300, 250);
        primaryStage.setTitle("Otopark Yönetim Uygulaması");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

