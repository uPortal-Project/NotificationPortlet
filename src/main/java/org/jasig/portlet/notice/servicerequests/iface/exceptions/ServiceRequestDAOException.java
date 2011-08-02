package org.jasig.portlet.notice.servicerequests.iface.exceptions;

public class ServiceRequestDAOException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceRequestDAOException() {
		super();
	}

	public ServiceRequestDAOException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceRequestDAOException(String message) {
		super(message);
	}

	public ServiceRequestDAOException(Throwable cause) {
		super(cause);
	}
	
}
