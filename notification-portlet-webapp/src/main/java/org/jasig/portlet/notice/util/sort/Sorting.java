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

    public static NotificationResponse sort(PortletRequest req, NotificationResponse data) {

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
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

        final Comparator<NotificationEntry> comparator = chooseConfiguredComparator(req);
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

    private static Comparator<NotificationEntry> chooseConfiguredComparator(PortletRequest req) {

        final PortletPreferences prefs = req.getPreferences();
        final String strategyName = prefs.getValue(SORT_STRATEGY_PREFERENCE, null);
        if (strategyName == null) {
            // No strategy means "natural" ordering;  we won't be sorting...
            return null;
        }

        // We WILL be sorting;  work out the details...
        final SortStrategy strategy = SortStrategy.valueOf(strategyName);
        final String orderName = prefs.getValue(SORT_ORDER_PREFERENCE, SORT_ORDER_DEFAULT);
        final SortOrder order = SortOrder.valueOf(orderName);

        return order.equals(SortOrder.ASCENDING)
                ? strategy.getComparator()                             // Default/ascending order
                : Collections.reverseOrder(strategy.getComparator());  // Descending order

    }

}
