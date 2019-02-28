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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility for sorting {@link NotificationEntry} objects, either within a List
 * or within their {@link NotificationCategory} containers.
 *
 * @author drewwills
 */
public final class Sorting {

    public static final String SORT_STRATEGY_PARAMETER_NAME = "sort";
    public static final String SORT_ORDER_PARAMETER_NAME = "order";

    /**
     * Used to define a portlet preference that indicates the way
     * {@link NotificationEntry} objects should be sorted within their
     * {@link NotificationCategory} containers.  Using a {@link SortStrategy} is
     * completely optional;  if none is specified, no sorting will be applied.
     *
     * @deprecated Part of the legacy, Portlet-based API.
     */
    @Deprecated
    public static final String SORT_STRATEGY_PREFERENCE = "Sorting.sortStrategy";

    /**
     * Either ASCENDING (default) or DECENDING.
     *
     * @deprecated Part of the legacy, Portlet-based API.
     */
    @Deprecated
    public static final String SORT_ORDER_PREFERENCE = "Sorting.sortOrder";

    public static final String SORT_ORDER_DEFAULT = SortOrder.ASCENDING.name();

    private static final Logger LOGGER = LoggerFactory.getLogger(Sorting.class);

    public static NotificationResponse sort(HttpServletRequest req, NotificationResponse data) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
        if (comparator == null) {
            // No sorting;  we're done...
            return data;
        }

        return sortNotificationResponse(comparator, data);

    }

    public static List<NotificationEntry> sort(HttpServletRequest req, List<NotificationEntry> entries) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
        if (comparator == null) {
            // No sorting;  we're done...
            return entries;
        }

        final List<NotificationEntry> rslt = new ArrayList<>(entries);  // defensive copy
        rslt.sort(comparator);

        return rslt;

    }

    /**
     * @deprecated Part of the legacy, Portlet-based API.
     */
    @Deprecated
    public static NotificationResponse sort(PortletRequest req, NotificationResponse data) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
        if (comparator == null) {
            // No sorting;  we're done...
            return data;
        }

        return sortNotificationResponse(comparator, data);

    }

    /**
     * @deprecated Part of the legacy, Portlet-based API.
     */
    @Deprecated
    public static List<NotificationEntry> sort(PortletRequest req, List<NotificationEntry> entries) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
        if (comparator == null) {
            // No sorting;  we're done...
            return entries;
        }

        final List<NotificationEntry> rslt = new ArrayList<>(entries);  // defensive copy
        rslt.sort(comparator);

        return rslt;

    }

    /*
     * Implementation
     */

    private static Comparator<NotificationEntry> chooseConfiguredComparator(HttpServletRequest req) {

        final String strategyName = req.getParameter(SORT_STRATEGY_PARAMETER_NAME);
        if (strategyName == null) {
            // No strategy means "natural" ordering;  we won't be sorting...
            return null;
        }

        // We WILL be sorting;  work out the details...
        try {
            final SortStrategy strategy = SortStrategy.valueOf(strategyName.toUpperCase()); // tolerant of case mismatch
            final String orderName = req.getParameter(SORT_ORDER_PARAMETER_NAME);
            final SortOrder order = StringUtils.isNotBlank(orderName)
                    ? SortOrder.valueOf(orderName.toUpperCase()) // tolerant of case mismatch
                    : SortOrder.valueOf(SORT_ORDER_DEFAULT);

            return order.equals(SortOrder.ASCENDING)
                    ? strategy.getComparator()                             // Default/ascending order
                    : Collections.reverseOrder(strategy.getComparator());  // Descending order
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unable to sort based on parameters {}='{}' and {}='{}'",
                    SORT_STRATEGY_PARAMETER_NAME,
                    strategyName,
                    SORT_ORDER_PARAMETER_NAME,
                    req.getParameter(SORT_ORDER_PARAMETER_NAME));
        }

        // We didn't succeed in selecting a strategy & order
        return null;

    }

    /**
     * @deprecated Part of the legacy, Portlet-based API.
     */
    @Deprecated
    private static Comparator<NotificationEntry> chooseConfiguredComparator(PortletRequest req) {

        final PortletPreferences prefs = req.getPreferences();
        final String strategyName = prefs.getValue(SORT_STRATEGY_PREFERENCE, null);
        if (strategyName == null) {
            // No strategy means "natural" ordering;  we won't be sorting...
            return null;
        }

        // We WILL be sorting;  work out the details...
        try {
            final SortStrategy strategy = SortStrategy.valueOf(strategyName.toUpperCase()); // tolerant of case mismatch
            final String orderName = req.getParameter(SORT_ORDER_PARAMETER_NAME);
            final SortOrder order = StringUtils.isNotBlank(orderName)
                    ? SortOrder.valueOf(orderName.toUpperCase()) // tolerant of case mismatch
                    : SortOrder.valueOf(SORT_ORDER_DEFAULT);

            return order.equals(SortOrder.ASCENDING)
                    ? strategy.getComparator()                             // Default/ascending order
                    : Collections.reverseOrder(strategy.getComparator());  // Descending order
        } catch (IllegalArgumentException e) {
            LOGGER.warn("Unable to sort based on parameters {}='{}' and {}='{}'",
                    SORT_STRATEGY_PARAMETER_NAME,
                    strategyName,
                    SORT_ORDER_PARAMETER_NAME,
                    req.getParameter(SORT_ORDER_PARAMETER_NAME));
        }

        // We didn't succeed in selecting a strategy & order
        return null;

    }

    private static NotificationResponse sortNotificationResponse(Comparator<NotificationEntry> comparator, NotificationResponse data) {
        // Sort each category...
        final List<NotificationCategory> copies = new ArrayList<>();
        for (NotificationCategory category : data.getCategories()) {
            final List<NotificationEntry> entries = new ArrayList<>(category.getEntries());  // defensive copy
            entries.sort(comparator);
            copies.add(new NotificationCategory(category.getTitle(), entries));
        }

        return new NotificationResponse(copies, data.getErrors());
    }

}
