import javafx.beans.Observable;

public interface Observer {
    void update(Observable observable, String message);
}
