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
package org.jasig.portlet.notice.action.hide;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationState;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.util.JpaServices;
import org.jasig.portlet.notice.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HideAction extends NotificationAction {

    public static final String HIDE_DURATION_HOURS_ATTRIBUTE_NAME = HideAction.class.getName()
                                                    + ".HIDE_DURATION_HOURS_ATTRIBUTE_NAME";

    /**
     * This INSTANCE is only for convenience -- HideAction is not a singleton.  
     * There may be situations where de-serialization will create additional 
     * instances, and that's okay.
     */
    public static final HideAction INSTANCE = new HideAction();

    private static final long MILLIS_IN_ONE_HOUR = 60L * 60L * 1000L;

    private static final long serialVersionUID = 1L;

    private static final Logger logger = LoggerFactory.getLogger(HideAction.class);

    /**
     * Must remain public, no-arg for de-serialization.
     */
    public HideAction() {
        // Set a default label;  most use cases will use the setter and override
        setLabel("HIDE");
    }

    /**
     * Invoking a HideAction toggles it.
     */
    @Override
    public void invoke(final ActionRequest req, final ActionResponse res) throws IOException {

        final NotificationEntry entry = getTarget();

        /*
         * The HideAction works like a toggle
         */
        if (!isEntrySnoozed(entry, req)) {
            // Hide it...
            hide(entry, req);
        } else {
            // Un-hide it...
            unhide(entry, req);
        }

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        final String id = getId();
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        // At present, any instance HideAction is equal to another
        return true;
    }

    /* package-private */ long calculateHideDurationMillis(final NotificationEntry entry, final PortletRequest req) {

        /*
         * A notification may specify it's own duration as an attribute;
         * if it doesn't, there is a default from the (portlet) publication record.
         */

        final PortletPreferences prefs = req.getPreferences();

        // This is the default
        String hideDurationHours = prefs.getValue(
                HideNotificationServiceDecorator.HIDE_DURATION_HOURS_PREFERENCE, 
                HideNotificationServiceDecorator.DEFAULT_HIDE_DURATION.toString());

        // Duration specified on the entry itself will trump
        final Map<String,List<String>> attributes = entry.getAttributesMap();
        if (attributes.containsKey(HIDE_DURATION_HOURS_ATTRIBUTE_NAME)) {
            final List<String> values = attributes.get(HIDE_DURATION_HOURS_ATTRIBUTE_NAME);
            if (values.size() != 0) {
                hideDurationHours = values.get(0);  // First is the only one that matters
            }
        }

        long rslt = Long.parseLong(hideDurationHours) * MILLIS_IN_ONE_HOUR;
        return rslt;

    }

    /* package-private */ boolean isEntrySnoozed(NotificationEntry entry, PortletRequest req) {

        // An id is required for hide behavior
        if (StringUtils.isBlank(entry.getId())) {
            return false;
        }

        boolean rslt = false;  // default (clearly)

        final long snoozeDurationMillis = calculateHideDurationMillis(entry, req);

        final JpaServices jpaServices = (JpaServices) SpringContext.getApplicationContext().getBean("jpaServices");
        final List<EventDTO> history = jpaServices.getHistory(entry, req.getRemoteUser());
        logger.debug("List<EventDTO> within getNotificationsBySourceAndCustomAttribute contains {} elements", history.size());

        // Review the history...
        for (EventDTO event : history) {
            switch (event.getState()) {
                case SNOOZED:
                    logger.debug("Found a SNOOZED event:  {}", event);
                    // Nice, but it only counts if it isn't expired...
                    if (event.getTimestamp().getTime() + snoozeDurationMillis > System.currentTimeMillis()) {
                        rslt = true;
                    }
                    break;
                case ISSUED:
                    logger.debug("Found an ISSUED event:  {}", event);
                    // Re-issuing a notification un-snoozes it...
                    rslt = false;
                    break;
                default:
                    // We don't care about any other events in the SNOOZED evaluation...
                    break;
            }
        }

        logger.debug("Returning SNOOZED='{}' for the following notification:  {}", rslt, entry);
        return rslt;

    }

    /*
     * Implementation
     */

    private void hide(NotificationEntry entry, ActionRequest req) {
        final JpaServices jpaServices = (JpaServices) SpringContext.getApplicationContext().getBean("jpaServices");
        jpaServices.applyState(entry, req.getRemoteUser(), NotificationState.SNOOZED);
    }

    private void unhide(NotificationEntry entry, ActionRequest req) {
        final JpaServices jpaServices = (JpaServices) SpringContext.getApplicationContext().getBean("jpaServices");
        // Re-issuing trumps the snooze
        jpaServices.applyState(entry, req.getRemoteUser(), NotificationState.ISSUED);
    }

}
