package isp.lab7.safehome.exceptions;

public class TenantNotFoundException extends Exception {
    public TenantNotFoundException(String message) {
        super(message);
    }
}