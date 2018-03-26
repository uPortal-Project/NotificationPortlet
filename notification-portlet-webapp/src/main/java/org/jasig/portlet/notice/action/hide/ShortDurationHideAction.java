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
package org.jasig.portlet.notice.action.hide;

import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.rest.EventDTO;
import org.jasig.portlet.notice.util.JpaServices;
import org.jasig.portlet.notice.util.SpringContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The original {@link HideAction} only supports durations measured in hours.  Recently, it has
 * become clear that we need to support hiding notices for shorter periods.  Subclass this abstract
 * {@link NotificationAction} in order to support any duration.  Pass a duration in millis via the
 * constructor.
 *
 * @since 3.1
 */
public abstract class ShortDurationHideAction extends HideAction {

    private static final long serialVersionUID = 1L;

    private final long hideDurationMillis;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Subclasses must specify the duration with a call to <code>super()</code>.
     */
    protected ShortDurationHideAction(long hideDurationMillis) {
        this.hideDurationMillis = hideDurationMillis;
    }

    /**
     * Override <code>isEntrySnoozed</code> because we need a duration measured in seconds.
     */
    @Override
    /* package-private */ boolean isEntrySnoozed(NotificationEntry entry, PortletRequest req) {

        // An id is required for hide behavior
        if (StringUtils.isBlank(entry.getId())) {
            return false;
        }

        boolean rslt = false;  // default (clearly)

        final JpaServices jpaServices = (JpaServices) SpringContext.getApplicationContext().getBean("jpaServices");
        final List<EventDTO> history = jpaServices.getHistory(entry, req.getRemoteUser());
        logger.debug("List<EventDTO> within getNotificationsBySourceAndCustomAttribute contains {} elements", history.size());

        // Review the history...
        for (EventDTO event : history) {
            switch (event.getState()) {
                case SNOOZED:
                    logger.debug("Found a SNOOZED event:  {}", event);
                    // Nice, but it only counts if it isn't expired...
                    if (event.getTimestamp().getTime() + hideDurationMillis > System.currentTimeMillis()) {
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

}
