/*
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.notice.util;

import org.jasig.portlet.notice.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link INotificationService} beans return {@link NotificationResponse} objects, which are tree
 * structures wherein notices are grouped by category.  Most APIs and clients probably prefer
 * notices in a list (often sorted).  This utility provides a standard way to flatten a
 * <code>NotificationResponse</code>.
 */
@Component
public class NotificationResponseFlattener {

    public List<NotificationEntry> flatten(NotificationResponse response) {

        // We will be modifying the entries to add the category since it will not be represented in the uncategorized list, so create a
        // copy of the data if it is not already cloned.
        final NotificationResponse clone = response.cloneIfNotCloned();

        // Combine all categories into one list and create a category list.  The category list will include categories that have no elements so
        // it can be used for a consistent filtering interface if the data source provides a full list.  (This is helpful for an interface such as
        // student jobs where you always want the user to see a consistent list of all the categories for a category filter).
        List<NotificationEntry> rslt = new ArrayList<>();
        final Set<String> categoryList = new HashSet<>();
        for (final NotificationCategory notificationCategory : clone.getCategories()) {
            categoryList.add(notificationCategory.getTitle());
            addAndCategorizeEntries(rslt, notificationCategory);
        }

        return rslt;

    }

    /*
     * Implementation
     */

    /**
     * Add all entries from the notification category to the <code>allEntries</code> list after adding an attribute 'category' that contains
     * the category.  That allows UIs that want the convenience of an uncategorized list, such as dataTables, to obtain the data in a simple
     * format that requires no additional processing but maintains the knowledge of the category of the entries.
     *
     * @param allEntries List of all entries
     * @param notificationCategory <code>NotificationCategory</code> to add its entries to the <code>allEntries</code> list
     */
    private void addAndCategorizeEntries(List<NotificationEntry> allEntries, NotificationCategory notificationCategory) {
        for (NotificationEntry entry : notificationCategory.getEntries()) {
            List<NotificationAttribute> attrs = new ArrayList<>(entry.getAttributes());
            attrs.add(new NotificationAttribute("category", notificationCategory.getTitle()));
            entry.setAttributes(attrs);
            allEntries.add(entry);
        }
    }

}
