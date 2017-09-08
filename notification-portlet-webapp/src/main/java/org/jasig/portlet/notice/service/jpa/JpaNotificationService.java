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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationAttribute;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationError;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.NotificationState;
import org.jasig.portlet.notice.service.AbstractNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@code INotificationService} backed by a Spring-/JPA-managed
 * relational database schema.
 * 
 * @since 3.0
 * @author drewwills
 */
public class JpaNotificationService extends AbstractNotificationService {

    /**
     * This prefix helps to keep the Notification table together (in an
     * alphabetized list) and provides clarity to their origin and purpose.
     */
    public static final String TABLENAME_PREFIX = "NOTICE_";

    /* package-private */ static final String ID_PREFIX = "jpa_";

    private static final NotificationResponse EMPTY_RESPONSE = new NotificationResponse();
    private static final String UNCATEGORIZED_MESSAGE_CODE = "uncategorized";
    private static final String UNCATEGORIZED_DEFAULT_MESSAGE = "(Uncategorized)";
    private static final String PREFS_ENABLED = "JpaNotificationService.enabled";

    @Autowired
    private INotificationDao notificationDao;

    @Autowired
    private MessageSource messages;

    private final Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Is the {@link NotificationEntry} object owned by the JPA service?
     */
    public boolean contains(NotificationEntry entry) {
        return entry.getId().startsWith(ID_PREFIX);
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {

        NotificationResponse rslt = EMPTY_RESPONSE;  // default

        PortletPreferences prefs = req.getPreferences();

        // We do not perform a check for unauthenticated users (but this
        // is an assumption that may need revisiting in the future).
        if (usernameFinder.isAuthenticated(req) && Boolean.parseBoolean(prefs.getValue(PREFS_ENABLED, "false"))) {
            final String username = usernameFinder.findUsername(req);

            log.debug("Fetching notifications for user:  {}", username);

            final Set<JpaEntry> entries = notificationDao.getEntriesByRecipient(username);

            log.debug("Found the following notifications for user '{}':  {}",
                                                username, entries.toString());

            rslt = prepareResponse(entries, username);
        }

        return rslt;

    }
	
    /**
     * Caller must insure that the state being set has not already been added to the entry
     * to avoid multiple events with the same state.
     * 
     * @param req
     * @param entryId
     * @param state 
     */
    public void addEntryState(PortletRequest req, String entryId, NotificationState state) {
        if (usernameFinder.isAuthenticated(req)) {

            final String username = usernameFinder.findUsername(req);

            String idStr = entryId.replaceAll(ID_PREFIX, ""); // remove the prefix

            JpaEntry jpaEntry = notificationDao.getEntry(Long.parseLong(idStr));
            if (jpaEntry != null) {
                JpaEvent event = new JpaEvent();
                event.setEntry(jpaEntry);
                event.setState(state);
                event.setTimestamp(new Timestamp(new Date().getTime()));
                event.setUsername(username);

                notificationDao.createOrUpdateEvent(event);
            }
            else {
                throw new IllegalArgumentException("JpaEntry not found");
            }
        }
    }
	
    /*
     * Implementation
     */

    private NotificationResponse prepareResponse(Set<JpaEntry> entries, String username) {

        Map<String,NotificationCategory> categories = new HashMap<String,NotificationCategory>();
        for (JpaEntry entry : entries) {

            // Choose a category title
            final String categryTitle = !StringUtils.isBlank(entry.getCategory())
                    ? entry.getCategory()
                    : messages.getMessage(UNCATEGORIZED_MESSAGE_CODE, null, 
                            UNCATEGORIZED_DEFAULT_MESSAGE, Locale.getDefault());

            // Obtain the category object matching the title
            NotificationCategory category = categories.get(categryTitle);
            if (category == null) {
                category = new NotificationCategory();
                category.setTitle(categryTitle);
                categories.put(categryTitle, category);
            }

            // Prepare a NotificationEntry
            NotificationEntry y = prepareEntry(entry, username);
            if (y != null) {
                category.addEntries(Collections.singletonList(y));
            }

        }

        // Create & load the response
        final List<NotificationCategory> cList = new ArrayList<NotificationCategory>(categories.values());
        final List<NotificationError> eList = Collections.emptyList();  // Anything here?
        NotificationResponse rslt = new NotificationResponse(cList, eList);

        return rslt;

    }

    /**
     * Creates a {@link NotificationEntry} from a {@link JpaEntry}, but will
     * return null if that process cannot be performed in a valid way.
     * 
     * @param entry
     * @param username
     * @return A fully-constituted {@link NotificationEntry} or null
     */
    private NotificationEntry prepareEntry(JpaEntry entry, String username) {

        /*
         * Implementation Note:  Most notification fields are optional.  This
         * method impl avoids setting fields within the NotificationEntry when
         * those fields are (essentially) unspecified on the JpaEntry in order
         * not to run afoul of any logic (present or future) that may trigger
         * when a field is set (e.g. validation, virtual members, change
         * tracking).
         */

        NotificationEntry rslt = new NotificationEntry();

        // Id & title will always be present
        rslt.setId(ID_PREFIX + entry.getId());
        final String title = entry.getTitle();
        if (StringUtils.isBlank(title)) {
            log.warn("User '" + username + "' had a notification with an empty title:  " + entry.toString());
            return null;
        }
        rslt.setTitle(title);

        // But these fields are optional
        if (!StringUtils.isBlank(entry.getBody())) {  // Body
            rslt.setBody(entry.getBody());
        }
        if (entry.getDueDate() != null) {  // Due date
            rslt.setDueDate(entry.getDueDate());
        }
        if (!StringUtils.isBlank(entry.getImage())) {  // Image
            rslt.setImage(entry.getImage());
        }
        if (!StringUtils.isBlank(entry.getLinkText())) {  // Link text
            rslt.setLinkText(entry.getLinkText());
        }
        if (entry.getPriority() != 0) {  // Priority
            rslt.setPriority(entry.getPriority());
        }
        if (!StringUtils.isBlank(entry.getSource())) {  // Source
            rslt.setSource(entry.getSource());
        }
        if (!StringUtils.isBlank(entry.getUrl())) {  // Url
            rslt.setUrl(entry.getUrl());
        }

        // States (transaction log)
        Map<NotificationState,Date> states = prepareStates(entry, username);
        rslt.setStates(states);

        // Collections of items...
        if (!entry.getAttributes().isEmpty()) {  // Attributes
            List<NotificationAttribute> attributes = prepareAttributes(entry.getAttributes(), username);
            rslt.setAttributes(attributes);
        }
        if (!entry.getActions().isEmpty()) {  // Actions
            List<NotificationAction> actions = prepareActions(entry.getActions(),
                                                    states.keySet(), username);
            rslt.setAvailableActions(actions);
        }

        return rslt;

    }

    private Map<NotificationState, Date> prepareStates(JpaEntry entry, String username) {
        Map<NotificationState, Date> rslt = new HashMap<NotificationState, Date>();
        List<JpaEvent> events = notificationDao.getEvents(entry.getId(), username);
        Collections.reverse(events);  // Process in reverse-chronological order
        for (JpaEvent e : events) {
            // NOTE:  We're obligated to filter out states
            // that are "canceled out" by subsequent events
            Set<NotificationState> subsequentHistory = rslt.keySet();
            if (e.getState().isActive(e.getTimestamp(), subsequentHistory)) {
                rslt.put(e.getState(), e.getTimestamp());
            }
        }
        return rslt;
    }

    private List<NotificationAttribute> prepareAttributes(Set<JpaAttribute> attributes, String username) {
        List<NotificationAttribute> rslt = new ArrayList<NotificationAttribute>();
        for (JpaAttribute a : attributes) {
            NotificationAttribute n = new NotificationAttribute(a.getName(), a.getValues());
            rslt.add(n);
        }
        return rslt;
    }

    private List<NotificationAction> prepareActions(Set<JpaAction> actions,
            Set<NotificationState> activeStates, String username) {

        List<NotificationAction> rslt = new ArrayList<NotificationAction>();

        String className = null;
        try {
            // NB:  Do we need to filter out actions that are not currently
            // applicable?  (Or is that better-handled downstream?)
            for (JpaAction a : actions) {
                className = a.getClazz();
                @SuppressWarnings("unchecked")
                final Class<? extends NotificationAction> clazz = (Class<? extends NotificationAction>) Class.forName(className);
                NotificationAction n = clazz.newInstance();
                n.setLabel(a.getLabel());
                rslt.add(n);
            }
        } catch (Exception e) {
            log.warn("User '" + username + "' had an action of an unknown className:  " + className);
        }

        return rslt;

    }

}
