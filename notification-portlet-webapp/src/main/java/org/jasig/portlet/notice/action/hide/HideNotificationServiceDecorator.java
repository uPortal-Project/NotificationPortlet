/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jasig.portlet.notice.action.hide;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.notice.INotificationService;
import org.jasig.portlet.notice.NotificationAction;
import org.jasig.portlet.notice.NotificationCategory;
import org.jasig.portlet.notice.NotificationEntry;
import org.jasig.portlet.notice.NotificationResponse;

/**
 * This class can be used to add a "hide" or "snooze" feature to notifications 
 * that have an id attribute.  By default, notices will be hidden for 365 days 
 * (effectively permanent).
 * 
 * @author awills
 */
public class HideNotificationServiceDecorator implements INotificationService {

    public static final String HIDE_DURATION_HOURS_PREFERENCE = "HideNotificationServiceDecorator.hideDurationHours";
    public static final Integer DEFAULT_HIDE_DURATION = -1;  // The feature is disabled by default

    // Instance members
    private INotificationService enclosedNotificationService;
    private final Log log = LogFactory.getLog(getClass());

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

        // Just pass through the enclosed collection if this feature is disabled
        if (HideAction.INSTANCE.calculateHideDurationMillis(req) < 0) {
            return enclosedNotificationService.fetch(req);
        }

        /*
         * We will build a fresh NotificationResponse based on a deep-copy of the one we enclose
         * 
         * NB:  TBH I'm not certain this deep-clone thing is a good idea at all;  need to see how it plays out.  ~drew
         */
        final NotificationResponse sourceResponse = enclosedNotificationService.fetch(req);
        NotificationResponse rslt = sourceResponse.cloneIfNotCloned();

        final Set<String> currentlyHiddenNotificationIds = HideAction.INSTANCE.getHiddenNoticesMap(req).keySet();

        // Add and implement the hide behavior with our copy
        for (NotificationCategory category : rslt.getCategories()) {

            // We have to track if any were removed, and which ones remain
            final List<NotificationEntry> entriesAfterHiding = new ArrayList<NotificationEntry>(category.getEntries());

            for (NotificationEntry entry : category.getEntries()) {

                final List<NotificationAction> currentList = entry.getAvailableActions();

                /*
                 * There are 2 requirements for an entry to be decorated with Hide behavior:
                 * 
                 *   - (1) It must have an id set
                 *   - (2) It must not have a HideAction already
                 */
                if (StringUtils.isNotBlank(entry.getId()) && !currentList.contains(HideAction.INSTANCE)) {
                    final List<NotificationAction> replacementList = new ArrayList<NotificationAction>(currentList);
                    replacementList.add(new HideAction());
                    entry.setAvailableActions(replacementList); // Also sets HideAction.targetEntity
                }

                /*
                 * And 2 requirements for an entry to be removed based on it:
                 * 
                 *   - (1) It must be hidden by the user
                 *   - (2) We must not be in "display hidden notices" mode (TODO:  Implement!)
                 */
                if (StringUtils.isNotBlank(entry.getId()) && currentlyHiddenNotificationIds.contains(entry.getId())) {
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
