package org.jasig.portlet.notice.response;

import java.io.Serializable;

/**
 * This class is used report errors when a INotificationService
 * tries to retrieve notifications but is unable to due to
 * some error.
 */
public class NotificationError implements Serializable {
	private static final long serialVersionUID = 1L;

	private String error;
	private String source;
	
	public NotificationError(){}
	
	public NotificationError(String error, String source)
	{
		this.error = error;
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	@Override
	/**
	 * Returns a string representation of this class' data.
	 * 
	 * @return String.
	 */
	public String toString() {
		return "org.jasig.portlet.notice.serverresponse.NotificationError\n"
				+ "\tSource            = " + source  + "\n"
				+ "\tError             = " + error  + "\n";
	}
}
