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
package org.jasig.portlet.notice.util.sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

/**
 * Utility for sorting {@link NotificationEntry} objects, either within a List
 * or within their {@link NotificationCategory} containers.
 *
 * @author drewwills
 */
public final class Sorting {

    /**
     * Used to define a portlet preference that indicates the way
     * {@link NotificationEntry} objects should be sorted within their
     * {@link NotificationCategory} containers.  Using a {@link SortStrategy} is
     * completely optional;  if none is specified, no sorting will be applied.
     */
    public static final String SORT_STRATEGY_PREFERENCE = "Sorting.sortStrategy";

    /**
     * Either ASCENDING (default) or DECENDING.
     */
    public static final String SORT_ORDER_PREFERENCE = "Sorting.sortOrder";
    public static final String SORT_ORDER_DEFAULT = SortOrder.ASCENDING.name();

    public static final String REQUEST_PARAM_SORT = "sort";
    public static final String REQUEST_PARAM_ORDER = "order";


    public static NotificationResponse sort(PortletRequest req, NotificationResponse data) {
        final PortletPreferences prefs = req.getPreferences();

        return sort(prefs.getValue(SORT_STRATEGY_PREFERENCE, null), prefs.getValue(SORT_ORDER_PREFERENCE, SORT_ORDER_DEFAULT), data);

    }

    public static NotificationResponse sort(String sortStrategy, String sortOrder, NotificationResponse data) {
        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(sortStrategy, sortOrder);
        if (comparator == null) {
            // No sorting;  we're done...
            return data;
        }

        // Sort each category...
        final List<NotificationCategory> copies = new ArrayList<NotificationCategory>();
        for (NotificationCategory category : data.getCategories()) {
            final List<NotificationEntry> entries = new ArrayList<NotificationEntry>(category.getEntries());  // defensive copy
            Collections.sort(entries, comparator);
            copies.add(new NotificationCategory(category.getTitle(), entries));
        }

        return new NotificationResponse(copies, data.getErrors());

    }

    public static List<NotificationEntry> sort(PortletRequest req, List<NotificationEntry> entries) {
        final PortletPreferences prefs = req.getPreferences();

        return sort(prefs.getValue(SORT_STRATEGY_PREFERENCE, null), prefs.getValue(SORT_ORDER_PREFERENCE, SORT_ORDER_DEFAULT), entries);

    }

    public static List<NotificationEntry> sort(String sortStrategy, String sortOrder, List<NotificationEntry> entries) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(sortStrategy, sortOrder);
        if (comparator == null) {
            // No sorting;  we're done...
            return entries;
        }

        final List<NotificationEntry> rslt = new ArrayList<NotificationEntry>(entries);  // defensive copy
        Collections.sort(rslt, comparator);

        return rslt;

    }

    /*
     * Implementation
     */

    public static Comparator<NotificationEntry> chooseConfiguredComparator(String sortStrategy, String sortOrder) {

        try {
            final SortStrategy strategy = SortStrategy.valueOf(sortStrategy.toUpperCase());

            SortOrder order = SortOrder.ASCENDING; // Default
            try {
                order = SortOrder.valueOf(sortOrder.toUpperCase());
            } catch (Exception e) {
                // value not found or not known.  Stick with default order for sort strategy.
            }

            return (order.equals(SortOrder.ASCENDING) || order.equals(SortOrder.ASC))
                    ? strategy.getComparator()                             // Default/ascending order
                    : Collections.reverseOrder(strategy.getComparator());  // Descending order
        } catch (Exception e) {
            // SortStrategy value not found or not known.  Stick with natural sorting / ordering.
            return null;
        }
    }

}
