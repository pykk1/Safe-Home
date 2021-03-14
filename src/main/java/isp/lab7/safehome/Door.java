package isp.lab7.safehome;

public class Door {
    private DoorStatus status;

    public Door(DoorStatus status) {
        this.status = status;
    }

    public void lockDoor() {
        this.status = DoorStatus.CLOSED;
    }

    public void unlockDoor() {
        this.status = DoorStatus.OPEN;
    }

    public DoorStatus getStatus() {
        return status;
    }
}
