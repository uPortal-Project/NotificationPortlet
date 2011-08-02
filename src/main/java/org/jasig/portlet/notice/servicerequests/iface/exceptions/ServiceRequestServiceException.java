package org.jasig.portlet.notice.servicerequests.iface.exceptions;

public class ServiceRequestServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public ServiceRequestServiceException() {
		super();
	}

	public ServiceRequestServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceRequestServiceException(String message) {
		super(message);
	}

	public ServiceRequestServiceException(Throwable cause) {
		super(cause);
	}
	
}
