package isp.lab7.safehome;

import isp.lab7.safehome.exceptions.*;

import java.time.LocalDateTime;
import java.util.*;

public class DoorLockController implements ControllerInterface {
    private Map<Tenant, AccessKey> validAccess;
    private List<AccessLog> accessLogList;
    private Door door = new Door(DoorStatus.CLOSED);
    private String MASTER_KEY;
    private String MASTER_TENANT_NAME;
    private int numberOfTries = 0;

    public DoorLockController() {
        this.validAccess = new HashMap<>();
        this.accessLogList = new ArrayList<>();
        this.MASTER_KEY = ControllerInterface.MASTER_KEY;
        this.MASTER_TENANT_NAME = ControllerInterface.MASTER_TENANT_NAME;
    }

    @Override
    public DoorStatus enterPin(String pin) throws InvalidPinException, TooManyAttemptsException {

        //Due to tenant being the Key in the Map, I need to get the tenant name to use it for logs
        String tenant = "";
        for (Map.Entry<Tenant, AccessKey> entry : validAccess.entrySet()) {
            if (entry.getValue().getPin().equals(pin)) {
                tenant = entry.getKey().getName();
            }
        }

        if (numberOfTries > 1) {
            throw new TooManyAttemptsException("Too many attempts, master key needed");
        }

        if (!validAccess.containsValue(new AccessKey(pin))) {
            numberOfTries++;
            throw new InvalidPinException("Wrong pin");
        } else {
            this.numberOfTries = 0;
            if (door.getStatus() == DoorStatus.CLOSED) {
                door.unlockDoor();
                accessLogList.add(new AccessLog(tenant, LocalDateTime.now(), "Unlocking door", door.getStatus(), "Success"));
                System.out.println("Door unlocked by " + tenant);
                return door.getStatus();

            } else {
                door.lockDoor();
                accessLogList.add(new AccessLog(tenant, LocalDateTime.now(), "Locking door", door.getStatus(), "Success"));
                System.out.println("Door locked by " + tenant);

            }
        }
        return DoorStatus.CLOSED; //I think this shouldn't return anything
    }

    @Override
    public void addTenant(String pin, String name) throws TenantAlreadyExistsException {

        if (this.validAccess.containsKey(new Tenant(name))) {
            throw new TenantAlreadyExistsException("Tenant already exists");
        } else {
            this.validAccess.put(new Tenant(name), new AccessKey(pin));
            System.out.println("Tenant " + name + " with key " + pin + " added");
            accessLogList.add(new AccessLog(MASTER_TENANT_NAME, LocalDateTime.now(), "Add tenant", door.getStatus(), "Success"));
        }

    }

    @Override
    public void removeTenant(String name) throws TenantNotFoundException {

        if (!this.validAccess.containsKey(new Tenant(name))) {
            throw new TenantNotFoundException("Tenant not found");
        } else {
            this.validAccess.remove(new Tenant(name));
            System.out.println("Tenant " + name + " removed");
            accessLogList.add(new AccessLog(MASTER_TENANT_NAME, LocalDateTime.now(), "Add tenant", door.getStatus(), "Success"));
        }
    }


    public List<AccessLog> getAccessLogList() {
        return accessLogList;
    }

    public Door getDoor() {
        return door;
    }

    public String getMASTER_KEY() {
        return MASTER_KEY;
    }

    public String getMASTER_TENANT_NAME() {
        return MASTER_TENANT_NAME;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }



}
