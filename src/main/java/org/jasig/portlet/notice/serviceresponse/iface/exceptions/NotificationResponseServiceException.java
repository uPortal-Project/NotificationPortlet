package org.jasig.portlet.notice.serviceresponse.iface.exceptions;

public class NotificationResponseServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotificationResponseServiceException() {
        super();
    }

    public NotificationResponseServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationResponseServiceException(String message) {
        super(message);
    }

    public NotificationResponseServiceException(Throwable cause) {
        super(cause);
    }
    
}