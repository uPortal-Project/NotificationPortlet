/**
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
package org.jasig.portlet.notice.service.jpa;

import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;

import java.util.List;
import java.util.Set;

/**
 * Service for the REST-ful API for JPA Notifications.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
public interface IJpaNotificationRESTService {

    /**
     * Get an {@link EntryDTO} by id.
     */
    EntryDTO getNotification(long id, boolean full);

    /**
     * Get an {@link EntryDTO} for a {@link NotificationEntry}, if applicable.
     */
    EntryDTO getNotification(NotificationEntry entry, boolean full);

    /**
     * Get a paged list of all notifications in the data source.
     *
     * @param page 0 based page #
     * @param pageSize page size
     * @return the list of entries.
     */
    List<EntryDTO> getNotifications(Integer page, Integer pageSize);

    /**
     * Supports custom integrations and decorators.  Allows another component
     * within the Notification app to find entries that match a source (a string
     * representing the other component) and a custom criterion.
     */
    List<EntryDTO> getNotificationsBySourceAndCustomAttribute(String source, String attributeName, String attributeValue);

    /**
     * Create a notification.
     *
     * @param notification the notification object.  Should *NOT* contain a populated id field.
     * @return the newly created Entry
     */
    EntryDTO createNotification(EntryDTO notification);

    /**
     * Get the List addressees for a notification.
     *
     * @param notificationId the notification id
     * @return the set of notifications
     */
    Set<AddresseeDTO> getAddressees(long notificationId);

    /**
     * Get a single addressee by id.
     * @param addresseeId the addressee id
     * @return the addressee if found, else null
     */
    AddresseeDTO getAddressee(long addresseeId);

    /**
     * Create the addressee.
     *
     * @param notificationId the notification to attach the addressee to
     * @param addressee the addressee object.  Should *NOT* create an id
     * @return the newly created addressee
     */
    AddresseeDTO createAddressee(long notificationId, AddresseeDTO addressee);

    /**
     * Get the list of events by notification.
     *
     * @param notificationId the notification id
     * @return the list of events
     */
    List<EventDTO> getEventsByNotification(long notificationId);

    /**
     * Get the list of events by notification.
     *
     * @param notificationId the notification id
     * @return the list of events
     */
    List<EventDTO> getEventsByNotificationAndUser(long notificationId, String username);

    /**
     * Get a single event.
     *
     * @param eventId the event id
     * @return the event if found, else null
     */
    EventDTO getEvent(long eventId);

    /**
     * Create an event.
     *
     * @param notificationId the notification id
     * @param event the entry to create.  Should *NOT* contain an id.
     * @return the newly created event
     */
    EventDTO createEvent(long notificationId, EventDTO event);

}
