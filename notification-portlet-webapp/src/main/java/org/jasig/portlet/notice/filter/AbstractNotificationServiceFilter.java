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
