package org.jasig.portlet.notice.service.ssp;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationState;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;


/**
 * Read the list of user task from SSP.
 *
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPTaskNotificationService extends AbstractNotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String NOTIFICATION_CATEGORY_PREF = "SSPTaskNotificationService.categoryName";
    private static final String SSP_NOTIFICATIONS_ENABLED = "SSPTaskNotificationService.enabled";
    private static final String SSP_NOTIFICATIONS_ENABLE_MARK_COMPLETED = "SSPTaskNotificationService.enableMarkCompletedAction";
    private static final String DEFAULT_CATEGORY = "Student Success Plan";
    private static final String NOTIFICATION_SOURCE_PREF = "SSPTaskNotificationService.sourceName";
    private static final String DEFAULT_NOTIFICATION_SOURCE = "Student Success Plan";
    private static final String SUCCESS_QUERY = "$.success";
    private static final String MESSAGE_QUERY = "$.message";
    private static final String ROWS_QUERY = "$.rows";
    private static final String ROW_ID_QUERY_FMT = "$.rows[%d].id";
    private static final String ROW_NAME_QUERY_FMT = "$.rows[%d].name";
    private static final String ROW_DESCRIPTION_QUERY_FMT = "$.rows[%d].description";
    private static final String ROW_DUE_DATE_QUERY_FMT = "$.rows[%d].dueDate";
    private static final String ROW_COMPLETED_QUERY_FMT = "$.rows[%d].completed";
    private static final String ROW_LINK_QUERY_FMT = "$.rows[%d].link";

    private ISSPApi sspApi;
    private ISSPPersonLookup personLookup;
    private String activeTaskURLFragment = "/api/1/person/{personId}/task?STATUS=ACTIVE&limit=1000";
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");


    @Autowired
    public void setSspApi(ISSPApi sspApi) {
        this.sspApi = sspApi;
    }


    @Autowired
    public void setPersonLookup(ISSPPersonLookup personLookup) {
        this.personLookup = personLookup;
    }


    /**
     * Fetch the set of SSP tasks for the uPortal user.
     *
     * @param req The <code>PortletRequest</code>
     * @return The set of notifications for this data source.
     */
    @Override
    public NotificationResponse fetch(PortletRequest req) {
        PortletPreferences preferences = req.getPreferences();
        String enabled = preferences.getValue(SSP_NOTIFICATIONS_ENABLED, "false");
        if (!"true".equalsIgnoreCase(enabled)) {
            return new NotificationResponse();
        }

        String personId = getPersonId(req);
        if (personId == null) {
            // Not all students will have active SSP records,
            // so if no entry is found in SSP, just return an
            // empty response set.
            return new NotificationResponse();
        }

        String urlFragment = getActiveTaskUrl();
        SSPApiRequest<String> request = new SSPApiRequest<>(urlFragment, String.class)
                .addUriParameter("personId", personId);

        ResponseEntity<String> response;
        try {
            response = sspApi.doRequest(request);
        } catch (Exception e) {
            log.error("Error reading SSP Notifications: " + e.getMessage());
            return notificationError(e.getMessage());
        }

        if (response.getStatusCode().series() != HttpStatus.Series.SUCCESSFUL) {
            log.error("Error reading SSP Notifications: " + response);
            return notificationError(response.getBody());
        }

        NotificationResponse notification = mapToNotificationResponse(req, response);
        return notification;
    }


    /**
     * Error handler.
     *
     * @param errorMsg The error message
     * @return a notification response with the error message
     */
    private NotificationResponse notificationError(String errorMsg) {
        NotificationError error = new NotificationError();
        error.setError(errorMsg);
        error.setSource(getClass().getSimpleName());

        NotificationResponse notification = new NotificationResponse();
        notification.setErrors(Arrays.asList(error));
        return notification;
    }


    /**
     * Map and SSP Response to a NotificationResponse.
     *
     * @param request the portlet request
     * @param response the response from the REST call to SSP
     * @return the mapped notification response
     */
    private NotificationResponse mapToNotificationResponse(PortletRequest request, ResponseEntity<String> response) {
        Configuration config = Configuration.builder().options(
                Option.DEFAULT_PATH_LEAF_TO_NULL
        ).build();
        ReadContext readContext = JsonPath
                .using(config)
                .parse(response.getBody());

        // check the status embedded in the response too...
        String success = readContext.read(SUCCESS_QUERY);
        // grr. SSP returns this as a string...
        if (!"true".equalsIgnoreCase(success)) {
            String error = readContext.read(MESSAGE_QUERY);
            return notificationError(error);
        }

        // read the actual tasks...
        Object rows = readContext.read(ROWS_QUERY);
        if (!(rows instanceof JSONArray)) {
            throw new RuntimeException("Expected 'rows' to be an array of tasks");
        }

        String source = getNotificationSource(request);

        List<NotificationEntry> list = new ArrayList<>();
        for (int i = 0; i < ((JSONArray)rows).size(); i++) {
            NotificationEntry entry = mapNotificationEntry(readContext, i, source);
            if (entry != null) {
                attachActions(request, entry);
                list.add(entry);
            }
        }

        // build the notification response...
        NotificationResponse notification = new NotificationResponse();
        if (!list.isEmpty()) {
            NotificationCategory category = getNotificationCategory(request);
            category.addEntries(list);

            notification.setCategories(Arrays.asList(category));
        }

        return notification;
    }


    /**
     * Map a single notification entry.
     *
     * @param readContext the parsed JSON from SSP
     * @param index the index of the current entry to read
     * @param source the source value to use for the entry
     * @return a new Notification Entry.  May return null if the entry is invalid or complete
     */
    private NotificationEntry mapNotificationEntry(ReadContext readContext, int index, String source) {
        boolean completed = readContext.read(format(ROW_COMPLETED_QUERY_FMT, index), Boolean.class);
        if (completed) {
            return null;
        }

        NotificationEntry entry = new NotificationEntry();
        entry.setSource(source);

        String id = readContext.read(format(ROW_ID_QUERY_FMT, index));
        entry.setId(id);

        String title = readContext.read(format(ROW_NAME_QUERY_FMT, index));
        entry.setTitle(title);

        String desc = readContext.read(format(ROW_DESCRIPTION_QUERY_FMT, index));
        entry.setBody(desc);

        String link = readContext.read(format(ROW_LINK_QUERY_FMT, index));
        URL fixedLink = normalizeLink(link);
        if (fixedLink != null) {
            entry.setUrl(fixedLink.toExternalForm());
        }

        Date createDate = readContext.read(format("$.rows[%d].createdDate", index), Date.class);
        Map<NotificationState, Date> states = new HashMap<>();
        states.put(NotificationState.ISSUED, createDate);

        try {
            // the date is in an odd format, need to parse by hand...
            String dateStr = readContext.read(format(ROW_DUE_DATE_QUERY_FMT, index));
            if (!StringUtils.isBlank(dateStr)) {
                synchronized (dateFormat) {
                    Date dueDate = dateFormat.parse(dateStr);
                    entry.setDueDate(dueDate);
                }
            }
        } catch (Exception e) {
            log.warn("Error parsing due date.  Ignoring", e);
        }


        return entry;
    }


    /**
     * Attach any SSP specific actions to this entry, if enabled.
     *
     * @param request the portlet request
     * @param entry the entry
     */
    private void attachActions(PortletRequest request, NotificationEntry entry) {
        PortletPreferences prefs = request.getPreferences();
        String stringVal = prefs.getValue(SSP_NOTIFICATIONS_ENABLE_MARK_COMPLETED, "false");
        boolean enableMarkCompleted = ("true".equalsIgnoreCase(stringVal));

        List<NotificationAction> actions = new ArrayList<>();

        if (enableMarkCompleted) {
            MarkTaskCompletedAction action = new MarkTaskCompletedAction(entry.getId());
            actions.add(action);
        }

        entry.setAvailableActions(actions);
    }


    /**
     * Some of the links I have seen from SSP are not well formed.   Try to convert any URLs
     * to a usable form.
     *
     * @param link The link value from SSP
     * @return A full URL
     * @throws java.net.MalformedURLException if the link value can not be converted
     *      to a URL.
     */
    private URL normalizeLink(String link) {
        try {
            if (StringUtils.isEmpty(link)) {
                return null;
            }

            if (link.startsWith("/")) {
                return sspApi.getSSPUrl(link, true);
            }

            if (link.startsWith("http://") || link.startsWith("https://")) {
                return new URL(link);
            }

            // if all else fails, just tack on http:// and see if the URL parser can handle
            // it.  Perhaps, not ideal...
            return new URL("http://" + link);
        } catch (MalformedURLException e) {
            log.warn("Bad URL from SSP Entry: " + link, e);
            return null;
        }
    }


    /**
     * Get the category name to use for SSP notifications.
     *
     * @param request the portlet request
     * @return The notification category to use
     */
    private NotificationCategory getNotificationCategory(PortletRequest request) {
        PortletPreferences preferences = request.getPreferences();
        String title = preferences.getValue(NOTIFICATION_CATEGORY_PREF, DEFAULT_CATEGORY);

        NotificationCategory category = new NotificationCategory();
        category.setTitle(title);

        return category;
    }


    private String getActiveTaskUrl() {
        return activeTaskURLFragment;
    }


    private String getPersonId(PortletRequest req) {
        return personLookup.lookupPersonId(req);
    }


    /**
     * Get the source value to use for a Notification entry.
     *
     * @param req the portlet request
     * @return the source value to use.
     */
    private String getNotificationSource(PortletRequest req) {
        PortletPreferences preferences = req.getPreferences();
        String source = preferences.getValue(NOTIFICATION_SOURCE_PREF, DEFAULT_NOTIFICATION_SOURCE);

        return source;
    }
}
