/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container class that holds the entries for a given category.
 * The category title is used to determine if entries from other
 * sources should be grouped together in the same category.
 */
public class NotificationCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private List<NotificationEntry> entries = new ArrayList<NotificationEntry>();

	/**
	 * Constructor.
	 */
	public NotificationCategory()
	{
	}

	/**
	 * Constructor.
	 */
	public NotificationCategory(String title, List<NotificationEntry> entries)
	{
		this.title = title;
		this.entries = entries;
	}

	/**
	 * Set the source of the data. This method will iterate through the
	 * data and set the source value for the entries and error (if any).
	 * @param source is the source of the data.
	 */
	public void setSource(String source) {
		for(NotificationEntry entry : entries)
			entry.setSource(source);
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

	@Override
	/**
	 * Returns a string representation of this class' data.
	 * 
	 * @return String.
	 */
	public String toString() {
		StringBuffer buffer = new StringBuffer(
				"org.jasig.portlet.notice.serverresponse.NotificationCategory\n"
				+ "\tCategory Title    = " + title + "\n");
		
		for(NotificationEntry entry : entries)
			buffer.append(entry.toString());

		return buffer.toString();
	}
}
