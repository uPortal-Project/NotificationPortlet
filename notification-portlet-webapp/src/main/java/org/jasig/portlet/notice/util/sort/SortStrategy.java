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

import java.util.Comparator;
import java.util.Date;

import org.jasig.portlet.notice.NotificationEntry;

/**
 * Indicates how to sort {@link NotificationEntry} objects within a
 * {@code NotificationCategory}.  Sorting options are typically based on
 * strongly-typed member variables.  Non-null values always come before null
 * values.
 */
public enum SortStrategy {

    PRIORITY {
        private final Comparator<NotificationEntry> comparator =
                Comparator.comparingInt(NotificationEntry::getPriority);

        @Override
        public Comparator<NotificationEntry> getComparator() {
            return comparator;
        }
    },

    DUE_DATE {
        private final Comparator<NotificationEntry> comparator = (o1, o2) -> {
            final Date o1due = o1.getDueDate();
            final Date o2due = o2.getDueDate();

            int rslt = 0;  // default
            if (o1due == null && o2due != null) {
                // Non-null before null...
                rslt = -1;
            } else if (o1due != null && o2due == null) {
                // Non-null before null...
                rslt = 1;
            } else if (o1due != null && o2due != null) {
                // Compare time in millis
                final long difference = o1due.getTime() - o2due.getTime();
                if (difference != 0L) {
                    // Convert to something guaranteed to
                    // be within the precision of an integer
                    rslt = difference > 0L ? 1 : -1;
                }
            }
            return rslt;
        };

        @Override
        public Comparator<NotificationEntry> getComparator() {
            return comparator;
        }
    };

    public abstract Comparator<NotificationEntry> getComparator();

}
