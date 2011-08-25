package org.jasig.portlet.notice.response;

import java.io.Serializable;
import java.util.Date;

/**
 * The class contains the notification information. The
 * NotificationCategory class contains all the entries
 * for the same category title.
 */
public class NotificationEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	public enum Priority{ Low, Normal, High };	
	
	private String    source;
	private String    title;
	private String    summary;
	private String    body;
	private String    link;
	private Date      startDate;
	private Date      endDate;
	private Date      dueDate;
	private Priority  priority = Priority.Low;
	private boolean   dismissed;
	private String    imageUrl;

	/**
	 * Constructor.
	 */
	public NotificationEntry()
	{
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * Returns the title attribute.
	 *
	 * @return String.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Sets the title attribute.
	 *
	 * @param title is the new value of this attribute.
	 */
    public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Returns the summary attribute.
	 *
	 * @return String.
	 */
	public String getSummary() {
		return summary;
	}

	/**
	 * Sets the summary attribute.
	 *
	 * @param summary is the new value of this attribute.
	 */
	public void setSummary(String summary) {
		this.summary = summary;
	}
	
	/**
	 * Returns the body attribute.
	 *
	 * @return String.
	 */
	public String getBody() {
		return body;
	}

	/**
	 * Sets the body attribute.
	 *
	 * @param body is the new value of this attribute.
	 */
	public void setBody(String body) {
		this.body = body;
	}
		
	/**
	 * Returns the link attribute.
	 *
	 * @return String.
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Sets the link attribute.
	 *
	 * @param link is the new value of this attribute.
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Returns the start date attribute.
	 *
	 * @return Date.
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * Sets the start date attribute.
	 *
	 * @param startDate is the new start date value.
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Returns the end date attribute.
	 *
	 * @return Date.
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * Sets the end date attribute.
	 *
	 * @param endDate is the new end date value.
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	/**
	 * Returns the due date attribute.
	 *
	 * @return Date.
	 */
	public Date getDueDate() {
		return dueDate;
	}

	/**
	 * Sets the due date attribute.
	 *
	 * @param dueDate is the new due date value.
	 */
	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	/**
	 * Returns the priority attribute.
	 *
	 * @return int.
	 */
	public Priority getPriority() {
		return priority;
	}

	/**
	 * Sets the priority attribute.
	 *
	 * @param priority is the new priority value.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	/**
	 * Returns the dismissed attribute.
	 *
	 * @return boolean.
	 */
	public boolean isDismissed() {
		return dismissed;
	}

	/**
	 * Sets the dismissed attribute.
	 *
	 * @param dismissed is the new priority dismissed.
	 */
	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	@Override
	/**
	 * Returns a string representation of this class' data.
	 * 
	 * @return String.
	 */
	public String toString() {
		return "org.jasig.portlet.notice.serverresponse.NotificationEntry\n"
				+ "\tSource     = " + source  + "\n"
				+ "\tTitle      = " + title  + "\n"
				+ "\tSummary    = " + summary  + "\n"
				+ "\tBody       = " + body  + "\n"
				+ "\tLink       = " + link  + "\n"
				+ "\tStartDate  = " + startDate  + "\n"
				+ "\tEndDate    = " + endDate  + "\n"
				+ "\tDueDate    = " + dueDate  + "\n"
				+ "\tPriority   = " + priority.toString()  + "\n"
				+ "\tDismissed  = " + dismissed  + "\n"
				+ "\tImage URL  = " + imageUrl + "\n";
	}
}
