package org.jasig.portlet.notice.serviceresponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jasig.portlet.notice.source.NotificationIdentifier;


public class NotificationCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private List<NotificationEntry> entries = new ArrayList<NotificationEntry>();
	private NotificationIdentifier source;

	/**
	 * Constructor.
	 */
	public NotificationCategory()
	{
	}

	/**
	 * Constructor.
	 */
	public NotificationCategory(String title, List<NotificationEntry> entries, NotificationIdentifier source)
	{
		this.title = title;
		this.entries = entries;
		this.source = source;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<NotificationEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<NotificationEntry> entries) {
		this.entries = entries;
	}	                            

	public void addEntries(List<NotificationEntry> newEntries) {
		for(NotificationEntry entry : newEntries)
			entries.add(entry);
	}	                            

	public void clearEntries() {
		entries.size();
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
		StringBuffer buffer = new StringBuffer(
				"org.jasig.portlet.notice.serverresponse.NotificationCategory\n"
				+ "\tCategory Title    = " + title  + "\n"
				+ "\tSource Title      = " + source.getTitle() + "\n"
				+ "\tSource Identifier = " + source.getIdentifier() + "\n");
		
		for(NotificationEntry entry : entries)
			buffer.append(entry.toString());

		return buffer.toString();
	}
}
