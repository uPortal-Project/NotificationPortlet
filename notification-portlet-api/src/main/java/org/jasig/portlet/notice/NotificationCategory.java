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

package org.jasig.portlet.notice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Container class that holds the entries for a given category.
 * The category title is used to determine if entries from other
 * sources should be grouped together in the same category.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class NotificationCategory implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title;
	private List<NotificationEntry> entries;

	/**
	 * Constructor.
	 */
	public NotificationCategory() {
	    entries = new ArrayList<NotificationEntry>();
	}

	/**
	 * Constructor.
	 */
	public NotificationCategory(String title, List<NotificationEntry> entries) {
		this.title = title;
		this.entries = new ArrayList<NotificationEntry>(entries);  // defensive copy
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<NotificationEntry> getEntries() {
		return Collections.unmodifiableList(entries);
	}

	public void setEntries(List<NotificationEntry> entries) {
		this.entries = new ArrayList<NotificationEntry>(entries);
	}	                            

	public void addEntries(List<NotificationEntry> newEntries) {
	    this.entries.addAll(newEntries);
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationCategory [title=");
        builder.append(title);
        builder.append(", entries=");
        builder.append(entries);
        builder.append("]");
        return builder.toString();
    }

}
