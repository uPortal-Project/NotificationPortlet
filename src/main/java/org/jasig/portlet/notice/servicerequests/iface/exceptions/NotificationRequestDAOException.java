package org.jasig.portlet.notice.servicerequests.iface.exceptions;

public class NotificationRequestDAOException extends Exception {

    private static final long serialVersionUID = 1L;

    public NotificationRequestDAOException() {
        super();
    }

    public NotificationRequestDAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotificationRequestDAOException(String message) {
        super(message);
    }

    public NotificationRequestDAOException(Throwable cause) {
        super(cause);
    }
    
}