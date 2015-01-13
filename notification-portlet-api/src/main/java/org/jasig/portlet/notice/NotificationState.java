/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice;

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
     * Assigned when the {@link NotificationEntry} is created.  Convenient for
     * signaling the moment a notification was created.
     */
    CREATED,

    /**
     * Indicates one or more fields of the {@link NotificationEntry} were
     * modified <em>after</em> it was <code>CREATED</code>.
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
    SNOOZED,

    /**
     * Indicates the {@link NotificationEntry} is about a task that is partially
     * completed..
     */
    IN_PROGRESS,

    /**
     * Indicates the {@link NotificationEntry} is about a task that is completed.
     */
    COMPLETED,

    /**
     * Indicates the {@link NotificationEntry} is no longer in the user's active
     * notifications.  Notifications may be archived by user action or by system
     * rules.
     */
    ARCHIVED

}
