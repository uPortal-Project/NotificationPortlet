package org.jasig.portlet.notice.service.exceptions;

public class NotificationServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotificationServiceException() {
        super();
    }

    public NotificationServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationServiceException(String message) {
        super(message);
    }

    public NotificationServiceException(Throwable cause) {
        super(cause);
    }
    
}