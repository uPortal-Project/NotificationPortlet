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
import java.util.Set;

/**
 * This class contains all the categories and errors
 * retrieved by an INotificationService. It is also
 * used to aggregate all the NotificationResponses from
 * various services into a single NotificationResponse.
 * The data from the overall NotificationResponse instance
 * is returned to the portlet to be rendered.
 */
public class NotificationResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<NotificationCategory> categories;
	private List<NotificationError> errors;

    public NotificationResponse() {
        categories = new ArrayList<NotificationCategory>();
        errors = new ArrayList<NotificationError>();
    }

    public NotificationResponse(NotificationResponse response) {
        this(response.getCategories(), response.getErrors());
    }

	public NotificationResponse(List<NotificationCategory> categories, List<NotificationError> errors) {
		this.categories = new ArrayList<NotificationCategory>(categories);  // defensive copy
		this.errors = new ArrayList<NotificationError>(errors);  // defensive copy
	}

	public List<NotificationCategory> getCategories() {
		return Collections.unmodifiableList(categories);
	}

	public void setCategories(List<NotificationCategory> categories) {
		this.categories = new ArrayList<NotificationCategory>(categories);  // defensive copy
	}

	public List<NotificationError> getErrors() {
		return Collections.unmodifiableList(errors);
	}

	public void setErrors(List<NotificationError> errors) {
		this.errors = new ArrayList<NotificationError>(errors); // defensive copy
	}

    /**
     * Combine the contents of this response with the provided response and 
     * return a <b>new instance</b> of {@link NotificationResponse}.  The 
     * original instances are unchanged.
     * 
     * @param A new {@link NotificationResponse} that contains data from both 
     * originals
     */
    public NotificationResponse combine(NotificationResponse response) {
        NotificationResponse rslt = new NotificationResponse(this);
        rslt.addCategories(response.getCategories());
        rslt.addErrors(response.getErrors());
        return rslt;
    }

    /**
     * Return a <b>new instance</b> of {@link NotificationResponse} from which 
     * the specified errors have been removed.  The original instances are 
     * unchanged.
     * 
     * @param A new {@link NotificationResponse} that does not contain the 
     * specified errors
     */
    public NotificationResponse filterErrors(Set<Integer> hiddenErrorKeys) {
        NotificationResponse rslt = new NotificationResponse(this);
        List<NotificationError> filteredErrors = new ArrayList<NotificationError>();
        for (NotificationError r : errors) {
            if (!hiddenErrorKeys.contains(r.getKey())) {
                filteredErrors.add(r);
            }
        }
        rslt.setErrors(filteredErrors);
        return rslt;
	}
	
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("NotificationResponse [categories=");
        builder.append(categories);
        builder.append(", errors=");
        builder.append(errors);
        builder.append("]");
        return builder.toString();
    }

    /*
	 * Implementation
	 */

    /** 
     * Insert the given categories and their entries into the any existing
     * categories of the same title. If a category doesn't match an existing
     * one, add it to the list.
     * @param newCategories collection of new categories and their entries.
     */
    private void addCategories(List<NotificationCategory> newCategories) {
        
        //check if an existing category (by the same title) already exists
        //if so, add the new categories entries to the existing category
        for(NotificationCategory newCategory : newCategories) {
            boolean found = false;

            for(NotificationCategory myCategory : categories) {
                if(myCategory.getTitle().toLowerCase().equals(newCategory.getTitle().toLowerCase())){
                    found = true;
                    myCategory.addEntries(newCategory.getEntries());
                }
            }
            
            if(!found)
                categories.add(newCategory);
        }
    }

    private void addErrors(List<NotificationError> newErrors) {
        for(NotificationError error : newErrors)
            errors.add(error);
    }

}
