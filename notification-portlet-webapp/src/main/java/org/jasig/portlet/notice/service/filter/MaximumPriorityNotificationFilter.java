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
package org.jasig.portlet.notice.service.filter;

import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This concrete implementation of {@link INotificationFilter} filters out notices above the
 * specified priority value.  Remember that higher-priority values are lower numbers -- e.g.
 * Priority 1 is the highest priority.
 *
 * @since 3.1
 */
/* package-private */ class MaximumPriorityNotificationFilter implements INotificationFilter {

    private final int maxPriority;

    private Logger logger = LoggerFactory.getLogger(getClass());
    public MaximumPriorityNotificationFilter(int maxPriority) {
        if (maxPriority <= 0) {
            final String msg = "Argument 'maxPriority' must be greater than zero";
            throw new IllegalArgumentException(msg);
        }

        this.maxPriority = maxPriority;
    }

    @Override
    public boolean doFilter(NotificationCategory category, NotificationEntry entry) {
        return isNotHigherThanMaximumPriority(entry);
    }

    private boolean isNotHigherThanMaximumPriority(NotificationEntry entry) {
        // Remember lower number is higher priority...
        final boolean rslt = entry.getPriority() >= maxPriority
                || entry.getPriority() == NotificationEntry.PRIORITY_UNSPECIFIED;
        logger.debug("Filtering entry '{}'... maxPriority='{}', entry.priority='{}', decision='{}'",
                entry.getTitle(), maxPriority, entry.getPriority(), rslt);
        return rslt;
    }

}
