public class VehicleLog {
    private String plateNumber;
    private String entryTime;
    private String exitTime;
    private String spaceId;

    public VehicleLog(String plateNumber, String entryTime, String exitTime,String spaceId) {
        this.plateNumber = plateNumber;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.spaceId = spaceId;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }
    public String getSpaceId(){
        return spaceId;
    }
}
