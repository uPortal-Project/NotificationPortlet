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
package org.jasig.portlet.notice.util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationState;
import org.jasig.portlet.notice.action.hide.HideNotificationServiceDecorator;
import org.jasig.portlet.notice.rest.AttributeDTO;
import org.jasig.portlet.notice.rest.EntryDTO;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.service.jpa.IJpaNotificationRESTService;
import org.jasig.portlet.notice.service.jpa.JpaNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This bean allows features and data sources outside of the JPA service
 * implementation to "piggyback" on some of the persistent capabilities of
 * the JPA service.
 *
 * @author drewwills
 */
@Service("jpaServices")
public class JpaServices implements IJpaServices {

    /**
     * The {@link HideNotificationServiceDecorator} uses this value as the
     * <code>source</code> string when masquerading as an external data source
     * in the JPA system.
     */
    private static final String PROXY_SOURCE_NAME = JpaServices.class.getName() + ".PROXY_SOURCE_NAME";

    /**
     * The {@link HideNotificationServiceDecorator} uses this value to store an
     * entry's original <code>id</code> as a custom attribute within the JPA
     * system.
     */
    private static final String PROXY_ID_ATTRIBUTE = JpaServices.class.getName() + ".PROXY_ID_ATTRIBUTE";

    /**
     * Short message WRT what this object is.
     */
    private static final String PROXY_BODY_CONTENT = "This is a proxy notification representing "
                + "one from an external data source;  this strategy supports status changes.";

    @Autowired
    private IJpaNotificationRESTService jpaNotificationRestService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Is the {@link NotificationEntry} object owned by the JPA service?
     */
    public boolean contains(NotificationEntry entry) {
        return entry.getId().startsWith(JpaNotificationService.ID_PREFIX);
    }

    /**
     * Provides the known history of status changes for the specified user and
     * notification <strong>in chronological order</strong>.
     */
    @Override
    public List<EventDTO> getHistory(NotificationEntry entry, String username) {

        List<EventDTO> rslt = Collections.emptyList();  // default

        /*
         * The JPA system owns status tracking, but it only tracks status for
         * entries that it owns.  If the entry is not already a JPA-backed
         * entry, we would use a JPA-side "proxy."
         */
        final EntryDTO entryDto = contains(entry)
                ? jpaNotificationRestService.getNotification(entry, false)
                : fetchJpaProxyIfAvailable(entry);

        // There can't be history if there isn't (yet) a proxy
        if (entryDto != null) {
            rslt = jpaNotificationRestService.getEventsByNotificationAndUser(entryDto.getId(), username);
        }

        return rslt;

    }

    @Override
    public void applyState(NotificationEntry entry, String username, NotificationState state) {

        /*
         * The JPA system owns status tracking, but it only tracks status for
         * entries that it owns.  If the entry is not already a JPA-backed
         * entry, we need to create a JPA-side "proxy."
         */
        final EntryDTO entryDto = contains(entry)
                ? jpaNotificationRestService.getNotification(entry, false)
                : fetchOrCreateJpaProxy(entry);

        final EventDTO event = new EventDTO();
        event.setState(state);
        event.setTimestamp(new Timestamp(System.currentTimeMillis()));
        event.setUsername(username);

        jpaNotificationRestService.createEvent(entryDto.getId(), event);

    }

    /*
     * Implementation
     */

    private EntryDTO fetchJpaProxyIfAvailable(NotificationEntry entry) {
        EntryDTO rslt = null;  // default
        final List<EntryDTO> list = jpaNotificationRestService.getNotificationsBySourceAndCustomAttribute(
                PROXY_SOURCE_NAME,
                PROXY_ID_ATTRIBUTE,
                entry.getId());
        logger.debug("Search for JPA-backed entry with id='{}' returned the following:  {}", entry.getId(), list);
        switch (list.size()) {
            case 1:
                // Cool;  we have one...
                rslt = list.get(0);
                break;
            case 0:
                // Also cool;  we don't have one...
                break;
            default:
                // Not cool...
                throw new IllegalStateException("More than one JPA-back entry exists for id=" + entry.getId());
        }
        return rslt;
    }

    private EntryDTO fetchOrCreateJpaProxy(NotificationEntry entry) {

        // Try fetch first...
        EntryDTO rslt = fetchJpaProxyIfAvailable(entry);

        // Otherwise create...
        if (rslt == null) {
            final EntryDTO newEntry = new EntryDTO();

            // Important stuff that allows us to identify and retrieve this entry
            final List<String> values = new ArrayList<>();
            values.add(entry.getId());
            final AttributeDTO idAttribute = new AttributeDTO();
            idAttribute.setName(PROXY_ID_ATTRIBUTE);
            idAttribute.setValues(values);
            final Set<AttributeDTO> attributes = new HashSet<>();
            attributes.add(idAttribute);
            newEntry.setAttributes(attributes);
            newEntry.setSource(PROXY_SOURCE_NAME);

            // Just fluff
            newEntry.setTitle(entry.getTitle());
            newEntry.setBody(PROXY_BODY_CONTENT);

            rslt = jpaNotificationRestService.createNotification(newEntry);
        }

        return rslt;

    }

}
