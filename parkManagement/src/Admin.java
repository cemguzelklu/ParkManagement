import javafx.stage.Stage;

public class Admin extends User {
    public Admin(String username) {
        super(username);
    }

    @Override
    public void performRole(Stage stage) {
        new AdminDashboard().showDashboard(stage);
    }
}