import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class ParkingLotManager extends java.util.Observable {
    private List<Observer> observers = new ArrayList<>();
    private int availableSpaces;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    public void vehicleEntry() {
        // Araç giriş işlemi
        availableSpaces--;
        setChanged(); // Observable'ın değiştiğini belirtiyoruz
        notifyObservers("Araç giriş yaptı. Güncel doluluk: " + availableSpaces + " boş yer.");
    }

    public void vehicleExit() {
        // Araç çıkış işlemi
        availableSpaces++;
        setChanged(); // Observable'ın değiştiğini belirtiyoruz
        notifyObservers("Araç çıkış yaptı. Güncel doluluk: " + availableSpaces + " boş yer.");
    }

    @Override
    public void notifyObservers(Object arg) {
        super.notifyObservers(arg); // Observable'ın notifyObservers metodunu kullanıyoruz
        for (Observer observer : observers) {
            observer.update((Observable) this, (String) arg); // Observable ve mesajı gözlemcilere iletiyoruz
        }
    }
}
