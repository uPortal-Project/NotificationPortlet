package org.jasig.portlet.notice.controller.rest;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portlet.notice.rest.AddresseeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.service.jpa.IJpaNotificationRESTService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


/**
 * Controller for the REST endpoints exposed to manage
 * JPA notifications.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 * @since 3.0
 */
@Controller
@RequestMapping(JPANotificationRESTController.REQUEST_ROOT)
public class JPANotificationRESTController {
    private Logger log = LoggerFactory.getLogger(getClass());
    static final String REQUEST_ROOT = "/v1/notifications";
    private static final String API_ROOT = "/api";


    @Autowired
    private IJpaNotificationRESTService restService;


    /**
     * Get the list of notifications.  Supports optional (and recommended!) paging
     *
     * @param page The 0 based page number
     * @param pageSize the page size
     * @return the list of matching notifications.
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<EntryDTO> getNotifications(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "pageSize", required = false) Integer pageSize) {

        return restService.getNotifications(page, pageSize);
    }


    /**
     * Create a notification.
     *
     * @param req the Http request
     * @param response the Http response
     * @param entry The Entry
     * @return The persisted entry
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EntryDTO createNotification(HttpServletRequest req, HttpServletResponse response, @RequestBody EntryDTO entry) {
        EntryDTO persisted = restService.createNotification(entry);

        String url = getSingleNotificationRESTUrl(req, persisted.getId());
        response.addHeader("Location", url);

        return persisted;
    }


    /**
     * Get 1 notification by id.
     *
     * @param response the Http response
     * @param id the id of the notification
     * @param full optionally fetch addressee info.  If false, addressee info will be omitted.
     * @return the notification
     */
    @RequestMapping(value = "/{notificationId}", method = RequestMethod.GET)
    @ResponseBody
    public EntryDTO getNotification(HttpServletResponse response, @PathVariable("notificationId") long id,
            @RequestParam(value = "full", required = false, defaultValue = "false") boolean full) {
        EntryDTO notification = restService.getNotification(id, full);
        if (notification == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return notification;
    }


    /**
     * Get the set of addressees for a notification.
     *
     * @param id the notification id
     * @return the set of addressees
     */
    @RequestMapping(value = "/{notificationId}/addressees", method = RequestMethod.GET)
    @ResponseBody
    public Set<AddresseeDTO> getAddressees(@PathVariable("notificationId") long id) {
        return restService.getAddressees(id);
    }


    /**
     * Create a new addressee for a notification.
     *
     * @param req the Http request
     * @param resp the Http response
     * @param id the notification id
     * @param addressee the list of addressees
     * @return the persisted addressee
     */
    @RequestMapping(value = "/{notificationId}/addressees", method = RequestMethod.POST)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public AddresseeDTO addAddressee(HttpServletRequest req, HttpServletResponse resp,
                @PathVariable("notificationId") long id,
                @RequestBody AddresseeDTO addressee) {
        AddresseeDTO dto = restService.createAddressee(id, addressee);
        if (dto == null) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        String url = getSingleNotificationRESTUrl(req, id) + "/addressee/" + dto.getId();
        resp.addHeader("Location", url);

        return dto;
    }


    /**
     * Get a specific addressee
     *
     * @param resp the Http Response
     * @param notificationId the notification id
     * @param addresseeId the addressee id
     * @return the matching addressee if available
     */
    @RequestMapping(value = "/{notificationId}/addressees/{addresseeId}", method = RequestMethod.GET)
    @ResponseBody
    public AddresseeDTO getAddressee(HttpServletResponse resp,
            @PathVariable("notificationId") long notificationId,
            @PathVariable("addresseeId") long addresseeId) {
        AddresseeDTO dto = restService.getAddressee(addresseeId);
        if (dto == null) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return dto;
    }


    /**
     * Get the list of events for a notification.
     *
     * @param id the notification id
     * @return the list of events
     */
    @RequestMapping(value = "/{notificationId}/events", method = RequestMethod.GET)
    @ResponseBody
    public List<EventDTO> getEventsByNotification(@PathVariable("notificationId") long id) {
        return restService.getEventsByNotification(id);
    }


    /**
     * Get a specific event.
     *
     * @param response The Http response
     * @param notificationId the notification id
     * @param eventId the event id
     * @return the matching event, if found
     */
    @RequestMapping(value = "/{notificationId}/events/{eventId}", method = RequestMethod.GET)
    @ResponseBody
    public EventDTO getEvent(HttpServletResponse response, @PathVariable("notificationId") long notificationId,
            @PathVariable("eventId") long eventId) {
        EventDTO event = restService.getEvent(eventId);
        if (event == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        return event;
    }


    /**
     * Create a new event.
     *
     * @param req the Http request
     * @param resp the Http response
     * @param notificationId the notification id
     * @param event the event id
     * @return The newly persisted object
     */
    @RequestMapping(value = "/{notificationId}/events", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public EventDTO createEvent(HttpServletRequest req, HttpServletResponse resp,
            @PathVariable("notificationId") long notificationId,
            @RequestBody EventDTO event) {
        EventDTO dto = restService.createEvent(notificationId, event);
        if (dto == null) {
            resp.setStatus(HttpStatus.NOT_FOUND.value());
            return null;
        }

        String url = getSingleNotificationRESTUrl(req, notificationId) + "/state/" + dto.getId();
        resp.addHeader("Location", url);

        return dto;
    }


    /**
     * Build the URL for a specific notification.
     *
     * @param request the Http request
     * @param id the notification id
     * @return the URL to hit that specific id
     */
    private String getSingleNotificationRESTUrl(HttpServletRequest request, long id) {
        String path = request.getContextPath() + API_ROOT + REQUEST_ROOT + id;
        try {
            URL url = new URL(request.getScheme(), request.getServerName(), request.getServerPort(), path);
            return url.toExternalForm();

        } catch (MalformedURLException e) {
            // if it fails, just return a relative path.  Not ideal, but better than nothing...
            log.warn("Error building Location header", e);
            return path;
        }
    }
}
