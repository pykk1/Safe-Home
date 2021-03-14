package isp.lab7.safehome;

import isp.lab7.safehome.exceptions.InvalidPinException;
import isp.lab7.safehome.exceptions.TenantAlreadyExistsException;
import isp.lab7.safehome.exceptions.TenantNotFoundException;
import isp.lab7.safehome.exceptions.TooManyAttemptsException;

import java.time.LocalDateTime;
import java.util.*;

import static java.lang.System.exit;

public class SafeHome {

    public static void main(String[] args) {

        DoorLockController doorLockController = new DoorLockController();
        Scanner scanner = new Scanner(System.in);
        List<AccessLog> accessLogList;

        System.out.println("Options............");
        System.out.println("1.Enter Pin........");
        System.out.println("2.Add Tenant.......");
        System.out.println("3.Remove Tenant....");
        System.out.println("4.Access logs......");
        System.out.println("5.Exit.............");

        while (true) {
            System.out.println("Enter option: ");
            int option = scanner.nextInt();
            accessLogList = doorLockController.getAccessLogList();
            Door door = doorLockController.getDoor();
            switch (option) {
                case 1: {
                    try {
                        System.out.println("Enter pin: ");
                        doorLockController.enterPin(scanner.next());
                    } catch (InvalidPinException e) {
                        accessLogList.add(new AccessLog("Unknown", LocalDateTime.now(), "Enter pin", door.getStatus(), "Invalid pin"));
                        System.out.println("Invalid pin(from catch)");
                    } catch (TooManyAttemptsException e) {
                        accessLogList.add(new AccessLog("Unknown", LocalDateTime.now(), "Enter pin", door.getStatus(), "Too many attempts"));
                        if (door.getStatus() == DoorStatus.CLOSED) {
                            System.out.println("You need to insert the master key in order to unlock the door");
                            System.out.println("Enter administrator password: ");
                        } else {
                            System.out.println("You need to insert the master key in order to unlock the door");
                            System.out.println("Enter administrator password: ");
                        }

                        if (scanner.next().equals(doorLockController.getMASTER_KEY())) {
                            accessLogList.add(new AccessLog(doorLockController.getMASTER_TENANT_NAME(), LocalDateTime.now(), "Enter pin", door.getStatus(), "System reset, door unlocked"));
                            System.out.println("Access granted, system unlocked.");
                            doorLockController.setNumberOfTries(0);
                            door.unlockDoor();
                        } else {
                            System.out.println("Wrong password");
                            accessLogList.add(new AccessLog("Unknown", LocalDateTime.now(), "System reset", door.getStatus(), "Wrong admin password"));
                        }
                    }
                    break;
                }
                case 2: {
                    System.out.println("You need be the administrator in order to perform this operation");
                    System.out.println("Insert admin password: ");
                    if (!scanner.next().equals(doorLockController.getMASTER_KEY())) {
                        System.out.println("Wrong pasword");
                        break;
                    }
                    System.out.println("Insert tenant name: ");
                    String tenantName = scanner.next();
                    System.out.println("Insert access key: ");
                    String accessKey = scanner.next();
                    try {
                        doorLockController.addTenant(accessKey, tenantName);
                    } catch (TenantAlreadyExistsException e) {
                        System.out.println("Tenant " + tenantName + " already exists(from catch)");
                        accessLogList.add(new AccessLog(doorLockController.getMASTER_TENANT_NAME(), LocalDateTime.now(), "Add tenant", door.getStatus(), "Tenant already exists"));
                    }
                    break;
                }
                case 3: {
                    System.out.println("You need be the administrator in order to perform this operation");
                    System.out.println("Insert admin password: ");
                    if (!scanner.next().equals(doorLockController.getMASTER_KEY())) {
                        System.out.println("Wrong pasword");
                        break;
                    }
                    System.out.println("Insert tenant name: ");
                    String tenant = scanner.next();

                    try {
                        doorLockController.removeTenant(tenant);
                    } catch (TenantNotFoundException e) {
                        System.out.println("Tenant " + tenant + " not found(from catch)");
                        accessLogList.add(new AccessLog(doorLockController.getMASTER_TENANT_NAME(), LocalDateTime.now(), "Add tenant", door.getStatus(), "Tenant not found"));
                    }
                    break;
                }
                case 4: {
                    System.out.println("You need be the administrator in order to perform this operation");
                    System.out.println("Insert admin password: ");
                    if (!scanner.next().equals(doorLockController.getMASTER_KEY())) {
                        System.out.println("Wrong pasword");
                        break;
                    }
                    accessLogList = doorLockController.getAccessLogList();
                    for (AccessLog accessLog : accessLogList)
                        System.out.println(accessLog.toString());
                    break;
                }
                case 5: {
                    System.out.println("Bye !");
                    exit(1);
                }
            }
        }
    }
}
