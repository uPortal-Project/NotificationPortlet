package org.jasig.portlet.notice.service.studentsuccessplan;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.ReadContext;
import net.minidev.json.JSONArray;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;


/**
 * @author Josh Helmer, jhelmer.unicon.net
 */
public class SSPTaskNotificationService extends AbstractNotificationService {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final String NOTIFICATION_CATEGORY_PREF = "SSPTaskNotificationService.categoryName";
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


    private ISSPApi sspApi;
    private ISSPPersonLookup personLookup;
    private String activeTaskURLFragment = "/api/1/person/{personId}/task?STATUS=ACTIVE&limit=1000";


    @Autowired
    public void setSspApi(ISSPApi sspApi) {
        this.sspApi = sspApi;
    }


    @Autowired
    public void setPersonLookup(ISSPPersonLookup personLookup) {
        this.personLookup = personLookup;
    }


    @Override
    public NotificationResponse fetch(PortletRequest req) {

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


    private NotificationResponse notificationError(String errorMsg) {
        NotificationError error = new NotificationError();
        error.setError(errorMsg);
        error.setSource(getClass().getSimpleName());

        NotificationResponse notification = new NotificationResponse();
        notification.setErrors(Arrays.asList(error));
        return notification;
    }


    private NotificationResponse mapToNotificationResponse(PortletRequest request, ResponseEntity<String> response) {
        // This could be done with Jackson mappings too, but the
        // ssp "mark completed" action requires the full task JSON.
        // Rather than trying to maintain full parallel entities, or trying
        // to use generic maps, use json path.  I believe this should
        // make the API a bit less likely to break with minor SSP
        // entity updates.  May need to revisit this decision in
        // the future.
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
            list.add(entry);
        }

        // build the notification response...
        NotificationCategory category = getNotificationCategory(request);
        category.addEntries(list);

        NotificationResponse notification = new NotificationResponse();
        notification.setCategories(Arrays.asList(category));

        return notification;
    }


    private NotificationEntry mapNotificationEntry(ReadContext readContext, int index, String source) {
        NotificationEntry entry = new NotificationEntry();
        entry.setSource(source);

        String id = readContext.read(format(ROW_ID_QUERY_FMT, index));
        entry.setId(id);

        String title = readContext.read(format(ROW_NAME_QUERY_FMT, index));
        entry.setTitle(title);

        String desc = readContext.read(format(ROW_DESCRIPTION_QUERY_FMT, index));
        entry.setBody(desc);

        try {
            Date dueDate = readContext.read(format(ROW_DUE_DATE_QUERY_FMT, index), Date.class);
            entry.setDueDate(dueDate);
        } catch (Exception e) {
            log.warn("Error parsing due date.  Ignoring", e);
        }

        return entry;
    }


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


    private String getNotificationSource(PortletRequest req) {
        PortletPreferences preferences = req.getPreferences();
        String source = preferences.getValue(NOTIFICATION_SOURCE_PREF, DEFAULT_NOTIFICATION_SOURCE);

        return source;
    }
}
