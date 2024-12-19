public class ParkingSpace {
    private String spaceNumber;
    private State currentState;

    public ParkingSpace(String spaceNumber) {
        this.spaceNumber = spaceNumber;
        this.currentState = new EmptyState(); // Başlangıçta park alanı boş olacak
    }

    public String getSpaceNumber() {
        return spaceNumber;
    }

    public String getStatus() {
        return currentState instanceof EmptyState ? "Boş" : "Dolu";
    }

    public void setStatus(String status) {
        // Durumu değiştirmek için state nesnesini güncelle
        if ("Boş".equals(status)) {
            this.currentState = new EmptyState();
        } else if ("Dolu".equals(status)) {
            this.currentState = new OccupiedState();
        }
    }

    public void changeState() {
        currentState.handle(this);
    }
}
