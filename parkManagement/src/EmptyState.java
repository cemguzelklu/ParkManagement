public class EmptyState implements State {
    @Override
    public void handle(ParkingSpace parkingSpace) {
        parkingSpace.setStatus("Boş");
        System.out.println("Park alanı boş.");
    }
}
