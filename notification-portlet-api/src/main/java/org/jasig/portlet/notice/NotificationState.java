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
package org.jasig.portlet.notice;

import java.util.Date;
import java.util.Set;

/**
 * Represents a state that a {@link NotificationEntry} is currently in, such as
 * <em>new</em>, <em>completed</em>, or <em>favorated</em>.  This list is
 * intended to be comprehensive, but new states may be added in the future as
 * needed.  Notification data sources are afforded reasonable latitude as to
 * when and how a notification enters into a state.  <em>All states are
 * optional</em>.  View implementations vary as to how they represent a state
 * to a user, or whether they represent it.
 *
 * @since 2.2
 */
public enum NotificationState {

    /**
     * Registered when the {@link NotificationEntry} is assigned to an
     * individual.  Represents the creation date of a notification vis-a-vis a
     * recipient.
     */
    ISSUED,

    /**
     * Indicates one or more fields of the {@link NotificationEntry} were
     * modified <em>after</em> it was created.
     */
    UPDATED,

    /**
     * Indicates the {@link NotificationEntry} has been pushed to a mobile
     * device.
     */
    PUSHED,

    /**
     * Indicates the recipient was sent an SMS message to alert him or her about
     * the {@link NotificationEntry}.
     */
    TEXTED,

    /**
     * Indicates the recipient was sent an email about the {@link NotificationEntry}.
     */
    EMAILED,

    /**
     * Indicates that (at minimum) the title of the {@link NotificationEntry}
     * was displayed to to the recipient.  Notifications with the
     * <code>READ</code> state may be considered <em>no longer new</em>.
     */
    READ,

    /**
     * Indicates the user has signaled receipt of the {@link NotificationEntry},
     * typically because the sender of the notification requested it.
     */
    ACKNOWLEDGED,

    /**
     * Indicates the user "starred" or "favorited" the {@link NotificationEntry}.
     * This state must be removed if the user later undoes the action.
     */
    FAVORITED,

    /**
     * Indicates the user <em>dismissed</em> the {@link NotificationEntry}
     * temporarily.  This state must be removed after a (probably configurable)
     * period has elapsed.
     */
    SNOOZED {
        private static final long MILLIS_IN_ONE_HOUR = 60L /* min */ * 60L /* sec */ * 1000L /* millis */;
        private int snoozePeriodHours = 72;  // Default

        @SuppressWarnings("unused")
        public void setSnoozePeriodHours(int snoozePeriodHours) {
            this.snoozePeriodHours = snoozePeriodHours;
        }

        @Override
        public boolean isActive(Date timestamp, Set<NotificationState> subsequentHistory) {
            long timeout = timestamp.getTime() + ((long) snoozePeriodHours * MILLIS_IN_ONE_HOUR);
            return System.currentTimeMillis() > timeout;
        }
    },

    /**
     * Indicates the {@link NotificationEntry} is about a task that is partially
     * completed..
     */
    IN_PROGRESS {
        @Override
        public boolean isActive(Date timestamp, Set<NotificationState> subsequentHistory) {
            // A workaround for referencing an enum member before it is declared...
            NotificationState completed = NotificationState.valueOf("COMPLETED");
            return !subsequentHistory.contains(completed);
        }
    },

    /**
     * Indicates the {@link NotificationEntry} is about a task that is completed.
     */
    COMPLETED {
        @Override
        public boolean isActive(Date timestamp, Set<NotificationState> subsequentHistory) {
            return !subsequentHistory.contains(NotificationState.IN_PROGRESS);
        }
    },

    /**
     * Indicates the {@link NotificationEntry} is no longer in the user's active
     * notifications.  Notifications may be archived by user action or by system
     * rules.
     */
    ARCHIVED;

    /**
     * Indicates whether this {@link NotificationState} is still considered
     * "active" given when it occurred (the specified timestamp) and the
     * subsequent history of the {@link NotificationEntry} for this user.  Most
     * states can use the default implementation of this method, which simply
     * returns <code>true</code>.  States that may be effectively "canceled" by
     * other states -- or by the passage of time -- should override this method.
     * 
     * @param timestamp The moment when this state was applied
     * @param subsequentHistory States that were applied to this
     * {@link NotificationEntry} for this user after this one.
     * @return TRUE if this state is still considered active for this
     * {@link NotificationEntry} for this user
     */
    public boolean isActive(Date timestamp, Set<NotificationState> subsequentHistory) {
        return true;
    }

}
