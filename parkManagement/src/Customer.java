import javafx.stage.Stage;

public class Customer extends User {
    public Customer(String username) {
        super(username);
    }

    @Override
    public void performRole(Stage stage) {
        new UserDashboard().showDashboard(stage,username);
    }
}