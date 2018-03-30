package org.jasig.portlet.notice.filter;

import org.jasig.portlet.notice.INotificationServiceFilter;

/**
 * Base class for conclrete {@link INotificationServiceFilter} implementations that provides an
 * implementation of <code>Comparable.compareTo</code>.
 *
 * @since 4.0
 */
public abstract class AbstractNotificationServiceFilter implements INotificationServiceFilter {

    /*
     * Helper constants for ordering;  based on prime numbers
     */
    public static final int ORDER_FIRST = Integer.MIN_VALUE;
    public static final int ORDER_VERY_EARLY = -100;
    public static final int ORDER_EARLY = -10;
    public static final int ORDER_NORMAL = 0;
    public static final int ORDER_LATE = 10;
    public static final int ORDER_VERY_LATE = 100;
    public static final int ORDER_LAST = Integer.MAX_VALUE;

    private final int order;

    protected AbstractNotificationServiceFilter(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    public int compareTo(INotificationServiceFilter filter) {
        return order - filter.getOrder();
    }

}
