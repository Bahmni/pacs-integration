package org.bahmni.module.pacsintegration.exception;

public class LocationResolutionException extends RuntimeException {
    public LocationResolutionException(String message) {
        super(message);
    }

    public LocationResolutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
