package org.jasig.portlet.notice.serviceresponse;

import java.io.Serializable;

import org.jasig.portlet.notice.source.NotificationIdentifier;

public class NotificationError implements Serializable {
	private static final long serialVersionUID = 1L;

	private String error;
	private NotificationIdentifier source;
	
	public NotificationError(){}
	
	public NotificationError(String error, NotificationIdentifier source)
	{
		this.error = error;
		this.source = source;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public NotificationIdentifier getSource() {
		return source;
	}

	public void setSource(NotificationIdentifier source) {
		this.source = source;
	}

	@Override
	/**
	 * Returns a string representation of this class' data.
	 * 
	 * @return String.
	 */
	public String toString() {
		return "org.jasig.portlet.notice.serverresponse.NotificationError\n"
				+ "\tError             = " + error  + "\n"
				+ "\tSource Title      = " + source.getTitle() + "\n"
				+ "\tSource Identifier = " + source.getIdentifier() + "\n";
	}
}
