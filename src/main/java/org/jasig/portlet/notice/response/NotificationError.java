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
	private int    key;   //hashcode of the "source:error"
	
	public NotificationError(){}
	
	public NotificationError(String error, String source)
	{
		this.error = error;
		this.source = source;
		setKey(source, error);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		setKey(source, error);
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
		setKey(source, error);
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}

	public void setKey(String source, String error) {
		String keyString = source + ":" + error;
		this.key = keyString.hashCode();
	}

	public boolean equals(Object object)
	{
		if(object instanceof NotificationError)
		{
			NotificationError temp = (NotificationError)object;
			
			return (temp.key == key);
		}
		
		return false;
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
				+ "\tError             = " + error  + "\n"
				+ "\tKey               = " + key  + "\n";
	}
}
