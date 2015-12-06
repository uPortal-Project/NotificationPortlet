package org.jasig.portlet.notice.service.jpa;

import org.codehaus.jackson.JsonNode;
import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.rest.RecipientDTO;

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
     * Get the list of notifications.
     *
     * @param page 0 based page #
     * @param pageSize page size
     * @return the list of entries.
     */
    List<EntryDTO> getNotifications(Integer page, Integer pageSize);

    /**
     * Get a notification by id.
     *
     * @param id the notification id
     * @param full if true, will fetch the addressee info as well, otherwise will omit
     * @return The entry if found, else null
     */
    EntryDTO getNotification(long id, boolean full);


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
     * @param entry the entry to create.  Should *NOT* contain an id.
     * @return the newly created event
     */
    EventDTO createEvent(long notificationId, EventDTO entry);
}
