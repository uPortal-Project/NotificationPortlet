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

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;
import org.jasig.portlet.notice.service.AbstractNotificationServiceDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class can be used to add a "hide" or "snooze" feature to notifications 
 * that have an id attribute.  By default, notices will be hidden for 365 days 
 * (effectively permanent).
 * 
 * @author awills
 * @deprecated The entire notion of Portlet API-based decorators is deprecated
 */
public class HideNotificationServiceDecorator extends AbstractNotificationServiceDecorator {

    public static final String HIDE_DURATION_HOURS_PREFERENCE = "HideNotificationServiceDecorator.hideDurationHours";
    public static final long HIDE_DURATION_NONE = -1L;  // The feature is disabled by default

    // Instance members
    private INotificationService enclosedNotificationService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void setEnclosedNotificationService(INotificationService enclosedNotificationService) {
        this.enclosedNotificationService = enclosedNotificationService;
    }

    @Override
    public String getName() {
        return enclosedNotificationService.getName();
    }

    @Override
    public void invoke(ActionRequest req, ActionResponse res, boolean refresh) {
        enclosedNotificationService.invoke(req, res, refresh);
    }

    @Override
    public void collect(EventRequest req, EventResponse res) {
        enclosedNotificationService.collect(req, res);
    }

    @Override
    public NotificationResponse fetch(PortletRequest req) {

        logger.debug("Processing notifications for username='{}'", req.getRemoteUser());

        /*
         * We will build a fresh NotificationResponse based on a deep-copy of the one we enclose
         * 
         * NB:  TBH I'm not certain this deep-clone thing is a good idea at all;  need to see how it plays out.  ~drew
         */
        final NotificationResponse sourceResponse = enclosedNotificationService.fetch(req);
        NotificationResponse rslt = sourceResponse.cloneIfNotCloned();

        // Add and implement the hide behavior with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            // We have to track if any were removed, and which ones remain
            final List<NotificationEntry> entriesAfterHiding = new ArrayList<>(category.getEntries());

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> currentList = entry.getAvailableActions();

                /*
                 * There are 3 requirements for an entry to be decorated with Hide behavior:
                 * 
                 *   - (1) It must have an id set
                 *   - (2) It must not have a HideAction (or subclass) already
                 *   - (3) Hiding the entry must be *meaningful* (i.e. must be a duration specified
                 *         on either the portlet or the entry)
                 */
                if (StringUtils.isNotBlank(entry.getId()) // #1
                        && !currentList.stream().anyMatch(action -> action instanceof HideAction) // #2
                        && HideAction.INSTANCE.calculateHideDurationMillis(entry, req)
                                > HIDE_DURATION_NONE) { // #3
                    logger.debug("Adding hide action to notification with id='{}' for username='{}'", entry.getId(), req.getRemoteUser());
                    final List<NotificationAction> replacementList = new ArrayList<>(currentList);
                    replacementList.add(new HideAction());
                    entry.setAvailableActions(replacementList); // Also sets HideAction.targetEntity
                }

                /*
                 * Now that we know yea or nay (WRT Hide behavior), is the entry currently hidden?
                 */
                if (entry.getAvailableActions().stream()
                        .filter(action -> action instanceof HideAction)
                        .anyMatch(action -> ((HideAction) action).isEntrySnoozed(entry, req))) {
                    logger.debug("Hiding entry with id='{}' for username='{}' based on user's previous action", entry.getId(), req.getRemoteUser());
                    entriesAfterHiding.remove(entry);
                }
            }

            if (category.getEntries().size() != entriesAfterHiding.size()) {
                category.setEntries(entriesAfterHiding);
            }

        }

        return rslt;

    }

    @Override
    public boolean isValid(PortletRequest req, NotificationResponse previousResponse) {
        return enclosedNotificationService.isValid(req, previousResponse);
    }

}
