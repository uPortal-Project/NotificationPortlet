package org.jasig.portlet.notice.servicerequests.iface.exceptions;

public class NotificationRequestServiceException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotificationRequestServiceException() {
        super();
    }

    public NotificationRequestServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationRequestServiceException(String message) {
        super(message);
    }

    public NotificationRequestServiceException(Throwable cause) {
        super(cause);
    }
    
}