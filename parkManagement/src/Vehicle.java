public class Vehicle {
    private String plateNumber;
    private String model;
    private String vehicleType;
    private String color;
    private String parkingSpace;

    public Vehicle(String plateNumber, String model, String vehicleType, String color,String parkingSpace) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.vehicleType = vehicleType;
        this.color = color;
        this.parkingSpace=parkingSpace;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getModel() {
        return model;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public String getColor() {
        return color;
    }


    public String getParkingSpace(){
        return parkingSpace;
    }

    @Override
    public String toString() {
        return plateNumber + " - " + model;
    }
}
