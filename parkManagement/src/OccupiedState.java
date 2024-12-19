public class OccupiedState implements State {
    @Override
    public void handle(ParkingSpace parkingSpace) {
        parkingSpace.setStatus("Dolu");
        System.out.println("Park alanı dolu.");
    }
}
