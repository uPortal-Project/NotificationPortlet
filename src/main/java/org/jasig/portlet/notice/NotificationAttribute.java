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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents an extra, weakly-typed piece of meta data attached to a 
 * {@link NotificationEntry}.  These attributes are normally displayed, 
 * as-provided, on the details screen. 
 */
public class NotificationAttribute implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private List<String> values = Collections.emptyList();

	public NotificationAttribute() {}

	/**
	 * Shortcut constructor.
	 */
    public NotificationAttribute(String name, String value) {
        this(name, Arrays.asList(new String[] { value }));
    }

    public NotificationAttribute(String name, List<String> values) {
        this.name = name;
        this.values = new ArrayList<String>(values);  // defensive copy
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getValues() {
		return Collections.unmodifiableList(values);
	}

	public void setValues(List<String> values) {
		this.values = new ArrayList<String>(values);  // defensive copy
	}

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationAttribute [name=");
        builder.append(name);
        builder.append(", values=");
        builder.append(values);
        builder.append("]");
        return builder.toString();
    }
	
}
