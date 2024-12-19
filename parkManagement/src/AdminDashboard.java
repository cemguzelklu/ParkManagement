import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminDashboard implements Observer {

    private TableView<ParkingSpace> parkingTable;
    private TableView<VehicleLog> vehicleTable;

    private Label totalVehiclesLabel;
    private Label parkedVehiclesLabel;
    private Label availableSpacesLabel;

    public void showDashboard(Stage stage) {
        // Park Alanları Tablosu
        parkingTable = new TableView<>();
        setupParkingTable();

        // Araç Bilgileri ve Zamanları Tablosu
        vehicleTable = new TableView<>();
        setupVehicleTable();

        // İstatistik Label'ları
        totalVehiclesLabel = new Label("Toplam Araç Sayısı: ");
        parkedVehiclesLabel = new Label("Şu An Park Halindeki Araç Sayısı: ");
        availableSpacesLabel = new Label("Boş Park Yeri Sayısı: ");

        // Çıkış Yap Butonu
        Button logoutButton = new Button("Çıkış Yap");
        logoutButton.setOnAction(e -> handleLogout(stage));

        // Verileri yükle
        loadParkingSpaces();
        loadVehicleLogs();
        loadStatistics();

        // Layout
        VBox layout = new VBox(10, parkingTable, vehicleTable, totalVehiclesLabel, parkedVehiclesLabel, availableSpacesLabel, logoutButton);
        layout.setPadding(new Insets(20));

        // Scene ve Stage ayarları
        Scene scene = new Scene(layout, 800, 600);
        stage.setTitle("Park Alanları ve Araç Bilgileri");
        stage.setScene(scene);
        stage.show();
    }

    // Çıkış yap butonunun işlevi
    private void handleLogout(Stage stage) {
        // Mevcut pencereyi kapat
        stage.close();

        // Giriş ekranını göster
        Main loginScreen = new Main();
        loginScreen.start(new Stage());  // Yeni bir Stage ile giriş ekranını aç
    }

    // PARK ALANI TABLOSU
    private void setupParkingTable() {
        TableColumn<ParkingSpace, String> spaceNumberColumn = new TableColumn<>("Park Alanı");
        spaceNumberColumn.setCellValueFactory(new PropertyValueFactory<>("spaceNumber"));

        TableColumn<ParkingSpace, String> statusColumn = new TableColumn<>("Durum");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        parkingTable.getColumns().addAll(spaceNumberColumn, statusColumn);
    }

    private void loadParkingSpaces() {
        ObservableList<ParkingSpace> spaces = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getInstance()) {
            String query = "SELECT space_number, status FROM parkingspaces";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String spaceNumber = rs.getString("space_number");
                    String status = rs.getString("status");
                    spaces.add(new ParkingSpace(spaceNumber, status));
                }
            }
        } catch (SQLException e) {
            showError("Veritabanı hatası: " + e.getMessage());
        }

        parkingTable.setItems(spaces);
    }

    // ARAÇ BİLGİLERİ VE LOG TABLOSU
    private void setupVehicleTable() {
        TableColumn<VehicleLog, String> plateNumberColumn = new TableColumn<>("Plaka");
        plateNumberColumn.setCellValueFactory(new PropertyValueFactory<>("plateNumber"));

        TableColumn<VehicleLog, String> entryTimeColumn = new TableColumn<>("Giriş Zamanı");
        entryTimeColumn.setCellValueFactory(new PropertyValueFactory<>("entryTime"));

        TableColumn<VehicleLog, String> exitTimeColumn = new TableColumn<>("Çıkış Zamanı");
        exitTimeColumn.setCellValueFactory(new PropertyValueFactory<>("exitTime"));

        vehicleTable.getColumns().addAll(plateNumberColumn, entryTimeColumn, exitTimeColumn);
    }

    private void loadVehicleLogs() {
        ObservableList<VehicleLog> logs = FXCollections.observableArrayList();

        try (Connection connection = DatabaseConnection.getInstance()) {
            String query = "SELECT v.plate_number, l.entry_time, l.exit_time, l.space_id " +
                    "FROM vehicles v " +
                    "JOIN logs l ON v.vehicle_id = l.vehicle_id";
            try (PreparedStatement stmt = connection.prepareStatement(query);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    String plateNumber = rs.getString("plate_number");
                    String entryTime = rs.getString("entry_time");
                    String exitTime = rs.getString("exit_time");
                    String spaceId = rs.getString("space_id");
                    // Yeni VehicleLog nesnesi oluşturuluyor
                    VehicleLog log = new VehicleLog(plateNumber, entryTime, exitTime, spaceId);
                    logs.add(log);  // VehicleLog nesnesini listeye ekliyoruz
                }
            }
        } catch (SQLException e) {
            showError("Veritabanı hatası: " + e.getMessage());
        }

        // Tabloyu güncelleme
        vehicleTable.setItems(logs);  // logs listesini tabloya bağlıyoruz
    }

    private void loadStatistics() {
        try (Connection connection = DatabaseConnection.getInstance()) {
            // Toplam Araç Sayısı
            String totalVehiclesQuery = "SELECT COUNT(*) AS total FROM vehicles";
            try (PreparedStatement stmt = connection.prepareStatement(totalVehiclesQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    totalVehiclesLabel.setText("Toplam Araç Sayısı: " + rs.getInt("total"));
                }
            }

            // Park Halindeki Araç Sayısı
            String occupiedSpacesQuery = "SELECT COUNT(*) AS occupied FROM parkingspaces WHERE status = 'occupied'";
            try (PreparedStatement stmt = connection.prepareStatement(occupiedSpacesQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    parkedVehiclesLabel.setText("Şu An Park Halindeki Araç Sayısı: " + rs.getInt("occupied"));
                }
            }

            // Boş Park Alanı Sayısı
            String availableSpacesQuery = "SELECT COUNT(*) AS available FROM parkingspaces WHERE status = 'available'";
            try (PreparedStatement stmt = connection.prepareStatement(availableSpacesQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    availableSpacesLabel.setText("Boş Park Yeri Sayısı: " + rs.getInt("available"));
                }
            }

        } catch (SQLException e) {
            showError("Veritabanı hatası: " + e.getMessage());
        }
    }

    // Hata mesajı gösterme fonksiyonu
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Hata");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void update(Observable observable, String message) {
        if (observable instanceof ParkingLotManager) {
            // Admin'e bildirilen mesajı işleme
            System.out.println("Admin'e bildirilen mesaj: " + message);
        }
    }

    // PARK ALANI SINIFI
    public static class ParkingSpace {
        private String spaceNumber;
        private String status;

        public ParkingSpace(String spaceNumber, String status) {
            this.spaceNumber = spaceNumber;
            this.status = status;
        }

        public String getSpaceNumber() {
            return spaceNumber;
        }

        public String getStatus() {
            return status;
        }
    }
}
