import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;

public class UserDashboard {

    private VBox vehicleVBox;
    private ArrayList<Vehicle> vehicles;

    public UserDashboard() {
        vehicles = new ArrayList<>();
    }

    public void showDashboard(Stage primaryStage, String username) {
        // Araç ekleme butonu
        Button addVehicleButton = new Button("Araç Girişi Yap");
        addVehicleButton.setOnAction(e -> showAddVehicleDialog(username));

        // Araç çıkarma butonu
        Button removeVehicleButton = new Button("Araç Çıkışı Yap");
        removeVehicleButton.setOnAction(e -> showRemoveVehicleDialog(username));

        // Çıkış yap butonu
        Button logoutButton = new Button("Çıkış Yap");
        logoutButton.setOnAction(e -> {
            // Mevcut pencereyi kapat
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();
            currentStage.close();
            // Giriş ekranına yönlendir
            Main loginPage = new Main();  // Main sınıfını oluştur
            loginPage.start(new Stage()); // Yeni bir Stage başlat
        });

        // Araç listesi için VBox
        vehicleVBox = new VBox(10);
        updateVehicleList(username); // Kullanıcıya göre araç listesi güncelle

        // Tüm bileşenleri içeren ana düzen
        VBox layout = new VBox(10,
                new Label("Kullanıcı Paneli"),
                addVehicleButton,
                removeVehicleButton,
                vehicleVBox,
                logoutButton
        );
        layout.setStyle("-fx-padding: 20;");

        // Sahne ve Stage ayarları
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Kullanıcı Paneli");
        primaryStage.show();
    }


    public void showAddVehicleDialog(String username) {
        Stage dialog = new Stage();
        VBox vbox = new VBox(10);

        // Plaka, model, araç türü, renk için TextField'lar
        TextField plateField = new TextField();
        plateField.setPromptText("Plaka");

        TextField modelField = new TextField();
        modelField.setPromptText("Model");

        TextField vehicleTypeField = new TextField();
        vehicleTypeField.setPromptText("Araç Türü");

        TextField colorField = new TextField();
        colorField.setPromptText("Renk");

        // Park alanları için ComboBox
        ComboBox<String> parkingSpaceComboBox = new ComboBox<>();
        loadParkingSpaces(parkingSpaceComboBox); // Park alanlarını yükle

        Button saveButton = new Button("Kaydet");
        saveButton.setOnAction(e -> {
            String plate = plateField.getText();
            String model = modelField.getText();
            String vehicleType = vehicleTypeField.getText();
            String color = colorField.getText();
            String selectedSpace = parkingSpaceComboBox.getValue(); // Seçilen park alanı

            // Boş alan kontrolü
            if (plate.isEmpty() || model.isEmpty() || vehicleType.isEmpty() || color.isEmpty() || selectedSpace == null) {
                showError("Tüm alanlar doldurulmalıdır!");
                return;
            }

            // Park alanı kontrolü
            if (!isParkingSpaceAvailable(selectedSpace)) {
                showError("Seçilen park alanı dolu! Lütfen başka bir alan seçin.");
                return;
            }

            // Veritabanına araç bilgilerini ekle
            addVehicleToDatabase(username, plate, model, vehicleType, color, selectedSpace);
            updateVehicleList(username);
            dialog.close();
        });

        vbox.getChildren().addAll(
                new Label("Araç Bilgilerini Girin"),
                plateField, modelField, vehicleTypeField, colorField,
                new Label("Park Alanı Seçin:"), parkingSpaceComboBox, saveButton
        );
        dialog.setScene(new Scene(vbox, 300, 300));
        dialog.show();
    }

    private boolean isParkingSpaceAvailable(String spaceNumber) {
        try (Connection connection = DatabaseConnection.getInstance()) {
            String checkQuery = "SELECT status FROM parkingspaces WHERE space_number = ?";
            try (PreparedStatement stmt = connection.prepareStatement(checkQuery)) {
                stmt.setString(1, spaceNumber);  // space_number'ı String olarak alıyoruz
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        return status.equals("available"); // Eğer park alanı 'available' ise true döndür
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
        return false; // Eğer park alanı 'available' değilse false döndür
    }



    private void loadParkingSpaces(ComboBox<String> parkingSpaceComboBox) {
        parkingSpaceComboBox.getItems().clear(); // Daha önce eklenmiş öğeleri temizle
        try (Connection connection = DatabaseConnection.getInstance()) {
            String query = "SELECT space_number FROM parkingspaces WHERE status = 'available'"; // 'available' durumundaki park alanlarını listele
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String spaceNumber = rs.getString("space_number"); // space_number'ı alıyoruz
                    parkingSpaceComboBox.getItems().add(spaceNumber); // Boş park alanlarını listele
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }




    private void addVehicleToDatabase(String username, String plate, String model, String vehicleType, String color, String selectedSpace) {
        try (Connection connection = DatabaseConnection.getInstance()) {  // Yeni bağlantı oluşturuyoruz
            String query = "INSERT INTO vehicles (owner_id, plate_number, model, vehicle_type, color, parking_space) " +
                    "VALUES ((SELECT user_id FROM parkusers WHERE username = ?), ?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, plate);
                stmt.setString(3, model);
                stmt.setString(4, vehicleType);
                stmt.setString(5, color);
                stmt.setString(6, selectedSpace); // space_number'ı String olarak ekliyoruz
                stmt.executeUpdate();
            }

            // Park alanını güncelle (boş alanı dolu olarak işaretle)
            updateParkingSpaceStatus(selectedSpace);
            String insertLogQuery = "INSERT INTO logs (vehicle_id, entry_time, space_id) " +
                    "VALUES ((SELECT vehicle_id FROM vehicles WHERE plate_number = ?), NOW(), (SELECT space_id FROM parkingspaces WHERE space_number = ?))";
            try (PreparedStatement logStmt = connection.prepareStatement(insertLogQuery)) {
                logStmt.setString(1, plate);        // Araç plakasına göre araç ID'sini al
                logStmt.setString(2, selectedSpace); // Park alanına göre alan ID'sini al
                logStmt.executeUpdate();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // MySQL Duplicate entry error code
                showError("Bu plaka numarası zaten kayıtlı! Lütfen farklı bir plaka numarası girin.");
            } else {
                System.err.println("Veritabanı hatası: " + e.getMessage());
            }
        }
    }

    private void updateParkingSpaceStatus(String spaceNumber) {
        try (Connection connection = DatabaseConnection.getInstance()) {  // Yeni bağlantı oluşturuyoruz
            // Park alanını güncelle (boş alanı dolu olarak işaretle)
            String updateQuery = "UPDATE parkingspaces SET status = 'occupied' WHERE space_number = ?";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setString(1, spaceNumber); // spaceNumber bir String olmalı
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }



    private void showRemoveVehicleDialog(String username) {
        Stage dialog = new Stage();
        VBox vbox = new VBox(10);

        // Araçları listelemek için ComboBox
        ComboBox<String> vehicleComboBox = new ComboBox<>();
        updateVehicleComboBox(username, vehicleComboBox);

        Button removeButton = new Button("Araç Çıkışı Yap");
        removeButton.setOnAction(e -> {
            String selectedPlate = vehicleComboBox.getValue();
            if (selectedPlate != null && !selectedPlate.isEmpty()) {
                removeVehicleFromDatabase(username, selectedPlate);
                updateVehicleList(username);
                dialog.close();
            } else {
                showError("Bir araç seçin!");
            }
        });

        vbox.getChildren().addAll(new Label("Araç Seçin"), vehicleComboBox, removeButton);
        dialog.setScene(new Scene(vbox, 300, 200));
        dialog.show();
    }

    private void updateVehicleComboBox(String username, ComboBox<String> vehicleComboBox) {
        vehicleComboBox.getItems().clear(); // Önceki öğeleri temizle
        try (Connection connection = DatabaseConnection.getInstance()) {
            String query = "SELECT plate_number FROM vehicles WHERE owner_id = (SELECT user_id FROM parkusers WHERE username = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String plate = rs.getString("plate_number");
                        vehicleComboBox.getItems().add(plate); // Araç plakasını ekle
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }

    private void removeVehicleFromDatabase(String username, String plate) {
        try (Connection connection = DatabaseConnection.getInstance()) {
            // Öncelikle aracın park ettiği alanı alın
            String getParkingSpaceQuery = "SELECT parking_space FROM vehicles WHERE plate_number = ? AND owner_id = (SELECT user_id FROM parkusers WHERE username = ?)";
            String parkingSpace = null;
            try (PreparedStatement stmt = connection.prepareStatement(getParkingSpaceQuery)) {
                stmt.setString(1, plate);
                stmt.setString(2, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        parkingSpace = rs.getString("parking_space");
                    }
                }
            }

            // Eğer park alanı bilgisi alındıysa, park alanını boş olarak güncelle
            if (parkingSpace != null) {
                String updateSpaceStatusQuery = "UPDATE parkingspaces SET status = 'available' WHERE space_number = ?";
                try (PreparedStatement stmt = connection.prepareStatement(updateSpaceStatusQuery)) {
                    stmt.setString(1, parkingSpace);
                    stmt.executeUpdate();
                }
            }

            // Logları sil
            String deleteLogsQuery = "DELETE FROM logs WHERE vehicle_id = (SELECT vehicle_id FROM vehicles WHERE plate_number = ? AND owner_id = (SELECT user_id FROM parkusers WHERE username = ?))";
            try (PreparedStatement deleteLogsStmt = connection.prepareStatement(deleteLogsQuery)) {
                deleteLogsStmt.setString(1, plate);
                deleteLogsStmt.setString(2, username);
                deleteLogsStmt.executeUpdate();
            }

            // Aracı veritabanından sil
            String deleteVehicleQuery = "DELETE FROM vehicles WHERE plate_number = ? AND owner_id = (SELECT user_id FROM parkusers WHERE username = ?)";
            try (PreparedStatement deleteVehicleStmt = connection.prepareStatement(deleteVehicleQuery)) {
                deleteVehicleStmt.setString(1, plate);
                deleteVehicleStmt.setString(2, username);
                deleteVehicleStmt.executeUpdate();
            }

        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }

    private void updateParkingSpaceToAvailable(String plate) {
        try (Connection connection = DatabaseConnection.getInstance()) {
            // Park alanını boş olarak işaretle
            String updateQuery = "UPDATE parkingspaces SET status = 'available' WHERE space_number = (SELECT parking_space FROM vehicles WHERE plate_number = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
                stmt.setString(1, plate);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }



    private void updateVehicleList(String username) {
        vehicleVBox.getChildren().clear();
        try (Connection connection = DatabaseConnection.getInstance()) {
            // Araçları ve park alanlarını birlikte sorgula
            String query = "SELECT plate_number, model, vehicle_type, color, parking_space FROM vehicles WHERE owner_id = (SELECT user_id FROM parkusers WHERE username = ?)";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String plate = rs.getString("plate_number");
                        String model = rs.getString("model");
                        String vehicleType = rs.getString("vehicle_type");
                        String color = rs.getString("color");
                        String parkingSpace = rs.getString("parking_space");

                        HBox vehicleBox = new HBox(10);
                        vehicleBox.setStyle("-fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5;");

                        VBox vehicleInfo = new VBox(5);
                        vehicleInfo.getChildren().addAll(
                                new Label("Plaka: " + plate),
                                new Label("Model: " + model),
                                new Label("Araç Türü: " + vehicleType),
                                new Label("Renk: " + color),
                                new Label("Park Alanı: " + parkingSpace) // Park alanı bilgisini de ekliyoruz
                        );

                        Button removeButton = new Button("Sil");
                        removeButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                        removeButton.setOnAction(e -> {
                            removeVehicleFromDatabase(username, plate);
                            updateVehicleList(username);
                        });

                        vehicleBox.getChildren().addAll(vehicleInfo, removeButton);
                        vehicleVBox.getChildren().add(vehicleBox);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Veritabanı hatası: " + e.getMessage());
        }
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}