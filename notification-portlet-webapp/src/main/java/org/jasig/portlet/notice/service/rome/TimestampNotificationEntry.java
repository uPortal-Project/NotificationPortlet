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
package org.jasig.portlet.notice.service.rome;

import org.jasig.portlet.notice.NotificationEntry;

/* package-private */ class TimestampNotificationEntry extends NotificationEntry implements Comparable<TimestampNotificationEntry> {
    
    private static final long serialVersionUID = 1L;

    // Instance Members.
    private final long timestamp;

    public TimestampNotificationEntry(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(TimestampNotificationEntry notice) {
        final long diff = this.timestamp - notice.timestamp;
        if (diff > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        } else if (diff < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        } else {
            return (int) diff;
        }
    }

}
